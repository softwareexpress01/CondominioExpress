/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuracion.modelos;

import datosbasicos.modelos.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author jesus
 */
public class TasaDT implements Serializable {

    private Integer id;
    private String periodo;
    private String tasa;
    
    /* Asociaciones */
    private Condominio condominio;
    private Collection<Tasa> tasas = new ArrayList<Tasa>();

    public TasaDT () {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    public String getTasa() {
        return tasa;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
    public void setTasas(Collection<Tasa> tasas) {
        this.tasas = tasas;
    }

    public Collection<Tasa> getTasas() {
        return tasas;
    }
    
    public void addCuentas(Tasa tasa) {
        this.tasas.add(tasa);
    }
}
