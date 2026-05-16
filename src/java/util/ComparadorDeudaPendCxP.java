/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import movimientos.modelos.DeudaPendCxP;
import java.util.Comparator;

/**
 *
 * @author Frank Rojas H
 */
public class ComparadorDeudaPendCxP implements Comparator {

    public int compare(Object o1, Object o2) {
        DeudaPendCxP c1 = (DeudaPendCxP) o1;
        DeudaPendCxP c2 = (DeudaPendCxP) o2;
        return c1.getFecDeudaPendCxP().compareTo(c2.getFecDeudaPendCxP());

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
 
