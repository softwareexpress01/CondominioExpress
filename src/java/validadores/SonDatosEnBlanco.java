
 
package validadores;

/**
 *
 * @author Frank Rojas
 */
public class SonDatosEnBlanco {
    
    public boolean sonDatosEnBlanco(String datos)
	
	{
        String datosEntrada;
        datosEntrada = datos;
        boolean siSon = false;
        int vacio = datosEntrada.length();
        if (vacio==0)
        {
           siSon = true; 
        }
        if (siSon) 
        {
            
            return true;
        }
	else
        {
            return false;        }
        }

    public static void main(String args[]) {
        String car, car2;
        car = null;
        car2= null;
        String datos = car + car2;
        boolean siEs;
        SonDatosEnBlanco email = new SonDatosEnBlanco();
        siEs = email.sonDatosEnBlanco(datos);
        if (siEs) {
            System.out.println("Son datos en blanco" + datos);
        } else {
            System.out.println("No son datos en blanco" + datos);
        }

    }
}
