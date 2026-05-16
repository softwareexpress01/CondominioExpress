package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class FacGastoNoComun implements Serializable {

    private Integer id;
    private Calendar fecFacGNC;
    private Double mtoFacGastoNoComun;
    private Double saldoFacGastoNoComun;
    private String edoFacGastoNoComun;
    private GastoNoComun gastoNoComun;
    private Inmueble inmueble;

    public FacGastoNoComun() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecFacGNC(Calendar fecFacGNC) {
        this.fecFacGNC = fecFacGNC;
    }

    public Calendar getFecFacGNC() {
        return fecFacGNC;
    }

    public void setMtoFacGastoNoComun(Double mtoFacGastoNoComun) {
        this.mtoFacGastoNoComun = mtoFacGastoNoComun;
    }

    public Double getMtoFacGastoNoComun() {
        return mtoFacGastoNoComun;
    }

    public String getEdoFacGastoNoComun() {
        return edoFacGastoNoComun;
    }

    public void setEdoFacGastoNoComun(String edoFacGastoNoComun) {
        this.edoFacGastoNoComun = edoFacGastoNoComun;
    }

    public Double getSaldoFacGastoNoComun() {
        return saldoFacGastoNoComun;
    }

    public void setSaldoFacGastoNoComun(Double saldoFacGastoNoComun) {
        this.saldoFacGastoNoComun = saldoFacGastoNoComun;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setGastoNoComun(GastoNoComun gastoNoComun) {
        this.gastoNoComun = gastoNoComun;
    }

    public GastoNoComun getGastoNoComun() {
        return gastoNoComun;
    }
}