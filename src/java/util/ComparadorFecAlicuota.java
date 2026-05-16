/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


import java.util.Comparator;
import movimientos.modelos.Alicuota;


/**
 *
 * @author softw
 */

  
public class ComparadorFecAlicuota implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        Alicuota c1 = (Alicuota) o1;
        Alicuota c2 = (Alicuota) o2;
       
        return c1.getFecEmiAlicuota().compareTo(c2.getFecEmiAlicuota());

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

