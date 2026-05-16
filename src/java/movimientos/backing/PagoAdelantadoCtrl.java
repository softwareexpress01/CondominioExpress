/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import movimientos.modelos.PagoAdelantado;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import movimientos.modelos.PagoAdelantadoDT;
import movimientos.modelos.MovBancario;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.GregorianCalendar;
import java.util.Calendar;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.event.ValueChangeEvent;

public class PagoAdelantadoCtrl extends DaoCrud<PagoAdelantado, Inmueble, Integer> implements Serializable {

    private String idInPagoAdelantado;
    private HtmlInputText nombPropPagoAdelantado;
    private HtmlInputTextarea descPagoAdelantado;
    private HtmlInputText fecPagoAdelantado;
    private HtmlInputText mtoPagoAdelantado;
    private HtmlSelectOneMenu formaPagoAdelantado;
    private HtmlInputText refPagoAdelantado;
    private HtmlSelectOneMenu idBcoPagoAdelantado;
    private HtmlSelectOneMenu idCuentaPagoAdelantado;
    //------------------------------------------------
    private Double saldoPagoAdelantado;
    private String estadoPagoAdelantado;
    private Collection listBan = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private Collection<PagoAdelantadoDT> listaPagoAdelantadoDT = new ArrayList<PagoAdelantadoDT>();
    private Collection<PagoAdelantado> listaPagoAdelantado = new ArrayList<PagoAdelantado>();
    private Collection<PagoAdelantado> listaPagoAdelantadoSel = new ArrayList<PagoAdelantado>();
    public String nombProp;
    public String refPagoAdelantadoSel;
    private PagoAdelantadoDT filaSeleccionada;
    private Double mtoPre;

    public PagoAdelantadoCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        nombPropPagoAdelantado.setValue(null);
        descPagoAdelantado.setValue(null);
        fecPagoAdelantado.setValue(null);
        mtoPagoAdelantado.setValue(null);
        formaPagoAdelantado.setValue(null);
        refPagoAdelantado.setValue(null);
        idBcoPagoAdelantado.setValue(null);
        idCuentaPagoAdelantado.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public void inicializarPanel() {

        //setIdInPagoAdelantado(null);
        nombPropPagoAdelantado.setValue(null);
        descPagoAdelantado.setValue(null);
        fecPagoAdelantado.setValue(null);
        mtoPagoAdelantado.setValue(null);
        formaPagoAdelantado.setValue(null);
        refPagoAdelantado.setValue(null);
        idBcoPagoAdelantado.setValue(null);
        idCuentaPagoAdelantado.setValue(null);

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

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado);
        if (idInmueble == 0) {
            mensaje = "Debe seleccionar un Inmueble de la lista, por favor intente de nuevo ";
            return mensaje;
        }
        if ("Seleccione".equals(idInPagoAdelantado)) {
            mensaje = " Aun no ha seleccionado el Inmueble, por favor intente de nuevo";
            return mensaje;
        }

        if (mtoPagoAdelantado.getValue().toString().equals(enBlanco)) {
            mensaje = "El Monto está en blanco . Por favor, intente de nuevo";
            return mensaje;
        }
        if (!val.esMonto(mtoPagoAdelantado.getValue().toString())) {
            mensaje = "El campo Monto debe ser numérico. Use punto decimal si lo requiere, por favor intente de nuevo ";
            return mensaje;
        }

        if (descPagoAdelantado.getValue().toString().equals(enBlanco)) {
            mensaje = "La Descripción está en blanco . Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(descPagoAdelantado.getValue().toString())) {
            mensaje = "El campo Descripción contiene caractéres inválidos, por favor intente de nuevo ";
            return mensaje;
        }

        if (fecPagoAdelantado.getValue().toString().equals(enBlanco)) {
            mensaje = "La Fecha del Pago está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esFecha(fecPagoAdelantado.getValue().toString())) {
            mensaje = "La Fecha del pago no es válida, debe ser con el formato DD-MM-AAAA,  por favor intente de nuevo ";
            return mensaje;
        }

