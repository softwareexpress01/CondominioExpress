package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Calendar;

public class AjustesCond implements Serializable {

    private Integer id;
    private Calendar fecMovAjustesCond;
    public String tipoAjustesCond;
    private Integer idConcepto;
    public String descAjustesCond;
    private Double mtoAjustesCond;
    private Calendar perAjustesCond;
    public String edoAjustesCond;
 
    private Condominio condominio;
 
    public AjustesCond() {
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

    public void setCondominio(Condominio Condominio) {
        this.condominio = Condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public Calendar getFecMovAjustesCond() {
        return fecMovAjustesCond;
    }

    public void setFecMovAjustesCond(Calendar fecMovAjustesCond) {
        this.fecMovAjustesCond = fecMovAjustesCond;
    }

    public Calendar getPerAjustesCond() {
        return perAjustesCond;
    }

    public void setPerAjustesCond(Calendar perAjustesCond) {
        this.perAjustesCond = perAjustesCond;
    }

}
