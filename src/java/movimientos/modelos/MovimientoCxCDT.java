package movimientos.modelos;

import java.io.Serializable;
import java.util.Calendar;

public class MovimientoCxCDT implements Serializable {

    private Integer id;
    private String codInmueble;
    private String fecMovCxC;
    private Calendar calFecMovCxC;
    private String numDocPago;
    private Double mtoMovCxC;
    private Double abonoMovCxC;
    private Double saldoMovCxC;
    private String discMovCxC;
    private String desMovCxC;

    public MovimientoCxCDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecMovCxC(String fecMovCxC) {
        this.fecMovCxC = fecMovCxC;
    }

    public String getFecMovCxC() {
        return fecMovCxC;
    }

    public void setCalFecMovCxC(Calendar calFecMovCxC) {
        this.calFecMovCxC = calFecMovCxC;
    }

    public Calendar getCalFecMovCxC() {
        return calFecMovCxC;
    }

    public void setMtoMovCxC(Double mtoMovCxC) {
        this.mtoMovCxC = mtoMovCxC;
    }

    public Double getMtoMovCxC() {
        return mtoMovCxC;
    }

    public void setAbonoMovCxC(Double abonoMovCxC) {
        this.abonoMovCxC = abonoMovCxC;
    }

    public Double getAbonoMovCxC() {
        return abonoMovCxC;
    }

    public String getDiscMovCxC() {
        return discMovCxC;
    }

    public void setDiscMovCxC(String discMovCxC) {
        this.discMovCxC = discMovCxC;
    }

    public Double getSaldoMovCxC() {
        return saldoMovCxC;
    }

    public void setSaldoMovCxC(Double saldoMovCxC) {
        this.saldoMovCxC = saldoMovCxC;
    }

    public String getDesMovCxC() {
        return desMovCxC;
    }

    public void setDesMovCxC(String desMovCxC) {
        this.desMovCxC = desMovCxC;
    }

    public String getNumDocPago() {
        return numDocPago;
    }

    public void setNumDocPago(String numDocPago) {
        this.numDocPago = numDocPago;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }
}
