/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informes.backing;

/**
 *
 * @author Frank R.H.
 */
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Proveedor;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import movimientos.modelos.Alicuota;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import informes.modelos.BancoSaldoDT;
import informes.modelos.CxCDT;
import informes.modelos.CxPProveedorDT;
import informes.modelos.SaldosProvDT;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.TranFondo;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ConExpBalGenCtrl extends DaoCrud<Propiedad, Propiedad, Integer> implements Serializable {

    BeanUsuario beanUsuario;
    Double totGenBancos = 0.0;
    String nombProveedor;
    Double totSaldoProv = 0.0;
    Double totProv = 0.0;

    public ConExpBalGenCtrl() {
    }

    public void generarReporteBalGen() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/BalGen.jasper");
        InputStream subrep = this.getClass().getResourceAsStream("/informes/modelos/BalGenSubRep01.jasper");
        InputStream subrep02 = this.getClass().getResourceAsStream("/informes/modelos/BalGenSubRep02.jasper");



        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();

        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        Condominio condominio = buscarCondominio(idCond);
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);


        //-------------------------------------------------------
        // Se genera el datasource de Bancos y cuentas 
        //-------------------------------------------------------
        JRBeanCollectionDataSource dataSourceSaldos;
        JRBeanCollectionDataSource dataSourceSaldosProv;

        Collection saldos = buscarSaldosPorCuentas(idCond);

        Double montoCxC = buscarMontoCxC(idCond);

        Collection saldosProv = buscarSaldosPorProveedor(idCond);

        Double saldoPrestaciones = buscarSaldoPrestaciones(idCond);

        dataSourceSaldos = new JRBeanCollectionDataSource(saldos);
        dataSourceSaldosProv = new JRBeanCollectionDataSource(saldosProv);

        //---------------------------------------------------------
        HashMap parametros = new HashMap();


        parametros.put("condominio", nombCond);
        parametros.put("subrep", subrep);
        parametros.put("bancos", dataSourceSaldos);
        parametros.put("subrep02", subrep02);
        parametros.put("saldosProv", dataSourceSaldosProv);
        parametros.put("montoCxC", montoCxC);
        parametros.put("totGenBancos", totGenBancos);
        parametros.put("totProv", totProv);
        parametros.put("saldoPrestaciones", saldoPrestaciones);
        parametros.put("logo", logo);

        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, new JREmptyDataSource());
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
    }

    public Collection buscarSaldosPorCuentas(Integer idCond) {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado
        //-------------------------------------------------------------------------------------
        Collection listBan = new ArrayList();

        Collection cuentas = new ArrayList();
        List bancos = new ArrayList();

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            bancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {

            return (null);
        }
        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        Banco verLectura = (Banco) bancos.get(0);
        if (verLectura == null) {
            return (List) listBan;
        }

        /*---------------------------------------------------------------------------------*/

        Double totCuentas;

        for (int i = 0; i < bancos.size(); i++) {
            Banco bco = (Banco) bancos.get(i);

            String nombQuery2 = "buscarCuentasPorIdBanco";
            DaoQuery<Collection> dataAcces2 = new DaoQuery<Collection>();
            Integer idBcoInteger = bco.getId();
            try {
                cuentas = dataAcces.buscarListaEntidadesPorId(idBcoInteger, nombQuery2);
            } catch (HibernateException he) {
                return listBan;
            }

            totCuentas = 0.0;
            if (!(cuentas.isEmpty())) {
                for (Iterator<Cuenta> it = cuentas.iterator();
                        it.hasNext();) {
                    Cuenta cta = (Cuenta) it.next();
                    if (!(cta.getEdoCuenta().equals("INA"))) {
                        totCuentas = totCuentas + cta.getSaldoActCuenta();
                    }
                }
            }
            BancoSaldoDT bancoSaldoDT = new BancoSaldoDT();
            bancoSaldoDT.setNombBanco(bco.getNombBanco());
            bancoSaldoDT.setSaldoBanco(totCuentas);
            listBan.add(bancoSaldoDT);
            totGenBancos = totGenBancos + totCuentas;

        }
        return (List) listBan;
    }

