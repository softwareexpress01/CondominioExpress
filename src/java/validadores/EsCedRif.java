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
public class EsCedRif {
    
    public boolean esCedRif(String cedRif)
	
	{
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([JjVvEeGg][0-9])");
        mat = pat.matcher(cedRif);
        if (mat.find()) {
            
            return true;
        }
	else
	{
            return false;        }
        }

    public static void main(String args[]) {
        String car;
        car = "t5962891";
        boolean siEs;
        EsCedRif email = new EsCedRif();
        siEs = email.esCedRif(car);
        if (siEs) {
            System.out.println("es un CedRif :" + car);
        } else {
            System.out.println("No es un CedRif:" + car);
        }

    }
}
