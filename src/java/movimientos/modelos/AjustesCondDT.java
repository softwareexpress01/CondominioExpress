package movimientos.modelos;

import java.io.Serializable;

public class AjustesCondDT implements Serializable {

    private Integer id;
    private String fecMovAjustesCond;
    private String tipoAjustesCond;
    private Integer idConcepto;
    private String descAjustesCond;
    private Double mtoAjustesCond;
    private String mtoAjustesCondString;
    private String perAjustesCond;
    private String nombConcepto;
    private String edoAjustesCond;

    public AjustesCondDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMtoAjustesCond(Double mtoAjustesCond) {
        this.mtoAjustesCond = mtoAjustesCond;
    }

    public Double getMtoAjustesCond() {
        return mtoAjustesCond;
    }

    public void setMtoAjustesCondString(String mtoAjustesCondString) {
        this.mtoAjustesCondString = mtoAjustesCondString;
    }

    public String getMtoAjustesCondString() {
        return mtoAjustesCondString;
    }

    public String getEdoAjustesCond() {
        return edoAjustesCond;
    }

    public void setEdoAjustesCond(String edoAjustesCond) {
        this.edoAjustesCond = edoAjustesCond;
    }

    public String getTipoAjustesCond() {
        return tipoAjustesCond;
    }

    public void setTipoAjustesCond(String tipoAjustesCond) {
        this.tipoAjustesCond = tipoAjustesCond;
    }

    public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }
    public void setDescAjustesCond(String descAjustescond) {
        this.descAjustesCond = descAjustescond;
    }

    public String getDescAjustesCond() {
        return descAjustesCond;
    }

    public String getFecMovAjustesCond() {
        return fecMovAjustesCond;
    }

    public void setFecMovAjustesCond(String fecMovAjustesCond) {
        this.fecMovAjustesCond = fecMovAjustesCond;
    }

    public String getPerAjustesCond() {
        return perAjustesCond;
    }

    public void setPerAjustesCond(String perAjustesCond) {
        this.perAjustesCond = perAjustesCond;
    }

    public Object getRowKey(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getRowData(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
