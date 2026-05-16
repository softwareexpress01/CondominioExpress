/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

/**
 *
 * @author Frank
 */

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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

public class ContactenosCtrl implements Serializable {

  
    private String nombContacto;
    private String apelContacto;
    private String emailContacto;
    private String telContacto;
    private String mensajeContacto;
  
    public ContactenosCtrl() {
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        return mensaje;
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
        
        port = 587;
       /*String host = "condominioexpressve.com";
        final String emailUsuario="administrador@condominioexpressve.com"; 
        final String contraseñaUsuario="tango#16";*/
        Protocol protocol = Protocol.SMTP;
        /*String host = "us2.smtp.mailhostbox.com";
        final String emailUsuario = "informes@condominioexpressve.com";
        final String contraseñaUsuario = "X^Z%Bmhs7";*/
        String host = "smtp.gmail.com";
        final String emailUsuario="condoexpress9@gmail.com"; 
        final String contraseñaUsuario="molr kjeo foup vmnw";
        
        debug = true;
        
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
                + "correo electrónico: " + "" + emailContacto + " " + "," + "teléfono:" + telContacto + ","
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
            message.addRecipient(Message.RecipientType.CC, new InternetAddress("softwareexpress01@gmail.com"));
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
