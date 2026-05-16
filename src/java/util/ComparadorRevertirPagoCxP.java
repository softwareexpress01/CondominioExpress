package util;

import java.util.Comparator;
import movimientos.modelos.PagoCxP;
/**
 *
 * @author jesus
 */
public class ComparadorRevertirPagoCxP implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoCxP c1 = (PagoCxP) o1;
        PagoCxP c2 = (PagoCxP) o2;
        return c1.getFecDocPagoCxP().compareTo(c2.getFecDocPagoCxP());

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
 
