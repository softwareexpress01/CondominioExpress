/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.util.Calendar;
/**
 *
 * @author FrankRH
 */
public class DeudaPendCxP implements Serializable {

    public Integer id;
    private Calendar fecMovDeudaPendCxP;
    public  Integer idProvDeudaPendCxP;
    private String refDeudaPendCxP;
    private String descDeudaPendCxP;
    private String fecDeudaPendCxP;
    private Double mtoDeudaPendCxP;
    private Double saldoDeudaPendCxP;
    public String estatusDeudaPendCxP;
    
    /* Asociaciones */
    
    private Proveedor proveedor;

    public DeudaPendCxP() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecMovDeudaPendCxP(Calendar fecMovDeudaPendCxP) {
        this.fecMovDeudaPendCxP = fecMovDeudaPendCxP;
    }

    public Calendar getFecMovDeudaPendCxP() {
        return fecMovDeudaPendCxP;
    }
    public void setIdProvDeudaPendCxP(Integer idProvDeudaPendCxP) {
        this.idProvDeudaPendCxP = idProvDeudaPendCxP;
    }

    public Integer getIdProvDeudaPendCxP() {
        return idProvDeudaPendCxP;
    }

    public void setRefDeudaPendCxP(String refDeudaPendCxP) {
        this.refDeudaPendCxP = refDeudaPendCxP;
    }

    public String getRefDeudaPendCxP() {
        return refDeudaPendCxP;
    }

    public void setDescDeudaPendCxP(String descDeudaPendCxP) {
        this.descDeudaPendCxP = descDeudaPendCxP;
    }

    public String getDescDeudaPendCxP() {
        return descDeudaPendCxP;
    }

    public void setFecDeudaPendCxP(String fecDeudaPendCxP) {
        this.fecDeudaPendCxP = fecDeudaPendCxP;
    }

    public String getFecDeudaPendCxP() {
        return fecDeudaPendCxP;
    }

    public void setMtoDeudaPendCxP(Double mtoDeudaPendCxP) {
        this.mtoDeudaPendCxP = mtoDeudaPendCxP;
    }

    public Double getMtoDeudaPendCxP() {
        return mtoDeudaPendCxP;
    }

    public void setSaldoDeudaPendCxP(Double saldoDeudaPendCxP) {
        this.saldoDeudaPendCxP = saldoDeudaPendCxP;
    }

    public Double getSaldoDeudaPendCxP() {
        return saldoDeudaPendCxP;
    }
    public void setEstatusDeudaPendCxP(String estatusDeudaPendCxP ) {
        this.estatusDeudaPendCxP = estatusDeudaPendCxP;
    }

    public String getEstatusDeudaPendCxP() {
        return estatusDeudaPendCxP;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }
}
