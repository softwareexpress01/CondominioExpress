
package validadores;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Frank Rojas
 */
public class EsNombApell {
    
    public boolean esNombApell(String nombApell)
	
	{
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([a-zA-Z]+\\s*)");
        mat = pat.matcher(nombApell);
        if (mat.find()) {
            
            return true;
        }
	else
	{
            return false;        }
        }

    public static void main(String args[]) {
        String car;
        car = "a          ";
        boolean siEs;
        EsNombApell email = new EsNombApell();
        siEs = email.esNombApell(car);
        if (siEs) {
            System.out.println("es un nombre o apellido :" + car);
        } else {
            System.out.println("No es un nombre o apellido:" + car);
        }
    }
}