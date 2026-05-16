package informes.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Cuenta;
import informes.modelos.MovBanRepDT;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.MovBancario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class MovBanRepCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    public String idBanco;
    public String idCuenta;
    private String fecDesde;
    private String fecHasta;
    private Collection<MovBancario> listaMovBan = new ArrayList<MovBancario>();
    private Collection<MovBanRepDT> listaMovBanDT = new ArrayList<MovBanRepDT>();
    private Collection listaOrd = new ArrayList();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private Condominio condominio;
    private Calendar fecDesdeCal;
    private Calendar fecHastaCal;
    private Calendar fecCreacion;
    Integer idBan;
    Integer idCta;

    public MovBanRepCtrl() {
    }

    public void inicializarPanel() {

        this.setFecDesde(null);
        this.setFecHasta(null);

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "informes";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idBanco.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el Banco ...";
            return mensaje;
        }

        if (idCuenta.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione la Cuenta ...";
            return mensaje;
        }

        if (this.getFecDesde().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha desde la cual desea el informe...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDesde())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFecHasta().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha hasta la cual desea el informe...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecHasta())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        buscarCondominio();
        //-----------------------------------------------------------
        //       obtiene la fecha de creacion del condominio
        //-----------------------------------------------------------
        fecCreacion = condominio.getFecCreacion();
        //-----------------------------------------------------------
        //       la fecha obtenida se pasa a string
        //-----------------------------------------------------------
        String diaString;
        String mesString;

        int dia = fecCreacion.get(Calendar.DAY_OF_MONTH);
        int mes = fecCreacion.get(Calendar.MONTH) + 1;
        int año = fecCreacion.get(Calendar.YEAR);

        if (dia < 10) {
            diaString = "0" + dia;
        } else {
            diaString = String.valueOf(dia);
        }

        if (mes < 10) {
            mesString = "0" + mes;
        } else {
            mesString = String.valueOf(mes);
        }

        String stringFechaCreacion = diaString + "-" + mesString + "-" + String.valueOf(año);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fechaHoy = new GregorianCalendar();
        //-----------------------------------------------------------
        //          Convierte el rango de fechas a calendar 
        //-----------------------------------------------------------
        fecDesdeCal = convertirFecha(fecDesde);
        fecHastaCal = convertirFecha(fecHasta);
        //-----------------------------------------------------------
        //     Valida que las fechas esten en un rango valido
        //-----------------------------------------------------------

        if (fecHastaCal.before(fecDesdeCal)) {
            mensaje = "La fecha (hasta) es menor que la fecha (desde), por favor intente de nuevo...";
            return mensaje;
        }
        if (!(fecDesdeCal.after(fecCreacion) | fecDesdeCal.equals(fecCreacion))) {
            mensaje = "La fecha (desde) debe ser mayor o igual a " + "" + stringFechaCreacion + "" + ", por favor intente de nuevo...";
            return mensaje;
        }
        if (!(fecHastaCal.before(fechaHoy) | fecHastaCal.equals(fechaHoy))) {
            mensaje = "La fecha (hasta) debe ser menor o igual a la fecha de hoy, por favor intente de nuevo...";
            return mensaje;
        }
        fecHastaCal = convertirFechaHasta(fecHasta);
        return mensaje;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Calendar convertirFechaHasta(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 25);
        cal.set(Calendar.MILLISECOND, 0);

        System.out.println("la fecha hasta es: " + cal.toString());
        return cal;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void generarMovBancario(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        idBan = Integer.parseInt(idBanco);
        idCta = Integer.parseInt(idCuenta);
        //--------------------------------------------------------
        // busca los Movimientos de la cuenta seleccionada el  rango dado
        //--------------------------------------------------------
        listaMovBan = getMovBancarioPorCuenta(idCta, fecDesdeCal, fecHastaCal);
        if (listaMovBan.isEmpty()) {
            mensaje = "No hay movimientos bancarios registrados, para el Banco y la Cuenta seleccionadas ";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        listaMovBanDT = convertirMovBanADT(listaMovBan);
        generarReporteMovBancario(listaMovBanDT);
    }

    public Collection getMovBancarioPorCuenta(Integer idCta, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarMovBancariosPorIdCtaRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idCta, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;

    }

    private Collection<MovBanRepDT> convertirMovBanADT(Collection listaMovBan) {

        Collection listaMovBanDT = new ArrayList();
        Double mto;
        if (listaMovBan == null) {
            return listaMovBanDT;
        }
        for (Iterator<MovBancario> it = listaMovBan.iterator();
                it.hasNext();) {

            MovBancario mov = (MovBancario) it.next();
            if (mov == null) {// Si no hay movimientos bancarios devuelve la lista en blanco
                break;
            } else {

                MovBanRepDT movBanDT = new MovBanRepDT();

                movBanDT.setId(mov.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = mov.getFecMovBancario();
                String dia = "";
                String mes = "";
                int diaFac = fechaFac.get(Calendar.DAY_OF_MONTH);
                int mesFac = fechaFac.get(Calendar.MONTH) + 1;
                int añoFac = fechaFac.get(Calendar.YEAR);

                if (diaFac < 10) {
                    dia = "0" + diaFac;
                } else {
                    dia = String.valueOf(diaFac);
                }

                if (mesFac < 10) {
                    mes = "0" + mesFac;
                } else {
                    mes = String.valueOf(mesFac);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoFac;

                movBanDT.setFecMovBan(stringFechaDp);
                movBanDT.setTipoMovBan(mov.getTipoOperMovBancario());
                movBanDT.setRefMovBan(mov.getRefMovBancario());
                movBanDT.setConceptoMovBan(mov.getDescMovBancario());
                mto = mov.getMtoMovBancario();
                if (mto > 0) {
                    movBanDT.setAbonoMovBan(mov.getMtoMovBancario());
                } else {
                    movBanDT.setMtoMovBan(mov.getMtoMovBancario());
                }

                listaMovBanDT.add(movBanDT);
            }
        }
        return listaMovBanDT;
    }

    public void generarReporteMovBancario(Collection movimientos) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/MovBanRep.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //------------------------------------------------------------
        // Se crea el datasource de movimientos
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceMovimientos;

        dataSourceMovimientos = new JRBeanCollectionDataSource(movimientos);

        Banco bco = buscarBanco(idBan);
        Cuenta cta = buscarCuenta(idCta);

        String nombBanco = bco.getNombBanco();
        String numCuenta = cta.getNumCuenta();
        String tipoCuenta = cta.getTipoCuenta();
        Double saldoCuenta = cta.getSaldoActCuenta();

        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio = buscarCondominio(idCond);
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("nombBanco", nombBanco);
        parametros.put("numCuenta", numCuenta);
        parametros.put("tipoCuenta", tipoCuenta);
        parametros.put("saldoCuenta", saldoCuenta);
        parametros.put("desde", fecDesde);
        parametros.put("hasta", fecHasta);
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceMovimientos);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public List getBancosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------
        Collection listBan = new ArrayList();

        listBan.add(new SelectItem(new Integer(0), "Seleccione"));
        List bancos = new ArrayList();

        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            bancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        /*
         * --------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS
         * ---------------------------------
         */

        if (bancos.isEmpty()) {

            listBan.clear();
            listBan.add(new SelectItem(new Integer(0), "Sin Configurar"));
            return (List) listBan;
        }

        /*
         * ---------------------------------------------------------------------------------
         */

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listBan;
    }

    public Collection getCuentasPorBanco() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));
        if (idBanco == null) {
            return listaOrd;
        }

        String nombQuery = "buscarCuentasPorIdBanco";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        Integer idBcoInteger = Integer.parseInt(idBanco);
        try {
            cuentas = dataAcces.buscarListaEntidadesPorId(idBcoInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaOrd;
        }

        if (cuentas.isEmpty()) {
            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            return listaOrd;
        }

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            if (!(cta.getEdoCuenta().equals("INA"))) {
                listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
            }
        }

        return listaOrd;
    }

    public Banco buscarBanco(Integer idBco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return banco;
    }

    public Cuenta buscarCuenta(Integer idCta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return cuenta;
    }

    public Condominio buscarCondominio(Integer idCond) throws ValidatorException {

        Condominio condominio;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setFecDesde(String fecDesde) {
        this.fecDesde = fecDesde;
    }

    public String getFecDesde() {
        return fecDesde;
    }

    public void setFecHasta(String fecHasta) {
        this.fecHasta = fecHasta;
    }

    public String getFecHasta() {
        return fecHasta;
    }
}
