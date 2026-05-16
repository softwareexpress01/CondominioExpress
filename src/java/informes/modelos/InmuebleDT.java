package informes.modelos;

import java.io.Serializable;

/**
 * Inmueble.java serves as the POJO for storing information about inmuebles
 */
public class InmuebleDT implements Serializable {

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
    private String nombPropiedad;

    public InmuebleDT() {
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

    public void setNombPropiedad(String nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public String getNombPropiedad() {
        return nombPropiedad;
    }
}
