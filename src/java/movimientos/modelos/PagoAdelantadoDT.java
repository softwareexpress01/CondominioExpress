/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
/**
 *
 * @author FrankRH
 */
public class PagoAdelantadoDT implements Serializable {

    public Integer id;
    /*private Integer idInPagoAdelantado;
    private String codInmueblePagoAdelantado;
    private String nombPropPagoAdelantado;*/
    private String descPagoAdelantado;
    private String fecPagoAdelantado;
    private String mtoPagoAdelantado;
    private String formaPagoAdelantado;
    private String refPagoAdelantado;
    private Integer idBcoPagoAdelantado;
    private Integer idCuentaPagoAdelantado;
    private String nombBcoPagoAdelantado;
    private String descCuentaPagoAdelantado;
    private String saldoPagoAdelantado;
    private String estadoPagoAdelantado;
    private String fecMovPagoAdelantado;
    
  
    /* Asociaciones */
    
    private Inmueble inmueble;

    public PagoAdelantadoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public void setMtoPagoAdelantado(String mtoPagoAdelantado) {
        this.mtoPagoAdelantado = mtoPagoAdelantado;
    }

    public String getMtoPagoAdelantado() {
        return mtoPagoAdelantado;
    }

    public void setFormaPagoAdelantado(String formaPagoAdelantado) {
        this.formaPagoAdelantado = formaPagoAdelantado;
    }

    public String getFormaPagoAdelantado() {
        return formaPagoAdelantado;
    }

    public void setRefPagoAdelantado(String refPagoAdelantado ) {
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
    
    public void setNombBcoPagoAdelantado(String nombBcoPagoAdelantado) {
        this.nombBcoPagoAdelantado = nombBcoPagoAdelantado;
    }

    public String getNombBcoPagoAdelantado() {
        return nombBcoPagoAdelantado;
    }
    
    public void setDescCuentaPagoAdelantado(String descCuentaPagoAdelantado) {
        this.descCuentaPagoAdelantado = descCuentaPagoAdelantado;
    }

    public String getDescCuentaPagoAdelantado() {
        return descCuentaPagoAdelantado;
    }
    
    public void setSaldoPagoAdelantado(String saldoPagoAdelantado) {
        this.saldoPagoAdelantado = saldoPagoAdelantado;
    }

    public String getSaldoPagoAdelantado() {
        return saldoPagoAdelantado;
    }
    
    public void setEstadoPagoAdelantado(String estadoPagoAdelantado) {
        this.estadoPagoAdelantado = estadoPagoAdelantado;
    }

    public String getEstadoPagoAdelantado() {
        return estadoPagoAdelantado;
    }
    
    public void setFecMovPagoAdelantado(String fecMovPagoAdelantado) {
        this.fecMovPagoAdelantado = fecMovPagoAdelantado;
    }

    public String getFecMovPagoAdelantado() {
        return fecMovPagoAdelantado;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
