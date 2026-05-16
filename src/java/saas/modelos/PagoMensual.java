/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.modelos;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author Frank
 */
public class PagoMensual implements Serializable {

    private Integer id;
    public String bcoPagoMensual;
    public String ctaPagoMensual;
    public Calendar fecPagoMensual;
    private String refPagoMensual;
    private Float mtoPagoMensual;
    public Calendar fecMovPagoMensual;
    private String edoPagoMensual;
    private String edoAntSuscripcion;
    private String obsPagoMensual;
    /* Asociaciones */
    private Suscripcion suscripcion;

    public PagoMensual() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setBcoPagoMensual(String bcoPagoMensual) {
        this.bcoPagoMensual = bcoPagoMensual;
    }

    public String getBcoPagoMensual() {
        return bcoPagoMensual;
    }

    public void setCtaPagoMensual(String ctaPagoMensual) {
        this.ctaPagoMensual = ctaPagoMensual;
    }

    public String getCtaPagoMensual() {
        return ctaPagoMensual;
    }

    public void setFecPagoMensual(Calendar fecPagoMensual) {
        this.fecPagoMensual = fecPagoMensual;
    }

    public Calendar getFecPagoMensual() {
        return fecPagoMensual;
    }

    public void setRefPagoMensual(String refPagoMensual) {
        this.refPagoMensual = refPagoMensual;
    }

    public String getRefPagoMensual() {
        return refPagoMensual;
    }

    public void setMtoPagoMensual(Float mtoPagoMensual) {
        this.mtoPagoMensual = mtoPagoMensual;
    }

    public Float getMtoPagoMensual() {
        return mtoPagoMensual;
    }

    public void setFecMovPagoMensual(Calendar fecMovPagoMensual) {
        this.fecMovPagoMensual = fecMovPagoMensual;
    }

    public Calendar getFecMovPagoMensual() {
        return fecMovPagoMensual;
    }

    public void setEdoPagoMensual(String edoPagoMensual) {
        this.edoPagoMensual = edoPagoMensual;
    }

    public String getEdoPagoMensual() {
        return edoPagoMensual;
    }

    public void setEdoAntSuscripcion(String edoAntSuscripcion) {
        this.edoAntSuscripcion = edoAntSuscripcion;
    }

    public String getEdoAntSuscripcion() {
        return edoAntSuscripcion;
    }

    public void setObsPagoMensual(String obsPagoMensual) {
        this.obsPagoMensual = obsPagoMensual;
    }

    public String getObsPagoMensual() {
        return obsPagoMensual;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }
}
