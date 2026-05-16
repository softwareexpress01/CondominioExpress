/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validadores;

/**
 *
 * @author Frank Rojas 
 */
public class EsEntero {

    public boolean esEntero(String numIn) {
        try {
            Integer.parseInt(numIn);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void main(String args[]) {
        String car;
        car = "....";
        boolean siEs;
        EsEntero entero = new EsEntero();
        siEs = entero.esEntero(car);
        if (siEs) {
            System.out.println("es un entero:" + car);
        } else {
            System.out.println("No es un entero;" + car);
        }

    }
}