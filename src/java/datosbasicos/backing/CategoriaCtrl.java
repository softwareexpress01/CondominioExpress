package datosbasicos.backing;

import datosbasicos.modelos.Categoria;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;

public class CategoriaCtrl extends DaoCrud<Categoria, Condominio, Integer> implements Serializable {

    private String nombCategoria;
    private String descCategoria;
    private Categoria filaSeleccionada;

    public CategoriaCtrl() {
    }

    public void inicializarPanel() {

        this.setNombCategoria(null);
        this.setDescCategoria(null);

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

        return "datosbasicos";

    }

    public String validarPanel() {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        String enBlanco = "";
        String mensaje = "";

        if (val.sonDatosEnBlanco(this.nombCategoria) | this.nombCategoria.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre de la categoría...";
            return mensaje;
        }
        if (!val.esNombApell(this.nombCategoria)) {

            mensaje = "El nombre categoría debe ser alfanumérico. Por favor, intente de nuevo...";
            return mensaje;
        }

        if (val.sonDatosEnBlanco(this.descCategoria) | this.descCategoria.equals(enBlanco)) {
            mensaje = "Por favor, ingrese la descripción ...";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(descCategoria)) {
            mensaje = "Descripción de categoría debe ser alfanumérica. Por favor, intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public Collection getCategoriasPorCondominio() {

        // Obtiene las categorias para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Categoria> listCat = new ArrayList<Categoria>();
        String nombQuery = "buscarCategoriasPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listCat = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listCat);// para ordenar la lista de categorias

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Categoria> listaDesOrdenada = new ArrayList<Categoria>();

        for (Iterator<Categoria> it = lista.iterator();
                it.hasNext();) {
            Categoria cat = (Categoria) it.next();
            listaDesOrdenada.add(cat);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCat());//devuelve la lista ordenada en listaDesOrdenada

        Collection<Categoria> coleccionOrdenada = new ArrayList<Categoria>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Categoria ca = listaDesOrdenada.get(i);
            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public void editarCategoriaAction() {

        Categoria catSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila de la categoria seleccionada la cual se buscara como "Categoria" en el map #{Categoria} 
        ctx.getExternalContext().getSessionMap().put("Categoria", catSel);
        inicializarPanel();

        this.setNombCategoria(catSel.getNombCategoria());
        this.setDescCategoria(catSel.getDescCategoria());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(catSel.getNombCategoria());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarCategoria() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Categoria categoriaNueva = new Categoria();

        categoriaNueva.setNombCategoria(nombCategoria);
        categoriaNueva.setDescCategoria(descCategoria);
        categoriaNueva.setEdoCategoria("ACT");


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        categoriaNueva.setCondominio(condominio);
        try {
            almacenarOModificarEntidad(categoriaNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Categoría " + categoriaNueva.getNombCategoria() + " ha sido registrada satisfactoriamente!"));
        return;
    }

    public void modificarCategoria() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        Categoria catSel = (Categoria) ctx.getExternalContext().getSessionMap().get("Categoria");

        catSel.setNombCategoria(this.nombCategoria);
        catSel.setDescCategoria(this.descCategoria);

        try {
            almacenarOModificarEntidad(catSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Categoría " + catSel.getNombCategoria() + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public void setNombCategoria(String nombCategoria) {
        this.nombCategoria = nombCategoria;
    }

    public String getNombCategoria() {
        return nombCategoria;
    }

    public void setDescCategoria(String descCategoria) {
        this.descCategoria = descCategoria;
    }

    public String getDescCategoria() {
        return descCategoria;
    }

    // .....
    public void setFilaSeleccionada(Categoria filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public Categoria getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
