/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package administracion.backing;

/**
 *
 * @author jesus
 */
import configuracion.modelos.Parametro;
import datosbasicos.modelos.*;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import movimientos.modelos.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.HibernateException;
import util.*;

public class CierreMensualCtrl extends DaoCrud<Propiedad, Propiedad, Integer> implements Serializable {

    private BeanUsuario beanUsuario;
    private Collection<Inmueble> listaInm = new ArrayList<>();
    private Collection<Alicuota> listaAlic = new ArrayList<>();
    private Collection<FacCuotaEsp> listaFCE = new ArrayList<>();
    private Collection<GastoComunOrd> listaGCOrd = new ArrayList<>();
    private Collection<GastoComunDT> listaGastoComunOrdDT = new ArrayList<>();
    private Collection<CuotaEsp> listaCEsp = new ArrayList<>();
    private Collection<PagoAdelantado> listaInmPagosAdel = new ArrayList<>();
    private Collection<Alicuota> listaInmAlicuotas = new ArrayList<>();
    private Collection<NotaCredDebCxC> listaInmNotasCred = new ArrayList<>();
    private Collection<NotaCredDebCxC> listaInmNotasDeb = new ArrayList<>();
    private Collection<FacCuotaEsp> listaInmFacCuotaEsp = new ArrayList<>();
    private Collection<DeudaPendCxC> listaInmDeudaPend = new ArrayList<>();
    private Collection<FacGastoNoComun> listaInmFacGNC = new ArrayList<>();
    private Collection<NotaCredDebCxC> listaNotaCredDebCxC = new ArrayList<>();
    private Collection<AjustesCond> listaAjustesCond = new ArrayList<>();
    private Collection<PagoFacCxC> listaPagosFacCxCInm = new ArrayList<>();
    private Collection<PagoFacCxC> listaPagosFacCxCInmPer = new ArrayList<>();
    private Parametro parametro;
    private Integer nroNota;
    private double mtoMora;
    private int numDias;
    private Calendar fechaCorte;
    private Boolean cxcCancel = false;// true la cxc a procesar tiene estado CANC.Se usa para el calculo de la mora. 
    Calendar periodoActual;

    public CierreMensualCtrl() {
    }

