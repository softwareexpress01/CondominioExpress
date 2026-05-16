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
public class MovDelMesDT implements Serializable {

    public Integer id;
    
    public  String descMov;
    private String fecFacMov;
    private String numDocMov;
    private String conceptoMov;
    private String nombProveedorMov;
    private Double mtoMov;
    private Double saldoMov;
    private Double mtoFac;
    private Double saldoFac;
    private String edoMov;
    private String obsMov;
    /* Asociaciones */
    
   
    public MovDelMesDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDescMov(String descMov) {
        this.descMov = descMov;
    }

    public String getDescMov() {
        return descMov;
    }
    public void setFecFacMov(String fecFacMov) {
        this.fecFacMov = fecFacMov;
    }

    public String getFecFacMov() {
        return fecFacMov;
    }

    public void setNumDocMov(String numDocMov) {
        this.numDocMov = numDocMov;
    }

    public String getNumDocMov() {
        return numDocMov;
    }

    public void setConceptoMov(String conceptoMov) {
        this.conceptoMov = conceptoMov;
    }

    public String getConceptoMov() {
        return conceptoMov;
    }

    public void setNombProveedorMov(String nombProveedorMov) {
        this.nombProveedorMov = nombProveedorMov;
    }

    public String getNombProveedorMov() {
        return nombProveedorMov;
    }

    public void setMtoMov(Double mtoMov) {
        this.mtoMov = mtoMov;
    }

    public Double getMtoMov() {
        return mtoMov;
    }
    
    public void setSaldoMov(Double saldoMov) {
        this.saldoMov = saldoMov;
    }

    public Double getSaldoMov() {
        return saldoMov;
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
    public void setEdoMov(String edoMov) {
        this.edoMov = edoMov;
    }

    public String getEdoMov() {
        return edoMov;
    }
    
     public void setObsMov(String obsMov) {
        this.obsMov = obsMov;
    }

    public String getObsMov() {
        return obsMov;
    }
    
}
