/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

import configuracion.modelos.Parametro;
import movimientos.modelos.PagoAdelantado;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Cuenta;
import movimientos.modelos.PagoAdelantadoDT;

import datosbasicos.modelos.Inmueble;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import java.text.ParseException;
import javax.faces.application.FacesMessage;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.Calendar;
import java.util.HashMap;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

public class ReciboPagoAdelCtrl extends DaoCrud<PagoAdelantado, Inmueble, Integer> implements Serializable {

    private String idInPagoAdelantado;
    private HtmlInputText nombPropPagoAdelantado;
    private HtmlInputTextarea descPagoAdelantado;
    private HtmlInputText fecPagoAdelantado;
    private HtmlInputText mtoPagoAdelantado;
    private HtmlInputText formaPagoAdelantado;
    private HtmlInputText refPagoAdelantado;
    private HtmlInputText nombBcoPagoAdelantado;
    private HtmlInputText descCuentaPagoAdelantado;
    private Double saldoPagoAdelantado;
    private String estadoPagoAdelantado;
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private PagoAdelantado pagoAdelantado;
    private Collection<PagoAdelantadoDT> listaPagoAdelantadoDT = new ArrayList<PagoAdelantadoDT>();
    private Collection<PagoAdelantado> listaPagoAdelantado = new ArrayList<PagoAdelantado>();
    Collection<Inmueble> listaInmuebles = new ArrayList<Inmueble>();
    public String refPagoAdelantadoSel;
    private PagoAdelantadoDT filaSeleccionada;

