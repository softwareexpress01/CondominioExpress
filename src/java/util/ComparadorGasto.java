package util;

import informes.modelos.GastoDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorGasto implements Comparator {

    public int compare(Object o1, Object o2) {
        GastoDT c1 = (GastoDT) o1;
        GastoDT c2 = (GastoDT) o2;
        return c1.getDesCategoria().compareTo(c2.getDesCategoria());

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
 
