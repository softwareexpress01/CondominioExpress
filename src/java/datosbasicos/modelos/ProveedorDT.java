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
/**
 *
 * @author jesus
 */
public class ProveedorDT implements Serializable {

    public Integer id;
    
    private String nombProveedor;
    private String cedRifProveedor;
    private String dirProveedor;
    private String emailProveedor;
    private String contactoProveedor;
    private String codTel1Proveedor;
    private String tel1Proveedor;
    private String tel1ConcatProveedor;
    private String codTel2Proveedor;
    private String tel2Proveedor;
    private String tel2ConcatProveedor;
    private String codOperCelProveedor;
    private String telCelProveedor;
    private String telCelConcatProveedor;
    /* Asociaciones */
    private Condominio condominio;
    private Collection<DeudaPendCxP> deudasPendientesCxP = new ArrayList<DeudaPendCxP>();
    private Collection<NotaCredDebCxP> notasCredDebCxP = new ArrayList<NotaCredDebCxP>();

    public ProveedorDT() {
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

    public void setCodTel1Proveedor(String codTel1Proveedor) {
        this.codTel1Proveedor = codTel1Proveedor;
    }

    public String getCodTel1Proveedor() {
        return codTel1Proveedor;
    }

    public void setTel1Proveedor(String tel1Proveedor) {
        this.tel1Proveedor = tel1Proveedor;
    }

    public String getTel1Proveedor() {
        return tel1Proveedor;
    }
    
    public void setTel1ConcatProveedor(String tel1ConcatProveedor) {
        this.tel1ConcatProveedor = tel1ConcatProveedor;
    }

    public String getTel1ConcatProveedor() {
        return tel1ConcatProveedor;
    }

    public void setCodTel2Proveedor(String codTel2Proveedor) {
        this.codTel2Proveedor = codTel2Proveedor;
    }

    public String getCodTel2Proveedor() {
        return codTel2Proveedor;

    }

    public void setTel2Proveedor(String tel2Proveedor) {
        this.tel2Proveedor = tel2Proveedor;
    }

    public String getTel2Proveedor() {
        return tel2Proveedor;

    }

    public void setTel2ConcatProveedor(String tel2ConcatProveedor) {
        this.tel2ConcatProveedor = tel2ConcatProveedor;
    }

    public String getTel2ConcatProveedor() {
        return tel2ConcatProveedor;

    }
    public void setCodOperCelProveedor(String codOperCelProveedor) {
        this.codOperCelProveedor = codOperCelProveedor;
    }

    public String getCodOperCelProveedor() {
        return codOperCelProveedor;

    }

    public void setTelCelProveedor(String telCelProveedor) {
        this.telCelProveedor = telCelProveedor;
    }

    public String getTelCelProveedor() {
        return telCelProveedor;

    }

    public void setTelCelConcatProveedor(String telCelConcatProveedor) {
        this.telCelConcatProveedor = telCelConcatProveedor;
    }

    public String getTelCelConcatProveedor() {
        return telCelConcatProveedor;

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
}
