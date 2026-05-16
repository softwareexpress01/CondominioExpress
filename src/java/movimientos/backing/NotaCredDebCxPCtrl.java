package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.text.DateFormat;
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
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.NotaCredDebCxPDT;

public class NotaCredDebCxPCtrl extends DaoCrud<NotaCredDebCxP, Proveedor, Integer> implements Serializable {

    private HtmlInputText fecNotaCredDebCxP;
    private HtmlSelectOneMenu idConcepto;
    public HtmlInputText mtoNotaCredDebCxP;
    public String idProveedor;
    public String idPago;
    private Collection<NotaCredDebCxP> listaNCDCxP = new ArrayList<NotaCredDebCxP>();
    private Collection<NotaCredDebCxP> listaNCDCxPSel = new ArrayList<NotaCredDebCxP>();
    private Collection<NotaCredDebCxPDT> listaNotaCredDebDT = new ArrayList<NotaCredDebCxPDT>();
    private List listaProveedores = new ArrayList();
    private List listaConceptos = new ArrayList();
    private NotaCredDebCxPDT filaSeleccionada;

    public NotaCredDebCxPCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        this.fecNotaCredDebCxP.setValue(null);
        this.idConcepto.setValue(null);
        this.mtoNotaCredDebCxP.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public void inicializarPanel() {

        this.fecNotaCredDebCxP.setValue(null);
        this.idConcepto.setValue(null);
        this.mtoNotaCredDebCxP.setValue(null);

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
        // Verifica si selecciono algun Proveedor   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idProveedor.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el Proveedor ...";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------


        if (this.fecNotaCredDebCxP.getValue().toString().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha...";
            return mensaje;
        }
        //  Convertir el string de fecha a Calendar para verificar que la fecha del Mov sea del periodo actual o del siguiente
//--------------------------------------------------------------------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecNotaCredDebCxP.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mesMov = cal.get(Calendar.MONTH);
        int añoMov = cal.get(Calendar.YEAR);
        Integer mes = mesMov;
        Integer año = añoMov;
        Integer mesMas = mes + 1;

        String periodo = mesMas.toString() + "-" + año.toString();
        if (!val.esRangoValido(periodo)) {
            mensaje = "La Fecha de la Nota debe ser del periodo actual o del próximo. Por favor intente de nuevo ";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Concepto   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------
        if (idConcepto.getValue().toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el concepto ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.mtoNotaCredDebCxP.getValue().toString().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.mtoNotaCredDebCxP.getValue().toString())) {
            mensaje = "El monto debe ser un numero entero o decimal. Use punto decimal si lo requiere, por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public Collection getNotasCredDebCxPPorProveedor() {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idProveedor == null) {
            idProveedor = "0";
        }

        listaNotaCredDebDT.clear();
        listaNCDCxP.clear();
        listaNCDCxPSel.clear();
        // Obtiene las notas de credito/debito para el Proveedor dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idProveedor).intValue();
        String nombQuery = "buscarNotasCredDebCxPPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaNCDCxP = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listaNCDCxP.isEmpty()) {
            return listaNotaCredDebDT;
        }

        /**
         * **************** AQUI SE SELECCIONAN SOLO AQUELLOS OBJETOS CON
         * ESTADO DIFERENTE DE "CANC" *************
         */
        for (Iterator<NotaCredDebCxP> it = listaNCDCxP.iterator();
                it.hasNext();) {
            NotaCredDebCxP nCD = (NotaCredDebCxP) it.next();
            if (!(nCD.edoNotaCredDebCxP.equals("CANC"))) {
                listaNCDCxPSel.add(nCD);
            }
        }

        /**
         * *******************************************************************************************************
         */
        // se inicializa el objeto de notas para el DataTable...
        listaNotaCredDebDT = inicializarNotaCredDebDT(listaNCDCxPSel);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table