        //  Convertir el string de fecha a Calendar para verificar que la fecha del Mov sea del periodo actual o del siguiente
//--------------------------------------------------------------------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecPagoAdelantado.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mesMov = cal.get(Calendar.MONTH);
        int añoMov = cal.get(Calendar.YEAR);
        Integer mes = mesMov;
        Integer año = añoMov;
        Integer mesMas = mes + 1;

        String periodo = mesMas.toString() + "-" + año.toString();
        if (!val.esRangoValido(periodo)) {
            mensaje = "La Fecha del Pago debe ser del periodo actual o del próximo. Por favor intente de nuevo ";
            return mensaje;
        }

//---------------------------------------------------------------------------------------------------------------------


        if (!val.esLong(refPagoAdelantado.getValue().toString())) {
            mensaje = "El campo Número de Documento debe ser un valor numérico entero positivo, por favor intente de nuevo ";
            return mensaje;
        }

        Integer idBanco = Integer.parseInt(idBcoPagoAdelantado.getValue().toString());
        if (idBanco == 0) {
            mensaje = "Debe seleccionar un Banco de la lista, por favor intente de nuevo ";
            return mensaje;
        }
        if ("Seleccione".equals(idBcoPagoAdelantado.getValue().toString())) {
            mensaje = " Aun no ha seleccionado el Banco, por favor intente de nuevo";
            return mensaje;
        }

        Integer idCuenta = Integer.parseInt(idCuentaPagoAdelantado.getValue().toString());
        if (idCuenta == 0) {
            mensaje = "Debe seleccionar una Cuenta de la lista, por favor intente de nuevo ";
            return mensaje;
        }
        if ("Seleccione".equals(idCuentaPagoAdelantado.getValue().toString())) {
            mensaje = " Aun no ha seleccionado la Cuenta, por favor intente de nuevo";
            return mensaje;
        }

        if ("Sin Configurar".equals(idCuentaPagoAdelantado.getValue().toString())) {
            mensaje = " Aun no ha configurado las Cuentas Bancarias, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(idCuentaPagoAdelantado.getValue().toString())) {
            mensaje = " Aun no ha seleccionado la Cuenta, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(formaPagoAdelantado.getValue().toString())) {
            mensaje = " Aun no ha seleccionado la Forma de Pago, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public String verificarRefPagoAdelantado(String ref) {

        String mensaje = new String();

        if (listaPagoAdelantado.isEmpty()) {
            return mensaje;
        }

        for (Iterator<PagoAdelantado> it = listaPagoAdelantado.iterator();
                it.hasNext();) {
            PagoAdelantado pagoAdel = (PagoAdelantado) it.next();
            if (ref.equals(pagoAdel.getRefPagoAdelantado())) {
                mensaje = "El número de referencia ya existe, por favor intente de nuevo";
            }
        }

        return mensaje;
    }

    public Collection getInmueblesPorPropiedad() {

        listaOrdII.clear();
        listaOrdII.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idProp = beanUsuario.getIdPropActual();

        Collection<Inmueble> listInm = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listInm.isEmpty()) {

            listaOrdII.clear();
            listaOrdII.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrdII;

        }

        listaOrd = ordenarLista(listInm);// para ordenar la lista de inmuebles

        for (Iterator<Inmueble> it = listaOrd.iterator();
                it.hasNext();) {
            Inmueble cta = (Inmueble) it.next();
            listaOrdII.add(new SelectItem(cta.getId(), cta.getCodInmueble()));
        }

        return listaOrdII;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Inmueble> listaDesOrdenada = new ArrayList<Inmueble>();

