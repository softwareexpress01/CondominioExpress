/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class DaoCrud<T, T1, ID extends Serializable>
        implements GenericDaoCrud<T, T1, ID> {

    private Class<T> persistentClass;
    private Class<T1> persistentClass1;
    private Session session;

    public DaoCrud() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.persistentClass1 = (Class<T1>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public void setSession(Session s) {
        this.session = s;
    }

    protected Session getSession() {
        if (session == null) {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
        }
        return session;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;

    }

    public Class<T1> getPersistentClass1() {
        return persistentClass1;

    }

    @SuppressWarnings("unchecked")
    public void almacenarOModificarEntidad(T persistentClass) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(persistentClass);
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

    @SuppressWarnings("unchecked")
    public T1 encontrarEntidadPorId(ID id) {

        T1 entidad = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            entidad = (T1) sesion.get(getPersistentClass1(), id);
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

            sesion.close();
        }
        return entidad;
    }
    //---------------------------------------------------------------------------------------
    // Para asociaciones muchos a muchos como Usuario-Condominio
    //---------------------------------------------------------------------------------------

    public void eliminarEntidad(T persistentClass, T1 persistentClass1) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(persistentClass);
            sesion.delete(persistentClass1);
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

    //---------------------------------------------------------------------------------------
    // Para asociaciones uno a muchos como Condomino-Propiedad
    //---------------------------------------------------------------------------------------
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

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }
}
