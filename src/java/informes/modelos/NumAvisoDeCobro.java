package informes.modelos;

import java.io.Serializable;

public class NumAvisoDeCobro implements Serializable {

    private Integer id;
    private Integer numAviso;
    
    public NumAvisoDeCobro() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNumAviso(Integer numAviso) {
        this.numAviso = numAviso;
    }

    public Integer getNumAviso() {
        return numAviso;
    }

}
