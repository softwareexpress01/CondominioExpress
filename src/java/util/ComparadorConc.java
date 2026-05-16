package util;

import datosbasicos.modelos.Concepto;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorConc implements Comparator {

    public int compare(Object o1, Object o2) {
        Concepto c1 = (Concepto) o1;
        Concepto c2 = (Concepto) o2;
        return c1.getNombConcepto().compareTo(c2.getNombConcepto());

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
 
