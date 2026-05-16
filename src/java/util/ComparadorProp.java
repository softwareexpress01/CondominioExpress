/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Propiedad;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorProp implements Comparator {

    public int compare(Object o1, Object o2) {
        Propiedad c1 = (Propiedad) o1;
        Propiedad c2 = (Propiedad) o2;
        return c1.getNombPropiedad().compareTo(c2.getNombPropiedad());

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
 
