package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class PagoCxC implements Serializable {

    private Integer id;
    private String desPagoCxC;
    private String numDocPagoCxC;
    private Double mtoPagoCxC;
    private String formaPagoCxC;
    private String fecDocPagoCxC;
    private String bancoPagoCxC;
    private String cuentaPagoCxC;
    private Calendar fecMovPagoCxC;
    private Calendar periodoActual;
    private Integer idFormaPagoCxC; 
    private Inmueble inmueble;
    public Collection<PagoFacCxC> pagoFacCxC = new ArrayList<PagoFacCxC>();

    public PagoCxC() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public void setNumDocPagoCxC(String numDocPagoCxC) {
        this.numDocPagoCxC = numDocPagoCxC;
    }

    public String getNumDocPagoCxC() {
        return numDocPagoCxC;
    }

    public Double getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(Double mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getFecDocPagoCxC() {
        return fecDocPagoCxC;
    }

    public void setFecDocPagoCxC(String fecDocPagoCxC) {
        this.fecDocPagoCxC = fecDocPagoCxC;
    }

    public void setBancoPagoCxC(String bancoPagoCxC) {
        this.bancoPagoCxC = bancoPagoCxC;
    }

    public String getBancoPagoCxC() {
        return bancoPagoCxC;
    }

    public void setCuentaPagoCxC(String cuentaPagoCxC) {
        this.cuentaPagoCxC = cuentaPagoCxC;
    }

    public String getCuentaPagoCxC() {
        return cuentaPagoCxC;
    }

    public void setFecMovPagoCxC(Calendar fecMovPagoCxC) {
        this.fecMovPagoCxC = fecMovPagoCxC;
    }

    public Calendar getFecMovPagoCxC() {
        return fecMovPagoCxC;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Calendar periodoActual) {
        this.periodoActual = periodoActual;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setPagoFacCxC(Collection<PagoFacCxC> pagoFacCxC) {
        this.pagoFacCxC = pagoFacCxC;
    }

    public Collection<PagoFacCxC> getPagoFacCxC() {
        return pagoFacCxC;
    }
    public void setIdFormaPagoCxC(Integer idFormaPagoCxC) {
        this.idFormaPagoCxC = idFormaPagoCxC;
    }

    public Integer getIdFormaPagoCxC() {
        return idFormaPagoCxC;
    }
}
