/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import java.io.Serializable;
import java.util.Calendar;


/**
 *
 * @author Frank
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
    private String nombLogo;
    private String periodoActual;
    private Calendar fecCreacion;
    
    
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

    public void setNombLogo(String nombLogo) {
        this.nombLogo = nombLogo;
    }

    public String getNombLogo() {
        return nombLogo;
    }

    public String getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(String periodoActual) {
        this.periodoActual = periodoActual;
    }

    public Calendar getFecCreacion() {
        return fecCreacion;
    }

    public void setFecCreacion(Calendar fecCreacion) {
        this.fecCreacion = fecCreacion;
    }


}
 