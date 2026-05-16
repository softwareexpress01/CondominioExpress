package informes.backing;

import configuracion.modelos.Parametro;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import informes.modelos.PagoFacCxCDT;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
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
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoCxCDT;
import movimientos.modelos.PagoFacCxC;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class EmailReciboPagoCxCCtrl extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

    private HtmlSelectOneRadio seleccion;
    private String emailUsuario;
    private String contraseñaUsuario;
    private String para;
    private String asunto;
    private String mensaje;
    private BeanUsuario beanUsuario;
    private Condominio condominio;

    public EmailReciboPagoCxCCtrl() {
    }

    public String irASeleccionarCondominio() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        beanUsuario.modificar = true;

        return "selcond";
    }

    public String salir() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

        return "informes";

    }

    public String validarPanel() {

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

    public void generarReciboPago(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, MessagingException {

        String vacio = new String();
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String men = validarPanel();

        if (!men.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(men)); 
            return;
        }

        //-------------------------------------------------------------------------------------------------------------------
        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPago.jasper");

        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //-------------------------------------------------------------------------
        // Se busca el inmueble y el pago para obtener los parametros del reporte
        //-------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();
        Integer idInm = (Integer) ctx.getExternalContext().getSessionMap().get("IdInmueble");

        Inmueble inm = buscarInmueble(idInm);

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();

        PagoCxCDT pagoCxCDT = (PagoCxCDT) ctx.getExternalContext().getSessionMap().get("PagoCxCDT");

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
        condominio = buscarCondominio();
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //--------------------------------------------------------------
        // Se obtiene el numero del recibo y las observaciones de la 
        // la clase parametro del condominio actual.
        //--------------------------------------------------------------
        Integer numReciboCobro = null;
        String obsReciboCobro = null;

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

        //------------------------------------------------------------
        // Se crea el datasource de facturas de pagos de cxc
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagoFacCxC;

        Collection<PagoFacCxCDT> pagoFacCxCDT = buscarPagoFacCxC(codInm, pagoCxCDT);

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

        Protocol protocol;
        Properties props = new Properties();
        int port;
       // String host;
        boolean auth = true;
        boolean debug = true;

        port = 587;
        /*host = "condominioexpressve.com";
        emailUsuario = "recibopagocxc@condominioexpressve.com";
        contraseñaUsuario = "tango#16";*/
        
        final String emailUsuario = "condominioexpressve@gmail.com";
        final String contraseñaUsuario = "pish unfc sxxl hzwf";
        String host = "smtp.gmail.com";
        
        /*host = "us2.smtp.mailhostbox.com";
        emailUsuario = "informes@condominioexpressve.com";
        contraseñaUsuario = "X^Z%Bmhs7";*/
        
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
            message.setRecipients(Message.RecipientType.BCC, condominio.getEmailCondominio());
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setContent(multiParte);

            Transport.send(message);
        } catch (MessagingException ex) {
            System.out.println("Ocurrio un error de conexión con el servidor de correo:" + ex);
        }

    }

    public Inmueble buscarInmueble(Integer idInm) throws ValidatorException {

        Inmueble inm;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inm = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return inm;
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

    public Collection<PagoFacCxCDT> buscarPagoFacCxC(String codInm, PagoCxCDT pagoCxCDT) {

        Collection<PagoFacCxC> lista = new ArrayList<PagoFacCxC>();
        Collection<PagoFacCxCDT> pagosDT = new ArrayList<PagoFacCxCDT>();
        Integer idPagoCxC = pagoCxCDT.getId();

        //------------------Se agregaron las notas de credito generadas por el pago al recibo-------------------------
        NotaCredDebCxC notaCred;
        PagoFacCxCDT pagoFacCxCDT;

        String nombQuery = "buscarPagoFacCxCPorIdPago";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idPagoCxC, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        pagosDT = convertirPagosADT(codInm, lista);

        notaCred = buscarNotaCreditoPago(codInm, idPagoCxC);

        if (!(notaCred == null)) {
            pagoFacCxCDT = convertirNotaCredApagoFacCxCDT(codInm, notaCred);
            pagosDT.add(pagoFacCxCDT);
        }
        return pagosDT;
    }

    public NotaCredDebCxC buscarNotaCreditoPago(String codInm, Integer idPagoCxC) {

        NotaCredDebCxC ncr;

        String nombQuery = "buscarNotaCreditoPorIdPago";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncr = dataAcces.buscarEntidadPorId(idPagoCxC, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return ncr;
    }

    private Collection convertirPagosADT(String codInmueble, Collection listaPagos) {

        Collection listaDT = new ArrayList();
        Integer cero = 0;

        for (Iterator<PagoFacCxC> it = listaPagos.iterator();
                it.hasNext();) {

            PagoFacCxC pago = (PagoFacCxC) it.next();
            if (pago == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                PagoFacCxCDT pagoFacCxCDT = new PagoFacCxCDT();

                pagoFacCxCDT.setId(pago.getId());

                pagoFacCxCDT.setCodInmueble(codInmueble);

                //--------------------------------------------
                // Se busca las facturas involucradas
                //--------------------------------------------
                String dis = pago.getDiscFacCxC();
                Integer idFac = pago.getIdFacCxC();

                if (dis.equals("GNC")) {
                    FacGastoNoComun fgnc = buscarFacGastoNoComun(idFac);
                    Integer idCon = fgnc.getGastoNoComun().getIdConcepto();
                    Concepto con = buscarConcepto(idCon);
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    pagoFacCxCDT.setMtoCxC(fgnc.getMtoFacGastoNoComun());
                    pagoFacCxCDT.setSaldoCxC(fgnc.getSaldoFacGastoNoComun());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(fgnc.getFecFacGNC());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("ALI")) {
                    Alicuota alic = buscarAlicuota(idFac);
                    pagoFacCxCDT.setDesCxC(alic.getDesAlicuota());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(alic.getMtoAlicuota());
                    pagoFacCxCDT.setSaldoCxC(alic.getSaldoAlicuota());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(alic.getFecEmiAlicuota());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("CES")) {
                    FacCuotaEsp fce = buscarFacCuotaEsp(idFac);
                    pagoFacCxCDT.setDesCxC(fce.getDesFacCuotaEsp());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(fce.getMtoFacCuotaEsp());
                    pagoFacCxCDT.setSaldoCxC(fce.getSaldoFacCuotaEsp());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(fce.getFecEmiFacCuotaEsp());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("NCD") | dis.equals("NDE")) {
                    NotaCredDebCxC ncd = buscarNotaDebito(idFac);
                    //---------------------------------------------------------------------------------------------------
                    // Se busca el concepto de la nota de credito asociado a la factura y luego su
                    // descripcion. Para las notas de credito con idCon=cero - pago en exceso -se asigna la descripcion
                    // pero si es debito por mora , se toma la descripción (10-11-2016)JR
                    //---------------------------------------------------------------------------------------------------
                    Integer idCon = ncd.getIdConcepto();
                    if (!idCon.equals(cero)) {
                        Concepto con = buscarConcepto(idCon);
                        pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    } else {
                        if (ncd.getTipoNotaCredDebCxC().equals("Credito")) {
                            pagoFacCxCDT.setDesCxC("Pago en exceso");
                        } else {
                            pagoFacCxCDT.setDesCxC(ncd.getDescNotaCredDebCxC());
                        }
                    }
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(ncd.getMtoNotaCredDebCxC());
                    pagoFacCxCDT.setSaldoCxC(ncd.getSaldoNotaCredDebCxC());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());

                    pagoFacCxCDT.setFecEmisionCxC(ncd.getFecNotaCredDebCxC());
                }
                if (dis.equals("DPE")) {
                    DeudaPendCxC dpe = buscarDeudaPendiente(idFac);
                    Integer idCon = dpe.getIdConcepto();
                    Concepto con = buscarConcepto(idCon);
                    pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(dpe.getMtoDeudaPendCxC());
                    pagoFacCxCDT.setSaldoCxC(dpe.getSaldoDeudaPendCxC());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(dpe.getFecDeudaPendCxC());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : pago
                //----------------------------------------------------------------------------------

                listaDT.add(pagoFacCxCDT);
            }
        }
        return listaDT;
    }

    private PagoFacCxCDT convertirNotaCredApagoFacCxCDT(String codInmueble, NotaCredDebCxC notaCred) {

        PagoFacCxCDT pagoFacCxCDT = new PagoFacCxCDT();

        pagoFacCxCDT.setId(notaCred.getIdPagoCxC());
        pagoFacCxCDT.setCodInmueble(codInmueble);
        pagoFacCxCDT.setTipoCxC("NCR");
        pagoFacCxCDT.setDesCxC("Pago excedió monto facturas");
        pagoFacCxCDT.setMtoCxC(notaCred.getMtoNotaCredDebCxC());
        pagoFacCxCDT.setSaldoCxC(notaCred.getSaldoNotaCredDebCxC());
        pagoFacCxCDT.setMtoPagadoCxC(notaCred.getMtoNotaCredDebCxC());
        pagoFacCxCDT.setFecEmisionCxC(notaCred.getFecNotaCredDebCxC());

        return pagoFacCxCDT;
    }

    public Concepto buscarConcepto(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto;
    }

    public String formatearFecha(Calendar fechaFac) {

        //----------------------------------------------------------------
        //Se da formato a la fecha del pago para mostrarla en el reporte
        //----------------------------------------------------------------
        String dia = "";
        String mes = "";
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

        String stringFechaDp = dia + "-" + mes + "-" + añoFac;

        return stringFechaDp;
    }

    public FacGastoNoComun buscarFacGastoNoComun(Integer idFac) throws ValidatorException {

        FacGastoNoComun fgnc;
        String nombQuery = "buscarFacGastoNoComunPorId";
        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fgnc;
    }

    public Alicuota buscarAlicuota(Integer idFac) throws ValidatorException {

        Alicuota alic;
        String nombQuery = "buscarAlicuotaPorId";
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            alic = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return alic;
    }

    public FacCuotaEsp buscarFacCuotaEsp(Integer idFac) throws ValidatorException {

        FacCuotaEsp fce;
        String nombQuery = "buscarFacCuotaEspPorId";
        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fce;
    }

    public NotaCredDebCxC buscarNotaDebito(Integer idFac) throws ValidatorException {

        NotaCredDebCxC ncd;
        String nombQuery = "buscarNotaCredDebCxCPorId";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncd = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return ncd;
    }

    public DeudaPendCxC buscarDeudaPendiente(Integer idFac) throws ValidatorException {

        DeudaPendCxC dpe;
        String nombQuery = "buscarDeudaPendPorId";
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dpe = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return dpe;
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
