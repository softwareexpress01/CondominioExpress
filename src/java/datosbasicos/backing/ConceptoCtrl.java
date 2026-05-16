package datosbasicos.backing;

import datosbasicos.modelos.Categoria;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.ConceptoDT;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import validadores.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

public class ConceptoCtrl extends DaoCrud<Concepto, Condominio, Integer> implements Serializable {

    private String nombConcepto;
    private String descConcepto;
    private HtmlSelectOneMenu categoria;
    private Collection listaCat = new ArrayList();
    private Collection listaOrd = new ArrayList();
    Collection<Concepto> listaCon = new ArrayList<Concepto>();
    private ConceptoDT filaSeleccionada;

    public ConceptoCtrl() {
    }

    public void inicializarPanel() {

        this.setNombConcepto(null);
        this.setDescConcepto(null);

        Integer cero = new Integer(0);
        this.categoria.setValue(cero);

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

        return "datosbasicos";

    }

    public String validarPanel() {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        String enBlanco = new String();
        String mensaje = new String();

        if (nombConcepto.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del concepto...";
            return mensaje;
        }

        if (!val.esNombApell(this.nombConcepto)) {
            mensaje = "El nombre del concepto debe ser alfanumérico.Por favor, intente de nuevo";
            return mensaje;
        }

        if (this.getDescConcepto().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la descripción ...";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(this.getDescConcepto())) {
            mensaje = "La descripción del concepto debe ser alfanumérica. Por favor, intente de nuevo";
            return mensaje;
        }
        //---------------------------------------------------------------------------
        // Verifica si selecciono alguna categoria(diferente de la opcion Seleccione)
        //---------------------------------------------------------------------------
        String sel = categoria.getValue().toString();
        String cero = "0";

        if (sel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione la categoría ...";
        }

        return mensaje;
    }

