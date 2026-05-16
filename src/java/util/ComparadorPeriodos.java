package util;

import informes.modelos.PeriodoDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorPeriodos implements Comparator {

    public int compare(Object o1, Object o2) {
        PeriodoDT c1 = (PeriodoDT) o1;
        PeriodoDT c2 = (PeriodoDT) o2;
        return c1.getNumPeriodo().compareTo(c2.getNumPeriodo());

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
 
