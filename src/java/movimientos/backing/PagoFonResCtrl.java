package movimientos.backing;

import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import configuracion.modelos.Parametro;
import java.io.Serializable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import movimientos.modelos.PagoFonRes;
import movimientos.modelos.PagoFacFonRes;
import movimientos.modelos.MovBancario;
import movimientos.modelos.PagoFonResDT;
import movimientos.modelos.TranFondo;

import org.hibernate.HibernateException;
import util.*;

public class PagoFonResCtrl extends DaoCrud<PagoFonRes, TranFondo, Integer> implements Serializable {

    private String desPagoFonRes;
    private String numDocPagoFonRes;
    private String mtoPagoFonRes;
    private String formaPagoFonRes;
    private String fecDocPagoFonRes;
    private String idBcoPagoFonRes;
    private String idCuentaPagoFonRes;
    private Double saldoPagoFonRes = new Double(0);
    private Double PagoFonRes = new Double(0);
    private Collection<TranFondo> listaFonRes = new ArrayList<TranFondo>();
    private Collection<PagoFonResDT> listaFonResDT = new ArrayList<PagoFonResDT>();
    private Collection<PagoFonResDT> listaDT = new ArrayList<PagoFonResDT>();
    private Collection listBan = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private PagoFonResDT[] facSeleccionadas;
    Integer idFondo;
    Integer idPagoFonRes;
    Fondo fondo;

    public PagoFonResCtrl() {
    }

    public void inicializarPanel() {


        this.setDesPagoFonRes(null);
        this.setNumDocPagoFonRes(null);
        this.setMtoPagoFonRes(null);
        this.setFormaPagoFonRes(null);
        this.setFecDocPagoFonRes(null);
        this.setIdBcoPagoFonRes(null);
    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        if (this.getDesPagoFonRes().equals(enBlanco)) {
            mensaje = "La Descripción del pago está en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(desPagoFonRes)) {
            mensaje = "La descripción del pago debe ser alfanumérica, por favor intente de nuevo...";
            return mensaje;
        }
        if (!this.getFormaPagoFonRes().equals("Efectivo")) {

            if (this.getNumDocPagoFonRes().equals(enBlanco)) {
                mensaje = "Por favor, ingrese el numero del documento ...";
                return mensaje;
            }

            if (!val.esEntero(numDocPagoFonRes)) {
                mensaje = "El campo Nro. Doc/Ref debe ser un número entero, por favor intente de nuevo...";
                return mensaje;
            }
        }

        //--------------------------------------------------------------------------------------
        if (this.getMtoPagoFonRes().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto del pago ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoPagoFonRes())) {
            mensaje = "El campo Monto debe ser numérico. Use punto decimal cuando lo requiera, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFormaPagoFonRes().equals(enBlanco)) {
            mensaje = "Por favor, seleccione la forma de  pago ...";
            return mensaje;
        }

        if (this.getFecDocPagoFonRes().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento de  pago...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDocPagoFonRes())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        // -------------------------- Validar que la fecha sea mayor o igual a la actual 

        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------

        Calendar fechaHoy = new GregorianCalendar();

        //-----------------------------------------------------------
        //          Convierte la fecha de Sgtring a calendar 
        //-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDocPagoFonRes);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //-----------------------------------------------------------

        if (!cal.before(fechaHoy) | cal.equals(fechaHoy)) {
            mensaje = "La fecha del pago debe ser menor o igual a la fecha de hoy, por favor intente de nuevo...";
            return mensaje;
        }

        // ----------------------------------------------------------------------------

        Integer idBanco = Integer.parseInt(idBcoPagoFonRes);
        if (this.getIdBcoPagoFonRes().equals(enBlanco) | idBanco.equals(0)) {
            mensaje = "Por favor, seleccione el Banco para esta operación...";
            return mensaje;
        }
        Integer idCuenta = Integer.parseInt(idCuentaPagoFonRes);
        if (this.getIdCuentaPagoFonRes().equals(enBlanco) | idCuenta.equals(0)) {
            mensaje = "Por favor, seleccione la cuenta para esta operación...";
            return mensaje;
        }

        //--------------------------------------------------------
        // Verifica si selecciono todas o alguna de las facturas 
        //--------------------------------------------------------

        if (facSeleccionadas == null | facSeleccionadas.length == 0) {
            mensaje = "Por favor, seleccione la(s) factura(s) a procesar ...";
            return mensaje;
        }

        // valida que el monto del pago no sea mayor que el monto total de las facturas seleccionadas

