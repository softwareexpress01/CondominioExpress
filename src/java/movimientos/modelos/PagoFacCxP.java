package movimientos.modelos;


import java.io.Serializable;
import movimientos.modelos.PagoCxP;


public class PagoFacCxP implements Serializable {

    private Integer id;
    private Double mtoPagadoCxP;
    private String discFacCxP;
    private Integer idFacCxP;
    
    private PagoCxP pagoCxP;
    
    
    public PagoFacCxP() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Double getMtoPagadoCxP() {
        return mtoPagadoCxP;
    }

    public void setMtoPagadoCxP(Double mtoPagadoCxP) {
        this.mtoPagadoCxP = mtoPagadoCxP;
    }

    public String getDiscFacCxP() {
        return discFacCxP;
    }

    public void setDiscFacCxP(String discFacCxP) {
        this.discFacCxP = discFacCxP;
    }

    public Integer getIdFacCxP() {
        return idFacCxP;
    }

    public void setIdFacCxP(Integer idFacCxP) {
        this.idFacCxP = idFacCxP;
    }

    
    public void setPagoCxP(PagoCxP pagoCxP) {
        this.pagoCxP = pagoCxP;
    }

    public PagoCxP getPagoCxP() {
        return pagoCxP;
    }
}