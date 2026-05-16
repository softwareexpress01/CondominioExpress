/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Chequera;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
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
public class ElimChequeraCtrl extends DaoCrud<Chequera, Cuenta, Integer> implements Serializable {

    public ElimChequeraCtrl() {
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

    public void eliminarChequera() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Chequera chequeraSel = (Chequera) ctx.getExternalContext().getSessionMap().get("Chequera");

        try {
            eliminarEntidad(chequeraSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Chequera");
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La chequera " + chequeraSel.getNumChequera() + " ha sido eliminada de la Base de datos "));
        return;

    }
}
