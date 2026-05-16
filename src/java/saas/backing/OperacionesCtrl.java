/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;



import saas.modelos.Suscripcion;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import util.*;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import saas.modelos.PagoMensual;
import saas.modelos.PagoMensualDT;

/**
 *
 * @author jesus
 */
public class OperacionesCtrl {

    private Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();

    public void generarReportePagosPendientes() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, InstantiationException, IllegalAccessException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/PagosPendientes.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo de SExpress
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        InputStream logo = new FileInputStream(ruta + "logoTemp.jpg");
        String nombComp = "Software Express";
        //------------------------------------------------------------
        // Se genera el datasource de pagos mensuales pendientes
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagosPendientes;

        Collection pagosPendientes = buscarPagosMensuales();
        if (pagosPendientes.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se han realizado pagos ...por favor, intente mas tarde"));
            return;
        }

        dataSourcePagosPendientes = new JRBeanCollectionDataSource(pagosPendientes);

        HashMap parametros = new HashMap();
        parametros.put("nombCompañia", nombComp);
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourcePagosPendientes);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection buscarPagosMensuales() {

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensuales";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaPagosMensualesDT = inicializarPagosMensualesDT(listaPagosMensuales);
        Collection listaOrd = ordenarLista(listaPagosMensualesDT);

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoMensualDT> listaDesOrdenada = new ArrayList<PagoMensualDT>();

        for (Iterator<PagoMensualDT> it = lista.iterator();
                it.hasNext();) {
            PagoMensualDT pm = (PagoMensualDT) it.next();
            listaDesOrdenada.add(pm);
        }
        Collections.sort(listaDesOrdenada, new ComparadorPagoMensual());

        Collection<PagoMensualDT> coleccionOrdenada = new ArrayList<PagoMensualDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoMensualDT pmo = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pmo);
        }
        return coleccionOrdenada;
    }

    private Collection<PagoMensualDT> inicializarPagosMensualesDT(Collection listaPagosMensuales) {

        Collection listaPagos = new ArrayList();
        Suscripcion sus;
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {

            PagoMensual pm = (PagoMensual) it.next();

            PagoMensualDT pagoMensualDT = new PagoMensualDT();

            pagoMensualDT.setId(pm.getId());
            pagoMensualDT.setBcoPagoMensual(pm.getBcoPagoMensual());
            pagoMensualDT.setCtaPagoMensual(pm.getCtaPagoMensual());

            //----------------------------------------------------------------
            //Se da formato a la fecha del pago para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaPagoMensual = pm.getFecPagoMensual();
            String dia = "";
            String mes = "";
            int diaPM = fechaPagoMensual.get(Calendar.DAY_OF_MONTH);
            int mesPM = fechaPagoMensual.get(Calendar.MONTH) + 1;
            int añoPM = fechaPagoMensual.get(Calendar.YEAR);

            if (diaPM < 10) {
                dia = "0" + diaPM;
            } else {
                dia = String.valueOf(diaPM);
            }

            if (mesPM < 10) {
                mes = "0" + mesPM;
            } else {
                mes = String.valueOf(mesPM);
            }

            String stringFechaDp = dia + "-" + mes + "-" + añoPM;


            pagoMensualDT.setFecPagoMensual(stringFechaDp);
            pagoMensualDT.setRefPagoMensual(pm.getRefPagoMensual());

            //----------------------------------------------------------------
            //Se da formato al monto y al saldo para mostrarlos en el DT
            //----------------------------------------------------------------
            pagoMensualDT.setMtoPagoMensual(val.convertirFloatAString(pm.getMtoPagoMensual()));

            //----------------------------------------------------------------
            //Se da formato a la fecha del mov para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaMov = pm.getFecMovPagoMensual();
            String diaM = "";
            String mesM = "";
            int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
            int mesMov = fechaMov.get(Calendar.MONTH) + 1;
            int añoMov = fechaMov.get(Calendar.YEAR);

            if (diaMov < 10) {
                diaM = "0" + diaMov;
            } else {
                diaM = String.valueOf(diaMov);
            }

            if (mesMov < 10) {
                mesM = "0" + mesMov;
            } else {
                mesM = String.valueOf(mesMov);
            }

            String stringFechaMov = diaM + "-" + mesM + "-" + añoMov;

            pagoMensualDT.setFecMovPagoMensual(stringFechaMov);
            pagoMensualDT.setEdoPagoMensual(pm.getEdoPagoMensual());
            
            sus = pm.getSuscripcion();
            
            pagoMensualDT.setNumSuscriptor(sus.getNumSuscripcion());
            pagoMensualDT.setNumCedSuscriptor(sus.getNumCedSuscriptor());
            pagoMensualDT.setNombSuscriptor(sus.getNombSuscriptor());
            pagoMensualDT.setApelSuscriptor(sus.getApelSuscriptor());

            listaPagos.add(pagoMensualDT);
        }
        return listaPagos;
    }
}
