/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Proveedor;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorProv implements Comparator {

    public int compare(Object o1, Object o2) {
        Proveedor c1 = (Proveedor) o1;
        Proveedor c2 = (Proveedor) o2;
        return c1.getNombProveedor().compareTo(c2.getNombProveedor());

    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
 
