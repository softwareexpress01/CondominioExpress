package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class NotaCredDebCxC implements Serializable {

    private Integer id;
    private String numNotaCredDebCxC;
    private String fecNotaCredDebCxC;
    private Double mtoNotaCredDebCxC;
    private String edoNotaCredDebCxC;
    private Double saldoNotaCredDebCxC;
    private String tipoNotaCredDebCxC;
    private String tipoOperNotaCredDebCxC;
    private Integer idConcepto;
    private Inmueble inmueble;
    private String descNotaCredDebCxC;
    private Calendar fecMovNCD;
    private Calendar perNotaCredDebCxC;
    private Integer idPagoCxC;

    public NotaCredDebCxC() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecNotaCredDebCxC(String fecNotaCredDebCxC) {
        this.fecNotaCredDebCxC = fecNotaCredDebCxC;
    }

    public String getFecNotaCredDebCxC() {
        return fecNotaCredDebCxC;
    }

    public void setNumNotaCredDebCxC(String numNotaCredDebCxC) {
        this.numNotaCredDebCxC = numNotaCredDebCxC;
    }

    public String getNumNotaCredDebCxC() {
        return numNotaCredDebCxC;
    }

    public void setMtoNotaCredDebCxC(Double mtoNotaCredDebCxC) {
        this.mtoNotaCredDebCxC = mtoNotaCredDebCxC;
    }

    public Double getMtoNotaCredDebCxC() {
        return mtoNotaCredDebCxC;
    }

    public String getEdoNotaCredDebCxC() {
        return edoNotaCredDebCxC;
    }

    public void setEdoNotaCredDebCxC(String edoNotaCredDebCxC) {
        this.edoNotaCredDebCxC = edoNotaCredDebCxC;
    }

    public Double getSaldoNotaCredDebCxC() {
        return saldoNotaCredDebCxC;
    }

    public void setSaldoNotaCredDebCxC(Double saldoNotaCredDebCxC) {
        this.saldoNotaCredDebCxC = saldoNotaCredDebCxC;
    }

    public String getTipoNotaCredDebCxC() {
        return tipoNotaCredDebCxC;
    }

    public void setTipoNotaCredDebCxC(String tipoNotaCredDebCxC) {
        this.tipoNotaCredDebCxC = tipoNotaCredDebCxC;
    }

    public String getTipoOperNotaCredDebCxC() {
        return tipoOperNotaCredDebCxC;
    }

    public void setTipoOperNotaCredDebCxC(String tipoOperNotaCredDebCxC) {
        this.tipoOperNotaCredDebCxC = tipoOperNotaCredDebCxC;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public String getDescNotaCredDebCxC() {
        return descNotaCredDebCxC;
    }

    public void setDescNotaCredDebCxC(String descNotaCredDebCxC) {
        this.descNotaCredDebCxC = descNotaCredDebCxC;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setIdPagoCxC(Integer idPagoCxC) {
        this.idPagoCxC = idPagoCxC;
    }

    public Integer getIdPagoCxC() {
        return idPagoCxC;
    }

    public Calendar getFecMovNCD() {
        return fecMovNCD;
    }

    public void setFecMovNCD(Calendar fecMovNCD) {
        this.fecMovNCD = fecMovNCD;
    }
    public Calendar getPerNotaCredDebCxC() {
        return perNotaCredDebCxC;
    }

    public void setPerNotaCredDebCxC(Calendar perNotaCredDebCxC) {
        this.perNotaCredDebCxC = perNotaCredDebCxC;
    }
}