    public Collection getConceptosPorCondominio() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//Controla la ejecucion del data table, si va o no a la bd
            return ((List) listaOrd);
        }
        // Obtiene los conceptos para el condominio dado (id), los muestra en el panel 2
        // Obtiene el id del condominio del beanUsuario
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarConceptoPorIdCondFetchCat";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaCon = dataAcces.buscarListaFetchPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        beanUsuario.ejecutarQuery = false;// no volvera a ejecutar los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;// Contrala la ejecucion de busqueda para el data table

        Collection listaDT = inicializarConceptoDT(listaCon);

        listaOrd = ordenarLista(listaDT);// para ordenar la lista de conceptos

        return listaOrd;
    }

    public Collection inicializarConceptoDT(Collection listaCon) {

        Collection<ConceptoDT> lista = new ArrayList<ConceptoDT>();

        for (Iterator<Concepto> it = listaCon.iterator();
                it.hasNext();) {
            Concepto con = (Concepto) it.next();
            ConceptoDT conceptoDT = new ConceptoDT();
            conceptoDT.setId(con.getId());
            conceptoDT.setNombConcepto(con.getNombConcepto());
            conceptoDT.setDescConcepto(con.getDescConcepto());
            conceptoDT.setNombCategoria(con.getCategoria().getNombCategoria());
            conceptoDT.setCategoria(con.getCategoria());
            lista.add(conceptoDT);
        }
        return lista;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<ConceptoDT> listaDesOrdenada = new ArrayList<ConceptoDT>();

        for (Iterator<ConceptoDT> it = lista.iterator();
                it.hasNext();) {
            ConceptoDT con = (ConceptoDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorConDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<ConceptoDT> coleccionOrdenada = new ArrayList<ConceptoDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            ConceptoDT conDT = listaDesOrdenada.get(i);
            coleccionOrdenada.add(conDT);
        }
        return coleccionOrdenada;
    }

    public List getCategoriasPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
            return ((List) listaCat);
        }
        listaCat.clear();
        listaCat.add(new SelectItem(new Integer(0), "Seleccione"));
        List categorias = new ArrayList();
        //-------------------------------------------------------------------------------------
        // Genera la lista de categorias para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarCategoriasPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            categorias = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (categorias.isEmpty()) {// Si no hay categorias devuelve la lista en blanco
            beanUsuario.agregar = true;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ninguna categoría para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }

        for (int i = 0; i < categorias.size(); i++) {
            Categoria cat = (Categoria) categorias.get(i);
            listaCat.add(new SelectItem(cat.getId(), cat.getNombCategoria()));
        }

        return (List) listaCat;
    }

    public void editarConceptoAction() {

        ConceptoDT concSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del concepto seleccionado ,el cual se buscara como "ConceptoDT" en el map #{ConceptoDT} 
        ctx.getExternalContext().getSessionMap().put("ConceptoDT", concSel);
        Concepto con = null;
        for (Iterator<Concepto> it = listaCon.iterator();
                it.hasNext();) {
            con = (Concepto) it.next();
            if (concSel.getId().equals(con.getId())) {
                ctx.getExternalContext().getSessionMap().put("Concepto", con); // Por si se va a eliminar
            }
        }

        inicializarPanel();

        this.setNombConcepto(concSel.getNombConcepto());
        this.setDescConcepto(concSel.getDescConcepto());
        this.categoria.setValue(concSel.getCategoria().getId());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(concSel.getNombConcepto());// por si se va a eliminar

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarConcepto() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Concepto conceptoNuevo = new Concepto();

        conceptoNuevo.setNombConcepto(nombConcepto);
        conceptoNuevo.setDescConcepto(descConcepto);
        conceptoNuevo.setEdoConcepto("ACT");


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

        Categoria cat = null;//Para buscar la categoria usa el DaoQuery
        //-----------------------------------------------------------
        //                  Convertir de HtmlSelecOneMenu a Entero
        //-----------------------------------------------------------
        String catString = categoria.getValue().toString();
        Integer catId = Integer.parseInt(catString);
        //-----------------------------------------------------------
        String nombQuery = "buscarCategoriaPorId";
        DaoQuery<Categoria> dataAcces = new DaoQuery<Categoria>();
        try {
            cat = dataAcces.buscarEntidadPorId(catId, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        conceptoNuevo.setCondominio(condominio);
        conceptoNuevo.setCategoria(cat);
        try {
            almacenarOModificarEntidad(conceptoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El concepto " + conceptoNuevo.getNombConcepto() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarConcepto() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ConceptoDT concSel = (ConceptoDT) ctx.getExternalContext().getSessionMap().get("ConceptoDT");
        Concepto con = null;

        for (Iterator<Concepto> it = listaCon.iterator();
                it.hasNext();) {
            con = (Concepto) it.next();
            if (concSel.getId().equals(con.getId())) {
                break;
            }
        }
        con.setNombConcepto(nombConcepto);
        con.setDescConcepto(descConcepto);

        String catConc = categoria.getValue().toString();
        Integer idCat = Integer.parseInt(catConc);
        Categoria cat;
        String nombQuery = "buscarCategoriaPorId";

        DaoQuery<Categoria> dataAcces = new DaoQuery<Categoria>();
        try {
            cat = dataAcces.buscarEntidadPorId(idCat, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        con.setCategoria(cat);
        try {
            almacenarOModificarEntidad(con);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El concepto " + concSel.getNombConcepto() + " ha sido modificado satisfactoriamente!"));
        return;
    
}
public void setNombConcepto(String nombConcepto) {
        this.nombConcepto = nombConcepto;
    }

    public String getNombConcepto() {
        return nombConcepto;
    }

    public void setDescConcepto(String descConcepto) {
        this.descConcepto = descConcepto;
    }

    public String getDescConcepto() {
        return descConcepto;
    }

    public void setCategoria(HtmlSelectOneMenu categoria) {
        this.categoria = categoria;
    }

    public HtmlSelectOneMenu getCategoria() {
        return categoria;
    }
    // .....

    public void setFilaSeleccionada(ConceptoDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public ConceptoDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
