package util;

import datosbasicos.modelos.ConceptoDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorConDT implements Comparator {

    public int compare(Object o1, Object o2) {
        ConceptoDT c1 = (ConceptoDT) o1;
        ConceptoDT c2 = (ConceptoDT) o2;
        return c1.getNombCategoria().compareTo(c2.getNombCategoria());

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
 