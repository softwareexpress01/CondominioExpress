package util;


import informes.modelos.CarteleraDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCarDT implements Comparator {

    public int compare(Object o1, Object o2) {
        CarteleraDT c1 = (CarteleraDT) o1;
        CarteleraDT c2 = (CarteleraDT) o2;
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
 
