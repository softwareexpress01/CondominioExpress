/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author Frank
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.io.FileInputStream;
import javax.faces.application.FacesMessage;
import util.*;
import java.util.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.faces.event.ActionEvent;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Proveedor;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.TranFondo;
import movimientos.modelos.PagoFacCxP;
import movimientos.modelos.PagoCxP;
import informes.modelos.PagosProvDT;

import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import javax.faces.validator.ValidatorException;

public class PagosProvCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    public String idProveedor;
    private String fecDesde;
    private String fecHasta;
    List bancos = new ArrayList();
    String nombProveedor;
    Condominio condominio;
    private List listaProveedores = new ArrayList();
    Calendar fecDesdeCal, fecHastaCal;

    public PagosProvCtrl() {
    }

    public void inicializarPanel() {

        this.setIdProveedor(null);
        this.setFecDesde(null);
        this.setFecHasta(null);

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "informes";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        Calendar fecCreacion;

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------


        if (this.getFecDesde().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha desde la cual desea el informe de pagos...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDesde())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFecHasta().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha hasta la cual desea el informe de pagos...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecHasta())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        buscarCondominio();
        //-----------------------------------------------------------
        //       obtiene la fecha de creacion del condominio
        //-----------------------------------------------------------

        fecCreacion = condominio.getFecCreacion();
        //-----------------------------------------------------------
        //       la fecha obtenida se pasa a string
        //-----------------------------------------------------------
        String diaString;
        String mesString;

        int dia = fecCreacion.get(Calendar.DAY_OF_MONTH);
        int mes = fecCreacion.get(Calendar.MONTH) + 1;
        int año = fecCreacion.get(Calendar.YEAR);

        if (dia < 10) {
            diaString = "0" + dia;
        } else {
            diaString = String.valueOf(dia);
        }

        if (mes < 10) {
            mesString = "0" + mes;
        } else {
            mesString = String.valueOf(mes);
        }

        String stringFechaCreacion = diaString + "-" + mesString + "-" + String.valueOf(año);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fechaHoy = new GregorianCalendar();
        //-----------------------------------------------------------
        //          Convierte el rango de fechas a calendar 
        //-----------------------------------------------------------
        fecDesdeCal = convertirFecha(fecDesde);
        fecHastaCal = convertirFecha(fecHasta);
        //-----------------------------------------------------------
        //     Valida que las fechas esten en un rango valido
        //-----------------------------------------------------------

        if (fecHastaCal.before(fecDesdeCal)) {
            mensaje = "La fecha (hasta) es menor que la fecha (desde), por favor intente de nuevo...";
            return mensaje;
        }

        if (!(fecDesdeCal.after(fecCreacion) | fecDesdeCal.equals(fecCreacion))) {
            mensaje = "La fecha (desde) debe ser mayor o igual a" + "" + stringFechaCreacion + "" + ", por favor intente de nuevo...";
            return mensaje;
        }
        if (!(fecHastaCal.before(fechaHoy) | fecHastaCal.equals(fechaHoy))) {
            mensaje = "La fecha (hasta) debe ser menor o igual a la fecha de hoy, por favor intente de nuevo...";
            return mensaje;
        }
        fecHastaCal = convertirFechaHasta(fecHasta);


        return mensaje;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Calendar convertirFechaHasta(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 25);
        cal.set(Calendar.MILLISECOND, 0);

        System.out.println("la fecha hasta es: " + cal.toString());
        return cal;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void generarPagosProv(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        generarReportePagosProveedor();
    }

    public List getProveedoresPorCondominio() {


        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Todos"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Proveedores para el  propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();
        List<Proveedor> proveedores = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            proveedores = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (proveedores.isEmpty()) {// Si no hay proveeedores devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaProveedores.clear();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun Proveedor para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }

        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor prov = (Proveedor) proveedores.get(i);
            listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaProveedores;
    }

    public Proveedor buscarProveedor(Integer idProv) throws ValidatorException {

        Proveedor prov;

        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            prov = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return prov;
    }

    public void generarReportePagosProveedor() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/PagosProv.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = getPagosRealizadosPorProv();
        String mensaje;
        if (filasReporte.isEmpty()) {
            mensaje = "No hay movimientos registrados para el Proveedor y/o período seleccionado ";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCondominio = beanUsuario.getCondominioActual();

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio = buscarCondominio(idCond);
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);


        HashMap parametro = new HashMap();
        parametro.put("condominio", nombCondominio);
        parametro.put("logo", logo);
        parametro.put("desde", fecDesde);
        parametro.put("hasta", fecHasta);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getPagosRealizadosPorProv() throws ParseException {

        List listaProv = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<Proveedor> proveedores = new ArrayList();
        Collection<PagosProvDT> listaPagos; // contiene PagosCXP con PagoFacCxP 
        Collection<PagosProvDT> listaPagosParcial = new ArrayList<PagosProvDT>(); // lista que devuelve el método igual que listaPagos pero con datos de las facturas
        Collection<PagosProvDT> listaPagosTotal = new ArrayList<PagosProvDT>();

        Integer idProvInt = Integer.valueOf(idProveedor).intValue();
        if (idProvInt == 0) { // Todos 
            proveedores = getProveedoresPorIdCond();
        } else {
            Proveedor proveedorSel = buscarProveedor(idProvInt);
            proveedores.add(proveedorSel);

        }

        if (proveedores.isEmpty()) {
            return (List) listaOrd;
        }
        for (Iterator<Proveedor> it = proveedores.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            listaProv.add(prov);
        }

        /*
         * -----POR CADA PROVEEDOR SE BUSCAN PAGOS REALIZADOS -------------------------------------------------------------
         */

        Integer idProv;
        Proveedor provObj;
        for (int i = 0; i < proveedores.size(); i++) {

            provObj = (Proveedor) listaProv.get(i);
            idProv = provObj.getId();
            listaPagos = getPagosCxP(idProv, fecDesdeCal, fecHastaCal);
            listaPagosParcial.addAll(listaPagos);
            listaPagos.clear();

        }

        Integer idFac;
        String disc;
        Integer idConcepto;
        for (Iterator<PagosProvDT> it = listaPagosParcial.iterator();
                it.hasNext();) {
            PagosProvDT pagoParcial = (PagosProvDT) it.next();

            PagosProvDT pagoTotal = new PagosProvDT();
            pagoTotal.setNombProveedor(pagoParcial.getNombProveedor());
            pagoTotal.setFecFac(pagoParcial.getFecFac());
            pagoTotal.setNumDoc(pagoParcial.getNumDoc());
            pagoTotal.setNombBanco(pagoParcial.getNombBanco());
            pagoTotal.setFecMovPago(pagoParcial.getFecMovPago());
            pagoTotal.setMtoPago(pagoParcial.getMtoPago());
            pagoTotal.setIdFac(pagoParcial.getIdFac());
            pagoTotal.setDisc(pagoParcial.getDisc());

            idFac = pagoParcial.getIdFac();
            disc = pagoParcial.getDisc();
            if ("GCO".equals(disc)) {
                GastoComunOrd gco;
                gco = getGCOrd(idFac);
                if (!(gco == null)) {
                    pagoTotal.setNumFac(gco.getNumDocGastoComunOrd());
                    idConcepto = gco.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(gco.getMtoGastoComunOrd());
                    pagoTotal.setSaldoFac(gco.getSaldoGastoComunOrd());
                }
            }
            if ("GCE".equals(disc)) {
                GastoComunExt gce;
                gce = getGCExt(idFac);
                if (!(gce == null)) {
                    pagoTotal.setNumFac(gce.getNumDocGastoComunExt());
                    idConcepto = gce.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(gce.getMtoGastoComunExt());
                    pagoTotal.setSaldoFac(gce.getSaldoGastoComunExt());
                }
            }
            if ("GNC".equals(disc)) {
                GastoNoComun gnc;
                gnc = getGNC(idFac);
                if (!(gnc == null)) {
                    pagoTotal.setNumFac(gnc.getNumDocGastoNoComun());
                    idConcepto = gnc.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(gnc.getMtoGastoNoComun());
                    pagoTotal.setSaldoFac(gnc.getSaldoGastoNoComun());
                }
            }
            if ("DPE".equals(disc)) {
                DeudaPendCxP dep;
                dep = getDeudaPend(idFac);
                if (!(dep == null)) {
                    pagoTotal.setNumFac(dep.getRefDeudaPendCxP());
                    pagoTotal.setConcepto(dep.getDescDeudaPendCxP());
                    pagoTotal.setMtoFac(dep.getMtoDeudaPendCxP());
                    pagoTotal.setSaldoFac(dep.getSaldoDeudaPendCxP());
                }
            }
            if ("NCD".equals(disc)) {
                NotaCredDebCxP ncd;
                ncd = getNotaCredDebCxP(idFac);
                if (!(ncd == null)) {
                    pagoTotal.setNumFac(ncd.getNumNotaCredDebCxP());
                    idConcepto = ncd.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(ncd.getMtoNotaCredDebCxP());
                    pagoTotal.setSaldoFac(ncd.getSaldoNotaCredDebCxP());
                }
            }
            /*
             * if ("TFO".equals(disc)) { TranFondo tfo; tfo =
             * getTranFondo(idFac); pagoTotal.setNumFac(tfo.getDocTranFondo());
             * idConcepto = tfo.getIdConceptoTranFondo();
             * pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
             * pagoTotal.setMtoFac(tfo.getMtoTranFondo());
             * pagoTotal.setSaldoFac(tfo.getSaldoTranFondo());
            }
             */
            listaPagosTotal.add(pagoTotal);
        }

        listaOrd = ordenarListaPagos(listaPagosTotal);// para ordenar la lista de cuentas por pagar*/
        return listaOrd;
    }

    public Collection getPagosCxP(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection<PagosProvDT> listaPagos = new ArrayList<PagosProvDT>();
        Collection<PagoFacCxP> pagos;
        Collection<PagoFacCxP> pagosRango = new ArrayList<PagoFacCxP>();;

        String nombQuery = "buscarPagoFacCxPPorIdProveedorRangoFec";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            pagos = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaPagos;
        }

        if (pagos.isEmpty()) {
            return (List) pagos;
        }
        Calendar fechaPagoCxP;
        for (Iterator<PagoFacCxP> it = pagos.iterator();
                it.hasNext();) {
            PagoFacCxP pagoFacCxP = (PagoFacCxP) it.next();
            fechaPagoCxP = convertirFecha(pagoFacCxP.getPagoCxP().getFecDocPagoCxP());
            if ((fechaPagoCxP.after(fecDesdeCal) | fechaPagoCxP.equals(fecDesdeCal)) & (fechaPagoCxP.before(fecHastaCal) | fechaPagoCxP.equals(fecHastaCal))) {
                pagosRango.add(pagoFacCxP);
            }
        }

        PagoCxP pagoM;
        Integer idBanco;
        Integer idProvM;

        Calendar fechaG;
        for (Iterator<PagoFacCxP> it = pagosRango.iterator();
                it.hasNext();) {

            PagoFacCxP pagoDet = (PagoFacCxP) it.next();

            PagosProvDT pagoProv = new PagosProvDT();
            pagoM = pagoDet.getPagoCxP();
            idProvM = pagoM.getProveedor().getId();
            pagoProv.setNombProveedor(buscarNombProveedor(idProvM));
            pagoProv.setFecFac(pagoM.getFecDocPagoCxP());
            pagoProv.setNumDoc(pagoM.getNumDocPagoCxP());
            idBanco = pagoM.getBancoPagoCxP();
            pagoProv.setNombBanco(buscarNombreBanco(idBanco));
            fechaG = pagoM.getFecMovPagoCxP();
            pagoProv.setFecMovPago(CalendarAString(fechaG));
            pagoProv.setMtoPago(pagoDet.getMtoPagadoCxP());

            pagoProv.setIdFac(pagoDet.getIdFacCxP());
            pagoProv.setDisc(pagoDet.getDiscFacCxP());

            listaPagos.add(pagoProv);

        }
        return listaPagos;
    }

    public String buscarNombConcepto(Integer idConc) {

        Concepto concepto;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String descConcepto = concepto.getDescConcepto();
        return descConcepto;
    }

    public GastoComunOrd getGCOrd(Integer idFac) {

        GastoComunOrd gco;

        String nombQuery = "buscarGastoComunOrdPorId";
        DaoQuery<GastoComunOrd> dataAcces = new DaoQuery<GastoComunOrd>();
        try {
            gco = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return gco;
    }

    public GastoComunExt getGCExt(Integer idFac) {

        GastoComunExt gce;

        String nombQuery = "buscarGastoComunExtPorId";
        DaoQuery<GastoComunExt> dataAcces = new DaoQuery<GastoComunExt>();
        try {
            gce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return gce;
    }

    public GastoNoComun getGNC(Integer idFac) {

        GastoNoComun gnc;

        String nombQuery = "buscarGastoNoComunPorId";
        DaoQuery<GastoNoComun> dataAcces = new DaoQuery<GastoNoComun>();
        try {
            gnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return gnc;
    }

    public DeudaPendCxP getDeudaPend(Integer idFac) {

        DeudaPendCxP dep = null;

        String nombQuery = "buscarDeudaPendCxPPorId";
        DaoQuery<DeudaPendCxP> dataAcces = new DaoQuery<DeudaPendCxP>();
        try {
            dep = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return dep;
        }

        return dep;
    }

    public NotaCredDebCxP getNotaCredDebCxP(Integer idFac) {

        NotaCredDebCxP ncd;

        String nombQuery = "buscarNotaCredDebCxPPorId";
        DaoQuery<NotaCredDebCxP> dataAcces = new DaoQuery<NotaCredDebCxP>();
        try {
            ncd = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return ncd;
    }

    public TranFondo getTranFondo(Integer idFac) {

        TranFondo tfo;

        String nombQuery = "buscarTranFondoPorId";
        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();
        try {
            tfo = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return tfo;
    }

    public Collection getProveedoresPorIdCond() {

        // Obtiene los Proveedores para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Proveedor> listaProv;
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaProv = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listaProv;
    }

    public Collection ordenarListaPagos(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagosProvDT> listaDesOrdenada = new ArrayList<PagosProvDT>();

        for (Iterator<PagosProvDT> it = lista.iterator();
                it.hasNext();) {
            PagosProvDT con = (PagosProvDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagosProvDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagosProvDT> coleccionOrdenada = new ArrayList<PagosProvDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagosProvDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public String buscarNombreBanco(Integer idBanco) {

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

    public String buscarNombProveedor(Integer idProv) {

        Proveedor proveedor;
        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            proveedor = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombProv = proveedor.getNombProveedor();
        return nombProv;
    }

    public String CalendarAString(Calendar fecha) {

        //----------------------------------------------------------------
        //Se da formato a la fecha del mov para mostrarla en el data table
        //----------------------------------------------------------------
        Calendar fechaFac = fecha;
        String dia;
        String mes;
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

    public Condominio buscarCondominio(Integer idCond) throws ValidatorException {

        Condominio condominio;
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

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setFecDesde(String fecDesde) {
        this.fecDesde = fecDesde;
    }

    public String getFecDesde() {
        return fecDesde;
    }

    public void setFecHasta(String fecHasta) {
        this.fecHasta = fecHasta;
    }

    public String getFecHasta() {
        return fecHasta;
    }
}