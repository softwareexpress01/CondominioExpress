/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

/**
 *
 * @author Frank R.H.
 */

import datosbasicos.modelos.Propiedad;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class SolSusPendCtrl extends DaoCrud<Suscripcion, Propiedad, Integer> implements Serializable {

    Collection<SuscripcionDT> listaSolSusPend = new ArrayList<SuscripcionDT>();

    public SolSusPendCtrl() {
    }

    public void generarReporteSolSusPend() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/SolSusPend.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------

        JRBeanCollectionDataSource dataSourceSusProcesadas;;

        Collection solSusPend = buscarSolSusPend();

        if (listaSolSusPend.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No se encontraron Solicitudes de Suscripción pendientes."));
            return;

        }
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo de la compañia
        //--------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        String ruta = beanUsuario.getRuta();
        String nombLogo = "Software Express.png";
        InputStream logo = new FileInputStream(ruta + nombLogo);
        HashMap parametro = new HashMap();
        parametro.put("logo", logo);

        dataSourceSusProcesadas = new JRBeanCollectionDataSource(solSusPend);

        //---------------------------------------------------------


        //parametros.put("susProcesadas", dataSourceSusProcesadas);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSourceSusProcesadas);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection buscarSolSusPend() {


        Collection<Suscripcion> listaSol = new ArrayList<Suscripcion>();
        Collection<SuscripcionDT> listaSolDT = new ArrayList<SuscripcionDT>();

        String nombQuery = "buscarSuscripcionesPend";
        // from Suscripcion s join fetch s.plan where s.diaCorteSuscripcion = :dia and s.edoSuscripcion = 'ACT'
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listaSol = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {

            return (null);
        }

        if (listaSol.isEmpty()) {

            return listaSol;
        }
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        
        for (Iterator<Suscripcion> it = listaSol.iterator();
                it.hasNext();) {
            Suscripcion susSel = (Suscripcion) it.next();
            SuscripcionDT susDT = new SuscripcionDT();
            susDT.setNumSuscripcion(susSel.getId().toString());
            susDT.setNumCedSuscriptor(susSel.getNumCedSuscriptor());
            susDT.setNombSuscriptor(susSel.getNombSuscriptor() + " " + susSel.getApelSuscriptor());
            susDT.setTelSuscriptor(susSel.getTelSuscriptor());
            susDT.setCelSuscriptor(susSel.getCelSuscriptor());
            susDT.setEdoSuscripcion(susSel.getEdoSolSuscripcion());
            susDT.setFecSuscripcionString(calendarAString(susSel.getFechaMovSol()));
            susDT.setRefSolSuscripcion(susSel.getRefSolSuscripcion());
            susDT.setMtoSolSuscripcionString(val.convertirFloatAString(susSel.getMtoSolSuscripcion()));
            listaSolSusPend.add(susDT);
        }

        return listaSolSusPend;
    }

    public String calendarAString(Calendar fecha) {

        Calendar StringFec = fecha;
        String perDia = "";
        String perMes = "";
        int dMov = fecha.get(Calendar.DAY_OF_MONTH);
        int mMov = fecha.get(Calendar.MONTH) + 1;
        int aMov = fecha.get(Calendar.YEAR);

        if (dMov < 10) {
            perDia = "0" + dMov;
        } else {
            perDia = String.valueOf(dMov);
        }

        if (mMov < 10) {
            perMes = "0" + mMov;
        } else {
            perMes = String.valueOf(mMov);
        }

        String stringFec = perDia + "-" + perMes + "-" + aMov;

        return stringFec;

    }
}
