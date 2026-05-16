package util;

import datosbasicos.modelos.Usuario;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorUsu implements Comparator {

    public int compare(Object o1, Object o2) {
        Usuario c1 = (Usuario) o1;
        Usuario c2 = (Usuario) o2;
        return c1.getCodUsuario().compareTo(c2.getCodUsuario());

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
 
