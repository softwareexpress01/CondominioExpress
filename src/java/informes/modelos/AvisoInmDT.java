/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import java.io.Serializable;

/**
 *
 * @author Frank R.H.
 */
public class AvisoInmDT implements Serializable {

    private Integer id;
    
    private String codInm;
    private String propInm;
    private String telInm;
    private String emailInm;
    private Integer numAviso;
    
    private Double totCxC;
    private Double mtoAlicuota;
    private Double totGenCxC;

    /* Asociaciones */
    public AvisoInmDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodInm(String codInm) {
        this.codInm = codInm;
    }

    public String getCodInm() {
        return codInm;
    }

    public void setPropInm(String propInm) {
        this.propInm = propInm;
    }

    public String getPropInm() {
        return propInm;
    }

    public void setTelInm(String telInm) {
        this.telInm = telInm;
    }

    public String getTelInm() {
        return telInm;
    }

    public void setEmailInm(String emailInm) {
        this.emailInm = emailInm;
    }

    public String getEmailInm() {
        return emailInm;
    }
    
    public void setNumAviso(Integer numAviso) {
        this.numAviso = numAviso;
    }

    public Integer getNumAviso() {
        return numAviso;
    }

    public void setTotCxC(Double totCxC) {
        this.totCxC = totCxC;
    }
    public Double getTotCxC() {
        return totCxC;
    }
    
    public void setMtoAlicuota(Double mtoAlicuota) {
        this.mtoAlicuota = mtoAlicuota;
    }
    public Double getMtoAlicuota() {
        return mtoAlicuota;
    }
    
    public void setTotGenCxC(Double totGenCxC) {
        this.totGenCxC = totGenCxC;
    }
    public Double getTotGenCxC() {
        return totGenCxC;
    }
}
