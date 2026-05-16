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
public class EsFecha {

    public boolean esFecha(String fechax)

    {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9]{2}/[0-9]{2}/[0-9]{2,4})");
        mat = pat.matcher(fechax);
        if (mat.find()) {
                        return true;
        }
	else
	{
            return false;        }
        }

    public static void main(String args[]) {
        String car;
        car = "01/01/12345";
        boolean siEs;
        EsFecha fecha = new EsFecha();
        siEs = fecha.esFecha(car);
        if (siEs) {
            System.out.println("es una fecha:" + car);
        } else {
            System.out.println("No es una fecha;" + car);
        }

    }
}