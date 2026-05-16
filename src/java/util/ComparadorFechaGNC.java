/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Comparator;
import movimientos.modelos.FacGastoNoComun;


/**
 *
 * @author softw
 */

  public class ComparadorFechaGNC implements Comparator{  
      
      
    @Override
    public int compare(Object o1, Object o2) {

        FacGastoNoComun c1 = (FacGastoNoComun) o1;
        FacGastoNoComun c2 = (FacGastoNoComun) o2;
       
        return c1.getFecFacGNC().compareTo(c2.getFecFacGNC());

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
