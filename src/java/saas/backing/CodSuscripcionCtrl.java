/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

import saas.modelos.Suscripcion;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import saas.modelos.SuscripcionDT;
import util.DaoQuery;

/**
 *
 * @author jesus
 */
public class CodSuscripcionCtrl implements Serializable {

    private String numSuscripcion;

    public CodSuscripcionCtrl() {
    }

    public String salir() {

        return "salir";

    }

    public String buscarSuscripcion() {

        Suscripcion susc = null;
        String mensaje = null;

        String nombQuery = "buscarSuscripcionPlan";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            susc = dataAcces.buscarEntidad(numSuscripcion, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mensaje;
        }
        if (susc == null) {
            // suscripcion no existe
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Suscripción no ha sido registrada!, por favor intente de nuevo"));
            return mensaje;
        }

        SuscripcionDT suscripcionDT = new SuscripcionDT();

        suscripcionDT.setNombSuscriptor(susc.getNombSuscriptor());
        suscripcionDT.setApelSuscriptor(susc.getApelSuscriptor());
        suscripcionDT.setNumCedSuscriptor(susc.getNumCedSuscriptor());
        suscripcionDT.setEmailSuscriptor(susc.getEmailSuscriptor());
        suscripcionDT.setTelSuscriptor(susc.getTelSuscriptor());
        suscripcionDT.setCelSuscriptor(susc.getCelSuscriptor());
        suscripcionDT.setEdoSuscripcion(susc.getEdoSuscripcion());
        suscripcionDT.setDiaCorteSuscripcion(susc.getDiaCorteSuscripcion());
       
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("SuscripcionDT", suscripcionDT);
        ctx.getExternalContext().getSessionMap().put("Suscripcion", susc);

        return "suscripcion";
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }
}
