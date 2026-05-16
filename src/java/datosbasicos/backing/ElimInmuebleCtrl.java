package datosbasicos.backing;

import acceso.modelos.CodigoAcceso;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimInmuebleCtrl extends DaoCrud<Inmueble, Propiedad, Integer> implements Serializable {

    public ElimInmuebleCtrl() {
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

    public void eliminarInmueble() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Inmueble inmSel = (Inmueble) ctx.getExternalContext().getSessionMap().get("Inmueble");

        try {
            eliminarEntidad(inmSel);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        
        /* Burcar objeto en la tabla de codigo de acceso y eliminar

        Integer idCodigoAcceso = inmSel.getCodigoAcceso().getId();
        
        String nombQuery = "buscarCodigoAccesoPorId";
       
        DaoQuery<CodigoAcceso> dataAcces = new DaoQuery<CodigoAcceso>();
        CodigoAcceso codAccesoObj = new CodigoAcceso();
        
        try {
            codAccesoObj = dataAcces.buscarEntidad(idCodigoAcceso, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            
        }

        DaoQuery<CodigoAcceso> dataAcces1 = new DaoQuery<CodigoAcceso>();
        try {
            dataAcces1.eliminarEntidad(codAccesoObj);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }

        */
        
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El inmueble " + inmSel.getCodInmueble() + " ha sido eliminado satisfactoriamente!"));
        return;

    }
}
