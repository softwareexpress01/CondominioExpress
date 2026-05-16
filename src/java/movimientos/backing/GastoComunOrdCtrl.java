package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.GastoComunDT;
import movimientos.modelos.GastoComunOrd;
import org.hibernate.HibernateException;
import util.*;

public class GastoComunOrdCtrl extends DaoCrud<GastoComunOrd, Condominio, Integer> implements Serializable {

    private String fecGastoComunOrd;
    private String numDocGastoComunOrd;
    private HtmlSelectOneMenu idProveedor;
    private HtmlSelectOneMenu idConcepto;
    private HtmlInputText perGastoComunOrd;
    public String mtoGastoComunOrd;
    public String obsGastoComunOrd;
    public String numDocSel;
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunDT> listaGastoComunOrdDT = new ArrayList<GastoComunDT>();
    private List listaProveedores = new ArrayList();
    private List listaConceptos = new ArrayList();
    private GastoComunDT filaSeleccionada;
    private GastoComunOrd gcov;

    public GastoComunOrdCtrl() {
    }

    public void inicializarPanel() {

        this.setFecGastoComunOrd(null);
        this.setNumDocGastoComunOrd(null);
        Integer cero = new Integer(0);
        this.idProveedor.setValue(cero);
        this.idConcepto.setValue(cero);
        this.setMtoGastoComunOrd(null);
        this.perGastoComunOrd.setValue(null);
        this.setObsGastoComunOrd(null);

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
        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Proveedor  (diferente de la opcion "Seleccione")
        //--------------------------------------------------------------------------------------
        String provSel = idProveedor.getValue().toString();
        String cero = "0";

        if (provSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el proveedor ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------

        if (this.getNumDocGastoComunOrd().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del documento/factura ...";
            return mensaje;
        }
        if (!val.esLong(this.getNumDocGastoComunOrd())) {
            mensaje = "El número del documento debe ser numérico, por favor intente de nuevo...";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------
        if (this.getFecGastoComunOrd().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento/factura...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecGastoComunOrd())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun concepto  (diferente de la opcion "Seleccione")
        //--------------------------------------------------------------------------------------
        String conSel = idConcepto.getValue().toString();

        if (conSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el concepto  ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que haya ingresado el periodo
        //--------------------------------------------------------------------
        String perGasto = perGastoComunOrd.getValue().toString();

        if (perGasto.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el período al cual va a asignar el gasto ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que el formato del periodo sea el adecuado
        //--------------------------------------------------------------------

        if (!val.esFormatoPeriodo(this.perGastoComunOrd.getValue().toString())) {
            mensaje = "El formato del período debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que el formato este dentro del rango adecuado o valido
        //--------------------------------------------------------------------

        if (!val.esRangoValido(this.perGastoComunOrd.getValue().toString())) {
            mensaje = "El período ingresado esta fuera del rango permitido, por favor intente de nuevo...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.getMtoGastoComunOrd().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoGastoComunOrd())) {
            mensaje = "El monto debe ser un número entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }

        return mensaje;
    }

    public GastoComunOrd buscarDocumento(String doc) {

        String nombQuery = "buscarGCOPorNumDoc";
        DaoQuery<GastoComunOrd> dataAcces = new DaoQuery<GastoComunOrd>();
        try {
            gcov = dataAcces.buscarEntidad(doc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return gcov;
    }

    public Collection getGastosComunesOrdPorCondominio() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaGastoComunOrdDT;
        }

        listaGastoComunOrdDT.clear();
        listaGCOrd.clear();
        // Obtiene los gastos comunes ordinarios para el condominio dado (id), los muestra en el panel 2
        // Obtiene el id del condominio del beanUsuario

        Integer idCond = beanUsuario.getIdCondActual();
        Calendar periodo = beanUsuario.getPeriodoActual();
        String nombQuery = "buscarGastosComunesOrdPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCOrd = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }


        Collection listaOrd = ordenarLista(listaGCOrd);// para ordenar la lista de gastos

        // se inicializa el objeto de gastos comunes ordinarios para el DataTable...
        listaGastoComunOrdDT = inicializarGastoComunOrdDT(listaOrd);
        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table
        return listaGastoComunOrdDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<GastoComunOrd> listaDesOrdenada = new ArrayList<GastoComunOrd>();

        for (Iterator<GastoComunOrd> it = lista.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            listaDesOrdenada.add(gco);
        }

        Collections.sort(listaDesOrdenada, new ComparadorGCOrd());//devuelve la lista ordenada en listaDesOrdenada

        Collection<GastoComunOrd> coleccionOrdenada = new ArrayList<GastoComunOrd>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            GastoComunOrd gasto = listaDesOrdenada.get(i);
            coleccionOrdenada.add(gasto);
        }
        return coleccionOrdenada;
    }

    private Collection<GastoComunDT> inicializarGastoComunOrdDT(Collection listaGCOrd) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Calendar periodo = beanUsuario.getPeriodoActual();

        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {

            GastoComunOrd gco = (GastoComunOrd) it.next();
            if (gco == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                Integer idProv = gco.getIdProveedor();
                String nombProv = buscarNombProveedor(idProv);
                Integer idConc = gco.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                GastoComunDT gastoComunOrdDT = new GastoComunDT();

                gastoComunOrdDT.setId(gco.getId());
                gastoComunOrdDT.setIdProveedor(idProv);
                gastoComunOrdDT.setIdConcepto(idConc);
                gastoComunOrdDT.setNombProveedor(nombProv);
                gastoComunOrdDT.setNombConcepto(nombConc);
                gastoComunOrdDT.setFecGastoComun(gco.getFecGastoComunOrd());
                gastoComunOrdDT.setNumDocGastoComun(gco.getNumDocGastoComunOrd());

                gastoComunOrdDT.setMtoGastoComun(gco.getMtoGastoComunOrd());
                gastoComunOrdDT.setObsGastoComun(gco.getObsGastoComunOrd());
                gastoComunOrdDT.setEdoGastoComun(gco.getEdoGastoComunOrd());
                gastoComunOrdDT.setSaldoGastoComun(gco.getSaldoGastoComunOrd());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                gastoComunOrdDT.setMtoGastoComunString(val.convertirDoubleAString(gco.getMtoGastoComunOrd()));
                gastoComunOrdDT.setSaldoGastoComunString(val.convertirDoubleAString(gco.getSaldoGastoComunOrd()));
                //----------------------------------------------------------------
                //Se da formato al periodo para mostrarlo en el data table (mm-aaaa)
                //----------------------------------------------------------------
                String mesp = "";
                Calendar per = gco.getPerGastoComunOrd();
                int mesPer = per.get(Calendar.MONTH) + 1;
                int añoPer = per.get(Calendar.YEAR);

                if (mesPer < 10) {
                    mesp = "0" + mesPer;
                } else {
                    mesp = String.valueOf(mesPer);
                }

                String stringPer = mesp + "-" + añoPer;

                gastoComunOrdDT.setPerGastoComun(stringPer);
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = gco.getFecMovGCOrd();
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

                gastoComunOrdDT.setFecMovGC(stringFechaMov);

                Calendar perGasto = gco.getPerGastoComunOrd();
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimintos con periodo mayor que el periodo actual
                //----------------------------------------------------------------------------------
                if (perGasto.equals(periodo) | perGasto.after(periodo)) {
                    listaGastoComunOrdDT.add(gastoComunOrdDT);
                }
            }
        }
        return listaGastoComunOrdDT;
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
        String nombConcepto = concepto.getNombConcepto();
        return nombConcepto;
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
        Collections.sort(proveedores, new ComparadorProv());
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

    public void editarGastoComunOrdAction() {

        GastoComunDT gcodtSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("GastoComunOrdDT", gcodtSel);

        this.setFecGastoComunOrd(gcodtSel.getFecGastoComun());
        this.setNumDocGastoComunOrd(gcodtSel.getNumDocGastoComun());

        this.setNumDocSel(gcodtSel.getNumDocGastoComun());//Si es modificado, se valida que no exista en la bd

        this.idProveedor.setValue(gcodtSel.getIdProveedor());
        this.idConcepto.setValue(gcodtSel.getIdConcepto());
        this.setObsGastoComunOrd(gcodtSel.getObsGastoComun());
        this.perGastoComunOrd.setValue(gcodtSel.getPerGastoComun());

        this.setMtoGastoComunOrd(gcodtSel.getMtoGastoComunString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(gcodtSel.getNumDocGastoComun());

        String idGCOrd = gcodtSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoComunOrd gcord = null;
        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {
            gcord = (GastoComunOrd) it.next();
            if (gcord.getId().toString().equals(idGCOrd)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("GastoComunOrd", gcord);
                break;
            }
        }

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

    public void registrarGastoComunOrd() throws ValidatorException, ParseException {

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

        gcov = null;
        gcov = buscarDocumento(this.getNumDocGastoComunOrd());
        if (!(gcov == null)) {
            if (gcov.getIdProveedor().equals(Integer.parseInt(idProveedor.getValue().toString()))) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                return;
            }
        }

        GastoComunOrd gastoComunOrdNuevo = new GastoComunOrd();

        gastoComunOrdNuevo.setFecGastoComunOrd(fecGastoComunOrd);
        gastoComunOrdNuevo.setNumDocGastoComunOrd(numDocGastoComunOrd);
        gastoComunOrdNuevo.setObsGastoComunOrd(obsGastoComunOrd);
        gastoComunOrdNuevo.setEdoGastoComunOrd("PEND");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        gastoComunOrdNuevo.setFecMovGCOrd(fecha);

        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        //-----------------------------------------------------------
        gastoComunOrdNuevo.setIdProveedor(idProvInteger);
        gastoComunOrdNuevo.setIdConcepto(idConcInteger);
        //-------------------------------------------------------------------
        // Convertir de HtmlInputText (periodo) a String, despues a Calendar
        //-------------------------------------------------------------------
        String stringPer = perGastoComunOrd.getValue().toString();

        String stringPeriodo = "01-" + stringPer;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gastoComunOrdNuevo.setPerGastoComunOrd(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoComunOrd).doubleValue();
        gastoComunOrdNuevo.setMtoGastoComunOrd(mto);
        gastoComunOrdNuevo.setSaldoGastoComunOrd(mto);
        //-----------------------------------------------------------
        Condominio condominio = buscarCondominio();
        gastoComunOrdNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(gastoComunOrdNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto comun ordinario correspondiente al documento Nro. " + gastoComunOrdNuevo.getNumDocGastoComunOrd() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarGastoComunOrd() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        if (!numDocSel.equals(numDocGastoComunOrd)) {
            //----------------------------------------------------------------
            // Verifica si ya existe en la bd, el nro de documento ingresado 
            //----------------------------------------------------------------
            gcov = null;
            gcov = buscarDocumento(this.getNumDocGastoComunOrd());
            if (!(gcov == null)) {
                if (gcov.getIdProveedor().equals(Integer.parseInt(idProveedor.getValue().toString()))) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Ya existe un gasto con el nro. documento ingresado, por favor, intente de nuevo..."));
                    return;
                }
            }
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoComunDT gcodtSel = (GastoComunDT) ctx.getExternalContext().getSessionMap().get("GastoComunOrdDT");
        String idGCOrd = gcodtSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoComunOrd gcord = null;
        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {
            gcord = (GastoComunOrd) it.next();
            if (gcord.getId().toString().equals(idGCOrd)) {
                break;
            }
        }
        gcord.setFecGastoComunOrd(fecGastoComunOrd);
        gcord.setNumDocGastoComunOrd(numDocGastoComunOrd);
        gcord.setObsGastoComunOrd(obsGastoComunOrd);
        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        //-----------------------------------------------------------
        gcord.setIdProveedor(idProvInteger);
        gcord.setIdConcepto(idConcInteger);
        //-------------------------------------------------------------------
        // Convertir de HtmlInputText (periodo) a String, despues a Calendar
        //-------------------------------------------------------------------
        String stringPer = perGastoComunOrd.getValue().toString();

        String stringPeriodo = "01-" + stringPer;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        gcord.setPerGastoComunOrd(cal);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoComunOrd).doubleValue();
        gcord.setMtoGastoComunOrd(mto);
        gcord.setSaldoGastoComunOrd(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(gcord);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto comun ordinario correspondiente al documento " + gcord.getNumDocGastoComunOrd() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setFecGastoComunOrd(String fecGastoComunOrd) {
        this.fecGastoComunOrd = fecGastoComunOrd;
    }

    public String getFecGastoComunOrd() {
        return fecGastoComunOrd;
    }

    public void setNumDocGastoComunOrd(String numDocGastoComunOrd) {
        this.numDocGastoComunOrd = numDocGastoComunOrd;
    }

    public String getNumDocGastoComunOrd() {
        return numDocGastoComunOrd;
    }

    public void setMtoGastoComunOrd(String mtoGastoComunOrd) {
        this.mtoGastoComunOrd = mtoGastoComunOrd;
    }

    public String getMtoGastoComunOrd() {
        return mtoGastoComunOrd;
    }

    public void setNumDocSel(String numDocSel) {
        this.numDocSel = numDocSel;
    }

    public String getNumDocSel() {
        return numDocSel;
    }

    public String getObsGastoComunOrd() {
        return obsGastoComunOrd;
    }

    public void setObsGastoComunOrd(String obsGastoComunOrd) {
        this.obsGastoComunOrd = obsGastoComunOrd;
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

    public void setPerGastoComunOrd(HtmlInputText perGastoComunOrd) {
        this.perGastoComunOrd = perGastoComunOrd;
    }

    public HtmlInputText getPerGastoComunOrd() {
        return perGastoComunOrd;
    }

    public void setFilaSeleccionada(GastoComunDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public GastoComunDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
