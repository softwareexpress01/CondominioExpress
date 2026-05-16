/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Condominio;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class Comparador implements Comparator {

    public int compare(Object o1, Object o2) {
        Condominio c1 = (Condominio) o1;
        Condominio c2 = (Condominio) o2;
        return c1.getNombCondominio().compareTo(c2.getNombCondominio());

    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
}
 
