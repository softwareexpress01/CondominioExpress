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
public class RevisarCuentasPorCobrarCtrl extends DaoCrud<Inmueble, Propiedad, Integer> implements Serializable {

    private final int idCond = 305;
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

    public RevisarCuentasPorCobrarCtrl() {

    }

    public void revisarCuentasPorCobrar() {

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

        if (diferencia < 0) {
            if (Math.abs(diferencia) >= 0.01) {
                System.out.println(
                        "--------------- > Sumatoria de pagos mayor que el monto :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
                contadorPagosDemas++;
                return;
            } else {
                if (!fgnc.getEdoFacGastoNoComun().equals("CANC")) {
                    System.out.println(
                            "--------------- > Queda saldo < 0.01 (negativo), EDO deberia ser CANC :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
                    saldoMenorCeroPuntoUno++;

                }
            }
            return;
        }

        if (diferencia < 0.01) {
            if (!fgnc.getEdoFacGastoNoComun().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda saldo < 0.01, EDO deberia ser CANC :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
                saldoMenorCeroPuntoUno++;
            }
            return;
        }
        if (diferencia < 1) {
            if (fgnc.getEdoFacGastoNoComun().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda 0.01 <= saldo < 1, EDO deberia ser EPRO :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
                saldoMenorUno++;
            }
        } else {
            if (!fgnc.getEdoFacGastoNoComun().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda saldo >  1, EDO deberia ser EPRO :" + " " + fgnc.getId() + " " + fgnc.getMtoFacGastoNoComun() + " " + fgnc.getSaldoFacGastoNoComun() + " " + fgnc.getEdoFacGastoNoComun() + " " + suma);
                saldoMayorUno++;
            }
        }
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

        if (diferencia < 0) {
            if (Math.abs(diferencia) >= 0.01) {
                System.out.println(
                        "--------------- > Sumatoria de pagos mayor que el monto :" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
                contadorPagosDemas++;
                return;
            } else {
                if (!alic.getEdoAlicuota().equals("CANC")) {
                    System.out.println(
                            "--------------- > Queda saldo < 0.01 (negativo), EDO deberia ser CANC :" + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
                    saldoMenorCeroPuntoUno++;
                    return;
                }
            }
        }

        if (diferencia < 0.01) {
            if (!alic.getEdoAlicuota().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda saldo < 0.01 (positivo), EDO deberia ser CANC :" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
                saldoMenorCeroPuntoUno++;
            }
            return;
        }
        if (diferencia < 1) {
            if (alic.getEdoAlicuota().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda 0.01 <= saldo < 1, EDO deberia ser EPRO :" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
                saldoMenorUno++;
            }
        } else {
            if (!alic.getEdoAlicuota().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda saldo >  1, EDO deberia ser EPRO :" + " " + alic.getId() + " " + alic.getMtoAlicuota() + " " + alic.getSaldoAlicuota() + " " + alic.getEdoAlicuota() + " " + suma);
                saldoMayorUno++;
            }

        }
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

        if (diferencia < 0) {
            if (Math.abs(diferencia) >= 0.01) {
                System.out.println(
                        "--------------- > Sumatoria de pagos mayor que el monto :" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
                contadorPagosDemas++;
                return;
            } else {
                if (!dpen.getEdoDeudaPendCxC().equals("CANC")) {
                    System.out.println(
                            "--------------- > Queda saldo < 0.01 (negativo), EDO deberia ser CANC :" + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
                    saldoMenorCeroPuntoUno++;
                    return;
                }
            }
        }

        if (diferencia < 0.01) {
            if (!dpen.getEdoDeudaPendCxC().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda saldo < 0.01, EDO deberia ser CANC :" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
                saldoMenorCeroPuntoUno++;
            }
            return;
        }
        if (diferencia < 1) {
            if (dpen.getEdoDeudaPendCxC().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda 0.01 <= saldo < 1, EDO deberia ser EPRO :" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
                saldoMenorUno++;
            }
        } else {
            if (!dpen.getEdoDeudaPendCxC().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda saldo >  1, EDO deberia ser EPRO :" + " " + dpen.getId() + " " + dpen.getMtoDeudaPendCxC() + " " + dpen.getSaldoDeudaPendCxC() + " " + dpen.getEdoDeudaPendCxC() + " " + suma);
                saldoMayorUno++;
            }

        }
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

        if (diferencia < 0) {
            if (Math.abs(diferencia) >= 0.01) {
                System.out.println(
                        "--------------- > Sumatoria de pagos mayor que el monto :" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
                contadorPagosDemas++;
                return;
            } else {
                if (!ncd.getEdoNotaCredDebCxC().equals("CANC")) {
                    System.out.println(
                            "--------------- > Queda saldo < 0.01 (negativo), EDO deberia ser CANC :" + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
                    saldoMenorCeroPuntoUno++;
                    return;
                }
            }
        }
        if (diferencia < 0.01) {
            if (!ncd.getEdoNotaCredDebCxC().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda saldo < 0.01, EDO deberia ser CANC :" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
                saldoMenorCeroPuntoUno++;
            }
            return;
        }
        if (diferencia < 1) {
            if (ncd.getEdoNotaCredDebCxC().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda 0.01 <= saldo < 1, EDO deberia ser EPRO :" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
                saldoMenorUno++;
            }
        } else {
            if (!ncd.getEdoNotaCredDebCxC().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda saldo >  1, EDO deberia ser EPRO :" + " " + ncd.getId() + " " + ncd.getMtoNotaCredDebCxC() + " " + ncd.getSaldoNotaCredDebCxC() + " " + ncd.getEdoNotaCredDebCxC() + " " + suma);
                saldoMayorUno++;
            }

        }
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

        if (diferencia < 0) {
            if (Math.abs(diferencia) >= 0.01) {
                System.out.println(
                        "--------------- > Sumatoria de pagos mayor que el monto :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
                contadorPagosDemas++;
                return;
            } else {
                if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                    System.out.println(
                            "--------------- > Queda saldo < 0.01 (negativo), EDO deberia ser CANC :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
                    saldoMenorCeroPuntoUno++;
                    return;
                }
            }
        }

        if (diferencia < 0.01) {
            if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                System.out.println(
                        "--------------- > Queda saldo < 0.01, EDO deberia ser CANC :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
                saldoMenorCeroPuntoUno++;
            }
            return;
        }
        if (diferencia < 1) {
            if (!fce.getEdoFacCuotaEsp().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda 0.01 <= saldo < 1, EDO deberia ser EPRO :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
                saldoMenorUno++;
            }

        } else {
            if (!fce.getEdoFacCuotaEsp().equals("EPRO")) {
                System.out.println(
                        "--------------- > Queda saldo >  1, EDO deberia ser EPRO :" + " " + fce.getId() + " " + fce.getMtoFacCuotaEsp() + " " + fce.getSaldoFacCuotaEsp() + " " + fce.getEdoFacCuotaEsp() + " " + suma);
                saldoMayorUno++;
            }

        }
    }
}
