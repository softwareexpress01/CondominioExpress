package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class PagoFonRes implements Serializable {

    public Integer id;
    private String desPagoFonRes;
    private String numDocPagoFonRes;
    private Double mtoPagoFonRes;
    private String formaPagoFonRes;
    private String fecDocPagoFonRes;
    private Integer bancoPagoFonRes;
    private Integer idCuentaPagoFonRes;
    private Calendar fecMovPagoFonRes;
    private Calendar periodoActual;
    private Condominio condominio;
    public Collection<PagoFacFonRes> pagoFacFonRes = new ArrayList<PagoFacFonRes>();

    public PagoFonRes() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDesPagoFonRes(String desPagoFonRes) {
        this.desPagoFonRes = desPagoFonRes;
    }

    public String getDesPagoFonRes() {
        return desPagoFonRes;
    }

    public void setNumDocPagoFonRes(String numDocPagoFonRes) {
        this.numDocPagoFonRes = numDocPagoFonRes;
    }

    public String getNumDocPagoFonRes() {
        return numDocPagoFonRes;
    }

    public Double getMtoPagoFonRes() {
        return mtoPagoFonRes;
    }

    public void setMtoPagoFonRes(Double mtoPagoFonRes) {
        this.mtoPagoFonRes = mtoPagoFonRes;
    }

    public String getFormaPagoFonRes() {
        return formaPagoFonRes;
    }

    public void setFormaPagoFonRes(String formaPagoFonRes) {
        this.formaPagoFonRes = formaPagoFonRes;
    }

    public String getFecDocPagoFonRes() {
        return fecDocPagoFonRes;
    }

    public void setFecDocPagoFonRes(String fecDocPagoFonRes) {
        this.fecDocPagoFonRes = fecDocPagoFonRes;
    }

    public void setBancoPagoFonRes(Integer bancoPagoFonRes) {
        this.bancoPagoFonRes = bancoPagoFonRes;
    }

    public Integer getBancoPagoFonRes() {
        return bancoPagoFonRes;
    }
    
    public void setIdCuentaPagoFonRes(Integer idCuentaPagoFonRes) {
        this.idCuentaPagoFonRes = idCuentaPagoFonRes;
    }

    public Integer getIdCuentaPagoFonRes() {
        return idCuentaPagoFonRes;
    }

    public void setFecMovPagoFonRes(Calendar fecMovPagoFonRes) {
        this.fecMovPagoFonRes = fecMovPagoFonRes;
    }

    public Calendar getFecMovPagoFonRes() {
        return fecMovPagoFonRes;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setPeriodoActual(Calendar periodoActual) {
        this.periodoActual = periodoActual;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    } 

    public void setPagoFacFonRes(Collection<PagoFacFonRes> pagoFacFonRes) {
        this.pagoFacFonRes = pagoFacFonRes;
    }

    public Collection<PagoFacFonRes> getPagoFacFonRes() {
        return pagoFacFonRes;
    }
}
