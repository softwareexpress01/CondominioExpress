package util;

import datosbasicos.modelos.Inmueble;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorInm implements Comparator {

    public int compare(Object o1, Object o2) {
        Inmueble c1 = (Inmueble) o1;
        Inmueble c2 = (Inmueble) o2;
        return c1.getCodInmueble().compareTo(c2.getCodInmueble());

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
 
