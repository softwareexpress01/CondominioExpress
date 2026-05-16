package administracion.backing;

/**
 *
 * @author jesus 29-07-2012
 */
import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import datosbasicos.modelos.Propiedad;
import datosbasicos.modelos.Proveedor;
import informes.modelos.AlicuotaDT;
import informes.modelos.FacCuotaEspDT;
import informes.modelos.NotaDebitoCxCDT;
import informes.modelos.PagoCxCDTR;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.AjustesCond;
import movimientos.modelos.Alicuota;
import movimientos.modelos.CuotaEsp;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoComunDT;
import movimientos.modelos.GastoComunOrd;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoAdelantado;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoFacCxC;
import movimientos.modelos.TranFondo;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class CierrePreliminarCtrl implements Serializable {

    private BeanUsuario beanUsuario;
    private Integer idInmAnterior = 0;
    private Integer idInmAntNd = 0;
    private Integer idInmAntDp = 0;
    private Integer idInmAntNc = 0;
    private Integer idInmAntAl = 0;
    private Integer idInmAntCe = 0;
    private Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
    private Collection<Alicuota> listaAlic = new ArrayList<Alicuota>();
    private Collection<FacCuotaEsp> listaFCE = new ArrayList<FacCuotaEsp>();
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<GastoComunOrd>();
    private Collection<GastoComunDT> listaGastoComunOrdDT = new ArrayList<GastoComunDT>();
    private Collection<CuotaEsp> listaCEsp = new ArrayList<CuotaEsp>();
    private Collection<PagoAdelantado> listaInmPagosAdel = new ArrayList<PagoAdelantado>();
    private Collection<Alicuota> listaInmAlicuotas = new ArrayList<Alicuota>();
    private Collection<Alicuota> listaAlicuotasProcesPad = new ArrayList<Alicuota>();
    private Collection<NotaCredDebCxC> listaInmNotasCred = new ArrayList<NotaCredDebCxC>();
    private Collection<NotaCredDebCxC> listaInmNotasDeb = new ArrayList<NotaCredDebCxC>();
    private Collection<FacCuotaEsp> listaInmFacCuotaEsp = new ArrayList<FacCuotaEsp>();
    private Collection<DeudaPendCxC> listaInmDeudaPend = new ArrayList<DeudaPendCxC>();
    private Collection<FacGastoNoComun> listaInmFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection<PagoCxC> listaPagosCxCNuevos = new ArrayList<PagoCxC>();
    private Collection<PagoFacCxC> listaPagoFacCxC = new ArrayList<PagoFacCxC>();
    private Collection<FacCuotaEsp> listaFacCuotaEspNuevas = new ArrayList<FacCuotaEsp>();
    private TranFondo tranFondoNueva = new TranFondo();//Generada para el fondo de reserva
    private Collection<TranFondo> tranFondo = new ArrayList<TranFondo>();//Transacciones de fondo de prestaciones y reserva
    private Collection<AjustesCond> listaAjustesCond = new ArrayList<AjustesCond>();
    private Collection<NotaCredDebCxC> listaNotaCredDebCxC = new ArrayList<NotaCredDebCxC>();
    private Collection<PagoFacCxC> listaPagosFacCxCInm = new ArrayList<PagoFacCxC>();
    private Collection<PagoFacCxC> listaPagosFacCxCInmPer = new ArrayList<PagoFacCxC>();
    private Parametro parametro;
    private Integer nroNota;
    private double mtoMora;
    private int numDias;
    private Calendar fechaCorte;
    private Calendar periodoActual;
    private Boolean cxcCancel = false;// true la cxc a procesar tiene estado CANC.Se usa para el calculo de la mora. 

    public CierrePreliminarCtrl() {
    }

    public String irASeleccionarCondominio() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "administracion";

    }

    public void procesarCierrePreliminar(ActionEvent actionEvent) throws ClassNotFoundException, SQLException, IOException, JRException, ParseException {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        Integer idCondActual = beanUsuario.getIdCondActual();
        periodoActual = beanUsuario.getPeriodoActual();

        //---------------------------------------------------------------------------------
        // Retorna el condominio actual con todas sus propiedades e inmuebles
        // A partir del 09-11-2016, los pagos automaticos tendran la fecha de corte
        //---------------------------------------------------------------------------------
        Condominio condominio = buscarCondominioFetchPropiedades(idCondActual);
        fechaCorte = generarFechaCorte(condominio);
        //---------------------------------------------------------------------------------
        // Busca el tipo de facturacion . Si los parametros estan configurados, continua
        // para generar las alicuotas para cada inmueble
        //---------------------------------------------------------------------------------
        parametro = condominio.getParametro();

        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...cierre cancelado"));
            return;
        }
        String tipoFac = parametro.getTipoFacturacion();
        //---------------------------------------------------------------------------------
        // Genera una lista con los gastos comunes del periodo a cerrar (actual)
        //---------------------------------------------------------------------------------
        Collection gastosComunes = buscarGastosComunesOrdPeridoActual(idCondActual, periodoActual);

        if (gastosComunes == null & tipoFac.equals("gastos")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados gastos comunes ordinarios para el período actual...cierre cancelado"));
            return;
        }
        //---------------------------------------------------------------------------------
        // Genera la suma de los montos de cada gasto comun ordinario: total de gastos
        //---------------------------------------------------------------------------------
        Double totalGastos = totalizarGastosComunesOrd(gastosComunes);
        //---------------------------------------------------------------------------
        // Buscar los inmuebles de cada propiedad para el condominio encontrado
        //---------------------------------------------------------------------------
        listaInm = buscarInmueblesPropiedades(condominio);
        //---------------------------------------------------------------------------------
        // Genera una lista con los ajustes del periodo a cerrar (actual)
        //---------------------------------------------------------------------------------
        Collection ajustes = buscarAjustesPeridoActual(idCondActual, periodoActual);
        //---------------------------------------------------------------------------------
        // Genera la suma de los montos de cada ajuste: total de ajustes,si los hubo
        // para el periodo actual. El total se suma en el total de gastos. Los ajustes
        // aplicados cambian su estado a CANC.
        //---------------------------------------------------------------------------------

        if (!(ajustes == null)) {
            Double totalAjustes = totalizarAjustes(ajustes);
            totalGastos = totalGastos + totalAjustes;
        }

        //---------------------------------------------------------------------------
        // En caso de que el tipo de facturacion sea por alicuota, se verifica la
        // existencia del fondo de reserva, si existe, se calcula el mto mensual
        // y se genera una tranFondo. Lo mismo aplica para el fondo de prestaciones,
        // con la diferencia de que no se genera ninguna transaccion.
        //---------------------------------------------------------------------------
        Double mtoFondoPres = new Double(0.0);
        Fondo fondoPres = null;
        fondoPres = buscarFondoPrestaciones(condominio.getId());
        if (fondoPres != null) {
            mtoFondoPres = calcularMtoFondoPrestaciones(fondoPres, periodoActual);

        }
        totalGastos = totalGastos + mtoFondoPres;

        Fondo fondo = null;
        Double mtoFondoRes = new Double(0.0);
        if (tipoFac.equals("gastos")) {
            fondo = buscarFondoReserva(condominio.getId());
            if (fondo != null) {
                mtoFondoRes = totalGastos * fondo.getPorcentFondo() / 100;
                totalGastos = totalGastos + mtoFondoRes;
                generarTranFondoReserva(fondo, mtoFondoRes, periodoActual);
            }
        }

        //----------------------------------------------------------------------------------------------
        // para efectos del reporte preliminar se guardan todas las trans de fondo en la misma coleccion
        //----------------------------------------------------------------------------------------------
        if (fondo != null) {
            tranFondo.add(tranFondoNueva);
        }

        //---------------------------------------------------------------------------------
        // Genera las alicuotas para cada inmueble del condominio actual
        //---------------------------------------------------------------------------------
        generarAlicuotaInmueble(condominio, listaInm, tipoFac, totalGastos);
        //---------------------------------------------------------------------------------
        // Se buscan las cuotas especiales del condominio con estado diferente de "CANC"  
        // y fecha de inicio (aplicacion) menor o igual al periodo actual
        //---------------------------------------------------------------------------------
        listaCEsp = buscarCuotasEspeciales(condominio);
        //---------------------------------------------------------------------------------
        // Se generan las cxc de cuotas especiales a cada inmueble, de cada propiedad, 
        // en caso de que tengan alguna cuota especial pendiente por aplicar.
        //---------------------------------------------------------------------------------

        Collection<FacCuotaEsp> fce = new ArrayList<FacCuotaEsp>();
        if (!listaCEsp.isEmpty()) {
            for (Iterator<CuotaEsp> it = listaCEsp.iterator();
                    it.hasNext();) {
                CuotaEsp cesp = (CuotaEsp) it.next();
                fce = generarFacCuotaEsp(condominio, listaInm, cesp);
                listaFacCuotaEspNuevas.addAll(fce);
                fce.clear();
            }
        }
        listaCEsp.clear();
        //---------------------------------------------------------------------------------
        // Se buscan las listas de pagos adelantados y de alicuotas pendientes.
        // Se generan los pagos de alicuotas correpondientes
        //---------------------------------------------------------------------------------
        procesarPagosAlicuotas(condominio);
        listaInmPagosAdel.clear();

        //---------------------------------------------------------------------------------
        // Se verifica si existen notas de credito. Si es asi, se usan para pagar, parcial 
        // o totalmente, las cxc de cuotas especiales que tenga el inmueble.
        //---------------------------------------------------------------------------------
        procesarPagosCuotasEspeciales(condominio);
        listaInmFacCuotaEsp.clear();
        //---------------------------------------------------------------------------------
        // Se generan pagos automaticos para las otras cuentas por cobrar en caso de que 
        // existan notas de credito a favor de algun inmueble.
        //---------------------------------------------------------------------------------
        procesarPagosOtrasCxC(condominio);

        //---------------------------------------------------------------------------------
        // Se generan notas de débito por concepto de intereses de mora para cada inmueble 
        // que tenga cxc vencidas - con fecha de emisión menor al período actual
        //---------------------------------------------------------------------------------
        float porMora = 0;

        if (!parametro.getPorcMora().equals(porMora)) {
            generarNotasDebitoPorMora(listaInm, condominio);
        }
        //---------------------------------------------------------------------------------
        // Se genera el reporte preliminar del cierre
        //---------------------------------------------------------------------------------
        generarReporteCierrePreliminar(condominio, gastosComunes, listaAlic, listaFacCuotaEspNuevas, listaPagosCxCNuevos, ajustes);

        listaInmNotasCred.clear();
        listaInmNotasDeb.clear();
        listaInmDeudaPend.clear();
        listaInmFacGNC.clear();
        listaInmAlicuotas.clear();
        listaInmFacCuotaEsp.clear();
        listaNotaCredDebCxC.clear();
        //    listaInmAlicuotasPad.clear();
        listaInm.clear();
    }

    public Fondo buscarFondoReserva(Integer idCond) {

        String nombQuery = "buscarFondoReservaPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        Fondo fondo = null;
        try {
            fondo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fondo;
    }

    public Fondo buscarFondoPrestaciones(Integer idCond) {

        String nombQuery = "buscarFondoPrestacionesPorIdCond";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        Fondo fondo = null;
        try {
            fondo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fondo;
    }

    public Double calcularMtoFondoPrestaciones(Fondo fondoPres, Calendar periodoActual) {

        String nombQuery = "buscarTranFondoPresPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            tranFondo = dataAcces.buscarListaEntidadesPeriodoActual(fondoPres.getId(), periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Double total = new Double(0);

        for (Iterator<TranFondo> it = tranFondo.iterator();
                it.hasNext();) {
            TranFondo tfp = (TranFondo) it.next();
            total = total + tfp.getMtoTranFondo();
        }
        return total;

    }

    public void generarTranFondoReserva(Fondo fondo, Double mtoFondoRes, Calendar periodoActual) {

        // Se genera la tranFondo con estado pendiente 
        tranFondoNueva.setEstadoTranFondo("PEND");
        tranFondoNueva.setMtoTranFondo(mtoFondoRes);
        tranFondoNueva.setSaldoTranFondo(mtoFondoRes);
        tranFondoNueva.setIdPago(0);
        tranFondoNueva.setIdConceptoTranFondo(0);
        tranFondoNueva.setTipoOperTranFondo("Ingreso");
        tranFondoNueva.setDocTranFondo("N/A");
        tranFondoNueva.setPerTranFondo(periodoActual);
        tranFondoNueva.setTipoOperTranFondo("Auto");
        tranFondoNueva.setFondo(fondo);

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        tranFondoNueva.setFecMovTranFondo(fecha);
        // String fechaString = convertirCalendarString(fecha);
        String fechaString = convertirCalendarString(periodoActual);

        tranFondoNueva.setFecTranFondo(fechaString);
        tranFondoNueva.setDescTranFondo("Fdo Res:" + fechaString);

    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public String convertirCalendarString(Calendar fechaMov) {

        String dia = "";
        String mes = "";
        int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
        int mesMov = fechaMov.get(Calendar.MONTH) + 1;
        int añoMov = fechaMov.get(Calendar.YEAR);

        if (diaMov < 10) {
            dia = "0" + diaMov;
        } else {
            dia = String.valueOf(diaMov);
        }

        if (mesMov < 10) {
            mes = "0" + mesMov;
        } else {
            mes = String.valueOf(mesMov);
        }

        String stringFechaMov = dia + "-" + mes + "-" + añoMov;

        return stringFechaMov;
    }

    public Collection buscarGastosComunesOrdPeridoActual(Integer idCondActual, Calendar periodoActual) {

        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarGastosComunesOrdPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaGCOrd = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listaGCOrd;
    }

    public Double totalizarGastosComunesOrd(Collection gastosComunes) {

        Double total = new Double(0);

        for (Iterator<GastoComunOrd> it = gastosComunes.iterator();
                it.hasNext();) {
            GastoComunOrd gco = (GastoComunOrd) it.next();
            total = total + gco.getMtoGastoComunOrd();
        }
        System.out.println("El total de gastos comunes ordinarios es:" + total);
        return total;
    }

    public Collection buscarAjustesPeridoActual(Integer idCondActual, Calendar periodoActual) {

        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarAjustesCondPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaAjustesCond = dataAcces.buscarListaEntidadesPeriodoActual(idCond, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return listaAjustesCond;
    }

    public Double totalizarAjustes(Collection ajustes) {

        Double total = new Double(0);

        for (Iterator<AjustesCond> it = ajustes.iterator();
                it.hasNext();) {
            AjustesCond acond = (AjustesCond) it.next();
            if (acond.tipoAjustesCond.equals("Debito")) {
                total = total - acond.getMtoAjustesCond();
            } else {
                total = total + acond.getMtoAjustesCond();
            }

        }
        System.out.println("El total de ajustes es:" + total);
        return total;
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
                listaInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            for (Iterator<Inmueble> it1 = listaInm.iterator();
                    it1.hasNext();) {
                Inmueble inm = (Inmueble) it1.next();
                inm.setPropiedad(prop);
            }
            lista.addAll(listaInm);
            listaInm.clear();
        }
        return lista;
    }

    public Collection generarAlicuotaInmueble(Condominio condominio, Collection inmuebles, String tipoFac, Double totalGastos) {

        Double mtoFac = new Double(0);
        if (tipoFac.equals("monto")) {
            mtoFac = condominio.getParametro().getMtoFijoFacturacion();
        }
        //-----------------------------------------------------------------
        //    Toma la fecha del servidor y el periodo actual del condominio
        //-----------------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        Calendar perAct = condominio.getPeriodoActual();
        //----------------------------------------------------------------
        // se genera la fecha del  periodo actual como 01mmyyyy (default)
        //----------------------------------------------------------------
        int dia = new Integer(01);
        int mes = perAct.get(Calendar.MONTH);
        int año = perAct.get(Calendar.YEAR);
        Calendar perAlic = new GregorianCalendar(año, mes, dia);
        //----------------------------------------------------------------------
        // Para cada inmueble, de cada propiedad, se crea su alicuota del periodo
        //----------------------------------------------------------------------

        for (Iterator<Inmueble> iter = inmuebles.iterator();
                iter.hasNext();) {
            Inmueble inm = (Inmueble) iter.next();
            Alicuota alicuotaNueva = new Alicuota();
            alicuotaNueva.setFecEmiAlicuota(fecha);

            if (tipoFac.equals("monto")) {
                alicuotaNueva.setMtoAlicuota(mtoFac);
                alicuotaNueva.setSaldoAlicuota(mtoFac);
            } else {
                alicuotaNueva.setMtoAlicuota(totalGastos * inm.getAlicInmueble() / 100);
                alicuotaNueva.setSaldoAlicuota(totalGastos * inm.getAlicInmueble() / 100);
            }

            int mesDes = mes + 1;
            alicuotaNueva.setEdoAlicuota("PEND");
            alicuotaNueva.setDesAlicuota("Alic. periodo:" + "" + mesDes + "-" + año);
            alicuotaNueva.setPeriodoActual(perAlic);
            alicuotaNueva.setInmueble(inm);
            listaAlic.add(alicuotaNueva);
        }
        return listaAlic;
    }

    public Collection buscarCuotasEspeciales(Condominio condominio) {

        Integer idCond = condominio.getId();
        String nombQuery = "buscarCuotasEspecialesPorIdCond";
        DaoQuery<Collection> dao = new DaoQuery<Collection>();
        try {
            listaCEsp = dao.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return listaCEsp;
    }

    public Collection generarFacCuotaEsp(Condominio condominio, Collection inmuebles, CuotaEsp cesp) {

        Double mtoFac = cesp.getMtoTotCuotaEsp() / cesp.getNroMesesCuotaEsp();//mto es el monto del mes de la cuota esp.
        Integer nroInm = new Integer(0);
        Double mtoInm = new Double(0);
        String Fac = cesp.getCalcCuotaEsp();
        if (Fac.equals("TotInmuebles")) {
            nroInm = listaInm.size();
            mtoInm = mtoFac / nroInm;
        }
        String nombConcepto = buscarNombConceptoFCE(cesp.getIdConceptoCuotaEsp());
        //-----------------------------------------------------------------
        //    Toma la fecha del servidor y el periodo actual de beanUsuario
        //-----------------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Calendar perAct = beanUsuario.getPeriodoActual();
        //----------------------------------------------------------------
        // se genera la fecha del  periodo actual como 01mmyyyy (default)
        //----------------------------------------------------------------
        int dia = new Integer(01);
        int mes = perAct.get(Calendar.MONTH);
        int año = perAct.get(Calendar.YEAR);
        Calendar perCEsp = new GregorianCalendar(año, mes, dia);
        //----------------------------------------------------------------------
        // Para cada inmueble, de cada propiedad, se crea su facCuotaEsp
        //----------------------------------------------------------------------

        for (Iterator<Inmueble> iter = inmuebles.iterator();
                iter.hasNext();) {
            Inmueble inm = (Inmueble) iter.next();
            FacCuotaEsp facCuotaEspNueva = new FacCuotaEsp();
            //facCuotaEspNueva.setFecEmiFacCuotaEsp(fecha);
            facCuotaEspNueva.setFecEmiFacCuotaEsp(fecha);

            if (Fac.equals("TotInmuebles")) {
                facCuotaEspNueva.setMtoFacCuotaEsp(mtoInm);
                facCuotaEspNueva.setSaldoFacCuotaEsp(mtoInm);
            } else {
                facCuotaEspNueva.setMtoFacCuotaEsp(mtoFac * inm.getAlicInmueble() / 100);
                facCuotaEspNueva.setSaldoFacCuotaEsp(mtoFac * inm.getAlicInmueble() / 100);
            }

            facCuotaEspNueva.setEdoFacCuotaEsp("PEND");
            facCuotaEspNueva.setDesFacCuotaEsp("Cuota esp. " + "" + nombConcepto);
            facCuotaEspNueva.setPeriodoActual(perCEsp);

            facCuotaEspNueva.setInmueble(inm);
            facCuotaEspNueva.setCuotaEsp(cesp);
            listaFCE.add(facCuotaEspNueva);
        }

        return listaFCE;
    }

    //-------------P R O C E S A R   P A G O S -------------------------
    public void procesarPagosAlicuotas(Condominio condominio) {

        obtenerPagosAdelantadosInm(condominio);

        if (!listaInmPagosAdel.isEmpty()) {
            Inmueble inm;
            PagoAdelantado pagoAdel;
            double uno = 0.01;

            for (Iterator<PagoAdelantado> it = listaInmPagosAdel.iterator();
                    it.hasNext();) {
                pagoAdel = (PagoAdelantado) it.next();
                if (pagoAdel.getSaldoPagoAdelantado() < uno) {
                    continue;
                }
                inm = pagoAdel.getInmueble();
                if (!inm.getId().equals(idInmAnterior)) {
                    obtenerAlicuotasInm(inm);
                }
                for (Iterator<Alicuota> iter = listaInmAlicuotas.iterator();
                        iter.hasNext();) {
                    Alicuota alic = (Alicuota) iter.next();
                    if (!alic.getEdoAlicuota().equals("CANC")) {
                        registrarPagoAlicuota(pagoAdel, inm, alic);
                        listaAlicuotasProcesPad.add(alic);
                        if (pagoAdel.getSaldoPagoAdelantado() < uno) {
                            break;
                        }
                    }
                }
            }
        }
        idInmAnterior = 0;
    }

    public void procesarPagosCuotasEspeciales(Condominio condominio) {

        obtenerNotasCreditoInm(condominio);
        Double uno = new Double(0.01);
        Inmueble inm;
        NotaCredDebCxC notaCredDebCxC;

        for (Iterator<NotaCredDebCxC> it = listaInmNotasCred.iterator();
                it.hasNext();) {
            notaCredDebCxC = (NotaCredDebCxC) it.next();
            if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                continue;
            }
            inm = notaCredDebCxC.getInmueble();
            if (!inm.getId().equals(idInmAnterior)) {
                obtenerInmFacCuotaEsp(inm);
            }
            for (Iterator<FacCuotaEsp> iter = listaInmFacCuotaEsp.iterator();
                    iter.hasNext();) {
                FacCuotaEsp fce = (FacCuotaEsp) iter.next();
                if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                    registrarPagoFacCuotaEsp(notaCredDebCxC, inm, fce);
                    if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                        break;
                    }
                }
            }
        }

        idInmAnterior = 0;
    }

    public void procesarPagosOtrasCxC(Condominio condominio) {

        Inmueble inm;
        NotaCredDebCxC notaCredDebCxC;
        Double uno = 0.01;

        for (Iterator<NotaCredDebCxC> it = listaInmNotasCred.iterator();
                it.hasNext();) {
            notaCredDebCxC = (NotaCredDebCxC) it.next();
            inm = notaCredDebCxC.getInmueble();
            if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                continue;
            }

            if (!inm.getId().equals(idInmAntNd)) {
                obtenerNotasDebitoInm(inm);
            }

            for (Iterator<NotaCredDebCxC> iter = listaInmNotasDeb.iterator();
                    iter.hasNext();) {
                NotaCredDebCxC notaDebito = (NotaCredDebCxC) iter.next();
                if (!notaDebito.getEdoNotaCredDebCxC().equals("CANC")) {
                    registrarPagoNotaDebito(notaCredDebCxC, inm, notaDebito);
                    if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                        break;
                    }
                }
            }

            if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                continue;
            }
            if (!inm.getId().equals(idInmAntDp)) {
                obtenerInmDeudaPend(inm);
            }

            for (Iterator<DeudaPendCxC> iter = listaInmDeudaPend.iterator();
                    iter.hasNext();) {
                DeudaPendCxC dpend = (DeudaPendCxC) iter.next();
                if (!dpend.getEdoDeudaPendCxC().equals("CANC")) {
                    registrarPagoDeudaPend(notaCredDebCxC, inm, dpend);
                    if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                        break;
                    }
                }
            }

            if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                continue;
            }
            if (!inm.getId().equals(idInmAntNc)) {
                obtenerInmFacGNC(inm);
            }

            for (Iterator<FacGastoNoComun> iter = listaInmFacGNC.iterator();
                    iter.hasNext();) {
                FacGastoNoComun gnc = (FacGastoNoComun) iter.next();
                if (gnc.getEdoFacGastoNoComun().equals("PEND") | gnc.getEdoFacGastoNoComun().equals("EPRO")) {
                    registrarPagoFacGNC(notaCredDebCxC, inm, gnc);
                    if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                        break;
                    }
                }
            }

            if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                continue;
            }
            if (!inm.getId().equals(idInmAntAl)) {
                obtenerAlicuotasInmOtrasCxC(inm);
            }

            for (Iterator<Alicuota> iter = listaInmAlicuotas.iterator();
                    iter.hasNext();) {
                Alicuota alic = (Alicuota) iter.next();
                if (!alic.getEdoAlicuota().equals("CANC")) {
                    registrarPagoAlicuota(notaCredDebCxC, inm, alic);
                    if (notaCredDebCxC.getSaldoNotaCredDebCxC() < uno) {
                        break;
                    }
                }
            }
        }
    }

    public void obtenerPagosAdelantadosInm(Condominio condominio) {

        Collection<PagoAdelantado> lista = new ArrayList<PagoAdelantado>();

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad pro = (Propiedad) it.next();
            Integer idProp = pro.getId();
            String nombQuery = "buscarInmueblesConPagoAdel";
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                lista = dataAcces.buscarListaFetchPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            for (Iterator<PagoAdelantado> it1 = lista.iterator();
                    it1.hasNext();) {
                PagoAdelantado pAdel = (PagoAdelantado) it1.next();
                pAdel.getInmueble().setPropiedad(pro);
            }
            listaInmPagosAdel.addAll(lista);
            lista.clear();
        }
        // ----------------------- Sort  de los pagos adelantados 31-10-2025 -----------------------------

        List<PagoAdelantado> list = new ArrayList<>(listaInmPagosAdel);
        Collections.sort(list, new ComparadorFechaPagoAdel());
        listaInmPagosAdel.clear();
        listaInmPagosAdel.addAll(list);
        list.clear();

    }

    public void obtenerAlicuotasInm(Inmueble inmueble) {

        listaInmAlicuotas.clear();

        Collection<Alicuota> lista = new ArrayList<Alicuota>();

        String nombQuery = "buscarAlicuotasInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmAlicuotas.addAll(lista);
        agregarAlicuotaGenerada(inmueble);//agrega a la lista de alicuotas la alicuota recien generada

        List<Alicuota> list = new ArrayList<>(listaInmAlicuotas);
        Collections.sort(list, new ComparadorFecAlicuota());
        listaInmAlicuotas.clear();
        listaInmAlicuotas.addAll(list);
        list.clear();
        //------------------------------------------------------------------------------------

        for (Alicuota a : listaInmAlicuotas) {
            System.out.println("Alicuotas " + a.getId() + "" + a.getFecEmiAlicuota().getTime());
        }
        lista.clear();
        idInmAnterior = inmueble.getId();
    }

    public void obtenerAlicuotasInmEnMora(Inmueble inmueble) {

        listaInmAlicuotas.clear();

        String nombQuery = "buscarAlicuotasInmEnMora";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaInmAlicuotas = dataAcces.buscarListaEntidadesPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void obtenerDeudaPendInmEnMora(Inmueble inmueble) {

        listaInmDeudaPend.clear();

        String nombQuery = "buscarDeudasPendInmEnMora";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaInmDeudaPend = dataAcces.buscarListaEntidadesPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void obtenerFacCuotaEspInmEnMora(Inmueble inmueble) {

        listaInmFacCuotaEsp.clear();

        String nombQuery = "buscarFacCuotaEspInmEnMora";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaInmFacCuotaEsp = dataAcces.buscarListaEntidadesPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void obtenerNotasDebitoCxCInmEnMora(Inmueble inmueble) throws ParseException {

        listaInmNotasDeb.clear();
        Collection<NotaCredDebCxC> listaNCD = new ArrayList<NotaCredDebCxC>();
        NotaCredDebCxC ncd;
        Calendar cal;

        String nombQuery = "buscarNotasDebitoCxCInmEnMora";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaNCD = dataAcces.buscarListaEntidadesPorId(inmueble.getId(), nombQuery); // En realidad, trae aquellas ncd en mora
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<NotaCredDebCxC> iter1 = listaNCD.iterator();
                iter1.hasNext();) {
            ncd = (NotaCredDebCxC) iter1.next();
            cal = convertirFecha(ncd.getFecNotaCredDebCxC());
            if (cal.before(beanUsuario.getPeriodoActual())) {
                listaInmNotasDeb.add(ncd);
            }
        }
        return;
    }

    public void obtenerFacGastoNoComunInmEnMora(Inmueble inmueble) {

        listaInmFacGNC.clear();

        String nombQuery = "buscarFacGastoNoComunInmEnMora";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaInmFacGNC = dataAcces.buscarListaEntidadesPorId(inmueble.getId(), nombQuery); // En realidad, trae aquellas dpend en mora
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void obtenerAlicuotasInmOtrasCxC(Inmueble inmueble) {

        listaInmAlicuotas.clear();

        Collection<Alicuota> lista = new ArrayList<Alicuota>();
        Collection<Alicuota> lista1 = new ArrayList<Alicuota>();

        String nombQuery = "buscarAlicuotasInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //------------------------------------------------------------------------------------------------------
        // Se buscan, en la lista de alicuotas procesadas con pagos adelantados, las alicuotas obtenidas (lista)
        // si se encuentra alguna, se selecciona para ser procesada por las notas de credito
        //-----------------------------------------------------------------------------------------------------

        Boolean encontro = false;
        for (Iterator<Alicuota> it = lista.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();

            for (Iterator<Alicuota> it1 = listaAlicuotasProcesPad.iterator();
                    it1.hasNext();) {
                Alicuota alic1 = (Alicuota) it1.next();
                if (alic.getId().equals(alic1.getId())) {
                    lista1.add(alic1);
                    encontro = true;
                    break;
                }
            }
            if (encontro) {
                encontro = false;
                continue;
            } else {
                lista1.add(alic);
            }
            encontro = false;
        }

        listaInmAlicuotas.addAll(lista1);
        agregarAlicuotaGenerada(inmueble);//agrega a la lista de alicuotas la alicuota recien generada

        List<Alicuota> list = new ArrayList<>(listaInmAlicuotas);
        Collections.sort(list, new ComparadorFecAlicuota());
        listaInmAlicuotas.clear();
        listaInmAlicuotas.addAll(list);
        list.clear();
        //------------------------------------------------------------------------------------
        System.out.println("ALICUOTAS OTRAS CUENTAS POR COBRAR: ");
        for (Alicuota a : listaInmAlicuotas) {
            System.out.println("Alicuotas " + a.getId() + "" + a.getFecEmiAlicuota().getTime());
        }
        list.clear();
        lista.clear();
        idInmAntAl = inmueble.getId();

    }

    public void agregarAlicuotaGenerada(Inmueble inmueble) {

        for (Iterator<Alicuota> it = listaAlic.iterator();
                it.hasNext();) {
            Alicuota alicGen = (Alicuota) it.next();
            if (alicGen.getInmueble().getId().equals(inmueble.getId())) {
                listaInmAlicuotas.add(alicGen);
            }
        }
    }

    public void obtenerInmDeudaPend(Inmueble inm) {

        listaInmDeudaPend.clear();

        Collection<DeudaPendCxC> lista = new ArrayList<DeudaPendCxC>();

        String nombQuery = "buscarDeudasPendInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmDeudaPend.addAll(lista);

        List<DeudaPendCxC> list = new ArrayList<>(listaInmDeudaPend);
        Collections.sort(list, new ComparadorFechaDeudaPend());
        listaInmDeudaPend.clear();
        listaInmDeudaPend.addAll(list);

        lista.clear();
        list.clear();
        idInmAntDp = inm.getId();
        //-------------------------------------------------------------------------------
        System.out.println("DEUDAS PENDIENTES: ");
        for (DeudaPendCxC a : listaInmDeudaPend) {
            System.out.println("DEUDAS PENDIENTES: " + a.getFecDeudaPendCxC().getTime());
        }
    }

    public void obtenerInmFacGNC(Inmueble inm) {

        listaInmFacGNC.clear();

        Collection<FacGastoNoComun> lista = new ArrayList<FacGastoNoComun>();

        String nombQuery = "buscarFacGastosNoComunesInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmFacGNC.addAll(lista);

        List<FacGastoNoComun> list = new ArrayList<>(listaInmFacGNC);
        Collections.sort(list, new ComparadorFechaGNC());
        listaInmFacGNC.clear();
        listaInmFacGNC.addAll(list);
        lista.clear();
        list.clear();
        idInmAntNc = inm.getId();
        //-----------------------------------------------------------------------------
        System.out.println("FACTURAS GASTO NO COMUN: ");
        for (FacGastoNoComun a : listaInmFacGNC) {
            System.out.println("GASTO NO COMUN: " + a.getFecFacGNC().getTime());
        }
    }

    public void registrarPagoAlicuota(PagoAdelantado pagoAdel, Inmueble inm, Alicuota alic) throws ValidatorException {

        Double cero = new Double(0);
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago alic. con pago adelantado");
        pagoCxCNuevo.setNumDocPagoCxC("AUTO/PAD");

        if (pagoAdel.getSaldoPagoAdelantado() > alic.getSaldoAlicuota() | pagoAdel.getSaldoPagoAdelantado().equals(alic.getSaldoAlicuota())) {
            pagoCxCNuevo.setMtoPagoCxC(alic.getSaldoAlicuota());
            alic.setEdoAlicuota("CANC");
            pagoAdel.setSaldoPagoAdelantado(pagoAdel.getSaldoPagoAdelantado() - alic.getSaldoAlicuota());
            alic.setSaldoAlicuota(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(pagoAdel.getSaldoPagoAdelantado());
            alic.setSaldoAlicuota(alic.getSaldoAlicuota() - pagoAdel.getSaldoPagoAdelantado());
            if (alic.getSaldoAlicuota() < 0.01) {
                alic.setEdoAlicuota("CANC");
                alic.setSaldoAlicuota(cero);
            } else {
                alic.setEdoAlicuota("EPRO");
            }
            pagoAdel.setSaldoPagoAdelantado(new Double(0));
        }

        pagoCxCNuevo.setFormaPagoCxC("PAD");

        //--------------------------------------------------------------
        // Toma la fecha del servidor la asigna a la fecha del mov.
        // luego la transforma a string para pasarla a la fecha del doc.
        //--------------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);
        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());

        pagoCxCNuevo.setInmueble(inm);

        listaPagosCxCNuevos.add(pagoCxCNuevo);

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, alic);
        listaPagoFacCxC.add(pagoFacCxC);
    }

    public PagoFacCxC generarPagoFacCxC(PagoCxC pagoCxC, Alicuota alic) throws ValidatorException {

        PagoFacCxC pagoFac = new PagoFacCxC();
        pagoFac.setPagoCxC(pagoCxC);
        //-------------------------------------------------------------------
        // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
        //-------------------------------------------------------------------
        pagoFac.setDiscFacCxC("ALI");
        pagoFac.setIdFacCxC(alic.getId());
        pagoFac.setMtoPagadoCxC(pagoCxC.getMtoPagoCxC());

        return pagoFac;
    }

    public void obtenerNotasCreditoInm(Condominio condominio) {

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad pro = (Propiedad) it.next();
            Integer idProp = pro.getId();
            String nombQuery = "buscarInmueblesConNotasCred";
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                lista = dataAcces.buscarListaFetchPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            for (Iterator<NotaCredDebCxC> it1 = lista.iterator();
                    it1.hasNext();) {
                NotaCredDebCxC nota = (NotaCredDebCxC) it1.next();
                nota.getInmueble().setPropiedad(pro);
            }
            listaInmNotasCred.addAll(lista);
            lista.clear();
        }
        //-------------------------Sort de las notas de credito 31-10-2025 --------------------

        List<NotaCredDebCxC> list = new ArrayList<>(listaInmNotasCred);
        Collections.sort(list, new ComparadorFechaNDC());
        listaInmNotasCred.clear();
        listaInmNotasCred.addAll(list);
        list.clear();
for (NotaCredDebCxC a : listaInmNotasCred) {
            System.out.println("Notas de credito " + a.getId() + "" + a.getFecNotaCredDebCxC());
        }
    }

    public void obtenerNotasDebitoInm(Inmueble inm) {

        listaInmNotasDeb.clear();

        Collection<NotaCredDebCxC> lista = new ArrayList<NotaCredDebCxC>();

        String nombQuery = "buscarInmueblesConNotasDeb";  
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inm.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmNotasDeb.addAll(lista);
        //-------------------------------- Sort notas de debito 31-10-2025 ----------------
        List<NotaCredDebCxC> list = new ArrayList<>(listaInmNotasDeb);
        Collections.sort(list, new ComparadorFechaNDC());
        listaInmNotasDeb.clear();
        listaInmNotasDeb.addAll(list);
        list.clear();
        lista.clear();
        
        idInmAntNd = inm.getId();
        //------------------------------------------------------------------------------------
        System.out.println("NOTAS DE DEBITO: ");
        for (NotaCredDebCxC a : listaInmNotasDeb) {
            System.out.println("NOTAS DE DEBITO: " + a.getFecNotaCredDebCxC());
        }
    }

    public void obtenerInmFacCuotaEsp(Inmueble inmueble) {

        listaInmFacCuotaEsp.clear();

        Collection<FacCuotaEsp> lista = new ArrayList<FacCuotaEsp>();

        String nombQuery = "buscarFacCuotaEspInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmFacCuotaEsp.addAll(lista);
        agregarFacCuotaEspGenerada(inmueble);
        //---------------------------sort------------------------------------------
        List<FacCuotaEsp> list = new ArrayList<>(listaInmFacCuotaEsp);
        Collections.sort(list, new ComparadorFechaFCE());
        listaInmFacCuotaEsp.clear();
        listaInmFacCuotaEsp.addAll(list);
        //---------------------------------------------------------------------------
        for (FacCuotaEsp a : listaInmFacCuotaEsp) {
            System.out.println("FCE " + a.getId() + "" + a.getFecEmiFacCuotaEsp().getTime());
        }

        list.clear();
        lista.clear();
        idInmAnterior = inmueble.getId();
    }

    public void obtenerInmFacCuotaEspOtrasCxC(Inmueble inmueble) {

        listaInmFacCuotaEsp.clear();

        Collection<FacCuotaEsp> lista = new ArrayList<FacCuotaEsp>();

        String nombQuery = "buscarFacCuotaEspInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaFetchPorId(inmueble.getId(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        listaInmFacCuotaEsp.addAll(lista);
        agregarFacCuotaEspGenerada(inmueble);
        lista.clear();
        idInmAntCe = inmueble.getId();
    }

    public void agregarFacCuotaEspGenerada(Inmueble inmueble) {

        for (Iterator<FacCuotaEsp> it = listaFacCuotaEspNuevas.iterator();
                it.hasNext();) {
            FacCuotaEsp fcen = (FacCuotaEsp) it.next();
            if (fcen.getInmueble().getId().equals(inmueble.getId())) {
                listaInmFacCuotaEsp.add(fcen);
            }
        }
    }

    public void registrarPagoFacCuotaEsp(NotaCredDebCxC notaCred, Inmueble inm, FacCuotaEsp fce) throws ValidatorException {

        Double cero = new Double(0);
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago cuota esp. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > fce.getSaldoFacCuotaEsp() | notaCred.getSaldoNotaCredDebCxC().equals(fce.getSaldoFacCuotaEsp())) {
            pagoCxCNuevo.setMtoPagoCxC(fce.getSaldoFacCuotaEsp());
            fce.setEdoFacCuotaEsp("CANC");
            notaCred.setSaldoNotaCredDebCxC(notaCred.getSaldoNotaCredDebCxC() - fce.getSaldoFacCuotaEsp());
            fce.setSaldoFacCuotaEsp(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
            fce.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp() - notaCred.getSaldoNotaCredDebCxC());
            if (fce.getSaldoFacCuotaEsp() < 0.01) {
                fce.setEdoFacCuotaEsp("CANC");
                fce.setSaldoFacCuotaEsp(cero);
            } else {
                fce.setEdoFacCuotaEsp("EPRO");
            }
            notaCred.setSaldoNotaCredDebCxC(new Double(0));
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);

        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());
        pagoCxCNuevo.setInmueble(inm);
        listaPagosCxCNuevos.add(pagoCxCNuevo);

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, fce);
        listaPagoFacCxC.add(pagoFacCxC);
    }

    public PagoFacCxC generarPagoFacCxC(PagoCxC pagoCxC, FacCuotaEsp fce) throws ValidatorException {

        PagoFacCxC pagoFac = new PagoFacCxC();
        pagoFac.setPagoCxC(pagoCxC);
        //-------------------------------------------------------------------
        // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
        //-------------------------------------------------------------------
        pagoFac.setDiscFacCxC("CES");
        pagoFac.setIdFacCxC(fce.getId());
        pagoFac.setMtoPagadoCxC(pagoCxC.getMtoPagoCxC());

        return pagoFac;
    }

    public void registrarPagoNotaDebito(NotaCredDebCxC notaCred, Inmueble inm, NotaCredDebCxC notaDebito) throws ValidatorException {

        Double cero = new Double(0);
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago nota deb. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > notaDebito.getSaldoNotaCredDebCxC() | notaCred.getSaldoNotaCredDebCxC().equals(notaDebito.getSaldoNotaCredDebCxC())) {
            pagoCxCNuevo.setMtoPagoCxC(notaDebito.getSaldoNotaCredDebCxC());
            notaDebito.setEdoNotaCredDebCxC("CANC");
            notaCred.setSaldoNotaCredDebCxC(notaCred.getSaldoNotaCredDebCxC() - notaDebito.getSaldoNotaCredDebCxC());
            notaDebito.setSaldoNotaCredDebCxC(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
            notaDebito.setSaldoNotaCredDebCxC(notaDebito.getSaldoNotaCredDebCxC() - notaCred.getSaldoNotaCredDebCxC());
            if (notaDebito.getSaldoNotaCredDebCxC() < 0.01) {
                notaDebito.setEdoNotaCredDebCxC("CANC");
                notaDebito.setSaldoNotaCredDebCxC(cero);
            } else {
                notaDebito.setEdoNotaCredDebCxC("EPRO");
            }
            notaCred.setSaldoNotaCredDebCxC(cero);
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);
        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());
        pagoCxCNuevo.setInmueble(inm);
        listaPagosCxCNuevos.add(pagoCxCNuevo);

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, notaDebito);
        listaPagoFacCxC.add(pagoFacCxC);
    }

    public PagoFacCxC generarPagoFacCxC(PagoCxC pagoCxC, NotaCredDebCxC notaDebito) throws ValidatorException {

        PagoFacCxC pagoFac = new PagoFacCxC();
        pagoFac.setPagoCxC(pagoCxC);
        //-------------------------------------------------------------------
        // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
        //-------------------------------------------------------------------
        pagoFac.setDiscFacCxC("NDE");
        pagoFac.setIdFacCxC(notaDebito.getId());
        pagoFac.setMtoPagadoCxC(pagoCxC.getMtoPagoCxC());

        return pagoFac;
    }

    public void registrarPagoDeudaPend(NotaCredDebCxC notaCred, Inmueble inm, DeudaPendCxC dpend) throws ValidatorException {

        Double cero = new Double(0);
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago deuda pend. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > dpend.getSaldoDeudaPendCxC() | notaCred.getSaldoNotaCredDebCxC().equals(dpend.getSaldoDeudaPendCxC())) {
            pagoCxCNuevo.setMtoPagoCxC(dpend.getSaldoDeudaPendCxC());
            dpend.setEdoDeudaPendCxC("CANC");
            notaCred.setSaldoNotaCredDebCxC(notaCred.getSaldoNotaCredDebCxC() - dpend.getSaldoDeudaPendCxC());
            dpend.setSaldoDeudaPendCxC(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
            dpend.setSaldoDeudaPendCxC(dpend.getSaldoDeudaPendCxC() - notaCred.getSaldoNotaCredDebCxC());
            if (dpend.getSaldoDeudaPendCxC() < 0.01) {
                dpend.setEdoDeudaPendCxC("CANC");
                dpend.setSaldoDeudaPendCxC(cero);
            } else {
                dpend.setEdoDeudaPendCxC("EPRO");
            }
            notaCred.setSaldoNotaCredDebCxC(cero);
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();

        pagoCxCNuevo.setFecMovPagoCxC(fecha);
        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC(
                "N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());

        pagoCxCNuevo.setInmueble(inm);

        listaPagosCxCNuevos.add(pagoCxCNuevo);
        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, dpend);

        listaPagoFacCxC.add(pagoFacCxC);
    }

    public PagoFacCxC generarPagoFacCxC(PagoCxC pagoCxC, DeudaPendCxC dpend) throws ValidatorException {

        PagoFacCxC pagoFac = new PagoFacCxC();
        pagoFac.setPagoCxC(pagoCxC);
        //-------------------------------------------------------------------
        // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
        //-------------------------------------------------------------------
        pagoFac.setDiscFacCxC("DPE");
        pagoFac.setIdFacCxC(dpend.getId());
        pagoFac.setMtoPagadoCxC(pagoCxC.getMtoPagoCxC());

        return pagoFac;
    }

    public void registrarPagoFacGNC(NotaCredDebCxC notaCred, Inmueble inm, FacGastoNoComun fgnc) throws ValidatorException {

        Double cero = new Double(0);
        Double ceroLogico = 0.01;
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago gasto no comun. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > fgnc.getSaldoFacGastoNoComun() | notaCred.getSaldoNotaCredDebCxC().equals(fgnc.getSaldoFacGastoNoComun())) {
            pagoCxCNuevo.setMtoPagoCxC(fgnc.getSaldoFacGastoNoComun());
            fgnc.setEdoFacGastoNoComun("CANC");
            notaCred.setSaldoNotaCredDebCxC(notaCred.getSaldoNotaCredDebCxC() - fgnc.getSaldoFacGastoNoComun());
            fgnc.setSaldoFacGastoNoComun(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
            fgnc.setSaldoFacGastoNoComun(fgnc.getSaldoFacGastoNoComun() - notaCred.getSaldoNotaCredDebCxC());
            if (fgnc.getSaldoFacGastoNoComun() < ceroLogico) {
                fgnc.setEdoFacGastoNoComun("CANC");
                fgnc.setSaldoFacGastoNoComun(cero);
            } else {
                fgnc.setEdoFacGastoNoComun("EPRO");
            }
            notaCred.setSaldoNotaCredDebCxC(cero);
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);
        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());
        pagoCxCNuevo.setInmueble(inm);
        listaPagosCxCNuevos.add(pagoCxCNuevo);

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, fgnc);
        listaPagoFacCxC.add(pagoFacCxC);

    }

    public PagoFacCxC generarPagoFacCxC(PagoCxC pagoCxC, FacGastoNoComun fgnc) throws ValidatorException {

        PagoFacCxC pagoFac = new PagoFacCxC();
        pagoFac.setPagoCxC(pagoCxC);
        //-------------------------------------------------------------------
        // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
        //-------------------------------------------------------------------
        pagoFac.setDiscFacCxC("GNC");
        pagoFac.setIdFacCxC(fgnc.getId());
        pagoFac.setMtoPagadoCxC(pagoCxC.getMtoPagoCxC());

        return pagoFac;
    }

    public void registrarPagoAlicuota(NotaCredDebCxC notaCred, Inmueble inm, Alicuota alic) throws ValidatorException {

        Double cero = new Double(0);
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago alic. con nota cred.");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > alic.getSaldoAlicuota() | notaCred.getSaldoNotaCredDebCxC().equals(alic.getSaldoAlicuota())) {
            pagoCxCNuevo.setMtoPagoCxC(alic.getSaldoAlicuota());
            alic.setEdoAlicuota("CANC");
            notaCred.setSaldoNotaCredDebCxC(notaCred.getSaldoNotaCredDebCxC() - alic.getSaldoAlicuota());
            alic.setSaldoAlicuota(cero);
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
            alic.setSaldoAlicuota(alic.getSaldoAlicuota() - notaCred.getSaldoNotaCredDebCxC());
            if (alic.getSaldoAlicuota() < 0.01) {
                alic.setEdoAlicuota("CANC");
                alic.setSaldoAlicuota(cero);
            } else {
                alic.setEdoAlicuota("EPRO");
            }
            notaCred.setSaldoNotaCredDebCxC(new Double(0));
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);
        fecha = fechaCorte;

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());

        pagoCxCNuevo.setInmueble(inm);
        listaPagosCxCNuevos.add(pagoCxCNuevo);

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxCNuevo, alic);
        listaPagoFacCxC.add(pagoFacCxC);
    }

    public Calendar generarFechaCorte(Condominio condominio) {

        Calendar perAct = condominio.getPeriodoActual();

        int mesPerAct = perAct.get(Calendar.MONTH);
        int añoPerAct = perAct.get(Calendar.YEAR);

        int mesPerSig = mesPerAct + 1;
        int añoPerSig = añoPerAct;

        if (mesPerSig > 12) {
            mesPerSig = 1;
            añoPerSig = añoPerSig + 1;

        }

        int diaPerSig = 01;
        Calendar fecha = Calendar.getInstance();
        fecha.set(añoPerSig, mesPerSig, diaPerSig);

        return fecha;
    }

    public void generarNotasDebitoPorMora(Collection listaInm, Condominio cond) throws ParseException {

        Alicuota alic;
        DeudaPendCxC dpend;
        FacCuotaEsp fce;
        NotaCredDebCxC ncd;
        FacGastoNoComun fgnc;
        listaNotaCredDebCxC.clear();

        nroNota = parametro.getProxNotaCredCxC();

        for (Iterator<Inmueble> iter = listaInm.iterator();
                iter.hasNext();) {
            Inmueble inm = (Inmueble) iter.next();
            obtenerAlicuotasInmEnMora(inm);
            for (Iterator<Alicuota> iter1 = listaInmAlicuotas.iterator();
                    iter1.hasNext();) {
                alic = (Alicuota) iter1.next();
                if (alic.getPeriodoActual().before(periodoActual)) {
                    actualizarAlicuotaConPagosAuto(alic, inm);
                }
            }
            obtenerDeudaPendInmEnMora(inm);
            for (Iterator<DeudaPendCxC> iter1 = listaInmDeudaPend.iterator();
                    iter1.hasNext();) {
                dpend = (DeudaPendCxC) iter1.next();
                if (dpend.getFecDeudaPendCxC().before(fechaCorte)) {
                    actualizarDeudaPendConPagosAuto(dpend, inm);
                }
            }
            obtenerFacCuotaEspInmEnMora(inm);
            for (Iterator<FacCuotaEsp> iter1 = listaInmFacCuotaEsp.iterator();
                    iter1.hasNext();) {
                fce = (FacCuotaEsp) iter1.next();
                if (fce.getPeriodoActual().before(periodoActual)) {
                    actualizarFacCuotaEspConPagosAuto(fce, inm);
                }
            }
            obtenerNotasDebitoCxCInmEnMora(inm);
            for (Iterator<NotaCredDebCxC> iter1 = listaInmNotasDeb.iterator();
                    iter1.hasNext();) {
                ncd = (NotaCredDebCxC) iter1.next();
                if (convertirFecha(ncd.getFecNotaCredDebCxC()).before(fechaCorte)) {
                    actualizarNotaCredDebCxCConPagosAuto(ncd, inm);
                }
            }
            obtenerFacGastoNoComunInmEnMora(inm);
            for (Iterator<FacGastoNoComun> iter1 = listaInmFacGNC.iterator();
                    iter1.hasNext();) {
                fgnc = (FacGastoNoComun) iter1.next();
                if (fgnc.getFecFacGNC().before(fechaCorte)) {
                    actualizarFacGastoNoComunConPagosAuto(fgnc, inm);
                }
            }
            generarNotaDebitoMoraCxCPagadas(inm, cond);
        }
    }

    public void actualizarAlicuotaConPagosAuto(Alicuota alicuota, Inmueble inmueble) {

        PagoFacCxC pagoFac = null;

        for (Iterator<PagoFacCxC> iter2 = listaPagoFacCxC.iterator();
                iter2.hasNext();) {
            pagoFac = (PagoFacCxC) iter2.next();
            if (pagoFac.getIdFacCxC() == null) {
                continue;
            }
            if (pagoFac.getDiscFacCxC().equals("ALI") && pagoFac.getIdFacCxC().equals(alicuota.getId())) {
                if (alicuota.getSaldoAlicuota() - pagoFac.getMtoPagadoCxC() < 0.01) {
                    alicuota.setSaldoAlicuota(0.00);
                    alicuota.setEdoAlicuota("CANC");
                } else {
                    alicuota.setSaldoAlicuota(alicuota.getSaldoAlicuota() - pagoFac.getMtoPagadoCxC());
                    alicuota.setEdoAlicuota("EPRO");
                }
                break;
            }
        }
        if (alicuota.getEdoAlicuota().equals("CANC")) {
            cxcCancel = true;
            generarNotaDebitoAlicuota(alicuota, inmueble, fechaCorte, pagoFac);
            cxcCancel = false;
        } else {
            generarNotaDebitoAlicuota(alicuota, inmueble, fechaCorte, null);
        }
        return;
    }

    public void actualizarDeudaPendConPagosAuto(DeudaPendCxC deudaPend, Inmueble inmueble) {

        PagoFacCxC pagoFac = null;

        for (Iterator<PagoFacCxC> iter2 = listaPagoFacCxC.iterator();
                iter2.hasNext();) {
            pagoFac = (PagoFacCxC) iter2.next();
            if (pagoFac.getDiscFacCxC().equals("DPE") && pagoFac.getIdFacCxC().equals(deudaPend.getId())) {
                if (deudaPend.getSaldoDeudaPendCxC() - pagoFac.getMtoPagadoCxC() < 0.01) {
                    deudaPend.setSaldoDeudaPendCxC(0.00);
                    deudaPend.setEdoDeudaPendCxC("CANC");
                } else {
                    deudaPend.setSaldoDeudaPendCxC(deudaPend.getSaldoDeudaPendCxC() - pagoFac.getMtoPagadoCxC());
                    deudaPend.setEdoDeudaPendCxC("EPRO");
                }
                break;
            }
        }
        if (deudaPend.getEdoDeudaPendCxC().equals("CANC")) {
            cxcCancel = true;
            generarNotaDebitoDeudaPend(deudaPend, inmueble, fechaCorte, pagoFac);
            cxcCancel = false;
        } else {
            generarNotaDebitoDeudaPend(deudaPend, inmueble, fechaCorte, null);
        }
        return;
    }

    public void actualizarFacCuotaEspConPagosAuto(FacCuotaEsp fce, Inmueble inmueble) {

        PagoFacCxC pagoFac = null;

        for (Iterator<PagoFacCxC> iter2 = listaPagoFacCxC.iterator();
                iter2.hasNext();) {
            pagoFac = (PagoFacCxC) iter2.next();
            if (pagoFac.getIdFacCxC() == null) {
                continue;
            }
            if (pagoFac.getDiscFacCxC().equals("CES") && pagoFac.getIdFacCxC().equals(fce.getId())) {
                if (fce.getSaldoFacCuotaEsp() - pagoFac.getMtoPagadoCxC() < 0.01) {
                    fce.setSaldoFacCuotaEsp(0.00);
                    fce.setEdoFacCuotaEsp("CANC");
                } else {
                    fce.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp() - pagoFac.getMtoPagadoCxC());
                    fce.setEdoFacCuotaEsp("EPRO");
                }
                break;
            }
        }
        if (fce.getEdoFacCuotaEsp().equals("CANC")) {
            cxcCancel = true;
            generarNotaDebitoFacturaCuotaEsp(fce, inmueble, fechaCorte, pagoFac);
            cxcCancel = false;
        } else {
            generarNotaDebitoFacturaCuotaEsp(fce, inmueble, fechaCorte, null);
        }

        return;
    }

    public void actualizarNotaCredDebCxCConPagosAuto(NotaCredDebCxC ncd, Inmueble inmueble) throws ParseException {

        PagoFacCxC pagoFac = null;

        for (Iterator<PagoFacCxC> iter2 = listaPagoFacCxC.iterator();
                iter2.hasNext();) {
            pagoFac = (PagoFacCxC) iter2.next();
            if (pagoFac.getDiscFacCxC().equals("NCD") && pagoFac.getIdFacCxC().equals(ncd.getId())) {
                if (ncd.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC() < 0.01) {
                    ncd.setSaldoNotaCredDebCxC(0.00);
                    ncd.setEdoNotaCredDebCxC("CANC");
                } else {
                    ncd.setSaldoNotaCredDebCxC(ncd.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC());
                    ncd.setEdoNotaCredDebCxC("EPRO");
                }
                break;
            }
        }

        if (ncd.getEdoNotaCredDebCxC().equals("CANC")) {
            cxcCancel = true;
            generarNotaDebitoNotaDebitoCxC(ncd, inmueble, fechaCorte, pagoFac);
            cxcCancel = false;
        } else {
            generarNotaDebitoNotaDebitoCxC(ncd, inmueble, fechaCorte, null);
        }

        return;
    }

    public void actualizarFacGastoNoComunConPagosAuto(FacGastoNoComun fgnc, Inmueble inmueble) {

        PagoFacCxC pagoFac = null;

        for (Iterator<PagoFacCxC> iter2 = listaPagoFacCxC.iterator();
                iter2.hasNext();) {
            pagoFac = (PagoFacCxC) iter2.next();
            if (pagoFac.getDiscFacCxC().equals("GNC") && pagoFac.getIdFacCxC().equals(fgnc.getId())) {
                if (fgnc.getSaldoFacGastoNoComun() - pagoFac.getMtoPagadoCxC() < 0.01) {
                    fgnc.setSaldoFacGastoNoComun(0.00);
                    fgnc.setEdoFacGastoNoComun("CANC");
                } else {
                    fgnc.setSaldoFacGastoNoComun(fgnc.getSaldoFacGastoNoComun() - pagoFac.getMtoPagadoCxC());
                    fgnc.setEdoFacGastoNoComun("EPRO");
                }
                break;
            }
        }
        cxcCancel = false;
        if (fgnc.getEdoFacGastoNoComun().equals("CANC")) {
            if (pagoFac == null) {
                generarNotaDebitoFacGastoNoComun(fgnc, inmueble, fechaCorte, null);
                return;
            }
            cxcCancel = true;
            generarNotaDebitoFacGastoNoComun(fgnc, inmueble, fechaCorte, pagoFac);
        } else {
            generarNotaDebitoFacGastoNoComun(fgnc, inmueble, fechaCorte, null);
        }
        return;
    }

    public int calcularNumDiasEntreFechas(Calendar fechaRef, Calendar fecVen) {

        //Calendar c = Calendar.getInstance();
        long numDiasRango = (fechaRef.getTime().getTime() - fecVen.getTime().getTime()) / (1000 * 60 * 60 * 24);
        //  c.setTimeInMillis(fechaRef.getTime().getTime() - fecVen.getTime().getTime());
        //  return c.get(Calendar.DAY_OF_YEAR);
        return (int) numDiasRango;
    }

    public Calendar calcularFechaVencimiento(Calendar fechaCxC) {

        Calendar c1 = GregorianCalendar.getInstance();

        Date fechaDate = fechaCxC.getTime();
        c1.setTime(fechaDate);
        c1.add(Calendar.DATE, parametro.getNumDiasVenCxC());

        return c1;
    }

    public NotaCredDebCxC crearNotaDebito() {

        NotaCredDebCxC notaCreDebCxCNueva = new NotaCredDebCxC();

        notaCreDebCxCNueva.setEdoNotaCredDebCxC("PEND");

        notaCreDebCxCNueva.setIdConcepto(0);
        notaCreDebCxCNueva.setTipoNotaCredDebCxC("Debito");
        notaCreDebCxCNueva.setTipoOperNotaCredDebCxC("AUTO");

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        notaCreDebCxCNueva.setPerNotaCredDebCxC(usuarioActual.getPeriodoActual());
        notaCreDebCxCNueva.setFecNotaCredDebCxC(convertirCalendarString(usuarioActual.getPeriodoActual()));

        return notaCreDebCxCNueva;
    }

    public Alicuota buscarAlicuota(Integer idFac) {

        Alicuota alic;
        String nombQuery = "buscarAlicuotaPorId";

        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            alic = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return alic;
    }

    public DeudaPendCxC buscarDeudaPend(Integer idFac) {

        DeudaPendCxC dpend;
        String nombQuery = "buscarDeudaPendPorId";

        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dpend = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return dpend;
    }

    public NotaCredDebCxC buscarNotaDebito(Integer idFac) {

        NotaCredDebCxC ncd;
        String nombQuery = "buscarNotaCredDebCxCPorId";

        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            ncd = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return ncd;
    }

    public FacCuotaEsp buscarFCE(Integer idFac) {

        FacCuotaEsp fce;
        String nombQuery = "buscarFacCuotaEspPorId";

        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            fce = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return fce;
    }

    public FacGastoNoComun buscarFGNC(Integer idFac) {

        FacGastoNoComun fgnc;
        String nombQuery = "buscarFacGastoNoComunPorId";

        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            fgnc = dataAcces.buscarEntidadPorId(idFac, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        return fgnc;
    }

    public void generarNotaDebitoAlicuota(Alicuota alicuota, Inmueble inmueble, Calendar fechaRef, PagoFacCxC pfcxc) {

        Calendar fecVen = calcularFechaVencimiento(alicuota.getFecEmiAlicuota());
        //---------------------------------------------------------------------------------------------
        // la fecha de referencia puede ser la fecha de hoy (fechaHoy) o la fecha del pago (fechaPago)
        //---------------------------------------------------------------------------------------------
        if (fechaRef.after(fecVen)) {
            numDias = calcularNumDiasEntreFechas(fechaRef, fecVen);
            if (numDias >= 30) {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                } else {
                    mtoMora = (alicuota.getSaldoAlicuota() * ((parametro.getPorcMora() / 100) / 365) * 30);
                }
            } else {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                } else {
                    mtoMora = (alicuota.getSaldoAlicuota() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                }
            }
        } else {
            return;
        }

        //-----------------------------------------------------
        // No se generan ndd por menos de Bs 0.01 , 19-08-2018 JR
        //-----------------------------------------------------
        if (mtoMora < 0.01) {
            return;
        }

        NotaCredDebCxC notaCredDebCxCNueva = crearNotaDebito();

        notaCredDebCxCNueva.setMtoNotaCredDebCxC(mtoMora);
        notaCredDebCxCNueva.setSaldoNotaCredDebCxC(mtoMora);

        nroNota = nroNota + 1;

        notaCredDebCxCNueva.setNumNotaCredDebCxC(nroNota.toString());
        notaCredDebCxCNueva.setDescNotaCredDebCxC("% Interes de mora, alicuota" + " " + convertirCalendarString(alicuota.getPeriodoActual()));

        notaCredDebCxCNueva.setInmueble(inmueble);

        listaNotaCredDebCxC.add(notaCredDebCxCNueva);

        return;
    }

    public void generarNotaDebitoDeudaPend(DeudaPendCxC dpend, Inmueble inmueble, Calendar fechaRef, PagoFacCxC pfcxc) {

        Calendar fecVen = calcularFechaVencimiento(dpend.getFecDeudaPendCxC());

        if (fechaRef.after(fecVen)) {
            numDias = calcularNumDiasEntreFechas(fechaRef, fecVen);
            if (numDias >= 30) {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                } else {
                    mtoMora = (dpend.getSaldoDeudaPendCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                }
            } else {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                } else {
                    mtoMora = (dpend.getSaldoDeudaPendCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                }
            }
        } else {
            return;
        }
        //-------------------------------------------------------
        // No se generan ndd por menos de Bs 0.01 , 19-08-2018 JR
        //-------------------------------------------------------
        if (mtoMora < 0.01) {
            return;
        }

        NotaCredDebCxC notaCredDebCxCNueva = crearNotaDebito();

        notaCredDebCxCNueva.setMtoNotaCredDebCxC(mtoMora);
        notaCredDebCxCNueva.setSaldoNotaCredDebCxC(mtoMora);

        nroNota = nroNota + 1;

        notaCredDebCxCNueva.setNumNotaCredDebCxC(nroNota.toString());
        notaCredDebCxCNueva.setDescNotaCredDebCxC("% Interes de mora, deuda pend" + " " + convertirCalendarString(dpend.getFecDeudaPendCxC()));

        notaCredDebCxCNueva.setInmueble(inmueble);

        listaNotaCredDebCxC.add(notaCredDebCxCNueva);

        return;
    }

    public void generarNotaDebitoFacturaCuotaEsp(FacCuotaEsp fce, Inmueble inmueble, Calendar fechaRef, PagoFacCxC pfcxc) {

        Calendar fecVen = calcularFechaVencimiento(fce.getFecEmiFacCuotaEsp());

        if (fechaRef.after(fecVen)) {
            numDias = calcularNumDiasEntreFechas(fechaRef, fecVen);
            if (numDias >= 30) {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                } else {
                    mtoMora = (fce.getSaldoFacCuotaEsp() * ((parametro.getPorcMora() / 100) / 365) * 30);
                }
            } else {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                } else {
                    mtoMora = (fce.getSaldoFacCuotaEsp() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                }
            }
        } else {
            return;
        }
        //-----------------------------------------------------
        // No se generan ndd por menos de Bs 0.01 , 19-08-2018 JR
        //-----------------------------------------------------
        if (mtoMora < 0.01) {
            return;
        }

        NotaCredDebCxC notaCredDebCxCNueva = crearNotaDebito();

        notaCredDebCxCNueva.setMtoNotaCredDebCxC(mtoMora);
        notaCredDebCxCNueva.setSaldoNotaCredDebCxC(mtoMora);

        nroNota = nroNota + 1;

        notaCredDebCxCNueva.setNumNotaCredDebCxC(nroNota.toString());
        notaCredDebCxCNueva.setDescNotaCredDebCxC("% Interes de mora, fac cuota esp" + " " + convertirCalendarString(fce.getPeriodoActual()));

        notaCredDebCxCNueva.setInmueble(inmueble);

        listaNotaCredDebCxC.add(notaCredDebCxCNueva);

        return;
    }

    public void generarNotaDebitoNotaDebitoCxC(NotaCredDebCxC ncd, Inmueble inmueble, Calendar fechaRef, PagoFacCxC pfcxc) throws ParseException {

        Calendar fecVen = calcularFechaVencimiento(convertirFecha(ncd.getFecNotaCredDebCxC()));

        if (fechaRef.after(fecVen)) {
            numDias = calcularNumDiasEntreFechas(fechaRef, fecVen);
            if (numDias >= 30) {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                } else {
                    mtoMora = (ncd.getSaldoNotaCredDebCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                }
            } else {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                } else {
                    mtoMora = (ncd.getSaldoNotaCredDebCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                }
            }
        } else {
            return;
        }

        //-----------------------------------------------------
        // No se generan ndd por menos de Bs 0.01 , 19-08-2018 JR
        //-----------------------------------------------------
        if (mtoMora < 0.01) {
            return;
        }

        NotaCredDebCxC notaCredDebCxCNueva = crearNotaDebito();

        notaCredDebCxCNueva.setMtoNotaCredDebCxC(mtoMora);
        notaCredDebCxCNueva.setSaldoNotaCredDebCxC(mtoMora);

        nroNota = nroNota + 1;

        notaCredDebCxCNueva.setNumNotaCredDebCxC(nroNota.toString());
        notaCredDebCxCNueva.setDescNotaCredDebCxC("% Interes de mora, fac nota deb" + " " + ncd.getFecNotaCredDebCxC());

        notaCredDebCxCNueva.setInmueble(inmueble);

        listaNotaCredDebCxC.add(notaCredDebCxCNueva);

        return;
    }

    public void generarNotaDebitoFacGastoNoComun(FacGastoNoComun fgnc, Inmueble inmueble, Calendar fechaRef, PagoFacCxC pfcxc) {

        Calendar fecVen = calcularFechaVencimiento((fgnc.getFecFacGNC()));

        if (fechaRef.after(fecVen)) {
            numDias = calcularNumDiasEntreFechas(fechaRef, fecVen);
            if (numDias >= 30) {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * 30);
                } else {
                    mtoMora = (fgnc.getSaldoFacGastoNoComun() * ((parametro.getPorcMora() / 100) / 365) * 30);
                }
            } else {
                if (cxcCancel) {
                    mtoMora = (pfcxc.getMtoPagadoCxC() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                } else {
                    mtoMora = (fgnc.getSaldoFacGastoNoComun() * ((parametro.getPorcMora() / 100) / 365) * numDias);
                }
            }
        } else {
            return;
        }

        //-----------------------------------------------------
        // No se generan ndd por menos de Bs 0.01 , 19-08-2018 JR
        //-----------------------------------------------------
        if (mtoMora < 0.01) {
            return;
        }

        NotaCredDebCxC notaCredDebCxCNueva = crearNotaDebito();

        notaCredDebCxCNueva.setMtoNotaCredDebCxC(mtoMora);
        notaCredDebCxCNueva.setSaldoNotaCredDebCxC(mtoMora);

        nroNota = nroNota + 1;

        notaCredDebCxCNueva.setNumNotaCredDebCxC(nroNota.toString());
        notaCredDebCxCNueva.setDescNotaCredDebCxC("% Interes de mora, fac gasto nc" + " " + convertirCalendarString(fgnc.getFecFacGNC()));

        notaCredDebCxCNueva.setInmueble(inmueble);

        listaNotaCredDebCxC.add(notaCredDebCxCNueva);

        return;
    }

    public void obtenerPagosInmueblesCxCPagadas(Inmueble inmueble, Condominio cond) throws ParseException {

        listaPagosFacCxCInm.clear();
        listaPagosFacCxCInmPer.clear();
        PagoFacCxC pfcxc;
        Calendar fechaPago = null;

        String nombQuery = "buscarFacPagoCxCPorIdInmRango";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosFacCxCInm = dataAcces.buscarListaFetchPorId(inmueble.getId(), nombQuery);  //fetch con PagoFacCxC
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        for (Iterator<PagoFacCxC> iter1 = listaPagosFacCxCInm.iterator();
                iter1.hasNext();) {
            pfcxc = (PagoFacCxC) iter1.next();
            fechaPago = convertirFecha(pfcxc.getPagoCxC().getFecDocPagoCxC());
            if (fechaPago.after(cond.getPeriodoActual()) & (fechaPago.before(fechaCorte) | fechaPago.equals(fechaCorte))) {
                listaPagosFacCxCInmPer.add(pfcxc);
            }
        }
    }

    public void generarNotaDebitoMoraCxCPagadas(Inmueble inmueble, Condominio cond) throws ParseException {

        PagoFacCxC pfcxc = null;
        Calendar fechaPago;
        Alicuota alic;
        DeudaPendCxC dpend;
        NotaCredDebCxC ncd;
        FacCuotaEsp fce;
        FacGastoNoComun fgnc;
        cxcCancel = true;
        int disc = 0;

        obtenerPagosInmueblesCxCPagadas(inmueble, cond);

        for (Iterator<PagoFacCxC> iter1 = listaPagosFacCxCInmPer.iterator();
                iter1.hasNext();) {
            try {
                pfcxc = (PagoFacCxC) iter1.next();
            } catch (NoSuchElementException ne) {
                System.out.println("Error al tratar de leer la coleccion de pagoFacCxCInmPer");
            }
            fechaPago = convertirFecha(pfcxc.getPagoCxC().getFecDocPagoCxC());

            if (pfcxc.getDiscFacCxC().equals("ALI")) {
                disc = 1;
            } else {
                if (pfcxc.getDiscFacCxC().equals("DPE")) {
                    disc = 2;
                } else {
                    if (pfcxc.getDiscFacCxC().equals("NCD") | pfcxc.getDiscFacCxC().equals("NDE")) {
                        disc = 3;
                    } else {
                        if (pfcxc.getDiscFacCxC().equals("CES")) {
                            disc = 4;
                        } else {
                            if (pfcxc.getDiscFacCxC().equals("GNC")) {
                                disc = 5;
                            }
                        }
                    }
                }
            }
            switch (disc) {
                case 1:
                    alic = buscarAlicuota(pfcxc.getIdFacCxC());
                    if (alic.getEdoAlicuota().equals("CANC")) {
                        generarNotaDebitoAlicuota(alic, inmueble, fechaPago, pfcxc);
                    }
                    break;
                case 2:
                    dpend = buscarDeudaPend(pfcxc.getIdFacCxC());
                    if (dpend.getEdoDeudaPendCxC().equals("CANC")) {
                        generarNotaDebitoDeudaPend(dpend, inmueble, fechaPago, pfcxc);
                    }
                    break;
                case 3:
                    ncd = buscarNotaDebito(pfcxc.getIdFacCxC());
                    if (ncd.getTipoOperNotaCredDebCxC().equals("MANUAL")) {
                        if (ncd.getEdoNotaCredDebCxC().equals("CANC")) {
                            generarNotaDebitoNotaDebitoCxC(ncd, inmueble, fechaCorte, pfcxc);
                        }
                    }
                    break;
                case 4:
                    fce = buscarFCE(pfcxc.getIdFacCxC());
                    if (fce.getEdoFacCuotaEsp().equals("CANC")) {
                        generarNotaDebitoFacturaCuotaEsp(fce, inmueble, fechaPago, pfcxc);
                    }

                    break;
                case 5:
                    fgnc = buscarFGNC(pfcxc.getIdFacCxC());
                    if (fgnc == null) {
                        System.out.println(" No se consigue la factura de gasto no comun, el id del pago fac es:" + pfcxc.getId());
                    }
                    if (fgnc.getEdoFacGastoNoComun().equals("CANC")) {
                        generarNotaDebitoFacGastoNoComun(fgnc, inmueble, fechaPago, pfcxc);
                    }
                    break;
            }
        }
        cxcCancel = false;
    }

    public void generarReporteCierrePreliminar(Condominio condominio, Collection gastoComun, Collection listaAlic, Collection listaFacCuotaEspNuevas,
            Collection listaPagosCxCNuevos, Collection ajustes)
            throws ClassNotFoundException, SQLException, IOException, JRException, java.io.IOException {

        //-------------------------------------------------------------------------------------------------------------------
        FacesContext context = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

        InputStream reportStream = this.getClass().getResourceAsStream("/informes/modelos/CierreMensual.jasper");
        InputStream subrep = this.getClass().getResourceAsStream("/informes/modelos/GastosComunesOrd.jasper");
        InputStream subrep1 = this.getClass().getResourceAsStream("/informes/modelos/Alicuotas.jasper");
        InputStream subrep2 = this.getClass().getResourceAsStream("/informes/modelos/FacCuotaEsp.jasper");
        InputStream subrep3 = this.getClass().getResourceAsStream("/informes/modelos/PagoCxC.jasper");
        InputStream subrep4 = this.getClass().getResourceAsStream("/informes/modelos/NotaDebMoraCxC.jasper");

        ServletOutputStream servletOutputStream = (ServletOutputStream) response.getOutputStream();
        //-------------------------------------------------------------------------------------------------------------------

        parametro = condominio.getParametro();

        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...cierre cancelado"));
            return;
        }
        String tipoFac = parametro.getTipoFacturacion();

        String nombCond = condominio.getNombCondominio();
        Calendar periodoActual = condominio.getPeriodoActual();

        String periodo = convertirPeriodoAString(periodoActual);
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //-------------------------------------------------------
        // Se genera el datasource de gastos comunes ordinarios
        // incluye las transacciones de fondo de reserva y pres.
        //-------------------------------------------------------
        JRBeanCollectionDataSource dataSourceGastos;

        Collection gastosDT = inicializarGastoComunOrdDT(gastoComun, tranFondo, ajustes);

        dataSourceGastos = new JRBeanCollectionDataSource(gastosDT);

        //--------------------------------------------------------
        // Se genera el datasource de alicuotas del periodo actual
        //--------------------------------------------------------
        JRBeanCollectionDataSource dataSourceAlicuotas;

        Collection<AlicuotaDT> alicuotasDT = inicializarAlicuotasDT(listaAlic);

        dataSourceAlicuotas = new JRBeanCollectionDataSource(alicuotasDT);
        //-----------------------------------------------------------------------
        // Se genera el datasource de cxc de cuotas especiales del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceFacCuotaEsp;

        Collection<FacCuotaEspDT> facCuotaEspDT = inicializarFacCuotaEspDT(listaFacCuotaEspNuevas);

        dataSourceFacCuotaEsp = new JRBeanCollectionDataSource(facCuotaEspDT);
        //-----------------------------------------------------------------------

        // Se genera el datasource de pagos automaticos de cxc del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagosCxC;

        Collection<PagoCxCDTR> pagosCxCDTR = inicializarPagosCxCDTR(listaPagosCxCNuevos);

        dataSourcePagosCxC = new JRBeanCollectionDataSource(pagosCxCDTR);
        //-----------------------------------------------------------------------

        // Se genera el datasource de notas de débito de cxc del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceNotasDebCxC;

        Collection<NotaDebitoCxCDT> notasDebitoCxCDT = inicializarNotasDebCxCDT(listaNotaCredDebCxC);

        dataSourceNotasDebCxC = new JRBeanCollectionDataSource(notasDebitoCxCDT);
        //---------------------------------------------------------
        HashMap parametros = new HashMap();
        parametros.put("gastos", dataSourceGastos);
        parametros.put("alicuotas", dataSourceAlicuotas);
        parametros.put("facCuotaEsp", dataSourceFacCuotaEsp);
        parametros.put("pagosCxC", dataSourcePagosCxC);
        parametros.put("notasDebCxC", dataSourceNotasDebCxC);
        parametros.put("condominio", nombCond);
        parametros.put("periodo", periodo);
        parametros.put("subrep", subrep);
        parametros.put("tipoFac", tipoFac);
        parametros.put("subrep1", subrep1);
        parametros.put("subrep2", subrep2);
        parametros.put("subrep3", subrep3);
        parametros.put("subrep4", subrep4);
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

    public String convertirPeriodoAString(Calendar periodoActual) {
        //----------------------------------------------------------------
        //Se da formato al periodo para mostrarlo en el reporte (mm-aaaa)
        //----------------------------------------------------------------
        String mesp = "";

        int mesPer = periodoActual.get(Calendar.MONTH) + 1;
        int añoPer = periodoActual.get(Calendar.YEAR);

        if (mesPer < 10) {
            mesp = "0" + mesPer;
        } else {
            mesp = String.valueOf(mesPer);
        }

        String stringPer = mesp + "-" + añoPer;
        return stringPer;
    }

    private Collection<GastoComunDT> inicializarGastoComunOrdDT(Collection listaGCOrd, Collection tranfondo, Collection ajustes) {

        for (Iterator<GastoComunOrd> it = listaGCOrd.iterator();
                it.hasNext();) {

            GastoComunOrd gco = (GastoComunOrd) it.next();
            if (gco == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                Integer idProv = gco.getIdProveedor();
                String nombProv = buscarNombProveedor(idProv);
                Integer idConc = gco.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                GastoComunDT gastoComunOrdDT = new GastoComunDT();

                gastoComunOrdDT.setId(gco.getId());
                gastoComunOrdDT.setIdProveedor(idProv);
                gastoComunOrdDT.setIdConcepto(idConc);
                gastoComunOrdDT.setNombProveedor(nombProv);
                gastoComunOrdDT.setNombConcepto(nombConc);
                gastoComunOrdDT.setFecGastoComun(gco.getFecGastoComunOrd());
                gastoComunOrdDT.setNumDocGastoComun(gco.getNumDocGastoComunOrd());

                gastoComunOrdDT.setMtoGastoComun(gco.getMtoGastoComunOrd());
                gastoComunOrdDT.setObsGastoComun(gco.getObsGastoComunOrd());
                gastoComunOrdDT.setEdoGastoComun(gco.getEdoGastoComunOrd());
                gastoComunOrdDT.setSaldoGastoComun(gco.getSaldoGastoComunOrd());

                listaGastoComunOrdDT.add(gastoComunOrdDT);

            }
        }
        for (Iterator<TranFondo> it = tranfondo.iterator();
                it.hasNext();) {
            TranFondo tranfon = (TranFondo) it.next();

            Integer cero = new Integer(0);

            GastoComunDT gastoComunOrdDT = new GastoComunDT();

            gastoComunOrdDT.setId(tranfon.getId());
            gastoComunOrdDT.setIdProveedor(0);

            if (tranfon.getIdConceptoTranFondo().equals(cero)) {// Es un fondo de reserva, se genera con id=0 en el cierre
                gastoComunOrdDT.setIdConcepto(0);
                gastoComunOrdDT.setNombConcepto("Res. mes actual");
                gastoComunOrdDT.setNombProveedor("Reserva");
                gastoComunOrdDT.setNumDocGastoComun(tranfon.getDocTranFondo());
            } else {
                Integer idConc = tranfon.getIdConceptoTranFondo();
                String nombConc = buscarNombConcepto(idConc);

                gastoComunOrdDT.setIdConcepto(idConc);
                gastoComunOrdDT.setNombConcepto(nombConc);
                gastoComunOrdDT.setNombProveedor("Prestaciones");
                gastoComunOrdDT.setNumDocGastoComun(tranfon.getDocTranFondo());
            }

            gastoComunOrdDT.setFecGastoComun(tranfon.getFecTranFondo());
            gastoComunOrdDT.setMtoGastoComun(tranfon.getMtoTranFondo());
            gastoComunOrdDT.setObsGastoComun(tranfon.getDescTranFondo());
            gastoComunOrdDT.setEdoGastoComun(tranfon.getEstadoTranFondo());
            gastoComunOrdDT.setSaldoGastoComun(tranfon.getSaldoTranFondo());

            listaGastoComunOrdDT.add(gastoComunOrdDT);
        }

        Integer cero = new Integer(0);
        Double ceroDouble = new Double(0.0);
        String blanco = "";

        for (Iterator<AjustesCond> it = ajustes.iterator();
                it.hasNext();) {
            AjustesCond ajuste = (AjustesCond) it.next();

            GastoComunDT gastoComunOrdDT = new GastoComunDT();

            gastoComunOrdDT.setId(ajuste.getId());
            gastoComunOrdDT.setIdProveedor(cero);

            Integer idConc = ajuste.getIdConcepto();
            String nombConc = buscarNombConcepto(idConc);

            gastoComunOrdDT.setIdConcepto(idConc);
            gastoComunOrdDT.setNombConcepto(nombConc);
            gastoComunOrdDT.setNombProveedor("Ajuste");
            gastoComunOrdDT.setNumDocGastoComun("N/A");

            gastoComunOrdDT.setFecGastoComun(convertirCalendarString(ajuste.getFecMovAjustesCond()));

            if (ajuste.getTipoAjustesCond().equals("Debito")) {
                gastoComunOrdDT.setMtoGastoComun(ajuste.getMtoAjustesCond() * -1);
            } else {
                gastoComunOrdDT.setMtoGastoComun(ajuste.getMtoAjustesCond());
            }
            gastoComunOrdDT.setObsGastoComun(blanco);
            gastoComunOrdDT.setEdoGastoComun(ajuste.getEdoAjustesCond());
            gastoComunOrdDT.setSaldoGastoComun(ceroDouble);

            listaGastoComunOrdDT.add(gastoComunOrdDT);
        }
        return listaGastoComunOrdDT;
    }

    public String buscarNombProveedor(Integer idProv) {

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
        String nombreProv = proveedor.getNombProveedor();
        return nombreProv;
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

    public String buscarNombConceptoFCE(Integer idConc) {

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
        String nombConcepto = concepto.getNombConcepto();
        return nombConcepto;
    }

    public Collection ordenarListaAlicuotas(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<AlicuotaDT> listaDesOrdenada = new ArrayList<AlicuotaDT>();

        for (Iterator<AlicuotaDT> it = lista.iterator();
                it.hasNext();) {
            AlicuotaDT alic = (AlicuotaDT) it.next();
            listaDesOrdenada.add(alic);
        }

        Collections.sort(listaDesOrdenada, new ComparadorAlicDT());//devuelve la lista ordenada en listaDesOrdenada

        Collection<AlicuotaDT> coleccionOrdenada = new ArrayList<AlicuotaDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            AlicuotaDT alicDT = listaDesOrdenada.get(i);
            coleccionOrdenada.add(alicDT);
        }
        return coleccionOrdenada;
    }

    private Collection<AlicuotaDT> inicializarAlicuotasDT(Collection alicuotas) {

        Collection<AlicuotaDT> alicuotasDT = new ArrayList<AlicuotaDT>();

        for (Iterator<Alicuota> it = alicuotas.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();

            AlicuotaDT alicuotaDT = new AlicuotaDT();

            alicuotaDT.setNombPropiedad(alic.getInmueble().getPropiedad().getNombPropiedad());
            alicuotaDT.setCodInmueble(alic.getInmueble().getCodInmueble());
            alicuotaDT.setDesAlicuota(alic.getDesAlicuota());
            alicuotaDT.setMtoAlicuota(alic.getMtoAlicuota());
            alicuotaDT.setSaldoAlicuota(alic.getSaldoAlicuota());
            alicuotaDT.setEdoAlicuota(alic.getEdoAlicuota());

            alicuotasDT.add(alicuotaDT);
        }
        //   alicuotasDTOrdenada = ordenarListaAlicuotas(alicuotasDT);
        return alicuotasDT;
    }

    private Collection<FacCuotaEspDT> inicializarFacCuotaEspDT(Collection facCuotaEsp) {

        Collection<FacCuotaEspDT> facturasDT = new ArrayList<FacCuotaEspDT>();

        for (Iterator<FacCuotaEsp> it = facCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();

            FacCuotaEspDT facCuotaEspDT = new FacCuotaEspDT();

            facCuotaEspDT.setNombPropiedad(fce.getInmueble().getPropiedad().getNombPropiedad());
            facCuotaEspDT.setCodInmueble(fce.getInmueble().getCodInmueble());
            facCuotaEspDT.setDesFacCuotaEsp(fce.getDesFacCuotaEsp());
            facCuotaEspDT.setMtoFacCuotaEsp(fce.getMtoFacCuotaEsp());
            facCuotaEspDT.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp());
            facCuotaEspDT.setEdoFacCuotaEsp(fce.getEdoFacCuotaEsp());

            facturasDT.add(facCuotaEspDT);
        }
        return facturasDT;
    }

    private Collection<PagoCxCDTR> inicializarPagosCxCDTR(Collection pagoCxC) {

        Collection<PagoCxCDTR> pagosCxCDTR = new ArrayList<PagoCxCDTR>();
        Collection<PagoCxCDTR> listaOrd = new ArrayList<PagoCxCDTR>();

        for (Iterator<PagoCxC> it = pagoCxC.iterator();
                it.hasNext();) {
            PagoCxC pcxc = (PagoCxC) it.next();

            PagoCxCDTR pagoCxCDTR = new PagoCxCDTR();

            pagoCxCDTR.setNombPropiedad(pcxc.getInmueble().getPropiedad().getNombPropiedad());
            pagoCxCDTR.setCodInmueble(pcxc.getInmueble().getCodInmueble());
            pagoCxCDTR.setDesPagoCxC(pcxc.getDesPagoCxC());
            pagoCxCDTR.setMtoPagoCxC(pcxc.getMtoPagoCxC());
            pagoCxCDTR.setFormaPagoCxC(pcxc.getFormaPagoCxC());

            pagosCxCDTR.add(pagoCxCDTR);
        }
        listaOrd = ordenarLista(pagosCxCDTR);// para ordenar la lista de gastos
        return listaOrd;
    }

    private Collection<NotaDebitoCxCDT> inicializarNotasDebCxCDT(Collection notasDebCxC) {

        Collection<NotaDebitoCxCDT> notaDebDT = new ArrayList<NotaDebitoCxCDT>();

        for (Iterator<NotaCredDebCxC> it = notasDebCxC.iterator();
                it.hasNext();) {

            NotaCredDebCxC ndc = (NotaCredDebCxC) it.next();

            NotaDebitoCxCDT ndcDT = new NotaDebitoCxCDT();

            ndcDT.setNombPropiedad(ndc.getInmueble().getPropiedad().getNombPropiedad());
            ndcDT.setCodInmueble(ndc.getInmueble().getCodInmueble());
            ndcDT.setDescNotaDebCxC(ndc.getDescNotaCredDebCxC());
            ndcDT.setMtoNotaDebCxC(ndc.getMtoNotaCredDebCxC());

            notaDebDT.add(ndcDT);
        }

        return notaDebDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<PagoCxCDTR> listaDesOrdenada = new ArrayList<PagoCxCDTR>();

        for (Iterator<PagoCxCDTR> it = lista.iterator();
                it.hasNext();) {
            PagoCxCDTR pago = (PagoCxCDTR) it.next();
            listaDesOrdenada.add(pago);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagoCxCDTR());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoCxCDTR> coleccionOrdenada = new ArrayList<PagoCxCDTR>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoCxCDTR pagoOrd = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pagoOrd);
        }
        return coleccionOrdenada;
    }
}
