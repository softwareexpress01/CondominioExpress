/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;


/**
 *
 * @author jesus
 */
public class TerminosYCondicionesCtrl implements Serializable {

    private HtmlSelectBooleanCheckbox aceptar;
    
    public TerminosYCondicionesCtrl() {
    }

    public String continuar() {

        if (aceptar.isSelected()) {
            return "continuar";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Por favor, especifique si acepta los términos y condiciones."));
            return (null);
        }
    }
    public void setAceptar(HtmlSelectBooleanCheckbox aceptar) {
        this.aceptar = aceptar;
    }

    public HtmlSelectBooleanCheckbox getAceptar() {
        return aceptar;
    }
}
