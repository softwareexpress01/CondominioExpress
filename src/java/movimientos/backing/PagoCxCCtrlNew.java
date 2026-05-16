package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.Inmueble;
import java.io.Serializable;
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
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.FacturaCxCDT;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.MovBancario;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoCxC;
import movimientos.modelos.PagoFacCxC;
import movimientos.modelos.TranFondo;

public class PagoCxCCtrlNew extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

    public String idInmueble;
    private String desPagoCxC;
    private String numDocPagoCxC;
    private String mtoPagoCxC;
    private String formaPagoCxC;
    private String fecDocPagoCxC;
    private String bancoPagoCxC;
    private String cuentaPagoCxC;
    private Double saldoPagoCxC = new Double(0);
    private Double PagoCxC = new Double(0);
    private List listaInmuebles = new ArrayList();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private Collection listaBancos = new ArrayList();
    private Collection<FacGastoNoComun> listaFacGNC = new ArrayList<FacGastoNoComun>();
    private Collection<DeudaPendCxC> listaDeudaPend = new ArrayList<DeudaPendCxC>();
    private Collection<Alicuota> listaAlicuota = new ArrayList<Alicuota>();
    private Collection<FacCuotaEsp> listaFacCuotaEsp = new ArrayList<FacCuotaEsp>();
    private Collection<NotaCredDebCxC> listaNotaDebito = new ArrayList<NotaCredDebCxC>();
    private Collection<FacturaCxCDT> listaFacturaCxCDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaFacGNCDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaDeudaPendDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaAlicuotaDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaFacCuotaEspDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaNotaDebitoDT = new ArrayList<FacturaCxCDT>();
    private Collection<FacturaCxCDT> listaDT = new ArrayList<FacturaCxCDT>();
    private Collection<PagoFacCxC> listaPagoFacCesp = new ArrayList<PagoFacCxC>();
    private Collection listaOrd = new ArrayList();
    private Integer idPagoCxC;
    private FacturaCxCDT[] facSeleccionadas;
    private Boolean deshabilitaBco;

    public PagoCxCCtrlNew() {
    }

    public void inicializarPanel() {


        this.setDesPagoCxC(null);
        this.setNumDocPagoCxC(null);
        this.setMtoPagoCxC(null);
        this.setFormaPagoCxC(null);
        this.setFecDocPagoCxC(null);
        this.setBancoPagoCxC(null);
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

    public String emitirReciboPagoCxC() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "recibopago";
    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------

        String cero = "0";
        if (idInmueble.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el inmueble ...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (this.getDesPagoCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la descripción del pago...";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(this.getDesPagoCxC())) {
            mensaje = "La descripción del pago debe ser alfanumérica, por favor intente de nuevo...";
            return mensaje;
        }

        if (!this.getFormaPagoCxC().equals("Efectivo")) {
            if (this.getNumDocPagoCxC().equals(enBlanco)) {
                mensaje = "Por favor, ingrese el número del documento ...";
                return mensaje;
            }
            if (!val.esEntero(this.getNumDocPagoCxC())) {
                mensaje = "El número del documento debe ser un número entero, por favor intente de nuevo...";
                return mensaje;
            }
        }

        //--------------------------------------------------------------------------------------
        if (this.getMtoPagoCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto del pago ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoPagoCxC())) {
            mensaje = "El monto del pago debe ser un número entero o decimal, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFormaPagoCxC().equals("Seleccione")) {
            mensaje = "Por favor, seleccione la forma de  pago ...";
            return mensaje;
        }

        if (this.getFecDocPagoCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la fecha del documento de  pago...";
            return mensaje;
        }
        if (!val.esFecha(this.getFecDocPagoCxC())) {
            mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getBancoPagoCxC().equals(enBlanco)) {
            mensaje = "Por favor, seleccione la el banco ...";
            return mensaje;
        }

        if (!(this.getFormaPagoCxC().equals("Efectivo") | this.getFormaPagoCxC().equals("Cheque"))) {

            Integer idBanco = Integer.parseInt(bancoPagoCxC);
            if (this.getBancoPagoCxC().equals(enBlanco) | idBanco.equals(0)) {
                mensaje = "Por favor, seleccione el Banco para esta operacion...";
                return mensaje;
            }
            Integer idCuenta = Integer.parseInt(cuentaPagoCxC);
            if (this.getCuentaPagoCxC().equals(enBlanco) | idCuenta.equals(0)) {
                mensaje = "Por favor, seleccione la cuenta para esta operacion...";
                return mensaje;
            }
        }

        //--------------------------------------------------------
        // Verifica si selecciono todas o alguna de las facturas 
        //--------------------------------------------------------

        if (facSeleccionadas == null | facSeleccionadas.length == 0) {
            mensaje = "Por favor, seleccione la(s) factura(s) a procesar ...";
            return mensaje;
        }
        return mensaje;
    }

    public Boolean buscarDocumento(String doc) {

        Boolean existe = null;
        String nombQuery = "buscarPagoCxCPorNumDoc";
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

    public Collection getCuentasPorCobrarPorInmueble() {


        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idInmueble == null) {
            idInmueble = "0";
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaFacturaCxCDT;
        }

        listaFacturaCxCDT.clear();
        listaFacGNC.clear();
        listaFacGNCDT.clear();
        listaDeudaPend.clear();
        listaDeudaPendDT.clear();
        listaAlicuota.clear();
        listaAlicuotaDT.clear();
        listaFacCuotaEsp.clear();
        listaFacCuotaEspDT.clear();
        listaNotaDebito.clear();
        listaNotaDebitoDT.clear();
        listaDT.clear();

        listaFacGNC = obtenerFacGastoNoComun();
        listaFacGNCDT = convertirGNCADT(listaFacGNC);
        listaDeudaPend = obtenerFacDeudaPend();
        listaDeudaPendDT = convertirDeudaPendADT(listaDeudaPend);
        listaAlicuota = obtenerAlicuota();
        listaAlicuotaDT = convertirAlicuotaADT(listaAlicuota);
        listaFacCuotaEsp = obtenerFacCuotaEsp();
        listaFacCuotaEspDT = convertirFacCuotaEspADT(listaFacCuotaEsp);
        listaNotaDebito = obtenerNotaDebito();
        listaNotaDebitoDT = convertirNotaDebitoADT(listaNotaDebito);

        listaDT.addAll(listaFacGNCDT);
        listaDT.addAll(listaDeudaPendDT);
        listaDT.addAll(listaAlicuotaDT);
        listaDT.addAll(listaFacCuotaEspDT);
        listaDT.addAll(listaNotaDebitoDT);

        listaFacturaCxCDT = ordenarLista(listaDT);// para ordenar la lista de cxc

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        Integer co = new Integer(0);
        for (Iterator<FacturaCxCDT> it = listaFacturaCxCDT.iterator();// Asigna un id a cada objeto del data table
                it.hasNext();) {
            FacturaCxCDT fcxc = (FacturaCxCDT) it.next();
            co = co + 1;
            fcxc.setIdDT(co.toString());
        }
        return listaFacturaCxCDT;
    }

    public Collection obtenerFacGastoNoComun() {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarFacGastoNoComunPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacDeudaPend() {

        Collection lista = new ArrayList();
        // Obtiene las facturas por gasto no comun para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarFacDeudaPendPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerAlicuota() {

        Collection lista = new ArrayList();
        // Obtiene las alicuotas para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarAlicuotasPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerFacCuotaEsp() {

        Collection lista = new ArrayList();
        // Obtiene las cuotas especiales para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarFacCuotaEspPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection obtenerNotaDebito() {

        Collection lista = new ArrayList();
        // Obtiene las notas de debito para el inmueble dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idInmueble).intValue();
        String nombQuery = "buscarNotasDebitoCxCPorIdInm";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return lista;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<FacturaCxCDT> listaDesOrdenada = new ArrayList<FacturaCxCDT>();

        for (Iterator<FacturaCxCDT> it = lista.iterator();
                it.hasNext();) {
            FacturaCxCDT fcxc = (FacturaCxCDT) it.next();
            listaDesOrdenada.add(fcxc);
        }

        Collections.sort(listaDesOrdenada, new ComparadorCxC());//devuelve la lista ordenada en listaDesOrdenada

        Collection<FacturaCxCDT> coleccionOrdenada = new ArrayList<FacturaCxCDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            FacturaCxCDT fdt = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fdt);
        }
        return coleccionOrdenada;
    }

    private Collection<FacturaCxCDT> convertirGNCADT(Collection listaFacGNC) {

        Collection listaGNCDT = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        for (Iterator<FacGastoNoComun> it = listaFacGNC.iterator();
                it.hasNext();) {

            FacGastoNoComun fac = (FacGastoNoComun) it.next();
            if (fac == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(fac.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fac.getFecFacGNC();
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

                facturacxcDT.setFecFacCxC(stringFechaDp);

                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------

                facturacxcDT.setMtoFacCxC(val.convertirDoubleAString(fac.getMtoFacGastoNoComun()));
                facturacxcDT.setSaldoFacCxC(val.convertirDoubleAString(fac.getSaldoFacGastoNoComun()));

                facturacxcDT.setEdoFacCxC(fac.getEdoFacGastoNoComun());
                facturacxcDT.setDiscFacCxC("Gasto no Comun");
                //----------------------------------------------------------------------------------
                // Se busca el concepto del gasto no comun asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fac.getGastoNoComun().getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturacxcDT.setDesFacCxC(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las cxc que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fac.getEdoFacGastoNoComun().equals("CANC")) {
                    listaGNCDT.add(facturacxcDT);
                }

            }
        }
        return listaGNCDT;
    }

    private Collection<FacturaCxCDT> convertirDeudaPendADT(Collection listaDeudaPend) {

        Collection listaDPendDT = new ArrayList();
        for (Iterator<DeudaPendCxC> it = listaDeudaPend.iterator();
                it.hasNext();) {

            DeudaPendCxC dpend = (DeudaPendCxC) it.next();
            if (dpend == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(dpend.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaDPend = dpend.getFecDeudaPendCxC();
                String dia = "";
                String mes = "";
                int diaDpend = fechaDPend.get(Calendar.DAY_OF_MONTH);
                int mesDpend = fechaDPend.get(Calendar.MONTH) + 1;
                int añoDPend = fechaDPend.get(Calendar.YEAR);

                if (diaDpend < 10) {
                    dia = "0" + diaDpend;
                } else {
                    dia = String.valueOf(diaDpend);
                }

                if (mesDpend < 10) {
                    mes = "0" + mesDpend;
                } else {
                    mes = String.valueOf(mesDpend);
                }

                String stringFechaDp = dia + "-" + mes + "-" + añoDPend;

                facturacxcDT.setFecFacCxC(stringFechaDp);

                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                facturacxcDT.setMtoFacCxC(val.convertirDoubleAString(dpend.getMtoDeudaPendCxC()));
                facturacxcDT.setSaldoFacCxC(val.convertirDoubleAString(dpend.getSaldoDeudaPendCxC()));

                facturacxcDT.setEdoFacCxC(dpend.getEdoDeudaPendCxC());
                facturacxcDT.setDiscFacCxC("Deuda pendiente");
                //----------------------------------------------------------------------------------------
                // Se busca el concepto de la deuda pendiente, luego su descripcion para mostrarla en el DT
                //----------------------------------------------------------------------------------------
                Integer idDP = dpend.getIdConcepto();
                Concepto concepto = buscarConcepto(idDP);
                facturacxcDT.setDesFacCxC(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las cxc que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------
                if (!dpend.getEdoDeudaPendCxC().equals("CANC")) {
                    listaDPendDT.add(facturacxcDT);
                }

            }
        }
        return listaDPendDT;
    }

    private Collection<FacturaCxCDT> convertirAlicuotaADT(Collection listaAlicuota) {


        for (Iterator<Alicuota> it = listaAlicuota.iterator();
                it.hasNext();) {

            Alicuota alicuota = (Alicuota) it.next();
            if (alicuota == null) {// Si no hay alicuotas devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(alicuota.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaAlic = alicuota.getFecEmiAlicuota();
                String dia = "";
                String mes = "";
                int diaAlic = fechaAlic.get(Calendar.DAY_OF_MONTH);
                int mesAlic = fechaAlic.get(Calendar.MONTH) + 1;
                int añoAlic = fechaAlic.get(Calendar.YEAR);

                if (diaAlic < 10) {
                    dia = "0" + diaAlic;
                } else {
                    dia = String.valueOf(diaAlic);
                }

                if (mesAlic < 10) {
                    mes = "0" + mesAlic;
                } else {
                    mes = String.valueOf(mesAlic);
                }

                String stringFechaAlic = dia + "-" + mes + "-" + añoAlic;

                facturacxcDT.setFecFacCxC(stringFechaAlic);
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                facturacxcDT.setMtoFacCxC(val.convertirDoubleAString(alicuota.getMtoAlicuota()));
                facturacxcDT.setSaldoFacCxC(val.convertirDoubleAString(alicuota.getSaldoAlicuota()));

                facturacxcDT.setEdoFacCxC(alicuota.getEdoAlicuota());
                facturacxcDT.setDiscFacCxC("Alicuota");
                facturacxcDT.setDesFacCxC(alicuota.getDesAlicuota());

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las cxc que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------
                if (!alicuota.getEdoAlicuota().equals("CANC")) {
                    listaAlicuotaDT.add(facturacxcDT);
                }

            }
        }
        return listaAlicuotaDT;
    }

    private Collection<FacturaCxCDT> convertirFacCuotaEspADT(Collection listaFacCuotasEsp) {

        for (Iterator<FacCuotaEsp> it = listaFacCuotasEsp.iterator();
                it.hasNext();) {

            FacCuotaEsp fce = (FacCuotaEsp) it.next();
            if (fce == null) {// Si no hay facturas de ce devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(fce.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getFecEmiFacCuotaEsp();
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

                facturacxcDT.setFecFacCxC(stringFechaDp);
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                facturacxcDT.setMtoFacCxC(val.convertirDoubleAString(fce.getMtoFacCuotaEsp()));
                facturacxcDT.setSaldoFacCxC(val.convertirDoubleAString(fce.getSaldoFacCuotaEsp()));

                facturacxcDT.setEdoFacCxC(fce.getEdoFacCuotaEsp());
                facturacxcDT.setDiscFacCxC("Cuota Especial");
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la cuota especial asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = fce.getCuotaEsp().getIdConceptoCuotaEsp();
                Concepto concepto = buscarConcepto(idCon);
                facturacxcDT.setDesFacCxC(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista solo las cxc que aun no hayan sido canceladas en su totalidad
                //----------------------------------------------------------------------------------

                if (!fce.getEdoFacCuotaEsp().equals("CANC")) {
                    listaFacCuotaEspDT.add(facturacxcDT);
                }

            }
        }
        return listaFacCuotaEspDT;
    }

    private Collection<FacturaCxCDT> convertirNotaDebitoADT(Collection listaNotaDebito) {

        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {

            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (ncd == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(ncd.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = ncd.getFecMovNCD();
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

                facturacxcDT.setFecFacCxC(stringFechaDp);

                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                Validator val = (Validator) JSFUtil.getManagedObject("Validator");
                facturacxcDT.setMtoFacCxC(val.convertirDoubleAString(ncd.getMtoNotaCredDebCxC()));
                facturacxcDT.setSaldoFacCxC(val.convertirDoubleAString(ncd.getSaldoNotaCredDebCxC()));

                facturacxcDT.setEdoFacCxC(ncd.getEdoNotaCredDebCxC());
                facturacxcDT.setDiscFacCxC("Nota Debito");
                //----------------------------------------------------------------------------------
                // Se busca el concepto de la cuota especial asociado a la factura y luego su
                // descripcion para mostrarlo en el DT
                //----------------------------------------------------------------------------------
                Integer idCon = ncd.getIdConcepto();
                Concepto concepto = buscarConcepto(idCon);
                facturacxcDT.setDesFacCxC(concepto.getDescConcepto());
                //----------------------------------------------------------------------------------
                // Pasa a la lista la factura de cxc : nota de debito
                //----------------------------------------------------------------------------------

                listaNotaDebitoDT.add(facturacxcDT);
            }
        }
        return listaNotaDebitoDT;
    }

    public Concepto buscarConcepto(Integer idCon) throws ValidatorException {

        Concepto concepto = null;
        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idCon, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return concepto;
    }

    public List getInmueblesPorPropiedad() throws Excepcion {


        listaInmuebles.clear();

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Inmuebles para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idProp = usuarioActual.getIdPropActual();
        List<Inmueble> inmuebles = new ArrayList<Inmueble>();
        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            inmuebles = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (inmuebles.isEmpty()) {// Si no hay inmuebles devuelve la lista en blanco  
            listaInmuebles.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaInmuebles;
        }
        listaInmuebles.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = (Inmueble) inmuebles.get(i);
            listaInmuebles.add(new SelectItem(inm.getId(), inm.getCodInmueble()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaInmuebles;
    }

    public List getBancosPorCondominio() {

        listaBancos.clear();

        //Para deshabilitar la lista desplegable de bancos si el pago es en efectivo o cheque
        if (!(formaPagoCxC == null)) {
            if (formaPagoCxC.equals("Efectivo") | (formaPagoCxC.equals("Cheque"))) {
                setDeshabilitaBco(true);
                return (List) listaBancos;
            } else {
                setDeshabilitaBco(false);
            }
        }

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
        /*--------------------- VERFICACIÃ“N DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        if (bancos.isEmpty()) {
            listaBancos.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaBancos;
        }

        /*---------------------------------------------------------------------------------*/
        listaBancos.add(new SelectItem(new Integer(0), "Seleccione"));
        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listaBancos.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listaBancos;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();

        if (bancoPagoCxC == null) {
            return listaOrd;
        }
        Integer idBcoInteger = Integer.valueOf(bancoPagoCxC).intValue();
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
            listaOrd.add(new SelectItem(new Integer(0), "Sin configurar"));
            return listaOrd;
        }

        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));
        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
        }
        return listaOrd;
    }

    public void actualizarEdoFacGNC(FacturaCxCDT fcxc, PagoFacCxC pagoFac, FacGastoNoComun fgnc) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
        if (pagoFac.getMtoPagadoCxC().equals(mtoFac)) {
            fgnc.setSaldoFacGastoNoComun(cero);
            fgnc.setEdoFacGastoNoComun(edo);
        } else {
            fgnc.setSaldoFacGastoNoComun(fgnc.getSaldoFacGastoNoComun() - pagoFac.getMtoPagadoCxC());
            fgnc.setEdoFacGastoNoComun("EPRO");
        }

        DaoQuery<FacGastoNoComun> dataAcces2 = new DaoQuery<FacGastoNoComun>();
        try {
            dataAcces2.almacenarOModificarEntidad(fgnc);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //------------------------------------------------------------------------------------------
        // Se actualiza el saldo de las facturas por gasto no comun y el edo general de las facturas
        // esto ultimo se toma en consideracion a la hora de modificar o eliminar el gnc.
        //------------------------------------------------------------------------------------------
        GastoNoComun gnc = fgnc.getGastoNoComun();
        gnc.setSaldoFacGnc(gnc.getSaldoFacGnc() - pagoFac.getMtoPagadoCxC());
        if (gnc.getSaldoFacGnc().equals(cero)) {
            gnc.setEdoGralFac(edo);
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

    public void actualizarEdoDeudaPend(FacturaCxCDT fcxc, PagoFacCxC pagoFac, DeudaPendCxC dp) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
        if (pagoFac.getMtoPagadoCxC().equals(mtoFac)) {
            dp.setSaldoDeudaPendCxC(cero);
            dp.setEdoDeudaPendCxC(edo);
        } else {
            dp.setSaldoDeudaPendCxC(dp.getSaldoDeudaPendCxC() - pagoFac.getMtoPagadoCxC());
            dp.setEdoDeudaPendCxC("EPRO");
        }
        DaoQuery<DeudaPendCxC> dataAcces = new DaoQuery<DeudaPendCxC>();
        try {
            dataAcces.almacenarOModificarEntidad(dp);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdoAlicuota(FacturaCxCDT fcxc, PagoFacCxC pagoFac, Alicuota alic) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC());
        if (pagoFac.getMtoPagadoCxC().equals(mtoFac)) {
            alic.setSaldoAlicuota(cero);
            alic.setEdoAlicuota(edo);
        } else {
            alic.setSaldoAlicuota(alic.getSaldoAlicuota() - pagoFac.getMtoPagadoCxC());
            alic.setEdoAlicuota("EPRO");
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

    public void actualizarEdoFacCuotaEsp(FacturaCxCDT fcxc, PagoFacCxC pagoFac, FacCuotaEsp fce) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
        if (pagoFac.getMtoPagadoCxC().equals(mtoFac)) {
            fce.setSaldoFacCuotaEsp(cero);
            fce.setEdoFacCuotaEsp(edo);
        } else {
            fce.setSaldoFacCuotaEsp(fce.getSaldoFacCuotaEsp() - pagoFac.getMtoPagadoCxC());
            fce.setEdoFacCuotaEsp("EPRO");
        }

        DaoQuery<FacCuotaEsp> dataAcces2 = new DaoQuery<FacCuotaEsp>();
        try {
            dataAcces2.almacenarOModificarEntidad(fce);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void actualizarEdoNotaDebito(FacturaCxCDT fcxc, PagoFacCxC pagoFac, NotaCredDebCxC ncd) throws ValidatorException {

        Double cero = new Double(0);
        String edo = "CANC";

        Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
        if (pagoFac.getMtoPagadoCxC().equals(mtoFac)) {
            ncd.setSaldoNotaCredDebCxC(cero);
            ncd.setEdoNotaCredDebCxC(edo);
        } else {
            ncd.setSaldoNotaCredDebCxC(ncd.getSaldoNotaCredDebCxC() - pagoFac.getMtoPagadoCxC());
            ncd.setEdoNotaCredDebCxC("EPRO");
        }
        DaoQuery<NotaCredDebCxC> dataAcces = new DaoQuery<NotaCredDebCxC>();
        try {
            dataAcces.almacenarOModificarEntidad(ncd);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public Double calcularMtoTotalFacSelecciondas() throws ValidatorException {

        Double totalFac = new Double(0);

        for (int i = 0; i < facSeleccionadas.length; i++) {
            FacturaCxCDT fcxc = (FacturaCxCDT) facSeleccionadas[i];

            if (fcxc.getDiscFacCxC().equals("Gasto no Comun")) {
                for (Iterator<FacGastoNoComun> iter = listaFacGNC.iterator();
                        iter.hasNext();) {
                    FacGastoNoComun fgnc = (FacGastoNoComun) iter.next();
                    if (fgnc.getId().equals(fcxc.getId())) {
                        fcxc.setSaldoFacCxC(fgnc.getMtoFacGastoNoComun().toString());
                        break;
                    }
                }
            } else if (fcxc.getDiscFacCxC().equals("Deuda pendiente")) {
                for (Iterator<DeudaPendCxC> iter1 = listaDeudaPend.iterator();
                        iter1.hasNext();) {
                    DeudaPendCxC dp = (DeudaPendCxC) iter1.next();
                    if (dp.getId().equals(fcxc.getId())) {
                        fcxc.setSaldoFacCxC(dp.getSaldoDeudaPendCxC().toString());
                        break;
                    }
                }
            } else if (fcxc.getDiscFacCxC().equals("Alicuota")) {
                for (Iterator<Alicuota> iter1 = listaAlicuota.iterator();
                        iter1.hasNext();) {
                    Alicuota alic = (Alicuota) iter1.next();
                    if (alic.getId().equals(fcxc.getId())) {
                        fcxc.setSaldoFacCxC(alic.getSaldoAlicuota().toString());
                        break;
                    }
                }
            } else if (fcxc.getDiscFacCxC().equals("Cuota Especial")) {
                for (Iterator<FacCuotaEsp> iter = listaFacCuotaEsp.iterator();
                        iter.hasNext();) {
                    FacCuotaEsp fce = (FacCuotaEsp) iter.next();
                    if (fce.getId().equals(fcxc.getId())) {
                        fcxc.setSaldoFacCxC(fce.getSaldoFacCuotaEsp().toString());
                        break;

                    }
                }
            } else if (fcxc.getDiscFacCxC().equals("Nota Debito")) {
                for (Iterator<NotaCredDebCxC> iter1 = listaNotaDebito.iterator();
                        iter1.hasNext();) {
                    NotaCredDebCxC ncd = (NotaCredDebCxC) iter1.next();
                    if (ncd.getId().equals(fcxc.getId())) {
                        fcxc.setSaldoFacCxC(ncd.getSaldoNotaCredDebCxC().toString());
                        break;
                    }
                }

                Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
                totalFac = totalFac + mtoFac;
            }
        }
        return totalFac;

    }

    public void almacenarPagoFacCxC(PagoFacCxC pagoFac) throws ValidatorException {

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery<PagoFacCxC>();
        try {
            dataAcces.almacenarEntidad(pagoFac);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarEdosYSaldos(FacturaCxCDT fcxc, PagoFacCxC pagoFac) {

        //-------------------------------------------------------------------
        // Se actualiza el saldo y el estado de facturas de gasto no comun,
        // deuda pendiente, alicuota, cuota especial, nota de debito
        // asi como tambien, el estado de la cxc correspondiente.
        //-------------------------------------------------------------------
        if (fcxc.getDiscFacCxC().equals("Gasto no Comun")) {
            FacGastoNoComun fgnc = null;
            for (Iterator<FacGastoNoComun> iter = listaFacGNC.iterator();
                    iter.hasNext();) {
                fgnc = (FacGastoNoComun) iter.next();
                if (fgnc.getId().equals(fcxc.getId())) {
                    fcxc.setSaldoFacCxC(fgnc.getMtoFacGastoNoComun().toString());
                    break;
                }
            }
            actualizarEdoFacGNC(fcxc, pagoFac, fgnc);
        } else if (fcxc.getDiscFacCxC().equals("Deuda pendiente")) {
            DeudaPendCxC dp = null;
            for (Iterator<DeudaPendCxC> iter1 = listaDeudaPend.iterator();
                    iter1.hasNext();) {
                dp = (DeudaPendCxC) iter1.next();
                if (dp.getId().equals(fcxc.getId())) {
                    fcxc.setSaldoFacCxC(dp.getSaldoDeudaPendCxC().toString());
                    break;
                }
            }
            actualizarEdoDeudaPend(fcxc, pagoFac, dp);
        } else if (fcxc.getDiscFacCxC().equals("Alicuota")) {
            Alicuota alic = null;
            for (Iterator<Alicuota> iter1 = listaAlicuota.iterator();
                    iter1.hasNext();) {
                alic = (Alicuota) iter1.next();
                if (alic.getId().equals(fcxc.getId())) {
                    fcxc.setSaldoFacCxC(alic.getSaldoAlicuota().toString());
                    break;
                }
            }
            actualizarEdoAlicuota(fcxc, pagoFac, alic);
        } else if (fcxc.getDiscFacCxC().equals("Cuota Especial")) {
            FacCuotaEsp fce = null;
            for (Iterator<FacCuotaEsp> iter = listaFacCuotaEsp.iterator();
                    iter.hasNext();) {
                fce = (FacCuotaEsp) iter.next();
                if (fce.getId().equals(fcxc.getId())) {
                    fcxc.setSaldoFacCxC(fce.getSaldoFacCuotaEsp().toString());
                    break;

                }
            }
            actualizarEdoFacCuotaEsp(fcxc, pagoFac, fce);
        } else if (fcxc.getDiscFacCxC().equals("Nota Debito")) {
            NotaCredDebCxC ncd = null;
            for (Iterator<NotaCredDebCxC> iter1 = listaNotaDebito.iterator();
                    iter1.hasNext();) {
                ncd = (NotaCredDebCxC) iter1.next();
                if (ncd.getId().equals(fcxc.getId())) {
                    fcxc.setSaldoFacCxC(ncd.getSaldoNotaCredDebCxC().toString());
                    break;
                }
            }
            actualizarEdoNotaDebito(fcxc, pagoFac, ncd);
        }
    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoQuery<Parametro> dataAcces = new DaoQuery<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void generarNotaCreditoInm(Double saldo, PagoCxC pagoCxC) throws ValidatorException {

        NotaCredDebCxC notaCreDebCxCNueva = new NotaCredDebCxC();

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        notaCreDebCxCNueva.setFecMovNCD(fecha);
        //----------------------------------------------------------------
        //Se da formato string a la fecha actual 
        //----------------------------------------------------------------

        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int año = fecha.get(Calendar.YEAR);

        String stringFecha = dia + "-" + mes + "-" + año;

        notaCreDebCxCNueva.setFecNotaCredDebCxC(stringFecha);
        notaCreDebCxCNueva.setEdoNotaCredDebCxC("PEND");
        notaCreDebCxCNueva.setTipoNotaCredDebCxC("Credito");

        notaCreDebCxCNueva.setMtoNotaCredDebCxC(saldo);
        notaCreDebCxCNueva.setSaldoNotaCredDebCxC(saldo);
        notaCreDebCxCNueva.setIdConcepto(new Integer(0));

        //-----------------------------------------------------------
        // Se busca el parametro consecutivo de notas de credito/debito
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio = null;

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        Parametro parametro = condominio.getParametro();
        Integer credInt = parametro.getProxNotaCredCxC() + 1;
        String credString = credInt.toString();

        notaCreDebCxCNueva.setNumNotaCredDebCxC(credString);
        parametro.setProxNotaCredCxC(credInt);

        actualizarParametro(parametro);

        notaCreDebCxCNueva.setIdPagoCxC(pagoCxC.getId());//Referencia al pago correspondiente

        Integer idInm = Integer.valueOf(idInmueble);

        Inmueble inmueble = null;//Para buscar el objeto inmueble usa el DaoCrud
        try {
            inmueble = (Inmueble) encontrarEntidadPorId(idInm);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        notaCreDebCxCNueva.setInmueble(inmueble);

        DaoQuery<NotaCredDebCxC> dataAcces1 = new DaoQuery<NotaCredDebCxC>();
        try {
            dataAcces1.almacenarEntidad(notaCreDebCxCNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void generarPagoFacCxC(PagoCxC pagoCxC) throws ValidatorException {

        Double mtoTotalFacSelec = calcularMtoTotalFacSelecciondas();
        PagoCxC = pagoCxC.getMtoPagoCxC();
        saldoPagoCxC = pagoCxC.getMtoPagoCxC();

        for (int i = 0; i < facSeleccionadas.length; i++) {
            FacturaCxCDT fcxc = (FacturaCxCDT) facSeleccionadas[i];
            PagoFacCxC pagoFac = new PagoFacCxC();
            pagoFac.setPagoCxC(pagoCxC);
            //-------------------------------------------------------------------
            // Se asigna un discriminador a cada PagoFacCxC para diferenciarlos        
            //-------------------------------------------------------------------
            if (fcxc.getDiscFacCxC().equals("Gasto no Comun")) {
                pagoFac.setDiscFacCxC("GNC");
            } else if (fcxc.getDiscFacCxC().equals("Deuda pendiente")) {
                pagoFac.setDiscFacCxC("DPE");
            } else if (fcxc.getDiscFacCxC().equals("Alicuota")) {
                pagoFac.setDiscFacCxC("ALI");
            } else if (fcxc.getDiscFacCxC().equals("Cuota Especial")) {
                pagoFac.setDiscFacCxC("CES");
            } else if (fcxc.getDiscFacCxC().equals("Nota Debito")) {
                pagoFac.setDiscFacCxC("NCD");
            }
            //-------------------------------------------------------------------

            pagoFac.setIdFacCxC(fcxc.getId());

            if (PagoCxC.equals(mtoTotalFacSelec)) {
                Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
                pagoFac.setMtoPagadoCxC(mtoFac);
                saldoPagoCxC = saldoPagoCxC - mtoFac;
                almacenarPagoFacCxC(pagoFac);
                actualizarEdosYSaldos(fcxc, pagoFac);
            }
            Double mtoCxC = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
            if (PagoCxC > mtoTotalFacSelec) {
                pagoFac.setMtoPagadoCxC(mtoCxC);
                saldoPagoCxC = saldoPagoCxC - mtoCxC;
                almacenarPagoFacCxC(pagoFac);
                actualizarEdosYSaldos(fcxc, pagoFac);
            }
            Double cero = new Double(0);
            if (PagoCxC < mtoTotalFacSelec) {
                if (saldoPagoCxC.equals(cero)) {
                    break;
                }
                Double diferencia = saldoPagoCxC - mtoCxC;
                if (diferencia > 0) {
                    pagoFac.setMtoPagadoCxC(mtoCxC);
                    saldoPagoCxC = saldoPagoCxC - mtoCxC;
                } else {
                    pagoFac.setMtoPagadoCxC(saldoPagoCxC);
                    saldoPagoCxC = cero;
                }
                almacenarPagoFacCxC(pagoFac);
                actualizarEdosYSaldos(fcxc, pagoFac);
            }
            if (pagoFac.getDiscFacCxC().equals("CES")) {
                listaPagoFacCesp.add(pagoFac);
            }
        }
        if (saldoPagoCxC >= 1) {
            generarNotaCreditoInm(saldoPagoCxC, pagoCxC);
        }

    }

    public void registrarPagoCxC() throws ValidatorException, ParseException {

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

        Boolean existe = buscarDocumento(this.getNumDocPagoCxC());
        if (existe) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ya existe un gasto con el nro. documemto ingresado, por favor, intente de nuevo..."));
            return;
        }
        PagoCxC pagoCxCNuevo = new PagoCxC();

        pagoCxCNuevo.setDesPagoCxC(desPagoCxC);

        if (this.getFormaPagoCxC().equals("Efectivo")) {
            pagoCxCNuevo.setNumDocPagoCxC("N/A");
        } else {
            pagoCxCNuevo.setNumDocPagoCxC(numDocPagoCxC);
        }
        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoPagoCxC).doubleValue();
        //-----------------------------------------------------------
        pagoCxCNuevo.setMtoPagoCxC(mto);
        pagoCxCNuevo.setFormaPagoCxC(formaPagoCxC);
        pagoCxCNuevo.setFecDocPagoCxC(fecDocPagoCxC);
        pagoCxCNuevo.setBancoPagoCxC(bancoPagoCxC);
        pagoCxCNuevo.setCuentaPagoCxC(cuentaPagoCxC);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fecha);

        //-----------------------------------------------------------
        //   Se obtiene el periodo actual y se asigana al pago
        //-----------------------------------------------------------

        Calendar perAct = beanUsuario.getPeriodoActual();

        int mesPerAct = perAct.get(Calendar.MONTH);
        int añoPerAct = perAct.get(Calendar.YEAR);
        int diaPerAct = new Integer(01);

        Calendar periodoPago = new GregorianCalendar(añoPerAct, mesPerAct, diaPerAct);
        pagoCxCNuevo.setPeriodoActual(periodoPago);
        //-----------------------------------------------------------
        //         Se busca el inmueble a asociarle el pago
        //-----------------------------------------------------------
        Integer idInm = Integer.valueOf(idInmueble);

        Inmueble inmueble = null;//Para buscar el objeto inmueble usa el DaoCrud
        try {
            inmueble = (Inmueble) encontrarEntidadPorId(idInm);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        //-----------------------------------------------------------
        pagoCxCNuevo.setInmueble(inmueble);

        DaoQuery<PagoCxC> dataAcces = new DaoQuery<PagoCxC>();
        Integer idPagoCxCNueva;
        try {
            idPagoCxCNueva = dataAcces.almacenarEntidad(pagoCxCNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
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
            return;
        }
        //---------------------------------------------------------------------------
        // Para cada factura seleccionada se genera un objeto PagoFacCxC y se asocia 
        // al pago y a su respectiva factura:GNC,DPend,Alic o CuotaEsp.
        //---------------------------------------------------------------------------

        generarPagoFacCxC(pagoCxC);
        idPagoCxC = pagoCxC.getId();

        //NO genera movimiento bancario para los pagos en efectivo cheque
        if (!(this.getFormaPagoCxC().equals("Efectivo") | this.getFormaPagoCxC().equals("Cheque"))) {
            registrarMovBancario();
        }
        //---------------------------------------------------------------------------
        // Si hay pagos de facturas de cuotas especiales, se genera una transaccion
        // al fondo especial (TranFondo)
        //---------------------------------------------------------------------------
        if (!listaPagoFacCesp.isEmpty()) {
            registrarTranFondo();
        }
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table   

        for (int i = 0; i < facSeleccionadas.length; i++) {
            facSeleccionadas[i] = null;
        }
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        if (saldoPagoCxC >= 1) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El pago correspondiente al documento Nro. " + pagoCxC.getNumDocPagoCxC() + " ha sido registrado satisfactoriamente!"));
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Adicionalmente, debido al monto sobrante,se emitió una nota de crédito a favor del inmueble por Bs."
                    + val.convertirDoubleAString(saldoPagoCxC)));
            if (formaPagoCxC.equals("Efectivo") | formaPagoCxC.equals("Cheque")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Recuerde realizar el depósito del efectivo/cheque en el banco y registrar  el movimiento bancario en el sistema "));
            }
            inicializarPanel();
            return;
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("El pago correspondiente al documento Nro. " + pagoCxC.getNumDocPagoCxC() + " ha sido registrado satisfactoriamente!"));
            if (formaPagoCxC.equals("Efectivo") | formaPagoCxC.equals("Cheque")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Recuerde realizar el depósito del efectivo/cheque en el banco y registrar  el movimiento bancario en el sistema "));
            }
            inicializarPanel();
            return;
        }
    }

    public void registrarTranFondo() {
        //---------------------------------------------------------------------------------
        // En caso de que el pago incluya alguna cuota especial, se genera una transaccion 
        // de ingreso al fondo asociado a la misma, y se actualiza el saldo del mismo.
        //----------------------------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        FacCuotaEsp fcesp = null;
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            fcesp = (FacCuotaEsp) it.next();
            break;
        }
        Integer idFondo = fcesp.getCuotaEsp().getIdFondoCuotaEsp();
        Fondo fondo = buscarFondo(idFondo);// Devuelve el fondo de la cuota especial

        Double cero = new Double(0.0);

        for (Iterator<PagoFacCxC> it = listaPagoFacCesp.iterator();
                it.hasNext();) {
            PagoFacCxC pfcesp = (PagoFacCxC) it.next();

            // Se genera la tranFondo con estado cancelado, se actualiza el fondo
            TranFondo tranFondoNueva = new TranFondo();
            tranFondoNueva.setEstadoTranFondo("CANC");
            tranFondoNueva.setMtoTranFondo(pfcesp.getMtoPagadoCxC());
            tranFondoNueva.setSaldoTranFondo(cero);
            tranFondoNueva.setIdPago(idPagoCxC);
            tranFondoNueva.setIdConceptoTranFondo(buscarIdConceptoFacCuotaEsp(pfcesp.getIdFacCxC()));
            tranFondoNueva.setTipoOperTranFondo("Ingreso");
            tranFondoNueva.setDocTranFondo("N/A");
            tranFondoNueva.setFondo(fondo);
            tranFondoNueva.setTipoOperTranFondo("Auto");
            tranFondoNueva.setIdFondoTranFondo(fondo.getId());
            //-----------------------------------------------------------
            //                  Toma la fecha del servidor 
            //-----------------------------------------------------------
            Calendar fecha = new GregorianCalendar();
            tranFondoNueva.setFecMovTranFondo(fecha);
            tranFondoNueva.setPerTranFondo(beanUsuario.getPeriodoActual());
            tranFondoNueva.setFecTranFondo(convertirCalendarString(fecha));
            registrarTranFondo(tranFondoNueva);
            actualizarFondo(tranFondoNueva, fondo);

        }
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

    public Integer buscarIdConceptoFacCuotaEsp(Integer id) {

        FacCuotaEsp fcesp;
        Integer idCpto = null;
        for (Iterator<FacCuotaEsp> it = listaFacCuotaEsp.iterator();
                it.hasNext();) {
            fcesp = (FacCuotaEsp) it.next();
            if (fcesp.getId().equals(id)) {
                idCpto = fcesp.getCuotaEsp().getIdConceptoCuotaEsp();
            }
        }
        return idCpto;
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

    public void registrarMovBancario() throws ValidatorException, ParseException {


        MovBancario MovBancarioNuevo = new MovBancario();

        Integer idBcoInt = Integer.parseInt(bancoPagoCxC);
        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(cuentaPagoCxC);
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(formaPagoCxC);
        //------------------------------------------------------
        // Conversion de la fecha a Calendar (consolidacion)
        //------------------------------------------------------
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecDocPagoCxC);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        MovBancarioNuevo.setFecMovBancario(cal);

        MovBancarioNuevo.setRefMovBancario(numDocPagoCxC);

        Double mto = Double.valueOf(mtoPagoCxC).doubleValue();
        MovBancarioNuevo.setMtoMovBancario(mto);
        MovBancarioNuevo.setDescMovBancario(desPagoCxC);
        MovBancarioNuevo.setIdPago(idPagoCxC);


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

        /* actualizaciÃ³n del Saldo de la Cuenta. Esta operaciÃ³n aumenta el monto ya que es un pago recibido*/
        /*_______________________________________________________________________________________*/

        Double nuevoSaldo = cuenta.getSaldoActCuenta() + mto;
        cuenta.setSaldoActCuenta(nuevoSaldo);

        DaoCrudAux daoCrud2 = new DaoCrudAux();
        try {

            daoCrud2.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

    }

    public void setIdInmueble(String idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getIdInmueble() {
        return idInmueble;
    }

    public void setDesPagoCxC(String desPagoCxC) {
        this.desPagoCxC = desPagoCxC;
    }

    public String getDesPagoCxC() {
        return desPagoCxC;
    }

    public void setNumDocPagoCxC(String numDocPagoCxC) {
        this.numDocPagoCxC = numDocPagoCxC;
    }

    public String getNumDocPagoCxC() {
        return numDocPagoCxC;
    }

    public String getMtoPagoCxC() {
        return mtoPagoCxC;
    }

    public void setMtoPagoCxC(String mtoPagoCxC) {
        this.mtoPagoCxC = mtoPagoCxC;
    }

    public String getFormaPagoCxC() {
        return formaPagoCxC;
    }

    public void setFormaPagoCxC(String formaPagoCxC) {
        this.formaPagoCxC = formaPagoCxC;
    }

    public String getFecDocPagoCxC() {
        return fecDocPagoCxC;
    }

    public void setFecDocPagoCxC(String fecDocPagoCxC) {
        this.fecDocPagoCxC = fecDocPagoCxC;
    }

    public void setBancoPagoCxC(String bancoPagoCxC) {
        this.bancoPagoCxC = bancoPagoCxC;
    }

    public String getCuentaPagoCxC() {
        return cuentaPagoCxC;
    }

    public void setCuentaPagoCxC(String cuentaPagoCxC) {
        this.cuentaPagoCxC = cuentaPagoCxC;
    }

    public String getBancoPagoCxC() {
        return bancoPagoCxC;
    }

    public FacturaCxCDT[] getFacSeleccionadas() {
        return facSeleccionadas;
    }

    public void setFacSeleccionadas(FacturaCxCDT[] facSelecciondas) {
        this.facSeleccionadas = facSelecciondas;
    }

    public Boolean getDeshabilitaBco() {
        return deshabilitaBco;
    }

    public void setDeshabilitaBco(Boolean deshabilitaBco) {
        this.deshabilitaBco = deshabilitaBco;
    }
}
