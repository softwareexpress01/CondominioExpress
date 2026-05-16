package util;

import java.util.Comparator;
import movimientos.modelos.PagoCxC;
/**
 *
 * @author jesus
 */
public class ComparadorPagoCxC implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoCxC c1 = (PagoCxC) o1;
        PagoCxC c2 = (PagoCxC) o2;
        return c1.getFecDocPagoCxC().compareTo(c2.getFecDocPagoCxC());

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
 
