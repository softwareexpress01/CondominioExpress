package util;

import movimientos.modelos.GastoPropiedad;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorGP implements Comparator {

    public int compare(Object o1, Object o2) {
        GastoPropiedad c1 = (GastoPropiedad) o1;
        GastoPropiedad c2 = (GastoPropiedad) o2;
        return c1.getFecGastoPropiedad().compareTo(c2.getFecGastoPropiedad());

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
 
