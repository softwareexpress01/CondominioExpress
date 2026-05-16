package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import movimientos.modelos.GastoComunDT;
import movimientos.modelos.GastoComunExt;

public class GastoComunExtCtrl extends DaoCrud<GastoComunExt, Condominio, Integer> implements Serializable {

    private String fecGastoComunExt;
    private String numDocGastoComunExt;
    private HtmlSelectOneMenu idProveedor;
    private HtmlSelectOneMenu idConcepto;
    private HtmlSelectOneMenu idFondo;
    private HtmlInputText perGastoComunExt;
    public String mtoGastoComunExt;
    public String obsGastoComunExt;
    public String numDocSel;
    private Collection<GastoComunExt> listaGCExt = new ArrayList<GastoComunExt>();
    private Collection<GastoComunDT> listaGastoComunDT = new ArrayList<GastoComunDT>();
    private List listaProveedores = new ArrayList();
    private List listaConceptos = new ArrayList();
    private List listaFondos = new ArrayList();
    private GastoComunDT filaSeleccionada;
    private GastoComunExt gcev;

    public GastoComunExtCtrl() {
    }

    public void inicializarPanel() {

        this.setFecGastoComunExt(null);
        this.setNumDocGastoComunExt(null);
        Integer cero = new Integer(0);
        this.idProveedor.setValue(cero);
        this.idConcepto.setValue(cero);
        this.idFondo.setValue(cero);
        this.setMtoGastoComunExt(null);
        this.setObsGastoComunExt(null);
        this.perGastoComunExt.setValue(null);

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

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //---------------------------------------------------------------------------------------------
        // Verifica si selecciono algun Proveedor, Concepto y Fondo (diferente de la opcion Seleccione)
        //---------------------------------------------------------------------------------------------
        String provSel = idProveedor.getValue().toString();
        String cero = "0";

        if (provSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el proveedor ...";
            return mensaje;
        }

        String concSel = idConcepto.getValue().toString();
        if (concSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el concepto ...";
            return mensaje;
        }

        String fondoSel = idFondo.getValue().toString();
        if (fondoSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el fondo ...";
            return mensaje;
        }
        //----------------------------------------------------------------------------
        if (this.getNumDocGastoComunExt().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del documento/factura ...";
            return mensaje;
        }

        if (!val.esLong(this.getNumDocGastoComunExt())) {
            mensaje = "El número del documento debe ser numérico, por favor intente de nuevo...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.getFecGastoComunExt().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento/factura...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecGastoComunExt())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que haya ingresado el periodo
        //--------------------------------------------------------------------
        String perGasto = perGastoComunExt.getValue().toString();

        if (perGasto.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el período al cual va a asignar el gasto ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que el formato del periodo sea el adecuado
        //--------------------------------------------------------------------

        if (!val.esFormatoPeriodo(this.perGastoComunExt.getValue().toString())) {
            mensaje = "El formato del período debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que el formato este dentro del rango adecuado o valido
        //--------------------------------------------------------------------

        if (!val.esRangoValido(this.perGastoComunExt.getValue().toString())) {
            mensaje = "El período ingresado esta fuera del rango permitido, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getMtoGastoComunExt().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoGastoComunExt())) {
            mensaje = "El monto debe ser un número entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public GastoComunExt buscarDocumento(String doc) {

        String nombQuery = "buscarGCEPorNumDoc";
        DaoQuery<GastoComunExt> dataAcces = new DaoQuery<GastoComunExt>();
        try {
            gcev = dataAcces.buscarEntidad(doc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return gcev;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Collection getGastosComunesExtPorCondominio() throws ParseException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaGastoComunDT;
        }
        listaGastoComunDT.clear();
        listaGCExt.clear();
        // Obtiene los gastos comunes ordinarios para el condominio dado (id), los muestra en el panel 2
        // Obtiene el id del condominio del beanUsuario
        Integer idCond = beanUsuario.getIdCondActual();


        String nombQuery = "buscarGastosComunesExtPendientes";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCExt = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        // se inicializa el objeto de gastos comunes ordinarios para el DataTable...
        listaGastoComunDT = inicializarGastoComunDT(listaGCExt);

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaGastoComunDT;
    }

    private Collection<GastoComunDT> inicializarGastoComunDT(Collection listaOrd) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Calendar periodo = beanUsuario.getPeriodoActual();

        for (Iterator<GastoComunExt> it = listaOrd.iterator();
                it.hasNext();) {

            GastoComunExt gcoext = (GastoComunExt) it.next();
            if (gcoext == null) {// Si no hay gastos comunes extraordinarios devuelve la lista en blanco
                break;
            } else {
                Integer idProv = gcoext.getIdProveedor();
                String nombProv = buscarNombProveedor(idProv);
                Integer idConc = gcoext.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);
                Integer idFon = gcoext.getIdFondo();
                String nombFondo = buscarNombFondo(idFon);

                GastoComunDT gastoComunDT = new GastoComunDT();

                gastoComunDT.setId(gcoext.getId());
                gastoComunDT.setIdProveedor(idProv);
                gastoComunDT.setIdFondo(idFon);
                gastoComunDT.setIdConcepto(idConc);
                gastoComunDT.setNombProveedor(nombProv);
                gastoComunDT.setNombConcepto(nombConc);
                gastoComunDT.setNombFondo(nombFondo);
                gastoComunDT.setFecGastoComun(gcoext.getFecGastoComunExt());
                gastoComunDT.setNumDocGastoComun(gcoext.getNumDocGastoComunExt());
                gastoComunDT.setMtoGastoComun(gcoext.getMtoGastoComunExt());
                gastoComunDT.setObsGastoComun(gcoext.getObsGastoComunExt());
                gastoComunDT.setEdoGastoComun(gcoext.getEdoGastoComunExt());
                gastoComunDT.setSaldoGastoComun(gcoext.getSaldoGastoComunExt());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                gastoComunDT.setMtoGastoComunString(val.convertirDoubleAString(gcoext.getMtoGastoComunExt()));
                gastoComunDT.setSaldoGastoComunString(val.convertirDoubleAString(gcoext.getSaldoGastoComunExt()));

                //----------------------------------------------------------------
                //Se da formato al periodo para mostrarlo en el data table (mm-aaaa)
                //----------------------------------------------------------------
                String mesp = "";
                Calendar per = gcoext.getPerGastoComunExt();
                int mesPer = per.get(Calendar.MONTH) + 1;
                int añoPer = per.get(Calendar.YEAR);

                if (mesPer < 10) {
                    mesp = "0" + mesPer;
                } else {
                    mesp = String.valueOf(mesPer);
                }

                String stringPer = mesp + "-" + añoPer;

                gastoComunDT.setPerGastoComun(stringPer);
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = gcoext.getFecMovGCExt();
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

                gastoComunDT.setFecMovGC(stringFechaMov);
                Calendar perGasto = gcoext.getPerGastoComunExt();
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimintos con periodo mayor que el periodo actual
                //----------------------------------------------------------------------------------
                if (perGasto.equals(periodo) | perGasto.after(periodo)) {
                    listaGastoComunDT.add(gastoComunDT);
                }
            }
        }
        return listaGastoComunDT;
    }

    public String buscarNombProveedor(Integer idProv) {

        Proveedor proveedor;
        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            proveedor = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreProv = proveedor.getNombProveedor();
        return nombreProv;
    }

    public String buscarNombConcepto(Integer idConc) {

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
        String nombreConcepto = concepto.getNombConcepto();
        return nombreConcepto;
    }

    public String buscarNombFondo(Integer idFondo) {
        Fondo fondo;
        String nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces.buscarEntidadPorId(idFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreFondo = fondo.getDescFondo();
        return nombreFondo;
    }

    public List getProveedoresPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaProveedores);
        }

        listaProveedores.clear();


        //-------------------------------------------------------------------------------------
        // Genera la lista de Proveedores para el condominio dado, la muestra en el selecOneMenu
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
        if (proveedores.isEmpty()) {// Si no hay proveedor devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaProveedores.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaProveedores;
        }
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor prov = (Proveedor) proveedores.get(i);
            listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        return (List) listaProveedores;
    }

    public List getConceptosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaConceptos);
        }
        //-------------------------------------------------------------------------------------
        // Genera la lista de Conceptos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------
        listaConceptos.clear();

        Integer idCond = usuarioActual.getIdCondActual();
        List<Concepto> conceptos = new ArrayList<Concepto>();
        String nombQuery = "buscarConceptosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            conceptos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (conceptos.isEmpty()) {// Si no hay concepto devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaConceptos.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaConceptos;
        }

        listaConceptos.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < conceptos.size(); i++) {
            Concepto conc = (Concepto) conceptos.get(i);
            listaConceptos.add(new SelectItem(conc.getId(), conc.getNombConcepto()));
        }

        return (List) listaConceptos;
    }

    public List getFondosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaFondos);
        }

        listaFondos.clear();

        //-------------------------------------------------------------------------------------
        // Genera la lista de Fondos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------
        Integer idCond = usuarioActual.getIdCondActual();
        List<Fondo> fondos = new ArrayList<Fondo>();
        String nombQuery = "buscarFondosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            fondos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (fondos.isEmpty()) {// Si no hay fondos devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaFondos.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaFondos;
        }
        listaFondos.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < fondos.size(); i++) {
            Fondo fondo = (Fondo) fondos.get(i);
            listaFondos.add(new SelectItem(fondo.getId(), fondo.getDescFondo()));
        }
        return (List) listaFondos;
    }

    public void editarGastoComunExtAction() {


        GastoComunDT gcodtSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("GastoComunDT", gcodtSel);

        this.setFecGastoComunExt(gcodtSel.getFecGastoComun());
        this.setNumDocGastoComunExt(gcodtSel.getNumDocGastoComun());

        this.setNumDocSel(gcodtSel.getNumDocGastoComun());//Si es modificado, se valida que no exista en la bd

        this.idProveedor.setValue(gcodtSel.getIdProveedor());
        this.idConcepto.setValue(gcodtSel.getIdConcepto());
        this.idFondo.setValue(gcodtSel.getIdFondo());
        this.setObsGastoComunExt(gcodtSel.getObsGastoComun());
        this.perGastoComunExt.setValue(gcodtSel.getPerGastoComun());

        this.setMtoGastoComunExt(gcodtSel.getMtoGastoComunString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(gcodtSel.getNumDocGastoComun());

        String idGCOrd = gcodtSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoComunExt gcoextra = null;
        for (Iterator<GastoComunExt> it = listaGCExt.iterator();
                it.hasNext();) {
            gcoextra = (GastoComunExt) it.next();
            if (gcoextra.getId().toString().equals(idGCOrd)) {
                break;
            }
        }

        //Almacena la fila del concepto seleccionado ,el cual se buscara como "GastoComunExt" en el map #{GastoComunExt} 
        ctx.getExternalContext().getSessionMap().put("GastoComunExt", gcoextra);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoCrud
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void registrarGastoComunExt() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        //----------------------------------------------------------------
        // Verifica si ya existe en la bd, el nro de documento ingresado 
        //----------------------------------------------------------------

        gcev = null;
        gcev = buscarDocumento(this.getNumDocGastoComunExt());
        if (!(gcev == null)) {
            if (gcev.getIdProveedor().equals(Integer.parseInt(idProveedor.getValue().toString()))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                return;
            }
        }
        GastoComunExt gastoComunExtNuevo = new GastoComunExt();

        gastoComunExtNuevo.setFecGastoComunExt(fecGastoComunExt);
        gastoComunExtNuevo.setNumDocGastoComunExt(numDocGastoComunExt);
        gastoComunExtNuevo.setObsGastoComunExt(obsGastoComunExt);
        gastoComunExtNuevo.setEdoGastoComunExt("PEND");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        gastoComunExtNuevo.setFecMovGCExt(fecha);
        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        String idFondoString = idFondo.getValue().toString();
        Integer idFondoInteger = Integer.parseInt(idFondoString);
        //-----------------------------------------------------------
        gastoComunExtNuevo.setIdProveedor(idProvInteger);
        gastoComunExtNuevo.setIdConcepto(idConcInteger);
        gastoComunExtNuevo.setIdFondo(idFondoInteger);
        //-------------------------------------------------------------------
        // Convertir de HtmlInputText (periodo) a String, despues a Calendar
        //-------------------------------------------------------------------
        String stringPer = perGastoComunExt.getValue().toString();

        String stringPeriodo = "01-" + stringPer;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gastoComunExtNuevo.setPerGastoComunExt(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoComunExt).doubleValue();
        gastoComunExtNuevo.setMtoGastoComunExt(mto);
        gastoComunExtNuevo.setSaldoGastoComunExt(mto);
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoCrud
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        gastoComunExtNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(gastoComunExtNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto comun extraordinario correspondiente al documento Nro. " + gastoComunExtNuevo.getNumDocGastoComunExt() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarGastoComunExt() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        if (!numDocSel.equals(numDocGastoComunExt)) {
            //----------------------------------------------------------------
            // Verifica si ya existe en la bd, el nro de documento ingresado 
            //----------------------------------------------------------------
            gcev = null;
            gcev = buscarDocumento(this.getNumDocGastoComunExt());
            if (!(gcev == null)) {
                if (gcev.getIdProveedor().equals(Integer.parseInt(idProveedor.getValue().toString()))) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                    return;
                }
            }
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoComunDT gcodtSel = (GastoComunDT) ctx.getExternalContext().getSessionMap().get("GastoComunDT");
        String idGCExt = gcodtSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoComunExt gcoextra = null;
        for (Iterator<GastoComunExt> it = listaGCExt.iterator();
                it.hasNext();) {
            gcoextra = (GastoComunExt) it.next();
            if (gcoextra.getId().toString().equals(idGCExt)) {
                break;
            }
        }
        gcoextra.setFecGastoComunExt(fecGastoComunExt);
        gcoextra.setNumDocGastoComunExt(numDocGastoComunExt);
        gcoextra.setObsGastoComunExt(obsGastoComunExt);
        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        String idFondoString = idFondo.getValue().toString();
        Integer idFondoInteger = Integer.parseInt(idFondoString);
        //-----------------------------------------------------------
        gcoextra.setIdProveedor(idProvInteger);
        gcoextra.setIdConcepto(idConcInteger);
        gcoextra.setIdFondo(idFondoInteger);
        //-------------------------------------------------------------------
        // Convertir de HtmlInputText (periodo) a String, despues a Calendar
        //-------------------------------------------------------------------
        String stringPer = perGastoComunExt.getValue().toString();

        String stringPeriodo = "01-" + stringPer;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gcoextra.setPerGastoComunExt(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoComunExt).doubleValue();
        gcoextra.setMtoGastoComunExt(mto);
        gcoextra.setSaldoGastoComunExt(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(gcoextra);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto comun extraordinario correspondiente al documento " + gcoextra.getNumDocGastoComunExt() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setFecGastoComunExt(String fecGastoComunExt) {
        this.fecGastoComunExt = fecGastoComunExt;
    }

    public String getFecGastoComunExt() {
        return fecGastoComunExt;
    }

    public void setNumDocGastoComunExt(String numDocGastoComunExt) {
        this.numDocGastoComunExt = numDocGastoComunExt;
    }

    public String getNumDocGastoComunExt() {
        return numDocGastoComunExt;
    }

    public void setMtoGastoComunExt(String mtoGastoComunExt) {
        this.mtoGastoComunExt = mtoGastoComunExt;
    }

    public String getMtoGastoComunExt() {
        return mtoGastoComunExt;
    }

    public String getObsGastoComunExt() {
        return obsGastoComunExt;
    }

    public void setObsGastoComunExt(String obsGastoComunExt) {
        this.obsGastoComunExt = obsGastoComunExt;
    }

    public void setIdProveedor(HtmlSelectOneMenu idProveedor) {
        this.idProveedor = idProveedor;
    }

    public HtmlSelectOneMenu getIdProveedor() {
        return idProveedor;
    }

    public void setIdConcepto(HtmlSelectOneMenu idConcepto) {
        this.idConcepto = idConcepto;
    }

    public HtmlSelectOneMenu getIdConcepto() {
        return idConcepto;
    }

    public void setIdFondo(HtmlSelectOneMenu idFondo) {
        this.idFondo = idFondo;
    }

    public HtmlSelectOneMenu getIdFondo() {
        return idFondo;
    }

    public void setNumDocSel(String numDocSel) {
        this.numDocSel = numDocSel;
    }

    public String getNumDocSel() {
        return numDocSel;
    }

    public void setPerGastoComunExt(HtmlInputText perGastoComunExt) {
        this.perGastoComunExt = perGastoComunExt;
    }

    public HtmlInputText getPerGastoComunExt() {
        return perGastoComunExt;
    }

    // .....
    public void setFilaSeleccionada(GastoComunDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public GastoComunDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
