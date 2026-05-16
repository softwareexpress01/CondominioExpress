package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;

import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;

import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.GastoNoComunDT;

public class GastoNoComunCtrl extends DaoCrud<GastoNoComun, Condominio, Integer> implements Serializable {

    private String fecGastoNoComun;
    private String idConcepto;
    private String idProveedor;
    private String numDocGastoNoComun;
    private String obsGastoNoComun;
    private String perGastoNoComun;
    private String mtoGastoNoComun;
    private String numDocSel;
    private HtmlSelectOneRadio seleccion;
    private List idInmueble = new ArrayList();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<GastoNoComunDT> listaGastoNoComunDT = new ArrayList<GastoNoComunDT>();
    private List<Inmueble> inmuebles = new ArrayList<Inmueble>();
    private List listaInmuebles = new ArrayList();
    private List listaConceptos = new ArrayList();
    private List listaProveedores = new ArrayList();
    private String mtoGNCOriginal;
    private Collection<Inmueble> listaInmFac = new ArrayList<Inmueble>();
    private GastoNoComunDT filaSeleccionada;
    private GastoNoComun gncv;

    public GastoNoComunCtrl() {
    }

    public void inicializarPanel() {

        this.setFecGastoNoComun(null);
        this.setIdConcepto(null);
        this.setIdProveedor(null);
        this.setMtoGastoNoComun(null);
        this.setObsGastoNoComun(null);
        this.setNumDocGastoNoComun(null);
        this.seleccion.setValue(null);
        this.setPerGastoNoComun(null);

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
        beanUsuario.mostrar = false;
        beanUsuario.deshabilitar = false;
        beanUsuario.deshabRadio = false;
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
        beanUsuario.mostrar = false;
        beanUsuario.deshabilitar = false;
        beanUsuario.deshabRadio = false;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        String cero = "0";
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        //------------------------------------------------------------------------------------------------
        // Verifica si se especifico si se asignara el gasto a todos o algunos de los inmuebles
        //------------------------------------------------------------------------------------------------
        try {
            seleccion.getValue().toString();
        } catch (NullPointerException npe) {
            mensaje = "Por favor, especifique si va a asignar el gasto a todos o algunos de los inmuebles...";
            return (mensaje);
        }

        String varios = "varios";
        if (seleccion.getValue().toString().equals(varios) & idInmueble.isEmpty()) {
            mensaje = "Por favor, seleccione al menos un inmueble ...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------
        // Verifica si selecciono alguna Proveedor (diferente de la opcion Seleccione)
        //-----------------------------------------------------------------------------
        if (idProveedor.toString().equals(cero)) {//selecciono la que aparece como "Seleccione"
            mensaje = "Por favor, seleccione el proveedor ...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------
        if (this.getNumDocGastoNoComun().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de factura/documento ...";
            return mensaje;
        }
        if (!val.esLong(this.getNumDocGastoNoComun())) {
            mensaje = "El número del documento debe ser numérico, por favor intente de nuevo...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.getFecGastoNoComun().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecGastoNoComun())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------
        // Verifica si selecciono alguna Concepto (diferente de la opcion Seleccione)
        //-----------------------------------------------------------------------------
        if (idConcepto.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el concepto ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que haya ingresado el periodo
        //--------------------------------------------------------------------

        if (perGastoNoComun.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el período al cual va a asignar el gasto ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que el formato del periodo sea el adecuado
        //--------------------------------------------------------------------

        if (!val.esFormatoPeriodo(this.perGastoNoComun)) {
            mensaje = "El formato del período debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que el formato este dentro del rango adecuado o valido
        //--------------------------------------------------------------------

        if (!val.esRangoValido(this.perGastoNoComun)) {
            mensaje = "El período ingresado esta fuera del rango permitido, por favor intente de nuevo...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------
        if (this.getMtoGastoNoComun().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoGastoNoComun())) {
            mensaje = "El monto debe ser un número entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public GastoNoComun buscarDocumento(String doc) {

        String nombQuery = "buscarGNCPorNumDoc";
        DaoQuery<GastoNoComun> dataAcces = new DaoQuery<GastoNoComun>();
        try {
            gncv = dataAcces.buscarEntidad(doc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return gncv;
    }

    public Collection getGastosNoComunesPorCondominio() {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        //   if (idInmueble == null) {
        //     idInmueble = "0";
        //   }

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaGastoNoComunDT;
        }

        listaGastoNoComunDT.clear();
        listaGNC.clear();
        // Obtiene los gastos no comunes para el inmueble dado (id), los muestra en el panel 2
        Integer idCond = usuarioActual.getIdCondActual();
        Calendar periodo = usuarioActual.getPeriodoActual();
        String nombQuery = "buscarGastosNoComunesPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGNC = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listaGNC);// para ordenar la lista de notas

        // se inicializa el objeto de notas para el DataTable...
        listaGastoNoComunDT = inicializarGastoNoComunDT(listaOrd);

        usuarioActual.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        usuarioActual.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaGastoNoComunDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<GastoNoComun> listaDesOrdenada = new ArrayList<GastoNoComun>();

        for (Iterator<GastoNoComun> it = lista.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            listaDesOrdenada.add(gnc);
        }

        Collections.sort(listaDesOrdenada, new ComparadorGNC());//devuelve la lista ordenada en listaDesOrdenada

        Collection<GastoNoComun> coleccionOrdenada = new ArrayList<GastoNoComun>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            GastoNoComun gasto = listaDesOrdenada.get(i);
            coleccionOrdenada.add(gasto);
        }
        return coleccionOrdenada;
    }

    private Collection<GastoNoComunDT> inicializarGastoNoComunDT(Collection lista) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Calendar periodo = beanUsuario.getPeriodoActual();

        Collection listaGnc = new ArrayList();
        for (Iterator<GastoNoComun> it = lista.iterator();
                it.hasNext();) {

            GastoNoComun gnc = (GastoNoComun) it.next();
            if (gnc == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {

                Integer idProv = gnc.getIdProveedor();
                String nombProv = buscarNombProveedor(idProv);

                Integer idConc = gnc.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                GastoNoComunDT gastoNoComunDT = new GastoNoComunDT();

                gastoNoComunDT.setId(gnc.getId());
                gastoNoComunDT.setIdProveedor(idProv);
                gastoNoComunDT.setIdConcepto(idConc);

                gastoNoComunDT.setNombProveedor(nombProv);
                gastoNoComunDT.setNombConcepto(nombConc);

                gastoNoComunDT.setFecGastoNoComun(gnc.getFecGastoNoComun());
                gastoNoComunDT.setNumDocGastoNoComun(gnc.getNumDocGastoNoComun());
                gastoNoComunDT.setMtoGastoNoComun(gnc.getMtoGastoNoComun());

                gastoNoComunDT.setEdoGastoNoComun(gnc.getEdoGastoNoComun());
                gastoNoComunDT.setSaldoGastoNoComun(gnc.getSaldoGastoNoComun());
                gastoNoComunDT.setObsGastoNoComun(gnc.getObsGastoNoComun());

                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                gastoNoComunDT.setMtoGastoNoComunString(val.convertirDoubleAString(gnc.getMtoGastoNoComun()));
                gastoNoComunDT.setSaldoGastoNoComunString(val.convertirDoubleAString(gnc.getSaldoGastoNoComun()));
                //----------------------------------------------------------------
                //Se da formato al periodo para mostrarlo en el data table (mm-aaaa)
                //----------------------------------------------------------------
                String mesp = "";
                Calendar per = gnc.getPerGastoNoComun();
                int mesPer = per.get(Calendar.MONTH) + 1;
                int añoPer = per.get(Calendar.YEAR);

                if (mesPer < 10) {
                    mesp = "0" + mesPer;
                } else {
                    mesp = String.valueOf(mesPer);
                }

                String stringPer = mesp + "-" + añoPer;

                gastoNoComunDT.setPerGastoNoComun(stringPer);
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = gnc.getFecMovGNC();
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

                gastoNoComunDT.setFecMovGNC(stringFechaMov);


                Calendar perGasto = gnc.getPerGastoNoComun();
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimintos con periodo mayor que el periodo actual
                //----------------------------------------------------------------------------------
                if (perGasto.equals(periodo) | perGasto.after(periodo)) {
                    listaGnc.add(gastoNoComunDT);
                }
            }
        }

        return listaGnc;
    }

    public String buscarNombProveedor(Integer idProv) {

        Proveedor proveedor = null;
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
        String nombConcepto = concepto.getNombConcepto();
        return nombConcepto;
    }

    public List getInmueblesPorPropiedad() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        listaInmuebles.clear();
        listaInmuebles.add(new SelectItem(new Integer(0), "Seleccione"));
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();

        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (inmuebles.isEmpty()) {// Si no hay inmueble devuelve la lista en blanco
            beanUsuario.agregar = true;
            beanUsuario.mostrar = false;
            beanUsuario.deshabRadio = true;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun inmueble para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }
        return (List) listaInmuebles;
    }

    public List getConceptosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        listaConceptos.clear();

        //-------------------------------------------------------------------------------------
        // Genera la lista de Conceptos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

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

        getInmueblesPorPropiedad();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (conceptos.isEmpty()) {// Si no hay conceptos devuelve la lista en blanco
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

    public List getProveedoresPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaProveedores);
        }

        listaProveedores.clear();

        //-------------------------------------------------------------------------------------
        // Genera la lista de Conceptos para el condominio dado, la muestra en el selecOneMenu
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

    public void mostrarLista(ValueChangeEvent vce) {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        String varios = "varios";
        if (seleccion.getValue().toString().equals(varios)) {
            beanUsuario.mostrar = true;
        } else {
            beanUsuario.mostrar = false;
        }
    }

    public void editarGastosNoComunesAction() {


        GastoNoComunDT gncSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("GastoNoComunDT", gncSel);

        this.setFecGastoNoComun(gncSel.getFecGastoNoComun());
        this.setIdConcepto(gncSel.getIdConcepto().toString());
        this.setIdProveedor(gncSel.getIdProveedor().toString());
        this.setNumDocGastoNoComun(gncSel.getNumDocGastoNoComun());

        this.setNumDocSel(gncSel.getNumDocGastoNoComun());//Si es modificado, se valida que no exista en la bd

        this.setObsGastoNoComun(gncSel.getObsGastoNoComun());
        this.setPerGastoNoComun(gncSel.getPerGastoNoComun());

        this.setMtoGastoNoComun(gncSel.getMtoGastoNoComunString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(gncSel.getNumDocGastoNoComun());

        String idGNC = gncSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos no comunes , listaGNC
        GastoNoComun gnc = null;
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            gnc = (GastoNoComun) it.next();
            if (gnc.getId().toString().equals(idGNC)) {
                //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoNoComun" en el map #{GastoNoComun} 
                ctx.getExternalContext().getSessionMap().put("GastoNoComun", gnc);
                break;
            }
        }
      
        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;
        this.seleccion.setValue(null);
    }

    public void registrarGastoNoComun() throws ValidatorException, ParseException {

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
        gncv = null;
        gncv = buscarDocumento(this.getNumDocGastoNoComun());
        if (!(gncv == null)) {
            if (gncv.getIdProveedor().equals(Integer.parseInt(idProveedor))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                return;
            }
        }
        GastoNoComun gastoNoComunNuevo = new GastoNoComun();

        gastoNoComunNuevo.setFecGastoNoComun(fecGastoNoComun);
        gastoNoComunNuevo.setEdoGastoNoComun("PEND");
        gastoNoComunNuevo.setEdoGralFac("PEND");
        Integer idprov = Integer.valueOf(idProveedor).intValue();
        gastoNoComunNuevo.setIdProveedor(idprov);
        Integer idcon = Integer.valueOf(idConcepto).intValue();
        gastoNoComunNuevo.setIdConcepto(idcon);
        gastoNoComunNuevo.setNumDocGastoNoComun(numDocGastoNoComun);
        gastoNoComunNuevo.setObsGastoNoComun(obsGastoNoComun);

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        gastoNoComunNuevo.setFecMovGNC(fecha);

        String stringPeriodo = "01-" + perGastoNoComun;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gastoNoComunNuevo.setPerGastoNoComun(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoNoComun).doubleValue();
        //-----------------------------------------------------------
        gastoNoComunNuevo.setMtoGastoNoComun(mto);
        gastoNoComunNuevo.setSaldoGastoNoComun(mto);
        gastoNoComunNuevo.setSaldoFacGnc(mto);

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        Integer idCond = usuarioActual.getIdCondActual();
        Condominio condominio = null;//Para buscar el objeto inmueble usa el DaoCrud
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        gastoNoComunNuevo.setCondominio(condominio);
        Integer idGastoNoComunNuevo;

        DaoQuery<GastoNoComun> dataAcces = new DaoQuery<GastoNoComun>();

        try {
            idGastoNoComunNuevo = dataAcces.almacenarEntidad(gastoNoComunNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el gasto recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarGastoNoComunPorId";
        GastoNoComun gasto;
        try {
            gasto = dataAcces.buscarEntidadPorId(idGastoNoComunNuevo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        crearVinculosGncFacInm(gasto);

        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto no comun correspondiente al documento Nro. " + gastoNoComunNuevo.getNumDocGastoNoComun() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void crearVinculosGncFacInm(GastoNoComun gasto) throws ValidatorException, ParseException {
        //-------------------------------------------------------------------------------------
        // Se busca los objetos Inmueble correspondientes a el(los) inmueble(s )seleccionado(s)
        // para establecer el vinculo con cada uno y las facturas a generar...
        //-------------------------------------------------------------------------------------
        String todos = "todos";
        Double mtoFac = new Double(0);
        mtoFac = calcularMtoFactura(gasto);
        if (seleccion.getValue().toString().equals(todos)) {
            Inmueble inm = null;
            for (int i = 0; i < inmuebles.size(); i++) {
                inm = (Inmueble) inmuebles.get(i);
                generarFacturaGNC(gasto, inm, mtoFac);
            }
        } else {
            Inmueble inm = null;
            for (int i = 0; i < idInmueble.size(); i++) {
                String idString = idInmueble.get(i).toString();
                Integer idInteger = Integer.parseInt(idString);
                for (int j = 0; j < inmuebles.size(); j++) {
                    inm = (Inmueble) inmuebles.get(j);
                    if (idInteger.equals(inm.getId())) {
                        generarFacturaGNC(gasto, inm, mtoFac);
                        break;
                    }
                }
            }
        }
    }

    public void deshacerVinculosGncFacInm(GastoNoComun gasto) throws HibernateException {

        listaInmFac.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, para deshacer sus vinculos con
        // las facturaras correspondientes al gasto no comun a modificar
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();

        String nombQuery = "buscarInmPorIdPropFetchFac";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaInmFac = (Collection) dataAcces.buscarListaFetchPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        DaoQuery<Inmueble> dA = new DaoQuery<Inmueble>();

        String idAnt = "";

        for (Iterator<Inmueble> it = listaInmFac.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            if (idAnt.equals(inm.getId().toString())) {
                it.remove();
            }
            idAnt = inm.getId().toString();
        }


        for (Iterator<Inmueble> it = listaInmFac.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            GastoNoComun gnc = null;
            FacGastoNoComun fgnc = null;
            Boolean actualizar = false;

            for (Iterator<FacGastoNoComun> iter = inm.facturasGNC.iterator();
                    iter.hasNext();) {
                fgnc = (FacGastoNoComun) iter.next();
                gnc = fgnc.getGastoNoComun();
                String idgnc = gnc.getId().toString();
                if (gasto.getId().toString().equals(idgnc)) {
                    iter.remove();
                    actualizar = true;
                }
            }

            if (actualizar) {
                try {
                    dA.almacenarOModificarEntidad(inm);
                } catch (HibernateException h) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
            }
        }
        //-------------------------------------------------------------------------------------
        // Se busca el gasto no comun por su id pero con la opcion fetch para lo traiga con 
        // todas sus factura asociadas..
        //-------------------------------------------------------------------------------------
        String nombQry = "buscarGNCPorIdFetchFac";
        GastoNoComun gastoNC = null;
        FacGastoNoComun facGNC = null;
        Integer idGNC = gasto.getId();

        DaoQuery<GastoNoComun> dA2 = new DaoQuery<GastoNoComun>();

        try {
            gastoNC = dA2.buscarEntidadPorId(idGNC, nombQry);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        for (Iterator<FacGastoNoComun> it = gastoNC.facturasGNC.iterator();
                it.hasNext();) {
            facGNC = (FacGastoNoComun) it.next();
            it.remove();
        }
        try {
            dA2.almacenarOModificarEntidad(gastoNC);
        } catch (HibernateException h) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public Double calcularMtoFactura(GastoNoComun gasto) {

        Double monto = new Double(0);
        String todos = "todos";
        if (seleccion.getValue().toString().equals(todos)) {
            monto = gasto.getMtoGastoNoComun() / inmuebles.size();
        } else {
            monto = gasto.getMtoGastoNoComun() / idInmueble.size();
        }
        return monto;
    }

    public void generarFacturaGNC(GastoNoComun gasto, Inmueble inmueble, Double mtoFactura) throws ValidatorException, ParseException {

        FacGastoNoComun facNueva = new FacGastoNoComun();

        facNueva.setMtoFacGastoNoComun(mtoFactura);
        facNueva.setSaldoFacGastoNoComun(mtoFactura);
        facNueva.setEdoFacGastoNoComun("PEND");
        //-----------------------------------------------------------
        //           convierte la fecha del gnc a calendar 
        //-----------------------------------------------------------
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(gasto.getFecGastoNoComun());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        facNueva.setFecFacGNC(cal);

        facNueva.setGastoNoComun(gasto);
        facNueva.setInmueble(inmueble);

        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();

        try {
            dataAcces.almacenarEntidad(facNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarGastoNoComun(GastoNoComun gnc) throws ValidatorException, ParseException {

        gnc.setFecGastoNoComun(fecGastoNoComun);
        Integer idprov = Integer.valueOf(idProveedor).intValue();
        gnc.setIdProveedor(idprov);
        Integer idcon = Integer.valueOf(idConcepto).intValue();
        gnc.setIdConcepto(idcon);
        gnc.setNumDocGastoNoComun(numDocGastoNoComun);
        gnc.setObsGastoNoComun(obsGastoNoComun);


        String stringPeriodo = "01-" + perGastoNoComun;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gnc.setPerGastoNoComun(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoNoComun).doubleValue();
        gnc.setMtoGastoNoComun(mto);
        gnc.setSaldoGastoNoComun(mto);
        gnc.setSaldoFacGnc(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(gnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void modificarGastoNoComun() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        if (!numDocSel.equals(numDocGastoNoComun)) {
            //----------------------------------------------------------------
            // Verifica si ya existe en la bd, el nro de documento ingresado 
            //----------------------------------------------------------------
            gncv = null;
            gncv = buscarDocumento(this.getNumDocGastoNoComun());
            if (!(gncv == null)) {
                if (gncv.getIdProveedor().equals(Integer.parseInt(idProveedor))) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                    return;
                }
            }
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoNoComunDT gncdtSel = (GastoNoComunDT) ctx.getExternalContext().getSessionMap().get("GastoNoComunDT");
        String idGNC = gncdtSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoNoComun gnc = null;
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            gnc = (GastoNoComun) it.next();
            if (gnc.getId().toString().equals(idGNC)) {
                break;
            }
        }
        //------------------------------------------------------------------------------------------
        // 1. Si el gasto no comun tiene estado=PEND, no tiene pagos, se puede generar todo otra vez
        // con las modificaciones, deshaciendo los vinculos con los inmuebles por si los modifico.
        // 2. Si el estado del gasto no comun no es igual a PEND, tiene pagos, se verifica entonces 
        // el estado de las facturas: 
        //    2.1 Si TODAS las facturas tienen estado PEND, se procede como en el caso 1, siempre 
        //        que no se haya modificado el monto.
        //    2.2 Si alguna factura tiene estado diferente de PEND, tiene pagos, lo que significa
        //        que se puede modificar el gasto, siempre que el mto y los inmueble seleccionados
        //        sean los mismos.
        // 3. El estado general de la factura (edoGralFac) se actualiza de la siguiente forma:
        //    3.1 Cuando se crea el gasto y se generan las facturas por primera vez, quedando INAC.
        //    3.2 Cuando se realiza un pago del gasto no comun, en el caso de uso de pagos de cxp, 
        //        donde se le asigna edo PEND.
        //    3.3 Al realizar algun pago de facturas de cxc, asignandosele el edo EPROG O CANCEL.
        //        El estado CANCEL se asigna solo cuando TODAS las facturas han sido canceladas, 
        //        El estado EPROG, cuando al menos una ya ha sido pagada, parcial o totalmente..
        //--------------------------------------------------------------------------------------------
        mtoGNCOriginal = gnc.getMtoGastoNoComun().toString();
        
        if (gnc.getEdoGastoNoComun().equals("PEND") & gnc.getEdoGralFac().equals("PEND")) {
            actualizarGastoNoComun(gnc);
            deshacerVinculosGncFacInm(gnc);
            crearVinculosGncFacInm(gnc);

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Para proceder con la modificación de este gasto, deberan revertirse antes los pagos realizados sobre el..."));
            return;
        }
        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto no comun correspondiente al nro. " + gnc.getNumDocGastoNoComun() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setFecGastoNoComun(String fecGastoNoComun) {
        this.fecGastoNoComun = fecGastoNoComun;
    }

    public String getFecGastoNoComun() {
        return fecGastoNoComun;
    }

    public void setMtoGastoNoComun(String mtoGastoNoComun) {
        this.mtoGastoNoComun = mtoGastoNoComun;
    }

    public String getMtoGastoNoComun() {
        return mtoGastoNoComun;
    }

    public void setNumDocGastoNoComun(String numDocGastoNoComun) {
        this.numDocGastoNoComun = numDocGastoNoComun;
    }

    public String getNumDocGastoNoComun() {
        return numDocGastoNoComun;
    }

    public void setObsGastoNoComun(String obsGastoNoComun) {
        this.obsGastoNoComun = obsGastoNoComun;
    }

    public String getObsGastoNoComun() {
        return obsGastoNoComun;
    }

    public void setIdInmueble(List idInmueble) {
        this.idInmueble = idInmueble;
    }

    public List getIdInmueble() {
        return idInmueble;
    }

    public void setIdConcepto(String idConcepto) {
        this.idConcepto = idConcepto;
    }

    public String getIdConcepto() {
        return idConcepto;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setSeleccion(HtmlSelectOneRadio seleccion) {
        this.seleccion = seleccion;
    }

    public HtmlSelectOneRadio getSeleccion() {
        return seleccion;
    }

    public void setListaInmuebles(List listaInmuebles) {
        this.listaInmuebles = listaInmuebles;
    }

    public List getListaInmuebles() {
        return listaInmuebles;
    }

    public void setPerGastoNoComun(String perGastoNoComun) {
        this.perGastoNoComun = perGastoNoComun;
    }

    public String getPerGastoNoComun() {
        return perGastoNoComun;
    }

    // .....
    public void setFilaSeleccionada(GastoNoComunDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public GastoNoComunDT getFilaSeleccionada() {
        return filaSeleccionada;
    }

    public void setMtoGNCOriginal(String mtoGNCOriginal) {
        this.mtoGNCOriginal = mtoGNCOriginal;
    }

    public String getMtoGNCOriginal() {
        return mtoGNCOriginal;
    }

    public void setNumDocSel(String numDocSel) {
        this.numDocSel = numDocSel;
    }

    public String getNumDocSel() {
        return numDocSel;
    }
}
