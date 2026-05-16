package movimientos.modelos;

import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

public class PagoFonResDT extends ListDataModel implements Serializable, SelectableDataModel {

    private Integer id;
    private String fecEmiPagoFonRes;
    private String desPagoFonRes;
    private String numDocPagoFonRes;
    private String mtoPagoFonRes;
    private String saldoPagoFonRes;
    private String formaPagoFonRes;
    private String fecMovPagoFonRes;
    private String nombBancoPagoFonRes;
    public TranFondo tranFondo;

    
    public PagoFonResDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecEmiPagoFonRes(String fecEmiPagoFonRes) {
        this.fecEmiPagoFonRes = fecEmiPagoFonRes;
    }

    public String getFecEmiPagoFonRes() {
        return fecEmiPagoFonRes;
    }

    public void setDesPagoFonRes(String desPagoFonRes) {
        this.desPagoFonRes = desPagoFonRes;
    }

    public String getDesPagoFonRes() {
        return desPagoFonRes;
    }

    public void setNumDocPagoFonRes(String numDocPagoFonRes) {
        this.numDocPagoFonRes = numDocPagoFonRes;
    }

    public String getNumDocPagoFonRes() {
        return numDocPagoFonRes;
    } 

    public String getMtoPagoFonRes() {
        return mtoPagoFonRes;
    }

    public void setMtoPagoFonRes(String mtoPagoFonRes) {
        this.mtoPagoFonRes = mtoPagoFonRes;
    }
     public String getSaldoPagoFonRes() {
        return saldoPagoFonRes;
    }

    public void setSaldoPagoFonRes(String saldoPagoFonRes) {
        this.saldoPagoFonRes = saldoPagoFonRes;
    }

    public String getFormaPagoFonRes() {
        return formaPagoFonRes;
    }

    public void setFormaPagoFonRes(String formaPagoFonRes) {
        this.formaPagoFonRes = formaPagoFonRes;
    }

   
    public void setFecMovPagoFonRes(String fecMovPagoFonRes) {
        this.fecMovPagoFonRes = fecMovPagoFonRes;
    }

    public String getFecMovPagoFonRes() {
        return fecMovPagoFonRes;
    }
    
    public void setNombBancoPagoFonRes(String nombBancoPagoFonRes) {
        this.nombBancoPagoFonRes = nombBancoPagoFonRes;
    }

    public String getNombBancoPagoFonRes() {
        return nombBancoPagoFonRes;
    }

    public void setTranFondo(TranFondo tranFondo) {
        this.tranFondo = tranFondo;
    }

    public TranFondo getGTranFondo() {
        return tranFondo;
    }
    public Object getRowKey(PagoFonResDT pagoFonResDT) {
        
        return pagoFonResDT.getId();
    }

    public Object getRowData(String rowKey) {

        List<PagoFonResDT> pagos = (List<PagoFonResDT>) getWrappedData();
        for (PagoFonResDT pagoFonResDT : pagos) {
            if (pagoFonResDT.getId().toString().equals(rowKey)) {
                return pagoFonResDT;
            }
        }
        return null;
    }

    public Object getRowKey(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
}
