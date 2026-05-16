package util;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import org.hibernate.HibernateException;

/**
 * @author Jesus A. Rojas H.
 * @version 1.0
 * @author-mail softwareexpress00@gmail.com
 * @date 28-10-2011
 * Descripcion de los Metodos:
 * -------------------------------------------------------------------------------------------------------------
 * buscarEntidad(String cod, String nombQuery):  encuentra el objeto en la bd que contenga el codigo dado.
 *                                               Ejm, buscar en la table de suscripcion un objeto  que tenga 
 *                                               el numSus dado.
 * --------------------------------------------------------------------------------------------------------------
 * buscarEntidadPorId(Integer id, String nombQuery): encuentra el objeto en la bd con su id, devolviendolo 
 *                                                   con sus objetos asociados. Ejm. Para el id de un condominio, 
 *                                                   devuelve el condominio con sus propiedades(Dependiendo del
 *                                                   query, podria solo devolver el objeto identificado)
 * ---------------------------------------------------------------------------------------------------------------
 * buscarListaEntidadesPorCodigo(): lista las entidades que pertenezcan al codigo dado.(ejm. usuario-condominios,
 *                                  para un usuario dado -cod- devolveria un usuario con sus condominios)
 * ---------------------------------------------------------------------------------------------------------------
 * buscarListaEntidadesPorId(): lista las entidades asociadas a la que se da el id(ejm. las propiedades para un
 *                              condominio dado (id))
 * ----------------------------------------------------------------------------------------------------------------
 * buscarEntidadExiste: determina si algun objeto de la bd contiene el codigo dado, ejm. en la tabla de usuarios 
 *                      buscar el codUsuario.
 * ----------------------------------------------------------------------------------------------------------------
 * buscarAdminExiste: determina si el administrador existe. Busca en la tabla de usuarios si existe un usuario con 
 *                    perfil admin.
 * ----------------------------------------------------------------------------------------------------------------
 * nombQuery : es el nombre del query a utilizar, esta ubicado en el mapping de la clase usuario.
 * 
 * T: este parametro lo proporciona el backing que usa la interfaz.
---------------------------------------------------------------------------------------------------------------- */
public interface GenericDaoQuery<T> {

    public T buscarEntidad(String cod, String nombQuery);

    public T buscarEntidad(Integer cod, String nombQuery);

    public T buscarEntidadPorId(Integer id, String nombQuery);

    public T buscarListaEntidadesPorCodigo(String cod, String nombQuery);

    public T buscarListaEntidadesPeriodoActual(Integer id, Calendar periodo, String nombQuery);

    public T buscarListaEntidadesPorId(Integer id, String nombQuery);

    public T buscarListaEntidadesPorIdRango(Integer id, Calendar desde, Calendar hasta, String nombQuery);

    public T buscarEntidadExiste(String cod, String nombQuery);

    public T buscarAdminExiste(String numSus, String nombQuery);

    public void almacenarOModificarEntidad(T entidad);

    public Serializable almacenarEntidad(T persistentClass);

    public void almacenarListaEntidades(Collection<T> lista);

    public T buscarListaFetchPorId(Integer cod, String nombQuery);

    public T buscarListaFetchPorId(Integer id, Calendar periodo, String nombQuery);

    public void eliminarEntidad(T entidad);

    public T buscarEntidad(String nombQuery);

    public T buscarListaEntidades(String nombQuery);

    public T buscarListaEntidadesRango(Calendar desde, Calendar hasta, String nombQuery);

    public T buscarListaFetchPorDia(String dia, String nombQuery);

    public T buscarListaEntidadesPorCedRif(String cedRif, String nombQuery);

   
}
