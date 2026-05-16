/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.modelos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import movimientos.modelos.MovBancario;

/**
 *
 * @author jesus
 */
public class Cuenta implements Serializable {

    public Integer id;
    private Integer nombBanco;
    private String numCuenta;
    private String tipoCuenta;
    private Double saldoIniCuenta;
    private Double saldoActCuenta;
    private String edoCuenta;
    /* Asociaciones */
    private Banco banco;
    private Collection<Chequera> chequeras = new ArrayList<Chequera>();
    private Collection<MovBancario> movBancarios = new ArrayList<MovBancario>();
    private Collection<Fondo> fondos = new ArrayList<Fondo>();

    public Cuenta() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNombBanco(Integer nombBanco) {
        this.nombBanco = nombBanco;
    }

    public Integer getNombBanco() {
        return nombBanco;
    }

    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setSaldoIniCuenta(Double saldoIniCuenta) {
        this.saldoIniCuenta = saldoIniCuenta;
    }

    public Double getSaldoIniCuenta() {
        return saldoIniCuenta;
    }

    public void setSaldoActCuenta(Double saldoActCuenta) {
        this.saldoActCuenta = saldoActCuenta;
    }

    public Double getSaldoActCuenta() {
        return saldoActCuenta;
    }

    public void setEdoCuenta(String edoCuenta) {
        this.edoCuenta = edoCuenta;
    }

    public String getEdoCuenta() {
        return edoCuenta;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Banco getBanco() {
        return banco;
    }
    public void setFondos(Collection<Fondo> fondos) {
        this.fondos = fondos;
    }

    public Collection<Fondo> getFondos() {
        return fondos;
    }

    public void setChequeras(Collection<Chequera> chequeras) {
        this.chequeras = chequeras;
    }

    public Collection<Chequera> getChequeras() {
        return chequeras;
    }

    public void setMovBancarios(Collection<MovBancario> movBancarios) {
        this.movBancarios = movBancarios;
    }

    public Collection<MovBancario> getMovBancarios() {
        return movBancarios;
    }
}
