package movimientos.backing;

import java.io.Serializable;
import java.util.Calendar;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.DeudaPendCxC;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimDeudaPendCxCCtrl extends DaoCrud<DeudaPendCxC, DeudaPendCxC, Integer> implements Serializable {

    public ElimDeudaPendCxCCtrl() {
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

    public void eliminarDeudaPendCxC() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        DeudaPendCxC dpSel = (DeudaPendCxC) ctx.getExternalContext().getSessionMap().get("DeudaPendCxC");

        if (!dpSel.getEdoDeudaPendCxC().equals("PEND")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Para proceder con la eliminacion de esta deuda, deberan revertirse antes los pagos realizados sobre ella..."));
            return;
        }

        try {
            eliminarEntidad(dpSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("DeudaPendCxC");
        ctx.getExternalContext().getSessionMap().remove("DeudaPendDT");

        //-----------------------------------------------------------------
        //Se da formato a la fecha de la deuda para mostrarla en el mensaje
        //-----------------------------------------------------------------
        Calendar fechaMov = dpSel.getFecDeudaPendCxC();
        String dia = "";
        String mes = "";
        int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
        int mesMov = fechaMov.get(Calendar.MONTH) + 1;
        int añoMov = fechaMov.get(Calendar.YEAR);

        if (diaMov < 10) {
            dia = "0" + diaMov;
        } else {
            dia = String.valueOf(diaMov);
        }

        if (mesMov < 10) {
            mes = "0" + mesMov;
        } else {
            mes = String.valueOf(mesMov);
        }

        String stringFechaMov = dia + "-" + mes + "-" + añoMov;


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La deuda correspondiente a la fecha " + stringFechaMov + " ha sido eliminada satisfactoriamente!"));
        return;

    }
}
