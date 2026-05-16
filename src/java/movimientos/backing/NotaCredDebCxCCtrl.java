package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

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
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.NotaCredDebDT;

public class NotaCredDebCxCCtrl extends DaoCrud<NotaCredDebCxC, Inmueble, Integer> implements Serializable {

    private HtmlInputText fecNotaCredDebCxC;
    private HtmlSelectOneMenu idConcepto;
    private HtmlSelectOneMenu tipoNotaCredDebCxC;
    private HtmlInputText mtoNotaCredDebCxC;
    public String idInmueble;
    private Collection<NotaCredDebCxC> listaNCDCxC = new ArrayList<NotaCredDebCxC>();
    private Collection<NotaCredDebDT> listaNotaCredDebDT = new ArrayList<NotaCredDebDT>();
    private List listaInmuebles = new ArrayList();
    private List listaConceptos = new ArrayList();
    private NotaCredDebDT filaSeleccionada;

    public NotaCredDebCxCCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        System.out.println("En inicializar, valueChange");

        this.fecNotaCredDebCxC.setValue(null);
        this.idConcepto.setValue(null);
        this.tipoNotaCredDebCxC.setValue(null);
        this.mtoNotaCredDebCxC.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public void inicializarPanel() {

        System.out.println("En inicializar, sin valueChange");
        this.fecNotaCredDebCxC.setValue(null);
        this.idConcepto.setValue(null);
        this.tipoNotaCredDebCxC.setValue(null);
        this.mtoNotaCredDebCxC.setValue(null);

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

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idInmueble.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el inmueble ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if ((this.getFecNotaCredDebCxC().getValue().toString()).equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha...";
            return mensaje;
        }
        if (!val.esFecha(this.fecNotaCredDebCxC.getValue().toString())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
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
        if (this.getMtoNotaCredDebCxC().getValue().toString().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoNotaCredDebCxC().getValue().toString())) {
            mensaje = "El monto debe ser un numero entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }
        return mensaje;
    }

    public Collection getNotasCredDebCxCPorInmueble() {
       
        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInmueble == null) {
            idInmueble = "0";
        }

        listaNotaCredDebDT.clear();
        listaNCDCxC.clear();
        // Obtiene las notas de credito/debito ACTIVAS para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarNotasCredDebCxCPorIdInmAct";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaNCDCxC = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        // se inicializa el objeto de notas para el DataTable...
        listaNotaCredDebDT = inicializarNotaCredDebDT(listaNCDCxC);

        return listaNotaCredDebDT;
    }

