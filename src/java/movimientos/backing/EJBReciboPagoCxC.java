/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Usuario;
import informes.backing.EmailReciboPagoCxCCtrl;
import informes.backing.ReciboPagoCtrl;
import informes.modelos.PagoFacCxCDT;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoCxCDT;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoQuery;
import util.JSFUtil;
import util.Validator;

/**
 *
 * @author Jesus
 */
@Stateless
public class EJBReciboPagoCxC {

    private String para;
    private String asunto = "Recibo de pago del condominio";
    private String mensaje = "";
    private EmailReciboPagoCxCCtrl emailReciboPago;
    private ReciboPagoCtrl reciboPago;
    private BeanUsuario beanUsuario;
    private PagoCxCDT pagoCxCDT;
    private Usuario usuario;

    public void EJBReciboPagoCxC() {
    }

    public void generarReciboPago() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, MessagingException, ParseException {

        pagoCxCDT = new PagoCxCDT();
        emailReciboPago = new EmailReciboPagoCxCCtrl();
        reciboPago = new ReciboPagoCtrl();
        //-------------------------------------------------------------------------------------------------------------------

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPago.jasper");

        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoCxC pagocxc = (PagoCxC) ctx.getExternalContext().getSessionMap().get("PagoCxC");
        //-------------------------------------------------------------------------
        // Se busca el inmueble y el pago para obtener los parametros del reporte
        //-------------------------------------------------------------------------
        System.out.println("Estoy en el EJB");
        Inmueble inm = emailReciboPago.buscarInmueble(pagocxc.getInmueble().getId());

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();

        convertirPagoCxCADT(pagocxc);

        String fecEmiPago = pagoCxCDT.getFecDocPagoCxC();
        String desPago = pagoCxCDT.getDesPagoCxC();
        String numDocPago = pagoCxCDT.getNumDocPagoCxC();
        String mtoPago = pagoCxCDT.getMtoPagoCxC();
        String formaPago = pagoCxCDT.getFormaPagoCxC();
        String fecDocPago = pagoCxCDT.getFecDocPagoCxC();
        String bancoPago = pagoCxCDT.getBancoPagoCxC();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        Condominio condominio = emailReciboPago.buscarCondominio();
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //--------------------------------------------------------------
        // Se obtiene el numero del recibo y las observaciones de la 
        // la clase parametro del condominio actual.
        //--------------------------------------------------------------
        Integer numReciboCobro;
        String obsReciboCobro;

        Parametro parametro = condominio.getParametro();

        if (parametro != null) {
            numReciboCobro = parametro.getProxReciboCobro();
            obsReciboCobro = parametro.getObsReciboPago();
            parametro.setProxReciboCobro(numReciboCobro + 1);
            emailReciboPago.actualizarParametro(parametro);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...reporte cancelado"));
            return;
        }

        //------------------------------------------------------------
        // Se crea el datasource de facturas de pagos de cxc
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagoFacCxC;

        Collection<PagoFacCxCDT> pagoFacCxCDT = emailReciboPago.buscarPagoFacCxC(codInm, pagoCxCDT);

        dataSourcePagoFacCxC = new JRBeanCollectionDataSource(pagoFacCxCDT);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("codInm", codInm);
        parametros.put("nombProp", nombProp);
        parametros.put("cedProp", cedProp);
        parametros.put("fecEmiPago", fecEmiPago);
        parametros.put("desPago", desPago);
        parametros.put("numDocPago", numDocPago);
        parametros.put("mtoPago", mtoPago);
        parametros.put("formaPago", formaPago);
        parametros.put("fecDocPago", fecDocPago);
        parametros.put("bancoPago", bancoPago);
        parametros.put("logo", logo);
        parametros.put("numReciboCobro", numReciboCobro);
        parametros.put("obsReciboCobro", obsReciboCobro);
        // ------------------------------------------------------------------------------------------------------------------
        // Genera el reporte en jasper nativo : EdoCuentaInm.jrprint
        //-------------------------------------------------------------------------------------------------------------------
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, dataSourcePagoFacCxC);

        //-------------------------------------------------------------------------------------------------------------------
        // Exportar el reporte anterior a pdf
        // ------------------------------------------------------------------------------------------------------------------
        Long milisegundos = Calendar.getInstance().getTimeInMillis();
        String nombre = milisegundos.toString().concat("ReciboPagoInm.pdf");
        String reportePdf = beanUsuario.getRutaTemporal().concat(nombre);

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

        setPara(inm.getEmailPropInmueble());

        enviarEmail(para, asunto, mensaje, reportePdf, pagocxc, condominio);

        //--------------------------------------
        // Se elimina el anexo creado 
        // -------------------------------------
        File f = new File(reportePdf);
        f.delete();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El correo electrónico fué enviado exitosamente!"));
    }

    public void enviarEmail(String para, String asunto, String mensaje, String reportePdf, PagoCxC pagocxc, Condominio cond) throws MessagingException {

        //  Protocol protocol = Protocol.SMTP;
        Properties props = new Properties();
        int port = 587;

        /*String host = "condominioexpressve.com";
        final String emailUsuario="recibopagocxc@condominioexpressve.com";
        final String contraseñaUsuario="tango#16";*/
        
        final String emailUsuario = "condominioexpressve@gmail.com";
        final String contraseñaUsuario = "pish unfc sxxl hzwf";
        String host = "smtp.gmail.com";

        boolean auth = true;
        boolean debug = true;

        String nombQuery = "buscarUsuarioPorId";
        DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
        try {
            usuario = dataAcces.buscarEntidadPorId(beanUsuario.getIdUsuario(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        if (usuario.getEmailUsuario().isEmpty()) {

            return;
        }

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);

        /* Propiedades recomendadas por JRH:
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.office365.com");*/
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
        adjunto.setFileName("Recibo de pago del condominio.pdf");

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
            message.setRecipients(Message.RecipientType.BCC, cond.getEmailCondominio());
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

    }

    public void convertirPagoCxCADT(PagoCxC pagocxc) throws ParseException {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        pagoCxCDT.setId(pagocxc.getId());
        pagoCxCDT.setDesPagoCxC(pagocxc.getDesPagoCxC());
        pagoCxCDT.setNumDocPagoCxC(pagocxc.getNumDocPagoCxC());
        pagoCxCDT.setMtoPagoCxC(val.convertirDoubleAString(pagocxc.getMtoPagoCxC()));
        pagoCxCDT.setFormaPagoCxC(pagocxc.getFormaPagoCxC());
        pagoCxCDT.setFecDocPagoCxC(pagocxc.getFecDocPagoCxC());
        pagoCxCDT.setFecDocPagoCal(reciboPago.convertirFecha(pagocxc.getFecDocPagoCxC()));
        Integer idBanco = Integer.parseInt(pagocxc.getBancoPagoCxC());
        String nombBco = reciboPago.buscarBanco(idBanco);
        pagoCxCDT.setBancoPagoCxC(nombBco);

        pagoCxCDT.setFecMovPagoCxC(pagocxc.getFecDocPagoCxC());
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getPara() {
        return para;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
