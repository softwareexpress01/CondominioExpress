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
public class CxCInmDT implements Serializable {

    public Integer id;
    
    private String codInm;
    private Double totCxC;
    private Double totGenCxC;
    
    
    /* Asociaciones */
    
   
    public CxCInmDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodInm(String codInm) {
        this.codInm = codInm;
    }

    public String getCodInm() {
        return codInm;
    }
    
    public void setTotCxC(Double totCxC) {
        this.totCxC = totCxC;
    }

    public Double getTotCxC() {
        return totCxC;
    }
    
    public void setTotGenCxC(Double totGenCxC) {
        this.totGenCxC = totGenCxC;
    }

    public Double getTotGenCxC() {
        return totGenCxC;
    }
 
}
 