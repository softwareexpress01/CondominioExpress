package util;

import java.util.Comparator;
import movimientos.modelos.FacturaCxCDT;
/**
 *
 * @author jesus
 */
public class ComparadorCxC implements Comparator {

    public int compare(Object o1, Object o2) {
        FacturaCxCDT c1 = (FacturaCxCDT) o1;
        FacturaCxCDT c2 = (FacturaCxCDT) o2;
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
 
