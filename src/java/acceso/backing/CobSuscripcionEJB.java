/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.HibernateException;
import saas.modelos.Plan;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import util.DaoQuery;

/**
 *
 * @author Jesus
 */
@Stateless
public class CobSuscripcionEJB {

    Collection<SuscripcionDT> listaSusIna = new ArrayList();
    Collection<Suscripcion> listaSusSel = new ArrayList();
    String para;
    String asunto;
    String mensaje;
    String ruta = "D:/Sistemas/DesarrolloNew/CondominioExpress/web/temporal/";

    public CobSuscripcionEJB() {
    }

    //  @Schedule(minute = "50", hour = "17", persistent = false)
    public void generarReporteCobSuscripcion() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, MessagingException {

        System.out.println("Iniciando....EJB de cobranza ");
        //-------------------------------------------------------------------------------------------------------------------

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/CobSuscripcion.jasper");

        JRBeanCollectionDataSource dataSourceSusProcesadas;

        Collection susCorteHoy = buscarSusCorteHoy();

        if (listaSusIna.isEmpty()) {
            System.out.println("No se desactivó ninguna ...");
        }

        dataSourceSusProcesadas = new JRBeanCollectionDataSource(susCorteHoy);

        //---------------------------------------------------------
        HashMap parametros = new HashMap();

        //parametros.put("susProcesadas", dataSourceSusProcesadas);
        // ------------------------------------------------------------------------------------------------------------------
        // Genera el reporte en jasper nativo : EdoCuentaInm.jrprint
        //-------------------------------------------------------------------------------------------------------------------
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, dataSourceSusProcesadas);

        //-------------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------------------------------------------------------------------------
        // Exportar el reporte anterior a pdf
        // ------------------------------------------------------------------------------------------------------------------
        Long milisegundos = Calendar.getInstance().getTimeInMillis();

        //    BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //     String ruta = beanUsuario.getRuta();
        String nombre = milisegundos.toString().concat("CobSuscripcion.pdf");

        String reportePdf = ruta.concat(nombre);

