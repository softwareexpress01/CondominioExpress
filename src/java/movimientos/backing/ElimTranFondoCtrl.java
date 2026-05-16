/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.MovBancario;
import movimientos.modelos.TranFondo;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoCrudAux;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author Frank
 */
public class ElimTranFondoCtrl extends DaoCrud<TranFondo, Fondo, Integer> implements Serializable {

    public ElimTranFondoCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        return "regresar";

    }

    public void eliminarTranFondo() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        TranFondo tranFondoSel = (TranFondo) ctx.getExternalContext().getSessionMap().get("TranFondo");


        try {
            eliminarEntidad(tranFondoSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        revertirTransaccion(tranFondoSel);
        if (!(tranFondoSel.getIdCuentaTranFondo() == null)) {
            revertirMovimientoBancarioCtaOrigen(tranFondoSel);
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Transacción de Fondo seleccionada ha sido eliminada satisfactoriamente!"));


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("TranFondo");
        ctx.getExternalContext().getSessionMap().remove("TranFondoDT");


    }

    public void revertirTransaccion(TranFondo tranFondoSel) throws ParseException {


        // Revertir saldo del Fondo afectado

        Integer idFondoInt = tranFondoSel.getIdFondoTranFondo();
        Fondo fondo;

        try {

            fondo = (Fondo) encontrarEntidadPorId(idFondoInt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        Double nuevoSaldo = fondo.getSaldoFondo() - tranFondoSel.getMtoTranFondo();
        fondo.setSaldoFondo(nuevoSaldo);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(fondo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }

        // Revertir Movimiento bancario cuenta destino

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = fondo.getIdBcoFondo();
        MovBancarioNuevo.setIdBcoMovBancario(fondo.getIdBcoFondo());
        MovBancarioNuevo.setIdCtaMovBancario(fondo.getIdCtaFondo());
        MovBancarioNuevo.setTipoOperMovBancario("N/A");

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(tranFondoSel.getFecTranFondo());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(tranFondoSel.getDocTranFondo());
        Double mto = tranFondoSel.getMtoTranFondo();
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Revertir Ingreso a fondos");
        MovBancarioNuevo.setIdPago(null);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(fondo.getIdCtaFondo(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        nuevoSaldo = cuenta.getSaldoActCuenta() - tranFondoSel.getMtoTranFondo();
        cuenta.setSaldoActCuenta(nuevoSaldo);
        MovBancarioNuevo.setCuenta(cuenta);

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

    public void revertirMovimientoBancarioCtaOrigen(TranFondo tranFondoSel) throws ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        MovBancarioNuevo.setIdBcoMovBancario(tranFondoSel.getIdBancoTranFondo());
        MovBancarioNuevo.setIdCtaMovBancario(tranFondoSel.getIdCuentaTranFondo());
        MovBancarioNuevo.setTipoOperMovBancario("N/A");

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(tranFondoSel.getFecTranFondo());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(tranFondoSel.getDocTranFondo());
        MovBancarioNuevo.setMtoMovBancario(tranFondoSel.getMtoTranFondo());
        MovBancarioNuevo.setDescMovBancario("Revertir Ingreso a fondos");
        MovBancarioNuevo.setIdPago(null);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(tranFondoSel.getIdCuentaTranFondo(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Double nuevoSaldo;
        nuevoSaldo = cuenta.getSaldoActCuenta() + tranFondoSel.getMtoTranFondo();
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
}
