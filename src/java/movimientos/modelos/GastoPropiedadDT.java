package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;

public class GastoPropiedadDT implements Serializable {

    private Integer id;
    private String fecGastoPropiedad;
    private String numDocGastoPropiedad;
    private Double mtoGastoPropiedad;
    private String obsGastoPropiedad;
    private String nombProveedor;
    private String nombConcepto;
    private String nombFondo;
    private Integer idProveedor;
    private Integer idConcepto;
    private Integer idFondo;
    private String edoGastoPropiedad;
    private Double saldoGastoPropiedad;
    private String fecMovGP;
    private Condominio condominio;

    public GastoPropiedadDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoPropiedad(String fecGastoPropiedad) {
        this.fecGastoPropiedad = fecGastoPropiedad;
    }

    public String getFecGastoPropiedad() {
        return fecGastoPropiedad;
    }

    public void setNumDocGastoPropiedad(String numDocGastoPropiedad) {
        this.numDocGastoPropiedad = numDocGastoPropiedad;
    }

    public String getNumDocGastoPropiedad() {
        return numDocGastoPropiedad;
    }

    public void setMtoGastoPropiedad(Double mtoGastoPropiedad) {
        this.mtoGastoPropiedad = mtoGastoPropiedad;
    }

    public Double getMtoGastoPropiedad() {
        return mtoGastoPropiedad;
    }

    public String getObsGastoPropiedad() {
        return obsGastoPropiedad;
    }

    public void setObsGastoPropiedad(String obsGastoPropiedad) {
        this.obsGastoPropiedad = obsGastoPropiedad;
    }
 
    public String getEdoGastoPropiedad() {
        return edoGastoPropiedad;
    }

    public void setEdoGastoPropiedad(String edoGastoPropiedad) {
        this.edoGastoPropiedad = edoGastoPropiedad;
    }

    public String getFecMovGP() {
        return fecMovGP;
    }

    public void setFecMovGP(String fecMovGP) {
        this.fecMovGP = fecMovGP;
    }

    public Double getSaldoGastoPropiedad() {
        return saldoGastoPropiedad;
    }

    public void setSaldoGastoPropiedad(Double saldoGastoPropiedad) {
        this.saldoGastoPropiedad = saldoGastoPropiedad;
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

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
