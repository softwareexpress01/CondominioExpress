package saas.backing;

import saas.modelos.Plan;
import datosbasicos.modelos.Proveedor;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import movimientos.modelos.PagoFonRes;
import movimientos.modelos.RevertirPagoFonResDT;
import org.hibernate.HibernateException;
import saas.modelos.Compañia;
import util.*;

public class RevertirConfSusCtrl extends DaoCrud<Suscripcion, Proveedor, Integer> implements Serializable {

    public String numCedSuscriptor;
    private String nombSuscriptor;
    private String apelSuscriptor;
    private String emailSuscriptor;
    private String telSuscriptor;
    private String celSuscriptor;
    private String fecSuscripcion;
    private String numSuscripcion;
    private String edoSuscripcion;
    private Collection<PagoFonRes> listaPagosFonRes = new ArrayList<PagoFonRes>();
    private Collection<RevertirPagoFonResDT> listaPagosFonResDT = new ArrayList<RevertirPagoFonResDT>();
    private SuscripcionDT filaSeleccionada;

    public RevertirConfSusCtrl() {
    }

    public void inicializarPanel() {

        this.setNumCedSuscriptor(null);
        this.setNombSuscriptor(null);
        this.setApelSuscriptor(null);
        this.setEmailSuscriptor(null);
        this.setTelSuscriptor(null);
        this.setCelSuscriptor(null);
        this.setFecSuscripcion(null);
        this.setNumSuscripcion(null);
        this.setEdoSuscripcion(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.revertir = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "datosbasicos";

    }

    public Collection getSuscripcionesConfirmadas() {

        Collection<Suscripcion> lista = new ArrayList<Suscripcion>();
        Collection<Suscripcion> listaOrd = new ArrayList<Suscripcion>();

        // Asignar fecha desde/hasta para el query

        Calendar fecDesdeCal = Calendar.getInstance();
        fecDesdeCal.set(Calendar.DAY_OF_MONTH, 1);

        Calendar fecHastaCal = Calendar.getInstance();
        fecHastaCal.set(Calendar.DAY_OF_MONTH, 28);

        String nombQuery = "buscarSuscripcionesConf";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesRango(fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return lista;
        }

        if (lista.isEmpty()) {

            return lista;

        }

        listaOrd = ordenarLista(lista);
        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Suscripcion> listaDesOrdenada = new ArrayList<Suscripcion>();

        for (Iterator<Suscripcion> it = lista.iterator();
                it.hasNext();) {
            Suscripcion suscrip = (Suscripcion) it.next();
            listaDesOrdenada.add(suscrip);
        }

        Collections.sort(listaDesOrdenada, new ComparadorSuscripcion());//devuelve la lista ordenada en listaDesOrdenada

        Collection<SuscripcionDT> coleccionOrdenada = new ArrayList<SuscripcionDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Suscripcion fdt = listaDesOrdenada.get(i);

            SuscripcionDT sDT = new SuscripcionDT();
            sDT.setId(fdt.getId());
            sDT.setNombSuscriptor(fdt.getNombSuscriptor());
            sDT.setApelSuscriptor(fdt.getApelSuscriptor());
            sDT.setNumCedSuscriptor(fdt.getNumCedSuscriptor());
            sDT.setEmailSuscriptor(fdt.getEmailSuscriptor());
            sDT.setTelSuscriptor(fdt.getTelSuscriptor());
            sDT.setCelSuscriptor(fdt.getCelSuscriptor());
            sDT.setFechaMovSol(fdt.getFechaMovSol());
            sDT.setFechaMovSolString(calendarAString(fdt.getFechaMovSol()));
            sDT.setEdoSolSuscripcion(fdt.getEdoSolSuscripcion());
            sDT.setBcoSolSuscripcion(fdt.getBcoSolSuscripcion());
            sDT.setNumCtaSolSuscripcion(fdt.getNumCtaSolSuscripcion());
            sDT.setRefSolSuscripcion(fdt.getRefSolSuscripcion());
            sDT.setMtoSolSuscripcion(fdt.getMtoSolSuscripcion());
            sDT.setFecPagoSolSuscripcion(fdt.getFecPagoSolSuscripcion());
            sDT.setNumSuscripcion(fdt.getNumSuscripcion());
            sDT.setFecSuscripcion(fdt.getFecSuscripcion());
            sDT.setFecSuscripcionString(calendarAString(fdt.getFecSuscripcion()));
            sDT.setEdoSuscripcion(fdt.getEdoSuscripcion());
            sDT.setPlan(fdt.getPlan());

            coleccionOrdenada.add(sDT);
        }
        return coleccionOrdenada;
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

    public void editarSuscripcionAction() {

        SuscripcionDT suscripcionSel = this.getFilaSeleccionada();

        this.setNumCedSuscriptor(suscripcionSel.getNumCedSuscriptor());
        this.setNombSuscriptor(suscripcionSel.getNombSuscriptor());
        this.setApelSuscriptor(suscripcionSel.getApelSuscriptor());
        this.setEmailSuscriptor(suscripcionSel.getEmailSuscriptor());
        this.setTelSuscriptor(suscripcionSel.getTelSuscriptor());
        this.setCelSuscriptor(suscripcionSel.getCelSuscriptor());
        this.setNumSuscripcion(suscripcionSel.getNumSuscripcion());
        this.setEdoSuscripcion(suscripcionSel.getEdoSuscripcion());

        this.setFecSuscripcion(calendarAString(suscripcionSel.getFecSuscripcion()));
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(suscripcionSel.getNumSuscripcion());
        beanUsuario.revertir = false;

        // Preparar objeto para Revertir

        Suscripcion suscripcionRev = new Suscripcion();

        suscripcionRev.setNombSuscriptor(suscripcionSel.getNombSuscriptor());
        suscripcionRev.setApelSuscriptor(suscripcionSel.getApelSuscriptor());
        suscripcionRev.setNumCedSuscriptor(suscripcionSel.getNumCedSuscriptor());
        suscripcionRev.setEmailSuscriptor(suscripcionSel.getEmailSuscriptor());
        suscripcionRev.setTelSuscriptor(suscripcionSel.getTelSuscriptor());
        suscripcionRev.setCelSuscriptor(suscripcionSel.getCelSuscriptor());
        suscripcionRev.setFechaMovSol(suscripcionSel.getFechaMovSol());
        suscripcionRev.setBcoSolSuscripcion(suscripcionSel.getBcoSolSuscripcion());
        suscripcionRev.setNumCtaSolSuscripcion(suscripcionSel.getNumCtaSolSuscripcion());
        suscripcionRev.setRefSolSuscripcion(suscripcionSel.getRefSolSuscripcion());
        suscripcionRev.setFecPagoSolSuscripcion(suscripcionSel.getFecPagoSolSuscripcion());
        suscripcionRev.setMtoSolSuscripcion(suscripcionSel.getMtoSolSuscripcion());

        suscripcionRev.setEdoSolSuscripcion("PEND");
        suscripcionRev.setObsSolSuscripcion("");
        suscripcionRev.setNumSuscripcion("");
        suscripcionRev.setEdoSuscripcion("INA");
        suscripcionRev.setContSuscripcion("");
        suscripcionRev.setDiaCorteSuscripcion("");
        // se coloca cero en el saldo de la suscripcion y la fecha del movimiento en la fecha de la  misma para evitar valores null
        suscripcionRev.setSaldoSuscripcion(0.0f);
        suscripcionRev.setFecSuscripcion(suscripcionSel.getFechaMovSol());

        Integer idPlan;
        idPlan = suscripcionSel.getPlan().getId();
        String nombQuery = "buscarPlanPorId";
        DaoQuery<Plan> dataAcces = new DaoQuery<Plan>();
        Plan plan = new Plan();
        try {
            plan = dataAcces.buscarEntidadPorId(idPlan, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        suscripcionRev.setId(suscripcionSel.getId());
        suscripcionRev.setPlan(plan);
        Compañia com = buscarCompañia();
        suscripcionRev.setCompañia(com);

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("Suscripcion", suscripcionRev);

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

    public void setNumCedSuscriptor(String numCedSuscriptor) {
        this.numCedSuscriptor = numCedSuscriptor;
    }

    public String getNumCedSuscriptor() {
        return numCedSuscriptor;
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

    public void setEmailSuscriptor(String emailSuscriptor) {
        this.emailSuscriptor = emailSuscriptor;
    }

    public String getEmailSuscriptor() {
        return emailSuscriptor;
    }

    public void setTelSuscriptor(String telSuscriptor) {
        this.telSuscriptor = telSuscriptor;
    }

    public String getTelSuscriptor() {
        return telSuscriptor;
    }

    public void setCelSuscriptor(String celSuscriptor) {
        this.celSuscriptor = celSuscriptor;
    }

    public String getCelSuscriptor() {
        return celSuscriptor;
    }

    public void setFecSuscripcion(String fecSuscripcion) {
        this.fecSuscripcion = fecSuscripcion;
    }

    public String getFecSuscripcion() {
        return fecSuscripcion;
    }

    public void setEdoSuscripcion(String edoSuscripcion) {
        this.edoSuscripcion = edoSuscripcion;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getEdoSuscripcion() {
        return edoSuscripcion;
    }

    public void setFilaSeleccionada(SuscripcionDT filaSeleccionada) {

        this.filaSeleccionada = filaSeleccionada;
    }

    public SuscripcionDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