        return listaNotaCredDebDT;
    }

    private Collection<NotaCredDebCxPDT> inicializarNotaCredDebDT(Collection listaNCDCxP) {

        Collection listaNotas = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<NotaCredDebCxP> it = listaNCDCxP.iterator();
                it.hasNext();) {

            NotaCredDebCxP ncd = (NotaCredDebCxP) it.next();
            if (ncd == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                Integer idConc = ncd.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                NotaCredDebCxPDT notaCredDebCxPDT = new NotaCredDebCxPDT();

                notaCredDebCxPDT.setId(ncd.getId());

                notaCredDebCxPDT.setIdConcepto(idConc);

                notaCredDebCxPDT.setNombConcepto(nombConc);
                notaCredDebCxPDT.setFecNotaCredDebCxP(ncd.getFecNotaCredDebCxP());
                notaCredDebCxPDT.setNumNotaCredDebCxP(ncd.getNumNotaCredDebCxP());
                notaCredDebCxPDT.setMtoNotaCredDebCxP(ncd.getMtoNotaCredDebCxP());

                notaCredDebCxPDT.setEdoNotaCredDebCxP(ncd.getEdoNotaCredDebCxP());
                notaCredDebCxPDT.setTipoNotaCredDebCxP(ncd.getTipoNotaCredDebCxP());
                notaCredDebCxPDT.setSaldoNotaCredDebCxP(ncd.getSaldoNotaCredDebCxP());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                notaCredDebCxPDT.setMtoNotaCredDebCxPString(val.convertirDoubleAString(ncd.getMtoNotaCredDebCxP()));
                notaCredDebCxPDT.setSaldoNotaCredDebCxPString(val.convertirDoubleAString(ncd.getSaldoNotaCredDebCxP()));
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = ncd.getFecMovNCD();
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

                notaCredDebCxPDT.setFecMovNCD(stringFechaMov);
                

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos con estado diferente de Cancelado
                //----------------------------------------------------------------------------------
                String cancelado = "CANC";
                if (!ncd.getEdoNotaCredDebCxP().equals(cancelado)) {
                    listaNotas.add(notaCredDebCxPDT);
                }

            }
        }
        return listaNotas;
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

        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Proveedors para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProv = usuarioActual.getIdCondActual();
        List<Proveedor> Proveedor = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            Proveedor = dataAcces.buscarListaEntidadesPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (Proveedor.isEmpty()) {

            listaProveedores.clear();
            listaProveedores.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaProveedores;

        }

        for (int i = 0; i < Proveedor.size(); i++) {
            Proveedor prov = (Proveedor) Proveedor.get(i);
            listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaProveedores;
    }

    public List getConceptosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaConceptos);
        }
        listaConceptos.clear();
        listaConceptos.add(new SelectItem(new Integer(0), "Seleccione"));

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
        if (conceptos.isEmpty()) {

            listaConceptos.clear();
            listaConceptos.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaConceptos;

        }
        for (int i = 0; i < conceptos.size(); i++) {
            Concepto conc = (Concepto) conceptos.get(i);
            listaConceptos.add(new SelectItem(conc.getId(), conc.getNombConcepto()));
        }
        return (List) listaConceptos;
    }

    public void editarNotasCredDebCxPAction() {

        NotaCredDebCxPDT ncddtSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado  
        ctx.getExternalContext().getSessionMap().put("NotaCredDebDT", ncddtSel);

        this.fecNotaCredDebCxP.setValue(ncddtSel.getFecNotaCredDebCxP());

        this.idConcepto.setValue(ncddtSel.getIdConcepto().toString());

        this.mtoNotaCredDebCxP.setValue(ncddtSel.getMtoNotaCredDebCxPString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(ncddtSel.getNumNotaCredDebCxP());

        String idNCDCxC = ncddtSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        NotaCredDebCxP ncdcxp = null;
        for (Iterator<NotaCredDebCxP> it = listaNCDCxP.iterator();
                it.hasNext();) {
            ncdcxp = (NotaCredDebCxP) it.next();
            if (ncdcxp.getId().toString().equals(idNCDCxC)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("NotaCredDebCxP", ncdcxp);
                break;
            }
        }

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarNotaCredDebCxP() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        NotaCredDebCxP notaCreDebCxPNueva = new NotaCredDebCxP();

        notaCreDebCxPNueva.setFecNotaCredDebCxP(fecNotaCredDebCxP.getValue().toString());
        notaCreDebCxPNueva.setEdoNotaCredDebCxP("PEND");
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        notaCreDebCxPNueva.setIdConcepto(idcon);
        notaCreDebCxPNueva.setTipoNotaCredDebCxP("Credito");

        // Integer cero = 0;
        //notaCreDebCxPNueva.setIdPago(cero);// a este atributo se le asignará el id del pago de la notaCredDeb cuando ocurra el mismo

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        notaCreDebCxPNueva.setFecMovNCD(fecha);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoNotaCredDebCxP.getValue().toString()).doubleValue();
        //-----------------------------------------------------------
        notaCreDebCxPNueva.setMtoNotaCredDebCxP(mto);
        notaCreDebCxPNueva.setSaldoNotaCredDebCxP(mto);

        //-----------------------------------------------------------
        // Se busca el parametro consecutivo de notas de credito/debito
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio = null;

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Parametro parametro = condominio.getParametro();
        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No hay parámetros definidos. Por favor contacte a su administrador."));
            return;
        }

        Integer credInt = parametro.getProxNotaCredCxP();

        String credString = credInt.toString();

        notaCreDebCxPNueva.setNumNotaCredDebCxP(credString);
        credInt = parametro.getProxNotaCredCxP() + 1;
        parametro.setProxNotaCredCxP(credInt);

        actualizarParametro(parametro);

        Integer idProv = Integer.valueOf(idProveedor);

        Proveedor Proveedor = null;//Para buscar el objeto Proveedor usa el DaoCrud
        try {
            Proveedor = (Proveedor) encontrarEntidadPorId(idProv);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        notaCreDebCxPNueva.setProveedor(Proveedor);


        try {
            almacenarOModificarEntidad(notaCreDebCxPNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La nota correspondiente al documento Nro. " + notaCreDebCxPNueva.getNumNotaCredDebCxP() + " ha sido registrada satisfactoriamente!"));
        return;
    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoCrudAux<Parametro> dataAcces = new DaoCrudAux<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void modificarNotaCredDebCxP() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        NotaCredDebCxPDT gcodtSel = (NotaCredDebCxPDT) ctx.getExternalContext().getSessionMap().get("NotaCredDebDT");
        String idNCD = gcodtSel.getId().toString();
        //buscar objeto a modificar en el arreglo de notas de credito
        NotaCredDebCxP ncd = null;
        for (Iterator<NotaCredDebCxP> it = listaNCDCxP.iterator();
                it.hasNext();) {
            ncd = (NotaCredDebCxP) it.next();
            if (ncd.getId().toString().equals(idNCD)) {
                break;
            }
        }

        ncd.setFecNotaCredDebCxP(fecNotaCredDebCxP.getValue().toString());
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        ncd.setIdConcepto(idcon);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoNotaCredDebCxP.getValue().toString()).doubleValue();
        ncd.setMtoNotaCredDebCxP(mto);
        ncd.setSaldoNotaCredDebCxP(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(ncd);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La nota correspondiente al nro. " + ncd.getNumNotaCredDebCxP() + " ha sido modificada satisfactoriamente!"));
    }

    public HtmlInputText getFecNotaCredDebCxP() {
        return fecNotaCredDebCxP;
    }

    public HtmlSelectOneMenu getIdConcepto() {
        return idConcepto;
    }

    public HtmlInputText getMtoNotaCredDebCxP() {
        return mtoNotaCredDebCxP;
    }

    public void setFecNotaCredDebCxP(HtmlInputText fecNotaCredDebCxP) {
        this.fecNotaCredDebCxP = fecNotaCredDebCxP;
    }

    public void setIdConcepto(HtmlSelectOneMenu idConcepto) {
        this.idConcepto = idConcepto;
    }

    public void setMtoNotaCredDebCxP(HtmlInputText mtoNotaCredDebCxP) {
        this.mtoNotaCredDebCxP = mtoNotaCredDebCxP;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdPago(String idPago) {
        this.idPago = idPago;
    }

    public String getIdPagoCxP() {
        return idPago;
    }

    public void setFilaSeleccionada(NotaCredDebCxPDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public NotaCredDebCxPDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
