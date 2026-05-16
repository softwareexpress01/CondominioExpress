package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.GastoPropiedad;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimGastoPropiedadCtrl extends DaoCrud<GastoPropiedad, GastoPropiedad, Integer> implements Serializable {

    public ElimGastoPropiedadCtrl() {
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

    public void eliminarGastoPropiedad() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoPropiedad gproSel = (GastoPropiedad) ctx.getExternalContext().getSessionMap().get("GastoPropiedad");

        try {
            eliminarEntidad(gproSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("GastoPropiedad");
        ctx.getExternalContext().getSessionMap().remove("GastoPropiedadDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto correspondiente al documento nro. " + gproSel.getNumDocGastoPropiedad() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
