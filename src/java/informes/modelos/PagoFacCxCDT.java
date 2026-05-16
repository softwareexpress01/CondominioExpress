package informes.modelos;

import java.io.Serializable;

public class PagoFacCxCDT implements Serializable {

    private Integer id;
    private String codInmueble;
    private String fecEmisionCxC;
    private String tipoCxC;
    private String desCxC;
    private Double mtoCxC;
    private Double saldoCxC;
    private Double mtoPagadoCxC;

    public PagoFacCxCDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getFecEmisionCxC() {
        return fecEmisionCxC;
    }

    public void setFecEmisionCxC(String fecEmisionCxC) {
        this.fecEmisionCxC = fecEmisionCxC;
    }

    public String getTipoCxC() {
        return tipoCxC;
    }

    public void setTipoCxC(String tipoCxC) {
        this.tipoCxC = tipoCxC;
    }

    public String getDesCxC() {
        return desCxC;
    }

    public void setDesCxC(String desCxC) {
        this.desCxC = desCxC;
    }

    public Double getMtoCxC() {
        return mtoCxC;
    }

    public void setMtoCxC(Double mtoCxC) {
        this.mtoCxC = mtoCxC;
    }

    public Double getSaldoCxC() {
        return saldoCxC;
    }

    public void setSaldoCxC(Double saldoCxC) {
        this.saldoCxC = saldoCxC;
    }

    public Double getMtoPagadoCxC() {
        return mtoPagadoCxC;
    }

    public void setMtoPagadoCxC(Double mtoPagadoCxC) {
        this.mtoPagadoCxC = mtoPagadoCxC;
    }
}