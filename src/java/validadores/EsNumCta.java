/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validadores;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Frank Rojas
 */
public class EsNumCta {

    public boolean esNumCta(String numCta)

    {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9]{4}-[0-9]{4}-[0-9]{2}-[0-9]{10})");
        mat = pat.matcher(numCta);
        if (mat.find()) {
                        return true;
        }
	else
	{
            return false;        }
        }

    public static void main(String args[]) {
        String car;
        car = "0100-0202-99-1234567890";
        boolean siEs;
        EsNumCta cuenta = new EsNumCta();
        siEs = cuenta.esNumCta(car);
        if (siEs) {
            System.out.println("es una Cuenta Bancaria:" + car);
        } else {
            System.out.println("No es una Cuenta Bancaria: " + car);
        }

    }
}