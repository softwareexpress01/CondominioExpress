/*-
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

import datosbasicos.modelos.Condominio;
import saas.modelos.Plan;
import saas.modelos.PlanDT;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.*;

public class PlanCtrl extends DaoCrud<Plan, Condominio, Integer> implements Serializable {

    private String nombPlan;
    private String descPlan;
    private String mtoPlan;
    private String obsPlan;
    private String fecCreacionPlan;
    private String fecModificacionPlan;
    private String nroMaxCondPlan;
    private String mtoSusPlan;
    private Collection listPlan = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection listPlanDT = new ArrayList();
    private boolean primerRequest = true;
    private Integer idPlanSel;
    private PlanDT filaSeleccionada;

    public PlanCtrl() {
    }

    public void inicializarPanel() {

        this.setNombPlan(null);
        this.setDescPlan(null);
        this.setMtoPlan(null);
        this.setObsPlan(null);
        this.setFecCreacionPlan(null);
        this.setFecModificacionPlan(null);
        this.setNroMaxCondPlan(null);
        this.setMtoPlan(null);
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        return "datosbasicos";

    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (!(nombPlan.length() > 0)) {
            mensaje = "El Nombre del Plan está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(nombPlan)) {
            mensaje = "El nombre del Plan no es válido, por favor intente de nuevo ";
            return mensaje;
        }

        if (!(descPlan.length() > 0)) {
            mensaje = "La Descripción está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(descPlan)) {
            mensaje = "La Descripción no es válida, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esMontoCero(mtoPlan)) {
            mensaje = "El Monto del Plan debe ser numérico. Use punto decimal si lo requiere. Por favor intente de nuevo";
            return mensaje;
        }

        Float mtoFloat = Float.valueOf(mtoPlan);

        if (mtoFloat < 0) {
            mensaje = "El Monto del Plan no puede ser negativo. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(fecCreacionPlan.length() > 0)) {
            mensaje = "La fecha de creación está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esFecha(fecCreacionPlan)) {
            mensaje = "La Fecha de Creación debe estar en el formato DD-MM-AAAA, por favor intente de nuevo ";
            return mensaje;
        }

        if (!(nroMaxCondPlan.length() > 0)) {
            mensaje = "El número máximo de Condominios está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        Integer nroCond = Integer.parseInt(nroMaxCondPlan);

        if (!(nroCond > 0)) {
            mensaje = "El número máximo de Condominios debe ser un valor númerico mayor que cero, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esMontoCero(mtoSusPlan)) {
            mensaje = "El Monto de Suscripción debe ser numérico. Use punto decimal si lo requiere. Por favor intente de nuevo";
            return mensaje;
        }

        mtoFloat = Float.valueOf(mtoSusPlan);

        if (mtoFloat < 0) {
            mensaje = "El Monto de Suscripción no puede ser negativo. Por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getPlanes() {

        listPlan.clear();
        listPlanDT.clear();

        String nombQuery = "buscarPlanes";
        idPlanSel = 1; // este valor será ignorado en el query. Debe traer todos los planes existentes con estado <> INA
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listPlan = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones... por favor, intente mas tarde"));
            return (null);
        }

        if (listPlan.isEmpty()) {

            return listPlanDT;
        } else {

            listPlanDT = inicializarPlanDT(listPlan);

            return listPlanDT;
        }

    }

    private Collection<PlanDT> inicializarPlanDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        for (Iterator<Plan> it = listaOrd.iterator();
                it.hasNext();) {

            Plan plan = (Plan) it.next();
            if (plan == null) {// Si no hay fONDOS DEFINIDOS devuelve la lista en blanco
                break;
            } else {

                PlanDT planDT = new PlanDT();

                planDT.setId(plan.getId());

                planDT.setNombPlan(plan.getNombPlan());
                planDT.setDescPlan(plan.getDescPlan());
                planDT.setMtoPlan(plan.getMtoPlan());
                planDT.setMtoPlanString(val.convertirFloatAString(plan.getMtoPlan()));
                planDT.setObsPlan(plan.getObsPlan());

                String fecha = calendarAString(plan.getFecCreacionPlan());
                planDT.setFecCreacionPlan(fecha);
                fecha = calendarAString(plan.getFecModificacionPlan());
                planDT.setFecModificacionPlan(fecha);
                planDT.setNroMaxCondPlan(plan.getNroMaxCondPlan());
                planDT.setMtoSusPlan(plan.getMtoSusPlan());
                planDT.setMtoSusPlanString(val.convertirFloatAString(plan.getMtoSusPlan()));

                listPlanDT.add(planDT);
            }


        }
        return listPlanDT;
    }

    public void editarPlanAction() {

        PlanDT planSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Propiedad" en el map #{Propiedad} 
        ctx.getExternalContext().getSessionMap().put("Plan", planSel);

        this.setNombPlan(planSel.getNombPlan());
        this.setDescPlan(planSel.getDescPlan());

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        this.setMtoPlan(val.convertirFloatAString(planSel.getMtoPlan()));
        this.setObsPlan(planSel.getObsPlan());
        this.setFecCreacionPlan(planSel.getFecCreacionPlan());
        this.setFecModificacionPlan(planSel.getFecModificacionPlan());
        this.setNroMaxCondPlan(planSel.getNroMaxCondPlan().toString());
        this.setMtoSusPlan(val.convertirFloatAString(planSel.getMtoSusPlan()));

        //----------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(planSel.getNombPlan());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idFondo = planSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Fondos, listaOrd
        Plan plan = null;

        for (Iterator<Plan> it = listPlan.iterator();
                it.hasNext();) {
            plan = (Plan) it.next();
            if (plan.getId().toString().equals(idFondo)) {
                //Almacena la fila del plan seleccionado ,el cual se buscara como "Plan" en el map #{Plan} 
                ctx.getExternalContext().getSessionMap().put("Plan", plan);
                break;
            }
        }

        ctx.getExternalContext().getSessionMap().put("Plan", plan);

        idPlanSel = plan.getId();

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = true;

    }

    public void registrarPlan() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Plan planNuevo = new Plan();

        planNuevo.setNombPlan(nombPlan);
        planNuevo.setDescPlan(descPlan);

        Float mto = Float.valueOf(mtoPlan).floatValue();
        planNuevo.setMtoPlan(mto);

        planNuevo.setObsPlan(obsPlan);
        Calendar fecCal = stringACalendar(fecCreacionPlan);
        planNuevo.setFecCreacionPlan(fecCal);
        planNuevo.setFecModificacionPlan(fecCal);
        planNuevo.setNroMaxCondPlan(Integer.parseInt(nroMaxCondPlan));

        mto = Float.valueOf(mtoSusPlan).floatValue();
        planNuevo.setMtoSusPlan(mto);

        planNuevo.setEdoPlan("ACT");


        try {
            almacenarOModificarEntidad(planNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones FONDO NUEVO...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Plan " + planNuevo.getDescPlan() + " ha sido registrado satisfactoriamente!"));

    }

    public void modificarPlan() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Plan planSel = (Plan) JSFUtil.getManagedObject("Plan");

        planSel.setNombPlan(nombPlan);
        planSel.setDescPlan(descPlan);

        //-----------------------------------------------------------        
        //                  Convertir de String a Float y mover
        //-----------------------------------------------------------
        Float mto = Float.valueOf(mtoPlan).floatValue();
        planSel.setMtoPlan(mto);

        planSel.setObsPlan(obsPlan);
        Calendar fecCal = stringACalendar(fecCreacionPlan);
        planSel.setFecCreacionPlan(fecCal);
        fecCal = Calendar.getInstance(TimeZone.getDefault());
        planSel.setFecModificacionPlan(fecCal);
        planSel.setNroMaxCondPlan(Integer.parseInt(nroMaxCondPlan));

        mto = Float.valueOf(mtoSusPlan).floatValue();
        planSel.setMtoSusPlan(mto);

        planSel.setEdoPlan("ACT");

        try {
            almacenarOModificarEntidad(planSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones (Modif) ...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Plan " + planSel.getDescPlan() + " ha sido modificado satisfactoriamente!"));

    }

    public String calendarAString(Calendar fecha) {

        Calendar StringFec = fecha;
        String perDia = "";
        String perMes = "";
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

    public Calendar stringACalendar(String fecha) throws ParseException {

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal;

    }

    public void setNombPlan(String nombPlan) {
        this.nombPlan = nombPlan;
    }

    public String getNombPlan() {
        return nombPlan;
    }

    public void setDescPlan(String descPlan) {
        this.descPlan = descPlan;
    }

    public String getDescPlan() {
        return descPlan;
    }

    public void setMtoPlan(String mtoPlan) {
        this.mtoPlan = mtoPlan;
    }

    public String getMtoPlan() {
        return mtoPlan;
    }

    public void setObsPlan(String obsPlan) {
        this.obsPlan = obsPlan;
    }

    public String getObsPlan() {
        return obsPlan;
    }

    public void setFecCreacionPlan(String fecCreacionPlan) {
        this.fecCreacionPlan = fecCreacionPlan;
    }

    public String getFecCreacionPlan() {
        return fecCreacionPlan;
    }

    public void setFecModificacionPlan(String fecModificacionPlan) {
        this.fecModificacionPlan = fecModificacionPlan;
    }

    public String getFecModificacionPlan() {
        return fecModificacionPlan;
    }

    public void setNroMaxCondPlan(String nroMaxCondPlan) {
        this.nroMaxCondPlan = nroMaxCondPlan;
    }

    public String getNroMaxCondPlan() {
        return nroMaxCondPlan;
    }

    public void setMtoSusPlan(String mtoSusPlan) {
        this.mtoSusPlan = mtoSusPlan;
    }

    public String getMtoSusPlan() {
        return mtoSusPlan;
    }

    public void setFilaSeleccionada(PlanDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PlanDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}