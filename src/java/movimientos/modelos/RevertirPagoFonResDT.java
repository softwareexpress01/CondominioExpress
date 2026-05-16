package movimientos.modelos;

import movimientos.modelos.TranFondo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class RevertirPagoFonResDT implements Serializable {

    public Integer id;
    private String desPagoFonRes;
    private String numDocPagoFonRes;
    private Double mtoPagoFonRes;
    private String mtoPagoFonResString;
    private String formaPagoFonRes;
    private String fecDocPagoFonRes;
    private String nombBancoPagoFonRes;
    private Integer bancoPagoFonRes;
    private Integer idCuentaPagoFonRes;
    private String fecMovPagoFonRes;
    private Calendar periodoActual;
    public Collection<PagoFacFonRes> pagoFacFonRes = new ArrayList<PagoFacFonRes>();

    public RevertirPagoFonResDT() {
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

    public String getMtoPagoFonResString() {
        return mtoPagoFonResString;
    }

    public void setMtoPagoFonResString(String mtoPagoFonResString) {
        this.mtoPagoFonResString = mtoPagoFonResString;
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

    public String getNombBancoPagoFonRes() {
        return nombBancoPagoFonRes;
    }

    public void setNombBancoPagoFonRes(String nombBancoPagoFonRes) {
        this.nombBancoPagoFonRes = nombBancoPagoFonRes;
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

    public void setFecMovPagoFonRes(String fecMovPagoFonRes) {
        this.fecMovPagoFonRes = fecMovPagoFonRes;
    }

    public String getFecMovPagoFonRes() {
        return fecMovPagoFonRes;
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
