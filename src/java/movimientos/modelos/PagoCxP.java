package movimientos.modelos;

import datosbasicos.modelos.Proveedor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class PagoCxP implements Serializable {

    public Integer id;
    private String desPagoCxP;
    private String numDocPagoCxP;
    private Double mtoPagoCxP;
    private String formaPagoCxP;
    private String fecDocPagoCxP;
    private Integer bancoPagoCxP;
    private Integer idCuentaPagoCxP;
    private Calendar fecMovPagoCxP;
    private Calendar periodoActual;
    private Proveedor proveedor;
    public Collection<PagoFacCxP> pagoFacCxP = new ArrayList<PagoFacCxP>();

    public PagoCxP() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDesPagoCxP(String desPagoCxP) {
        this.desPagoCxP = desPagoCxP;
    }

    public String getDesPagoCxP() {
        return desPagoCxP;
    }

    public void setNumDocPagoCxP(String numDocPagoCxP) {
        this.numDocPagoCxP = numDocPagoCxP;
    }

    public String getNumDocPagoCxP() {
        return numDocPagoCxP;
    }

    public Double getMtoPagoCxP() {
        return mtoPagoCxP;
    }

    public void setMtoPagoCxP(Double mtoPagoCxP) {
        this.mtoPagoCxP = mtoPagoCxP;
    }

    public String getFormaPagoCxP() {
        return formaPagoCxP;
    }

    public void setFormaPagoCxP(String formaPagoCxP) {
        this.formaPagoCxP = formaPagoCxP;
    }

    public String getFecDocPagoCxP() {
        return fecDocPagoCxP;
    }

    public void setFecDocPagoCxP(String fecDocPagoCxP) {
        this.fecDocPagoCxP = fecDocPagoCxP;
    }

    public void setBancoPagoCxP(Integer bancoPagoCxP) {
        this.bancoPagoCxP = bancoPagoCxP;
    }

    public Integer getBancoPagoCxP() {
        return bancoPagoCxP;
    }
    
    public void setIdCuentaPagoCxP(Integer idCuentaPagoCxP) {
        this.idCuentaPagoCxP = idCuentaPagoCxP;
    }

    public Integer getIdCuentaPagoCxP() {
        return idCuentaPagoCxP;
    }

    public void setFecMovPagoCxP(Calendar fecMovPagoCxP) {
        this.fecMovPagoCxP = fecMovPagoCxP;
    }

    public Calendar getFecMovPagoCxP() {
        return fecMovPagoCxP;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setPeriodoActual(Calendar periodoActual) {
        this.periodoActual = periodoActual;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    } 

    public void setPagoFacCxP(Collection<PagoFacCxP> pagoFacCxP) {
        this.pagoFacCxP = pagoFacCxP;
    }

    public Collection<PagoFacCxP> getPagoFacCxP() {
        return pagoFacCxP;
    }
}
