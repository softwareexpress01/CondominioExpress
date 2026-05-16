/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author Frank
 */
import java.io.IOException; 
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.io.FileInputStream;
import javax.faces.application.FacesMessage;
import util.*;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Proveedor;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import informes.modelos.AvisoDeCobroDT;
import informes.modelos.AvisoInmDT;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.TranFondo;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.NotaCredDebCxC;
import informes.modelos.NumAvisoDeCobro;
import configuracion.modelos.Parametro;

import informes.modelos.CuentaDT;
import informes.modelos.CxPProveedorDT;
import informes.modelos.CxCDT;
import informes.modelos.CxCInmDT;
import java.util.Calendar;

import java.util.Iterator;
import java.util.List;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;


public class InformesCxPCtrl implements Serializable {

    List bancos = new ArrayList();
    String nombProveedor;
    Calendar periodoActual;
    BeanUsuario beanUsuario;
    Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
    Collection<AvisoDeCobroDT> gastoTotal = new ArrayList<AvisoDeCobroDT>();
    Collection<AvisoDeCobroDT> gastoTotalOrd = new ArrayList<AvisoDeCobroDT>();
    Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();
    Collection avisos = new ArrayList();
    Double mtoReserva;
    Double mtoPrestaciones;
    Double totalGeneral = 0.0;
    Double cxcTotal = 0.0;
    Double mtoAlicuota = 0.0;
    Double mtoIniReserva;
    Double saldoReserva;
    Double mtoIniPrestaciones;
    Double saldoPrestaciones;
    Double mtoIniEspecial;
    Double saldoEspecial;
    Integer proxAvisoCobro;
    String obsAvisoCobro;

    public InformesCxPCtrl() {
    }

    public void generarReporteProveedores() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/Proveedor.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = getProveedoresPorIdCond();

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
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

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getProveedoresPorIdCond() {

        // Obtiene los Proveedores para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Proveedor> listaProv = new ArrayList<Proveedor>();
        Collection<Proveedor> listaProvEdit = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaProv = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        // Editar el atributo ced/rif

        if (listaProv.isEmpty()) {
            return listaProv;
        }
        String rifProv1;
        String rifProv2;
        String guion = "-";
       
        for (Iterator<Proveedor> it = listaProv.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            Proveedor provEdit = new Proveedor();
            provEdit.setId(prov.getId());
            provEdit.setCedRifProveedor(prov.getCedRifProveedor());
            provEdit.setContactoProveedor(prov.getContactoProveedor());
            provEdit.setDirProveedor(prov.getDirProveedor());
            provEdit.setEmailProveedor(prov.getEmailProveedor());
            provEdit.setNombProveedor(prov.getNombProveedor());
            provEdit.setTel1Proveedor(prov.getTel1Proveedor());
            provEdit.setTelCelProveedor(prov.getTelCelProveedor());
            
            rifProv1 = prov.getCedRifProveedor().substring(0,1);
            rifProv2 = prov.getCedRifProveedor().substring(1);
            provEdit.setCedRifProveedor(rifProv1+guion+rifProv2);
            
            listaProvEdit.add(provEdit);

        }
        return listaProvEdit;
    }

    public void generarReporteCuentas() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/Cuentas.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = getCuentasBancarias();

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCondominio = beanUsuario.getCondominioActual();

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

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getCuentasBancarias() {

        Collection listBan = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
        Collection<Cuenta> listaCuentas = new ArrayList<Cuenta>();
        Collection<CuentaDT> listaCuentasDT = new ArrayList<CuentaDT>();
        listBan.clear();

        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------
        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
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

        if (bancos.isEmpty()) {
            return (List) listaOrd;
        }
        /*-----POR CADA BANCO SE BUSCAN LAS CUENTAS ASOCIADAS -------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {

            Banco bco = (Banco) bancos.get(i);
            Integer idBanco = bco.getId();
            nombQuery = "buscarCuentasPorIdBanco";
            DaoQuery<Collection> dataAccesII = new DaoQuery<Collection>();
            try {
                cuentas = dataAccesII.buscarListaEntidadesPorId(idBanco, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return listaOrd;
            }

            /*if (cuentas.isEmpty()) {
             return (List) listaOrd;
             }*/

            for (Iterator<Cuenta> it = cuentas.iterator();
                    it.hasNext();) {
                Cuenta cta = (Cuenta) it.next();
                listaCuentas.add(cta);
            }

            cuentas.clear();
        }

