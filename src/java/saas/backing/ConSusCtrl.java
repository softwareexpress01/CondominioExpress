package saas.backing;

import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.TranFondo;
import org.hibernate.HibernateException;
import util.*;

public class ConSusCtrl extends DaoCrud<Suscripcion, TranFondo, Integer> implements Serializable {

    public String numCedSuscriptor;
    Suscripcion suscripcion = new Suscripcion();
    Usuario usuario = new Usuario();
    String mensaje = "";
    Collection<Suscripcion> lista = new ArrayList<Suscripcion>();

    public ConSusCtrl() {
    }

    public void inicializarPanel() {

        this.setNumCedSuscriptor(null);


    }

    public String validarPanel() {


        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (!val.esCedRif(this.getNumCedSuscriptor())) {
            mensaje = "La cédula debe comenzar con V o E, el Rif con J o G, seguido de un valor numérico. Por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public String salir() {

        return "salir";

    }

    public Collection getSuscripcionesRegistradas() {

        Collection<Suscripcion> listaOrd = new ArrayList<Suscripcion>();

        if (numCedSuscriptor == null) {
            return listaOrd;
        }
        if (numCedSuscriptor.length() == 0) {
            return listaOrd;
        }
        String nombQuery = "buscarSuscripcionesPorCedRif";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorCedRif(numCedSuscriptor, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
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
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

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
            sDT.setNumSuscripcion(fdt.getNumSuscripcion());
            sDT.setContSuscripcion(fdt.getContSuscripcion());
            sDT.setEdoSuscripcion(fdt.getEdoSuscripcion());
            sDT.setDiaCorteSuscripcion(fdt.getDiaCorteSuscripcion());
            sDT.setFecSuscripcionString(calendarAString(fdt.getFecSuscripcion()));
            sDT.setBcoSolSuscripcion(fdt.getBcoSolSuscripcion());
            sDT.setNumCtaSolSuscripcion(fdt.getNumCtaSolSuscripcion());
            sDT.setSaldoSuscripcion(fdt.getSaldoSuscripcion());
            sDT.setSaldoSuscripcionString(val.convertirFloatAString(fdt.getSaldoSuscripcion()));
            sDT.setRefSolSuscripcion(fdt.getRefSolSuscripcion());
            sDT.setMtoSolSuscripcion(fdt.getMtoSolSuscripcion());
            sDT.setFecPagoSolSuscripcion(fdt.getFecPagoSolSuscripcion());
            sDT.setPlanSuscripcion(fdt.getPlan().nombPlan);
            String numSus;
            sDT.setNombAdmin("");
            sDT.setCodAdmin("");
            sDT.setContAdmin("");
            if (!(fdt.getNumSuscripcion().length() == 0)) {
                numSus = fdt.getNumSuscripcion();
                buscarUsuarioPorSus(numSus);
                if (!(usuario == null)) {
                    sDT.setNombAdmin(usuario.getNombUsuario() + " " + usuario.getApelUsuario());
                    sDT.setCodAdmin(usuario.getCodUsuario());
                    sDT.setContAdmin(usuario.getContUsuario());
                }
            }

            coleccionOrdenada.add(sDT);
        }
        return coleccionOrdenada;
    }

    public void consultarSuscripcion() throws ValidatorException, ParseException {

        String vacio = new String();
        mensaje = "";
        mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));

        } else {
            if (lista.isEmpty()) {

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("No se encontraron suscripciones asociadas a la Ced/Rif indicada. Por favor, intente de nuevo"));
            }

        }
    }

    public String calendarAString(Calendar fecha) {

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

    public String buscarUsuarioPorSus(String numSus) {

        mensaje = "";
        suscripcion = null;
        usuario = null;
        String nombQuery = "buscarAdmin";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            suscripcion = dataAcces.buscarEntidad(numSus, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mensaje;
        }
        if (!(suscripcion == null)) {
            for (Iterator<Usuario> it = suscripcion.getUsuarios().iterator();
                    it.hasNext();) {
                usuario = (Usuario) it.next();

                break;
            }
        }

        return mensaje;
    }

    public void setNumCedSuscriptor(String numCedSuscriptor) {
        this.numCedSuscriptor = numCedSuscriptor;
    }

    public String getNumCedSuscriptor() {
        return numCedSuscriptor;
    }
}
