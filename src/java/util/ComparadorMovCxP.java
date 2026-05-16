package util;

import java.util.Comparator;
import informes.modelos.MovimientoCxPDT;
/**
 *
 * @author jesus
 */
public class ComparadorMovCxP implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        MovimientoCxPDT c1 = (MovimientoCxPDT) o1;
        MovimientoCxPDT c2 = (MovimientoCxPDT) o2;
        return c1.getCalFecMovCxP().compareTo(c2.getCalFecMovCxP());

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
 