    public ReciboPagoAdelCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        nombPropPagoAdelantado.setValue(null);
        descPagoAdelantado.setValue(null);
        fecPagoAdelantado.setValue(null);
        mtoPagoAdelantado.setValue(null);
        formaPagoAdelantado.setValue(null);
        refPagoAdelantado.setValue(null);
        nombBcoPagoAdelantado.setValue(null);
        descCuentaPagoAdelantado.setValue(null);

    }

    public String irASeleccionarCondominio() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        return "selcond";
    }

    public String salir() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "informes";

    }

    public String validarPanel() throws ParseException {

        String mensaje = new String();

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado);
        if (idInmueble == 0) {
            mensaje = "Debe seleccionar un Inmueble de la lista, por favor intente de nuevo ";
            return mensaje;
        }

        if (this.getFilaSeleccionada() == null) {
            mensaje = "Por favor, seleccione el pago para el cual desea emitir/enviar el recibo ...";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getInmueblesPorPropiedad() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        listaOrdII.clear();

        Integer idProp = beanUsuario.getIdPropActual();

        Collection<Inmueble> listInm = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }


        if (listInm.isEmpty()) {// Si no hay inmuebles devuelve la lista en blanco
            beanUsuario.modificar = true;
            listaOrdII.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaOrdII;
        }


        listaOrd = ordenarLista(listInm);// para ordenar la lista de inmuebles
        listaOrdII.add(new SelectItem(new Integer(0), "Seleccione"));

        for (Iterator<Inmueble> it = listaOrd.iterator();
                it.hasNext();) {
            Inmueble cta = (Inmueble) it.next();
            listaOrdII.add(new SelectItem(cta.getId(), cta.getCodInmueble()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return listaOrdII;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Inmueble> listaDesOrdenada = new ArrayList<Inmueble>();

        for (Iterator<Inmueble> it = lista.iterator();
                it.hasNext();) {
            Inmueble Inmueble1 = (Inmueble) it.next();
            listaDesOrdenada.add(Inmueble1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorInm());

        Collection<Inmueble> coleccionOrdenada = new ArrayList<Inmueble>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Inmueble Inmueble2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(Inmueble2);
        }
        return coleccionOrdenada;
    }

    public HtmlInputText traerNombPropietario() {

        Integer idInmuebleSel = Integer.parseInt(idInPagoAdelantado);

        Inmueble inmueble;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInmuebleSel, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        nombPropPagoAdelantado.setValue(inmueble.getNombPropInmueble());

        return nombPropPagoAdelantado;
    }

    public Collection getPagosAdelantadosPorInmueble() {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInPagoAdelantado == null) {
            idInPagoAdelantado = "0";
        }

        listaPagoAdelantadoDT.clear();

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado);
        String nombQuery = "buscarPagosAdelantadosPorIdInmueble";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagoAdelantado = dataAcces.buscarListaEntidadesPorId(idInmueble, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        if (listaPagoAdelantado.isEmpty()) {
            return listaPagoAdelantadoDT;
        }

        listaPagoAdelantadoDT = inicializarPagoAdelantadoDT(listaPagoAdelantado);

        traerNombPropietario();

        return listaPagoAdelantadoDT;
    }

    private Collection<PagoAdelantadoDT> inicializarPagoAdelantadoDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<PagoAdelantado> it = listaOrd.iterator();
                it.hasNext();) {

            pagoAdelantado = (PagoAdelantado) it.next();
            if (pagoAdelantado == null) {// Si no hay pagos adelantados devuelve la lista en blanco
                break;
            } else {

                Integer idBcoInteger = pagoAdelantado.getIdBcoPagoAdelantado();
                String nombBco = buscarNombBanco(idBcoInteger);

                Integer idCuentaInteger = pagoAdelantado.getIdCuentaPagoAdelantado();
                String numCuenta = buscarNumCuenta(idCuentaInteger);

                PagoAdelantadoDT PagoAdelantadoDT = new PagoAdelantadoDT();
                
                PagoAdelantadoDT.setId(pagoAdelantado.getId());

                PagoAdelantadoDT.setDescPagoAdelantado(pagoAdelantado.getDescPagoAdelantado());
                PagoAdelantadoDT.setFecPagoAdelantado(pagoAdelantado.getFecPagoAdelantado());

                PagoAdelantadoDT.setMtoPagoAdelantado(val.convertirDoubleAString(pagoAdelantado.getMtoPagoAdelantado()));

                PagoAdelantadoDT.setFormaPagoAdelantado(pagoAdelantado.getFormaPagoAdelantado());
                PagoAdelantadoDT.setRefPagoAdelantado(pagoAdelantado.getRefPagoAdelantado());

                PagoAdelantadoDT.setNombBcoPagoAdelantado(nombBco);
                PagoAdelantadoDT.setDescCuentaPagoAdelantado(numCuenta);
                PagoAdelantadoDT.setSaldoPagoAdelantado(val.convertirDoubleAString(pagoAdelantado.getSaldoPagoAdelantado()));
                PagoAdelantadoDT.setEstadoPagoAdelantado(pagoAdelantado.getEstadoPagoAdelantado());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = pagoAdelantado.getFecMovPagoAdelantado();
                String dia = "";
                String mes = "";
                int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (diaMov < 10) {
                    dia = "0" + diaMov;
                } else {
                    dia = String.valueOf(diaMov);
                }

                if (mesMov < 10) {
                    mes = "0" + mesMov;
                } else {
                    mes = String.valueOf(mesMov);
                }

                String stringFechaMov = dia + "-" + mes + "-" + añoMov;

                PagoAdelantadoDT.setFecMovPagoAdelantado(stringFechaMov);
                // ----------------------------------------------------------

                listaPagoAdelantadoDT.add(PagoAdelantadoDT);
            }
        }
        return listaPagoAdelantadoDT;
    }

    public String buscarNombBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarNumCuenta(Integer idCuenta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCuenta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String numCuenta = cuenta.getNumCuenta();
        return numCuenta;
    }

    public Inmueble buscarInmueble(Integer idInmueble) {

        Inmueble inmueble;
        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInmueble, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde  ******** BUSCARiNMUEBLE "));
            return (null);
        }

        return inmueble;
    }

    public void editarPagoAdelantadoAction() {

        PagoAdelantadoDT pagoAdelantadoSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("PagoAdelantadoDT", pagoAdelantadoSel);

        descPagoAdelantado.setValue(pagoAdelantadoSel.getDescPagoAdelantado());
        fecPagoAdelantado.setValue(pagoAdelantadoSel.getFecPagoAdelantado());
        mtoPagoAdelantado.setValue(pagoAdelantadoSel.getMtoPagoAdelantado());
        formaPagoAdelantado.setValue(pagoAdelantadoSel.getFormaPagoAdelantado());
        refPagoAdelantado.setValue(pagoAdelantadoSel.getRefPagoAdelantado());
        nombBcoPagoAdelantado.setValue(pagoAdelantadoSel.getNombBcoPagoAdelantado());
        descCuentaPagoAdelantado.setValue(pagoAdelantadoSel.getDescCuentaPagoAdelantado());

    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoQuery
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoCrudAux<Parametro> dataAcces = new DaoCrudAux<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void generarReciboPagoAdel(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPagoAdel.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //-------------------------------------------------------------------------
        // Se busca el inmueble y el pago para obtener los parametros del reporte
        //-------------------------------------------------------------------------
        Integer idInm = Integer.parseInt(idInPagoAdelantado);
        Inmueble inm = buscarInmueble(idInm);

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoAdelantadoDT pagoAdelantadoDT = (PagoAdelantadoDT) ctx.getExternalContext().getSessionMap().get("PagoAdelantadoDT");

        String fecEmiPago = pagoAdelantadoDT.getFecPagoAdelantado();
        String desPago = pagoAdelantadoDT.getDescPagoAdelantado();
        String numDocPago = pagoAdelantadoDT.getRefPagoAdelantado();
        Double mtoPago = Double.parseDouble(pagoAdelantadoDT.getMtoPagoAdelantado());
        String formaPago = pagoAdelantadoDT.getFormaPagoAdelantado();
        String fecDocPago = pagoAdelantadoDT.getFecPagoAdelantado();
        String bancoPago = pagoAdelantadoDT.getNombBcoPagoAdelantado();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        Condominio condominio = buscarCondominio();
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //--------------------------------------------------------------
        // Se obtiene el numero del recibo y las observaciones de la 
        // la clase parametro del condominio actual.
        //--------------------------------------------------------------
        Integer numReciboCobro;
        String obsReciboCobro;

        Parametro parametro = condominio.getParametro();

        if (parametro != null) {
            numReciboCobro = parametro.getProxReciboCobro();
            obsReciboCobro = parametro.getObsReciboPago();
            parametro.setProxReciboCobro(numReciboCobro + 1);
            actualizarParametro(parametro);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...reporte cancelado"));
            return;
        }

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("codInm", codInm);
        parametros.put("nombProp", nombProp);
        parametros.put("cedProp", cedProp);
        parametros.put("fecEmiPago", fecEmiPago);
        parametros.put("desPago", desPago);
        parametros.put("numDocPago", numDocPago);
        parametros.put("mtoPago", mtoPago);
        parametros.put("formaPago", formaPago);
        parametros.put("fecDocPago", fecDocPago);
        parametros.put("bancoPago", bancoPago);
        parametros.put("logo", logo);
        parametros.put("numReciboCobro", numReciboCobro);
        parametros.put("obsReciboCobro", obsReciboCobro);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, new JREmptyDataSource());
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------

    }

    public String enviarEmailReciboPagoAdelInm() throws ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }
        
        Inmueble inmueble = buscarInmueble(Integer.valueOf(idInPagoAdelantado));
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setEmailPropInmueble(inmueble.getEmailPropInmueble());

        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("IdInmueble", Integer.parseInt(idInPagoAdelantado));

        return "email";
    }

    public void setIdInPagoAdelantado(String idInPagoAdelantado) {
        this.idInPagoAdelantado = idInPagoAdelantado;
    }

    public String getIdInPagoAdelantado() {
        return idInPagoAdelantado;
    }

    public HtmlInputText getDescCuentaPagoAdelantado() {
        return descCuentaPagoAdelantado;
    }

    public HtmlInputTextarea getDescPagoAdelantado() {
        return descPagoAdelantado;
    }

    public HtmlInputText getFecPagoAdelantado() {
        return fecPagoAdelantado;
    }

    public HtmlInputText getFormaPagoAdelantado() {
        return formaPagoAdelantado;
    }

    public HtmlInputText getMtoPagoAdelantado() {
        return mtoPagoAdelantado;
    }

    public HtmlInputText getNombBcoPagoAdelantado() {
        return nombBcoPagoAdelantado;
    }

    public HtmlInputText getNombPropPagoAdelantado() {
        return nombPropPagoAdelantado;
    }

    public HtmlInputText getRefPagoAdelantado() {
        return refPagoAdelantado;
    }

    public void setDescCuentaPagoAdelantado(HtmlInputText descCuentaPagoAdelantado) {
        this.descCuentaPagoAdelantado = descCuentaPagoAdelantado;
    }

    public void setDescPagoAdelantado(HtmlInputTextarea descPagoAdelantado) {
        this.descPagoAdelantado = descPagoAdelantado;
    }

    public void setFecPagoAdelantado(HtmlInputText fecPagoAdelantado) {
        this.fecPagoAdelantado = fecPagoAdelantado;
    }

    public void setFormaPagoAdelantado(HtmlInputText formaPagoAdelantado) {
        this.formaPagoAdelantado = formaPagoAdelantado;
    }

    public void setMtoPagoAdelantado(HtmlInputText mtoPagoAdelantado) {
        this.mtoPagoAdelantado = mtoPagoAdelantado;
    }

    public void setNombBcoPagoAdelantado(HtmlInputText nombBcoPagoAdelantado) {
        this.nombBcoPagoAdelantado = nombBcoPagoAdelantado;
    }

    public void setNombPropPagoAdelantado(HtmlInputText nombPropPagoAdelantado) {
        this.nombPropPagoAdelantado = nombPropPagoAdelantado;
    }

    public void setRefPagoAdelantado(HtmlInputText refPagoAdelantado) {
        this.refPagoAdelantado = refPagoAdelantado;
    }

    public void setSaldoPagoAdelantado(Double saldoPagoAdelantado) {
        this.saldoPagoAdelantado = saldoPagoAdelantado;
    }

    public Double getSaldoPagoAdelantado() {
        return saldoPagoAdelantado;
    }

    public void setEstadoPagoAdelantado(String estadoPagoAdelantado) {
        this.estadoPagoAdelantado = estadoPagoAdelantado;
    }

    public String getEstadoPagoAdelantado() {
        return estadoPagoAdelantado;
    }

    public void setFilaSeleccionada(PagoAdelantadoDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoAdelantadoDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