        try {
            JRPdfExporter pdfExporter = new JRPdfExporter();

            pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportePdf);
            System.out.println("exportando el reporte.....");
            pdfExporter.exportReport();
            System.out.println("Hecho!.....");
        } catch (JRException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ocurrió un a falla temporal de comunicaciones...Por favor, intente mas tarde.."));
            return;
        }

        para = "softwareexpress00@gmail.com";
        asunto = "Desactivación de usuarios automática *PRUEBA* ";
        mensaje = "Se anexan los usuarios desactivados por falta de pago";

        enviarEmail(para, asunto, mensaje, reportePdf);

        //--------------------------------------
        // Se elimina el anexo creado 
        // -------------------------------------
        File f = new File(reportePdf);
        f.delete();

        System.out.println("Proceso de cobranza concluido * AMBIENTE DE PRUEBA * .El correo electrónico fué enviado exitosamente!");
    }

    public Collection buscarSusCorteHoy() {

        Collection<Suscripcion> listaSus;

        Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
        String fecCalString = calendarAString(fecCal);

        Integer diaHoy = fecCal.get(Calendar.DAY_OF_MONTH);
        String diaCorte = diaHoy.toString();

        String nombQuery = "buscarSuscripcionFechaCorteHoy";
        // from Suscripcion s join fetch s.plan where s.diaCorteSuscripcion = :dia and s.edoSuscripcion = 'ACT'
        DaoQuery<List> dataAcces = new DaoQuery();
        try {
            listaSus = dataAcces.buscarListaFetchPorDia(diaCorte, nombQuery);
        } catch (HibernateException he) {

            return (null);
        }

        if (listaSus.isEmpty()) {

            return listaSusIna;
        }

        // Seleccionar suscripciones con fecha de suscripcion <> hoy 
        String fecSusString;
        for (Iterator<Suscripcion> it = listaSus.iterator();
                it.hasNext();) {
            Suscripcion sus = (Suscripcion) it.next();
            fecSusString = calendarAString(sus.getFecSuscripcion());
            if (!(fecCalString.equals(fecSusString))) {
                listaSusSel.add(sus);
            }
        }

        //
        for (Iterator<Suscripcion> it = listaSusSel.iterator();
                it.hasNext();) {
            Suscripcion susSel = (Suscripcion) it.next();
            Plan plan = susSel.getPlan();
            Integer montoPlan = plan.getMtoPlan().intValue();
            if (susSel.getSaldoSuscripcion() >= montoPlan) {
                susSel.setSaldoSuscripcion(susSel.getSaldoSuscripcion() - susSel.getPlan().getMtoPlan());
            } else {
                susSel.setEdoSuscripcion("INA");
            }
            try {
                DaoQuery<Suscripcion> dataAcces1 = new DaoQuery();
                dataAcces1.almacenarEntidad(susSel);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones (SUSCRIPCION).por favor, intente mas tarde"));
                return listaSusIna;
            }
            if ("INA".equals(susSel.getEdoSuscripcion())) {
                SuscripcionDT susDT = new SuscripcionDT();
                susDT.setNumSuscripcion(susSel.getNumSuscripcion());
                susDT.setNumCedSuscriptor(susSel.getNumCedSuscriptor());
                susDT.setNombSuscriptor(susSel.getNombSuscriptor());
                susDT.setApelSuscriptor(susSel.getApelSuscriptor());
                susDT.setTelSuscriptor(susSel.getTelSuscriptor());
                susDT.setCelSuscriptor(susSel.getCelSuscriptor());
                susDT.setEdoSuscripcion(susSel.getEdoSuscripcion());
                susDT.setFecSuscripcionString(calendarAString(susSel.getFecSuscripcion()));
                listaSusIna.add(susDT);
            }

        }

        return listaSusIna;
    }

    public String calendarAString(Calendar fecha) {

        String perDia;
        String perMes;
        int dMov = fecha.get(Calendar.DAY_OF_MONTH);
        int mMov = fecha.get(Calendar.MONTH) + 1;
        int aMov = fecha.get(Calendar.YEAR);

        if (dMov < 10) {
            perDia = "0" + dMov;
        } else {
            perDia = String.valueOf(dMov);
        }

        if (mMov < 10) {
            perMes = "0" + mMov;
        } else {
            perMes = String.valueOf(mMov);
        }

        String stringFec = perDia + "-" + perMes + "-" + aMov;

        return stringFec;

    }

    public void enviarEmail(String para, String asunto, String mensaje, String reportePdf) throws MessagingException {

        Properties props = new Properties();
        int port;
        boolean auth;
        boolean debug;

        port = 587;
        /*String host = "condominioexpressve.com";
        final String emailUsuario = "recibopagocxc@condominioexpressve.com";
        final String contraseñaUsuario = "tango#16";*/
        
        final String emailUsuario = "condominioexpressve@gmail.com";
        final String contraseñaUsuario = "pish unfc sxxl hzwf";
        String host = "smtp.gmail.com";
        
        /*String host = "us2.smtp.mailhostbox.com";
        final String emailUsuario = "informes@condominioexpressve.com";
        final String contraseñaUsuario = "X^Z%Bmhs7";*/
        auth = true;
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

        // 2. Se construye el anexo
        BodyPart adjunto = new MimeBodyPart();
        adjunto.setDataHandler(new DataHandler(new FileDataSource(reportePdf)));
        adjunto.setFileName("Usuarios desactivados.pdf");

        // 2. Se une el anexo y el texto
        MimeMultipart multiParte = new MimeMultipart();

        multiParte.addBodyPart(texto);
        multiParte.addBodyPart(adjunto);

        //-----------------------------------------------------------
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(emailUsuario));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setRecipients(Message.RecipientType.CC, "softwareexpress01@gmail.com");

            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

    }
}
