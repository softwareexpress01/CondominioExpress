package util;

import java.util.Comparator;
import movimientos.modelos.FacturaCxPDT;
/**
 *
 * @author jesus
 */
public class ComparadorPagoCxP implements Comparator {

    public int compare(Object o1, Object o2) {
        FacturaCxPDT c1 = (FacturaCxPDT) o1;
        FacturaCxPDT c2 = (FacturaCxPDT) o2;
        return c1.getCalFecFacCxC().compareTo(c2.getCalFecFacCxC());

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
 
