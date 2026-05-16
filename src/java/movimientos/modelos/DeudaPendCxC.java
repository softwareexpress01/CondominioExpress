package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class DeudaPendCxC implements Serializable {

    private Integer id;
    private Calendar fecDeudaPendCxC;
    private Double mtoDeudaPendCxC;
    private String edoDeudaPendCxC;
    private Double saldoDeudaPendCxC;
    private Integer idConcepto;
    private Calendar fecMovDP;
    private String tipoDeudaPendCxC;
    private Inmueble inmueble;

    public DeudaPendCxC() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecDeudaPendCxC(Calendar fecDeudaPendCxC) {
        this.fecDeudaPendCxC = fecDeudaPendCxC;
    }

    public Calendar getFecDeudaPendCxC() {
        return fecDeudaPendCxC;
    }

    public void setMtoDeudaPendCxC(Double mtoDeudaPendCxC) {
        this.mtoDeudaPendCxC = mtoDeudaPendCxC;
    }

    public Double getMtoDeudaPendCxC() {
        return mtoDeudaPendCxC;
    }

    public String getEdoDeudaPendCxC() {
        return edoDeudaPendCxC;
    }

    public void setEdoDeudaPendCxC(String edoDeudaPendCxC) {
        this.edoDeudaPendCxC = edoDeudaPendCxC;
    }

    public Double getSaldoDeudaPendCxC() {
        return saldoDeudaPendCxC;
    }

    public void setSaldoDeudaPendCxC(Double saldoDeudaPendCxC) {
        this.saldoDeudaPendCxC = saldoDeudaPendCxC;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public Calendar getFecMovDP() {
        return fecMovDP;
    }

    public void setFecMovDP(Calendar fecMovDP) {
        this.fecMovDP = fecMovDP;
    }

    public String getTipoDeudaPendCxC() {
        return tipoDeudaPendCxC;
    }

    public void setTipoDeudaPendCxC(String tipoDeudaPendCxC) {
        this.tipoDeudaPendCxC = tipoDeudaPendCxC;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
