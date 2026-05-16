/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

/**
 *
 * @author jesus
 */
import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Proveedor;
import static informes.backing.ConExpAvisoDeCobroCtrl.redondearDecimales;
import informes.modelos.AvisoDeCobroDT;
import informes.modelos.AvisoInmDT;
import informes.modelos.CxCDT;
import informes.modelos.CxCInmDT;
import informes.modelos.EmailAvisosEJB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import movimientos.modelos.AjustesCond;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoAdelantado;
import movimientos.modelos.TranFondo;
import movimientos.modelos.GastoComunExt;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.HibernateException;
import util.*;

public class EmailAvisoDeCobroCtrl_1 extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    @EJB
    private EmailAvisosEJB ejb;
    private HtmlSelectOneRadio seleccion;
    private String emailUsuario;
    private String contraseñaUsuario;
    private String para;
    private String asunto;
    private String mensaje;
    private Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();
    private Condominio condominio;
    private String idInmueble;
    private String periodo;
    private int progreso;
    Calendar periodoCal;
    String periodoStr;
    String nombProveedor;
    Calendar periodoActual;
    Calendar periodoActual2;
    BeanUsuario beanUsuario;
    BeanUsuario beanUsuarioEJB;
    Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
    Collection<AvisoDeCobroDT> gastoTotal = new ArrayList<AvisoDeCobroDT>();
    Collection<AvisoDeCobroDT> gastoTotalOrd = new ArrayList<AvisoDeCobroDT>();
    Collection<FacGastoNoComun> listaGNCMes = new ArrayList<FacGastoNoComun>();
    Collection<NotaCredDebCxC> notasDebPer = new ArrayList<NotaCredDebCxC>();
    Collection listaAjustes = new ArrayList();
    Collection avisos = new ArrayList();
    Double mtoReserva;
    Double mtoPrestaciones;
    Double mtoAjustes;
    Double totalGeneral = 0.0;
    Double cxcTotal = 0.0;
    Double gncTotalMes = 0.0;
    Double totalMes = 0.0;
    Double mtoAlicuota = 0.0;
    Double totalParcial = 0.0;
    Double mtoIniReserva;
    Double saldoReserva;
    Double mtoIniPrestaciones;
    Double saldoPrestaciones;
    Double mtoIniEspecial;
    Double saldoEspecial;
    Integer proxAvisoCobro;
    String obsAvisoCobro;
    String obsAvisoCobro2;
    private String servidor;
    private Boolean enviado = true;
    // Parámetros:
    String codInm;
    String propInm;
    String telInm;
    String emailInm;
    Double porcentajeInm;
    String tipoFacturacion;
    Double mtoFijoFacturacion = 0.0;
    Double totalGCE = 0.0;
    private String ceroString = "0";
    private String mensajeError;
    Float porcMora;
    Double totalMora = 0.0;
    Double totalNotasDebPer;
    Double totalCEsp = 0.0;
    Double totalAntInm = 0.0;
    Double mtoAliPag = 0.0;
    Collection<AjustesCond> ajustes = new ArrayList<AjustesCond>();
    Boolean tasaExiste;
    Float valorTasa = 0.0f;
    Double valorTasaDouble = 0.0;
    Double mtoTotalDolares = 0.0;

    public EmailAvisoDeCobroCtrl_1() {
        //this.beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
    }

    public void inicializarPanel() {

        this.setSeleccion(null);
        this.setEmailUsuario(null);
        this.setContraseñaUsuario(null);
        this.setPara(null);
        this.setAsunto(null);
        this.setMensaje(null);
        this.setProgreso(0);

    }

    public String irASeleccionarCondominio() {

        //beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        beanUsuario.setDeshabilitar(false);

        return "selcond";
    }

    public String salir() {

        //beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setDeshabilitar(false);

        return "salir";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String msg = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (!this.idInmueble.equals(ceroString)) {
            if (beanUsuario.getEmailPropInmueble().equals(enBlanco)) {
                msg = "Por favor, ingrese el correo electrónico del destinatario...";
                return msg;
            } else {
                if (!val.esEmail(beanUsuario.getEmailPropInmueble())) {
                    msg = "El formato del correo electrónico del destinatario es inválido, por favor intente de nuevo";
                    return msg;
                }
            }
        }
        if (this.getAsunto().equals(enBlanco)) {
            msg = "Por favor, ingrese el asunto a referirse en el correo...";
            return msg;
        }
        return msg;
    }

    public String generarAvisoDeCobro() throws ParseException, ClassNotFoundException, SQLException, IOException, JRException, MessagingException {

        //---------------------------------------------------------------------------------
        // Recupera el inmueble desde la pagina de generación de avisos de cobro
        //---------------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();

        periodo = (String) ctx.getExternalContext().getSessionMap().get("periodo");
        idInmueble = (String) ctx.getExternalContext().getSessionMap().get("inmueble");

        String vacio = new String();

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //tasaExiste = beanUsuario.getTasaExiste();
        tasaExiste = (Boolean) ctx.getExternalContext().getSessionMap().get("tasaExiste");
        valorTasa = (float) ctx.getExternalContext().getSessionMap().get("tasa");
        beanUsuario.setTasaExiste(tasaExiste);
        beanUsuario.setTasa(valorTasa.toString());

        mensajeError = validarPanel();

        if (!mensajeError.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensajeError));
            return null;
        }

        // ********************  Se verifica si el proceso se efectuará local o por medio del EJB
        Integer idInm = Integer.valueOf(idInmueble);

        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = buscarCondominioFetchPropiedades(idCond);

        if (idInm == 0) {

            listaInm = buscarInmueblesPropiedades(condominio);

            if (listaInm.size() > 45) {

                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::: LLAMANDO AL METODO ");
                ejb.sendEmail(idCond, periodo, beanUsuario, idInmueble, asunto, mensaje, emailUsuario, contraseñaUsuario,
                        tasaExiste, valorTasa);
                System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::: REGRESANDO DEL  METODO ");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Solicitud del Informe aceptada, por favor verifique su correo electrónico en unos minutos."));
                return "volverAvisoDeCobro";

            }
        }
        generarReporteAvisosDeCobro();

        if (enviado) {
            if (idInm == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Los correos electrónicos fueron enviados exitosamente!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("El correo electrónico fué enviado exitosamente!"));
            }
        } else {
            enviado = true;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ocurrió un a falla temporal de comunicaciones...Por favor, intente mas tarde.. (1)"));
        }
        inicializarPanel();
        return "volverAvisoDeCobro";
    }

    public void generarReporteAvisosDeCobro() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException, MessagingException {

        Integer cero = 0;
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        tasaExiste = beanUsuario.getTasaExiste();

        //-------------------------------------------------------
        // Se genera el datasource de Gastos e inmuebles 
        //-------------------------------------------------------
        String stringPeriodo = "01-" + periodo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        periodoActual = cal;
        periodoActual2 = cal1;

        periodoActual2.add(Calendar.MONTH, 1);

        buscarFondos(idCond);
        totalGCE = buscarGCE(idCond);

        // Aqui se determina si se buscaran todos los inmuebles o uno en particular 
        Integer idInm = Integer.valueOf(idInmueble);
        Collection filasReporte = null;
        Inmueble inm;

        if (idInm.equals(cero)) {
            Condominio condProp = buscarCondominioFetchPropiedades(idCond);
            listaInm = buscarInmueblesPropiedades(condProp);
            generarDatosYEnviarEmailATodos(idCond, listaInm, periodo);

        } else {
            inm = buscarInmueblePorId();
            filasReporte = generarDatosInmueble(idCond, inm);
            emitirAvisoDeCobro(filasReporte, periodo);
        }

    }

    public void generarReporteAvisosDeCobro(Integer idCond, String periodo, BeanUsuario beanUsuario, String idInmueble, String asunto,
            String mensaje, String emailUsuario, String contraseñaUsuario, Boolean tasaExiste, Float valorTasa) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException, MessagingException {

        Integer cero = 0;
        this.idInmueble = idInmueble;
        this.periodo = periodo;
        this.beanUsuario = beanUsuario;
        beanUsuarioEJB = beanUsuario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.emailUsuario = emailUsuario;
        this.contraseñaUsuario = contraseñaUsuario;

        this.tasaExiste = tasaExiste;
        this.valorTasa = valorTasa;

        //-------------------------------------------------------
        // Se genera el datasource de Gastos e inmuebles 
        //-------------------------------------------------------
        String stringPeriodo = "01-" + periodo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        periodoActual = cal;
        periodoActual2 = cal1;

        periodoActual2.add(Calendar.MONTH, 1);

        buscarFondos(idCond);
        totalGCE = buscarGCE(idCond);

        // Aqui se determina si se buscaran todos los inmuebles o uno en particular 
        Integer idInm = Integer.valueOf(idInmueble);
        Collection filasReporte = null;
        Inmueble inm;

        if (idInm.equals(cero)) {
            Condominio condProp = buscarCondominioFetchPropiedades(idCond);
            listaInm = buscarInmueblesPropiedades(condProp);
            generarDatosYEnviarEmailATodos(idCond, listaInm, periodo);

        } else {
            inm = buscarInmueblePorId();
            filasReporte = generarDatosInmueble(idCond, inm);
            emitirAvisoDeCobro(filasReporte, periodo);
        }

    }

    public void emitirAvisoDeCobro(Collection filasReporte, String periodo)
            throws FileNotFoundException, JRException, MessagingException {

        //-------------------------------------------------------------------------------------------------------------------
        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobroMstr.jasper");
        InputStream subRep = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobro.jasper");
        //-------------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String ruta = beanUsuario.getRuta();
        setPara(beanUsuario.getEmailPropInmueble());

        condominio = buscarCondominio(beanUsuario.getIdCondActual());
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        String dirCondominio = condominio.getDirCondominio();

        //-------------------------------------------------------
        // Se genera el datasource de Gastos e inmuebles 
        //-------------------------------------------------------
        JRBeanCollectionDataSource dataSource;
        dataSource = new JRBeanCollectionDataSource(filasReporte);

        HashMap parametros = new HashMap();

        parametros.put("nombCondominio", condominio.getNombCondominio());
        parametros.put("dirCondominio", dirCondominio);
        parametros.put("periodo", periodo);
        parametros.put("logo", logo);
        parametros.put("subRep01", subRep);
        parametros.put("gastos", dataSource);
        parametros.put("mtoIniReserva", mtoIniReserva);
        parametros.put("saldoReserva", saldoReserva);
        parametros.put("mtoIniPrestaciones", mtoIniPrestaciones);
        parametros.put("saldoPrestaciones", saldoPrestaciones);
        parametros.put("mtoIniEspecial", mtoIniEspecial);
        parametros.put("saldoEspecial", saldoEspecial);
        parametros.put("mtoGCE", totalGCE);
        // parametros.put("proxAvisoCobro", proxAvisoCobro);
        parametros.put("obsAvisoCobro", obsAvisoCobro);
        parametros.put("obsAvisoCobro2", obsAvisoCobro2);

        // ------------------------------------------------------------------------------------------------------------------
        // Genera el reporte en jasper nativo : EdoCuentaInm.jrprint
        //-------------------------------------------------------------------------------------------------------------------
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, new JREmptyDataSource());

        //-------------------------------------------------------------------------------------------------------------------
        // Exportar el reporte anterior a pdf
        // ------------------------------------------------------------------------------------------------------------------
        Long milisegundos = Calendar.getInstance().getTimeInMillis();
        String nombre = milisegundos.toString().concat("AvisoDeCobro.pdf");
        String reportePdf = beanUsuario.getRutaTemporal().concat(nombre);

        try {
            JRPdfExporter pdfExporter = new JRPdfExporter();

            pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportePdf);
            System.out.println("exportando el reporte.....");
            pdfExporter.exportReport();

        } catch (JRException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ocurrió un a falla temporal de comunicaciones...Por favor, intente mas tarde..(2)"));
            return;
        }
        enviarEmail(para, asunto, mensaje, reportePdf);

        //--------------------------------------
        // Se elimina el anexo creado 
        // -------------------------------------
        File f = new File(reportePdf);
        f.delete();
    }

    public void emitirAvisoDeCobro(Collection filasReporte, String periodo, String para)
            throws FileNotFoundException, JRException, MessagingException {

        //-------------------------------------------------------------------------------------------------------------------
        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobroMstr.jasper");
        InputStream subRep = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobro.jasper");
        //-------------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        //beanUsuario = beanUsuarioEJB;
        String ruta = beanUsuario.getRuta();
        condominio = buscarCondominio(beanUsuario.getIdCondActual());
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        String dirCondominio = condominio.getDirCondominio();
        //-------------------------------------------------------
        // Se genera el datasource de Gastos e inmuebles 
        //-------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        HashMap parametros = new HashMap();

        parametros.put("nombCondominio", condominio.getNombCondominio());
        parametros.put("dirCondominio", dirCondominio);
        parametros.put("periodo", periodo);
        parametros.put("logo", logo);
        parametros.put("subRep01", subRep);
        parametros.put("gastos", dataSource);
        parametros.put("mtoIniReserva", mtoIniReserva);
        parametros.put("saldoReserva", saldoReserva);
        parametros.put("mtoIniPrestaciones", mtoIniPrestaciones);
        parametros.put("saldoPrestaciones", saldoPrestaciones);
        parametros.put("mtoIniEspecial", mtoIniEspecial);
        parametros.put("saldoEspecial", saldoEspecial);
        parametros.put("mtoGCE", totalGCE);
        // parametros.put("proxAvisoCobro", proxAvisoCobro);
        parametros.put("obsAvisoCobro", obsAvisoCobro);
        parametros.put("obsAvisoCobro2", obsAvisoCobro2);

        // ------------------------------------------------------------------------------------------------------------------
        // Genera el reporte en jasper nativo : EdoCuentaInm.jrprint
        //-------------------------------------------------------------------------------------------------------------------
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, new JREmptyDataSource());

        //-------------------------------------------------------------------------------------------------------------------
        // Exportar el reporte anterior a pdf
        // ------------------------------------------------------------------------------------------------------------------
        Long milisegundos = Calendar.getInstance().getTimeInMillis();
        String nombre = milisegundos.toString().concat("AvisoDeCobro.pdf");
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
                    new FacesMessage("Ocurrió un a falla temporal de comunicaciones...Por favor, intente mas tarde..(3)"));
            return;
        }
        enviarEmail(para, asunto, mensaje, reportePdf);

        //--------------------------------------
        // Se elimina el anexo creado 
        // -------------------------------------
        File f = new File(reportePdf);
        f.delete();
        //--------------------------------------------------------------------//
    }

    public void enviarEmail(String para, String asunto, String mensaje, String reportePdf) throws MessagingException {

        final MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");

        final MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

        Protocol protocol = Protocol.SMTP;
        Properties props = new Properties();
        int port;
        String host;
        boolean auth = true;
        boolean debug = true;

        port = 587;
        host = "condominioexpressve.com";
        //host = "us2.smtp.mailhostbox.com";
        /*emailUsuario = "informes@condominioexpressve.com";
        contraseñaUsuario = "X^Z%Bmhs7";*/
        emailUsuario = "avisodecobro@condominioexpressve.com";
        contraseñaUsuario = "tango#16";
        auth = true;
        protocol = Protocol.SMTP; 
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

        adjunto.setDataHandler(
                new DataHandler(new FileDataSource(reportePdf)));
        adjunto.setFileName(
                "Aviso De Cobro.pdf");

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
            message.setRecipients(Message.RecipientType.BCC, condominio.getEmailCondominio());
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);

        } catch (AddressException sf) {
            System.out.println("Ocurrio un error al enviar el correo:" + sf);
        } catch (MessagingException ex) {
            enviado = false;
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

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

    public Collection generarDatosInmueble(Integer idCond, Inmueble inm) throws ParseException {

        listaGCO = buscarGastoComunOrdPorIdCond(idCond);
        Collection<FacCuotaEsp> listaCEsp = new ArrayList<FacCuotaEsp>();

        Alicuota aliPag; // Alicuota pagada
        String enBlanco = new String();

        // Las siguientes son para almacenar las nc y pad despues registradas despues del periodo
        Collection<PagoAdelantado> listaPADAfter = new ArrayList<PagoAdelantado>();
        Collection<NotaCredDebCxC> listaNCAfter = new ArrayList<NotaCredDebCxC>();
        //
        mtoReserva = buscarMontoReserva(idCond);
        mtoPrestaciones = buscarMontoPrestaciones(idCond);
        ajustes.clear();
        mtoAjustes = 0.0;
        ajustes = getAjustes(periodoActual);
        Double totalGNCInm = 0.0;
        Double alicuota = 0.0;
        buscarParametros(idCond);
        progreso += 20;

        //
        listaCEsp = obtenerFacCuotaEspInm(inm.getId());
        totalMora = getNotasDebitoInmMora(inm.getId());

        mtoAliPag = 0.0;
        aliPag = obtenerAlicuotaPeriodo(inm.getId(), periodoActual);
        if (!(aliPag == null)) {
            mtoAliPag = (aliPag.getMtoAlicuota() - aliPag.getSaldoAlicuota());
        }
        buscarMontoCxCInm(inm.getId(), idCond, inm.getAlicInmueble());

        //Para los saldos a favor con doc. periodo siguiente para deducirlos del recibo 06032018
        totalAntInm = 0.0;
        listaPADAfter = getPagosAdelantadosInmAfter(inm.getId());
        listaNCAfter = getNotasDeCreditoInmAfter(inm.getId());

        if (beanUsuario.getTasaExiste()) {
            valorTasa = Float.parseFloat(beanUsuario.getTasa());
        }

        //
        for (Iterator<GastoComunOrd> it2 = listaGCO.iterator();
                it2.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it2.next();

            AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();

            //--------------------------------------------------------------
            //Se da formato a la fecha del mov para llevarla al  data table
            //--------------------------------------------------------------
            Calendar fechaFac = gco.getFecMovGCOrd();
            String dia;
            String mes;
            int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
            int mesFac = fechaFac.get(Calendar.MONTH) + 1;
            int añoFac = fechaFac.get(Calendar.YEAR);

            if (diaFac < 10) {
                dia = "0" + diaFac;
            } else {
                dia = String.valueOf(diaFac);
            }

            if (mesFac < 10) {
                mes = "0" + mesFac;
            } else {
                mes = String.valueOf(mesFac);
            }

            String stringFechagco = dia + "-" + mes + "-" + añoFac;
            //--------------------------------------------------------------

            gastoDT.setCodInm(inm.getCodInmueble());
            gastoDT.setGrupo(1);
            gastoDT.setPropInm(inm.getNombPropInmueble());
            gastoDT.setTelInm(inm.getTelInmueble());
            gastoDT.setDirInm(inm.getDirInmueble());
            gastoDT.setEmailInm(inm.getEmailPropInmueble());
            gastoDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoDT.setNumAviso(proxAvisoCobro);
// Buscar y asignar el codigo de acceso a Consulta Express

            Integer idAcceso = null;
            if (!(inm.getCodigoAcceso() == null)) {

                idAcceso = inm.getCodigoAcceso().getId();
            } else {
                System.out.println(" no es null");
                idAcceso = 0;
            }
            gastoDT.setCodAcc(idAcceso.toString());

            gastoDT.setTotCxC(cxcTotal);
            gastoDT.setTotalMes(totalMes - totalAntInm);
            gastoDT.setTotGenCxC(totalGeneral - totalAntInm);

            gastoDT.setFecGasto(gco.getFecGastoComunOrd());
            Integer idConcepto = gco.getIdConcepto();
            String concepto = buscarNombConcepto(idConcepto);
            gastoDT.setConceptoGasto(concepto);
            gastoDT.setRefGasto(gco.getNumDocGastoComunOrd());
            Integer idProv = gco.getIdProveedor();
            String nombProv = buscarProveedor(idProv);
            gastoDT.setProvGasto(nombProv);
            gastoDT.setMtoGasto(gco.getMtoGastoComunOrd());

            if ("gastos".equals(tipoFacturacion)) {
                gastoDT.setAlicuotaGasto((gco.getMtoGastoComunOrd() * inm.getAlicInmueble()) / 100);
                gastoDT.setMtoAlicuota(mtoAlicuota);

            } else {
                gastoDT.setAlicuotaGasto(0.0);
                gastoDT.setMtoAlicuota(mtoFijoFacturacion);
            }
            gastoTotal.add(gastoDT);

            // crear un objeto para las observaciones 
            if (!gco.getObsGastoComunOrd().equals(enBlanco)) {
                AvisoDeCobroDT gastoDT2 = new AvisoDeCobroDT();
                gastoDT2.setCodInm(inm.getCodInmueble());
                gastoDT2.setGrupo(1);
                gastoDT2.setPropInm(inm.getNombPropInmueble());
                gastoDT2.setTelInm(inm.getTelInmueble());
                gastoDT2.setDirInm(inm.getDirInmueble());
                gastoDT2.setEmailInm(inm.getEmailPropInmueble());
                gastoDT2.setAlicuotaInm(inm.getAlicInmueble());
                gastoDT2.setNumAviso(proxAvisoCobro);

                // Buscar y asignar el codigo de acceso a Consulta Express
                Integer idAcceso2 = null;
                if (!(inm.getCodigoAcceso() == null)) {

                    idAcceso2 = inm.getCodigoAcceso().getId();
                } else {
                    idAcceso2 = 0;
                }

                gastoDT2.setCodAcc(idAcceso2.toString());

                gastoDT2.setTotCxC(cxcTotal);
                gastoDT2.setTotalMes(totalMes - totalAntInm);
                gastoDT2.setTotGenCxC(totalGeneral - totalAntInm);

                gastoDT2.setFecGasto(" ");
                gastoDT2.setConceptoGasto(" ");
                gastoDT2.setRefGasto(" ");
                gastoDT2.setProvGasto(gco.getObsGastoComunOrd());
                gastoDT2.setMtoGasto(null);

                gastoDT2.setAlicuotaGasto(null);
                gastoDT2.setMtoAlicuota(null);
                gastoTotal.add(gastoDT2);

            }

        }

        // Agregar un objeto con el gasto por el fondo de reserva //
        String stringFecha = CalendarAString(periodoActual);

        if (mtoReserva > 0) {
            AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();
            gastoDT.setCodInm(inm.getCodInmueble());
            gastoDT.setGrupo(1);

            gastoDT.setPropInm(inm.getNombPropInmueble());
            gastoDT.setTelInm(inm.getTelInmueble());
            gastoDT.setDirInm(inm.getDirInmueble());
            gastoDT.setEmailInm(inm.getEmailPropInmueble());
            gastoDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoDT.setNumAviso(proxAvisoCobro);
            gastoDT.setTotCxC(cxcTotal);
            gastoDT.setTotalMes(totalMes);

            gastoDT.setMtoAlicuota(mtoAlicuota);
            gastoDT.setTotGenCxC(totalGeneral);

            gastoDT.setFecGasto(stringFecha);
            gastoDT.setConceptoGasto("Fondo de Reserva");
            gastoDT.setRefGasto("N/A");
            gastoDT.setProvGasto("N/A");
            gastoDT.setMtoGasto(mtoReserva);
            gastoDT.setAlicuotaGasto((mtoReserva * inm.getAlicInmueble()) / 100);

            gastoTotal.add(gastoDT);

        }
        // Agregar un objeto con el gasto por el fondo de prestaciones //

        if (mtoPrestaciones > 0) {
            AvisoDeCobroDT gastoPresDT = new AvisoDeCobroDT();
            gastoPresDT.setCodInm(inm.getCodInmueble());
            gastoPresDT.setGrupo(1);

            gastoPresDT.setPropInm(inm.getNombPropInmueble());
            gastoPresDT.setTelInm(inm.getTelInmueble());
            gastoPresDT.setDirInm(inm.getDirInmueble());
            gastoPresDT.setEmailInm(inm.getEmailPropInmueble());
            gastoPresDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoPresDT.setNumAviso(proxAvisoCobro);
            gastoPresDT.setTotCxC(cxcTotal);
            gastoPresDT.setTotalMes(totalMes);

            gastoPresDT.setMtoAlicuota(mtoAlicuota);
            gastoPresDT.setTotGenCxC(totalGeneral);

            gastoPresDT.setFecGasto(stringFecha);
            gastoPresDT.setConceptoGasto("Prestaciones Sociales");
            gastoPresDT.setRefGasto("N/A");

            gastoPresDT.setProvGasto("N/A ");
            gastoPresDT.setMtoGasto(mtoPrestaciones);
            gastoPresDT.setAlicuotaGasto((mtoPrestaciones * inm.getAlicInmueble()) / 100);

            gastoTotal.add(gastoPresDT);
        }
        // Agregar objetos con los gastos por Ajustes //

        for (Iterator<AjustesCond> it2 = ajustes.iterator();
                it2.hasNext();) {
            AjustesCond ajuste = (AjustesCond) it2.next();

            AvisoDeCobroDT gastoAjusteDT = new AvisoDeCobroDT();
            gastoAjusteDT.setCodInm(inm.getCodInmueble());
            gastoAjusteDT.setGrupo(1);

            gastoAjusteDT.setPropInm(inm.getNombPropInmueble());
            gastoAjusteDT.setTelInm(inm.getTelInmueble());
            gastoAjusteDT.setDirInm(inm.getDirInmueble());
            gastoAjusteDT.setEmailInm(inm.getEmailPropInmueble());
            gastoAjusteDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoAjusteDT.setNumAviso(proxAvisoCobro);
            gastoAjusteDT.setTotCxC(cxcTotal);

            gastoAjusteDT.setTotalMes(totalMes);

            gastoAjusteDT.setTotGenCxC(totalGeneral);

            gastoAjusteDT.setFecGasto(stringFecha);
            gastoAjusteDT.setConceptoGasto(buscarNombConcepto(ajuste.getIdConcepto()));
            gastoAjusteDT.setRefGasto("N/A");

            gastoAjusteDT.setProvGasto(ajuste.getDescAjustesCond());
            gastoAjusteDT.setMtoGasto(ajuste.getMtoAjustesCond());
            gastoAjusteDT.setMtoAlicuota(mtoAlicuota);

            // Las siguientes condiciones son para que coloque el monto del ajuste y su alicuota con signo
            if ("Debito".equals(ajuste.tipoAjustesCond)) {
                gastoAjusteDT.setMtoGasto(ajuste.getMtoAjustesCond() * -1);
            }

            alicuota = (ajuste.getMtoAjustesCond() * inm.getAlicInmueble()) / 100;

            // Este if es para que muestre el porcentaje de ajuste No lo mostraba 01Nov2016 FRojas
            gastoAjusteDT.setAlicuotaGasto(0.0);
            if ("gastos".equals(tipoFacturacion)) {
                if ("Debito".equals(ajuste.tipoAjustesCond)) {
                    gastoAjusteDT.setAlicuotaGasto(alicuota * -1);
                } else {
                    gastoAjusteDT.setAlicuotaGasto(alicuota);
                }
            }
            gastoTotal.add(gastoAjusteDT);
        }

        // Agregar un objeto con el total gastos 
        AvisoDeCobroDT gastoTotDT = new AvisoDeCobroDT();
        gastoTotDT.setCodInm(inm.getCodInmueble());
        gastoTotDT.setGrupo(1);

        gastoTotDT.setPropInm(inm.getNombPropInmueble());
        gastoTotDT.setTelInm(inm.getTelInmueble());
        gastoTotDT.setDirInm(inm.getDirInmueble());
        gastoTotDT.setEmailInm(inm.getEmailPropInmueble());
        gastoTotDT.setAlicuotaInm(inm.getAlicInmueble());
        gastoTotDT.setNumAviso(proxAvisoCobro);
        gastoTotDT.setTotCxC(cxcTotal);

        gastoTotDT.setTotalMes(totalMes);

        gastoTotDT.setTotGenCxC(totalGeneral);

        gastoTotDT.setFecGasto("");
        gastoTotDT.setConceptoGasto("");
        gastoTotDT.setRefGasto("");
        gastoTotDT.setProvGasto("TOTAL GASTOS DEL MES");
        gastoTotDT.setMtoGasto(totalParcial);

        // condicion oara que imprima el monto fijo en caso de este tipo de facturación hoy 090518 FRH
        if ("gastos".equals(tipoFacturacion)) {
            gastoTotDT.setAlicuotaGasto((totalParcial * inm.getAlicInmueble()) / 100);
        } else {
            gastoTotDT.setAlicuotaGasto(mtoFijoFacturacion);
        }
        //

        gastoTotal.add(gastoTotDT);

        // verificar si la alicuota del periodo fue pagada en el cierre y reflejar este pago en el aviso
        if (!(aliPag == null)) {
            if (!aliPag.getEdoAlicuota().equals("PEND")) {
                // Generar un objeto con cancelacion de la alicuota

                AvisoDeCobroDT alicuotaDT = new AvisoDeCobroDT();
                alicuotaDT.setCodInm(inm.getCodInmueble());
                alicuotaDT.setGrupo(1);

                alicuotaDT.setPropInm(inm.getNombPropInmueble());
                alicuotaDT.setTelInm(inm.getTelInmueble());
                alicuotaDT.setDirInm(inm.getDirInmueble());
                alicuotaDT.setEmailInm(inm.getEmailPropInmueble());
                alicuotaDT.setAlicuotaInm(inm.getAlicInmueble());
                alicuotaDT.setNumAviso(proxAvisoCobro);
                alicuotaDT.setTotCxC(cxcTotal);

                alicuotaDT.setTotalMes(totalMes);

                alicuotaDT.setTotGenCxC(totalGeneral);

                alicuotaDT.setFecGasto("");
                alicuotaDT.setConceptoGasto("");
                alicuotaDT.setRefGasto("");
                alicuotaDT.setProvGasto("Pago automático alícuota ");
                alicuotaDT.setMtoGasto(null);
                alicuotaDT.setAlicuotaGasto(mtoAliPag * -1);

                gastoTotal.add(alicuotaDT);
            }
        }

        // Crear un objeto para el título del grupo de GNC
        if ((listaGNCMes.size() > 0) | (porcMora > 0) | (notasDebPer.size() > 0) | (listaCEsp.size() > 0)) {

            AvisoDeCobroDT tituloDT = new AvisoDeCobroDT();
            tituloDT.setCodInm(inm.getCodInmueble());
            tituloDT.setGrupo(1);

            tituloDT.setPropInm(inm.getNombPropInmueble());
            tituloDT.setTelInm(inm.getTelInmueble());
            tituloDT.setDirInm(inm.getDirInmueble());
            tituloDT.setEmailInm(inm.getEmailPropInmueble());
            tituloDT.setAlicuotaInm(inm.getAlicInmueble());
            tituloDT.setNumAviso(proxAvisoCobro);
            tituloDT.setTotCxC(cxcTotal);

            tituloDT.setTotalMes(totalMes);

            tituloDT.setTotGenCxC(totalGeneral);

            tituloDT.setFecGasto("");
            tituloDT.setConceptoGasto("GASTOS INDIVIDUALES");
            tituloDT.setRefGasto("");
            tituloDT.setProvGasto("");
            tituloDT.setMtoGasto(null);
            tituloDT.setAlicuotaGasto(null);

            gastoTotal.add(tituloDT);
        }

        // agregar objetos con los GNC detallados:
        totalGNCInm = 0.0;

        for (Iterator<FacGastoNoComun> it5 = listaGNCMes.iterator();
                it5.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it5.next();

            AvisoDeCobroDT gastoNCDT = new AvisoDeCobroDT();
            gastoNCDT.setCodInm(inm.getCodInmueble());
            gastoNCDT.setGrupo(1);

            gastoNCDT.setPropInm(inm.getNombPropInmueble());
            gastoNCDT.setTelInm(inm.getTelInmueble());
            gastoNCDT.setDirInm(inm.getDirInmueble());
            gastoNCDT.setEmailInm(inm.getEmailPropInmueble());
            gastoNCDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoNCDT.setNumAviso(proxAvisoCobro);
            gastoNCDT.setTotCxC(cxcTotal);

            gastoNCDT.setTotalMes(totalMes);

            gastoNCDT.setTotGenCxC(totalGeneral);

            gastoNCDT.setFecGasto(CalendarAString(fgnc.getFecFacGNC()));
            gastoNCDT.setConceptoGasto(buscarNombConcepto(fgnc.getGastoNoComun().getIdConcepto()));
            gastoNCDT.setRefGasto(fgnc.getGastoNoComun().getNumDocGastoNoComun());
            gastoNCDT.setProvGasto("");
            gastoNCDT.setMtoGasto(fgnc.getSaldoFacGastoNoComun());
            gastoNCDT.setAlicuotaGasto(null);

            totalGNCInm = totalGNCInm + fgnc.getSaldoFacGastoNoComun();

            gastoTotal.add(gastoNCDT);

        }
        // Intereses de mora

        if (porcMora > 0) {

            if (totalMora > 0) {

                AvisoDeCobroDT moraDT = new AvisoDeCobroDT();
                moraDT.setCodInm(inm.getCodInmueble());
                moraDT.setGrupo(1);

                moraDT.setPropInm(inm.getNombPropInmueble());
                moraDT.setTelInm(inm.getTelInmueble());
                moraDT.setDirInm(inm.getDirInmueble());
                moraDT.setEmailInm(inm.getEmailPropInmueble());
                moraDT.setAlicuotaInm(inm.getAlicInmueble());
                moraDT.setNumAviso(proxAvisoCobro);
                moraDT.setTotCxC(cxcTotal);
                moraDT.setTotalMes(totalMes);
                moraDT.setTotGenCxC(totalGeneral);

                moraDT.setFecGasto(CalendarAString(periodoActual));
                moraDT.setConceptoGasto("Intereses de mora");
                moraDT.setRefGasto("");
                moraDT.setProvGasto("");
                moraDT.setMtoGasto(totalMora);
                moraDT.setAlicuotaGasto(null);

                totalGNCInm = totalGNCInm + totalMora;
                gastoTotal.add(moraDT);
            }
        }

        // Generar objetos con las ND del periodo
        for (Iterator<NotaCredDebCxC> it6 = notasDebPer.iterator();
                it6.hasNext();) {
            NotaCredDebCxC nd = (NotaCredDebCxC) it6.next();

            AvisoDeCobroDT NDDT = new AvisoDeCobroDT();
            NDDT.setCodInm(inm.getCodInmueble());
            NDDT.setGrupo(1);

            NDDT.setPropInm(inm.getNombPropInmueble());
            NDDT.setTelInm(inm.getTelInmueble());
            NDDT.setDirInm(inm.getDirInmueble());
            NDDT.setEmailInm(inm.getEmailPropInmueble());
            NDDT.setAlicuotaInm(inm.getAlicInmueble());
            NDDT.setNumAviso(proxAvisoCobro);
            NDDT.setTotCxC(cxcTotal);

            NDDT.setTotalMes(totalMes);

            NDDT.setTotGenCxC(totalGeneral);

            NDDT.setFecGasto(nd.getFecNotaCredDebCxC());
            NDDT.setConceptoGasto(buscarNombConcepto(nd.getIdConcepto()));
            NDDT.setRefGasto(nd.getNumNotaCredDebCxC());
            NDDT.setProvGasto("");
            NDDT.setMtoGasto(nd.getSaldoNotaCredDebCxC());
            NDDT.setAlicuotaGasto(null);

            totalGNCInm = totalGNCInm + nd.getSaldoNotaCredDebCxC();

            gastoTotal.add(NDDT);

        }

        // Generar objetos con las Cuotss especiales del periodo
        if (listaCEsp.size() > 0) {

            for (Iterator<FacCuotaEsp> it7 = listaCEsp.iterator();
                    it7.hasNext();) {
                FacCuotaEsp ce = (FacCuotaEsp) it7.next();

                AvisoDeCobroDT CEDT = new AvisoDeCobroDT();
                CEDT.setCodInm(inm.getCodInmueble());
                CEDT.setGrupo(1);

                CEDT.setPropInm(inm.getNombPropInmueble());
                CEDT.setTelInm(inm.getTelInmueble());
                CEDT.setDirInm(inm.getDirInmueble());
                CEDT.setEmailInm(inm.getEmailPropInmueble());
                CEDT.setAlicuotaInm(inm.getAlicInmueble());
                CEDT.setNumAviso(proxAvisoCobro);
                CEDT.setTotCxC(cxcTotal);

                CEDT.setTotalMes(totalMes);

                CEDT.setTotGenCxC(totalGeneral);

                CEDT.setFecGasto(CalendarAString(ce.getPeriodoActual()));
                CEDT.setConceptoGasto(ce.getDesFacCuotaEsp());
                CEDT.setRefGasto("S/N");
                CEDT.setProvGasto("Administracion");
                CEDT.setMtoGasto(ce.getSaldoFacCuotaEsp()); // Modificado por FRH antes mostraba el monto de la facCES 30102018
                CEDT.setAlicuotaGasto(null);

                totalGNCInm = totalGNCInm + ce.getSaldoFacCuotaEsp(); //ver comentario anterior

                gastoTotal.add(CEDT);
            }
        }

        // Generar un objeto con el subtotal GNC 
        if ((listaGNCMes.size() > 0) | (porcMora > 0) | (notasDebPer.size() > 0) | (listaCEsp.size() > 0)) {

            AvisoDeCobroDT totGNCInmDT = new AvisoDeCobroDT();
            totGNCInmDT.setCodInm(inm.getCodInmueble());
            totGNCInmDT.setGrupo(1);

            totGNCInmDT.setPropInm(inm.getNombPropInmueble());
            totGNCInmDT.setTelInm(inm.getTelInmueble());
            totGNCInmDT.setDirInm(inm.getDirInmueble());
            totGNCInmDT.setEmailInm(inm.getEmailPropInmueble());
            totGNCInmDT.setAlicuotaInm(inm.getAlicInmueble());
            totGNCInmDT.setNumAviso(proxAvisoCobro);
            totGNCInmDT.setTotCxC(cxcTotal);

            totGNCInmDT.setTotalMes(totalMes);

            totGNCInmDT.setTotGenCxC(totalGeneral);

            totGNCInmDT.setFecGasto("");
            totGNCInmDT.setConceptoGasto("");
            totGNCInmDT.setRefGasto("");
            totGNCInmDT.setProvGasto("TOTAL GASTOS INDIVIDUALES");
            totGNCInmDT.setMtoGasto(null);
            totGNCInmDT.setAlicuotaGasto(totalGNCInm);

            gastoTotal.add(totGNCInmDT);
        }
        if (totalAntInm > 0) {

            // Crear un objeto para el título del grupo de Documentos a favor despues del cierre
            AvisoDeCobroDT titulo2DT = new AvisoDeCobroDT();
            titulo2DT.setCodInm(inm.getCodInmueble());
            titulo2DT.setGrupo(1);

            titulo2DT.setPropInm(inm.getNombPropInmueble());
            titulo2DT.setTelInm(inm.getTelInmueble());
            titulo2DT.setDirInm(inm.getDirInmueble());
            titulo2DT.setEmailInm(inm.getEmailPropInmueble());
            titulo2DT.setAlicuotaInm(inm.getAlicInmueble());
            titulo2DT.setNumAviso(proxAvisoCobro);
            titulo2DT.setTotCxC(cxcTotal);

            titulo2DT.setTotalMes(totalMes);

            titulo2DT.setTotGenCxC(totalGeneral);

            titulo2DT.setFecGasto("");
            titulo2DT.setConceptoGasto("A FAVOR DESPUES DEL PERIODO");
            titulo2DT.setRefGasto("");
            titulo2DT.setProvGasto("");
            titulo2DT.setMtoGasto(null);
            titulo2DT.setAlicuotaGasto(null);

            gastoTotal.add(titulo2DT);

            // Generar objetos con las PAD Despues del periodo
            for (Iterator<PagoAdelantado> it8 = listaPADAfter.iterator();
                    it8.hasNext();) {
                PagoAdelantado pad = (PagoAdelantado) it8.next();

                AvisoDeCobroDT PADDT = new AvisoDeCobroDT();
                PADDT.setCodInm(inm.getCodInmueble());
                PADDT.setGrupo(1);

                PADDT.setPropInm(inm.getNombPropInmueble());
                PADDT.setTelInm(inm.getTelInmueble());
                PADDT.setDirInm(inm.getDirInmueble());
                PADDT.setEmailInm(inm.getEmailPropInmueble());
                PADDT.setAlicuotaInm(inm.getAlicInmueble());
                PADDT.setNumAviso(proxAvisoCobro);
                PADDT.setTotCxC(cxcTotal);

                PADDT.setTotalMes(totalMes);

                PADDT.setTotGenCxC(totalGeneral);

                PADDT.setFecGasto(pad.getFecPagoAdelantado());
                PADDT.setConceptoGasto(pad.getDescPagoAdelantado());
                PADDT.setRefGasto(pad.getRefPagoAdelantado());
                PADDT.setProvGasto("");
                PADDT.setMtoGasto(pad.getSaldoPagoAdelantado());
                PADDT.setAlicuotaGasto(null);

                gastoTotal.add(PADDT);

            }
            // Generar objetos con las NC Despues del periodo

            for (Iterator<NotaCredDebCxC> it9 = listaNCAfter.iterator();
                    it9.hasNext();) {
                NotaCredDebCxC nc = (NotaCredDebCxC) it9.next();

                AvisoDeCobroDT NCDT = new AvisoDeCobroDT();
                NCDT.setCodInm(inm.getCodInmueble());
                NCDT.setGrupo(1);

                NCDT.setPropInm(inm.getNombPropInmueble());
                NCDT.setTelInm(inm.getTelInmueble());
                NCDT.setDirInm(inm.getDirInmueble());
                NCDT.setEmailInm(inm.getEmailPropInmueble());
                NCDT.setAlicuotaInm(inm.getAlicInmueble());
                NCDT.setNumAviso(proxAvisoCobro);
                NCDT.setTotCxC(cxcTotal);

                NCDT.setTotalMes(totalMes);

                NCDT.setTotGenCxC(totalGeneral);

                NCDT.setFecGasto(nc.getFecNotaCredDebCxC());
                NCDT.setConceptoGasto("Nota de credito");
                NCDT.setRefGasto(nc.getNumNotaCredDebCxC());
                NCDT.setProvGasto("");
                NCDT.setMtoGasto(nc.getSaldoNotaCredDebCxC());
                NCDT.setAlicuotaGasto(null);

                gastoTotal.add(NCDT);

            }
            // Generar un objeto con el subtotal Doc. A favor despues del cierre

            AvisoDeCobroDT tituelo3DT = new AvisoDeCobroDT();
            tituelo3DT.setCodInm(inm.getCodInmueble());
            tituelo3DT.setGrupo(1);

            tituelo3DT.setPropInm(inm.getNombPropInmueble());
            tituelo3DT.setTelInm(inm.getTelInmueble());
            tituelo3DT.setDirInm(inm.getDirInmueble());
            tituelo3DT.setEmailInm(inm.getEmailPropInmueble());
            tituelo3DT.setAlicuotaInm(inm.getAlicInmueble());
            tituelo3DT.setNumAviso(proxAvisoCobro);
            tituelo3DT.setTotCxC(cxcTotal);

            tituelo3DT.setTotalMes(totalMes);

            tituelo3DT.setTotGenCxC(totalGeneral);

            tituelo3DT.setFecGasto("");
            tituelo3DT.setConceptoGasto("");
            tituelo3DT.setRefGasto("");
            tituelo3DT.setProvGasto("TOTAL A FAVOR DESP.PER.");
            tituelo3DT.setMtoGasto(null);
            tituelo3DT.setAlicuotaGasto(totalAntInm * -1);

            gastoTotal.add(tituelo3DT);
        }

        // Generar un objeto con el total del mes
        AvisoDeCobroDT totalMesDT = new AvisoDeCobroDT();
        totalMesDT.setCodInm(inm.getCodInmueble());
        totalMesDT.setGrupo(1);

        totalMesDT.setPropInm(inm.getNombPropInmueble());
        totalMesDT.setTelInm(inm.getTelInmueble());
        totalMesDT.setDirInm(inm.getDirInmueble());
        totalMesDT.setEmailInm(inm.getEmailPropInmueble());
        totalMesDT.setAlicuotaInm(inm.getAlicInmueble());
        totalMesDT.setNumAviso(proxAvisoCobro);
        totalMesDT.setTotCxC(cxcTotal);

        totalMesDT.setTotalMes(totalMes);

        totalMesDT.setTotGenCxC(totalGeneral);

        totalMesDT.setFecGasto("");
        totalMesDT.setConceptoGasto("");
        totalMesDT.setRefGasto("");
        totalMesDT.setProvGasto("TOTAL A PAGAR ESTE MES");
        totalMesDT.setMtoGasto(null);
        // Condicion para que sume el monto alicuota o el monto fijo segun tipo de facturación FRH 090518
        if ("gastos".equals(tipoFacturacion)) {
            totalMesDT.setAlicuotaGasto((totalGNCInm + mtoAlicuota) - (totalAntInm + mtoAliPag));
        } else {
            totalMesDT.setAlicuotaGasto((totalGNCInm + mtoFijoFacturacion) - (totalAntInm + mtoAliPag));
        }
        //

        gastoTotal.add(totalMesDT);

        mtoTotalDolares = totalMesDT.getAlicuotaGasto();
        // Crear un objeto con el total en dólares
        if (beanUsuario.getTasaExiste()) {

            AvisoDeCobroDT lineaEnBlancoDT = new AvisoDeCobroDT();
            lineaEnBlancoDT.setCodInm(inm.getCodInmueble());
            lineaEnBlancoDT.setGrupo(1);

            lineaEnBlancoDT.setPropInm(inm.getNombPropInmueble());
            lineaEnBlancoDT.setTelInm(inm.getTelInmueble());
            lineaEnBlancoDT.setDirInm(inm.getDirInmueble());
            lineaEnBlancoDT.setEmailInm(inm.getEmailPropInmueble());
            lineaEnBlancoDT.setAlicuotaInm(inm.getAlicInmueble());
            lineaEnBlancoDT.setNumAviso(proxAvisoCobro);
            lineaEnBlancoDT.setTotCxC(cxcTotal);

            lineaEnBlancoDT.setTotalMes(totalMes);
            lineaEnBlancoDT.setTotGenCxC(totalGeneral);

            lineaEnBlancoDT.setFecGasto("");
            lineaEnBlancoDT.setConceptoGasto("");
            lineaEnBlancoDT.setRefGasto("");
            lineaEnBlancoDT.setProvGasto("");

            lineaEnBlancoDT.setAlicuotaGasto(null);
            gastoTotal.add(lineaEnBlancoDT);

            AvisoDeCobroDT totalDolaresDT = new AvisoDeCobroDT();
            totalDolaresDT.setCodInm(inm.getCodInmueble());
            totalDolaresDT.setGrupo(1);

            totalDolaresDT.setPropInm(inm.getNombPropInmueble());
            totalDolaresDT.setTelInm(inm.getTelInmueble());
            totalDolaresDT.setDirInm(inm.getDirInmueble());
            totalDolaresDT.setEmailInm(inm.getEmailPropInmueble());
            totalDolaresDT.setAlicuotaInm(inm.getAlicInmueble());
            totalDolaresDT.setNumAviso(proxAvisoCobro);
            totalDolaresDT.setTotCxC(cxcTotal);

            totalDolaresDT.setTotalMes(totalMes);
            totalDolaresDT.setTotGenCxC(totalGeneral);

            totalDolaresDT.setFecGasto("");
            totalDolaresDT.setConceptoGasto("");
            totalDolaresDT.setRefGasto("");
            totalDolaresDT.setProvGasto("**TOTAL EN DOLARES**");

            totalDolaresDT.setAlicuotaGasto(mtoTotalDolares / valorTasa);
            gastoTotal.add(totalDolaresDT);

            AvisoDeCobroDT tasaDeCambioDT = new AvisoDeCobroDT();
            tasaDeCambioDT.setCodInm(inm.getCodInmueble());
            tasaDeCambioDT.setGrupo(1);

            tasaDeCambioDT.setPropInm(inm.getNombPropInmueble());
            tasaDeCambioDT.setTelInm(inm.getTelInmueble());
            tasaDeCambioDT.setDirInm(inm.getDirInmueble());
            tasaDeCambioDT.setEmailInm(inm.getEmailPropInmueble());
            tasaDeCambioDT.setAlicuotaInm(inm.getAlicInmueble());
            tasaDeCambioDT.setNumAviso(proxAvisoCobro);
            tasaDeCambioDT.setTotCxC(cxcTotal);

            tasaDeCambioDT.setTotalMes(totalMes);
            tasaDeCambioDT.setTotGenCxC(totalGeneral);

            tasaDeCambioDT.setFecGasto("");
            tasaDeCambioDT.setConceptoGasto("");
            tasaDeCambioDT.setRefGasto("");
            tasaDeCambioDT.setProvGasto("**TASA DE CAMBIO HOY**");

            valorTasaDouble = valorTasa.doubleValue();
            tasaDeCambioDT.setAlicuotaGasto(valorTasaDouble);
            gastoTotal.add(tasaDeCambioDT);

        }
        proxAvisoCobro = proxAvisoCobro + 1;

        actualizarParametros(idCond);

        gastoTotalOrd = ordenarListaGastos(gastoTotal);
        progreso += 20;
        return gastoTotalOrd;
    }

    public Alicuota obtenerAlicuotaPeriodo(Integer idInm, Calendar fecDesdeCal) {

        Collection lista = new ArrayList();
        String nombQuery;
        Alicuota aliPagada = null;

        nombQuery = "buscarAlicuotasPorIdInmPeriodo";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecDesdeCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (!lista.isEmpty()) {

            for (Iterator<Alicuota> itA = lista.iterator();
                    itA.hasNext();) {
                Alicuota ali = (Alicuota) itA.next();
                aliPagada = ali;

            }
        }
        return aliPagada;
    }

    public String CalendarAString(Calendar fecha) {

        Calendar fechaFac = fecha;
        String dia;
        String mes;
        int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
        int mesFac = fechaFac.get(Calendar.MONTH) + 1;
        int añoFac = fechaFac.get(Calendar.YEAR);

        if (diaFac < 10) {
            dia = "0" + diaFac;
        } else {
            dia = String.valueOf(diaFac);
        }

        if (mesFac < 10) {
            mes = "0" + mesFac;
        } else {
            mes = String.valueOf(mesFac);
        }

        String stringFecha = dia + "-" + mes + "-" + añoFac;
        return stringFecha;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        System.out.println("la fecha desde es: " + cal.toString());
        return cal;
    }

    public Collection<AjustesCond> getAjustes(Calendar periodo) {

        try {
            beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        } catch (NullPointerException e) {
            beanUsuario = beanUsuarioEJB;
        }

        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarAjustesCondPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            ajustes = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return ajustes;
        }

        if (ajustes.isEmpty()) {
            return ajustes;
        }

        for (Iterator<AjustesCond> it = ajustes.iterator();
                it.hasNext();) {
            AjustesCond mov = (AjustesCond) it.next();
            if ("Credito".equals(mov.getTipoAjustesCond())) {
                mtoAjustes = mtoAjustes + mov.getMtoAjustesCond();
            } else {
                mtoAjustes = mtoAjustes - mov.getMtoAjustesCond();
            }

        }
        return ajustes;
    }

    public Double buscarGCE(Integer idCond) {

        Collection<GastoComunExt> listaGCExt = new ArrayList<GastoComunExt>();
        String nombQuery = "buscarGastosComunesExtPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            listaGCExt = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Double mtoGCE = 0.00;

        if (listaGCExt.isEmpty()) {
            return mtoGCE;
        }

        for (Iterator<GastoComunExt> it = listaGCExt.iterator();
                it.hasNext();) {
            GastoComunExt mov = (GastoComunExt) it.next();
            mtoGCE = mtoGCE + mov.getMtoGastoComunExt();
        }
        return mtoGCE;
    }

    public Collection buscarDataInmuebles(Integer idCond) throws ParseException {

        Collection<AvisoInmDT> dataInmAvisos = new ArrayList<AvisoInmDT>();

        for (Iterator<Inmueble> it = listaInm.iterator();
                it.hasNext();) {
            Inmueble inmObj = (Inmueble) it.next();
            AvisoInmDT inmDT = new AvisoInmDT();

            inmDT.setCodInm(inmObj.getCodInmueble());
            inmDT.setPropInm(inmObj.getNombPropInmueble());
            inmDT.setTelInm(inmObj.getTelInmueble());
            inmDT.setEmailInm(inmObj.getEmailPropInmueble());
            buscarMontoCxCInm(inmObj.getId(), idCond, inmObj.getAlicInmueble());
            inmDT.setTotCxC(cxcTotal);
            inmDT.setTotGenCxC(totalGeneral);
            inmDT.setMtoAlicuota(mtoAlicuota);
            inmDT.setNumAviso(111);

            dataInmAvisos.add(inmDT);
        }
        return dataInmAvisos;
    }

    public void generarDatosYEnviarEmailATodos(Integer idCond, Collection listaInm, String periodo) throws FileNotFoundException, JRException, MessagingException, ParseException {

        Double totalGNCInm = 0.0;
        listaGCO = buscarGastoComunOrdPorIdCond(idCond);
        Collection<FacCuotaEsp> listaCEsp = new ArrayList<FacCuotaEsp>();

        Alicuota aliPag; // Alicuota pagada
        String enBlanco = new String();

        // Las siguientes son para almacenar las nc y pad despues registradas despues del periodo
        Collection<PagoAdelantado> listaPADAfter = new ArrayList<PagoAdelantado>();
        Collection<NotaCredDebCxC> listaNCAfter = new ArrayList<NotaCredDebCxC>();
        //
        mtoReserva = buscarMontoReserva(idCond);
        mtoPrestaciones = buscarMontoPrestaciones(idCond); 

        ajustes.clear();
        mtoAjustes = 0.0;
        Double alicuota = 0.0;
        ajustes = getAjustes(periodoActual);

        buscarParametros(idCond);
        int incremento = 100 / listaInm.size();

        try {
            beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            tasaExiste = beanUsuario.getTasaExiste();
            valorTasa = Float.parseFloat(beanUsuario.getTasa());
        } catch (NullPointerException e) {
            beanUsuario = beanUsuarioEJB;
        }

        for (Iterator<Inmueble> it1 = listaInm.iterator();
                it1.hasNext();) {
            Inmueble inm = (Inmueble) it1.next();
            progreso += incremento;

            //
            listaCEsp = obtenerFacCuotaEspInm(inm.getId());
            totalMora = getNotasDebitoInmMora(inm.getId());

            aliPag = null;
            mtoAliPag = 0.0;
            aliPag = obtenerAlicuotaPeriodo(inm.getId(), periodoActual);
            if (!(aliPag == null)) {
                mtoAliPag = (aliPag.getMtoAlicuota() - aliPag.getSaldoAlicuota());
            }
            buscarMontoCxCInm(inm.getId(), idCond, inm.getAlicInmueble());

            //Para los saldos a favor con doc. periodo siguiente para deducirlos del recibo 06032018
            totalAntInm = 0.0;
            listaPADAfter = getPagosAdelantadosInmAfter(inm.getId());
            listaNCAfter = getNotasDeCreditoInmAfter(inm.getId());

            //
            for (Iterator<GastoComunOrd> it2 = listaGCO.iterator();
                    it2.hasNext();) {
                GastoComunOrd gco = (GastoComunOrd) it2.next();

                AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();

                //--------------------------------------------------------------
                //Se da formato a la fecha del mov para llevarla al  data table
                //--------------------------------------------------------------
                Calendar fechaFac = gco.getFecMovGCOrd();
                String dia;
                String mes;
                int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
                int mesFac = fechaFac.get(Calendar.MONTH) + 1;
                int añoFac = fechaFac.get(Calendar.YEAR);

                if (diaFac < 10) {
                    dia = "0" + diaFac;
                } else {
                    dia = String.valueOf(diaFac);
                }

                if (mesFac < 10) {
                    mes = "0" + mesFac;
                } else {
                    mes = String.valueOf(mesFac);
                }

                String stringFechagco = dia + "-" + mes + "-" + añoFac;
                //--------------------------------------------------------------

                gastoDT.setCodInm(inm.getCodInmueble());
                gastoDT.setGrupo(1);
                gastoDT.setPropInm(inm.getNombPropInmueble());
                gastoDT.setDirInm(inm.getDirInmueble());
                gastoDT.setTelInm(inm.getTelInmueble());
                gastoDT.setEmailInm(inm.getEmailPropInmueble());
                gastoDT.setAlicuotaInm(inm.getAlicInmueble());
                gastoDT.setNumAviso(proxAvisoCobro);

                // Buscar y asignar el codigo de acceso a Consulta Express
                Integer idAcceso = null;
                if (!(inm.getCodigoAcceso() == null)) {

                    idAcceso = inm.getCodigoAcceso().getId();
                } else {
                    idAcceso = 0;
                }

                gastoDT.setCodAcc(idAcceso.toString());

                gastoDT.setTotCxC(cxcTotal);
                gastoDT.setTotalMes(totalMes - totalAntInm);
                gastoDT.setTotGenCxC(totalGeneral - totalAntInm);

                gastoDT.setFecGasto(gco.getFecGastoComunOrd());
                Integer idConcepto = gco.getIdConcepto();
                String concepto = buscarNombConcepto(idConcepto);
                gastoDT.setConceptoGasto(concepto);
                gastoDT.setRefGasto(gco.getNumDocGastoComunOrd());
                Integer idProv = gco.getIdProveedor();
                String nombProv = buscarProveedor(idProv);
                gastoDT.setProvGasto(nombProv);
                gastoDT.setMtoGasto(gco.getMtoGastoComunOrd());

                if ("gastos".equals(tipoFacturacion)) {
                    gastoDT.setAlicuotaGasto((gco.getMtoGastoComunOrd() * inm.getAlicInmueble()) / 100);
                    gastoDT.setMtoAlicuota(mtoAlicuota);

                } else {
                    gastoDT.setAlicuotaGasto(0.0);
                    gastoDT.setMtoAlicuota(mtoFijoFacturacion);
                }

                gastoTotal.add(gastoDT);

                // crear un objeto para las observaciones 
                if (!gco.getObsGastoComunOrd().equals(enBlanco)) {
                    AvisoDeCobroDT gastoDT2 = new AvisoDeCobroDT();
                    gastoDT2.setCodInm(inm.getCodInmueble());
                    gastoDT2.setGrupo(1);
                    gastoDT2.setPropInm(inm.getNombPropInmueble());
                    gastoDT2.setTelInm(inm.getTelInmueble());
                    gastoDT2.setDirInm(inm.getDirInmueble());
                    gastoDT2.setEmailInm(inm.getEmailPropInmueble());
                    gastoDT2.setAlicuotaInm(inm.getAlicInmueble());
                    gastoDT2.setNumAviso(proxAvisoCobro);

                    // Buscar y asignar el codigo de acceso a Consulta Express
                    Integer idAcceso2 = null;
                    if (!(inm.getCodigoAcceso() == null)) {

                        idAcceso2 = inm.getCodigoAcceso().getId();
                    } else {
                        idAcceso2 = 0;
                    }

                    gastoDT2.setCodAcc(idAcceso2.toString());

                    gastoDT2.setTotCxC(cxcTotal);
                    gastoDT2.setTotalMes(totalMes - totalAntInm);
                    gastoDT2.setTotGenCxC(totalGeneral - totalAntInm);

                    gastoDT2.setFecGasto(" ");
                    gastoDT2.setConceptoGasto(" ");
                    gastoDT2.setRefGasto(" ");
                    gastoDT2.setProvGasto(gco.getObsGastoComunOrd());
                    gastoDT2.setMtoGasto(null);

                    gastoDT2.setAlicuotaGasto(null);
                    gastoDT2.setMtoAlicuota(null);
                    gastoTotal.add(gastoDT2);

                }

            }

            // Agregar un objeto con el gasto por el fondo de reserva //
            String stringFecha = CalendarAString(periodoActual);

            if (mtoReserva > 0) {

                AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();
                gastoDT.setCodInm(inm.getCodInmueble());
                gastoDT.setGrupo(1);

                gastoDT.setPropInm(inm.getNombPropInmueble());
                gastoDT.setTelInm(inm.getTelInmueble());
                gastoDT.setEmailInm(inm.getEmailPropInmueble());
                gastoDT.setAlicuotaInm(inm.getAlicInmueble());
                gastoDT.setNumAviso(proxAvisoCobro);
                gastoDT.setTotCxC(cxcTotal);
                gastoDT.setTotalMes(totalMes);

                gastoDT.setMtoAlicuota(mtoAlicuota);
                gastoDT.setTotGenCxC(totalGeneral);

                gastoDT.setFecGasto(stringFecha);
                gastoDT.setConceptoGasto("Fondo de Reserva");
                gastoDT.setRefGasto("N/A");
                gastoDT.setProvGasto("N/A");
                gastoDT.setMtoGasto(mtoReserva);
                gastoDT.setAlicuotaGasto((mtoReserva * inm.getAlicInmueble()) / 100);

                gastoTotal.add(gastoDT);
            }
            // Agregar un objeto con el gasto por el fondo de prestaciones //

            if (mtoPrestaciones > 0) {
                AvisoDeCobroDT gastoPresDT = new AvisoDeCobroDT();
                gastoPresDT.setCodInm(inm.getCodInmueble());
                gastoPresDT.setGrupo(1);

                gastoPresDT.setPropInm(inm.getNombPropInmueble());
                gastoPresDT.setTelInm(inm.getTelInmueble());
                gastoPresDT.setEmailInm(inm.getEmailPropInmueble());
                gastoPresDT.setAlicuotaInm(inm.getAlicInmueble());
                gastoPresDT.setNumAviso(proxAvisoCobro);
                gastoPresDT.setTotCxC(cxcTotal);
                gastoPresDT.setTotalMes(totalMes);

                gastoPresDT.setMtoAlicuota(mtoAlicuota);
                gastoPresDT.setTotGenCxC(totalGeneral);

                gastoPresDT.setFecGasto(stringFecha);
                gastoPresDT.setConceptoGasto("Prestaciones Sociales");
                gastoPresDT.setRefGasto("N/A");

                gastoPresDT.setProvGasto("N/A ");
                gastoPresDT.setMtoGasto(mtoPrestaciones);
                gastoPresDT.setAlicuotaGasto((mtoPrestaciones * inm.getAlicInmueble()) / 100);

                gastoTotal.add(gastoPresDT);
            }
            // Agregar objetos con los gastos por Ajustes //

            for (Iterator<AjustesCond> it2 = ajustes.iterator();
                    it2.hasNext();) {
                AjustesCond ajuste = (AjustesCond) it2.next();

                AvisoDeCobroDT gastoAjusteDT = new AvisoDeCobroDT();
                gastoAjusteDT.setCodInm(inm.getCodInmueble());
                gastoAjusteDT.setGrupo(1);

                gastoAjusteDT.setPropInm(inm.getNombPropInmueble());
                gastoAjusteDT.setTelInm(inm.getTelInmueble());
                gastoAjusteDT.setDirInm(inm.getDirInmueble());
                gastoAjusteDT.setEmailInm(inm.getEmailPropInmueble());
                gastoAjusteDT.setAlicuotaInm(inm.getAlicInmueble());
                gastoAjusteDT.setNumAviso(proxAvisoCobro);
                gastoAjusteDT.setTotCxC(cxcTotal);

                gastoAjusteDT.setTotalMes(totalMes);

                gastoAjusteDT.setTotGenCxC(totalGeneral);

                gastoAjusteDT.setFecGasto(stringFecha);
                gastoAjusteDT.setConceptoGasto(buscarNombConcepto(ajuste.getIdConcepto()));
                gastoAjusteDT.setRefGasto("N/A");

                gastoAjusteDT.setProvGasto(ajuste.getDescAjustesCond());
                gastoAjusteDT.setMtoGasto(ajuste.getMtoAjustesCond());
                gastoAjusteDT.setMtoAlicuota(mtoAlicuota);

                // Las siguientes condiciones son para que coloque el monto del ajuste y su alicuota con signo
                if ("Debito".equals(ajuste.tipoAjustesCond)) {
                    gastoAjusteDT.setMtoGasto(ajuste.getMtoAjustesCond() * -1);
                }

                alicuota = (ajuste.getMtoAjustesCond() * inm.getAlicInmueble()) / 100;

                // Este if es para que muestre el porcentaje de ajuste No lo mostraba 01Nov2016 FRojas
                gastoAjusteDT.setAlicuotaGasto(0.0);
                if ("gastos".equals(tipoFacturacion)) {
                    if ("Debito".equals(ajuste.tipoAjustesCond)) {
                        gastoAjusteDT.setAlicuotaGasto(alicuota * -1);
                    } else {
                        gastoAjusteDT.setAlicuotaGasto(alicuota);
                    }
                }

                gastoTotal.add(gastoAjusteDT);
            }

            // Agregar un objeto con el total gastos 
            AvisoDeCobroDT gastoTotDT = new AvisoDeCobroDT();
            gastoTotDT.setCodInm(inm.getCodInmueble());
            gastoTotDT.setGrupo(1);

            gastoTotDT.setPropInm(inm.getNombPropInmueble());
            gastoTotDT.setTelInm(inm.getTelInmueble());
            gastoTotDT.setDirInm(inm.getDirInmueble());
            gastoTotDT.setEmailInm(inm.getEmailPropInmueble());
            gastoTotDT.setAlicuotaInm(inm.getAlicInmueble());
            gastoTotDT.setNumAviso(proxAvisoCobro);
            gastoTotDT.setTotCxC(cxcTotal);

            gastoTotDT.setTotalMes(totalMes);

            gastoTotDT.setTotGenCxC(totalGeneral);

            gastoTotDT.setFecGasto("");
            gastoTotDT.setConceptoGasto("");
            gastoTotDT.setRefGasto("");
            gastoTotDT.setProvGasto("TOTAL GASTOS DEL MES");
            gastoTotDT.setMtoGasto(totalParcial);

            // condicion oara que imprima el monto fijo en caso de este tipo de facturación hoy 090518 FRH
            if ("gastos".equals(tipoFacturacion)) {
                gastoTotDT.setAlicuotaGasto((totalParcial * inm.getAlicInmueble()) / 100);
            } else {
                gastoTotDT.setAlicuotaGasto(mtoFijoFacturacion);
            }
            //
            gastoTotal.add(gastoTotDT);

            // verificar si la alicuota del periodo fue pagada en el cierre y reflejar este pago en el aviso
            if (!(aliPag == null)) {
                if (!aliPag.getEdoAlicuota().equals("PEND")) {
                    // Generar un objeto con cancelacion de la alicuota

                    AvisoDeCobroDT alicuotaDT = new AvisoDeCobroDT();
                    alicuotaDT.setCodInm(inm.getCodInmueble());
                    alicuotaDT.setGrupo(1);

                    alicuotaDT.setPropInm(inm.getNombPropInmueble());
                    alicuotaDT.setTelInm(inm.getTelInmueble());
                    alicuotaDT.setDirInm(inm.getDirInmueble());
                    alicuotaDT.setEmailInm(inm.getEmailPropInmueble());
                    alicuotaDT.setAlicuotaInm(inm.getAlicInmueble());
                    alicuotaDT.setNumAviso(proxAvisoCobro);
                    alicuotaDT.setTotCxC(cxcTotal);

                    alicuotaDT.setTotalMes(totalMes);

                    alicuotaDT.setTotGenCxC(totalGeneral);

                    alicuotaDT.setFecGasto("");
                    alicuotaDT.setConceptoGasto("");
                    alicuotaDT.setRefGasto("");
                    alicuotaDT.setProvGasto("Pago automático alícuota ");
                    alicuotaDT.setMtoGasto(null);
                    alicuotaDT.setAlicuotaGasto(mtoAliPag * -1);

                    gastoTotal.add(alicuotaDT);
                }
            }

            // Crear un objeto para el título del grupo de GNC
            if ((listaGNCMes.size() > 0) | (porcMora > 0) | (notasDebPer.size() > 0) | (listaCEsp.size() > 0)) {

                AvisoDeCobroDT tituloDT = new AvisoDeCobroDT();
                tituloDT.setCodInm(inm.getCodInmueble());
                tituloDT.setGrupo(1);

                tituloDT.setPropInm(inm.getNombPropInmueble());
                tituloDT.setTelInm(inm.getTelInmueble());
                tituloDT.setDirInm(inm.getDirInmueble());
                tituloDT.setEmailInm(inm.getEmailPropInmueble());
                tituloDT.setAlicuotaInm(inm.getAlicInmueble());
                tituloDT.setNumAviso(proxAvisoCobro);
                tituloDT.setTotCxC(cxcTotal);

                tituloDT.setTotalMes(totalMes);

                tituloDT.setTotGenCxC(totalGeneral);

                tituloDT.setFecGasto("");
                tituloDT.setConceptoGasto("GASTOS INDIVIDUALES");
                tituloDT.setRefGasto("");
                tituloDT.setProvGasto("");
                tituloDT.setMtoGasto(null);
                tituloDT.setAlicuotaGasto(null);

                gastoTotal.add(tituloDT);
            }

            // agregar objetos con los GNC detallados:
            totalGNCInm = 0.0;

            for (Iterator<FacGastoNoComun> it5 = listaGNCMes.iterator();
                    it5.hasNext();) {
                FacGastoNoComun fgnc = (FacGastoNoComun) it5.next();

                AvisoDeCobroDT gastoNCDT = new AvisoDeCobroDT();
                gastoNCDT.setCodInm(inm.getCodInmueble());
                gastoNCDT.setGrupo(1);

                gastoNCDT.setPropInm(inm.getNombPropInmueble());
                gastoNCDT.setTelInm(inm.getTelInmueble());
                gastoNCDT.setDirInm(inm.getDirInmueble());
                gastoNCDT.setEmailInm(inm.getEmailPropInmueble());
                gastoNCDT.setAlicuotaInm(inm.getAlicInmueble());
                gastoNCDT.setNumAviso(proxAvisoCobro);
                gastoNCDT.setTotCxC(cxcTotal);

                gastoNCDT.setTotalMes(totalMes);

                gastoNCDT.setTotGenCxC(totalGeneral);

                gastoNCDT.setFecGasto(CalendarAString(fgnc.getFecFacGNC()));
                gastoNCDT.setConceptoGasto(buscarNombConcepto(fgnc.getGastoNoComun().getIdConcepto()));
                gastoNCDT.setRefGasto(fgnc.getGastoNoComun().getNumDocGastoNoComun());
                gastoNCDT.setProvGasto("");
                gastoNCDT.setMtoGasto(fgnc.getSaldoFacGastoNoComun());
                gastoNCDT.setAlicuotaGasto(null);

                totalGNCInm = totalGNCInm + fgnc.getSaldoFacGastoNoComun();

                gastoTotal.add(gastoNCDT);

            }
            // Intereses de Mora 

            if (porcMora > 0) {

                if (totalMora > 0) {

                    AvisoDeCobroDT moraDT = new AvisoDeCobroDT();
                    moraDT.setCodInm(inm.getCodInmueble());
                    moraDT.setGrupo(1);

                    moraDT.setPropInm(inm.getNombPropInmueble());
                    moraDT.setTelInm(inm.getTelInmueble());
                    moraDT.setDirInm(inm.getDirInmueble());
                    moraDT.setEmailInm(inm.getEmailPropInmueble());
                    moraDT.setAlicuotaInm(inm.getAlicInmueble());
                    moraDT.setNumAviso(proxAvisoCobro);
                    moraDT.setTotCxC(cxcTotal);
                    moraDT.setTotalMes(totalMes);
                    moraDT.setTotGenCxC(totalGeneral);

                    moraDT.setFecGasto(CalendarAString(periodoActual));
                    moraDT.setConceptoGasto("Intereses de mora");
                    moraDT.setRefGasto("");
                    moraDT.setProvGasto("");
                    moraDT.setMtoGasto(totalMora);
                    moraDT.setAlicuotaGasto(null);

                    totalGNCInm = totalGNCInm + totalMora;
                    gastoTotal.add(moraDT);
                }
            }

            // Generar objetos con las ND del periodo
            for (Iterator<NotaCredDebCxC> it6 = notasDebPer.iterator();
                    it6.hasNext();) {
                NotaCredDebCxC nd = (NotaCredDebCxC) it6.next();

                AvisoDeCobroDT NDDT = new AvisoDeCobroDT();
                NDDT.setCodInm(inm.getCodInmueble());
                NDDT.setGrupo(1);

                NDDT.setPropInm(inm.getNombPropInmueble());
                NDDT.setTelInm(inm.getTelInmueble());
                NDDT.setDirInm(inm.getDirInmueble());
                NDDT.setEmailInm(inm.getEmailPropInmueble());
                NDDT.setAlicuotaInm(inm.getAlicInmueble());
                NDDT.setNumAviso(proxAvisoCobro);
                NDDT.setTotCxC(cxcTotal);

                NDDT.setTotalMes(totalMes);

                NDDT.setTotGenCxC(totalGeneral);

                NDDT.setFecGasto(nd.getFecNotaCredDebCxC());
                NDDT.setConceptoGasto(buscarNombConcepto(nd.getIdConcepto()));
                NDDT.setRefGasto(nd.getNumNotaCredDebCxC());
                NDDT.setProvGasto("");
                NDDT.setMtoGasto(nd.getSaldoNotaCredDebCxC());
                NDDT.setAlicuotaGasto(null);

                totalGNCInm = totalGNCInm + nd.getSaldoNotaCredDebCxC();

                gastoTotal.add(NDDT);

            }

            // Generar objetos con las Cuotss especiales del periodo
            if (listaCEsp.size() > 0) {

                for (Iterator<FacCuotaEsp> it7 = listaCEsp.iterator();
                        it7.hasNext();) {
                    FacCuotaEsp ce = (FacCuotaEsp) it7.next();

                    AvisoDeCobroDT CEDT = new AvisoDeCobroDT();
                    CEDT.setCodInm(inm.getCodInmueble());
                    CEDT.setGrupo(1);

                    CEDT.setPropInm(inm.getNombPropInmueble());
                    CEDT.setTelInm(inm.getTelInmueble());
                    CEDT.setDirInm(inm.getDirInmueble());
                    CEDT.setEmailInm(inm.getEmailPropInmueble());
                    CEDT.setAlicuotaInm(inm.getAlicInmueble());
                    CEDT.setNumAviso(proxAvisoCobro);
                    CEDT.setTotCxC(cxcTotal);

                    CEDT.setTotalMes(totalMes);

                    CEDT.setTotGenCxC(totalGeneral);

                    CEDT.setFecGasto(CalendarAString(ce.getPeriodoActual()));
                    CEDT.setConceptoGasto(ce.getDesFacCuotaEsp());
                    CEDT.setRefGasto("S/N");
                    CEDT.setProvGasto("Administracion");
                    CEDT.setMtoGasto(ce.getSaldoFacCuotaEsp());  // MOdificado por FRH antes mostraba el monto de la facCes 30102018
                    CEDT.setAlicuotaGasto(null);

                    totalGNCInm = totalGNCInm + ce.getSaldoFacCuotaEsp(); // ver comentario anterior

                    gastoTotal.add(CEDT);

                }
            }

            // Generar un objeto con el subtotal GNC 
            if ((listaGNCMes.size() > 0) | (porcMora > 0) | (notasDebPer.size() > 0) | (listaCEsp.size() > 0)) {

                AvisoDeCobroDT totGNCInmDT = new AvisoDeCobroDT();
                totGNCInmDT.setCodInm(inm.getCodInmueble());
                totGNCInmDT.setGrupo(1);

                totGNCInmDT.setPropInm(inm.getNombPropInmueble());
                totGNCInmDT.setTelInm(inm.getTelInmueble());
                totGNCInmDT.setDirInm(inm.getDirInmueble());
                totGNCInmDT.setEmailInm(inm.getEmailPropInmueble());
                totGNCInmDT.setAlicuotaInm(inm.getAlicInmueble());
                totGNCInmDT.setNumAviso(proxAvisoCobro);
                totGNCInmDT.setTotCxC(cxcTotal);

                totGNCInmDT.setTotalMes(totalMes);

                totGNCInmDT.setTotGenCxC(totalGeneral);

                totGNCInmDT.setFecGasto("");
                totGNCInmDT.setConceptoGasto("");
                totGNCInmDT.setRefGasto("");
                totGNCInmDT.setProvGasto("TOTAL GASTOS INDIVIDUALES");
                totGNCInmDT.setMtoGasto(null);
                totGNCInmDT.setAlicuotaGasto(totalGNCInm);

                gastoTotal.add(totGNCInmDT);
            }
            if (totalAntInm > 0) {

                // Crear un objeto para el título del grupo de Documentos a favor despues del cierre
                AvisoDeCobroDT titulo2DT = new AvisoDeCobroDT();
                titulo2DT.setCodInm(inm.getCodInmueble());
                titulo2DT.setGrupo(1);

                titulo2DT.setPropInm(inm.getNombPropInmueble());
                titulo2DT.setTelInm(inm.getTelInmueble());
                titulo2DT.setDirInm(inm.getDirInmueble());
                titulo2DT.setEmailInm(inm.getEmailPropInmueble());
                titulo2DT.setAlicuotaInm(inm.getAlicInmueble());
                titulo2DT.setNumAviso(proxAvisoCobro);
                titulo2DT.setTotCxC(cxcTotal);

                titulo2DT.setTotalMes(totalMes);

                titulo2DT.setTotGenCxC(totalGeneral);

                titulo2DT.setFecGasto("");
                titulo2DT.setConceptoGasto("A FAVOR DESPUES DEL PERIODO");
                titulo2DT.setRefGasto("");
                titulo2DT.setProvGasto("");
                titulo2DT.setMtoGasto(null);
                titulo2DT.setAlicuotaGasto(null);

                gastoTotal.add(titulo2DT);

                // Generar objetos con las PAD Despues del periodo
                for (Iterator<PagoAdelantado> it8 = listaPADAfter.iterator();
                        it8.hasNext();) {
                    PagoAdelantado pad = (PagoAdelantado) it8.next();

                    AvisoDeCobroDT PADDT = new AvisoDeCobroDT();
                    PADDT.setCodInm(inm.getCodInmueble());
                    PADDT.setGrupo(1);

                    PADDT.setPropInm(inm.getNombPropInmueble());
                    PADDT.setTelInm(inm.getTelInmueble());
                    PADDT.setDirInm(inm.getDirInmueble());
                    PADDT.setEmailInm(inm.getEmailPropInmueble());
                    PADDT.setAlicuotaInm(inm.getAlicInmueble());
                    PADDT.setNumAviso(proxAvisoCobro);
                    PADDT.setTotCxC(cxcTotal);

                    PADDT.setTotalMes(totalMes);

                    PADDT.setTotGenCxC(totalGeneral);

                    PADDT.setFecGasto(pad.getFecPagoAdelantado());
                    PADDT.setConceptoGasto(pad.getDescPagoAdelantado());
                    PADDT.setRefGasto(pad.getRefPagoAdelantado());
                    PADDT.setProvGasto("");
                    PADDT.setMtoGasto(pad.getSaldoPagoAdelantado());
                    PADDT.setAlicuotaGasto(null);

                    gastoTotal.add(PADDT);

                }
                // Generar objetos con las NC Despues del periodo

                for (Iterator<NotaCredDebCxC> it9 = listaNCAfter.iterator();
                        it9.hasNext();) {
                    NotaCredDebCxC nc = (NotaCredDebCxC) it9.next();

                    AvisoDeCobroDT NCDT = new AvisoDeCobroDT();
                    NCDT.setCodInm(inm.getCodInmueble());
                    NCDT.setGrupo(1);

                    NCDT.setPropInm(inm.getNombPropInmueble());
                    NCDT.setTelInm(inm.getTelInmueble());
                    NCDT.setDirInm(inm.getDirInmueble());
                    NCDT.setEmailInm(inm.getEmailPropInmueble());
                    NCDT.setAlicuotaInm(inm.getAlicInmueble());
                    NCDT.setNumAviso(proxAvisoCobro);
                    NCDT.setTotCxC(cxcTotal);

                    NCDT.setTotalMes(totalMes);

                    NCDT.setTotGenCxC(totalGeneral);

                    NCDT.setFecGasto(nc.getFecNotaCredDebCxC());
                    NCDT.setConceptoGasto("Nota de credito");
                    NCDT.setRefGasto(nc.getNumNotaCredDebCxC());
                    NCDT.setProvGasto("");
                    NCDT.setMtoGasto(nc.getSaldoNotaCredDebCxC());
                    NCDT.setAlicuotaGasto(null);

                    gastoTotal.add(NCDT);

                }
                // Generar un objeto con el subtotal Doc. A favor despues del cierre

                AvisoDeCobroDT tituelo3DT = new AvisoDeCobroDT();
                tituelo3DT.setCodInm(inm.getCodInmueble());
                tituelo3DT.setGrupo(1);

                tituelo3DT.setPropInm(inm.getNombPropInmueble());
                tituelo3DT.setTelInm(inm.getTelInmueble());
                tituelo3DT.setDirInm(inm.getDirInmueble());
                tituelo3DT.setEmailInm(inm.getEmailPropInmueble());
                tituelo3DT.setAlicuotaInm(inm.getAlicInmueble());
                tituelo3DT.setNumAviso(proxAvisoCobro);
                tituelo3DT.setTotCxC(cxcTotal);

                tituelo3DT.setTotalMes(totalMes);

                tituelo3DT.setTotGenCxC(totalGeneral);

                tituelo3DT.setFecGasto("");
                tituelo3DT.setConceptoGasto("");
                tituelo3DT.setRefGasto("");
                tituelo3DT.setProvGasto("TOTAL A FAVOR DESP.PER.");
                tituelo3DT.setMtoGasto(null);
                tituelo3DT.setAlicuotaGasto(totalAntInm * -1);

                gastoTotal.add(tituelo3DT);
            }

            // Generar un objeto con el total del mes
            AvisoDeCobroDT totalMesDT = new AvisoDeCobroDT();
            totalMesDT.setCodInm(inm.getCodInmueble());
            totalMesDT.setGrupo(1);

            totalMesDT.setPropInm(inm.getNombPropInmueble());
            totalMesDT.setTelInm(inm.getTelInmueble());
            totalMesDT.setDirInm(inm.getDirInmueble());
            totalMesDT.setEmailInm(inm.getEmailPropInmueble());
            totalMesDT.setAlicuotaInm(inm.getAlicInmueble());
            totalMesDT.setNumAviso(proxAvisoCobro);
            totalMesDT.setTotCxC(cxcTotal);

            totalMesDT.setTotalMes(totalMes);

            totalMesDT.setTotGenCxC(totalGeneral);

            totalMesDT.setFecGasto("");
            totalMesDT.setConceptoGasto("");
            totalMesDT.setRefGasto("");
            totalMesDT.setProvGasto("TOTAL A PAGAR ESTE MES");
            totalMesDT.setMtoGasto(null);

            // Condicion para que sume el monto alicuota o el monto fijo segun tipo de facturación FRH 090518
            if ("gastos".equals(tipoFacturacion)) {
                totalMesDT.setAlicuotaGasto((totalGNCInm + mtoAlicuota) - (totalAntInm + mtoAliPag));
            } else {
                totalMesDT.setAlicuotaGasto((totalGNCInm + mtoFijoFacturacion) - (totalAntInm + mtoAliPag));
            }
            //
            gastoTotal.add(totalMesDT);

            mtoTotalDolares = totalMesDT.getAlicuotaGasto();
            // Crear un objeto con el total en dólares
            if (tasaExiste) {

                AvisoDeCobroDT lineaEnBlancoDT = new AvisoDeCobroDT();
                lineaEnBlancoDT.setCodInm(inm.getCodInmueble());
                lineaEnBlancoDT.setGrupo(1);

                lineaEnBlancoDT.setPropInm(inm.getNombPropInmueble());
                lineaEnBlancoDT.setTelInm(inm.getTelInmueble());
                lineaEnBlancoDT.setDirInm(inm.getDirInmueble());
                lineaEnBlancoDT.setEmailInm(inm.getEmailPropInmueble());
                lineaEnBlancoDT.setAlicuotaInm(inm.getAlicInmueble());
                lineaEnBlancoDT.setNumAviso(proxAvisoCobro);
                lineaEnBlancoDT.setTotCxC(cxcTotal);

                lineaEnBlancoDT.setTotalMes(totalMes);
                lineaEnBlancoDT.setTotGenCxC(totalGeneral);

                lineaEnBlancoDT.setFecGasto("");
                lineaEnBlancoDT.setConceptoGasto("");
                lineaEnBlancoDT.setRefGasto("");
                lineaEnBlancoDT.setProvGasto("");

                lineaEnBlancoDT.setAlicuotaGasto(null);
                gastoTotal.add(lineaEnBlancoDT);

                AvisoDeCobroDT totalDolaresDT = new AvisoDeCobroDT();
                totalDolaresDT.setCodInm(inm.getCodInmueble());
                totalDolaresDT.setGrupo(1);

                totalDolaresDT.setPropInm(inm.getNombPropInmueble());
                totalDolaresDT.setTelInm(inm.getTelInmueble());
                totalDolaresDT.setDirInm(inm.getDirInmueble());
                totalDolaresDT.setEmailInm(inm.getEmailPropInmueble());
                totalDolaresDT.setAlicuotaInm(inm.getAlicInmueble());
                totalDolaresDT.setNumAviso(proxAvisoCobro);
                totalDolaresDT.setTotCxC(cxcTotal);

                totalDolaresDT.setTotalMes(totalMes);
                totalDolaresDT.setTotGenCxC(totalGeneral);

                totalDolaresDT.setFecGasto("");
                totalDolaresDT.setConceptoGasto("");
                totalDolaresDT.setRefGasto("");
                totalDolaresDT.setProvGasto("**TOTAL EN DOLARES**");

                totalDolaresDT.setAlicuotaGasto(mtoTotalDolares / valorTasa);
                gastoTotal.add(totalDolaresDT);

                AvisoDeCobroDT tasaDeCambioDT = new AvisoDeCobroDT();
                tasaDeCambioDT.setCodInm(inm.getCodInmueble());
                tasaDeCambioDT.setGrupo(1);

                tasaDeCambioDT.setPropInm(inm.getNombPropInmueble());
                tasaDeCambioDT.setTelInm(inm.getTelInmueble());
                tasaDeCambioDT.setDirInm(inm.getDirInmueble());
                tasaDeCambioDT.setEmailInm(inm.getEmailPropInmueble());
                tasaDeCambioDT.setAlicuotaInm(inm.getAlicInmueble());
                tasaDeCambioDT.setNumAviso(proxAvisoCobro);
                tasaDeCambioDT.setTotCxC(cxcTotal);

                tasaDeCambioDT.setTotalMes(totalMes);
                tasaDeCambioDT.setTotGenCxC(totalGeneral);

                tasaDeCambioDT.setFecGasto("");
                tasaDeCambioDT.setConceptoGasto("");
                tasaDeCambioDT.setRefGasto("");
                tasaDeCambioDT.setProvGasto("**TASA DE CAMBIO HOY**");

                valorTasaDouble = valorTasa.doubleValue();
                tasaDeCambioDT.setAlicuotaGasto(valorTasaDouble);
                gastoTotal.add(tasaDeCambioDT);

            }
            proxAvisoCobro = proxAvisoCobro + 1;

            actualizarParametros(idCond);
            gastoTotalOrd = ordenarListaGastos(gastoTotal);
            emitirAvisoDeCobro(gastoTotalOrd, periodo, inm.getEmailPropInmueble());
            gastoTotal.clear();
            gastoTotalOrd.clear();

            if (!enviado) {
                break;
            }
        }
        progreso = 100;
    }

    public Condominio buscarCondominioFetchPropiedades(Integer idCond) {

        String nombQuery = "buscarCondominioFetchPropiedades";
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

    public Collection buscarInmueblesPropiedades(Condominio condominio) {

        Collection<Inmueble> lista = new ArrayList<Inmueble>();
        Collection<Inmueble> listaTot = new ArrayList<Inmueble>();

        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();

            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

            try {
                lista = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            listaTot.addAll(lista);
        }
        return listaTot;
    }

    public Inmueble buscarInmueblePorId() {

        String nombQuery = "buscarInmueblePorId";
        Integer idInm = Integer.valueOf(idInmueble);

        Inmueble inmueble = null;
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return inmueble;
    }

    public String buscarProveedor(Integer idProv) {

        Proveedor proveedor;
        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            proveedor = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombProv = proveedor.getNombProveedor();
        return nombProv;
    }

    public Collection buscarGastoComunOrdPorIdCond(Integer idCond) {

        Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();

        String nombQuery = "buscarGastosComunesOrdPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCO = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaGCO;
        }

        return listaGCO;
    }

    public Double buscarMontoReserva(Integer idCond) {

        Double mtoReserva = 0.0;

        Collection<Fondo> fondos = new ArrayList<Fondo>();
        fondos = getFondosPorCondominio(idCond);

        Integer idFondoRes = 0;
        if (fondos.isEmpty()) {
            return mtoReserva;
        }
        for (Iterator<Fondo> it = fondos.iterator();
                it.hasNext();) {
            Fondo tf = (Fondo) it.next();
            if ("Reserva".equals(tf.getTipoFondo())) {
                if ("ACT".equals(tf.getEdoFondo())) {
                    idFondoRes = tf.getId();
                    break;
                }
            }
        }
        if (idFondoRes != 0) {
            Collection<TranFondo> tranFondos = buscarTranFondoReserva(idFondoRes, periodoActual);
            for (Iterator<TranFondo> it2 = tranFondos.iterator();
                    it2.hasNext();) {
                TranFondo tranf = (TranFondo) it2.next();
                mtoReserva = (mtoReserva + tranf.getMtoTranFondo());
            }

        }

        return mtoReserva;
    }

    private Collection<TranFondo> buscarTranFondoReserva(Integer idFondoRes, Calendar periodoActual) {

        Collection<TranFondo> reserva = new ArrayList<TranFondo>();

        String nombQuery = "buscarTranFondosReservaPeriodo";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            reserva = dataAcces.buscarListaEntidadesPeriodoActual(idFondoRes, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return reserva;
    }

    public Double buscarMontoPrestaciones(Integer idCond) {

        Collection<Fondo> fondos = new ArrayList<Fondo>();
        fondos = getFondosPorCondominio(idCond);

        Double mtoTotPrestaciones = 0.0;
        Integer idFondoPres = 0;
        if (fondos.isEmpty()) {
            return mtoTotPrestaciones;
        }
        for (Iterator<Fondo> it = fondos.iterator();
                it.hasNext();) {
            Fondo tf = (Fondo) it.next();
            if ("Prestaciones".equals(tf.getTipoFondo())) {
                idFondoPres = tf.getId();
                break;
            }
        }
        if (idFondoPres != 0) {
            Collection<TranFondo> tranFondos = buscarTranFondoPrestaciones(idFondoPres, periodoActual);
            for (Iterator<TranFondo> it2 = tranFondos.iterator();
                    it2.hasNext();) {
                TranFondo tranf = (TranFondo) it2.next();
                mtoTotPrestaciones = (mtoTotPrestaciones + tranf.getMtoTranFondo());
            }

        }
        return mtoTotPrestaciones;
    }

    public Collection ordenarListaGastos(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<AvisoDeCobroDT> listaDesOrdenada = new ArrayList<AvisoDeCobroDT>();

        for (Iterator<AvisoDeCobroDT> it = lista.iterator();
                it.hasNext();) {
            AvisoDeCobroDT con = (AvisoDeCobroDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorAvisoDeCobroDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<AvisoDeCobroDT> coleccionOrdenada = new ArrayList<AvisoDeCobroDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            AvisoDeCobroDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public Collection getFondosPorCondominio(Integer idCond) {

        Collection<Fondo> listFondo = new ArrayList<Fondo>();
        String nombQuery = "buscarFondosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listFondo = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listFondo;
    }

    private Collection<TranFondo> buscarTranFondoPrestaciones(Integer idFondoPres, Calendar periodoActual) {

        Collection<TranFondo> prestaciones = new ArrayList<TranFondo>();

        String nombQuery = "buscarTranFondoPresPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            prestaciones = dataAcces.buscarListaEntidadesPeriodoActual(idFondoPres, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return prestaciones;
    }

    public void buscarFondos(Integer idCond) {

        Fondo fondoRes = new Fondo();
        Collection<Fondo> listFondo = new ArrayList<Fondo>();
        String nombQuery = "buscarFondoReservaPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondoRes = dataAcces.buscarEntidad(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
        if (fondoRes != null) {
            mtoIniReserva = fondoRes.getMtoIniFondo();
            saldoReserva = fondoRes.getSaldoFondo();
        } else {
            mtoIniReserva = 0.0;
            saldoReserva = 0.0;
        }
        // Busqueda de los fondos de prestaciones y especiales definidos para devolver la sumatoria de cada unao de ellos

        mtoIniPrestaciones = 0.0;
        saldoPrestaciones = 0.0;
        mtoIniEspecial = 0.0;
        saldoEspecial = 0.0;

        nombQuery = "buscarFondosPorIdCond";

        try {
            listFondo = (Collection<Fondo>) dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        if (listFondo.isEmpty()) {
            return;
        }
        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {
            Fondo fon = (Fondo) it.next();
            if ("Prestaciones".equals(fon.getTipoFondo())) {
                mtoIniPrestaciones = fon.getMtoIniFondo();
                saldoPrestaciones = saldoPrestaciones + fon.getSaldoFondo();

            }
            if ("Especial".equals(fon.getTipoFondo())) {
                mtoIniEspecial = fon.getMtoIniFondo();
                saldoEspecial = saldoEspecial + fon.getSaldoFondo();

            }
        }

    }

    public void buscarParametros(Integer idCond) {

        Condominio condominio;

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        Parametro parametro = condominio.getParametro();
        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun parametro para el condominio actual!.Por favor, contacte con su administrador..."));
            return;
        }

        proxAvisoCobro = parametro.getProxAvisoCobro();
        obsAvisoCobro = parametro.getObsAvisoCobro();
        obsAvisoCobro2 = parametro.getObsAvisoCobro2();
        tipoFacturacion = parametro.getTipoFacturacion();
        mtoFijoFacturacion = parametro.getMtoFijoFacturacion();
        porcMora = parametro.getPorcMora();

        return;

    }

    public void actualizarParametros(Integer idCond) {

        Condominio condominio;

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        Parametro parametro = condominio.getParametro();
        parametro.setProxAvisoCobro(proxAvisoCobro);

        try {
            dataAcces.almacenarOModificarEntidad(condominio);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

//******************************************REPORTE AVISO DE COBRO (Cont...) :  METODOS PARA EL obtener el total de la CXC/Inmn  ************************************************************//
     public void buscarMontoCxCInm(Integer idInm, Integer idCond, Double porcAli) throws ParseException {

        Collection<CxCDT> listaCxCParcial = new ArrayList<CxCDT>();
        Collection<CxCInmDT> listaCxCInm = new ArrayList<CxCInmDT>();
        Condominio condominio = buscarCondominioFetchPropiedades(idCond);

        Double totalGCO = 0.0;

        Double alicuota;
        BigDecimal saldo;
        BigDecimal cxcTotalBD = new BigDecimal("0.00");
        Double mtoRedondeado;
        cxcTotal = 0.0;

        //------------------------------------------------------------------
        listaGNCMes.clear();
        listaCxCParcial = getFacGNCInm(idInm);
        for (Iterator<CxCDT> it5 = listaCxCParcial.iterator();
                it5.hasNext();) {
            CxCDT dp = (CxCDT) it5.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }
        cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;

        //    System.out.println("**************** PARA EL ACUMULADO FACGNC Id: " + dp.getId() + " " + dp.getMtoFac());
        listaCxCParcial.clear();

        //-------------------------------------------------------------------//
        listaCxCParcial = getDeudaPendienteInm(idInm);
        for (Iterator<CxCDT> it1 = listaCxCParcial.iterator();
                it1.hasNext();) {
            CxCDT dp = (CxCDT) it1.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }
        cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        listaCxCParcial.clear();
        //-------------------------------------------------------------------//  
        listaCxCParcial = getAlicuotasInm(idInm);
        for (Iterator<CxCDT> it2 = listaCxCParcial.iterator();
                it2.hasNext();) {
            CxCDT dp = (CxCDT) it2.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }

        cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        listaCxCParcial.clear();
        //------------------------------------------------------------------
        listaCxCParcial = getFacCuotaEspInm(idInm);
        for (Iterator<CxCDT> it4 = listaCxCParcial.iterator();
                it4.hasNext();) {
            CxCDT dp = (CxCDT) it4.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }

        cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        listaCxCParcial.clear();
        //-------------------------------------------------------------------//
        listaCxCParcial = getNotasDeDebitoInm(idInm);
        for (Iterator<CxCDT> it3 = listaCxCParcial.iterator();
                it3.hasNext();) {
            CxCDT dp = (CxCDT) it3.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }

        //System.out.println("**************** PARA EL ACUMULADO ND Id: " + dp.getId() + " " + dp.getMtoFac());
        cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        listaCxCParcial.clear();
        //-------------------------------------------------------------------//

        listaCxCParcial = getNotasDeCreditoInm(idInm);
        for (Iterator<CxCDT> it3 = listaCxCParcial.iterator();
                it3.hasNext();) {
            CxCDT dp = (CxCDT) it3.next();
            //Prueba
            saldo = BigDecimal.valueOf(dp.getMtoFac()).setScale(2, RoundingMode.HALF_EVEN);
            cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
            //
            //cxcTotal = cxcTotal + dp.getMtoFac();
        }

       cxcTotal = cxcTotal + cxcTotalBD.doubleValue();
        listaCxCParcial.clear();

        //-------------------------------------------------------------------//
        // ******************* determinar alicuota y resto de los  gastos a pagar en el periodo  ************************
        totalGCO = 0.0;
        totalParcial = 0.0;
        mtoAlicuota = 0.0;
        totalGeneral = 0.0;

        for (Iterator<GastoComunOrd> it6 = listaGCO.iterator();
                it6.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it6.next();

            totalGCO = totalGCO + gco.getMtoGastoComunOrd();

        }
        totalParcial = totalGCO + mtoReserva + mtoPrestaciones + mtoAjustes;

        mtoAlicuota = (totalParcial * porcAli) / 100;

        // Condicion para que considere el monto de acuerdo al tipo de facturación FRH 090518
        if ("gastos".equals(tipoFacturacion)) {
            totalMes = ((mtoAlicuota - mtoAliPag) + gncTotalMes + totalMora + totalNotasDebPer + totalCEsp);
        } else {
            totalMes = ((mtoFijoFacturacion - mtoAliPag) + gncTotalMes + totalMora + totalNotasDebPer + totalCEsp);
        }

        totalGeneral = cxcTotal + totalMes;

    }

    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado = (resultado - parteEntera) * Math.pow(10, numeroDecimales);
        resultado = Math.round(resultado);
        resultado = (resultado / Math.pow(10, numeroDecimales)) + parteEntera;
        return resultado;
    }

    public Collection getDeudaPendienteInm(Integer idInm) {

        Collection<DeudaPendCxC> lista = new ArrayList<DeudaPendCxC>();
        Collection<CxCDT> listaDP = new ArrayList<CxCDT>();

        String nombQuery = "buscarDeudasPendInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            return listaDP;
        }
        if (lista.isEmpty()) {
            return listaDP;
        }

        String estado;
        for (Iterator<DeudaPendCxC> it = lista.iterator();
                it.hasNext();) {
            DeudaPendCxC deudaP = (DeudaPendCxC) it.next();
            if ((deudaP.getFecDeudaPendCxC().before(periodoActual))
                    | (deudaP.getFecDeudaPendCxC().equals(periodoActual))) {
                CxCDT cxc = new CxCDT();
                cxc.setId(deudaP.getId());
                cxc.setMtoFac(deudaP.getSaldoDeudaPendCxC());
                listaDP.add(cxc);
            }
        }

        return listaDP;
    }

    public Collection getAlicuotasInm(Integer idInm) {

        Collection<Alicuota> lista = new ArrayList<Alicuota>();
        Collection<CxCDT> listaAli = new ArrayList<CxCDT>();

        String nombQuery = "buscarAlicuotasInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaAli;
        }
        if (lista.isEmpty()) {
            return listaAli;
        }

        for (Iterator<Alicuota> it = lista.iterator();
                it.hasNext();) {
            Alicuota alicuota = (Alicuota) it.next();
            if (alicuota.getPeriodoActual().before(periodoActual)) {
                CxCDT cxc = new CxCDT();
                cxc.setId(alicuota.getId());
                cxc.setMtoFac(alicuota.getSaldoAlicuota());
                listaAli.add(cxc);
            }
        }

        return listaAli;
    }

    public Collection getNotasDeDebitoInm(Integer idInm) throws ParseException {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();
        Collection<CxCDT> listaND = new ArrayList<CxCDT>();
        notasDebPer.clear();
        totalNotasDebPer = 0.0;
        String nombQuery = "buscarNotasDebInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

            return listaND;
        }

        if (lista.isEmpty()) {
            return listaND;
        }
        Calendar fechaNota;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC nd = (NotaCredDebCxC) it.next();
            fechaNota = convertirFecha(nd.getFecNotaCredDebCxC());
            if (fechaNota.before(periodoActual)) {
                CxCDT cxc = new CxCDT();
                cxc.setId(nd.getId());
                cxc.setMtoFac(nd.getSaldoNotaCredDebCxC());
                listaND.add(cxc);
            }
        }

        // obtener las ND del periodo y totalizarlas
        String tipoOper = "";
        for (Iterator<NotaCredDebCxC> it2 = lista.iterator();
                it2.hasNext();) {
            NotaCredDebCxC nd = (NotaCredDebCxC) it2.next();
            fechaNota = convertirFecha(nd.getFecNotaCredDebCxC());
            tipoOper = nd.getTipoOperNotaCredDebCxC();

            if (tipoOper.equals("MANUAL")) {
                if ((fechaNota.after(periodoActual) | fechaNota.equals(periodoActual))) {
                    if (fechaNota.before(periodoActual2)) {
                        notasDebPer.add(nd);
                        totalNotasDebPer = totalNotasDebPer + nd.getSaldoNotaCredDebCxC();
                    }
                }
            }

        }
        return listaND;
    }

    public Double getNotasDebitoInmMora(Integer idInm) throws ParseException {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();
        Collection<CxCDT> listaND = new ArrayList<CxCDT>();
        totalMora = 0.0;

        String nombQuery = "buscarNotasDebitoCxCInmEnMoraPerAct";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

            return totalMora;
        }

        if (lista.isEmpty()) {
            return totalMora;
        }

        for (Iterator<NotaCredDebCxC> it5 = lista.iterator();
                it5.hasNext();) {
            NotaCredDebCxC nD = (NotaCredDebCxC) it5.next();
            if (!("CANC".equals(nD.getEdoNotaCredDebCxC()))) {
                totalMora = totalMora + nD.getSaldoNotaCredDebCxC();
            }

        }

        return totalMora;

    }

    public Collection getFacCuotaEspInm(Integer idInm) {

        Collection<FacCuotaEsp> lista = new ArrayList<FacCuotaEsp>();
        Collection<CxCDT> listaCE = new ArrayList<CxCDT>();

        String nombQuery = "buscarFacCuotaEspInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaCE;
        }
        if (lista.isEmpty()) {
            return listaCE;
        }

        for (Iterator<FacCuotaEsp> it = lista.iterator();
                it.hasNext();) {
            FacCuotaEsp ce = (FacCuotaEsp) it.next();
            if (ce.getPeriodoActual().before(periodoActual)) {
                CxCDT cxc = new CxCDT();
                cxc.setId(ce.getId());
                cxc.setMtoFac(ce.getSaldoFacCuotaEsp());
                listaCE.add(cxc);
            }
        }

        return listaCE;
    }

    public Collection getFacGNCInm(Integer idInm) {

        Collection<FacGastoNoComun> lista = new ArrayList<FacGastoNoComun>();
        Collection<CxCDT> listaFGNC = new ArrayList<CxCDT>();
        Calendar fecFacGNC;
        gncTotalMes = 0.0;

        int mesFac;
        int añoFac;
        int mesPer;
        int añoPer;

        String nombQuery = "buscarTodasFacGastosNoComunesInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaFGNC;
        }
        if (lista.isEmpty()) {
            return listaFGNC;
        }

        for (Iterator<FacGastoNoComun> it = lista.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            if (fgnc.getFecFacGNC().before(periodoActual)) {
                if (!("INAC".equals(fgnc.getEdoFacGastoNoComun()))) {
                    if (!("CANC".equals(fgnc.getEdoFacGastoNoComun()))) {
                        CxCDT cxc = new CxCDT();
                        cxc.setId(fgnc.getId());
                        cxc.setMtoFac(fgnc.getSaldoFacGastoNoComun());
                        listaFGNC.add(cxc);
                    }
                }
            }
        }

        for (Iterator<FacGastoNoComun> it = lista.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();

            fecFacGNC = fgnc.getFecFacGNC();

            mesFac = fecFacGNC.get(Calendar.MONTH);
            añoFac = fecFacGNC.get(Calendar.YEAR);
            mesPer = periodoActual.get(Calendar.MONTH);
            añoPer = periodoActual.get(Calendar.YEAR);

            if ((añoPer == añoFac) & (mesPer == mesFac)) {

                if (!("INAC".equals(fgnc.getEdoFacGastoNoComun()))) {

                    if (!("CANC".equals(fgnc.getEdoFacGastoNoComun()))) {
                        gncTotalMes = gncTotalMes + fgnc.getSaldoFacGastoNoComun();
                        listaGNCMes.add(fgnc);
                    }
                }
            }
        }

        return listaFGNC;

    }

    public Collection getNotasDeCreditoInm(Integer idInm) throws ParseException {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();
        Collection<CxCDT> listaNC = new ArrayList<CxCDT>();

        String nombQuery = "buscarNotasCredInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

            return lista;
        }

        if (lista.isEmpty()) {
            return lista;
        }
        Calendar fechaNota;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC nc = (NotaCredDebCxC) it.next();
            fechaNota = convertirFecha(nc.getFecNotaCredDebCxC());
            if (fechaNota.before(periodoActual)) {
                CxCDT cxc = new CxCDT();
                cxc.setId(nc.getId());
                cxc.setMtoFac(nc.getSaldoNotaCredDebCxC());
                listaNC.add(cxc);
            }
        }

        return listaNC;

    }

    public Collection getNotasDeCreditoInmAfter(Integer idInm) throws ParseException {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();
        Collection<NotaCredDebCxC> listaNC = new ArrayList<NotaCredDebCxC>();

        String nombQuery = "buscarNotasCredInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

            return listaNC;
        }

        if (lista.isEmpty()) {
            return listaNC;
        }
        Calendar fechaNota;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC nc = (NotaCredDebCxC) it.next();
            //Convertir la fecha del documento a periodo:
            fechaNota = convertirFecha(nc.getFecNotaCredDebCxC());
            fechaNota.set(Calendar.DAY_OF_MONTH, 01);
            fechaNota.set(Calendar.MILLISECOND, 0);
            if (fechaNota.equals(periodoActual) | fechaNota.equals(periodoActual2)) {
                totalAntInm = totalAntInm + nc.getSaldoNotaCredDebCxC();
                listaNC.add(nc);
            }
        }
        return listaNC;

    }

    public Collection getPagosAdelantadosInmAfter(Integer idInm) throws ParseException {

        Collection<PagoAdelantado> lista = new ArrayList<PagoAdelantado>();
        Collection<PagoAdelantado> listaPAD = new ArrayList<PagoAdelantado>();

        String nombQuery = "buscarPagosAdelantadosPorIdInmVigentes";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

            return listaPAD;
        }

        if (lista.isEmpty()) {
            return listaPAD;
        }
        Calendar fechaNota;
        for (Iterator<PagoAdelantado> it = lista.iterator();
                it.hasNext();) {
            PagoAdelantado pa = (PagoAdelantado) it.next();
            //Convertir la fecha del documento a periodo:
            fechaNota = convertirFecha(pa.getFecPagoAdelantado());
            fechaNota.set(Calendar.DAY_OF_MONTH, 01);
            fechaNota.set(Calendar.MILLISECOND, 0);
            if (fechaNota.equals(periodoActual) | fechaNota.equals(periodoActual2)) {
                totalAntInm = totalAntInm + pa.getSaldoPagoAdelantado();
                listaPAD.add(pa);
            }
        }

        return listaPAD;

    }

    public Collection obtenerFacCuotaEspInm(Integer idInm) {

        Collection<FacCuotaEsp> lista = new ArrayList<FacCuotaEsp>();
        Collection<FacCuotaEsp> listaCE = new ArrayList<FacCuotaEsp>();

        String nombQuery = "buscarFacCuotaEspPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaCE;
        }

        if (lista.isEmpty()) {
            return listaCE;
        }

        for (Iterator<FacCuotaEsp> it = lista.iterator();
                it.hasNext();) {
            FacCuotaEsp ce = (FacCuotaEsp) it.next();
            if (ce.getPeriodoActual().equals(periodoActual)) {
                if (!(ce.getEdoFacCuotaEsp().equals("CANC"))) {
                    listaCE.add(ce);
                }
            }
        }
        totalCEsp = 0.0;
        for (Iterator<FacCuotaEsp> it5 = listaCE.iterator();
                it5.hasNext();) {
            FacCuotaEsp ce = (FacCuotaEsp) it5.next();
            //totalCEsp = totalCEsp + ce.getMtoFacCuotaEsp();
            totalCEsp = totalCEsp + ce.getSaldoFacCuotaEsp();
        }

        return listaCE;
    }

    public String buscarNombConcepto(Integer idConc) {

        Concepto concepto;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String descConcepto = concepto.getDescConcepto();
        return descConcepto;
    }

    public void setSeleccion(HtmlSelectOneRadio seleccion) {
        this.seleccion = seleccion;
    }

    public HtmlSelectOneRadio getSeleccion() {
        return seleccion;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setContraseñaUsuario(String contraseñaUsuario) {
        this.contraseñaUsuario = contraseñaUsuario;
    }

    public String getContraseñaUsuario() {
        return contraseñaUsuario;
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

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public int getProgreso() {
        return progreso;
    }
}