//*************************************************** METODOS PARA EL obtener el total de la CXC  ************************************************************//
    public Double buscarMontoCxC(Integer idCond) {

        Condominio condominio = buscarCondominioFetchPropiedades(idCond);
        Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
        listaInm = buscarInmueblesPropiedades(condominio);

        Collection<CxCDT> listaCxCParcial = new ArrayList<CxCDT>();
        Double cxcTotal = 0.0;

        /*-----POR CADA INMUEBLE SE BUSCAN LAS CUENTAS X COBRAR ASOCIADAS -----------*/

        for (Iterator<Inmueble> it = listaInm.iterator();
                it.hasNext();) {
            Inmueble inmObj = (Inmueble) it.next();
            Integer idInm = inmObj.getId();
            //-------------------------------------------------------------------//
            listaCxCParcial = getDeudaPendiente(idInm);
            for (Iterator<CxCDT> it1 = listaCxCParcial.iterator();
                    it1.hasNext();) {
                CxCDT dp = (CxCDT) it1.next();
                cxcTotal = cxcTotal + dp.getMtoFac();
            }
            listaCxCParcial.clear();
            //-------------------------------------------------------------------//  
            listaCxCParcial = getAlicuotas(idInm);
            for (Iterator<CxCDT> it2 = listaCxCParcial.iterator();
                    it2.hasNext();) {
                CxCDT dp = (CxCDT) it2.next();
                cxcTotal = cxcTotal + dp.getMtoFac();
            }
            listaCxCParcial.clear();
            //-------------------------------------------------------------------//
            listaCxCParcial = getNotasDeDebito(condominio);
            for (Iterator<CxCDT> it3 = listaCxCParcial.iterator();
                    it3.hasNext();) {
                CxCDT dp = (CxCDT) it3.next();
                cxcTotal = cxcTotal + dp.getMtoFac();
            }
            listaCxCParcial.clear();
            //------------------------------------------------------------------
            listaCxCParcial = getFacCuotaEsp(idInm);
            for (Iterator<CxCDT> it4 = listaCxCParcial.iterator();
                    it4.hasNext();) {
                CxCDT dp = (CxCDT) it4.next();
                cxcTotal = cxcTotal + dp.getMtoFac();
            }
            listaCxCParcial.clear();
            //------------------------------------------------------------------

            listaCxCParcial = getFacGNC(idInm);
            for (Iterator<CxCDT> it5 = listaCxCParcial.iterator();
                    it5.hasNext();) {
                CxCDT dp = (CxCDT) it5.next();
                cxcTotal = cxcTotal + dp.getMtoFac();
            }
        }

        return cxcTotal;

    }

    public Collection getDeudaPendiente(Integer idInm) {

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

    public Collection getAlicuotas(Integer idInm) {

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

    public Collection getNotasDeDebito(Condominio condominio) {

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

    public Collection getFacCuotaEsp(Integer idInm) {

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

    public Collection getFacGNC(Integer idInm) {

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
            if (!("INAC".equals(fgnc.getEdoFacGastoNoComun()))) {

                cxc.setId(fgnc.getId());
                cxc.setMtoFac(fgnc.getSaldoFacGastoNoComun());
                listaFGNC.add(cxc);

            }
        }

        return listaFGNC;

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

        }
        return lista;
    }

    // ********************************** METODOS PARA EL SUBREP02 : OBTIENE EL SALDO DE CADA UNO DE LOS PROVEEDORES ************************//
    public Collection buscarSaldosPorProveedor(Integer idCond) {

        List listaProv = new ArrayList();
        Collection listaOrd = new ArrayList();
        Collection<CxPProveedorDT> listaCxPParcial = new ArrayList<CxPProveedorDT>();
        Collection<SaldosProvDT> listaCxPTotal = new ArrayList<SaldosProvDT>();

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
            SaldosProvDT saldosProv = new SaldosProvDT();

            listaCxPParcial = getDeudaPendienteProv(idProv);
            for (Iterator<CxPProveedorDT> it = listaCxPParcial.iterator();
                    it.hasNext();) {
                CxPProveedorDT cxpDP = (CxPProveedorDT) it.next();
                totSaldoProv = totSaldoProv + cxpDP.getSaldoFac();
            }
            listaCxPParcial.clear();

            listaCxPParcial = getNotasCrProv(idProv);
            for (Iterator<CxPProveedorDT> it2 = listaCxPParcial.iterator();
                    it2.hasNext();) {
                CxPProveedorDT cxpNCr = (CxPProveedorDT) it2.next();
                totSaldoProv = totSaldoProv + cxpNCr.getSaldoFac();
            }
            listaCxPParcial.clear();

            listaCxPParcial = getGCOrdProv(idProv);
            for (Iterator<CxPProveedorDT> it3 = listaCxPParcial.iterator();
                    it3.hasNext();) {
                CxPProveedorDT cxpGCord = (CxPProveedorDT) it3.next();
                totSaldoProv = totSaldoProv + cxpGCord.getSaldoFac();

            }
            listaCxPParcial.clear();

            listaCxPParcial = getGCExtProv(idProv);
            for (Iterator<CxPProveedorDT> it4 = listaCxPParcial.iterator();
                    it4.hasNext();) {
                CxPProveedorDT cxpGCext = (CxPProveedorDT) it4.next();
                totSaldoProv = totSaldoProv + cxpGCext.getSaldoFac();
            }
            listaCxPParcial.clear();

            listaCxPParcial = getGNCProv(idProv);
            for (Iterator<CxPProveedorDT> it5 = listaCxPParcial.iterator();
                    it5.hasNext();) {
                CxPProveedorDT cxpGNC = (CxPProveedorDT) it5.next();
                totSaldoProv = totSaldoProv + cxpGNC.getSaldoFac();
            }
            /**
             * listaCxPParcial.clear();
             *
             * listaCxPParcial = getTranFondoProv(idProv); for
             * (Iterator<CxPProveedorDT> it6 = listaCxPParcial.iterator();
             * it6.hasNext();) { CxPProveedorDT cxpTranFondo = (CxPProveedorDT)
             * it6.next(); totSaldoProv = totSaldoProv +
             * cxpTranFondo.getSaldoFac(); }
             */
            saldosProv.setId(idProv);
            saldosProv.setNombProv(nombProveedor);
            saldosProv.setSaldoProv(totSaldoProv);
            listaCxPTotal.add(saldosProv);
            totProv = totProv + totSaldoProv;
            totSaldoProv = 0.0;

        }

        return listaCxPTotal;
    }

    public Collection getDeudaPendienteProv(Integer idProv) {

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
        //CxPProveedorDT cxp = new CxPProveedorDT();
        String estado;
        for (Iterator<DeudaPendCxP> it = deudaPendiente.iterator();
                it.hasNext();) {
            DeudaPendCxP deudaP = (DeudaPendCxP) it.next();
            estado = deudaP.getEstatusDeudaPendCxP();
            if (!("CANC".equals(estado))) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecDeudaPendCxP());
                cxp.setNumDoc(deudaP.getRefDeudaPendCxP());
                cxp.setConcepto(deudaP.getDescDeudaPendCxP());
                cxp.setMtoFac(deudaP.getMtoDeudaPendCxP());
                cxp.setSaldoFac(deudaP.getSaldoDeudaPendCxP());
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getNotasCrProv(Integer idProv) {

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
        String estado;
        String tipoNota;
        Integer idConcepto;
        String concepto;

        for (Iterator<NotaCredDebCxP> it = deuda.iterator();
                it.hasNext();) {
            NotaCredDebCxP deudaP = (NotaCredDebCxP) it.next();
            estado = deudaP.getEdoNotaCredDebCxP();
            tipoNota = deudaP.getTipoNotaCredDebCxP();

            if (!(estado.equals("CANC"))) {
                if (tipoNota.equals("Credito")) {
                    CxPProveedorDT cxp = new CxPProveedorDT();
                    cxp.setNombProveedor(nombProveedor);
                    cxp.setFecFac(deudaP.getFecNotaCredDebCxP());
                    cxp.setNumDoc(deudaP.getNumNotaCredDebCxP());
                    idConcepto = deudaP.getIdConcepto();
                    concepto = buscarNombConcepto(idConcepto);
                    cxp.setConcepto(concepto);
                    cxp.setMtoFac(deudaP.getMtoNotaCredDebCxP());
                    cxp.setSaldoFac(deudaP.getSaldoNotaCredDebCxP());
                    listaDP.add(cxp);
                }
            }
        }
        return listaDP;
    }

    public String buscarNombConcepto(Integer idConc) {

        Concepto concepto;
        String descConcepto;
        String nombQuery = "buscarConceptoPorId";
        if (idConc > 0) {
            DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
            try {
                concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            descConcepto = concepto.getDescConcepto();
        } else {
            descConcepto = " ";
        }

        return descConcepto;
    }

    public Collection getGCOrdProv(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<GastoComunOrd> deudaPendiente = new ArrayList<GastoComunOrd>();

        String nombQuery = "buscarGastoComunOrdPorIdProveedor";
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
        //CxPProveedorDT cxp = new CxPProveedorDT();
        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoComunOrd> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoComunOrd deudaP = (GastoComunOrd) it.next();
            estado = deudaP.getEdoGastoComunOrd();
            if (!("CANC".equals(estado))) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoComunOrd());
                cxp.setNumDoc(deudaP.getNumDocGastoComunOrd());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoComunOrd());
                cxp.setSaldoFac(deudaP.getSaldoGastoComunOrd());
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getGCExtProv(Integer idProv) {

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
        //CxPProveedorDT cxp = new CxPProveedorDT();
        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoComunExt> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoComunExt deudaP = (GastoComunExt) it.next();
            estado = deudaP.getEdoGastoComunExt();
            if (!("CANC".equals(estado))) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoComunExt());
                cxp.setNumDoc(deudaP.getNumDocGastoComunExt());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoComunExt());
                cxp.setSaldoFac(deudaP.getSaldoGastoComunExt());
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getGNCProv(Integer idProv) {

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
        //CxPProveedorDT cxp = new CxPProveedorDT();
        String estado;
        Integer idConcepto;
        String concepto;

        for (Iterator<GastoNoComun> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoNoComun deudaP = (GastoNoComun) it.next();
            estado = deudaP.getEdoGastoNoComun();
            if (!("CANC".equals(estado))) {
                CxPProveedorDT cxp = new CxPProveedorDT();
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecGastoNoComun());
                cxp.setNumDoc(deudaP.getNumDocGastoNoComun());
                idConcepto = deudaP.getIdConcepto();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoGastoNoComun());
                cxp.setSaldoFac(deudaP.getSaldoGastoNoComun());
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

    public Collection getTranFondoProv(Integer idProv) {

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
            if (!("CANC".equals(estado))) {
                cxp.setNombProveedor(nombProveedor);
                cxp.setFecFac(deudaP.getFecTranFondo());
                cxp.setNumDoc(deudaP.getDocTranFondo());
                idConcepto = deudaP.getIdConceptoTranFondo();
                concepto = buscarNombConcepto(idConcepto);
                cxp.setConcepto(concepto);
                cxp.setMtoFac(deudaP.getMtoTranFondo());
                cxp.setSaldoFac(deudaP.getSaldoTranFondo());
                listaDP.add(cxp);
            }
        }
        return listaDP;
    }

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

    public Collection getProveedoresPorIdCond() {

        // Obtiene los Proveedores para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Proveedor> listaProv = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaProv = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listaProv;
    }

    public Double buscarSaldoPrestaciones(Integer idCond) {

        Double saldoPrestaciones = 0.0;
        Collection<Fondo> listFondo = getFondosPorCondominio(idCond);
        if (listFondo.isEmpty()) {
            saldoPrestaciones = 0.0;
            return saldoPrestaciones;
        }
        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {
            Fondo fon = (Fondo) it.next();
            if ("Prestaciones Sociales".equals(fon.getTipoFondo())) {
                saldoPrestaciones = fon.getSaldoFondo();
            }
        }
        return saldoPrestaciones;
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
}
