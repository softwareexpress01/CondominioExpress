/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validadores;

/**
 *
 * @author Frank Rojas
 */
public class EsMinSeisCar {
    
        public boolean esMinSeisCar(String numIn) {
        if (numIn.length() >= 6)
        {
            return true;
        } 
        else
        {
            return false;
        }
    }

    public static void main(String args[]) {
        String car;
        car = "1234";
        boolean siEs;
        EsMinSeisCar entero = new EsMinSeisCar();
        siEs = entero.esMinSeisCar(car);
        if (siEs) {
            System.out.println("es mayor o igual a seis car: " + car);
        } else {
            System.out.println("No es mayor o igual a seis car: " + car);
        }

    }
}
    

