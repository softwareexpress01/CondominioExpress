/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Proveedor;
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
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.DeudaPendCxP;
import movimientos.modelos.DeudaPendCxPDT;
import org.hibernate.HibernateException;
import util.*;

public class DeudaPendCxPCtrl extends DaoCrud<DeudaPendCxP, Proveedor, Integer> implements Serializable {

    private String fecMovDeudaPendCxP;
    private String idProvDeudaPendCxP;
    private String nombProvDeudaPendCxP;
    private HtmlInputText refDeudaPendCxP;
    private HtmlInputTextarea descDeudaPendCxP;
    private HtmlInputText fecDeudaPendCxP;
    private HtmlInputText mtoDeudaPendCxP;
    private String saldoDeudaPendCxP;
    private String estatusDeudaPendCxP;
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private DeudaPendCxP deudaPendCxP;
    private Collection<DeudaPendCxPDT> listaDeudaPendCxPDT = new ArrayList<DeudaPendCxPDT>();
    private Collection<DeudaPendCxP> listaDeudaPendCxP = new ArrayList<DeudaPendCxP>();
    private Collection<DeudaPendCxP> listaDeudaPendCxPSel = new ArrayList<DeudaPendCxP>();
    private Collection<Proveedor> listaProveedores = new ArrayList<Proveedor>();
    private DeudaPendCxPDT filaSeleccionada;
    private Integer idDeudaPend = 0;

    public DeudaPendCxPCtrl() {
    }

    public void inicializarPanel(ValueChangeEvent vce) {


        System.out.println("En inicializarPanel(ValueChangeEvent vce)");
        this.setNombProvDeudaPendCxP(null);
        this.descDeudaPendCxP.setValue(null);
        this.fecDeudaPendCxP.setValue(null);
        this.mtoDeudaPendCxP.setValue(null);
        this.refDeudaPendCxP.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public void inicializarPanel() {
        System.out.println("En inicializarPanel()");
        this.setNombProvDeudaPendCxP(null);
        this.descDeudaPendCxP.setValue(null);
        this.fecDeudaPendCxP.setValue(null);
        this.mtoDeudaPendCxP.setValue(null);
        this.refDeudaPendCxP.setValue(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Integer idProveedor = Integer.parseInt(idProvDeudaPendCxP);
        if (idProveedor == 0) {
            mensaje = "Debe seleccionar un Proveedor de la lista, por favor intente de nuevo ";
            return mensaje;
        }
        System.out.println("En validarPanel()");
        if (this.refDeudaPendCxP.getValue().toString().equals(enBlanco)) {
            mensaje = "La Referencia del Movimiento está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esEntero(refDeudaPendCxP.getValue().toString())) {
            mensaje = "El campo Número de Documento debe ser entero mayor que cero, por favor intente de nuevo ";
            return mensaje;
        }

        mensaje = verificarRefExiste(refDeudaPendCxP.getValue().toString()); /* Se verifica que la referncia no exista en el mov.*/
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }

        if ((descDeudaPendCxP.getValue().toString()).equals(enBlanco)) {
            mensaje = "El Campo Descripción está en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esAlfaNumPlus(descDeudaPendCxP.getValue().toString())) {
            mensaje = "El campo Descripción contiene carácteres inválidos, por favor intente de nuevo ";
            return mensaje;
        }

        if ((fecDeudaPendCxP.getValue().toString()).equals(enBlanco)) {
            mensaje = "La Fecha del Movimiento está en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esFecha(fecDeudaPendCxP.getValue().toString())) {
            mensaje = "La Fecha no es válida, debe ser con el formato DD-MM-AAAA, por favor intente de nuevo ";
            return mensaje;
        }

        // Validar que la fecha del doc no sea mayor a la del dia de la carga de la trans.
        Calendar fecha = new GregorianCalendar();
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecDeudaPendCxP.getValue().toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.after(fecha)) {
            mensaje = "La fecha del documento no puede ser mayor a la fecha de hoy. Por favor intente de nuevo";
            return mensaje;
        }
        // ---------------------------------------------------------------------------------

        if (mtoDeudaPendCxP.getValue().toString().equals(enBlanco)) {
            mensaje = "EL Monto está en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esMonto(mtoDeudaPendCxP.getValue().toString())) {
            mensaje = "El campo Monto debe ser numérico mayor que cero. Use punto decimal si lo requeiere, por favor intente de nuevo ";
            return mensaje;
        }


        return mensaje;
    }

