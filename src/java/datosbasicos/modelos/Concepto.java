package datosbasicos.modelos;

import java.io.Serializable;

public class Concepto implements Serializable {

    private Integer id;
    private String nombConcepto;
    private String descConcepto;
    private String edoConcepto;
    private Condominio condominio;
    private Categoria categoria;

    public Concepto() {
    }

    public Concepto(String nombre, String descripcion, String estado, Condominio cond) {

        nombConcepto=nombre;
        descConcepto=descripcion;
        edoConcepto=estado;
        condominio=cond;

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

    public void setEdoConcepto(String edoConcepto) {
        this.edoConcepto = edoConcepto;
    }

    public String getEdoConcepto() {
        return edoConcepto;
    }
}
