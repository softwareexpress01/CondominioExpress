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
public class EsEmail {
    
    public boolean esEmail(String correo)
                             
	{
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
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
        car = "f@hoysi12.com";
        boolean siEs;
        EsEmail email = new EsEmail();
        siEs = email.esEmail(car);
        if (siEs) {
            System.out.println("es un email:" + car);
        } else {
            System.out.println("No es un email:" + car);
        }

    }
}
