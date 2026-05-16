package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class GastoNoComun implements Serializable {

    private Integer id;
    private String numDocGastoNoComun;
    private String fecGastoNoComun;
    private Double mtoGastoNoComun;
    private String edoGastoNoComun;
    private String edoGralFac;
    private Double saldoGastoNoComun;
    private Double saldoFacGnc;
    private Integer idProveedor;
    private Integer idConcepto;
    private Condominio condominio;
    private Calendar fecMovGNC;
    private Calendar perGastoNoComun;
    private String obsGastoNoComun;
    public Collection<FacGastoNoComun> facturasGNC = new ArrayList<FacGastoNoComun>();

    public GastoNoComun() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoNoComun(String fecGastoNoComun) {
        this.fecGastoNoComun = fecGastoNoComun;
    }

    public String getFecGastoNoComun() {
        return fecGastoNoComun;
    }

    public void setNumDocGastoNoComun(String numDocGastoNoComun) {
        this.numDocGastoNoComun = numDocGastoNoComun;
    }

    public String getNumDocGastoNoComun() {
        return numDocGastoNoComun;
    }

    public void setMtoGastoNoComun(Double mtoGastoNoComun) {
        this.mtoGastoNoComun = mtoGastoNoComun;
    }

    public Double getMtoGastoNoComun() {
        return mtoGastoNoComun;
    }

    public String getEdoGastoNoComun() {
        return edoGastoNoComun;
    }

    public void setEdoGastoNoComun(String edoGastoNoComun) {
        this.edoGastoNoComun = edoGastoNoComun;
    }

    public String getEdoGralFac() {
        return edoGralFac;
    }

    public void setEdoGralFac(String edoGralFac) {
        this.edoGralFac = edoGralFac;
    }

    public Double getSaldoGastoNoComun() {
        return saldoGastoNoComun;
    }

    public void setSaldoGastoNoComun(Double saldoGastoNoComun) {
        this.saldoGastoNoComun = saldoGastoNoComun;
    }

    public Double getSaldoFacGnc() {
        return saldoFacGnc;
    }

    public void setSaldoFacGnc(Double saldoFacGnc) {
        this.saldoFacGnc = saldoFacGnc;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public Calendar getFecMovGNC() {
        return fecMovGNC;
    }

    public void setFecMovGNC(Calendar fecMovGNC) {
        this.fecMovGNC = fecMovGNC;
    }

    public String getObsGastoNoComun() {
        return obsGastoNoComun;
    }

    public void setPerGastoNoComun(Calendar perGastoNoComun) {
        this.perGastoNoComun = perGastoNoComun;
    }

    public Calendar getPerGastoNoComun() {
        return perGastoNoComun;
    }

    public void setObsGastoNoComun(String obsGastoNoComun) {
        this.obsGastoNoComun = obsGastoNoComun;
    }

    public void setFacturasGNC(Collection<FacGastoNoComun> facturasGNC) {
        this.facturasGNC = facturasGNC;
    }

    public Collection<FacGastoNoComun> getFacturasGNC() {
        return facturasGNC;
    }
}