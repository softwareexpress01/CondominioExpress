package util;

import java.io.Serializable;
import java.util.Calendar;

/**
 * BeanUsuario java sirve para almacenar informacion acerca del usuario actual,
 * condominio y propiedad actuales, etc
 */
public class BeanUsuario implements Serializable {

    private String codUsuario;
    private String nombUsuario;
    private Integer idSuscripcion;
    private String nombSuscriptor;
    private String numSuscripcion;
    private String perUsuario;
    private Integer idCondActual;
    private Integer idPropActual;
    private Integer idInm;      // Creado para trabajar con la Consulta Express
    private String condominioActual;
    private String propiedadActual;
    private Calendar periodoActual;
    private int idUsuario;
    private boolean isLoggedIn;
    private String nombEntidadElim;
    private String ruta;
    private String rutaTemporal;
    private String hostCond;
    private String puerto;
    private Integer contadorVisitas;
    private String emailPropInmueble;
    private String emailProveedor;
    public String reporte;
    public String invitacion;
    public String ipHost;
    /**
     * para habilitar/deshabilitar botones
     */
    public Boolean agregar = false;
    public Boolean modificar = true;
    public Boolean eliminar = true;
    public Boolean regresar = false;
    public Boolean revertir = true;
    public Boolean si = false;
    public Boolean no = false;
    public Boolean mostrar = false;
    public Boolean mostrarBox = true;
    public Boolean deshabilitar = false;
    public Boolean deshabRadio = false;
    public Boolean irACrearCondominio = false;
    public Boolean mostrarMonto = false;
    /**
     * para habilitar/deshabilitar opciones del menu, dependiendo de si
     * selecciono una propiedad o no
     */
    public Boolean datosBasicos = false;
    public Boolean configuracion = false;
    public Boolean operaciones = false;
    public Boolean movMes = false;
    public Boolean informes = false;
    public Boolean gestion = false;
    public Boolean admin = false;
    public Boolean inmuebles = false;
    public Boolean propSel = false;
    /**
     * para habilitar/deshabilitar busquedas
     */
    public Boolean ejecutarQuery = true;
    public Boolean ejecutarQueryDT = true;
    public Boolean selecciono = false;
    public Boolean inicializarMap = true;
    public Double porcInmRegistrados;
    public Double porcAlicRegistrado;
    private Boolean seleccionada = false;
    /**
     * mostrar en area centro pagina seleccionada del menú
     */
    public Boolean mostrar1 = false;
    public Boolean mostrar2 = false;
    private String tasa;
    private Boolean tasaExiste;
    private String periodoRep;

