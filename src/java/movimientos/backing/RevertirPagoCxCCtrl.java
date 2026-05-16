package movimientos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoCxCDT;

public class RevertirPagoCxCCtrl extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

    public String idInmueble;
    private String desPagoCxC;
    private String numDocPagoCxC;
    private String mtoPagoCxC;
    private String formaPagoCxC;
    private String numRefPagoCxC;
    private String fecDocPagoCxC;
    private String bancoPagoCxC;
    private Collection<PagoCxC> listaPagosCxC = new ArrayList<PagoCxC>();
    private Collection<PagoCxCDT> listaPagosCxCDT = new ArrayList<PagoCxCDT>();
    private List listaInmuebles = new ArrayList();
    private PagoCxCDT filaSeleccionada;

    public RevertirPagoCxCCtrl() {
    }

    public void inicializarPanel() {

        this.setDesPagoCxC(null);
        this.setNumDocPagoCxC(null);
        this.setMtoPagoCxC(null);
        this.setFormaPagoCxC(null);
        this.setNumRefPagoCxC(null);
        this.setFecDocPagoCxC(null);
        this.setBancoPagoCxC(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.revertir = true;
        beanUsuario.agregar = false;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.revertir = true;
        beanUsuario.agregar = false;

        return "movimientos";

    }

    public Collection getPagosCxCPorInmueble() throws ParseException {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInmueble == null) {
            idInmueble = "0";
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaPagosCxCDT;
        }
        Calendar periodo = beanUsuario.getPeriodoActual();
        listaPagosCxCDT.clear();
        listaPagosCxC.clear();
        // Obtiene los pagos para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarPagosCxCPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosCxC = dataAcces.buscarListaEntidadesPeriodoActual(idInm, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listaPagosCxC);// para ordenar la lista de notas

        // se inicializa el objeto de notas para el DataTable...
        listaPagosCxCDT = inicializarPagosCxCDT(listaOrd);

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaPagosCxCDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoCxC> listaDesOrdenada = new ArrayList<PagoCxC>();

        for (Iterator<PagoCxC> it = lista.iterator();
                it.hasNext();) {
            PagoCxC pcxc = (PagoCxC) it.next();
            listaDesOrdenada.add(pcxc);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagoCxC());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoCxC> coleccionOrdenada = new ArrayList<PagoCxC>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoCxC pago = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pago);
        }
        return coleccionOrdenada;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoQuery
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private Collection<PagoCxCDT> inicializarPagosCxCDT(Collection lista) throws ParseException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        Calendar periodo = beanUsuario.getPeriodoActual();

        for (Iterator<PagoCxC> it = lista.iterator();
                it.hasNext();) {

            PagoCxC pagocxc = (PagoCxC) it.next();
            if (pagocxc == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                PagoCxCDT pagoCxCDT = new PagoCxCDT();

                pagoCxCDT.setId(pagocxc.getId());
                pagoCxCDT.setDesPagoCxC(pagocxc.getDesPagoCxC());
                pagoCxCDT.setNumDocPagoCxC(pagocxc.getNumDocPagoCxC());
                pagoCxCDT.setMtoPagoCxC(val.convertirDoubleAString(pagocxc.getMtoPagoCxC()));
                pagoCxCDT.setFormaPagoCxC(pagocxc.getFormaPagoCxC());
                pagoCxCDT.setFecDocPagoCxC(pagocxc.getFecDocPagoCxC());
                Integer idBanco = Integer.parseInt(pagocxc.getBancoPagoCxC());
                String nombBco = buscarBanco(idBanco);
                pagoCxCDT.setBancoPagoCxC(nombBco);

                pagoCxCDT.setFecMovPagoCxC(pagocxc.getFecDocPagoCxC());

                Calendar fecMov = convertirFecha(pagocxc.getFecDocPagoCxC());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos cargados despues del ultimo periodo cerrado
                //----------------------------------------------------------------------------------
                if (fecMov.after(periodo) | fecMov.equals(periodo)) {
                    listaPagosCxCDT.add(pagoCxCDT);
                }
            }
        }
        return listaPagosCxCDT;
    }

    public String buscarBanco(Integer idBanco) throws ValidatorException {

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
        return banco.getNombBanco();
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

    public void editarPagoCxCAction() {

        PagoCxCDT pagocxcSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("PagoCxCDT", pagocxcSel);

        this.setDesPagoCxC(pagocxcSel.getDesPagoCxC());
        this.setNumDocPagoCxC(pagocxcSel.getNumDocPagoCxC());
        this.setMtoPagoCxC(pagocxcSel.getMtoPagoCxC());
        this.setFormaPagoCxC(pagocxcSel.getFormaPagoCxC());
        this.setNumRefPagoCxC(pagocxcSel.getNumRefPagoCxC());
        this.setFecDocPagoCxC(pagocxcSel.getFecDocPagoCxC());
        this.setBancoPagoCxC(pagocxcSel.getBancoPagoCxC());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //             Guarda el nro del documento a revertir
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(pagocxcSel.getNumDocPagoCxC());
        beanUsuario.revertir = false;

    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }

    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public void setNumDocPagoCxC(String numDocPagoCxC) {
        this.numDocPagoCxC = numDocPagoCxC;
    }

    public String getNumDocPagoCxC() {
        return numDocPagoCxC;
    }

    public String getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(String mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getNumRefPagoCxC() {
        return numRefPagoCxC;
    }

    public void setNumRefPagoCxC(String numRefPagoCxC) {
        this.numRefPagoCxC = numRefPagoCxC;
    }

    public String getFecDocPagoCxC() {
        return fecDocPagoCxC;
    }

    public void setFecDocPagoCxC(String fecDocPagoCxC) {
        this.fecDocPagoCxC = fecDocPagoCxC;
    }

    public void setBancoPagoCxC(String bancoPagoCxC) {
        this.bancoPagoCxC = bancoPagoCxC;
    }

    public String getBancoPagoCxC() {
        return bancoPagoCxC;
    }

    // .....
    public void setFilaSeleccionada(PagoCxCDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoCxCDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
