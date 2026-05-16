package saas.backing;

import saas.modelos.Suscripcion;
import saas.modelos.PagoMensual;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.PagoFacFonRes;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author Frank Rojas H.
 */
public class ElimConfSusCtrl extends DaoCrud<Suscripcion, PagoFacFonRes, Integer> implements Serializable {

    public ElimConfSusCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.revertir = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        return "regresar";

    }

    public void revertirConfirmacion() throws ValidatorException, ParseException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Suscripcion suscripcion = (Suscripcion) ctx.getExternalContext().getSessionMap().get("Suscripcion");

        try {
            almacenarOModificarEntidad(suscripcion);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones (SUSCRIPCION).por favor, intente mas tarde"));
            return;
        }

        eliminarPagoSuscripcion(suscripcion);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Confirmación de la(s) Suscripcion(es) seleccionada(s) ha(n) sido Revertida(s)"));



        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Suscripcion");

        return;

    }

    public void eliminarPagoSuscripcion(Suscripcion sus) throws ParseException {

        
        Integer idSusc = sus.getId();
        Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensualesConfirmadosPorIdSusc";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidadesPorId(idSusc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {

            PagoMensual pm = (PagoMensual) it.next();
            if (sus.getRefSolSuscripcion().equals(pm.getRefPagoMensual())) {
                eliminarPago(pm);
                break;
            }
        }
    }

    public void eliminarPago(PagoMensual pm) {

        DaoQuery<PagoMensual> dataAcces = new DaoQuery<PagoMensual>();
        try {
            dataAcces.eliminarEntidad(pm);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }
}