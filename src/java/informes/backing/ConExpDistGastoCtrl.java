package informes.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import informes.modelos.GastoDT;
import java.io.FileInputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.TranFondo;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ConExpDistGastoCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private String fecDesde;
    private String fecHasta;
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunExt> listaGCExt = new ArrayList<GastoComunExt>();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<GastoDT> listaDT = new ArrayList<GastoDT>();
    private Collection<GastoDT> listaGastosDT = new ArrayList<GastoDT>();
    private Collection<TranFondo> listaTranFondo = new ArrayList<TranFondo>();
    private Condominio condominio;
    private Calendar fecDesdeCal;
    private Calendar fecHastaCal;
    private Calendar fecCreacion;

    public ConExpDistGastoCtrl() {
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

        return "salir";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (this.getFecDesde().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha desde la cual desea el estado de cuenta...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDesde())) {
            mensaje = "La fechas  deben tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFecHasta().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha hasta la cual desea el estado de cuenta...";
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
        if (!(fecDesdeCal.before(fecHastaCal))) {
            mensaje = "La fecha (desde) debe ser menor a la fecha hasta" + "" + ", por favor intente de nuevo...";
            return mensaje;
        }

        String espacio = " ";
        if (!(fecDesdeCal.after(fecCreacion) | fecDesdeCal.equals(fecCreacion))) {
            mensaje = "La fecha (desde) debe ser mayor o igual a" + espacio + stringFechaCreacion + espacio + ", por favor intente de nuevo...";
            return mensaje;
        }
        if (!fecHastaCal.before(fechaHoy) | fecHastaCal.equals(fechaHoy)) {
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

    public void generarReporteDistGasto(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        //--------------------------------------------------------
        // busca los gastos del periodo del condominio actual
        // incluye las transacciones de fondo
        //--------------------------------------------------------
        listaGCOrd.clear();
        listaGCExt.clear();
        listaGNC.clear();
        listaTranFondo.clear();
        listaDT.clear();
        listaGastosDT.clear();

        listaGCOrd = buscarGastosComunesOrd(condominio.getId(), fecDesdeCal, fecHastaCal);
        listaGCExt = buscarGastosComunesExt(condominio.getId(), fecDesdeCal, fecHastaCal);
        listaGNC = buscarGastosNoComunes(condominio.getId(), fecDesdeCal, fecHastaCal);

        //---------------------------------------------------------
        // Se verifica que los parametros hayan sido configurados
        //---------------------------------------------------------

        Parametro parametro = condominio.getParametro();
        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...reporte cancelado"));
            return;
        }
        //--------------------------------------------------------
        String tipoFac = parametro.getTipoFacturacion();

        listaTranFondo = buscarTranFondo(condominio.getId(), fecDesdeCal, fecHastaCal, tipoFac);

        listaDT = convertirGastosADT(listaGCOrd, listaGCExt, listaGNC, listaTranFondo);

        listaGastosDT = ordenarLista(listaDT);// para ordenar la lista de gastos

        if (listaGastosDT.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se han registrado gastos para el periodo seleccionado...por favor, intente de nuevo"));
            return;
        }

        generarReporteDistribucion(listaGastosDT);
    }

    public Collection buscarGastosComunesOrd(Integer idCond, Calendar fecDesdeCal, Calendar fecHastaCal) {

        String nombQuery = "buscarGastoComunOrdPorIdCondRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCOrd = dataAcces.buscarListaEntidadesPorIdRango(idCond, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return listaGCOrd;
    }

    public Collection buscarGastosComunesExt(Integer idCond, Calendar fecDesdeCal, Calendar fecHastaCal) {

        String nombQuery = "buscarGastoComunExtPorIdCondRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCExt = dataAcces.buscarListaEntidadesPorIdRango(idCond, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return listaGCExt;
    }

    public Collection buscarGastosNoComunes(Integer idCond, Calendar fecDesdeCal, Calendar fecHastaCal) {

        String nombQuery = "buscarGastoNoComunPorIdCondRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGNC = dataAcces.buscarListaEntidadesPorIdRango(idCond, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return listaGNC;
    }

    private Collection<TranFondo> buscarTranFondo(Integer idCond, Calendar fecDesdeCal, Calendar fecHastaCal, String tipoFac) {

        //---------------------------------------------------------------------------
        // En caso de que el tipo de facturacion sea por alicuota, se verifica la
        // existencia del fondo de reserva, si existe, se pasa a la coleccion la 
        // transaccion correspondiente al perido actual. Lo mismo aplica para el 
        // fondo de prestaciones
        //---------------------------------------------------------------------------

        Collection<TranFondo> fondos = new ArrayList<TranFondo>();
        Collection<TranFondo> prestaciones = new ArrayList<TranFondo>();
        Collection<TranFondo> tranFondoRes = new ArrayList<TranFondo>();

        if (tipoFac.equals("gastos")) {
            Fondo fondo = null;
            fondo = buscarFondoReserva(idCond);
            if (fondo != null) {
                tranFondoRes = buscarTranFondoReserva(fondo.getId(), fecDesdeCal, fecHastaCal);
                if (!tranFondoRes.isEmpty()) {
                    for (Iterator<TranFondo> it = tranFondoRes.iterator();
                            it.hasNext();) {
                        TranFondo tranFondoReserva = (TranFondo) it.next();
                        fondos.add(tranFondoReserva);
                    }
                }
            }

        }

        Fondo fondoPres = null;
        fondoPres = buscarFondoPrestaciones(idCond);

        if (fondoPres != null) {
            prestaciones = buscarTranFondoPrestaciones(fondoPres.getId(), fecDesdeCal, fecHastaCal);
            if (!prestaciones.isEmpty()) {
                for (Iterator<TranFondo> it = prestaciones.iterator();
                        it.hasNext();) {
                    TranFondo tranFondoPres = (TranFondo) it.next();
                    fondos.add(tranFondoPres);
                }
            }
        }

        return fondos;
    }

    public Fondo buscarFondoReserva(Integer idCond) {

        String nombQuery = "buscarFondoReservaPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        Fondo fondo = null;
        try {
            fondo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fondo;
    }

    public Fondo buscarFondoPrestaciones(Integer idCond) {

        String nombQuery = "buscarFondoPrestacionesPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        Fondo fondo = null;
        try {
            fondo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fondo;
    }

    private Collection buscarTranFondoReserva(Integer idFondoRes, Calendar fecDesdeCal, Calendar fecHastaCal) {


        Collection<TranFondo> fondoRes;

        String nombQuery = "buscarTranFondoResPorIdFondoRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            fondoRes = dataAcces.buscarListaEntidadesPorIdRango(idFondoRes, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return fondoRes;
    }

    private Collection<TranFondo> buscarTranFondoPrestaciones(Integer idFondoPres, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection<TranFondo> prestaciones = new ArrayList<TranFondo>();

        String nombQuery = "buscarTranFondoPorIdFondoRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            prestaciones = dataAcces.buscarListaEntidadesPorIdRango(idFondoPres, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return prestaciones;
    }

    public Collection convertirGastosADT(Collection listaGCOrd, Collection listaGCExt, Collection listaGNC, Collection listaTranFondo) {

        //-------------------------------------------------------
        //Genera el monto y la categoria del gasto comun ord
        //-------------------------------------------------------
        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            GastoDT gasto = new GastoDT();
            Integer id = gco.getIdConcepto();
            String cat = buscarCategoria(id);
            gasto.setDesCategoria(cat);
            gasto.setMtoCategoria(gco.getMtoGastoComunOrd());
            listaDT.add(gasto);
        }
        //-------------------------------------------------------
        //Genera el monto y la categoria del gasto comun ext
        //-------------------------------------------------------
        for (Iterator<GastoComunExt> it = listaGCExt.iterator();
                it.hasNext();) {
            GastoComunExt gce = (GastoComunExt) it.next();
            GastoDT gasto = new GastoDT();
            Integer id = gce.getIdConcepto();
            String cat = buscarCategoria(id);
            gasto.setDesCategoria(cat);
            gasto.setMtoCategoria(gce.getMtoGastoComunExt());
            listaDT.add(gasto);
        }
        //-------------------------------------------------------
        //Genera el monto y la categoria del gasto no comun 
        //-------------------------------------------------------
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            GastoDT gasto = new GastoDT();
            Integer id = gnc.getIdConcepto();
            String cat = buscarCategoria(id);
            gasto.setDesCategoria(cat);
            gasto.setMtoCategoria(gnc.getMtoGastoNoComun());
            listaDT.add(gasto);
        }
        //-------------------------------------------------------
        // Genera el monto y la categoria de los fondos de reserva
        // y prestaciones sociales
        //-------------------------------------------------------
        Integer cero = new Integer(0);
        for (Iterator<TranFondo> it = listaTranFondo.iterator();
                it.hasNext();) {
            TranFondo tf = (TranFondo) it.next();
            GastoDT gasto = new GastoDT();
            if (tf.getIdConceptoTranFondo().equals(cero)) {
                gasto.setDesCategoria("Fondo Reserva");
                gasto.setMtoCategoria(tf.getMtoTranFondo());
            } else {
                Integer id = tf.getIdConceptoTranFondo();
                String cat = buscarCategoria(id);
                gasto.setDesCategoria(cat);
                gasto.setMtoCategoria(tf.getMtoTranFondo());
            }
            listaDT.add(gasto);
        }
        return listaDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<GastoDT> listaDesOrdenada = new ArrayList<GastoDT>();

        for (Iterator<GastoDT> it = lista.iterator();
                it.hasNext();) {
            GastoDT gdt = (GastoDT) it.next();
            listaDesOrdenada.add(gdt);
        }

        Collections.sort(listaDesOrdenada, new ComparadorGasto());//devuelve la lista ordenada en listaDesOrdenada

        Collection<GastoDT> coleccionOrdenada = new ArrayList<GastoDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            GastoDT fdt = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    public String buscarCategoria(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorIdFetchCat";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto.getCategoria().getNombCategoria();
    }

    public void generarReporteDistribucion(Collection gastosDT) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/DistGasto.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //------------------------------------------------------------
        // Se crea el datasource de movimientos
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceGastos;

        dataSourceGastos = new JRBeanCollectionDataSource(gastosDT);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("fecDesde", fecDesde);
        parametros.put("fecHasta", fecHasta);
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceGastos);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
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
