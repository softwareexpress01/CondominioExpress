/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Calendar;
import datosbasicos.modelos.Cuenta;

/**
 *
 * @author FrankRH
 */
public class MovBancario implements Serializable {

    public Integer id;
    private Integer idBcoMovBancario;
    private Integer idCtaMovBancario;
    private String tipoOperMovBancario;
    private Calendar fecMovBancario;
    private String refMovBancario;
    private Double mtoMovBancario;
    private String descMovBancario;
    private Integer idPago;
    private Integer idPagoAdelantado;
    
    /* Asociaciones */
    private Cuenta cuenta;
    
   
    public MovBancario() {
    }

    
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

   
    public void setIdBcoMovBancario(Integer idBcoMovBancario) {
        this.idBcoMovBancario = idBcoMovBancario;
    }
    public Integer getIdBcoMovBancario() {
        return idBcoMovBancario;
    }

    
    public void setIdCtaMovBancario(Integer idCtaMovBancario) {
        this.idCtaMovBancario = idCtaMovBancario;
    }
    public Integer getIdCtaMovBancario() {
        return idCtaMovBancario;
    }

    
    public void setTipoOperMovBancario(String tipoOperMovBancario) {
        this.tipoOperMovBancario = tipoOperMovBancario;
    }
    public String getTipoOperMovBancario() {
        return tipoOperMovBancario;
    }

    
    public void setFecMovBancario(Calendar fecMovBancario) {
        this.fecMovBancario = fecMovBancario;
    }
    public Calendar getFecMovBancario() {
        return fecMovBancario;
    }


    public void setRefMovBancario(String refMovBancario) {
        this.refMovBancario = refMovBancario;
    }
    public String getRefMovBancario() {
        return refMovBancario;
    }

   
    public void setMtoMovBancario(Double mtoMovBancario) {
        this.mtoMovBancario = mtoMovBancario;
    }
    public Double getMtoMovBancario() {
        return mtoMovBancario;
    }

    
    public void setDescMovBancario(String descMovBancario) {
        this.descMovBancario = descMovBancario;
    }
    public String getDescMovBancario() {
        return descMovBancario;
    }

  
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
    public Cuenta getCuenta() {
        return cuenta;
    }
    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }
    public Integer getIdPago() {
        return idPago;
    }
    
    public void setIdPagoAdelantado(Integer idPagoAdelantado) {
        this.idPagoAdelantado = idPagoAdelantado;
    }
    public Integer getIdPagoAdelantado() {
        return idPagoAdelantado;
    }


}
