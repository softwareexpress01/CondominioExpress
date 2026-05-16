package movimientos.modelos;


import java.io.Serializable;


public class PagoFacFonRes implements Serializable {

    private Integer id;
    private Double mtoPagadoFonRes;
    private String discFacFonRes;
    private Integer idFacFonRes;
    
    private PagoFonRes pagoFonRes;
    
    
    public PagoFacFonRes() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Double getMtoPagadoFonRes() {
        return mtoPagadoFonRes;
    }

    public void setMtoPagadoFonRes(Double mtoPagadoFonRes) {
        this.mtoPagadoFonRes = mtoPagadoFonRes;
    }

    public String getDiscFacFonRes() {
        return discFacFonRes;
    }

    public void setDiscFacFonRes(String discFacFonRes) {
        this.discFacFonRes = discFacFonRes;
    }

    public Integer getIdFacFonRes() {
        return idFacFonRes;
    }

    public void setIdFacFonRes(Integer idFacFonRes) {
        this.idFacFonRes = idFacFonRes;
    }

    
    public void setPagoFonRes(PagoFonRes pagoFonRes) {
        this.pagoFonRes = pagoFonRes;
    }

    public PagoFonRes getPagoFonRes() {
        return pagoFonRes;
    }
}