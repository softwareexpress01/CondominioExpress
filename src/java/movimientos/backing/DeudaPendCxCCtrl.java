package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.text.DateFormat;
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
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.DeudaPendDT;

public class DeudaPendCxCCtrl extends DaoCrud<DeudaPendCxC, Inmueble, Integer> implements Serializable {

    private HtmlInputText fecDeudaPendCxC;
    private HtmlSelectOneMenu idConcepto;
    public HtmlInputText mtoDeudaPendCxC;
    private HtmlSelectOneMenu tipoDeudaPendCxC;
    public String idInmueble;
    private Collection<DeudaPendCxC> listaDPCxC = new ArrayList<DeudaPendCxC>();
    private Collection<DeudaPendDT> listaDeudaPendDT = new ArrayList<DeudaPendDT>();
    private List listaInmuebles = new ArrayList();
    private List listaConceptos = new ArrayList();
    private DeudaPendDT filaSeleccionada;

    public DeudaPendCxCCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        this.fecDeudaPendCxC.setValue(null);
        this.idConcepto.setValue(null);
        this.mtoDeudaPendCxC.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public void inicializarPanel() {

        this.fecDeudaPendCxC.setValue(null);
        this.idConcepto.setValue(null);
        this.mtoDeudaPendCxC.setValue(null);
        this.tipoDeudaPendCxC.setValue(null);

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

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //-------------------------------------------------------------------------------
        // Verifica si selecciono alguna Inmueble    (diferente de la opcion "Seleccione")
        //-------------------------------------------------------------------------------

        String cero = "0";
        if (idInmueble.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el inmueble ...";
            return mensaje;
        }
        //-------------------------------------------------------------------------------
        if (this.fecDeudaPendCxC.getValue().toString().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha...";
            return mensaje;
        }
        if (!val.esFecha(this.fecDeudaPendCxC.getValue().toString())) {
            mensaje = "La fecha de emision del pago debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        //-------------------------------------------------------------------------------
        // Verifica si selecciono algun Concepto    (diferente de la opcion "Seleccione")
        //-------------------------------------------------------------------------------
        if (idConcepto.getValue().toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el concepto ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.mtoDeudaPendCxC.getValue().toString().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.mtoDeudaPendCxC.getValue().toString())) {
            mensaje = "El monto debe ser un numero entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public Collection getDeudasPendCxCPorInmueble() {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInmueble == null) {
            idInmueble = "0";
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaDeudaPendDT;
        }
        listaDeudaPendDT.clear();
        listaDPCxC.clear();
        // Obtiene las Deudas pendientes para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        //String nombQuery = "buscarDeudasPendCxCPorIdInm"; 
        String nombQuery = "buscarDeudasPendCxCPorIdInmNoCanc"; //Para no traer las canc
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaDPCxC = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        // se inicializa el objeto de notas para el DataTable...
        listaDeudaPendDT = inicializarDeudaPendDT(listaDPCxC);

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaDeudaPendDT;
    }

