/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import movimientos.modelos.CuotaEsp;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCuotaEsp implements Comparator {

    public int compare(Object o1, Object o2) {
        CuotaEsp c1 = (CuotaEsp) o1;
        CuotaEsp c2 = (CuotaEsp) o2;
        return c1.getFecCuotaEsp().compareTo(c2.getFecCuotaEsp());

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
 
