/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Chequera;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;

import javax.faces.component.UIData;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

public class ChequeraCtrl extends DaoCrud<Chequera, Cuenta, Integer> implements Serializable {

    private String idBanco;
    private String idCuenta;
    private String numChequera;
    private String edoChequera;
    private String cantCheques;
    private String primerCheque;
    private String ultimoCheque;
    private Collection listBan = new ArrayList();
    List bancos = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private boolean primerRequest = true;
    private Chequera filaSeleccionada;

    public ChequeraCtrl() {
    }

    public void inicializarPanel() {

        this.setNumChequera(null);
        this.setEdoChequera(null);
        this.setCantCheques(null);
        this.setPrimerCheque(null);
        this.setUltimoCheque(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        return "datosbasicos";

    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Integer idBcoInteger = Integer.parseInt(idBanco);

        if (!(idBcoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el banco, por favor intente de nuevo";
            return mensaje;
        }

        Integer idCtaInteger = Integer.parseInt(idCuenta);

        if (!(idCtaInteger > 0)) {
            mensaje = " Aun no ha seleccionado una Cuenta, por favor intente de nuevo ";
            return mensaje;
        }

        if (numChequera.equals(enBlanco)) {
            mensaje = "El número de chequera está en blanco. Por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esEntero(numChequera)) {
            mensaje = "El campo Número de Chequera no es un número entero mayor que cero. Por favor intente de nuevo ";
            return mensaje;
        }

        if (cantCheques.equals(enBlanco)) {
            mensaje = "El campo Cantidad de cheques está en blanco. Por favor intente de nuevo ";
            return mensaje;
        }
        if (!val.esEntero(cantCheques)) {
            mensaje = "El campo Cantidad de cheques no es un número entero mayor que cero. Por favor intente de nuevo ";
            return mensaje;
        }

        if (primerCheque.equals(enBlanco)) {
            mensaje = "El campo Primer cheque está en blanco. Por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esEntero(primerCheque)) {
            mensaje = "El campo Primer Cheque no es un número entero mayor que cero. Por favor intente de nuevo ";
            return mensaje;
        }

        if (ultimoCheque.equals(enBlanco)) {
            mensaje = "El campo Último cheque está en blanco. Por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esEntero(ultimoCheque)) {
            mensaje = "El campo Último Cheque no es un número entero mayor que cero. Por favor intente de nuevo ";
            return mensaje;
        }

        Integer ultimoCh = Integer.parseInt(ultimoCheque);
        Integer primerCh = Integer.parseInt(primerCheque);
        Integer cantCh = Integer.parseInt(cantCheques);

        if (ultimoCh <= primerCh) {
            mensaje = "El Último Cheque debe ser mayor que el primero. Por favor intente de nuevo ";
            return mensaje;
        }
        Integer nroCheques = (primerCh + cantCh) - 1;

        if (!((ultimoCh > primerCh) & (ultimoCh <= nroCheques))) {
            mensaje = "El número del Último cheque no es válido. Por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();
        if (bancos.isEmpty()) {
            listaOrd.add(new SelectItem(new Integer(0), "Sin configurar"));
        } else {
            listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));
        }
        if (idBanco == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.parseInt(idBanco);

        if (!(idBcoInteger > 0)) {

            return listaOrd;
        }

        String nombQuery = "buscarCuentasPorIdBanco";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            cuentas = dataAcces.buscarListaEntidadesPorId(idBcoInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaOrd;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (cuentas.isEmpty()) {
            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            return listaOrd;

        }

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
        }
        return listaOrd;
    }

    public Collection getChequerasPorCuenta() {

        Collection<Chequera> listChequeras = new ArrayList<Chequera>();

        if (primerRequest) {
            primerRequest = false;
            return listChequeras;
        }

        if (idCuenta == null | "".equals(idCuenta)) {
            return listChequeras;
        }
        // Obtiene las chequeras  para la  cuenta seleccionada (id), los muestra en el panel 2

        String nombQuery = "buscarChequerasPorIdCuenta";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        Integer idCtaInteger = Integer.parseInt(idCuenta);
        try {
            listChequeras = dataAcces.buscarListaEntidadesPorId(idCtaInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listChequeras;
    }

    public List getBancosPorCondominio() {

        listBan.clear();
        listBan.add(new SelectItem(new Integer(0), "Seleccione"));


        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            bancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        if (bancos.isEmpty()) {

            listBan.clear();
            listBan.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return (List) listBan;

        }

        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }
        
        if (beanUsuario.modificar){
            beanUsuario.agregar = false;
        }
        
        return (List) listBan;
    }

    public void editarChequeraAction() {


        Chequera chequeraSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Propiedad" en el map #{Propiedad} 
        ctx.getExternalContext().getSessionMap().put("Chequera", chequeraSel);



        this.setIdBanco(idBanco);
        this.setIdCuenta(idCuenta);
        this.setNumChequera(chequeraSel.getNumChequera());
        this.setEdoChequera(chequeraSel.getEdoChequera().toString());
        this.setCantCheques(chequeraSel.getCantCheques());
        this.setPrimerCheque(chequeraSel.getPrimerCheque());
        this.setUltimoCheque(chequeraSel.getUltimoCheque());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(chequeraSel.getNumChequera().toString());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarChequera() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();


        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Chequera ChequeraNueva = new Chequera();

        ChequeraNueva.setNumChequera(numChequera);
        ChequeraNueva.setEdoChequera(edoChequera);
        ChequeraNueva.setCantCheques(cantCheques);
        ChequeraNueva.setPrimerCheque(primerCheque);
        ChequeraNueva.setUltimoCheque(ultimoCheque);
        ChequeraNueva.setEdoElimChequera("ACT");

        Cuenta cuenta = null;
        Integer idCtaInteger = Integer.parseInt(idCuenta);
        ChequeraNueva.setIdCuenta(idCtaInteger);
        Integer idBcoInteger = Integer.parseInt(idBanco);
        ChequeraNueva.setIdBanco(idBcoInteger);
        try {
            cuenta = (Cuenta) encontrarEntidadPorId(idCtaInteger);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }


        ChequeraNueva.setCuenta(cuenta);

        try {
            almacenarOModificarEntidad(ChequeraNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Chequera " + ChequeraNueva.getNumChequera() + " ha sido registrada satisfactoriamente!"));

        return;
    }

    public void modificarChequera() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Chequera chequeraSel = new Chequera();
        chequeraSel.setNumChequera(numChequera);
        chequeraSel.setEdoChequera(edoChequera);
        chequeraSel.setCantCheques(cantCheques);
        chequeraSel.setPrimerCheque(primerCheque);
        chequeraSel.setUltimoCheque(ultimoCheque);
        chequeraSel.setEdoElimChequera("ACT");

        Cuenta cuenta = null;

        Integer idCtaInteger = Integer.parseInt(idCuenta);
        chequeraSel.setIdCuenta(idCtaInteger);
        Integer idBcoInteger = Integer.parseInt(idBanco);
        chequeraSel.setIdBanco(idBcoInteger);
        try {
            cuenta = (Cuenta) encontrarEntidadPorId(idCtaInteger);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Chequera beanChequera = (Chequera) JSFUtil.getManagedObject("Chequera");
        chequeraSel.id = beanChequera.id;

        chequeraSel.setCuenta(cuenta);

        try {
            almacenarOModificarEntidad(chequeraSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Falla al modificar"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Chequera " + chequeraSel.getNumChequera() + " ha sido modificada satisfactoriamente!"));

    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setNumChequera(String numChequera) {
        this.numChequera = numChequera;
    }

    public String getNumChequera() {
        return numChequera;
    }

    public void setEdoChequera(String edoChequera) {
        this.edoChequera = edoChequera;
    }

    public String getEdoChequera() {
        return edoChequera;
    }

    public void setCantCheques(String cantCheques) {
        this.cantCheques = cantCheques;
    }

    public String getCantCheques() {
        return cantCheques;
    }

    public void setPrimerCheque(String primerCheque) {
        this.primerCheque = primerCheque;
    }

    public String getPrimerCheque() {
        return primerCheque;
    }

    public void setUltimoCheque(String ultimoCheque) {
        this.ultimoCheque = ultimoCheque;
    }

    public String getUltimoCheque() {
        return ultimoCheque;
    }

    public void setFilaSeleccionada(Chequera filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public Chequera getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
