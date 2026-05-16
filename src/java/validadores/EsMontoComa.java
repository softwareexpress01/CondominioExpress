/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validadores;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Frank Rojas
 */
public class EsMontoComa {
    
    public boolean esMontoComa(String correo)
	
	{
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([^a-zA-Z][,?+]*[0-9])");
        mat = pat.matcher(correo);
        if (mat.find()) {
            
            return true;
        }
	else
	{
            return false;        }
        }

    public static void main(String args[]) {
        String car;
        car = "9990.10";
        boolean siEs;
        EsMontoComa email = new EsMontoComa();
        siEs = email.esMontoComa(car);
        if (siEs) {
            System.out.println("es un Monto :" + car);
        } else {
            System.out.println("No es un Monto:" + car);
        }

    }
}
