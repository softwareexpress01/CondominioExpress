package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import movimientos.modelos.GastoPropiedad;
import movimientos.modelos.GastoComunDT;
import movimientos.modelos.GastoPropiedad;
import movimientos.modelos.GastoPropiedadDT;

public class GastoPropiedadCtrl extends DaoCrud<GastoPropiedad, Condominio, Integer> implements Serializable {

    private String fecGastoPropiedad;
    private String numDocGastoPropiedad;
    private HtmlSelectOneMenu idProveedor;
    private HtmlSelectOneMenu idConcepto;
    public String mtoGastoPropiedad;
    public String obsGastoPropiedad;
    private Collection<GastoPropiedad> listaGP = new ArrayList<GastoPropiedad>();
    private Collection<GastoPropiedadDT> listaGastoPropiedadDT = new ArrayList<GastoPropiedadDT>();
    private List listaProveedores = new ArrayList();
    private List listaConceptos = new ArrayList();

    public GastoPropiedadCtrl() {
    }

    public void inicializarPanel() {

        this.setFecGastoPropiedad(null);
        this.setNumDocGastoPropiedad(null);
        Integer cero = new Integer(0);
        this.idProveedor.setValue(cero);
        this.idConcepto.setValue(cero);
        this.setMtoGastoPropiedad(null);
        this.setObsGastoPropiedad(null);

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

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        if (this.getFecGastoPropiedad().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento/factura...";
            return mensaje;
        }
        if (this.getNumDocGastoPropiedad().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el numero del documento/factura ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        // Verifica si selecciono alguna Proveedor y Concepto (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------
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
        //--------------------------------------------------------------------------------------
        if (this.getMtoGastoPropiedad().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        return mensaje;
    }
    private UIData data = null;

    public Collection getGastosPropiedadPorCondominio() {

        listaGastoPropiedadDT.clear();
        listaGP.clear();
        // Obtiene los gastos por propiedad para la propiedad dada (id), los muestra en el panel 2
        // Obtiene el id de la propiedad del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();


        String nombQuery = "buscarGastosPropiedadPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGP = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listaGP);// para ordenar la lista de gastos

        // se inicializa el objeto de gastos comunes ordinarios para el DataTable...
        listaGastoPropiedadDT = inicializarGastoPropiedadDT(listaOrd);
        return listaGastoPropiedadDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<GastoPropiedad> listaDesOrdenada = new ArrayList<GastoPropiedad>();

        for (Iterator<GastoPropiedad> it = lista.iterator();
                it.hasNext();) {
            GastoPropiedad gp = (GastoPropiedad) it.next();
            listaDesOrdenada.add(gp);
        }

        Collections.sort(listaDesOrdenada, new ComparadorGP());//devuelve la lista ordenada en listaDesOrdenada

        Collection<GastoPropiedad> coleccionOrdenada = new ArrayList<GastoPropiedad>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            GastoPropiedad gasto = listaDesOrdenada.get(i);
            coleccionOrdenada.add(gasto);
        }
        return coleccionOrdenada;
    }

    private Collection<GastoPropiedadDT> inicializarGastoPropiedadDT(Collection listaGP) {

        for (Iterator<GastoPropiedad> it = listaGP.iterator();
                it.hasNext();) {

            GastoPropiedad gpro = (GastoPropiedad) it.next();
            if (gpro == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                Integer idProv = gpro.getIdProveedor();
                String nombProv = buscarNombProveedor(idProv);
                Integer idConc = gpro.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                GastoPropiedadDT GastoPropiedadDT = new GastoPropiedadDT();

                GastoPropiedadDT.setId(gpro.getId());
                GastoPropiedadDT.setIdProveedor(idProv);
                GastoPropiedadDT.setIdConcepto(idConc);
                GastoPropiedadDT.setNombProveedor(nombProv);
                GastoPropiedadDT.setNombConcepto(nombConc);
                GastoPropiedadDT.setFecGastoPropiedad(gpro.getFecGastoPropiedad());
                GastoPropiedadDT.setNumDocGastoPropiedad(gpro.getNumDocGastoPropiedad());
                GastoPropiedadDT.setMtoGastoPropiedad(gpro.getMtoGastoPropiedad());
                GastoPropiedadDT.setObsGastoPropiedad(gpro.getObsGastoPropiedad());
                GastoPropiedadDT.setEdoGastoPropiedad(gpro.getEdoGastoPropiedad());
                GastoPropiedadDT.setSaldoGastoPropiedad(gpro.getSaldoGastoPropiedad());

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                String fechaString = formatter.format(gpro.getFecMovGP());
                GastoPropiedadDT.setFecMovGP(fechaString);

                listaGastoPropiedadDT.add(GastoPropiedadDT);
            }
        }
        return listaGastoPropiedadDT;
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
        String descConcepto = concepto.getDescConcepto();
        return descConcepto;
    }

    public List getProveedoresPorCondominio() {

        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
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

        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor prov = (Proveedor) proveedores.get(i);
            if (prov == null) {// Si no hay proveedores devuelve la lista en blanco
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Aun no se ha sido registrado ningun proveedor para el condominio actual!.Por favor, contacte con su administrador..."));
                break;
            } else {
                listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
            }
        }
        return (List) listaProveedores;
    }

