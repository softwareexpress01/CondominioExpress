/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Comparator;
import movimientos.modelos.MovBancario;

/**
 *
 * @author jesus
 */
public class ComparadorMovBancario implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        MovBancario c1 = (MovBancario) o1;
        MovBancario c2 = (MovBancario) o2;
        return c1.getFecMovBancario().compareTo(c2.getFecMovBancario());

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
 
