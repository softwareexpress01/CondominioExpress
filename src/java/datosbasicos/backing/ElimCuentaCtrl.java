/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Chequera;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.CuentaDT;
import java.io.Serializable;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimCuentaCtrl extends DaoCrud<Cuenta, Condominio, Integer> implements Serializable {

    public ElimCuentaCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.si = false;
        beanUsuario.no = false;

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "regresar";

    }

    public void eliminarCuenta() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        CuentaDT cuentaSel = (CuentaDT) ctx.getExternalContext().getSessionMap().get("Cuenta");

        Cuenta cuenta = buscarCuentaChequeras(cuentaSel.getId());
        cuenta.setEdoCuenta("INA");
        for (Iterator<Chequera> it = cuenta.getChequeras().iterator();
                it.hasNext();) {
            Chequera cheq = (Chequera) it.next();
            cheq.setEdoElimChequera("INA");
        }

        try {
            almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext()
                .getSessionMap().remove("Cuenta");

        FacesContext.getCurrentInstance()
                .addMessage(null,
                new FacesMessage("La Cuenta " + cuentaSel.getNumCuenta() + " ha sido eliminada de la Base de datos "));

    }
    public Cuenta buscarCuentaChequeras(Integer idCta) {

        Cuenta cuenta = null;

        String nombQuery = "buscarCuentaFetchChequeras";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return cuenta;
    }
}