    public List getConceptosPorCondominio() {

        listaConceptos.clear();
        listaConceptos.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
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
        Integer cero = new Integer(0);
        Integer tamaño = conceptos.size();
        if (tamaño.equals(cero)) {// Si no hay conceptos devuelve la lista en blanco
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun concepto para condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }
        for (int i = 0; i < conceptos.size(); i++) {
            Concepto conc = (Concepto) conceptos.get(i);
            if (conc == null) {// Si no hay conceptos devuelve la lista en blanco
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Aun no ha sido registrado ningun concepto para el condominio actual!.Por favor, contacte con su administrador..."));
                break;
            } else {
                listaConceptos.add(new SelectItem(conc.getId(), conc.getNombConcepto()));
            }
        }
        return (List) listaConceptos;
    }

    public void editarGastoPropiedadAction() {

        GastoPropiedadDT gpSel = (GastoPropiedadDT) this.getData().getRowData();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoPropiedadDT" en el map #{GastoPropiedadDT} 
        ctx.getExternalContext().getSessionMap().put("GastoPropiedadDT", gpSel);

        this.setFecGastoPropiedad(gpSel.getFecGastoPropiedad());
        this.setNumDocGastoPropiedad(gpSel.getNumDocGastoPropiedad());
        this.idProveedor.setValue(gpSel.getIdProveedor());
        this.idConcepto.setValue(gpSel.getIdConcepto());
        this.setObsGastoPropiedad(gpSel.getObsGastoPropiedad());
        //-----------------------------------------------------------        
        //                  Convertir de Double a String
        //-----------------------------------------------------------
        String mtoPre = gpSel.getMtoGastoPropiedad().toString();
        this.setMtoGastoPropiedad(mtoPre);
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(gpSel.getNumDocGastoPropiedad());

        String idGP = gpSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        GastoPropiedad gpro = null;
        for (Iterator<GastoPropiedad> it = listaGP.iterator();
                it.hasNext();) {
            gpro = (GastoPropiedad) it.next();
            if (gpro.getId().toString().equals(idGP)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("GastoPropiedad", gpro);
                break;
            }
        }

        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoPropiedad" en el map #{GastoPropiedad} 
        ctx.getExternalContext().getSessionMap().put("GastoPropiedad", gpro);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarGastoPropiedad() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        GastoPropiedad GastoPropiedadNuevo = new GastoPropiedad();

        GastoPropiedadNuevo.setFecGastoPropiedad(fecGastoPropiedad);
        GastoPropiedadNuevo.setNumDocGastoPropiedad(numDocGastoPropiedad);
        GastoPropiedadNuevo.setObsGastoPropiedad(obsGastoPropiedad);
        GastoPropiedadNuevo.setEdoGastoPropiedad("PEND");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = Calendar.getInstance(TimeZone.getDefault());
        GastoPropiedadNuevo.setFecMovGP(fecha.getTime());

        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        //-----------------------------------------------------------
        GastoPropiedadNuevo.setIdProveedor(idProvInteger);
        GastoPropiedadNuevo.setIdConcepto(idConcInteger);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoPropiedad).doubleValue();
        GastoPropiedadNuevo.setMtoGastoPropiedad(mto);
        GastoPropiedadNuevo.setSaldoGastoPropiedad(mto);
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto propiedad usa el DaoCrud
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        GastoPropiedadNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(GastoPropiedadNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto por propiedad correspondiente al documento Nro. " + GastoPropiedadNuevo.getNumDocGastoPropiedad() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarGastoPropiedad() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        GastoPropiedadDT gproSel = (GastoPropiedadDT) ctx.getExternalContext().getSessionMap().get("GastoPropiedadDT");
        String idGP = gproSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos , listaGCOrd
        GastoPropiedad gpro = null;
        for (Iterator<GastoPropiedad> it = listaGP.iterator();
                it.hasNext();) {
            gpro = (GastoPropiedad) it.next();
            if (gpro.getId().toString().equals(idGP)) {
                break;
            }
        }
        gpro.setFecGastoPropiedad(fecGastoPropiedad);
        gpro.setNumDocGastoPropiedad(numDocGastoPropiedad);
        gpro.setObsGastoPropiedad(obsGastoPropiedad);
        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String idProvString = idProveedor.getValue().toString();
        Integer idProvInteger = Integer.parseInt(idProvString);
        String idConcString = idConcepto.getValue().toString();
        Integer idConcInteger = Integer.parseInt(idConcString);
        //-----------------------------------------------------------
        gpro.setIdProveedor(idProvInteger);
        gpro.setIdConcepto(idConcInteger);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoGastoPropiedad).doubleValue();
        gpro.setMtoGastoPropiedad(mto);
        gpro.setSaldoGastoPropiedad(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(gpro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El gasto por propiedad correspondiente al documento " + gpro.getNumDocGastoPropiedad() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setFecGastoPropiedad(String fecGastoPropiedad) {
        this.fecGastoPropiedad = fecGastoPropiedad;
    }

    public String getFecGastoPropiedad() {
        return fecGastoPropiedad;
    }

    public void setNumDocGastoPropiedad(String numDocGastoPropiedad) {
        this.numDocGastoPropiedad = numDocGastoPropiedad;
    }

    public String getNumDocGastoPropiedad() {
        return numDocGastoPropiedad;
    }

    public void setMtoGastoPropiedad(String mtoGastoPropiedad) {
        this.mtoGastoPropiedad = mtoGastoPropiedad;
    }

    public String getMtoGastoPropiedad() {
        return mtoGastoPropiedad;
    }

    public String getObsGastoPropiedad() {
        return obsGastoPropiedad;
    }

    public void setObsGastoPropiedad(String obsGastoPropiedad) {
        this.obsGastoPropiedad = obsGastoPropiedad;
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
    // .....

    public void setData(UIData data) {
        this.data = data;
    }

    public UIData getData() {
        return data;
    }
}
