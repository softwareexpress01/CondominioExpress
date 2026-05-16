package movimientos.modelos;

import java.io.Serializable;

public class DeudaPendDT implements Serializable {

    private Integer id;
    private String fecDeudaPend;
    private String numDeudaPend;
    private Double mtoDeudaPend;
    private String mtoDeudaPendString;
    private Integer idConcepto;
    private Integer idInmueble;
    private String nombConcepto;
    private String edoDeudaPend;
    private Double saldoDeudaPend;
    private String saldoDeudaPendString;
    private String tipoDeudaPend;
    private String fecMovDP;

    public DeudaPendDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecDeudaPend(String fecDeudaPend) {
        this.fecDeudaPend = fecDeudaPend;
    }

    public String getFecDeudaPend() {
        return fecDeudaPend;
    }

    public void setNumDeudaPend(String numDeudaPend) {
        this.numDeudaPend = numDeudaPend;
    }

    public String getNumDeudaPend() {
        return numDeudaPend;
    }

    public void setMtoDeudaPend(Double mtoDeudaPend) {
        this.mtoDeudaPend = mtoDeudaPend;
    }

    public Double getMtoDeudaPend() {
        return mtoDeudaPend;
    }

    public void setMtoDeudaPendString(String mtoDeudaPendString) {
        this.mtoDeudaPendString = mtoDeudaPendString;
    }

    public String getMtoDeudaPendString() {
        return mtoDeudaPendString;
    }

    public String getEdoDeudaPend() {
        return edoDeudaPend;
    }

    public void setEdoDeudaPend(String edoDeudaPend) {
        this.edoDeudaPend = edoDeudaPend;
    }

    public Double getSaldoDeudaPend() {
        return saldoDeudaPend;
    }

    public void setSaldoDeudaPend(Double saldoDeudaPend) {
        this.saldoDeudaPend = saldoDeudaPend;
    }

    public void setSaldoDeudaPendString(String saldoDeudaPendString) {
        this.saldoDeudaPendString = saldoDeudaPendString;
    }

    public String getSaldoDeudaPendString() {
        return saldoDeudaPendString;
    }

    public String getTipoDeudaPend() {
        return tipoDeudaPend;
    }

    public void setTipoDeudaPend(String tipoDeudaPend) {
        this.tipoDeudaPend = tipoDeudaPend;
    }

    public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdInmueble(Integer idInmueble) {
        this.idInmueble = idInmueble;
    }

    public Integer getIdInmueble() {
        return idInmueble;
    }

    public String getFecMovDP() {
        return fecMovDP;
    }

    public void setFecMovDP(String fecMovDP) {
        this.fecMovDP = fecMovDP;
    }
}
