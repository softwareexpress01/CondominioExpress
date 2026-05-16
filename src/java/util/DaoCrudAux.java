package util;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DaoCrudAux<T> {

    T entidad;

    public DaoCrudAux() {
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
    public Serializable almacenarEntidad(T persistentClass) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;
        Serializable idEntidad;
        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            idEntidad = sesion.save(persistentClass);
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
}