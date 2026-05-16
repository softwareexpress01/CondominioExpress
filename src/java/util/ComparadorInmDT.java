package util;


import informes.modelos.InmuebleDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorInmDT implements Comparator {

    public int compare(Object o1, Object o2) {
        InmuebleDT c1 = (InmuebleDT) o1;
        InmuebleDT c2 = (InmuebleDT) o2;
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
 
