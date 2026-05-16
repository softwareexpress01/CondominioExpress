package util;

import java.util.Comparator;
import movimientos.modelos.PagoFonResDT;
/**
 *
 * @author jesus
 */
public class ComparadorPagoFonRes implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoFonResDT c1 = (PagoFonResDT) o1;
        PagoFonResDT c2 = (PagoFonResDT) o2;
        return c1.getFecEmiPagoFonRes().compareTo(c2.getFecEmiPagoFonRes());

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
 
