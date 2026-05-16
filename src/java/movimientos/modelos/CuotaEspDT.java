/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;

/**
 *
 * @author FrankRH
 */
public class CuotaEspDT implements Serializable {

    public Integer id;
    private String fecCuotaEsp;
    private String mtoTotCuotaEsp;
    private String calcCuotaEsp;
    private Integer idFondoCuotaEsp;
    private String nombFondoCuotaEsp;
    private Integer idConceptoCuotaEsp;
    private String descConceptoCuotaEsp;
    private Integer nroMesesCuotaEsp;
    private String fecApartirCuotaEsp;
    private String saldoCuotaEsp;
    private String estadoCuotaEsp;
    private String obsCuotaEsp;
        /* Asociaciones */
    private Condominio condominio;

    public CuotaEspDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecCuotaEsp(String fecCuotaEsp) {
        this.fecCuotaEsp = fecCuotaEsp;
    }

    public String getFecCuotaEsp() {
        return fecCuotaEsp;
    }

    public void setMtoTotCuotaEsp(String mtoTotCuotaEsp) {
        this.mtoTotCuotaEsp = mtoTotCuotaEsp;
    }

    public String getMtoTotCuotaEsp() {
        return mtoTotCuotaEsp;
    }

    public void setCalcCuotaEsp(String calcCuotaEsp) {
        this.calcCuotaEsp = calcCuotaEsp;
    }

    public String getCalcCuotaEsp() {
        return calcCuotaEsp;
    }

    public void setIdFondoCuotaEsp(Integer idFondoCuotaEsp) {
        this.idFondoCuotaEsp = idFondoCuotaEsp;
    }

    public Integer getIdFondoCuotaEsp() {
        return idFondoCuotaEsp;
    }
    
    public void setNombFondoCuotaEsp(String nombFondoCuotaEsp) {
        this.nombFondoCuotaEsp = nombFondoCuotaEsp;
    }

    public String getNombFondoCuotaEsp() {
        return nombFondoCuotaEsp;
    }

    public void setIdConceptoCuotaEsp(Integer idConceptoCuotaEsp) {
        this.idConceptoCuotaEsp = idConceptoCuotaEsp;
    }

    public Integer getIdConceptoCuotaEsp() {
        return idConceptoCuotaEsp;
    }

    public void setDescConceptoCuotaEsp(String descConceptoCuotaEsp) {
        this.descConceptoCuotaEsp = descConceptoCuotaEsp;
    }

    public String getDescConceptoCuotaEsp() {
        return descConceptoCuotaEsp;
    }
    public void setNroMesesCuotaEsp(Integer nroMesesCuotaEsp) {
        this.nroMesesCuotaEsp = nroMesesCuotaEsp;
    }

    public Integer getNroMesesCuotaEsp() {
        return nroMesesCuotaEsp;
    }

    public void setFecApartirCuotaEsp(String fecApartirCuotaEsp) {
        this.fecApartirCuotaEsp = fecApartirCuotaEsp;
    }

    public String getFecApartirCuotaEsp() {
        return fecApartirCuotaEsp;
    }
    
    public void setSaldoCuotaEsp(String saldoCuotaEsp) {
        this.saldoCuotaEsp = saldoCuotaEsp;
    }

    public String getSaldoCuotaEsp() {
        return saldoCuotaEsp;

    }
    
    public void setEstadoCuotaEsp(String estadoCuotaEsp) {
        this.estadoCuotaEsp = estadoCuotaEsp;
    }

    public String getEstadoCuotaEsp() {
        return estadoCuotaEsp;
    }

    public void setObsCuotaEsp(String obsCuotaEsp) {
        this.obsCuotaEsp = obsCuotaEsp;
    }

    public String getObsCuotaEsp() {
        return obsCuotaEsp;

    }
    
    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
}