        listaCuentasDT = inicializarCuentasDT(listaCuentas);
        listaOrd = ordenarListaCuentas(listaCuentasDT);// para ordenar la lista de cuentas*/

        return listaOrd;
    }

    private Collection<CuentaDT> inicializarCuentasDT(Collection listaOrd) {

        Collection<CuentaDT> listaCuentasDT = new ArrayList<CuentaDT>();

        String nombreBanco = "";
        for (Iterator<Cuenta> it = listaOrd.iterator();
                it.hasNext();) {

            Cuenta cta = (Cuenta) it.next();
            if (cta == null) {// Si no hay Cuentas DEFINIDAS devuelve la lista en blanco
                break;
            } else {

                CuentaDT cuentaDT = new CuentaDT();

                cuentaDT.setId(cta.getId());
                Integer idBanco = cta.getNombBanco();
                nombreBanco = buscarNombreBanco(idBanco);
                cuentaDT.setNombreBanco(nombreBanco);
                cuentaDT.setNumCuenta(cta.getNumCuenta());
                cuentaDT.setTipoCuenta(cta.getTipoCuenta());
                cuentaDT.setSaldoIniCuenta(cta.getSaldoIniCuenta());
                cuentaDT.setSaldoActCuenta(cta.getSaldoActCuenta());

                listaCuentasDT.add(cuentaDT);
            }
        }
        return listaCuentasDT;
    }

    public Collection ordenarListaCuentas(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<CuentaDT> listaDesOrdenada = new ArrayList<CuentaDT>();

        for (Iterator<CuentaDT> it = lista.iterator();
                it.hasNext();) {
            CuentaDT con = (CuentaDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCuentaDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<CuentaDT> coleccionOrdenada = new ArrayList<CuentaDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            CuentaDT ca = listaDesOrdenada.get(i);

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

    public void generarReporteCxPProveedor() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/CxPProveedor.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = getCuentasPorPagar();

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String nombCondominio = beanUsuario.getCondominioActual();

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

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametro, dataSource);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection getCuentasPorPagar() {

        List listaProv = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<CxPProveedorDT> listaCxPParcial = new ArrayList<CxPProveedorDT>();
        Collection<CxPProveedorDT> listaCxPTotal = new ArrayList<CxPProveedorDT>();

        Collection<Proveedor> proveedores = new ArrayList<Proveedor>(); // se obtiene la colección de Proveedores y se pasa a una lista
        proveedores = getProveedoresPorIdCond();

        if (proveedores.isEmpty()) {
            return (List) listaOrd;
        }
        for (Iterator<Proveedor> it = proveedores.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            listaProv.add(prov);
        }

        /*-----POR CADA PROVEEDOR SE BUSCAN LAS CUENTAS X PAGAR ASOCIADAS EN CADA TABLA DE GASTOS -------------------------------------------------------------*/

        for (int i = 0; i < proveedores.size(); i++) {

            Proveedor provObj = (Proveedor) listaProv.get(i);
            Integer idProv = provObj.getId();
            nombProveedor = provObj.getNombProveedor();

            listaCxPParcial = getDeudaPendiente(idProv);
            for (Iterator<CxPProveedorDT> it = listaCxPParcial.iterator();
                    it.hasNext();) {
                CxPProveedorDT cxpDP = (CxPProveedorDT) it.next();
                listaCxPTotal.add(cxpDP);
            }
            listaCxPParcial.clear();

            listaCxPParcial = getNotasCr(idProv);
            for (Iterator<CxPProveedorDT> it2 = listaCxPParcial.iterator();
                    it2.hasNext();) {
                CxPProveedorDT cxpNCr = (CxPProveedorDT) it2.next();
                listaCxPTotal.add(cxpNCr);
            }
            listaCxPParcial.clear();

            listaCxPParcial = getGCOrd(idProv);
            for (Iterator<CxPProveedorDT> it3 = listaCxPParcial.iterator();
                    it3.hasNext();) {
                CxPProveedorDT cxpGCord = (CxPProveedorDT) it3.next();
                listaCxPTotal.add(cxpGCord);

            }
            listaCxPParcial.clear();

            listaCxPParcial = getGCExt(idProv);

            for (Iterator<CxPProveedorDT> it4 = listaCxPParcial.iterator();
                    it4.hasNext();) {
                CxPProveedorDT cxpGCext = (CxPProveedorDT) it4.next();
                listaCxPTotal.add(cxpGCext);
            }
            listaCxPParcial.clear();

            listaCxPParcial = getGNC(idProv);
            for (Iterator<CxPProveedorDT> it5 = listaCxPParcial.iterator();
                    it5.hasNext();) {
                CxPProveedorDT cxpGNC = (CxPProveedorDT) it5.next();
                listaCxPTotal.add(cxpGNC);
            }
            listaCxPParcial.clear();

            /*listaCxPParcial = getTranFondo(idProv);
             for (Iterator<CxPProveedorDT> it6 = listaCxPParcial.iterator();
             it6.hasNext();) {
             CxPProveedorDT cxpTranFondo = (CxPProveedorDT) it6.next();
             listaCxPTotal.add(cxpTranFondo);
             }
             listaCxPParcial.clear();*/

        }

        listaOrd = ordenarListaCxP(listaCxPTotal);// para ordenar la lista de cuentas por pagar*/
        return listaOrd;
    }

    public Collection getDeudaPendiente(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<DeudaPendCxP> deudaPendiente = new ArrayList<DeudaPendCxP>();

        String nombQuery = "buscarDeudasPendientesPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaEntidadesPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaDP;
        }

        if (deudaPendiente.isEmpty()) {
            return (List) listaDP;
        }

        String estado;
        for (Iterator<DeudaPendCxP> it = deudaPendiente.iterator();
                it.hasNext();) {
            DeudaPendCxP deudaP = (DeudaPendCxP) it.next();
            estado = deudaP.getEstatusDeudaPendCxP();
            if (!"CANC".equals(estado)) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecDeudaPendCxP());
                cxp.setNumDoc(deudaP.getRefDeudaPendCxP());
                cxp.setConcepto(deudaP.getDescDeudaPendCxP());
                cxp.setMtoFac(deudaP.getMtoDeudaPendCxP());
                cxp.setSaldoFac(deudaP.getSaldoDeudaPendCxP());
                cxp.setDisc("DPE");
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getNotasCr(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<NotaCredDebCxP> deuda = new ArrayList<NotaCredDebCxP>();

        String nombQuery = "buscarNotasCredDebCxPPorIdProveedorII";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deuda = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaDP;
        }

        if (deuda.isEmpty()) {

            return (List) listaDP;
        }

        //CxPProveedorDT cxp = new CxPProveedorDT();
        Integer idConcepto;
        String concepto;

        for (Iterator<NotaCredDebCxP> it = deuda.iterator();
                it.hasNext();) {

            NotaCredDebCxP deudaP = (NotaCredDebCxP) it.next();

            if (!(deudaP.getEdoNotaCredDebCxP().equals("CANC"))) {
                if (deudaP.getTipoNotaCredDebCxP().equals("Credito")) {

                    CxPProveedorDT cxp = new CxPProveedorDT();
                    cxp.setNombProveedor(nombProveedor);
                    cxp.setFecFac(deudaP.getFecNotaCredDebCxP());
                    cxp.setNumDoc(deudaP.getNumNotaCredDebCxP());
                    idConcepto = deudaP.getIdConcepto();
                    concepto = buscarNombConcepto(idConcepto);
                    cxp.setConcepto(concepto);
                    cxp.setMtoFac(deudaP.getMtoNotaCredDebCxP());
                    cxp.setSaldoFac(deudaP.getSaldoNotaCredDebCxP());
                    cxp.setDisc("NCR");
                    listaDP.add(cxp);
                }
            }
        }
        return listaDP;
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

    public Collection getGCOrd(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<GastoComunOrd> deudaGCO = new ArrayList<GastoComunOrd>();

        String nombQuery = "buscarGastoComunOrdPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaGCO = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaDP;
        }

        if (deudaGCO.isEmpty()) {
            return (List) listaDP;
        }

        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoComunOrd> it = deudaGCO.iterator();
                it.hasNext();) {
            GastoComunOrd deudaP = (GastoComunOrd) it.next();
            estado = deudaP.getEdoGastoComunOrd();
            if (!"CANC".equals(estado)) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoComunOrd());
                cxp.setNumDoc(deudaP.getNumDocGastoComunOrd());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoComunOrd());
                cxp.setSaldoFac(deudaP.getSaldoGastoComunOrd());
                cxp.setDisc("GCO");
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getGCExt(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<GastoComunExt> deudaPendiente = new ArrayList<GastoComunExt>();

        String nombQuery = "buscarGastoComunExtPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaDP;
        }

        if (deudaPendiente.isEmpty()) {
            return (List) listaDP;
        }

        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoComunExt> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoComunExt deudaP = (GastoComunExt) it.next();
            estado = deudaP.getEdoGastoComunExt();
            if (!"CANC".equals(estado)) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoComunExt());
                cxp.setNumDoc(deudaP.getNumDocGastoComunExt());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoComunExt());
                cxp.setSaldoFac(deudaP.getSaldoGastoComunExt());
                cxp.setDisc("GCE");
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getGNC(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<GastoNoComun> deudaPendiente = new ArrayList<GastoNoComun>();

        String nombQuery = "buscarGastoNoComunPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaDP;
        }

        if (deudaPendiente.isEmpty()) {
            return (List) listaDP;
        }

        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoNoComun> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoNoComun deudaP = (GastoNoComun) it.next();
            estado = deudaP.getEdoGastoNoComun();
            if (!"CANC".equals(estado)) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoNoComun());
                cxp.setNumDoc(deudaP.getNumDocGastoNoComun());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoNoComun());
                cxp.setSaldoFac(deudaP.getSaldoGastoNoComun());
                cxp.setDisc("GNC");
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    /*public Collection getTranFondo(Integer idProv) {

     Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
     Collection<TranFondo> deudaPendiente = new ArrayList<TranFondo>();

     String nombQuery = "buscarTranFondoPorIdProveedor";
     DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
     try {
     deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
     } catch (HibernateException he) {
     FacesContext.getCurrentInstance().addMessage(null,
     new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
     return listaDP;
     }

     if (deudaPendiente.isEmpty()) {
     return (List) listaDP;
     }
     CxPProveedorDT cxp = new CxPProveedorDT();
     String estado;
     Integer idConcepto;
     String concepto;

     for (Iterator<TranFondo> it = deudaPendiente.iterator();
     it.hasNext();) {
     TranFondo deudaP = (TranFondo) it.next();
     estado = deudaP.getEstadoTranFondo();
     if (!"CANC".equals(estado)) {
     cxp.setNombProveedor(nombProveedor);
     cxp.setFecFac(deudaP.getFecTranFondo());
     cxp.setNumDoc(deudaP.getDocTranFondo());
     idConcepto = deudaP.getIdConceptoTranFondo();

     if (idConcepto == 0) {
     concepto = " ";
     } else {
     concepto = buscarNombConcepto(idConcepto);
     }
     cxp.setConcepto(concepto);
     cxp.setMtoFac(deudaP.getMtoTranFondo());
     cxp.setSaldoFac(deudaP.getSaldoTranFondo());
     listaDP.add(cxp);
     }
     }
     return listaDP;
     }*/
    public Collection ordenarListaCxP(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<CxPProveedorDT> listaDesOrdenada = new ArrayList<CxPProveedorDT>();

        for (Iterator<CxPProveedorDT> it = lista.iterator();
                it.hasNext();) {
            CxPProveedorDT con = (CxPProveedorDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCxPProveedorDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<CxPProveedorDT> coleccionOrdenada = new ArrayList<CxPProveedorDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            CxPProveedorDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    //************************************************* METODOS PARA GENERAR LOS AVISOS DE COBRO ********************************************
    public void generarReporteAvisosDeCobroMstr() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {


        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobroMstr.jasper");
        InputStream subRep = this.getClass().getResourceAsStream("/informes/modelos/AvisoDeCobro.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();

        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();
        String periodo;

        //--------------------------------------------------------------
        //Se da formato al periodo para llevarla al  data table
        //--------------------------------------------------------------

        Calendar fechaFac = beanUsuario.getPeriodoActual();
        periodoActual = fechaFac;
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

        periodo = dia + "-" + mes + "-" + añoFac;
        //--------------------------------------------------------------

        //-------------------------------------------------------
        // Se genera el datasource de Gastos e inmuebles 
        //-------------------------------------------------------


        JRBeanCollectionDataSource dataSource;

        Collection filasReporte = buscarGastos(idCond);

        dataSource = new JRBeanCollectionDataSource(filasReporte);

        JRBeanCollectionDataSource dataSourceII;

        Collection filasReporteII = buscarDataInmuebles(idCond);

        dataSourceII = new JRBeanCollectionDataSource(filasReporteII);

        //---------------------------------------------------------

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        Condominio condominio = buscarCondominio(idCond);
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);

        // Parametros de pruebas para 1 Aviso

        buscarFondos(idCond);

        HashMap parametros = new HashMap();


        parametros.put("nombCondominio", nombCond);
        parametros.put("periodo", periodo);
        parametros.put("subRep", subRep);
        parametros.put("mtoIniReserva", mtoIniReserva);
        parametros.put("saldoReserva", saldoReserva);
        parametros.put("mtoIniPrestaciones", mtoIniPrestaciones);
        parametros.put("saldoPrestaciones", saldoPrestaciones);
        parametros.put("mtoIniEspecial", mtoIniEspecial);
        parametros.put("saldoEspecial", saldoEspecial);
        // parametros.put("proxAvisoCobro", proxAvisoCobro);
        parametros.put("obsAvisoCobro", obsAvisoCobro);


        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceII);
        //-------------------------------------------------------------------------------------------------------------------

        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();

        context.responseComplete();



        //--------------------------------------------------------------------//
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

    public Collection buscarGastos(Integer idCond) {

        Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
        Condominio condominio = buscarCondominioFetchPropiedades(idCond);
        
        // Aqui se determina si se buscaran todos los inmuebles o uno en particular 
        
        
        listaInm = buscarInmueblesPropiedades(condominio);
        listaGCO = buscarGastoComunOrdPorIdCond(idCond);
        mtoReserva = buscarMontoReserva(idCond);
        mtoPrestaciones = buscarMontoPrestaciones(idCond);

        for (Iterator<GastoComunOrd> it2 = listaGCO.iterator();
                it2.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it2.next();

            AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();

            //--------------------------------------------------------------
            //Se da formato a la fecha del mov para llevarla al  data table
            //--------------------------------------------------------------

            Calendar fechaFac = gco.getFecMovGCOrd();
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

            String stringFechagco = dia + "-" + mes + "-" + añoFac;
            //--------------------------------------------------------------

            gastoDT.setGrupo(1);
            gastoDT.setFecGasto(stringFechagco);
            Integer idConcepto = gco.getIdConcepto();
            String concepto = buscarNombConcepto(idConcepto);
            gastoDT.setConceptoGasto(concepto);
            gastoDT.setRefGasto(gco.getNumDocGastoComunOrd());
            Integer idProv = gco.getIdProveedor();
            String nombProv = buscarProveedor(idProv);
            gastoDT.setProvGasto(nombProv);
            gastoDT.setMtoGasto(gco.getMtoGastoComunOrd());

            gastoTotal.add(gastoDT);


        }

        // Agregar un objeto con el gasto por el fondo de reserva //


        AvisoDeCobroDT gastoDT = new AvisoDeCobroDT();
        gastoDT.setGrupo(1);
        gastoDT.setFecGasto("N/A");
        gastoDT.setConceptoGasto("Fondo de Reserva");
        gastoDT.setRefGasto("N/A");
        gastoDT.setProvGasto("N/A");
        gastoDT.setMtoGasto(mtoReserva);

        gastoTotal.add(gastoDT);

        // Agregar un objeto con el gasto por el fondo de prestaciones //

        AvisoDeCobroDT gastoPresDT = new AvisoDeCobroDT();
        gastoPresDT.setGrupo(1);
        gastoPresDT.setFecGasto("N/A");
        gastoPresDT.setConceptoGasto("Prestaciones Sociales");
        gastoPresDT.setRefGasto("N/A");

        gastoPresDT.setProvGasto("N/A ");
        gastoPresDT.setMtoGasto(mtoPrestaciones);

        gastoTotal.add(gastoPresDT);


        gastoTotalOrd = ordenarListaGastos(gastoTotal);
        return gastoTotalOrd;
    }

    public Collection buscarDataInmuebles(Integer idCond) {

        Collection<AvisoInmDT> dataInmAvisos = new ArrayList<AvisoInmDT>();

        for (Iterator<Inmueble> it = listaInm.iterator();
                it.hasNext();) {
            Inmueble inmObj = (Inmueble) it.next();
            AvisoInmDT inmDT = new AvisoInmDT();

            inmDT.setCodInm(inmObj.getCodInmueble());
            inmDT.setPropInm(inmObj.getNombPropInmueble());
            inmDT.setTelInm(inmObj.getTelInmueble());
            inmDT.setEmailInm(inmObj.getEmailPropInmueble());
            buscarMontoCxCInm(inmObj.getId(), idCond, inmObj.getAlicInmueble());
            inmDT.setTotCxC(cxcTotal);
            inmDT.setTotGenCxC(totalGeneral);
            inmDT.setMtoAlicuota(mtoAlicuota);
            inmDT.setNumAviso(111);

            dataInmAvisos.add(inmDT);
        }
        return dataInmAvisos;
    }

    public Condominio buscarCondominioFetchPropiedades(Integer idCond) {

        Condominio condominio;
        String nombQuery = "buscarCondominioFetchPropiedades";
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

    public Collection buscarInmueblesPropiedades(Condominio condominio) {

        Collection<Inmueble> lista = new ArrayList<Inmueble>();
        Collection<Inmueble> listaTot = new ArrayList<Inmueble>();

        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                lista = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            listaTot.addAll(lista);

        }
        return listaTot;
    }

    public String buscarProveedor(Integer idProv) {

        Proveedor proveedor;
        String nombQuery = "buscarProveedorPorId";
        DaoQuery<Proveedor> dataAcces = new DaoQuery<Proveedor>();
        try {
            proveedor = dataAcces.buscarEntidadPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombProv = proveedor.getNombProveedor();
        return nombProv;
    }

    public Collection buscarGastoComunOrdPorIdCond(Integer idCond) {

        Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();

        String nombQuery = "buscarGastosComunesOrdPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCO = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaGCO;
        }

        return listaGCO;
    }

    public Double buscarMontoReserva(Integer idCond) {

        Collection<TranFondo> listaTF = new ArrayList<TranFondo>();
        Double mtoReserva = 0.0;
        String nombQuery = "buscarTranFondosPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaTF = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return mtoReserva;
        }

        if (listaTF.isEmpty()) {
            return mtoReserva;
        }
        Integer idConcepto;
        Integer cero = 0;
        for (Iterator<TranFondo> it = listaTF.iterator();
                it.hasNext();) {
            TranFondo tf = (TranFondo) it.next();

            if (tf.getIdConceptoTranFondo() == 0) {
                mtoReserva = tf.getMtoTranFondo();
                break;
            }
        }
        return mtoReserva;
    }

    public Double buscarMontoPrestaciones(Integer idCond) {

        Collection<Fondo> fondos = new ArrayList<Fondo>();
        fondos = getFondosPorCondominio(idCond);

        Double mtoTotPrestaciones = 0.0;
        Integer idFondoPres = 0;
        if (fondos.isEmpty()) {
            return mtoTotPrestaciones;
        }
        for (Iterator<Fondo> it = fondos.iterator();
                it.hasNext();) {
            Fondo tf = (Fondo) it.next();
            if ("Prestaciones".equals(tf.getTipoFondo())) {
                idFondoPres = tf.getId();
                break;
            }
        }
        if (idFondoPres != 0) {
            Collection<TranFondo> tranFondos = buscarTranFondoPrestaciones(idFondoPres, periodoActual);
            for (Iterator<TranFondo> it2 = tranFondos.iterator();
                    it2.hasNext();) {
                TranFondo tranf = (TranFondo) it2.next();
                mtoTotPrestaciones = (mtoTotPrestaciones + tranf.getMtoTranFondo());
            }

        }
        return mtoTotPrestaciones;
    }

    public Collection ordenarListaGastos(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<AvisoDeCobroDT> listaDesOrdenada = new ArrayList<AvisoDeCobroDT>();

        for (Iterator<AvisoDeCobroDT> it = lista.iterator();
                it.hasNext();) {
            AvisoDeCobroDT con = (AvisoDeCobroDT) it.next();
            listaDesOrdenada.add(con);
        }

        Collections.sort(listaDesOrdenada, new ComparadorAvisoDeCobroDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<AvisoDeCobroDT> coleccionOrdenada = new ArrayList<AvisoDeCobroDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            AvisoDeCobroDT ca = listaDesOrdenada.get(i);

            coleccionOrdenada.add(ca);
        }
        return coleccionOrdenada;
    }

    public Collection getFondosPorCondominio(Integer idCond) {

        Collection<Fondo> listFondo = new ArrayList<Fondo>();
        String nombQuery = "buscarFondosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listFondo = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listFondo;
    }

    private Collection<TranFondo> buscarTranFondoPrestaciones(Integer idFondoPres, Calendar periodoActual) {

        Collection<TranFondo> prestaciones = new ArrayList<TranFondo>();

        String nombQuery = "buscarTranFondoPresPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            prestaciones = dataAcces.buscarListaEntidadesPeriodoActual(idFondoPres, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return prestaciones;
    }

    public void buscarFondos(Integer idCond) {

        Fondo fondoRes = new Fondo();
        String nombQuery = "buscarFondoReservaPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondoRes = dataAcces.buscarEntidad(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
        if (fondoRes != null) {
            mtoIniReserva = fondoRes.getMtoIniFondo();
            saldoReserva = fondoRes.getSaldoFondo();
        } else {
            mtoIniReserva = 0.0;
            saldoReserva = 0.0;
        }

        Fondo fondoPres = new Fondo();
        String nombQuery2 = "buscarFondoPrestacionesPorIdCond";
        DaoQuery<Fondo> dataAcces2 = new DaoQuery<Fondo>();
        try {
            fondoPres = dataAcces2.buscarEntidad(idCond, nombQuery2);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
        if (fondoPres != null) {
            mtoIniPrestaciones = fondoPres.getMtoIniFondo();
            saldoPrestaciones = fondoPres.getSaldoFondo();
        } else {
            mtoIniPrestaciones = 0.0;
            saldoPrestaciones = 0.0;
        }

        Fondo fondoEspecial = new Fondo();
        String nombQuery3 = "buscarFondoEspecialPorIdCond";
        DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
        try {
            fondoEspecial = dataAcces3.buscarEntidad(idCond, nombQuery3);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
        if (fondoEspecial != null) {
            mtoIniEspecial = fondoEspecial.getMtoIniFondo();
            saldoEspecial = fondoEspecial.getSaldoFondo();
        } else {
            mtoIniEspecial = 0.0;
            saldoEspecial = 0.0;
        }

    }

    public Collection buscarParametros(Integer idCond) {

        Condominio condominio;

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        Parametro parametro = condominio.getParametro();
        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun parametro para el condominio actual!.Por favor, contacte con su administrador..."));
            return null;
        }

        avisos.clear();
        proxAvisoCobro = parametro.getProxAvisoCobro();
        obsAvisoCobro = parametro.getObsAvisoCobro();
        for (int i = 1; i <= listaInm.size(); i++) {
            NumAvisoDeCobro avisoObj = new NumAvisoDeCobro();
            avisoObj.setNumAviso(proxAvisoCobro);
            avisos.add(avisoObj);
            proxAvisoCobro = proxAvisoCobro + 1;
        }
        parametro.setProxAvisoCobro(proxAvisoCobro);

        try {
            dataAcces.almacenarOModificarEntidad(condominio);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        return avisos;

    }

//******************************************REPORTE AVISO DE COBRO (Cont...) :  METODOS PARA EL obtener el total de la CXC/Inmn  ************************************************************//
    public void buscarMontoCxCInm(Integer idInm, Integer idCond, Double porcAli) {

        Collection<CxCDT> listaCxCParcial = new ArrayList<CxCDT>();
        Collection<CxCInmDT> listaCxCInm = new ArrayList<CxCInmDT>();
        Condominio condominio = buscarCondominioFetchPropiedades(idCond);

        Double totalGCO = 0.0;
        Double totalParcial = 0.0;
        Double alicuota;

        //-------------------------------------------------------------------//
        listaCxCParcial = getDeudaPendienteInm(idInm);
        for (Iterator<CxCDT> it1 = listaCxCParcial.iterator();
                it1.hasNext();) {
            CxCDT dp = (CxCDT) it1.next();
            cxcTotal = cxcTotal + dp.getMtoFac();
        }
        listaCxCParcial.clear();
        //-------------------------------------------------------------------//  
        listaCxCParcial = getAlicuotasInm(idInm);
        for (Iterator<CxCDT> it2 = listaCxCParcial.iterator();
                it2.hasNext();) {
            CxCDT dp = (CxCDT) it2.next();
            cxcTotal = cxcTotal + dp.getMtoFac();
        }
        listaCxCParcial.clear();
        //-------------------------------------------------------------------//
        listaCxCParcial = getNotasDeDebitoInm(condominio);
        for (Iterator<CxCDT> it3 = listaCxCParcial.iterator();
                it3.hasNext();) {
            CxCDT dp = (CxCDT) it3.next();
            cxcTotal = cxcTotal + dp.getMtoFac();
        }
        listaCxCParcial.clear();
        //------------------------------------------------------------------
        listaCxCParcial = getFacCuotaEspInm(idInm);
        for (Iterator<CxCDT> it4 = listaCxCParcial.iterator();
                it4.hasNext();) {
            CxCDT dp = (CxCDT) it4.next();
            cxcTotal = cxcTotal + dp.getMtoFac();
        }
        listaCxCParcial.clear();
        //------------------------------------------------------------------

        listaCxCParcial = getFacGNCInm(idInm);
        for (Iterator<CxCDT> it5 = listaCxCParcial.iterator();
                it5.hasNext();) {
            CxCDT dp = (CxCDT) it5.next();
            cxcTotal = cxcTotal + dp.getMtoFac();
        }

        // ******************* determinar alicuota y resto de los  gastos a pagar en el periodo  ************************

        totalGCO = 0.0;
        totalParcial = 0.0;
        mtoAlicuota = 0.0;
        totalGeneral = 0.0;

        for (Iterator<GastoComunOrd> it6 = listaGCO.iterator();
                it6.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it6.next();

            totalGCO = totalGCO + gco.getMtoGastoComunOrd();

        }
        totalParcial = totalGCO + mtoReserva + mtoPrestaciones;
        mtoAlicuota = (totalParcial * porcAli) / 100;

        totalGeneral = mtoAlicuota + cxcTotal;

    }

    public Collection getDeudaPendienteInm(Integer idInm) {

        Collection<DeudaPendCxC> lista = new ArrayList<DeudaPendCxC>();
        Collection<CxCDT> listaDP = new ArrayList<CxCDT>();

        String nombQuery = "buscarDeudasPendInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            return listaDP;
        }
        if (lista.isEmpty()) {
            return listaDP;
        }
        CxCDT cxc = new CxCDT();
        String estado;
        for (Iterator<DeudaPendCxC> it = lista.iterator();
                it.hasNext();) {
            DeudaPendCxC deudaP = (DeudaPendCxC) it.next();
            cxc.setId(deudaP.getId());
            cxc.setMtoFac(deudaP.getSaldoDeudaPendCxC());
            listaDP.add(cxc);
        }

        return listaDP;
    }

    public Collection getAlicuotasInm(Integer idInm) {

        Collection<Alicuota> lista = new ArrayList<Alicuota>();
        Collection<CxCDT> listaAli = new ArrayList<CxCDT>();

        String nombQuery = "buscarAlicuotasInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaAli;
        }
        if (lista.isEmpty()) {
            return listaAli;
        }
        CxCDT cxc = new CxCDT();
        for (Iterator<Alicuota> it = lista.iterator();
                it.hasNext();) {
            Alicuota alicuota = (Alicuota) it.next();
            cxc.setId(alicuota.getId());
            cxc.setMtoFac(alicuota.getSaldoAlicuota());
            listaAli.add(cxc);
        }

        return listaAli;
    }

    public Collection getNotasDeDebitoInm(Condominio condominio) {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();
        Collection<CxCDT> listaND = new ArrayList<CxCDT>();

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad pro = (Propiedad) it.next();
            Integer idProp = pro.getId();
            String nombQuery = "buscarInmueblesConNotasDeb";
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                lista = dataAcces.buscarListaFetchPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

                return listaND;
            }
        }
        if (lista.isEmpty()) {
            return listaND;
        }
        CxCDT cxc = new CxCDT();
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC nd = (NotaCredDebCxC) it.next();
            cxc.setId(nd.getId());
            cxc.setMtoFac(nd.getSaldoNotaCredDebCxC());
            listaND.add(cxc);
        }

        return listaND;  

    }

    public Collection getFacCuotaEspInm(Integer idInm) {

        Collection<FacCuotaEsp> lista = new ArrayList<FacCuotaEsp>();
        Collection<CxCDT> listaCE = new ArrayList<CxCDT>();

        String nombQuery = "buscarFacCuotaEspInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaCE;
        }
        if (lista.isEmpty()) {
            return listaCE;
        }
        CxCDT cxc = new CxCDT();
        for (Iterator<FacCuotaEsp> it = lista.iterator();
                it.hasNext();) {
            FacCuotaEsp ce = (FacCuotaEsp) it.next();
            cxc.setId(ce.getId());
            cxc.setMtoFac(ce.getSaldoFacCuotaEsp());
            listaCE.add(cxc);
        }

        return listaCE;
    }

    public Collection getFacGNCInm(Integer idInm) {

        Collection<FacGastoNoComun> lista = new ArrayList<FacGastoNoComun>();
        Collection<CxCDT> listaFGNC = new ArrayList<CxCDT>();

        String nombQuery = "buscarFacGastosNoComunesInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaFGNC;
        }
        if (lista.isEmpty()) {
            return listaFGNC;
        }
        CxCDT cxc = new CxCDT();
        for (Iterator<FacGastoNoComun> it = lista.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            cxc.setId(fgnc.getId());
            cxc.setMtoFac(fgnc.getSaldoFacGastoNoComun());
            listaFGNC.add(cxc);
        }

        return listaFGNC;


    }
}
