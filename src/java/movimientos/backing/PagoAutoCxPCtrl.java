package movimientos.backing;

import datosbasicos.modelos.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.*;
import org.hibernate.HibernateException;
import util.*;

public class PagoAutoCxPCtrl extends DaoCrud<PagoCxP, Proveedor, Integer> implements Serializable {

    private Double PagoCxP = new Double(0);
    private List listaProveedores = new ArrayList();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<DeudaPendCxP> listaDeudaPend = new ArrayList<DeudaPendCxP>();
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<GastoComunOrd>();
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection listaOrd = new ArrayList();
    private Integer idProv;
    private double ceroLogico = 0.01;
    private Integer idPagoCxP;

    private int contGCO = 0;
    private int contGNC = 0;
    private int contDP = 0;

    private String fecHasta = "31-12-2024";
    private Calendar fecHastaCal;
    private Calendar fecFac;

    public PagoAutoCxPCtrl() {
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Collection obtenerFacGastoNoComun(Integer idCond, Integer idProv) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaPorProv = new ArrayList();

        String nombQuery = "buscarGastoNoComunPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        for (Iterator<GastoNoComun> it = lista.iterator();
                it.hasNext();) {

            GastoNoComun gnc = (GastoNoComun) it.next();

            fecFac = convertirFecha(gnc.getFecGastoNoComun());

            if ((fecFac.before(fecHastaCal) | fecFac.equals(fecHastaCal))) {
                if (gnc.getIdProveedor().equals(idProv)) {
                    if (!("CANC".equals(gnc.getEdoGastoNoComun()))) {
                        listaPorProv.add(gnc);
                    }
                }
            }
        }

