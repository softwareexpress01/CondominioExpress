package validadores;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Frank Rojas
 */
public class EsAlfaNum {

    public boolean esAlfaNum(String campo) {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9a-zA-Z]+\\s*)");
        mat = pat.matcher(campo);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }
}