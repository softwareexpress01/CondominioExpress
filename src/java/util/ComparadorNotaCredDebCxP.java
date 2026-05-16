package util;

import java.util.Comparator;
import movimientos.modelos.NotaCredDebCxP;

/**
 *
 * @author jesus
 */
public class ComparadorNotaCredDebCxP implements Comparator {

    public int compare(Object o1, Object o2) {
        NotaCredDebCxP c1 = (NotaCredDebCxP) o1;
        NotaCredDebCxP c2 = (NotaCredDebCxP) o2;
        return c1.getNumNotaCredDebCxP().compareTo(c2.getNumNotaCredDebCxP());

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
 
