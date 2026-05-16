package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.GastoComunOrd;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimGastoComunOrdCtrl extends DaoCrud<GastoComunOrd, GastoComunOrd, Integer> implements Serializable {

    public ElimGastoComunOrdCtrl() {
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

    public void eliminarGastoComunOrd() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoComunOrd gcoSel = (GastoComunOrd) ctx.getExternalContext().getSessionMap().get("GastoComunOrd");

        if (!gcoSel.getEdoGastoComunOrd().equals("PEND")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Para proceder con la eliminacion de este gasto, deberan revertirse antes los pagos realizados sobre el..."));
            return;
        }
        try {
            eliminarEntidad(gcoSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("GastoComunOrd");
        ctx.getExternalContext().getSessionMap().remove("GastoComunOrdDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto correspondiente al documento nro. " + gcoSel.getNumDocGastoComunOrd() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
