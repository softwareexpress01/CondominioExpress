package movimientos.backing;

import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.Alicuota;
import movimientos.modelos.CuotaEsp;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.MovBancario;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoCxCDT;
import movimientos.modelos.PagoFacCxC;
import movimientos.modelos.TranFondo;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoCrudAux;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimPagoCxCCtrl extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

    private Double mto;
    private final double ceroLogico=0.01; 

    public ElimPagoCxCCtrl() {
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

    public void actualizarEdoFacGNC(PagoFacCxC pagofac) throws ValidatorException {

        FacGastoNoComun fgnc = null;
        String nombQuery = "buscarFacGastoNoComunPorId";
        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxC(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        fgnc.setSaldoFacGastoNoComun(fgnc.getSaldoFacGastoNoComun() + pagofac.getMtoPagadoCxC());
        if ((fgnc.getMtoFacGastoNoComun() - (fgnc.getSaldoFacGastoNoComun())) < ceroLogico) {
            fgnc.setSaldoFacGastoNoComun(fgnc.getMtoFacGastoNoComun());
            fgnc.setEdoFacGastoNoComun("PEND");
        } else {
            fgnc.setEdoFacGastoNoComun("EPRO");
        }
        DaoQuery<FacGastoNoComun> dataAcces2 = new DaoQuery<FacGastoNoComun>();

        try {
            dataAcces2.almacenarOModificarEntidad(fgnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //------------------------------------------------------------------------------------------
        // Se actualiza el saldo de las facturas por gasto no comun y el edo general de las facturas
        // esto ultimo se toma en consideracion a la hora de modificar o eliminar el gnc.
        //------------------------------------------------------------------------------------------

        GastoNoComun gnc = fgnc.getGastoNoComun();
        gnc.setSaldoFacGnc(gnc.getSaldoFacGnc() + pagofac.getMtoPagadoCxC());
        if ((gnc.getMtoGastoNoComun()) - (gnc.getSaldoFacGnc()) < ceroLogico) {
            gnc.setSaldoFacGnc(gnc.getMtoGastoNoComun());
            gnc.setEdoGralFac("PEND");
        } else {
            gnc.setEdoGralFac("EPRO");
        }
        DaoQuery<GastoNoComun> dataAcces3 = new DaoQuery<GastoNoComun>();
        try {
            dataAcces3.almacenarOModificarEntidad(gnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void actualizarEdoDeudaPend(PagoFacCxC pagofac) throws ValidatorException {

        DeudaPendCxC dpend = null;
        String nombQuery = "buscarDeudaPendPorId";
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dpend = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxC(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        dpend.setSaldoDeudaPendCxC(dpend.getSaldoDeudaPendCxC() + pagofac.getMtoPagadoCxC());
        if ((dpend.getMtoDeudaPendCxC() - (dpend.getSaldoDeudaPendCxC())) < ceroLogico) {
            dpend.setSaldoDeudaPendCxC(dpend.getMtoDeudaPendCxC());
            dpend.setEdoDeudaPendCxC("PEND");
        } else {
            dpend.setEdoDeudaPendCxC("EPRO");
        }
        DaoQuery<DeudaPendCxC> dataAcces2 = new DaoQuery<DeudaPendCxC>();

        try {
            dataAcces2.almacenarOModificarEntidad(dpend);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoAlicuota(PagoFacCxC pagofac) throws ValidatorException {

        Alicuota alicuota = null;
        String nombQuery = "buscarAlicuotaPorId";
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            alicuota = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxC(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        alicuota.setSaldoAlicuota(alicuota.getSaldoAlicuota() + pagofac.getMtoPagadoCxC());
        if ((alicuota.getMtoAlicuota() - (alicuota.getSaldoAlicuota())) < ceroLogico) {
            alicuota.setSaldoAlicuota(alicuota.getMtoAlicuota());
            alicuota.setEdoAlicuota("PEND");
        } else {
            alicuota.setEdoAlicuota("EPRO");
        }
        DaoQuery<Alicuota> dataAcces2 = new DaoQuery<Alicuota>();

        try {
            dataAcces2.almacenarOModificarEntidad(alicuota);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoNotaDebito(PagoFacCxC pagofac) throws ValidatorException {

        NotaCredDebCxC nota = null;
        String nombQuery = "buscarNotaCredDebCxCPorId";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            nota = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxC(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        nota.setSaldoNotaCredDebCxC(nota.getSaldoNotaCredDebCxC() + pagofac.getMtoPagadoCxC());
        if ((nota.getMtoNotaCredDebCxC() - (nota.getSaldoNotaCredDebCxC())) < ceroLogico) {
            nota.setSaldoNotaCredDebCxC(nota.getMtoNotaCredDebCxC());
            nota.setEdoNotaCredDebCxC("PEND");
        } else {
            nota.setEdoNotaCredDebCxC("EPRO");
        }
        DaoQuery<NotaCredDebCxC> dataAcces2 = new DaoQuery<NotaCredDebCxC>();

        try {
            dataAcces2.almacenarOModificarEntidad(nota);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoFacCuotaEsp(PagoFacCxC pagofac) throws ValidatorException {

        FacCuotaEsp fce = null;
        String nombQuery = "buscarFacCuotaEspPorId";
        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fce = dataAcces.buscarEntidadPorId(pagofac.getIdFacCxC(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        //--------------------------------------------------------------------------- 
        // Se va a considerar despreciable una diferecia < 0.01 entre el saldo y el mto
        //---------------------------------------------------------------------------
        fce.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp() + pagofac.getMtoPagadoCxC());
        if ((fce.getMtoFacCuotaEsp() - (fce.getSaldoFacCuotaEsp())) < ceroLogico) {
            fce.setSaldoFacCuotaEsp(fce.getMtoFacCuotaEsp());
            fce.setEdoFacCuotaEsp("PEND");
        } else {
            fce.setEdoFacCuotaEsp("EPRO");
        }
        DaoQuery<FacCuotaEsp> dataAcces2 = new DaoQuery<FacCuotaEsp>();

        try {
            dataAcces2.almacenarOModificarEntidad(fce);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoYSaldoFac(PagoCxC pagocxc) throws ValidatorException {


        for (Iterator<PagoFacCxC> it = pagocxc.pagoFacCxC.iterator();
                it.hasNext();) {
            PagoFacCxC fac = (PagoFacCxC) it.next();
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la factura del gasto no comun,
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxC().equals("GNC")) {
                actualizarEdoFacGNC(fac);
            }
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la deuda pendiente
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxC().equals("DPE")) {
                actualizarEdoDeudaPend(fac);
            }
            //-------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la alicuota
            //-------------------------------------------------------------------
            if (fac.getDiscFacCxC().equals("ALI")) {
                actualizarEdoAlicuota(fac);
            }
            //---------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la Facturaq por Cuota Especial
            //---------------------------------------------------------------------
            if (fac.getDiscFacCxC().equals("CES")) {
                actualizarEdoFacCuotaEsp(fac);
            }
            //---------------------------------------------------------------------
            // Se actualiza el saldo y el estado de la nota de debito
            //---------------------------------------------------------------------
            if (fac.getDiscFacCxC().equals("NCD") | fac.getDiscFacCxC().equals("NDE")) {
                actualizarEdoNotaDebito(fac);
            }

        }
    }

    public PagoCxC buscarPagoFacCxC(Integer id) throws ValidatorException {

        PagoCxC pagoCxC;
        String nombQuery = "buscarPagoCxCPorIdFetchFac";
        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(id, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return pagoCxC;
    }

    public void eliminarNotaCredito(PagoCxC pagocxc) throws ValidatorException {

        NotaCredDebCxC notacd = null;
        String nombQuery = "buscarNotaCreditoPorIdPago";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            notacd = dataAcces.buscarEntidad(pagocxc.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        if (notacd == null) {
            return;
        } else {
            DaoQuery<NotaCredDebCxC> dataAcces1 = new DaoQuery<NotaCredDebCxC>();
            try {
                dataAcces1.eliminarEntidad(notacd);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            }
        }
    }

    public void ajustarMovBancario(PagoCxC pagoCxC) throws ValidatorException, ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(pagoCxC.getBancoPagoCxC());
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        MovBancarioNuevo.setIdCtaMovBancario(Integer.parseInt(pagoCxC.getCuentaPagoCxC()));
        MovBancarioNuevo.setTipoOperMovBancario("Ajuste Mov.");

        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(pagoCxC.getFecDocPagoCxC());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(pagoCxC.getNumDocPagoCxC());

        MovBancarioNuevo.setMtoMovBancario(pagoCxC.getMtoPagoCxC() * -1);
        MovBancarioNuevo.setDescMovBancario("Revertir Pago de cxc");


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(Integer.parseInt(pagoCxC.getCuentaPagoCxC()), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Double nuevoSaldo;
        nuevoSaldo = cuenta.getSaldoActCuenta() - pagoCxC.getMtoPagoCxC();
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

    public void eliminarTranFondo(PagoCxC pagoCxC) throws ValidatorException {

        TranFondo tranFondo = null;
        Fondo fondo = null;
        String nombQuery = "buscarTranFondoFetchFondo";
        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();
        Integer idPago = pagoCxC.getId();
        try {
            tranFondo = dataAcces.buscarEntidadPorId(idPago, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        if (tranFondo == null) {
            return;
        } else {
            fondo = tranFondo.getFondo();
            mto = tranFondo.getMtoTranFondo();
            DaoQuery<TranFondo> dataAcces1 = new DaoQuery<TranFondo>();
            try {
                dataAcces1.eliminarEntidad(tranFondo);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            }
        }
        // Actualizacion del fondo

        Double nuevoSaldo = fondo.getSaldoFondo() - mto;
        fondo.setSaldoFondo(nuevoSaldo);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(fondo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void eliminarPagoCxC() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoCxCDT pcxcSel = (PagoCxCDT) ctx.getExternalContext().getSessionMap().get("PagoCxCDT");

        Integer idPago = pcxcSel.getId();
        PagoCxC pagoCxC = buscarPagoFacCxC(idPago);
        actualizarEdoYSaldoFac(pagoCxC);
        eliminarNotaCredito(pagoCxC);
        ajustarMovBancario(pagoCxC);
        eliminarTranFondo(pagoCxC);

        try {
            eliminarEntidad(pagoCxC);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("PagoCxCDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago correspondiente al nro. " + pcxcSel.getNumDocPagoCxC() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
