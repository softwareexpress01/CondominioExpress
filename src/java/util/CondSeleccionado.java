/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author jesus
 */


public class CondSeleccionado {
    
    private Integer id;
    private String nombCondominio;
    private String dirCondominio;
    private String nombAdmin ;
    private String emailCondominio;
    private String cedRifCondominio;
    private String telOficina;
    private String telAdmin;

    public CondSeleccionado() {
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

    public void setNombAdmin(String nombAdmin) {
        this.nombAdmin = nombAdmin;
    }

    public String getNombAdmin() {
        return nombAdmin;
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

    public void setTelOficina(String telOficina) {
        this.telOficina = telOficina;
    }

    public String getTelOficina() {
        return telOficina;

    }

    public String getTelAdmin() {
        return telAdmin;

    }
    
    public void setTelAdmin(String telAdmin) {
        this.telAdmin = telAdmin;
    }




}
