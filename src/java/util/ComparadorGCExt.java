/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Comparator;
import movimientos.modelos.GastoComunExt;

/**
 *
 * @author jesus
 */
public class ComparadorGCExt implements Comparator {

    public int compare(Object o1, Object o2) {
        GastoComunExt c1 = (GastoComunExt) o1;
        GastoComunExt c2 = (GastoComunExt) o2;
        return c1.getFecGastoComunExt().compareTo(c2.getFecGastoComunExt());

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
 
