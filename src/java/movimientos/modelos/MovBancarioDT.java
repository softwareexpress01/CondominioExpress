/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import java.io.Serializable;
import java.util.Date;
import datosbasicos.modelos.Cuenta;

/**
 *
 * @author FrankRH
 */
public class MovBancarioDT implements Serializable {

    public Integer id;
    private Integer idBcoMovBancario;
    private String nombBcoMovBancario;
    private Integer idCtaMovBancario;
    private String descCtaMovBancario;
    private String tipoOperMovBancario;
    private String fecMovBancario;
    private String refMovBancario;
    private Double mtoMovBancario;
    private String mtoMovBancarioString;
    private String descMovBancario;
    /* Asociaciones */
    private Cuenta cuenta;

    public MovBancarioDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdBcoMovBancario(Integer idBcoMovBancario) {
        this.idBcoMovBancario = idBcoMovBancario;
    }

    public Integer getIdBcoMovBancario() {
        return idBcoMovBancario;
    }

    public void setNombBcoMovBancario(String nombBcoMovBancario) {
        this.nombBcoMovBancario = nombBcoMovBancario;
    }

    public String getNombBcoMovBancario() {
        return nombBcoMovBancario;
    }

    public void setIdCtaMovBancario(Integer idCtaMovBancario) {
        this.idCtaMovBancario = idCtaMovBancario;
    }

    public Integer getIdCtaMovBancario() {
        return idCtaMovBancario;
    }

    public void setDescCtaMovBancario(String descCtaMovBancario) {
        this.descCtaMovBancario = descCtaMovBancario;
    }

    public String getDescCtaMovBancario() {
        return descCtaMovBancario;
    }

    public void setTipoOperMovBancario(String tipoOperMovBancario) {
        this.tipoOperMovBancario = tipoOperMovBancario;
    }

    public String getTipoOperMovBancario() {
        return tipoOperMovBancario;
    }

    public void setFecMovBancario(String fecMovBancario) {
        this.fecMovBancario = fecMovBancario;
    }

    public String getFecMovBancario() {
        return fecMovBancario;
    }

    public void setRefMovBancario(String refMovBancario) {
        this.refMovBancario = refMovBancario;
    }

    public String getRefMovBancario() {
        return refMovBancario;
    }

    public void setMtoMovBancario(Double mtoMovBancario) {
        this.mtoMovBancario = mtoMovBancario;
    }

    public Double getMtoMovBancario() {
        return mtoMovBancario;
    }

    public void setMtoMovBancarioString(String mtoMovBancarioString) {
        this.mtoMovBancarioString = mtoMovBancarioString;
    }

    public String getMtoMovBancarioString() {
        return mtoMovBancarioString;
    }

    public void setDescMovBancario(String descMovBancario) {
        this.descMovBancario = descMovBancario;
    }

    public String getDescMovBancario() {
        return descMovBancario;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
}
