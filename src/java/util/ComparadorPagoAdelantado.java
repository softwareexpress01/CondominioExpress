/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import movimientos.modelos.PagoAdelantado;
import java.util.Comparator;

/**
 *
 * @author Frank Rojas H
 */
public class ComparadorPagoAdelantado implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoAdelantado c1 = (PagoAdelantado) o1;
        PagoAdelantado c2 = (PagoAdelantado) o2;
        return c1.getFecPagoAdelantado().compareTo(c2.getFecPagoAdelantado());

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
 