    public BeanUsuario() {
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getNombUsuario() {
        return nombUsuario;
    }

    public void setNombUsuario(String nombUsuario) {
        this.nombUsuario = nombUsuario;
    }

    public String getNombSuscriptor() {
        return nombSuscriptor;
    }

    public void setNombSuscriptor(String nombSuscriptor) {
        this.nombSuscriptor = nombSuscriptor;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getPerUsuario() {
        return perUsuario;
    }

    public void setPerUsuario(String perUsuario) {
        this.perUsuario = perUsuario;
    }

    public String getCondominioActual() {
        return condominioActual;
    }

    public void setCondominioActual(String condominioActual) {
        this.condominioActual = condominioActual;
    }

    public String getPropiedadActual() {
        return propiedadActual;
    }

    public void setPropiedadActual(String propiedadActual) {
        this.propiedadActual = propiedadActual;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Calendar peridodActual) {
        this.periodoActual = peridodActual;
    }

    public String getPeriodoActualString() {

        int mesPerAct = periodoActual.get(Calendar.MONTH) + 1;
        String mesPerActString = Integer.toString(mesPerAct);

        int añoPerAct = periodoActual.get(Calendar.YEAR);
        String añoPerActString = Integer.toString(añoPerAct);

        return mesPerActString + "-" + añoPerActString;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    public void setOperaciones(boolean operaciones) {
        this.operaciones = operaciones;
    }

    public boolean getOperaciones() {
        return operaciones;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdSuscripcion(Integer idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public Integer getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdCondActual(int idCondActual) {
        this.idCondActual = idCondActual;
    }

    public int getIdCondActual() {
        return idCondActual;
    }

    public void setIdPropActual(int idPropActual) {
        this.idPropActual = idPropActual;
    }

    public int getIdPropActual() {
        return idPropActual;
    }

    public void setIdInm(int idInm) {
        this.idInm = idInm;
    }

    public int getIdInm() {
        return idInm;
    }

    public void setAgregar(Boolean agregar) {
        this.agregar = agregar;
    }

    public Boolean getAgregar() {
        return agregar;
    }

    public void setModificar(Boolean modificar) {
        this.modificar = modificar;
    }

    public Boolean getModificar() {
        return modificar;
    }

    public void setRegresar(Boolean regresar) {
        this.regresar = regresar;
    }

    public Boolean getRegresar() {
        return regresar;
    }

    public void setEliminar(Boolean eliminar) {
        this.eliminar = eliminar;
    }

    public Boolean getEliminar() {
        return eliminar;
    }

    public void setSi(Boolean si) {
        this.si = si;
    }

    public Boolean getSi() {
        return si;
    }

    public void setNo(Boolean no) {
        this.no = no;
    }

    public Boolean getNo() {
        return no;
    }

    public void setMovMes(Boolean movMes) {
        this.movMes = movMes;
    }

    public Boolean getMovMes() {
        return movMes;
    }

    public void setInformes(Boolean informes) {
        this.informes = informes;
    }

    public Boolean getInformes() {
        return informes;
    }

    public void setGestion(Boolean gestion) {
        this.gestion = gestion;
    }

    public Boolean getGestion() {
        return gestion;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setDatosBasicos(Boolean datosBasicos) {
        this.datosBasicos = datosBasicos;
    }

    public Boolean getDatosBasicos() {
        return datosBasicos;
    }

    public void setConfiguracion(Boolean configuracion) {
        this.configuracion = configuracion;
    }

    public Boolean getConfiguracion() {
        return configuracion;
    }

    public void setInmuebles(Boolean inmuebles) {
        this.inmuebles = inmuebles;
    }

    public Boolean getInmuebles() {
        return inmuebles;
    }

    public void setMostrar(Boolean mostrar) {
        this.mostrar = mostrar;
    }

    public Boolean getMostrar() {
        return mostrar;
    }
    //------------------------------------------------------------

    public void setMostrar1(Boolean mostrar1) {
        this.mostrar1 = mostrar1;
    }

    public Boolean getMostrar1() {
        return mostrar1;
    }

    public void setMostrar2(Boolean mostrar2) {
        this.mostrar2 = mostrar2;
    }

    public Boolean getMostrar2() {
        return mostrar2;
    }

    //------------------------------------------------------------
    public void setRevertir(Boolean revertir) {
        this.revertir = revertir;
    }

    public Boolean getRevertir() {
        return revertir;
    }

    public void setMostrarMonto(Boolean mostrarMonto) {
        this.mostrarMonto = mostrarMonto;
    }

    public Boolean getMostrarMonto() {
        return mostrarMonto;
    }

    public void setDeshabilitar(Boolean deshabilitar) {
        this.deshabilitar = deshabilitar;
    }

    public Boolean getDeshabilitar() {
        return deshabilitar;
    }

    public void setDeshabRadio(Boolean deshabRadio) {
        this.deshabRadio = deshabRadio;
    }

    public Boolean getDeshabRadio() {
        return deshabRadio;
    }

    public void setMostrarBox(Boolean mostrarBox) {
        this.mostrarBox = mostrarBox;
    }

    public Boolean getMostrarBox() {
        return mostrarBox;
    }

    public void setInicializarMap(Boolean inicializarMap) {
        this.inicializarMap = inicializarMap;
    }

    public Boolean getInicializarMap() {
        return inicializarMap;
    }

    public void setEjecutarQuery(Boolean ejecutarQuery) {
        this.ejecutarQuery = ejecutarQuery;
    }

    public Boolean getEjecutarQuery() {
        return ejecutarQuery;
    }

    public void setEjecutarQueryDT(Boolean ejecutarQueryDT) {
        this.ejecutarQueryDT = ejecutarQueryDT;
    }

    public Boolean getEjecutarQueryDT() {
        return ejecutarQueryDT;
    }

    public void setPropSel(Boolean propSel) {
        this.propSel = propSel;
    }

    public Boolean getPropSel() {
        return propSel;
    }

    public void setNombEntidadElim(String nombEntidadElim) {
        this.nombEntidadElim = nombEntidadElim;
    }

    public String getNombEntidadElim() {
        return nombEntidadElim;
    }

    public Boolean getIrACrearCondominio() {
        return irACrearCondominio;
    }

    public void setIrACrearCondominio(Boolean irACrearCondominio) {
        this.irACrearCondominio = irACrearCondominio;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getHostCond() {
        return hostCond;
    }

    public void setHostCond(String hostCond) {
        this.hostCond = hostCond;
    }

    public String getRutaTemporal() {
        return rutaTemporal;
    }

    public void setRutaTemporal(String rutaTemporal) {
        this.rutaTemporal = rutaTemporal;
    }

    public void setPorcInmRegistrados(Double porcInmRegistrados) {
        this.porcInmRegistrados = porcInmRegistrados;
    }

    public Double getPorcInmRegistrados() {
        return porcInmRegistrados;
    }

    public void setPorcAlicRegistrado(Double porcAlicRegistrado) {
        this.porcAlicRegistrado = porcAlicRegistrado;
    }

    public Double getPorcAlicRegistrado() {
        return porcAlicRegistrado;
    }

    public void setContadorVisitas(Integer contadorVisitas) {
        this.contadorVisitas = contadorVisitas;
    }

    public Integer getContadorVisitas() {
        return contadorVisitas;
    }

    public void setSeleccionada(Boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    public Boolean getSeleccionada() {
        return seleccionada;
    }

    public String getEmailPropInmueble() {
        return emailPropInmueble;
    }

    public void setEmailPropInmueble(String emailInm) {
        this.emailPropInmueble = emailInm;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProv) {
        this.emailProveedor = emailProv;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getInvitacion() {
        return invitacion;
    }

    public void setInvitacion(String invitacion) {
        this.invitacion = invitacion;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }

    public String getTasa() {
        return tasa;
    }

    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    public Boolean getTasaExiste() {
        return tasaExiste;
    }

    public void setTasaExiste(Boolean tasaExiste) {
        this.tasaExiste = tasaExiste;
    }

    public String getPeriodoRep() {
        return periodoRep;
    }

    public void setPeriodoRep(String periodoRep) {
        this.periodoRep = periodoRep;
    }
}