        return listaPorProv;
    }

    public Collection obtenerFacDeudaPend(Integer idProv) throws ParseException {

        Collection<DeudaPendCxP> lista = new ArrayList<DeudaPendCxP>();
        Collection listaPorProv = new ArrayList();

        String nombQuery = "buscarDeudasPendientesPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        for (Iterator<DeudaPendCxP> it = lista.iterator();
                it.hasNext();) {

            DeudaPendCxP dPend = (DeudaPendCxP) it.next();

            fecFac = convertirFecha(dPend.getFecDeudaPendCxP());
            if ((fecFac.before(fecHastaCal) | fecFac.equals(fecHastaCal))) {
                if (dPend.getIdProvDeudaPendCxP().equals(idProv)) {
                    listaPorProv.add(dPend);
                }
            }
        }

        return listaPorProv;
    }

    public Collection obtenerGCOrd(Integer idCond, Integer idProv) throws ParseException {

        Collection<GastoComunOrd> lista = new ArrayList<GastoComunOrd>();
        Collection listaPorProv = new ArrayList();

        String nombQuery = "buscarTodosGastosComunesOrdPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        for (Iterator<GastoComunOrd> it = lista.iterator();
                it.hasNext();) {

            GastoComunOrd gCOrd = (GastoComunOrd) it.next();
            fecFac = convertirFecha(gCOrd.getFecGastoComunOrd());
            if ((fecFac.before(fecHastaCal) | fecFac.equals(fecHastaCal))) {
                if (gCOrd.getIdProveedor().equals(idProv)) {
                    listaPorProv.add(gCOrd);
                }
            }
        }
        return listaPorProv;
    }

    public String convertirFechaString() {

        Calendar fecha = new GregorianCalendar();
        //----------------------------------------------------------------
        //Se da formato a la fecha del dia para asignarla al pago efectivo
        //----------------------------------------------------------------
        String dia = "";
        String mes = "";
        int diaPago = fecha.get(Calendar.DAY_OF_MONTH);
        int mesPago = fecha.get(Calendar.MONTH) + 1;
        int añoPago = fecha.get(Calendar.YEAR);

        if (diaPago < 10) {
            dia = "0" + diaPago;
        } else {
            dia = String.valueOf(diaPago);
        }

        if (mesPago < 10) {
            mes = "0" + mesPago;
        } else {
            mes = String.valueOf(mesPago);
        }

        String stringFechaPago = dia + "-" + mes + "-" + añoPago;

        return stringFechaPago;

    }

    public Collection getProveedoresPorCondominio(Integer idCond) {

        Collection<Proveedor> listaProv = new ArrayList<Proveedor>();
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

    public void actualizarEdoGNC(PagoFacCxP pagoFac) throws ValidatorException {

        String nombQry = "buscarGastoNoComunPorId";
        GastoNoComun gastoNC = null;

        Integer idGNC = pagoFac.getIdFacCxP();

        DaoQuery<GastoNoComun> dA2 = new DaoQuery<GastoNoComun>();

        try {
            gastoNC = dA2.buscarEntidadPorId(idGNC, nombQry);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        Double cero = 0.0;
        String cancelado = "CANC";
        gastoNC.setSaldoGastoNoComun(cero);
        gastoNC.setEdoGastoNoComun(cancelado);

        DaoQuery<GastoNoComun> dataAcces2 = new DaoQuery<GastoNoComun>();
        try {
            dataAcces2.almacenarOModificarEntidad(gastoNC);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...metodo:actualizarEDoGNC "));

        }

    }

    public void actualizarEdoDeudaPend(PagoFacCxP pagoFac) throws ValidatorException {

        String nombQry = "buscarDeudaPendCxPPorId";
        DeudaPendCxP gastoDP = null;

        Integer idDP = pagoFac.getIdFacCxP();

        DaoQuery<DeudaPendCxP> dA2 = new DaoQuery<DeudaPendCxP>();

        try {
            gastoDP = dA2.buscarEntidadPorId(idDP, nombQry);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        Double cero = 0.0;
        String cancelado = "CANC";
        gastoDP.setSaldoDeudaPendCxP(cero);
        gastoDP.setEstatusDeudaPendCxP(cancelado);

        DaoQuery<DeudaPendCxP> dataAcces2 = new DaoQuery<DeudaPendCxP>();
        try {
            dataAcces2.almacenarOModificarEntidad(gastoDP);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...metodo:actualizarEDoDP "));

        }

    }

    public void actualizarEdoGCO(PagoFacCxP pagoFac) throws ValidatorException {

        String nombQry = "buscarGastoComunOrdPorId";
        GastoComunOrd gastoGCO = null;

        Integer idGCO = pagoFac.getIdFacCxP();

        DaoQuery<GastoComunOrd> dA2 = new DaoQuery<GastoComunOrd>();

        try {
            gastoGCO = dA2.buscarEntidadPorId(idGCO, nombQry);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        Double cero = 0.0;
        String cancelado = "CANC";
        gastoGCO.setSaldoGastoComunOrd(cero);
        gastoGCO.setEdoGastoComunOrd(cancelado);

        DaoQuery<GastoComunOrd> dataAcces2 = new DaoQuery<GastoComunOrd>();
        try {
            dataAcces2.almacenarOModificarEntidad(gastoGCO);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...metodo:actualizarEDoGCO "));

        }

    }

    public void almacenarPagoFacCxP(PagoFacCxP pagoFac) throws ValidatorException {

        DaoQuery<PagoFacCxP> dataAcces = new DaoQuery<PagoFacCxP>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void actualizarEdosYSaldos(PagoFacCxP pagoFac) throws ValidatorException, ParseException {

        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la factura del gasto no comun,
        //-------------------------------------------------------------------
        if (pagoFac.getDiscFacCxP().equals("GNC")) {
            actualizarEdoGNC(pagoFac);
        }
        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la deuda pendiente
        //-------------------------------------------------------------------
        if (pagoFac.getDiscFacCxP().equals("DP")) {
            actualizarEdoDeudaPend(pagoFac);
        }

        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado del Gasto Comun Ord
        //-------------------------------------------------------------------
        if (pagoFac.getDiscFacCxP().equals("GCO")) {
            actualizarEdoGCO(pagoFac);
        }

    }

    public void generarPagoFacCxP(PagoCxP pagoCxP, Integer idFac, String disc) throws ValidatorException, ParseException {

        PagoFacCxP pagoFac;
        pagoFac = new PagoFacCxP();

        pagoFac.setDiscFacCxP(disc);
        pagoFac.setIdFacCxP(idFac);
        pagoFac.setMtoPagadoCxP(pagoCxP.getMtoPagoCxP());
        pagoFac.setPagoCxP(pagoCxP);
        almacenarPagoFacCxP(pagoFac);
        actualizarEdosYSaldos(pagoFac);

    }

    public void registrarPagoCxPGCO(GastoComunOrd gco) throws ValidatorException, ParseException {

        PagoCxP pagoCxPNuevo = new PagoCxP();

        pagoCxPNuevo.setDesPagoCxP("Productos y Servicios");
        pagoCxPNuevo.setNumDocPagoCxP("N/A");

        pagoCxPNuevo.setPeriodoActual(gco.getPerGastoComunOrd());

        pagoCxPNuevo.setMtoPagoCxP(gco.getMtoGastoComunOrd());
        pagoCxPNuevo.setFormaPagoCxP("Transferecia");
        pagoCxPNuevo.setFecDocPagoCxP(gco.getFecGastoComunOrd());

        //Depende del condominio los id del banco y cuenta
        switch (gco.getCondominio().getId()) {
            case 285:
                pagoCxPNuevo.setBancoPagoCxP(471);
                pagoCxPNuevo.setIdCuentaPagoCxP(502);
                break;
            case 290:
                pagoCxPNuevo.setBancoPagoCxP(479);
                pagoCxPNuevo.setIdCuentaPagoCxP(511);
                break;
            case 294:
                pagoCxPNuevo.setBancoPagoCxP(491);
                pagoCxPNuevo.setIdCuentaPagoCxP(522);
                break;
            case 337:
                pagoCxPNuevo.setBancoPagoCxP(609);
                pagoCxPNuevo.setIdCuentaPagoCxP(664);
                break;
        }
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        fecha = convertirFecha(gco.getFecGastoComunOrd());
        pagoCxPNuevo.setFecMovPagoCxP(fecha);
        //-----------------------------------------------------------
        //         Se busca el proveedor para asociarle el pago
        //-----------------------------------------------------------

        Proveedor proveedor = null;//Para buscar el objeto proveedor usa el DaoCrud
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(gco.getIdProveedor());
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-----------------------------------------------------------
        pagoCxPNuevo.setProveedor(proveedor);

        DaoQuery<PagoCxP> dataAcces = new DaoQuery<PagoCxP>();
        Integer idPagoCxPNueva;
        try {
            idPagoCxPNueva = dataAcces.almacenarEntidad(pagoCxPNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxPPorId";
        PagoCxP pagoCxP;
        try {
            pagoCxP = dataAcces.buscarEntidadPorId(idPagoCxPNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxP y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        Integer idFacGCO = gco.getId();
        String disc;
        disc = "GCO";
        generarPagoFacCxP(pagoCxP, idFacGCO, disc);

        idPagoCxP = pagoCxP.id; // Se conserva el id del pago para incluirlo al crear el mov bancario  
        registrarMovBancario(pagoCxP);

    }

    public void registrarPagoCxPGNC(GastoNoComun gnc) throws ValidatorException, ParseException {

        PagoCxP pagoCxPNuevo = new PagoCxP();

        pagoCxPNuevo.setDesPagoCxP("Productos y Servicios");
        pagoCxPNuevo.setNumDocPagoCxP("N/A");

        pagoCxPNuevo.setPeriodoActual(gnc.getPerGastoNoComun());

        pagoCxPNuevo.setMtoPagoCxP(gnc.getMtoGastoNoComun());
        pagoCxPNuevo.setFormaPagoCxP("Transferecia");
        pagoCxPNuevo.setFecDocPagoCxP(gnc.getFecGastoNoComun());

        //Depende del condominio los id del banco y cuenta
        switch (gnc.getCondominio().getId()) {
            case 285:
                pagoCxPNuevo.setBancoPagoCxP(471);
                pagoCxPNuevo.setIdCuentaPagoCxP(502);
                break;
            case 290:
                pagoCxPNuevo.setBancoPagoCxP(479);
                pagoCxPNuevo.setIdCuentaPagoCxP(511);
                break;
            case 294:
                pagoCxPNuevo.setBancoPagoCxP(491);
                pagoCxPNuevo.setIdCuentaPagoCxP(522);
                break;
            case 337:
                pagoCxPNuevo.setBancoPagoCxP(609);
                pagoCxPNuevo.setIdCuentaPagoCxP(664);
                break;
        }
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxPNuevo.setFecMovPagoCxP(fecha);
        //-----------------------------------------------------------
        //         Se busca el proveedor para asociarle el pago
        //-----------------------------------------------------------

        Proveedor proveedor = null;//Para buscar el objeto proveedor usa el DaoCrud
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(gnc.getIdProveedor());
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-----------------------------------------------------------
        pagoCxPNuevo.setProveedor(proveedor);

        DaoQuery<PagoCxP> dataAcces = new DaoQuery<PagoCxP>();
        Integer idPagoCxPNueva;
        try {
            idPagoCxPNueva = dataAcces.almacenarEntidad(pagoCxPNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxPPorId";
        PagoCxP pagoCxP;
        try {
            pagoCxP = dataAcces.buscarEntidadPorId(idPagoCxPNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Integer idFacGNC = gnc.getId();
        String disc;
        disc = "GNC";
        generarPagoFacCxP(pagoCxP, idFacGNC, disc);

        idPagoCxP = pagoCxP.id; // Se conserva el id del pago para incluirlo al crear el mov bancario  
        registrarMovBancario(pagoCxP);

    }

    public void registrarPagoCxPDP(DeudaPendCxP dp, Integer idCond) throws ValidatorException, ParseException {

        PagoCxP pagoCxPNuevo = new PagoCxP();

        pagoCxPNuevo.setDesPagoCxP("Productos y Servicios");
        pagoCxPNuevo.setNumDocPagoCxP("N/A");

        pagoCxPNuevo.setPeriodoActual(convertirFecha(dp.getFecDeudaPendCxP()));

        pagoCxPNuevo.setMtoPagoCxP(dp.getMtoDeudaPendCxP());
        pagoCxPNuevo.setFormaPagoCxP("Transferecia");
        pagoCxPNuevo.setFecDocPagoCxP(dp.getFecDeudaPendCxP());

        //Depende del condominio los id del banco y cuenta
        switch (idCond) {
            case 285:
                pagoCxPNuevo.setBancoPagoCxP(471);
                pagoCxPNuevo.setIdCuentaPagoCxP(502);
                break;
            case 290:
                pagoCxPNuevo.setBancoPagoCxP(479);
                pagoCxPNuevo.setIdCuentaPagoCxP(511);
                break;
            case 294:
                pagoCxPNuevo.setBancoPagoCxP(491);
                pagoCxPNuevo.setIdCuentaPagoCxP(522);
                break;
            case 337:
                pagoCxPNuevo.setBancoPagoCxP(609);
                pagoCxPNuevo.setIdCuentaPagoCxP(664);
                break;
        }
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxPNuevo.setFecMovPagoCxP(fecha);
        //-----------------------------------------------------------
        //         Se busca el proveedor para asociarle el pago
        //-----------------------------------------------------------

        Proveedor proveedor = null;//Para buscar el objeto proveedor usa el DaoCrud
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(dp.getIdProvDeudaPendCxP());
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-----------------------------------------------------------
        pagoCxPNuevo.setProveedor(proveedor);

        DaoQuery<PagoCxP> dataAcces = new DaoQuery<PagoCxP>();
        Integer idPagoCxPNueva;
        try {
            idPagoCxPNueva = dataAcces.almacenarEntidad(pagoCxPNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxPPorId";
        PagoCxP pagoCxP;
        try {
            pagoCxP = dataAcces.buscarEntidadPorId(idPagoCxPNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Integer idFacDP = dp.getId();
        String disc;
        disc = "DP";
        generarPagoFacCxP(pagoCxP, idFacDP, disc);

        idPagoCxP = pagoCxP.id; // Se conserva el id del pago para incluirlo al crear el mov bancario  
        registrarMovBancario(pagoCxP);

    }

    public void registrarMovBancario(PagoCxP pagoCxP) throws ValidatorException, ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        MovBancarioNuevo.setIdBcoMovBancario(pagoCxP.getBancoPagoCxP());
        MovBancarioNuevo.setIdCtaMovBancario(pagoCxP.getIdCuentaPagoCxP());
        MovBancarioNuevo.setTipoOperMovBancario(pagoCxP.getFormaPagoCxP());

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(pagoCxP.getFecDocPagoCxP());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------
        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(pagoCxP.getNumDocPagoCxP());
        Double mto = pagoCxP.getMtoPagoCxP();
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario(pagoCxP.getDesPagoCxP());
        MovBancarioNuevo.setIdPago(pagoCxP.getId());

        //***** buscar cuenta para hacer la asociacion con mov bancario *******
        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(pagoCxP.getIdCuentaPagoCxP(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
        /*_______________________________________________________________________________________*/
        Double nuevoSaldo = cuenta.getSaldoActCuenta() + mto;
        cuenta.setSaldoActCuenta(nuevoSaldo);

        DaoCrudAux daoCrud2 = new DaoCrudAux();
        try {

            daoCrud2.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

    }

    public void pagosAutoDeCxP() throws ValidatorException, ParseException {  //Metodo Principal de la clase

        fecHastaCal = convertirFecha(fecHasta);

        System.out.println(":::::::::::::: INICIO DEL PROCESO DE PAGOS AUTOMATICOS DE CXP ");
        Integer idCond = 285;       // Edififcio La Colina A
        System.out.println(":::::::::::::: EDIFICO COLINA A");
        procesarCondominio(idCond);
        idCond = 290;               // Resid. Tepuy   
        System.out.println(":::::::::::::: TEPUY");
        procesarCondominio(idCond);
        idCond = 294;               // Resid. Daymar III
        System.out.println(":::::::::::::: DAYMAR iii");
        procesarCondominio(idCond);
        idCond = 337;               // Resid. Miravalles
        System.out.println(":::::::::::::: MIRAVALLES");
        procesarCondominio(idCond);

        System.out.println(":::::::::::::: FIN DEL PROCESO DE PAGOS AUTOMATICOS DE CXP ");
    }

    public void procesarCondominio(Integer idCond) throws ValidatorException, ParseException {

        Collection proveedores = getProveedoresPorCondominio(idCond);
        for (Iterator<Proveedor> it = proveedores.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            procesarGCOProv(idCond, prov.getId());
            procesarGNCProv(idCond, prov.getId());
            procesarDeudaPendProv(idCond, prov.getId());
            //Conteo 
            System.out.println(":::::::::::::: EL CONDOMINIO ES " + idCond);
            System.out.println(":::::::::::::: EL PROVEEDOR  ES " + prov.getId() + " " + prov.getNombProveedor() + "TOTAL GCO = " + contGCO);
            System.out.println(":::::::::::::: EL PROVEEDOR  ES " + prov.getId() + " " + prov.getNombProveedor() + "TOTAL GNC = " + contGNC);
            System.out.println(":::::::::::::: EL PROVEEDOR  ES " + prov.getId() + " " + prov.getNombProveedor() + "TOTAL DP  = " + contDP);
            System.out.println("::::::::::::::::::::::::::::::::");
            System.out.println("                                ");
            contGCO = 0;
            contGNC = 0;
            contDP = 0;

        }
    }

    public void procesarGCOProv(Integer idCond, Integer idProv) throws ValidatorException, ParseException {

        listaGCOrd = obtenerGCOrd(idCond, idProv);
        contGCO = listaGCOrd.size();
        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            registrarPagoCxPGCO(gco);
        }

    }

    public void procesarGNCProv(Integer idCond, Integer idProv) throws ValidatorException, ParseException {

        listaGNC = obtenerFacGastoNoComun(idCond, idProv);
        contGNC = listaGNC.size();
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            registrarPagoCxPGNC(gnc);
        }
    }

    public void procesarDeudaPendProv(Integer idCond, Integer idProv) throws ValidatorException, ParseException {

        listaDeudaPend = obtenerFacDeudaPend(idProv);
        contDP = listaDeudaPend.size();
        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxP dp = (DeudaPendCxP) it.next();
            registrarPagoCxPDP(dp, idCond);
        }

    }

}
