/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Condominio;
import movimientos.modelos.CuotaEsp;
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
public class ElimCuotaEspCtrl extends DaoCrud<CuotaEsp, Condominio, Integer> implements Serializable {

    public ElimCuotaEspCtrl() {
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

    public void eliminarCuotaEsp() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        CuotaEsp cuotaEspSel = (CuotaEsp) ctx.getExternalContext().getSessionMap().get("CuotaEsp");

        if ("PEND".equals(cuotaEspSel.getEstadoCuotaEsp())) {
            try {
                eliminarEntidad(cuotaEspSel);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Cuota Especial " + cuotaEspSel.getId() + " ha sido eliminada de la Base de datos "));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Cuota Especial " + cuotaEspSel.getId() + " Está en proceso. No puede ser eliminada "));
        }


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("CuotaEsp");
        ctx.getExternalContext().getSessionMap().remove("CuotaEspDT");

    }
}
