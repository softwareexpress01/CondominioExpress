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
import java.util.Date;
import java.util.Properties;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.hibernate.HibernateException;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MainCtrl implements Serializable {

    private String numSuscripcion;
    private String contraseñaSuscripcion;
    private String contSus;
    private String nombContacto;
    private String apelContacto;
    private String emailContacto;
    private String telContacto;
    private String mensajeContacto;
    private Suscripcion susc;

    public MainCtrl() {
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
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            susc = dataAcces.buscarEntidad(Integer.parseInt(numero), nombQuery);
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

        return ("registrarpago");
    }

    public String validarPanelContacto() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (getNombContacto().equals(enBlanco)) {
            mensaje = "Por favor, ingrese su nombre...";
            return mensaje;
        }

        if (getApelContacto().equals(enBlanco)) {
            mensaje = "Por favor, ingrese su apellido...";
            return mensaje;
        }
        if (getEmailContacto().equals(enBlanco)) {
            mensaje = "Por favor, ingrese su correo electrónico...";
            return mensaje;
        }
        if (!val.esEmail(getEmailContacto())) {
            mensaje = "El formato del correo electrónico es inválido, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public void inicializarPanelContacto() {

        setNombContacto(null);
        setApelContacto(null);
        setEmailContacto(null);
        setTelContacto(null);
        setMensajeContacto(null);
        
    }

    public void enviarEmailContacto(ActionEvent actionEvent) throws MessagingException {

        String vacio = new String();
        String mensaje = validarPanelContacto();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage("men",
                    new FacesMessage(mensaje));
            return;
        }

        boolean auth = true;
        boolean debug = true;
        int port = 587;
        String host = "smtp.gmail.com";
        //   Protocol protocol = Protocol.SMTP;
        debug = true;
        final String emailUsuario = "softwareexpress00@gmail.com";
        final String contraseñaUsuario = "mcbo&59&";

        String para = "softwareexpress00@gmail.com";
        String asunto = "Pregunta o comentario desde la pagina de la compañia";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);//Protocol.SMTP

        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {

                private PasswordAuthentication pa = new PasswordAuthentication(emailUsuario, contraseñaUsuario);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);
        // Construir correo de texto 

        // 1. Se construye el texto que viene de la pagina

        BodyPart texto = new MimeBodyPart();
        String msj = "Fue enviado un mensaje por el Sr(a):" + nombContacto + " " + apelContacto + "," + "con los siguientes datos:"
                + "correo electrónico: " + "" + emailContacto + " " + "," + "telefono:" + telContacto + ","
                + "y el siguiente comentario / pregunta:" + "" + mensajeContacto;

        texto.setText(msj);

        MimeMultipart multiParte = new MimeMultipart();

        multiParte.addBodyPart(texto);

        //-----------------------------------------------------------
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailUsuario));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);
        } catch (MessagingException ex) {
            FacesContext.getCurrentInstance().addMessage("men",
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        
        inicializarPanelContacto();
        
        FacesContext.getCurrentInstance().addMessage("men",
                new FacesMessage("Su mensaje fue enviado satisfactoriamente!"));
        return;
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

    public void setNombContacto(String nombContacto) {
        this.nombContacto = nombContacto;
    }

    public String getNombContacto() {
        return nombContacto;
    }

    public void setApelContacto(String apelContacto) {
        this.apelContacto = apelContacto;
    }

    public String getApelContacto() {
        return apelContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setTelContacto(String telContacto) {
        this.telContacto = telContacto;
    }

    public String getTelContacto() {
        return telContacto;
    }

    public void setMensajeContacto(String mensajeContacto) {
        this.mensajeContacto = mensajeContacto;
    }

    public String getMensajeContacto() {
        return mensajeContacto;
    }
}
