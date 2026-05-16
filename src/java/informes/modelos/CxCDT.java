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
public class CxCDT implements Serializable {

    public Integer id;
    
    
    private Double mtoFac;
    
    
    /* Asociaciones */
    
   
    public CxCDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMtoFac(Double mtoFac) {
        this.mtoFac = mtoFac;
    }

    public Double getMtoFac() {
        return mtoFac;
    }
 
}