    public String irASeleccionarCondominio() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);
        beanUsuario.deshabilitar = false;

        return "selcond";
    }

    public String salir() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.deshabilitar = false;
        return "administracion";

    }

    public void procesarCierreMes(ActionEvent actionEvent) throws ParseException {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        Integer idCondActual = beanUsuario.getIdCondActual();
        periodoActual = beanUsuario.getPeriodoActual();
        //---------------------------------------------------------------------------------
        // Se verifica si el periodo a cerrar (periodo actual) ya finalizo, devuelve true
        // si es asi, si no devuelve false
        //---------------------------------------------------------------------------------

        Boolean finPerido = verificarFinPeridoActual(periodoActual);
        if (!finPerido) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El periodo actual aun no ha concluido...cierre cancelado"));
            return;
        }
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
                    new FacesMessage("Aun no han sido registrados gasto comunes ordinarios para el periodo actual...cierre cancelado"));
            return;
        }
        //---------------------------------------------------------------------------------
        // Genera la suma de los montos de cada gasto comun ordinario: total de gastos
        //---------------------------------------------------------------------------------
        Double totalGastos = totalizarGastosComunesOrd(gastosComunes);

        //---------------------------------------------------------------------------------
        // Genera una lista con los ajustes del periodo a cerrar (actual)
        //---------------------------------------------------------------------------------
        Collection ajustes = buscarAjustesPeriodoActual(idCondActual, periodoActual);
        //---------------------------------------------------------------------------------
        // Genera la suma de los montos de cada ajuste: total de ajustes,si los hubo
        // para el periodo actual. El total se suma en el total de gastos. Los ajustes
        // aplicados cambian su estado a CANC.
        //---------------------------------------------------------------------------------

        if (!(ajustes == null)) {
            Double totalAjustes = totalizarAjustes(ajustes);
            totalGastos = totalGastos + totalAjustes;
        }
        actualizarEdoAjustesAplicados(ajustes);
        listaAjustesCond.clear();
        //---------------------------------------------------------------------------
        // Buscar los inmuebles de cada propiedad para el condominio encontrado
        //---------------------------------------------------------------------------
        listaInm = buscarInmueblesPropiedades(condominio);
        //---------------------------------------------------------------------------
        // En caso de que el tipo de facturacion sea por gastos, se verifica la
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

        if (!listaCEsp.isEmpty()) {
            for (Iterator<CuotaEsp> it = listaCEsp.iterator();
                    it.hasNext();) {
                CuotaEsp cesp = (CuotaEsp) it.next();
                generarFacCuotaEsp(condominio, listaInm, cesp);
                listaFCE.clear();
            }
        }
        listaCEsp.clear();
        listaAlic.clear();
        //---------------------------------------------------------------------------------
        // Se buscan las listas de pagos adelantados y de alicuotas pendientes.
        // Se generan los pagos de alicuotas correpondientes
        //---------------------------------------------------------------------------------
        procesarPagosAlicuotas(condominio);
        listaInmPagosAdel.clear();
        listaInmAlicuotas.clear();
        //---------------------------------------------------------------------------------
        // Se verifica si existen notas de credito. Si es asi, se usan para pagar, parcial 
        // o totalmente, las cxc de cuotas especiales que tenga el inmueble.
        //---------------------------------------------------------------------------------
        procesarPagosCuotasEspeciales(condominio);
        listaInmNotasCred.clear();
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
        listaInm.clear();
        //---------------------------------------------------------------------------------
        // Se crea un nuevo periodo, el cual pasara a ser el periodo actual.
        //---------------------------------------------------------------------------------
        cerrarPeriodo(condominio);
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

    public void generarTranFondoReserva(Fondo fondo, Double mtoFondoRes, Calendar periodoActual) {

        //---------------------------------------------------
        // Se genera la tranFondo con estado pendiente. 
        // Se genera mov bancario a la cuenta del fondo
        // cuando se realice el pago de esta cuenta por
        // pagar en el cu de CxP. No afecta el saldo del fondo
        //----------------------------------------------------
        TranFondo tranFondoNueva = new TranFondo();
        tranFondoNueva.setEstadoTranFondo("PEND");
        tranFondoNueva.setMtoTranFondo(mtoFondoRes);
        tranFondoNueva.setSaldoTranFondo(mtoFondoRes);
        tranFondoNueva.setIdFondoTranFondo(0);
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
        //String fechaString = convertirCalendarString(fecha);
        String fechaString = convertirCalendarString(periodoActual);

        tranFondoNueva.setFecTranFondo(fechaString);
        tranFondoNueva.setDescTranFondo("Fdo Res:" + fechaString);
        registrarTranFondo(tranFondoNueva);

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

    public void registrarTranFondo(TranFondo tranFondo) {

        DaoQuery<TranFondo> dataAcces = new DaoQuery<TranFondo>();
        try {
            dataAcces.almacenarEntidad(tranFondo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

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
        Collection tranFondo = null;
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

    public void cerrarPeriodo(Condominio condominio) {

        Calendar perAct = condominio.getPeriodoActual();

        int mesPerAct = perAct.get(Calendar.MONTH);
        int añoPerAct = perAct.get(Calendar.YEAR);

        int mesPerSig = mesPerAct + 1;
        int añoPerSig = añoPerAct;

        if (mesPerSig > 12) {
            mesPerSig = 1;
            añoPerSig = añoPerSig + 1;
        }
        int diaPerSig = new Integer(01);

        Calendar fecha = new GregorianCalendar(añoPerSig, mesPerSig, diaPerSig);

        condominio.setPeriodoActual(fecha);
        beanUsuario.setPeriodoActual(fecha);

        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            dataAcces.almacenarOModificarEntidad(condominio);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        beanUsuario.deshabilitar = true;
        int mesCerrado = mesPerAct + 1;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El cierre de mes, correspondiente al periodo: " + mesCerrado + "-" + añoPerAct + ", " + "ha sido completado satisfactoriamente!"));
        return;
    }

    public Boolean verificarFinPeridoActual(Calendar periodoActual) {

        Integer mesPerAct = periodoActual.get(Calendar.MONTH);
        Integer añoPerAct = periodoActual.get(Calendar.YEAR);
        //-------------------------------------------
        // Se busca la fecha de hoy en el servidor
        //-------------------------------------------
        Calendar hoy = new GregorianCalendar();

        Integer mesHoy = hoy.get(Calendar.MONTH);
        Integer añoHoy = hoy.get(Calendar.YEAR);

        if ((añoHoy).equals(añoPerAct)) {
            if (mesHoy > mesPerAct) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
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

    public Collection buscarAjustesPeriodoActual(Integer idCondActual, Calendar periodoActual) {

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

    public void actualizarEdoAjustesAplicados(Collection ajustes) {

        DaoQuery<AjustesCond> dataAcces = new DaoQuery<AjustesCond>();
        for (Iterator<AjustesCond> it = ajustes.iterator();
                it.hasNext();) {
            AjustesCond acond = (AjustesCond) it.next();
            acond.setEdoAjustesCond("CANC");
            try {
                dataAcces.almacenarOModificarEntidad(acond);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
        }
        return;
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
            lista.addAll(listaInm);
            listaInm.clear();
        }
        return lista;
    }

    public void generarAlicuotaInmueble(Condominio condominio, Collection inmuebles, String tipoFac, Double totalGastos) {

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
        //----------------------------------------------------------------------
        // Se crean las alicuotas en la bd enviando la lista al dao
        //----------------------------------------------------------------------
        DaoQuery<Alicuota> dao = new DaoQuery<Alicuota>();

        try {
            dao.almacenarListaEntidades(listaAlic);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
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

    public void generarFacCuotaEsp(Condominio condominio, Collection inmuebles, CuotaEsp cesp) {

        Double mtoFac = cesp.getMtoTotCuotaEsp() / cesp.getNroMesesCuotaEsp();//mto es el monto del mes de la cuota esp.
        int nroInm = 0;
        double mtoInm = 0;
        String Fac = cesp.getCalcCuotaEsp();
        if (Fac.equals("TotInmuebles")) {
            nroInm = nroInm = listaInm.size();
            mtoInm = mtoFac / nroInm;
        }
        String nombConcepto = buscarNombConceptoFCE(cesp.getIdConceptoCuotaEsp());
        //-----------------------------------------------------------------
        //    Toma la fecha del servidor y el periodo actual de beanUsuario
        //-----------------------------------------------------------------

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
        Calendar fecha = new GregorianCalendar();
        for (Iterator<Inmueble> iter = inmuebles.iterator();
                iter.hasNext();) {
            Inmueble inm = (Inmueble) iter.next();
            FacCuotaEsp facCuotaEspNueva = new FacCuotaEsp();
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

        //----------------------------------------------------------------------
        // Se crean las facturas por cuota especial enviando la lista al dao
        //----------------------------------------------------------------------
        DaoQuery<FacCuotaEsp> dao = new DaoQuery<FacCuotaEsp>();

        try {
            dao.almacenarListaEntidades(listaFCE);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        //--------------------------------------------------------------------------------
        // Se actualiza el saldo y el estado de la cuota especial
        //--------------------------------------------------------------------------------
        Double cero = new Double(0);
        Double saldoNuevo = cesp.getSaldoCuotaEsp() - mtoFac;

        cesp.setSaldoCuotaEsp(saldoNuevo);

        if (saldoNuevo.equals(cero)) {
            cesp.setEstadoCuotaEsp("CANC");
        } else {
            cesp.setEstadoCuotaEsp("EPRO");
        }

        DaoQuery<CuotaEsp> dataAcces = new DaoQuery<CuotaEsp>();
        try {
            dataAcces.almacenarOModificarEntidad(cesp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    //-------------P R O C E S A R   P A G O S -------------------------
    public void procesarPagosAlicuotas(Condominio condominio) {

        obtenerPagosAdelantadosInm(condominio);

        if (!listaInmPagosAdel.isEmpty()) {
            Inmueble inm;
            PagoAdelantado pagoAdel;
            PagoFacCxC pagoFac;
            double cero = 0.00;
            double uno = 0.01;

            for (Iterator<PagoAdelantado> it = listaInmPagosAdel.iterator();
                    it.hasNext();) {
                pagoAdel = (PagoAdelantado) it.next();
                inm = pagoAdel.getInmueble();
                obtenerAlicuotasInm(inm);
                for (Iterator<Alicuota> iter = listaInmAlicuotas.iterator();
                        iter.hasNext();) {
                    Alicuota alic = (Alicuota) iter.next();
                    if (!alic.getEdoAlicuota().equals("CANC")) {
                        pagoFac = registrarPagoAlicuota(pagoAdel, inm, alic);

                        Double saldo = pagoAdel.getSaldoPagoAdelantado() - pagoFac.getMtoPagadoCxC();
                        if (saldo > uno) {
                            pagoAdel.setSaldoPagoAdelantado(saldo);
                        } else {
                            pagoAdel.setSaldoPagoAdelantado(cero);
                            break;
                        }
                    }
                }
                actualizarPagoAdelantado(pagoAdel);
            }
        }

    }

    public void procesarPagosCuotasEspeciales(Condominio condominio) {

        obtenerNotasCreditoInm(condominio);

        if (!listaInmNotasCred.isEmpty()) {
            Inmueble inm;
            Double saldoCero = new Double(0.01);
            Double cero = new Double(0);
            NotaCredDebCxC notaCredDebCxC;
            for (Iterator<NotaCredDebCxC> it = listaInmNotasCred.iterator();
                    it.hasNext();) {
                notaCredDebCxC = (NotaCredDebCxC) it.next();
                if (notaCredDebCxC.getSaldoNotaCredDebCxC() < saldoCero) {
                    actualizarNotaCredDebCxC(notaCredDebCxC);
                    continue;
                }
                inm = notaCredDebCxC.getInmueble();
                obtenerInmFacCuotaEsp(inm);// Obtiene la lista de facturas de cuotas especiales para el inmueble dado:listaInmFacCuotaEsp
                for (Iterator<FacCuotaEsp> iter = listaInmFacCuotaEsp.iterator();
                        iter.hasNext();) {
                    FacCuotaEsp fce = (FacCuotaEsp) iter.next();
                    if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                        PagoFacCxC pagoFacCxC = registrarPagoFacCuotaEsp(notaCredDebCxC, inm, fce);
                        //---------------------------------------------------------------------------
                        // se genera una transaccion al fondo especial (TranFondo)
                        //---------------------------------------------------------------------------
                        Fondo fondo = buscarFondoCuotaEspecial();
                        registrarTranFondo(fondo, pagoFacCxC);
                        //---------------------------------------------------------------------------

                        Double saldo = notaCredDebCxC.getSaldoNotaCredDebCxC() - pagoFacCxC.getMtoPagadoCxC();
                        if (saldo >= saldoCero) {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(saldo);
                        } else {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(cero);
                            break;
                        }
                    }
                }
                if (!listaInmFacCuotaEsp.isEmpty()) {
                    actualizarNotaCredDebCxC(notaCredDebCxC);
                }
            }
        }

    }

    public void registrarTranFondo(Fondo fondo, PagoFacCxC pagoFacCxC) {
        //---------------------------------------------------------------------------------
        // En caso de que el pago incluya alguna cuota especial, se genera una transaccion 
        // de ingreso al fondo asociado a la misma, y se actualiza el saldo del mismo.
        //----------------------------------------------------------------------------------
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        Double cero = new Double(0.0);

        TranFondo tranFondoNueva = new TranFondo();
        tranFondoNueva.setEstadoTranFondo("CANC");
        tranFondoNueva.setMtoTranFondo(pagoFacCxC.getMtoPagadoCxC());
        tranFondoNueva.setSaldoTranFondo(cero);
        tranFondoNueva.setIdPago(pagoFacCxC.getPagoCxC().getId());
        tranFondoNueva.setIdConceptoTranFondo(buscarIdConceptoFacCuotaEsp(pagoFacCxC.getIdFacCxC()));
        tranFondoNueva.setTipoOperTranFondo("Ingreso");
        tranFondoNueva.setDocTranFondo("N/A");
        tranFondoNueva.setFondo(fondo);
        tranFondoNueva.setIdConceptoTranFondo(fondo.getId());
        tranFondoNueva.setDescTranFondo("Pago fac. cuota especial");
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        tranFondoNueva.setFecMovTranFondo(fecha);
        tranFondoNueva.setPerTranFondo(beanUsuario.getPeriodoActual());
        //tranFondoNueva.setFecTranFondo(convertirCalendarString(fecha));
        tranFondoNueva.setFecTranFondo(convertirCalendarString(beanUsuario.getPeriodoActual()));
        registrarTranFondo(tranFondoNueva);
        actualizarFondo(tranFondoNueva, fondo);

    }

    public Fondo buscarFondoCuotaEspecial() {

        FacCuotaEsp fcesp = null;
        for (Iterator<FacCuotaEsp> it = listaInmFacCuotaEsp.iterator();
                it.hasNext();) {
            fcesp = (FacCuotaEsp) it.next();
            break;
        }
        Integer idFondo = fcesp.getCuotaEsp().getIdFondoCuotaEsp();
        Fondo fondo = buscarFondo(idFondo);// Devuelve el fondo de la cuota especial
        return fondo;
    }

    public Fondo buscarFondo(Integer idFondo) {

        String nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        Fondo fondo = null;
        try {
            fondo = dataAcces.buscarEntidadPorId(idFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return fondo;
    }

    public Integer buscarIdConceptoFacCuotaEsp(Integer id) {

        FacCuotaEsp fcesp;
        Integer idCpto = null;
        for (Iterator<FacCuotaEsp> it = listaInmFacCuotaEsp.iterator();
                it.hasNext();) {
            fcesp = (FacCuotaEsp) it.next();
            if (fcesp.getId().equals(id)) {
                idCpto = fcesp.getCuotaEsp().getIdConceptoCuotaEsp();
            }
        }
        return idCpto;
    }

    public void actualizarFondo(TranFondo tranFondo, Fondo fondo) {

        Double saldo = fondo.getSaldoFondo() + tranFondo.getMtoTranFondo();
        fondo.setSaldoFondo(saldo);

        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            dataAcces.almacenarOModificarEntidad(fondo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void procesarPagosOtrasCxC(Condominio condominio) {

        obtenerNotasCreditoInm(condominio);

        if (!listaInmNotasCred.isEmpty()) {
            Inmueble inm;
            NotaCredDebCxC notaCredDebCxC;
            Double cero = new Double(0.0);
            Double saldoCero = new Double(0.01);
            for (Iterator<NotaCredDebCxC> it = listaInmNotasCred.iterator();
                    it.hasNext();) {
                notaCredDebCxC = (NotaCredDebCxC) it.next();
                if (notaCredDebCxC.getSaldoNotaCredDebCxC() < saldoCero) {
                    actualizarNotaCredDebCxC(notaCredDebCxC);
                    continue;
                }
                inm = notaCredDebCxC.getInmueble();
                obtenerNotasDebitoInm(inm);

                for (Iterator<NotaCredDebCxC> iter = listaInmNotasDeb.iterator();
                        iter.hasNext();) {
                    NotaCredDebCxC notaDebito = (NotaCredDebCxC) iter.next();
                    if (!notaDebito.getEdoNotaCredDebCxC().equals("CANC")) {
                        PagoFacCxC pagoFac = registrarPagoNotaDebito(notaCredDebCxC, inm, notaDebito);
                        Double saldo = notaCredDebCxC.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC();
                        if (saldo > saldoCero) {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(saldo);
                        } else {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(cero);
                            break;
                        }
                    }
                }
                actualizarNotaCredDebCxC(notaCredDebCxC);

                listaInmNotasDeb.clear();
                if (notaCredDebCxC.getSaldoNotaCredDebCxC() < saldoCero) {
                    continue;
                }
                obtenerInmDeudaPend(inm);

                for (Iterator<DeudaPendCxC> iter = listaInmDeudaPend.iterator();
                        iter.hasNext();) {
                    DeudaPendCxC dpend = (DeudaPendCxC) iter.next();
                    if (!dpend.getEdoDeudaPendCxC().equals("CANC")) {
                        PagoFacCxC pagoFac = registrarPagoDeudaPend(notaCredDebCxC, inm, dpend);
                        Double saldo = notaCredDebCxC.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC();
                        if (saldo >= saldoCero) {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(saldo);
                        } else {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(cero);
                            break;
                        }
                    }
                }
                actualizarNotaCredDebCxC(notaCredDebCxC);

                listaInmDeudaPend.clear();
                if (notaCredDebCxC.getSaldoNotaCredDebCxC() < saldoCero) {
                    continue;
                }
                obtenerInmFacGNC(inm);

                for (Iterator<FacGastoNoComun> iter = listaInmFacGNC.iterator();
                        iter.hasNext();) {
                    FacGastoNoComun gnc = (FacGastoNoComun) iter.next();
                    if (gnc.getEdoFacGastoNoComun().equals("PEND") | gnc.getEdoFacGastoNoComun().equals("EPRO")) {
                        PagoFacCxC pagoFac = registrarPagoFacGNC(notaCredDebCxC, inm, gnc);
                        Double saldo = notaCredDebCxC.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC();
                        if (saldo >= saldoCero) {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(saldo);
                        } else {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(cero);
                            break;
                        }
                    }
                }

                actualizarNotaCredDebCxC(notaCredDebCxC);

                listaInmFacGNC.clear();
                if (notaCredDebCxC.getSaldoNotaCredDebCxC() < saldoCero) {
                    continue;
                }
                obtenerAlicuotasInm(inm);

                for (Iterator<Alicuota> iter = listaInmAlicuotas.iterator();
                        iter.hasNext();) {
                    Alicuota alic = (Alicuota) iter.next();
                    if (!alic.getEdoAlicuota().equals("CANC")) {
                        PagoFacCxC pagoFac = registrarPagoAlicuota(notaCredDebCxC, inm, alic);
                        Double saldo = notaCredDebCxC.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC();
                        if (saldo >= saldoCero) {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(saldo);
                        } else {
                            notaCredDebCxC.setSaldoNotaCredDebCxC(cero);
                            break;
                        }
                    }
                }
                actualizarNotaCredDebCxC(notaCredDebCxC);
                listaInmAlicuotas.clear();
            }
            listaInmNotasCred.clear();
        }
    }

    public void obtenerPagosAdelantadosInm(Condominio condominio) {

        System.out.println("obteniendo pagos adelantados...");

        Collection<PagoAdelantado> lista = new ArrayList<PagoAdelantado>();
        Collection<PagoAdelantado> listaPad = new ArrayList<PagoAdelantado>();
        PagoAdelantado pagoAdel;
        double uno = 1.00;

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad pro = (Propiedad) it.next();
            Integer idProp = pro.getId();
            String nombQuery = "buscarInmueblesConPagoAdel";

            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                listaPad = dataAcces.buscarListaFetchPorId(idProp, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }
            //---------------------------------------------------------------------------------
            // Se descartan los pagos adelantados con saldo menor que Bs 1, 01-01-2008 JR
            //---------------------------------------------------------------------------------
            for (Iterator<PagoAdelantado> it2 = listaPad.iterator();
                    it2.hasNext();) {
                pagoAdel = (PagoAdelantado) it2.next();
                if (pagoAdel.getSaldoPagoAdelantado() >= uno) {
                    lista.add(pagoAdel);
                } else {
                    pagoAdel.setSaldoPagoAdelantado(0.00);
                    actualizarPagoAdelantado(pagoAdel);
                    continue;
                }
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
        
        List<Alicuota> list = new ArrayList<>(listaInmAlicuotas);
        Collections.sort(list, new ComparadorFecAlicuota());
        listaInmAlicuotas.clear();
        listaInmAlicuotas.addAll(list);
        list.clear();
        lista.clear();
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
        //----------------------------sort------------------------------
        List<FacGastoNoComun> list = new ArrayList<>(listaInmFacGNC);
        Collections.sort(list, new ComparadorFechaGNC());
        listaInmFacGNC.clear();
        listaInmFacGNC.addAll(list);
        lista.clear();
        list.clear();
       
    }

    public PagoFacCxC registrarPagoAlicuota(PagoAdelantado pagoAdel, Inmueble inm, Alicuota alic) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago alic. con pago adelantado");
        pagoCxCNuevo.setNumDocPagoCxC("AUTO/PAD");

        if (pagoAdel.getSaldoPagoAdelantado() > alic.getSaldoAlicuota() | pagoAdel.getSaldoPagoAdelantado().equals(alic.getSaldoAlicuota())) {
            pagoCxCNuevo.setMtoPagoCxC(alic.getSaldoAlicuota());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(pagoAdel.getSaldoPagoAdelantado());
        }

        pagoCxCNuevo.setFormaPagoCxC("PAD");
        pagoCxCNuevo.setIdFormaPagoCxC(pagoAdel.getId());

        //--------------------------------------------------------------
        // Toma la fecha del servidor la asigna a la fecha del mov.
        // luego la transforma a string para pasarla a la fecha del doc.
        //--------------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));
        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());

        pagoCxCNuevo.setInmueble(inm);

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxC, alic);
        return pagoFacCxC;
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

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        actualizarEdoAlicuota(alic, pagoFac);
        return pagoFac;
    }

    public void actualizarEdoAlicuota(Alicuota alic, PagoFacCxC pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        if (pagoFac.getMtoPagadoCxC().equals(alic.getSaldoAlicuota())) {
            alic.setSaldoAlicuota(cero);
            alic.setEdoAlicuota(edo);
        } else {
            alic.setSaldoAlicuota(alic.getSaldoAlicuota() - pagoFac.getMtoPagadoCxC());
            if (alic.getSaldoAlicuota() < 0.01) {
                alic.setSaldoAlicuota(cero);
                alic.setEdoAlicuota(edo);
            } else {
                alic.setEdoAlicuota("EPRO");
            }
        }
        DaoQuery<Alicuota> dataAcces = new DaoQuery<Alicuota>();
        try {
            dataAcces.almacenarOModificarEntidad(alic);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarPagoAdelantado(PagoAdelantado pagoAdel) throws ValidatorException {

        if (pagoAdel.getSaldoPagoAdelantado() < 0.01) {
            pagoAdel.setEstadoPagoAdelantado("CANC");
        } else {
            pagoAdel.setEstadoPagoAdelantado("EPRO");
        }
        DaoQuery<PagoAdelantado> dataAcces = new DaoQuery<PagoAdelantado>();
        try {
            dataAcces.almacenarOModificarEntidad(pagoAdel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void obtenerNotasCreditoInm(Condominio condominio) {

        listaInmNotasCred.clear();

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
            listaInmNotasCred.addAll(lista);
            lista.clear();
        }
        //-------------------------Sort de las notas de credito 31-10-2025 --------------------
       
        List<NotaCredDebCxC> list = new ArrayList<>(listaInmNotasCred);
        Collections.sort(list, new ComparadorFechaNDC());
        listaInmNotasCred.clear();
        listaInmNotasCred.addAll(list);
        list.clear();
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
        
        //---------------------------sort------------------------------------------
        List<FacCuotaEsp> list = new ArrayList<>(listaInmFacCuotaEsp);
        Collections.sort(list, new ComparadorFechaFCE());
        listaInmFacCuotaEsp.clear();
        listaInmFacCuotaEsp.addAll(list);
        lista.clear();
        list.clear();
    }

    public PagoFacCxC registrarPagoFacCuotaEsp(NotaCredDebCxC notaCred, Inmueble inm, FacCuotaEsp fce) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago cuota esp. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > fce.getSaldoFacCuotaEsp() | notaCred.getSaldoNotaCredDebCxC().equals(fce.getSaldoFacCuotaEsp())) {
            pagoCxCNuevo.setMtoPagoCxC(fce.getSaldoFacCuotaEsp());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        pagoCxCNuevo.setIdFormaPagoCxC(notaCred.getId());

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);

        pagoCxCNuevo.setFecDocPagoCxC(convertirCalendarString(beanUsuario.getPeriodoActual()));

        pagoCxCNuevo.setBancoPagoCxC("N/A");

        pagoCxCNuevo.setPeriodoActual(beanUsuario.getPeriodoActual());

        pagoCxCNuevo.setInmueble(inm);

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFacCxC = generarPagoFacCxC(pagoCxC, fce);

        return pagoFacCxC;
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

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        actualizarEdoFacCuotaEsp(fce, pagoFac);
        return pagoFac;
    }

    public void actualizarEdoFacCuotaEsp(FacCuotaEsp fce, PagoFacCxC pagoFac) throws ValidatorException {

        Double cero = new Double(0);

        if (pagoFac.getMtoPagadoCxC().equals(fce.getSaldoFacCuotaEsp())) {
            fce.setSaldoFacCuotaEsp(cero);
            fce.setEdoFacCuotaEsp("CANC");
        } else {
            fce.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp() - pagoFac.getMtoPagadoCxC());
            if (fce.getSaldoFacCuotaEsp() < 0.01) {
                fce.setSaldoFacCuotaEsp(cero);
                fce.setEdoFacCuotaEsp("CANC");
            } else {
                fce.setEdoFacCuotaEsp("EPRO");
            }
        }

        DaoQuery<FacCuotaEsp> dataAcces = new DaoQuery<FacCuotaEsp>();
        try {
            dataAcces.almacenarOModificarEntidad(fce);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarNotaCredDebCxC(NotaCredDebCxC notaCred) throws ValidatorException {

        Double saldoCero = new Double(0.01);

        if (notaCred.getSaldoNotaCredDebCxC() < saldoCero) {
            notaCred.setSaldoNotaCredDebCxC(0.00);
            notaCred.setEdoNotaCredDebCxC("CANC");
        } else {
            notaCred.setEdoNotaCredDebCxC("EPRO");
        }

        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            dataAcces.almacenarOModificarEntidad(notaCred);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public PagoFacCxC registrarPagoNotaDebito(NotaCredDebCxC notaCred, Inmueble inm, NotaCredDebCxC notaDebito) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago nota deb. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > notaDebito.getSaldoNotaCredDebCxC() | notaCred.getSaldoNotaCredDebCxC().equals(notaDebito.getSaldoNotaCredDebCxC())) {
            pagoCxCNuevo.setMtoPagoCxC(notaDebito.getSaldoNotaCredDebCxC());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        pagoCxCNuevo.setIdFormaPagoCxC(notaCred.getId());

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

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFac = generarPagoFacCxC(pagoCxC, notaDebito);
        return pagoFac;
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

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        actualizarEdoNotaCredDebCxC(notaDebito, pagoFac);
        return pagoFac;
    }

    public void actualizarEdoNotaCredDebCxC(NotaCredDebCxC notaDebito, PagoFacCxC pagoFac) throws ValidatorException {

        Double cero = new Double(0);

        if (pagoFac.getMtoPagadoCxC().equals(notaDebito.getSaldoNotaCredDebCxC())) {
            notaDebito.setSaldoNotaCredDebCxC(cero);
            notaDebito.setEdoNotaCredDebCxC("CANC");
        } else {
            notaDebito.setSaldoNotaCredDebCxC(notaDebito.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC());
            if (notaDebito.getSaldoNotaCredDebCxC() < 0.01) {
                notaDebito.setSaldoNotaCredDebCxC(cero);
                notaDebito.setEdoNotaCredDebCxC("CANC");
            } else {
                notaDebito.setEdoNotaCredDebCxC("EPRO");
            }
        }
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            dataAcces.almacenarOModificarEntidad(notaDebito);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public PagoFacCxC registrarPagoDeudaPend(NotaCredDebCxC notaCred, Inmueble inm, DeudaPendCxC dpend) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago deuda pend. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > dpend.getSaldoDeudaPendCxC() | notaCred.getSaldoNotaCredDebCxC().equals(dpend.getSaldoDeudaPendCxC())) {
            pagoCxCNuevo.setMtoPagoCxC(dpend.getSaldoDeudaPendCxC());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        pagoCxCNuevo.setIdFormaPagoCxC(notaCred.getId());

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

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFac = generarPagoFacCxC(pagoCxC, dpend);
        return pagoFac;
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

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        actualizarEdoDeudaPendCxC(dpend, pagoFac);
        return pagoFac;
    }

    public void actualizarEdoDeudaPendCxC(DeudaPendCxC dpend, PagoFacCxC pagoFac) throws ValidatorException {

        Double cero = new Double(0);

        if (pagoFac.getMtoPagadoCxC().equals(dpend.getSaldoDeudaPendCxC())) {
            dpend.setSaldoDeudaPendCxC(cero);
            dpend.setEdoDeudaPendCxC("CANC");
        } else {
            dpend.setSaldoDeudaPendCxC(dpend.getSaldoDeudaPendCxC() - pagoFac.getMtoPagadoCxC());
            if (dpend.getSaldoDeudaPendCxC() < 0.01) {
                dpend.setSaldoDeudaPendCxC(cero);
                dpend.setEdoDeudaPendCxC("CANC");
            } else {
                dpend.setEdoDeudaPendCxC("EPRO");
            }
        }
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dataAcces.almacenarOModificarEntidad(dpend);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public PagoFacCxC registrarPagoFacGNC(NotaCredDebCxC notaCred, Inmueble inm, FacGastoNoComun fgnc) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago gasto no comun. con nota cred");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > fgnc.getSaldoFacGastoNoComun() | notaCred.getSaldoNotaCredDebCxC().equals(fgnc.getSaldoFacGastoNoComun())) {
            pagoCxCNuevo.setMtoPagoCxC(fgnc.getSaldoFacGastoNoComun());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        pagoCxCNuevo.setIdFormaPagoCxC(notaCred.getId());

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

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFac = generarPagoFacCxC(pagoCxC, fgnc);
        return pagoFac;
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

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        actualizarEdoFacGNC(fgnc, pagoFac);
        return pagoFac;
    }

    public void actualizarEdoFacGNC(FacGastoNoComun fgnc, PagoFacCxC pagoFac) throws ValidatorException {

        Double cero = new Double(0);
        Double ceroLogico = 0.01;

        if (pagoFac.getMtoPagadoCxC().equals(fgnc.getSaldoFacGastoNoComun())) {
            fgnc.setSaldoFacGastoNoComun(cero);
            fgnc.setEdoFacGastoNoComun("CANC");
        } else {
            fgnc.setSaldoFacGastoNoComun(fgnc.getSaldoFacGastoNoComun() - pagoFac.getMtoPagadoCxC());
            if (fgnc.getSaldoFacGastoNoComun() < ceroLogico) {
                fgnc.setSaldoFacGastoNoComun(cero);
                fgnc.setEdoFacGastoNoComun("CANC");
            } else {
                fgnc.setEdoFacGastoNoComun("EPRO");
            }
        }
        DaoQuery<FacGastoNoComun> dataAcces = new DaoQuery<FacGastoNoComun>();
        try {
            dataAcces.almacenarOModificarEntidad(fgnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //------------------------------------------------------------------------------------------
        // Se actualiza el saldo deL gasto no comun y el edo general de las facturas
        // esto ultimo se toma en consideracion a la hora de modificar o eliminar el gnc.
        //------------------------------------------------------------------------------------------

        GastoNoComun gnc = fgnc.getGastoNoComun();
        gnc.setSaldoFacGnc(gnc.getSaldoFacGnc() - pagoFac.getMtoPagadoCxC());
        if (gnc.getSaldoFacGnc() < ceroLogico) {
            gnc.setSaldoFacGnc(cero);
            gnc.setEdoGralFac("CANC");
        } else {
            gnc.setEdoGralFac("EPRO");
        }
        DaoQuery<GastoNoComun> dataAcces3 = new DaoQuery<GastoNoComun>();
        try {
            dataAcces3.almacenarOModificarEntidad(gnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public PagoFacCxC registrarPagoAlicuota(NotaCredDebCxC notaCred, Inmueble inm, Alicuota alic) throws ValidatorException {

        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC("Pago alic. con nota cred.");
        pagoCxCNuevo.setNumDocPagoCxC(notaCred.getNumNotaCredDebCxC());

        if (notaCred.getSaldoNotaCredDebCxC() > alic.getSaldoAlicuota() | notaCred.getSaldoNotaCredDebCxC().equals(alic.getSaldoAlicuota())) {
            pagoCxCNuevo.setMtoPagoCxC(alic.getSaldoAlicuota());
        } else {
            pagoCxCNuevo.setMtoPagoCxC(notaCred.getSaldoNotaCredDebCxC());
        }

        pagoCxCNuevo.setFormaPagoCxC("NCR");
        pagoCxCNuevo.setIdFormaPagoCxC(notaCred.getId());

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

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        String nombQuery = "buscarPagoCxCPorId";
        PagoCxC pagoCxC;
        try {
            pagoCxC = dataAcces.buscarEntidadPorId(idPagoCxCNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        PagoFacCxC pagoFac = generarPagoFacCxC(pagoCxC, alic);
        return pagoFac;
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
        // No se generan ndd por menos de Bs 1 , 30-01-2018 JR
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

        //-----------------------------------------------------
        // No se generan ndd por menos de Bs 1 , 30-01-2018 JR
        //-----------------------------------------------------
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
        // No se generan ndd por menos de Bs 1 , 30-01-2018 JR
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
        // No se generan ndd por menos de Bs 1 , 30-01-2018 JR
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
        // No se generan ndd por menos de Bs 1 , 30-01-2018 JR
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
        periodoActual = beanUsuario.getPeriodoActual();

        nroNota = parametro.getProxNotaCredCxC();

        for (Iterator<Inmueble> iter = listaInm.iterator();
                iter.hasNext();) {
            Inmueble inm = (Inmueble) iter.next();
            obtenerAlicuotasInmEnMora(inm);
            for (Iterator<Alicuota> iter1 = listaInmAlicuotas.iterator();
                    iter1.hasNext();) {
                alic = (Alicuota) iter1.next();
                if (alic.getPeriodoActual().before(periodoActual)) {
                    generarNotaDebitoAlicuota(alic, inm, fechaCorte, null);
                }
            }
            obtenerDeudaPendInmEnMora(inm);
            for (Iterator<DeudaPendCxC> iter1 = listaInmDeudaPend.iterator();
                    iter1.hasNext();) {
                dpend = (DeudaPendCxC) iter1.next();
                if (dpend.getFecDeudaPendCxC().before(fechaCorte)) {
                    generarNotaDebitoDeudaPend(dpend, inm, fechaCorte, null);
                }
            }
            obtenerFacCuotaEspInmEnMora(inm);
            for (Iterator<FacCuotaEsp> iter1 = listaInmFacCuotaEsp.iterator();
                    iter1.hasNext();) {
                fce = (FacCuotaEsp) iter1.next();
                if (fce.getPeriodoActual().before(periodoActual)) {
                    generarNotaDebitoFacturaCuotaEsp(fce, inm, fechaCorte, null);
                }
            }
            obtenerNotasDebitoCxCInmEnMora(inm);
            for (Iterator<NotaCredDebCxC> iter1 = listaInmNotasDeb.iterator();
                    iter1.hasNext();) {
                ncd = (NotaCredDebCxC) iter1.next();
                if (convertirFecha(ncd.getFecNotaCredDebCxC()).before(fechaCorte)) {
                    generarNotaDebitoNotaDebitoCxC(ncd, inm, fechaCorte, null);
                }
            }
            obtenerFacGastoNoComunInmEnMora(inm);
            for (Iterator<FacGastoNoComun> iter1 = listaInmFacGNC.iterator();
                    iter1.hasNext();) {
                fgnc = (FacGastoNoComun) iter1.next();
                if (fgnc.getFecFacGNC().before(fechaCorte)) {
                    generarNotaDebitoFacGastoNoComun(fgnc, inm, fechaCorte, null);
                }
            }
            generarNotaDebitoMoraCxCPagadas(inm, cond);
        }

        //----------------------------------------------------------------------
        // Se crean las notas de debito por mora, enviando la lista al dao
        //----------------------------------------------------------------------
        DaoQuery<NotaCredDebCxC> dao = new DaoQuery<NotaCredDebCxC>();

        try {
            dao.almacenarListaEntidades(listaNotaCredDebCxC);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde" + he));
            return;
        }
        listaNotaCredDebCxC.clear();
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

        PagoFacCxC pfcxc;
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
            pfcxc = (PagoFacCxC) iter1.next();

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
                    System.out.println(" No se consigue la factura cuota especial, el id es:" + pfcxc.getId());
                    if (fce.getEdoFacCuotaEsp().equals("CANC")) {
                        generarNotaDebitoFacturaCuotaEsp(fce, inmueble, fechaPago, pfcxc);
                    }
                    break;
                case 5:
                    fgnc = buscarFGNC(pfcxc.getIdFacCxC());
                    if (fgnc.getEdoFacGastoNoComun().equals("CANC")) {
                        generarNotaDebitoFacGastoNoComun(fgnc, inmueble, fechaPago, pfcxc);
                    }
                    break;
            }
        }
        cxcCancel = false;
    }
//-----------------------------------------------
// Generacion del reporte del cierre del periodo 
//-----------------------------------------------

    public void generarReporteCierre(ActionEvent actionEvent) throws ClassNotFoundException,
            SQLException, IOException, JRException, java.io.IOException {

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
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();

        //--------------------------------------------------------
        // Se busca el condominio con sus propiedades, se obtiene 
        // el tipo de facturacion: Alicuota o Monto Fijo.
        //--------------------------------------------------------
        Condominio condominio;
        try {
            condominio = buscarCondominioFetchPropiedades(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        parametro = condominio.getParametro();

        if (parametro == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no han sido registrados los parametros del sistema...cierre cancelado"));
            return;
        }
        String tipoFac = parametro.getTipoFacturacion();
        Calendar periodoAnterior = obtenerUltimoPeriodoCerrado(condominio);

        String periodo = convertirPeriodoAString(periodoAnterior);
        //--------------------------------------------------------------
        // Se obtiene el parametro con la imagen del logo del condominio
        //--------------------------------------------------------------
        String ruta = beanUsuario.getRuta();
        String nombLogo = condominio.getNombLogo();
        InputStream logo = new FileInputStream(ruta + nombLogo);
        //----------------------------------------------------------
        // Se genera el datasource de gastos comunes ordinarios
        // que incluye fondos de reserva y prestaciones, si los hay
        //----------------------------------------------------------
        JRBeanCollectionDataSource dataSourceGastos;

        Collection gastos = buscarGastosComunesOrdPeridoActual(idCond, periodoAnterior);
        Collection fondos = buscarTranFondoPeriodoActual(idCond, periodoAnterior, tipoFac);
        Collection ajustes = buscarAjustesPeriodoActual(idCond, periodoAnterior);
        Collection gastosDT = inicializarGastoComunOrdDT(gastos, fondos, ajustes);

        dataSourceGastos = new JRBeanCollectionDataSource(gastosDT);

        //--------------------------------------------------------
        // Se genera el datasource de alicuotas del periodo actual
        //--------------------------------------------------------
        JRBeanCollectionDataSource dataSourceAlicuotas;

        Collection<AlicuotaDT> alicuotasDT = buscarAlicuotasPeridoActual(condominio, periodoAnterior);

        dataSourceAlicuotas = new JRBeanCollectionDataSource(alicuotasDT);

        //-----------------------------------------------------------------------
        // Se genera el datasource de cxc de cuotas especiales del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceFacCuotaEsp;

        Collection<FacCuotaEspDT> facCuotaEspDT = buscarFacCuotaEspPeridoActual(condominio, periodoAnterior);

        dataSourceFacCuotaEsp = new JRBeanCollectionDataSource(facCuotaEspDT);
        //-----------------------------------------------------------------------

        // Se genera el datasource de pagos automaticos de cxc del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourcePagosCxC;

        Collection<PagoCxCDTR> pagosCxCDTR = buscarPagosAutomaticosPeridoActual(condominio, periodoAnterior);

        dataSourcePagosCxC = new JRBeanCollectionDataSource(pagosCxCDTR);
        //-----------------------------------------------------------------------

        // Se genera el datasource de notas de débito de cxc del periodo actual
        //-----------------------------------------------------------------------
        JRBeanCollectionDataSource dataSourceNotasDebCxC;

        Collection<NotaDebitoCxCDT> notasDebitoCxCDT = buscarNotasDebitoMoraPeridoActual(condominio, periodoAnterior);

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

    public Calendar obtenerUltimoPeriodoCerrado(Condominio condominio) {

        Calendar perAct = condominio.getPeriodoActual();

        int mesPerAct = perAct.get(Calendar.MONTH);
        int añoPerAct = perAct.get(Calendar.YEAR);

        int mesPerAnt = mesPerAct - 1;
        int añoPerAnt = añoPerAct;

        if (mesPerAnt == 0) {
            mesPerAnt = 12;
            añoPerAnt = añoPerAnt - 1;
        }

        int diaPerAnt = new Integer(01);
        Calendar fecha = new GregorianCalendar(añoPerAnt, mesPerAnt, diaPerAnt);
        return fecha;
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

    private Collection<TranFondo> buscarTranFondoPeriodoActual(Integer idCond, Calendar periodoActual, String tipoFac) {

        //---------------------------------------------------------------------------
        // En caso de que el tipo de facturacion sea por alicuota, se verifica la
        // existencia del fondo de reserva, si existe, se pasa a la coleccion la 
        // transaccion correspondiente al periodo actual. Lo mismo aplica para el
        // fondo de prestaciones
        //---------------------------------------------------------------------------
        Collection<TranFondo> fondos = new ArrayList<TranFondo>();
        Collection<TranFondo> prestaciones = new ArrayList<TranFondo>();

        if (tipoFac.equals("gastos")) {
            Fondo fondo = null;
            fondo = buscarFondoReserva(idCond);
            if (fondo != null) {
                TranFondo tranFondoRes = buscarTranFondoReserva(fondo.getId(), periodoActual);
                if (tranFondoRes != null) {
                    fondos.add(tranFondoRes);
                }
            }

        }

        Fondo fondoPres = null;
        fondoPres = buscarFondoPrestaciones(idCond);

        if (fondoPres != null) {
            prestaciones = buscarTranFondoPrestaciones(fondoPres.getId(), periodoActual);
            if (prestaciones != null) {
                for (Iterator<TranFondo> it = prestaciones.iterator();
                        it.hasNext();) {
                    TranFondo tranFondoPres = (TranFondo) it.next();
                    fondos.add(tranFondoPres);
                }
            }
        }

        return fondos;
    }

    private TranFondo buscarTranFondoReserva(Integer idFondoRes, Calendar periodoActual) {

        TranFondo tranFondo = null;
        Collection<TranFondo> fondoRes;

        String nombQuery = "buscarTranFondoResPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            fondoRes = dataAcces.buscarListaEntidadesPeriodoActual(idFondoRes, periodoActual, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        for (Iterator<TranFondo> it = fondoRes.iterator();
                it.hasNext();) {
            tranFondo = (TranFondo) it.next();
            break;
        }
        return tranFondo;
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

    private Collection<GastoComunDT> inicializarGastoComunOrdDT(Collection listaGCOrd, Collection fondos, Collection ajustes) {

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
        for (Iterator<TranFondo> it = fondos.iterator();
                it.hasNext();) {
            TranFondo tranfondo = (TranFondo) it.next();

            Integer cero = new Integer(0);

            GastoComunDT gastoComunOrdDT = new GastoComunDT();

            gastoComunOrdDT.setId(tranfondo.getId());
            gastoComunOrdDT.setIdProveedor(0);

            if (tranfondo.getIdConceptoTranFondo().equals(cero)) {// Es un fondo de reserva, se genera con id=0 en el cierre
                gastoComunOrdDT.setIdConcepto(0);
                gastoComunOrdDT.setNombConcepto("Res. mes actual");
                gastoComunOrdDT.setNombProveedor("Reserva");

            } else {
                Integer idConc = tranfondo.getIdConceptoTranFondo();
                String nombConc = buscarNombConcepto(idConc);

                gastoComunOrdDT.setIdConcepto(idConc);
                gastoComunOrdDT.setNombConcepto(nombConc);
                gastoComunOrdDT.setNombProveedor("Prestaciones");
                gastoComunOrdDT.setNumDocGastoComun(tranfondo.getDocTranFondo());
            }

            gastoComunOrdDT.setFecGastoComun(tranfondo.getFecTranFondo());
            gastoComunOrdDT.setMtoGastoComun(tranfondo.getMtoTranFondo());
            gastoComunOrdDT.setObsGastoComun(tranfondo.getDescTranFondo());
            gastoComunOrdDT.setEdoGastoComun(tranfondo.getEstadoTranFondo());
            gastoComunOrdDT.setSaldoGastoComun(tranfondo.getSaldoTranFondo());

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

    private Collection<AlicuotaDT> buscarAlicuotasPeridoActual(Condominio condominio, Calendar periodoActual) {

        Collection<AlicuotaDT> lista = new ArrayList<AlicuotaDT>();
        Collection<Alicuota> alicuotas;
        Collection<AlicuotaDT> alicuotasDT;
        String nombQuery = "buscarAlicuotasPeriodoActual";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                alicuotas = dataAcces.buscarListaFetchPorId(idProp, periodoActual, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            alicuotasDT = inicializarAlicuotasDT(alicuotas, nombProp);
            lista.addAll(alicuotasDT);
            alicuotas.clear();
            alicuotasDT.clear();
        }
        return lista;
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

    private Collection<AlicuotaDT> inicializarAlicuotasDT(Collection alicuotas, String nombProp) {

        Collection<AlicuotaDT> alicuotasDT = new ArrayList<AlicuotaDT>();

        for (Iterator<Alicuota> it = alicuotas.iterator();
                it.hasNext();) {
            Alicuota alic = (Alicuota) it.next();

            AlicuotaDT alicuotaDT = new AlicuotaDT();

            alicuotaDT.setNombPropiedad(nombProp);
            alicuotaDT.setCodInmueble(alic.getInmueble().getCodInmueble());
            alicuotaDT.setDesAlicuota(alic.getDesAlicuota());
            alicuotaDT.setMtoAlicuota(alic.getMtoAlicuota());
            alicuotaDT.setSaldoAlicuota(alic.getSaldoAlicuota());
            alicuotaDT.setEdoAlicuota(alic.getEdoAlicuota());

            alicuotasDT.add(alicuotaDT);
        }

        return alicuotasDT;
    }

    private Collection<FacCuotaEspDT> buscarFacCuotaEspPeridoActual(Condominio condominio, Calendar periodoActual) {

        Collection<FacCuotaEspDT> lista = new ArrayList<FacCuotaEspDT>();
        Collection<FacCuotaEsp> facCuotaEsp;
        Collection<FacCuotaEspDT> facCuotaEspDT;
        String nombQuery = "buscarFacCuotaEspPeriodoActual";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                facCuotaEsp = dataAcces.buscarListaFetchPorId(idProp, periodoActual, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            facCuotaEspDT = inicializarFacCuotaEspDT(facCuotaEsp, nombProp);
            lista.addAll(facCuotaEspDT);
            facCuotaEspDT.clear();
            facCuotaEsp.clear();
        }
        return lista;
    }

    private Collection<FacCuotaEspDT> inicializarFacCuotaEspDT(Collection facCuotaEsp, String nombProp) {

        Collection<FacCuotaEspDT> facturasDT = new ArrayList<FacCuotaEspDT>();

        for (Iterator<FacCuotaEsp> it = facCuotaEsp.iterator();
                it.hasNext();) {
            FacCuotaEsp fce = (FacCuotaEsp) it.next();

            FacCuotaEspDT facCuotaEspDT = new FacCuotaEspDT();

            facCuotaEspDT.setNombPropiedad(nombProp);
            facCuotaEspDT.setCodInmueble(fce.getInmueble().getCodInmueble());
            facCuotaEspDT.setDesFacCuotaEsp(fce.getDesFacCuotaEsp());
            facCuotaEspDT.setMtoFacCuotaEsp(fce.getMtoFacCuotaEsp());
            facCuotaEspDT.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp());
            facCuotaEspDT.setEdoFacCuotaEsp(fce.getEdoFacCuotaEsp());

            facturasDT.add(facCuotaEspDT);
        }
        return facturasDT;
    }

    private Collection<PagoCxCDTR> buscarPagosAutomaticosPeridoActual(Condominio condominio, Calendar periodoActual) {

        Collection<PagoCxCDTR> lista = new ArrayList<PagoCxCDTR>();
        Collection<PagoCxC> pagoCxC;
        Collection<PagoCxCDTR> pagoCxCDTR;
        String nombQuery = "buscarPagosAutomaticosPeriodoActual";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                pagoCxC = dataAcces.buscarListaFetchPorId(idProp, periodoActual, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }
            pagoCxCDTR = inicializarPagosCxCDTR(pagoCxC, nombProp);
            lista.addAll(pagoCxCDTR);
            pagoCxCDTR.clear();
            pagoCxC.clear();
        }
        return lista;
    }

    private Collection<PagoCxCDTR> inicializarPagosCxCDTR(Collection pagoCxC, String nombProp) {

        Collection<PagoCxCDTR> pagosCxCDTR = new ArrayList<PagoCxCDTR>();

        for (Iterator<PagoCxC> it = pagoCxC.iterator();
                it.hasNext();) {
            PagoCxC pcxc = (PagoCxC) it.next();

            PagoCxCDTR pagoCxCDTR = new PagoCxCDTR();

            pagoCxCDTR.setNombPropiedad(nombProp);
            pagoCxCDTR.setCodInmueble(pcxc.getInmueble().getCodInmueble());
            pagoCxCDTR.setDesPagoCxC(pcxc.getDesPagoCxC());
            pagoCxCDTR.setMtoPagoCxC(pcxc.getMtoPagoCxC());
            pagoCxCDTR.setFormaPagoCxC(pcxc.getFormaPagoCxC());

            pagosCxCDTR.add(pagoCxCDTR);
        }
        return pagosCxCDTR;
    }

    private Collection<NotaDebitoCxCDT> buscarNotasDebitoMoraPeridoActual(Condominio condominio, Calendar periodoActual) {

        Collection<NotaDebitoCxCDT> lista = new ArrayList<NotaDebitoCxCDT>();
        Collection<NotaCredDebCxC> notaCredDebCxC;
        Collection<NotaDebitoCxCDT> notaDebitoCxCDT;
        String nombQuery = "buscarNotasDebitoPorMoraPeriodoActual";

        for (Iterator<Propiedad> it = condominio.propiedades.iterator();
                it.hasNext();) {
            Propiedad prop = (Propiedad) it.next();
            Integer idProp = prop.getId();
            String nombProp = prop.getNombPropiedad();
            DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
            try {
                notaCredDebCxC = dataAcces.buscarListaFetchPorId(idProp, periodoActual, nombQuery);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return (null);
            }

            notaDebitoCxCDT = inicializarNotasDebCxCDT(notaCredDebCxC, nombProp);

            lista.addAll(notaDebitoCxCDT);
            notaCredDebCxC.clear();
            notaDebitoCxCDT.clear();
        }
        return lista;
    }

    private Collection<NotaDebitoCxCDT> inicializarNotasDebCxCDT(Collection notasDebCxC, String nombProp) {

        Collection<NotaDebitoCxCDT> notaDebDT = new ArrayList<NotaDebitoCxCDT>();

        for (Iterator<NotaCredDebCxC> it = notasDebCxC.iterator();
                it.hasNext();) {

            NotaCredDebCxC ndc = (NotaCredDebCxC) it.next();

            NotaDebitoCxCDT ndcDT = new NotaDebitoCxCDT();

            ndcDT.setNombPropiedad(nombProp);
            ndcDT.setCodInmueble(ndc.getInmueble().getCodInmueble());
            ndcDT.setDescNotaDebCxC(ndc.getDescNotaCredDebCxC());
            ndcDT.setMtoNotaDebCxC(ndc.getMtoNotaCredDebCxC());

            notaDebDT.add(ndcDT);
        }

        return notaDebDT;
    }
}
