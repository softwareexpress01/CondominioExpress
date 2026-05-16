/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.text.ParseException;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import util.BeanUsuario;
import util.JSFUtil;
import util.Validator;

/**
 *
 * @author Jesus
 */
public class EmailCorrespondenciaCtrl implements Serializable {

    private HtmlSelectOneRadio seleccion;
    private String emailUsuario;
    private String contraseñaUsuario;
    private String para;
    private String asunto;
    private String mensaje;
    private String idInmueble;
    private BeanUsuario beanUsuario;
    private String mensajeError;
    @EJB
    private EmailCorrespondenciaEJB ejb;

    public EmailCorrespondenciaCtrl() {
    }

    public void inicializarPanel() {

        this.setSeleccion(null);
        this.setEmailUsuario(null);
        this.setContraseñaUsuario(null);
        this.setPara(null);
        this.setAsunto(null);
        this.setMensaje(null);

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String msg = new String();
        String ceroString = "0";
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

    public void generarCorrespondencia() throws MessagingException, ParseException {

        //---------------------------------------------------------------------------------
        // Recupera el inmueble desde la pagina de generación de correspondencia
        //---------------------------------------------------------------------------------
        FacesContext ctx = FacesContext.getCurrentInstance();
        EmailAvisoDeCobroCtrl e = new EmailAvisoDeCobroCtrl();

        idInmueble = (String) ctx.getExternalContext().getSessionMap().get("inmueble");

        String vacio = new String();
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        mensajeError = validarPanel();

        if (!mensajeError.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensajeError));
            return;
        }

        Integer idInm = Integer.valueOf(idInmueble);
        Condominio condominio;
        Integer idCond = beanUsuario.getIdCondActual();

        if (idInm == 0) {
            condominio = e.buscarCondominioFetchPropiedades(idCond);
            e.listaInm = e.buscarInmueblesPropiedades(condominio);
            ejb.enviarEmailTodos(e.listaInm, asunto, mensaje, condominio);
        } else {
            condominio = e.buscarCondominio(idCond);
            ejb.enviarEmail(beanUsuario.getEmailPropInmueble(), asunto, mensaje, condominio);
        }

        inicializarPanel();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Envío de correo(s) en progreso... Por favor, verifique su correo electrónico en unos minutos."));

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
