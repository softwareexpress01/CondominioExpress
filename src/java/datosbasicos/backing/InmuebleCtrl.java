package datosbasicos.backing;

import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.ProgresoDT;
import datosbasicos.modelos.Propiedad;
import acceso.modelos.CodigoAcceso;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

public class InmuebleCtrl extends DaoCrud<Inmueble, Propiedad, Integer> implements Serializable {

    private Integer id;
    private String codInmueble;
    private String nombPropInmueble;
    private String cedRifPropInmueble;
    private String dirInmueble;
    private String codTelInmueble;
    private String numTelInmueble;
    private String emailPropInmueble;
    private HtmlSelectOneMenu tipoInmueble;
    private String areaInmueble;
    private String alicInmueble;
    private String notaRecInmueble;
    private Inmueble filaSeleccionada;
    private Collection<Inmueble> listInm = new ArrayList<Inmueble>();
    private Propiedad propiedad;

    public InmuebleCtrl() {
    }

    public void inicializarPanel() {

        this.setCodInmueble(null);
        this.setNombPropInmueble(null);
        this.setCedRifPropInmueble(null);
        this.setDirInmueble(null);
        this.setCodTelInmueble(null);
        this.setNumTelInmueble(null);
        this.setEmailPropInmueble(null);
        String tipo = null;
        this.tipoInmueble.setValue(tipo);
        this.setTipoInmueble(null);
        this.setAreaInmueble(null);
        this.setAlicInmueble(null);
        this.setNotaRecInmueble(null);

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

        return "datosbasicos";

    }

