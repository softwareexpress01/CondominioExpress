package informes.modelos;

import java.io.Serializable;

/**
 *
 * @author jesus
 */
public class CondominioDT implements Serializable {

    private Integer id;
    private String nombCondominio;
    private String dirCondominio;
    private String nombAdminCondominio;
    private String emailCondominio;
    private String cedRifCondominio;
    private String telOficCondominio;
    private String telAdminCondominio;
    private Integer numPropiedades;
    private Integer numInmuebles;

    public CondominioDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombCondominio(String nombCondominio) {
        this.nombCondominio = nombCondominio;
    }

    public String getNombCondominio() {
        return nombCondominio;
    }

    public void setDirCondominio(String dirCondominio) {
        this.dirCondominio = dirCondominio;
    }

    public String getDirCondominio() {
        return dirCondominio;
    }

    public void setNombAdminCondominio(String nombAdminCondominio) {
        this.nombAdminCondominio = nombAdminCondominio;
    }

    public String getNombAdminCondominio() {
        return nombAdminCondominio;
    }

    public void setEmailCondominio(String emailCondominio) {
        this.emailCondominio = emailCondominio;
    }

    public String getEmailCondominio() {
        return emailCondominio;
    }

    public void setCedRifCondominio(String cedRifCondominio) {
        this.cedRifCondominio = cedRifCondominio;
    }

    public String getCedRifCondominio() {
        return cedRifCondominio;
    }

    public void setTelOficCondominio(String telOficCondominio) {
        this.telOficCondominio = telOficCondominio;
    }

    public String getTelOficCondominio() {
        return telOficCondominio;

    }

    public void setTelAdminCondominio(String telAdminCondominio) {
        this.telAdminCondominio = telAdminCondominio;
    }

    public String getTelAdminCondominio() {
        return telAdminCondominio;
    }

    public void setNumPropiedades(Integer numPropiedades) {
        this.numPropiedades = numPropiedades;
    }

    public Integer getNumPropiedades() {
        return numPropiedades;
    }
    public void setNumInmuebles(Integer numInmuebles) {
        this.numInmuebles = numInmuebles;
    }

    public Integer getNumInmuebles() {
        return numInmuebles;
    }
}
