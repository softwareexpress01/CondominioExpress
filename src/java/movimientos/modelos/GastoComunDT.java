package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;

public class GastoComunDT implements Serializable {

    private Integer id;
    private String fecGastoComun;
    private String numDocGastoComun;
    private Double mtoGastoComun;
    private String mtoGastoComunString;
    private String obsGastoComun;
    private String nombProveedor;
    private String nombConcepto;
    private String nombFondo;
    private Integer idProveedor;
    private Integer idConcepto;
    private Integer idFondo;
    private String edoGastoComun;
    private Double saldoGastoComun;
    private String saldoGastoComunString;
    private String fecMovGC;
    private String perGastoComun;
    private Condominio condominio;

    public GastoComunDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoComun(String fecGastoComun) {
        this.fecGastoComun = fecGastoComun;
    }

    public String getFecGastoComun() {
        return fecGastoComun;
    }

    public void setNumDocGastoComun(String numDocGastoComun) {
        this.numDocGastoComun = numDocGastoComun;
    }

    public String getNumDocGastoComun() {
        return numDocGastoComun;
    }

    public void setMtoGastoComun(Double mtoGastoComun) {
        this.mtoGastoComun = mtoGastoComun;
    }

    public Double getMtoGastoComun() {
        return mtoGastoComun;
    }

    public String getObsGastoComun() {
        return obsGastoComun;
    }

    public void setObsGastoComun(String obsGastoComun) {
        this.obsGastoComun = obsGastoComun;
    }

    public String getEdoGastoComun() {
        return edoGastoComun;
    }

    public void setEdoGastoComun(String edoGastoComun) {
        this.edoGastoComun = edoGastoComun;
    }

    public String getFecMovGC() {
        return fecMovGC;
    }

    public void setFecMovGC(String fecMovGC) {
        this.fecMovGC = fecMovGC;
    }

    public Double getSaldoGastoComun() {
        return saldoGastoComun;
    }

    public void setSaldoGastoComun(Double saldoGastoComun) {
        this.saldoGastoComun = saldoGastoComun;
    }

    public String getSaldoGastoComunString() {
        return saldoGastoComunString;
    }

    public void setSaldoGastoComunString(String saldoGastoComunString) {
        this.saldoGastoComunString = saldoGastoComunString;
    }

    public String getMtoGastoComunString() {
        return mtoGastoComunString;
    }

    public void setMtoGastoComunString(String mtoGastoComunString) {
        this.mtoGastoComunString = mtoGastoComunString;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }

    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setNombFondo(String nombFondo) {
        this.nombFondo = nombFondo;
    }

    public String getNombFondo() {
        return nombFondo;
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

    public void setPerGastoComun(String perGastoComun) {
        this.perGastoComun = perGastoComun;
    }

    public String getPerGastoComun() {
        return perGastoComun;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}