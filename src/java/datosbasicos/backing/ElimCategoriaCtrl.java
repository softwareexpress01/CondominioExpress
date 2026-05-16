package datosbasicos.backing;

import datosbasicos.modelos.Categoria;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.Iterator;
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
public class ElimCategoriaCtrl extends DaoCrud<Categoria, Condominio, Integer> implements Serializable {

    public ElimCategoriaCtrl() {
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

    public void eliminarCategoria() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Categoria catSel = (Categoria) ctx.getExternalContext().getSessionMap().get("Categoria");

        Categoria categoria = buscarCategoriaConceptos(catSel.getId());
        categoria.setEdoCategoria("INA");

        for (Iterator<Concepto> it = categoria.getConceptos().iterator();
                it.hasNext();) {
            Concepto con = (Concepto) it.next();
            con.setEdoConcepto("INA");
        }

        try {
            almacenarOModificarEntidad(categoria);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Categoria " + catSel.getNombCategoria() + " ha sido eliminada satisfactoriamente!"));
        return;

    }

    public Categoria buscarCategoriaConceptos(Integer idCat) {

        Categoria cat = null;

        String nombQuery = "buscarCategoriaFetchConceptos";
        DaoQuery<Categoria> dataAcces = new DaoQuery<Categoria>();
        try {
            cat = dataAcces.buscarEntidadPorId(idCat, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return cat;
    }
}