    public String verificarRefExiste(String ref) {

        String mensaje = new String();

        if (listaDeudaPendCxPDT.isEmpty()) {
            return mensaje;
        }

        if (idDeudaPend == null) {
            return mensaje;
        }

        if (idDeudaPend == 0) {  // Caso de Agregar

            for (Iterator<DeudaPendCxPDT> it = listaDeudaPendCxPDT.iterator();
                    it.hasNext();) {
                DeudaPendCxPDT deudaPend = (DeudaPendCxPDT) it.next();
                if (ref.equals(deudaPend.getRefDeudaPendCxP())) {
                    mensaje = "El número de referencia ya existe, por favor intente de nuevo";
                    break;
                }
            }
        }
        if (idDeudaPend != 0) { // Caso de Modificar

            for (Iterator<DeudaPendCxPDT> it = listaDeudaPendCxPDT.iterator();
                    it.hasNext();) {
                DeudaPendCxPDT deudaPend = (DeudaPendCxPDT) it.next();

                if (ref.equals(deudaPend.getRefDeudaPendCxP())) {

                    if (!((deudaPend.getId().equals(idDeudaPend)))) {

                        mensaje = "El número de referencia ya existe, por favor intente de nuevo";
                        break;
                    }
                }
            }


        }
        return mensaje;
    }

    public Collection getProveedoresPorCondominio() {

        listaOrdII.clear();
        listaOrdII.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaProveedores = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listaProveedores.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }

        listaOrd = ordenarLista(listaProveedores);// para ordenar la lista de Proveedores

        for (Iterator<Proveedor> it = listaProveedores.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            listaOrdII.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        return listaOrdII;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Proveedor> listaDesOrdenada = new ArrayList<Proveedor>();

        for (Iterator<Proveedor> it = lista.iterator();
                it.hasNext();) {
            Proveedor Proveedor1 = (Proveedor) it.next();
            listaDesOrdenada.add(Proveedor1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorProv());

        Collection<Proveedor> coleccionOrdenada = new ArrayList<Proveedor>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Proveedor Proveedor2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(Proveedor2);
        }
        return coleccionOrdenada;
    }

    public Collection getDeudasPendientesPorProveedor() {

        //   inicializarPanel();

        listaDeudaPendCxPDT.clear();
        listaDeudaPendCxPSel.clear();

        if (idProvDeudaPendCxP == null) {
            return null;
        }
        if (idProvDeudaPendCxP.equals("0")) {
            return null;
        }


        Integer idProveedor = Integer.parseInt(idProvDeudaPendCxP);

        String nombQuery = "buscarDeudasPendientesPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaDeudaPendCxP = dataAcces.buscarListaEntidadesPorId(idProveedor, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        if (listaDeudaPendCxP.isEmpty()) {
            return listaDeudaPendCxPDT;
        }

        /**
         * **************** AQUI SE SELECCIONAN SOLO AQUELLOS OBJETOS CON
         * ESTADO DIFERENTE DE "CANC" *************
         */
        for (Iterator<DeudaPendCxP> it = listaDeudaPendCxP.iterator();
                it.hasNext();) {
            DeudaPendCxP dP = (DeudaPendCxP) it.next();
            if (!(dP.estatusDeudaPendCxP.equals("CANC"))) {
                listaDeudaPendCxPSel.add(dP);
            }
        }

        listaDeudaPendCxPDT = inicializarDeudaPendCxPDT(listaDeudaPendCxPSel);

