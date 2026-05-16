package acceso.backing;

import datosbasicos.backing.CondominioCtrl;
import datosbasicos.modelos.Usuario;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.HibernateException;
import saas.modelos.Compañia;
import saas.modelos.Plan;
import saas.modelos.Suscripcion;
import util.BeanUsuario;
import util.DaoQuery;
import util.JSFUtil;

/**
 * Backing bean for login
 */
public class LoginCtrl implements Serializable {

    private HtmlInputText codUsuario;
    private HtmlInputSecret contUsuario;
    private Suscripcion sus;
    private Compañia comp;
    
    //   private final static String AUTH_USER = "Authorized_User";

    public LoginCtrl() {
    }

    public String login() throws ValidatorException {

        Usuario usuarioActual;
        String cod;
        cod = this.codUsuario.getValue().toString();

        String enBlanco = new String();

        if (cod.equals(enBlanco)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Por favor, ingrese el código del usuario..."));
            return (null);
        }
        String contraseña = (String) this.contUsuario.getValue().toString();
        if (contraseña.equals(enBlanco)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Por favor, ingrese la contraseña..."));
            return (null);
        }

        String nombQuery = "buscarUsuario";
        DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
        try {
            usuarioActual = dataAcces.buscarEntidad(cod, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (usuarioActual == null) {
            // login failed
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Código de usuario no existe!..por favor intente de nuevo"));
            return (null);
        }

        if (!usuarioActual.getContUsuario().equals(contraseña)) {
            this.contUsuario = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Contraseña inválida!.. por favor ingresela de nuevo"));
            return (null);
        }
        // Valida que el plan de sucripcion no sea de la versión gratis

        sus = buscarSuscripcion(usuarioActual.getId());
        CondominioCtrl cond=new CondominioCtrl(); 
        Plan plan=cond.buscarPlanSuscriptor(sus.getId()); 

        if (plan.nombPlan.equals("Gratis")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Versión profesional (Premium). Por favor, diríjase al link para la versión gratis"));
            return (null);
        }
        // Valida que la suscripcion este activa
        
        if (sus.getEdoSuscripcion().equals("INA")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Estimado usuario, su suscripción está inactiva... "
                            + "Le invitamos a realizar su pago para continuar disfrutando de nuestros servicios"));
            return (null);
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        copiarPropiedadesUsuario(usuarioActual, beanUsuario);
        beanUsuario.setIsLoggedIn(true);

        // Place authorized user on session to disable security filter
        //JSFUtil.storeOnSession(FacesContext.getCurrentInstance(), AUTH_USER, "Authorized_User");
        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        beanUsuario.setDatosBasicos(false);
        beanUsuario.setConfiguracion(false);
        comp = buscarCompañia();
        beanUsuario.setIpHost(comp.getIp());
        
        
        beanUsuario.setInvitacion(beanUsuario.getPuerto());  

        // ------------------------------------------------------------------------------
        // System.out.println("reporte = " + beanUsuario.invitacion);
        // Chequea el caso de que el usuario  sea un SuperUsuario - usuario de SoftwareExpress - o que sea usuario de software express. 
        // En caso de que asi sea, le resta uno al contador de visitas. No deben contabilizarse las visitas de la compañia.
        String superUsuario = "Super";
        String softwareExpresSus = "100";
        if (beanUsuario.getPerUsuario().equals(superUsuario) | sus.getNumSuscripcion().equals(softwareExpresSus)) {
            descontarNroVisitas();
        }
        // Chequea el caso de que el usuario  se un SuperUsuario - usuario de SoftwareExpress - y lo envía a Menú principal
        if (beanUsuario.getPerUsuario().equals(superUsuario)) {
            beanUsuario.setOperaciones(true);
            Integer contador = comp.getContadorVisitas();
            beanUsuario.setContadorVisitas(contador);
            return "menu";
        }
        //
        String admin = "Admin";
        if (!beanUsuario.getPerUsuario().equals(admin)) {
            beanUsuario.setIrACrearCondominio(true);
        } else {
            beanUsuario.setIrACrearCondominio(false);
        }

        return "selcond";

    }

    public void descontarNroVisitas() {

        Compañia comp = buscarCompañia();
        Integer contador = comp.getContadorVisitas();
        contador = contador - 1;
        comp.setContadorVisitas(contador);
        actualizarNumVisitas(comp);
    }

    public Compañia buscarCompañia() {

        Compañia compañia;
        String nombQuery = "buscarCompañia";
        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();
        try {
            compañia = dataAcces.buscarEntidad(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return compañia;
    }

    public void actualizarNumVisitas(Compañia comp) {

        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();

        try {
            dataAcces.almacenarOModificarEntidad(comp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public Suscripcion buscarSuscripcion(Integer idUsuario) {

        String nombQuery = "buscarSuscripcionPorIdUsuario";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            sus = dataAcces.buscarEntidadPorId(idUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return sus;
    }

    public static void copiarPropiedadesUsuario(Usuario usuarioActual, BeanUsuario beanUsuario) {

        beanUsuario.setCodUsuario(usuarioActual.getCodUsuario());
        beanUsuario.setNombUsuario(usuarioActual.getNombUsuario());
        beanUsuario.setPerUsuario(usuarioActual.getPerUsuario());
        beanUsuario.setIdUsuario(usuarioActual.getId());
        beanUsuario.setCondominioActual(null);
        beanUsuario.setPropiedadActual(null);

        /**
         * beanUsuario.setIsLoggedIn(usuarioActual.isLoggedIn());
         */
    }

    public String logout() throws IOException {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
        HttpSession session = (HttpSession) ectx.getSession(false);
        session.invalidate();

        FacesContext ctx = FacesContext.getCurrentInstance();
        Application app = ctx.getApplication();
        app.getNavigationHandler().handleNavigation(ctx, "/welcome.jsp", "welcome");

        // To avoid using the navigation handler you could also use...
        //response.sendRedirect("../index.jsp");
        return null;

    }

    public void setCodUsuario(HtmlInputText codUsuario) {
        this.codUsuario = codUsuario;
    }

    public HtmlInputText getCodUsuario() {
        return codUsuario;
    }

    public void setContUsuario(HtmlInputSecret contUsuario) {
        this.contUsuario = contUsuario;
    }

    public HtmlInputSecret getContUsuario() {
        return contUsuario;
    }
}
