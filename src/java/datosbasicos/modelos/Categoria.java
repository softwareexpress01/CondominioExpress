package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * UserBean.java serves as the POJO for storing information about a Virtual
 * Trainer User.
 */
public class Categoria implements Serializable {

    private Integer id;
    private String nombCategoria;
    private String descCategoria;
    private String edoCategoria;
    private Condominio condominio;
    private Collection<Concepto> conceptos = new ArrayList<Concepto>();

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion, String estado, Condominio cond) {

        nombCategoria=nombre;
        descCategoria=descripcion;
        edoCategoria=estado;
        condominio=cond;

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombCategoria(String nombCategoria) {
        this.nombCategoria = nombCategoria;
    }

    public String getNombCategoria() {
        return nombCategoria;
    }

    public void setDescCategoria(String descCategoria) {
        this.descCategoria = descCategoria;
    }

    public String getDescCategoria() {
        return descCategoria;
    }

    public void setEdoCategoria(String edoCategoria) {
        this.edoCategoria = edoCategoria;
    }

    public String getEdoCategoria() {
        return edoCategoria;
    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }

    public void setConceptos(Collection<Concepto> conceptos) {
        this.conceptos = conceptos;
    }

    public Collection<Concepto> getConceptos() {
        return conceptos;
    }

    public void addConceptos(Concepto concepto) {
        this.conceptos.add(concepto);
    }
}