        return listaDeudaPendCxPDT;
    }

    private Collection<DeudaPendCxPDT> inicializarDeudaPendCxPDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<DeudaPendCxP> it = listaOrd.iterator();
                it.hasNext();) {

            deudaPendCxP = (DeudaPendCxP) it.next();
            if (deudaPendCxP == null) {// Si no hay pagos adelantados devuelve la lista en blanco
                break;
            } else {

                Integer idProvInteger = deudaPendCxP.getIdProvDeudaPendCxP();
                String nombProv = buscarNombProv(idProvInteger);

                DeudaPendCxPDT deudaPendCxPDT = new DeudaPendCxPDT();
                deudaPendCxPDT.setId(deudaPendCxP.getId());


                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = deudaPendCxP.getFecMovDeudaPendCxP();
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
                // ---------------------------------------------------------------

                deudaPendCxPDT.setFecMovDeudaPendCxP(stringFechaMov);


                deudaPendCxPDT.setIdProvDeudaPendCxP(deudaPendCxP.getIdProvDeudaPendCxP());
                deudaPendCxPDT.setNombProvDeudaPendCxP(nombProv);
                deudaPendCxPDT.setRefDeudaPendCxP(deudaPendCxP.getRefDeudaPendCxP());
                deudaPendCxPDT.setDescDeudaPendCxP(deudaPendCxP.getDescDeudaPendCxP());
                deudaPendCxPDT.setFecDeudaPendCxP(deudaPendCxP.getFecDeudaPendCxP());

                deudaPendCxPDT.setMtoDeudaPendCxP(deudaPendCxP.getMtoDeudaPendCxP());
                deudaPendCxPDT.setSaldoDeudaPendCxP(deudaPendCxP.getSaldoDeudaPendCxP());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                deudaPendCxPDT.setMtoDeudaPendCxPString(val.convertirDoubleAString(deudaPendCxP.getMtoDeudaPendCxP()));
                deudaPendCxPDT.setSaldoDeudaPendCxPString(val.convertirDoubleAString(deudaPendCxP.getSaldoDeudaPendCxP()));

                deudaPendCxPDT.setEstatusDeudaPendCxP(deudaPendCxP.getEstatusDeudaPendCxP());

                listaDeudaPendCxPDT.add(deudaPendCxPDT);
            }
        }
        return listaDeudaPendCxPDT;
    }

    public String buscarNombProv(Integer idProv) {

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
        String nombProv = proveedor.getNombProveedor();
        return nombProv;
    }

    public void editarDeudaPendCxPAction() {

        DeudaPendCxPDT deudaPendCxPSel = this.getFilaSeleccionada();

        idDeudaPend = deudaPendCxPSel.getId(); // Usado para verificar si la referncia existe

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("deudaPendCxPDT", deudaPendCxPSel);

        this.setIdProvDeudaPendCxP(deudaPendCxPSel.getIdProvDeudaPendCxP().toString());
        //this.nombProvDeudaPendCxP.setValue(deudaPendCxPSel.getNombProvDeudaPendCxP());
        this.refDeudaPendCxP.setValue(deudaPendCxPSel.getRefDeudaPendCxP());
        this.descDeudaPendCxP.setValue(deudaPendCxPSel.getDescDeudaPendCxP());
        this.fecDeudaPendCxP.setValue(deudaPendCxPSel.getFecDeudaPendCxP());

        this.mtoDeudaPendCxP.setValue(deudaPendCxPSel.getMtoDeudaPendCxPString());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(deudaPendCxPSel.getRefDeudaPendCxP().toString());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idDeudaPendCxP = deudaPendCxPSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de DeudaPendCxP, listaOrd
        deudaPendCxP = null;

        for (Iterator<DeudaPendCxP> it = listaDeudaPendCxP.iterator();
                it.hasNext();) {
            deudaPendCxP = (DeudaPendCxP) it.next();
            if (deudaPendCxP.getId().toString().equals(idDeudaPendCxP)) {

                //Almacena la fila del pago seleccionado ,el cual se buscara como "DeudaPendCxP" en el map #{DeudaPendCxP} 

                ctx.getExternalContext().getSessionMap().put("DeudaPendCxP", deudaPendCxP);
                break;
            }
        }

        //Almacena la fila del pago seleccionado ,el cual se buscara como "DeudaPendCxP" en el map #{DeudaPendCxP} 

        ctx.getExternalContext().getSessionMap().put("DeudaPendCxP", deudaPendCxP);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarDeudaPendCxP() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        idDeudaPend = 0; // Usado en la verificación de las referncias

        DeudaPendCxP deudaPendCxPSel = new DeudaPendCxP();

        Calendar fecha = new GregorianCalendar();
        deudaPendCxPSel.setFecMovDeudaPendCxP(fecha);

        Integer idProveedor = Integer.parseInt(idProvDeudaPendCxP);
        deudaPendCxPSel.setIdProvDeudaPendCxP(idProveedor);
        deudaPendCxPSel.setRefDeudaPendCxP(refDeudaPendCxP.getValue().toString());
        deudaPendCxPSel.setDescDeudaPendCxP(descDeudaPendCxP.getValue().toString());
        deudaPendCxPSel.setFecDeudaPendCxP(fecDeudaPendCxP.getValue().toString());


        Double mto = Double.valueOf(mtoDeudaPendCxP.getValue().toString()).doubleValue();   // de String a Double 
        deudaPendCxPSel.setMtoDeudaPendCxP(mto);
        deudaPendCxPSel.setSaldoDeudaPendCxP(mto);
        String estatus = "PEND";
        deudaPendCxPSel.setEstatusDeudaPendCxP(estatus);

        Proveedor proveedor = null;
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(idProveedor);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        deudaPendCxPSel.setProveedor(proveedor);

        try {
            almacenarOModificarEntidad(deudaPendCxPSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        inicializarPanel();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Deuda Pendiente ha sido registrada satisfactoriamente!"));

        return;
    }

    public void modificarDeudaPendCxP() throws ValidatorException, ParseException {

        System.out.println("**************************** Entrando a modificar");
        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        DeudaPendCxP DeudaPendCxPCtx = (DeudaPendCxP) JSFUtil.getManagedObject("DeudaPendCxP");

        if (!("PEND".equals(DeudaPendCxPCtx.getEstatusDeudaPendCxP()))) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La deuda pendiente seleccionada no puede ser modificada. Esta en proceso o cancelada... por favor intente de nuevo "));
            return;
        }

        DeudaPendCxP deudaPendCxPSel = new DeudaPendCxP();

        Calendar fecha = new GregorianCalendar();
        deudaPendCxPSel.setFecMovDeudaPendCxP(fecha);

        Integer idProveedor = Integer.parseInt(idProvDeudaPendCxP);
        deudaPendCxPSel.setIdProvDeudaPendCxP(idProveedor);
        deudaPendCxPSel.setRefDeudaPendCxP(refDeudaPendCxP.getValue().toString());
        deudaPendCxPSel.setDescDeudaPendCxP(descDeudaPendCxP.getValue().toString());
        deudaPendCxPSel.setFecDeudaPendCxP(fecDeudaPendCxP.getValue().toString());

        Double mto = Double.valueOf(mtoDeudaPendCxP.getValue().toString()).doubleValue();   // de String a Double 
        deudaPendCxPSel.setMtoDeudaPendCxP(mto);
        deudaPendCxPSel.setSaldoDeudaPendCxP(mto);

        deudaPendCxPSel.setEstatusDeudaPendCxP("PEND");

        System.out.println("**************************** el id del proveedor seleccionado es: " + idProvDeudaPendCxP);
        Proveedor proveedor = null;
        try {
            proveedor = (Proveedor) encontrarEntidadPorId(idProveedor);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return;
        }

        deudaPendCxPSel.setProveedor(proveedor);

        deudaPendCxPSel.id = DeudaPendCxPCtx.getId();

        try {
            almacenarOModificarEntidad(deudaPendCxPSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        System.out.println("**************************** Almacenada la modificación  " + this.getIdProvDeudaPendCxP().toString());
        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Deuda Pendiente ha sido modificada satisfactoriamente"));

    }

    public void setFecMovDeudaPendCxP(String fecMovDeudaPendCxP) {
        this.fecMovDeudaPendCxP = fecMovDeudaPendCxP;
    }

    public String getFecMovDeudaPendCxP() {
        return fecMovDeudaPendCxP;
    }

    public void setNombProvDeudaPendCxP(String nombProvDeudaPendCxP) {
        this.nombProvDeudaPendCxP = nombProvDeudaPendCxP;
    }

    public String getNombProvDeudaPendCxP() {
        return nombProvDeudaPendCxP;
    }

    public String getIdProvDeudaPendCxP() {
        return idProvDeudaPendCxP;
    }

    public void setIdProvDeudaPendCxP(String idProvDeudaPendCxP) {
        this.idProvDeudaPendCxP = idProvDeudaPendCxP;
    }

    public HtmlInputText getRefDeudaPendCxP() {
        return refDeudaPendCxP;
    }

    public void setRefDeudaPendCxP(HtmlInputText refDeudaPendCxP) {
        this.refDeudaPendCxP = refDeudaPendCxP;
    }

    public HtmlInputTextarea getDescDeudaPendCxP() {
        return descDeudaPendCxP;
    }

    public void setDescDeudaPendCxP(HtmlInputTextarea descDeudaPendCxP) {
        this.descDeudaPendCxP = descDeudaPendCxP;
    }

    public HtmlInputText getFecDeudaPendCxP() {
        return fecDeudaPendCxP;
    }

    public void setFecDeudaPendCxP(HtmlInputText fecDeudaPendCxP) {
        this.fecDeudaPendCxP = fecDeudaPendCxP;
    }

    public HtmlInputText getMtoDeudaPendCxP() {
        return mtoDeudaPendCxP;
    }

    public void setMtoDeudaPendCxP(HtmlInputText mtoDeudaPendCxP) {
        this.mtoDeudaPendCxP = mtoDeudaPendCxP;
    }

    public void setSaldoDeudaPendCxP(String saldoDeudaPendCxP) {
        this.saldoDeudaPendCxP = saldoDeudaPendCxP;
    }

    public String getSaldoDeudaPendCxP() {
        return saldoDeudaPendCxP;
    }

    public void setEstatusDeudaPendCxP(String estatusDeudaPendCxP) {
        this.estatusDeudaPendCxP = estatusDeudaPendCxP;
    }

    public String getEstatusDeudaPendCxP() {
        return estatusDeudaPendCxP;
    }

    public void setFilaSeleccionada(DeudaPendCxPDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public DeudaPendCxPDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
