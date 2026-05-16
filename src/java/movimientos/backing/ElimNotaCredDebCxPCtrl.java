package movimientos.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import movimientos.modelos.NotaCredDebCxP;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimNotaCredDebCxPCtrl extends DaoCrud<NotaCredDebCxP, NotaCredDebCxP, Integer> implements Serializable {

    public ElimNotaCredDebCxPCtrl() {
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

    public void eliminarNotaCredDebCxP() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        NotaCredDebCxP ncdSel = (NotaCredDebCxP) ctx.getExternalContext().getSessionMap().get("NotaCredDebCxP");

        
        if ("PEND".equals(ncdSel.getEdoNotaCredDebCxP())) {
            try {
                eliminarEntidad(ncdSel);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Nota de Cr-Db de CxP " + ncdSel.getId() + " ha sido eliminada de la Base de datos "));
        }else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Nota de Cr-Db de CxP " + ncdSel.getId() + " No se puede eliminar. Está en proceso "));
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("NotaCredDebCxP");
        ctx.getExternalContext().getSessionMap().remove("NotaCredDebCxPDT");

        

    }
}
