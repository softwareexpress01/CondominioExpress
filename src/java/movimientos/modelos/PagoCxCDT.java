package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.Calendar;

public class PagoCxCDT implements Serializable {

    private Integer id;
    private String fecEmiPagoCxC;
    private String desPagoCxC;
    private String numDocPagoCxC;
    private String mtoPagoCxC;
    private String formaPagoCxC;
    private String numRefPagoCxC;
    private String fecDocPagoCxC;
    private Calendar fecDocPagoCal;
    private String bancoPagoCxC;
    private String fecMovPagoCxC;
    private Inmueble inmueble;

    public PagoCxCDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecEmiPagoCxC(String fecEmiPagoCxC) {
        this.fecEmiPagoCxC = fecEmiPagoCxC;
    }

    public String getFecEmiPagoCxC() {
        return fecEmiPagoCxC;
    }

    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public void setNumDocPagoCxC(String numDocPagoCxC) {
        this.numDocPagoCxC = numDocPagoCxC;
    }

    public String getNumDocPagoCxC() {
        return numDocPagoCxC;
    }

    public String getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(String mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getNumRefPagoCxC() {
        return numRefPagoCxC;
    }

    public void setNumRefPagoCxC(String numRefPagoCxC) {
        this.numRefPagoCxC = numRefPagoCxC;
    }

    public String getFecDocPagoCxC() {
        return fecDocPagoCxC;
    }

    public void setFecDocPagoCxC(String fecDocPagoCxC) {
        this.fecDocPagoCxC = fecDocPagoCxC;
    }

    public Calendar getFecDocPagoCal() {
        return fecDocPagoCal;
    }

    public void setFecDocPagoCal(Calendar fecDocPagoCal) {
        this.fecDocPagoCal = fecDocPagoCal;
    }

    public void setBancoPagoCxC(String bancoPagoCxC) {
        this.bancoPagoCxC = bancoPagoCxC;
    }

    public String getBancoPagoCxC() {
        return bancoPagoCxC;
    }

    public void setFecMovPagoCxC(String fecMovPagoCxC) {
        this.fecMovPagoCxC = fecMovPagoCxC;
    }

    public String getFecMovPagoCxC() {
        return fecMovPagoCxC;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
