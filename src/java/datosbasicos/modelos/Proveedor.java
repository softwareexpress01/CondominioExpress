/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.PagoCxP;
/**
 *
 * @author Frank R.H.
 */
public class Proveedor implements Serializable {

    public Integer id;
    
    private String nombProveedor;
    private String cedRifProveedor;
    private String dirProveedor;
    private String emailProveedor;
    private String contactoProveedor;
    private String tel1Proveedor;
    private String tel2Proveedor;
    private String telCelProveedor;
    private String edoProveedor;
    
    
    /* Asociaciones */
    private Condominio condominio;
    private Collection<DeudaPendCxP> deudasPendientesCxP = new ArrayList<DeudaPendCxP>();
    private Collection<NotaCredDebCxP> notasCredDebCxP = new ArrayList<NotaCredDebCxP>();
    private Collection<PagoCxP> pagosCxP = new ArrayList<PagoCxP>();

    public Proveedor() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }

    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setCedRifProveedor(String cedRifProveedor) {
        this.cedRifProveedor = cedRifProveedor;
    }

    public String getCedRifProveedor() {
        return cedRifProveedor;
    }

    public void setDirProveedor(String dirProveedor) {
        this.dirProveedor = dirProveedor;
    }

    public String getDirProveedor() {
        return dirProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setContactoProveedor(String contactoProveedor) {
        this.contactoProveedor = contactoProveedor;
    }

    public String getContactoProveedor() {
        return contactoProveedor;
    }

    public void setTel1Proveedor(String tel1Proveedor) {
        this.tel1Proveedor = tel1Proveedor;
    }

    public String getTel1Proveedor() {
        return tel1Proveedor;
    }


    public void setTel2Proveedor(String tel2Proveedor) {
        this.tel2Proveedor = tel2Proveedor;
    }

    public String getTel2Proveedor() {
        return tel2Proveedor;

    }


    public void setTelCelProveedor(String telCelProveedor) {
        this.telCelProveedor = telCelProveedor;
    }

    public String getTelCelProveedor() {
        return telCelProveedor;

    }
    
    public void setEdoProveedor(String edoProveedor) {
        this.edoProveedor = edoProveedor;
    }

    public String getEdoProveedor() {
        return edoProveedor;

    }

    public void setCondominio(Condominio condominio) {
        this.condominio = condominio;
    }

    public Condominio getCondominio() {
        return condominio;
    }
    
    public void setDeudasPendientesCxP(Collection<DeudaPendCxP> deudasPendientesCxP) {
        this.deudasPendientesCxP = deudasPendientesCxP;
    }

    public Collection<DeudaPendCxP> getDeudasPendientesCxP() {
        return deudasPendientesCxP;
    }
    
    public void setNotasCredDebCxP(Collection<NotaCredDebCxP> notasCredDebCxP) {
        this.notasCredDebCxP = notasCredDebCxP;
    }

    public Collection<NotaCredDebCxP> getNotasCredDebCxP() {
        return notasCredDebCxP;
    }
    
     public void setPagosCxP(Collection<PagoCxP> pagosCxP) {
        this.pagosCxP = pagosCxP;
    }

    public Collection<PagoCxP> getPagosCxP() {
        return pagosCxP;
    }
}
