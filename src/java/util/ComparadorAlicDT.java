package util;


import informes.modelos.AlicuotaDT;
import informes.modelos.InmuebleDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorAlicDT implements Comparator {

    public int compare(Object o1, Object o2) {
        AlicuotaDT c1 = (AlicuotaDT) o1;
        AlicuotaDT c2 = (AlicuotaDT) o2;
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
 
