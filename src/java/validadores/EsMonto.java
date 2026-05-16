/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validadores;

/**
 *
 * @author JesusRojas
 */
public class EsMonto {

    public boolean esMonto(String numIn) {
        Float monto;
        
        try {
            monto = Float.valueOf(numIn);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }  
}