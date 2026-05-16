package util;

/**
 *
 * @author jesus
 */
public class Excepcion extends Exception {

    public Excepcion() {
        super(); // constructor por defecto de Exception
    }

    public Excepcion(String cadena) {
        super(cadena); // constructor param. de Exception
    }
}