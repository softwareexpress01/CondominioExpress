package util;

import java.util.Comparator;
import movimientos.modelos.NotaCredDebCxC;

/**
 *
 * @author jesus
 */
public class ComparadorNCD implements Comparator {

    public int compare(Object o1, Object o2) {
        NotaCredDebCxC c1 = (NotaCredDebCxC) o1;
        NotaCredDebCxC c2 = (NotaCredDebCxC) o2;
        return c1.getNumNotaCredDebCxC().compareTo(c2.getNumNotaCredDebCxC());

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
 
