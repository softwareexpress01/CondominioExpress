/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.hibernate.HibernateException;
import saas.modelos.Compañia;
import util.DaoQuery;
import util.Protocol;

/**
 *
 * @author Jesus
 */
@Stateless
public class IpEJB {

    String ipActual = "";
    String ip = "";
    Compañia comp;
    LoginCtrl login;

    public void IPEJB() {
    }

 //  @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void verificarIP() throws IOException, MessagingException {

        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine();
            in.close();
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }

        buscarIpActual();
        if (!ip.equals(ipActual)) {
            actualizarIpActual(ip);
            enviarEmail();
        } else {
            System.out.println("Mi ip publica sigue siendo = " + ipActual);
        }
    }

    private void buscarIpActual() {

        login = new LoginCtrl();
        comp = login.buscarCompañia();
        ipActual = comp.getIp();

    }

    private void actualizarIpActual(String ip) {

        comp.setIp(ip);

        DaoQuery<Compañia> dataAcces1 = new DaoQuery<Compañia>();
        try {
            dataAcces1.almacenarOModificarEntidad(comp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void enviarEmail() throws MessagingException {


        Properties props = new Properties();
        int port;
        String host;
        boolean auth;
        boolean debug;
        final String emailUsuario = "softwareexpress00@gmail.com";
        final String contraseñaUsuario = "tango#16";
        String para = "softwareexpress00@gmail.com";
        String asunto = "Cambio de direccion ip *PRUEBA*";
        String mensaje = "La nueva dirección ip pública es: " + ip;

        port = 587;
        host = "smtp.gmail.com";
        auth = true;
        Protocol protocol = Protocol.SMTP;
        debug = true;
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);

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
        // Construir correo de texto con anexo

        // 1. Se construye el texto que viene de la pagina

        BodyPart texto = new MimeBodyPart();
        texto.setText(mensaje);

        MimeMultipart multiParte = new MimeMultipart();

        multiParte.addBodyPart(texto);

        //-----------------------------------------------------------
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailUsuario));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(
                    "softwareexpress01@gmail.com"));
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);

        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

    }
}
