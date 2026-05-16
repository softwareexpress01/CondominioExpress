/**
 * Backing bean para registro xhtml
 */
package acceso.backing;

/**
 *
 * @author jesus
 */
import saas.modelos.Suscripcion;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;

public class RegistroCtrl extends DaoCrud<Usuario, Suscripcion, Integer> implements Serializable {

    private String numSuscripcion;
    private String contSuscripcion;
    private String nombUsuario;
    private String cedUsuario;
    private String emailUsuario;
    private String contUsuario;
    private String codTelUsuario;
    private String numTelUsuario;
    private String apelUsuario;
    private String sexoUsuario;
    private String codUsuario;
    private String contUsuario2;
    private String codCelUsuario;
    private String numCelUsuario;
    
    private String contSus;
    private Suscripcion susc;

    public RegistroCtrl() {
    }

    public String validarPanel() {

        String enBlanco = new String();;
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //  ------------------Validar la suscripcion -------------
        if (this.getNumSuscripcion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de la suscripción...";
            return mensaje;
        }
        mensaje = validarSuscripcion(numSuscripcion);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }
        //------------------Validar contraseña de suscripcion--------------------
        if (this.contSuscripcion.equals(enBlanco)) {
            mensaje = "Por favor, ingrese la contraseña de suscripción...";
            return mensaje;
        }
        mensaje = validarContSuscripcion(contSuscripcion);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }
        //------------------Validar nombre de usuario--------------------
        if (this.nombUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del usuario...";
            return mensaje;
        }
        if (!val.esNombApell(this.nombUsuario)) {
            mensaje = "El nombre del usuario debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        //------------------Validar apellido del  usuario--------------------
        if (this.apelUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el apellido del usuario...";
            return mensaje;
        }
        if (!val.esNombApell(this.apelUsuario)) {
            mensaje = "El apellido del usuario debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        //------------------Validar cedula del  usuario--------------------
        if (this.cedUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de cédula del usuario...";
            return mensaje;
        }
        if (!val.esCedRif(this.cedUsuario)) {
            mensaje = "El número de cédula debe comenzar con V o v, por favor intente de nuevo...";
            return mensaje;
        }
        //------------------Validar el sexo del  usuario--------------------
        if (this.sexoUsuario==null) {
            mensaje = "Por favor, seleccione el sexo del usuario...";
            return mensaje;
        }
        //------------------Validar correo del  usuario--------------------
        if (this.emailUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el correo del usuario...";
            return mensaje;
        }
        if (!val.esEmail(this.emailUsuario)) {
            mensaje = "El correo electrónico es inválido, por favor, intente de nuevo...";
            return mensaje;
        }
        //------------------Validar codigo del  usuario--------------------
        if (this.codUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código del usuario...";
            return mensaje;
        }
        if (!val.esMinSeisCar(this.codUsuario)) {
            mensaje = "El código de usuario debe tener como mínimo seis caracteres, por favor intente de nuevo...";
            return mensaje;
        }
        mensaje = validarCodUsuario(codUsuario);
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }
        //------------------Validar contraseña del  usuario--------------------
        if (this.contUsuario.equals(enBlanco)) {
            mensaje = "Por favor, ingrese la contraseña del usuario...";
            return mensaje;
        }
        if (!val.esMinSeisCar(this.contUsuario)) {
            mensaje = "La contraseña debe tener como mínimo seis caracteres, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.contUsuario2.equals(enBlanco)) {
            mensaje = "Por favor, repita la contraseña...";
            return mensaje;
        }
        if (!this.contUsuario.equals(this.contUsuario2)) {
            mensaje = "Las contraseñas suministradas deben coincidir, por favor, intente de nuevo...";
            return mensaje;
        }
        //------------------Validar telefono  y celular del  usuario--------------------
        if (this.getCodTelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de área del teléfono del usuario...";
            return mensaje;
        }
        if (!val.esEntero(this.getCodTelUsuario())) {
            mensaje = "El código de área del teléfono del usuario debe ser un número entero, por favor, intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodTelUsuario())) {
            mensaje = "El código de área del teléfono del usuario debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumTelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono del usuario...";
            return mensaje;
        }
        if (!val.esEntero(this.getNumTelUsuario())) {
            mensaje = "El número de teléfono debe constar solo de números enteros, por favor, intente de nuevo";
            return mensaje;
        }
        if (this.getCodCelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de área del celular del usuario...";
            return mensaje;
        }
        if (!val.esEntero(this.getCodCelUsuario())) {
            mensaje = "El código de la operadora del celular debe ser un número entero, por favor, intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodCelUsuario())) {
            mensaje = "El código de área del celular del usuario debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumCelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del celular del usuario...";
            return mensaje;
        }
        if (!val.esEntero(this.getNumCelUsuario())) {
            mensaje = "El número del celular debe ser un número entero, por favor, intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public String validarSuscripcion(String numero) {

        String mensaje = "";
        String nombQuery = "buscarSuscripcion";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            susc = dataAcces.buscarEntidad(Integer.parseInt(numero), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mensaje;
        }

        if (susc == null) {
            // suscripcion no existe
            mensaje = "Suscripción no ha sido registrada!, por favor intente de nuevo";
            return mensaje;
        }

        Boolean existe;
        nombQuery = "buscarAdminExiste";
        DaoQuery<Boolean> dataAcces1 = new DaoQuery<Boolean>();
        try {
            existe = dataAcces1.buscarAdminExiste(numero, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mensaje;
        }

        if (existe) {
            // la suscripcion ya tiene asociada un usuario administrador
            mensaje = "Ya existe un administrador para la suscripción dada, por favor intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public String validarContSuscripcion(String contraseña) {

        String mensaje = "";
        contSus = susc.getContSuscripcion();

        if (!contraseña.equals(contSus)) {
            // contraseña de suscripcion invalida
            mensaje = "Contraseña de suscripción inválida!, por favor, intente de nuevo";
            return mensaje;
        }
        return mensaje;

    }

    public String validarCodUsuario(String cod) {

        Boolean resultado = null;
        String mensaje = "";
        String nombQuery = "buscarUsuarioExiste";
        DaoQuery<Boolean> dataAcces = new DaoQuery<Boolean>();
        try {
            resultado = dataAcces.buscarEntidadExiste(cod, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mensaje;
        }

        if (resultado) {
            // usuario ya existe
            mensaje = "Usuario " + cod + " ya existe! Por favor, intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public void registrarUsuario() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Usuario UsuarioNuevo = new Usuario();

        String guion = "-";
        UsuarioNuevo.setNombUsuario(nombUsuario);
        UsuarioNuevo.setCedUsuario(cedUsuario);
        UsuarioNuevo.setEmailUsuario(emailUsuario);
        UsuarioNuevo.setContUsuario(contUsuario);
        UsuarioNuevo.setTelUsuario(codTelUsuario.concat(guion.concat(numTelUsuario)));
        UsuarioNuevo.setApelUsuario(apelUsuario);
        UsuarioNuevo.setSexoUsuario(sexoUsuario);
        UsuarioNuevo.setCodUsuario(codUsuario);
        UsuarioNuevo.setCelUsuario(codCelUsuario.concat(guion.concat(numCelUsuario)));
        UsuarioNuevo.setPerUsuario("Admin");

        UsuarioNuevo.setSuscripcion(susc);
        susc.addUsuario(UsuarioNuevo);

        try {
            almacenarOModificarEntidad(UsuarioNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El usuario " + UsuarioNuevo.getCodUsuario() + " ha sido registrado satisfactoriamente!.. Perfil asignado: Administrador"));
        return;
    }

    public void setNombUsuario(String nombUsuario) {
        this.nombUsuario = nombUsuario;
    }

    public String getNombUsuario() {
        return nombUsuario;
    }

    public void setCedUsuario(String cedUsuario) {
        this.cedUsuario = cedUsuario;
    }

    public String getCedUsuario() {
        return cedUsuario;
    }

    public void setNumSuscripcion(String numSuscripcion) {
        this.numSuscripcion = numSuscripcion;
    }

    public String getNumSuscripcion() {
        return numSuscripcion;
    }

    public void setContSuscripcion(String contSuscripcion) {
        this.contSuscripcion = contSuscripcion;
    }

    public String getContSuscripcion() {
        return contSuscripcion;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setContUsuario(String contUsuario) {
        this.contUsuario = contUsuario;
    }

    public String getContUsuario() {
        return contUsuario;

    }

    public void setContUsuario2(String contUsuario2) {
        this.contUsuario2 = contUsuario2;
    }

    public String getContUsuario2() {
        return contUsuario2;
    }

    public void setCodTelUsuario(String codTelUsuario) {
        this.codTelUsuario = codTelUsuario;
    }

    public String getCodTelUsuario() {
        return codTelUsuario;
    }

    public void setNumTelUsuario(String numTelUsuario) {
        this.numTelUsuario = numTelUsuario;
    }

    public String getNumTelUsuario() {
        return numTelUsuario;
    }

    public void setApelUsuario(String apelUsuario) {
        this.apelUsuario = apelUsuario;
    }

    public String getApelUsuario() {
        return apelUsuario;
    }

    public void setSexoUsuario(String sexoUsuario) {
        this.sexoUsuario = sexoUsuario;
    }

    public String getSexoUsuario() {
        return sexoUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodCelUsuario(String codCelUsuario) {
        this.codCelUsuario = codCelUsuario;
    }

    public String getCodCelUsuario() {
        return codCelUsuario;
    }

    public void setNumCelUsuario(String numCelUsuario) {
        this.numCelUsuario = numCelUsuario;
    }

    public String getNumCelUsuario() {
        return numCelUsuario;
    }
}
