/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.*;
import util.Protocol;

/**
 *
 * @author Jesus
 */
@Stateless
public class EmailCorrespondenciaEJB {

    private Boolean cc = true; //Con copia al administrador

    @Asynchronous
    public void enviarEmailTodos(Collection listaInm, String asunto, String mensaje, Condominio condominio) throws MessagingException {

        String espacios = " ";

        try {
            for (Iterator<Inmueble> it = listaInm.iterator();
                    it.hasNext();) {
                Inmueble inm = (Inmueble) it.next();
                if ((inm.getEmailPropInmueble().equals(espacios))) {
                    continue;
                }
                enviarEmailInmueble(inm.getEmailPropInmueble(), asunto, mensaje, condominio);
            }
            cc = true;
        } catch (RuntimeException re) {
            cc = true;
            System.out.println(" Ocurrió un erro al procesar la lista de inmuebles en enviarEmailTodos del ejb");
        }
        
    }

    public void enviarEmailInmueble(String para, String asunto, String mensaje, Condominio condominio) throws MessagingException {

        Protocol protocol = Protocol.SMTP;
        Properties props = new Properties();
        int port;
        String host;
        boolean auth = true;
        boolean debug = true;
       final String emailUsuarioEjb = "administrador@condominioexpressve.com";
        final String contraseñaUsuarioEjb = "tango#16";
       
       /*host = "us2.smtp.mailhostbox.com";
        final String emailUsuarioEjb = "informes@condominioexpressve.com";
        final String contraseñaUsuarioEjb = "X^Z%Bmhs7";*/

        port = 587;
        host = "condominioexpressve.com";
        auth = true;
        debug = true;
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        switch (protocol) {
            case SMTP:
                props.put("mail.smtp.starttls.enable", true);
                break;
            case SMTPS:
                props.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                props.put("mail.smtp.starttls.enable", true);
                break;
        }
        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(emailUsuarioEjb, contraseñaUsuarioEjb);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);

        session.setDebug(debug);
        // Construir correo de texto con anexo

        // 1. Se construye el texto que viene de la pagina
        BodyPart texto = new MimeBodyPart();
        texto.setText(mensaje);

        MimeMultipart multiParte = new MimeMultipart();

        multiParte.addBodyPart(texto);

        //-----------------------------------------------------------
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailUsuarioEjb));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            if (cc) {
                message.setRecipients(Message.RecipientType.BCC, condominio.getEmailCondominio());
                cc = false;
            }
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);

        } catch (AddressException sf) {
            System.out.println("Ocurrio un error al enviar el correo:" + sf);
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }
    }

    /**
     *
     * @param para
     * @param asunto
     * @param mensaje
     * @param condominio
     * @throws MessagingException
     */
    @Asynchronous
    public void enviarEmail(String para, String asunto, String mensaje, Condominio condominio) throws MessagingException {

        Protocol protocol = Protocol.SMTP;
        Properties props = new Properties();
        int port;
        String host;
        boolean auth = true;
        boolean debug = true;
        final String emailUsuarioEjb = "administrador@condominioexpressve.com";
        final String contraseñaUsuarioEjb = "tango#16";
        
       /* host = "us2.smtp.mailhostbox.com";
        final String emailUsuarioEjb = "informes@condominioexpressve.com";
        final String contraseñaUsuarioEjb = "X^Z%Bmhs7";*/

        port = 587;
        host = "condominioexpressve.com";
        auth = true;
        debug = true;
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        switch (protocol) {
            case SMTP:
                props.put("mail.smtp.starttls.enable", true);
                break;
            case SMTPS:
                props.put("mail.smtp.ssl.enable", true);
                break;
            case TLS:
                props.put("mail.smtp.starttls.enable", true);
                break;
        }
        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(emailUsuarioEjb, contraseñaUsuarioEjb);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);

        session.setDebug(debug);
        // Construir correo de texto con anexo

        // 1. Se construye el texto que viene de la pagina
        BodyPart texto = new MimeBodyPart();
        texto.setText(mensaje);

        MimeMultipart multiParte = new MimeMultipart();

        multiParte.addBodyPart(texto);

        //-----------------------------------------------------------
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailUsuarioEjb));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setRecipients(Message.RecipientType.BCC, condominio.getEmailCondominio());
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);

        } catch (AddressException sf) {
            System.out.println("Ocurrio un error al enviar el correo:" + sf);
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }
    }

}
