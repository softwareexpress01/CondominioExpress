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
public class BancoDT implements Serializable {

    private Integer id;
    private String nombBanco;
    private String nombOficBanco;
    private String codTelBanco;
    private String telBanco;
    private String telConcatBanco;
    /* Asociaciones */
    private Condominio condominio;
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();

    public BancoDT () {
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

    public void setCodTelBanco(String codTelBanco) {
        this.codTelBanco = codTelBanco;
    }

    public String getCodTelBanco() {
        return codTelBanco;
    }

    public void setTelBanco(String telBanco) {
        this.telBanco = telBanco;
    }

    public String getTelBanco() {
        return telBanco;
    }
    
    public void setTelConcatBanco(String telConcatBanco) {
        this.telConcatBanco = telConcatBanco;
    }

    public String getTelConcatBanco() {
        return telConcatBanco;
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
