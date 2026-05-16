package informes.modelos;

import java.io.Serializable;
import java.util.Calendar;

public class MovimientoCxPDT implements Serializable {

    private Integer id;
    private String nombProveedor;
    private String fecMovCxP;
    private Calendar calFecMovCxP;
    private String numDocPago;
    private String mtoMovCxP;
    private String abonoMovCxP;
    private Double saldoMovCxP;
    private String discMovCxP;
    private String desMovCxP;

    public MovimientoCxPDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecMovCxP(String fecMovCxP) {
        this.fecMovCxP = fecMovCxP;
    }

    public String getFecMovCxP() {
        return fecMovCxP;
    }
    
    public void setCalFecMovCxP(Calendar calFecMovCxP) {
        this.calFecMovCxP = calFecMovCxP;
    }

    public Calendar getCalFecMovCxP() {
        return calFecMovCxP;
    }
   

    public void setMtoMovCxP(String mtoMovCxP) {
        this.mtoMovCxP = mtoMovCxP;
    }

    public String getMtoMovCxP() {
        return mtoMovCxP;
    }

    public void setAbonoMovCxP(String abonoMovCxP) {
        this.abonoMovCxP = abonoMovCxP;
    }

    public String getAbonoMovCxP() {
        return abonoMovCxP;
    }

    public String getDiscMovCxP() {
        return discMovCxP;
    }

    public void setDiscMovCxP(String discMovCxP) {
        this.discMovCxP = discMovCxP;
    }

    public Double getSaldoMovCxP() {
        return saldoMovCxP;
    }

    public void setSaldoMovCxP(Double saldoMovCxP) {
        this.saldoMovCxP = saldoMovCxP;
    }

    public String getDesMovCxP() {
        return desMovCxP;
    }

    public void setDesMovCxP(String desMovCxP) {
        this.desMovCxP = desMovCxP;
    }
    public String getNumDocPago() {
        return numDocPago;
    }

    public void setNumDocPago(String numDocPago) {
        this.numDocPago = numDocPago;
    }
    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }
}
