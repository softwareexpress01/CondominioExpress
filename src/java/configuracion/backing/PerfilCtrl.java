package configuracion.backing;

import saas.modelos.Suscripcion;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

public class PerfilCtrl extends DaoCrud<Usuario, Usuario, Integer> implements Serializable {

    private Integer id;
    private String codUsuario;
    private String contUsuario;
    private String repContUsuario;
    private String nivelAuto;
    private String nombUsuario;
    private String apelUsuario;
    private String cedUsuario;
    private HtmlSelectOneMenu sexoUsuario;
    private String codTelUsuario;
    private String numTelUsuario;
    private String codCelUsuario;
    private String numCelUsuario;
    private String emailUsuario;
    private Usuario filaSeleccionada;

    public PerfilCtrl() {
    }

    public void inicializarPanel() {

        this.setCodUsuario(null);
        this.setContUsuario(null);
        this.setRepContUsuario(null);
        String nulo = null;
        this.setNivelAuto(null);
        this.setNombUsuario(null);
        this.setApelUsuario(null);
        this.setCedUsuario(null);
        this.sexoUsuario.setValue(nulo);
        this.setCodTelUsuario(null);
        this.setNumTelUsuario(null);
        this.setCodCelUsuario(null);
        this.setNumCelUsuario(null);
        this.setEmailUsuario(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        return "configuracion";

    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (this.getCodUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código del usuario...";
            return mensaje;
        }
        if (!val.esMinSeisCar(this.getCodUsuario())) {
            mensaje = "El código de usuario debe tener como mínimo seis caracteres, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getNombUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del usuario...";
            return mensaje;
        }
        if (!val.esNombApell(this.getNombUsuario())) {
            mensaje = "El nombre del usuario debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        if (this.getApelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el apellido del usuario...";
            return mensaje;
        }
        if (!val.esNombApell(this.getApelUsuario())) {
            mensaje = "El apellido del usuario debe ser alfabético, por favor, intente de nuevo...";
            return mensaje;
        }
        if (this.getCedUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de cédula del usuario...";
            return mensaje;
        }
        if (!val.esCedRif(this.getCedUsuario())) {
            mensaje = "El número de cédula debe comenzar con V o v, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getCodTelUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de área del teléono del usuario...";
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
        if (this.getEmailUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el correo del usuario...";
            return mensaje;
        }
        if (!val.esEmail(this.getEmailUsuario())) {
            mensaje = "El correo electrónico es invalido, por favor, intente de nuevo...";
            return mensaje;
        }
        if (this.getContUsuario().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la contraseña...";
            return mensaje;
        }
        if (!val.esMinSeisCar(this.getContUsuario())) {
            mensaje = "La contraseña debe tener como mínimo seis caracteres, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getRepContUsuario().equals(enBlanco)) {
            mensaje = "Por favor, repita la contraseña...";
            return mensaje;
        }
        if (!this.getContUsuario().equals(this.getRepContUsuario())) {
            mensaje = "Las contraseñas suministradas deben coincidir, por favor, intente de nuevo...";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getUsuariosPorAdministrador() {

        // Obtiene los usuarios asociados a un administrador dado (id)
        // Obtiene el id del usuario del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idUsuario = beanUsuario.getIdUsuario();

        Collection<Usuario> listUsuarios = new ArrayList<Usuario>();
        String nombQuery = "buscarUsuariosPorIdUsuario";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listUsuarios = dataAcces.buscarListaEntidadesPorId(idUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        //Para obtener el administrador...tambien se mostrara en el panel 2
        Usuario admin;
        try {
            admin = encontrarEntidadPorId(idUsuario);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        listUsuarios.add(admin);

        Collection listaOrd = ordenarLista(listUsuarios);// para ordenar la lista de usuarios

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Usuario> listaDesOrdenada = new ArrayList<Usuario>();

        for (Iterator<Usuario> it = lista.iterator();
                it.hasNext();) {
            Usuario u = (Usuario) it.next();
            listaDesOrdenada.add(u);
        }
        Collections.sort(listaDesOrdenada, new ComparadorUsu());

        Collection<Usuario> coleccionOrdenada = new ArrayList<Usuario>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Usuario usu = listaDesOrdenada.get(i);
            coleccionOrdenada.add(usu);
        }
        return coleccionOrdenada;
    }

    public void editarUsuarioAction() {

        Usuario usuarioSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del inmueble seleccionado la cual se buscara como "Inmueble" en el map #{Inmueble} 
        ctx.getExternalContext().getSessionMap().put("Usuario", usuarioSel);

        this.setCodUsuario(usuarioSel.getCodUsuario());
        this.setContUsuario(usuarioSel.getContUsuario());

        this.setNivelAuto(usuarioSel.getPerUsuario());

        this.setNombUsuario(usuarioSel.getNombUsuario());
        this.setApelUsuario(usuarioSel.getApelUsuario());
        this.setCedUsuario(usuarioSel.getCedUsuario());
        //------------------------------------------------------------------------
        // se separan los telefonos cod + telefonos para mostrarlos en el panel 1
        //------------------------------------------------------------------------
        this.setCodTelUsuario(usuarioSel.getTelUsuario().substring(0, 4));
        this.setNumTelUsuario(usuarioSel.getTelUsuario().substring(5));
         
        this.setCodCelUsuario(usuarioSel.getCelUsuario().substring(0, 4));
        this.setNumCelUsuario(usuarioSel.getCelUsuario().substring(5));

        this.setEmailUsuario(usuarioSel.getEmailUsuario());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(usuarioSel.getCodUsuario());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarUsuario() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Usuario usuarioNuevo = new Usuario();
        String guion = "-";

        usuarioNuevo.setCodUsuario(codUsuario);
        usuarioNuevo.setPerUsuario("Operador");
        usuarioNuevo.setContUsuario(contUsuario);

        usuarioNuevo.setNombUsuario(nombUsuario);
        usuarioNuevo.setApelUsuario(apelUsuario);
        usuarioNuevo.setCedUsuario(cedUsuario);
        usuarioNuevo.setSexoUsuario(sexoUsuario.getValue().toString());

        usuarioNuevo.setTelUsuario(codTelUsuario.concat(guion.concat(numTelUsuario)));
        usuarioNuevo.setCelUsuario(codCelUsuario.concat(guion.concat(numCelUsuario)));

        usuarioNuevo.setEmailUsuario(emailUsuario);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idUsuario = beanUsuario.getIdUsuario();

        Usuario usuarioAdmin = null;
        try {
            usuarioAdmin = (Usuario) encontrarEntidadPorId(idUsuario);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-------------------------buscar suscripcion---------------
        Suscripcion suscripcion = usuarioAdmin.getSuscripcion();
        //    usuarioAdmin.getOperadores().add(usuarioNuevo);
        usuarioNuevo.setSuscripcion(suscripcion);
        usuarioNuevo.setUsuarioAdmin(usuarioAdmin);
        try {
            almacenarOModificarEntidad(usuarioNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El usuario " + usuarioNuevo.getCodUsuario() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarUsuario() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        Usuario usuarioSel = (Usuario) ctx.getExternalContext().getSessionMap().get("Usuario");

        String guion = "-";

        usuarioSel.setCodUsuario(codUsuario);
        usuarioSel.setContUsuario(contUsuario);
        usuarioSel.setNombUsuario(nombUsuario);
        usuarioSel.setApelUsuario(apelUsuario);
        usuarioSel.setCedUsuario(cedUsuario);
        usuarioSel.setSexoUsuario(sexoUsuario.getValue().toString());
        usuarioSel.setTelUsuario(codTelUsuario.concat(guion.concat(numTelUsuario)));
        usuarioSel.setCelUsuario(codCelUsuario.concat(guion.concat(numCelUsuario)));
        usuarioSel.setEmailUsuario(emailUsuario);

        try {
            almacenarOModificarEntidad(usuarioSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El usuario " + usuarioSel.getCodUsuario() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setContUsuario(String contUsuario) {
        this.contUsuario = contUsuario;
    }

    public String getContUsuario() {
        return contUsuario;
    }

    public void setRepContUsuario(String repContUsuario) {
        this.repContUsuario = repContUsuario;
    }

    public String getRepContUsuario() {
        return repContUsuario;
    }

    public void setNivelAuto(String nivelAuto) {
        this.nivelAuto = nivelAuto;
    }

    public String getNivelAuto() {
        return nivelAuto;
    }

    public void setNombUsuario(String nombUsuario) {
        this.nombUsuario = nombUsuario;
    }

    public String getNombUsuario() {
        return nombUsuario;
    }

    public void setApelUsuario(String apelUsuario) {
        this.apelUsuario = apelUsuario;
    }

    public String getApelUsuario() {
        return apelUsuario;
    }

    public void setCedUsuario(String cedUsuario) {
        this.cedUsuario = cedUsuario;
    }

    public String getCedUsuario() {
        return cedUsuario;
    }

    public void setSexoUsuario(HtmlSelectOneMenu sexoUsuario) {
        this.sexoUsuario = sexoUsuario;
    }

    public HtmlSelectOneMenu getSexoUsuario() {
        return sexoUsuario;
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

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    // .....
    public void setFilaSeleccionada(Usuario filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public Usuario getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
