/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.modelos;

import informes.backing.AvisoDeCobroCtrl;
import informes.backing.EmailAvisoDeCobroCtrl;
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
public class EmailAvisosEJB implements Serializable {

   /* public void avisosEJB() {
    }*/
    public void EmailAvisosEJB(){
    }
    
  
    @Asynchronous
    public void sendEmail(Integer idCond, String periodo, BeanUsuario beanUsuario, String idInmueble, String asunto, String mensaje, String emailUsuario, String contraseñaUsuario,
            Boolean tasaExiste, Float valorTasa) 
            throws ClassNotFoundException, SQLException, IOException, JRException, ParseException, MessagingException {

        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::INICIANDO PROCESO EN EJB");
        EmailAvisoDeCobroCtrl avisoCtrl = new EmailAvisoDeCobroCtrl();
        avisoCtrl.generarReporteAvisosDeCobro(idCond, periodo, beanUsuario, idInmueble, asunto, mensaje, emailUsuario, contraseñaUsuario, tasaExiste,valorTasa);
        
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::EN EJB, el beanusuario-tasa = "+beanUsuario.getTasa());
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::CONCLUIDO PROCESO EN EJB");
        
    } 
}
