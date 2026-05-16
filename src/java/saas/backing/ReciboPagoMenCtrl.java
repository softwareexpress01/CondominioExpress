/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jesus
 */
package saas.backing;

import datosbasicos.modelos.Condominio;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import saas.modelos.Suscripcion;
import java.io.Serializable;
import java.sql.SQLException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import net.sf.jasperreports.engine.JREmptyDataSource;
import saas.modelos.Compañia;
import saas.modelos.PagoMensual;
import saas.modelos.PagoMensualDT;

public class ReciboPagoMenCtrl extends DaoCrud<PagoMensual, Suscripcion, Integer> implements Serializable {

    private Integer id;
    private String nombBanco;
    private String nombCuenta;
    private String fecPagoMensual;
    private String refPagoMensual;
    private String mtoPagoMensual;
    private String numMesesPagoMensual;
    private PagoMensualDT filaSeleccionada;
    private Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();
  

    public ReciboPagoMenCtrl() {
    }

    public void inicializarPanel() {

        setNombBanco(null);
        setNombCuenta(null);
        setFecPagoMensual(null);
        setRefPagoMensual(null);
        setMtoPagoMensual(null);
        setNumMesesPagoMensual(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

        return "salir";

    }

    public Collection getPagosMensualesConf() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idSusc = beanUsuario.getIdSuscripcion();

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensualesConfirmadosPorIdSusc";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidadesPorId(idSusc, nombQuery);
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
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        Suscripcion sus;

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

    public void editarPagoMensualAction() {

        PagoMensualDT pmSel = getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del pago  seleccionado la cual se buscara como "PagoMensualDT" en el map #{PagoMensualDT} 
        ctx.getExternalContext().getSessionMap().put("PagoMensualDT", pmSel);

        setFecPagoMensual(pmSel.getFecPagoMensual());
        setRefPagoMensual(pmSel.getRefPagoMensual());
        setMtoPagoMensual(pmSel.getMtoPagoMensual());
        setNombBanco(pmSel.getBcoPagoMensual());
        setNombCuenta(pmSel.getCtaPagoMensual());
        //   setNumMesesPagoMensual(pmSel.getNumMesesPagoMensual());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = false;

    }

    public Compañia buscarCompañia() {

        Compañia compañia;
        String nombQuery = "buscarCompañia";
        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();
        try {
            compañia = dataAcces.buscarEntidad(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return compañia;
    }

    public void generarReciboPago(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {


        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPagoMen.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------

        //----------------------------------------------------------------------------
        // Se busca el pago en el facesContext para obtener los parametros del reporte
        //----------------------------------------------------------------------------

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoMensualDT pagoMensual = (PagoMensualDT) ctx.getExternalContext().getSessionMap().get("PagoMensualDT");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fechaHoy = new GregorianCalendar();
        String fechaEmision = formatearFecha(fechaHoy);
        String fecPago = pagoMensual.getFecPagoMensual();
        String numDocPago = pagoMensual.getRefPagoMensual();
        String mtoPago = pagoMensual.getMtoPagoMensual();
        String bancoPago = pagoMensual.getBcoPagoMensual();
        String cuentaPago = pagoMensual.getCtaPagoMensual();

        String nombSuscriptor = pagoMensual.getNombSuscriptor();
        String apelSuscriptor = pagoMensual.getApelSuscriptor();
        String numSuscripcion = pagoMensual.getNumSuscriptor();
        String numCedSuscriptor = pagoMensual.getNumCedSuscriptor();
        String desPago = "Pago servicio sistema Condominio Express";
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo de la compañia
        //--------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        String ruta = beanUsuario.getRuta();

        String nombLogo = "6logo para facebook 6.png";
        InputStream logo = new FileInputStream(ruta + nombLogo);

        String nombSello = "sello.jpg";
        InputStream sello = new FileInputStream(ruta + nombSello);
        //--------------------------------------------------------------
        // Se obtiene el numero del recibo de la clase Compañia
        //--------------------------------------------------------------

        Compañia comp = buscarCompañia();
        String numReciboCobro = comp.getNumReciboPago().toString();
        comp.setNumReciboPago(comp.getNumReciboPago() + 1);
        actualizarNroRecibo(comp);
        HashMap parametros = new HashMap();

        parametros.put("fechaEmision", fechaEmision);
        parametros.put("fecPago", fecPago);
        parametros.put("numDocPago", numDocPago);
        parametros.put("mtoPago", mtoPago);
        parametros.put("bancoPago", bancoPago);
        parametros.put("cuentaPago", cuentaPago);
        parametros.put("logo", logo);
        parametros.put("sello", sello);
        parametros.put("numReciboCobro", numReciboCobro);
        parametros.put("nombSuscriptor", nombSuscriptor);
        parametros.put("apelSuscriptor", apelSuscriptor);
        parametros.put("numCedSuscriptor", numCedSuscriptor);
        parametros.put("numSuscripcion", numSuscripcion);
        parametros.put("desPago", desPago);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, new JREmptyDataSource());
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario.ejecutarQueryDT = true;
    }

    public void actualizarNroRecibo(Compañia comp) {

        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();

        try {
            dataAcces.almacenarOModificarEntidad(comp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public String formatearFecha(Calendar fechaFac) {

        //----------------------------------------------------------------
        //Se da formato a la fecha del pago para mostrarla en el reporte
        //----------------------------------------------------------------

        String dia = "";
        String mes = "";
        int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
        int mesFac = fechaFac.get(Calendar.MONTH) + 1;
        int añoFac = fechaFac.get(Calendar.YEAR);

        if (diaFac < 10) {
            dia = "0" + diaFac;
        } else {
            dia = String.valueOf(diaFac);
        }

        if (mesFac < 10) {
            mes = "0" + mesFac;
        } else {
            mes = String.valueOf(mesFac);
        }

        String stringFechaDp = dia + "-" + mes + "-" + añoFac;

        return stringFechaDp;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombBanco(String nombBanco) {
        this.nombBanco = nombBanco;
    }

    public String getNombBanco() {
        return nombBanco;
    }

    public void setNombCuenta(String nombCuenta) {
        this.nombCuenta = nombCuenta;
    }

    public String getNombCuenta() {
        return nombCuenta;
    }

    public void setFecPagoMensual(String fecPagoMensual) {
        this.fecPagoMensual = fecPagoMensual;
    }

    public String getFecPagoMensual() {
        return fecPagoMensual;
    }

    public void setRefPagoMensual(String refPagoMensual) {
        this.refPagoMensual = refPagoMensual;
    }

    public String getRefPagoMensual() {
        return refPagoMensual;
    }

    public void setMtoPagoMensual(String mtoPagoMensual) {
        this.mtoPagoMensual = mtoPagoMensual;
    }

    public String getMtoPagoMensual() {
        return mtoPagoMensual;
    }

    public void setNumMesesPagoMensual(String numMesesPagoMensual) {
        this.numMesesPagoMensual = numMesesPagoMensual;
    }

    public String getNumMesesPagoMensual() {
        return numMesesPagoMensual;
    }
    // .....

    public void setFilaSeleccionada(PagoMensualDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoMensualDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
