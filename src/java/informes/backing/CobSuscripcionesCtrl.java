/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

/**
 *
 * @author Frank R.H.
 */
import saas.modelos.Plan;
import datosbasicos.modelos.Propiedad;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
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
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class CobSuscripcionesCtrl extends DaoCrud<Suscripcion, Propiedad, Integer> implements Serializable {

    Collection<SuscripcionDT> listaSusIna = new ArrayList<SuscripcionDT>();
    Collection<Suscripcion> listaSusSel = new ArrayList<Suscripcion>();

    public CobSuscripcionesCtrl() {
    }

    public void generarReporteCobSuscripcion() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/CobSuscripcion.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------

        JRBeanCollectionDataSource dataSourceSusProcesadas;;

        Collection susCorteHoy = buscarSusCorteHoy();

        if (listaSusIna.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Proceso concluido. Suscripciones procesadas: " + listaSusSel.size() + ". No se desactivó ninguna ..."));
            return;

        }

        dataSourceSusProcesadas = new JRBeanCollectionDataSource(susCorteHoy);

        //---------------------------------------------------------
        HashMap parametros = new HashMap();

        //parametros.put("susProcesadas", dataSourceSusProcesadas);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceSusProcesadas);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection buscarSusCorteHoy() {


        Collection<Suscripcion> listaSus = new ArrayList<Suscripcion>();
        
        Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
        String fecCalString = calendarAString(fecCal);

        Integer diaHoy = fecCal.get(Calendar.DAY_OF_MONTH);
        String diaCorte = diaHoy.toString();

        String nombQuery = "buscarSuscripcionFechaCorteHoy";
        // from Suscripcion s join fetch s.plan where s.diaCorteSuscripcion = :dia and s.edoSuscripcion = 'ACT'
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listaSus = dataAcces.buscarListaFetchPorDia(diaCorte, nombQuery);
        } catch (HibernateException he) {

            return (null);
        }

        if (listaSus.isEmpty()) {

            return listaSusIna;
        }

        // Seleccionar suscripciones con fecha de suscripcion <> hoy 

        String fecSusString;
        for (Iterator<Suscripcion> it = listaSus.iterator();
                it.hasNext();) {
            Suscripcion sus = (Suscripcion) it.next();
            fecSusString = calendarAString(sus.getFecSuscripcion());
            if (!(fecCalString.equals(fecSusString))) {
                listaSusSel.add(sus);
            }
        }

        //

        for (Iterator<Suscripcion> it = listaSusSel.iterator();
                it.hasNext();) {
            Suscripcion susSel = (Suscripcion) it.next();
            Plan plan = susSel.getPlan();
            Integer montoPlan = plan.getMtoPlan().intValue();
            if (susSel.getSaldoSuscripcion() >= montoPlan) {
                susSel.setSaldoSuscripcion(susSel.getSaldoSuscripcion() - susSel.getPlan().getMtoPlan());
            } else {
                susSel.setEdoSuscripcion("INA");
            }
            try {
                almacenarOModificarEntidad(susSel);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones (SUSCRIPCION).por favor, intente mas tarde"));
                return listaSusIna;
            }
            if ("INA".equals(susSel.getEdoSuscripcion())) {
                SuscripcionDT susDT = new SuscripcionDT();
                susDT.setNumSuscripcion(susSel.getNumSuscripcion());
                susDT.setNumCedSuscriptor(susSel.getNumCedSuscriptor());
                susDT.setNombSuscriptor(susSel.getNombSuscriptor());
                susDT.setApelSuscriptor(susSel.getApelSuscriptor());
                susDT.setTelSuscriptor(susSel.getTelSuscriptor());
                susDT.setCelSuscriptor(susSel.getCelSuscriptor());
                susDT.setEdoSuscripcion(susSel.getEdoSuscripcion());
                susDT.setFecSuscripcionString(calendarAString(susSel.getFecSuscripcion()));
                listaSusIna.add(susDT);
            }

        }

        return listaSusIna;
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
