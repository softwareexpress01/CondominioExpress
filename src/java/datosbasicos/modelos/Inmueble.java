package datosbasicos.modelos;

import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import acceso.modelos.CodigoAcceso;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoAdelantado;
import movimientos.modelos.PagoCxC;

/**
 * Inmueble.java serves as the POJO for storing information about inmuebles
 */
public class Inmueble implements Serializable {

    private Integer id;
    private String codInmueble;
    private String nombPropInmueble;
    private String cedRifPropInmueble;
    private String dirInmueble;
    private String telInmueble;
    private String emailPropInmueble;
    private String tipoInmueble;
    private Double areaInmueble;
    private Double alicInmueble;
    private String notaRecInmueble;
    private Integer idProp;
    private Propiedad propiedad;
    private CodigoAcceso codigoAcceso;
    private Collection<NotaCredDebCxC> notasCredDebCxC = new ArrayList<NotaCredDebCxC>();
    private Collection<DeudaPendCxC> deudasPendientesCxC = new ArrayList<DeudaPendCxC>();
    public Collection<FacGastoNoComun> facturasGNC = new ArrayList<FacGastoNoComun>();
    public Collection<PagoCxC> pagoCxC = new ArrayList<PagoCxC>();
    public Collection<Alicuota> alicuotas = new ArrayList<Alicuota>();
    public Collection<FacCuotaEsp> facturasCuotaEsp = new ArrayList<FacCuotaEsp>();
    public Collection<PagoAdelantado> pagosAdelantados = new ArrayList<PagoAdelantado>();

    public Inmueble() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setNombPropInmueble(String nombPropInmueble) {
        this.nombPropInmueble = nombPropInmueble;
    }

    public String getNombPropInmueble() {
        return nombPropInmueble;
    }

    public void setCedRifPropInmueble(String cedRifPropInmueble) {
        this.cedRifPropInmueble = cedRifPropInmueble;
    }

    public String getCedRifPropInmueble() {
        return cedRifPropInmueble;
    }

    public void setDirInmueble(String dirInmueble) {
        this.dirInmueble = dirInmueble;
    }

    public String getDirInmueble() {
        return dirInmueble;
    }

    public void setTelInmueble(String telInmueble) {
        this.telInmueble = telInmueble;
    }

    public String getTelInmueble() {
        return telInmueble;
    }

    public void setEmailPropInmueble(String emailPropInmueble) {
        this.emailPropInmueble = emailPropInmueble;
    }

    public String getEmailPropInmueble() {
        return emailPropInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setAreaInmueble(Double areaInmueble) {
        this.areaInmueble = areaInmueble;
    }

    public Double getAreaInmueble() {
        return areaInmueble;
    }

    public void setAlicInmueble(Double alicInmueble) {
        this.alicInmueble = alicInmueble;
    }

    public Double getAlicInmueble() {
        return alicInmueble;
    }

    public void setNotaRecInmueble(String notaRecInmueble) {
        this.notaRecInmueble = notaRecInmueble;
    }

    public String getNotaRecInmueble() {
        return notaRecInmueble;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setIdProp(Integer idProp) {
        this.idProp = idProp;
    }

    public Integer getIdProp() {
        return idProp;
    }

    public void setNotasCredDebCxC(Collection<NotaCredDebCxC> notasCredDebCxC) {
        this.notasCredDebCxC = notasCredDebCxC;
    }

    public Collection<NotaCredDebCxC> getNotasCredDebCxC() {
        return notasCredDebCxC;
    }

    public void setDeudasPendientesCxC(Collection<DeudaPendCxC> deudasPendientesCxC) {
        this.deudasPendientesCxC = deudasPendientesCxC;
    }

    public Collection<DeudaPendCxC> getDeudasPendientesCxC() {
        return deudasPendientesCxC;
    }

    public void setFacturasGNC(Collection<FacGastoNoComun> facturasGNC) {
        this.facturasGNC = facturasGNC;
    }

    public Collection<FacGastoNoComun> getFacturasGNC() {
        return facturasGNC;
    }

    public void setFacturasCuotaEsp(Collection<FacCuotaEsp> facturasCuotaEsp) {
        this.facturasCuotaEsp = facturasCuotaEsp;
    }

    public Collection<FacCuotaEsp> getFacturasCuotaEsp() {
        return facturasCuotaEsp;
    }

    public void setPagoCxC(Collection<PagoCxC> pagoCxC) {
        this.pagoCxC = pagoCxC;
    }

    public Collection<PagoCxC> getPagoCxC() {
        return pagoCxC;
    }

    public void setAlicuotas(Collection<Alicuota> alicuotas) {
        this.alicuotas = alicuotas;
    }

    public Collection<Alicuota> getAlicuotas() {
        return alicuotas;
    }

    public void setPagosAdelantados(Collection<PagoAdelantado> pagosAdelantados) {
        this.pagosAdelantados = pagosAdelantados;
    }

    public Collection<PagoAdelantado> getPagosAdelantados() {
        return pagosAdelantados;
    }

    public void setCodigoAcceso(CodigoAcceso codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public CodigoAcceso getCodigoAcceso() {
        return codigoAcceso;
    }
}
