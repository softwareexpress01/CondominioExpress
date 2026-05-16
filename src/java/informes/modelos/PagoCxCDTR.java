package informes.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;

public class PagoCxCDTR implements Serializable {

    private Integer id;
    private String desPagoCxC;
    private Double mtoPagoCxC;
    private String formaPagoCxC;
    private String nombPropiedad;
    private String codInmueble;
    private Inmueble inmueble;

    public PagoCxCDTR() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    
    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public Double getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(Double mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getNombPropiedad() {
        return nombPropiedad;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setNombPropiedad(String nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
