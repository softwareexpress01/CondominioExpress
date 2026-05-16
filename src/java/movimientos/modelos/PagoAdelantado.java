/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author FrankRH
 */
public class PagoAdelantado implements Serializable {

    public Integer id;
    private Integer idInPagoAdelantado;
    private String descPagoAdelantado;
    private String fecPagoAdelantado;
    private Double mtoPagoAdelantado;
    private String formaPagoAdelantado;
    private String refPagoAdelantado;
    private Integer idBcoPagoAdelantado;
    private Integer idCuentaPagoAdelantado;
    private Double saldoPagoAdelantado;
    public String estadoPagoAdelantado;
    public Calendar fecMovPagoAdelantado;
    /* Asociaciones */
    private Inmueble inmueble;

    public PagoAdelantado() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdInPagoAdelantado(Integer idInPagoAdelantado) {
        this.idInPagoAdelantado = idInPagoAdelantado;
    }

    public Integer getIdInPagoAdelantado() {
        return idInPagoAdelantado;
    }

    public void setDescPagoAdelantado(String descPagoAdelantado) {
        this.descPagoAdelantado = descPagoAdelantado;
    }

    public String getDescPagoAdelantado() {
        return descPagoAdelantado;
    }

    public void setFecPagoAdelantado(String fecPagoAdelantado) {
        this.fecPagoAdelantado = fecPagoAdelantado;
    }

    public String getFecPagoAdelantado() {
        return fecPagoAdelantado;
    }

    public void setMtoPagoAdelantado(Double mtoPagoAdelantado) {
        this.mtoPagoAdelantado = mtoPagoAdelantado;
    }

    public Double getMtoPagoAdelantado() {
        return mtoPagoAdelantado;
    }

    public void setFormaPagoAdelantado(String formaPagoAdelantado) {
        this.formaPagoAdelantado = formaPagoAdelantado;
    }

    public String getFormaPagoAdelantado() {
        return formaPagoAdelantado;
    }

    public void setRefPagoAdelantado(String refPagoAdelantado) {
        this.refPagoAdelantado = refPagoAdelantado;
    }

    public String getRefPagoAdelantado() {
        return refPagoAdelantado;
    }

    public void setIdBcoPagoAdelantado(Integer idBcoPagoAdelantado) {
        this.idBcoPagoAdelantado = idBcoPagoAdelantado;
    }

    public Integer getIdBcoPagoAdelantado() {
        return idBcoPagoAdelantado;
    }
    
    public void setIdCuentaPagoAdelantado(Integer idCuentaPagoAdelantado) {
        this.idCuentaPagoAdelantado = idCuentaPagoAdelantado;
    }

    public Integer getIdCuentaPagoAdelantado() {
        return idCuentaPagoAdelantado;
    }

    public void setSaldoPagoAdelantado(Double saldoPagoAdelantado) {
        this.saldoPagoAdelantado = saldoPagoAdelantado;
    }

    public Double getSaldoPagoAdelantado() {
        return saldoPagoAdelantado;
    }

    public void setEstadoPagoAdelantado(String estadoPagoAdelantado) {
        this.estadoPagoAdelantado = estadoPagoAdelantado;
    }

    public String getEstadoPagoAdelantado() {
        return estadoPagoAdelantado;
    }

    public void setFecMovPagoAdelantado(Calendar fecMovPagoAdelantado) {
        this.fecMovPagoAdelantado = fecMovPagoAdelantado;
    }

    public Calendar getFecMovPagoAdelantado() {
        return fecMovPagoAdelantado;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
