package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoComunOrd;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimGastoComunExtCtrl extends DaoCrud<GastoComunExt, GastoComunOrd, Integer> implements Serializable {

    public ElimGastoComunExtCtrl() {
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

    public void eliminarGastoComunExt() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoComunExt gcoExtSel = (GastoComunExt) ctx.getExternalContext().getSessionMap().get("GastoComunExt");
        
        if (!gcoExtSel.getEdoGastoComunExt().equals("PEND")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Para proceder con la eliminacion de este gasto, deberan revertirse antes los pagos realizados sobre el..."));
            return;
        }

        try {
            eliminarEntidad(gcoExtSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("GastoComunExt");
        ctx.getExternalContext().getSessionMap().remove("GastoComunDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto correspondiente al documento nro. " + gcoExtSel.getNumDocGastoComunExt() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
