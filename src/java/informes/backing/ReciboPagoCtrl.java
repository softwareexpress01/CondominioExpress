package informes.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import informes.modelos.PagoFacCxCDT;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoCxCDT;
import movimientos.modelos.PagoFacCxC;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReciboPagoCtrl extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

    public String idInmueble;
    private String desPagoCxC;
    private String numDocPagoCxC;
    private String mtoPagoCxC;
    private String formaPagoCxC;
    private String numRefPagoCxC;
    private String fecDocPagoCxC;
    private String bancoPagoCxC;
    private Collection<PagoCxC> listaPagosCxC = new ArrayList<PagoCxC>();
    private Collection<PagoCxCDT> listaPagosCxCDT = new ArrayList<PagoCxCDT>();
    private List listaInmuebles = new ArrayList();
    private PagoCxCDT filaSeleccionada;

    public ReciboPagoCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {

        System.out.println("pasando por Inicializar");

        setDesPagoCxC(null);
        setNumDocPagoCxC(null);
        setMtoPagoCxC(null);
        setFormaPagoCxC(null);
        setNumRefPagoCxC(null);
        setFecDocPagoCxC(null);
        setBancoPagoCxC(null);

    }

    public void inicializarPanel() {

        System.out.println("pasando por Inicializar desde generar pdf");

        setDesPagoCxC(null);
        setNumDocPagoCxC(null);
        setMtoPagoCxC(null);
        setFormaPagoCxC(null);
        setNumRefPagoCxC(null);
        setFecDocPagoCxC(null);
        setBancoPagoCxC(null);

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.modificar = true;

        return "informes";

    }

    public String validarPanel() {

        String mensaje = new String();
        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idInmueble.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el inmueble ...";
            return mensaje;
        }

        if (this.getFilaSeleccionada() == null) {
            mensaje = "Por favor, seleccione el pago para el cual desea emitir/enviar el recibo ...";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getPagosCxCPorInmueble() throws ParseException {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInmueble == null) {
            return listaPagosCxCDT;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaPagosCxCDT;
        }

        listaPagosCxCDT.clear();
        listaPagosCxC.clear();

        // Obtiene los pagos para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarTodosPagosCxCPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosCxC = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (listaPagosCxC.isEmpty()) {
            return listaPagosCxCDT;
        }

        // se inicializa el objeto de pagos para el DataTable...
        listaPagosCxCDT = inicializarPagosCxCDT(listaPagosCxC);

        //   beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        //   beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table


        return listaPagosCxCDT;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoQuery
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

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoCxCDT> listaDesOrdenada = new ArrayList<PagoCxCDT>();

        for (Iterator<PagoCxCDT> it = lista.iterator();
                it.hasNext();) {
            PagoCxCDT gco = (PagoCxCDT) it.next();
            listaDesOrdenada.add(gco);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagoCxCDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoCxCDT> coleccionOrdenada = new ArrayList<PagoCxCDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoCxCDT pago = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pago);
        }
        return coleccionOrdenada;
    }

    private Collection<PagoCxCDT> inicializarPagosCxCDT(Collection lista) throws ParseException {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<PagoCxC> it = lista.iterator();
                it.hasNext();) {

            PagoCxC pagocxc = (PagoCxC) it.next();
            if (pagocxc == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                PagoCxCDT pagoCxCDT = new PagoCxCDT();

                pagoCxCDT.setId(pagocxc.getId());
                pagoCxCDT.setDesPagoCxC(pagocxc.getDesPagoCxC());
                pagoCxCDT.setNumDocPagoCxC(pagocxc.getNumDocPagoCxC());
                pagoCxCDT.setMtoPagoCxC(val.convertirDoubleAString(pagocxc.getMtoPagoCxC()));
                pagoCxCDT.setFormaPagoCxC(pagocxc.getFormaPagoCxC());
                pagoCxCDT.setFecDocPagoCxC(pagocxc.getFecDocPagoCxC());
                pagoCxCDT.setFecDocPagoCal(convertirFecha(pagocxc.getFecDocPagoCxC()));
                Integer idBanco = Integer.parseInt(pagocxc.getBancoPagoCxC());
                String nombBco = buscarBanco(idBanco);
                pagoCxCDT.setBancoPagoCxC(nombBco);

                pagoCxCDT.setFecMovPagoCxC(pagocxc.getFecDocPagoCxC());

                listaPagosCxCDT.add(pagoCxCDT);


            }
        }
        Collection listaOrd = ordenarLista(listaPagosCxCDT);// para ordenar la lista de gastos
        return listaOrd;
    }

    public String buscarBanco(Integer idBanco) throws ValidatorException {

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
        return banco.getNombBanco();
    }

    public List getInmueblesPorPropiedad() {

        System.out.println("pasando por getInmueblesPorPropiedad");

        listaInmuebles.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();
        List<Inmueble> inmuebles = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (inmuebles.isEmpty()) {// Si no hay inmuebles devuelve la lista en blanco
            beanUsuario.modificar = true;
            listaInmuebles.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaInmuebles;
        }

        listaInmuebles.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }


        return (List) listaInmuebles;
    }

    public void editarPagoCxCAction() {

        System.out.println("pasando por Editar");

        PagoCxCDT pagocxcSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "PagoCxCDT" en el map #{PagoCxCDT} 
        ctx.getExternalContext().getSessionMap().put("PagoCxCDT", pagocxcSel);

        this.setDesPagoCxC(pagocxcSel.getDesPagoCxC());
        this.setNumDocPagoCxC(pagocxcSel.getNumDocPagoCxC());
        this.setMtoPagoCxC(pagocxcSel.getMtoPagoCxC());
        this.setFormaPagoCxC(pagocxcSel.getFormaPagoCxC());
        this.setNumRefPagoCxC(pagocxcSel.getNumRefPagoCxC());
        this.setFecDocPagoCxC(pagocxcSel.getFecDocPagoCxC());
        this.setBancoPagoCxC(pagocxcSel.getBancoPagoCxC());

    }

    public void generarReciboPago(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        inicializarPanel();

        String vacio = new String();
        String mensaje = new String();

        mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/ReciboPago.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCond = beanUsuario.getCondominioActual();

        //-------------------------------------------------------------------------
        // Se busca el inmueble y el pago para obtener los parametros del reporte
        //-------------------------------------------------------------------------
        Integer idInm = Integer.parseInt(idInmueble);
        Inmueble inm = buscarInmueble(idInm);

        String codInm = inm.getCodInmueble().toString();
        String nombProp = inm.getNombPropInmueble();
        String cedProp = inm.getCedRifPropInmueble();

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoCxCDT pagoCxCDT = (PagoCxCDT) ctx.getExternalContext().getSessionMap().get("PagoCxCDT");

        String fecEmiPago = pagoCxCDT.getFecDocPagoCxC();
        String desPago = pagoCxCDT.getDesPagoCxC();
        String numDocPago = pagoCxCDT.getNumDocPagoCxC();
        String mtoPago = pagoCxCDT.getMtoPagoCxC();
        String formaPago = pagoCxCDT.getFormaPagoCxC();
        String fecDocPago = pagoCxCDT.getFecDocPagoCxC();
        String bancoPago = pagoCxCDT.getBancoPagoCxC();
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        Condominio condominio = buscarCondominio();
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //--------------------------------------------------------------
        // Se obtiene el numero del recibo y las observaciones de la 
        // la clase parametro del condominio actual.
        //--------------------------------------------------------------
        Integer numReciboCobro = null;
        String obsReciboCobro = null;

        Parametro parametro = condominio.getParametro();

        if (parametro != null) {
            numReciboCobro = parametro.getProxReciboCobro();
            obsReciboCobro = parametro.getObsReciboPago();
            parametro.setProxReciboCobro(numReciboCobro + 1);
            actualizarParametro(parametro);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...reporte cancelado"));
            return;
        }

        //------------------------------------------------------------
        // Se crea el datasource de facturas de pagos de cxc
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagoFacCxC;

        Collection<PagoFacCxCDT> pagoFacCxCDT = buscarPagoFacCxC(codInm, pagoCxCDT);


        dataSourcePagoFacCxC = new JRBeanCollectionDataSource(pagoFacCxCDT);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("codInm", codInm);
        parametros.put("nombProp", nombProp);
        parametros.put("cedProp", cedProp);
        parametros.put("fecEmiPago", fecEmiPago);
        parametros.put("desPago", desPago);
        parametros.put("numDocPago", numDocPago);
        parametros.put("mtoPago", mtoPago);
        parametros.put("formaPago", formaPago);
        parametros.put("fecDocPago", fecDocPago);
        parametros.put("bancoPago", bancoPago);
        parametros.put("logo", logo);
        parametros.put("numReciboCobro", numReciboCobro);
        parametros.put("obsReciboCobro", obsReciboCobro);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourcePagoFacCxC);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario.ejecutarQueryDT = true;
    }

    public Inmueble buscarInmueble(Integer idInm) throws ValidatorException {

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

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoCrudAux<Parametro> dataAcces = new DaoCrudAux<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public Collection<PagoFacCxCDT> buscarPagoFacCxC(String codInm, PagoCxCDT pagoCxCDT) {

        Collection<PagoFacCxC> lista = new ArrayList<PagoFacCxC>();
        Collection<PagoFacCxCDT> pagosDT = new ArrayList<PagoFacCxCDT>();
        Integer idPagoCxC = pagoCxCDT.getId();

        //------------------Se agregaron las notas de credito generadas por el pago al recibo-------------------------

        NotaCredDebCxC notaCred;
        PagoFacCxCDT pagoFacCxCDT;

        String nombQuery = "buscarPagoFacCxCPorIdPago";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idPagoCxC, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        pagosDT = convertirPagosADT(codInm, lista);

        notaCred = buscarNotaCreditoPago(codInm, idPagoCxC);

        if (!(notaCred == null)) {
            pagoFacCxCDT = convertirNotaCredApagoFacCxCDT(codInm, notaCred);
            pagosDT.add(pagoFacCxCDT);
        }
        return pagosDT;
    }

    public NotaCredDebCxC buscarNotaCreditoPago(String codInm, Integer idPagoCxC) {

        NotaCredDebCxC ncr;

        String nombQuery = "buscarNotaCreditoPorIdPago";
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncr = dataAcces.buscarEntidadPorId(idPagoCxC, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return ncr;
    }

    private Collection convertirPagosADT(String codInmueble, Collection listaPagos) {

        Collection listaDT = new ArrayList();
        Integer cero = 0;

        for (Iterator<PagoFacCxC> it = listaPagos.iterator();
                it.hasNext();) {

            PagoFacCxC pago = (PagoFacCxC) it.next();
            if (pago == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                PagoFacCxCDT pagoFacCxCDT = new PagoFacCxCDT();

                pagoFacCxCDT.setId(pago.getId());

                pagoFacCxCDT.setCodInmueble(codInmueble);

                //--------------------------------------------
                // Se busca las facturas involucradas
                //--------------------------------------------
                String dis = pago.getDiscFacCxC();
                Integer idFac = pago.getIdFacCxC();

                if (dis.equals("GNC")) {
                    FacGastoNoComun fgnc = buscarFacGastoNoComun(idFac);
                    Integer idCon = fgnc.getGastoNoComun().getIdConcepto();
                    Concepto con = buscarConcepto(idCon);
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    pagoFacCxCDT.setMtoCxC(fgnc.getMtoFacGastoNoComun());
                    pagoFacCxCDT.setSaldoCxC(fgnc.getSaldoFacGastoNoComun());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(fgnc.getFecFacGNC());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("ALI")) {
                    Alicuota alic = buscarAlicuota(idFac);
                    pagoFacCxCDT.setDesCxC(alic.getDesAlicuota());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(alic.getMtoAlicuota());
                    pagoFacCxCDT.setSaldoCxC(alic.getSaldoAlicuota());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(alic.getFecEmiAlicuota());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("CES")) {
                    FacCuotaEsp fce = buscarFacCuotaEsp(idFac);
                    pagoFacCxCDT.setDesCxC(fce.getDesFacCuotaEsp());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(fce.getMtoFacCuotaEsp());
                    pagoFacCxCDT.setSaldoCxC(fce.getSaldoFacCuotaEsp());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(fce.getFecEmiFacCuotaEsp());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                if (dis.equals("NCD") | dis.equals("NDE")) {
                    NotaCredDebCxC ncd = buscarNotaDebito(idFac);

                    //---------------------------------------------------------------------------------------------------
                    // Se busca el concepto de la nota de credito asociado a la factura y luego su
                    // descripcion. Para las notas de credito con idCon=cero - pago en exceso -se asigna la descripcion
                    // pero si es debito por mora , se toma la descripción (10-11-2016)JR
                    //---------------------------------------------------------------------------------------------------
                    Integer idCon = ncd.getIdConcepto();
                    if (!idCon.equals(cero)) {
                        Concepto con = buscarConcepto(idCon);
                        pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    } else {
                        if (ncd.getTipoNotaCredDebCxC().equals("Credito")) {
                            pagoFacCxCDT.setDesCxC("Pago en exceso");
                        } else {
                            pagoFacCxCDT.setDesCxC(ncd.getDescNotaCredDebCxC());
                        }
                    }
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(ncd.getMtoNotaCredDebCxC());
                    pagoFacCxCDT.setSaldoCxC(ncd.getSaldoNotaCredDebCxC());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());

                    pagoFacCxCDT.setFecEmisionCxC(ncd.getFecNotaCredDebCxC());
                }
                if (dis.equals("DPE")) {
                    DeudaPendCxC dpe = buscarDeudaPendiente(idFac);
                    Integer idCon = dpe.getIdConcepto();
                    Concepto con = buscarConcepto(idCon);
                    pagoFacCxCDT.setDesCxC(con.getNombConcepto());
                    pagoFacCxCDT.setTipoCxC(dis);
                    pagoFacCxCDT.setMtoCxC(dpe.getMtoDeudaPendCxC());
                    pagoFacCxCDT.setSaldoCxC(dpe.getSaldoDeudaPendCxC());
                    pagoFacCxCDT.setMtoPagadoCxC(pago.getMtoPagadoCxC());
                    String fecha = formatearFecha(dpe.getFecDeudaPendCxC());
                    pagoFacCxCDT.setFecEmisionCxC(fecha);
                }
                //----------------------------------------------------------------------------------
                // Pasa a la lista de movimientos de cxc : pago
                //----------------------------------------------------------------------------------

                listaDT.add(pagoFacCxCDT);
            }
        }
        return listaDT;
    }

    private PagoFacCxCDT convertirNotaCredApagoFacCxCDT(String codInmueble, NotaCredDebCxC notaCred) {


        PagoFacCxCDT pagoFacCxCDT = new PagoFacCxCDT();

        pagoFacCxCDT.setId(notaCred.getIdPagoCxC());
        pagoFacCxCDT.setCodInmueble(codInmueble);
        pagoFacCxCDT.setTipoCxC("NCR");
        pagoFacCxCDT.setDesCxC("Pago excedió monto facturas");
        pagoFacCxCDT.setMtoCxC(notaCred.getMtoNotaCredDebCxC());
        pagoFacCxCDT.setSaldoCxC(notaCred.getSaldoNotaCredDebCxC());
        pagoFacCxCDT.setMtoPagadoCxC(notaCred.getMtoNotaCredDebCxC());
        pagoFacCxCDT.setFecEmisionCxC(notaCred.getFecNotaCredDebCxC());

        return pagoFacCxCDT;
    }

    public Concepto buscarConcepto(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto;
    }

    public String formatearFecha(Calendar fechaFac) {

        //----------------------------------------------------------------
        //Se da formato a la fecha del pago para mostrarla en el reporte
        //----------------------------------------------------------------

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

        return stringFechaDp;
    }

    public FacGastoNoComun buscarFacGastoNoComun(Integer idFac) throws ValidatorException {

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

    public Alicuota buscarAlicuota(Integer idFac) throws ValidatorException {

        Alicuota alic;
        String nombQuery = "buscarAlicuotaPorId";
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            alic = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return alic;
    }

    public FacCuotaEsp buscarFacCuotaEsp(Integer idFac) throws ValidatorException {

        FacCuotaEsp fce;
        String nombQuery = "buscarFacCuotaEspPorId";
        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fce;
    }

    public NotaCredDebCxC buscarNotaDebito(Integer idFac) throws ValidatorException {

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

    public DeudaPendCxC buscarDeudaPendiente(Integer idFac) throws ValidatorException {

        DeudaPendCxC dpe;
        String nombQuery = "buscarDeudaPendPorId";
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dpe = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return dpe;
    }

    public String enviarEmailReciboPagoInm() throws ParseException {

        String vacio = new String();
        String mensaje = new String();

        mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return (null);
        }

        Inmueble inmueble = buscarInmueble(Integer.valueOf(idInmueble));
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setEmailPropInmueble(inmueble.getEmailPropInmueble());

        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getSessionMap().put("IdInmueble", Integer.parseInt(idInmueble));

        return "email";
    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }

    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public void setNumDocPagoCxC(String numDocPagoCxC) {
        this.numDocPagoCxC = numDocPagoCxC;
    }

    public String getNumDocPagoCxC() {
        return numDocPagoCxC;
    }

    public String getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(String mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getNumRefPagoCxC() {
        return numRefPagoCxC;
    }

    public void setNumRefPagoCxC(String numRefPagoCxC) {
        this.numRefPagoCxC = numRefPagoCxC;
    }

    public String getFecDocPagoCxC() {
        return fecDocPagoCxC;
    }

    public void setFecDocPagoCxC(String fecDocPagoCxC) {
        this.fecDocPagoCxC = fecDocPagoCxC;
    }

    public void setBancoPagoCxC(String bancoPagoCxC) {
        this.bancoPagoCxC = bancoPagoCxC;
    }

    public String getBancoPagoCxC() {
        return bancoPagoCxC;
    }

    // .....
    public void setFilaSeleccionada(PagoCxCDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoCxCDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
