package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.GastoNoComun;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimGastoNoComunCtrl extends DaoCrud<GastoNoComun, GastoNoComun, Integer> implements Serializable {

    public ElimGastoNoComunCtrl() {
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

    public void eliminarGastoNoComun() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoNoComun gncSel = (GastoNoComun) ctx.getExternalContext().getSessionMap().get("GastoNoComun");

        if (!(gncSel.getEdoGastoNoComun().equals("PEND") & gncSel.getEdoGralFac().equals("PEND"))) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Para proceder con la eliminacion de este gasto, deberan revertirse antes los pagos realizados sobre el..."));
            return;
        }

        try {
            eliminarEntidad(gncSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("GastoNoComun");
        ctx.getExternalContext().getSessionMap().remove("GastoNoComunDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto no comun correspondiente al nro. " + gncSel.getNumDocGastoNoComun() + " ha sido eliminado satisfactoriamente!"));
       
    }
}
