/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Fondo;
import movimientos.modelos.TranFondo;
import movimientos.modelos.TranFondoDT;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.GregorianCalendar;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import movimientos.modelos.MovBancario;

public class TranFondoCtrl extends DaoCrud<TranFondo, Fondo, Integer> implements Serializable {

    private String idFondoTranFondo;
    private String fecTranFondo;
    private String docTranFondo;
    private String idConceptoTranFondo;
    private String idBancoTranFondo;
    private String idCuentaTranFondo;
    private String formaPagoTranFondo;
    private String mtoTranFondo;
    private String tipoOperTranFondo;
    private String descTranFondo;
    private String saldoTranFondo;
    private String estadoTranFondo;
    private String perTranFondo;
    private Collection listBan = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Fondo fondo;
    private Collection<TranFondoDT> listaTranFondoDT = new ArrayList<TranFondoDT>();
    private Collection listaOrdII = new ArrayList();
    Collection<TranFondo> listFondo = new ArrayList<TranFondo>();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private String mtoPre;
    private String tipoFondo;
    private TranFondoDT filaSeleccionada;

    public TranFondoCtrl() {
    }

    public void inicializarPanel() {

        this.setIdFondoTranFondo(null);
        this.setFecTranFondo(null);
        this.setDocTranFondo(null);
        this.setIdConceptoTranFondo(null);
        this.setIdBancoTranFondo(null);
        this.setMtoTranFondo(null);
        this.setDescTranFondo(null);
        this.setPerTranFondo(null);

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

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Integer idFondoInteger = Integer.parseInt(idFondoTranFondo);

        if (!(idFondoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Fondo, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idFondoTranFondo)) {
            mensaje = " Aun no ha seleccionado elFondo, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esEntero(docTranFondo)) {
            mensaje = "El campo Documento deber ser un valor numérico entero, por favor intente de nuevo ";
            return mensaje;
        }

        if (fecTranFondo.equals(enBlanco)) {
            mensaje = "La Fecha del Movimiento está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esFecha(fecTranFondo)) {
            mensaje = "La Fecha debe ser con el formato DD-MM-AAAA. Por favor intente de nuevo ";
            return mensaje;
        }

        Integer idConceptoInteger = Integer.parseInt(idConceptoTranFondo);

        if (!(idConceptoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idConceptoTranFondo)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }

        if (descTranFondo.equals(enBlanco)) {
            mensaje = "La descripción de la Operación está en blanco. Por favor intente de nuevo.";
            return mensaje;
        }


        //-------------------------------------------------------------------
        // Verifica que haya ingresado el periodo
        //--------------------------------------------------------------------
        String perGasto = perTranFondo;

        if (perGasto.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el periodo al cual va a asignar el gasto  ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que el formato del periodo sea el adecuado
        //--------------------------------------------------------------------

        if (!val.esFormatoPeriodo(this.perTranFondo)) {
            mensaje = "El formato del periodo debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que el formato este dentro del rango adecuado o valido
        //--------------------------------------------------------------------

        if (!val.esRangoValido(this.perTranFondo)) {
            mensaje = "El periodo ingresado esta fuera del rango permitido, por favor intente de nuevo...";
            return mensaje;
        }

        Integer idBancoInteger = Integer.parseInt(idBancoTranFondo);

        if (!(idBancoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Banco, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idBancoTranFondo)) {
            mensaje = " Aun no ha seleccionado el Banco, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esMonto(mtoTranFondo)) {
            mensaje = "El campo Monto debe ser numérico. Use punto decimal si lo requiere, por favor intente de nuevo ";
            return mensaje;
        }

        Integer idCuentaInteger = Integer.parseInt(idCuentaTranFondo);

        if (!(idCuentaInteger > 0)) {
            mensaje = " Aun no ha seleccionado la cuenta, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idCuentaTranFondo)) {
            mensaje = " Aun no ha seleccionado la cuenta, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getFondosPorCondominio() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        // Obtiene los  para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Fondo> listFondo = new ArrayList<Fondo>();
        String nombQuery = "buscarFondosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listFondo = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listFondo.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }
        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {
            Fondo fon = (Fondo) it.next();
            listaOrd.add(new SelectItem(fon.getId(), fon.getDescFondo()));
        }

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Fondo> listaDesOrdenada = new ArrayList<Fondo>();

        for (Iterator<Fondo> it = lista.iterator();
                it.hasNext();) {
            Fondo fondo1 = (Fondo) it.next();
            listaDesOrdenada.add(fondo1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorFondo());

        Collection<Fondo> coleccionOrdenada = new ArrayList<Fondo>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Fondo fondo2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fondo2);
        }
        return coleccionOrdenada;
    }

    public Collection getConceptosPorCondominio() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));


