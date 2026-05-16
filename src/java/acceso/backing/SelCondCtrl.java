package acceso.backing;

import datosbasicos.modelos.Condominio;


import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import javax.faces.model.SelectItem;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 * Backing bean for SelCondUI.java
 */
public class SelCondCtrl extends DaoCrud<Usuario, Usuario, Integer> implements Serializable {

    private HtmlSelectOneMenu nombCondominio;
    private HtmlSelectOneMenu nombPropiedad;
    private List listCond = new ArrayList();
    private List listProp = new ArrayList();
    private Usuario usuario;
    private Boolean propSeleccionada = false;
    private Boolean condSeleccionado = false;

    public SelCondCtrl() {
    }

    public void propiedadesCondominio(AjaxBehaviorEvent event) {

        listProp.clear();
        listProp.add(new SelectItem(new Integer(0), "Seleccione"));
        //---------------------------------------------------------------------------
        // Verifica si selecciono algun condominio(diferente de la opcion Seleccione)
        //---------------------------------------------------------------------------
        String cero = "0";

        String idCondSel = nombCondominio.getValue().toString();

        if (idCondSel.equals(cero)) {
            condSeleccionado = false;
            return;
        }
        //---------------------------------------------------------------------------
        String nombCond = null;
        Integer idCond = null;
        Calendar periodo = null;
        //---------------------------------------------------------------------------
        // Genera la lista de propiedades del condominio dado
        //---------------------------------------------------------------------------
        for (int i = 0; i < usuario.condominios.size(); i++) {
            Condominio condLista = usuario.condominios.get(i);
            String idCondLista = condLista.getId().toString();
            if (idCondSel.equals(idCondLista)) {
                nombCond = condLista.getNombCondominio();
                idCond = condLista.getId();
                periodo = condLista.getPeriodoActual();
                for (Iterator<Propiedad> it = condLista.getPropiedades().iterator();
                        it.hasNext();) {
                    Propiedad propSel = (Propiedad) it.next();
                    listProp.add(new SelectItem(propSel.getId(), propSel.getNombPropiedad()));
                }
            }
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setIdCondActual(idCond);
        beanUsuario.setCondominioActual(nombCond);
        beanUsuario.setPeriodoActual(periodo);
        /*
         * beanUsuario.setRuta("C:/Users/jesus/Documents/NetBeansProjects/Produccion/Condominio4/web/logos/");
         */

        condSeleccionado = true;
    }

    public List getCondominiosPorUsuario() {

        listCond.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //---------------------------------------------------------------------------------------
        // Genera la lista de condominios para el usuario dado
        // Si el usuario no es administrador, busca su administrador asociado
        //----------------------------------------------------------------------------------------
        String codUsuario;
        String cod = new String();
        Integer idAdmin;
        String admin = "Admin";
        Usuario noAdmin;

        if (!usuarioActual.getPerUsuario().equals(admin)) {//No es administrador
            codUsuario = usuarioActual.getCodUsuario();
            String nombQuery = "buscarUsuario";
            DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
            try {
                noAdmin = dataAcces.buscarEntidad(codUsuario, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            Usuario proxyAdmin = noAdmin.getUsuarioAdmin();
            idAdmin = proxyAdmin.getId();
            Usuario administrador;
            try {
                administrador = encontrarEntidadPorId(idAdmin);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            cod = administrador.getCodUsuario();
        } else {
            cod = usuarioActual.getCodUsuario();
        }

        String nombQuery = "buscarUsuarioConCondominios";
        DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
        try {
            usuario = dataAcces.buscarEntidad(cod, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        listCond.add(new SelectItem(new Integer(0), "Seleccione"));
        //-------------------------------------
        // Salta los condominios inactivos
        //------------------------------------
        for (int i = 0; i < usuario.condominios.size(); i++) {
            Condominio cond = usuario.condominios.get(i);
            if (cond.getEdoCondominio().equals("INA")) {
                continue;
            }
            listCond.add(new SelectItem(cond.getId(), cond.getNombCondominio()));
        }

        //---------------------------------------------------------------------------
        // Si el usuario no tiene condominios, deshabilita el boton aceptar.
        // Pone null y cero al condominio y propiedad actual del bean
        //---------------------------------------------------------------------------

        Integer cont = 0;
        for (int i = 0; i < listCond.size(); i++) {
            listCond.get(i);
            cont++;
        }
        if (cont < 2) {    // Se desabilita el boton de aceptar si el usuario no tiene condominios asociados
            usuarioActual.setAgregar(true);
        }
        Integer cero = 0;
        usuarioActual.setCondominioActual(null);
        usuarioActual.setPropiedadActual(null);
        usuarioActual.setIdCondActual(cero);
        usuarioActual.setIdPropActual(cero);
        //---------------------------------------------------------------------------
        return listCond;
    }

    public Collection getListaPropiedades() {

        return listProp;
    }

    public String aceptar() {

        if (!condSeleccionado) {
            // no ha seleccionado el condominio
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Por favor, seleccione un condominio..."));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setAgregar(false);

        String Admin = "Admin";// Si no es administrador desabilita las opciones de datos basico y configuracion
        if (!beanUsuario.getPerUsuario().equals(Admin)) {
            beanUsuario.setDatosBasicos(true);
            beanUsuario.setConfiguracion(true);
            beanUsuario.setPropSel(true);
        }

        if (!propSeleccionada) {
            // no ha seleccionado la propiedad, se deshabilitan algunas opciones del menu
            beanUsuario.setMovMes(true);
            beanUsuario.setInformes(true);
            beanUsuario.setGestion(true);
            beanUsuario.setAdmin(true);
            beanUsuario.setInmuebles(true);
            beanUsuario.setPropSel(true);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Algunas opciones del menú aparecen deshabilitadas debido a que no se ha creado o seleccionado ninguna propiedad..."));
            return "menu";
        } else {
            // se habilitan nuevamente las opciones del menu
            beanUsuario.setMovMes(false);
            beanUsuario.setInformes(false);
            beanUsuario.setGestion(false);
            beanUsuario.setAdmin(false);
            beanUsuario.setInmuebles(false);
            beanUsuario.setPropSel(false);
        }
        beanUsuario.mostrar2 = true;
        beanUsuario.mostrar1 = false;

        return "menu";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        propSeleccionada = false;

        return "bienvenida";

    }

    public String irACrearCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        Integer idActual = 0;
        beanUsuario.setIdCondActual(idActual);

        beanUsuario.agregar = false;
        propSeleccionada = false;

        return "datoscondominio";

    }

    public void aceptarPropiedad(AjaxBehaviorEvent event) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //---------------------------------------------------------------------------
        // Verifica si selecciono alguna propiedad(diferente de la opcion Seleccione)
        //---------------------------------------------------------------------------
        String sel = nombPropiedad.getValue().toString();
        String cero = "0";

        if (sel.equals(cero)) {//selecciono la que aparece como Seleccione
            String propActual = null;
            Integer idActual = 0;
            beanUsuario.setPropiedadActual(propActual);
            beanUsuario.setIdPropActual(idActual);
            //    nombPropiedad = null;
            return;
        }
        //---------------------------------------------------------------------------
        // Busca el nombre y el id de la propiedad, lo pasa al bean
        //---------------------------------------------------------------------------
        String idCondominio = nombCondominio.getValue().toString();
        String idPropiedad = nombPropiedad.getValue().toString();

        String nombProp = null;
        Integer idPropA = null;

        for (int i = 0; i < usuario.condominios.size(); i++) {
            Condominio condi = usuario.condominios.get(i);
            String idCondi = condi.getId().toString();
            if (idCondi.equals(idCondominio)) {
                for (Iterator<Propiedad> it = condi.getPropiedades().iterator();
                        it.hasNext();) {
                    Propiedad propSelj = (Propiedad) it.next();
                    String idPropj = propSelj.getId().toString();
                    if (idPropj.equals(idPropiedad)) {
                        nombProp = propSelj.getNombPropiedad();
                        idPropA = propSelj.getId();
                    }
                }
            }
        }

        beanUsuario.setPropiedadActual(nombProp);
        beanUsuario.setIdPropActual(idPropA);

        propSeleccionada = true;
    }

    public void setNombCondominio(HtmlSelectOneMenu nombCondominio) {
        this.nombCondominio = nombCondominio;
    }

    public HtmlSelectOneMenu getNombCondominio() {
        return nombCondominio;
    }

    public void setNombPropiedad(HtmlSelectOneMenu nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public HtmlSelectOneMenu getNombPropiedad() {
        return nombPropiedad;
    }

    public void setListCond(List<Condominio> listCond) {
        this.listCond = listCond;
    }

    public List<Condominio> getListCond() {

        return listCond;
    }

    public void setListProp(List<Propiedad> listProp) {
        this.listProp = listProp;
    }

    public List<Propiedad> getListProp() {
        return listProp;
    }
}
