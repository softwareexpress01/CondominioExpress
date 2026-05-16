package movimientos.modelos;

import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

public class PagoCxPDT extends ListDataModel implements Serializable, SelectableDataModel {

    private Integer id;
    private String fecEmiPagoCxP;
    private String desPagoCxP;
    private String numDocPagoCxP;
    private String mtoPagoCxP;
    private String formaPagoCxP;
    private String numRefPagoCxP;
    private String fecDocPagoCxP;
    private String bancoPagoCxP;
    private String fecMovPagoCxP;
    private Proveedor proveedor;

    
    public PagoCxPDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecEmiPagoCxP(String fecEmiPagoCxP) {
        this.fecEmiPagoCxP = fecEmiPagoCxP;
    }

    public String getFecEmiPagoCxP() {
        return fecEmiPagoCxP;
    }

    public void setDesPagoCxP(String desPagoCxP) {
        this.desPagoCxP = desPagoCxP;
    }

    public String getDesPagoCxP() {
        return desPagoCxP;
    }

    public void setNumDocPagoCxP(String numDocPagoCxP) {
        this.numDocPagoCxP = numDocPagoCxP;
    }

    public String getNumDocPagoCxP() {
        return numDocPagoCxP;
    }

    public String getMtoPagoCxP() {
        return mtoPagoCxP;
    }

    public void setMtoPagoCxP(String mtoPagoCxP) {
        this.mtoPagoCxP = mtoPagoCxP;
    }

    public String getFormaPagoCxP() {
        return formaPagoCxP;
    }

    public void setFormaPagoCxP(String formaPagoCxP) {
        this.formaPagoCxP = formaPagoCxP;
    }

    public String getNumRefPagoCxP() {
        return numRefPagoCxP;
    }

    public void setNumRefPagoCxP(String numRefPagoCxP) {
        this.numRefPagoCxP = numRefPagoCxP;
    }

    public String getFecDocPagoCxP() {
        return fecDocPagoCxP;
    }

    public void setFecDocPagoCxP(String fecDocPagoCxP) {
        this.fecDocPagoCxP = fecDocPagoCxP;
    }

    public void setBancoPagoCxP(String bancoPagoCxP) {
        this.bancoPagoCxP = bancoPagoCxP;
    }

    public String getBancoPagoCxP() {
        return bancoPagoCxP;
    }

    public void setFecMovPagoCxP(String fecMovPagoCxP) {
        this.fecMovPagoCxP = fecMovPagoCxP;
    }

    public String getFecMovPagoCxP() {
        return fecMovPagoCxP;
    }

    public void setInmueble(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Proveedor getInmueble() {
        return proveedor;
    }
    public Object getRowKey(PagoCxPDT pagoCxPDT) {
        
        return pagoCxPDT.getId();
    }

    public Object getRowData(String rowKey) {

        List<PagoCxPDT> pagos = (List<PagoCxPDT>) getWrappedData();
        for (PagoCxPDT pagoCxPDT : pagos) {
            if (pagoCxPDT.getId().toString().equals(rowKey)) {
                return pagoCxPDT;
            }
        }
        return null;
    }

    public Object getRowKey(Object t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   
}
