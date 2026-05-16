/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jesus
 */
package saas.backing;

import saas.modelos.Suscripcion;
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
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;

import saas.modelos.PagoMensual;
import saas.modelos.PagoMensualDT;

public class RegPagosMenCtrl extends DaoCrud<PagoMensual, Suscripcion, Integer> implements Serializable {

    private Integer id;
    private HtmlSelectOneMenu idBanco;
    private HtmlSelectOneMenu idCuenta;
    private String fecPagoMensual;
    private String refPagoMensual;
    private String mtoPagoMensual;
    private String numMesesPagoMensual;
    private PagoMensualDT filaSeleccionada;
    private Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();
    private Suscripcion suscripcion;

    public RegPagosMenCtrl() {
    }

    public void inicializarPanel() {

        Integer cero = new Integer(0);
        idBanco.setValue(cero);
        idCuenta.setValue(cero);
        setFecPagoMensual(null);
        setRefPagoMensual(null);
        setMtoPagoMensual(null);
        setNumMesesPagoMensual(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        return "salir";

    }

    public String emitirReciboPago() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        return "emitirReciboPago";

    }

    public String validarPanel() {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        String enBlanco = new String();
        String mensaje = new String();

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Banco y cuenta  (diferente de la opcion "Seleccione")
        //--------------------------------------------------------------------------------------
        String bcoSel = idBanco.getValue().toString();
        String cero = "0";

        if (bcoSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione el banco en que realizó el depósito/transferencia...";
            return mensaje;
        }

        String ctaSel = idCuenta.getValue().toString();

        if (ctaSel.equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Por favor, seleccione la cuenta en que realizó el depósito/transferencia...";
            return mensaje;
        }
        //--------------------------------------------------------------------------------------
        if (getFecPagoMensual().equals(enBlanco)) {
            mensaje = "Por favor, seleccione la fecha en que realizó el depósito/transferencia...";
            return mensaje;
        }

        if (!val.esFecha(getFecPagoMensual())) {
            mensaje = "Formato de fecha inválido, por favor intente de nuevo...";
            return mensaje;
        }


        if (!val.esEntero(getRefPagoMensual())) {
            mensaje = "El número de referencia debe ser entero y diferente de cero, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esMonto(getMtoPagoMensual())) {
            mensaje = "El monto del pago debe ser un número entero o decimal, por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public boolean esEntero(String numIn) {

        try {
            Integer entero = Integer.parseInt(numIn);
            if (entero > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public Collection getPagosMensualesPorSuscripcion() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idSusc = beanUsuario.getIdSuscripcion();

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensualesPorIdSusc";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidadesPorId(idSusc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaPagosMensualesDT = inicializarPagosMensualesDT(listaPagosMensuales);
        Collection listaOrd = ordenarLista(listaPagosMensualesDT);

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoMensualDT> listaDesOrdenada = new ArrayList<PagoMensualDT>();

        for (Iterator<PagoMensualDT> it = lista.iterator();
                it.hasNext();) {
            PagoMensualDT pm = (PagoMensualDT) it.next();
            listaDesOrdenada.add(pm);
        }
        Collections.sort(listaDesOrdenada, new ComparadorPagoMensualFecha());

        Collection<PagoMensualDT> coleccionOrdenada = new ArrayList<PagoMensualDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoMensualDT pmo = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pmo);
        }
        return coleccionOrdenada;
    }

    private Collection<PagoMensualDT> inicializarPagosMensualesDT(Collection listaPagosMensuales) {

        Collection listaPagos = new ArrayList();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {

            PagoMensual pm = (PagoMensual) it.next();

            PagoMensualDT pagoMensualDT = new PagoMensualDT();

            pagoMensualDT.setId(pm.getId());
            pagoMensualDT.setBcoPagoMensual(pm.getBcoPagoMensual());
            pagoMensualDT.setCtaPagoMensual(pm.getCtaPagoMensual());

            //----------------------------------------------------------------
            //Se da formato a la fecha del pago para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaPagoMensual = pm.getFecPagoMensual();
            String dia = "";
            String mes = "";
            int diaPM = fechaPagoMensual.get(Calendar.DAY_OF_MONTH);
            int mesPM = fechaPagoMensual.get(Calendar.MONTH) + 1;
            int añoPM = fechaPagoMensual.get(Calendar.YEAR);

            if (diaPM < 10) {
                dia = "0" + diaPM;
            } else {
                dia = String.valueOf(diaPM);
            }

            if (mesPM < 10) {
                mes = "0" + mesPM;
            } else {
                mes = String.valueOf(mesPM);
            }

            String stringFechaDp = dia + "-" + mes + "-" + añoPM;


            pagoMensualDT.setFecPagoMensual(stringFechaDp);
            pagoMensualDT.setRefPagoMensual(pm.getRefPagoMensual());

            //----------------------------------------------------------------
            //Se da formato al monto y al saldo para mostrarlos en el DT
            //----------------------------------------------------------------
            pagoMensualDT.setMtoPagoMensual(val.convertirFloatAString(pm.getMtoPagoMensual()));

            //----------------------------------------------------------------
            //Se da formato a la fecha del mov para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaMov = pm.getFecMovPagoMensual();
            String diaM = "";
            String mesM = "";
            int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
            int mesMov = fechaMov.get(Calendar.MONTH) + 1;
            int añoMov = fechaMov.get(Calendar.YEAR);

            if (diaMov < 10) {
                diaM = "0" + diaMov;
            } else {
                diaM = String.valueOf(diaMov);
            }

            if (mesMov < 10) {
                mesM = "0" + mesMov;
            } else {
                mesM = String.valueOf(mesMov);
            }

            String stringFechaMov = diaM + "-" + mesM + "-" + añoMov;

            pagoMensualDT.setFecMovPagoMensual(stringFechaMov);
            pagoMensualDT.setEdoPagoMensual(pm.getEdoPagoMensual());

            listaPagos.add(pagoMensualDT);
        }
        return listaPagos;
    }

    public void editarPagoMensualAction() {

        PagoMensualDT pmSel = getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del pago  seleccionado la cual se buscara como "PagoMensualDT" en el map #{PagoMensual} 
        ctx.getExternalContext().getSessionMap().put("PagoMensualDT", pmSel);

        setFecPagoMensual(pmSel.getFecPagoMensual());
        setRefPagoMensual(pmSel.getRefPagoMensual());
        setMtoPagoMensual(pmSel.getMtoPagoMensual());
        idBanco.setValue("1");
        idCuenta.setValue("1");
        //   setNumMesesPagoMensual(pmSel.getNumMesesPagoMensual());


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarPagoSuscripcion() throws ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        PagoMensual pagoMensualNuevo = new PagoMensual();

        pagoMensualNuevo.setBcoPagoMensual("Provincial");
        pagoMensualNuevo.setCtaPagoMensual("1111-1234-56-1234567890");
        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecPagoMensual);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        pagoMensualNuevo.setFecPagoMensual(cal);
        pagoMensualNuevo.setRefPagoMensual(refPagoMensual);
        //-----------------------------------------------------------
        //                  Convertir de String a Float
        //-----------------------------------------------------------
        Float mto = Float.valueOf(mtoPagoMensual);
        pagoMensualNuevo.setMtoPagoMensual(mto);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoMensualNuevo.setFecMovPagoMensual(fecha);
        pagoMensualNuevo.setEdoPagoMensual("PEND");

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idSusc = beanUsuario.getIdSuscripcion();

        suscripcion = encontrarEntidadPorId(idSusc);
        pagoMensualNuevo.setSuscripcion(suscripcion);

        try {
            almacenarOModificarEntidad(pagoMensualNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago mensual de la suscripción número " + " " + beanUsuario.getNumSuscripcion() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarPagoSuscripcion() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        PagoMensualDT pmSel = (PagoMensualDT) ctx.getExternalContext().getSessionMap().get("PagoMensualDT");

        String idPagoMensual = pmSel.getId().toString();
        //buscar objeto a modificar en el arreglo de pagos mensuales, listaPagosMensuales
        PagoMensual pagoMensual = null;
        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {
            pagoMensual = (PagoMensual) it.next();
            if (pagoMensual.getId().toString().equals(idPagoMensual)) {
                break;
            }
        }

        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------
        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecPagoMensual);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        pagoMensual.setFecPagoMensual(cal);
        pagoMensual.setRefPagoMensual(refPagoMensual);
        //-----------------------------------------------------------
        //                  Convertir de String a Float
        //-----------------------------------------------------------
        Float mto = Float.valueOf(mtoPagoMensual);
        pagoMensual.setMtoPagoMensual(mto);
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoMensual.setFecMovPagoMensual(fecha);
        pagoMensual.setEdoPagoMensual("PEND");
        try {
            almacenarOModificarEntidad(pagoMensual);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago mensual de la suscripción número " + " " + beanUsuario.getIdSuscripcion() + " ha sido modificado satisfactoriamente!"));

        return;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdBanco(HtmlSelectOneMenu idBanco) {
        this.idBanco = idBanco;
    }

    public HtmlSelectOneMenu getIdBanco() {
        return idBanco;
    }

    public void setIdCuenta(HtmlSelectOneMenu idCuenta) {
        this.idCuenta = idCuenta;
    }

    public HtmlSelectOneMenu getIdCuenta() {
        return idCuenta;
    }

    public void setFecPagoMensual(String fecPagoMensual) {
        this.fecPagoMensual = fecPagoMensual;
    }

    public String getFecPagoMensual() {
        return fecPagoMensual;
    }

    public void setRefPagoMensual(String refPagoMensual) {
        this.refPagoMensual = refPagoMensual;
    }

    public String getRefPagoMensual() {
        return refPagoMensual;
    }

    public void setMtoPagoMensual(String mtoPagoMensual) {
        this.mtoPagoMensual = mtoPagoMensual;
    }

    public String getMtoPagoMensual() {
        return mtoPagoMensual;
    }

    public void setNumMesesPagoMensual(String numMesesPagoMensual) {
        this.numMesesPagoMensual = numMesesPagoMensual;
    }

    public String getNumMesesPagoMensual() {
        return numMesesPagoMensual;
    }
    // .....

    public void setFilaSeleccionada(PagoMensualDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoMensualDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
