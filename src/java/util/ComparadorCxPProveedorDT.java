/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import informes.modelos.CxPProveedorDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCxPProveedorDT implements Comparator {

    public int compare(Object o1, Object o2) {
        CxPProveedorDT c1 = (CxPProveedorDT) o1;
        CxPProveedorDT c2 = (CxPProveedorDT) o2;
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
 
