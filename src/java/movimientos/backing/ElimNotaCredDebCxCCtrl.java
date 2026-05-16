package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.NotaCredDebCxC;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimNotaCredDebCxCCtrl extends DaoCrud<NotaCredDebCxC, NotaCredDebCxC, Integer> implements Serializable {

    public ElimNotaCredDebCxCCtrl() {
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

    public void eliminarNotaCredDebCxC() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        NotaCredDebCxC ncdSel = (NotaCredDebCxC) ctx.getExternalContext().getSessionMap().get("NotaCredDebCxC");

        if (!ncdSel.getEdoNotaCredDebCxC().equals("PEND") & ncdSel.getTipoNotaCredDebCxC().equals("Credito")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Nota de credito en progreso/cancelada, su eliminacion no esta permitida..."));
            return;
        }
        if (!ncdSel.getEdoNotaCredDebCxC().equals("PEND") & ncdSel.getTipoNotaCredDebCxC().equals("Debito")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Para proceder con la eliminacion de esta nota de debito, deberan revertirse antes los pagos realizados sobre ella..."));
            return;
        }
        try {
            eliminarEntidad(ncdSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("NotaCredDebCxC");
        ctx.getExternalContext().getSessionMap().remove("NotaCredDebCxCDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La nota correspondiente al nro. " + ncdSel.getNumNotaCredDebCxC() + " ha sido eliminada satisfactoriamente!"));
        return;

    }
}
