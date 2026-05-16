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
public class CxPProveedorDT implements Serializable {

    public Integer id;
    
    public  String nombProveedor;
    private String fecFac;
    private String numDoc;
    private String concepto;
    private String disc;
    private Double mtoFac;
    private Double saldoFac;
    
    /* Asociaciones */
    
   
    public CxPProveedorDT() {
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

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getConcepto() {
        return concepto;
    }
    
    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getDisc() {
        return disc;
    }

    public void setMtoFac(Double mtoFac) {
        this.mtoFac = mtoFac;
    }

    public Double getMtoFac() {
        return mtoFac;
    }

    public void setSaldoFac(Double saldoFac) {
        this.saldoFac = saldoFac;
    }

    public Double getSaldoFac() {
        return saldoFac;
    }
    
}
