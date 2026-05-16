package util;

import java.util.Comparator;
import saas.modelos.Suscripcion;
/**
 *
 * @author jesus
 */
public class ComparadorSuscripcion implements Comparator {

    public int compare(Object o1, Object o2) {
        Suscripcion c1 = (Suscripcion) o1;
        Suscripcion c2 = (Suscripcion) o2;
        return c1.getFechaMovSol().compareTo(c2.getFechaMovSol());

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
 
