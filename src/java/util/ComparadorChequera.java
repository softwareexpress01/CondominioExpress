/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Chequera;
import java.util.Comparator;

/**
 *
 * @author jesus
 */
public class ComparadorChequera implements Comparator {

    public int compare(Object o1, Object o2) {
        Chequera c1 = (Chequera) o1;
        Chequera c2 = (Chequera) o2;
        return c1.getNumChequera().compareTo(c2.getNumChequera());

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
 
