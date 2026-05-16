/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import administracion.backing.CierreMensualCtrl;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;

import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoFacCxC;
import org.hibernate.HibernateException;

import util.DaoCrud;
import util.DaoQuery;

/**
 *
 * @author jesus
 */
public class ActualizarCuentasPorCobrarCtrl extends DaoCrud<Inmueble, Propiedad, Integer> implements Serializable {

    private final int idCond = 278;
    private int saldoMenorUno = 0;
    private int saldoMenorCeroPuntoUno = 0;
    private int saldoMayorUno = 0;
    private int contadorGnc = 0;
    private int contadorPagos = 0;
    private int contadorPagosDemas = 0;
    private double diferencia = 0.00;
    private Collection<Inmueble> listaInmuebles = new ArrayList<>();
    private Collection<PagoFacCxC> listaPagosFacCxCInm = new ArrayList<>();
    private Condominio condominio;
    private PagoFacCxC pfcxcAnt;
    private double suma;
    private final CierreMensualCtrl cm = new CierreMensualCtrl();

    public ActualizarCuentasPorCobrarCtrl() {

    }

    public void actualizarCuentasPorCobrar() {

        condominio = cm.buscarCondominioFetchPropiedades(idCond);
        listaInmuebles = cm.buscarInmueblesPropiedades(condominio);

        inicializarContadores();
        ajustarFacGastoNoComun();

        inicializarContadores();
        ajustarFacAlicuotas();

        inicializarContadores();
        ajustarDeudaPend();

        inicializarContadores();
        ajustarNotaDebito();

        inicializarContadores();
        ajustarCuotasEsp();
    }

    private void inicializarContadores() {

        contadorGnc = 0;
        contadorPagos = 0;
        saldoMenorUno = 0;
        contadorPagosDemas = 0;
        saldoMenorCeroPuntoUno = 0;
        saldoMayorUno = 0;
        diferencia = 0.00;

    }

    private void ajustarFacGastoNoComun() {

        PagoFacCxC pfcxc = null;

        Iterator<Inmueble> it = listaInmuebles.iterator();

        while (it.hasNext()) {
            Inmueble inm = (Inmueble) it.next();
            buscarPagoFacGNCInm(inm);
            if (!listaPagosFacCxCInm.isEmpty()) {
            } else {
                continue;
            }
            suma = 0.00;
            Iterator<PagoFacCxC> it1 = listaPagosFacCxCInm.iterator();
            while (it1.hasNext()) {
                pfcxc = (PagoFacCxC) it1.next();
                if (pfcxc.getIdFacCxC().equals(pfcxcAnt.getIdFacCxC())) {
                    suma = suma + pfcxc.getMtoPagadoCxC();
                } else {
                    ajustarFGNC(suma, pfcxcAnt);
                    pfcxcAnt = pfcxc;
                    suma = pfcxc.getMtoPagadoCxC();
                }
                contadorPagos++;
            }
            ajustarFGNC(suma, pfcxc);
        }
        System.out.println(
                "Total pagos procesados   --------------->:" + contadorPagos);
        System.out.println(
                "Total Saldo menor que 0.01 --------------->:" + saldoMenorCeroPuntoUno);
        System.out.println(
                "Total Saldo menor que uno --------------->:" + saldoMenorUno);
        System.out.println(
                "Total Saldo mayor que uno --------------->:" + saldoMayorUno);
        System.out.println(
                "Total fgnc con pagos demas  --------------->:" + contadorPagosDemas);
        System.out.println(
                "Total fgnc actualizadas --------------->:" + contadorGnc);

    }

    private Collection buscarPagoFacGNCInm(Inmueble inm) {

        String nombQuery = "buscarFacPagoCxCGncPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        listaPagosFacCxCInm.clear();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        Iterator<PagoFacCxC> it = listaPagosFacCxCInm.iterator();

        while (it.hasNext()) {
            pfcxcAnt = (PagoFacCxC) it.next();
            break;
        }

        return listaPagosFacCxCInm;
    }

