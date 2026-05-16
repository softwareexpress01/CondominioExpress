package util;

import informes.modelos.PagoCxCDTR;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorPagoCxCDTR1 implements Comparator {

    public int compare(Object o1, Object o2) {

        PagoCxCDTR c1 = (PagoCxCDTR) o1;
        PagoCxCDTR c2 = (PagoCxCDTR) o2;

        int resultadoProp = c1.getNombPropiedad().compareTo(c2.getNombPropiedad());
        if (resultadoProp != 0) {
            return resultadoProp; // Si son diferentes, ordena por propiedad
        } else {
            return c1.getCodInmueble().compareTo(c2.getCodInmueble());
        }
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
