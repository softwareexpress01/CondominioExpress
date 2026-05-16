/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import informes.modelos.AvisoDeCobroDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorAvisoDeCobroDT implements Comparator {

    public int compare(Object o1, Object o2) {
        AvisoDeCobroDT c1 = (AvisoDeCobroDT) o1;
        AvisoDeCobroDT c2 = (AvisoDeCobroDT) o2;
        return c1.getCodInm().compareTo(c2.getCodInm());

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
 
