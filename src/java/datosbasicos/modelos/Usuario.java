package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import saas.modelos.Suscripcion;

/**
 * UserBean.java serves as the POJO for storing information about a Virtual
 * Trainer User.
 */
public class Usuario implements Serializable {

    private Integer id;
    private String nombUsuario;
    private String apelUsuario;
    private String cedUsuario;
    private String sexoUsuario;
    private String emailUsuario;
    private String codUsuario;
    private String contUsuario;
    private String telUsuario;
    private String celUsuario;
    private String perUsuario;
    /** Asociaciones*/
    private Suscripcion suscripcion;
    private Usuario usuarioAdmin;
    public List<Condominio> condominios = new ArrayList<Condominio>();
    private Collection<Usuario> operadores = new ArrayList<Usuario>();

    public Usuario() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombUsuario(String nombUsuario) {
        this.nombUsuario = nombUsuario;
    }

    public String getNombUsuario() {
        return nombUsuario;
    }

    public void setCedUsuario(String cedUsuario) {
        this.cedUsuario = cedUsuario;
    }

    public String getCedUsuario() {
        return cedUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setContUsuario(String contUsuario) {
        this.contUsuario = contUsuario;
    }

    public String getContUsuario() {
        return contUsuario;
    }

    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }

    public void setApelUsuario(String apelUsuario) {
        this.apelUsuario = apelUsuario;
    }

    public String getApelUsuario() {
        return apelUsuario;
    }

    public void setSexoUsuario(String sexoUsuario) {
        this.sexoUsuario = sexoUsuario;
    }

    public String getSexoUsuario() {
        return sexoUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCelUsuario(String celUsuario) {
        this.celUsuario = celUsuario;
    }

    public String getCelUsuario() {
        return celUsuario;
    }

    public void setPerUsuario(String perUsuario) {
        this.perUsuario = perUsuario;
    }

    public String getPerUsuario() {
        return perUsuario;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setUsuarioAdmin(Usuario usuarioAdmin) {
        this.usuarioAdmin = usuarioAdmin;
    }

    public Usuario getUsuarioAdmin() {
        return usuarioAdmin;
    }

    public void setOperadores(Collection<Usuario> operadores) {
        this.operadores = operadores;
    }

    public Collection<Usuario> getOperadores() {
        return operadores;
    }

    public List<Condominio> getCondominios() {
        return condominios;
    }

    public void setCondominios(List<Condominio> condominios) {
        this.condominios = condominios;
    }

    public void addCondominios(Condominio condominio) {
        this.condominios.add(condominio);
    }
    public void addOperadores(Usuario operador) {
        this.operadores.add(operador);
    }
}
