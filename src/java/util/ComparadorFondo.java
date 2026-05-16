/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Fondo;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorFondo implements Comparator {

    public int compare(Object o1, Object o2) {
        Fondo c1 = (Fondo) o1;
        Fondo c2 = (Fondo) o2;
        return c1.getTipoFondo().compareTo(c2.getTipoFondo());

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
 
