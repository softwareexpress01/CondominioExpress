/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import movimientos.modelos.TranFondo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author FrankRH
 */
public class Fondo implements Serializable {

    public Integer id;
    private String fecFondo;
    private String descFondo;
    private String tipoFondo;
    private Double porcentFondo;
    private Integer idBcoFondo;
    private Integer idCtaFondo;
    private Double mtoIniFondo;
    private Double saldoFondo;
    private String obsFondo;
    private String edoFondo;
    /* Asociaciones */
    private Condominio condominio;
    private Cuenta cuenta;
    
    private Collection<TranFondo> tranFondos = new ArrayList<TranFondo>();

    public Fondo() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecFondo(String fecFondo) {
        this.fecFondo = fecFondo;
    }

    public String getFecFondo() {
        return fecFondo;
    }

    public void setDescFondo(String descFondo) {
        this.descFondo = descFondo;
    }

    public String getDescFondo() {
        return descFondo;
    }

    public void setTipoFondo(String tipoFondo) {
        this.tipoFondo = tipoFondo;
    }

    public String getTipoFondo() {
        return tipoFondo;
    }

    public void setPorcentFondo(Double porcentFondo) {
        this.porcentFondo = porcentFondo;
    }

    public Double getPorcentFondo() {
        return porcentFondo;
    }

    public void setIdBcoFondo(Integer idBcoFondo) {
        this.idBcoFondo = idBcoFondo;
    }

    public Integer getIdBcoFondo() {
        return idBcoFondo;
    }

    public void setIdCtaFondo(Integer idCtaFondo) {
        this.idCtaFondo = idCtaFondo;
    }

    public Integer getIdCtaFondo() {
        return idCtaFondo;
    }

    public void setMtoIniFondo(Double mtoIniFondo) {
        this.mtoIniFondo = mtoIniFondo;
    }

    public Double getMtoIniFondo() {
        return mtoIniFondo;
    }

    public void setSaldoFondo(Double saldoFondo) {
        this.saldoFondo = saldoFondo;
    }

    public Double getSaldoFondo() {
        return saldoFondo;

    }

    public void setObsFondo(String obsFondo) {
        this.obsFondo = obsFondo;
    }

    public String getObsFondo() {
        return obsFondo;

    }
    public void setEdoFondo(String edoFondo) {
        this.edoFondo = edoFondo;
    }

    public String getEdoFondo() {
        return edoFondo;

    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
    
    public void setTranFondos(Collection<TranFondo> tranFondos) {
        this.tranFondos = tranFondos;
    }

    public Collection<TranFondo> getTranFondos() {
        return tranFondos;
    }

    public void addTranFondos(TranFondo tranFondos) {
        this.tranFondos.add(tranFondos);
    }
}
