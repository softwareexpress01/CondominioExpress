package movimientos.modelos;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;

public class GastoNoComunDT implements Serializable {

    private Integer id;
    private String fecGastoNoComun;
    private String numDocGastoNoComun;
    private Double mtoGastoNoComun;
    private String mtoGastoNoComunString;
    private String obsGastoNoComun;
    private String nombProveedor;
    private String nombConcepto;
    private Integer idProveedor;
    private Integer idConcepto;
    private String edoGastoNoComun;
    private Double saldoGastoNoComun;
    private String saldoGastoNoComunString;
    private String fecMovGNC;
    private String perGastoNoComun;
    private Inmueble inmueble;

    public GastoNoComunDT() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setFecGastoNoComun(String fecGastoNoComun) {
        this.fecGastoNoComun = fecGastoNoComun;
    }

    public String getFecGastoNoComun() {
        return fecGastoNoComun;
    }

    public void setNumDocGastoNoComun(String numDocGastoNoComun) {
        this.numDocGastoNoComun = numDocGastoNoComun;
    }

    public String getNumDocGastoNoComun() {
        return numDocGastoNoComun;
    }

    public void setMtoGastoNoComun(Double mtoGastoNoComun) {
        this.mtoGastoNoComun = mtoGastoNoComun;
    }

    public Double getMtoGastoNoComun() {
        return mtoGastoNoComun;
    }

    public String getObsGastoNoComun() {
        return obsGastoNoComun;
    }

    public void setObsGastoNoComun(String obsGastoNoComun) {
        this.obsGastoNoComun = obsGastoNoComun;
    }

    public String getEdoGastoNoComun() {
        return edoGastoNoComun;
    }

    public void setEdoGastoNoComun(String edoGastoNoComun) {
        this.edoGastoNoComun = edoGastoNoComun;
    }

    public String getFecMovGNC() {
        return fecMovGNC;
    }

    public void setFecMovGNC(String fecMovGNC) {
        this.fecMovGNC = fecMovGNC;
    }

    public Double getSaldoGastoNoComun() {
        return saldoGastoNoComun;
    }

    public void setSaldoGastoNoComun(Double saldoGastoNoComun) {
        this.saldoGastoNoComun = saldoGastoNoComun;
    }

    public String getSaldoGastoNoComunString() {
        return saldoGastoNoComunString;
    }

    public void setSaldoGastoNoComunString(String saldoGastoNoComunString) {
        this.saldoGastoNoComunString = saldoGastoNoComunString;
    }

    public String getMtoGastoNoComunString() {
        return mtoGastoNoComunString;
    }

    public void setMtoGastoNoComunString(String mtoGastoNoComunString) {
        this.mtoGastoNoComunString = mtoGastoNoComunString;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }

    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdConcepto(Integer idConcepto) {
        this.idConcepto = idConcepto;
    }

    public Integer getIdConcepto() {
        return idConcepto;
    }

    public void setPerGastoNoComun(String perGastoNoComun) {
        this.perGastoNoComun = perGastoNoComun;
    }

    public String getPerGastoNoComun() {
        return perGastoNoComun;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }
}
