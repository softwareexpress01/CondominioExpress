/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.modelos;

/**
 *
 * @author jesus
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Compañia implements Serializable {

    public Integer id;
    public String nombCompañia;
    public String rifCompañia;
    private Integer numReciboPago;
    private Integer numFactura;
    private Integer contadorVisitas;
    private String ip;
    public Collection<Suscripcion> suscripciones = new ArrayList<Suscripcion>();

    public Compañia() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombCompañia(String nombCompañia) {
        this.nombCompañia = nombCompañia;
    }

    public String getNombCompañia() {
        return nombCompañia;
    }

    public void setRifCompañia(String rifCompañia) {
        this.rifCompañia = rifCompañia;
    }

    public String getRifCompañia() {
        return rifCompañia;
    }

    public void setNumReciboPago(Integer numReciboPago) {
        this.numReciboPago = numReciboPago;
    }

    public Integer getNumReciboPago() {
        return numReciboPago;
    }

    public void setNumFactura(Integer numFactura) {
        this.numFactura = numFactura;
    }

    public Integer getNumFactura() {
        return numFactura;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setSuscripciones(Collection<Suscripcion> suscripciones) {
        this.suscripciones = suscripciones;
    }

    public Collection<Suscripcion> getSuscripciones() {
        return suscripciones;
    }

    public void addSuscripciones(Suscripcion suscripcion) {
        this.suscripciones.add(suscripcion);
    }

    public void setContadorVisitas(Integer contadorVisitas) {
        this.contadorVisitas = contadorVisitas;
    }

    public Integer getContadorVisitas() {
        return contadorVisitas;
    }
}
