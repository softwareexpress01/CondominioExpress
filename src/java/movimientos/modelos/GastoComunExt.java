package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Calendar;

public class GastoComunExt implements Serializable {

    private Integer id;
    private String fecGastoComunExt;
    private String numDocGastoComunExt;
    private Double mtoGastoComunExt;
    private String obsGastoComunExt;
    private Integer idProveedor;
    private Integer idConcepto;
    private Integer idFondo;
    private String edoGastoComunExt;
    private Double saldoGastoComunExt;
    private Calendar fecMovGCExt;
    private Calendar perGastoComunExt;
    private Condominio condominio;

    public GastoComunExt() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoComunExt(String fecGastoComunExt) {
        this.fecGastoComunExt = fecGastoComunExt;
    }

    public String getFecGastoComunExt() {
        return fecGastoComunExt;
    }

    public void setNumDocGastoComunExt(String numDocGastoComunExt) {
        this.numDocGastoComunExt = numDocGastoComunExt;
    }

    public String getNumDocGastoComunExt() {
        return numDocGastoComunExt;
    }

    public void setMtoGastoComunExt(Double mtoGastoComunExt) {
        this.mtoGastoComunExt = mtoGastoComunExt;
    }

    public Double getMtoGastoComunExt() {
        return mtoGastoComunExt;
    }

    public String getObsGastoComunExt() {
        return obsGastoComunExt;
    }

    public void setObsGastoComunExt(String obsGastoComunExt) {
        this.obsGastoComunExt = obsGastoComunExt;
    }

    public String getEdoGastoComunExt() {
        return edoGastoComunExt;
    }

    public void setEdoGastoComunExt(String edoGastoComunExt) {
        this.edoGastoComunExt = edoGastoComunExt;
    }

    public Calendar getFecMovGCExt() {
        return fecMovGCExt;
    }

    public void setFecMovGCExt(Calendar fecMovGCExt) {
        this.fecMovGCExt = fecMovGCExt;
    }

    public Double getSaldoGastoComunExt() {
        return saldoGastoComunExt;
    }

    public void setSaldoGastoComunExt(Double saldoGastoComunExt) {
        this.saldoGastoComunExt = saldoGastoComunExt;
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

    public void setIdFondo(Integer idFondo) {
        this.idFondo = idFondo;
    }

    public Integer getIdFondo() {
        return idFondo;
    }

    public void setPerGastoComunExt(Calendar perGastoComunExt) {
        this.perGastoComunExt = perGastoComunExt;
    }

    public Calendar getPerGastoComunExt() {
        return perGastoComunExt;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
