package informes.modelos;

import java.io.Serializable;

public class NotaDebitoCxCDT implements Serializable { 

    private Integer id;
    private String nombPropiedad;
    private String codInmueble;
    private String descNotaDebCxC;
    private Double mtoNotaDebCxC;

    public NotaDebitoCxCDT() {
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
    
    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setDescNotaDebCxC(String descNotaDebCxC) {
        this.descNotaDebCxC = descNotaDebCxC;
    }

    public String getDescNotaDebCxC() {
        return descNotaDebCxC;
    }

    public Double getMtoNotaDebCxC() {
        return mtoNotaDebCxC;
    }

    public void setMtoNotaDebCxC(Double mtoNotaDebCxC) {
        this.mtoNotaDebCxC = mtoNotaDebCxC;
    }
}
