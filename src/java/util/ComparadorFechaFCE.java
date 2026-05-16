/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author softw
 */
package util;
import java.util.Comparator;

import movimientos.modelos.FacCuotaEsp;

public class ComparadorFechaFCE implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {

        FacCuotaEsp c1 = (FacCuotaEsp) o1;
        FacCuotaEsp c2 = (FacCuotaEsp) o2;

        return c1.getFecEmiFacCuotaEsp().compareTo(c2.getFecEmiFacCuotaEsp());

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
