package saas.modelos;

import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class Suscripcion implements Serializable {

    private Integer id;
    private String nombSuscriptor;
    private String apelSuscriptor;
    private String numCedSuscriptor;
    private String emailSuscriptor;
    private String telSuscriptor;
    private String celSuscriptor;
    private Calendar fechaMovSol;
    private String edoSolSuscripcion;
    private String obsSolSuscripcion;
    private String bcoSolSuscripcion;
    private String numCtaSolSuscripcion;
    private String refSolSuscripcion;
    private Float mtoSolSuscripcion;
    private Calendar fecPagoSolSuscripcion;
    private String numSuscripcion;
    private String contSuscripcion;
    private String edoSuscripcion;
    private String diaCorteSuscripcion;
    private Float saldoSuscripcion;
    private Calendar fecSuscripcion;
    private Collection<Usuario> usuarios = new ArrayList<Usuario>();
    private Collection<PagoMensual> pagosMen = new ArrayList<PagoMensual>();
    public Plan plan;
    public Compañia compañia;

    public Suscripcion() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombSuscriptor(String nombSuscriptor) {
        this.nombSuscriptor = nombSuscriptor;
    }

    public String getNombSuscriptor() {
        return nombSuscriptor;
    }

    public void setApelSuscriptor(String apelSuscriptor) {
        this.apelSuscriptor = apelSuscriptor;
    }

    public String getApelSuscriptor() {
        return apelSuscriptor;
    }

    public void setNumCedSuscriptor(String numCedSuscriptor) {
        this.numCedSuscriptor = numCedSuscriptor;
    }

    public String getNumCedSuscriptor() {
        return numCedSuscriptor;
    }

    public void setEmailSuscriptor(String emailSuscriptor) {
        this.emailSuscriptor = emailSuscriptor;
    }

    public String getEmailSuscriptor() {
        return emailSuscriptor;
    }

    public void setTelSuscriptor(String telSuscriptor) {
        this.telSuscriptor = telSuscriptor;
    }

    public String getTelSuscriptor() {
        return telSuscriptor;
    }

    public void setCelSuscriptor(String celSuscriptor) {
        this.celSuscriptor = celSuscriptor;
    }

    public String getCelSuscriptor() {
        return celSuscriptor;
    }

    public void setFechaMovSol(Calendar fechaMovSol) {
        this.fechaMovSol = fechaMovSol;
    }

    public Calendar getFechaMovSol() {
        return fechaMovSol;
    }

    public void setEdoSolSuscripcion(String edoSolSuscripcion) {
        this.edoSolSuscripcion = edoSolSuscripcion;
    }

    public String getEdoSolSuscripcion() {
        return edoSolSuscripcion;
    }

    public void setObsSolSuscripcion(String obsSolSuscripcion) {
        this.obsSolSuscripcion = obsSolSuscripcion;
    }

    public String getObsSolSuscripcion() {
        return obsSolSuscripcion;
    }

    public void setBcoSolSuscripcion(String bcoSolSuscripcion) {
        this.bcoSolSuscripcion = bcoSolSuscripcion;
    }

    public String getBcoSolSuscripcion() {
        return bcoSolSuscripcion;
    }

    public void setNumCtaSolSuscripcion(String numCtaSolSuscripcion) {
        this.numCtaSolSuscripcion = numCtaSolSuscripcion;
    }

    public String getNumCtaSolSuscripcion() {
        return numCtaSolSuscripcion;
    }

    public void setRefSolSuscripcion(String refSolSuscripcion) {
        this.refSolSuscripcion = refSolSuscripcion;
    }

    public String getRefSolSuscripcion() {
        return refSolSuscripcion;
    }

    public void setMtoSolSuscripcion(Float mtoSolSuscripcion) {
        this.mtoSolSuscripcion = mtoSolSuscripcion;
    }

    public Float getMtoSolSuscripcion() {
        return mtoSolSuscripcion;
    }

    public void setFecPagoSolSuscripcion(Calendar fecPagoSolSuscripcion) {
        this.fecPagoSolSuscripcion = fecPagoSolSuscripcion;
    }

    public Calendar getFecPagoSolSuscripcion() {
        return fecPagoSolSuscripcion;
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }

    public void setContSuscripcion(String contSuscripcion) {
        this.contSuscripcion = contSuscripcion;
    }

    public String getContSuscripcion() {
        return contSuscripcion;
    }

    public void setEdoSuscripcion(String edoSuscripcion) {
        this.edoSuscripcion = edoSuscripcion;
    }

    public String getEdoSuscripcion() {
        return edoSuscripcion;
    }

    public void setDiaCorteSuscripcion(String diaCorteSuscripcion) {
        this.diaCorteSuscripcion = diaCorteSuscripcion;
    }

    public String getDiaCorteSuscripcion() {
        return diaCorteSuscripcion;
    }

    public void setSaldoSuscripcion(Float saldoSuscripcion) {
        this.saldoSuscripcion = saldoSuscripcion;
    }

    public Float getSaldoSuscripcion() {
        return saldoSuscripcion;
    }

    public void setFecSuscripcion(Calendar fecSuscripcion) {
        this.fecSuscripcion = fecSuscripcion;
    }

    public Calendar getFecSuscripcion() {
        return fecSuscripcion;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setCompañia(Compañia compañia) {
        this.compañia = compañia;
    }

    public Compañia getCompañia() {
        return compañia;
    }

    public void setUsuarios(Collection<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios;
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    public void setPagosMen(Collection<PagoMensual> pagosMen) {
        this.pagosMen = pagosMen;
    }

    public Collection<PagoMensual> getPagosMen() {
        return pagosMen;
    }

    public void addPagosMen(PagoMensual pagosMen) {
        this.pagosMen.add(pagosMen);
    }
}
