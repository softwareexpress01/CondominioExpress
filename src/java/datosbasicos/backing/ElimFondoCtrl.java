/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
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
public class ElimFondoCtrl extends DaoCrud<Fondo, Condominio, Integer> implements Serializable {

    public ElimFondoCtrl() {
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

    public void eliminarFondo() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Fondo fondoSel = (Fondo) ctx.getExternalContext().getSessionMap().get("Fondo");
        fondoSel.setEdoFondo("INA");

        try {
            almacenarOModificarEntidad(fondoSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Fondo");
        ctx.getExternalContext().getSessionMap().remove("FondoDT");
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Fondo " + fondoSel.getDescFondo() + " ha sido eliminado de la Base de datos "));

    }
}
