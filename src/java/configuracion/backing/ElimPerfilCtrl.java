package configuracion.backing;

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
public class ElimPerfilCtrl extends DaoCrud<Usuario, Usuario, Integer> implements Serializable {

    public ElimPerfilCtrl() {
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

    public void eliminarUsuario() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Usuario usuarioSel = (Usuario) ctx.getExternalContext().getSessionMap().get("Usuario");

        if (usuarioSel.getPerUsuario().equals("Admin")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La acción de eliminación solo está permitida sobre usuarios Operadores. Operación cancelada..."));
            return;
        }
        try {
            eliminarEntidad(usuarioSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El usuario " + usuarioSel.getNombUsuario() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
