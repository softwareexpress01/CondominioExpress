/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import configuracion.modelos.Tasa;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorTasa implements Comparator {

    public int compare(Object o1, Object o2) {
        Tasa c1 = (Tasa) o1;
        Tasa c2 = (Tasa) o2;
        return c1.getPeriodoTasa().compareTo(c2.getPeriodoTasa());

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
 

