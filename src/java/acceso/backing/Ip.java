/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jesus
 */
public class Ip {

    public void IP() {
    }

    public static void main(String arg[]) throws IOException {

        InetAddress IP;

        try {

            IP = InetAddress.getLocalHost();

            System.out.println("Mi ip local es = " + IP.getHostAddress());

        } catch (UnknownHostException e) {
        }
    
    
    try {

            URL whatismyip = new URL("http://checkip.amazonaws.com");

            BufferedReader in = new BufferedReader(new InputStreamReader(

                    whatismyip.openStream()));     

            String ip = in.readLine();     

            System.out.println("My Public ip is = "+ip);

            in.close();

        } catch (MalformedURLException ex) {
            
        } catch (IOException ex) {
          

        }

    }
}