    private Collection<DeudaPendDT> inicializarDeudaPendDT(Collection listaDPCxC) {

        Collection listaNotas = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<DeudaPendCxC> it = listaDPCxC.iterator();
                it.hasNext();) {

            DeudaPendCxC dpend = (DeudaPendCxC) it.next();
            if (dpend == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                Integer idConc = dpend.getIdConcepto();

                String nombConc = buscarNombConcepto(idConc);

                DeudaPendDT deudaPendDT = new DeudaPendDT();

                deudaPendDT.setId(dpend.getId());

                deudaPendDT.setIdConcepto(idConc);

                deudaPendDT.setNombConcepto(nombConc);
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaDPend = dpend.getFecDeudaPendCxC();
                String dia = "";
                String mes = "";
                int diaDpend = fechaDPend.get(Calendar.DAY_OF_MONTH);
                int mesDpend = fechaDPend.get(Calendar.MONTH) + 1;
                int añoDPend = fechaDPend.get(Calendar.YEAR);

                if (diaDpend < 10) {
                    dia = "0" + diaDpend;
                } else {
                    dia = String.valueOf(diaDpend);
                }

                if (mesDpend < 10) {
                    mes = "0" + mesDpend;
                } else {
                    mes = String.valueOf(mesDpend);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoDPend;


                deudaPendDT.setFecDeudaPend(stringFechaDp);

                deudaPendDT.setMtoDeudaPend(dpend.getMtoDeudaPendCxC());

                deudaPendDT.setEdoDeudaPend(dpend.getEdoDeudaPendCxC());

                deudaPendDT.setSaldoDeudaPend(dpend.getSaldoDeudaPendCxC());

                deudaPendDT.setTipoDeudaPend(dpend.getTipoDeudaPendCxC());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                deudaPendDT.setMtoDeudaPendString(val.convertirDoubleAString(dpend.getMtoDeudaPendCxC()));
                deudaPendDT.setSaldoDeudaPendString(val.convertirDoubleAString(dpend.getSaldoDeudaPendCxC()));
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = dpend.getFecMovDP();
                String diaM = "";
                String mesM = "";
                int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (diaMov < 10) {
                    diaM = "0" + diaMov;
                } else {
                    diaM = String.valueOf(diaMov);
                }

                if (mesMov < 10) {
                    mesM = "0" + mesMov;
                } else {
                    mesM = String.valueOf(mesMov);
                }

                String stringFechaMov = diaM + "-" + mesM + "-" + añoMov;

                deudaPendDT.setFecMovDP(stringFechaMov);

                listaNotas.add(deudaPendDT);
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
        System.out.println("El id del concepto encontrado es: " + idConc);
        if (concepto == null) {
            System.out.println("El id del concepto NO encontrado es: " + idConc);
        }
        String descConcepto = concepto.getNombConcepto();
        return descConcepto;
    }

    public List getInmueblesPorPropiedad() {

        listaInmuebles.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();
        List<Inmueble> inmuebles = new ArrayList<Inmueble>();
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
        if (inmuebles.isEmpty()) {// Si no hay inmuebles devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaInmuebles.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaInmuebles;
        }

        listaInmuebles.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }

        beanUsuario.ejecutarQueryDT = true;
        return (List) listaInmuebles;
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

    public void editarDeudasPendCxCAction() {

        DeudaPendDT dpSel = this.getFilaSeleccionada();

        if ("EPRO".equals(dpSel.getEdoDeudaPend())) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Deuda Pendiente seleccionada esta en Progreso, no se puede modificar ni eliminar."));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("DeudaPendDT", dpSel);

        this.fecDeudaPendCxC.setValue(dpSel.getFecDeudaPend());

        this.idConcepto.setValue(dpSel.getIdConcepto().toString());

        this.mtoDeudaPendCxC.setValue(dpSel.getMtoDeudaPendString());
        
        this.tipoDeudaPendCxC.setValue(dpSel.getTipoDeudaPend());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(dpSel.getNombConcepto());

        String idDPCxC = dpSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        DeudaPendCxC dpcxc = null;
        for (Iterator<DeudaPendCxC> it = listaDPCxC.iterator();
                it.hasNext();) {
            dpcxc = (DeudaPendCxC) it.next();
            if (dpcxc.getId().toString().equals(idDPCxC)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("DeudaPendCxC", dpcxc);
                break;
            }
        }

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarDeudaPendCxC() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        DeudaPendCxC deudaPendCxCNueva = new DeudaPendCxC();
        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDeudaPendCxC.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------

        deudaPendCxCNueva.setFecDeudaPendCxC(cal);
        deudaPendCxCNueva.setEdoDeudaPendCxC("PEND");
        deudaPendCxCNueva.setTipoDeudaPendCxC(tipoDeudaPendCxC.getValue().toString());
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        deudaPendCxCNueva.setIdConcepto(idcon);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        deudaPendCxCNueva.setFecMovDP(fecha);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoDeudaPendCxC.getValue().toString()).doubleValue();
        //-----------------------------------------------------------
        deudaPendCxCNueva.setMtoDeudaPendCxC(mto);
        deudaPendCxCNueva.setSaldoDeudaPendCxC(mto);

        //-----------------------------------------------------------
        // Se busca el parametro consecutivo de deuda pendiente
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
                    new FacesMessage("Aun no se ha sido registrado ningun parametro para el condominio actual!.Por favor, contacte con su administrador..."));
            return;
        }

