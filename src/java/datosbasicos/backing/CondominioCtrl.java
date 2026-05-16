/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Categoria;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.CondominioDT;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import saas.modelos.Plan;
import saas.modelos.Suscripcion;
import util.*;

public class CondominioCtrl extends DaoCrud<Condominio, Propiedad, Integer> implements Serializable {

    private String nombCondominio;
    private String dirCondominio;
    private String nombAdminCondominio;
    private String emailCondominio;
    private String cedRifCondominio;
    private String codTelOficCondominio;
    private String numTelOficCondominio;
    private String codTelAdminCondominio;
    private String numTelAdminCondominio;
    private CondominioDT filaSeleccionada;
    private Integer numCondominios;
    private Suscripcion sus;
    private Plan plan;
    private Collection<CondominioDT> listaCondominioDT = new ArrayList<CondominioDT>();

    public CondominioCtrl() {
    }

    public void inicializarPanel() {

        this.setNombCondominio(null);
        this.setDirCondominio(null);
        this.setNombAdminCondominio(null);
        this.setEmailCondominio(null);
        this.setCedRifCondominio(null);
        this.setCodTelOficCondominio(null);
        this.setNumTelOficCondominio(null);
        this.setCodTelAdminCondominio(null);
        this.setNumTelAdminCondominio(null);

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
        Integer cero = 0;

        Integer idBean = beanUsuario.getIdCondActual();
        if (idBean.equals(cero)) {
            return "selcond";
        }
        return "datosbasicos";

    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (this.getNombCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del condominio...";
            return mensaje;
        }
        if (!val.esNombApell(this.getNombCondominio())) {
            mensaje = "El nombre del Condominio debe ser alfabético";
            return mensaje;
        }
        if (this.getCedRifCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la cédula del administrador o el Rif del condominio...";
            return mensaje;
        }

        if (!val.esCedRif(this.getCedRifCondominio())) {
            mensaje = "La cédula debe comenzar con V o E, el Rif con J o G, por favor intente de nuevo...";
            return mensaje;
        }

        if (this.getDirCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la dirección del condominio...";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(this.getDirCondominio())) {
            mensaje = "La dirección tiene caracteres inválidos, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNombAdminCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el nombre del administrador del condominio...";
            return mensaje;
        }

        if (!val.esAlfa(this.getNombAdminCondominio())) {
            mensaje = "El nombre del administrador debe ser alfabético, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getEmailCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el correo electrónico del administrador/condominio...";
            return mensaje;
        }
        if (!val.esEmail(this.getEmailCondominio())) {
            mensaje = "El formato del correo es inválido, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getCodTelOficCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de área del número de teléfono del la oficina del condominio...";
            return mensaje;
        }
        if (!val.esEntero(this.getCodTelOficCondominio())) {
            mensaje = "El código de area del teléfono de la oficina del condominio debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodTelOficCondominio())) {
            mensaje = "El código de área del teléfono de la ofic. debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumTelOficCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono del la oficina del condominio...";
            return mensaje;
        }
        if (!val.esEntero(this.getNumTelOficCondominio())) {
            mensaje = "El número de teléfono de la oficina del condominio debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getCodTelAdminCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el código de area del teléfono del administrador del condominio...";
            return mensaje;
        }
        if (!val.esEntero(this.getCodTelAdminCondominio())) {
            mensaje = "El código de área del teléfono del administrador del condominio debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esMinCuatroCar(this.getCodTelAdminCondominio())) {
            mensaje = "El código de área del teléfono del administrador debe ser de cuatro caracteres, por favor intente de nuevo";
            return mensaje;
        }
        if (this.getNumTelAdminCondominio().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de teléfono del administrador del condominio...";
            return mensaje;
        }
        if (!val.esEntero(this.getNumTelAdminCondominio())) {
            mensaje = "El número de teléfono del administrador del condominio debe ser numérico, por favor intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public void validarEmail(FacesContext context, UIComponent toValidate,
            Object value) throws ValidatorException {
        String eMail = (String) value;
        if (eMail.indexOf("@") < 0) {
            FacesMessage message = new FacesMessage("Correo electrónico inválido!");
            throw new ValidatorException(message);
        }
    }

    public List getCondominiosPorUsuario() {

        // Obtiene los condominios para el usuario dado
        // Obtiene el codUsuario, del usuario actual, del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String codUsuario = beanUsuario.getCodUsuario();

        listaCondominioDT.clear();
        List listCond = new ArrayList();
     //   List listCond1 = new ArrayList();
        String nombQuery = "buscarCondominiosUsuario";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listCond = dataAcces.buscarListaEntidadesPorCodigo(codUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collections.sort(listCond, new Comparador());

        numCondominios = listCond.size();

        if (listCond.isEmpty()) {
            return (List) listaCondominioDT;
        } else {
            inicializarCondominioDT(listCond);
            return (List) listaCondominioDT;
        }
    }

    private Collection<CondominioDT> inicializarCondominioDT(Collection listCond) {

        for (Iterator<Condominio> it = listCond.iterator();
                it.hasNext();) {

            Condominio cond = (Condominio) it.next();
            if (cond == null) {// Si no hay CondominioS DEFINIDOS devuelve la lista en blanco
                break;
            } else {

                CondominioDT CondominioDT = new CondominioDT();

                CondominioDT.setId(cond.getId());
                CondominioDT.setNombCondominio(cond.getNombCondominio());
                CondominioDT.setDirCondominio(cond.getDirCondominio());
                CondominioDT.setNombAdminCondominio(cond.getNombAdminCondominio());
                CondominioDT.setEmailCondominio(cond.getEmailCondominio());
                CondominioDT.setCedRifCondominio(cond.getCedRifCondominio());
                CondominioDT.setTelOficCondominio(cond.getTelOficCondominio());
                CondominioDT.setTelAdminCondominio(cond.getTelAdminCondominio());
                CondominioDT.setNombLogo(cond.getNombLogo());

                // convertir periodo actual  de Calendar a String para mostrar en el DT

                Calendar fechaMov = cond.getPeriodoActual();

                String mesM = "";
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (mesMov < 10) {
                    mesM = "0" + mesMov;
                } else {
                    mesM = String.valueOf(mesMov);
                }

                String stringFechaMov = mesM + "-" + añoMov;

                CondominioDT.setPeriodoActual(stringFechaMov);
                CondominioDT.setFecCreacion(cond.getFecCreacion());

                listaCondominioDT.add(CondominioDT);
            }
        }
        return listaCondominioDT;
    }

    public void editarCondominioAction() {

        CondominioDT condSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Condominio" en el map #{Condominio} 
        ctx.getExternalContext().getSessionMap().put("CondominioDT", condSel);

        this.setNombCondominio(condSel.getNombCondominio());
        this.setDirCondominio(condSel.getDirCondominio());
        this.setNombAdminCondominio(condSel.getNombAdminCondominio());
        this.setEmailCondominio(condSel.getEmailCondominio());
        this.setCedRifCondominio(condSel.getCedRifCondominio());
        //------------------------------------------------------------------------
        // se separan los telefonos cod + telefonos para mostrarlos en el panel 1
        //------------------------------------------------------------------------
        this.setCodTelOficCondominio(condSel.getTelOficCondominio().substring(0, 4));
        this.setNumTelOficCondominio(condSel.getTelOficCondominio().substring(5));
        this.setCodTelAdminCondominio(condSel.getTelAdminCondominio().substring(0, 4));
        this.setNumTelAdminCondominio(condSel.getTelAdminCondominio().substring(5));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setNombEntidadElim(condSel.getNombCondominio());

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public Suscripcion buscarSuscripcion(Integer idUsuario) {

        String nombQuery = "buscarSuscripcionPorIdUsuario";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            sus = dataAcces.buscarEntidadPorId(idUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return sus;
    }

    public Plan buscarPlanSuscriptor(Integer idSuscripcion) {

        String nombQuery = "buscarPlanPorIdSuscripcion";
        DaoQuery<Plan> dataAcces = new DaoQuery<Plan>();
        try {
            plan = dataAcces.buscarEntidadPorId(idSuscripcion, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return plan;
    }

    public void registrarCondominio() throws ValidatorException {

        //----------------------------------------------------------------
        // se valida que el usuario aun no haya alcanzado el numero max
        // de condominios permitido por su suscripcion
        //----------------------------------------------------------------

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idUsuario = beanUsuario.getIdUsuario();

        sus = buscarSuscripcion(idUsuario);
        plan = buscarPlanSuscriptor(sus.getId());

        if (numCondominios.equals(plan.getNroMaxCondPlan())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Estimado usuario, usted ya creó el número máximo de condominios permitido para su suscripción..."));
            return;
        }
        //----------------------------------------------------------------
        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        String guion = "-";
        Condominio CondominioNuevo = new Condominio();

        CondominioNuevo.setNombCondominio(nombCondominio);
        CondominioNuevo.setDirCondominio(dirCondominio);
        CondominioNuevo.setNombAdminCondominio(nombAdminCondominio);
        CondominioNuevo.setEmailCondominio(emailCondominio);
        CondominioNuevo.setCedRifCondominio(cedRifCondominio);
        CondominioNuevo.setTelOficCondominio(codTelOficCondominio.concat(guion.concat(numTelOficCondominio)));
        CondominioNuevo.setTelAdminCondominio(codTelAdminCondominio.concat(guion.concat(numTelAdminCondominio)));
        CondominioNuevo.setNombLogo("logoTemp.jpg");
        //----------------------------------------------------------------
        // se genera la fecha del  periodo actual como 01mmyyyy (default)
        //----------------------------------------------------------------
        Calendar cal = new GregorianCalendar();

        int dia = new Integer(01);
        int mes = cal.get(Calendar.MONTH);
        int año = cal.get(Calendar.YEAR);
        System.out.println("la fecha obtenida es : " + mes + "" + año);
        Calendar fecha = new GregorianCalendar(año, mes, dia);
        System.out.println("la fecha generada es : " + fecha.get(Calendar.MONTH) + fecha.get(Calendar.YEAR));
        CondominioNuevo.setPeriodoActual(fecha);
        CondominioNuevo.setFecCreacion(fecha);

        String codUsuario = beanUsuario.getCodUsuario();

        Usuario usuario;

        String nombQuery = "buscarUsuario";
        DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
        try {
            usuario = dataAcces.buscarEntidad(codUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        usuario.condominios.add(CondominioNuevo);
        CondominioNuevo.getUsuarios().add(usuario);
        Integer idCond;
        DaoQuery<Condominio> dataAcces1 = new DaoQuery<Condominio>();
        try {
            idCond = dataAcces1.almacenarEntidad(CondominioNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        Condominio cond = crearCategoriasYConceptos(idCond);
        registrarParametro(cond);
        crearBancoYCuenta(cond);
        inicializarPanel();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Condominio " + CondominioNuevo.getNombCondominio() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void crearBancoYCuenta(Condominio cond) {

        //-------------------------------------------------------------------
        //  Se crea un nuevo banco (virtual) para utilizarlo como caja chica
        //  para pagos de efectivo y cheque
        //-------------------------------------------------------------------
        Banco bancoNuevo = new Banco();
        bancoNuevo.setNombBanco("Caja Chica");
        bancoNuevo.setNombOficBanco("Local");
        bancoNuevo.setEdoBanco("ACT");
        bancoNuevo.setTelBanco("0212-1234567");

        bancoNuevo.setCondominio(cond);
        Integer idBanco;

        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            idBanco = dataAcces.almacenarEntidad(bancoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //--------------------------------------------------------------------------
        //  Se busca el banco recien creado para asociarlo a la cta nueva
        //-------------------------------------------------------------------
        Banco banco = null;
        String nombQuery = "buscarBancoPorId";

        DaoQuery<Banco> dataAcces1 = new DaoQuery<Banco>();
        try {
            banco = dataAcces1.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        //-----------------------------------------------------------
        //  Se crea la cuenta de caja chica (virtual)
        //-----------------------------------------------------------
        Cuenta cuentaNueva = new Cuenta();

        cuentaNueva.setNombBanco(idBanco);
        cuentaNueva.setNumCuenta("1234-1234-12-1234567890");
        cuentaNueva.setTipoCuenta("Corriente");

        cuentaNueva.setSaldoIniCuenta(0.00);
        cuentaNueva.setSaldoActCuenta(0.00);
        cuentaNueva.setEdoCuenta("ACT");
        cuentaNueva.setBanco(banco);

        DaoQuery<Cuenta> dataAcces2 = new DaoQuery<Cuenta>();
        try {
            idBanco = dataAcces2.almacenarEntidad(cuentaNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public Condominio crearCategoriasYConceptos(Integer idCond) {

        //-----------------------------------------------------------------
        // Se realiza una configuracion inicial de categorias y conceptos
        // para el condominio recien creado
        //-----------------------------------------------------------------
        Condominio condominio = null;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidad(idCond, nombQuery);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        //-----------------------------------------------------------------
        // Se crean las categorias basicas y sus conceptos relacionados
        //-----------------------------------------------------------------
        Collection<Categoria> categorias = new ArrayList<Categoria>();

        categorias.add(new Categoria("Administrativo", "Conjunto de conceptos administrativos", "ACT", condominio));
        categorias.add(new Categoria("Mantenimiento", "Conjunto de conceptos de mantenimiento", "ACT", condominio));
        categorias.add(new Categoria("Reparaciones", "Conjunto de conceptos de reparaciones", "ACT", condominio));
        categorias.add(new Categoria("Uso comun", "Conjunto de conceptos de uso comun y consumo", "ACT", condominio));

        for (Iterator<Categoria> it = categorias.iterator();
                it.hasNext();) {
            Categoria cat = (Categoria) it.next();
            crearCategoriaCondominio(cat);
        }
        categorias.clear();
        categorias = getCategoriasPorCondominio(idCond);

        Collection<Concepto> administrativos = new ArrayList<Concepto>();

        administrativos.add(new Concepto("Honorarios administrador", "Honorarios del administrador del condominio", "ACT", condominio));
        administrativos.add(new Concepto("Salario conserje", "Salario del conserje del condominio", "ACT", condominio));
        administrativos.add(new Concepto("Honorarios administradora", "Honorarios administradora del condominio", "ACT", condominio));
        administrativos.add(new Concepto("Gastos oficina", "Gastos en material de oficina", "ACT", condominio));
        administrativos.add(new Concepto("Gastos varios", "Gastos fotocopias, impresiones, internet", "ACT", condominio));
        administrativos.add(new Concepto("Retiro prestaciones", "Retiro de prestaciones sociales", "ACT", condominio));
        administrativos.add(new Concepto("Prestaciones soc.", "Prestaciones sociales trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Seguro social", "Seguro social trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Bono Vacacional ", "Bono vacacional trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Liquidación ", "Liquidacion trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Provisión utilidades ", "Utilidades del trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Provisión vacaciones ", "Vacaciones trabajador", "ACT", condominio));
        administrativos.add(new Concepto("Ley de alimentación", "Pago alimentación", "ACT", condominio));
        administrativos.add(new Concepto("FAHO", "Fondo de ahorro obligatorio de la vivienda", "ACT", condominio));
        administrativos.add(new Concepto("Caja chica", "Caja chica", "ACT", condominio));

        Collection<Concepto> mantenimiento = new ArrayList<Concepto>();

        mantenimiento.add(new Concepto("Mant. ascensores", "Mantenimiento de los asecensores", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. hidroneumatico", "Mantenimiento del hidroneumatico", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. porton electrico", "Mantenimiento del porton electrico", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. cerco electrico", "Mantenimiento del cerco electrico", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. areas verdes", "Mantenimiento de areas verdes", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. piscina", "Mantenimiento de la piscina", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. parque infantil", "Mantenimiento del parque infantil", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. camaras vigilancia", "Mantenimiento camaras de vigilancia", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. tuberias", "Mantenimiento de tuberias ", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. estacionamiento", "Mantenimiento del estacionamiento ", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. pintura edif. ", "Pintura de areas comunes ", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. alumbrado elect. ", "Mantenimiento del alumbrado elect.", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. compra prod. limp.", "Gastos productos de limpieza", "ACT", condominio));
        mantenimiento.add(new Concepto("Mant. prod. limpieza ", "Gasto otros productos", "ACT", condominio));

        Collection<Concepto> reparaciones = new ArrayList<Concepto>();

        reparaciones.add(new Concepto("Repar. ascensores", "Reparacion de los asecensores", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. tuberia", "Reparacion de tuberias de aguas", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. porton", "Reparacion del porton electrico", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. cerco elect.", "Reparacion del cerco electrico", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. hidroneumatico.", "Reparacion la bomba de agua", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. camaras.", "Reparacion de camaras", "ACT", condominio));
        reparaciones.add(new Concepto("Repar. electricas.", "Reparacion/Sustitucion de componentes electricos", "ACT", condominio));

        Collection<Concepto> usoComun = new ArrayList<Concepto>();

        usoComun.add(new Concepto("CorpoElec", "Consumo de electricidad", "ACT", condominio));
        usoComun.add(new Concepto("Cantv", "Consumo de telefonos", "ACT", condominio));
        usoComun.add(new Concepto("HidroCapital", "Consumo de agua", "ACT", condominio));
        usoComun.add(new Concepto("Vigilancia", "Gastos por vigilancia", "ACT", condominio));

        for (Iterator<Categoria> it = categorias.iterator();
                it.hasNext();) {
            Categoria cat = (Categoria) it.next();
            if (cat.getNombCategoria().equals("Administrativo")) {
                for (Iterator<Concepto> it1 = administrativos.iterator();
                        it1.hasNext();) {
                    Concepto con = (Concepto) it1.next();
                    con.setCategoria(cat);
                    crearConceptoCondominio(con);
                }
                continue;
            }
            if (cat.getNombCategoria().equals("Mantenimiento")) {
                for (Iterator<Concepto> it1 = mantenimiento.iterator();
                        it1.hasNext();) {
                    Concepto con = (Concepto) it1.next();
                    con.setCategoria(cat);
                    crearConceptoCondominio(con);
                }
                continue;
            }
            if (cat.getNombCategoria().equals("Reparaciones")) {
                for (Iterator<Concepto> it1 = reparaciones.iterator();
                        it1.hasNext();) {
                    Concepto con = (Concepto) it1.next();
                    con.setCategoria(cat);
                    crearConceptoCondominio(con);
                }
                continue;
            }
            if (cat.getNombCategoria().equals("Uso comun")) {
                for (Iterator<Concepto> it1 = usoComun.iterator();
                        it1.hasNext();) {
                    Concepto con = (Concepto) it1.next();
                    con.setCategoria(cat);
                    crearConceptoCondominio(con);
                }
            }
        }
        return condominio;
    }

    public void crearCategoriaCondominio(Categoria cat) {

        DaoQuery<Categoria> dataAcces = new DaoQuery<Categoria>();
        try {
            dataAcces.almacenarEntidad(cat);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void crearConceptoCondominio(Concepto con) {

        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            dataAcces.almacenarEntidad(con);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public Collection getCategoriasPorCondominio(Integer idCond) {

        Collection<Categoria> listaCat = new ArrayList<Categoria>();
        String nombQuery = "buscarCategoriasPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaCat = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listaCat;
    }

    public void registrarParametro(Condominio cond) {


        Parametro parametroNuevo = new Parametro();
        //-----------------------------------------------------------------------------------
        //Convertir de String a Double o Integer segun sea el caso antes de pasarlo a Parametro
        //-----------------------------------------------------------------------------------
        int prox = 1;
        double mtoFijo = 0;
        float cero = (float) 0.0;
        String tipoFac = "gastos";
        String observacion = "Felicitaciones por la puntualidad en sus pagos!";

        parametroNuevo.setProxAvisoCobro(prox);
        parametroNuevo.setProxReciboCobro(prox);

        parametroNuevo.setProxNotaDebCxC(prox);
        parametroNuevo.setProxNotaCredCxC(prox);

        parametroNuevo.setProxNotaDebCxP(prox);
        parametroNuevo.setProxNotaCredCxP(prox);
        parametroNuevo.setTipoFacturacion(tipoFac);
        parametroNuevo.setMtoFijoFacturacion(mtoFijo);
        parametroNuevo.setPorcMora(cero);

        parametroNuevo.setObsAvisoCobro(observacion);
        parametroNuevo.setObsReciboPago(observacion);
        //-----------------------------------------------------------------------------------

        parametroNuevo.setCondominio(cond);

        DaoQuery<Parametro> dataAcces = new DaoQuery<Parametro>();
        try {
            dataAcces.almacenarEntidad(parametroNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void modificarCondominio() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        CondominioDT condSel = (CondominioDT) ctx.getExternalContext().getSessionMap().get("CondominioDT");

        // Con el id de condominio seleccionado buscamos el objeto persitido 

        Condominio condominio = null;
        Integer idCond = condSel.getId();
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidad(idCond, nombQuery);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        // 
        String guion = "-";
        condominio.setNombCondominio(nombCondominio);
        condominio.setDirCondominio(dirCondominio);
        condominio.setNombAdminCondominio(nombAdminCondominio);
        condominio.setEmailCondominio(emailCondominio);
        condominio.setCedRifCondominio(cedRifCondominio);
        condominio.setTelOficCondominio(codTelOficCondominio.concat(guion.concat(numTelOficCondominio)));
        condominio.setTelAdminCondominio(codTelAdminCondominio.concat(guion.concat(numTelAdminCondominio)));



        try {
            almacenarOModificarEntidad(condominio);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Condominio " + condSel.getNombCondominio() + " ha sido modificado satisfactoriamente!"));
    }

    public void setNombCondominio(String nombCondominio) {
        this.nombCondominio = nombCondominio;
    }

    public String getNombCondominio() {
        return nombCondominio;
    }

    public void setDirCondominio(String dirCondominio) {
        this.dirCondominio = dirCondominio;
    }

    public String getDirCondominio() {
        return dirCondominio;
    }

    public void setNombAdminCondominio(String nombAdminCondominio) {
        this.nombAdminCondominio = nombAdminCondominio;
    }

    public String getNombAdminCondominio() {
        return nombAdminCondominio;
    }

    public void setEmailCondominio(String emailCondominio) {
        this.emailCondominio = emailCondominio;
    }

    public String getEmailCondominio() {
        return emailCondominio;
    }

    public void setCedRifCondominio(String cedRifCondominio) {
        this.cedRifCondominio = cedRifCondominio;
    }

    public String getCedRifCondominio() {
        return cedRifCondominio;
    }

    public void setCodTelOficCondominio(String codTelOficCondominio) {
        this.codTelOficCondominio = codTelOficCondominio;
    }

    public String getCodTelOficCondominio() {
        return codTelOficCondominio;

    }

    public void setNumTelOficCondominio(String numTelOficCondominio) {
        this.numTelOficCondominio = numTelOficCondominio;
    }

    public String getNumTelOficCondominio() {
        return numTelOficCondominio;

    }

    public void setCodTelAdminCondominio(String codTelAdminCondominio) {
        this.codTelAdminCondominio = codTelAdminCondominio;
    }

    public String getCodTelAdminCondominio() {
        return codTelAdminCondominio;

    }

    public void setNumTelAdminCondominio(String numTelAdminCondominio) {
        this.numTelAdminCondominio = numTelAdminCondominio;
    }

    public String getNumTelAdminCondominio() {
        return numTelAdminCondominio;

    }
    // .....

    public void setFilaSeleccionada(CondominioDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public CondominioDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
