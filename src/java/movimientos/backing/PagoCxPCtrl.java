package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.*;
import org.hibernate.HibernateException;
import util.*;

public class PagoCxPCtrl extends DaoCrud<PagoCxP, Proveedor, Integer> implements Serializable {

    public String idProveedor;
    private String desPagoCxP;
    private String numDocPagoCxP;
    private String mtoPagoCxP;
    private String formaPagoCxP;
    private String fecDocPagoCxP;
    private String idBcoPagoCxP;
    private String idCuentaPagoCxP;
    private Double saldoPagoCxP = new Double(0);
    private Double PagoCxP = new Double(0);
    private List listaProveedores = new ArrayList();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<DeudaPendCxP> listaDeudaPend = new ArrayList<DeudaPendCxP>();
    private Collection<NotaCredDebCxP> listaNotaCredDeb = new ArrayList<NotaCredDebCxP>();
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunExt> listaGCExt = new ArrayList<GastoComunExt>();
    private Collection<FacturaCxPDT> listaFacturaCxPDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaGNCDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaDeudaPendDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaNotaCredDebDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaGCOrdDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaGCExtDT = new ArrayList<FacturaCxPDT>();
    private Collection<FacturaCxPDT> listaDT = new ArrayList<FacturaCxPDT>();
    private Collection listBan = new ArrayList();
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection listaOrd = new ArrayList();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private FacturaCxPDT[] facSeleccionadas;
    private Integer idFondo;
    private Integer idPagoCxP;
    private Calendar fecCreacion;
    private Condominio condominio;
    private double ceroLogico=0.01; 

    public PagoCxPCtrl() {
    }

    public void inicializarPanel() {


        this.setDesPagoCxP(null);
        this.setNumDocPagoCxP(null);
        this.setMtoPagoCxP(null);
        this.setFormaPagoCxP(null);
        this.setFecDocPagoCxP(null);
        this.setIdBcoPagoCxP(null);
    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.agregar = false;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.agregar = false;

        return "informes";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idProveedor.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Debe selccionar un Proveedor. Por favor intente de nuevo";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.getDesPagoCxP().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la descripcion del pago...";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(this.getDesPagoCxP())) {
            mensaje = "La descripción del pago debe ser alfanumerica, por favor intente de nuevo...";
            return mensaje;
        }
        if (!this.getFormaPagoCxP().equals("Efectivo")) {
            if (this.getNumDocPagoCxP().equals(enBlanco)) {
                mensaje = "Por favor, ingrese el numero del documento ...";
                return mensaje;
            }
            if (!val.esEntero(this.getNumDocPagoCxP())) {
                mensaje = "El numero del documento debe ser un valor entero mayor que cero, por favor intente de nuevo...";
                return mensaje;
            }
        }

        //--------------------------------------------------------------------------------------
        if (this.getMtoPagoCxP().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto del pago ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoPagoCxP())) {
            mensaje = "El monto debe ser un valor numérico mayor que cero, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFormaPagoCxP().equals(enBlanco)) {
            mensaje = "Por favor, seleccione la forma de  pago ...";
            return mensaje;
        }

        if (this.getFecDocPagoCxP().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento de  pago...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDocPagoCxP())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        // -------------------------------- Verificar fecha contra fecha de creación del Condominio 

        condominio = buscarCondominio();
        //-----------------------------------------------------------
        //       obtiene la fecha de creacion del condominio
        //-----------------------------------------------------------
        fecCreacion = condominio.getFecCreacion();
        //-----------------------------------------------------------
        //       la fecha obtenida se pasa a string
        //-----------------------------------------------------------
        String diaString;
        String mesString;

        int dia = fecCreacion.get(Calendar.DAY_OF_MONTH);
        int mes = fecCreacion.get(Calendar.MONTH) + 1;
        int año = fecCreacion.get(Calendar.YEAR);

        if (dia < 10) {
            diaString = "0" + dia;
        } else {
            diaString = String.valueOf(dia);
        }

        if (mes < 10) {
            mesString = "0" + mes;
        } else {
            mesString = String.valueOf(mes);
        }

        String stringFechaCreacion = diaString + "-" + mesString + "-" + String.valueOf(año);

        //-----------------------------------------------------------
        //          Convierte fecha a calendar 
        //-----------------------------------------------------------
        Calendar fecCal = convertirFecha(this.getFecDocPagoCxP());

        //-----------------------------------------------------------
        //     Valida que las fechas esten en un rango valido
        //-----------------------------------------------------------
        String espacio = " ";
        if (!(fecCal.after(fecCreacion) | fecCal.equals(fecCreacion))) {
            mensaje = "La fecha debe ser mayor o igual a" + espacio + stringFechaCreacion + espacio + ", por favor intente de nuevo...";
            return mensaje;
        }

        //  Convertir el string de fecha a Calendar para verificar que la fecha del Mov sea del periodo actual o del siguiente
//--------------------------------------------------------------------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDocPagoCxP);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mesMov = cal.get(Calendar.MONTH);
        int añoMov = cal.get(Calendar.YEAR);
        Integer mesPago = mesMov;
        Integer añoPago = añoMov;
        Integer mesMas = mesPago + 1;

        String periodo = mesMas.toString() + "-" + añoPago.toString();
        if (!val.esRangoValido(periodo)) {
            mensaje = "La Fecha del Pago debe ser del periodo actual o del próximo. Por favor intente de nuevo ";
            return mensaje;
        }
