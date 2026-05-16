/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author jesus
 */
public class Chequera implements Serializable {

    public Integer id;
    private Integer idBanco;
    private Integer idCuenta;
    
    private String numChequera;
    private String edoChequera;
    private String cantCheques;
    private String primerCheque;
    private String ultimoCheque;
    private String edoElimChequera;
    
    /** Asociaciones */
    
    private Cuenta cuenta;

    public Chequera() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdBanco(Integer idBanco) {
        this.idBanco = idBanco;
    }

    public Integer getIdBanco() {
        return idBanco;
    }
    
    public void setIdCuenta(Integer idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Integer getIdCuenta() {
        return idCuenta;
    }

    public void setNumChequera(String numChequera) {
        this.numChequera = numChequera;
    }

    public String getNumChequera() {
        return numChequera;
    }

    public void setEdoChequera(String edoChequera) {
        this.edoChequera = edoChequera;
    }

    public String getEdoChequera() {
        return edoChequera;
    }
    
    public void setCantCheques(String cantCheques) {
        this.cantCheques = cantCheques;
    }

    public String getCantCheques() {
        return cantCheques;
    }
    
    public void setPrimerCheque(String primerCheque) {
        this.primerCheque = primerCheque;
    }

    public String getPrimerCheque() {
        return primerCheque;
    }

    public void setUltimoCheque(String ultimoCheque) {
        this.ultimoCheque = ultimoCheque;
    }

    public String getUltimoCheque() {
        return ultimoCheque;
    }
    
    public void setEdoElimChequera(String edoElimChequera) {
        this.edoElimChequera = edoElimChequera;
    }

    public String getEdoElimChequera() {
        return edoElimChequera;
    }
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
}
