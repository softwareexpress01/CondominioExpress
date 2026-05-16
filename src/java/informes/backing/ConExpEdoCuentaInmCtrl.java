package informes.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.FileInputStream;
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
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.MovimientoCxCDT;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoAdelantado;
import movimientos.modelos.PagoFacCxC;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ConExpEdoCuentaInmCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    public String idInmueble;
    private String fecDesde;
    private String fecHasta;
    private List listaInmuebles = new ArrayList();
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection<DeudaPendCxC> listaDeudaPend = new ArrayList<DeudaPendCxC>();
    private Collection<Alicuota> listaAlicuota = new ArrayList<Alicuota>();
    private Collection<FacCuotaEsp> listaFacCuotaEsp = new ArrayList<FacCuotaEsp>();
    private Collection<NotaCredDebCxC> listaNotaDebito = new ArrayList<NotaCredDebCxC>();
    private Collection<MovimientoCxCDT> listaMovimientoCxCDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaMovGNCDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaDeudaPendDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaAlicuotaDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaFacCuotaEspDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaNotaDebitoDT = new ArrayList<MovimientoCxCDT>();
    private Collection<MovimientoCxCDT> listaDT = new ArrayList<MovimientoCxCDT>();
    private Condominio condominio;
    private Calendar fecDesdeCal;
    private Calendar fecHastaCal;
    private Calendar fecCreacion;
    private Inmueble inm;
    private Double SaldoCxP;

    public ConExpEdoCuentaInmCtrl() {
    }

    public void inicializarPanel() {

        this.setFecDesde(null);
        this.setFecHasta(null);

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        beanUsuario.agregar = false;

        return "selcond";
    }

    public String salir() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.agregar = false;

        return "salir";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";

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

        if (!(fecDesdeCal.before(fecHastaCal))) {
            mensaje = "La fecha (desde) debe ser menor a la fecha hasta" + "" + ", por favor intente de nuevo...";
            return mensaje;
        }

        String espacio = " ";
        if (!(fecDesdeCal.after(fecCreacion) | fecDesdeCal.equals(fecCreacion))) {
            mensaje = "La fecha (desde) debe ser mayor o igual a" + espacio + stringFechaCreacion + espacio + ", por favor intente de nuevo...";
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

    public void generarEdoCuentaInmueble(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        idInmueble = Integer.toString(beanUsuario.getIdInm());
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Integer idInm = beanUsuario.getIdInm();     // Consulta Express

        inm = buscarInmueble(idInm);

        //--------------------------------------------------------
        // busca las cxc del inmueble comprendida en el rango dado
        //--------------------------------------------------------
        getCuentasPorCobrarPorInmueble(idInm, fecDesdeCal, fecHastaCal);
        //--------------------------------------------------------
        // busca los pagos del inm comprendidos en el rando dado
        //--------------------------------------------------------
        getPagosPorInmueble(idInm, fecDesdeCal, fecHastaCal);
        //------------------------------------------------------------
        // Determina el saldo total desde la fecha de creacion del
        // condominio hasta la fecha "desde" dada. Genera un solo
        // objeto movimientoCxCDT con todo en blanco excepto el saldo.
        // Lo agrega a la lista de mov.
        //------------------------------------------------------------
        getSaldoAnteriorCxCPorInmueble(idInm, fecCreacion, fecDesdeCal);

        listaMovimientoCxCDT = ordenarLista(listaDT);// para ordenar la lista de cxc
        calcularSaldosCxCInm(listaMovimientoCxCDT);
        generarReporteEdoCuenta(listaMovimientoCxCDT);
    }

    public void getCuentasPorCobrarPorInmueble(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        String nombQuery = "buscarFacGastoNoComunPorIdInmRango";
        listaFacGNC = obtenerFacGastoNoComun(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        listaMovGNCDT = convertirGNCADT(listaFacGNC);

        nombQuery = "buscarFacDeudaPendPorIdInmRango";
        listaDeudaPend = obtenerFacDeudaPend(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        listaDeudaPendDT = convertirDeudaPendADT(listaDeudaPend);

        nombQuery = "buscarAlicuotasPorIdInmRango";
        listaAlicuota = obtenerAlicuota(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        listaAlicuotaDT = convertirAlicuotaADT(listaAlicuota);

        nombQuery = "buscarFacCuotaEspPorIdInmRango";
        listaFacCuotaEsp = obtenerFacCuotaEsp(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        listaFacCuotaEspDT = convertirFacCuotaEspADT(listaFacCuotaEsp);

        listaNotaDebito = obtenerNotaDebito(idInm, fecDesdeCal, fecHastaCal);
        listaNotaDebitoDT = convertirNotaDebitoADT(listaNotaDebito);

        listaDT.addAll(listaMovGNCDT);
        listaDT.addAll(listaDeudaPendDT);
        listaDT.addAll(listaAlicuotaDT);
        listaDT.addAll(listaFacCuotaEspDT);
        listaDT.addAll(listaNotaDebitoDT);


    }

    public void getSaldoAnteriorCxCPorInmueble(Integer idInm, Calendar desde, Calendar hasta) throws ParseException {


        listaFacGNC.clear();
        listaDeudaPend.clear();
        listaDeudaPendDT.clear();
        listaAlicuota.clear();
        listaAlicuotaDT.clear();
        listaFacCuotaEsp.clear();
        listaFacCuotaEspDT.clear();
        listaNotaDebito.clear();
        listaNotaDebitoDT.clear();

        Double saldoAnterior = new Double(0);

        String nombQuery = "buscarFacGastoNoComunPorIdInmRangoCobranza";
        listaFacGNC = obtenerFacGastoNoComun(idInm, desde, hasta, nombQuery);

        nombQuery = "buscarFacDeudaPendPorIdInmRangoSaldo";
        listaDeudaPend = obtenerFacDeudaPendSaldo(idInm, hasta, nombQuery);

        nombQuery = "buscarAlicuotasPorIdInmRangoCobranza";
        listaAlicuota = obtenerAlicuota(idInm, desde, hasta, nombQuery);

        nombQuery = "buscarFacCuotaEspPorIdInmRangoCobranza";
        listaFacCuotaEsp = obtenerFacCuotaEsp(idInm, desde, hasta, nombQuery);

        listaNotaDebito = obtenerNotaDebitoSaldoAnt(idInm, hasta);


        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos no comunes
        //-------------------------------------------------------
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            if (!(fgnc.getEdoFacGastoNoComun().equals("INAC"))) {
                saldoAnterior = saldoAnterior + fgnc.getMtoFacGastoNoComun();
            }
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a deuda pendiente
        //-------------------------------------------------------
        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxC dp = (DeudaPendCxC) it.next();
            saldoAnterior = saldoAnterior + dp.getMtoDeudaPendCxC();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a alicuotas
        //-------------------------------------------------------
        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();
            saldoAnterior = saldoAnterior + alic.getMtoAlicuota();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de cuotas especiales
        //-------------------------------------------------------
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            saldoAnterior = saldoAnterior + fce.getMtoFacCuotaEsp();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a notas de debito
        //-------------------------------------------------------
        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (ncd.getTipoNotaCredDebCxC().equals("Debito")) {
                saldoAnterior = saldoAnterior + ncd.getSaldoNotaCredDebCxC();
            } else {
                saldoAnterior = saldoAnterior - ncd.getSaldoNotaCredDebCxC();
            }
        }

        Double pagosPeriodoAnterior = getPagosPorInmuebleParaSaldoAnterior(idInm, hasta);
        saldoAnterior = saldoAnterior - pagosPeriodoAnterior;

        MovimientoCxCDT movimientocxcDT = new MovimientoCxCDT();

        movimientocxcDT.setFecMovCxC(new String());
        movimientocxcDT.setCalFecMovCxC(new GregorianCalendar(0, 0, 0));
        movimientocxcDT.setDesMovCxC("Saldo anterior...");
        movimientocxcDT.setSaldoMovCxC(saldoAnterior);
        movimientocxcDT.setCodInmueble(inm.getCodInmueble());

        listaDT.add(movimientocxcDT);

    }

    public Collection obtenerFacGastoNoComun(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal, String nombQuery) {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacDeudaPend(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal, String nombQuery) {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacDeudaPendSaldo(Integer idInm, Calendar fecHastaCal, String nombQuery) {

        Collection lista = new ArrayList();
        // Obtiene las facturas de deuda pendiente que tengan fecha (llamada periodo en el query),menor que la fecha hasta
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerAlicuota(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal, String nombQuery) {

        Collection lista = new ArrayList();
        // Obtiene las alicuotas para el inmueble dado (id), los muestra en el panel 2
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacCuotaEsp(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal, String nombQuery) {

        Collection lista = new ArrayList();
        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerNotaDebito(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();
        // Obtiene las notas de debito para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarNotasDebitoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            fechaPago = convertirFecha(ncd.getFecNotaCredDebCxC());
            if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                listaRango.add(ncd);
            }
        }
        return listaRango;
    }

    public Collection obtenerNotaDebitoSaldoAnt(Integer idInm, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();
        // Obtiene las notas de debito para el inmueble dado (id),  que tengan fecha (llamada periodo en el query),menor que la fecha Hasta
        String nombQuery = "buscarNotasDebitoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaNota;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC notaDebito = (NotaCredDebCxC) it.next();
            fechaNota = convertirFecha(notaDebito.getFecNotaCredDebCxC());
            if (fechaNota.before(fecHastaCal)) {
                listaRango.add(notaDebito);
            }
        }
        return listaRango;
    }

    public void getPagosPorInmueble(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();
        // Obtiene las notas de debito para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarFacPagoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        Calendar fechaPago;
        for (Iterator<PagoFacCxC> it = lista.iterator();
                it.hasNext();) {
            PagoFacCxC pagoFac = (PagoFacCxC) it.next();
            fechaPago = convertirFecha(pagoFac.getPagoCxC().getFecDocPagoCxC());
            if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                listaRango.add(pagoFac);
            }
        }
        convertirPagosADT(listaRango);
    }

    public Double getPagosPorInmuebleParaSaldoAnterior(Integer idInm, Calendar fecHastaCal) throws ParseException {

        Collection listaPagos = new ArrayList();
        Collection listaPagosRango = new ArrayList();

        String nombQuery = "buscarFacPagoCxCPorIdInmRangoAnterior";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagos = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<PagoFacCxC> it = listaPagos.iterator();
                it.hasNext();) {
            PagoFacCxC pagoFac = (PagoFacCxC) it.next();
            fechaPago = convertirFecha(pagoFac.getPagoCxC().getFecDocPagoCxC());
            if (fechaPago.before(fecHastaCal)) {
                listaPagosRango.add(pagoFac);
                System.out.println("----------------- pagos:" + pagoFac.getId() + "-" + pagoFac.getMtoPagadoCxC());
            }
        }
        Double totalPagos = 0.00;
        for (Iterator<PagoFacCxC> it = listaPagosRango.iterator();
                it.hasNext();) {
            PagoFacCxC pcxc = (PagoFacCxC) it.next();
            totalPagos = totalPagos + pcxc.getMtoPagadoCxC();
        }
        return totalPagos;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<MovimientoCxCDT> listaDesOrdenada = new ArrayList<MovimientoCxCDT>();

        for (Iterator<MovimientoCxCDT> it = lista.iterator();
                it.hasNext();) {
            MovimientoCxCDT mcxc = (MovimientoCxCDT) it.next();
            listaDesOrdenada.add(mcxc);
        }

        Collections.sort(listaDesOrdenada, new ComparadorMovCxC());//devuelve la lista ordenada en listaDesOrdenada

        Collection<MovimientoCxCDT> coleccionOrdenada = new ArrayList<MovimientoCxCDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            MovimientoCxCDT fdt = listaDesOrdenada.get(i);
            System.out.println("Fecha del movimiento : " + fdt.getFecMovCxC());
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    private Collection<MovimientoCxCDT> convertirGNCADT(Collection listaFacGNC) {

        Collection listaGNCDT = new ArrayList();
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {

            FacGastoNoComun fac = (FacGastoNoComun) it.next();
            if (fac.getEdoFacGastoNoComun().equals("INAC")) {
                continue;
            } else {

                MovimientoCxCDT movimientocxcDT = new MovimientoCxCDT();

                movimientocxcDT.setId(fac.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fac.getFecFacGNC();
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

                movimientocxcDT.setFecMovCxC(stringFechaDp);
                movimientocxcDT.setCalFecMovCxC(fechaFac);
                movimientocxcDT.setMtoMovCxC(fac.getMtoFacGastoNoComun());
                movimientocxcDT.setSaldoMovCxC(0.0);
                movimientocxcDT.setDiscMovCxC("Gasto no Comun");
                movimientocxcDT.setCodInmueble(inm.getCodInmueble());
                //----------------------------------------------------------------------------------
                // Se busca el concepto del gasto no comun asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fac.getGastoNoComun().getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                movimientocxcDT.setDesMovCxC(concepto.getNombConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : gasto no comun
                //----------------------------------------------------------------------------------

                listaGNCDT.add(movimientocxcDT);
            }
        }
        return listaGNCDT;
    }

    private Collection<MovimientoCxCDT> convertirDeudaPendADT(Collection listaDeudaPend) {

        Collection listaDPendDT = new ArrayList();
        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {

            DeudaPendCxC dpend = (DeudaPendCxC) it.next();
            if (dpend == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                MovimientoCxCDT movimientoCxCDT = new MovimientoCxCDT();

                movimientoCxCDT.setId(dpend.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaDPend = dpend.getFecDeudaPendCxC();
                String dia = "";
                String mes = "";
                int diaDpend = fechaDPend.get(Calendar.DAY_OF_MONTH);
                int mesDpend = fechaDPend.get(Calendar.MONTH) + 1;
                int añoDPend = fechaDPend.get(Calendar.YEAR);

                if (diaDpend < 10) {
                    dia = "0" + diaDpend;
                } else {
                    dia = String.valueOf(diaDpend);
                }

                if (mesDpend < 10) {
                    mes = "0" + mesDpend;
                } else {
                    mes = String.valueOf(mesDpend);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoDPend;

                movimientoCxCDT.setFecMovCxC(stringFechaDp);
                movimientoCxCDT.setCalFecMovCxC(fechaDPend);
                movimientoCxCDT.setMtoMovCxC(dpend.getMtoDeudaPendCxC());
                movimientoCxCDT.setSaldoMovCxC(0.0);
                movimientoCxCDT.setCodInmueble(inm.getCodInmueble());
                movimientoCxCDT.setDiscMovCxC("Deuda pendiente");
                //----------------------------------------------------------------------------------------
                // Se busca el concepto de la deuda pendiente, luego su descripcion para mostrarla en el DT
                //----------------------------------------------------------------------------------------
                Integer idDP = dpend.getIdConcepto();
                Concepto concepto = buscarConcepto(idDP);
                movimientoCxCDT.setDesMovCxC(concepto.getNombConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : deuda pendiente
                //----------------------------------------------------------------------------------

                listaDPendDT.add(movimientoCxCDT);
            }
        }
        return listaDPendDT;
    }

    private Collection<MovimientoCxCDT> convertirAlicuotaADT(Collection listaAlicuota) {


        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {

            Alicuota alicuota = (Alicuota) it.next();
            if (alicuota == null) {// Si no hay alicuotas devuelve la lista en blanco
                break;
            } else {

                MovimientoCxCDT movimientoCxCDT = new MovimientoCxCDT();

                movimientoCxCDT.setId(alicuota.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaAlic = alicuota.getPeriodoActual();
                String dia = "";
                String mes = "";
                int diaAlic = fechaAlic.get(Calendar.DAY_OF_MONTH);
                int mesAlic = fechaAlic.get(Calendar.MONTH) + 1;
                int añoAlic = fechaAlic.get(Calendar.YEAR);

                if (diaAlic < 10) {
                    dia = "0" + diaAlic;
                } else {
                    dia = String.valueOf(diaAlic);
                }

                if (mesAlic < 10) {
                    mes = "0" + mesAlic;
                } else {
                    mes = String.valueOf(mesAlic);
                }

                String stringFechaAlic = dia + "-" + mes + "-" + añoAlic;

                movimientoCxCDT.setFecMovCxC(stringFechaAlic);
                movimientoCxCDT.setCalFecMovCxC(fechaAlic);
                movimientoCxCDT.setMtoMovCxC(alicuota.getMtoAlicuota());
                movimientoCxCDT.setSaldoMovCxC(0.0);
                movimientoCxCDT.setCodInmueble(inm.getCodInmueble());
                movimientoCxCDT.setDiscMovCxC("Alicuota");
                movimientoCxCDT.setDesMovCxC(alicuota.getDesAlicuota());
                //----------------------------------------------------------------------------------
                // Pasa a la lista la factura de cxc : alicuota
                //----------------------------------------------------------------------------------

                listaAlicuotaDT.add(movimientoCxCDT);
            }
        }
        return listaAlicuotaDT;
    }

    private Collection<MovimientoCxCDT> convertirFacCuotaEspADT(Collection listaFacCuotasEsp) {

        for (Iterator<FacCuotaEsp> it = listaFacCuotasEsp.iterator();
                it.hasNext();) {

            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            if (fce == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                MovimientoCxCDT movimientoCxCDT = new MovimientoCxCDT();

                movimientoCxCDT.setId(fce.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getPeriodoActual();
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

                movimientoCxCDT.setFecMovCxC(stringFechaDp);
                movimientoCxCDT.setCalFecMovCxC(fechaFac);
                movimientoCxCDT.setMtoMovCxC(fce.getMtoFacCuotaEsp());
                movimientoCxCDT.setSaldoMovCxC(0.0);
                movimientoCxCDT.setCodInmueble(inm.getCodInmueble());
                movimientoCxCDT.setDiscMovCxC("Cuota Especial");
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la cuota especial asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fce.getCuotaEsp().getIdConceptoCuotaEsp();
                Concepto concepto = buscarConcepto(idCon);
                movimientoCxCDT.setDesMovCxC(concepto.getNombConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : factura cuota especial
                //----------------------------------------------------------------------------------

                listaFacCuotaEspDT.add(movimientoCxCDT);
            }
        }
        return listaFacCuotaEspDT;
    }

    private Collection<MovimientoCxCDT> convertirNotaDebitoADT(Collection listaNotaDebito) throws ParseException {

        Integer cero = 0;

        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {

            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (ncd == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                MovimientoCxCDT movimientoCxCDT = new MovimientoCxCDT();

                movimientoCxCDT.setId(ncd.getId());

                movimientoCxCDT.setFecMovCxC(ncd.getFecNotaCredDebCxC());
                movimientoCxCDT.setCalFecMovCxC(convertirFecha(ncd.getFecNotaCredDebCxC()));

                movimientoCxCDT.setCodInmueble(inm.getCodInmueble());

                if (ncd.getTipoNotaCredDebCxC().equals("Debito")) {
                    movimientoCxCDT.setMtoMovCxC(ncd.getMtoNotaCredDebCxC());
                    movimientoCxCDT.setDiscMovCxC("Nota Debito");
                } else {
                    movimientoCxCDT.setAbonoMovCxC(ncd.getSaldoNotaCredDebCxC());
                    movimientoCxCDT.setDiscMovCxC("Nota Credito");
                }
                movimientoCxCDT.setSaldoMovCxC(0.0);
                movimientoCxCDT.setNumDocPago(ncd.getNumNotaCredDebCxC());
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la nota de credito asociado a la factura y luego su
                // descripcion. Para las notas de credito con idCon=cero se asigna la descripcion
                //----------------------------------------------------------------------------------
                Integer idCon = ncd.getIdConcepto();
                if (!idCon.equals(cero)) {
                    Concepto concepto = buscarConcepto(idCon);
                    movimientoCxCDT.setDesMovCxC(concepto.getNombConcepto());
                } else {
                    movimientoCxCDT.setDesMovCxC("Pago excedió mto. cxc selec.");
                }
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : nota de debito
                //----------------------------------------------------------------------------------

                listaNotaDebitoDT.add(movimientoCxCDT);
            }
        }
        return listaNotaDebitoDT;
    }

    private void convertirPagosADT(Collection listaPagos) throws ParseException {

        int cero = 0;

        for (Iterator<PagoFacCxC> it = listaPagos.iterator();
                it.hasNext();) {

            PagoFacCxC pago = (PagoFacCxC) it.next();
            if (pago == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                MovimientoCxCDT movimientoCxCDT = new MovimientoCxCDT();

                movimientoCxCDT.setId(pago.getId());

                movimientoCxCDT.setFecMovCxC(pago.getPagoCxC().getFecDocPagoCxC());
                movimientoCxCDT.setCalFecMovCxC(convertirFecha(pago.getPagoCxC().getFecDocPagoCxC()));
                movimientoCxCDT.setNumDocPago(pago.getPagoCxC().getNumDocPagoCxC());
                movimientoCxCDT.setAbonoMovCxC(pago.getMtoPagadoCxC());
                movimientoCxCDT.setSaldoMovCxC(0.0);

                if (pago.getPagoCxC().getFormaPagoCxC().equals("NCR")) {
                    movimientoCxCDT.setDiscMovCxC("Pago AUTO/NCR");
                } else {
                    movimientoCxCDT.setDiscMovCxC("Pago");
                }

                movimientoCxCDT.setCodInmueble(inm.getCodInmueble());
                //--------------------------------------------
                // Se busca la descripcion para el movimiento
                //--------------------------------------------
                String dis = pago.getDiscFacCxC();
                Integer idFac = pago.getIdFacCxC();

                if (dis.equals("GNC")) {
                    FacGastoNoComun fgnc = buscarFacGastoNoComun(idFac);
                    if (!(fgnc == null)) {
                        Integer idCon = fgnc.getGastoNoComun().getIdConcepto();
                        Concepto con = buscarConcepto(idCon);
                        movimientoCxCDT.setDesMovCxC(con.getNombConcepto());
                    } else {
                        movimientoCxCDT.setDesMovCxC("Gasto No Comun");
                    }
                }
                if (dis.equals("ALI")) {
                    Alicuota alic = buscarAlicuota(idFac);
                    movimientoCxCDT.setDesMovCxC(alic.getDesAlicuota());
                }
                if (dis.equals("CES")) {
                    FacCuotaEsp fce = buscarFacCuotaEsp(idFac);
                    movimientoCxCDT.setDesMovCxC(fce.getDesFacCuotaEsp());
                }
                if (dis.equals("NDE") | dis.equals("NCD")) {
                    NotaCredDebCxC ncd = buscarNotaDebito(idFac);
                    Integer idCon = ncd.getIdConcepto();
                    if (!idCon.equals(cero)) {
                        Concepto con = buscarConcepto(idCon);
                        movimientoCxCDT.setDesMovCxC(con.getNombConcepto());
                    } else {
                        movimientoCxCDT.setDesMovCxC(ncd.getDescNotaCredDebCxC());
                    }
                }
                if (dis.equals("DPE")) {
                    DeudaPendCxC dpe = buscarDeudaPendiente(idFac);
                    Integer idCon = dpe.getIdConcepto();
                    Concepto con = buscarConcepto(idCon);
                    movimientoCxCDT.setDesMovCxC(con.getNombConcepto());
                }
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : pago
                //----------------------------------------------------------------------------------

                listaDT.add(movimientoCxCDT);
            }
        }
    }

    public void calcularSaldosCxCInm(Collection listaMovimientoCxCDT) {

        MovimientoCxCDT mov;

        for (Iterator<MovimientoCxCDT> it = listaMovimientoCxCDT.iterator();
                it.hasNext();) {

            mov = (MovimientoCxCDT) it.next();

            if (mov.getDesMovCxC().equals("Saldo anterior...")) {
                SaldoCxP = mov.getSaldoMovCxC();
                continue;
            } else {
                if (mov.getDiscMovCxC().equals("Pago") | mov.getDiscMovCxC().equals("Nota Credito") | mov.getDiscMovCxC().equals("Pago AUTO/NCR")) {
                    SaldoCxP = SaldoCxP - mov.getAbonoMovCxC();
                } else {
                    SaldoCxP = SaldoCxP + mov.getMtoMovCxC();
                }
                mov.setSaldoMovCxC(SaldoCxP);
            }
        }
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

    public FacGastoNoComun buscarFacGastoNoComun(Integer idFac) throws ValidatorException {

        FacGastoNoComun fgnc;
        String nombQuery = "buscarFacGastoNoComunPorId";
        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fgnc;
    }

    public Alicuota buscarAlicuota(Integer idFac) throws ValidatorException {

        Alicuota alic;
        String nombQuery = "buscarAlicuotaPorId";
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            alic = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return alic;
    }

    public FacCuotaEsp buscarFacCuotaEsp(Integer idFac) throws ValidatorException {

        FacCuotaEsp fce;
        String nombQuery = "buscarFacCuotaEspPorId";
        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fce;
    }

    public NotaCredDebCxC buscarNotaDebito(Integer idFac) throws ValidatorException {

        NotaCredDebCxC ncd;
        String nombQuery = "buscarNotaCredDebCxCPorId";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncd = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return ncd;
    }

    public DeudaPendCxC buscarDeudaPendiente(Integer idFac) throws ValidatorException {

        DeudaPendCxC dpe;
        String nombQuery = "buscarDeudaPendPorId";
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dpe = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return dpe;
    }

    public List getInmueblesPorPropiedad() {

        listaInmuebles.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();
        List<Inmueble> inmuebles = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (inmuebles.isEmpty()) {// Si no hay inmuebles devuelve la lista en blanco
            listaInmuebles.add(new SelectItem(new Integer(0), "Sin configurar"));
            beanUsuario.agregar = true;
            return (List) listaInmuebles;
        }

        listaInmuebles.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }

        return (List) listaInmuebles;
    }

    public Inmueble buscarInmueble(Integer idInm) throws ValidatorException {

        Inmueble inm;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inm = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return inm;
    }

    public void generarReporteEdoCuenta(Collection movimientos) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/EdoCuentaInm.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //------------------------------------------------------------
        // Se crea el datasource de movimientos
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceMovimientos;

        dataSourceMovimientos = new JRBeanCollectionDataSource(movimientos);

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();
        String telInm = inm.getTelInmueble();
        Double alicInm = inm.getAlicInmueble();


        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("codInm", codInm);
        parametros.put("nombProp", nombProp);
        parametros.put("cedProp", cedProp);
        parametros.put("telInm", telInm);
        parametros.put("alicInm", alicInm);
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

    public String enviarEmailEdoCtaInm() throws ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }
        //---------------------------------------------------------------------------------------------------------
        //Guarda el inmueble, la fecha desde y la fecha hasta para pasarlos al caso de uso que emite el correo 
        //---------------------------------------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("IdInmueble", Integer.parseInt(idInmueble));
        ctx.getExternalContext().getSessionMap().put("fechaDesde", fecDesde);
        ctx.getExternalContext().getSessionMap().put("fechaHasta", fecHasta);

        return "email";
    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
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
