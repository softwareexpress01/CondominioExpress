package movimientos.backing;

import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.Collection;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.PagoCxP;
import movimientos.modelos.PagoCxPDT;
import movimientos.modelos.PagoFacCxP;
import movimientos.modelos.MovBancario;
import movimientos.modelos.TranFondo;
import datosbasicos.modelos.Cuenta;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoCrudAux;
import util.DaoQuery;
import util.JSFUtil;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Frank Rojas H.
 */
public class ElimPagoCxPCtrl extends DaoCrud<PagoCxP, Inmueble, Integer> implements Serializable {

    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Integer idCta;
    private Double mto;
    private double ceroLogico=0.01; 

    public ElimPagoCxPCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.revertir = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        return "regresar";

    }

    @SuppressWarnings("empty-statement")
    public void actualizarEdoFacGNC(PagoFacCxP pagofac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";
        Integer idGNC = pagofac.getIdFacCxP();
        GastoNoComun gnc = new GastoNoComun();

        String nombQuery = "buscarGastoNoComunPorId";
        DaoQuery<GastoNoComun> dataAcces = new DaoQuery<GastoNoComun>();
        try {
            gnc = dataAcces.buscarEntidadPorId(idGNC, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        gnc.setSaldoGastoNoComun(gnc.getSaldoGastoNoComun() + pagofac.getMtoPagadoCxP());
        if ((gnc.getMtoGastoNoComun()) - (gnc.getSaldoGastoNoComun()) < ceroLogico) {;
            gnc.setSaldoGastoNoComun(gnc.getMtoGastoNoComun());
            gnc.setEdoGastoNoComun("PEND");
            gnc.setEdoGralFac("PEND");
        } else {
            gnc.setEdoGastoNoComun("EPRO");
        }

        DaoQuery<GastoNoComun> dataAcces2 = new DaoQuery<GastoNoComun>();
        try {
            dataAcces2.almacenarOModificarEntidad(gnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        // ******************** Actualizar el edo de las facturas asociadas al GNC *****************

        nombQuery = "buscarFacGastoNoComunPorIdGNC";
        Integer idGNCint = pagofac.getIdFacCxP();
        DaoQuery<Collection> dataAcces3 = new DaoQuery<Collection>();
        try {
            listaFacGNC = dataAcces3.buscarListaEntidadesPorId(idGNCint, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        if (!(listaFacGNC.isEmpty())) {

            for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                    it.hasNext();) {

                FacGastoNoComun fGNC = (FacGastoNoComun) it.next();
                if (fGNC.getEdoFacGastoNoComun().equals("PEND")) {
                    fGNC.setEdoFacGastoNoComun("PEND");
                    DaoQuery<FacGastoNoComun> dataAccesGNC = new DaoQuery<FacGastoNoComun>();
                    try {
                        dataAccesGNC.almacenarOModificarEntidad(fGNC);
                    } catch (HibernateException he) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                        return;
                    }

                }
            }
        }
        // ******************** FIN Actualizar el edo de las facturas asociadas al GNC *****************


    }

    public void actualizarEdoDeudaPend(PagoFacCxP pagofac) throws ValidatorException {

        DeudaPendCxP dpend = null;
        String nombQuery = "buscarDeudaPendCxPPorId";
        DaoQuery<DeudaPendCxP> dataAcces = new DaoQuery<DeudaPendCxP>();
        try {
            dpend = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxP(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        dpend.setSaldoDeudaPendCxP(dpend.getSaldoDeudaPendCxP() + pagofac.getMtoPagadoCxP());
        if ((dpend.getMtoDeudaPendCxP() - (dpend.getSaldoDeudaPendCxP())) < ceroLogico) {
            dpend.setSaldoDeudaPendCxP(dpend.getMtoDeudaPendCxP());
            dpend.setEstatusDeudaPendCxP("PEND");
        } else {
            dpend.setEstatusDeudaPendCxP("EPRO");
        }
        DaoQuery<DeudaPendCxP> dataAcces2 = new DaoQuery<DeudaPendCxP>();

        try {
            dataAcces2.almacenarOModificarEntidad(dpend);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoGCOrd(PagoFacCxP pagofac) throws ValidatorException {

        GastoComunOrd gastoCOrd = null;
        String nombQuery = "buscarGastoComunOrdPorId";
        DaoQuery<GastoComunOrd> dataAcces = new DaoQuery<GastoComunOrd>();
        try {
            gastoCOrd = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxP(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        gastoCOrd.setSaldoGastoComunOrd(gastoCOrd.getSaldoGastoComunOrd() + pagofac.getMtoPagadoCxP());
        if ((gastoCOrd.getMtoGastoComunOrd() - (gastoCOrd.getSaldoGastoComunOrd())) < ceroLogico) {
            gastoCOrd.setSaldoGastoComunOrd(gastoCOrd.getMtoGastoComunOrd());
            gastoCOrd.setEdoGastoComunOrd("PEND");
        } else {
            gastoCOrd.setEdoGastoComunOrd("EPRO");
        }
        DaoQuery<GastoComunOrd> dataAcces2 = new DaoQuery<GastoComunOrd>();

        try {
            dataAcces2.almacenarOModificarEntidad(gastoCOrd);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoGCExt(PagoFacCxP pagofac) throws ValidatorException {

        GastoComunExt gastoCExt = null;
        String nombQuery = "buscarGastoComunExtPorId";
        DaoQuery<GastoComunExt> dataAcces = new DaoQuery<GastoComunExt>();
        try {
            gastoCExt = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxP(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia <0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        gastoCExt.setSaldoGastoComunExt(gastoCExt.getSaldoGastoComunExt() + pagofac.getMtoPagadoCxP());
        if ((gastoCExt.getMtoGastoComunExt() - (gastoCExt.getSaldoGastoComunExt())) < ceroLogico) {
            gastoCExt.setSaldoGastoComunExt(gastoCExt.getMtoGastoComunExt());
            gastoCExt.setEdoGastoComunExt("PEND");
        } else {
            gastoCExt.setEdoGastoComunExt("EPRO");
        }
        DaoQuery<GastoComunExt> dataAcces2 = new DaoQuery<GastoComunExt>();

        try {
            dataAcces2.almacenarOModificarEntidad(gastoCExt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //*********************** ACTUAIZACION DEL FONDO CORRESPONDIENTE ********************************

        Fondo fondo = null;
        Integer idFondo = gastoCExt.getIdFondo();
        String nombQuery2 = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces3.buscarEntidadPorId(idFondo, nombQuery2);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        if (fondo != null) {
            fondo.setSaldoFondo(fondo.getSaldoFondo() + pagofac.getMtoPagadoCxP());
            try {
                dataAcces3.almacenarOModificarEntidad(fondo);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
        }

    }

    public void actualizarEdoNotaCredDeb(PagoFacCxP pagofac) throws ValidatorException {

        NotaCredDebCxP nCD = null;
        String nombQuery = "buscarNotaCredDebCxPPorId";
        DaoQuery<NotaCredDebCxP> dataAcces = new DaoQuery<NotaCredDebCxP>();
        try {
            nCD = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxP(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        nCD.setSaldoNotaCredDebCxP(nCD.getSaldoNotaCredDebCxP() + pagofac.getMtoPagadoCxP());
        if ((nCD.getMtoNotaCredDebCxP() - (nCD.getSaldoNotaCredDebCxP())) < ceroLogico) {
            nCD.setSaldoNotaCredDebCxP(nCD.getMtoNotaCredDebCxP());
            nCD.setEdoNotaCredDebCxP("PEND");
        } else {
            nCD.setEdoNotaCredDebCxP("EPRO");
        }
        DaoQuery<NotaCredDebCxP> dataAcces2 = new DaoQuery<NotaCredDebCxP>();

        try {
            dataAcces2.almacenarOModificarEntidad(nCD);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void actualizarEdoYSaldoFac(PagoCxP pagoCxP) throws ValidatorException {


        for (Iterator<PagoFacCxP> it = pagoCxP.pagoFacCxP.iterator();
                it.hasNext();) {
            PagoFacCxP fac = (PagoFacCxP) it.next();
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la factura del gasto no comun,
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxP().equals("GNC")) {
                actualizarEdoFacGNC(fac);
            }
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la deuda pendiente
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxP().equals("DPE")) {
                actualizarEdoDeudaPend(fac);
            }
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado del Gasto comun Ord
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxP().equals("GCO")) {
                actualizarEdoGCOrd(fac);
            }
            //---------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la Nota de Cred/Deb
            //---------------------------------------------------------------------
            if (fac.getDiscFacCxP().equals("NCD")) {
                actualizarEdoNotaCredDeb(fac);
            }
            //---------------------------------------------------------------------
            // Se actualiza el saldo y el estado del Gasto Comun Extraordinario
            //---------------------------------------------------------------------
            if (fac.getDiscFacCxP().equals("GCE")) {
                actualizarEdoGCExt(fac);
            }

        }
    }

    public PagoCxP buscarPagoFacCxP(Integer id) throws ValidatorException {

        PagoCxP pagoCxP;
        String nombQuery = "buscarPagoCxPPorIdFetchFac";
        DaoQuery<PagoCxP> dataAcces = new DaoQuery<PagoCxP>();
        try {
            pagoCxP = dataAcces.buscarEntidadPorId(id, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return pagoCxP;
    }

    public void eliminarNotaCredito(PagoCxP pagoCxP) throws ValidatorException {

        NotaCredDebCxP notacd = null;
        String nombQuery = "buscarNotaCreditoCxPPorIdPago";
        DaoQuery<NotaCredDebCxP> dataAcces = new DaoQuery<NotaCredDebCxP>();
        String idPago = pagoCxP.getId().toString();
        try {
            notacd = dataAcces.buscarEntidad(idPago, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        if (notacd == null) {
            return;
        } else {
            DaoQuery<NotaCredDebCxP> dataAcces1 = new DaoQuery<NotaCredDebCxP>();
            try {
                dataAcces1.eliminarEntidad(notacd);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            }
        }
    }

    public void ajustarMovBancario(PagoCxP pagoCxP) throws ValidatorException, ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = pagoCxP.getBancoPagoCxP();
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        MovBancarioNuevo.setIdCtaMovBancario(pagoCxP.getIdCuentaPagoCxP());
        MovBancarioNuevo.setTipoOperMovBancario("Revertir Pago");

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

        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Revertir Pago a Proveedor");


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

        Double nuevoSaldo;
        nuevoSaldo = cuenta.getSaldoActCuenta() + pagoCxP.getMtoPagoCxP();
        cuenta.setSaldoActCuenta(nuevoSaldo);
        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

        try {

            daoCrud.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
    }

    public void eliminarTranFondo(PagoCxP pagoCxP) throws ValidatorException {

        TranFondo tranFondo = null;
        String nombQuery = "buscarTranFondoPorIdPago";
        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();
        Integer idPago = pagoCxP.getId();
        try {
            tranFondo = dataAcces.buscarEntidadPorId(idPago, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        if (!(tranFondo == null)) {

            DaoQuery<TranFondo> dataAcces1 = new DaoQuery<TranFondo>();
            try {
                dataAcces1.eliminarEntidad(tranFondo);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            }
        }
    }

    public void eliminarPagoCxP() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoCxPDT pCxPSel = (PagoCxPDT) ctx.getExternalContext().getSessionMap().get("PagoCxPDT");

        Integer idPago = pCxPSel.getId();
        PagoCxP pagoCxP = buscarPagoFacCxP(idPago);
        actualizarEdoYSaldoFac(pagoCxP);
        eliminarNotaCredito(pagoCxP);
        ajustarMovBancario(pagoCxP);
        eliminarTranFondo(pagoCxP);

        try {
            eliminarEntidad(pagoCxP);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("PagoCxPDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago correspondiente al nro. " + pCxPSel.getNumDocPagoCxP() + " ha sido revertido satisfactoriamente!"));
        return;

    }
}
