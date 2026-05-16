package movimientos.modelos;

import java.io.Serializable;

public class NotaCredDebCxPDT implements Serializable {

    private Integer id;
    private String fecNotaCredDebCxP;
    private String numNotaCreDebCxP;
    private Double mtoNotaCredDebCxP;
    private String mtoNotaCredDebCxPString;
    private String tipoNotaCredDebCxP;
    private Integer idConcepto;
    private Integer idInmueble;
    private String nombConcepto;
    private String edoNotaCredDebCxP;
    private Double saldoNotaCredDebCxP;
    private String saldoNotaCredDebCxPString;
    private String fecMovNCD;
    private Integer idPagoCxP;

    public NotaCredDebCxPDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecNotaCredDebCxP(String fecNotaCredDebCxP) {
        this.fecNotaCredDebCxP = fecNotaCredDebCxP;
    }

    public String getFecNotaCredDebCxP() {
        return fecNotaCredDebCxP;
    }

    public void setNumNotaCredDebCxP(String numNotaCreDebCxP) {
        this.numNotaCreDebCxP = numNotaCreDebCxP;
    }

    public String getNumNotaCredDebCxP() {
        return numNotaCreDebCxP;
    }

    public void setMtoNotaCredDebCxP(Double mtoNotaCredDebCxP) {
        this.mtoNotaCredDebCxP = mtoNotaCredDebCxP;
    }

    public Double getMtoNotaCredDebCxP() {
        return mtoNotaCredDebCxP;
    }

    public void setMtoNotaCredDebCxPString(String mtoNotaCredDebCxPString) {
        this.mtoNotaCredDebCxPString = mtoNotaCredDebCxPString;
    }

    public String getMtoNotaCredDebCxPString() {
        return mtoNotaCredDebCxPString;
    }

    public String getEdoNotaCredDebCxP() {
        return edoNotaCredDebCxP;
    }

    public void setEdoNotaCredDebCxP(String edoNotaCredDebCxP) {
        this.edoNotaCredDebCxP = edoNotaCredDebCxP;
    }

    public String getTipoNotaCredDebCxP() {
        return tipoNotaCredDebCxP;
    }

    public void setTipoNotaCredDebCxP(String tipoNotaCredDebCxP) {
        this.tipoNotaCredDebCxP = tipoNotaCredDebCxP;
    }

    public Double getSaldoNotaCredDebCxP() {
        return saldoNotaCredDebCxP;
    }

    public void setSaldoNotaCredDebCxP(Double saldoNotaCredDebCxP) {
        this.saldoNotaCredDebCxP = saldoNotaCredDebCxP;
    }

    public String getSaldoNotaCredDebCxPString() {
        return saldoNotaCredDebCxPString;
    }

    public void setSaldoNotaCredDebCxPString(String saldoNotaCredDebCxPString) {
        this.saldoNotaCredDebCxPString = saldoNotaCredDebCxPString;
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

    public void setIdPagoCxP(Integer idPagoCxP) {
        this.id = idPagoCxP;
    }

    public Integer getIdPagoCxP() {
        return idPagoCxP;
    }

    public Object getRowKey(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getRowData(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
