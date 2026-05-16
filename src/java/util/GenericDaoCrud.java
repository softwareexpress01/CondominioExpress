/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;

public interface GenericDaoCrud<T, T1, ID extends Serializable> {

    public void almacenarOModificarEntidad(T entidad);

    public T1 encontrarEntidadPorId(ID id);

    public void eliminarEntidad(T entidad, T1 entidad1);

    public void eliminarEntidad(T entidad);

    void flush();

    void clear();
}