package util;

import configuracion.modelos.Tasa;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.hibernate.Transaction;

public class DaoQuery<T> implements GenericDaoQuery<T> {

    T entidad;

    public DaoQuery() {
    }

    public T buscarEntidad(String cod, String nombQuery) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", cod);
            resultado = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            entidad = (T) resultado.get(i);
            return entidad;
        }
        return entidad;
    }

    public T buscarEntidad(Integer cod, String nombQuery) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", cod);
            resultado = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            entidad = (T) resultado.get(i);
            return entidad;
        }
        return entidad;
    }

    public T buscarEntidadPorId(Integer id, String nombQuery) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id);
            resultado = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            entidad = (T) resultado.get(i);
            return entidad;
        }
        return entidad;
    }

    public T buscarEntidad(String nombQuery) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery);
            resultado = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            entidad = (T) resultado.get(i);
            return entidad;
        }
        return entidad;
    }

    public T buscarListaEntidadesPorCodigo(String cod, String nombQuery) throws HibernateException {

        Query q = null;
        Iterator pares = null;
        Session sesion = null;
        Transaction tx = null;
        List<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", cod);
            pares = q.list().iterator();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        while (pares.hasNext()) {
            Object[] par = (Object[]) pares.next();
            lista.add((T) par[1]);
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaEntidadesPeriodoActual(Integer id, Calendar periodo, String nombQuery) throws HibernateException {

        Query q = null;
        Iterator pares = null;
        Session sesion = null;
        Transaction tx = null;
        List<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id).setParameter("periodo", periodo);
            pares = q.list().iterator();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        while (pares.hasNext()) {
            Object[] par = (Object[]) pares.next();
            lista.add((T) par[1]);
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaEntidadesPorId(Integer id, String nombQuery) throws HibernateException {

        Query q = null;
        Iterator pares = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id);
            pares = q.list().iterator();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        while (pares.hasNext()) {
            Object[] par = (Object[]) pares.next();
            lista.add((T) par[1]);
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaFetchPorId(Integer id, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaFetchPorId(Integer id, Calendar periodo, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id).setParameter("periodo", periodo);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaEntidadesPorIdRango(Integer id, Calendar desde, Calendar hasta, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", id).setParameter("desde", desde).setParameter("hasta", hasta);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarEntidadExiste(String cod, String nombQuery) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Boolean existe = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", cod);
            resultado = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            existe = true;
            return entidad = (T) existe;
        }
        existe = false;
        return entidad = (T) existe;
    }

    public T buscarAdminExiste(String numSusc, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;
        Iterator pares = null;
        Boolean existe = null;
        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("sus", numSusc).setParameter("Admin", "Admin");
            pares = q.list().iterator();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        while (pares.hasNext()) {
            existe = true;
            return entidad = (T) existe;
        }
        existe = false;
        return entidad = (T) existe;
    }

    public void almacenarOModificarEntidad(T entidad) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(entidad);
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }
    }

    public Integer almacenarEntidad(T persistentClass) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;
        Integer idEntidad;
        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            idEntidad = (Integer) sesion.save(persistentClass);
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }
        return idEntidad;
    }

    public void almacenarListaEntidades(Collection<T> lista) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;
        T elemento;
        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            for (Iterator<T> it = lista.iterator();
                    it.hasNext();) {
                elemento = (T) it.next();
                sesion.saveOrUpdate(elemento);
            }
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }
    }

    public void eliminarEntidad(T persistentClass) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.delete(persistentClass);
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }
    }

    public T buscarListaEntidades(String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaEntidadesRango(Calendar desde, Calendar hasta, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("desde", desde).setParameter("hasta", hasta);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaFetchPorDia(String dia, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("dia", dia);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }

    public T buscarListaEntidadesPorCedRif(String cedRif, String nombQuery) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;

        Collection<T> lista = new ArrayList<T>();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = (Query) sesion.getNamedQuery(nombQuery).setParameter("codigo", cedRif);
            lista = q.list();
            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            sesion.clear();
            sesion.close();
        }

        entidad = (T) lista;
        return entidad;
    }
   
   public String buscarTasaPeriodo() throws ParseException {

        Collection<Tasa> listaTasas = new ArrayList<>();
        listaTasas.clear();
        Float tasaPeriodo = 0.0f;
        String mensaje;

        Integer idCondominio;
        String tasaPeriodoStr = null;
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        idCondominio = usuarioActual.getIdCondActual();
        String periodoRep = usuarioActual.getPeriodoRep();

        String nombQuery = "buscarTasasPorIdCondominio";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listaTasas = dataAcces.buscarListaEntidadesPorId(idCondominio, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return tasaPeriodoStr;
        }

        if (listaTasas.isEmpty()) {
            return tasaPeriodoStr;
        }

        if ((periodoRep.equals(" "))) {
            return tasaPeriodoStr;
        }
        String stringPeriodo = "01-" + periodoRep;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String calStr = CalendarAString(cal);
        String tPeriodoStr;

        for (Iterator<Tasa> it21 = listaTasas.iterator();
                it21.hasNext();) {
            Tasa t = (Tasa) it21.next();
            tPeriodoStr = CalendarAString(t.getPeriodoTasa());
            if (calStr.equals(tPeriodoStr)) {
                tasaPeriodo = t.getMtoTasa();
                break;
            }
        }
        if (!(tasaPeriodo.equals(0.0f))) {
            tasaPeriodoStr = String.valueOf(tasaPeriodo);
        }

        return tasaPeriodoStr;
    }

    public String CalendarAString(Calendar fecha) {

        Calendar fechaFac = fecha;
        String dia;
        String mes;
        int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
        int mesFac = fechaFac.get(Calendar.MONTH) + 1;
        int añoFac = fechaFac.get(Calendar.YEAR);

        if (diaFac < 10) {
            dia = "0" + diaFac;
        } else {
            dia = String.valueOf(diaFac);
        }

        if (mesFac < 10) {
            mes = "0" + mesFac;
        } else {
            mes = String.valueOf(mesFac);
        }

        String stringFecha = dia + "-" + mes + "-" + añoFac;
        return stringFecha;
    }

}