    public String validarPanel() {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        String enBlanco = new String();
        String mensaje = new String();

        if (this.getCodInmueble().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código del inmueble...";
            return mensaje;
        }

        if (!val.esAlfaNum(this.getCodInmueble())) {
            mensaje = "El código del inmueble debe ser alfanumérico, por favor intente de nuevo";
            return mensaje;
        }

        if (this.getNombPropInmueble().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del propietario del inmueble...";
            return mensaje;
        }

        if (!val.esNombApell(this.getNombPropInmueble())) {
            mensaje = "El nombre del propietario debe ser alfabético, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getCedRifPropInmueble().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de cédula o rif del propietario del inmueble...";
            return mensaje;
        }

        if (!val.esCedRif(this.getCedRifPropInmueble())) {
            mensaje = "La cédula debe comenzar con V o E, el Rif con J o G, por favor intente de nuevo...";
            return mensaje;
        }
        //---------------------------------------------------- 
        // Se cambio a opcional la direccion del inmuble       
        // Jesus Rojas 12-03-2013                              
        //---------------------------------------------------  
        if (!this.getDirInmueble().equals(enBlanco)) {

            if (!val.esAlfaNumPlus(this.getDirInmueble())) {
                mensaje = "La dirección del inmueble debe ser alfanumérica, por favor intente de nuevo";
                return mensaje;
            }
        }
        //----------------------------------------------------//
        // Se cambio a opcional los datos de telefono e email //
        // Jesus Rojas 22-01-2013                            //
        //---------------------------------------------------//
        if (!this.getCodTelInmueble().equals(enBlanco)) {

            if (!val.esEntero(this.getCodTelInmueble())) {
                mensaje = "El código de área del teléfono del inmueble debe ser numérico, por favor intente de nuevo";
                return mensaje;
            }
            if (!val.esMinCuatroCar(this.getCodTelInmueble())) {
                mensaje = "El código de área del teléfono del inmueble debe ser de cuatro caracteres, por favor intente de nuevo";
                return mensaje;
            }
        }
        if (!this.getNumTelInmueble().equals(enBlanco)) {
            if (!val.esEntero(this.getNumTelInmueble())) {
                mensaje = "El número de teléfono del inmueble debe ser numérico, por favor intente de nuevo";
                return mensaje;
            }
        }
        if (!this.getEmailPropInmueble().equals(enBlanco)) {
            if (!val.esEmail(this.getEmailPropInmueble())) {
                mensaje = "El formato del correo electrónico es inválido, por favor intente de nuevo";
                return mensaje;
            }
        }

        if (this.getTipoInmueble()
                == null) {
            mensaje = "Por favor, seleccione el tipo de inmueble...";
            return mensaje;
        }


        if (this.getAreaInmueble().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el área (mts2) del inmueble...";
            return mensaje;
        }


        if (!val.esMonto(
                this.getAreaInmueble())) {
            mensaje = "El área del inmueble debe ser un número entero o decimal, por favor intente de nuevo";
            return mensaje;
        }

        if (this.getAlicInmueble().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el porcentaje de alícuota del inmueble...";
            return mensaje;
        }

        if (!val.esMonto(
                this.getAlicInmueble())) {
            mensaje = "La alicuota del inmueble debe ser un número entero o decimal, por favor intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public boolean esEntero(String numIn) {

        try {
            Integer entero = Integer.parseInt(numIn);
            if (entero > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public Collection getInmueblesPorPropiedad() {

        // Obtiene las propiedades para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idProp = beanUsuario.getIdPropActual();

        listInm.clear();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaOrd = ordenarLista(listInm);// para ordenar la lista de inmuebles

        return listaOrd;
    }

    public Collection getCalcularProgresoRegistro() {

        //-------------------------------------------------------------------------
        // Genera una ventana con el progreso del registro de inmuebles
        // Verifica antes, si se ha creadoa algun inmueble
        //-------------------------------------------------------------------------

        Collection<ProgresoDT> progreso = new ArrayList<ProgresoDT>();
        ProgresoDT prog = new ProgresoDT();

        listInm = getInmueblesPorPropiedad();

        if (listInm.isEmpty()) {

            prog.setNumInmProp("0");
            prog.setPorcInmRegistrados("0");
            prog.setPorcAlicRegistrado("0");
            progreso.add(prog);
            System.out.println("los porcentajes son: " + prog.getPorcInmRegistrados() + "/" + prog.getPorcAlicRegistrado());
            return progreso;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idProp = beanUsuario.getIdPropActual();
        Propiedad prop = buscarPropiedad(idProp);

        float numInmProp = Integer.parseInt(prop.getNumInmuebles());
        float totalInm = listInm.size();
        Double totalAlic = 0.0;

        for (Iterator<Inmueble> it = listInm.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            totalAlic = totalAlic + inm.getAlicInmueble();
        }
        //-------------------------------------------------------------------------------------
        // Se da formato al porcentaje de inmuebles y de alicuotas para pasarlos al Data Table
        //-------------------------------------------------------------------------------------
        Float pa = totalAlic.floatValue();
        Float porcInmRegistrados = (totalInm / numInmProp) * 100;

        Formatter fmt = new Formatter();
        String porcInm = fmt.format(Locale.US, "%.2f", porcInmRegistrados).toString();

        Formatter fmt1 = new Formatter();
        String porcAlic = fmt1.format(Locale.US, "%.2f", pa).toString();

        fmt.close();
        fmt1.close();

        prog.setNumInmProp(prop.getNumInmuebles());
        prog.setPorcInmRegistrados(porcInm);
        prog.setPorcAlicRegistrado(porcAlic);
        progreso.add(prog);
        System.out.println("los porcentajes son: " + prog.getPorcInmRegistrados() + "/" + prog.getPorcAlicRegistrado());
        return progreso;
    }

    public Propiedad buscarPropiedad(Integer idProp) {

        try {
            propiedad = (Propiedad) encontrarEntidadPorId(idProp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return propiedad;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Inmueble> listaDesOrdenada = new ArrayList<Inmueble>();

        for (Iterator<Inmueble> it = lista.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            listaDesOrdenada.add(inm);
        }
        Collections.sort(listaDesOrdenada, new ComparadorInm());

        Collection<Inmueble> coleccionOrdenada = new ArrayList<Inmueble>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Inmueble inm = listaDesOrdenada.get(i);
            coleccionOrdenada.add(inm);
        }
        return coleccionOrdenada;
    }

    public void editarInmuebleAction() {

        Inmueble inmSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del inmueble seleccionado la cual se buscara como "Inmueble" en el map #{Inmueble} 
        ctx.getExternalContext().getSessionMap().put("Inmueble", inmSel);

        this.setCodInmueble(inmSel.getCodInmueble());
        this.setNombPropInmueble(inmSel.getNombPropInmueble());
        this.setCedRifPropInmueble(inmSel.getCedRifPropInmueble());
        this.setDirInmueble(inmSel.getDirInmueble());

        //------------------------------------------------------------------------
        // se separan los telefonos cod + telefonos para mostrarlos en el panel 1
        //------------------------------------------------------------------------
        String blanco = "";

        if (!inmSel.getTelInmueble().equals(blanco)) {
            this.setCodTelInmueble(inmSel.getTelInmueble().substring(0, 4));
            this.setNumTelInmueble(inmSel.getTelInmueble().substring(5));
        } else {
            this.setCodTelInmueble(blanco);
            this.setNumTelInmueble(blanco);
        }

        this.setEmailPropInmueble(inmSel.getEmailPropInmueble());
        this.tipoInmueble.setValue(inmSel.getTipoInmueble());
        this.setAreaInmueble(inmSel.getAreaInmueble().toString());
        this.setAlicInmueble(inmSel.getAlicInmueble().toString());
        this.setNotaRecInmueble(inmSel.getNotaRecInmueble());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(inmSel.getCodInmueble());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarInmueble() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        String guion = "-";
        Inmueble inmuebleNuevo = new Inmueble();

        inmuebleNuevo.setCodInmueble(codInmueble);
        inmuebleNuevo.setNombPropInmueble(nombPropInmueble);
        inmuebleNuevo.setCedRifPropInmueble(cedRifPropInmueble);
        inmuebleNuevo.setDirInmueble(dirInmueble);
        // ---------------------------------------------------------- //
        // En caso de que haya dejado el telefono o el email en blanco//
        // ---------------------------------------------------------- //
        String blanco = "";

        if (codTelInmueble.equals(blanco) | numTelInmueble.equals(blanco)) {
            inmuebleNuevo.setTelInmueble(blanco);
        } else {
            inmuebleNuevo.setTelInmueble(codTelInmueble.concat(guion.concat(numTelInmueble)));
        }
        if (emailPropInmueble.equals(blanco)) {
            inmuebleNuevo.setEmailPropInmueble(blanco);
        } else {
            inmuebleNuevo.setEmailPropInmueble(emailPropInmueble);
        }
        inmuebleNuevo.setTipoInmueble(tipoInmueble.getValue().toString());
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double area = Double.valueOf(areaInmueble).doubleValue();
        inmuebleNuevo.setAreaInmueble(area);
        Double alic = Double.valueOf(alicInmueble).doubleValue();
        inmuebleNuevo.setAlicInmueble(alic);
        //-----------------------------------------------------------

        inmuebleNuevo.setNotaRecInmueble(notaRecInmueble);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idProp = beanUsuario.getIdPropActual();

        inmuebleNuevo.setIdProp(idProp);

        buscarPropiedad(idProp);
        inmuebleNuevo.setPropiedad(propiedad);
        Integer idInm;

        // -------------- generar y registrar el codigo de acceso y establecer vinculo con el inmueble

        Integer idcodigoAcceso;
        idcodigoAcceso = asignarCodigoAcceso();
        if (idcodigoAcceso == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(Codigo Acceso)...por favor, intente mas tarde"));
            return;
        }
        CodigoAcceso codAccesoObj = new CodigoAcceso();
        codAccesoObj = buscarCodigoAcceso(idcodigoAcceso);
        inmuebleNuevo.setCodigoAcceso(codAccesoObj);

        // --------------
        
        try {
            //almacenarOModificarEntidad(inmuebleNuevo);     *************Modificado para la Consulta Express ya que se require el idInm despues de almacenar
            idInm = almacenarEntidad(inmuebleNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El inmueble " + inmuebleNuevo.getCodInmueble() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public Integer asignarCodigoAcceso() {

        Boolean codExiste = true;
        Integer cont;
        Integer idAcceso = null;

        while (codExiste) {

            //Generar Codigo de acceso:

            Random r = new Random();
            Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
            Long seed = fecCal.getTimeInMillis();
            Integer ent = Long.valueOf(seed).intValue();
            if (ent < 0) {
                ent = ent * -1;
            }
            cont = r.nextInt(ent);
            idAcceso = cont;

            // Verificar si id ya existe en la tabla de codigos

            CodigoAcceso codAccesoObj = new CodigoAcceso();
            codAccesoObj = buscarCodigoAcceso(idAcceso);

            if (codAccesoObj == null) {
                // codigo de acceso no existe
                codExiste = false;
            }
        }
        // Almacenar en tabla

        CodigoAcceso codigoAccesoNuevo = new CodigoAcceso();

        codigoAccesoNuevo.setId(idAcceso);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        // Buscar Condominio

        Condominio condominio = null;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidad(idCond, nombQuery);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(Condominio)...por favor, intente mas tarde"));

            return null;
        }
        codigoAccesoNuevo.setCondominio(condominio);

        DaoQuery<CodigoAcceso> dataAcces1 = new DaoQuery<CodigoAcceso>();
        Integer idCodigoAccesoNuevo;
        try {
            idCodigoAccesoNuevo = dataAcces1.almacenarEntidad(codigoAccesoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones.(Almacenar codAcc)..por favor, intente mas tarde"));

            return null;
        }

        return idCodigoAccesoNuevo;
    }

    public CodigoAcceso buscarCodigoAcceso(Integer idAcceso) {

        String nombQuery = "buscarCodigoAccesoPorId";

        DaoQuery<CodigoAcceso> dataAcces = new DaoQuery<CodigoAcceso>();
        CodigoAcceso codAccesoObj = new CodigoAcceso();

        try {
            codAccesoObj = dataAcces.buscarEntidad(idAcceso, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(buscar codAcceso)...por favor, intente mas tarde"));

        }
        return codAccesoObj;
    }

    public void modificarInmueble() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        String guion = "-";
        FacesContext ctx = FacesContext.getCurrentInstance();
        Inmueble inmSel = (Inmueble) ctx.getExternalContext().getSessionMap().get("Inmueble");

        inmSel.setCodInmueble(codInmueble);
        inmSel.setNombPropInmueble(nombPropInmueble);
        inmSel.setCedRifPropInmueble(cedRifPropInmueble);
        inmSel.setDirInmueble(dirInmueble);
        // ---------------------------------------------------------- //
        // En caso de que haya dejado el telefono o el email en blanco//
        // ---------------------------------------------------------- //
        String blanco = "";

        if (codTelInmueble.equals(blanco) | numTelInmueble.equals(blanco)) {
            inmSel.setTelInmueble(blanco);
        } else {
            inmSel.setTelInmueble(codTelInmueble.concat(guion.concat(numTelInmueble)));
        }
        if (emailPropInmueble.equals(blanco)) {
            inmSel.setEmailPropInmueble(blanco);
        } else {
            inmSel.setEmailPropInmueble(emailPropInmueble);
        }

        inmSel.setTipoInmueble(tipoInmueble.getValue().toString());
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double area = Double.valueOf(areaInmueble).doubleValue();
        inmSel.setAreaInmueble(area);
        Double alic = Double.valueOf(alicInmueble).doubleValue();
        inmSel.setAlicInmueble(alic);
        //-----------------------------------------------------------

        inmSel.setNotaRecInmueble(notaRecInmueble);

        try {
            almacenarOModificarEntidad(inmSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El inmueble " + inmSel.getCodInmueble() + " ha sido modificado satisfactoriamente!"));

        return;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setCodInmueble(String codInmueble) {
        this.codInmueble = codInmueble;
    }

    public String getCodInmueble() {
        return codInmueble;
    }

    public void setNombPropInmueble(String nombPropInmueble) {
        this.nombPropInmueble = nombPropInmueble;
    }

    public String getNombPropInmueble() {
        return nombPropInmueble;
    }

    public void setCedRifPropInmueble(String cedRifPropInmueble) {
        this.cedRifPropInmueble = cedRifPropInmueble;
    }

    public String getCedRifPropInmueble() {
        return cedRifPropInmueble;
    }

    public void setDirInmueble(String dirInmueble) {
        this.dirInmueble = dirInmueble;
    }

    public String getDirInmueble() {
        return dirInmueble;
    }

    public void setCodTelInmueble(String codTelInmueble) {
        this.codTelInmueble = codTelInmueble;
    }

    public String getCodTelInmueble() {
        return codTelInmueble;
    }

    public void setNumTelInmueble(String numTelInmueble) {
        this.numTelInmueble = numTelInmueble;
    }

    public String getNumTelInmueble() {
        return numTelInmueble;
    }

    public void setEmailPropInmueble(String emailPropInmueble) {
        this.emailPropInmueble = emailPropInmueble;
    }

    public String getEmailPropInmueble() {
        return emailPropInmueble;
    }

    public void setTipoInmueble(HtmlSelectOneMenu tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public HtmlSelectOneMenu getTipoInmueble() {
        return tipoInmueble;
    }

    public void setAreaInmueble(String areaInmueble) {
        this.areaInmueble = areaInmueble;
    }

    public String getAreaInmueble() {
        return areaInmueble;
    }

    public void setAlicInmueble(String alicInmueble) {
        this.alicInmueble = alicInmueble;
    }

    public String getAlicInmueble() {
        return alicInmueble;
    }

    public void setNotaRecInmueble(String notaRecInmueble) {
        this.notaRecInmueble = notaRecInmueble;
    }

    public String getNotaRecInmueble() {
        return notaRecInmueble;
    }

    // .....
    public void setFilaSeleccionada(Inmueble filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public Inmueble getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
