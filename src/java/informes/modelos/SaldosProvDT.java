/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author Frank R.H.
 */
public class SaldosProvDT implements Serializable {

    public Integer id;
    private String nombProv;
    public Double saldoProv;
    
    /* Asociaciones */
    
    
    public SaldosProvDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombProv(String nombProv) {
        this.nombProv = nombProv;
    }

    public String getNombProv() {
        return nombProv;
    }
   
     public void setSaldoProv(Double saldoProv){
        this.saldoProv = saldoProv;
    }
    public Double getSaldoProv() {
        return saldoProv;
    }
   
}
