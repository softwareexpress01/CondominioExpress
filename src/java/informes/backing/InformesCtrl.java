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
import saas.modelos.Suscripcion;
import datosbasicos.modelos.Usuario;
import informes.modelos.CarteleraDT;
import informes.modelos.CondominioDT;
import informes.modelos.InmuebleDT;
import informes.modelos.PeriodoDT;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;

public class InformesCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private List<PeriodoDT> periodos = new ArrayList<PeriodoDT>();
    private String encMes1 = null;
    private String encMes2 = null;
    private String encMes3 = null;
    private String encMes4 = null;
    private String encMes5 = null;
    private String encMes6 = null;
    private boolean periodoInicial;
    private boolean periodoFinal;
    private Collection listaRango = new ArrayList();

    public InformesCtrl() {
    }

    public void generarReporteInmuebles(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, InstantiationException, IllegalAccessException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/GralInmueble.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();
        //--------------------------------------------------------
        // Se busca el condominio con sus propiedades
        //--------------------------------------------------------
        Condominio condominio;
        try {
            condominio = buscarCondominioFetchPropiedades(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //------------------------------------------------------------
        // Se genera el datasource de inmubles del condominio actual
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceInmuebles;

        Collection inmuebles = buscarInmueblesCondominio(condominio);
        if (inmuebles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se han configurado los inmuebles...por favor, contacte a su administrador"));
            return;
        }

        dataSourceInmuebles = new JRBeanCollectionDataSource(inmuebles);

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceInmuebles);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();
        //-------------------------------------------------------------------------------------------------------------------
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

    private Collection<InmuebleDT> buscarInmueblesCondominio(Condominio condominio) {

        Collection<InmuebleDT> lista = new ArrayList<InmuebleDT>();

        Collection<Inmueble> inmuebles;
        Collection<InmuebleDT> inmueblesDT;
        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }

            inmueblesDT = inicializarInmueblesDT(inmuebles, nombProp);
            lista.addAll(inmueblesDT);
            inmuebles.clear();
            inmueblesDT.clear();
        }
        return lista;
    }

    private Collection<InmuebleDT> inicializarInmueblesDT(Collection inmuebles, String nombProp) {

        Collection<InmuebleDT> inmueblesDT = new ArrayList<InmuebleDT>();

        for (Iterator<Inmueble> it = inmuebles.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();

            InmuebleDT inmuebleDT = new InmuebleDT();

            inmuebleDT.setNombPropiedad(nombProp);
            inmuebleDT.setCodInmueble(inm.getCodInmueble());
            inmuebleDT.setTipoInmueble(inm.getTipoInmueble());
            inmuebleDT.setAreaInmueble(inm.getAreaInmueble());
            inmuebleDT.setNombPropInmueble(inm.getNombPropInmueble());
            inmuebleDT.setTelInmueble(inm.getTelInmueble());
            inmuebleDT.setEmailPropInmueble(inm.getEmailPropInmueble());
            inmuebleDT.setAlicInmueble(inm.getAlicInmueble());

            inmueblesDT.add(inmuebleDT);
        }
        return inmueblesDT;
    }

    public void generarReporteCartelera(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException, ParseException {

        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/Cartelera.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();
        //--------------------------------------------------------
        // Se busca el condominio con sus propiedades
        //--------------------------------------------------------
        Condominio condominio;
        try {
            condominio = buscarCondominioFetchPropiedades(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //------------------------------------------------------------
        // Se genera el datasource de cartelera del condominio actual
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceCartelera;

        Collection cartelera = buscarInmueblesCartelera(condominio);

        if (cartelera.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Hasta ahora no se han registrado cxc para ningún inmueble...reporte cancelado"));
            return;
        }
        dataSourceCartelera = new JRBeanCollectionDataSource(cartelera);
        //------------------------------------------------------------
        // Se inicializan los parametros para el encabezado del 
        // reporte de acuerdo a los periodos generados.
        //------------------------------------------------------------

        inicializarParametros();

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("encMes1", encMes1);
        parametros.put("encMes2", encMes2);
        parametros.put("encMes3", encMes3);
        parametros.put("encMes4", encMes4);
        parametros.put("encMes5", encMes5);
        parametros.put("encMes6", encMes6);
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceCartelera);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();

    }

    private Collection<CarteleraDT> buscarInmueblesCartelera(Condominio condominio) throws ParseException {

        Collection<CarteleraDT> lista = new ArrayList<CarteleraDT>();
        Collection<Inmueble> inmuebles;
        Collection<CarteleraDT> carteleraDT;
        String nombQuery = "buscarInmueblesPorIdProp";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }

            carteleraDT = inicializarCarteleraDT(inmuebles, nombProp, condominio);
            lista.addAll(carteleraDT);
            inmuebles.clear();
            carteleraDT.clear();
        }
        return lista;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<CarteleraDT> listaDesOrdenada = new ArrayList<CarteleraDT>();

        for (Iterator<CarteleraDT> it = lista.iterator();
                it.hasNext();) {
            CarteleraDT car = (CarteleraDT) it.next();
            listaDesOrdenada.add(car);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCarDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<CarteleraDT> coleccionOrdenada = new ArrayList<CarteleraDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            CarteleraDT carDT = listaDesOrdenada.get(i);
            coleccionOrdenada.add(carDT);
        }
        return coleccionOrdenada;
    }

    public Collection ordenarListaInmuebles(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<InmuebleDT> listaDesOrdenada = new ArrayList<InmuebleDT>();

        for (Iterator<InmuebleDT> it = lista.iterator();
                it.hasNext();) {
            InmuebleDT inm = (InmuebleDT) it.next();
            listaDesOrdenada.add(inm);
        }

        Collections.sort(listaDesOrdenada, new ComparadorInmDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<InmuebleDT> coleccionOrdenada = new ArrayList<InmuebleDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            InmuebleDT inmDT = listaDesOrdenada.get(i);
            coleccionOrdenada.add(inmDT);
        }
        return coleccionOrdenada;
    }

    private void inicializarParametros() {

        Integer contador = 0;
        for (int i = 0; i < periodos.size(); i++) {
            PeriodoDT pdt = periodos.get(i);

            if (contador.equals(new Integer(0))) {
                encMes1 = pdt.getDesPeriodo();
            }
            if (contador.equals(new Integer(1))) {
                encMes2 = pdt.getDesPeriodo();
            }
            if (contador.equals(new Integer(2))) {
                encMes3 = pdt.getDesPeriodo();
            }
            if (contador.equals(new Integer(3))) {
                encMes4 = pdt.getDesPeriodo();
            }
            if (contador.equals(new Integer(4))) {
                encMes5 = pdt.getDesPeriodo();
            }
            if (contador.equals(new Integer(5))) {
                encMes6 = pdt.getDesPeriodo();
            }
            contador++;
        }
    }

    private Collection<CarteleraDT> inicializarCarteleraDT(Collection inmuebles, String nombProp, Condominio condominio) throws ParseException {

        Collection<CarteleraDT> listaDT = new ArrayList<CarteleraDT>();
        int contador;
        periodos.clear();
        PeriodoDT pdt = null;
        Double saldoPer = 0.0;
        BigDecimal saldo;

        periodos = generarPeriodosCartelera(condominio);

        for (Iterator<Inmueble> it = inmuebles.iterator();
                it.hasNext();) {
            Inmueble inm = (Inmueble) it.next();
            Integer idInm = inm.getId();
            periodoInicial = true;
            periodoFinal = false;
            for (int i = 0; i < periodos.size() - 1; i++) {
                pdt = periodos.get(i);
                saldoPer = obtenerSaldosPorPeriodo(idInm, pdt.getFecDesde(), pdt.getFecHasta());
                if (periodoInicial) {
                    periodoInicial = false;
                }
                pdt.setSaldoPeriodo(saldoPer);
            }
            periodoFinal = true;
            pdt = periodos.get(periodos.size() - 2);
            saldoPer = obtenerSaldosPorPeriodo(idInm, pdt.getFecDesde(), pdt.getFecHasta());
            pdt = periodos.get(periodos.size() - 1);
            pdt.setSaldoPeriodo(saldoPer);
            periodoFinal = false;

            CarteleraDT carteleraDT = new CarteleraDT();

            carteleraDT.setNombPropiedad(nombProp);
            carteleraDT.setCodInmueble(inm.getCodInmueble());
            carteleraDT.setNombPropietario(inm.getNombPropInmueble());
            carteleraDT.setSaldoTotal(0.0);
            carteleraDT.setSaldoAnterior(0.0);
            contador = 0;

            for (int i = 0; i < periodos.size() - 1; i++) {
                pdt = periodos.get(i);

                /*saldo = BigDecimal.valueOf(pdt.getSaldoPeriodo()).setScale(2, RoundingMode.HALF_EVEN);;
                saldoPer = saldo.doubleValue();*/
                saldoPer = pdt.getSaldoPeriodo();

                switch (i) {
                    case 0:
                        carteleraDT.setSaldoCxCP1(saldoPer);
                        contador++;
                        break;
                    case 1:
                        carteleraDT.setSaldoCxCP2(saldoPer);
                        contador++;
                        break;

                    case 2:
                        carteleraDT.setSaldoCxCP3(saldoPer);
                        contador++;
                        break;
                    case 3:
                        carteleraDT.setSaldoCxCP4(saldoPer);
                        contador++;
                        break;
                    case 4:
                        carteleraDT.setSaldoCxCP5(saldoPer);
                        contador++;
                        break;
                    case 5:
                        carteleraDT.setSaldoCxCP6(saldoPer);
                        contador++;
                        break;
                    case 6:
                        carteleraDT.setSaldoAnterior(saldoPer);
                        contador++;
                        break;
                }
                carteleraDT.setSaldoTotal(carteleraDT.getSaldoTotal() + saldoPer);
            }

            pdt = periodos.get(periodos.size() - 1);

            saldoPer = pdt.getSaldoPeriodo();

            carteleraDT.setSaldoAnterior(carteleraDT.getSaldoAnterior() + saldoPer);
            carteleraDT.setSaldoTotal(carteleraDT.getSaldoTotal() + saldoPer);

            listaDT.add(carteleraDT);
        }
        periodoInicial = true;
        periodoFinal = false;

        return listaDT;
    }

    private List<PeriodoDT> generarPeriodosCartelera(Condominio condominio) {

        //-----------------------------------------------------------------
        //                  Se obtiene el primer periodo
        // Los periodos van desde el mas reciente (actual) hacia atras, para
        // un total de 7 periodos (si hay historia suficiente), los primeros seis 
        //corresponden a los seis meses mas recientes. El ultimo al tiempo que va
        //desde el ultimo generado (fecha desde) hacia atras.
        //-----------------------------------------------------------------
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
        periodoDT1.setDesPeriodo(desc1);

        periodos.add(periodoDT1);

        if (periodo1.equals(condominio.getFecCreacion())) {
            //-----------------------------------------------------------
            //        Se obtiene el septimo periodo para el saldo
            //-----------------------------------------------------------
            PeriodoDT periodoDT7 = new PeriodoDT();
            periodoDT7.setFecDesde(null);
            periodoDT7.setFecHasta(periodo1);
            periodos.add(periodoDT7);

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

        periodos.add(periodoDT2);

        if (periodo2.equals(condominio.getFecCreacion())) {
            //-----------------------------------------------------------
            //        Se obtiene el septimo periodo para el saldo
            //-----------------------------------------------------------
            PeriodoDT periodoDT7 = new PeriodoDT();
            periodoDT7.setFecDesde(null);
            periodoDT7.setFecHasta(periodo2);
            periodos.add(periodoDT7);

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

        periodos.add(periodoDT3);

        //-------------------------------------------
        if (periodo3.equals(condominio.getFecCreacion())) {
            //-----------------------------------------------------------
            //        Se obtiene el septimo periodo para el saldo
            //-----------------------------------------------------------
            PeriodoDT periodoDT7 = new PeriodoDT();
            periodoDT7.setFecDesde(null);
            periodoDT7.setFecHasta(periodo3);
            periodos.add(periodoDT7);
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

        periodos.add(periodoDT4);

        if (periodo4.equals(condominio.getFecCreacion())) {
            //-----------------------------------------------------------
            //        Se obtiene el septimo periodo para el saldo
            //-----------------------------------------------------------
            PeriodoDT periodoDT7 = new PeriodoDT();
            periodoDT7.setFecDesde(null);
            periodoDT7.setFecHasta(periodo4);
            periodos.add(periodoDT7);
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

        periodos.add(periodoDT5);

        if (periodo5.equals(condominio.getFecCreacion())) {
            //-----------------------------------------------------------
            //        Se obtiene el septimo periodo para el saldo
            //-----------------------------------------------------------
            PeriodoDT periodoDT7 = new PeriodoDT();
            periodoDT7.setFecDesde(null);
            periodoDT7.setFecHasta(periodo5);
            periodos.add(periodoDT7);
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

        periodos.add(periodoDT6);

        //-----------------------------------------------------------
        //        Se obtiene el septimo periodo para el saldo
        //-----------------------------------------------------------
        PeriodoDT periodoDT7 = new PeriodoDT();
        periodoDT7.setFecDesde(periodo6);
        periodoDT7.setFecHasta(periodo6);
        periodos.add(periodoDT7);

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

            String stringD = mesp + "-" + añoD + "-" + diaf;
            String stringH = mesp1 + "-" + añoH + "-" + diaH;
            System.out.println("----------------------------Desde:" + stringD + "Hasta:" + stringH + "descripcion:" + desD);
        }
        return periodos;

    }

    public String obtenerDesPeriodo(GregorianCalendar periodo) {

        String descripcion = "";

        int mes = periodo.get(Calendar.MONTH);
        int año = periodo.get(Calendar.YEAR);

        String mesp = String.valueOf(mes);
        System.out.println("---------------------------El valor de mesp es igual a : " + mesp);
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

    public Double obtenerSaldosPorPeriodo(Integer idInm, Calendar desde, Calendar hasta) throws ParseException {

        Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
        Collection<DeudaPendCxC> listaDeudaPend = new ArrayList<DeudaPendCxC>();
        Collection<Alicuota> listaAlicuota = new ArrayList<Alicuota>();
        Collection<FacCuotaEsp> listaFacCuotaEsp = new ArrayList<FacCuotaEsp>();
        Collection<NotaCredDebCxC> listaNotaDebito = new ArrayList<NotaCredDebCxC>();

        Double saldoPeriodo = new Double(0);
        BigDecimal saldo;
        BigDecimal cxcTotalBD = new BigDecimal("0.00");

        if (periodoFinal) {
            listaFacGNC = obtenerFacGastoNoComunSaldo(idInm, desde);
            listaDeudaPend = obtenerFacDeudaPendSaldo(idInm, desde);
            listaAlicuota = obtenerAlicuotaSaldo(idInm, desde);
            listaFacCuotaEsp = obtenerFacCuotaEspSaldo(idInm, desde);
            listaNotaDebito = obtenerNotaDebitoSaldo(idInm, desde);
        } else {
            listaFacGNC = obtenerFacGastoNoComun(idInm, desde, hasta);
            listaDeudaPend = obtenerFacDeudaPend(idInm, desde, hasta);
            listaAlicuota = obtenerAlicuota(idInm, desde, hasta);
            listaFacCuotaEsp = obtenerFacCuotaEsp(idInm, desde, hasta);
            listaNotaDebito = obtenerNotaDebito(idInm, desde, hasta);
        }

        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de gastos no comunes
        //-------------------------------------------------------
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {
            FacGastoNoComun fgnc = (FacGastoNoComun) it.next();
            if (!fgnc.getEdoFacGastoNoComun().equals("CANC") & !fgnc.getEdoFacGastoNoComun().equals("INAC")) {
                //Prueba
                saldo = BigDecimal.valueOf(fgnc.getSaldoFacGastoNoComun()).setScale(2, RoundingMode.HALF_EVEN);
                cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
                //
                // saldoPeriodo = saldoPeriodo + fgnc.getSaldoFacGastoNoComun();
            }
        }
        saldoPeriodo = saldoPeriodo + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        //-------------------------------------------------------
        //Acumula el saldo debido a deuda pendiente
        //-------------------------------------------------------
        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {
            DeudaPendCxC dp = (DeudaPendCxC) it.next();
            if (!dp.getEdoDeudaPendCxC().equals("CANC")) {
                //Prueba
                saldo = BigDecimal.valueOf(dp.getSaldoDeudaPendCxC()).setScale(2, RoundingMode.HALF_EVEN);
                cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
                //
                //saldoPeriodo = saldoPeriodo + dp.getSaldoDeudaPendCxC();
            }
        }
        saldoPeriodo = saldoPeriodo + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        //-------------------------------------------------------
        //Acumula el saldo debido a alicuotas
        //-------------------------------------------------------
        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();
            if (!alic.getEdoAlicuota().equals("CANC")) {
                //Prueba
                saldo = BigDecimal.valueOf(alic.getSaldoAlicuota()).setScale(2, RoundingMode.HALF_EVEN);
                cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
                //
                //saldoPeriodo = saldoPeriodo + alic.getSaldoAlicuota();
            }
        }
        saldoPeriodo = saldoPeriodo + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        //-------------------------------------------------------
        //Acumula el saldo debido a facturas de cuotas especiales
        //-------------------------------------------------------
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                //Prueba
                saldo = BigDecimal.valueOf(fce.getSaldoFacCuotaEsp()).setScale(2, RoundingMode.HALF_EVEN);
                cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
                //
                //saldoPeriodo = saldoPeriodo + fce.getSaldoFacCuotaEsp();
            }
        }
        saldoPeriodo = saldoPeriodo + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        //-------------------------------------------------------
        //Acumula el saldo debido a notas de debito
        //-------------------------------------------------------
        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (!ncd.getEdoNotaCredDebCxC().equals("CANC")) {
                if (ncd.getTipoNotaCredDebCxC().equals("Credito")) {
                    //Prueba
                    saldo = BigDecimal.valueOf(ncd.getSaldoNotaCredDebCxC()).setScale(2, RoundingMode.HALF_EVEN);
                    cxcTotalBD = cxcTotalBD.subtract(saldo).setScale(2, RoundingMode.HALF_EVEN);
                    //
                    //saldoPeriodo = saldoPeriodo - ncd.getSaldoNotaCredDebCxC();
                } else {
                    //Prueba
                    saldo = BigDecimal.valueOf(ncd.getSaldoNotaCredDebCxC()).setScale(2, RoundingMode.HALF_EVEN);
                    cxcTotalBD = cxcTotalBD.add(saldo).setScale(2, RoundingMode.HALF_EVEN);
                    //
                    //saldoPeriodo = saldoPeriodo + ncd.getSaldoNotaCredDebCxC();
                }
            }
        }
        saldoPeriodo = saldoPeriodo + cxcTotalBD.doubleValue();
        cxcTotalBD = BigDecimal.ZERO;
        
        periodoInicial = false;
        return saldoPeriodo;

    }

    public Collection obtenerFacGastoNoComun(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        if (periodoInicial) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarFacGastoNoComunPorIdInmRango";
        } else {
            nombQuery = "buscarFacGastoNoComunPorIdInmRangoCobranza";
        }
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2

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

    public Collection obtenerFacGastoNoComunSaldo(Integer idInm, Calendar fecDesdeCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        nombQuery = "buscarFacGastoNoComunPorIdInmRangoSaldo";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecDesdeCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacDeudaPend(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        if (periodoInicial) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarFacDeudaPendPorIdInmRango";
        } else {
            nombQuery = "buscarFacDeudaPendPorIdInmRangoCobranza";
        }
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2

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

    public Collection obtenerFacDeudaPendSaldo(Integer idInm, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery = "buscarFacDeudaPendPorIdInmRangoSaldo";
        // Obtiene las facturas de deuda pendiente que tengan fecha (llamada periodo en el query),menor que la fecha hasta
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecHastaCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerAlicuota(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;
        // Obtiene las alicuotas para el inmueble dado (id), los muestra en el panel 2
        if (periodoInicial) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarAlicuotasPorIdInmRango";
        } else {
            nombQuery = "buscarAlicuotasPorIdInmRangoCobranza";
        }

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

    public Collection obtenerAlicuotaSaldo(Integer idInm, Calendar fecDesdeCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        nombQuery = "buscarAlicuotasPorIdInmRangoSaldo";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecDesdeCal, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacCuotaEsp(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) {

        Collection lista = new ArrayList();
        String nombQuery;
        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        if (periodoInicial) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
            nombQuery = "buscarFacCuotaEspPorIdInmRango";
        } else {
            nombQuery = "buscarFacCuotaEspPorIdInmRangoCobranza";
        }

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

    public Collection obtenerFacCuotaEspSaldo(Integer idInm, Calendar fecDesdeCal) {

        Collection lista = new ArrayList();
        String nombQuery;

        nombQuery = "buscarFacCuotaEspPorIdInmRangoSaldo";

        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, fecDesdeCal, nombQuery);
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

    public Collection obtenerNotaDebito(Integer idInm, Calendar fecDesdeCal, Calendar fecHastaCal) throws ParseException {

        Collection lista = new ArrayList();
        listaRango.clear();
        String nombQuery;

        if (periodoInicial) {
            fecHastaCal.set(Calendar.HOUR, 23);
            fecHastaCal.set(Calendar.MINUTE, 59);
            fecHastaCal.set(Calendar.SECOND, 25);
            fecHastaCal.set(Calendar.MILLISECOND, 0);
        }
        nombQuery = "buscarNotasDebitoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaPago;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            fechaPago = convertirFecha(ncd.getFecNotaCredDebCxC());
            if (periodoInicial) {
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

    public Collection obtenerNotaDebitoSaldo(Integer idInm, Calendar fecDesdeCal) throws ParseException {

        Collection lista = new ArrayList();
        Collection listaRang = new ArrayList();
        // Obtiene las notas de debito para el inmueble dado (id),  que tengan fecha (llamada periodo en el query),menor que la fecha Hasta
        String nombQuery = "buscarNotasDebitoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Calendar fechaNota;
        for (Iterator<NotaCredDebCxC> it = lista.iterator();
                it.hasNext();) {
            NotaCredDebCxC notaDebito = (NotaCredDebCxC) it.next();
            fechaNota = convertirFecha(notaDebito.getFecNotaCredDebCxC());
            if (fechaNota.before(fecDesdeCal)) {
                listaRang.add(notaDebito);
            }
        }
        return listaRang;
    }

    public void generarReporteCondominios(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/Condominios.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        Condominio condominio = buscarCondominio();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);

        String nombCond = beanUsuario.getCondominioActual();
        //--------------------------------------------------------
        // Se busca la suscripcion asociada al usuario
        //--------------------------------------------------------
        Integer idUsuario = beanUsuario.getIdUsuario();
        String codUsuario = beanUsuario.getCodUsuario();
        Suscripcion sus = buscarSuscripcion(idUsuario);
        String fecSus = convertirFechaAString(sus.getFecSuscripcion());
        //--------------------------------------------------------
        // Se buscan los condominios asociados al administrador
        // con sus propiedades asociadas
        //--------------------------------------------------------
        Collection<Condominio> condominios = new ArrayList<Condominio>();

        if (!beanUsuario.getPerUsuario().equals("Admin")) {
            Usuario admin = buscarAdministrador(idUsuario);
            condominios = buscarCondominios(admin.getCodUsuario());
        } else {
            condominios = buscarCondominios(codUsuario);
        }
        Collection<CondominioDT> condominiosDT = new ArrayList<CondominioDT>();
        //--------------------------------------------------------
        // Se inicializan los condominios a mostrar en el reporte
        //--------------------------------------------------------

        condominiosDT = inicializarCondominioDT(condominios);

        //------------------------------------------------------------
        // Se genera el datasource de condominios y los datos del susc
        //------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceCondominio;

        dataSourceCondominio = new JRBeanCollectionDataSource(condominiosDT);
        //------------------------------------------------------------
        // Se inicializan los parametros para el encabezado del 
        // reporte.
        //------------------------------------------------------------

        inicializarParametros();

        HashMap parametros = new HashMap();
        parametros.put("nombCondominio", nombCond);
        parametros.put("numSus", sus.getNumSuscripcion());
        parametros.put("nombSus", sus.getNombSuscriptor());
        parametros.put("apelSus", sus.getApelSuscriptor());
        parametros.put("cedRifSus", sus.getNumCedSuscriptor());
        parametros.put("fecSus", fecSus);
        parametros.put("telSus", sus.getTelSuscriptor());
        parametros.put("celSus", sus.getCelSuscriptor());
        parametros.put("logo", logo);
        //-------------------------------------------------------------------------------------------------------------------
        JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, dataSourceCondominio);
        //-------------------------------------------------------------------------------------------------------------------
        response.setContentType("application/pdf");

        servletOutputStream.flush();

        servletOutputStream.close();
        context.responseComplete();

    }

    public Suscripcion buscarSuscripcion(Integer idUsuario) {

        Suscripcion sus;
        String nombQuery = "buscarSuscripcionPorIdUsuario";
        DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();
        try {
            sus = dataAcces.buscarEntidadPorId(idUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return sus;
    }

    public Usuario buscarAdministrador(Integer idUsuario) {

        Usuario admin;
        String nombQuery = "buscarAdminPorIdUsuario";
        DaoQuery<Usuario> dataAcces = new DaoQuery<Usuario>();
        try {
            admin = dataAcces.buscarEntidadPorId(idUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return admin;
    }

    public Collection buscarCondominios(String codUsuario) {

        // Obtiene los condominios para el usuario dado
        // Obtiene el codUsuario, del usuario actual, del beanUsuario
        List listCond = new ArrayList();
        String nombQuery = "buscarCondominiosUsuario";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listCond = dataAcces.buscarListaEntidadesPorCodigo(codUsuario, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collections.sort(listCond, new Comparador());

        Collection<Condominio> coleccion = new ArrayList<Condominio>();

        for (int i = 0; i < listCond.size(); i++) {
            Condominio cond = (Condominio) listCond.get(i);
            coleccion.add(cond);
        }
        return coleccion;
    }

    public Collection inicializarCondominioDT(Collection condominios) {

        Collection<CondominioDT> condominiosDT = new ArrayList<CondominioDT>();
        Integer numPropCond;
        Integer numInmCond;

        for (Iterator<Condominio> it = condominios.iterator();
                it.hasNext();) {
            Condominio cond = (Condominio) it.next();
            Integer idCond = cond.getId();
            Condominio condProp = buscarCondominioFetchPropiedades(idCond);
            if (condProp.getPropiedades().isEmpty()) {
                numPropCond = 0;
                numInmCond = 0;
            } else {
                numPropCond = condProp.getPropiedades().size();
                numInmCond = obtenerTotalInmCond(condProp);
            }
            CondominioDT condominioDT = new CondominioDT();

            condominioDT.setNombCondominio(cond.getNombCondominio());
            condominioDT.setCedRifCondominio(cond.getCedRifCondominio());
            condominioDT.setDirCondominio(cond.getDirCondominio());
            condominioDT.setTelOficCondominio(cond.getTelOficCondominio());
            condominioDT.setNombAdminCondominio(cond.getNombAdminCondominio());
            condominioDT.setTelAdminCondominio(cond.getTelAdminCondominio());
            condominioDT.setNumPropiedades(numPropCond);
            condominioDT.setNumInmuebles(numInmCond);

            condominiosDT.add(condominioDT);
        }

        return condominiosDT;
    }

    public Integer obtenerTotalInmCond(Condominio condProp) {

        Integer numInm = 0;
        for (Iterator<Propiedad> it = condProp.getPropiedades().iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            numInm = numInm + Integer.parseInt(prop.getNumInmuebles());
        }
        return numInm;
    }

    public Condominio buscarCondominio() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio = null;

        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
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
}
