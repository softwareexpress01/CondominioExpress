package informes.modelos;

import java.io.Serializable;
import java.util.Calendar;

public class PeriodoDT implements Serializable {

    private Integer id;
    private Calendar fecDesde;
    private Calendar fecHasta;
    private Double saldoPeriodo;
    private Double indicadorPeriodo;
    private String desPeriodo;
    private String numPeriodo;

    public PeriodoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecDesde(Calendar fecDesde) {
        this.fecDesde = fecDesde;
    }

    public Calendar getFecDesde() {
        return fecDesde;
    }

    public void setFecHasta(Calendar fecHasta) {
        this.fecHasta = fecHasta;
    }

    public Calendar getFecHasta() {
        return fecHasta;
    }

    public void setSaldoPeriodo(Double saldoPeriodo) {
        this.saldoPeriodo = saldoPeriodo;
    }

    public Double getSaldoPeriodo() {
        return saldoPeriodo;
    }

    public void setIndicadorPeriodo(Double indicadorPeriodo) {
        this.indicadorPeriodo = indicadorPeriodo;
    }

    public Double getIndicadorPeriodo() {
        return indicadorPeriodo;
    }

    public String getDesPeriodo() {
        return desPeriodo;
    }

    public void setDesPeriodo(String desPeriodo) {
        this.desPeriodo = desPeriodo;
    }
    public String getNumPeriodo() {
        return numPeriodo;
    }

    public void setNumPeriodo(String numPeriodo) {
        this.numPeriodo = numPeriodo;
    }
}
