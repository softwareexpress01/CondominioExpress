/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

import configuracion.modelos.Parametro;
import movimientos.modelos.PagoAdelantado;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Cuenta;
import movimientos.modelos.PagoAdelantadoDT;

import datosbasicos.modelos.Inmueble;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import java.text.ParseException;
import javax.faces.application.FacesMessage;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
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

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import net.sf.jasperreports.engine.export.JRPdfExporter;

public class EmailReciboPagoAdelCtrl extends DaoCrud<PagoAdelantado, Inmueble, Integer> implements Serializable {

    private HtmlSelectOneRadio seleccion;
    private String emailUsuario;
    private String contraseñaUsuario;
    private String para;
    private String asunto;
    private String mensaje;
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    Collection<Inmueble> listaInmuebles = new ArrayList<Inmueble>();
    public String refPagoAdelantadoSel;
    private BeanUsuario beanUsuario;
    private Condominio condominio;

    public EmailReciboPagoAdelCtrl() {
    }

    public String irASeleccionarCondominio() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        return "selcond";
    }

    public String salir() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

        return "informes";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String msg = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (beanUsuario.getEmailPropInmueble().equals(enBlanco)) {
            msg = "Por favor, ingrese el correo electrónico del destinatario...";
            return msg;
        } else {
            if (!val.esEmail(beanUsuario.getEmailPropInmueble())) {
                msg = "El formato del correo electrónico del destinatario es inválido, por favor intente de nuevo";
                return msg;
            }
        }

        if (this.getAsunto().equals(enBlanco)) {
            msg = "Por favor, ingrese el asunto a referirse en el correo...";
            return msg;
        }
        return msg;
    }

    public Collection getInmueblesPorPropiedad() {

        listaOrdII.clear();
        listaOrdII.add(new SelectItem(new Integer(0), "Seleccione"));

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idProp = beanUsuario.getIdPropActual();

        Collection<Inmueble> listInm = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        listaOrd = ordenarLista(listInm);// para ordenar la lista de inmuebles

        for (Iterator<Inmueble> it = listaOrd.iterator();
                it.hasNext();) {
            Inmueble cta = (Inmueble) it.next();
            listaOrdII.add(new SelectItem(cta.getId(), cta.getCodInmueble()));
        }

        return listaOrdII;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<Inmueble> listaDesOrdenada = new ArrayList<Inmueble>();

        for (Iterator<Inmueble> it = lista.iterator();
                it.hasNext();) {
            Inmueble Inmueble1 = (Inmueble) it.next();
            listaDesOrdenada.add(Inmueble1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorInm());

        Collection<Inmueble> coleccionOrdenada = new ArrayList<Inmueble>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Inmueble Inmueble2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(Inmueble2);
        }
        return coleccionOrdenada;
    }

    public String buscarNombBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarNumCuenta(Integer idCuenta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCuenta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String numCuenta = cuenta.getNumCuenta();
        return numCuenta;
    }

    public Inmueble buscarInmueble(Integer idInmueble) {

        Inmueble inmueble;
        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInmueble, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde  ******** BUSCARiNMUEBLE "));
            return (null);
        }
        return inmueble;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

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

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoCrudAux<Parametro> dataAcces = new DaoCrudAux<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void generarEmailReciboPagoAdel(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException, MessagingException {

        String vacio = new String();

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String men = validarPanel();

        if (!men.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(men));
            return;
        }

        //-------------------------------------------------------------------------------------------------------------------
        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPagoAdel.jasper");

        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //-------------------------------------------------------------------------
        // Se busca el inmueble y el pago para obtener los parametros del reporte
        //-------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();
        Integer IdInmueble = (Integer) ctx.getExternalContext().getSessionMap().get("IdInmueble");

        Inmueble inm = buscarInmueble(IdInmueble);

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();

        PagoAdelantadoDT pagoAdelantadoDT = (PagoAdelantadoDT) ctx.getExternalContext().getSessionMap().get("PagoAdelantadoDT");

        String fecEmiPago = pagoAdelantadoDT.getFecMovPagoAdelantado();
        String desPago = pagoAdelantadoDT.getDescPagoAdelantado();
        String numDocPago = pagoAdelantadoDT.getRefPagoAdelantado();
        Double mtoPago = Double.parseDouble(pagoAdelantadoDT.getMtoPagoAdelantado());
        String formaPago = pagoAdelantadoDT.getFormaPagoAdelantado();
        String fecDocPago = pagoAdelantadoDT.getFecPagoAdelantado();
        String bancoPago = pagoAdelantadoDT.getNombBcoPagoAdelantado();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        condominio = buscarCondominio();
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
            actualizarParametro(parametro);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...reporte cancelado"));
            return;
        }

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
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, new JREmptyDataSource());

        //-------------------------------------------------------------------------------------------------------------------
        // Exportar el reporte anterior a pdf
        // ------------------------------------------------------------------------------------------------------------------
        Long milisegundos = Calendar.getInstance().getTimeInMillis();
        String nombre = milisegundos.toString().concat("ReciboPagoAdel.pdf");
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

        setPara(beanUsuario.getEmailPropInmueble());
        enviarEmail(para, asunto, mensaje, reportePdf);

        //--------------------------------------
        // Se elimina el anexo creado 
        // -------------------------------------
        File f = new File(reportePdf);
        f.delete();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El correo electrónico fué enviado exitosamente!"));
        return;

    }

    public void enviarEmail(String para, String asunto, String mensaje, String reportePdf) throws MessagingException {

        Properties props = new Properties();
        int port;
        String host;
        boolean auth = true;
        boolean debug = true;

        port = 587;
        host = "condominioexpressve.com";
        emailUsuario = "recibopagoadel@condominioexpressve.com";
        contraseñaUsuario = "tango#16";
        /*host = "us2.smtp.mailhostbox.com";
        emailUsuario = "informes@condominioexpressve.com";
        contraseñaUsuario = "X^Z%Bmhs7";*/
        auth = true;
        Protocol protocol = Protocol.SMTP;
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
        adjunto.setDataHandler(new DataHandler(new FileDataSource(reportePdf)));
        adjunto.setFileName("Recibo de pago adelantado.pdf");

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
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

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
}
