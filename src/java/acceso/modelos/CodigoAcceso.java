/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.modelos;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;

/**
 *
 * @author jesus
 */
public class CodigoAcceso implements Serializable {

    private Integer id;
    /* Asociaciones */
    private Condominio condominio;
    private Inmueble inmueble;

    public CodigoAcceso() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
