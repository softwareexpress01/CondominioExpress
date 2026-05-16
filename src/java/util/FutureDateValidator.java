package util;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;


/**
 * Simple Date validator that checks to see if a date is a future date.
 */


public class FutureDateValidator implements Validator{
  public FutureDateValidator() {
  }

  public void validate(FacesContext FacesContext, UIComponent UIComponent, 
                       Object value) throws ValidatorException {
    Date enteredDate = (Date) value;
    
    Date today = new java.util.Date();    
    
    if(today.after(enteredDate)) {
      FacesMessage message = new FacesMessage("Entered date must be a future date");
      throw new ValidatorException(message);
    }                       
  }
}
