/**
 *
 * @author jesus
 */

package util;

import java.util.Comparator;
import saas.modelos.PagoMensualDT;

public class ComparadorPagoMensualFecha implements Comparator {

    public int compare(Object o1, Object o2) {
        PagoMensualDT c1 = (PagoMensualDT) o1;
        PagoMensualDT c2 = (PagoMensualDT) o2;
        return c1.fecPagoMensual.compareTo(c2.fecPagoMensual);

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
 
