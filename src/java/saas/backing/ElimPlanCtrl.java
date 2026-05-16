/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

import datosbasicos.modelos.Condominio;
import saas.modelos.Plan;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author Frank
 */
public class ElimPlanCtrl extends DaoCrud<Plan, Condominio, Integer> implements Serializable {

    public ElimPlanCtrl() {
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

    public void eliminarPlan() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Plan planSel = (Plan) ctx.getExternalContext().getSessionMap().get("Plan");
        planSel.setEdoPlan("INA");

        try {
            almacenarOModificarEntidad(planSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Plan");
        ctx.getExternalContext().getSessionMap().remove("PlanDT");
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Plan " + planSel.getDescPlan() + " ha sido eliminado de la Base de datos "));

    }
}
