package util;

import java.util.Comparator;
import movimientos.modelos.GastoNoComun;

/**
 *
 * @author jesus
 */
public class ComparadorGNC implements Comparator {

    public int compare(Object o1, Object o2) {
        GastoNoComun c1 = (GastoNoComun) o1;
        GastoNoComun c2 = (GastoNoComun) o2;
        return c1.getFecGastoNoComun().compareTo(c2.getFecGastoNoComun());

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
 
