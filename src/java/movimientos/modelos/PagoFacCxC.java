package movimientos.modelos;


import java.io.Serializable;


public class PagoFacCxC implements Serializable {

    private Integer id;
    private Double mtoPagadoCxC;
    private String discFacCxC;
    private Integer idFacCxC;
    private PagoCxC pagoCxC;

    public PagoFacCxC() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Double getMtoPagadoCxC() {
        return mtoPagadoCxC;
    }

    public void setMtoPagadoCxC(Double mtoPagadoCxC) {
        this.mtoPagadoCxC = mtoPagadoCxC;
    }

    public String getDiscFacCxC() {
        return discFacCxC;
    }

    public void setDiscFacCxC(String discFacCxC) {
        this.discFacCxC = discFacCxC;
    }

    public Integer getIdFacCxC() {
        return idFacCxC;
    }

    public void setIdFacCxC(Integer idFacCxC) {
        this.idFacCxC = idFacCxC;
    }

    
    public void setPagoCxC(PagoCxC pagoCxC) {
        this.pagoCxC = pagoCxC;
    }

    public PagoCxC getPagoCxC() {
        return pagoCxC;
    }
}