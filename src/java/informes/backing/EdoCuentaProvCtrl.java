package informes.backing;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.TranFondo;
import informes.modelos.MovimientoCxPDT;

import movimientos.modelos.PagoFacCxP;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

// *********** MODIFICACIONES **************
// 04022019 Modificado para que las notas de credito no sumen al saldo sino lo contrario Método convertirNotaCredDebADT().
public class EdoCuentaProvCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private String idProveedor;
    private String fecDesde;
    private String fecHasta;
    private List listaProveedores = new ArrayList();
    private Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunExt> listaGCE = new ArrayList<GastoComunExt>();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<DeudaPendCxP> listaDeudaPend = new ArrayList<DeudaPendCxP>();
    private Collection<NotaCredDebCxP> listaNotaCredDeb = new ArrayList<NotaCredDebCxP>();
    private Collection<MovimientoCxPDT> listaMovimientoCxPDT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaGCODT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaGCEDT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaGNCDT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaDeudaPendDT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaNotaCredDebDT = new ArrayList<MovimientoCxPDT>();
    private Collection<MovimientoCxPDT> listaDT = new ArrayList<MovimientoCxPDT>();
    private Condominio condominio;
    private Calendar fecDesdeCal;
    private Calendar fecHastaCal;
    private Calendar fecCreacion;
    private Calendar fechaMov;
    private Proveedor prov;
    private Integer idProv;
    private Collection listaTotal = new ArrayList();
    private Boolean saldo = false;

    public EdoCuentaProvCtrl() {
    }

    public void inicializarPanel() {

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

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idProveedor.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el Proveedor ...";
            return mensaje;
        }

        if (this.getFecDesde().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha desde la cual desea el estado de cuenta...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDesde())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFecHasta().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha hasta la cual desea el estado de cuenta...";
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

    public void generarEdoCuentaProveedor(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        idProv = Integer.parseInt(idProveedor);

        prov = buscarProveedor(idProv);
        //--------------------------------------------------------
        // busca las cxp del Proveedor comprendida en el rango dado
        //--------------------------------------------------------
        getCuentasPorPagarPorProveedor(idProv, fecDesdeCal, fecHastaCal);
        //--------------------------------------------------------
        // busca los pagos del prov comprendidos en el rando dado
        //--------------------------------------------------------
        getPagosPorProveedor(idProv, fecDesdeCal, fecHastaCal);
        //------------------------------------------------------------
        // Determina el saldo total desde la fecha de creacion del
        // condominio hasta la fecha "desde" dada. Genera un solo
        // objeto movimientoCxPDT con todo en blanco excepto el saldo.
        // Lo agrega a la lista de mov.
        //------------------------------------------------------------
        getSaldoAnteriorCxPPorProveedor(idProv, fecCreacion, fecDesdeCal);

        listaMovimientoCxPDT = ordenarLista(listaDT);// para ordenar la lista de cxc

        generarReporteEdoCuenta(listaMovimientoCxPDT);
    }

    public void getCuentasPorPagarPorProveedor(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        obtenerFacGastoComunOrd(idProv, fecDesdeCal, fecHastaCal);
        if (!listaGCO.isEmpty()) {
            convertirGCOADT(listaGCO);
        }
        obtenerFacGastoComunExt(idProv, fecDesdeCal, fecHastaCal);
        if (!listaGCE.isEmpty()) {
            convertirGCEADT(listaGCE);
        }
        obtenerFacGastoNoComun(idProv, fecDesdeCal, fecHastaCal);
        if (!listaGNC.isEmpty()) {
            convertirGNCADT(listaGNC);
        }
        obtenerFacDeudaPend(idProv, fecDesdeCal, fecHastaCal);
        if (!listaDeudaPend.isEmpty()) {
            convertirDeudaPendADT(listaDeudaPend);
        }
        obtenerNotaCredDeb(idProv, fecDesdeCal, fecHastaCal);
        if (!listaNotaCredDeb.isEmpty()) {
            convertirNotaCredDebADT(listaNotaCredDeb);
        }

        listaDT.addAll(listaGCODT);
        listaDT.addAll(listaGCEDT);
        listaDT.addAll(listaGNCDT);
        listaDT.addAll(listaDeudaPendDT);
        listaDT.addAll(listaNotaCredDebDT);

    }

    public void getSaldoAnteriorCxPPorProveedor(Integer idProv, Calendar desde, Calendar hasta) throws ParseException {

        listaGCO.clear();
        listaGCODT.clear();
        listaGCE.clear();
        listaGCEDT.clear();
        listaGNC.clear();
        listaGNCDT.clear();
        listaDeudaPend.clear();
        listaDeudaPendDT.clear();
        listaNotaCredDeb.clear();
        listaNotaCredDebDT.clear();

        Double saldoAnterior = new Double(0);

        if (fecCreacion.equals(hasta)) {
            MovimientoCxPDT movimientocxpDT = new MovimientoCxPDT();
            movimientocxpDT.setFecMovCxP(new String());
            movimientocxpDT.setCalFecMovCxP(new GregorianCalendar(0, 0, 0));
            movimientocxpDT.setDesMovCxP("Saldo anterior...");
            movimientocxpDT.setSaldoMovCxP(saldoAnterior);
            movimientocxpDT.setNombProveedor(prov.getNombProveedor());
            listaDT.add(movimientocxpDT);
            return;
        }
        saldo = true;

        obtenerFacGastoComunOrd(idProv, desde, hasta);

        obtenerFacGastoComunExt(idProv, desde, hasta);

        obtenerFacGastoNoComun(idProv, desde, hasta);

        obtenerFacDeudaPend(idProv, desde, hasta);

        obtenerNotaCredDeb(idProv, desde, hasta);

        saldo = false;
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos Comun Ord
        //-------------------------------------------------------
        for (Iterator<GastoComunOrd> it = listaGCO.iterator();
                it.hasNext();) {
            GastoComunOrd fgco = (GastoComunOrd) it.next();
            saldoAnterior = saldoAnterior + fgco.getMtoGastoComunOrd();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos Comun Ext
        //-------------------------------------------------------
        for (Iterator<GastoComunExt> it = listaGCE.iterator();
                it.hasNext();) {
            GastoComunExt fgce = (GastoComunExt) it.next();
            saldoAnterior = saldoAnterior + fgce.getMtoGastoComunExt();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos No Comun
        //-------------------------------------------------------
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            GastoNoComun fgnc = (GastoNoComun) it.next();
            saldoAnterior = saldoAnterior + fgnc.getMtoGastoNoComun();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a deuda pendiente
        //-------------------------------------------------------
        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxP dp = (DeudaPendCxP) it.next();
            saldoAnterior = saldoAnterior + dp.getMtoDeudaPendCxP();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a notas 
        //-------------------------------------------------------
        for (Iterator<NotaCredDebCxP> it = listaNotaCredDeb.iterator();
                it.hasNext();) {
            NotaCredDebCxP ncd = (NotaCredDebCxP) it.next();
            if (ncd.getTipoNotaCredDebCxP().equals("Debito")) {
                saldoAnterior = saldoAnterior + ncd.getMtoNotaCredDebCxP();
            } else {
                saldoAnterior = saldoAnterior - ncd.getMtoNotaCredDebCxP();
            }
        }

        Double pagosPeriodoAnterior = getPagosPorProveedorParaSaldoAnterior(idProv, fecCreacion, fecDesdeCal);

        MovimientoCxPDT movimientocxpDT = new MovimientoCxPDT();

        movimientocxpDT.setFecMovCxP(new String());
        movimientocxpDT.setCalFecMovCxP(new GregorianCalendar(0, 0, 0));
        movimientocxpDT.setDesMovCxP("Saldo anterior...");
        movimientocxpDT.setSaldoMovCxP(saldoAnterior - pagosPeriodoAnterior);
        movimientocxpDT.setNombProveedor(prov.getNombProveedor());

        listaDT.add(movimientocxpDT);

    }

    public void obtenerFacGastoComunOrd(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();

        String nombQuery = "buscarFacGastoComunOrdPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<GastoComunOrd> it = listaTotal.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            fechaMov = convertirFecha(gco.getFecGastoComunOrd());
            if (saldo) {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                    listaGCO.add(gco);
                }
            } else {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                    listaGCO.add(gco);
                }
            }
        }
        return;
    }

    public void obtenerFacGastoComunExt(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();

        String nombQuery = "buscarFacGastoComunExtPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<GastoComunExt> it = listaTotal.iterator();
                it.hasNext();) {
            GastoComunExt gce = (GastoComunExt) it.next();
            fechaMov = convertirFecha(gce.getFecGastoComunExt());
            if (saldo) {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                    listaGCE.add(gce);
                }
            } else {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                    listaGCE.add(gce);
                }
            }
        }
        return;
    }

    public void obtenerFacGastoNoComun(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();

        String nombQuery = "buscarFacGastoNoComunPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<GastoNoComun> it = listaTotal.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            fechaMov = convertirFecha(gnc.getFecGastoNoComun());
            if (saldo) {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                    listaGNC.add(gnc);
                }
            } else {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                    listaGNC.add(gnc);
                }
            }
        }
        return;
    }

    public void obtenerFacDeudaPend(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {


        listaTotal.clear();

        String nombQuery = "buscarFacDeudaPendPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        for (Iterator<DeudaPendCxP> it = listaTotal.iterator();
                it.hasNext();) {
            DeudaPendCxP dpcxp = (DeudaPendCxP) it.next();
            fechaMov = convertirFecha(dpcxp.getFecDeudaPendCxP());
            if (saldo) {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                    listaDeudaPend.add(dpcxp);
                }
            } else {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                    listaDeudaPend.add(dpcxp);
                }
            }
        }
        return;
    }

    public void obtenerNotaCredDeb(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();

        String nombQuery = "buscarNotaCredDebPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<NotaCredDebCxP> it = listaTotal.iterator();
                it.hasNext();) {
            NotaCredDebCxP ncd = (NotaCredDebCxP) it.next();
            fechaMov = convertirFecha(ncd.getFecNotaCredDebCxP());
            if (saldo) {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                    listaNotaCredDeb.add(ncd);
                }
            } else {
                if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                    listaNotaCredDeb.add(ncd);
                }
            }
        }
        return;
    }

    public void getPagosPorProveedor(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();
        Collection listaPagosPer = new ArrayList();

        String nombQuery = "buscarPagoFacCxPPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<PagoFacCxP> it = listaTotal.iterator();
                it.hasNext();) {
            PagoFacCxP pfcxp = (PagoFacCxP) it.next();
            fechaMov = convertirFecha(pfcxp.getPagoCxP().getFecDocPagoCxP());
            if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal) | fechaMov.equals(fecHastaCal))) {
                listaPagosPer.add(pfcxp);
            }
        }
        if (!listaPagosPer.isEmpty()) {
            convertirPagosADT(listaPagosPer);
        }
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<MovimientoCxPDT> listaDesOrdenada = new ArrayList<MovimientoCxPDT>();

        for (Iterator<MovimientoCxPDT> it = lista.iterator();
                it.hasNext();) {
            MovimientoCxPDT mcxc = (MovimientoCxPDT) it.next();
            listaDesOrdenada.add(mcxc);
        }

        Collections.sort(listaDesOrdenada, new ComparadorMovCxP());//devuelve la lista ordenada en listaDesOrdenada

        Collection<MovimientoCxPDT> coleccionOrdenada = new ArrayList<MovimientoCxPDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            MovimientoCxPDT fdt = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    private void convertirGCOADT(Collection listaGCO) throws ParseException {

        listaGCODT.clear();

        for (Iterator<GastoComunOrd> it = listaGCO.iterator();
                it.hasNext();) {

            GastoComunOrd fac = (GastoComunOrd) it.next();

            MovimientoCxPDT movimientocxpDT = new MovimientoCxPDT();

            movimientocxpDT.setId(fac.getId());


            movimientocxpDT.setFecMovCxP(fac.getFecGastoComunOrd());
            movimientocxpDT.setCalFecMovCxP(convertirFecha(fac.getFecGastoComunOrd()));
            movimientocxpDT.setMtoMovCxP(fac.getMtoGastoComunOrd().toString());
            movimientocxpDT.setSaldoMovCxP(fac.getMtoGastoComunOrd());
            movimientocxpDT.setDiscMovCxP("Gasto C.Ord");
            movimientocxpDT.setNumDocPago(fac.getNumDocGastoComunOrd());

            //----------------------------------------------------------------------------------
            // Se busca el concepto del gasto no comun asociado a la factura y luego su
            // descripcion 
            //----------------------------------------------------------------------------------
            Integer idCon = fac.getIdConcepto();
            Concepto concepto = buscarConcepto(idCon);
            movimientocxpDT.setDesMovCxP(concepto.getDescConcepto());
            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : gasto comun ordinario
            //----------------------------------------------------------------------------------

            listaGCODT.add(movimientocxpDT);
        }
        return;
    }

    private void convertirGCEADT(Collection listaGCE) throws ParseException {

        listaGCEDT.clear();

        for (Iterator<GastoComunExt> it = listaGCE.iterator();
                it.hasNext();) {

            GastoComunExt fac = (GastoComunExt) it.next();

            MovimientoCxPDT movimientocxpDT = new MovimientoCxPDT();

            movimientocxpDT.setId(fac.getId());

            movimientocxpDT.setFecMovCxP(fac.getFecGastoComunExt());
            movimientocxpDT.setCalFecMovCxP(convertirFecha(fac.getFecGastoComunExt()));
            movimientocxpDT.setMtoMovCxP(fac.getMtoGastoComunExt().toString());
            movimientocxpDT.setSaldoMovCxP(fac.getMtoGastoComunExt());
            movimientocxpDT.setDiscMovCxP("Gasto C. Ext");
            movimientocxpDT.setNumDocPago(fac.getNumDocGastoComunExt());
            //----------------------------------------------------------------------------------
            // Se busca el concepto del gasto comun Ext asociado a la factura y luego su
            // descripcion 
            //----------------------------------------------------------------------------------
            Integer idCon = fac.getIdConcepto();
            Concepto concepto = buscarConcepto(idCon);
            movimientocxpDT.setDesMovCxP(concepto.getDescConcepto());
            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : gasto comun ext
            //----------------------------------------------------------------------------------

            listaGCEDT.add(movimientocxpDT);
        }

        return;
    }

    private void convertirGNCADT(Collection listaGNC) throws ParseException {

        listaGNCDT.clear();

        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {

            GastoNoComun fac = (GastoNoComun) it.next();

            MovimientoCxPDT movimientocxpDT = new MovimientoCxPDT();

            movimientocxpDT.setId(fac.getId());

            movimientocxpDT.setFecMovCxP(fac.getFecGastoNoComun());
            movimientocxpDT.setCalFecMovCxP(convertirFecha(fac.getFecGastoNoComun()));
            movimientocxpDT.setMtoMovCxP(fac.getMtoGastoNoComun().toString());
            movimientocxpDT.setSaldoMovCxP(fac.getMtoGastoNoComun());
            movimientocxpDT.setDiscMovCxP("Gasto No C.");
            movimientocxpDT.setNumDocPago(fac.getNumDocGastoNoComun());
            //----------------------------------------------------------------------------------
            // Se busca el concepto del gasto comun Ext asociado a la factura y luego su
            // descripcion 
            //----------------------------------------------------------------------------------
            Integer idCon = fac.getIdConcepto();
            Concepto concepto = buscarConcepto(idCon);
            movimientocxpDT.setDesMovCxP(concepto.getDescConcepto());
            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : gasto no comun
            //----------------------------------------------------------------------------------

            listaGNCDT.add(movimientocxpDT);
        }

        return;
    }

    private void convertirDeudaPendADT(Collection listaDeudaPend) throws ParseException {

        listaDeudaPendDT.clear();

        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {

            DeudaPendCxP dpend = (DeudaPendCxP) it.next();

            MovimientoCxPDT movimientoCxPDT = new MovimientoCxPDT();

            movimientoCxPDT.setId(dpend.getId());

            movimientoCxPDT.setFecMovCxP(dpend.getFecDeudaPendCxP());
            movimientoCxPDT.setCalFecMovCxP(convertirFecha(dpend.getFecDeudaPendCxP()));
            movimientoCxPDT.setMtoMovCxP(dpend.getMtoDeudaPendCxP().toString());
            movimientoCxPDT.setSaldoMovCxP(dpend.getMtoDeudaPendCxP());
            movimientoCxPDT.setNombProveedor(dpend.getIdProvDeudaPendCxP().toString());
            movimientoCxPDT.setDiscMovCxP("Deuda Pend");
            movimientoCxPDT.setNumDocPago(dpend.getRefDeudaPendCxP());
            //----------------------------------------------------------------------------------------
            // Se busca el concepto de la deuda pendiente, luego su descripcion para mostrarla en el DT
            //----------------------------------------------------------------------------------------

            movimientoCxPDT.setDesMovCxP(dpend.getDescDeudaPendCxP());
            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : deuda pendiente
            //----------------------------------------------------------------------------------

            listaDeudaPendDT.add(movimientoCxPDT);
        }
        return;
    }

    private void convertirNotaCredDebADT(Collection listaNotaCredDeb) throws ParseException {

        listaNotaCredDebDT.clear();

        for (Iterator<NotaCredDebCxP> it = listaNotaCredDeb.iterator();
                it.hasNext();) {

            NotaCredDebCxP ncd = (NotaCredDebCxP) it.next();

            MovimientoCxPDT movimientoCxPDT = new MovimientoCxPDT();

            movimientoCxPDT.setId(ncd.getId());

            movimientoCxPDT.setFecMovCxP(ncd.getFecNotaCredDebCxP());
            movimientoCxPDT.setCalFecMovCxP(convertirFecha(ncd.getFecNotaCredDebCxP()));
            movimientoCxPDT.setNombProveedor(ncd.getProveedor().getNombProveedor());
            movimientoCxPDT.setNumDocPago(ncd.getNumNotaCredDebCxP());

            if (ncd.getTipoNotaCredDebCxP().equals("Credito")) {   //MOdificación ya que las NC no deben sumar al saldo 04022019
                //movimientoCxPDT.setMtoMovCxP(ncd.getMtoNotaCredDebCxP().toString());
                movimientoCxPDT.setAbonoMovCxP(ncd.getMtoNotaCredDebCxP().toString());
                //movimientoCxPDT.setSaldoMovCxP(ncd.getMtoNotaCredDebCxP());
                movimientoCxPDT.setSaldoMovCxP(ncd.getMtoNotaCredDebCxP() * -1);
                movimientoCxPDT.setDiscMovCxP("Nota Credito");
            } else {
                movimientoCxPDT.setMtoMovCxP(ncd.getMtoNotaCredDebCxP().toString());
                //movimientoCxPDT.setAbonoMovCxP(ncd.getMtoNotaCredDebCxP().toString());
                //movimientoCxPDT.setSaldoMovCxP(ncd.getMtoNotaCredDebCxP() * -1);
                movimientoCxPDT.setSaldoMovCxP(ncd.getMtoNotaCredDebCxP());
                movimientoCxPDT.setDiscMovCxP("Nota Debito");
            }

            //----------------------------------------------------------
            // Se busca el concepto de la NCD, luego su descripcion 
            //----------------------------------------------------------
            Integer idCon = ncd.getIdConcepto();
            Concepto concepto = buscarConcepto(idCon);
            movimientoCxPDT.setDesMovCxP(concepto.getDescConcepto());

            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : NCD
            //----------------------------------------------------------------------------------

            listaNotaCredDebDT.add(movimientoCxPDT);
        }
        return;
    }

    private void convertirPagosADT(Collection listaPagos) throws ParseException {


        for (Iterator<PagoFacCxP> it = listaPagos.iterator();
                it.hasNext();) {

            PagoFacCxP pago = (PagoFacCxP) it.next();

            MovimientoCxPDT movimientoCxPDT = new MovimientoCxPDT();

            movimientoCxPDT.setId(pago.getId());

            movimientoCxPDT.setFecMovCxP(pago.getPagoCxP().getFecDocPagoCxP());
            movimientoCxPDT.setCalFecMovCxP(convertirFecha(pago.getPagoCxP().getFecDocPagoCxP()));
            movimientoCxPDT.setNumDocPago(pago.getPagoCxP().getNumDocPagoCxP());
            movimientoCxPDT.setAbonoMovCxP(pago.getMtoPagadoCxP().toString());
            movimientoCxPDT.setSaldoMovCxP(pago.getMtoPagadoCxP() * -1);
            movimientoCxPDT.setDiscMovCxP("Pago");
            movimientoCxPDT.setNombProveedor(prov.getNombProveedor());
            //--------------------------------------------
            // Se busca la descripcion para bel movimiento
            //--------------------------------------------
            String dis = pago.getDiscFacCxP();
            Integer idFac = pago.getIdFacCxP();

            if (dis.equals("GCO")) {
                GastoComunOrd fgco = buscarFacGastoComunOrd(idFac);
                Integer idCon = fgco.getIdConcepto();
                Concepto con = buscarConcepto(idCon);
                movimientoCxPDT.setDesMovCxP(con.getDescConcepto());
            }
            if (dis.equals("GCE")) {
                GastoComunExt fgce = buscarFacGastoComunExt(idFac);
                Integer idCon = fgce.getIdConcepto();
                Concepto con = buscarConcepto(idCon);
                movimientoCxPDT.setDesMovCxP(con.getDescConcepto());
            }
            if (dis.equals("GNC")) {
                GastoNoComun fgnc = buscarFacGastoNoComun(idFac);
                Integer idCon = fgnc.getIdConcepto();
                Concepto con = buscarConcepto(idCon);
                movimientoCxPDT.setDesMovCxP(con.getDescConcepto());
            }
            if (dis.equals("DPE")) {
                DeudaPendCxP dpe = buscarDeudaPendiente(idFac);
                movimientoCxPDT.setDesMovCxP(dpe.getDescDeudaPendCxP());
            }

            if (dis.equals("NCD")) {
                NotaCredDebCxP ncd = buscarNotaCredDebCxP(idFac);
                Integer idCon = ncd.getIdConcepto();
                Concepto con = buscarConcepto(idCon);
                movimientoCxPDT.setDesMovCxP(con.getDescConcepto());
            }

            //----------------------------------------------------------------------------------
            // Pasa a la lista de movimientos de cxc : pago
            //----------------------------------------------------------------------------------

            listaDT.add(movimientoCxPDT);
        }
    }

    public Double getPagosPorProveedorParaSaldoAnterior(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        listaTotal.clear();
        Collection listaPagos = new ArrayList();

        String nombQuery = "buscarFacPagoCxPPorIdProvRangoAnterior";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTotal = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        for (Iterator<PagoFacCxP> it = listaTotal.iterator();
                it.hasNext();) {
            PagoFacCxP pfcxp = (PagoFacCxP) it.next();
            fechaMov = convertirFecha(pfcxp.getPagoCxP().getFecDocPagoCxP());
            if ((fechaMov.after(fecDesdeCal) | fechaMov.equals(fecDesdeCal)) & (fechaMov.before(fecHastaCal))) {
                listaPagos.add(pfcxp);
            }
        }
        Double totalPagos = 0.00;
        for (Iterator<PagoFacCxP> it = listaPagos.iterator();
                it.hasNext();) {
            PagoFacCxP pcxc = (PagoFacCxP) it.next();
            totalPagos = totalPagos + pcxc.getMtoPagadoCxP();
        }
        return totalPagos;
    }

    public Concepto buscarConcepto(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto;
    }

    public List getProveedoresPorCondominio() {


        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));

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

        if (proveedores.isEmpty()) {
            listaProveedores.clear();
            listaProveedores.add(new SelectItem(new Integer(0), "Sin Configurar"));
            return listaProveedores;
        }
        Proveedor pro;
        for (int i = 0; i < proveedores.size(); i++) {
            pro = (Proveedor) proveedores.get(i);
            listaProveedores.add(new SelectItem(pro.getId(), pro.getNombProveedor()));
        }
        return (List) listaProveedores;
    }

    public Proveedor buscarProveedor(Integer idProv) throws ValidatorException {

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

    public GastoComunOrd buscarFacGastoComunOrd(Integer idFac) throws ValidatorException {

        GastoComunOrd fgco;
        String nombQuery = "buscarGastoComunOrdPorId";
        DaoQuery<GastoComunOrd> dataAcces = new DaoQuery<GastoComunOrd>();
        try {
            fgco = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fgco;
    }

    public GastoComunExt buscarFacGastoComunExt(Integer idFac) throws ValidatorException {

        GastoComunExt fgce;
        String nombQuery = "buscarGastoComunExtPorId";
        DaoQuery<GastoComunExt> dataAcces = new DaoQuery<GastoComunExt>();
        try {
            fgce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fgce;
    }

    public GastoNoComun buscarFacGastoNoComun(Integer idFac) throws ValidatorException {

        GastoNoComun fgnc;
        String nombQuery = "buscarGastoNoComunPorId";
        DaoQuery<GastoNoComun> dataAcces = new DaoQuery<GastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fgnc;
    }

    public DeudaPendCxP buscarDeudaPendiente(Integer idFac) throws ValidatorException {

        DeudaPendCxP dpe;
        String nombQuery = "buscarDeudaPendCxPPorId";
        DaoQuery<DeudaPendCxP> dataAcces = new DaoQuery<DeudaPendCxP>();
        try {
            dpe = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return dpe;
    }

    public NotaCredDebCxP buscarNotaCredDebCxP(Integer idFac) throws ValidatorException {

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

    public TranFondo buscarTranFondo(Integer idFac) throws ValidatorException {

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

    public void generarReporteEdoCuenta(Collection movimientos) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/EdoCuentaProv.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //------------------------------------------------------------
        // Se crea el datasource de movimientos
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceMovimientos;

        dataSourceMovimientos = new JRBeanCollectionDataSource(movimientos);


        String nombProv = prov.getNombProveedor();
        String cedProv = prov.getCedRifProveedor();
        String telProv = prov.getTel1Proveedor();
        String contacto = prov.getContactoProveedor();

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();

        InputStream logo = new FileInputStream(ruta + condominio.getNombLogo());

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("nombProveedor", nombProv);
        parametros.put("cedRifProveedor", cedProv);
        parametros.put("telProveedor", telProv);
        parametros.put("contactoProv", contacto);
        parametros.put("desde", fecDesde);
        parametros.put("hasta", fecHasta);
        parametros.put("logo", logo);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceMovimientos);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
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

    public String enviarEmailEdoCtaProv() throws ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }

        Proveedor proveedor = buscarProveedor(Integer.valueOf(idProveedor));
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setEmailProveedor(proveedor.getEmailProveedor());

        //---------------------------------------------------------------------------------------------------------
        //Guarda el proveedor, la fecha desde y la fecha hasta para pasarlos al caso de uso que emite el correo 
        //---------------------------------------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("IdProveedor", Integer.parseInt(idProveedor));
        ctx.getExternalContext().getSessionMap().put("fechaDesde", fecDesde);
        ctx.getExternalContext().getSessionMap().put("fechaHasta", fecHasta);

        return "email";
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