        // Obtiene los conceptos para el condominio dado (id), los muestra en el panel 1
        // Obtiene el id del condominio del beanUsuario

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Concepto> listCon = new ArrayList<Concepto>();
        String nombQuery = "buscarConceptosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            listCon = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listCon.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }
        for (Iterator<Concepto> it = listCon.iterator();
                it.hasNext();) {
            Concepto con = (Concepto) it.next();
            listaOrd.add(new SelectItem(con.getId(), con.getNombConcepto()));
        }


        return listaOrd;
    }

    public Collection getBancosPorCondominio() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));


        // Obtiene las proveedores para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Banco> listBco = new ArrayList<Banco>();
        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listBco = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listBco.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }
        for (Iterator<Banco> it = listBco.iterator();
                it.hasNext();) {
            Banco bco = (Banco) it.next();
            listaOrd.add(new SelectItem(bco.getId(), bco.getNombBanco()));
        }



        return listaOrd;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBancoTranFondo == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.valueOf(idBancoTranFondo).intValue();
        if (idBcoInteger == 0) {
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

        if (cuentas.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            if (!(cta.getEdoCuenta().equals("INA"))) {
                listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
            }
        }
        return listaOrd;
    }

    public Collection getTranFondoPorFondo() {

        listaTranFondoDT.clear();
        Collection<TranFondo> listFondoPer = new ArrayList<TranFondo>();

        if (idFondoTranFondo == null) {
            return listaTranFondoDT;

        }

        String nombQuery = "buscarTranFondosManualPorIdFondo";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            Integer idFondoInt = Integer.parseInt(idFondoTranFondo);
            listFondo = dataAcces.buscarListaEntidadesPorId(idFondoInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listFondo.isEmpty()) {
            return listaTranFondoDT;
        }
        // Seleccionar solo aquellas con tipoOper = Manual y, dentro de estas, sólo las de Prestaciones "PEND" y también aquellas 
        //  de Reserva o especiales siempre que sean del periodo actual :

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String descFondo;
        String estado;

        for (Iterator<TranFondo> it = listFondo.iterator();
                it.hasNext();) {
            TranFondo tranFdo = (TranFondo) it.next();
            descFondo = buscarFondo(tranFdo.getIdFondoTranFondo());
            estado = tranFdo.getEstadoTranFondo();

            if ("MANUAL".equals(tranFdo.getTipoOperTranFondo())) {

                if ((tranFdo.getPerTranFondo().equals(beanUsuario.getPeriodoActual())) | (tranFdo.getPerTranFondo().after(beanUsuario.getPeriodoActual()))) {
                    listFondoPer.add(tranFdo);
                }

            }
        }


        // -------------------------------

        /* Collection listaOrd = ordenarLista(listFondo);// para ordenar la lista de fondos*/

        listaTranFondoDT = inicializarFondoDT(listFondoPer);

        return listaTranFondoDT;
    }

    private Collection<TranFondoDT> inicializarFondoDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<TranFondo> it = listaOrd.iterator();
                it.hasNext();) {

            TranFondo tranFdo = (TranFondo) it.next();
            if (tranFdo == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {

                Integer idConcInteger = tranFdo.getIdConceptoTranFondo();
                String nombConc = buscarConcepto(idConcInteger);

                String nombBco = "";
                String numCuenta = "";

                if (!(tranFdo.getIdBancoTranFondo() == null)) {

                    Integer idBcoInteger = tranFdo.getIdBancoTranFondo();

                    nombBco = buscarNombBanco(idBcoInteger);
                   
                }

                //Integer idCuentaInteger = Integer.parseInt(idCuentaTranFondo);

                if (!(tranFdo.getIdCuentaTranFondo() == null)) {

                    numCuenta = buscarCuenta(tranFdo.getIdCuentaTranFondo());
                }

                TranFondoDT tranFondoDT = new TranFondoDT();

                tranFondoDT.setId(tranFdo.getId());
                tranFondoDT.setIdFondoTranFondo(tranFdo.getIdFondoTranFondo());

                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = tranFdo.getFecMovTranFondo();
                String dia = "";
                String mes = "";
                int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (diaMov < 10) {
                    dia = "0" + diaMov;
                } else {
                    dia = String.valueOf(diaMov);
                }

                if (mesMov < 10) {
                    mes = "0" + mesMov;
                } else {
                    mes = String.valueOf(mesMov);
                }

                String stringFechaMov = dia + "-" + mes + "-" + añoMov;

                tranFondoDT.setFecTranFondo(stringFechaMov);
                // ----------------------------------------------------------------------------------

                tranFondoDT.setDocTranFondo(tranFdo.getDocTranFondo());
                tranFondoDT.setIdConceptoTranFondo(tranFdo.getIdConceptoTranFondo());
                tranFondoDT.setDescConceptoTranFondo(nombConc);
                tranFondoDT.setIdBancoTranFondo(tranFdo.getIdBancoTranFondo());
                tranFondoDT.setMtoTranFondo(tranFdo.getMtoTranFondo());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                tranFondoDT.setMtoTranFondoString(val.convertirDoubleAString(tranFdo.getMtoTranFondo()));

                tranFondoDT.setTipoOperTranFondo(tranFdo.getTipoOperTranFondo());
                tranFondoDT.setDescTranFondo(tranFdo.getDescTranFondo());
                tranFondoDT.setFormaPagoTranFondo(tranFdo.getFormaPagoTranFondo());
                tranFondoDT.setNombBancoTranFondo(nombBco);
                tranFondoDT.setCuentaTranFondo(numCuenta);

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar periodo = tranFdo.getPerTranFondo();
                String perDia = "";
                String perMes = "";
                int dMov = periodo.get(Calendar.DAY_OF_MONTH);
                int mMov = periodo.get(Calendar.MONTH) + 1;
                int aMov = periodo.get(Calendar.YEAR);

                if (dMov < 10) {
                    perDia = "0" + dMov;
                } else {
                    perDia = String.valueOf(dMov);
                }

                if (mMov < 10) {
                    perMes = "0" + mMov;
                } else {
                    perMes = String.valueOf(mMov);
                }

                String stringPer = perMes + "-" + aMov;
                tranFondoDT.setPerTranFondo(stringPer);

                listaTranFondoDT.add(tranFondoDT);
            }
        }
        return listaTranFondoDT;
    }

    public String buscarFondo(Integer idFondo) {

        String nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces.buscarEntidadPorId(idFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreFdo = fondo.getDescFondo();
        tipoFondo = fondo.getTipoFondo();
        return nombreFdo;
    }

    public String buscarConcepto(Integer idConc) {

        String nombConc;

        if (idConc == 0) {      // Caso en que sea una transacción generada por el cierre . tiene idConcepto = 0 
            nombConc = "Mov. automático del cierre";
            return nombConc;
        }

        Concepto concepto;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        nombConc = concepto.getNombConcepto();
        return nombConc;
    }

    public String buscarNombBanco(Integer idBco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        String nombBco = banco.getNombBanco();

        return nombBco;
    }

    public String buscarCuenta(Integer idCta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...(Cuenta) por favor, intente mas tarde"));
            return (null);
        }
        String numCuenta = "";
        if (!(cuenta == null)) {

            numCuenta = cuenta.getNumCuenta();
        }

        return numCuenta;
    }

    public String getFechaDelServidor() {

        Calendar fecha = Calendar.getInstance(TimeZone.getDefault());
        Date fechaII = fecha.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String fechaIII = formatter.format(fechaII);
        return fechaIII;

    }

    public void editarTranFondoAction() {

        TranFondoDT tranFondoSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Propiedad" en el map #{Propiedad} 
        ctx.getExternalContext().getSessionMap().put("TranFondoDT", tranFondoSel);

        this.setIdFondoTranFondo(tranFondoSel.getIdFondoTranFondo().toString());
        this.setFecTranFondo(tranFondoSel.getFecTranFondo());
        this.setDocTranFondo(tranFondoSel.getDocTranFondo());
        this.setIdConceptoTranFondo(tranFondoSel.getIdConceptoTranFondo().toString());
        setIdBancoTranFondo(tranFondoSel.getIdBancoTranFondo().toString());

        Integer idCuenta = 0;  // 05nov2015 Debido a que hay transacciones anteriores que no tienen id de la cuenta origen
        if (!(tranFondoSel.getIdCuentaTranFondo() == null)) {
            this.setIdCuentaTranFondo(tranFondoSel.getIdCuentaTranFondo().toString());
        } else {
            this.setIdCuentaTranFondo(idCuenta.toString());
        }

        setFormaPagoTranFondo(tranFondoSel.getFormaPagoTranFondo());

        this.setMtoTranFondo(tranFondoSel.getMtoTranFondoString());

        this.setTipoOperTranFondo(tranFondoSel.getTipoOperTranFondo());
        this.setDescTranFondo(tranFondoSel.getDescTranFondo());
        this.setPerTranFondo(tranFondoSel.getPerTranFondo());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(tranFondoSel.getDocTranFondo().toString());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idTranFdo = tranFondoSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Fondos, listaOrd
        TranFondo tranFondo = null;

        for (Iterator<TranFondo> it = listFondo.iterator();
                it.hasNext();) {
            tranFondo = (TranFondo) it.next();
            if (tranFondo.getId().toString().equals(idTranFdo)) {
                //Almacena la fila del fondo seleccionado ,el cual se buscara como "Fondo" en el map #{Fondo} 
                ctx.getExternalContext().getSessionMap().put("TranFondo", tranFondo);
                break;
            }
        }

//Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
        ctx.getExternalContext().getSessionMap().put("TranFondo", tranFondo);


        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarTranFondo() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();


        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        TranFondo tranFondoNuevo = new TranFondo();
        Fondo fondo = null;

        tranFondoNuevo.setIdFondoTranFondo(Integer.parseInt(idFondoTranFondo));

        Calendar fecha = new GregorianCalendar();
        tranFondoNuevo.setFecTranFondo(fecTranFondo);
        tranFondoNuevo.setFecMovTranFondo(fecha);

        tranFondoNuevo.setDocTranFondo(docTranFondo);
        tranFondoNuevo.setIdConceptoTranFondo(Integer.parseInt(idConceptoTranFondo));
        tranFondoNuevo.setIdBancoTranFondo(Integer.parseInt(idBancoTranFondo));
        tranFondoNuevo.setIdCuentaTranFondo(Integer.parseInt(idCuentaTranFondo));
        tranFondoNuevo.setFormaPagoTranFondo(formaPagoTranFondo);
        Double mto = Double.valueOf(mtoTranFondo).doubleValue();
        tranFondoNuevo.setMtoTranFondo(mto);
        tranFondoNuevo.setSaldoTranFondo(mto);

        String descFondo = buscarFondo(Integer.parseInt(idFondoTranFondo));

        /* if ("Prestaciones".equals(tipoFondo)) { 
        tranFondoNuevo.setEstadoTranFondo("PEND");
        } else {
        tranFondoNuevo.setEstadoTranFondo("CANC");
        }
         */
        tranFondoNuevo.setEstadoTranFondo("CANC"); // Modificado por FR ya que se van a pagar todas en el momento 05oct15
        tranFondoNuevo.setTipoOperTranFondo("MANUAL");
        tranFondoNuevo.setDescTranFondo(descTranFondo);
        tranFondoNuevo.setIdPago(0);
        //-------------------------------------------------------------------
        // Convertir periodo a Calendar
        //-------------------------------------------------------------------

        String stringPeriodo = "01-" + perTranFondo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        tranFondoNuevo.setPerTranFondo(cal);
        Integer idFondoInt = Integer.parseInt(idFondoTranFondo);

        try {

            fondo = (Fondo) encontrarEntidadPorId(idFondoInt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        // actualización del Saldo del Fondo. Esta operación suma el monto de la Trans al Saldo 

        Double nuevoSaldo = fondo.getSaldoFondo() + mto;
        fondo.setSaldoFondo(nuevoSaldo);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(fondo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        //______________________________________________________________________________________


        tranFondoNuevo.setFondo(fondo);

        try {

            almacenarOModificarEntidad(tranFondoNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        //______________________________________________________________________________________



        registrarMovBancario(fondo);
        registrarMovBancarioCuentaOrigen(fondo);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Transaccion con el Fondo ha sido registrada satisfactoriamente!"));

        //inicializarPanel();
    }

    public void registrarMovBancario(Fondo fondo) throws ValidatorException, ParseException {


        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idFondoTranFondo);
        MovBancarioNuevo.setIdBcoMovBancario(fondo.getIdBcoFondo());
        MovBancarioNuevo.setIdCtaMovBancario(fondo.getIdCtaFondo());
        MovBancarioNuevo.setTipoOperMovBancario(tipoOperTranFondo);

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecTranFondo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(docTranFondo);
        Double mto = Double.valueOf(mtoTranFondo).doubleValue();
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Ingreso extra a fondos");
        MovBancarioNuevo.setIdPago(null);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoTranFondo);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(fondo.getIdCtaFondo(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
        /*_______________________________________________________________________________________*/

        Double nuevoSaldo = cuenta.getSaldoActCuenta() + mto;
        cuenta.setSaldoActCuenta(nuevoSaldo);

        DaoCrudAux daoCrud2 = new DaoCrudAux();
        try {

            daoCrud2.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

    }

    public void registrarMovBancarioCuentaOrigen(Fondo fondo) throws ValidatorException, ParseException {


        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idBancoTranFondo);
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCuentaTranFondo);
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(tipoOperTranFondo);

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecTranFondo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(docTranFondo);
        Double mto = Double.valueOf(mtoTranFondo).doubleValue();
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario("Aporte extra a fondos");
        MovBancarioNuevo.setIdPago(null);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoTranFondo);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCtaInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
        /*_______________________________________________________________________________________*/

        mto = Double.valueOf(mtoTranFondo).doubleValue();   // Para no restar un valor negativo 17may2018 FRH
        Double nuevoSaldo = cuenta.getSaldoActCuenta() - mto;
        cuenta.setSaldoActCuenta(nuevoSaldo);

        DaoCrudAux daoCrud2 = new DaoCrudAux();
        try {

            daoCrud2.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

    }

    public void setIdFondoTranFondo(String idFondoTranFondo) {
        this.idFondoTranFondo = idFondoTranFondo;
    }

    public String getIdFondoTranFondo() {
        return idFondoTranFondo;
    }

    public void setFecTranFondo(String fecTranFondo) {
        this.fecTranFondo = fecTranFondo;
    }

    public String getFecTranFondo() {
        return fecTranFondo;
    }

    public void setDocTranFondo(String docTranFondo) {
        this.docTranFondo = docTranFondo;
    }

    public String getDocTranFondo() {
        return docTranFondo;
    }

    public void setIdConceptoTranFondo(String idConceptoTranFondo) {
        this.idConceptoTranFondo = idConceptoTranFondo;
    }

    public String getIdConceptoTranFondo() {
        return idConceptoTranFondo;
    }

    public void setIdBancoTranFondo(String idBancoTranFondo) {
        this.idBancoTranFondo = idBancoTranFondo;
    }

    public String getIdBancoTranFondo() {
        return idBancoTranFondo;
    }

    public void setIdCuentaTranFondo(String idCuentaTranFondo) {
        this.idCuentaTranFondo = idCuentaTranFondo;
    }

    public String getIdCuentaTranFondo() {
        return idCuentaTranFondo;
    }

    public void setFormaPagoTranFondo(String formaPagoTranFondo) {
        this.formaPagoTranFondo = formaPagoTranFondo;
    }

    public String getFormaPagoTranFondo() {
        return formaPagoTranFondo;
    }

    public void setMtoTranFondo(String mtoTranFondo) {
        this.mtoTranFondo = mtoTranFondo;
    }

    public String getMtoTranFondo() {
        return mtoTranFondo;
    }

    public void setTipoOperTranFondo(String tipoOperTranFondo) {
        this.tipoOperTranFondo = tipoOperTranFondo;
    }

    public String getTipoOperTranFondo() {
        return tipoOperTranFondo;
    }

    public void setSaldoTranFondo(String saldoTranFondo) {
        this.saldoTranFondo = saldoTranFondo;
    }

    public String getSaldoTranFondo() {
        return saldoTranFondo;
    }

    public void setDescTranFondo(String descTranFondo) {
        this.descTranFondo = descTranFondo;
    }

    public String getDescTranFondo() {
        return descTranFondo;
    }

    public void setEstadoTranFondo(String estadoTranFondo) {
        this.estadoTranFondo = estadoTranFondo;
    }

    public String getEstadoTranFondo() {
        return estadoTranFondo;
    }

    public void setPerTranFondo(String perTranFondo) {
        this.perTranFondo = perTranFondo;
    }

    public String getPerTranFondo() {
        return perTranFondo;
    }

    public void setFilaSeleccionada(TranFondoDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public TranFondoDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
