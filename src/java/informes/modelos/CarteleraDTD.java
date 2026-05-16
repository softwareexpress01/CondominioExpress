package informes.modelos;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cartelera.java serves as the POJO for storing information about inmuebles
 */
public class CarteleraDTD implements Serializable {

    private Integer id;
    private String nombPropiedad;
    private String codInmueble;
    private String nombPropietario;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoCxCP1;
    private BigDecimal saldoCxCP2;
    private BigDecimal saldoCxCP3;
    private BigDecimal saldoCxCP4;
    private BigDecimal saldoCxCP5;
    private BigDecimal saldoCxCP6;
    private BigDecimal saldoTotal;

    public CarteleraDTD() {
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

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoCxCP1(BigDecimal saldoCxCP1) {
        this.saldoCxCP1 = saldoCxCP1;
    }

    public BigDecimal getSaldoCxCP1() {
        return saldoCxCP1;
    }

    public void setSaldoCxCP2(BigDecimal saldoCxCP2) {
        this.saldoCxCP2 = saldoCxCP2;
    }

    public BigDecimal getSaldoCxCP2() {
        return saldoCxCP2;
    }

    public void setSaldoCxCP3(BigDecimal saldoCxCP3) {
        this.saldoCxCP3 = saldoCxCP3;
    }

    public BigDecimal getSaldoCxCP3() {
        return saldoCxCP3;
    }

    public void setSaldoCxCP4(BigDecimal saldoCxCP4) {
        this.saldoCxCP4 = saldoCxCP4;
    }

    public BigDecimal getSaldoCxCP4() {
        return saldoCxCP4;
    }

    public void setSaldoCxCP5(BigDecimal saldoCxCP5) {
        this.saldoCxCP5 = saldoCxCP5;
    }

    public BigDecimal getSaldoCxCP5() {
        return saldoCxCP5;
    }

    public void setSaldoCxCP6(BigDecimal saldoCxCP6) {
        this.saldoCxCP6 = saldoCxCP6;
    }

    public BigDecimal getSaldoCxCP6() {
        return saldoCxCP6;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }
}
