/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author Frank
 */
import datosbasicos.modelos.*;
import informes.modelos.CobranzaDT;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class CobranzaCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    public String idInm;
    private String fecDesde;
    private String fecHasta;
    List bancos = new ArrayList();
    String nombInm;
    String codInm;
    Condominio condominio;
    private List listaInm = new ArrayList();
    private List listaInmueble = new ArrayList();
    Calendar fecDesdeCal, fecHastaCal;
    private Double totalCobranza;

    public CobranzaCtrl() {
    }

    public void inicializarPanel() {

        this.setIdInm(null);
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
        Calendar fecCreacion;

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------


        if (this.getFecDesde().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha desde la cual desea el informe de pagos...";
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
            mensaje = "La fecha (desde) debe ser mayor o igual a" + "" + stringFechaCreacion + "" + ", por favor intente de nuevo...";
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

    public void generarCobros(ActionEvent actionEvent) throws ParseException, ClassNotFoundException, SQLException, IOException, JRException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        generarReporteCobros();
    }

    public List getInmueblesPorCondominio() {


        listaInm.clear();
        listaInmueble.clear();
        listaInm.add(new SelectItem(new Integer(0), "Todos"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles  para el  condominio, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();
        Condominio condominio;
        Collection<Inmueble> inmuebles = null;
        String nombQuery = "buscarCondominioFetchPropiedades";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAccesII = new DaoQuery<Collection>();
        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();

            try {
                inmuebles = dataAccesII.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            listaInmueble.addAll(inmuebles);

        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (listaInmueble.isEmpty()) {// Si no hay proveeedores devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaInm.clear();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun Inmueble para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }

        for (Iterator<Inmueble> it = listaInmueble.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            listaInm.add(new SelectItem(inm.getId(), inm.getCodInmueble()));

            beanUsuario.ejecutarQueryDT = true;

        }
        return (List) listaInm;
    }

    public List inmueblesPorCondominio() {


        listaInm.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();
        Condominio condominio;
        Collection<Inmueble> inmuebles = null;
        String nombQuery = "buscarCondominioFetchPropiedades";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<Collection> dataAccesII = new DaoQuery<Collection>();
        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();

            try {
                inmuebles = dataAccesII.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            listaInm.addAll(inmuebles);

        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (listaInm.isEmpty()) {// Si no hay proveeedores devuelve la lista en blanco
            beanUsuario.agregar = true;
            listaInm.clear();
            return (null);
        }

        return (List) listaInm;
    }

    public Inmueble buscarInm(Integer idInm) throws ValidatorException {

        Inmueble inm;

        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inm = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return inm;
    }

    public void generarReporteCobros() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/Cobranza.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = getCobrosPorInm();
        String mensaje;
        if (filasReporte.isEmpty()) {
            mensaje = "No hay movimientos registrados para el Inmueble o período seleccionado ";
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
        Condominio condominio = buscarCondominio(idCond);
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);


        HashMap parametro = new HashMap();
        parametro.put("condominio", nombCondominio);
        parametro.put("logo", logo);
        parametro.put("desde", fecDesde);
        parametro.put("hasta", fecHasta);
        parametro.put("total", totalCobranza);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getCobrosPorInm() throws ParseException {

        List listaInm = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<Inmueble> inmuebles = new ArrayList();
        Collection<CobranzaDT> listaCobros = new ArrayList<CobranzaDT>(); // contiene PagosCXC con PagoFacCxC 
        Collection<CobranzaDT> listaPagosParcial = new ArrayList<CobranzaDT>(); // lista que devuelve el método igual que listaPagos pero con datos de las facturas
        Collection<CobranzaDT> listaPagosTotal = new ArrayList<CobranzaDT>();

        Integer idInmInt = Integer.valueOf(idInm).intValue();
        if (idInmInt == 0) { // Todos 
            inmuebles = inmueblesPorCondominio();
        } else {
            Inmueble inmuebleSel = buscarInm(idInmInt);
            inmuebles.add(inmuebleSel);

        }

        if (inmuebles.isEmpty()) {
            return (List) listaOrd;
        }
        for (Iterator<Inmueble> it = inmuebles.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            listaInm.add(inm);
        }

        /*
         * -----POR CADA Immueble SE BUSCAN PAGOS REALIZADOS -------------------------------------------------------------
         */

        Integer idInm;
        Inmueble inmObj;
        totalCobranza = 0.0;
        for (int i = 0; i < listaInm.size(); i++) {

            inmObj = (Inmueble) listaInm.get(i);
            idInm = inmObj.getId();
            listaCobros.clear();
            listaCobros = getPagosCxC(idInm, fecDesdeCal, fecHastaCal);
            if (!listaCobros.isEmpty()) {
                listaPagosParcial.addAll(listaCobros);
            }

        }

        Integer idFac;
        String disc;
        Integer idConcepto;
        for (Iterator<CobranzaDT> it1 = listaPagosParcial.iterator();
                it1.hasNext();) {
            CobranzaDT pagoParcial = (CobranzaDT) it1.next();

            CobranzaDT pagoTotal = new CobranzaDT();
            pagoTotal.setId(pagoParcial.getId());
            pagoTotal.setCodInmueble(pagoParcial.getCodInmueble());
            pagoTotal.setNombPropInmueble(pagoParcial.getNombPropInmueble());
            pagoTotal.setFecFac(pagoParcial.getFecFac());
            pagoTotal.setNumDoc(pagoParcial.getNumDoc());
            pagoTotal.setConcepto(pagoParcial.getConcepto());
            pagoTotal.setNombBanco(pagoParcial.getNombBanco());
            pagoTotal.setFecMovPago(pagoParcial.getFecMovPago());
            pagoTotal.setMtoBco(pagoParcial.getMtoBco());
            pagoTotal.setMtoPago(pagoParcial.getMtoPago());
            pagoTotal.setSaldoFac(null);
            pagoTotal.setIdFac(pagoParcial.getIdFac());
            pagoTotal.setDisc(pagoParcial.getDisc());

            idFac = pagoParcial.getIdFac();
            disc = pagoParcial.getDisc();

            if ("GNC".equals(disc)) {
                FacGastoNoComun fgnc;
                fgnc = getfacGNC(idFac);
                if (!(fgnc == null)) {

                    GastoNoComun gnc = fgnc.getGastoNoComun();
                    pagoTotal.setNumFac(gnc.getNumDocGastoNoComun());
                    idConcepto = gnc.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(fgnc.getMtoFacGastoNoComun());
                    pagoTotal.setSaldoFac(fgnc.getSaldoFacGastoNoComun());
                }
            }
            if ("DPE".equals(disc)) {
                DeudaPendCxC dep;
                dep = getDeudaPend(idFac);
                if (!(dep == null)) {
                    pagoTotal.setNumFac(pagoParcial.getNumFac());
                    idConcepto = dep.getIdConcepto();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(dep.getMtoDeudaPendCxC());
                    pagoTotal.setSaldoFac(dep.getSaldoDeudaPendCxC());
                }
            }
            if ("ALI".equals(disc)) {
                Alicuota ali;
                ali = getAlicuota(idFac);
                if (!(ali == null)) {
                    pagoTotal.setNumFac(pagoParcial.getNumFac());
                    pagoTotal.setConcepto(ali.getDesAlicuota());
                    pagoTotal.setMtoFac(ali.getMtoAlicuota());
                    pagoTotal.setSaldoFac(ali.getSaldoAlicuota());
                }
            }
            if ("CES".equals(disc)) {
                FacCuotaEsp fcEs;
                fcEs = getFacCuotaEsp(idFac);
                if (!(fcEs == null)) {

                    CuotaEsp cEs = fcEs.getCuotaEsp();
                    pagoTotal.setNumFac(pagoParcial.getNumFac());
                    idConcepto = cEs.getIdConceptoCuotaEsp();
                    pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    pagoTotal.setMtoFac(fcEs.getMtoFacCuotaEsp());
                    pagoTotal.setSaldoFac(fcEs.getSaldoFacCuotaEsp());
                }
            }

            if ("NCD".equals(disc) | "NDE".equals(disc)) {
                NotaCredDebCxC ncd;
                ncd = getNotaCredDebCxC(idFac);
                if (!(ncd == null)) {
                    pagoTotal.setNumFac(ncd.getNumNotaCredDebCxC());
                    idConcepto = ncd.getIdConcepto();
                    if (!idConcepto.equals(0)) {
                        pagoTotal.setConcepto(buscarNombConcepto(idConcepto));
                    }else{
                        pagoTotal.setConcepto(ncd.getDescNotaCredDebCxC());
                    }
                    pagoTotal.setMtoFac(ncd.getMtoNotaCredDebCxC());
                    pagoTotal.setSaldoFac(ncd.getSaldoNotaCredDebCxC());
                }
            }

            listaPagosTotal.add(pagoTotal);
        }

        //listaOrd = ordenarListaPagos(listaPagosTotal);// para ordenar la lista de cuentas por pagar*/
        //return listaOrd;
        return listaPagosTotal;
    }

    public Collection getPagosCxC(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection<CobranzaDT> listaPagos = new ArrayList<CobranzaDT>();
        Collection<PagoCxC> pagos = new ArrayList<PagoCxC>();
        Collection<PagoFacCxC> pagoFac = new ArrayList<PagoFacCxC>();
        Collection<PagoCxC> pagosRango = new ArrayList<PagoCxC>();

        String nombQuery = "buscarPagosCxCPorInmueble";
        String nombQueryII = "buscarPagoFacCxCPorIdPago";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            pagos = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaPagos;
        }

        if (pagos.isEmpty()) {
            return listaPagos;
        }
        Calendar fechaPagoCxP;
        for (Iterator<PagoCxC> it = pagos.iterator();
                it.hasNext();) {
            PagoCxC pagoCxC = (PagoCxC) it.next();
            fechaPagoCxP = convertirFecha(pagoCxC.getFecDocPagoCxC());
            if ((fechaPagoCxP.after(fecDesdeCal) | fechaPagoCxP.equals(fecDesdeCal)) & (fechaPagoCxP.before(fecHastaCal) | fechaPagoCxP.equals(fecHastaCal))) {
                pagosRango.add(pagoCxC);
            }
        }

        Integer idInmM;
        Integer idBanco;
        Integer idPago;

        for (Iterator<PagoCxC> it1 = pagosRango.iterator();
                it1.hasNext();) {

            PagoCxC pagoM = (PagoCxC) it1.next();

            CobranzaDT pagoInm = new CobranzaDT();

            idInmM = pagoM.getInmueble().getId();
            pagoInm.setId(pagoM.getId());
            pagoInm.setNombPropInmueble(buscarNombPropietario(idInmM));
            pagoInm.setCodInmueble(codInm);
            pagoInm.setFecFac(pagoM.getFecDocPagoCxC());
            pagoInm.setNumDoc(pagoM.getNumDocPagoCxC());
            if ((pagoM.getFormaPagoCxC().equals("PAD") | pagoM.getFormaPagoCxC().equals("NCR"))) {
                pagoInm.setConcepto("PAGO AUTOMATICO");
            } else {
                pagoInm.setConcepto("PAGO REGISTRADO");
            }
            pagoInm.setNumDoc(pagoM.getNumDocPagoCxC());
            pagoInm.setNombBanco(pagoM.getBancoPagoCxC());
            if (!pagoM.getBancoPagoCxC().equals("N/A")) {
                idBanco = Integer.parseInt(pagoM.getBancoPagoCxC());
                pagoInm.setNombBanco(buscarNombreBanco(idBanco));
            }

            pagoInm.setNumFac("");
            pagoInm.setFecMovPago("");
            pagoInm.setMtoBco(pagoM.getMtoPagoCxC());
            pagoInm.setIdFac(null);
            pagoInm.setDisc(null);
            listaPagos.add(pagoInm);
            totalCobranza = totalCobranza + pagoM.getMtoPagoCxC();

            // Buscar facturas
            idPago = pagoM.getId();
            try {
                pagoFac = dataAcces.buscarListaFetchPorId(idPago, nombQueryII);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return listaPagos;
            }
            //
            if (pagoFac.size() > 0) {

                for (Iterator<PagoFacCxC> it2 = pagoFac.iterator();
                        it2.hasNext();) {

                    PagoFacCxC pagoF = (PagoFacCxC) it2.next();

                    CobranzaDT pagoInmF = new CobranzaDT();

                    idInmM = pagoM.getInmueble().getId();
                    pagoInmF.setId(pagoF.getId());
                    pagoInmF.setNombPropInmueble(buscarNombPropietario(idInmM));
                    pagoInmF.setCodInmueble(codInm);
                    pagoInmF.setFecFac("");
                    pagoInmF.setNumDoc("");

                    pagoInmF.setNumFac(pagoM.getNumDocPagoCxC());
                    pagoInmF.setNombBanco("");

                    pagoInmF.setFecMovPago("");
                    pagoInmF.setMtoPago(pagoF.getMtoPagadoCxC());
                    pagoInmF.setIdFac(pagoF.getIdFacCxC());
                    pagoInmF.setDisc(pagoF.getDiscFacCxC());

                    listaPagos.add(pagoInmF);
                }
            }
        }

        return listaPagos;
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
        String descConcepto = concepto.getDescConcepto();
        return descConcepto;
    }

    public Alicuota getAlicuota(Integer idFac) {

        Alicuota ali;

        String nombQuery = "buscarAlicuotaPorId";
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            ali = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return ali;
    }

    public FacCuotaEsp getFacCuotaEsp(Integer idFac) {

        FacCuotaEsp fcEs;

        String nombQuery = "buscarFacCuotaEspPorId";
        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fcEs = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return fcEs;
    }

    public FacGastoNoComun getfacGNC(Integer idFac) {

        FacGastoNoComun fgnc;

        String nombQuery = "buscarFacGastoNoComunPorId";
        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return fgnc;
    }

    public DeudaPendCxC getDeudaPend(Integer idFac) {

        DeudaPendCxC dep = null;

        String nombQuery = "buscarDeudaPendPorId";
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dep = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return dep;
        }

        return dep;
    }

    public NotaCredDebCxC getNotaCredDebCxC(Integer idFac) {

        NotaCredDebCxC ncd;

        String nombQuery = "buscarNotaCredDebCxCPorId";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncd = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return ncd;
    }

    public Collection ordenarListaPagos(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<CobranzaDT> listaDesOrdenada = new ArrayList<CobranzaDT>();

        for (Iterator<CobranzaDT> it = lista.iterator();
                it.hasNext();) {
            CobranzaDT con = (CobranzaDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCobranzaInmDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<CobranzaDT> coleccionOrdenada = new ArrayList<CobranzaDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            CobranzaDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public String buscarNombreBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarNombPropietario(Integer idInm) {

        Inmueble inmueble;
        String nombQuery = "buscarInmueblePorId";
        DaoQuery<Inmueble> dataAcces = new DaoQuery<Inmueble>();
        try {
            inmueble = dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombProp = inmueble.getNombPropInmueble();
        codInm = inmueble.getCodInmueble();
        return nombProp;
    }

    public String CalendarAString(Calendar fecha) {

        //----------------------------------------------------------------
        //Se da formato a la fecha del mov para mostrarla en el data table
        //----------------------------------------------------------------
        Calendar fechaFac = fecha;
        String dia;
        String mes;
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

    public void setIdInm(String idInm) {
        this.idInm = idInm;
    }

    public String getIdInm() {
        return idInm;
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

    public void setCodInm(String codInm) {
        this.codInm = codInm;
    }

    public String getCodInm() {
        return codInm;
    }
}
