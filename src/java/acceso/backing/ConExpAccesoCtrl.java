/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

/**
 *
 * @author jesus
 */
import acceso.modelos.CodigoAcceso;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.*;

public class ConExpAccesoCtrl implements Serializable {

    private String codAcceso;
    
    public ConExpAccesoCtrl() {
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = new Validator();

        //  ------------------Validar la suscripcion -------------
        if (this.getCodAcceso().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el codigo de acceso...";
            return mensaje;
        }
        if (!val.esEntero(codAcceso)) {
            mensaje = "El codigo de acceso debe ser numérico entero";
            return mensaje;
        }
        mensaje = validarCodAcceso(codAcceso);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }


        return mensaje;
    }

    public String validarCodAcceso(String numero) {
        
        // Aqui se valida si el codigo indicado por el usuario existe. Si es asi se asigna los valores al BeanUsuario para ser usados por los reportes

        String mensaje = "";

        String nombQuery = "buscarCodigoAccesoPorId";
        Integer idAcceso = Integer.parseInt(numero);

        DaoQuery<CodigoAcceso> dataAcces = new DaoQuery<CodigoAcceso>();
        CodigoAcceso codAccesoObj = new CodigoAcceso();
        
        try {
            codAccesoObj = dataAcces.buscarEntidad(idAcceso, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        
        if (codAccesoObj == null) {
            // suscripcion no existe
            mensaje = "Codigo de acceso no registrado!, por favor intente de nuevo";
            return mensaje;
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (beanUsuario == null) {
            System.out.println("No encontro el bean");
        }

        Condominio condominio = codAccesoObj.getCondominio();
        beanUsuario.setIdCondActual(condominio.getId());
        Inmueble inmueble = codAccesoObj.getInmueble();
        beanUsuario.setIdInm(inmueble.getId());
        beanUsuario.setNombUsuario(inmueble.getNombPropInmueble()); 
        beanUsuario.setCondominioActual(condominio.getNombCondominio());
  
        return mensaje;
    }

    public String entrar() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }
        
        return ("menuConsultaExpress");
    }

    public Condominio buscarCondominio(Integer idCond) throws ValidatorException {

        Condominio condominio;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void setCodAcceso(String codAcceso) {
        this.codAcceso = codAcceso;
    }

    public String getCodAcceso() {
        return codAcceso;
    }
}
