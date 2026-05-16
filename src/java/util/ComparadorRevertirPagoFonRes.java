package util;

import java.util.Comparator;
import movimientos.modelos.PagoFonRes;
import movimientos.modelos.RevertirPagoFonResDT;
/**
 *
 * @author jesus
 */
public class ComparadorRevertirPagoFonRes implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoFonRes c1 = (PagoFonRes) o1;
        PagoFonRes c2 = (PagoFonRes) o2;
        return c1.getFecDocPagoFonRes().compareTo(c2.getFecDocPagoFonRes());

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
 
