package informes.modelos;

import java.io.Serializable;

/**
 * Cartelera.java serves as the POJO for storing information about inmuebles
 */
public class CarteleraDT implements Serializable {

    private Integer id;
    private String nombPropiedad;
    private String codInmueble;
    private String nombPropietario;
    private Double saldoAnterior;
    private Double saldoCxCP1;
    private Double saldoCxCP2;
    private Double saldoCxCP3;
    private Double saldoCxCP4;
    private Double saldoCxCP5;
    private Double saldoCxCP6;
    private Double saldoTotal;

    public CarteleraDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setNombPropiedad(String nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public String getNombPropiedad() {
        return nombPropiedad;
    }

    public void setNombPropietario(String nombPropietario) {
        this.nombPropietario = nombPropietario;
    }

    public String getNombPropietario() {
        return nombPropietario;
    }

    public void setSaldoAnterior(Double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public Double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoCxCP1(Double saldoCxCP1) {
        this.saldoCxCP1 = saldoCxCP1;
    }

    public Double getSaldoCxCP1() {
        return saldoCxCP1;
    }

    public void setSaldoCxCP2(Double saldoCxCP2) {
        this.saldoCxCP2 = saldoCxCP2;
    }

    public Double getSaldoCxCP2() {
        return saldoCxCP2;
    }

    public void setSaldoCxCP3(Double saldoCxCP3) {
        this.saldoCxCP3 = saldoCxCP3;
    }

    public Double getSaldoCxCP3() {
        return saldoCxCP3;
    }

    public void setSaldoCxCP4(Double saldoCxCP4) {
        this.saldoCxCP4 = saldoCxCP4;
    }

    public Double getSaldoCxCP4() {
        return saldoCxCP4;
    }

    public void setSaldoCxCP5(Double saldoCxCP5) {
        this.saldoCxCP5 = saldoCxCP5;
    }

    public Double getSaldoCxCP5() {
        return saldoCxCP5;
    }

    public void setSaldoCxCP6(Double saldoCxCP6) {
        this.saldoCxCP6 = saldoCxCP6;
    }

    public Double getSaldoCxCP6() {
        return saldoCxCP6;
    }

    public void setSaldoTotal(Double saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public Double getSaldoTotal() {
        return saldoTotal;
    }
}
