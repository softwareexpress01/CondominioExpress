/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import movimientos.modelos.PagoAdelantado;
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
import movimientos.modelos.PagoAdelantado;
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
public class ElimPagoAdelantadoCtrl extends DaoCrud<PagoAdelantado, Inmueble, Integer> implements Serializable {

    public ElimPagoAdelantadoCtrl() {
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

    public void eliminarPagoAdelantado() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoAdelantado pagoAdelantadoSel = (PagoAdelantado) ctx.getExternalContext().getSessionMap().get("PagoAdelantado");

        if ("PEND".equals(pagoAdelantadoSel.getEstadoPagoAdelantado())) {
            try {
                eliminarEntidad(pagoAdelantadoSel);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            revertirTransaccion(pagoAdelantadoSel);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Pago adelantado seleccionado ha sido eliminado satisfactoriamente!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Pago adelantado seleccionado No se puede eliminar. Está en proceso!"));
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("PagoAdelantado");
        ctx.getExternalContext().getSessionMap().remove("PagoAdelantadoDT");


    }

    public void revertirTransaccion(PagoAdelantado pagoAdelantadoSel) throws ParseException {

        // Revertir Movimiento bancario 

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = pagoAdelantadoSel.getIdBcoPagoAdelantado();
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        MovBancarioNuevo.setIdCtaMovBancario(pagoAdelantadoSel.getIdCuentaPagoAdelantado());
        MovBancarioNuevo.setTipoOperMovBancario("Pago de Cheque");

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(pagoAdelantadoSel.getFecPagoAdelantado());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(pagoAdelantadoSel.getRefPagoAdelantado());
        Double mto = pagoAdelantadoSel.getMtoPagoAdelantado();
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Revertir Pago Adelantado Inm.");
        MovBancarioNuevo.setIdPago(-1);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(pagoAdelantadoSel.getIdCuentaPagoAdelantado(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Double nuevoSaldo;
        nuevoSaldo = cuenta.getSaldoActCuenta() - pagoAdelantadoSel.getMtoPagoAdelantado();
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