    private Collection<NotaCredDebDT> inicializarNotaCredDebDT(Collection listaNCDCxC) {

        Collection listaNotas = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        Integer cero=0;

        for (Iterator<NotaCredDebCxC> it = listaNCDCxC.iterator();
                it.hasNext();) {

            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (ncd == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                          
                NotaCredDebDT notaCredDebDT = new NotaCredDebDT();
                Integer idConc = ncd.getIdConcepto();
                
                if (idConc.equals(cero)) {
                    notaCredDebDT.setNombConcepto("Interes Mora");
                } else {
                    notaCredDebDT.setNombConcepto(buscarNombConcepto(idConc));
                    
                }

                notaCredDebDT.setId(ncd.getId());

                notaCredDebDT.setIdConcepto(idConc);

                notaCredDebDT.setFecNotaCredDeb(ncd.getFecNotaCredDebCxC());
                notaCredDebDT.setNumNotaCredDeb(ncd.getNumNotaCredDebCxC());
                notaCredDebDT.setMtoNotaCredDeb(ncd.getMtoNotaCredDebCxC());

                notaCredDebDT.setEdoNotaCredDeb(ncd.getEdoNotaCredDebCxC());
                notaCredDebDT.setTipoNotaCredDeb(ncd.getTipoNotaCredDebCxC());
                notaCredDebDT.setSaldoNotaCredDeb(ncd.getSaldoNotaCredDebCxC());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                notaCredDebDT.setMtoNotaCredDebString(val.convertirDoubleAString(ncd.getMtoNotaCredDebCxC()));
                notaCredDebDT.setSaldoNotaCredDebString(val.convertirDoubleAString(ncd.getSaldoNotaCredDebCxC()));
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

                notaCredDebDT.setFecMovNCD(stringFechaMov);

                listaNotas.add(notaCredDebDT);

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

    public void editarNotasCredDebCxCAction() {
       
        NotaCredDebDT ncddtSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("NotaCredDebDT", ncddtSel);

        this.fecNotaCredDebCxC.setValue(ncddtSel.getFecNotaCredDeb().toString());

        this.idConcepto.setValue(ncddtSel.getIdConcepto().toString());
        this.tipoNotaCredDebCxC.setValue(ncddtSel.getTipoNotaCredDeb());

        this.mtoNotaCredDebCxC.setValue(ncddtSel.getMtoNotaCredDebString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(ncddtSel.getNumNotaCredDeb());

        String idNCDCxC = ncddtSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaNCDCxC
        NotaCredDebCxC ncdcxc = null;
        for (Iterator<NotaCredDebCxC> it = listaNCDCxC.iterator();
                it.hasNext();) {
            ncdcxc = (NotaCredDebCxC) it.next();
            if (ncdcxc.getId().toString().equals(idNCDCxC)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("NotaCredDebCxC", ncdcxc);
                break;
            }
        }

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarNotaCredDebCxC() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        NotaCredDebCxC notaCreDebCxCNueva = new NotaCredDebCxC();

        notaCreDebCxCNueva.setFecNotaCredDebCxC(fecNotaCredDebCxC.getValue().toString());
        notaCreDebCxCNueva.setEdoNotaCredDebCxC("PEND");
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        notaCreDebCxCNueva.setIdConcepto(idcon);
        notaCreDebCxCNueva.setTipoNotaCredDebCxC(tipoNotaCredDebCxC.getValue().toString());
        notaCreDebCxCNueva.setTipoOperNotaCredDebCxC("MANUAL");
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        notaCreDebCxCNueva.setPerNotaCredDebCxC(usuarioActual.getPeriodoActual()); 
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        notaCreDebCxCNueva.setFecMovNCD(fecha);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoNotaCredDebCxC.getValue().toString()).doubleValue();
        //-----------------------------------------------------------
        notaCreDebCxCNueva.setMtoNotaCredDebCxC(mto);
        notaCreDebCxCNueva.setSaldoNotaCredDebCxC(mto);

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
                    new FacesMessage("Aun no se ha sido registrado ningun parametro para el condominio actual!.Por favor, contacte con su administrador..."));
            return;
        }


        Integer credInt = parametro.getProxNotaCredCxC() + 1;
        Integer debInt = parametro.getProxNotaDebCxC() + 1;

        String credString = credInt.toString();
        String debString = debInt.toString();

        String credito = "Credito";
        
        if (this.getTipoNotaCredDebCxC().getValue().toString().equals(credito)) {
            notaCreDebCxCNueva.setNumNotaCredDebCxC(credString);
            parametro.setProxNotaCredCxC(credInt);
        } else {
            notaCreDebCxCNueva.setNumNotaCredDebCxC(debString);
            parametro.setProxNotaDebCxC(debInt);
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

        notaCreDebCxCNueva.setInmueble(inmueble);

        try {
            almacenarOModificarEntidad(notaCreDebCxCNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La nota correspondiente al documento Nro. " + notaCreDebCxCNueva.getNumNotaCredDebCxC() + " ha sido registrada satisfactoriamente!"));
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

    public void modificarNotaCredDebCxC() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        NotaCredDebDT gcodtSel = (NotaCredDebDT) ctx.getExternalContext().getSessionMap().get("NotaCredDebDT");
        String idNCD = gcodtSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        NotaCredDebCxC ncd = null;
        for (Iterator<NotaCredDebCxC> it = listaNCDCxC.iterator();
                it.hasNext();) {
            ncd = (NotaCredDebCxC) it.next();
            if (ncd.getId().toString().equals(idNCD)) {
                break;
            }
        }
        ncd.setFecNotaCredDebCxC(fecNotaCredDebCxC.getValue().toString());
        Integer idcon = Integer.valueOf(idConcepto.getValue().toString()).intValue();
        ncd.setIdConcepto(idcon);
        ncd.setTipoNotaCredDebCxC(tipoNotaCredDebCxC.getValue().toString());
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoNotaCredDebCxC.getValue().toString()).doubleValue();
        ncd.setMtoNotaCredDebCxC(mto);
        ncd.setSaldoNotaCredDebCxC(mto);
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
                new FacesMessage("La nota correspondiente al nro. " + ncd.getNumNotaCredDebCxC() + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }

    public void setTipoNotaCredDebCxC(HtmlSelectOneMenu tipoNotaCredDebCxC) {
        this.tipoNotaCredDebCxC = tipoNotaCredDebCxC;
    }

    public HtmlSelectOneMenu getTipoNotaCredDebCxC() {
        return tipoNotaCredDebCxC;
    }

    public void setIdConcepto(HtmlSelectOneMenu idConcepto) {
        this.idConcepto = idConcepto;
    }

    public HtmlSelectOneMenu getIdConcepto() {
        return idConcepto;
    }

    public HtmlInputText getFecNotaCredDebCxC() {
        return fecNotaCredDebCxC;
    }

    public void setFecNotaCredDebCxC(HtmlInputText fecNotaCredDebCxC) {
        this.fecNotaCredDebCxC = fecNotaCredDebCxC;
    }

    public void setMtoNotaCredDebCxC(HtmlInputText mtoNotaCredDebCxC) {
        this.mtoNotaCredDebCxC = mtoNotaCredDebCxC;
    }

    public HtmlInputText getMtoNotaCredDebCxC() {
        return mtoNotaCredDebCxC;
    }
    // .....

    public void setFilaSeleccionada(NotaCredDebDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public NotaCredDebDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
