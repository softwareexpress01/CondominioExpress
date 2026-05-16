/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import acceso.modelos.CodigoAcceso;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import util.DaoQuery;
import util.Excepcion;

/**
 *
 * @author jesus
 */
public class ConExpGenCodigoAccesoCtrl {

    private Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
    private Collection<Inmueble> listaTotalInm = new ArrayList<Inmueble>();
    private Collection<Condominio> listaCond = new ArrayList<Condominio>();
    private Collection<Inmueble> lista = new ArrayList<Inmueble>();
    private Collection<Inmueble> listaInmProp = new ArrayList<Inmueble>();
    private Condominio cond;

    public void generarCodigosAcceso() throws Excepcion {
       
        listaTotalInm.clear();
        listaCond.clear();
        Integer idcodigoAcceso;
        CodigoAcceso codAccesoObj;

        buscarCondominiosPropiedades();
        
        for (Iterator<Condominio> it1 = listaCond.iterator();
                it1.hasNext();) {
            cond = (Condominio) it1.next();
            listaInm.clear();
            listaInm = buscarInmueblesCondominio(cond);

            for (Iterator<Inmueble> it2 = listaInm.iterator();
                    it2.hasNext();) {
                Inmueble inm = (Inmueble) it2.next();
                // generar y registrar el codigo de acceso y establecer vinculo con el inmueble
                idcodigoAcceso = asignarCodigoAcceso(cond);

                if (idcodigoAcceso == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones(Codigo Acceso)...por favor, intente mas tarde"));
                    return;
                }
                codAccesoObj = buscarCodigoAcceso(idcodigoAcceso);
                inm.setCodigoAcceso(codAccesoObj);
                listaTotalInm.add(inm);
            }
        }
        actualizarListaInmuebles(listaTotalInm);
    }

    public Collection buscarCondominiosPropiedades() {

        String nombQuery = "buscarCondominiosFetchPropiedades";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaCond = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return listaCond;
    }

    public Collection buscarInmueblesCondominio(Condominio condominio) {

        lista.clear();
        listaInmProp.clear();
        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                listaInmProp = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            lista.addAll(listaInmProp);
            listaInmProp.clear();
        }
        return lista;
    }

    public Integer asignarCodigoAcceso(Condominio condominio) {

        Boolean codExiste = true;
        Integer cont;
        Integer idAcceso = null;
        //Generar Codigo de acceso:
        while (codExiste) {
            Random r = new Random();
            Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
            Long seed = fecCal.getTimeInMillis();
            Integer ent = Long.valueOf(seed).intValue();
            if (ent < 0) {
                ent = ent * -1;
            }
            cont = r.nextInt(ent);
            idAcceso = cont;

            CodigoAcceso codAccesoObj = new CodigoAcceso();     // Verificar si id ya existe en la tabla de codigos
            codAccesoObj = buscarCodigoAcceso(idAcceso);

            if (codAccesoObj == null) {                         // codigo de acceso no existe
                codExiste = false;
            }
        }
        // Almacenar en tabla

        CodigoAcceso codigoAccesoNuevo = new CodigoAcceso();

        codigoAccesoNuevo.setId(idAcceso);

        codigoAccesoNuevo.setCondominio(condominio);

        DaoQuery<CodigoAcceso> dataAcces1 = new DaoQuery<CodigoAcceso>();
        Integer idCodigoAccesoNuevo;
        try {
            idCodigoAccesoNuevo = dataAcces1.almacenarEntidad(codigoAccesoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones.(Almacenar codAcc)..por favor, intente mas tarde"));

            return null;
        }

        return idCodigoAccesoNuevo;
    }

    public CodigoAcceso buscarCodigoAcceso(Integer idAcceso) {

        String nombQuery = "buscarCodigoAccesoPorId";

        DaoQuery<CodigoAcceso> dataAcces = new DaoQuery<CodigoAcceso>();
        CodigoAcceso codAccesoObj = new CodigoAcceso();

        try {
            codAccesoObj = dataAcces.buscarEntidad(idAcceso, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(buscar codAcceso)...por favor, intente mas tarde"));

        }
        return codAccesoObj;
    }

    public void actualizarListaInmuebles(Collection inmuebles) {

        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();

        for (Iterator<Inmueble> it = inmuebles.iterator(); 
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            try {
                dataAcces.almacenarOModificarEntidad(inm);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            System.out.println("Se actualizo el inmueble:" + inm.getId() + "y se le creo la clave:" + inm.getCodigoAcceso().getId());
        }
        return;
    }
}
