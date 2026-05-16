package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Propiedad;
import java.io.Serializable;
import java.util.Date;

public class GastoPropiedad implements Serializable {

    private Integer id;
    private String fecGastoPropiedad;
    private String numDocGastoPropiedad;
    private Double mtoGastoPropiedad;
    private String obsGastoPropiedad;
    private Integer idProveedor;
    private Integer idConcepto;
    private String edoGastoPropiedad;
    private Double saldoGastoPropiedad;
    private Date fecMovGP;
    private Condominio condominio;

    public GastoPropiedad() {
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

    public Date getFecMovGP() {
        return fecMovGP;
    }

    public void setFecMovGP(Date fecMovGP) {
        this.fecMovGP = fecMovGP;
    }

    public Double getSaldoGastoPropiedad() {
        return saldoGastoPropiedad;
    }

    public void setSaldoGastoPropiedad(Double saldoGastoPropiedad) {
        this.saldoGastoPropiedad = saldoGastoPropiedad;
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

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
