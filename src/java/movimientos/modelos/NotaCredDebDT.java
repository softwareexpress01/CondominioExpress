package movimientos.modelos;

import java.io.Serializable;

public class NotaCredDebDT implements Serializable {

    private Integer id;
    private String fecNotaCredDeb;
    private String numNotaCreDeb;
    private Double mtoNotaCredDeb;
    private String mtoNotaCredDebString;
    private String tipoNotaCredDeb;
    private Integer idConcepto;
    private Integer idInmueble;
    private String nombConcepto;
    private String edoNotaCredDeb;
    private Double saldoNotaCredDeb;
    private String saldoNotaCredDebString;
    private String fecMovNCD;

    public NotaCredDebDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecNotaCredDeb(String fecNotaCredDeb) {
        this.fecNotaCredDeb = fecNotaCredDeb;
    }

    public String getFecNotaCredDeb() {
        return fecNotaCredDeb;
    }

    public void setNumNotaCredDeb(String numNotaCreDeb) {
        this.numNotaCreDeb = numNotaCreDeb;
    }

    public String getNumNotaCredDeb() {
        return numNotaCreDeb;
    }

    public void setMtoNotaCredDeb(Double mtoNotaCredDeb) {
        this.mtoNotaCredDeb = mtoNotaCredDeb;
    }

    public Double getMtoNotaCredDeb() {
        return mtoNotaCredDeb;
    }

    public void setMtoNotaCredDebString(String mtoNotaCredDebString) {
        this.mtoNotaCredDebString = mtoNotaCredDebString;
    }

    public String getMtoNotaCredDebString() {
        return mtoNotaCredDebString;
    }

    public void setSaldoNotaCredDebString(String saldoNotaCredDebString) {
        this.saldoNotaCredDebString = saldoNotaCredDebString;
    }

    public String getSaldoNotaCredDebString() {
        return saldoNotaCredDebString;
    }

    public String getEdoNotaCredDeb() {
        return edoNotaCredDeb;
    }

    public void setEdoNotaCredDeb(String edoNotaCredDeb) {
        this.edoNotaCredDeb = edoNotaCredDeb;
    }

    public String getTipoNotaCredDeb() {
        return tipoNotaCredDeb;
    }

    public void setTipoNotaCredDeb(String tipoNotaCredDeb) {
        this.tipoNotaCredDeb = tipoNotaCredDeb;
    }

    public Double getSaldoNotaCredDeb() {
        return saldoNotaCredDeb;
    }

    public void setSaldoNotaCredDeb(Double saldoNotaCredDeb) {
        this.saldoNotaCredDeb = saldoNotaCredDeb;
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

    public String getFecMovNCD() {
        return fecMovNCD;
    }

    public void setFecMovNCD(String fecMovNCD) {
        this.fecMovNCD = fecMovNCD;
    }
}
