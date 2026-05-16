/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

import saas.modelos.Suscripcion;
import saas.modelos.Plan;
import datosbasicos.modelos.Propiedad;
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
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import saas.modelos.Compañia;

public class SolSuscripcionCtrl extends DaoCrud<Suscripcion, Propiedad, Integer> implements Serializable {

    private String nombSuscriptor;
    private String apelSuscriptor;
    private String cedSuscriptor;
    private String emailSuscriptor;
    private String codTelSuscriptor;
    private String numTelSuscriptor;
    private String codCelSuscriptor;
    private String numCelSuscriptor;
    private String fecMovSol;
    private String bcoSolSuscripcion;
    private String ctaSolSuscripcion;
    private String refSolSuscripcion;
    private String fecPagoSolSuscripcion;
    private String mtoSolSuscripcion;
    private String idPlanSolSuscripcion;
    private Plan plan;

    public SolSuscripcionCtrl() {
    }

    public void inicializarPanel() {

        this.setNombSuscriptor(null);
        this.setApelSuscriptor(null);
        this.setCedSuscriptor(null);
        this.setEmailSuscriptor(null);
        this.setCodTelSuscriptor(null);
        this.setNumTelSuscriptor(null);
        this.setCodCelSuscriptor(null);
        this.setNumCelSuscriptor(null);
        this.setBcoSolSuscripcion(null);
        this.setCtaSolSuscripcion(null);
        this.setFecPagoSolSuscripcion(null);
        this.setRefSolSuscripcion(null);
        this.setMtoSolSuscripcion(null);
        this.setIdPlanSolSuscripcion(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "datosbasicos";
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (this.getNombSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del Suscriptor...";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(this.getNombSuscriptor())) {
            mensaje = "El nombre del Suscriptor debe ser alfanumérico. Por favor intente de nuevo.";
            return mensaje;
        }
        if (this.getApelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el apellido del Suscriptor...";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(this.getApelSuscriptor())) {
            mensaje = "El nombre del Suscriptor debe ser alfanumérico. Por favor intente de nuevo.";
            return mensaje;
        }

        if (this.getCedSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la cédula o RIF del suscriptor...";
            return mensaje;
        }

        if (!val.esCedRif(this.getCedSuscriptor())) {
            mensaje = "La cédula debe comenzar con V o E, el Rif con J o G, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getEmailSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el correo electrónico del Suscriptor";
            return mensaje;
        }
        if (!val.esEmail(this.getEmailSuscriptor())) {
            mensaje = "El formato del correo es inválido, por favor intente de nuevo";
            return mensaje;
        }

        if (this.getCodTelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de área de su número de teléfono fijo.";
            return mensaje;
        }
        if (!val.esEntero(this.getCodTelSuscriptor())) {
            mensaje = "El código de area de su teléfono fijo debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodTelSuscriptor())) {
            mensaje = "El código de área de su teléfono fijo debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumTelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono fijo.";
            return mensaje;
        }
        if (!val.esEntero(this.getNumTelSuscriptor())) {
            mensaje = "El número de su teléfono fijo debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getCodCelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de operadora de su número de teléfono celular.";
            return mensaje;
        }
        if (!val.esEntero(this.getCodCelSuscriptor())) {
            mensaje = "El código de operadora de su teléfono celular debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodCelSuscriptor())) {
            mensaje = "El código de operadora de su teléfono celular debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumCelSuscriptor().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono celular.";
            return mensaje;
        }
        if (!val.esEntero(this.getNumCelSuscriptor())) {
            mensaje = "El número de su teléfono celular debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(bcoSolSuscripcion)) {
            mensaje = " Aun no ha seleccionado el Banco, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(ctaSolSuscripcion)) {
            mensaje = " Aun no ha seleccionado la Cuenta, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esLong(refSolSuscripcion)) {
            mensaje = "El Número de Referencia no es numérico entero";
            return mensaje;
        }
        
        if (this.getMtoSolSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto del pago ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoSolSuscripcion())) {
            mensaje = "El monto debe ser un valor numérico mayor que cero, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFecPagoSolSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento de  pago...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecPagoSolSuscripcion())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        Integer idPlan = Integer.parseInt(idPlanSolSuscripcion);
        if (!(idPlan > 0)) {
            mensaje = " Aun no ha seleccionado un Plan de Suscripción, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idPlanSolSuscripcion)) {
            mensaje = " Aun no ha seleccionado un Plan de Suscripción, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public void registrarSolSuscripcion() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        String guion = "-";
        Suscripcion SuscripcionNueva = new Suscripcion();

        SuscripcionNueva.setNombSuscriptor(nombSuscriptor);
        SuscripcionNueva.setApelSuscriptor(apelSuscriptor);
        SuscripcionNueva.setNumCedSuscriptor(cedSuscriptor);
        SuscripcionNueva.setEmailSuscriptor(emailSuscriptor);
        SuscripcionNueva.setTelSuscriptor(codTelSuscriptor.concat(guion.concat(numTelSuscriptor)));
        SuscripcionNueva.setCelSuscriptor(codCelSuscriptor.concat(guion.concat(numCelSuscriptor)));
        SuscripcionNueva.setBcoSolSuscripcion(bcoSolSuscripcion);
        SuscripcionNueva.setNumCtaSolSuscripcion(ctaSolSuscripcion);
        SuscripcionNueva.setRefSolSuscripcion(refSolSuscripcion);
        Calendar fecha = stringACalendar(fecPagoSolSuscripcion);
        SuscripcionNueva.setFecPagoSolSuscripcion(fecha);
        Float mto = Float.valueOf(mtoSolSuscripcion).floatValue();
        SuscripcionNueva.setMtoSolSuscripcion(mto);

        Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
        SuscripcionNueva.setFechaMovSol(fecCal);
        SuscripcionNueva.setEdoSolSuscripcion("PEND");
        SuscripcionNueva.setObsSolSuscripcion("");
        SuscripcionNueva.setNumSuscripcion("");
        SuscripcionNueva.setEdoSuscripcion("INA");
        SuscripcionNueva.setContSuscripcion("");
        SuscripcionNueva.setDiaCorteSuscripcion("");
        // Se coloca el saldo de la suscripcion en cero y la fecha de la misma igual a la fecha del dia para evitar valores null
        SuscripcionNueva.setSaldoSuscripcion(0.0f);
        SuscripcionNueva.setFecSuscripcion(fecCal);

        Integer idPlan = Integer.parseInt(idPlanSolSuscripcion);

        String nombQuery = "buscarPlanPorId";
        DaoQuery<Plan> dataAcces = new DaoQuery<Plan>();
        try {
            plan = dataAcces.buscarEntidadPorId(idPlan, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        if (!(SuscripcionNueva.getMtoSolSuscripcion() >= plan.getMtoPlan())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El monto del pago es menor que el que corresponde al Plan seleccionado. Por favor intente de nuevo."));
            return;
        }
        SuscripcionNueva.setPlan(plan);
        Compañia com = buscarCompañia();
        SuscripcionNueva.setCompañia(com);
        
        Integer idSuscrip;
        DaoQuery<Suscripcion> dataAcces1 = new DaoQuery<Suscripcion>();
        try {
            idSuscrip = dataAcces1.almacenarEntidad(SuscripcionNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Solicitud de Suscripción de " + SuscripcionNueva.getNombSuscriptor() + " " + SuscripcionNueva.getApelSuscriptor() + " ha sido registrada satisfactoriamente!"));
        return;
    }

    public Collection getPlanes() {

        Collection listPlanes = new ArrayList();
        listPlanes.add(new SelectItem(new Integer(0), "Seleccione"));

        Collection<Plan> listaPlan = new ArrayList<Plan>();
        String nombQuery = "buscarPlanes";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPlan = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (listaPlan.isEmpty()) {

            listPlanes.clear();
            listPlanes.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;

            return listPlanes;

        }

        /*---------------------------------------------------------------------------------*/
        for (Iterator<Plan> it = listaPlan.iterator();
                it.hasNext();) {

            Plan plan = (Plan) it.next();

            listPlanes.add(new SelectItem(plan.getId(), plan.getNombPlan()));
        }

        return listPlanes;
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

    public void setNombSuscriptor(String nombSuscriptor) {
        this.nombSuscriptor = nombSuscriptor;
    }

    public String getNombSuscriptor() {
        return nombSuscriptor;
    }

    public void setApelSuscriptor(String apelSuscriptor) {
        this.apelSuscriptor = apelSuscriptor;
    }

    public String getApelSuscriptor() {
        return apelSuscriptor;
    }

    public void setCedSuscriptor(String cedSuscriptor) {
        this.cedSuscriptor = cedSuscriptor;
    }

    public String getCedSuscriptor() {
        return cedSuscriptor;
    }

    public void setEmailSuscriptor(String emailSuscriptor) {
        this.emailSuscriptor = emailSuscriptor;
    }

    public String getEmailSuscriptor() {
        return emailSuscriptor;
    }

    public void setCodTelSuscriptor(String codTelSuscriptor) {
        this.codTelSuscriptor = codTelSuscriptor;
    }

    public String getCodTelSuscriptor() {
        return codTelSuscriptor;
    }

    public void setNumTelSuscriptor(String numTelSuscriptor) {
        this.numTelSuscriptor = numTelSuscriptor;
    }

    public String getNumTelSuscriptor() {
        return numTelSuscriptor;
    }

    public void setCodCelSuscriptor(String codCelSuscriptor) {
        this.codCelSuscriptor = codCelSuscriptor;
    }

    public String getCodCelSuscriptor() {
        return codCelSuscriptor;
    }

    public void setNumCelSuscriptor(String numCelSuscriptor) {
        this.numCelSuscriptor = numCelSuscriptor;
    }

    public String getNumCelSuscriptor() {
        return numCelSuscriptor;
    }

    public void setBcoSolSuscripcion(String bcoSolSuscripcion) {
        this.bcoSolSuscripcion = bcoSolSuscripcion;
    }

    public String getBcoSolSuscripcion() {
        return bcoSolSuscripcion;
    }

    public void setCtaSolSuscripcion(String ctaSolSuscripcion) {
        this.ctaSolSuscripcion = ctaSolSuscripcion;
    }

    public String getCtaSolSuscripcion() {
        return ctaSolSuscripcion;
    }

    public void setRefSolSuscripcion(String refSolSuscripcion) {
        this.refSolSuscripcion = refSolSuscripcion;
    }

    public String getRefSolSuscripcion() {
        return refSolSuscripcion;
    }

    public void setFecPagoSolSuscripcion(String fecPagoSolSuscripcion) {
        this.fecPagoSolSuscripcion = fecPagoSolSuscripcion;
    }

    public String getFecPagoSolSuscripcion() {
        return fecPagoSolSuscripcion;
    }

    public void setMtoSolSuscripcion(String mtoSolSuscripcion) {
        this.mtoSolSuscripcion = mtoSolSuscripcion;
    }

    public String getMtoSolSuscripcion() {
        return mtoSolSuscripcion;
    }

    public void setIdPlanSolSuscripcion(String idPlanSolSuscripcion) {
        this.idPlanSolSuscripcion = idPlanSolSuscripcion;
    }

    public String getIdPlanSolSuscripcion() {
        return idPlanSolSuscripcion;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan Plan() {
        return plan;
    }
}
