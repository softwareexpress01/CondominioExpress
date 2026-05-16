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
public class AvisoDeCobroDT implements Serializable {

    private Integer id;
    private Integer grupo;
    private String codInm;
    private String propInm;
    private String telInm;
    private String dirInm;
    private String codAcc;
    private String emailInm;
    private Double alicuotaInm;
    private Integer numAviso;
    private Double totCxC;
    private Double mtoAlicuota;
    private Double totalMes;
    private Double totGenCxC;
    private String fecGasto;
    private String conceptoGasto;
    private String refGasto;
    private String provGasto;
    private Double mtoGasto;
    private Double alicuotaGasto;

    /* Asociaciones */
    public AvisoDeCobroDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }

    public Integer getGrupo() {
        return grupo;
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
    
    public void setDirInm(String dirInm) {
        this.dirInm = dirInm;
    }

    public String getDirInm() {
        return dirInm;
    }
    
    public void setCodAcc(String codAcc) {
        this.codAcc = codAcc;
    }

    public String getCodAcc() {
        return codAcc;
    }

    public void setEmailInm(String emailInm) {
        this.emailInm = emailInm;
    }

    public String getEmailInm() {
        return emailInm;
    }
    public void setAlicuotaInm(Double alicuotaInm) {
        this.alicuotaInm = alicuotaInm;
    }
    public Double getAlicuotaInm() {
        return alicuotaInm;
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
    
    public void setTotalMes(Double totalMes) {
        this.totalMes = totalMes;
    }
    public Double getTotalMes() {
        return totalMes;
    }
    
    public void setTotGenCxC(Double totGenCxC) {
        this.totGenCxC = totGenCxC;
    }
    public Double getTotGenCxC() {
        return totGenCxC;
    }

    public void setFecGasto(String fecGasto) {
        this.fecGasto = fecGasto;
    }

    public String getFecGasto() {
        return fecGasto;
    }

    public void setConceptoGasto(String conceptoGasto) {
        this.conceptoGasto = conceptoGasto;
    }

    public String getConceptoGasto() {
        return conceptoGasto;
    }

    public void setRefGasto(String refGasto) {
        this.refGasto = refGasto;
    }

    public String getRefGasto() {
        return refGasto;
    }

    public void setProvGasto(String provGasto) {
        this.provGasto = provGasto;
    }

    public String getProvGasto() {
        return provGasto;
    }

    public void setMtoGasto(Double mtoGasto) {
        this.mtoGasto = mtoGasto;
    }

    public Double getMtoGasto() {
        return mtoGasto;
    }

    public void setAlicuotaGasto(Double alicuotaGasto) {
        this.alicuotaGasto = alicuotaGasto;
    }

    public Double getAlicuotaGasto() {
        return alicuotaGasto;
    }
}
