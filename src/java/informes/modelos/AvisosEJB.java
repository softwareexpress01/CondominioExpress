/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import informes.backing.AvisoDeCobroCtrl;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;
import util.BeanUsuario;

/**
 *
 * @author Frank Rojas H.
 */
@Stateless
public class AvisosEJB implements Serializable {

    public void avisosEJB() {
    }

    @Asynchronous
    public void sendEmail(Integer idCond, String periodo, BeanUsuario beanUsuario, String idInmueble) throws ClassNotFoundException, SQLException, IOException, JRException, ParseException, MessagingException {

        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::INICIANDO PROCESO EN EJB");
        AvisoDeCobroCtrl avisoCtrl = new AvisoDeCobroCtrl();
        avisoCtrl.generarReporteAvisosDeCobro(idCond, periodo, beanUsuario, idInmueble);
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::CONCLUIDO PROCESO EN EJB");
        
    }
}
