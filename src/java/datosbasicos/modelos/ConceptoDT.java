package datosbasicos.modelos;

import java.io.Serializable;

public class ConceptoDT implements Serializable {

    private Integer id;
    private String nombConcepto;
    private String descConcepto;
    private String nombCategoria;
    private Condominio condominio;
    private Categoria categoria;

    public ConceptoDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setDescConcepto(String descConcepto) {
        this.descConcepto = descConcepto;
    }

    public String getDescConcepto() {
        return descConcepto;
    }

    public void setNombCategoria(String nombCategoria) {
        this.nombCategoria = nombCategoria;
    }

    public String getNombCategoria() {
        return nombCategoria;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
