/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author Frank
 */
import datosbasicos.modelos.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Calendar;
import java.util.HashMap;
import java.io.FileInputStream;
import javax.faces.application.FacesMessage;
import util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.faces.event.ActionEvent;

import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.TranFondo;
import movimientos.modelos.CuotaEsp;
import movimientos.modelos.AjustesCond;

import informes.modelos.MovDelMesDT;

import java.util.Iterator;
import java.util.List;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;

public class MovDelMesCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private String periodo;
    private Condominio condominio;
    private String nombProveedor;
    private Calendar periodoCal;
    private String periodoStr;
    private Collection<FacGastoNoComun> facGastoNoComun = new ArrayList<FacGastoNoComun>();

    public MovDelMesCtrl() {
    }

    public void inicializarPanel() {

        this.setPeriodo(null);

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

        if (this.getPeriodo().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el Período para el cual requiere el Informe";
            return mensaje;
        }
        if (!(val.esFormatoPeriodo(periodo))) {
            mensaje = "Por favor, ingrese el Período en formato mm-aaaa. Ejemplo: 02-2012.";
            return mensaje;

        }
        return mensaje;
    }

    public void generarMovDelMes(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        generarReporteMovDelMes();
    }

    public void generarReporteMovDelMes() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/MovDelMes.jasper");
        try (ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream()) {
            JRBeanCollectionDataSource dataSource;

            Collection filasReporte = getMovDelMes();

            String mensaje;
            if (filasReporte.isEmpty()) {
                mensaje = "No hay movimientos registrados, para el período seleccionado ";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(mensaje));
                return;
            }

            dataSource = new JRBeanCollectionDataSource(filasReporte);

            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            String nombCondominio = beanUsuario.getCondominioActual();

            //--------------------------------------------------------------
            // Se obtiene el parametro con la imagen del logo del condominio
            //--------------------------------------------------------------
            String ruta = beanUsuario.getRuta();
            Integer idCond = beanUsuario.getIdCondActual();
            Condominio cond = buscarCondominio(idCond);
            String nombLogo = cond.getNombLogo();
            InputStream logo = new FileInputStream(ruta + nombLogo);

            HashMap parametro = new HashMap();
            parametro.put("condominio", nombCondominio);
            parametro.put("periodo", periodo);
            parametro.put("logo", logo);


            //-------------------------------------------------------------------------------------------------------------------
            JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
            //-------------------------------------------------------------------------------------------------------------------
            response.setContentType("application/pdf");

            servletOutputStream.flush();
        }
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getMovDelMes() throws ParseException {

        List listaMov = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<MovDelMesDT> listaMovParcial = new ArrayList<MovDelMesDT>();
        Collection<MovDelMesDT> listaMovTotal = new ArrayList<MovDelMesDT>();

        // Covertir de periodo de String a Calendar
        String stringPeriodo = "01-" + periodo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);


        listaMovParcial = getGCOrd(cal);
        for (Iterator<MovDelMesDT> it3 = listaMovParcial.iterator();
                it3.hasNext();) {
            MovDelMesDT movGCO = (MovDelMesDT) it3.next();
            listaMovTotal.add(movGCO);

        }
        listaMovParcial.clear();

        listaMovParcial = getGCExt(cal);
        for (Iterator<MovDelMesDT> it4 = listaMovParcial.iterator();
                it4.hasNext();) {
            MovDelMesDT cxpGCext = (MovDelMesDT) it4.next();
            listaMovTotal.add(cxpGCext);
        }
        listaMovParcial.clear();

        listaMovParcial = getGNC(cal);
        for (Iterator<MovDelMesDT> it5 = listaMovParcial.iterator();
                it5.hasNext();) {
            MovDelMesDT movGNC = (MovDelMesDT) it5.next();
            listaMovTotal.add(movGNC);
        }
        listaMovParcial.clear();

        listaMovParcial = getTranFondo(cal);
        for (Iterator<MovDelMesDT> it6 = listaMovParcial.iterator();
                it6.hasNext();) {
            MovDelMesDT movTranFondo = (MovDelMesDT) it6.next();
            listaMovTotal.add(movTranFondo);
        }
        listaMovParcial.clear();

        listaMovParcial = getAjustes(cal);
        for (Iterator<MovDelMesDT> it6 = listaMovParcial.iterator();
                it6.hasNext();) {
            MovDelMesDT movAjustes = (MovDelMesDT) it6.next();
            listaMovTotal.add(movAjustes);
        }

        listaOrd = ordenarListaMovDelMes(listaMovTotal);// para ordenar la lista de gastos registrados*/
        return listaOrd;
    }

    public Collection getGCOrd(Calendar periodo) {

        Collection<MovDelMesDT> listaGCO = new ArrayList<MovDelMesDT>();
        Collection<GastoComunOrd> gastoComunOrd = new ArrayList<GastoComunOrd>();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarGastosComunesOrdPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            gastoComunOrd = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaGCO;
        }

        if (gastoComunOrd.isEmpty()) {
            return (List) listaGCO;
        }
        Integer idConcepto;
        String concepto;
        Integer idProveedor;
        String nombProveedor;

        for (Iterator<GastoComunOrd> it = gastoComunOrd.iterator();
                it.hasNext();) {
            GastoComunOrd mov = (GastoComunOrd) it.next();

            MovDelMesDT movGCO = new MovDelMesDT();

            movGCO.setDescMov("Gasto Común Ordinario");
            movGCO.setFecFacMov(mov.getFecGastoComunOrd());
            movGCO.setNumDocMov(mov.getNumDocGastoComunOrd());
            idConcepto = mov.getIdConcepto();
            concepto = buscarNombConcepto(idConcepto);
            movGCO.setConceptoMov(concepto);
            idProveedor = mov.getIdProveedor();
            nombProveedor = buscarNombProveedor(idProveedor);
            movGCO.setNombProveedorMov(nombProveedor);
            movGCO.setMtoMov(mov.getMtoGastoComunOrd());
            movGCO.setSaldoMov(mov.getSaldoGastoComunOrd());
            movGCO.setEdoMov(mov.getEdoGastoComunOrd());
            movGCO.setObsMov(mov.getObsGastoComunOrd());
            listaGCO.add(movGCO);
        }
        return listaGCO;
    }

    public Collection getGCExt(Calendar periodo) {

        Collection<MovDelMesDT> listaGCE = new ArrayList<MovDelMesDT>();
        Collection<GastoComunExt> gastoComunExt = new ArrayList<GastoComunExt>();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarGastosComunesExtPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            gastoComunExt = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaGCE;
        }

        if (gastoComunExt.isEmpty()) {
            return (List) listaGCE;
        }

        Integer idConcepto;
        String concepto;
        Integer idProveedor;
        String nombProveedor;

        for (Iterator<GastoComunExt> it = gastoComunExt.iterator();
                it.hasNext();) {

            GastoComunExt mov = (GastoComunExt) it.next();

            MovDelMesDT movGCE = new MovDelMesDT();
            movGCE.setDescMov("Gasto Común ExtraOrd");
            movGCE.setFecFacMov(mov.getFecGastoComunExt());
            movGCE.setNumDocMov(mov.getNumDocGastoComunExt());
            idConcepto = mov.getIdConcepto();
            concepto = buscarNombConcepto(idConcepto);
            movGCE.setConceptoMov(concepto);
            idProveedor = mov.getIdProveedor();
            nombProveedor = buscarNombProveedor(idProveedor);
            movGCE.setNombProveedorMov(nombProveedor);
            movGCE.setMtoMov(mov.getMtoGastoComunExt());
            movGCE.setSaldoMov(mov.getSaldoGastoComunExt());
            movGCE.setEdoMov(mov.getEdoGastoComunExt());
            movGCE.setObsMov(mov.getObsGastoComunExt());
            listaGCE.add(movGCE);
        }
        return listaGCE;
    }

    public Collection getGNC(Calendar periodo) {

        Collection<MovDelMesDT> listaGNC = new ArrayList<MovDelMesDT>();
        Collection<GastoNoComun> gastoNoComun = new ArrayList<GastoNoComun>();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarGastosNoComunesPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            gastoNoComun = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaGNC;
        }

        if (gastoNoComun.isEmpty()) {
            return (List) listaGNC;
        }

        Integer idConcepto;
        String concepto;
        Integer idProveedor;
        String nombProv;
        Inmueble inmueble;
        MovDelMesDT movGNC;

        for (Iterator<GastoNoComun> it = gastoNoComun.iterator();
                it.hasNext();) {
            GastoNoComun mov = (GastoNoComun) it.next();
            movGNC = new MovDelMesDT();
            movGNC.setDescMov("Gasto No Común");
            movGNC.setFecFacMov(mov.getFecGastoNoComun());
            movGNC.setNumDocMov(mov.getNumDocGastoNoComun());
            idConcepto = mov.getIdConcepto();
            concepto = buscarNombConcepto(idConcepto);
            movGNC.setConceptoMov(concepto);
            idProveedor = mov.getIdProveedor();
            nombProv = buscarNombProveedor(idProveedor);
            movGNC.setNombProveedorMov(nombProv);
            movGNC.setMtoMov(mov.getMtoGastoNoComun());
            movGNC.setSaldoMov(mov.getSaldoGastoNoComun());
            movGNC.setEdoMov(mov.getEdoGastoNoComun());
            movGNC.setObsMov(mov.getObsGastoNoComun());
            listaGNC.add(movGNC);

            facGastoNoComun = buscarFacturasGNC(mov);
            CobranzaCtrl cobranza = new CobranzaCtrl();

            for (Iterator<FacGastoNoComun> it1 = facGastoNoComun.iterator();
                    it1.hasNext();) {
                FacGastoNoComun fgnc = (FacGastoNoComun) it1.next();
                movGNC = new MovDelMesDT();
                movGNC.setDescMov("Gasto No Común");
                movGNC.setFecFacMov("");
                movGNC.setNumDocMov("");
                movGNC.setConceptoMov("");
                inmueble = cobranza.buscarInm(fgnc.getInmueble().getId());
                movGNC.setNombProveedorMov(inmueble.getCodInmueble());
                movGNC.setMtoFac(fgnc.getMtoFacGastoNoComun());
                movGNC.setSaldoFac(fgnc.getSaldoFacGastoNoComun());
                movGNC.setEdoMov(fgnc.getEdoFacGastoNoComun());
                movGNC.setObsMov("");
                listaGNC.add(movGNC);
            }
            facGastoNoComun.clear();
        }
        return listaGNC;
    }

    public Collection getTranFondo(Calendar periodo) {

        Collection<MovDelMesDT> listaTFO = new ArrayList<MovDelMesDT>();
        Collection<TranFondo> tranFondo = new ArrayList<TranFondo>();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarTranFondosPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            tranFondo = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaTFO;
        }

        if (tranFondo.isEmpty()) {
            return (List) listaTFO;
        }

        Integer idConcepto;
        String concepto;
        Integer idBco;

        String nombBanco;

        for (Iterator<TranFondo> it = tranFondo.iterator();
                it.hasNext();) {
            TranFondo mov = (TranFondo) it.next();
            MovDelMesDT movTFO = new MovDelMesDT();
            //
            Fondo fondo = mov.getFondo();
            String tipoFondo = getTipoDeFondo(fondo.id);
            //
            if ("Reserva".equals(tipoFondo)) {
                movTFO.setDescMov("Trans Fondo Reserva");
            }
            if ("Prestaciones".equals(tipoFondo)) {
                movTFO.setDescMov("Trans Fondo Prestaciones");
            }
            if ("Especial".equals(tipoFondo)) {
                movTFO.setDescMov("Trans Fondo Especial");
            }
            if ("".equals(tipoFondo)) {
                movTFO.setDescMov("Trans Fondo");
            }

            movTFO.setFecFacMov(mov.getFecTranFondo());
            movTFO.setNumDocMov(mov.getDocTranFondo());
            idConcepto = mov.getIdConceptoTranFondo();
            concepto = buscarNombConcepto(idConcepto);
            movTFO.setConceptoMov(concepto);
            idBco = mov.getIdBancoTranFondo();
            if (idConcepto != 0) {
                nombBanco = buscarNombBanco(idBco);
            } else {
                nombBanco = "N/A";
            }
            movTFO.setNombProveedorMov("N/A");
            movTFO.setMtoMov(mov.getMtoTranFondo());
            movTFO.setSaldoMov(mov.getSaldoTranFondo());
            movTFO.setEdoMov(mov.getEstadoTranFondo());
            movTFO.setObsMov(mov.getDescTranFondo());
            listaTFO.add(movTFO);
        }
        return listaTFO;
    }

    public Collection getAjustes(Calendar periodo) {

        Collection<MovDelMesDT> listaAjustes = new ArrayList<MovDelMesDT>();
        Collection<AjustesCond> ajustes = new ArrayList<AjustesCond>();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarAjustesCondPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            ajustes = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaAjustes;
        }

        if (ajustes.isEmpty()) {
            return (List) listaAjustes;
        }

        Integer idConcepto;
        String concepto;

        for (Iterator<AjustesCond> it = ajustes.iterator();
                it.hasNext();) {
            AjustesCond mov = (AjustesCond) it.next();
            MovDelMesDT movAjustes = new MovDelMesDT();
            movAjustes.setDescMov("Ajustes Cond.");

            idConcepto = mov.getIdConcepto();
            concepto = buscarNombConcepto(idConcepto);
            movAjustes.setConceptoMov(concepto);
            movAjustes.setNombProveedorMov("N/A");
            movAjustes.setMtoMov(mov.getMtoAjustesCond());
            movAjustes.setSaldoMov(0.00);
            movAjustes.setEdoMov("CANC ");
            movAjustes.setObsMov(" ");
            String fechaStr = convertirCalendarAString(mov.getFecMovAjustesCond());
            movAjustes.setFecFacMov(fechaStr);
            movAjustes.setNumDocMov("S/N");

            listaAjustes.add(movAjustes);
        }
        return listaAjustes;
    }

    public Collection ordenarListaMovDelMes(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<MovDelMesDT> listaDesOrdenada = new ArrayList<MovDelMesDT>();

        for (Iterator<MovDelMesDT> it = lista.iterator();
                it.hasNext();) {
            MovDelMesDT con = (MovDelMesDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorMovDelMesDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<MovDelMesDT> coleccionOrdenada = new ArrayList<MovDelMesDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            MovDelMesDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public String buscarNombConcepto(Integer idConc) {

        Concepto concepto;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String descConcepto = concepto.getNombConcepto();
        return descConcepto;
    }

    public String getTipoDeFondo(Integer idFondo) {

        Fondo fondo;
        String nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces.buscarEntidadPorId(idFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        String tipo = "";
        if ("Reserva".equals(fondo.getTipoFondo())) {
            tipo = "Reserva";
        }
        if ("Prestaciones".equals(fondo.getTipoFondo())) {
            tipo = "Prestaciones";
        }
        if ("Especial".equals(fondo.getTipoFondo())) {
            tipo = "Especial";
        }

        return tipo;
    }

    public String convertirCalendarAString(Calendar fecha) {

        String dia = "";
        String mes = "";
        int diaFac = fecha.get(Calendar.DAY_OF_MONTH);
        int mesFac = fecha.get(Calendar.MONTH) + 1;
        int añoFac = fecha.get(Calendar.YEAR);

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
        return stringFechaDp;

    }

    public String buscarNombProveedor(Integer idProv) throws ValidatorException {

        Proveedor prov;
        String nombProv;

        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            prov = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        nombProv = prov.getNombProveedor();
        return nombProv;
    }

    public String buscarNombBanco(Integer idBco) throws ValidatorException {

        Banco banco;
        String nombBco;

        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (!(banco == null)) {
            nombBco = banco.getNombBanco();
        } else {
            nombBco = "N/A";
        }
        return nombBco;
    }

    public String convertirPeriodoAString(Calendar periodo) {

        Calendar fechaFac = periodo;

        String mes;

        int mesFac = fechaFac.get(Calendar.MONTH) + 1;
        int añoFac = fechaFac.get(Calendar.YEAR);


        if (mesFac < 10) {
            mes = "0" + mesFac;
        } else {
            mes = String.valueOf(mesFac);
        }

        String stringFechaDp = mes + "-" + añoFac;
        return stringFechaDp;

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

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    private Collection<FacGastoNoComun> buscarFacturasGNC(GastoNoComun mov) {

        String nombQuery = "buscarFacGastoNoComunPorIdGNC";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            facGastoNoComun = dataAcces.buscarListaEntidadesPorId(mov.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return facGastoNoComun;

    }

    private Inmueble buscarInmueble(Inmueble inmueble) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
