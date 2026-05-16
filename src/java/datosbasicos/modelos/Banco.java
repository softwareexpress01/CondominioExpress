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
public class Banco implements Serializable {

    private Integer id;
    public String nombBanco;
    private String nombOficBanco;
    private String telBanco;
    private String edoBanco;
    
    /* Asociaciones */
    private Condominio condominio;
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();

    public Banco() {
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

    public void setNombOficBanco(String nombOficBanco) {
        this.nombOficBanco = nombOficBanco;
    }

    public String getNombOficBanco() {
        return nombOficBanco;
    }

    public void setTelBanco(String telBanco) {
        this.telBanco = telBanco;
    }

    public String getTelBanco() {
        return telBanco;
    }
    
    public void setEdoBanco(String edoBanco) {
        this.edoBanco = edoBanco;
    }

    public String getEdoBanco() {
        return edoBanco;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
    public void setCuentas(Collection<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public Collection<Cuenta> getCuentas() {
        return cuentas;
    }
    
    public void addCuentas(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }
}
