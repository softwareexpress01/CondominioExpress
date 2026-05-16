/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Comparator;
import movimientos.modelos.GastoComunOrd;

/**
 *
 * @author jesus
 */
public class ComparadorGCOrd implements Comparator {

    public int compare(Object o1, Object o2) {
        GastoComunOrd c1 = (GastoComunOrd) o1;
        GastoComunOrd c2 = (GastoComunOrd) o2;
        return c1.getFecGastoComunOrd().compareTo(c2.getFecGastoComunOrd());

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
 
