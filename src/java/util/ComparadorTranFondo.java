/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Fondo;
import java.util.Comparator;
import movimientos.modelos.TranFondo;
/**
 *
 * @author jesus
 */
public class ComparadorTranFondo implements Comparator {

    public int compare(Object o1, Object o2) {
        TranFondo c1 = (TranFondo) o1;
        TranFondo c2 = (TranFondo) o2;
        return c1.getFecTranFondo().compareTo(c2.getFecTranFondo());

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
 
