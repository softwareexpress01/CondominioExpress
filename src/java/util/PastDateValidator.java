package util;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;



/**
 * Simple Date validator that checks to see if a date is a past date.
 */



public class PastDateValidator implements Validator {
  public PastDateValidator() {
  }

  public void validate(FacesContext FacesContext, UIComponent UIComponent, 
                       Object value) throws ValidatorException {
    Date enteredDate = (Date)value;

    Date today = new Date();

    if (today.before(enteredDate)) {
      FacesMessage message = 
        new FacesMessage("Entered date must not be in the future!");
      throw new ValidatorException(message);
    }
  }
}
