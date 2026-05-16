/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import datosbasicos.modelos.Banco;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
/**
 *
 * @author Frank R.H.
 */
public class BancoSaldoDT implements Serializable {

    public Integer id;
    private String nombBanco;
    public Double saldoBanco;
    
    /* Asociaciones */
    
    
    public BancoSaldoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombBanco(String nombBanco) {
        this.nombBanco = nombBanco;
    }

    public String getNombBanco() {
        return nombBanco;
    }
   
     public void setSaldoBanco(Double saldoBanco){
        this.saldoBanco = saldoBanco;
    }
    public Double getSaldoBanco() {
        return saldoBanco;
    }
   
}
