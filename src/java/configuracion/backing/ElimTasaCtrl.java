package configuracion.backing;

import configuracion.modelos.Tasa;
import datosbasicos.modelos.Usuario;
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
public class ElimTasaCtrl extends DaoCrud<Tasa, Tasa, Integer> implements Serializable {

    public ElimTasaCtrl() {
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

    public void eliminarTasa() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Tasa tasaSel = (Tasa) ctx.getExternalContext().getSessionMap().get("Tasa");

        
        try {
            eliminarEntidad(tasaSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La tasa ha sido eliminada satisfactoriamente!"));
        return;

    }
}