        Integer i = 0;
        double montoFacturas = 0;
        double mtoFacFonResDouble;
        Integer seleccionadas = 0;
        seleccionadas = facSeleccionadas.length;
        String monto;

        while (i < seleccionadas) {


            PagoFonResDT fFonRes = (PagoFonResDT) facSeleccionadas[i];
            monto = fFonRes.getSaldoPagoFonRes();
            mtoFacFonResDouble = Double.parseDouble(monto);
            montoFacturas = montoFacturas + mtoFacFonResDouble;

            i++;

        }

        double mtoPago = Double.parseDouble(mtoPagoFonRes);
        if (mtoPago > montoFacturas) {
            mensaje = "El Monto del Pago no puede ser mayor que el saldo total de las facturas seleccionadas, Por favor, revise...";
        }

        return mensaje;
    }

    public Boolean buscarDocumento(String doc) {

        Boolean existe = null;

        String nombQuery = "buscarPagoFonResPorNumDoc";
        DaoQuery<Boolean> dataAcces = new DaoQuery<Boolean>();
        try {
            existe = dataAcces.buscarEntidadExiste(doc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return existe;
    }

    public Collection getFondosDeReservaPorPagar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaFonResDT;
        }

        listaFonResDT.clear();
        listaFonRes.clear();
        listaDT.clear();

        listaFonRes = obtenerFonRes();
        listaFonResDT = convertirFonResADT(listaFonRes);

        listaDT = ordenarLista(listaFonResDT);// para ordenar la lista de FonRes

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        /*Integer co = new Integer(0);
         for (Iterator<PagoFonResDT> it = listaDT.iterator();// Asigna un id a cada objeto del data table
         it.hasNext();) {
         PagoFonResDT fFonRes = (PagoFonResDT) it.next();
         co = co + 1;
         fFonRes.setId(co);
         }*/
        return listaDT;
    }

    public List getBancosPorCondominio() {

        listBan.clear();
        listBan.add(new SelectItem(new Integer(0), "Seleccione"));
        List bancos = new ArrayList();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de bancos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            bancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        if (bancos.isEmpty()) {

            listBan.clear();
            listBan.add(new SelectItem(new Integer(0), "Sin Configurar"));
            return (List) listBan;

        }

        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listBan;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBcoPagoFonRes == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.valueOf(idBcoPagoFonRes).intValue();
        if (idBcoInteger == 0) {
            return listaOrd;
        }

        String nombQuery = "buscarCuentasPorIdBanco";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            cuentas = dataAcces.buscarListaEntidadesPorId(idBcoInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return listaOrd;
        }


        if (cuentas.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            return listaOrd;

        }

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            if (!(cta.getEdoCuenta().equals("INA"))) {
                listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
            }
        }
        return listaOrd;
    }

    public Collection obtenerFonRes() {

        Collection<TranFondo> lista = new ArrayList<TranFondo>();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = usuarioActual.getIdCondActual();


        String nombQuery = "buscarTranFondosReservaPeriodoActual";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return lista;
        }

        return lista;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoFonResDT> listaDesOrdenada = new ArrayList<PagoFonResDT>();

        for (Iterator<PagoFonResDT> it = lista.iterator();
                it.hasNext();) {
            PagoFonResDT fFonRes = (PagoFonResDT) it.next();
            listaDesOrdenada.add(fFonRes);
        }

        Collections.sort(listaDesOrdenada, new ComparadorPagoFonRes());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoFonResDT> coleccionOrdenada = new ArrayList<PagoFonResDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoFonResDT fdt = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    private Collection<PagoFonResDT> convertirFonResADT(Collection listaFonRes) {

        listaFonResDT.clear();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (listaFonRes.isEmpty()) {
            return listaFonResDT;
        }

        for (Iterator<TranFondo> it = listaFonRes.iterator();
                it.hasNext();) {

            TranFondo fce = (TranFondo) it.next();
            if (fce == null) {
                break;
            } else {

                PagoFonResDT pagoFonResDT = new PagoFonResDT();

                pagoFonResDT.setId(fce.getId());

                pagoFonResDT.setMtoPagoFonRes(val.convertirDoubleAString(fce.getMtoTranFondo()));
                pagoFonResDT.setSaldoPagoFonRes(val.convertirDoubleAString(fce.getSaldoTranFondo()));

                pagoFonResDT.setDesPagoFonRes(fce.getDescTranFondo());
                pagoFonResDT.setFecEmiPagoFonRes(fce.getFecTranFondo());
                pagoFonResDT.setNumDocPagoFonRes(fce.getDocTranFondo());
                pagoFonResDT.setNumDocPagoFonRes(fce.getDocTranFondo());
                pagoFonResDT.setFormaPagoFonRes(fce.getFormaPagoTranFondo());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getFecMovTranFondo();
                String dia = "";
                String mes = "";
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

                String stringFechaDp = dia + "-" + mes + "-" + añoFac;

                pagoFonResDT.setFecMovPagoFonRes(stringFechaDp);

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las FonRes que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fce.getEstadoTranFondo().equals("CANC")) {
                    listaFonResDT.add(pagoFonResDT);
                }

            }
        }
        return listaFonResDT;
    }

    public void actualizarEdoTranFondo(PagoFonResDT fFonRes, PagoFacFonRes pagoFac) throws ValidatorException, ParseException {

        Integer idPago = pagoFac.getPagoFonRes().getId();
        Double cero = new Double(0);
        String edo = "CANC";
        for (Iterator<TranFondo> iter = listaFonRes.iterator();
                iter.hasNext();) {

            TranFondo fce = (TranFondo) iter.next();


            if (fce.getId().equals(fFonRes.getId())) {
                Double mtoFac = Double.valueOf(fFonRes.getSaldoPagoFonRes()).doubleValue();
                if (pagoFac.getMtoPagadoFonRes().equals(mtoFac)) {
                    fce.setSaldoTranFondo(cero);
                    fce.setEstadoTranFondo(edo);
                } else {
                    fce.setSaldoTranFondo(fce.getSaldoTranFondo() - pagoFac.getMtoPagadoFonRes());
                    fce.setEstadoTranFondo("EPRO");
                }

                fce.setIdPago(pagoFac.getIdFacFonRes());


                DaoQuery<TranFondo> dataAcces2 = new DaoQuery<TranFondo>();
                try {
                    dataAcces2.almacenarOModificarEntidad(fce);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }


                //************************* ACTUALIZACION DEL MONTO DEL FONDO RELACIONADO ******************************

                BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
                Integer idCond = beanUsuario.getIdCondActual();
                Integer idFondo = fce.getIdFondoTranFondo();

                String nombQuery = "buscarFondoReservaPorIdCond";
                DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
                try {
                    fondo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
                fondo.setSaldoFondo(fondo.getSaldoFondo() + pagoFac.getMtoPagadoFonRes());
                DaoQuery<Fondo> dataAcces3 = new DaoQuery<Fondo>();
                try {
                    dataAcces3.almacenarOModificarEntidad(fondo);
                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }

                break;
            }
        }


    }

    public Double calcularMtoTotalFacSelecciondas() throws ValidatorException {

        Double totalFac = new Double(0);
        for (int i = 0; i < facSeleccionadas.length; i++) {
            PagoFonResDT fFonRes = (PagoFonResDT) facSeleccionadas[i];
            Double mtoFac = Double.valueOf(fFonRes.getSaldoPagoFonRes()).doubleValue();
            totalFac = totalFac + mtoFac;
        }
        return totalFac;
    }

    public void almacenarPagoFacFonRes(PagoFacFonRes pagoFac) throws ValidatorException {

        DaoQuery<PagoFacFonRes> dataAcces = new DaoQuery<PagoFacFonRes>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoQuery<Parametro> dataAcces = new DaoQuery<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public void generarPagoFacFonRes(PagoFonRes pagoFonRes) throws ValidatorException, ParseException {

        Double mtoTotalFacSelec = calcularMtoTotalFacSelecciondas();
        PagoFonRes = pagoFonRes.getMtoPagoFonRes();
        saldoPagoFonRes = pagoFonRes.getMtoPagoFonRes();

        for (int i = 0; i < facSeleccionadas.length; i++) {
            PagoFonResDT fFonRes = (PagoFonResDT) facSeleccionadas[i];
            PagoFacFonRes pagoFac = new PagoFacFonRes();
            pagoFac.setPagoFonRes(pagoFonRes);

            pagoFac.setDiscFacFonRes("FRE"); // Fondo de reserva

            //-------------------------------------------------------------------

            pagoFac.setIdFacFonRes(fFonRes.getId());

            if (PagoFonRes.equals(mtoTotalFacSelec)) {
                Double mtoFac = Double.valueOf(fFonRes.getSaldoPagoFonRes()).doubleValue();
                pagoFac.setMtoPagadoFonRes(mtoFac);
                saldoPagoFonRes = saldoPagoFonRes - mtoFac;
                almacenarPagoFacFonRes(pagoFac);
                actualizarEdoTranFondo(fFonRes, pagoFac);
            }
            Double mtoFonRes = Double.valueOf(fFonRes.getSaldoPagoFonRes()).doubleValue();
            if (PagoFonRes > mtoTotalFacSelec) {
                pagoFac.setMtoPagadoFonRes(mtoFonRes);
                saldoPagoFonRes = saldoPagoFonRes - mtoFonRes;
                almacenarPagoFacFonRes(pagoFac);
                actualizarEdoTranFondo(fFonRes, pagoFac);
            }
            Double cero = new Double(0);
            if (PagoFonRes < mtoTotalFacSelec) {
                if (saldoPagoFonRes.equals(cero)) {
                    break;
                }
                Double diferencia = saldoPagoFonRes - mtoFonRes;
                if (diferencia > 0) {
                    pagoFac.setMtoPagadoFonRes(mtoFonRes);
                    saldoPagoFonRes = saldoPagoFonRes - mtoFonRes;
                } else {
                    pagoFac.setMtoPagadoFonRes(saldoPagoFonRes);
                    saldoPagoFonRes = cero;
                }
                almacenarPagoFacFonRes(pagoFac);
                actualizarEdoTranFondo(fFonRes, pagoFac);
            }
        }


    }

    public void registrarPagoFonRes() throws ValidatorException, ParseException {

        String vacio = new String();

        String mensaje = validarPanel();
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        //----------------------------------------------------------------
        // Verifica si ya existe en la bd, el nro de documento ingresado 
        //----------------------------------------------------------------

        Boolean existe;
        existe = buscarDocumento(numDocPagoFonRes);
        if (existe) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ya existe un pago con el nro. documento ingresado, por favor, intente de nuevo..."));
            return;
        }
        PagoFonRes pagoFonResNuevo = new PagoFonRes();

        pagoFonResNuevo.setDesPagoFonRes(desPagoFonRes);

        if (this.getFormaPagoFonRes().equals("Efectivo")) {
            pagoFonResNuevo.setNumDocPagoFonRes("N/A");
        } else {
            pagoFonResNuevo.setNumDocPagoFonRes(numDocPagoFonRes);
        }


        Calendar periodo = beanUsuario.getPeriodoActual();
        pagoFonResNuevo.setPeriodoActual(periodo);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoPagoFonRes).doubleValue();
        //-----------------------------------------------------------
        pagoFonResNuevo.setMtoPagoFonRes(mto);
        pagoFonResNuevo.setFormaPagoFonRes(formaPagoFonRes);
        pagoFonResNuevo.setFecDocPagoFonRes(fecDocPagoFonRes);
        pagoFonResNuevo.setBancoPagoFonRes(Integer.parseInt(idBcoPagoFonRes));

        pagoFonResNuevo.setIdCuentaPagoFonRes(Integer.valueOf(idCuentaPagoFonRes));
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoFonResNuevo.setFecMovPagoFonRes(fecha);

        // buscar Condominio para establecer el vinculo con PagoFonRes:

        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        pagoFonResNuevo.setCondominio(condominio);

        // Almacenar el pago: 

        DaoQuery<PagoFonRes> dataAcces2 = new DaoQuery<PagoFonRes>();
        Integer idPagoFonResNueva;
        try {
            idPagoFonResNueva = dataAcces2.almacenarEntidad(pagoFonResNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones.por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // busca el pago recien almacenado, necesario para el vinculo con la factura
        //---------------------------------------------------------------------------
        nombQuery = "buscarPagoFonResPorId";
        PagoFonRes pagoFonRes;
        try {
            pagoFonRes = dataAcces2.buscarEntidadPorId(idPagoFonResNueva, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacFonRes y se asocia 
        // al pago y a su respectiva factura.
        //---------------------------------------------------------------------------

        generarPagoFacFonRes(pagoFonRes);


        idPagoFonRes = pagoFonRes.id; // Se conserva el id del pago para incluirlo al crear el mov bancario  
        registrarMovBancario();

        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table   

        inicializarPanel();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago correspondiente al documento Nro. " + pagoFonRes.getNumDocPagoFonRes() + " ha sido registrado satisfactoriamente!"));

    }

    public void registrarMovBancario() throws ValidatorException, ParseException {

        //***************Movimiento bancario de la cuenta con la que se paga (seleccionada en la página) *************//

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idBcoPagoFonRes);
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCuentaPagoFonRes);
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoFonRes);

//-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDocPagoFonRes);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//------------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(numDocPagoFonRes);
        Double mto = Double.valueOf(mtoPagoFonRes).doubleValue();
        Double mtoPago = mto;
        mto = mto * -1;
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario(desPagoFonRes);
        MovBancarioNuevo.setIdPago(idPagoFonRes);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        Cuenta cuenta = null;
        String nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCtaInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioNuevo.setCuenta(cuenta);

        DaoCrudAux daoCrud = new DaoCrudAux();
        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioNuevo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
         /*_______________________________________________________________________________________*/

        Double nuevoSaldo = cuenta.getSaldoActCuenta() - mtoPago;
        cuenta.setSaldoActCuenta(nuevoSaldo);
        DaoQuery<Cuenta> dataAcces2 = new DaoQuery<Cuenta>();

        try {

            dataAcces2.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

        //***************************** Movimiento bancario de la cuenta asociada al fondo de reserva ****************
 
        MovBancario MovBancarioFondo = new MovBancario();

        MovBancarioFondo.setIdBcoMovBancario(fondo.getIdBcoFondo());

        MovBancarioFondo.setIdCtaMovBancario(fondo.getIdCtaFondo());
        MovBancarioFondo.setTipoOperMovBancario(formaPagoFonRes);

        MovBancarioFondo.setFecMovBancario(cal);
        MovBancarioFondo.setRefMovBancario(numDocPagoFonRes);
        mto = Double.valueOf(mtoPagoFonRes).doubleValue();
        mtoPago = mto;
        MovBancarioFondo.setMtoMovBancario(mto);
        MovBancarioFondo.setDescMovBancario(desPagoFonRes);
        MovBancarioFondo.setIdPago(idPagoFonRes);


        //***** buscar cuenta para hacer la asociacion con mov bancario *******

        cuenta = null;
        nombQuery = "buscarCuentaPorId";

        DaoQuery<Cuenta> dataAcces4 = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces4.buscarEntidadPorId(fondo.getIdCtaFondo(), nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        MovBancarioFondo.setCuenta(cuenta);

        try {

            daoCrud.almacenarOModificarEntidad(MovBancarioFondo);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. 
         /*_______________________________________________________________________________________*/

        nuevoSaldo = cuenta.getSaldoActCuenta() + mtoPago;
        cuenta.setSaldoActCuenta(nuevoSaldo);
        DaoQuery<Cuenta> dataAcces5 = new DaoQuery<Cuenta>();

        try {

            dataAcces5.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }

    }

    public void setDesPagoFonRes(String desPagoFonRes) {
        this.desPagoFonRes = desPagoFonRes;
    }

    public String getDesPagoFonRes() {
        return desPagoFonRes;
    }

    public void setNumDocPagoFonRes(String numDocPagoFonRes) {
        this.numDocPagoFonRes = numDocPagoFonRes;
    }

    public String getNumDocPagoFonRes() {
        return numDocPagoFonRes;
    }

    public String getMtoPagoFonRes() {
        return mtoPagoFonRes;
    }

    public void setMtoPagoFonRes(String mtoPagoFonRes) {
        this.mtoPagoFonRes = mtoPagoFonRes;
    }

    public String getFormaPagoFonRes() {
        return formaPagoFonRes;
    }

    public void setFormaPagoFonRes(String formaPagoFonRes) {
        this.formaPagoFonRes = formaPagoFonRes;
    }

    public String getFecDocPagoFonRes() {
        return fecDocPagoFonRes;
    }

    public void setFecDocPagoFonRes(String fecDocPagoFonRes) {
        this.fecDocPagoFonRes = fecDocPagoFonRes;
    }

    public void setIdBcoPagoFonRes(String idBcoPagoFonRes) {
        this.idBcoPagoFonRes = idBcoPagoFonRes;
    }

    public String getIdBcoPagoFonRes() {
        return idBcoPagoFonRes;
    }

    public void setIdCuentaPagoFonRes(String idCuentaPagoFonRes) {
        this.idCuentaPagoFonRes = idCuentaPagoFonRes;
    }

    public String getIdCuentaPagoFonRes() {
        return idCuentaPagoFonRes;
    }

    public PagoFonResDT[] getFacSeleccionadas() {
        return facSeleccionadas;
    }

    public void setFacSeleccionadas(PagoFonResDT[] facSelecciondas) {
        this.facSeleccionadas = facSelecciondas;
    }
}
