package util;

import java.util.Comparator;

import movimientos.modelos.PagoCxCDT;
/**
 *
 * @author jesus
 */
public class ComparadorPagoCxCDT implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoCxCDT c1 = (PagoCxCDT) o1;
        PagoCxCDT c2 = (PagoCxCDT) o2;
        return c1.getFecDocPagoCal().compareTo(c2.getFecDocPagoCal());

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