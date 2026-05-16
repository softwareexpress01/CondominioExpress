package util;

import java.util.Comparator;
import movimientos.modelos.AjustesCond;

/**
 *
 * @author jesus
 */
public class ComparadorAjustesCond implements Comparator {

    public int compare(Object o1, Object o2) {
        AjustesCond c1 = (AjustesCond) o1;
        AjustesCond c2 = (AjustesCond) o2;
        return c1.getFecMovAjustesCond().compareTo(c2.getFecMovAjustesCond());

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
 
