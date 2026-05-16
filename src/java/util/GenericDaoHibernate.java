/**
 * @author Jesus A. Rojas H.
 * @version 1.0
 * @author-mail softwareexpress00@gmail.com
 * @date 28-10-2011
 */
package util;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Propiedad;
import saas.modelos.Suscripcion;
import datosbasicos.modelos.Usuario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.hibernate.*;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

public class GenericDaoHibernate {

    public GenericDaoHibernate() {
    }

    public void almacenaEntidad(Suscripcion sus) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(sus);
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
    }

    public void almacenarCondominio(Usuario usuario) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(usuario);
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
    }

    public void almacenarPropiedadCondominio(Condominio condominio) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(condominio);
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
    }

    public void modificarCondominio(Condominio condominio) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(condominio);
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
    }

    public void modificarPropiedad(Propiedad propiedad) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(propiedad);
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
    }

    public void eliminarCondominio(Usuario usuario, Condominio condSel) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(usuario);
            sesion.delete(condSel);
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
    }

    public void eliminarPropiedadCondominio(Condominio cond) throws HibernateException {

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            sesion.saveOrUpdate(cond);
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
    }

    public Suscripcion buscarSuscripcion(String codigo) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Suscripcion susc = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Suscripcion u where u.numSuscripcion = :codigo").setParameter("codigo", codigo);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            susc = (Suscripcion) resultado.get(i);
            return susc;
        }
        return susc;
    }

    public Boolean buscarUsuario(String codUsuario) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Usuario u where u.codUsuario = :codigo").setParameter("codigo", codUsuario);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            Usuario u = (Usuario) resultado.get(i);
            return true;
        }
        return false;
    }

    public Usuario buscarUsuarioLogin(String codUsuario) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Usuario u = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Usuario u where u.codUsuario = :codigo").setParameter("codigo", codUsuario);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            u = (Usuario) resultado.get(i);
            return u;
        }
        return u;
    }

    public List buscarCondominiosPorUsuario(String codUsuario) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Usuario usuario = null;
        List listCond = new ArrayList();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Usuario u left join fetch u.condominios where u.codUsuario = :codigo").setParameter("codigo", codUsuario);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            usuario = (Usuario) resultado.get(i);
        }
        listCond = usuario.getCondominios();

        return listCond;
    }

    public Usuario buscarUsuariosCondominios(String codUsuario) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Usuario usuario = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Usuario u left join fetch u.condominios c left join fetch c.propiedades p where u.codUsuario = :codigo").setParameter("codigo", codUsuario);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            usuario = (Usuario) resultado.get(i);
        }

        return usuario;
    }

    public Condominio buscarPropiedadesCondominio(Integer idCond) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Condominio condominio = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Condominio u left join fetch u.propiedades c  where u.id = :codigo").setParameter("codigo", idCond);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            condominio = (Condominio) resultado.get(i);
        }

        return condominio;
    }

    public Boolean administradorExiste(String numSusc) throws HibernateException {

        Query q = null;
        Session sesion = null;
        Transaction tx = null;
        Iterator pares = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Suscripcion s join s.usuarios u where s.numSuscripcion =:sus and u.perUsuario =:Admin ").setParameter("sus", numSusc).setParameter("Admin", "Admin");
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
            sesion.close();
        }

        while (pares.hasNext()) {
            Object[] par = (Object[]) pares.next();
            Suscripcion suscripcion = (Suscripcion) par[0];
            Usuario usuario = (Usuario) par[1];
            return true;
        }
        return false;
    }

    public Suscripcion buscarEntidadPorId(Integer id) throws HibernateException {

        Suscripcion susc = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            susc = (Suscripcion) sesion.get(Suscripcion.class, id);
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
        return susc;
    }

    public Condominio buscarCondominioPorId(Integer id) throws HibernateException {

        Condominio cond = null;
        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            cond = (Condominio) sesion.get(Condominio.class, id);
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
        return cond;
    }

    public Collection buscarPropiedadesPorCondominio(Integer idCond) throws HibernateException {

        Query q = null;
        List resultado = null;
        Session sesion = null;
        Transaction tx = null;
        Condominio condominio = null;
        Collection listProp = new ArrayList();

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();
            q = sesion.createQuery("from Condominio u left join fetch u.propiedades where u.id = :codigo").setParameter("codigo", idCond);
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
            sesion.close();
        }

        for (int i = 0; i < resultado.size(); i++) {
            condominio = (Condominio) resultado.get(i);
        }
        listProp = condominio.getPropiedades();

        return listProp;
    }
}