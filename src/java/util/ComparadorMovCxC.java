package util;

import java.util.Comparator;
import movimientos.modelos.MovimientoCxCDT;
/**
 *
 * @author jesus
 */
public class ComparadorMovCxC implements Comparator {

    public int compare(Object o1, Object o2) {
        MovimientoCxCDT c1 = (MovimientoCxCDT) o1;
        MovimientoCxCDT c2 = (MovimientoCxCDT) o2;
        return c1.getCalFecMovCxC().compareTo(c2.getCalFecMovCxC());

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
 
