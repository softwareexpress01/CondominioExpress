package movimientos.modelos;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

public class FacturaCxPDT extends ListDataModel implements Serializable, SelectableDataModel {

    private Integer id;
    private String fecFacCxP;
    private Calendar calFecFacCxP;
    public  String mtoFacCxP;
    private String saldoFacCxP;
    private String edoFacCxP;
    private String discFacCxP;
    private String desFacCxP;
    private String idDT;
    private boolean seleccionada;

    public FacturaCxPDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecFacCxP(String fecFacCxP) {
        this.fecFacCxP = fecFacCxP;
    }

    public String getFecFacCxP() {
        return fecFacCxP;
    }

    public void setCalFecFacCxP(Calendar calFecFacCxP) {
        this.calFecFacCxP = calFecFacCxP;
    }

    public Calendar getCalFecFacCxC() {
        return calFecFacCxP;
    }

    public void setMtoFacCxP(String mtoFacCxP) {
        this.mtoFacCxP = mtoFacCxP;
    }

    public String getMtoFacCxP() {
        return mtoFacCxP;
    }

    public String getEdoFacCxP() {
        return edoFacCxP;
    }

    public void setEdoFacCxP(String edoFacCxP) {
        this.edoFacCxP = edoFacCxP;
    }

    public String getDiscFacCxP() {
        return discFacCxP;
    }

    public void setDiscFacCxP(String discFacCxP) {
        this.discFacCxP = discFacCxP;
    }

    public String getSaldoFacCxP() {
        return saldoFacCxP;
    }

    public void setSaldoFacCxP(String saldoFacCxP) {
        this.saldoFacCxP = saldoFacCxP;
    }

    public String getDesFacCxP() {
        return desFacCxP;
    }

    public void setDesFacCxP(String desFacCxP) {
        this.desFacCxP = desFacCxP;
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

    public Object getRowKey(FacturaCxPDT fCxPDT) {

        return fCxPDT.getIdDT();
    }

    public Object getRowData(String rowKey) {

        List<FacturaCxPDT> cuentas = (List<FacturaCxPDT>) getWrappedData();
        for (FacturaCxPDT fCxPDT : cuentas) {
            if (fCxPDT.getIdDT().equals(rowKey)) {
                return fCxPDT;
            }
        }
        return null;
    }

    public Object getRowKey(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
