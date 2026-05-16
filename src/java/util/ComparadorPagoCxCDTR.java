package util;

import informes.modelos.PagoCxCDTR;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorPagoCxCDTR implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoCxCDTR c1 = (PagoCxCDTR) o1;
        PagoCxCDTR c2 = (PagoCxCDTR) o2;
        return c1.getNombPropiedad().compareTo(c2.getNombPropiedad());

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
 
