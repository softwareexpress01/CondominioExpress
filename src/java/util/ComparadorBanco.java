/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Banco;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorBanco implements Comparator {

    public int compare(Object o1, Object o2) {
        Banco c1 = (Banco) o1;
        Banco c2 = (Banco) o2;
        return c1.getNombBanco().compareTo(c2.getNombBanco());

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
 
