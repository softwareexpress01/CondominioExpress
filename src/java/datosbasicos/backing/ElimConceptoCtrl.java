package datosbasicos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
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
public class ElimConceptoCtrl extends DaoCrud<Concepto, Condominio, Integer> implements Serializable {

    public ElimConceptoCtrl() {
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

    public void eliminarConcepto() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Concepto concSel = (Concepto) ctx.getExternalContext().getSessionMap().get("Concepto");
        concSel.setEdoConcepto("INA"); 

        try {
            almacenarOModificarEntidad(concSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Concepto");
        ctx.getExternalContext().getSessionMap().remove("ConceptoDT");
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El concepto " + concSel.getNombConcepto() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
