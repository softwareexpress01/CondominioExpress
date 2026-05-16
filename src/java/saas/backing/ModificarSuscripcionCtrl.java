/**
 * Backing bean para registro xhtml
 */
package saas.backing;

/**
 *
 * @author jesus
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.hibernate.HibernateException;
import saas.modelos.Plan;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import util.*;

public class ModificarSuscripcionCtrl extends DaoCrud<Suscripcion, Plan, Integer> implements Serializable {

    private HtmlSelectOneMenu idPlan;
    private Suscripcion suscripcion;
    private SuscripcionDT suscripcionDT;
    private Collection<Plan> listaPlan = new ArrayList<Plan>();

    public ModificarSuscripcionCtrl() {
    }

    public String salir() {

        return "menup";
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //-----------------------------------------------------------------------------------
        if (suscripcionDT.getNombSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del suscriptor...";
            return mensaje;
        }
        if (!val.esNombApell(suscripcionDT.getNombSuscriptor())) {
            mensaje = "El nombre del suscriptor debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------   
        if (suscripcionDT.getApelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el apellido del suscriptor...";
            return mensaje;
        }
        if (!val.esNombApell(suscripcionDT.getApelSuscriptor())) {
            mensaje = "El apellido del suscriptor debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if (suscripcionDT.getNumCedSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el numero de cédula del suscriptor...";
            return mensaje;
        }
        if (!val.esCedRif(suscripcionDT.getNumCedSuscriptor())) {
            mensaje = "El número de cédula es inválido, por favor, intente de nuevo...";
            return mensaje;
        }
        //----------------------------------------------------------------------------------- 
        if (suscripcionDT.getEmailSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el correo electrónico del suscriptor...";
            return mensaje;
        }
        if (!val.esEmail(suscripcionDT.getEmailSuscriptor())) {
            mensaje = "El correo electrónico es inválido, por favor, intente de nuevo...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------------
        if (suscripcionDT.getTelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono del suscriptor...";
            return mensaje;
        }
        if (!val.esLong(suscripcionDT.getTelSuscriptor())) {
            mensaje = "El número de teléfono debe ser entero, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------

        if (suscripcionDT.getCelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del celular del suscriptor...";
            return mensaje;
        }
        if (!val.esLong(suscripcionDT.getCelSuscriptor())) {
            mensaje = "El número del celular debe ser entero, por favor, intente de nuevo...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------------
        if (suscripcionDT.getEdoSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el estado de la suscripción...";
            return mensaje;
        }
        if (!(suscripcionDT.getEdoSuscripcion().equals("ACT") | suscripcionDT.getEdoSuscripcion().equals("INA"))) {
            mensaje = "El estado de la suscripción debe ser ACT o INA, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if (suscripcionDT.getDiaCorteSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el dia de corte de la suscripción...";
            return mensaje;
        }
        if (!val.esEntero(suscripcionDT.getDiaCorteSuscripcion())) {
            mensaje = "El dia de corte debe ser un número entero, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if (suscripcionDT.idPlan.getValue().toString().equals("Seleccione")) {
            mensaje = "Por favor, seleccione un plan para la suscripción...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------------

        return mensaje;
    }

    public Collection getPlanes() {

        Collection listPlanes = new ArrayList();

        String nombQuery = "buscarPlanes";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPlan = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        suscripcionDT = (SuscripcionDT) ctx.getExternalContext().getSessionMap().get("SuscripcionDT");
        suscripcion = (Suscripcion) ctx.getExternalContext().getSessionMap().get("Suscripcion");

        if (listaPlan.isEmpty()) {

            listPlanes.clear();
            listPlanes.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;

            return listPlanes;

        }

        /*---------------------------------------------------------------------------------*/
        listPlanes.add(new SelectItem(suscripcion.getPlan().getId(), suscripcion.getPlan().getNombPlan()));

        for (Iterator<Plan> it = listaPlan.iterator();
                it.hasNext();) {
            Plan plan = (Plan) it.next();
            listPlanes.add(new SelectItem(plan.getId(), plan.getNombPlan()));
        }

        return listPlanes;
    }

    public void modificarSuscripcion() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }


        //-----------------------------------------------------------------------------------
        //Convertir de String a Double o Integer segun sea el caso antes de pasarlo a Parametro
        //-----------------------------------------------------------------------------------
        suscripcion.setNombSuscriptor(suscripcionDT.getNombSuscriptor());
        suscripcion.setApelSuscriptor(suscripcionDT.getApelSuscriptor());
        suscripcion.setNumCedSuscriptor(suscripcionDT.getNumCedSuscriptor());
        suscripcion.setEmailSuscriptor(suscripcionDT.getEmailSuscriptor());
        suscripcion.setTelSuscriptor(suscripcionDT.getTelSuscriptor());
        suscripcion.setCelSuscriptor(suscripcionDT.getCelSuscriptor());
        suscripcion.setEdoSuscripcion(suscripcionDT.getEdoSuscripcion());
        suscripcion.setDiaCorteSuscripcion(suscripcionDT.getDiaCorteSuscripcion());
        //-----------------------------------------------------------------------------------

        Integer id = Integer.parseInt(suscripcionDT.idPlan.getValue().toString());
        Plan plan = null;

        for (Iterator<Plan> it = listaPlan.iterator();
                it.hasNext();) {
            plan = (Plan) it.next();
            if (plan.getId().equals(id)) {
                break;
            }
        }

        suscripcion.setPlan(plan);

        try {
            almacenarOModificarEntidad(suscripcion);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La suscripcion número  " + suscripcion.getId() + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public void setIdPlan(HtmlSelectOneMenu id) {
        this.idPlan = id;
    }

    public HtmlSelectOneMenu getIdPlan() {
        return idPlan;
    }
}