//---------------------------------------------------------------------------------------------------------------------


        // ----------------------------------------------------------------------------------------------

        Integer idBanco = Integer.parseInt(idBcoPagoCxP);
        if (this.getIdBcoPagoCxP().equals(enBlanco) | idBanco.equals(0)) {
            mensaje = "Por favor, seleccione el Banco para esta operación...";
            return mensaje;
        }
        Integer idCuenta = Integer.parseInt(idCuentaPagoCxP);
        if (this.getIdCuentaPagoCxP().equals(enBlanco) | idCuenta.equals(0)) {
            mensaje = "Por favor, seleccione la cuenta para esta operación...";
            return mensaje;
        }

        //--------------------------------------------------------
        // Verifica si selecciono todas o alguna de las facturas 
        //--------------------------------------------------------

        if (facSeleccionadas == null | facSeleccionadas.length == 0) {
            mensaje = "Por favor, seleccione la(s) factura(s) a procesar ...";
            return mensaje;
        }

        // valida que el monto del pago no sea mayor que el monto total de las facturas seleccionadas

        Integer i = 0;
        float montoFacturas = 0;
        float mtoFacCxPFloat = 0;
        Integer seleccionadas = 0;
        seleccionadas = facSeleccionadas.length;
        String monto;

        while (i < seleccionadas) {

            FacturaCxPDT fCxP = (FacturaCxPDT) facSeleccionadas[i];
            monto = fCxP.getSaldoFacCxP();
            mtoFacCxPFloat = Float.parseFloat(monto);
            montoFacturas = montoFacturas + mtoFacCxPFloat;

            i++;

        }

        float mtoPago = Float.parseFloat(this.getMtoPagoCxP());

        if (mtoPago - montoFacturas >= 1) {
            mensaje = "El Monto del Pago no puede ser mayor que el saldo total de las facturas seleccionadas, Por favor, intente de nuevo";
        }

        return mensaje;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return condominio;
        }
        return condominio;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Boolean buscarDocumento(String doc) {

        Boolean existe = null;
        String nombQuery = "buscarPagoCxPPorNumDoc";
        DaoQuery<Boolean> dataAcces = new DaoQuery<Boolean>();
        try {
            existe = dataAcces.buscarEntidadExiste(doc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return existe;
    }

    public Collection getCuentasPorPagarPorProveedor() throws ParseException {


        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idProveedor == null) {
            idProveedor = "0";
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaFacturaCxPDT;
        }

        listaFacturaCxPDT.clear();
        listaGNC.clear();
        listaGNCDT.clear();
        listaDeudaPend.clear();
        listaDeudaPendDT.clear();
        listaNotaCredDeb.clear();
        listaNotaCredDebDT.clear();
        listaGCOrd.clear();
        listaGCOrdDT.clear();
        listaGCExt.clear();
        listaGCExtDT.clear();
        listaDT.clear();

        listaGNC = obtenerFacGastoNoComun();
        listaGNCDT = convertirGNCADT(listaGNC);
        listaDeudaPend = obtenerFacDeudaPend();
        listaDeudaPendDT = convertirDeudaPendADT(listaDeudaPend);
        listaNotaCredDeb = obtenerNotaCredDeb();
        listaNotaCredDebDT = convertirNotaCredDebADT(listaNotaCredDeb);
        listaGCOrd = obtenerGCOrd();
        listaGCOrdDT = convertirGCOrdADT(listaGCOrd);
        listaGCExt = obtenerGCExt();
        listaGCExtDT = convertirGCExtADT(listaGCExt);

        listaDT.addAll(listaGNCDT);
        listaDT.addAll(listaDeudaPendDT);
        listaDT.addAll(listaNotaCredDebDT);
        listaDT.addAll(listaGCOrdDT);
        listaDT.addAll(listaGCExtDT);

        listaFacturaCxPDT = ordenarLista(listaDT);// para ordenar la lista de CxP

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos cuentasde datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        Integer co = new Integer(0);
        for (Iterator<FacturaCxPDT> it = listaFacturaCxPDT.iterator();// Asigna un id a cada objeto del data table
                it.hasNext();) {
            FacturaCxPDT fCxP = (FacturaCxPDT) it.next();
            co = co + 1;
            fCxP.setIdDT(co.toString());
        }
        return listaFacturaCxPDT;
    }

    public Collection obtenerFacGastoNoComun() {

        Collection lista = new ArrayList();
        Collection listaPorProv = new ArrayList();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarGastoNoComunPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        Integer idProvInt = Integer.parseInt(idProveedor);
        for (Iterator<GastoNoComun> it = lista.iterator();
                it.hasNext();) {

            GastoNoComun gnc = (GastoNoComun) it.next();
            if (gnc.getIdProveedor().equals(idProvInt)) {
                if (!("CANC".equals(gnc.getEdoGastoNoComun()))) {
                    listaPorProv.add(gnc);
                }
            }
        }

        return listaPorProv;
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

        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBcoPagoCxP == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.valueOf(idBcoPagoCxP).intValue();
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

    public Collection obtenerFacDeudaPend() {

        Collection<DeudaPendCxP> lista = new ArrayList<DeudaPendCxP>();
        Collection listaPorProv = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idProveedor).intValue();
        String nombQuery = "buscarDeudasPendientesPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        Integer idProvInt = Integer.parseInt(idProveedor);
        for (Iterator<DeudaPendCxP> it = lista.iterator();
                it.hasNext();) {

            DeudaPendCxP dPend = (DeudaPendCxP) it.next();
            if (dPend.idProvDeudaPendCxP.equals(idProvInt)) {
                listaPorProv.add(dPend);
            }
        }

        return listaPorProv;
    }

    public Collection obtenerNotaCredDeb() {

        Collection<NotaCredDebCxP> lista = new ArrayList<NotaCredDebCxP>();

        // Obtiene las notas para el proveedor dado (id), los muestra en el panel 2
        Integer idProv = Integer.valueOf(idProveedor).intValue();
        String nombQuery = "buscarNotasCredDebCxPPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        return lista;
    }

    public Collection obtenerGCOrd() {

        Collection<GastoComunOrd> lista = new ArrayList<GastoComunOrd>();
        Collection listaPorProv = new ArrayList();
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarTodosGastosComunesOrdPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        Integer idProvInt = Integer.parseInt(idProveedor);
        for (Iterator<GastoComunOrd> it = lista.iterator();
                it.hasNext();) {

            GastoComunOrd gCOrd = (GastoComunOrd) it.next();
            if (gCOrd.getIdProveedor().equals(idProvInt)) {
                listaPorProv.add(gCOrd);
            }
        }
        return listaPorProv;
    }

    public Collection obtenerGCExt() {

        Collection<GastoComunExt> lista = new ArrayList<GastoComunExt>();
        Collection listaPorProv = new ArrayList();
        // Obtiene las facturas por gasto comun Extraordinario para el Proveedor dado (id), los muestra en el panel 2
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarTodosGastosComunesExtPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (lista.isEmpty()) {
            return lista;
        }

        Integer idProvInt = Integer.parseInt(idProveedor);
        for (Iterator<GastoComunExt> it = lista.iterator();
                it.hasNext();) {

            GastoComunExt gCExt = (GastoComunExt) it.next();
            if (gCExt.getIdProveedor().equals(idProvInt)) {
                listaPorProv.add(gCExt);
            }
        }

        return listaPorProv;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<FacturaCxPDT> listaDesOrdenada = new ArrayList<FacturaCxPDT>();

        for (Iterator<FacturaCxPDT> it = lista.iterator();
                it.hasNext();) {
            FacturaCxPDT fCxP = (FacturaCxPDT) it.next();
            listaDesOrdenada.add(fCxP);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagoCxP());//devuelve la lista ordenada en listaDesOrdenada

        Collection<FacturaCxPDT> coleccionOrdenada = new ArrayList<FacturaCxPDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            FacturaCxPDT fdt = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    public String convertirFechaString() {

        Calendar fecha = new GregorianCalendar();
        //----------------------------------------------------------------
        //Se da formato a la fecha del dia para asignarla al pago efectivo
        //----------------------------------------------------------------
        String dia = "";
        String mes = "";
        int diaPago = fecha.get(Calendar.DAY_OF_MONTH);
        int mesPago = fecha.get(Calendar.MONTH) + 1;
        int añoPago = fecha.get(Calendar.YEAR);

        if (diaPago < 10) {
            dia = "0" + diaPago;
        } else {
            dia = String.valueOf(diaPago);
        }

        if (mesPago < 10) {
            mes = "0" + mesPago;
        } else {
            mes = String.valueOf(mesPago);
        }

        String stringFechaPago = dia + "-" + mes + "-" + añoPago;

        return stringFechaPago;

    }

    private Collection<FacturaCxPDT> convertirGNCADT(Collection listaFacGNC) {

        Collection listaGNCDT = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaFacGNC.isEmpty()) {
            return listaGNCDT;
        }

        for (Iterator<GastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {

            GastoNoComun fac = (GastoNoComun) it.next();
            if (fac == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxPDT facturaCxPDT = new FacturaCxPDT();

                facturaCxPDT.setId(fac.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fac.getPerGastoNoComun();
                String dia = "";
                String mes = "";
                int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
                int mesFac = fechaFac.get(Calendar.MONTH) + 1;
                int añoFac = fechaFac.get(Calendar.YEAR);

                if (diaFac < 10) {
                    dia = "0" + diaFac;
                } else {
                    dia = String.valueOf(diaFac);
                }

                if (mesFac < 10) {
                    mes = "0" + mesFac;
                } else {
                    mes = String.valueOf(mesFac);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoFac;

                facturaCxPDT.setFecFacCxP(stringFechaDp);
                facturaCxPDT.setCalFecFacCxP(fechaFac);
                facturaCxPDT.setMtoFacCxP(val.convertirDoubleAString(fac.getMtoGastoNoComun()));
                facturaCxPDT.setSaldoFacCxP(val.convertirDoubleAString(fac.getSaldoGastoNoComun()));

                facturaCxPDT.setEdoFacCxP(fac.getEdoGastoNoComun());
                facturaCxPDT.setDiscFacCxP("Gasto no Comun");
                //----------------------------------------------------------------------------------
                // Se busca el concepto del gasto no comun asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fac.getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturaCxPDT.setDesFacCxP(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las CxP que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fac.getEdoGastoNoComun().equals("CANC")) {
                    listaGNCDT.add(facturaCxPDT);
                }

            }
        }
        return listaGNCDT;
    }

    private Collection<FacturaCxPDT> convertirDeudaPendADT(Collection listaDeudaPend) throws ParseException {

        Collection listaDPendDT = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaDeudaPend.isEmpty()) {
            return listaDPendDT;
        }

        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {

            DeudaPendCxP dpend = (DeudaPendCxP) it.next();
            if (dpend == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxPDT facturaCxPDT = new FacturaCxPDT();

                facturaCxPDT.setId(dpend.getId());

                //facturaCxPDT.setFecFacCxP(stringFechaDp);
                facturaCxPDT.setFecFacCxP(dpend.getFecDeudaPendCxP());
                facturaCxPDT.setCalFecFacCxP(convertirFecha(dpend.getFecDeudaPendCxP()));
                facturaCxPDT.setMtoFacCxP(val.convertirDoubleAString(dpend.getMtoDeudaPendCxP()));
                facturaCxPDT.setSaldoFacCxP(val.convertirDoubleAString(dpend.getSaldoDeudaPendCxP()));

                facturaCxPDT.setEdoFacCxP(dpend.getEstatusDeudaPendCxP());
                facturaCxPDT.setDiscFacCxP("Deuda pendiente");
                //----------------------------------------------------------------------------------------
                // Se busca el concepto de la deuda pendiente, luego su descripcion para mostrarla en el DT
                //----------------------------------------------------------------------------------------
                //Integer idDP = dpend.getIdConcepto();
                //Concepto concepto = buscarConcepto(idDP);
                facturaCxPDT.setDesFacCxP(dpend.getDescDeudaPendCxP());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las CxP que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------
                if (!dpend.getEstatusDeudaPendCxP().equals("CANC")) {
                    listaDPendDT.add(facturaCxPDT);
                }

            }
        }
        return listaDPendDT;
    }

    private Collection<FacturaCxPDT> convertirNotaCredDebADT(Collection listaNotaCredDeb) throws ParseException {

        listaNotaCredDebDT.clear();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaNotaCredDeb.isEmpty()) {
            return listaNotaCredDebDT;
        }


        for (Iterator<NotaCredDebCxP> it = listaNotaCredDeb.iterator();
                it.hasNext();) {

            NotaCredDebCxP notaCredDeb = (NotaCredDebCxP) it.next();
            if (notaCredDeb == null) {// Si no hay alicuotas devuelve la lista en blanco
                break;
            } else {

                FacturaCxPDT facturaCxPDT = new FacturaCxPDT();

                facturaCxPDT.setId(notaCredDeb.getId());

                //facturaCxPDT.setFecFacCxP(stringFechaAlic);
                facturaCxPDT.setFecFacCxP(notaCredDeb.getFecNotaCredDebCxP());
                facturaCxPDT.setCalFecFacCxP(convertirFecha(notaCredDeb.getFecNotaCredDebCxP()));
                facturaCxPDT.setMtoFacCxP(val.convertirDoubleAString(notaCredDeb.getMtoNotaCredDebCxP()));
                facturaCxPDT.setSaldoFacCxP(val.convertirDoubleAString(notaCredDeb.getSaldoNotaCredDebCxP()));

                facturaCxPDT.setEdoFacCxP(notaCredDeb.getEdoNotaCredDebCxP());
                facturaCxPDT.setDiscFacCxP("Nota de Credito");
                Integer idCon = notaCredDeb.getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturaCxPDT.setDesFacCxP(concepto.getDescConcepto());

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las CxP que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------
                if (!notaCredDeb.getEdoNotaCredDebCxP().equals("CANC")) {
                    listaNotaCredDebDT.add(facturaCxPDT);
                }

            }
        }
        return listaNotaCredDebDT;
    }

    private Collection<FacturaCxPDT> convertirGCOrdADT(Collection listaGCOrd) {

        listaGCOrdDT.clear();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaGCOrd.isEmpty()) {
            return listaGCOrdDT;
        }

        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {

            GastoComunOrd fce = (GastoComunOrd) it.next();
            if (fce == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxPDT facturaCxPDT = new FacturaCxPDT();

                facturaCxPDT.setId(fce.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getPerGastoComunOrd();
                String dia = "";
                String mes = "";
                int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
                int mesFac = fechaFac.get(Calendar.MONTH) + 1;
                int añoFac = fechaFac.get(Calendar.YEAR);

                if (diaFac < 10) {
                    dia = "0" + diaFac;
                } else {
                    dia = String.valueOf(diaFac);
                }

                if (mesFac < 10) {
                    mes = "0" + mesFac;
                } else {
                    mes = String.valueOf(mesFac);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoFac;

                facturaCxPDT.setFecFacCxP(stringFechaDp);
                facturaCxPDT.setCalFecFacCxP(fechaFac);
                facturaCxPDT.setMtoFacCxP(val.convertirDoubleAString(fce.getMtoGastoComunOrd()));
                facturaCxPDT.setSaldoFacCxP(val.convertirDoubleAString(fce.getSaldoGastoComunOrd()));

                facturaCxPDT.setEdoFacCxP(fce.getEdoGastoComunOrd());
                facturaCxPDT.setDiscFacCxP("Gasto Comun Ord");
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la cuota especial asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fce.getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturaCxPDT.setDesFacCxP(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las CxP que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fce.getEdoGastoComunOrd().equals("CANC")) {
                    listaGCOrdDT.add(facturaCxPDT);
                }

            }
        }
        return listaGCOrdDT;
    }

    private Collection<FacturaCxPDT> convertirGCExtADT(Collection listaGCExt) {

        listaGCExtDT.clear();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaGCExt.isEmpty()) {
            return listaGCExtDT;
        }

        for (Iterator<GastoComunExt> it = listaGCExt.iterator();
                it.hasNext();) {

            GastoComunExt fce = (GastoComunExt) it.next();
            if (fce == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxPDT facturaCxPDT = new FacturaCxPDT();

                facturaCxPDT.setId(fce.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getPerGastoComunExt();
                String dia = "";
                String mes = "";
                int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
                int mesFac = fechaFac.get(Calendar.MONTH) + 1;
                int añoFac = fechaFac.get(Calendar.YEAR);

                if (diaFac < 10) {
                    dia = "0" + diaFac;
                } else {
                    dia = String.valueOf(diaFac);
                }

                if (mesFac < 10) {
                    mes = "0" + mesFac;
                } else {
                    mes = String.valueOf(mesFac);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoFac;

                facturaCxPDT.setFecFacCxP(stringFechaDp);
                facturaCxPDT.setCalFecFacCxP(fechaFac);
                facturaCxPDT.setMtoFacCxP(val.convertirDoubleAString(fce.getMtoGastoComunExt()));
                facturaCxPDT.setSaldoFacCxP(val.convertirDoubleAString(fce.getSaldoGastoComunExt()));

                facturaCxPDT.setEdoFacCxP(fce.getEdoGastoComunExt());
                facturaCxPDT.setDiscFacCxP("Gasto Comun Ext");
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la cuota especial asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fce.getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturaCxPDT.setDesFacCxP(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las CxP que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fce.getEdoGastoComunExt().equals("CANC")) {
                    listaGCExtDT.add(facturaCxPDT);
                }

            }
        }
        return listaGCExtDT;
    }

    public Concepto buscarConcepto(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto;
    }

    public List getProveedoresPorCondominio() {


        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Proveedores para el  propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();
        List<Proveedor> proveedores = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            proveedores = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (proveedores.isEmpty()) {

            listaProveedores.clear();
            listaProveedores.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaProveedores;

        }

        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor prov = (Proveedor) proveedores.get(i);
            listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaProveedores;
    }

    public void actualizarEdoGNC(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<GastoNoComun> iter = listaGNC.iterator();
                iter.hasNext();) {
            GastoNoComun fgnc = (GastoNoComun) iter.next();
            if (fgnc.getId().equals(fCxP.getId())) {
                if ((fgnc.getSaldoFacGnc() - pagoFac.getMtoPagadoCxP()) < ceroLogico) {
                    fgnc.setSaldoGastoNoComun(cero);
                    fgnc.setEdoGastoNoComun(edo);
                } else {
                    fgnc.setSaldoGastoNoComun(fgnc.getSaldoGastoNoComun() - pagoFac.getMtoPagadoCxP());
                    fgnc.setEdoGastoNoComun("EPRO");
                }
                if (fgnc.getEdoGralFac().equals("INAC")) {
                    fgnc.setEdoGralFac("PEND");
                }
                DaoQuery<GastoNoComun> dataAcces2 = new DaoQuery<GastoNoComun>();
                try {
                    dataAcces2.almacenarOModificarEntidad(fgnc);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }

                // ******************** Actualizar el edo de las facturas asociadas al GNC *****************

                Integer idGNC = fgnc.getId();
                String nombQuery = "buscarFacGastoNoComunPorIdGNC";
                DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
                try {
                    listaFacGNC = dataAcces.buscarListaEntidadesPorId(idGNC, nombQuery);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }

                if (listaFacGNC.isEmpty()) {
                    break;
                }

                for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                        it.hasNext();) {

                    FacGastoNoComun fGNC = (FacGastoNoComun) it.next();
                    if (fGNC.getEdoFacGastoNoComun().equals("INAC")) {
                        fGNC.setEdoFacGastoNoComun("PEND");
                        DaoQuery<FacGastoNoComun> dataAccesGNC = new DaoQuery<FacGastoNoComun>();
                        try {
                            dataAccesGNC.almacenarOModificarEntidad(fGNC);
                        } catch (HibernateException he) {
                            FacesContext.getCurrentInstance().addMessage(null,
                                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                            return;
                        }

                    }
                }
                // ******************** FIN Actualizar el edo de las facturas asociadas al GNC *****************

                break;
            }
        }
    }

    public void actualizarEdoDeudaPend(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<DeudaPendCxP> iter1 = listaDeudaPend.iterator();
                iter1.hasNext();) {
            DeudaPendCxP dp = (DeudaPendCxP) iter1.next();
            if (dp.getId().equals(fCxP.getId())) {
                if ((dp.getSaldoDeudaPendCxP() - pagoFac.getMtoPagadoCxP()) < ceroLogico) {
                    dp.setSaldoDeudaPendCxP(cero);
                    dp.setEstatusDeudaPendCxP(edo);
                } else {
                    dp.setSaldoDeudaPendCxP(dp.getSaldoDeudaPendCxP() - pagoFac.getMtoPagadoCxP());
                    dp.setEstatusDeudaPendCxP("EPRO");
                }
                DaoQuery<DeudaPendCxP> dataAcces = new DaoQuery<DeudaPendCxP>();
                try {
                    dataAcces.almacenarOModificarEntidad(dp);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
                break;
            }
        }
    }

    public void actualizarEdoNotaCredDeb(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<NotaCredDebCxP> iter1 = listaNotaCredDeb.iterator();
                iter1.hasNext();) {
            NotaCredDebCxP notaCredDeb = (NotaCredDebCxP) iter1.next();

            if (notaCredDeb.getId().equals(fCxP.getId())) {
                if ((notaCredDeb.getSaldoNotaCredDebCxP() - pagoFac.getMtoPagadoCxP()) < ceroLogico) {
                    notaCredDeb.setSaldoNotaCredDebCxP(cero);
                    notaCredDeb.setEdoNotaCredDebCxP(edo);
                } else {
                    notaCredDeb.setSaldoNotaCredDebCxP(notaCredDeb.getSaldoNotaCredDebCxP() - pagoFac.getMtoPagadoCxP());
                    notaCredDeb.setEdoNotaCredDebCxP("EPRO");
                }
                DaoQuery<NotaCredDebCxP> dataAcces = new DaoQuery<NotaCredDebCxP>();
                try {
                    dataAcces.almacenarOModificarEntidad(notaCredDeb);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
                break;
            }
        }
    }

    public void actualizarEdoGCOrd(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<GastoComunOrd> iter = listaGCOrd.iterator();
                iter.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) iter.next();
            if (gco.getId().equals(fCxP.getId())) {
                if ((gco.getSaldoGastoComunOrd() - pagoFac.getMtoPagadoCxP()) < ceroLogico) {
                    gco.setSaldoGastoComunOrd(cero);
                    gco.setEdoGastoComunOrd(edo);
                } else {
                    gco.setSaldoGastoComunOrd(gco.getSaldoGastoComunOrd() - pagoFac.getMtoPagadoCxP());
                    gco.setEdoGastoComunOrd("EPRO");
                }

                DaoQuery<GastoComunOrd> dataAcces2 = new DaoQuery<GastoComunOrd>();
                try {
                    dataAcces2.almacenarOModificarEntidad(gco);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }

                break;
            }
        }
    }

    public void actualizarEdoGCExt(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException, ParseException {

        Integer idPago = pagoFac.getPagoCxP().getId();
        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<GastoComunExt> iter = listaGCExt.iterator();
                iter.hasNext();) {
            GastoComunExt gce = (GastoComunExt) iter.next();
            if (gce.getId().equals(fCxP.getId())) {
                if (gce.getSaldoGastoComunExt() - pagoFac.getMtoPagadoCxP() < ceroLogico) {
                    gce.setSaldoGastoComunExt(cero);
                    gce.setEdoGastoComunExt(edo);
                } else {
                    gce.setSaldoGastoComunExt(gce.getSaldoGastoComunExt() - pagoFac.getMtoPagadoCxP());
                    gce.setEdoGastoComunExt("EPRO");
                }

                DaoQuery<GastoComunExt> dataAcces2 = new DaoQuery<GastoComunExt>();
                try {
                    dataAcces2.almacenarOModificarEntidad(gce);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }


                //************************* ACTUALIZACION DEL MONTO DEL FONDO RELACIONADO ******************************

                Integer idFond = gce.getIdFondo();

                Fondo fondo = null;
                String nombQuery = "buscarFondoPorId";
                DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
                try {
                    fondo = dataAcces.buscarEntidadPorId(idFond, nombQuery);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
                if (fondo != null) {
                    fondo.setSaldoFondo(fondo.getSaldoFondo() - pagoFac.getMtoPagadoCxP());
                    DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
                    try {
                        dataAcces3.almacenarOModificarEntidad(fondo);
                    } catch (HibernateException he) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                        return;
                    }
                }

                registrarTranFondo(gce, fondo, idPago, pagoFac.getPagoCxP());
                break;
            }
        }


    }

    public Double calcularMtoTotalFacSelecciondas() throws ValidatorException {

        Double totalFac = new Double(0);
        for (int i = 0; i < facSeleccionadas.length; i++) {
            FacturaCxPDT fCxP = (FacturaCxPDT) facSeleccionadas[i];
            Double mtoFac = Double.valueOf(fCxP.getSaldoFacCxP()).doubleValue();
            totalFac = totalFac + mtoFac;
        }
        return totalFac;
    }

    public void almacenarPagoFacCxP(PagoFacCxP pagoFac) throws ValidatorException {

        DaoQuery<PagoFacCxP> dataAcces = new DaoQuery<PagoFacCxP>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void actualizarEdosYSaldos(FacturaCxPDT fCxP, PagoFacCxP pagoFac) throws ValidatorException, ParseException {

        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la factura del gasto no comun,
        // asi como tambien, el estado del gasto no comun asociado.
        //-------------------------------------------------------------------
        if (fCxP.getDiscFacCxP().equals("Gasto no Comun")) {
            actualizarEdoGNC(fCxP, pagoFac);
        }
        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la deuda pendiente
        //-------------------------------------------------------------------
        if (fCxP.getDiscFacCxP().equals("Deuda pendiente")) {
            actualizarEdoDeudaPend(fCxP, pagoFac);
        }
        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la Nota de crédito
        //-------------------------------------------------------------------
        if (fCxP.getDiscFacCxP().equals("Nota de Credito")) {
            actualizarEdoNotaCredDeb(fCxP, pagoFac);
        }
        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado del Gasto Comun Ord
        //-------------------------------------------------------------------
        if (fCxP.getDiscFacCxP().equals("Gasto Comun Ord")) {
            actualizarEdoGCOrd(fCxP, pagoFac);
        }
        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado del Gasto Comun Ext
        //-------------------------------------------------------------------
        if (fCxP.getDiscFacCxP().equals("Gasto Comun Ext")) {
            actualizarEdoGCExt(fCxP, pagoFac);
        }
    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoQuery<Parametro> dataAcces = new DaoQuery<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void generarPagoFacCxP(PagoCxP pagoCxP) throws ValidatorException, ParseException {

        Double mtoTotalFacSelec = calcularMtoTotalFacSelecciondas();
        PagoCxP = pagoCxP.getMtoPagoCxP();
        saldoPagoCxP = pagoCxP.getMtoPagoCxP();
        Double cero = new Double(0);
        PagoFacCxP pagoFac = null;

        for (int i = 0; i < facSeleccionadas.length; i++) {
            FacturaCxPDT fCxP = (FacturaCxPDT) facSeleccionadas[i];
            pagoFac = new PagoFacCxP();
            pagoFac.setPagoCxP(pagoCxP);
            //-------------------------------------------------------------------
            // Se asigna un discriminador a cada PagoFacCxP para diferenciarlos        
            //-------------------------------------------------------------------
            if (fCxP.getDiscFacCxP().equals("Gasto no Comun")) {
                pagoFac.setDiscFacCxP("GNC");
            }
            if (fCxP.getDiscFacCxP().equals("Deuda pendiente")) {
                pagoFac.setDiscFacCxP("DPE");
            }
            if (fCxP.getDiscFacCxP().equals("Nota de Credito")) {
                pagoFac.setDiscFacCxP("NCD");
            }
            if (fCxP.getDiscFacCxP().equals("Gasto Comun Ord")) {
                pagoFac.setDiscFacCxP("GCO");
            }
            if (fCxP.getDiscFacCxP().equals("Gasto Comun Ext")) {
                pagoFac.setDiscFacCxP("GCE");
            }
            //-------------------------------------------------------------------

            pagoFac.setIdFacCxP(fCxP.getId());
            Double mtoCxP = Double.valueOf(fCxP.getSaldoFacCxP()).doubleValue();

            if (PagoCxP.equals(mtoTotalFacSelec)) {
                pagoFac.setMtoPagadoCxP(mtoCxP);
                almacenarPagoFacCxP(pagoFac);
                actualizarEdosYSaldos(fCxP, pagoFac);
            } else {
                //-------------------------------------------------
                // Para los casos en que el saldo del pago 
                // no alcanza para pagar las facturas seleccionadas.
                // Evita pagar deudas con saldo menor que 0.01
                // -------------------------------------------------
                if ((saldoPagoCxP - mtoCxP) > 0) {
                    pagoFac.setMtoPagadoCxP(mtoCxP);
                    saldoPagoCxP = saldoPagoCxP - mtoCxP;
                } else {
                    if (saldoPagoCxP > ceroLogico) {
                        pagoFac.setMtoPagadoCxP(saldoPagoCxP);
                        saldoPagoCxP = cero;
                    } else {
                        break;
                    }
                }
                almacenarPagoFacCxP(pagoFac);
                actualizarEdosYSaldos(fCxP, pagoFac);
            }
        }
    }

    public void registrarPagoCxP() throws ValidatorException, ParseException {

        String vacio = new String();

        String mensaje = validarPanel();
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        //----------------------------------------------------------------
        // Verifica si ya existe en la bd, el nro de documento ingresado 
        //----------------------------------------------------------------

        Boolean existe = buscarDocumento(this.getNumDocPagoCxP());
        if (existe) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ya existe un gasto con el nro. documemto ingresado, por favor, intente de nuevo..."));
            return;
        }
        PagoCxP pagoCxPNuevo = new PagoCxP();

        pagoCxPNuevo.setDesPagoCxP(desPagoCxP);

        if (this.getFormaPagoCxP().equals("Efectivo")) {
            pagoCxPNuevo.setNumDocPagoCxP("N/A");
        } else {
            pagoCxPNuevo.setNumDocPagoCxP(numDocPagoCxP);
        }


        Calendar periodo = beanUsuario.getPeriodoActual();
        pagoCxPNuevo.setPeriodoActual(periodo);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoPagoCxP).doubleValue();
        //-----------------------------------------------------------
        pagoCxPNuevo.setMtoPagoCxP(mto);
        pagoCxPNuevo.setFormaPagoCxP(formaPagoCxP);
        pagoCxPNuevo.setFecDocPagoCxP(fecDocPagoCxP);
        pagoCxPNuevo.setBancoPagoCxP(Integer.parseInt(idBcoPagoCxP));

        pagoCxPNuevo.setIdCuentaPagoCxP(Integer.valueOf(idCuentaPagoCxP));
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxPNuevo.setFecMovPagoCxP(fecha);
        //-----------------------------------------------------------
        //         Se busca el proveedor para asociarle el pago
        //-----------------------------------------------------------
        Integer idProv = Integer.valueOf(idProveedor);

        Proveedor proveedor = null;//Para buscar el objeto proveedor usa el DaoCrud
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(idProv);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-----------------------------------------------------------
        pagoCxPNuevo.setProveedor(proveedor);

        DaoQuery<PagoCxP> dataAcces = new DaoQuery<PagoCxP>();
        Integer idPagoCxPNueva;
        try {
            idPagoCxPNueva = dataAcces.almacenarEntidad(pagoCxPNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxPPorId";
        PagoCxP pagoCxP;
        try {
            pagoCxP = dataAcces.buscarEntidadPorId(idPagoCxPNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxP y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        generarPagoFacCxP(pagoCxP);

        idPagoCxP = pagoCxP.id; // Se conserva el id del pago para incluirlo al crear el mov bancario  
        registrarMovBancario();

        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table   

        inicializarPanel();
        for (int i = 0; i < facSeleccionadas.length; i++) {
            facSeleccionadas[i] = null;
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago correspondiente al documento Nro. " + pagoCxP.getNumDocPagoCxP() + " ha sido registrado satisfactoriamente!"));

    }

    public void registrarMovBancario() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idBcoPagoCxP);
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCuentaPagoCxP);
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoCxP);

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDocPagoCxP);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(numDocPagoCxP);
        Double mto = Double.valueOf(mtoPagoCxP).doubleValue();
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario(desPagoCxP);
        MovBancarioNuevo.setIdPago(idPagoCxP);


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

    public void registrarTranFondo(GastoComunExt fce, Fondo fondo, Integer idPago, PagoCxP pcxp) throws ParseException {

        TranFondo tranFondoNuevo = new TranFondo();


        tranFondoNuevo.setIdFondoTranFondo(fondo.getId());

        Calendar fecha = new GregorianCalendar();
        tranFondoNuevo.setFecTranFondo(pcxp.getFecDocPagoCxP());
        tranFondoNuevo.setFecMovTranFondo(fecha);

        tranFondoNuevo.setDocTranFondo(fce.getNumDocGastoComunExt());
        tranFondoNuevo.setIdConceptoTranFondo(fce.getIdConcepto());
        tranFondoNuevo.setIdBancoTranFondo(fondo.getIdBcoFondo());
        tranFondoNuevo.setFormaPagoTranFondo(formaPagoCxP);

        Double mto = Double.valueOf(mtoPagoCxP).doubleValue();
        tranFondoNuevo.setMtoTranFondo(mto);
        tranFondoNuevo.setSaldoTranFondo(mto);
        tranFondoNuevo.setTipoOperTranFondo("Egreso");
        tranFondoNuevo.setDescTranFondo(fce.getObsGastoComunExt());
        tranFondoNuevo.setEstadoTranFondo("CANC");
        tranFondoNuevo.setPerTranFondo(fce.getPerGastoComunExt());
        tranFondoNuevo.setIdPago(idPago);

        tranFondoNuevo.setFondo(fondo);

        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();

        try {
            dataAcces.almacenarOModificarEntidad(tranFondoNuevo);
            //idTranFondo = dataAcces.almacenarEntidad(tranFondoNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde (TFondo)"));
            return;
        }


    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setDesPagoCxP(String desPagoCxP) {
        this.desPagoCxP = desPagoCxP;
    }

    public String getDesPagoCxP() {
        return desPagoCxP;
    }

    public void setNumDocPagoCxP(String numDocPagoCxP) {
        this.numDocPagoCxP = numDocPagoCxP;
    }

    public String getNumDocPagoCxP() {
        return numDocPagoCxP;
    }

    public String getMtoPagoCxP() {
        return mtoPagoCxP;
    }

    public void setMtoPagoCxP(String mtoPagoCxP) {
        this.mtoPagoCxP = mtoPagoCxP;
    }

    public String getFormaPagoCxP() {
        return formaPagoCxP;
    }

    public void setFormaPagoCxP(String formaPagoCxP) {
        this.formaPagoCxP = formaPagoCxP;
    }

    public String getFecDocPagoCxP() {
        return fecDocPagoCxP;
    }

    public void setFecDocPagoCxP(String fecDocPagoCxP) {
        this.fecDocPagoCxP = fecDocPagoCxP;
    }

    public void setIdBcoPagoCxP(String idBcoPagoCxP) {
        this.idBcoPagoCxP = idBcoPagoCxP;
    }

    public String getIdBcoPagoCxP() {
        return idBcoPagoCxP;
    }

    public void setIdCuentaPagoCxP(String idCuentaPagoCxP) {
        this.idCuentaPagoCxP = idCuentaPagoCxP;
    }

    public String getIdCuentaPagoCxP() {
        return idCuentaPagoCxP;
    }

    public FacturaCxPDT[] getFacSeleccionadas() {
        return facSeleccionadas;
    }

    public void setFacSeleccionadas(FacturaCxPDT[] facSelecciondas) {
        this.facSeleccionadas = facSelecciondas;
    }
}
