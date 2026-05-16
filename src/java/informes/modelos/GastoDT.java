package informes.modelos;

import java.io.Serializable;

public class GastoDT implements Serializable {

    private Integer id;
    private String desCategoria;
    private Double mtoCategoria;

    public GastoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDesCategoria(String desCategoria) {
        this.desCategoria = desCategoria;
    }

    public String getDesCategoria() {
        return desCategoria;
    }

    public void setMtoCategoria(Double mtoCategoria) {
        this.mtoCategoria = mtoCategoria;
    }

    public Double getMtoCategoria() {
        return mtoCategoria;
    }
}
