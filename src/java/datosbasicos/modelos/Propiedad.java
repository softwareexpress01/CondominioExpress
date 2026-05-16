package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * UserBean.java serves as the POJO for storing information about a Propiedad
 * 
 */
public class Propiedad implements Serializable {

    private Integer id;
    private String nombPropiedad;
    private String numInmuebles;
    private Condominio condominio;
    public Collection<Inmueble> inmuebles = new ArrayList<Inmueble>();
    
    public Propiedad() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombPropiedad(String nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public String getNombPropiedad() {
        return nombPropiedad;
    }

    public void setNumInmuebles(String numInmuebles) {
        this.numInmuebles = numInmuebles;
    }

    public String getNumInmuebles() {
        return numInmuebles;
    }

    public void setInmuebles(Collection<Inmueble> inmuebles) {
        this.inmuebles = inmuebles;
    }

    public Collection<Inmueble> getInmuebles() {
        return inmuebles;
    }

    public void addInmuebles(Inmueble inmueble) {
        this.inmuebles.add(inmueble);
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
