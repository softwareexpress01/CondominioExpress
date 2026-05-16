/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Proveedor;
import informes.modelos.CxPProveedorDT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.TranFondo;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.ComparadorCxPProveedorDT;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ElimProveedorCtrl extends DaoCrud<Proveedor, Condominio, Integer> implements Serializable {

    public ElimProveedorCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        return "regresar";

    }

    public void eliminarProveedor() throws ValidatorException {

        Collection<CxPProveedorDT> listaCxPParcial = new ArrayList<CxPProveedorDT>();

        FacesContext ctx = FacesContext.getCurrentInstance();
        Proveedor provSel = (Proveedor) ctx.getExternalContext().getSessionMap().get("Proveedor");

        Double totSaldoProv = buscarSaldoProveedor(provSel.getId());

        if (totSaldoProv == 0) {

            provSel.setEdoProveedor("INA");
            try {
                almacenarOModificarEntidad(provSel);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Este Proveedor no puede ser eliminado. Existen Cuentas por Pagar con él a la fecha"));
            return;

        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Proveedor");
        ctx.getExternalContext().getSessionMap().remove("ProveedorDT");

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Proveedor " + provSel.getNombProveedor() + " ha sido eliminado de la Base de datos "));
        return;

    }

    Double buscarSaldoProveedor(Integer idProv) {

        Double totSaldoProv = 0.0;
        Double cxpParcial = 0.0;

        cxpParcial = getDeudaPendienteProv(idProv);
        totSaldoProv = cxpParcial;
        cxpParcial = 0.0;

        cxpParcial = getNotasCrProv(idProv);
        totSaldoProv = totSaldoProv + cxpParcial;
        cxpParcial = 0.0;

        cxpParcial = getGCOrdProv(idProv);
        totSaldoProv = totSaldoProv + cxpParcial;
        cxpParcial = 0.0;

        cxpParcial = getGCExtProv(idProv);
        totSaldoProv = totSaldoProv + cxpParcial;
        cxpParcial = 0.0;

        cxpParcial = getGNCProv(idProv);
        totSaldoProv = totSaldoProv + cxpParcial;
        cxpParcial = 0.0;

        // lo siguiente se elimina debido a que ya se cambio TranFondo para que solo acepte ingresos de fondos no pagos a provvedores . FRH
        /*cxpParcial = getTranFondoProv(idProv);
        totSaldoProv = totSaldoProv + cxpParcial;
        cxpParcial = 0.0;*/

        return totSaldoProv;
    }

    public Double getDeudaPendienteProv(Integer idProv) {

        Collection<CxPProveedorDT> listaDP = new ArrayList<CxPProveedorDT>();
        Collection<DeudaPendCxP> deudaPendiente = new ArrayList<DeudaPendCxP>();
        Double cxp = 0.0;

        String nombQuery = "buscarDeudasPendientesPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaEntidadesPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deudaPendiente.isEmpty()) {
            return cxp;
        }

        String estado;
        for (Iterator<DeudaPendCxP> it = deudaPendiente.iterator();
                it.hasNext();) {
            DeudaPendCxP deudaP = (DeudaPendCxP) it.next();
            estado = deudaP.getEstatusDeudaPendCxP();
            if (!"CANC".equals(estado)) {
                cxp = cxp + deudaP.getSaldoDeudaPendCxP();
            }
        }
        return cxp;
    }

    public Double getNotasCrProv(Integer idProv) {

        Collection<NotaCredDebCxP> deuda = new ArrayList<NotaCredDebCxP>();

        Double cxp = 0.0;
        String nombQuery = "buscarNotasCredDebCxPPorIdProveedorII";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deuda = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deuda.isEmpty()) {

            return cxp;
        }
        String estado;
        String tipoNota;
        String concepto;

        for (Iterator<NotaCredDebCxP> it = deuda.iterator();
                it.hasNext();) {
            NotaCredDebCxP deudaP = (NotaCredDebCxP) it.next();
            estado = deudaP.getEdoNotaCredDebCxP();
            tipoNota = deudaP.getTipoNotaCredDebCxP();

            if (!(estado.equals("CANC"))) {
                if (tipoNota.equals("Credito")) {
                    cxp = cxp + deudaP.getSaldoNotaCredDebCxP();
                }
            }
        }
        return cxp;
    }

    public Double getGCOrdProv(Integer idProv) {

        Double cxp = 0.0;
        Collection<GastoComunOrd> deudaPendiente = new ArrayList<GastoComunOrd>();

        String nombQuery = "buscarGastoComunOrdPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deudaPendiente.isEmpty()) {
            return cxp;
        }
        String estado;

        for (Iterator<GastoComunOrd> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoComunOrd deudaP = (GastoComunOrd) it.next();
            estado = deudaP.getEdoGastoComunOrd();
            if (!"CANC".equals(estado)) {
                cxp = cxp + deudaP.getSaldoGastoComunOrd();
            }
        }
        return cxp;
    }

    public Double getGCExtProv(Integer idProv) {

        Double cxp = 0.0;
        Collection<GastoComunExt> deudaPendiente = new ArrayList<GastoComunExt>();

        String nombQuery = "buscarGastoComunExtPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deudaPendiente.isEmpty()) {
            return cxp;
        }
        String estado;

        for (Iterator<GastoComunExt> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoComunExt deudaP = (GastoComunExt) it.next();
            estado = deudaP.getEdoGastoComunExt();
            if (!"CANC".equals(estado)) {

                cxp = cxp + deudaP.getSaldoGastoComunExt();
            }
        }
        return cxp;
    }

    public Double getGNCProv(Integer idProv) {

        Double cxp = 0.0;
        Collection<GastoNoComun> deudaPendiente = new ArrayList<GastoNoComun>();

        String nombQuery = "buscarGastoNoComunPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deudaPendiente.isEmpty()) {
            return cxp;
        }
        String estado;

        for (Iterator<GastoNoComun> it = deudaPendiente.iterator();
                it.hasNext();) {
            GastoNoComun deudaP = (GastoNoComun) it.next();
            estado = deudaP.getEdoGastoNoComun();
            if (!"CANC".equals(estado)) {
                cxp = cxp + deudaP.getSaldoGastoNoComun();
            }
        }
        return cxp;
    }

    public Double getTranFondoProv(Integer idProv) {

        Double cxp = 0.0;
        Collection<TranFondo> deudaPendiente = new ArrayList<TranFondo>();

        String nombQuery = "buscarTranFondoPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            deudaPendiente = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return cxp;
        }

        if (deudaPendiente.isEmpty()) {
            return cxp;
        }
        String estado;

        for (Iterator<TranFondo> it = deudaPendiente.iterator();
                it.hasNext();) {
            TranFondo deudaP = (TranFondo) it.next();
            estado = deudaP.getEstadoTranFondo();
            if (!"CANC".equals(estado)) {

                cxp = cxp + deudaP.getSaldoTranFondo();
            }
        }
        return cxp;
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
}
