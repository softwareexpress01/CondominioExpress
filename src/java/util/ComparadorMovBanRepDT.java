package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import informes.modelos.MovBanRepDT;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorMovBanRepDT implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        MovBanRepDT c1 = (MovBanRepDT) o1;
        MovBanRepDT c2 = (MovBanRepDT) o2;
        return c1.getFecMovBan().compareTo(c2.getFecMovBan());

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
 
