package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Calendar;

public class GastoComunOrd implements Serializable {

    private Integer id;
    private String fecGastoComunOrd;
    private String numDocGastoComunOrd;
    private Double mtoGastoComunOrd;
    private String obsGastoComunOrd;
    private Integer idProveedor;
    private Integer idConcepto;
    private String edoGastoComunOrd;
    private Double saldoGastoComunOrd;
    private Calendar fecMovGCOrd;
    private Calendar perGastoComunOrd;
    private Condominio condominio;

    public GastoComunOrd() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoComunOrd(String fecGastoComunOrd) {
        this.fecGastoComunOrd = fecGastoComunOrd;
    }

    public String getFecGastoComunOrd() {
        return fecGastoComunOrd;
    }

    public void setNumDocGastoComunOrd(String numDocGastoComunOrd) {
        this.numDocGastoComunOrd = numDocGastoComunOrd;
    }

    public String getNumDocGastoComunOrd() {
        return numDocGastoComunOrd;
    }

    public void setMtoGastoComunOrd(Double mtoGastoComunOrd) {
        this.mtoGastoComunOrd = mtoGastoComunOrd;
    }

    public Double getMtoGastoComunOrd() {
        return mtoGastoComunOrd;
    }

    public String getObsGastoComunOrd() {
        return obsGastoComunOrd;
    }

    public void setObsGastoComunOrd(String obsGastoComunOrd) {
        this.obsGastoComunOrd = obsGastoComunOrd;
    }

    public String getEdoGastoComunOrd() {
        return edoGastoComunOrd;
    }

    public void setEdoGastoComunOrd(String edoGastoComunOrd) {
        this.edoGastoComunOrd = edoGastoComunOrd;
    }

    public Calendar getFecMovGCOrd() {
        return fecMovGCOrd;
    }

    public void setFecMovGCOrd(Calendar fecMovGCOrd) {
        this.fecMovGCOrd = fecMovGCOrd;
    }

    public Double getSaldoGastoComunOrd() {
        return saldoGastoComunOrd;
    }

    public void setSaldoGastoComunOrd(Double saldoGastoComunOrd) {
        this.saldoGastoComunOrd = saldoGastoComunOrd;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }
    public void setPerGastoComunOrd(Calendar perGastoComunOrd) {
        this.perGastoComunOrd = perGastoComunOrd;
    }

    public Calendar getPerGastoComunOrd() {
        return perGastoComunOrd;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
