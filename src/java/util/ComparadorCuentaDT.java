/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import informes.modelos.CuentaDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCuentaDT implements Comparator {

    public int compare(Object o1, Object o2) {
        CuentaDT c1 = (CuentaDT) o1;
        CuentaDT c2 = (CuentaDT) o2;
        return c1.getNombreBanco().compareTo(c2.getNombreBanco());

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
 
