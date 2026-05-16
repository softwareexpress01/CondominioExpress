/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Proveedor;
import movimientos.modelos.DeudaPendCxP;
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
 * @author jesus
 */
public class ElimDeudaPendCxPCtrl extends DaoCrud<DeudaPendCxP, Proveedor, Integer> implements Serializable {

    public ElimDeudaPendCxPCtrl() {
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

    public void eliminarDeudaPendCxP() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        DeudaPendCxP deudaPendCxPSel = (DeudaPendCxP) ctx.getExternalContext().getSessionMap().get("DeudaPendCxP");

        if ("PEND".equals(deudaPendCxPSel.getEstatusDeudaPendCxP())) {
            try {
                eliminarEntidad(deudaPendCxPSel);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Deuda Pendiente de CxP identificada con el número " + deudaPendCxPSel.getRefDeudaPendCxP() + " ha sido eliminada de la Base de datos "));
        }else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Deuda Pendiente de CxP identificada con el número " + deudaPendCxPSel.getRefDeudaPendCxP() + " No se puede eliminar. Está en proceso "));
        }
    

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("DeudaPendienteCxP");
        ctx.getExternalContext().getSessionMap().remove("DeudaPendienteCxPDT");



    }
}
