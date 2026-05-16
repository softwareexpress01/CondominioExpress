/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Calendar;
/**
 *
 * @author FrankRH
 */
public class CuotaEsp implements Serializable {

    public Integer id;
    private Calendar fecCuotaEsp;
    private Double mtoTotCuotaEsp;
    private String calcCuotaEsp;
    private Integer idFondoCuotaEsp;
    private Integer idConceptoCuotaEsp;
    private Integer nroMesesCuotaEsp;
    private Calendar fecApartirCuotaEsp;
    private Double saldoCuotaEsp;
    public String estadoCuotaEsp;
    private String obsCuotaEsp;
    
    
    /* Asociaciones */
    private Condominio condominio;

    public CuotaEsp() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecCuotaEsp(Calendar fecCuotaEsp) {
        this.fecCuotaEsp = fecCuotaEsp;
    }

    public Calendar getFecCuotaEsp() {
        return fecCuotaEsp;
    }

    public void setMtoTotCuotaEsp(Double mtoTotCuotaEsp) {
        this.mtoTotCuotaEsp = mtoTotCuotaEsp;
    }

    public Double getMtoTotCuotaEsp() {
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

    public void setIdConceptoCuotaEsp(Integer idConceptoCuotaEsp) {
        this.idConceptoCuotaEsp = idConceptoCuotaEsp;
    }

    public Integer getIdConceptoCuotaEsp() {
        return idConceptoCuotaEsp;
    }

    public void setNroMesesCuotaEsp(Integer nroMesesCuotaEsp) {
        this.nroMesesCuotaEsp = nroMesesCuotaEsp;
    }

    public Integer getNroMesesCuotaEsp() {
        return nroMesesCuotaEsp;
    }

    public void setFecApartirCuotaEsp(Calendar fecApartirCuotaEsp) {
        this.fecApartirCuotaEsp = fecApartirCuotaEsp;
    }

    public Calendar getFecApartirCuotaEsp() {
        return fecApartirCuotaEsp;
    }

    public void setSaldoCuotaEsp(Double saldoCuotaEsp) {
        this.saldoCuotaEsp = saldoCuotaEsp;
    }

    public Double getSaldoCuotaEsp() {
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
