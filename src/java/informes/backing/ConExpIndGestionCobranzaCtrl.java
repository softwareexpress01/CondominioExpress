/**
 * Backing bean para informes xhtml
 */
package informes.backing;

/**
 *
 * @author jesus
 */
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Inmueble;

import datosbasicos.modelos.Propiedad;

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
import util.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoFacCxC;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;

public class ConExpIndGestionCobranzaCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private List<PeriodoDT> periodos = new ArrayList<PeriodoDT>();
    private Condominio condominio;
    private Collection<Inmueble> inmuebles = new ArrayList<Inmueble>();
    private Calendar fecDesde;
    private Calendar fecHasta;
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection<DeudaPendCxC> listaDeudaPend = new ArrayList<DeudaPendCxC>();
    private Collection<Alicuota> listaAlicuota = new ArrayList<Alicuota>();
    private Collection<FacCuotaEsp> listaFacCuotaEsp = new ArrayList<FacCuotaEsp>();
    private Collection<NotaCredDebCxC> listaNotaDebito = new ArrayList<NotaCredDebCxC>();
    //Para debbuging
    private Collection<FacGastoNoComun> listaFacGNCTemp = new ArrayList<FacGastoNoComun>();
    private Collection<DeudaPendCxC> listaDeudaPendTemp = new ArrayList<DeudaPendCxC>();
    private Collection<Alicuota> listaAlicuotaTemp = new ArrayList<Alicuota>();
    private Collection<FacCuotaEsp> listaFacCuotaEspTemp = new ArrayList<FacCuotaEsp>();
    private Collection<NotaCredDebCxC> listaNotaDebitoTemp = new ArrayList<NotaCredDebCxC>();
    //Para debugging
    Collection<PagoFacCxC> pagoFacCxCInmPerTemp = new ArrayList<PagoFacCxC>();
    Collection<PagoFacCxC> pagoFacCxCInmPer = new ArrayList<PagoFacCxC>();
    private Double saldoPeriodo = 0.0;
    private Double pagoPeriodo;
    boolean periodoFinal = false;

    public ConExpIndGestionCobranzaCtrl() {
    }

    public Condominio buscarCondominioFetchPropiedades(Integer idCond) {

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

    private Collection<Inmueble> buscarInmueblesCondominio(Condominio condominio) {

        Collection<Inmueble> inmueblesCond = new ArrayList<Inmueble>();
        Collection<Inmueble> inmueblesProp = new ArrayList<Inmueble>();

        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();

            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                inmueblesProp = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            inmueblesCond.addAll(inmueblesProp);
            inmueblesProp.clear();
        }
        return inmueblesCond;
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
        int dia1 = fechaHoy.get(Calendar.DAY_OF_MONTH);

        GregorianCalendar periodo1 = new GregorianCalendar(año1, mes1, dia);
        GregorianCalendar periodo0 = new GregorianCalendar(año1, mes1, dia1);

        PeriodoDT periodoDT1 = new PeriodoDT();

        String desc1 = obtenerDesPeriodo(periodo1);

        periodoDT1.setFecDesde(periodo1);
        periodoDT1.setFecHasta(periodo0);
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
        chequearPeriodos();
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


        Collections.sort(periodos, new ComparadorPeriodos());
        return periodos;

    }

    private void chequearPeriodos() {
        //------------------------------------------
        // Chequea generacion de los periodos 
        //------------------------------------------
        System.out.println("---------------------------El tamaño de periodos es igual a : " + periodos.size());
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
            int diaH = pdt.getFecHasta().get(Calendar.DAY_OF_MONTH);

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

            String stringD = mesp + "-" + añof + "-" + diaf;
            String stringH = mesp1 + "-" + añoH + "-" + diaH;
            System.out.println("----------------------------Desde:" + stringD + "Hasta:" + stringH + "descripcion:" + desD);
        }
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

    private void generarIndGestionPorPeriodo() throws ParseException {

        Double cero = new Double(0);
        Double indPer = new Double(0);
        Double saldoInmPer;
        Double pagosInmPer;
        int numIteraciones = 0;
        int iteracionActual = 0;

        for (int i = 0; i < periodos.size() - 1; i++) {
            numIteraciones = numIteraciones + 1;
        }

        for (int i = 0; i < periodos.size() - 1; i++) {
            PeriodoDT pdt = periodos.get(i);
            saldoInmPer = 0.0;
            pagosInmPer = 0.0;
            // En los métodos obtenerPagoFacCxCPorPeriodo y obtenerPagosPorPeriodo, se cambia el query dependiendo de la variable periodoFinal
            iteracionActual += 1;
            if (iteracionActual == numIteraciones) {
                periodoFinal = true;
            }

            for (Iterator<Inmueble> it = inmuebles.iterator();
                    it.hasNext();) {

                Inmueble inm = (Inmueble) it.next();
                Integer idInm = inm.getId();

                saldoInmPer += obtenerSaldosPorPeriodo(idInm, pdt.getFecDesde(), pdt.getFecHasta());

                pagoFacCxCInmPer.clear();

                pagoFacCxCInmPer = obtenerPagoFacCxCPorPeriodo(idInm, pdt.getFecDesde(), periodos.get(i + 1).getFecHasta());
                pagosInmPer += obtenerPagosPorPeriodo(pagoFacCxCInmPer);
            }

            if (!saldoInmPer.equals(cero)) {
                indPer = (pagosInmPer / saldoInmPer) * 100;
            } else {
                indPer = 0.0;
            }

            pdt.setIndicadorPeriodo(indPer);
            mostrarDetallesPeriodo(i);
        }
        System.out.println("_____________________________________________________________ ");
        System.out.println("total  periodos   ======================== " + periodos.size());
        System.out.println("nro de iteraciones  ======================== " + numIteraciones);
        System.out.println("iteracion  ======================== " + iteracionActual);
        System.out.println("iteracion  ======================== " + periodoFinal);
        System.out.println("_____________________________________________________________ ");
    }

    public void mostrarDetallesPeriodo(int i) {

        System.out.println("Periodo :" + "" + i);
        System.out.println("-----------------------------------Facturas de gasto no comun ---------------------------------");
        for (Iterator<FacGastoNoComun> it = listaFacGNCTemp.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            System.out.println("Id: " + fgnc.getId() + "Monto: " + "" + fgnc.getMtoFacGastoNoComun() + "fecha:" + "" + fgnc.getFecFacGNC().getTime());
        }

        System.out.println("-----------------------------------Facturas de Deuda pendiente ---------------------------------");
        for (Iterator<DeudaPendCxC> it = listaDeudaPendTemp.iterator();
                it.hasNext();) {
            DeudaPendCxC dp = (DeudaPendCxC) it.next();
            System.out.println("Id: " + dp.getId() + "Monto: " + "" + dp.getMtoDeudaPendCxC() + "fecha:" + "" + dp.getFecMovDP().getTime());
        }

        System.out.println("-----------------------------------Facturas de Alicuotas ---------------------------------");
        for (Iterator<Alicuota> it = listaAlicuotaTemp.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();
            System.out.println("Id: " + alic.getId() + "Monto: " + "" + alic.getMtoAlicuota() + "fecha:" + "" + alic.getPeriodoActual().getTime());
        }
        System.out.println("-----------------------------------Facturas de cuotas especiales ---------------------------------");
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEspTemp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            System.out.println("Id: " + fce.getId() + "Monto: " + "" + fce.getMtoFacCuotaEsp() + "fecha:" + "" + fce.getPeriodoActual().getTime());
        }
        System.out.println("-----------------------------------Facturas de notas de debito ---------------------------------");
        for (Iterator<NotaCredDebCxC> it = listaNotaDebitoTemp.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (!ncd.getTipoNotaCredDebCxC().equals("Credito")) {
                System.out.println("Id: " + ncd.getId() + "Monto: " + "" + ncd.getMtoNotaCredDebCxC() + "fecha:" + "" + ncd.getFecNotaCredDebCxC());
            }
        }
        System.out.println("-----------------------------------Pagos del periodo ---------------------------------");
        for (Iterator<PagoFacCxC> it = pagoFacCxCInmPerTemp.iterator();
                it.hasNext();) {
            PagoFacCxC pago = (PagoFacCxC) it.next();
            System.out.println("Id: " + pago.getId() + "Monto: " + "" + pago.getMtoPagadoCxC() + "fecha:" + ""
                    + pago.getPagoCxC().getFecMovPagoCxC().getTime() + "Tipo CXC_:" + "" + pago.getDiscFacCxC() + " idCxC" + "" + pago.getIdFacCxC());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");

        listaFacGNCTemp.clear();
        listaDeudaPendTemp.clear();
        listaAlicuotaTemp.clear();
        listaFacCuotaEspTemp.clear();
        listaNotaDebitoTemp.clear();
        pagoFacCxCInmPerTemp.clear();
    }

    public Double obtenerSaldosPorPeriodo(Integer idInm, Calendar desde, Calendar hasta) throws ParseException {

        listaFacGNC.clear();
        listaDeudaPend.clear();
        listaAlicuota.clear();
        listaFacCuotaEsp.clear();
        listaNotaDebito.clear();

        saldoPeriodo = 0.0;

        listaFacGNC = obtenerFacGastoNoComun(idInm, desde, hasta);

        listaDeudaPend = obtenerFacDeudaPend(idInm, desde, hasta);

        listaAlicuota = obtenerAlicuota(idInm, desde, hasta);

        listaFacCuotaEsp = obtenerFacCuotaEsp(idInm, desde, hasta);

        listaNotaDebito = obtenerNotaDebito(idInm, desde, hasta);


        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos no comunes
        //-------------------------------------------------------
        listaFacGNCTemp.addAll(listaFacGNC);
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            if (!fgnc.getEdoFacGastoNoComun().equals("INAC")) {
                saldoPeriodo = saldoPeriodo + fgnc.getMtoFacGastoNoComun();
            }
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a deuda pendiente
        //-------------------------------------------------------
        listaDeudaPendTemp.addAll(listaDeudaPend);
        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxC dp = (DeudaPendCxC) it.next();
            saldoPeriodo = saldoPeriodo + dp.getMtoDeudaPendCxC();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a alicuotas
        //-------------------------------------------------------
        listaAlicuotaTemp.addAll(listaAlicuota);
        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();
            saldoPeriodo = saldoPeriodo + alic.getMtoAlicuota();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de cuotas especiales
        //-------------------------------------------------------
        listaFacCuotaEspTemp.addAll(listaFacCuotaEsp);
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            saldoPeriodo = saldoPeriodo + fce.getMtoFacCuotaEsp();
        }
        //-------------------------------------------------------
        //Acumula el saldo debido a notas de debito
        //-------------------------------------------------------
        listaNotaDebitoTemp.addAll(listaNotaDebito);
        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (!ncd.getTipoNotaCredDebCxC().equals("Credito")) {
                saldoPeriodo = saldoPeriodo + ncd.getMtoNotaCredDebCxC();
            }
        }

        return saldoPeriodo;

    }

    public Collection obtenerFacGastoNoComun(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id)
        String nombQuery = "buscarFacGastoNoComunPorIdInmRangoCobranza";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public Collection obtenerPagoFacCxCPorPeriodo(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection<PagoFacCxC> lista = new ArrayList<PagoFacCxC>();
        Collection<PagoFacCxC> listaRango = new ArrayList<PagoFacCxC>();

        String nombQuery = "buscarFacPagoCxCPorIdInmRangoCobranza";

        if (periodoFinal) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Calendar fechaPagoCxC;
        for (Iterator<PagoFacCxC> it = lista.iterator();
                it.hasNext();) {
            PagoFacCxC pagoFacCxc = (PagoFacCxC) it.next();
            fechaPagoCxC = convertirFecha(pagoFacCxc.getPagoCxC().getFecDocPagoCxC());
            if (periodoFinal) {
                if ((fechaPagoCxC.after(fecDesdeCal) | fechaPagoCxC.equals(fecDesdeCal)) & (fechaPagoCxC.before(fecHastaCal) | fechaPagoCxC.equals(fecHastaCal))) {
                    listaRango.add(pagoFacCxc);
                }
            } else {
                if ((fechaPagoCxC.after(fecDesdeCal) | fechaPagoCxC.equals(fecDesdeCal)) & (fechaPagoCxC.before(fecHastaCal))) {
                    listaRango.add(pagoFacCxc);
                }
            }

        }
        return listaRango;
    }

    public Collection obtenerFacDeudaPend(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();

        // Obtiene las facturas por deudda pendiente para el inmueble dado (id)
        String nombQuery = "buscarFacDeudaPendPorIdInmRangoCobranza";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerAlicuota(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        // Obtiene las alicuotas para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarAlicuotasPorIdInmRangoCobranza";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacCuotaEsp(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarFacCuotaEspPorIdInmRangoCobranza";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorIdRango(idInm, fecDesdeCal, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerNotaDebito(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection<NotaCredDebCxC> listaRango = new ArrayList<NotaCredDebCxC>();
        // Obtiene las notas de debito para el inmueble dado (id), los muestra en el panel 2
        String nombQuery = "buscarNotasDebitoCxCPorIdInmRangoCobranza";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaNotaCredDebCxC;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC notaCredDebCxc = (NotaCredDebCxC) it.next();
            fechaNotaCredDebCxC = convertirFecha(notaCredDebCxc.getFecNotaCredDebCxC());
            if ((fechaNotaCredDebCxC.after(fecDesdeCal) | fechaNotaCredDebCxC.equals(fecDesdeCal)) & (fechaNotaCredDebCxC.before(fecHastaCal) | fechaNotaCredDebCxC.equals(fecHastaCal))) {
                listaRango.add(notaCredDebCxc);
            }

        }
        return listaRango;
    }

    public Double obtenerPagosPorPeriodo(Collection pagoFacCxC) {

        pagoPeriodo = 0.0;

        //--------------------------------------------------------------
        //Acumula el monto pagado debido a facturas de gastos no comunes
        //--------------------------------------------------------------
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            for (Iterator<PagoFacCxC> it1 = pagoFacCxC.iterator();
                    it1.hasNext();) {
                PagoFacCxC pagoFac = (PagoFacCxC) it1.next();
                if (pagoFac.getDiscFacCxC().equals("GNC") & pagoFac.getIdFacCxC().equals(fgnc.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxC();
                    pagoFacCxCInmPerTemp.add(pagoFac);
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a deuda pendiente
        //-------------------------------------------------------

        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxC dp = (DeudaPendCxC) it.next();
            for (Iterator<PagoFacCxC> it1 = pagoFacCxC.iterator();
                    it1.hasNext();) {
                PagoFacCxC pagoFac = (PagoFacCxC) it1.next();
                if (pagoFac.getDiscFacCxC().equals("DPE") & pagoFac.getIdFacCxC().equals(dp.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxC();
                    pagoFacCxCInmPerTemp.add(pagoFac);
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a alicuotas
        //-------------------------------------------------------

        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();
            for (Iterator<PagoFacCxC> it1 = pagoFacCxC.iterator();
                    it1.hasNext();) {
                PagoFacCxC pagoFac = (PagoFacCxC) it1.next();
                if (pagoFac.getDiscFacCxC().equals("ALI") & pagoFac.getIdFacCxC().equals(alic.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxC();
                    pagoFacCxCInmPerTemp.add(pagoFac);
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a facturas de cuotas especiales
        //-------------------------------------------------------

        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            for (Iterator<PagoFacCxC> it1 = pagoFacCxC.iterator();
                    it1.hasNext();) {
                PagoFacCxC pagoFac = (PagoFacCxC) it1.next();
                if (pagoFac.getDiscFacCxC().equals("CES") & pagoFac.getIdFacCxC().equals(fce.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxC();
                    pagoFacCxCInmPerTemp.add(pagoFac);
                }

            }
        }
        //-------------------------------------------------------
        //Acumula el monto pagado debido a notas de debito
        //-------------------------------------------------------

        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            for (Iterator<PagoFacCxC> it1 = pagoFacCxC.iterator();
                    it1.hasNext();) {
                PagoFacCxC pagoFac = (PagoFacCxC) it1.next();
                if (pagoFac.getDiscFacCxC().equals("NCD") & pagoFac.getIdFacCxC().equals(ncd.getId())) {
                    pagoPeriodo += pagoFac.getMtoPagadoCxC();
                    pagoFacCxCInmPerTemp.add(pagoFac);
                }

            }
        }
        return pagoPeriodo;
    }

    public Collection<PagoCxC> obtenerPagosCxCPorInmueble(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection<PagoCxC> lista = new ArrayList<PagoCxC>();
        Collection<PagoCxC> listaRango = new ArrayList<PagoCxC>();

        String nombQuery = "buscarPagoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Calendar fechaPagoCxC;
        for (Iterator<PagoCxC> it = lista.iterator();
                it.hasNext();) {
            PagoCxC pagoCxc = (PagoCxC) it.next();
            fechaPagoCxC = convertirFecha(pagoCxc.getFecDocPagoCxC());
            if ((fechaPagoCxC.after(fecDesdeCal) | fechaPagoCxC.equals(fecDesdeCal)) & (fechaPagoCxC.before(fecHastaCal) | fechaPagoCxC.equals(fecHastaCal))) {
                listaRango.add(pagoCxc);
            }
        }
        return listaRango;
    }

    private void generarDataSource() throws ParseException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        //--------------------------------------------------------
        // Se busca el condominio con sus propiedades
        //--------------------------------------------------------
        condominio = buscarCondominioFetchPropiedades(idCond);

        if (condominio.getPropiedades().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Hasta ahora no se han registrado ninguna propiedad para el condomino actual...reporte cancelado"));
            return;
        }
        //------------------------------------------------------------
        // Se genera la lista de inmuebles del condominio
        //------------------------------------------------------------

        inmuebles = buscarInmueblesCondominio(condominio);

        if (inmuebles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Hasta ahora no se han registrado ningun inmueble para el condomino actual...reporte cancelado"));
            return;
        }
        //------------------------------------------------------------
        // Se generan los seis periodos a considerar en el reporte
        //------------------------------------------------------------
        generarPeriodos(condominio);
        chequearPeriodos();
        generarIndGestionPorPeriodo();
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

    public void generarReporteGestionCobranza(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        generarDataSource();

        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/IndGestionCobranza.jasper");

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

        PeriodoDT pdt = periodos.get(0);
        fecDesde = pdt.getFecDesde();
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
