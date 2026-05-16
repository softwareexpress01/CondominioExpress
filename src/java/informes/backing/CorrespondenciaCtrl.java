/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.ParseException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author Jesus
 */
public class CorrespondenciaCtrl implements Serializable {

    private String idInmueble;
    private List listaInmuebles = new ArrayList();
    private BeanUsuario beanUsuario;

    public CorrespondenciaCtrl() {
    }
    
    public List getInmueblesPorCondominio() {

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

        listaInmuebles.add(new SelectItem(new Integer(0), "Todos"));
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }
        return listaInmuebles;

    }

    public String enviarCorrespondencia() throws ParseException {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        FacesContext ctx = FacesContext.getCurrentInstance();

        if (idInmueble.equals("0")) { //Significa que los selecciono a todos
            beanUsuario.setDeshabilitar(Boolean.TRUE);
            beanUsuario.setEmailPropInmueble(" ");
            ctx.getExternalContext().getSessionMap().put("inmueble", idInmueble);
            return "email";
        }

        Inmueble inm = buscarInmueble(Integer.parseInt(idInmueble));
        beanUsuario.setEmailPropInmueble(inm.getEmailPropInmueble());
        //---------------------------------------------------------------------------------------------------------
        //Guarda el inmueble para pasarlos al caso de uso que emite el correo 
        //---------------------------------------------------------------------------------------------------------

        ctx.getExternalContext().getSessionMap().put("inmueble", idInmueble);

        return "email";
    }

    public Inmueble buscarInmueble(Integer idInm) throws ValidatorException {

        Inmueble inm;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inm = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return inm;
    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }
}
