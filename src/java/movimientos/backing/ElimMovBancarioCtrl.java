/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Cuenta;
import movimientos.modelos.MovBancario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoCrudAux;
import util.JSFUtil;

/**
 *
 * @author Frank Rojas H.
 */
public class ElimMovBancarioCtrl extends DaoCrud<MovBancario, Cuenta, Integer> implements Serializable {

    public ElimMovBancarioCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.si = false;
        beanUsuario.no = false;

        beanUsuario.setDeshabilitar(false);
        return "regresar";

    }

    public void eliminarMovBancario() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        MovBancario movBancarioSel = (MovBancario) ctx.getExternalContext().getSessionMap().get("MovBancario");

        if (movBancarioSel.getIdPago() == 0) {
            try {
                eliminarEntidad(movBancarioSel);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Movimiento Bancario " + movBancarioSel.getId() + " ha sido eliminado de la Base de datos "));

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Mov. Bancario seleccionado no se puede eliminar. Debe revertir ANTES el pago que lo originó"));

        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        /* Restablece el Saldo de la Cuenta. Aplica la modificación indicada al mismo /
         /*_______________________________________________________________________________________*/


        String tipoOper1 = "Pago de Cheque";
        String tipoOper2 = "Dep/Transferencia";
        String tipoOper3 = "Nota de Débito";
        String tipoOper4 = "Ret/Transferencia";
        String tipoOper5 = "Nota de Crédito";

        /* actualización del Saldo de la Cuenta. Esta operación restablece el monto de la Trans al Saldo por tipo de Operación*/
        /*_______________________________________________________________________________________*/


        Integer idCtaInt = movBancarioSel.getIdCtaMovBancario();
        Cuenta cuenta;
        try {

            cuenta = (Cuenta) encontrarEntidadPorId(idCtaInt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        if (tipoOper1.equals(movBancarioSel.getTipoOperMovBancario())) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - movBancarioSel.getMtoMovBancario();
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper2.equals(movBancarioSel.getTipoOperMovBancario())) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - movBancarioSel.getMtoMovBancario();
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper3.equals(movBancarioSel.getTipoOperMovBancario())) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - movBancarioSel.getMtoMovBancario();
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper4.equals(movBancarioSel.getTipoOperMovBancario())) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - movBancarioSel.getMtoMovBancario();
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper5.equals(movBancarioSel.getTipoOperMovBancario())) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - movBancarioSel.getMtoMovBancario();
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        ctx.getExternalContext().getSessionMap().remove("MovBancario");
        ctx.getExternalContext().getSessionMap().remove("MovBancarioDT");

        beanUsuario.setDeshabilitar(false);



    }
}
