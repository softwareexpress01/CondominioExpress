package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class FacCuotaEsp implements Serializable {

    private Integer id;
    private Calendar fecEmiFacCuotaEsp;
    private Integer idConcepto;
    private Double mtoFacCuotaEsp;
    private Double saldoFacCuotaEsp;
    private String edoFacCuotaEsp;
    private String desFacCuotaEsp;
    private Calendar periodoActual;
    private CuotaEsp cuotaEsp;
    private Inmueble inmueble;

    public FacCuotaEsp() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecEmiFacCuotaEsp(Calendar fecEmiFacCuotaEsp) {
        this.fecEmiFacCuotaEsp = fecEmiFacCuotaEsp;
    }

    public Calendar getFecEmiFacCuotaEsp() {
        return fecEmiFacCuotaEsp;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setMtoFacCuotaEsp(Double mtoFacCuotaEsp) {
        this.mtoFacCuotaEsp = mtoFacCuotaEsp;
    }

    public Double getMtoFacCuotaEsp() {
        return mtoFacCuotaEsp;
    }

    public String getEdoFacCuotaEsp() {
        return edoFacCuotaEsp;
    }

    public void setEdoFacCuotaEsp(String edoFacCuotaEsp) {
        this.edoFacCuotaEsp = edoFacCuotaEsp;
    }
   public void setDesFacCuotaEsp(String desFacCuotaEsp) {
        this.desFacCuotaEsp = desFacCuotaEsp;
    }

    public String getDesFacCuotaEsp() {
        return desFacCuotaEsp;
    } 

    public Double getSaldoFacCuotaEsp() {
        return saldoFacCuotaEsp;
    }

    public void setSaldoFacCuotaEsp(Double saldoFacCuotaEsp) {
        this.saldoFacCuotaEsp = saldoFacCuotaEsp;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public Calendar getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(Calendar periodoActual) {
        this.periodoActual = periodoActual;
    }

    public void setCuotaEsp(CuotaEsp cuotaEsp) {
        this.cuotaEsp = cuotaEsp;
    }

    public CuotaEsp getCuotaEsp() {
        return cuotaEsp;
    }
}