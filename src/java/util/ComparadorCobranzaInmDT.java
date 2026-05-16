/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import informes.modelos.CobranzaDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCobranzaInmDT implements Comparator {

    public int compare(Object o1, Object o2) {
        CobranzaDT c1 = (CobranzaDT) o1;
        CobranzaDT c2 = (CobranzaDT) o2;
        return c1.getNombPropInmueble().compareTo(c2.getNombPropInmueble());

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
 
