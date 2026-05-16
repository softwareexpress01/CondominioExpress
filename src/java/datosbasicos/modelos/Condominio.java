/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import acceso.modelos.CodigoAcceso;
import configuracion.modelos.Parametro;
import configuracion.modelos.Tasa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import movimientos.modelos.*;

/**
 *
 * @author jesus
 */
public class Condominio implements Serializable {

    private Integer id;
    private String nombCondominio;
    private String dirCondominio;
    private String nombAdminCondominio;
    private String emailCondominio;
    private String cedRifCondominio;
    private String telOficCondominio;
    private String telAdminCondominio;
    private String nombLogo;
    private Calendar periodoActual;
    private Calendar fecCreacion;
    private String edoCondominio; 
    /** Asociaciones */
    private List<Usuario> usuarios = new ArrayList<Usuario>();
    public Collection<Propiedad> propiedades = new ArrayList<Propiedad>();
    private Collection<Categoria> categorias = new ArrayList<Categoria>();
    private Collection<Concepto> conceptos = new ArrayList<Concepto>();
    private Collection<Proveedor> proveedores = new ArrayList<Proveedor>();
    private Collection<GastoComunOrd> gastosComunesOrd = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunExt> gastosComunesExt = new ArrayList<GastoComunExt>();
    private Collection<GastoNoComun> gastosNoComunes = new ArrayList<GastoNoComun>();
    private Collection<GastoPropiedad> gastosPropiedad = new ArrayList<GastoPropiedad>();
    private Collection<Fondo> fondos = new ArrayList<Fondo>();
    private Collection<CuotaEsp> cuotasEsp = new ArrayList<CuotaEsp>();
    private Parametro parametro;
    private Collection<Banco> bancos = new ArrayList<Banco>();
    private Collection<AjustesCond> ajustes = new ArrayList<AjustesCond>();
    private Collection<PagoFonRes> pagosfonres = new ArrayList<PagoFonRes>();
    private Collection<CodigoAcceso> codigoAcceso = new ArrayList<CodigoAcceso>();
    private Collection<Tasa> tasas = new ArrayList<>();

    public Condominio() {
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

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Calendar periodoActual) {
        this.periodoActual = periodoActual;
    }

    public Calendar getFecCreacion() {
        return fecCreacion;
    }

    public void setFecCreacion(Calendar fecCreacion) {
        this.fecCreacion = fecCreacion;
    }

    public void addCondominios(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    public void setPropiedades(Collection<Propiedad> propiedades) {
        this.propiedades = propiedades;
    }

    public Collection<Propiedad> getPropiedades() {
        return propiedades;
    }

    public void setGastosPropiedad(Collection<GastoPropiedad> gastosPropiedad) {
        this.gastosPropiedad = gastosPropiedad;
    }

    public Collection<GastoPropiedad> getGastosPropiedad() {
        return gastosPropiedad;
    }

    public void setCategorias(Collection<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Collection<Categoria> getCategorias() {
        return categorias;
    }

    public void setConceptos(Collection<Concepto> conceptos) {
        this.conceptos = conceptos;
    }

    public Collection<Concepto> getConceptos() {
        return conceptos;
    }

    public void setProveedores(Collection<Proveedor> proveedores) {
        this.proveedores = proveedores;
    }

    public Collection<Proveedor> getProveedores() {
        return proveedores;
    }

    public void setGastosComunesOrd(Collection<GastoComunOrd> gastosComunesOrd) {
        this.gastosComunesOrd = gastosComunesOrd;
    }

    public Collection<GastoComunOrd> getGastosComunesOrd() {
        return gastosComunesOrd;
    }

    public void setGastosNoComunes(Collection<GastoNoComun> gastosNoComunes) {
        this.gastosNoComunes = gastosNoComunes;
    }

    public Collection<GastoNoComun> getGastosNoComunes() {
        return gastosNoComunes;
    }

    public void setGastosComunesExt(Collection<GastoComunExt> gastosComunesExt) {
        this.gastosComunesExt = gastosComunesExt;
    }

    public Collection<GastoComunExt> getGastosComunesExt() {
        return gastosComunesExt;
    }

    public void setFondos(Collection<Fondo> fondos) {
        this.fondos = fondos;
    }

    public Collection<Fondo> getFondos() {
        return fondos;
    }

    public void setCuotasEsp(Collection<CuotaEsp> cuotasEsp) {
        this.cuotasEsp = cuotasEsp;
    }

    public Collection<CuotaEsp> getCuotasEsp() {
        return cuotasEsp;
    }

    public void setParametro(Parametro parametro) {
        this.parametro = parametro;
    }

    public Parametro getParametro() {
        return parametro;
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    public void addPropiedades(Propiedad propiedad) {
        this.propiedades.add(propiedad);
    }

    public void addCategorias(Categoria categoria) {
        this.categorias.add(categoria);
    }

    public void addConceptos(Concepto concepto) {
        this.conceptos.add(concepto);
    }

    public void addGastoComunOrd(GastoComunOrd gastoComunOrd) {
        this.gastosComunesOrd.add(gastoComunOrd);
    }

    public void addGastoComunExt(GastoComunExt gastoComunExt) {
        this.gastosComunesExt.add(gastoComunExt);
    }

    public void setBancos(Collection<Banco> bancos) {
        this.bancos = bancos;
    }

    public Collection<Banco> getBancos() {
        return bancos;
    }

    public void setAjustes(Collection<AjustesCond> ajustes) {
        this.ajustes = ajustes;
    }

    public Collection<AjustesCond> getAjustes() {
        return ajustes;
    }

    public void setPagosfonres(Collection<PagoFonRes> pagosfonres) {
        this.pagosfonres = pagosfonres;
    }

    public Collection<PagoFonRes> getPagosfonres() {
        return pagosfonres;
    }

    public void setCodigoAcceso(Collection<CodigoAcceso> codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public Collection<CodigoAcceso> getCodigoAcceso() {
        return codigoAcceso;
    }
    
    public void setEdoCondominio(String edoCondominio) {
        this.edoCondominio = edoCondominio;
    }

    public String getEdoCondominio() {
        return edoCondominio;
    }
    public void setTasas(Collection<Tasa> tasas) {
        this.tasas = tasas;
    } 

    public Collection<Tasa> getTasas() {
        return tasas;
    }
    
    public void addTasas(Tasa tasa){
        this.tasas.add(tasa); 
    }
}
