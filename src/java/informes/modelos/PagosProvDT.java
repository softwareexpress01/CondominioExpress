/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import java.io.Serializable;

/**
 *
 * @author FrankRH
 */
public class PagosProvDT implements Serializable {

    public Integer id;
    public String nombProveedor;
    private String fecFac;
    private String numFac;
    private String concepto;
    private String numDoc;
    private String nombBanco;
    private String fecMovPago;
    private Double mtoFac;
    private Double mtoPago;
    private Double saldoFac;
    public Integer idFac;
    private String disc;
    /* Asociaciones */

    public PagosProvDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }

    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setFecFac(String fecFac) {
        this.fecFac = fecFac;
    }

    public String getFecFac() {
        return fecFac;
    }

    public void setNumFac(String numFac) {
        this.numFac = numFac;
    }

    public String getNumFac() {
        return numFac;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNombBanco(String nombBanco) {
        this.nombBanco = nombBanco;
    }

    public String getNombBanco() {
        return nombBanco;
    }

    public void setFecMovPago(String fecMovPago) {
        this.fecMovPago = fecMovPago;
    }

    public String getFecMovPago() {
        return fecMovPago;
    }

    public void setMtoFac(Double mtoFac) {
        this.mtoFac = mtoFac;
    }

    public Double getMtoFac() {
        return mtoFac;
    }

    public void setMtoPago(Double mtoPago) {
        this.mtoPago = mtoPago;
    }

    public Double getMtoPago() {
        return mtoPago;
    }

    public void setSaldoFac(Double saldoFac) {
        this.saldoFac = saldoFac;
    }

    public Double getSaldoFac() {
        return saldoFac;
    }

    public void setIdFac(Integer idFac) {
        this.idFac = idFac;
    }

    public Integer getIdFac() {
        return idFac;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getDisc() {
        return disc;
    }
}
