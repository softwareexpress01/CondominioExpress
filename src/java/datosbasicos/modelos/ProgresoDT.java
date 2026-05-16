package datosbasicos.modelos;

import java.io.Serializable;

public class ProgresoDT implements Serializable {

    private Integer id;
    private String numInmProp;
    private String porcInmRegistrados;
    private String porcAlicRegistrado;
    

    public ProgresoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNumInmProp(String numInmProp) {
        this.numInmProp = numInmProp;
    }

    public String getNumInmProp() {
        return numInmProp;
    }

    public void setPorcInmRegistrados(String porcInmRegistrados) {
        this.porcInmRegistrados = porcInmRegistrados;
    }

    public String getPorcInmRegistrados() {
        return porcInmRegistrados;
    }

    public void setPorcAlicRegistrado(String porcAlicRegistrado) {
        this.porcAlicRegistrado = porcAlicRegistrado;
    }

    public String getPorcAlicRegistrado() {
        return porcAlicRegistrado;
    }

}
