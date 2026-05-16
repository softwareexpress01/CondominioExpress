/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author jesus
 */
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import datosbasicos.modelos.Fondo;
import informes.modelos.PeriodoDT;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.GastoComunExt;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.NotaCredDebCxP;
import movimientos.modelos.TranFondo;
import movimientos.modelos.PagoFacCxP;

import movimientos.modelos.PagoFacFonRes;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class ConExpIndGestionPagosCxPCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private List<PeriodoDT> periodos = new ArrayList<PeriodoDT>();
    private Condominio condominio;
    private Calendar fecDesde;
    private Calendar fecHasta;
    private Collection<DeudaPendCxP> listaDeudaPend = new ArrayList<DeudaPendCxP>();
    private Collection<NotaCredDebCxP> listaNotasCr = new ArrayList<NotaCredDebCxP>();
    private Collection<GastoComunOrd> listaGCO = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunExt> listaGCE = new ArrayList<GastoComunExt>();
    private Collection<GastoNoComun> listaGNC = new ArrayList<GastoNoComun>();
    private Collection<TranFondo> tranFonRes = new ArrayList<TranFondo>();
    private Collection<TranFondo> tranFonPres = new ArrayList<TranFondo>();
    boolean periodoFinal = false;

    public ConExpIndGestionPagosCxPCtrl() {
    }

    public Condominio buscarCondominioFetchProveedores(Integer idCond) {

        String nombQuery = "buscarCondominioFetchProveedores";
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

    private List<PeriodoDT> generarPeriodos(Condominio condominio) {

        //-----------------------------------------------------------------
        //                  Se obtiene el primer periodo
        // Los periodos van desde el mas reciente (actual) hacia atras, para
        // un total de 7 periodos.
        //-----------------------------------------------------------------
        //                  Se obtiene el primer periodo
        //-----------------------------------------------------------------
        GregorianCalendar fechaHoy = new GregorianCalendar();

        int dia = new Integer(01);
        int mes1 = fechaHoy.get(Calendar.MONTH);
        int año1 = fechaHoy.get(Calendar.YEAR);
        GregorianCalendar periodo1 = new GregorianCalendar(año1, mes1, dia);

        PeriodoDT periodoDT1 = new PeriodoDT();

        String desc1 = obtenerDesPeriodo(periodo1);

        periodoDT1.setFecDesde(periodo1);
        periodoDT1.setFecHasta(fechaHoy);
        periodoDT1.setIndicadorPeriodo(0.00);
        periodoDT1.setDesPeriodo(desc1);
        periodoDT1.setNumPeriodo("7");

        periodos.add(periodoDT1);

        if (periodo1.equals(condominio.getFecCreacion())) {
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el segundo periodo
        //-----------------------------------------------------------
        Integer mes2;
        Integer año2;
        int añoA = periodo1.get(Calendar.YEAR);
        int mesA = periodo1.get(Calendar.MONTH) - 1;
        if (!(mesA > 0 | mesA == 0)) {
            mes2 = 11;
            año2 = añoA - 1;
        } else {
            mes2 = mesA;
            año2 = añoA;
        }

        GregorianCalendar periodo2 = new GregorianCalendar(año2, mes2, dia);

        PeriodoDT periodoDT2 = new PeriodoDT();

        String desc2 = obtenerDesPeriodo(periodo2);

        periodoDT2.setFecDesde(periodo2);
        periodoDT2.setFecHasta(periodo1);
        periodoDT2.setDesPeriodo(desc2);
        periodoDT2.setIndicadorPeriodo(0.00);
        periodoDT2.setNumPeriodo("6");

        periodos.add(periodoDT2);

        if (periodo2.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el tercer periodo
        //-----------------------------------------------------------
        Integer mes3;
        Integer año3;
        int mesB = periodo2.get(Calendar.MONTH) - 1;
        int añoB = periodo2.get(Calendar.YEAR);
        if (!(mesB > 0 | mesB == 0)) {
            mes3 = 11;
            año3 = añoB - 1;
        } else {
            mes3 = mesB;
            año3 = añoB;
        }
        GregorianCalendar periodo3 = new GregorianCalendar(año3, mes3, dia);
        PeriodoDT periodoDT3 = new PeriodoDT();

        String desc3 = obtenerDesPeriodo(periodo3);

        periodoDT3.setFecDesde(periodo3);
        periodoDT3.setFecHasta(periodo2);
        periodoDT3.setDesPeriodo(desc3);
        periodoDT3.setIndicadorPeriodo(0.00);
        periodoDT3.setNumPeriodo("5");

        periodos.add(periodoDT3);

        //-------------------------------------------

        if (periodo3.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el cuarto periodo
        //-----------------------------------------------------------
        Integer mes4;
        Integer año4;
        int mesC = periodo3.get(Calendar.MONTH) - 1;
        int añoC = periodo3.get(Calendar.YEAR);
        if (!(mesC > 0 | mesC == 0)) {
            mes4 = 11;
            año4 = añoC - 1;
        } else {
            mes4 = mesC;
            año4 = añoC;
        }
        GregorianCalendar periodo4 = new GregorianCalendar(año4, mes4, dia);
        PeriodoDT periodoDT4 = new PeriodoDT();

        String desc4 = obtenerDesPeriodo(periodo4);

        periodoDT4.setFecDesde(periodo4);
        periodoDT4.setFecHasta(periodo3);
        periodoDT4.setDesPeriodo(desc4);
        periodoDT4.setIndicadorPeriodo(0.00);
        periodoDT4.setNumPeriodo("4");

        periodos.add(periodoDT4);

        if (periodo4.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el quinto periodo
        //-----------------------------------------------------------
        Integer mes5;
        Integer año5;
        int mesD = periodo4.get(Calendar.MONTH) - 1;
        int añoD = periodo4.get(Calendar.YEAR);
        if (!(mesD > 0 | mesD == 0)) {
            mes5 = 11;
            año5 = añoD - 1;
        } else {
            mes5 = mesD;
            año5 = añoD;
        }
        GregorianCalendar periodo5 = new GregorianCalendar(año5, mes5, dia);
        PeriodoDT periodoDT5 = new PeriodoDT();

        String desc5 = obtenerDesPeriodo(periodo5);

        periodoDT5.setFecDesde(periodo5);
        periodoDT5.setFecHasta(periodo4);
        periodoDT5.setDesPeriodo(desc5);
        periodoDT5.setIndicadorPeriodo(0.00);
        periodoDT5.setNumPeriodo("3");

        periodos.add(periodoDT5);

        if (periodo5.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el sexto periodo
        //-----------------------------------------------------------
        Integer mes6;
        Integer año6;
        int mesE = periodo5.get(Calendar.MONTH) - 1;
        int añoE = periodo5.get(Calendar.YEAR);
        if (!(mesE > 0 | mesE == 0)) {
            mes6 = 11;
            año6 = añoE - 1;
        } else {
            mes6 = mesE;
            año6 = añoE;
        }
        GregorianCalendar periodo6 = new GregorianCalendar(año6, mes6, dia);
        PeriodoDT periodoDT6 = new PeriodoDT();
        String desc6 = obtenerDesPeriodo(periodo6);

        periodoDT6.setFecDesde(periodo6);
        periodoDT6.setFecHasta(periodo5);
        periodoDT6.setDesPeriodo(desc6);
        periodoDT6.setIndicadorPeriodo(0.00);
        periodoDT6.setNumPeriodo("2");

        periodos.add(periodoDT6);

        if (periodo6.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //-----------------------------------------------------------
        //                  Se obtiene el septimo periodo
        //-----------------------------------------------------------
        Integer mes7;
        Integer año7;
        int mesF = periodo6.get(Calendar.MONTH) - 1;
        int añoF = periodo6.get(Calendar.YEAR);
        if (!(mesF > 0 | mesF == 0)) {
            mes7 = 11;
            año7 = añoF - 1;
        } else {
            mes7 = mesF;
            año7 = añoF;
        }
        GregorianCalendar periodo7 = new GregorianCalendar(año7, mes7, dia);
        PeriodoDT periodoDT7 = new PeriodoDT();
        String desc7 = obtenerDesPeriodo(periodo7);

        periodoDT7.setFecDesde(periodo7);
        periodoDT7.setFecHasta(periodo6);
        periodoDT7.setDesPeriodo(desc7);
        periodoDT7.setIndicadorPeriodo(0.00);
        periodoDT7.setNumPeriodo("1");

        periodos.add(periodoDT7);

        if (periodo7.equals(condominio.getFecCreacion())) {
            Collections.sort(periodos, new ComparadorPeriodos());
            return periodos;
        }
        //------------------------------------------
        // Chequea generacion de los periodos 
        //------------------------------------------
        String mesp;
        String mesp1;
        for (int i = 0; i < periodos.size(); i++) {
            PeriodoDT pdt = periodos.get(i);
            int mesf = pdt.getFecDesde().get(Calendar.MONTH) + 1;
            int añof = pdt.getFecDesde().get(Calendar.YEAR);
            int diaf = pdt.getFecDesde().get(Calendar.DAY_OF_MONTH);
            String desD = pdt.getDesPeriodo();

            int mesH = pdt.getFecHasta().get(Calendar.MONTH) + 1;
            int añoH = pdt.getFecHasta().get(Calendar.YEAR);
            int diaH = pdt.getFecDesde().get(Calendar.DAY_OF_MONTH);

            if (mesf < 10) {
                mesp = "0" + mesf;
            } else {
                mesp = String.valueOf(mesf);
            }
            if (mesH < 10) {
                mesp1 = "0" + mesH;
            } else {
                mesp1 = String.valueOf(mesH);
            }

        }

        Collections.sort(periodos, new ComparadorPeriodos());
        return periodos;

    }

    private String obtenerDesPeriodo(GregorianCalendar periodo) {

        String descripcion = "";

        int mes = periodo.get(Calendar.MONTH);
        int año = periodo.get(Calendar.YEAR);

        String mesp = String.valueOf(mes);

        if (mesp.equals("0")) {
            descripcion = "Ene - " + año;
            return descripcion;
        }
        if (mesp.equals("1")) {
            descripcion = "Feb - " + año;
            return descripcion;
        }
        if (mesp.equals("2")) {
            descripcion = "Mar - " + año;
            return descripcion;
        }
        if (mesp.equals("3")) {
            descripcion = "Abr - " + año;
            return descripcion;
        }
        if (mesp.equals("4")) {
            descripcion = "May - " + año;
            return descripcion;
        }
        if (mesp.equals("5")) {
            descripcion = "Jun - " + año;
            return descripcion;
        }
        if (mesp.equals("6")) {
            descripcion = "Jul - " + año;
            return descripcion;
        }
        if (mesp.equals("7")) {
            descripcion = "Ago - " + año;
            return descripcion;
        }
        if (mesp.equals("8")) {
            descripcion = "Sep - " + año;
            return descripcion;
        }
        if (mesp.equals("9")) {
            descripcion = "Oct - " + año;
            return descripcion;
        }
        if (mesp.equals("10")) {
            descripcion = "Nov - " + año;
            return descripcion;
        }
        if (mesp.equals("11")) {
            descripcion = "Dic - " + año;
            return descripcion;
        }
        return descripcion;
    }

    public void generarIndGestionPorPeriodo(Condominio condominio) throws ParseException {

        Collection<PagoFacCxP> pagoFacCxP = new ArrayList<PagoFacCxP>();
        Collection<PagoFacFonRes> pagoFacFondos = new ArrayList<PagoFacFonRes>();
        Double cero = new Double(0);
        Double indPer = new Double(0);
        Double saldoPer;
        Double pagosPer;
        Integer idCond = condominio.getId();
        int numIteraciones = 0;
        int iteracionActual = 0;

        for (int i = 0; i < periodos.size() - 1; i++) {
            numIteraciones = numIteraciones + 1;
        }

        for (int i = 0; i < periodos.size() - 1; i++) {
            PeriodoDT pdt = periodos.get(i);
            saldoPer = 0.0;
            pagosPer = 0.0;

            iteracionActual += 1;
            if (iteracionActual == numIteraciones) {
                periodoFinal = true;
            }


            //  lógica para buscar las transacciones de fondo del periodo y si hay pagos sobre las mismas - solo reserva y prestaciones -

            saldoPer += obtenerSaldosPorPeriodoFondos(idCond, pdt.getFecDesde(), pdt.getFecHasta());
            pagoFacFondos = obtenerPagoFacFondosPorPeriodo(idCond, pdt.getFecDesde(), periodos.get(i + 1).getFecHasta());
            pagosPer += obtenerPagosFondosPorPeriodo(pagoFacFondos);

            for (Iterator<Proveedor> it = condominio.getProveedores().iterator();
                    it.hasNext();) {
                Proveedor prov = (Proveedor) it.next();
                Integer idProv = prov.getId();


                saldoPer += obtenerSaldosPorPeriodo(idProv, pdt.getFecDesde(), pdt.getFecHasta());

                pagoFacCxP.clear();

                pagoFacCxP = obtenerPagoFacCxPPorPeriodo(idProv, pdt.getFecDesde(), periodos.get(i + 1).getFecHasta());
                pagosPer += obtenerPagosPorPeriodo(pagoFacCxP);


            }

            if (!saldoPer.equals(cero)) {
                indPer = (pagosPer / saldoPer) * 100;
            } else {
                indPer = 0.0;
            }
            pdt.setIndicadorPeriodo(indPer);

        }
    }

    public Double obtenerSaldosPorPeriodoFondos(Integer idCond, Calendar desde, Calendar hasta) {


        Integer idFonRes = 0;
        Integer idFonPres;
        Double saldoPeriodo = new Double(0);
        Double saldoFonRes = new Double(0);
        Double saldoFonPres = new Double(0);

        idFonRes = buscarFonRes(idCond);
        if (idFonRes > 0) {
            tranFonRes = buscarTranFonRes(idFonRes, desde, hasta);
            if (!tranFonRes.isEmpty()) {
                for (Iterator<TranFondo> it = tranFonRes.iterator();
                        it.hasNext();) {
                    TranFondo tranFondoRes = (TranFondo) it.next();
                    saldoFonRes += tranFondoRes.getSaldoTranFondo();
                }
            }
        }

        idFonPres = buscarFonPres(idCond);
        if (idFonPres > 0) {
            tranFonPres = buscarTranFonPres(idFonPres, desde, hasta);
            if (!tranFonPres.isEmpty()) {
                for (Iterator<TranFondo> it = tranFonPres.iterator();
                        it.hasNext();) {
                    TranFondo tranFondoPres = (TranFondo) it.next();
                    saldoFonPres += tranFondoPres.getSaldoTranFondo();
                }
            }
        }
        saldoPeriodo = saldoFonRes + saldoFonPres;
        return saldoPeriodo;
    }

    public Integer buscarFonRes(Integer idCond) {

        Fondo fonRes = new Fondo();
        Integer idFonRes;
        String nombQuery = "buscarFondoReservaPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fonRes = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (!(fonRes == null)) {
            idFonRes = fonRes.getId();
        } else {
            idFonRes = 0;
        }

        return idFonRes;
    }

    public Integer buscarFonPres(Integer idCond) {

        Fondo fonPres = new Fondo();
        Integer idFonPres;
        String nombQuery = "buscarFondoPrestacionesPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fonPres = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (!(fonPres == null)) {
            idFonPres = fonPres.getId();
        } else {
            idFonPres = 0;
        }

        return idFonPres;
    }

    public Collection buscarTranFonRes(Integer idFonRes, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarTranFondoPorIdFondoRango";
        } else {
            nombQuery = "buscarTranFondoPorIdFondoRangoFecMov";
        }

        // Obtiene las Transacciones de Fondo de reserva para el periodo dado por las fechas indicadas).

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idFonRes, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection buscarTranFonPres(Integer idFonPres, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarTranFondoPorIdFondoRango";
        } else {
            nombQuery = "buscarTranFondoPorIdFondoRangoFecMov";
        }
        // Obtiene las Transacciones de Fondo de presraciones para el periodo dado por las fechas indicadas).

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idFonPres, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Double obtenerSaldosPorPeriodo(Integer idProv, Calendar desde, Calendar hasta) throws ParseException {

        listaDeudaPend.clear();
        listaNotasCr.clear();
        listaGCO.clear();
        listaGCE.clear();
        listaGNC.clear();

        Double saldoPeriodo = new Double(0);

        listaDeudaPend = obtenerDeudaPendiente(idProv, desde, hasta);

        listaNotasCr = obtenerNotasCr(idProv, desde, hasta);

        listaGCO = obtenerGCO(idProv, desde, hasta);

        listaGCE = obtenerGCE(idProv, desde, hasta);

        listaGNC = obtenerGNC(idProv, desde, hasta);


        //-------------------------------------------------------
        //Acumula el saldo debido a Deuda Pendiente
        //-------------------------------------------------------
        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxP dp = (DeudaPendCxP) it.next();
            if (!dp.getEstatusDeudaPendCxP().equals("INAC")) {
                saldoPeriodo = saldoPeriodo + dp.getMtoDeudaPendCxP();
            }
        }
        //-------------------------------------------------------
        //Acumula el saldo debido NOtasd de crédito
        //-------------------------------------------------------
        for (Iterator<NotaCredDebCxP> it = listaNotasCr.iterator();
                it.hasNext();) {
            NotaCredDebCxP nc = (NotaCredDebCxP) it.next();

            if (!nc.getTipoNotaCredDebCxP().equals("Debito")) {
                saldoPeriodo = saldoPeriodo + nc.getMtoNotaCredDebCxP();
            }
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a GCO
        //-------------------------------------------------------
        for (Iterator<GastoComunOrd> it = listaGCO.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            saldoPeriodo = saldoPeriodo + gco.getMtoGastoComunOrd();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a GCE
        //-------------------------------------------------------
        for (Iterator<GastoComunExt> it = listaGCE.iterator();
                it.hasNext();) {
            GastoComunExt gce = (GastoComunExt) it.next();
            saldoPeriodo = saldoPeriodo + gce.getMtoGastoComunExt();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a GNC
        //-------------------------------------------------------
        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            saldoPeriodo = saldoPeriodo + gnc.getMtoGastoNoComun();

        }

        return saldoPeriodo;

    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Collection obtenerDeudaPendiente(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }

        // Obtiene las facturas por Deuda Perndiente para el proveedor dado (id).
        String nombQuery = "buscarFacDeudaPendPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<DeudaPendCxP> it = lista.iterator();
                it.hasNext();) {
            DeudaPendCxP dpcxp = (DeudaPendCxP) it.next();
            fechaPago = convertirFecha(dpcxp.getFecDeudaPendCxP());
            if (periodoFinal) {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                    listaRango.add(dpcxp);
                }
            } else {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal))) {
                    listaRango.add(dpcxp);
                }
            }

        }
        return listaRango;
    }

    public Collection obtenerNotasCr(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }

        // Obtiene las notas de crédito para el proveedor dado (id).
        String nombQuery = "buscarNotaCredDebPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<NotaCredDebCxP> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxP ncd = (NotaCredDebCxP) it.next();
            fechaPago = convertirFecha(ncd.getFecNotaCredDebCxP());
            if (periodoFinal) {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                    listaRango.add(ncd);
                }
            } else {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal))) {
                    listaRango.add(ncd);
                }
            }
        }
        return listaRango;
    }

    public Collection obtenerGCO(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }

        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarFacGastoComunOrdPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<GastoComunOrd> it = lista.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            fechaPago = convertirFecha(gco.getFecGastoComunOrd());
            if (periodoFinal) {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                    listaRango.add(gco);
                }
            } else {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal))) {
                    listaRango.add(gco);
                }
            }

        }
        return listaRango;
    }

    public Collection obtenerGCE(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }

        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarFacGastoComunExtPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<GastoComunExt> it = lista.iterator();
                it.hasNext();) {
            GastoComunExt gce = (GastoComunExt) it.next();
            fechaPago = convertirFecha(gce.getFecGastoComunExt());
            if (periodoFinal) {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                    listaRango.add(gce);
                }
            } else {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal))) {
                    listaRango.add(gce);
                }
            }
        }
        return listaRango;
    }

    public Collection obtenerGNC(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRango = new ArrayList();

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }

        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarFacGastoNoComunPorIdProvRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<GastoNoComun> it = lista.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            fechaPago = convertirFecha(gnc.getFecGastoNoComun());
            if (periodoFinal) {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal) | fechaPago.equals(fecHastaCal))) {
                    listaRango.add(gnc);
                }
            } else {
                if ((fechaPago.after(fecDesdeCal) | fechaPago.equals(fecDesdeCal)) & (fechaPago.before(fecHastaCal))) {
                    listaRango.add(gnc);
                }
            }

        }
        return listaRango;
    }

    public Collection obtenerPagoFacFondosPorPeriodo(Integer idCond, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection<PagoFacCxP> lista = new ArrayList<PagoFacCxP>();
        String nombQuery;
        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarPagoFacFonResPorIdCondRangoCompleto";
        } else {
            nombQuery = "buscarPagoFacFonResPorIdCondRango";
        }

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idCond, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerPagoFacCxPPorPeriodo(Integer idProv, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection<PagoFacCxP> lista = new ArrayList<PagoFacCxP>();
        Collection<PagoFacCxP> listaRango = new ArrayList<PagoFacCxP>();

        String nombQuery = "buscarPagoFacCxPPorIdProvRango";

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idProv, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPagoCxP;
        for (Iterator<PagoFacCxP> it = lista.iterator();
                it.hasNext();) {
            PagoFacCxP pagoFacCxP = (PagoFacCxP) it.next();
            fechaPagoCxP = convertirFecha(pagoFacCxP.getPagoCxP().getFecDocPagoCxP());
            if (periodoFinal) {
                if ((fechaPagoCxP.after(fecDesdeCal) | fechaPagoCxP.equals(fecDesdeCal)) & (fechaPagoCxP.before(fecHastaCal) | fechaPagoCxP.equals(fecHastaCal))) {
                    listaRango.add(pagoFacCxP);
                }
            } else {
                if ((fechaPagoCxP.after(fecDesdeCal) | fechaPagoCxP.equals(fecDesdeCal)) & (fechaPagoCxP.before(fecHastaCal))) {
                    listaRango.add(pagoFacCxP);
                }
            }

        }
        return listaRango;
    }

    public Double obtenerPagosPorPeriodo(Collection pagoFacCxP) {

        Double pagoPeriodo = new Double(0);

        //--------------------------------------------------------------
        //Acumula el monto pagado debido a Deudas Pendientes
        //--------------------------------------------------------------
        for (Iterator<DeudaPendCxP> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxP dp = (DeudaPendCxP) it.next();
            for (Iterator<PagoFacCxP> it1 = pagoFacCxP.iterator();
                    it1.hasNext();) {
                PagoFacCxP pagoFac = (PagoFacCxP) it1.next();
                if (pagoFac.getDiscFacCxP().equals("DPE") & pagoFac.getIdFacCxP().equals(dp.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxP();
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a Notas de cred.
        //-------------------------------------------------------

        for (Iterator<NotaCredDebCxP> it = listaNotasCr.iterator();
                it.hasNext();) {
            NotaCredDebCxP nc = (NotaCredDebCxP) it.next();
            for (Iterator<PagoFacCxP> it1 = pagoFacCxP.iterator();
                    it1.hasNext();) {
                PagoFacCxP pagoFac = (PagoFacCxP) it1.next();
                if (pagoFac.getDiscFacCxP().equals("NCR") & pagoFac.getIdFacCxP().equals(nc.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxP();
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a gasto CO
        //-------------------------------------------------------

        for (Iterator<GastoComunOrd> it = listaGCO.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            for (Iterator<PagoFacCxP> it1 = pagoFacCxP.iterator();
                    it1.hasNext();) {
                PagoFacCxP pagoFac = (PagoFacCxP) it1.next();
                if (pagoFac.getDiscFacCxP().equals("GCO") & pagoFac.getIdFacCxP().equals(gco.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxP();
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a GCE
        //-------------------------------------------------------

        for (Iterator<GastoComunExt> it = listaGCE.iterator();
                it.hasNext();) {
            GastoComunExt gce = (GastoComunExt) it.next();
            for (Iterator<PagoFacCxP> it1 = pagoFacCxP.iterator();
                    it1.hasNext();) {
                PagoFacCxP pagoFac = (PagoFacCxP) it1.next();
                if (pagoFac.getDiscFacCxP().equals("GCE") & pagoFac.getIdFacCxP().equals(gce.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxP();
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a GNC
        //-------------------------------------------------------

        for (Iterator<GastoNoComun> it = listaGNC.iterator();
                it.hasNext();) {
            GastoNoComun gnc = (GastoNoComun) it.next();
            for (Iterator<PagoFacCxP> it1 = pagoFacCxP.iterator();
                    it1.hasNext();) {
                PagoFacCxP pagoFac = (PagoFacCxP) it1.next();
                if (pagoFac.getDiscFacCxP().equals("GNC") & pagoFac.getIdFacCxP().equals(gnc.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxP();
                }

            }
        }
        return pagoPeriodo;
    }

    public Double obtenerPagosFondosPorPeriodo(Collection pagoFacFonRes) {

        Double pagoPeriodo = new Double(0);

        //--------------------------------------------------------------
        //Acumula el monto pagado debido a Pagos del Fondo de reserva
        //--------------------------------------------------------------
        for (Iterator<TranFondo> it = tranFonRes.iterator();
                it.hasNext();) {
            TranFondo tF = (TranFondo) it.next();
            for (Iterator<PagoFacFonRes> it1 = pagoFacFonRes.iterator();
                    it1.hasNext();) {
                PagoFacFonRes pagoFac = (PagoFacFonRes) it1.next();
                if (pagoFac.getIdFacFonRes().equals(tF.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoFonRes();
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a Notas de cred.
        //-------------------------------------------------------

        for (Iterator<TranFondo> it2 = tranFonPres.iterator();
                it2.hasNext();) {
            TranFondo tF = (TranFondo) it2.next();
            for (Iterator<PagoFacFonRes> it3 = pagoFacFonRes.iterator();
                    it3.hasNext();) {
                PagoFacFonRes pagoFac = (PagoFacFonRes) it3.next();
                if (pagoFac.getIdFacFonRes().equals(tF.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoFonRes();
                }

            }
        }
        return pagoPeriodo;
    }

    private void generarDataSource() throws ClassNotFoundException, SQLException, IOException, JRException, ParseException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        //--------------------------------------------------------
        // Se busca el condominio con sus propiedades
        //--------------------------------------------------------
        condominio = buscarCondominioFetchProveedores(idCond);

        if (condominio.getProveedores().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Hasta ahora no se ha registrado ningun proveedor para el condomino actual...reporte cancelado"));
            return;
        }

        //------------------------------------------------------------
        // Se generan los seis periodos a considerar en el reporte
        //------------------------------------------------------------
        generarPeriodos(condominio);
        generarIndGestionPorPeriodo(condominio);
    }

    private String convertirFechaAString(Calendar fecha) {

        String diaString;
        String mesString;

        int mes = fecha.get(Calendar.MONTH) + 1;
        int año = fecha.get(Calendar.YEAR);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

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

        String stringFecha = diaString + "-" + mesString + "-" + año;
        return stringFecha;
    }

    public void generarReporteGestionPagosCxP() throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        generarDataSource();

        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/IndGestionPagosCxP.jasper");

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
        // Se inicializan el dataSource del reporte 
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceIndicadores;
        dataSourceIndicadores = new JRBeanCollectionDataSource(periodos);
        //-----------------------------------------------------------------
        //  Se determina el rango de fechas para el reporte
        //-----------------------------------------------------------------
        fecHasta = new GregorianCalendar();

        for (int i = 0; i < 1; i++) { // Para que tome el primer elemento de la colección ya que los periodos estan en orden inverso
            PeriodoDT pdt = periodos.get(i);
            fecDesde = pdt.getFecDesde();
        }
        //------------------------------------------------------------
        // Se inicializan los parametros para el encabezado del 
        // reporte de acuerdo a los periodos generados.
        //------------------------------------------------------------

        String desde = convertirFechaAString(fecDesde);
        String hasta = convertirFechaAString(fecHasta);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("logo", logo);
        parametros.put("fecDesde", desde);
        parametros.put("fecHasta", hasta);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceIndicadores);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();

    }
}
