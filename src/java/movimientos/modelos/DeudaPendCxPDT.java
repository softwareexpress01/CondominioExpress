/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Proveedor;
import java.io.Serializable;

/**
 *
 * @author FrankRH
 */
public class DeudaPendCxPDT implements Serializable {

    public Integer id;
    private String fecMovDeudaPendCxP;
    private Integer idProvDeudaPendCxP;
    private String nombProvDeudaPendCxP;
    private String refDeudaPendCxP;
    private String descDeudaPendCxP;
    private String fecDeudaPendCxP;
    private Double mtoDeudaPendCxP;
    private Double saldoDeudaPendCxP;
    private String mtoDeudaPendCxPString;
    private String saldoDeudaPendCxPString;
    private String estatusDeudaPendCxP;
    /* Asociaciones */
    private Proveedor proveedor;

    public DeudaPendCxPDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecMovDeudaPendCxP(String fecMovDeudaPendCxP) {
        this.fecMovDeudaPendCxP = fecMovDeudaPendCxP;
    }

    public String getFecMovDeudaPendCxP() {
        return fecMovDeudaPendCxP;
    }

    public void setIdProvDeudaPendCxP(Integer idProvDeudaPendCxP) {
        this.idProvDeudaPendCxP = idProvDeudaPendCxP;
    }

    public Integer getIdProvDeudaPendCxP() {
        return idProvDeudaPendCxP;
    }

    public void setNombProvDeudaPendCxP(String nombProvDeudaPendCxP) {
        this.nombProvDeudaPendCxP = nombProvDeudaPendCxP;
    }

    public String getNombProvDeudaPendCxP() {
        return nombProvDeudaPendCxP;
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

    public void setMtoDeudaPendCxPString(String mtoDeudaPendCxPString) {
        this.mtoDeudaPendCxPString = mtoDeudaPendCxPString;
    }

    public String getMtoDeudaPendCxPString() {
        return mtoDeudaPendCxPString;
    }

    public void setSaldoDeudaPendCxP(Double saldoDeudaPendCxP) {
        this.saldoDeudaPendCxP = saldoDeudaPendCxP;
    }

    public Double getSaldoDeudaPendCxP() {
        return saldoDeudaPendCxP;
    }

    public void setSaldoDeudaPendCxPString(String saldoDeudaPendCxPString) {
        this.saldoDeudaPendCxPString = saldoDeudaPendCxPString;
    }

    public String getSaldoDeudaPendCxPString() {
        return saldoDeudaPendCxPString;
    }

    public void setEstatusDeudaPendCxP(String estatusDeudaPendCxP) {
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