        for (Iterator<Inmueble> it = lista.iterator();
                it.hasNext();) {
            Inmueble Inmueble1 = (Inmueble) it.next();
            listaDesOrdenada.add(Inmueble1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorInm());

        Collection<Inmueble> coleccionOrdenada = new ArrayList<Inmueble>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Inmueble Inmueble2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(Inmueble2);
        }
        return coleccionOrdenada;
    }

    public HtmlInputText buscarNombPropietario() {


        if (idInPagoAdelantado == null) {
            return null;
        }
        if (idInPagoAdelantado.equals("0")) {
            return null;
        }

        Integer idInmuebleSel = Integer.parseInt(idInPagoAdelantado);

        Inmueble inmueble;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInmuebleSel, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        System.out.println("El nombre del propietario es:" + inmueble.getNombPropInmueble());
        nombPropPagoAdelantado.setValue(inmueble.getNombPropInmueble());

        return nombPropPagoAdelantado;
    }

    public List getBancosPorCondominio() {

        listBan.clear();
        listBan.add(new SelectItem(new Integer(0), "Seleccione"));
        List bancos = new ArrayList();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            bancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
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
            return (List) listBan;

        }


        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listBan;
    }

    public Collection getCuentasPorBanco() {

        Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBcoPagoAdelantado.getValue() == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.valueOf(idBcoPagoAdelantado.getValue().toString()).intValue();
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
            return (List) listaOrd;

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

    public Collection getPagosAdelantadosPorInmueble() {

        listaPagoAdelantadoDT.clear();
        listaPagoAdelantadoSel.clear();

        if (idInPagoAdelantado == null) {
            return null;
        }
        if (idInPagoAdelantado.equals("0")) {
            return null;
        }

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado);
        String nombQuery = "buscarPagosAdelantadosPorIdInmueble";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagoAdelantado = dataAcces.buscarListaEntidadesPorId(idInmueble, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        if (listaPagoAdelantado.isEmpty()) {
            return listaPagoAdelantadoDT;
        }

        /**
         * **************** AQUI SE SELECCIONAN SOLO AQUELLOS OBJETOS CON
         * ESTADO DIFERENTE DE "CANC" *************
         */
        for (Iterator<PagoAdelantado> it = listaPagoAdelantado.iterator();
                it.hasNext();) {
            PagoAdelantado pA = (PagoAdelantado) it.next();
            if (!(pA.estadoPagoAdelantado.equals("CANC"))) {
                listaPagoAdelantadoSel.add(pA);
            }
        }
        if (!listaPagoAdelantadoSel.isEmpty()) {
            buscarNombPropietario();
        }

        /**
         * *******************************************************************************************************
         */
        listaPagoAdelantadoDT = inicializarPagoAdelantadoDT(listaPagoAdelantadoSel);

        return listaPagoAdelantadoDT;
    }

    private Collection<PagoAdelantadoDT> inicializarPagoAdelantadoDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<PagoAdelantado> it = listaOrd.iterator();
                it.hasNext();) {

            PagoAdelantado pagoAdelantado = (PagoAdelantado) it.next();
            if (pagoAdelantado == null) {// Si no hay pagos adelantados devuelve la lista en blanco
                break;
            } else {

                /* Integer idInmInteger = pagoAdelantado.getIdInPagoAdelantado();
                String codInmueble = buscarCodInmueble(idInmInteger);*/

                Integer idBcoInteger = pagoAdelantado.getIdBcoPagoAdelantado();
                String nombBco = buscarNombBanco(idBcoInteger);

                Integer idCuentaInteger = pagoAdelantado.getIdCuentaPagoAdelantado();
                String numCuenta = buscarNumCuenta(idCuentaInteger);

                PagoAdelantadoDT PagoAdelantadoDT = new PagoAdelantadoDT();
                PagoAdelantadoDT.setId(pagoAdelantado.getId());

                /*PagoAdelantadoDT.setIdInPagoAdelantado(pagoAdelantado.getIdInPagoAdelantado());
                PagoAdelantadoDT.setCodInmueblePagoAdelantado(codInmueble);*/
                PagoAdelantadoDT.setDescPagoAdelantado(pagoAdelantado.getDescPagoAdelantado());
                PagoAdelantadoDT.setFecPagoAdelantado(pagoAdelantado.getFecPagoAdelantado());

                PagoAdelantadoDT.setMtoPagoAdelantado(val.convertirDoubleAString(pagoAdelantado.getMtoPagoAdelantado()));

                PagoAdelantadoDT.setFormaPagoAdelantado(pagoAdelantado.getFormaPagoAdelantado());
                PagoAdelantadoDT.setRefPagoAdelantado(pagoAdelantado.getRefPagoAdelantado());
                PagoAdelantadoDT.setIdBcoPagoAdelantado(pagoAdelantado.getIdBcoPagoAdelantado());
                PagoAdelantadoDT.setIdCuentaPagoAdelantado(pagoAdelantado.getIdCuentaPagoAdelantado());
                PagoAdelantadoDT.setNombBcoPagoAdelantado(nombBco);
                PagoAdelantadoDT.setDescCuentaPagoAdelantado(numCuenta);
                PagoAdelantadoDT.setSaldoPagoAdelantado(val.convertirDoubleAString(pagoAdelantado.getSaldoPagoAdelantado()));
                PagoAdelantadoDT.setEstadoPagoAdelantado(pagoAdelantado.getEstadoPagoAdelantado());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = pagoAdelantado.getFecMovPagoAdelantado();
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

                PagoAdelantadoDT.setFecMovPagoAdelantado(stringFechaMov);
                // ----------------------------------------------------------

                listaPagoAdelantadoDT.add(PagoAdelantadoDT);
            }
        }
        return listaPagoAdelantadoDT;
    }

    public String buscarNombBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarNumCuenta(Integer idCuenta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCuenta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String numCuenta = cuenta.getNumCuenta();
        return numCuenta;
    }

    public String buscarCodInmueble(Integer idInmueble) {

        Inmueble inmueble;
        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInmueble, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde  ******** BUSCARiNMUEBLE "));
            return (null);
        }

        String codInmueble = inmueble.getCodInmueble();

        return codInmueble;
    }

    public void editarPagoAdelantadoAction() {

        PagoAdelantadoDT pagoAdelantadoSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("PagoAdelantadoDT", pagoAdelantadoSel);

        descPagoAdelantado.setValue(pagoAdelantadoSel.getDescPagoAdelantado());
        fecPagoAdelantado.setValue(pagoAdelantadoSel.getFecPagoAdelantado());
        mtoPagoAdelantado.setValue(pagoAdelantadoSel.getMtoPagoAdelantado());
        formaPagoAdelantado.setValue(pagoAdelantadoSel.getFormaPagoAdelantado());
        refPagoAdelantado.setValue(pagoAdelantadoSel.getRefPagoAdelantado());

        mtoPre = Double.valueOf(pagoAdelantadoSel.getMtoPagoAdelantado()).doubleValue();
        refPagoAdelantadoSel = pagoAdelantadoSel.getRefPagoAdelantado(); /* Para el Chequeo del número de referencia en caso de Modificar */

        idBcoPagoAdelantado.setValue(pagoAdelantadoSel.getIdBcoPagoAdelantado().toString());
        idCuentaPagoAdelantado.setValue(pagoAdelantadoSel.getIdCuentaPagoAdelantado().toString());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(pagoAdelantadoSel.getRefPagoAdelantado());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idPagoAdelantado = pagoAdelantadoSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de PagoAdelantado, listaOrd
        PagoAdelantado pagoAdelantado = null;

        for (Iterator<PagoAdelantado> it = listaPagoAdelantado.iterator();
                it.hasNext();) {
            pagoAdelantado = (PagoAdelantado) it.next();
            if (pagoAdelantado.getId().toString().equals(idPagoAdelantado)) {

                //Almacena la fila del pago seleccionado ,el cual se buscara como "PagoAdelantado" en el map #{PagoAdelantado} 

                ctx.getExternalContext().getSessionMap().put("PagoAdelantado", pagoAdelantado);
                break;
            }
        }

        //Almacena la fila del pago seleccionado ,el cual se buscara como "PagoAdelantado" en el map #{PagoAdelantado} 

        ctx.getExternalContext().getSessionMap().put("PagoAdelantado", pagoAdelantado);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarPagoAdelantado() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        PagoAdelantado pagoAdelantadoSel = new PagoAdelantado();

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado.toString());
        pagoAdelantadoSel.setIdInPagoAdelantado(idInmueble);
        pagoAdelantadoSel.setDescPagoAdelantado(descPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setFecPagoAdelantado(fecPagoAdelantado.getValue().toString());

        Double mto = Double.valueOf(mtoPagoAdelantado.getValue().toString()).doubleValue();   // de String a Double 
        pagoAdelantadoSel.setMtoPagoAdelantado(mto);

        pagoAdelantadoSel.setFormaPagoAdelantado(formaPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setRefPagoAdelantado(refPagoAdelantado.getValue().toString());

        mensaje = verificarRefPagoAdelantado(refPagoAdelantado.getValue().toString()); /* Se verifica que la referencia no exista en el mov.*/
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Integer idBco = Integer.parseInt(idBcoPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setIdBcoPagoAdelantado(idBco);

        Integer idCuenta = Integer.parseInt(idCuentaPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setIdCuentaPagoAdelantado(idCuenta);

        pagoAdelantadoSel.setSaldoPagoAdelantado(mto);
        pagoAdelantadoSel.setEstadoPagoAdelantado("PEND");

        Calendar fecha = new GregorianCalendar();
        pagoAdelantadoSel.setFecMovPagoAdelantado(fecha);

        Inmueble inmueble = null;
        try {
            inmueble = (Inmueble) encontrarEntidadPorId(idInmueble);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        pagoAdelantadoSel.setInmueble(inmueble);

        DaoQuery<PagoAdelantado> dataAcces = new DaoQuery<PagoAdelantado>();
        Integer idPagoAdelantado;
        try {
            idPagoAdelantado = dataAcces.almacenarEntidad(pagoAdelantadoSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        registrarMovBancario(idPagoAdelantado);


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Pago Adelantado ha sido registrado satisfactoriamente!"));

        //inicializarPanel();

    }

    public void modificarPagoAdelantado() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        // ------------------------- Verificar estado del Pago adelantado 

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoAdelantado pagoAdelantadoCtx = (PagoAdelantado) ctx.getExternalContext().getSessionMap().get("PagoAdelantado");

        if ("EPRO".equals(pagoAdelantadoCtx.getEstadoPagoAdelantado())) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El Pago adelantado seleccionado No se puede modificar. Está en proceso!"));
            return;
        }

        // ------------------------

        PagoAdelantado pagoAdelantadoSel = new PagoAdelantado();

        Integer idInmueble = Integer.parseInt(idInPagoAdelantado);
        pagoAdelantadoSel.setIdInPagoAdelantado(idInmueble);
        pagoAdelantadoSel.setDescPagoAdelantado(descPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setFecPagoAdelantado(fecPagoAdelantado.getValue().toString());

        Double mto = Double.valueOf(mtoPagoAdelantado.getValue().toString()).doubleValue();   // de String a Double 
        pagoAdelantadoSel.setMtoPagoAdelantado(mto);
        pagoAdelantadoSel.setSaldoPagoAdelantado(mto);

        pagoAdelantadoSel.setFormaPagoAdelantado(formaPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setRefPagoAdelantado(refPagoAdelantado.getValue().toString());

        if (!refPagoAdelantado.getValue().toString().equals(refPagoAdelantadoSel)) {
            mensaje = verificarRefPagoAdelantado(refPagoAdelantado.getValue().toString()); /* Se verifica que la referencia no exista en el mov.*/
            if (!mensaje.equals(vacio)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(mensaje));
                return;
            }
        }

        Integer idBco = Integer.parseInt(idBcoPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setIdBcoPagoAdelantado(idBco);

        Integer idCuenta = Integer.parseInt(idCuentaPagoAdelantado.getValue().toString());
        pagoAdelantadoSel.setIdCuentaPagoAdelantado(idCuenta);

        PagoAdelantadoDT pagoAdelantadoDT = (PagoAdelantadoDT) JSFUtil.getManagedObject("PagoAdelantadoDT");

        pagoAdelantadoSel.setEstadoPagoAdelantado(pagoAdelantadoDT.getEstadoPagoAdelantado());

        Calendar fecha = new GregorianCalendar();
        pagoAdelantadoSel.setFecMovPagoAdelantado(fecha);

        Inmueble inmueble = null;
        try {
            inmueble = (Inmueble) encontrarEntidadPorId(idInmueble);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        pagoAdelantadoSel.setInmueble(inmueble);

        pagoAdelantadoSel.id = pagoAdelantadoCtx.getId();

        try {
            almacenarOModificarEntidad(pagoAdelantadoSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }


        modificarMovBancario(pagoAdelantadoCtx.getId());

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Pago Adelantado ha sido modificado satisfactoriamente"));

    }

    public void registrarMovBancario(Integer idPagoAdelantado) throws ValidatorException, ParseException {

        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idBcoPagoAdelantado.getValue().toString());
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCuentaPagoAdelantado.getValue().toString());
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoAdelantado.getValue().toString());

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecPagoAdelantado.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(refPagoAdelantado.getValue().toString());
        Double mto = Double.valueOf(mtoPagoAdelantado.getValue().toString()).doubleValue();
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario(descPagoAdelantado.getValue().toString());
        MovBancarioNuevo.setIdPago(-1); // Para que no lo muestre en el DT de Mov Bancarios el cual selecciona solo aquellos con IdPago = 0
        MovBancarioNuevo.setIdPagoAdelantado(idPagoAdelantado);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCtaInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
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
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));

        }

    }

    public void modificarMovBancario(Integer idPagoAdelantado) throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoAdelantado pagoAdelantadoCtx = (PagoAdelantado) ctx.getExternalContext().getSessionMap().get("PagoAdelantado");

        Integer idCtaInt = pagoAdelantadoCtx.getIdCuentaPagoAdelantado();

        Collection lista = new ArrayList();

        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecPagoAdelantado.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //------------------------------------------------------------

        //String nombQuery = "buscarMovBancariosPorIdCtaRango";
        String nombQuery = "buscarMovBancariosPorCuenta";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCtaInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        if (lista.isEmpty()) { // significa que no hay movimiento bancario para esa cuenta que modificar 
            return;
        }
        MovBancario movimiento = null;
        for (Iterator<MovBancario> it = lista.iterator();
                it.hasNext();) {
            movimiento = (MovBancario) it.next();
            if (movimiento.getIdPagoAdelantado() == idPagoAdelantado) {
                break;
            }
        }
        Integer idBcoInt = Integer.parseInt(idBcoPagoAdelantado.getValue().toString());
        movimiento.setIdBcoMovBancario(idBcoInt);

        movimiento.setIdCtaMovBancario(idCtaInt);
        movimiento.setTipoOperMovBancario(formaPagoAdelantado.getValue().toString());


        movimiento.setFecMovBancario(cal);
        movimiento.setRefMovBancario(refPagoAdelantado.getValue().toString());
        Double mto = Double.valueOf(mtoPagoAdelantado.getValue().toString()).doubleValue();
        movimiento.setMtoMovBancario(mto);
        movimiento.setDescMovBancario(descPagoAdelantado.getValue().toString());
        movimiento.setIdPago(0);
        movimiento.setIdPagoAdelantado(idPagoAdelantado);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery2 = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces2 = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces2.buscarEntidadPorId(idCtaInt, nombQuery2);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        movimiento.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(movimiento);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        // actualización del Saldo de la (s)  Cuenta(s). 
        //_______________________________________________________________________________________

        Integer idCta2 = Integer.parseInt(idCuentaPagoAdelantado.getValue().toString());

        if (idCtaInt == idCta2) {

            Double saldoAnterior = cuenta.getSaldoActCuenta() - mtoPre;
            Double nuevoSaldo = saldoAnterior + mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);

            DaoCrudAux daoCrud2 = new DaoCrudAux();
            try {

                daoCrud2.almacenarOModificarEntidad(cuenta);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde ///// 8"));

            }
        } else {
            Double saldoAnterior = cuenta.getSaldoActCuenta() - mtoPre;
            cuenta.setSaldoActCuenta(saldoAnterior);
            DaoCrudAux daoCrud2 = new DaoCrudAux();
            try {

                daoCrud2.almacenarOModificarEntidad(cuenta);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde ///// 8"));

            }
            Cuenta cuenta2 = null;
            String nombQuery3 = "buscarCuentaPorId";

            DaoQuery<Cuenta> dataAcces3 = new DaoQuery<Cuenta>();
            try {
                cuenta2 = dataAcces3.buscarEntidadPorId(idCta2, nombQuery3);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
                return;
            }
            Double nuevoSaldo = cuenta2.getSaldoActCuenta() + mto;
            cuenta2.setSaldoActCuenta(nuevoSaldo);
            try {

                daoCrud2.almacenarOModificarEntidad(cuenta2);

            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));

            }

        }

    }

    public void setIdInPagoAdelantado(String idInPagoAdelantado) {
        this.idInPagoAdelantado = idInPagoAdelantado;
    }

    public String getIdInPagoAdelantado() {
        return idInPagoAdelantado;
    }

    public HtmlInputText getNombPropPagoAdelantado() {
        return nombPropPagoAdelantado;
    }

    public void setNombPropPagoAdelantado(HtmlInputText nombPropPagoAdelantado) {
        this.nombPropPagoAdelantado = nombPropPagoAdelantado;
    }

    public HtmlInputTextarea getDescPagoAdelantado() {
        return descPagoAdelantado;
    }

    public void setDescPagoAdelantado(HtmlInputTextarea descPagoAdelantado) {
        this.descPagoAdelantado = descPagoAdelantado;
    }

    public HtmlInputText getFecPagoAdelantado() {
        return fecPagoAdelantado;
    }

    public void setFecPagoAdelantado(HtmlInputText fecPagoAdelantado) {
        this.fecPagoAdelantado = fecPagoAdelantado;
    }

    public void setMtoPagoAdelantado(HtmlInputText mtoPagoAdelantado) {
        this.mtoPagoAdelantado = mtoPagoAdelantado;
    }

    public HtmlInputText getMtoPagoAdelantado() {
        return mtoPagoAdelantado;
    }

    public HtmlSelectOneMenu getFormaPagoAdelantado() {
        return formaPagoAdelantado;
    }

    public void setFormaPagoAdelantado(HtmlSelectOneMenu formaPagoAdelantado) {
        this.formaPagoAdelantado = formaPagoAdelantado;
    }

    public HtmlInputText getRefPagoAdelantado() {
        return refPagoAdelantado;
    }

    public void setRefPagoAdelantado(HtmlInputText refPagoAdelantado) {
        this.refPagoAdelantado = refPagoAdelantado;
    }

    public HtmlSelectOneMenu getIdBcoPagoAdelantado() {
        return idBcoPagoAdelantado;
    }

    public void setIdBcoPagoAdelantado(HtmlSelectOneMenu idBcoPagoAdelantado) {
        this.idBcoPagoAdelantado = idBcoPagoAdelantado;
    }

    public HtmlSelectOneMenu getIdCuentaPagoAdelantado() {
        return idCuentaPagoAdelantado;
    }

    public void setIdCuentaPagoAdelantado(HtmlSelectOneMenu idCuentaPagoAdelantado) {
        this.idCuentaPagoAdelantado = idCuentaPagoAdelantado;
    }

    public void setSaldoPagoAdelantado(Double saldoPagoAdelantado) {
        this.saldoPagoAdelantado = saldoPagoAdelantado;
    }

    public Double getSaldoPagoAdelantado() {
        return saldoPagoAdelantado;
    }

    public void setEstadoPagoAdelantado(String estadoPagoAdelantado) {
        this.estadoPagoAdelantado = estadoPagoAdelantado;
    }

    public String getEstadoPagoAdelantado() {
        return estadoPagoAdelantado;
    }

    public void setFilaSeleccionada(PagoAdelantadoDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoAdelantadoDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
