/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import java.io.IOException;
import java.sql.SQLException;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author Jesus
 */

@Singleton
public class SingletonEJB {
    @EJB
    CobSuscripcionEJB cobSuscripcionEJB;
  
 //   @Schedule(second="1", minute="1",hour="15", persistent=true)
    public void doWork() throws ClassNotFoundException, IOException, SQLException, JRException, MessagingException{
        cobSuscripcionEJB.generarReporteCobSuscripcion();
    }
}