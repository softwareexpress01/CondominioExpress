package configuracion.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;

/**
 * Parametro.java serves as the POJO for storing information about Parametros
 * 
 */
public class Parametro implements Serializable {

    private Integer id;
    private Integer proxAvisoCobro;
    private Integer proxReciboCobro;
    private Integer proxNotaDebCxC;
    private Integer proxNotaCredCxC;
    private Integer proxNotaDebCxP;
    private Integer proxNotaCredCxP;
    private String tipoFacturacion;
    private Double mtoFijoFacturacion;
    private Float porcMora;
    private Integer numDiasVenCxC; 
    private String obsReciboPago;
    private String obsAvisoCobro;
    private String obsAvisoCobro2;
    private Condominio condominio;

    public Parametro() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setProxAvisoCobro(Integer proxAvisoCobro) {
        this.proxAvisoCobro = proxAvisoCobro;
    }

    public Integer getProxAvisoCobro() {
        return proxAvisoCobro;
    }

    public void setProxReciboCobro(Integer proxReciboCobro) {
        this.proxReciboCobro = proxReciboCobro;
    }

    public Integer getProxReciboCobro() {
        return proxReciboCobro;
    }

    public void setProxNotaDebCxC(Integer proxNotaDebCxC) {
        this.proxNotaDebCxC = proxNotaDebCxC;
    }

    public Integer getProxNotaDebCxC() {
        return proxNotaDebCxC;
    }

    public void setProxNotaCredCxC(Integer proxNotaCredCxC) {
        this.proxNotaCredCxC = proxNotaCredCxC;
    }

    public Integer getProxNotaCredCxC() {
        return proxNotaCredCxC;
    }

    public void setProxNotaDebCxP(Integer proxNotaDebCxP) {
        this.proxNotaDebCxP = proxNotaDebCxP;
    }

    public Integer getProxNotaDebCxP() {
        return proxNotaDebCxP;
    }

    public void setProxNotaCredCxP(Integer proxNotaCredCxP) {
        this.proxNotaCredCxP = proxNotaCredCxP;
    }

    public Integer getProxNotaCredCxP() {
        return proxNotaCredCxP;
    }

    public void setTipoFacturacion(String tipoFacturacion) {
        this.tipoFacturacion = tipoFacturacion;
    }

    public String getTipoFacturacion() {
        return tipoFacturacion;
    }

    public void setMtoFijoFacturacion(Double mtoFijoFacturacion) {
        this.mtoFijoFacturacion = mtoFijoFacturacion;
    }

    public Double getMtoFijoFacturacion() {
        return mtoFijoFacturacion;
    }

    public void setPorcMora(Float porcMora) {
        this.porcMora = porcMora;
    }

    public Float getPorcMora() {
        return porcMora;
    }
public void setNumDiasVenCxC(Integer numDiasVenCxC) {
        this.numDiasVenCxC = numDiasVenCxC;
    }

    public Integer getNumDiasVenCxC() {
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
