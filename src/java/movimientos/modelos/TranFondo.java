/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author FrankRH
 */
public class TranFondo implements Serializable {

    public Integer id;
    private Integer idFondoTranFondo;
    private Calendar fecMovTranFondo;
    private String fecTranFondo;
    private String docTranFondo;
    private Integer idConceptoTranFondo;
    private Integer idBancoTranFondo;
    private Integer idCuentaTranFondo;
    private String formaPagoTranFondo;
    private Double mtoTranFondo;
    private String tipoOperTranFondo;
    private Double saldoTranFondo;
    private String descTranFondo;
    private String estadoTranFondo;
    private Calendar perTranFondo;
    private Integer idPago;
    
    /** Asociaciones */
    
    private Fondo fondo;
   

    public TranFondo() {
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
    public void setFecMovTranFondo(Calendar fecMovTranFondo) {
        this.fecMovTranFondo = fecMovTranFondo;
    }

    public Calendar getFecMovTranFondo() {
        return fecMovTranFondo;
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

    public void setIdBancoTranFondo(Integer idBancoTranFondo) {
        this.idBancoTranFondo = idBancoTranFondo;
    }

    public Integer getIdBancoTranFondo() {
        return idBancoTranFondo;
    }
    public void setIdCuentaTranFondo(Integer idCuentaTranFondo) {
        this.idCuentaTranFondo = idCuentaTranFondo;
    }

    public Integer getIdCuentaTranFondo() {
        return idCuentaTranFondo;
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
    
    public void setTipoOperTranFondo(String tipoOperTranFondo) {
        this.tipoOperTranFondo = tipoOperTranFondo;
    }

    public String getTipoOperTranFondo() {
        return tipoOperTranFondo;
    }
    
    public void setSaldoTranFondo(Double saldoTranFondo) {
        this.saldoTranFondo = saldoTranFondo;
    }

    public Double getSaldoTranFondo() {
        return saldoTranFondo;
    }

    public void setDescTranFondo(String descTranFondo) {
        this.descTranFondo = descTranFondo;
    }

    public String getDescTranFondo() {
        return descTranFondo;
    }
    public void setEstadoTranFondo(String estadoTranFondo) {
        this.estadoTranFondo = estadoTranFondo;
    }

    public String getEstadoTranFondo() {
        return estadoTranFondo;
    }
    public void setPerTranFondo(Calendar perTranFondo) {
        this.perTranFondo = perTranFondo;
    }

    public Calendar getPerTranFondo() {
        return perTranFondo;
    }
    
     public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Integer getIdPago() {
        return idPago;
    }
    public void setFondo(Fondo fondo) {
        this.fondo = fondo;
    }

    public Fondo getFondo() {
        return fondo;
    }
}
