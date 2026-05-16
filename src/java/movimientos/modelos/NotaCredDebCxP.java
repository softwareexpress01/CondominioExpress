package movimientos.modelos;

import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.util.Calendar;

public class NotaCredDebCxP implements Serializable {

    private Integer id;
    private String numNotaCredDebCxP;
    private String fecNotaCredDebCxP;
    private Double mtoNotaCredDebCxP;
    public String edoNotaCredDebCxP;
    private Double saldoNotaCredDebCxP;
    public String tipoNotaCredDebCxP;
    private Integer idConcepto;
    private Proveedor proveedor;
    private Calendar fecMovNCD;
    public String idPago;

    public NotaCredDebCxP() {
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

    public void setNumNotaCredDebCxP(String numNotaCredDebCxP) {
        this.numNotaCredDebCxP = numNotaCredDebCxP;
    }

    public String getNumNotaCredDebCxP() {
        return numNotaCredDebCxP;
    }

    public void setMtoNotaCredDebCxP(Double mtoNotaCredDebCxP) {
        this.mtoNotaCredDebCxP = mtoNotaCredDebCxP;
    }

    public Double getMtoNotaCredDebCxP() {
        return mtoNotaCredDebCxP;
    }

    public String getEdoNotaCredDebCxP() {
        return edoNotaCredDebCxP;
    }

    public void setEdoNotaCredDebCxP(String edoNotaCredDebCxP) {
        this.edoNotaCredDebCxP = edoNotaCredDebCxP;
    }

    public Double getSaldoNotaCredDebCxP() {
        return saldoNotaCredDebCxP;
    }

    public void setSaldoNotaCredDebCxP(Double saldoNotaCredDebCxP) {
        this.saldoNotaCredDebCxP = saldoNotaCredDebCxP;
    }

    public String getTipoNotaCredDebCxP() {
        return tipoNotaCredDebCxP;
    }

    public void setTipoNotaCredDebCxP(String tipoNotaCredDebCxP) {
        this.tipoNotaCredDebCxP = tipoNotaCredDebCxP;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public Calendar getFecMovNCD() {
        return fecMovNCD;
    }

    public void setFecMovNCD(Calendar fecMovNCD) {
        this.fecMovNCD = fecMovNCD;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdPago() {
        return idPago;
    }
}
