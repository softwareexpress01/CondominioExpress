package informes.modelos;

import java.io.Serializable;

public class MovBanRepDT implements Serializable {

    private Integer id;
    private String fecMovBan;
    private String tipoMovBan;
    private String refMovBan;
    private String conceptoMovBan;
    private Double mtoMovBan;
    private Double abonoMovBan;

    public MovBanRepDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecMovBan(String fecMovBan) {
        this.fecMovBan = fecMovBan;
    }

    public String getFecMovBan() {
        return fecMovBan;
    }

    public void setTipoMovBan(String tipoMovBan) {
        this.tipoMovBan = tipoMovBan;
    }

    public String getTipoMovBan() {
        return tipoMovBan;
    }

    public void setRefMovBan(String refMovBan) {
        this.refMovBan = refMovBan;
    }

    public String getRefMovBan() {
        return refMovBan;
    }

    public void setConceptoMovBan(String conceptoMovBan) {
        this.conceptoMovBan = conceptoMovBan;
    }

    public String getConceptoMovBan() {
        return conceptoMovBan;
    }

    public void setMtoMovBan(Double mtoMovBan) {
        this.mtoMovBan = mtoMovBan;
    }

    public Double getMtoMovBan() {
        return mtoMovBan;
    }

    public void setAbonoMovBan(Double abonoMovBan) {
        this.abonoMovBan = abonoMovBan;
    }
    public Double getAbonoMovBan() {
        return abonoMovBan;
    }
}
