package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import informes.modelos.MovDelMesDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorMovDelMesDT implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        MovDelMesDT c1 = (MovDelMesDT) o1;
        MovDelMesDT c2 = (MovDelMesDT) o2;
        return c1.getDescMov().compareTo(c2.getDescMov());

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
 
