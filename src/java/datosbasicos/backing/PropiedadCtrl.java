/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Propiedad;
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

public class PropiedadCtrl extends DaoCrud<Propiedad, Condominio, Integer> implements Serializable {

    private String nombPropiedad;
    private String numInmuebles;
    private Propiedad filaSeleccionada;

    public PropiedadCtrl() {
    }

    public void inicializarPanel() {

        this.setNombPropiedad(null);
        this.setNumInmuebles(null);

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

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (this.getNombPropiedad().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre de la propiedad...";
            return mensaje;
        }

        if (!val.esAlfaNum(this.getNombPropiedad())) {
            mensaje = "El nombre de la propiedad debe ser alfanumérico";
            return mensaje;
        }
        if (this.getNumInmuebles().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el numero de inmuebles de la propiedad...";
            return mensaje;
        }
        if (!val.esMonto(this.getNumInmuebles())) {
            mensaje = "El numero de inmuebles debe ser un numero entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }

        return mensaje;
    }

    public boolean esEntero(String numIn) {

        try {
            Integer entero = Integer.parseInt(numIn);
            if (entero > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public Collection getPropiedadesPorCondominio() {

        // Obtiene las propiedades para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Propiedad> listProp = new ArrayList<Propiedad>();
        String nombQuery = "buscarPropiedadesPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listProp = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaOrd = ordenarLista(listProp);// para ordenar la lista de propiedades

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Propiedad> listaDesOrdenada = new ArrayList<Propiedad>();

        for (Iterator<Propiedad> it = lista.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            listaDesOrdenada.add(prop);
        }
        Collections.sort(listaDesOrdenada, new ComparadorProp());

        Collection<Propiedad> coleccionOrdenada = new ArrayList<Propiedad>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Propiedad prop = listaDesOrdenada.get(i);
            coleccionOrdenada.add(prop);
        }
        return coleccionOrdenada;
    }

    public void editarPropiedadAction() {

        Propiedad propSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Propiedad" en el map #{Propiedad} 
        ctx.getExternalContext().getSessionMap().put("Propiedad", propSel);

        this.setNombPropiedad(propSel.getNombPropiedad());
        this.setNumInmuebles(propSel.getNumInmuebles());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(propSel.getNombPropiedad());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarPropiedad() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Propiedad propiedadNueva = new Propiedad();

        propiedadNueva.setNombPropiedad(nombPropiedad);
        propiedadNueva.setNumInmuebles(numInmuebles);

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
        propiedadNueva.setCondominio(condominio);
        try {
            almacenarOModificarEntidad(propiedadNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Propiedad " + propiedadNueva.getNombPropiedad() + " ha sido registrada satisfactoriamente!"));
        return;
    }

    public void modificarPropiedad() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        Propiedad propSel = (Propiedad) ctx.getExternalContext().getSessionMap().get("Propiedad");

        propSel.setNombPropiedad(nombPropiedad);
        propSel.setNumInmuebles(numInmuebles);

        try {
            almacenarOModificarEntidad(propSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Propiedad " + propSel.getNombPropiedad() + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public void setNombPropiedad(String nombPropiedad) {
        this.nombPropiedad = nombPropiedad;
    }

    public String getNombPropiedad() {
        return nombPropiedad;
    }

    public void setNumInmuebles(String numInmuebles) {
        this.numInmuebles = numInmuebles;
    }

    public String getNumInmuebles() {
        return numInmuebles;
    }

    public void setFilaSeleccionada(Propiedad filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public Propiedad getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
