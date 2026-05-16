/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.CondominioDT;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimCondominioCtrl extends DaoCrud<Usuario, Condominio, Integer> implements Serializable {

    public ElimCondominioCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        return "regresar";

    }

    public void eliminarCondominio() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        CondominioDT condSel = (CondominioDT) ctx.getExternalContext().getSessionMap().get("CondominioDT");

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //valida que el condominio a eliminar no sea el condominio actual
        Integer idCondSel = condSel.getId();
        Integer idCondActual = beanUsuario.getIdCondActual();
        if (idCondSel.equals(idCondActual)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No esta permitida la eliminacion de un condominio actualmente activo..."));
            return;
        }

        // Con el id de condominio seleccionado buscamos el objeto persitido para cambiar su estado

        Condominio condominio = null;
        Integer idCond = condSel.getId();
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces2 = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces2.buscarEntidad(idCond, nombQuery);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //
       
        DaoQuery<Condominio> dataAcces3 = new DaoQuery<Condominio>();
        try {
            condominio.setEdoCondominio("INA");
            dataAcces3.almacenarOModificarEntidad(condominio);
            System.out.println("El condominio con id siguiente, ha sido desactivado" + ""+ condominio.getId());

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

       
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Condominio " + condSel.getNombCondominio() + " ha sido eliminado satisfactoriamente!"));

        beanUsuario.si = true;
        beanUsuario.no = true;

        return;
    }
}
