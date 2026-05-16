/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

/**
 *
 * @author jesus
 */
import saas.modelos.Suscripcion;
import java.io.Serializable;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;


public class PagoMensualidadCtrl implements Serializable {

    private String numSuscripcion;
    private String contraseñaSuscripcion;
    private String contSus;
    
    private Suscripcion susc;

    public PagoMensualidadCtrl() {
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        //  ------------------Validar la suscripcion -------------
        if (this.getNumSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de la suscripción...";
            return mensaje;
        }
        mensaje = validarSuscripcion(numSuscripcion);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }
        //------------------Validar contraseña de suscripcion--------------------
        if (this.contraseñaSuscripcion.equals(enBlanco)) {
            mensaje = "Por favor, ingrese la contraseña de suscripción...";
            return mensaje;
        }
        mensaje = validarContSuscripcion(contraseñaSuscripcion);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }

        return mensaje;
    }

    public String validarSuscripcion(String numero) {

        String mensaje = "";
        String nombQuery = "buscarSuscripcion";
        Integer idSusc=Integer.parseInt(numero); 
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            susc = dataAcces.buscarEntidad(idSusc, nombQuery);
        } catch (HibernateException he) {
            mensaje = "Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde";
            return mensaje;
        }

        if (susc == null) {
            // suscripcion no existe
            mensaje = "Suscripción no ha sido registrada!, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public String validarContSuscripcion(String contraseña) {

        String mensaje = "";
        contSus = susc.getContSuscripcion();

        if (!contraseña.equals(contSus)) {
            // contraseña de suscripcion invalida
            mensaje = "Contraseña de suscripción inválida!, por favor, intente de nuevo";
            return mensaje;
        }
        return mensaje;

    }

    public String registrarPagoMensual() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setIdSuscripcion(susc.getId());
        beanUsuario.setNombSuscriptor(susc.getNombSuscriptor());
        beanUsuario.setNumSuscripcion(susc.getNumSuscripcion());
        
        if(beanUsuario==null) {
            System.out.println("No encontro el bean");
        }

        return ("registrarpagomensual");
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }

    public void setContraseñaSuscripcion(String contraseñaSuscripcion) {
        this.contraseñaSuscripcion = contraseñaSuscripcion;
    }

    public String getContraseñaSuscripcion() {
        return contraseñaSuscripcion;
    }

}
