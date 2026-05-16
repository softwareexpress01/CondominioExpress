/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Fondo;
import java.io.Serializable;

/**
 *
 * @author FrankRH
 */
public class TranFondoDT implements Serializable {

    public Integer id;
    private Integer idFondoTranFondo;
    private String nombFondoTranFondo;
    private String fecTranFondo;
    private String docTranFondo;
    private Integer idConceptoTranFondo;
    private String descConceptoTranFondo;
    private Integer idBancoTranFondo;
    private String nombBancoTranFondo;
    private Integer idCuentaTranFondo;
    private String cuentaTranFondo;
    private String formaPagoTranFondo;
    private Double mtoTranFondo;
    private String mtoTranFondoString;
    private String tipoOperTranFondo;
    private String descTranFondo;
    private String perTranFondo;
    /** Asociaciones */
    private Fondo fondo;

    public TranFondoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdFondoTranFondo(Integer idFondoTranFondo) {
        this.idFondoTranFondo = idFondoTranFondo;
    }

    public Integer getIdFondoTranFondo() {
        return idFondoTranFondo;
    }

    public void setNombFondoTranFondo(String nombFondoTranFondo) {
        this.nombFondoTranFondo = nombFondoTranFondo;
    }

    public String getNombFondoTranFondo() {
        return nombFondoTranFondo;
    }

    public void setFecTranFondo(String fecTranFondo) {
        this.fecTranFondo = fecTranFondo;
    }

    public String getFecTranFondo() {
        return fecTranFondo;
    }

    public void setDocTranFondo(String docTranFondo) {
        this.docTranFondo = docTranFondo;
    }

    public String getDocTranFondo() {
        return docTranFondo;
    }

    public void setIdConceptoTranFondo(Integer idConceptoTranFondo) {
        this.idConceptoTranFondo = idConceptoTranFondo;
    }

    public Integer getIdConceptoTranFondo() {
        return idConceptoTranFondo;
    }

    public void setDescConceptoTranFondo(String descConceptoTranFondo) {
        this.descConceptoTranFondo = descConceptoTranFondo;
    }

    public String getDescConceptoTranFondo() {
        return descConceptoTranFondo;
    }

    public void setIdBancoTranFondo(Integer idBancoTranFondo) {
        this.idBancoTranFondo = idBancoTranFondo;
    }

    public Integer getIdBancoTranFondo() {
        return idBancoTranFondo;
    }

    public void setNombBancoTranFondo(String nombBancoTranFondo) {
        this.nombBancoTranFondo = nombBancoTranFondo;
    }

    public String getNombBancoTranFondo() {
        return nombBancoTranFondo;
    }
    public void setIdCuentaTranFondo(Integer idCuentaTranFondo) {
        this.idCuentaTranFondo = idCuentaTranFondo;
    }

    public Integer getIdCuentaTranFondo() {
        return idCuentaTranFondo;
    }

    public void setCuentaTranFondo(String cuentaTranFondo) {
        this.cuentaTranFondo = cuentaTranFondo;
    }

    public String getCuentaTranFondo() {
        return cuentaTranFondo;
    }

    public void setFormaPagoTranFondo(String formaPagoTranFondo) {
        this.formaPagoTranFondo = formaPagoTranFondo;
    }

    public String getFormaPagoTranFondo() {
        return formaPagoTranFondo;
    }

    public void setMtoTranFondo(Double mtoTranFondo) {
        this.mtoTranFondo = mtoTranFondo;
    }

    public Double getMtoTranFondo() {
        return mtoTranFondo;
    }

    public void setMtoTranFondoString(String mtoTranFondoString) {
        this.mtoTranFondoString = mtoTranFondoString;
    }

    public String getMtoTranFondoString() {
        return mtoTranFondoString;
    }

    public void setTipoOperTranFondo(String tipoOperTranFondo) {
        this.tipoOperTranFondo = tipoOperTranFondo;
    }

    public String getTipoOperTranFondo() {
        return tipoOperTranFondo;
    }

    public void setDescTranFondo(String descTranFondo) {
        this.descTranFondo = descTranFondo;
    }

    public String getDescTranFondo() {
        return descTranFondo;
    }

    public void setPerTranFondo(String perTranFondo) {
        this.perTranFondo = perTranFondo;
    }

    public String getPerTranFondo() {
        return perTranFondo;
    }

    public void setFondo(Fondo fondo) {
        this.fondo = fondo;
    }

    public Fondo getFondo() {
        return fondo;
    }
}
