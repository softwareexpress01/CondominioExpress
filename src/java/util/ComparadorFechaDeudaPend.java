/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Comparator;
import movimientos.modelos.DeudaPendCxC;

/**
 *
 * @author softw
 */
public class ComparadorFechaDeudaPend implements Comparator{
    
    
    @Override
    public int compare(Object o1, Object o2) {

        DeudaPendCxC c1 = (DeudaPendCxC) o1;
        DeudaPendCxC c2 = (DeudaPendCxC) o2;
       
        return c1.getFecDeudaPendCxC().compareTo(c2.getFecDeudaPendCxC());

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
