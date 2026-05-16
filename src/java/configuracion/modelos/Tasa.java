
package configuracion.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author FrankRH
 */
public class Tasa implements Serializable {

    private Integer id;
    private Calendar periodoTasa;
    private Float mtoTasa;
    /*
     * Asociaciones
     */
    private Condominio condominio;

    public Tasa() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setPeriodoTasa(Calendar periodoTasa) {
        this.periodoTasa = periodoTasa;
    }

    public Calendar getPeriodoTasa() {
        return periodoTasa;
    }

    public void setMtoTasa(Float mtoTasa) {
        this.mtoTasa = mtoTasa;
    }

    public Float getMtoTasa() {
        return mtoTasa;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}