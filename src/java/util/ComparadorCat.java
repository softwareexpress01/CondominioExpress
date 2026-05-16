/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Categoria;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorCat implements Comparator {

    public int compare(Object o1, Object o2) {
        Categoria c1 = (Categoria) o1;
        Categoria c2 = (Categoria) o2;
        return c1.getNombCategoria().compareTo(c2.getNombCategoria());

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
 