    private void ajustarFGNC(Double suma, PagoFacCxC pfcxc) {

        FacGastoNoComun fgnc;

        try {
            fgnc = cm.buscarFGNC(pfcxc.getIdFacCxC());
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-------------------------------------------------//
        // "suma" es la sumatoria de los pagos de  la fgnc
        //------------------------------------------------//
        diferencia = fgnc.getMtoFacGastoNoComun() - suma;

        if (diferencia < 0 && Math.abs(diferencia) >= 0.01) {
            System.out.println(
                    "--------------- > Sumatoria de pagos mayor que el monto :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
            contadorPagosDemas++;
            return;
        }

        if (diferencia < 0.01) {
            fgnc.setEdoFacGastoNoComun("CANC");
            fgnc.setSaldoFacGastoNoComun(0.00);
            saldoMenorCeroPuntoUno++;
        } else {
            if (diferencia < 1) {
                saldoMenorUno++;
            } else {
                saldoMayorUno++;
            }
            fgnc.setEdoFacGastoNoComun("EPRO");
            fgnc.setSaldoFacGastoNoComun(diferencia);
        }

        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<>();
        try {
            dataAcces.almacenarOModificarEntidad(fgnc);
            contadorGnc++;
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        System.out.println(
                "FGNC actualizada:" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun());
    }

    private void ajustarFacAlicuotas() {

        PagoFacCxC pfcxc = null;

        Iterator<Inmueble> it = listaInmuebles.iterator();

        while (it.hasNext()) {
            Inmueble inm = (Inmueble) it.next();
            buscarPagoFacAlicuotaInm(inm);
            if (!listaPagosFacCxCInm.isEmpty()) {
            } else {
                continue;
            }
            suma = 0.00;
            Iterator<PagoFacCxC> it1 = listaPagosFacCxCInm.iterator();
            while (it1.hasNext()) {
                pfcxc = (PagoFacCxC) it1.next();
                if (pfcxc.getIdFacCxC().equals(pfcxcAnt.getIdFacCxC())) {
                    suma = suma + pfcxc.getMtoPagadoCxC();
                } else {
                    ajustarAlicuota(suma, pfcxcAnt);
                    pfcxcAnt = pfcxc;
                    suma = pfcxc.getMtoPagadoCxC();
                }
                contadorPagos++;
            }
            ajustarAlicuota(suma, pfcxc);
        }
        System.out.println(
                "Total pagos procesados   --------------->:" + contadorPagos);
        System.out.println(
                "Total Saldo menor que 0.01 --------------->:" + saldoMenorCeroPuntoUno);
        System.out.println(
                "Total Saldo menor que uno --------------->:" + saldoMenorUno);
        System.out.println(
                "Total Saldo mayor que uno --------------->:" + saldoMayorUno);
        System.out.println(
                "Total alicuotas con pagos demas  --------------->:" + contadorPagosDemas);
        System.out.println(
                "Total alicuotas actualizadas --------------->:" + contadorGnc);

    }

    private Collection buscarPagoFacAlicuotaInm(Inmueble inm) {

        String nombQuery = "buscarFacPagoCxCAlicPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<>();
        listaPagosFacCxCInm.clear();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando pagos fac");
            return null;
        }

        Iterator<PagoFacCxC> it = listaPagosFacCxCInm.iterator();
        while (it.hasNext()) {
            pfcxcAnt = (PagoFacCxC) it.next();
            break;
        }
        return listaPagosFacCxCInm;
    }

    private void ajustarAlicuota(Double suma, PagoFacCxC pfcxc) {

        Alicuota alic;

        try {
            alic = cm.buscarAlicuota(pfcxc.getIdFacCxC());
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando alicuota");
            return;
        }
        //-------------------------------------------------//
        // "suma" es la sumatoria de los pagos de  la fgnc
        //------------------------------------------------//
        diferencia = alic.getMtoAlicuota() - suma;

        if (diferencia < 0 && Math.abs(diferencia) >= 0.01) {
            System.out.println(
                    "--------------- > Sumatoria de pagos mayor que el monto :" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
            contadorPagosDemas++;
            return;
        }

        if (diferencia < 0.01) {
            alic.setEdoAlicuota("CANC");
            alic.setSaldoAlicuota(0.00);
            saldoMenorCeroPuntoUno++;
        } else {
            if (diferencia < 1) {
                saldoMenorUno++;
            } else {
                saldoMayorUno++;
            }
            alic.setEdoAlicuota("EPRO");
            alic.setSaldoAlicuota(diferencia);
        }

        DaoQuery<Alicuota> dataAcces = new DaoQuery<>();
        try {
            dataAcces.almacenarOModificarEntidad(alic);
            contadorGnc++;
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla almacenando alicuota");
            return;
        }

        System.out.println(
                "Alicuota actualizada:" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota());
    }

    private void ajustarDeudaPend() {

        PagoFacCxC pfcxc = null;

        Iterator<Inmueble> it = listaInmuebles.iterator();

        while (it.hasNext()) {
            Inmueble inm = (Inmueble) it.next();
            buscarPagoFacDeudaPendInm(inm);
            if (!listaPagosFacCxCInm.isEmpty()) {
            } else {
                continue;
            }
            suma = 0.00;
            Iterator<PagoFacCxC> it1 = listaPagosFacCxCInm.iterator();
            while (it1.hasNext()) {
                pfcxc = (PagoFacCxC) it1.next();
                if (pfcxc.getIdFacCxC().equals(pfcxcAnt.getIdFacCxC())) {
                    suma = suma + pfcxc.getMtoPagadoCxC();
                } else {
                    ajustarDeudaPendiente(suma, pfcxcAnt);
                    pfcxcAnt = pfcxc;
                    suma = pfcxc.getMtoPagadoCxC();
                }
                contadorPagos++;
            }
            ajustarDeudaPendiente(suma, pfcxc);
        }
        System.out.println(
                "Total pagos procesados   --------------->:" + contadorPagos);
        System.out.println(
                "Total Saldo menor que 0.01 --------------->:" + saldoMenorCeroPuntoUno);
        System.out.println(
                "Total Saldo menor que uno --------------->:" + saldoMenorUno);
        System.out.println(
                "Total Saldo mayor que uno --------------->:" + saldoMayorUno);
        System.out.println(
                "Total Deuda pend con pagos demas  --------------->:" + contadorPagosDemas);
        System.out.println(
                "Total Deuda pend actualizadas --------------->:" + contadorGnc);

    }

    private Collection buscarPagoFacDeudaPendInm(Inmueble inm) {

        String nombQuery = "buscarFacPagoCxCDeudaPendIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<>();
        listaPagosFacCxCInm.clear();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando pagos fac");
            return null;
        }

        Iterator<PagoFacCxC> it = listaPagosFacCxCInm.iterator();
        while (it.hasNext()) {
            pfcxcAnt = (PagoFacCxC) it.next();
            break;
        }
        return listaPagosFacCxCInm;
    }

    private void ajustarDeudaPendiente(Double suma, PagoFacCxC pfcxc) {

        DeudaPendCxC dpen;

        try {
            dpen = cm.buscarDeudaPend(pfcxc.getIdFacCxC());
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando alicuota");
            return;
        }
        //-------------------------------------------------//
        // "suma" es la sumatoria de los pagos de  la fgnc
        //------------------------------------------------//
        diferencia = dpen.getMtoDeudaPendCxC() - suma;

        if (diferencia < 0 && Math.abs(diferencia) >= 0.01) {
            System.out.println(
                    "--------------- > Sumatoria de pagos mayor que el monto :" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
            contadorPagosDemas++;
            return;
        }

        if (diferencia < 0.01) {
            dpen.setEdoDeudaPendCxC("CANC");
            dpen.setSaldoDeudaPendCxC(0.00);
            saldoMenorCeroPuntoUno++;
        } else {
            if (diferencia < 1) {
                saldoMenorUno++;
            } else {
                saldoMayorUno++;
            }
            dpen.setEdoDeudaPendCxC("EPRO");
            dpen.setSaldoDeudaPendCxC(diferencia);
        }

        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<>();
        try {
            dataAcces.almacenarOModificarEntidad(dpen);
            contadorGnc++;
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla almacenando alicuota");
            return;
        }

        System.out.println(
                "Deuda pendiente actualizada:" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC());
    }

    private void ajustarNotaDebito() {

        PagoFacCxC pfcxc = null;

        Iterator<Inmueble> it = listaInmuebles.iterator();

        while (it.hasNext()) {
            Inmueble inm = (Inmueble) it.next();
            buscarPagoFacNotaDebInm(inm);
            if (!listaPagosFacCxCInm.isEmpty()) {
            } else {
                continue;
            }
            suma = 0.00;
            Iterator<PagoFacCxC> it1 = listaPagosFacCxCInm.iterator();
            while (it1.hasNext()) {
                pfcxc = (PagoFacCxC) it1.next();
                if (pfcxc.getIdFacCxC().equals(pfcxcAnt.getIdFacCxC())) {
                    suma = suma + pfcxc.getMtoPagadoCxC();
                } else {
                    ajustarNotaDebito(suma, pfcxcAnt);
                    pfcxcAnt = pfcxc;
                    suma = pfcxc.getMtoPagadoCxC();
                }
                contadorPagos++;
            }
            ajustarNotaDebito(suma, pfcxc);
        }
        System.out.println(
                "Total pagos procesados   --------------->:" + contadorPagos);
        System.out.println(
                "Total Saldo menor que 0.01 --------------->:" + saldoMenorCeroPuntoUno);
        System.out.println(
                "Total Saldo menor que uno --------------->:" + saldoMenorUno);
        System.out.println(
                "Total Saldo mayor que uno --------------->:" + saldoMayorUno);
        System.out.println(
                "Total Notas debito con pagos demas  --------------->:" + contadorPagosDemas);
        System.out.println(
                "Total Notas debito actualizadas --------------->:" + contadorGnc);

    }

    private Collection buscarPagoFacNotaDebInm(Inmueble inm) {

        String nombQuery = "buscarFacPagoCxCNotaDebPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<>();
        listaPagosFacCxCInm.clear();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando pagos fac");
            return null;
        }

        Iterator<PagoFacCxC> it = listaPagosFacCxCInm.iterator();
        while (it.hasNext()) {
            pfcxcAnt = (PagoFacCxC) it.next();
            break;
        }
        return listaPagosFacCxCInm;
    }

    private void ajustarNotaDebito(Double suma, PagoFacCxC pfcxc) {

        NotaCredDebCxC ncd;

        try {
            ncd = cm.buscarNotaDebito(pfcxc.getIdFacCxC());
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando alicuota");
            return;
        }
        //-------------------------------------------------//
        // "suma" es la sumatoria de los pagos de  la fgnc
        //------------------------------------------------//
        diferencia = ncd.getMtoNotaCredDebCxC() - suma;

        if (diferencia < 0 && Math.abs(diferencia) >= 0.01) {
            System.out.println(
                    "--------------- > Sumatoria de pagos mayor que el monto :" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
            contadorPagosDemas++;
            return;
        }

        if (diferencia < 0.01) {
            ncd.setEdoNotaCredDebCxC("CANC");
            ncd.setSaldoNotaCredDebCxC(0.00);
            saldoMenorCeroPuntoUno++;
        } else {
            if (diferencia < 1) {
                saldoMenorUno++;
            } else {
                saldoMayorUno++;
            }
            ncd.setEdoNotaCredDebCxC("EPRO");
            ncd.setSaldoNotaCredDebCxC(diferencia);
        }

        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<>();
        try {
            dataAcces.almacenarOModificarEntidad(ncd);
            contadorGnc++;
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla almacenando nota debito");
            return;
        }

        System.out.println(
                "Nota debito actualizada:" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC());
    }

    private void ajustarCuotasEsp() {

        PagoFacCxC pfcxc = null;

        Iterator<Inmueble> it = listaInmuebles.iterator();

        while (it.hasNext()) {
            Inmueble inm = (Inmueble) it.next();
            buscarPagoFacCuotaEspInm(inm);
            if (!listaPagosFacCxCInm.isEmpty()) {
            } else {
                continue;
            }
            suma = 0.00;
            Iterator<PagoFacCxC> it1 = listaPagosFacCxCInm.iterator();
            while (it1.hasNext()) {
                pfcxc = (PagoFacCxC) it1.next();
                if (pfcxc.getIdFacCxC().equals(pfcxcAnt.getIdFacCxC())) {
                    suma = suma + pfcxc.getMtoPagadoCxC();
                } else {
                    ajustarFacCuotaEspecial(suma, pfcxcAnt);
                    pfcxcAnt = pfcxc;
                    suma = pfcxc.getMtoPagadoCxC();
                }
                contadorPagos++;
            }
            ajustarFacCuotaEspecial(suma, pfcxc);
        }
        System.out.println(
                "Total pagos procesados   --------------->:" + contadorPagos);
        System.out.println(
                "Total Saldo menor que 0.01 --------------->:" + saldoMenorCeroPuntoUno);
        System.out.println(
                "Total Saldo menor que uno --------------->:" + saldoMenorUno);
        System.out.println(
                "Total Saldo mayor que uno --------------->:" + saldoMayorUno);
        System.out.println(
                "Total Cuotas esp con pagos demas  --------------->:" + contadorPagosDemas);
        System.out.println(
                "Total Cuotas esp actualizadas --------------->:" + contadorGnc);

    }

    private Collection buscarPagoFacCuotaEspInm(Inmueble inm) {

        String nombQuery = "buscarFacPagoCxCCuotaEspPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<>();
        listaPagosFacCxCInm.clear();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando pagos fac");
            return null;
        }

        Iterator<PagoFacCxC> it = listaPagosFacCxCInm.iterator();
        while (it.hasNext()) {
            pfcxcAnt = (PagoFacCxC) it.next();
            break;
        }
        return listaPagosFacCxCInm;
    }

    private void ajustarFacCuotaEspecial(Double suma, PagoFacCxC pfcxc) {

        FacCuotaEsp fce;

        try {
            fce = cm.buscarFCE(pfcxc.getIdFacCxC());
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla buscando fce");
            return;
        }
        //-------------------------------------------------//
        // "suma" es la sumatoria de los pagos de  la fgnc
        //------------------------------------------------//
        diferencia = fce.getMtoFacCuotaEsp() - suma;

        if (diferencia < 0 && Math.abs(diferencia) >= 0.01) {
            System.out.println(
                    "--------------- > Sumatoria de pagos mayor que el monto :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
            contadorPagosDemas++;
            return;
        }

        if (diferencia < 0.01) {
            fce.setEdoFacCuotaEsp("CANC");
            fce.setSaldoFacCuotaEsp(0.00);
            saldoMenorCeroPuntoUno++;
        } else {
            if (diferencia < 1) {
                saldoMenorUno++;
            } else {
                saldoMayorUno++;
            }
            fce.setEdoFacCuotaEsp("EPRO");
            fce.setSaldoFacCuotaEsp(diferencia);
        }

        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<>();
        try {
            dataAcces.almacenarOModificarEntidad(fce);
            contadorGnc++;
        } catch (HibernateException he) {
            System.out.println("Ocurrio una falla almacenando fce");
            return;
        }

        System.out.println(
                "Factura cuota esp actualizada:" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp());
    }
}
