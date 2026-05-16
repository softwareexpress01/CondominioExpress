package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import movimientos.modelos.*;
import org.hibernate.HibernateException;
import saas.modelos.Suscripcion;
import util.*;

public class ConExpNotificacionCtrl extends DaoCrud<PagoCxC, Inmueble, Integer> implements Serializable {

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
    private Boolean deshabilita = false;
    private BeanUsuario beanUsuario;
    private double ceroLogico = 0.01;

    public ConExpNotificacionCtrl() {
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

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Inmueble   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------
        String cero = "0";

        //--------------------------------------------------------------------------------------
        if (this.getDesPagoCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese la descripción del pago...";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(this.getDesPagoCxC())) {
            mensaje = "La descripción del pago debe ser alfanumérica, por favor intente de nuevo...";
            return mensaje;
        }
        if (this.getFormaPagoCxC().equals("Seleccione")) {
            mensaje = "Por favor, seleccione la forma de  pago ...";
            return mensaje;
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

        if (!this.getFormaPagoCxC().equals("Efectivo")) {
            if (this.getFecDocPagoCxC().equals(enBlanco)) {
                mensaje = "Por favor, ingrese la fecha del documento de  pago...";
                return mensaje;
            }
            if (!val.esFecha(this.getFecDocPagoCxC())) {
                mensaje = "La fecha del documento debe tener formato dd-mm-aaaa, por favor intente de nuevo...";
                return mensaje;
            }
            Calendar fechaPagoCal = convertirFecha(this.getFecDocPagoCxC());
            if (fechaPagoCal.before(beanUsuario.getPeriodoActual())) {
                mensaje = "La fecha del documento debe ser mayor o igual al período actual, por favor intente de nuevo...";
                return mensaje;
            }
        }

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

        //--------------------------------------------------------
        // Verifica si selecciono todas o alguna de las facturas 
        //--------------------------------------------------------
        if (facSeleccionadas == null | facSeleccionadas.length == 0) {
            mensaje = "Por favor, seleccione la(s) factura(s) a procesar ...";
            return mensaje;
        }
        return mensaje;
    }

    public List getInmueblesPorPropiedad() throws Excepcion {

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        listaInmuebles.clear();

        idInmueble = Integer.toString(beanUsuario.getIdInm());

        Inmueble inmueble = new Inmueble();
        String nombQuery = "buscarInmueblePorId";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        int idInm = Integer.valueOf(idInmueble);
        try {
            inmueble = (Inmueble) dataAcces.buscarEntidadPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (inmueble == null) {// Si no hay inmuebles devuelve la lista en blanco  
            listaInmuebles.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaInmuebles;
        }

        listaInmuebles.add(new SelectItem(inmueble.getId(), inmueble.getCodInmueble()));

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaInmuebles;
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

    public Collection getCuentasPorCobrarPorInmueble() throws ParseException {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------
        if (idInmueble == null) {
            idInmueble = "0";
        }

        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaFacturaCxCDT;
        }

        idInmueble = String.valueOf(beanUsuario.getIdInm());

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
                facturacxcDT.setCalFecFacCxC(fechaFac);                             // para ordenar por esta fecha
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
                facturacxcDT.setCalFecFacCxC(fechaDPend);
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
                Calendar fechaAlic = alicuota.getPeriodoActual();
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
                facturacxcDT.setCalFecFacCxC(fechaAlic);
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
            if (fce == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(fce.getId());
                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrar la en el data table
                //----------------------------------------------------------------
                Calendar fechaFac = fce.getPeriodoActual();
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
                facturacxcDT.setCalFecFacCxC(fechaFac);
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

    private Collection<FacturaCxCDT> convertirNotaDebitoADT(Collection listaNotaDebito) throws ParseException {

        for (Iterator<NotaCredDebCxC> it = listaNotaDebito.iterator();
                it.hasNext();) {

            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            if (ncd == null) {// Si no hay facturas de gnc devuelve la lista en blanco
                break;
            } else {

                FacturaCxCDT facturacxcDT = new FacturaCxCDT();

                facturacxcDT.setId(ncd.getId());

                facturacxcDT.setFecFacCxC(ncd.getFecNotaCredDebCxC());
                facturacxcDT.setCalFecFacCxC(convertirFecha(ncd.getFecNotaCredDebCxC()));
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
                if (idCon == 0) {
                    facturacxcDT.setDesFacCxC(ncd.getDescNotaCredDebCxC());
                } else {
                    Concepto concepto = buscarConcepto(idCon);
                    facturacxcDT.setDesFacCxC(concepto.getDescConcepto());
                }
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

    public List getBancosPorCondominio() {

        listaBancos.clear();

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
        /*
         * --------------------- VERFICACIÃ“N DE SI HAY BANCOS DEFINIDOS
         * ---------------------------------
         */

        if (bancos.isEmpty()) {
            listaBancos.add(new SelectItem(new Integer(0), "Sin configurar"));
            return (List) listaBancos;
        }

        /*
         * ---------------------------------------------------------------------------------
         */
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

    public Double calcularMtoTotalFacSelecciondas() throws ValidatorException {

        Double totalFac = new Double(0);
        for (int i = 0; i < facSeleccionadas.length; i++) {
            FacturaCxCDT fcxc = (FacturaCxCDT) facSeleccionadas[i];
            Double mtoFac = Double.valueOf(fcxc.getSaldoFacCxC()).doubleValue();
            totalFac = totalFac + mtoFac;
        }
        return totalFac;
    }

    public void registrarPagoCxC() throws ValidatorException, ParseException, MessagingException {

        String vacio = new String();

        String mensaje = validarPanel();
        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

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
            pagoCxCNuevo.setNumDocPagoCxC("Efectivo");
            String stringFechaPago = convertirFechaString();
            pagoCxCNuevo.setFecDocPagoCxC(stringFechaPago);
        } else {
            pagoCxCNuevo.setNumDocPagoCxC(numDocPagoCxC);
            pagoCxCNuevo.setFecDocPagoCxC(fecDocPagoCxC);
        }
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fechaHoy = new GregorianCalendar();
        pagoCxCNuevo.setFecMovPagoCxC(fechaHoy);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoPagoCxC).doubleValue();
        //-----------------------------------------------------------
        pagoCxCNuevo.setMtoPagoCxC(mto);
        pagoCxCNuevo.setFormaPagoCxC(formaPagoCxC);

        // Nombre del banco
        Integer idBco = Integer.valueOf(bancoPagoCxC);
        String bco = buscarNombBanco(idBco);
        pagoCxCNuevo.setBancoPagoCxC(bco);

        // Numero de cuebta
        Integer idCta = Integer.valueOf(cuentaPagoCxC);
        String cta = buscarNumCuenta(idCta);
        pagoCxCNuevo.setCuentaPagoCxC(cta);

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

        enviarEmailNotificacion(pagoCxCNuevo, inmueble, facSeleccionadas);

    }

    public void enviarEmailNotificacion(PagoCxC pagoCxCPago, Inmueble inmueble, FacturaCxCDT[] facSeleccionadas) throws MessagingException {

        boolean auth = true;
        boolean debug = true;
        int port = 587;
        /* String host = "condominioexpressve.com";
        final String emailUsuario="recibopagocxc@condominioexpressve.com";
        final String contraseñaUsuario="tango#16";*/

        final String emailUsuario = "condominioexpressve@gmail.com";
        final String contraseñaUsuario = "pish unfc sxxl hzwf";
        String host = "smtp.gmail.com";
        
        /*String host = "us2.smtp.mailhostbox.com";
        final String emailUsuario = "informes@condominioexpressve.com";
        final String contraseñaUsuario = "X^Z%Bmhs7";*/

        String asunto = "Notificación de pago";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);//Protocol.SMTP

        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {

                private final PasswordAuthentication pa = new PasswordAuthentication(emailUsuario, contraseñaUsuario);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);
        // Construir texto del correo  para cada pago 
        Suscripcion sus;
        String para;
        //   BodyPart texto = new MimeBodyPart();

        String msj;
        Condominio cond = buscarCondominio(beanUsuario.getIdCondActual());
        para = cond.getEmailCondominio();
        String facturas = "";
        msj = "Sr(a): Administrador del condominio " + cond.getNombCondominio() + "\n" + "\n"
                + "Hemos recibido una NOTIFICACIÓN DE PAGO correspondiente al Inmueble "
                + inmueble.getCodInmueble() + " con los siguientes datos: " + "\n" + "\n"
                + "     Descripción: " + pagoCxCPago.getDesPagoCxC() + "," + "\n"
                + "     Forma de pago: " + pagoCxCPago.getFormaPagoCxC() + "," + "\n"
                + "     Monto: " + pagoCxCPago.getMtoPagoCxC() + "," + "\n"
                + "     Referencia: " + pagoCxCPago.getNumDocPagoCxC() + "," + "\n"
                + "     Fecha: " + pagoCxCPago.getFecDocPagoCxC() + "," + "\n"
                + "     Banco: " + pagoCxCPago.getBancoPagoCxC() + "," + "\n"
                + "     Cuenta: " + pagoCxCPago.getCuentaPagoCxC() + "\n" + "\n"
                + " Las facturas a cancelar con este pago son las siguientes: " + "\n" + "\n";
        int cont = 0;
        for (int i = 0; i < facSeleccionadas.length; i++) {
            cont = cont + 1;
            facturas = facturas + " " + cont + ". " + "Fecha: " + facSeleccionadas[i].getFecFacCxC() + ", "
                    + "Tipo de Movimiento: " + facSeleccionadas[i].getDiscFacCxC() + ", "
                    + "Saldo factura: " + facSeleccionadas[i].getSaldoFacCxC() + "\n";
        }
        msj = msj + facturas;

        //      texto.setText(msj);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(emailUsuario));
            InternetAddress[] address = {new InternetAddress(para)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setRecipients(Message.RecipientType.BCC, inmueble.getEmailPropInmueble());
            message.setSubject(asunto);
            message.setSentDate(new Date());
            message.setText(msj);

            Transport.send(message);
        } catch (MessagingException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Notificacion de pago enviada. Será procesada una vez que se haya hecho efectivo el depósito/transferencia"));

    }

    public String buscarNombBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarNumCuenta(Integer idCuenta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCuenta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String numCuenta = cuenta.getNumCuenta();
        return numCuenta;
    }

    public String convertirFechaString() {

        Calendar fecha = new GregorianCalendar();
        //----------------------------------------------------------------
        //Se da formato a la fecha del dia para asignarla al pago efectivo
        //----------------------------------------------------------------
        String dia = "";
        String mes = "";
        int diaPago = fecha.get(Calendar.DAY_OF_MONTH);
        int mesPago = fecha.get(Calendar.MONTH) + 1;
        int añoPago = fecha.get(Calendar.YEAR);

        if (diaPago < 10) {
            dia = "0" + diaPago;
        } else {
            dia = String.valueOf(diaPago);
        }

        if (mesPago < 10) {
            mes = "0" + mesPago;
        } else {
            mes = String.valueOf(mesPago);
        }

        String stringFechaPago = dia + "-" + mes + "-" + añoPago;

        return stringFechaPago;

    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
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

    public Condominio buscarCondominio(Integer idCond) {

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<>();
        Condominio condo = null;

        try {
            condo = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return condo;
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

    public Boolean getDeshabilita() {
        return deshabilita;
    }

    public void setDeshabilita(Boolean deshabilita) {
        this.deshabilita = deshabilita;
    }
}