        actualizarParametro(parametro);

        Integer idInm = Integer.valueOf(idInmueble);

        Inmueble inmueble = null;//Para buscar el objeto inmueble usa el DaoCrud
        try {
            inmueble = (Inmueble) encontrarEntidadPorId(idInm);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        deudaPendCxCNueva.setInmueble(inmueble);

        try {
            almacenarOModificarEntidad(deudaPendCxCNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //----------------------------------------------------------------
        //Se da formato a la fecha de la deudapara mostrarla en mensaje
        //----------------------------------------------------------------

        String dia = "";
        String mes = "";
        int diaMov = cal.get(Calendar.DAY_OF_MONTH);
        int mesMov = cal.get(Calendar.MONTH) + 1;
        int añoMov = cal.get(Calendar.YEAR);

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

        String stringFecha = dia + "-" + mes + "-" + añoMov;


        inicializarPanel();

        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La deuda correspondiente a la fecha " + stringFecha + " ha sido registrada satisfactoriamente!"));
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

    public void modificarDeudaPendCxC() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        DeudaPendDT dpSel = (DeudaPendDT) ctx.getExternalContext().getSessionMap().get("DeudaPendDT");
        String idDP = dpSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        DeudaPendCxC dpend = null;
        for (Iterator<DeudaPendCxC> it = listaDPCxC.iterator();
                it.hasNext();) {
            dpend = (DeudaPendCxC) it.next();
            if (dpend.getId().toString().equals(idDP)) {
                break;
            }
        }

        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDeudaPendCxC.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        dpend.setFecDeudaPendCxC(cal);
        dpend.setTipoDeudaPendCxC(tipoDeudaPendCxC.getValue().toString());
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        dpend.setIdConcepto(idcon);
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoDeudaPendCxC.getValue().toString()).doubleValue();
        dpend.setMtoDeudaPendCxC(mto);
        dpend.setSaldoDeudaPendCxC(mto);
        //-----------------------------------------------------------

        try {
            almacenarOModificarEntidad(dpend);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        //----------------------------------------------------------------
        //Se da formato a la fecha de la deudapara mostrarla en mensaje
        //----------------------------------------------------------------

        String dia = "";
        String mes = "";
        int diaMov = cal.get(Calendar.DAY_OF_MONTH);
        int mesMov = cal.get(Calendar.MONTH) + 1;
        int añoMov = cal.get(Calendar.YEAR);

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

        String stringFecha = dia + "-" + mes + "-" + añoMov;

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table
        
        inicializarPanel();
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La deuda correspondiente a la fecha " + stringFecha + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public HtmlInputText getFecDeudaPendCxC() {
        return fecDeudaPendCxC;
    }

    public HtmlSelectOneMenu getIdConcepto() {
        return idConcepto;
    }

    public HtmlInputText getMtoDeudaPendCxC() {
        return mtoDeudaPendCxC;
    }

    public void setFecDeudaPendCxC(HtmlInputText fecDeudaPendCxC) {
        this.fecDeudaPendCxC = fecDeudaPendCxC;
    }

    public void setIdConcepto(HtmlSelectOneMenu idConcepto) {
        this.idConcepto = idConcepto;
    }

    public HtmlSelectOneMenu getTipoDeudaPendCxC() {
        return tipoDeudaPendCxC;
    }

    public void setTipoDeudaPendCxC(HtmlSelectOneMenu tipoDeudaPendCxC) {
        this.tipoDeudaPendCxC = tipoDeudaPendCxC;
    }

    public void setMtoDeudaPendCxC(HtmlInputText mtoDeudaPendCxC) {
        this.mtoDeudaPendCxC = mtoDeudaPendCxC;
    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }

    // .....
    public void setFilaSeleccionada(DeudaPendDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public DeudaPendDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
