package configuracion.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;

/**
 * Parametro.java serves as the POJO for storing information about Parametros
 * 
 */
public class ParametroActMod implements Serializable {

    private Integer id;
    private String proxAvisoCobro;
    private String proxReciboCobro;
    private String diasVencAvisoCobro;
    private String inteMora;
    private String iva;
    private String proxNotaDebCxC;
    private String proxNotaCredCxC;
    private String proxNotaDebCxP;
    private String proxNotaCredCxP;
    private String periodoActual;
    public String tipoFacturacion;
    private String mtoFijoFacturacion;
    private String porcMora;
    private String numDiasVenCxC;
    private String obsReciboPago;
    private String obsAvisoCobro;
    private String obsAvisoCobro2;
    private Condominio condominio;

    public ParametroActMod() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setProxAvisoCobro(String proxAvisoCobro) {
        this.proxAvisoCobro = proxAvisoCobro;
    }

    public String getProxAvisoCobro() {
        return proxAvisoCobro;
    }

    public void setDiasVencAvisoCobro(String diasVencAvisoCobro) {
        this.diasVencAvisoCobro = diasVencAvisoCobro;
    }

    public String getDiasVencAvisoCobro() {
        return diasVencAvisoCobro;
    }

    public void setProxReciboCobro(String proxReciboCobro) {
        this.proxReciboCobro = proxReciboCobro;
    }

    public String getProxReciboCobro() {
        return proxReciboCobro;
    }

    public void setInteMora(String inteMora) {
        this.inteMora = inteMora;
    }

    public String getInteMora() {
        return inteMora;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getIva() {
        return iva;
    }

    public void setProxNotaDebCxC(String proxNotaDebCxC) {
        this.proxNotaDebCxC = proxNotaDebCxC;
    }

    public String getProxNotaDebCxC() {
        return proxNotaDebCxC;
    }

    public void setProxNotaCredCxC(String proxNotaCredCxC) {
        this.proxNotaCredCxC = proxNotaCredCxC;
    }

    public String getProxNotaCredCxC() {
        return proxNotaCredCxC;
    }

    public void setProxNotaDebCxP(String proxNotaDebCxP) {
        this.proxNotaDebCxP = proxNotaDebCxP;
    }

    public String getProxNotaDebCxP() {
        return proxNotaDebCxP;
    }

    public void setProxNotaCredCxP(String proxNotaCredCxP) {
        this.proxNotaCredCxP = proxNotaCredCxP;
    }

    public String getProxNotaCredCxP() {
        return proxNotaCredCxP;
    }

    public void setPeriodoActual(String peridoActual) {
        this.periodoActual = peridoActual;
    }

    public String getPeriodoActual() {
        return periodoActual;
    }

    public void setTipoFacturacion(String tipoFacturacion) {
        this.tipoFacturacion = tipoFacturacion;
    }

    public String getTipoFacturacion() {
        return tipoFacturacion;
    }

    public void setMtoFijoFacturacion(String mtoFijoFacturacion) {
        this.mtoFijoFacturacion = mtoFijoFacturacion;
    }

    public String getMtoFijoFacturacion() {
        return mtoFijoFacturacion;
    }

    public void setPorcMora(String porcMora) {
        this.porcMora = porcMora;
    }

    public String getPorcMora() {
        return porcMora;
    }

    public void setNumDiasVenCxC(String numDiasVenCxC) {
        this.numDiasVenCxC = numDiasVenCxC;
    }

    public String getNumDiasVenCxC() {
        return numDiasVenCxC;
    }

    public void setObsReciboPago(String obsReciboPago) {
        this.obsReciboPago = obsReciboPago;
    }

    public String getObsReciboPago() {
        return obsReciboPago;
    }

    public void setObsAvisoCobro(String obsAvisoCobro) {
        this.obsAvisoCobro = obsAvisoCobro;
    }

    public String getObsAvisoCobro() {
        return obsAvisoCobro;
    }

    public void setObsAvisoCobro2(String obsAvisoCobro2) {
        this.obsAvisoCobro2 = obsAvisoCobro2;
    }

    public String getObsAvisoCobro2() {
        return obsAvisoCobro2;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
