/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.CuentaDT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

public class CuentaCtrl extends DaoCrud<Cuenta, Banco, Integer> implements Serializable {

    private Integer nombBanco;
    private String numCuenta;
    private String tipoCuenta;
    private String saldoIniCuenta;
    private String saldoActCuenta;
    private List listBan = new ArrayList();
    private Collection listOrd = new ArrayList();
    private Collection listaOrd = new ArrayList();
    Collection<CuentaDT> listaOrdDT = new ArrayList<CuentaDT>();
    private CuentaDT filaSeleccionada;

    public CuentaCtrl() {
    }

    public void inicializarPanel() {

        //this.setNombBanco(null);
        this.setNumCuenta(null);
        this.setTipoCuenta(null);
        this.setSaldoIniCuenta(null);
        this.setSaldoActCuenta(null);

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
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;


        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;


        return "datosbasicos";

    }

    public String validarPanel() {

        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");


        if (!(nombBanco > 0)) {
            mensaje = " Aun no ha seleccionado el banco, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esNumCta(numCuenta)) {
            mensaje = "El Formato del Número de cuenta debe ser: 1111-1111-11-1234567890 ";
            return mensaje;
        }

        String sb = "Seleccione";
        if (tipoCuenta.contentEquals(sb)) {
            mensaje = " No ha seleccionado el Tipo de Cuenta, por favor intente de nuevo ";
            return mensaje;
        }
        if (saldoIniCuenta.length() == 0) {
            mensaje = "El Monto inicial esta en blanco, por favor intente de nuevo ";
            return mensaje;

        }
        if (!val.esMontoCero(saldoIniCuenta)) {
            mensaje = "El Monto Inicial debe ser mayor o igual a cero ";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getCuentasPorBanco() {

        if (nombBanco == null) {
            /*nombBanco = 0;*/
            return listaOrdDT;
        }

        // Obtiene las cuentas bancarias  para el  banco seleccionado (id), los muestra en el panel 2

        Integer idBanInteger = nombBanco;
        Collection<Cuenta> listCta = new ArrayList<Cuenta>();
        Collection<Cuenta> listAct = new ArrayList<Cuenta>();

        String nombQuery = "buscarCuentasPorIdBanco";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            listCta = dataAcces.buscarListaEntidadesPorId(idBanInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (listCta.isEmpty()) {
            listaOrdDT.clear();
            return listaOrdDT;
        }

        for (Iterator<Cuenta> it = listCta.iterator();
                it.hasNext();) {
            Cuenta cuenta = (Cuenta) it.next();
            if ("ACT".equals(cuenta.getEdoCuenta())) {
                listAct.add(cuenta);
            }
        }

        listaOrd = ordenarLista(listAct);// para ordenar la lista de cuentas


        listaOrdDT = inicializarCuentasDT(listaOrd);


        return listaOrdDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Cuenta> listaDesOrdenada = new ArrayList<Cuenta>();

        for (Iterator<Cuenta> it = lista.iterator();
                it.hasNext();) {
            Cuenta con = (Cuenta) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCuenta());//devuelve la lista ordenada en listaDesOrdenada

        Collection<Cuenta> coleccionOrdenada = new ArrayList<Cuenta>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Cuenta ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    private Collection<CuentaDT> inicializarCuentasDT(Collection listaCuentas) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Collection<CuentaDT> listaCuentasDT = new ArrayList();

        for (Iterator<Cuenta> it = listaCuentas.iterator();
                it.hasNext();) {

            Cuenta cuenta = (Cuenta) it.next();
            if (cuenta == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {

                CuentaDT CuentaDT = new CuentaDT();

                CuentaDT.setNombBanco(cuenta.getNombBanco());
                CuentaDT.setId(cuenta.getId());
                CuentaDT.setNumCuenta(cuenta.getNumCuenta());
                CuentaDT.setTipoCuenta(cuenta.getTipoCuenta());
                CuentaDT.setSaldoIniCuenta(cuenta.getSaldoIniCuenta());
                CuentaDT.setSaldoIniCuentaString(val.convertirDoubleAString(cuenta.getSaldoIniCuenta()));
                CuentaDT.setSaldoActCuenta(cuenta.getSaldoActCuenta());
                CuentaDT.setSaldoActCuentaString(val.convertirDoubleAString(cuenta.getSaldoActCuenta()));

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos con estado diferente de Cancelado
                //----------------------------------------------------------------------------------

                String cancelado = "CANC";
                if (!cuenta.getEdoCuenta().equals(cancelado)) {
                    listaCuentasDT.add(CuentaDT);
                }

            }
        }
        return listaCuentasDT;
    }

    public void cuentasPorBanco(AjaxBehaviorEvent event) {
        return;
        //  getCuentasPorBanco();

    }

    public List getBancosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        listBan.clear();
        listBan.add(new SelectItem(new Integer(0), "Seleccione"));
        List bancos = new ArrayList();

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
        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        if (bancos.isEmpty()) {

            listBan.clear();
            listBan.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listBan;

        }

        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }


        return (List) listBan;
    }

    public void editarCuentaAction() {

        CuentaDT cuentaSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila de la cuenta  seleccionada la cual se buscara como "Cuenta" en el map} 
        ctx.getExternalContext().getSessionMap().put("Cuenta", cuentaSel);

        this.setNombBanco(cuentaSel.getNombBanco());
        this.setNumCuenta(cuentaSel.getNumCuenta());
        this.setTipoCuenta(cuentaSel.getTipoCuenta());

        this.setSaldoIniCuenta(cuentaSel.getSaldoIniCuentaString());
        this.setSaldoActCuenta(cuentaSel.getSaldoActCuentaString());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(cuentaSel.getNumCuenta().toString());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarCuenta() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();


        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Cuenta cuentaNueva = new Cuenta();

        Integer idBanco = nombBanco;
        cuentaNueva.setNombBanco(idBanco);
        cuentaNueva.setNumCuenta(numCuenta);
        cuentaNueva.setTipoCuenta(tipoCuenta);

        Double mto = Double.valueOf(saldoIniCuenta).doubleValue();
        cuentaNueva.setSaldoIniCuenta(mto);
        cuentaNueva.setSaldoActCuenta(mto);
        cuentaNueva.setEdoCuenta("ACT");

        Banco banco = null;

        try {
            banco = (Banco) encontrarEntidadPorId(idBanco);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        cuentaNueva.setBanco(banco);
        String sb = "Seleccione";
        if (tipoCuenta.contentEquals(sb)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Debe seleccionar el tipo de cuenta... por favor"));
            return;
        }

        try {
            almacenarOModificarEntidad(cuentaNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Cuenta " + cuentaNueva.getNumCuenta() + " ha sido registrada satisfactoriamente!"));


        this.setNumCuenta(null);
        this.setTipoCuenta(null);
        this.setSaldoIniCuenta(null);
        this.setSaldoActCuenta(null);

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        listaOrd.clear();





    }

    public void modificarCuenta() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Cuenta cuentaSel = new Cuenta();

        Integer idBanco = nombBanco;
        cuentaSel.setNombBanco(idBanco);
        cuentaSel.setNumCuenta(numCuenta);
        cuentaSel.setTipoCuenta(tipoCuenta);

        Double mto = Double.valueOf(saldoIniCuenta).doubleValue();
        cuentaSel.setSaldoIniCuenta(mto);

        Boolean hayMovimientos = verificarMov();
        if (!hayMovimientos) {
            cuentaSel.setSaldoActCuenta(mto);
        } else {
            mto = Double.valueOf(saldoActCuenta).doubleValue();
            cuentaSel.setSaldoActCuenta(mto);
        }

        cuentaSel.setEdoCuenta("ACT");

        Banco banco = null;

        try {
            banco = (Banco) encontrarEntidadPorId(idBanco);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        CuentaDT beanCuenta = (CuentaDT) JSFUtil.getManagedObject("Cuenta");
        System.out.println("I de la cuenta es : " + beanCuenta.id);
        cuentaSel.id = beanCuenta.id;
        cuentaSel.setBanco(banco);

        try {
            almacenarOModificarEntidad(cuentaSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Falla al modificar"));
            return;
        }

        this.setNumCuenta(null);
        this.setTipoCuenta(null);
        this.setSaldoIniCuenta(null);
        this.setSaldoActCuenta(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        //beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Cuenta " + cuentaSel.getNumCuenta() + " ha sido modificada satisfactoriamente!"));

    }

    public boolean verificarMov() {


        Collection listMovBancario = new ArrayList();

        CuentaDT beanCuenta = (CuentaDT) JSFUtil.getManagedObject("Cuenta");

        String nombQuery = "buscarMovBancariosPorCuenta";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listMovBancario = dataAcces.buscarListaEntidadesPorId(beanCuenta.id, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return false;
        }

        if (listMovBancario.isEmpty()) {
            return false;
        } else {
            return true;
        }

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

    public void setSaldoIniCuenta(String saldoIniCuenta) {
        this.saldoIniCuenta = saldoIniCuenta;
    }

    public String getSaldoIniCuenta() {
        return saldoIniCuenta;
    }

    public void setSaldoActCuenta(String saldoActCuenta) {
        this.saldoActCuenta = saldoActCuenta;
    }

    public String getSaldoActCuenta() {
        return saldoActCuenta;
    }

    public void setFilaSeleccionada(CuentaDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public CuentaDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}