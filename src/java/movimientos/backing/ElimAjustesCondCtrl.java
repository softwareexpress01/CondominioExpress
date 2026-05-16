package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import movimientos.modelos.AjustesCond;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimAjustesCondCtrl extends DaoCrud<AjustesCond, AjustesCond, Integer> implements Serializable {

    public ElimAjustesCondCtrl() {
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

    public void eliminarAjustesCond() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        AjustesCond ncdSel = (AjustesCond) ctx.getExternalContext().getSessionMap().get("AjustesCond");

        if ("PEND".equals(ncdSel.getEdoAjustesCond())) {
            try {
                eliminarEntidad(ncdSel);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("el Ajuste seleccionado ha sido eliminado satisfactoriamente!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Ajuste seleccionado No se puede eliminar. Está en proceso!"));
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("AjustesCond");
        ctx.getExternalContext().getSessionMap().remove("AjustesCondDT");


    }
}
