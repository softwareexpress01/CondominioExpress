package movimientos.backing;

import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.Collection;

import movimientos.modelos.PagoFonRes;
import movimientos.modelos.PagoFonResDT;
import movimientos.modelos.RevertirPagoFonResDT;
import movimientos.modelos.PagoFacFonRes;
import movimientos.modelos.MovBancario;
import movimientos.modelos.TranFondo;
import datosbasicos.modelos.Cuenta;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoCrudAux;
import util.DaoQuery;
import util.JSFUtil;

import java.util.Calendar;
import java.util.Date;


/**
 *
 * @author Frank Rojas H.
 */
public class ElimPagoFonResCtrl extends DaoCrud<PagoFonRes, PagoFacFonRes, Integer> implements Serializable {

    private Integer idCta;
    private Double mto;
    Fondo fondo = null;
    
    public ElimPagoFonResCtrl() {
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
    public void actualizarEdoTranFondo(PagoFacFonRes pagofac) throws ValidatorException {

        TranFondo tranfondo = new TranFondo();
        String nombQuery = "buscarTranFondoFetchFondoPorId";
        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();

        Integer idTranFondo = pagofac.getIdFacFonRes();
        try {
            tranfondo = dataAcces.buscarEntidadPorId(idTranFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }

        tranfondo.setSaldoTranFondo(tranfondo.getSaldoTranFondo() + pagofac.getMtoPagadoFonRes());
        if (tranfondo.getMtoTranFondo().equals(tranfondo.getSaldoTranFondo())) {
            tranfondo.setEstadoTranFondo("PEND");
        } else {
            tranfondo.setEstadoTranFondo("EPRO");
        }

        try {
            dataAcces.almacenarOModificarEntidad(tranfondo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //*********************** ACTUAIZACION DEL FONDO CORRESPONDIENTE ********************************

        fondo = tranfondo.getFondo();
   
        /*nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces3.buscarEntidadPorId(fondoTranFondo.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }*/
        DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
        if (fondo != null) {
            fondo.setSaldoFondo(fondo.getSaldoFondo() - pagofac.getMtoPagadoFonRes());
            try {
                dataAcces3.almacenarOModificarEntidad(fondo);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
        }

    }

    public void actualizarEdoYSaldoFac(PagoFonRes pagoFonRes) throws ValidatorException {


        for (Iterator<PagoFacFonRes> it = pagoFonRes.pagoFacFonRes.iterator();
                it.hasNext();) {
            PagoFacFonRes fac = (PagoFacFonRes) it.next();

            actualizarEdoTranFondo(fac);

        }
    }

    public PagoFonRes buscarPagoFacFonRes(Integer id) throws ValidatorException {

        PagoFonRes pagoFonRes;
        String nombQuery = "buscarPagoFonResPorIdFetchFac";
        DaoQuery<PagoFonRes> dataAcces = new DaoQuery<PagoFonRes>();
        try {
            pagoFonRes = dataAcces.buscarEntidadPorId(id, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return pagoFonRes;
    }

     public void ajustarMovBancario(PagoFonRes pagoCxP) throws ValidatorException, ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = pagoCxP.getBancoPagoFonRes();
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        MovBancarioNuevo.setIdCtaMovBancario(pagoCxP.getIdCuentaPagoFonRes());
        MovBancarioNuevo.setTipoOperMovBancario("Revertir Pago");

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(pagoCxP.getFecDocPagoFonRes());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(pagoCxP.getNumDocPagoFonRes());
        Double mto = pagoCxP.getMtoPagoFonRes();
       
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Revertir Pago Fondo de Res.");
       

        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(pagoCxP.getIdCuentaPagoFonRes(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Double nuevoSaldo;
        nuevoSaldo = cuenta.getSaldoActCuenta() + pagoCxP.getMtoPagoFonRes();
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
        
        //***************************** Movimiento bancario de la cuenta asociada al fondo de reserva ****************
 
        MovBancario MovBancarioFondo = new MovBancario();

        MovBancarioFondo.setIdBcoMovBancario(fondo.getIdBcoFondo());

        MovBancarioFondo.setIdCtaMovBancario(fondo.getIdCtaFondo());
        MovBancarioFondo.setTipoOperMovBancario("Revertir pago");

        MovBancarioFondo.setFecMovBancario(cal);
        MovBancarioFondo.setRefMovBancario(pagoCxP.getNumDocPagoFonRes());
        mto = Double.valueOf(pagoCxP.getMtoPagoFonRes()).doubleValue();
        Double mtoPago = mto;
        mto = mto * -1;
        MovBancarioFondo.setMtoMovBancario(mto);
        MovBancarioFondo.setDescMovBancario("Revertir Pago Fondo de Res.");
        //MovBancarioFondo.setIdPago(null);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        cuenta = null;
        nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces4 = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces4.buscarEntidadPorId(fondo.getIdCtaFondo(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioFondo.setCuenta(cuenta);

        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioFondo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
         /*_______________________________________________________________________________________*/

        nuevoSaldo = cuenta.getSaldoActCuenta() - mtoPago;
        cuenta.setSaldoActCuenta(nuevoSaldo);
        DaoQuery<Cuenta> dataAcces5 = new DaoQuery<Cuenta>();

        try {

            dataAcces5.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
    }

    public void revertirPagoFonRes() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        RevertirPagoFonResDT pFonResSel = (RevertirPagoFonResDT) ctx.getExternalContext().getSessionMap().get("RevertirPagoFonResDT");

        Integer idPago = pFonResSel.getId();
        PagoFonRes pagoFonRes = buscarPagoFacFonRes(idPago);
        actualizarEdoYSaldoFac(pagoFonRes);
        ajustarMovBancario(pagoFonRes);

        try {
            eliminarEntidad(pagoFonRes);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("PagoFonResDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago correspondiente al nro. " + pFonResSel.getNumDocPagoFonRes() + " ha sido revertido satisfactoriamente!"));

    }
}
