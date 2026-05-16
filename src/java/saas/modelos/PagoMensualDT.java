/**
 *
 * @author jesus
 */
package saas.modelos;


import java.io.Serializable;

/**
 *
 * @author Jesus
 */
public class PagoMensualDT implements Serializable {

    private Integer id;
    public String bcoPagoMensual;
    public String ctaPagoMensual;
    public String fecPagoMensual;
    private String refPagoMensual;
    private String mtoPagoMensual;
    public String fecMovPagoMensual;
    private String edoPagoMensual;
    private String obsPagoMensual;
    private String numSuscriptor;
    private String nombSuscriptor;
    private String apelSuscriptor;
    private String numCedSuscriptor;
    /* Asociaciones */
    private Suscripcion suscripcion;

    public PagoMensualDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setBcoPagoMensual(String bcoPagoMensual) {
        this.bcoPagoMensual = bcoPagoMensual;
    }

    public String getBcoPagoMensual() {
        return bcoPagoMensual;
    }

    public void setCtaPagoMensual(String ctaPagoMensual) {
        this.ctaPagoMensual = ctaPagoMensual;
    }

    public String getCtaPagoMensual() {
        return ctaPagoMensual;
    }

    public void setFecPagoMensual(String fecPagoMensual) {
        this.fecPagoMensual = fecPagoMensual;
    }

    public String getFecPagoMensual() {
        return fecPagoMensual;
    }

    public void setRefPagoMensual(String refPagoMensual) {
        this.refPagoMensual = refPagoMensual;
    }

    public String getRefPagoMensual() {
        return refPagoMensual;
    }

    public void setMtoPagoMensual(String mtoPagoMensual) {
        this.mtoPagoMensual = mtoPagoMensual;
    }

    public String getMtoPagoMensual() {
        return mtoPagoMensual;
    }

    public void setFecMovPagoMensual(String fecMovPagoMensual) {
        this.fecMovPagoMensual = fecMovPagoMensual;
    }

    public String getFecMovPagoMensual() {
        return fecMovPagoMensual;
    }

    public void setEdoPagoMensual(String edoPagoMensual) {
        this.edoPagoMensual = edoPagoMensual;
    }

    public String getEdoPagoMensual() {
        return edoPagoMensual;
    }

    public void setObsPagoMensual(String obsPagoMensual) {
        this.obsPagoMensual = obsPagoMensual;
    }

    public String getObsPagoMensual() {
        return obsPagoMensual;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setNumSuscriptor(String numSuscriptor) {
        this.numSuscriptor = numSuscriptor;
    }

    public String getNumSuscriptor() {
        return numSuscriptor;
    }

    public void setNombSuscriptor(String nombSuscriptor) {
        this.nombSuscriptor = nombSuscriptor;
    }

    public String getNombSuscriptor() {
        return nombSuscriptor;
    }

    public void setApelSuscriptor(String apelSuscriptor) {
        this.apelSuscriptor = apelSuscriptor;
    }

    public String getApelSuscriptor() {
        return apelSuscriptor;
    }

    public void setNumCedSuscriptor(String numCedSuscriptor) {
        this.numCedSuscriptor = numCedSuscriptor;
    }

    public String getNumCedSuscriptor() {
        return numCedSuscriptor;
    }
}
