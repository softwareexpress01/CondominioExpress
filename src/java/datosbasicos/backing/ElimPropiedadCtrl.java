/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Propiedad;
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
public class ElimPropiedadCtrl extends DaoCrud<Propiedad, Condominio, Integer> implements Serializable {

    public ElimPropiedadCtrl() {
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

    public void eliminarPropiedad() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Propiedad propSel = (Propiedad) ctx.getExternalContext().getSessionMap().get("Propiedad");

        try {
            eliminarEntidad(propSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Propiedad " + propSel.getNombPropiedad() + " ha sido eliminada satisfactoriamente!"));
        return;

    }
}
