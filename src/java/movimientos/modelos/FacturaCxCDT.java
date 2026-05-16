package movimientos.modelos;

import java.io.Serializable;
import java.util.Calendar;

public class FacturaCxCDT implements Serializable {

    private Integer id;
    private String fecFacCxC;
    private Calendar calFecFacCxC;
    private String mtoFacCxC;
    private String saldoFacCxC;
    private String edoFacCxC;
    private String discFacCxC;
    private String desFacCxC;
    private String idDT;
    private boolean seleccionada;

    public FacturaCxCDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecFacCxC(String fecFacCxC) {
        this.fecFacCxC = fecFacCxC;
    }

    public String getFecFacCxC() {
        return fecFacCxC;
    }

    public void setCalFecFacCxC(Calendar calFecFacCxC) {
        this.calFecFacCxC = calFecFacCxC;
    }

    public Calendar getCalFecFacCxC() {
        return calFecFacCxC;
    }

    public void setMtoFacCxC(String mtoFacCxC) {
        this.mtoFacCxC = mtoFacCxC;
    }

    public String getMtoFacCxC() {
        return mtoFacCxC;
    }

    public String getEdoFacCxC() {
        return edoFacCxC;
    }

    public void setEdoFacCxC(String edoFacCxC) {
        this.edoFacCxC = edoFacCxC;
    }

    public String getDiscFacCxC() {
        return discFacCxC;
    }

    public void setDiscFacCxC(String discFacCxC) {
        this.discFacCxC = discFacCxC;
    }

    public String getSaldoFacCxC() {
        return saldoFacCxC;
    }

    public void setSaldoFacCxC(String saldoFacCxC) {
        this.saldoFacCxC = saldoFacCxC;
    }

    public String getDesFacCxC() {
        return desFacCxC;
    }

    public void setDesFacCxC(String desFacCxC) {
        this.desFacCxC = desFacCxC;
    }

    public boolean getSeleccionada() {
        return seleccionada;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    public void setIdDT(String idDT) {
        this.idDT = idDT;
    }

    public String getIdDT() {
        return idDT;
    }
}
