/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Frank
 */
public class PlanDT implements Serializable {

    private Integer id;
    public String nombPlan;
    public String descPlan;
    private Float mtoPlan;
    private String mtoPlanString;
    private String obsPlan;
    public String fecCreacionPlan;
    public String fecModificacionPlan;
    private Integer nroMaxCondPlan;
    private Float mtoSusPlan;
    private String mtoSusPlanString;
    private String edoPlan;
    /* Asociaciones */
    public Collection<Suscripcion> suscripciones = new ArrayList<Suscripcion>();


    public PlanDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public void setMtoPlan(Float mtoPlan) {
        this.mtoPlan = mtoPlan;
    }

    public Float getMtoPlan() {
        return mtoPlan;
    }
    public void setMtoPlanString(String mtoPlanString) {
        this.mtoPlanString = mtoPlanString;
    }

    public String getMtoPlanString() {
        return mtoPlanString;
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

    public void setNroMaxCondPlan(Integer nroMaxCondPlan) {
        this.nroMaxCondPlan = nroMaxCondPlan;
    }

    public Integer getNroMaxCondPlan() {
        return nroMaxCondPlan;
    }
    
    public void setMtoSusPlan(Float mtoSusPlan) {
        this.mtoSusPlan = mtoSusPlan;
    }

    public Float getMtoSusPlan() {
        return mtoSusPlan;
    }
    public void setMtoSusPlanString(String mtoSusPlanString) {
        this.mtoSusPlanString = mtoSusPlanString;
    }

    public String getMtoSusPlanString() {
        return mtoSusPlanString;
    }
    public void setEdoPlan(String edoPlan) {
        this.edoPlan = edoPlan;
    }

    public String getEdoPlan() {
        return edoPlan;
    }

    public void setSuscripciones(Collection<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public Collection<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

 public void addSuscripciones(Suscripcion suscripcion) {
        this.suscripciones.add(suscripcion);
    }
}
