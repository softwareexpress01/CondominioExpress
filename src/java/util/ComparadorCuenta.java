/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCuenta implements Comparator {

    public int compare(Object o1, Object o2) {
        Cuenta c1 = (Cuenta) o1;
        Cuenta c2 = (Cuenta) o2;
        return c1.getNumCuenta().compareTo(c2.getNumCuenta());

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
 
