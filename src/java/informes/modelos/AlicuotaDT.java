package informes.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class AlicuotaDT implements Serializable {

    private Integer id;
    private Calendar fecEmiAlicuota;
    private String desAlicuota;
    private Double mtoAlicuota;
    private String edoAlicuota;
    private Double saldoAlicuota;
    private Inmueble inmueble;
    private String codInmueble;
    private String nombPropiedad;

    public AlicuotaDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecEmiAlicuota(Calendar fecEmiAlicuota) {
        this.fecEmiAlicuota = fecEmiAlicuota;
    }

    public Calendar getFecEmiAlicuota() {
        return fecEmiAlicuota;
    }

    public void setDesAlicuota(String desAlicuota) {
        this.desAlicuota = desAlicuota;
    }

    public String getDesAlicuota() {
        return desAlicuota;
    }

    public void setMtoAlicuota(Double mtoAlicuota) {
        this.mtoAlicuota = mtoAlicuota;
    }

    public Double getMtoAlicuota() {
        return mtoAlicuota;
    }

    public String getEdoAlicuota() {
        return edoAlicuota;
    }

    public void setEdoAlicuota(String edoAlicuota) {
        this.edoAlicuota = edoAlicuota;
    }

    public Double getSaldoAlicuota() {
        return saldoAlicuota;
    }

    public void setSaldoAlicuota(Double saldoAlicuota) {
        this.saldoAlicuota = saldoAlicuota;
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

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }
}
