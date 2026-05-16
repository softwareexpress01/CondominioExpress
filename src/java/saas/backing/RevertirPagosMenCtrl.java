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

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import saas.modelos.PagoMensual;
import saas.modelos.PagoMensualDT;

public class RevertirPagosMenCtrl extends DaoCrud<PagoMensual, Suscripcion, Integer> implements Serializable {

    private Integer id;
    private String idBanco;
    private String idCuenta;
    private String fecPagoMensual;
    private String refPagoMensual;
    private String mtoPagoMensual;
    private String numMesesPagoMensual;
    private PagoMensualDT filaSeleccionada;
    private Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();
    private Suscripcion suscripcion;

    public RevertirPagosMenCtrl() {
    }

    public void inicializarPanel() {

        Integer cero = new Integer(0);
        setIdBanco(cero.toString());
        setIdCuenta(cero.toString());
        setFecPagoMensual(null);
        setRefPagoMensual(null);
        setMtoPagoMensual(null);
        setNumMesesPagoMensual(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = true;

        return "salir";

    }

    public Collection getPagosMensualesConf() {

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensualesConfirmados";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection pagosSeleccionados = buscarPagosDesdeMesAnterior(listaPagosMensuales);
        Collection listaPagosMensualesDT = inicializarPagosMensualesDT(pagosSeleccionados);
        Collection listaOrd = ordenarLista(listaPagosMensualesDT);

        return listaOrd;
    }

    public Collection buscarPagosDesdeMesAnterior(Collection listaPagosMensuales){
        
        Collection<PagoMensual> pagosSeleccionados = new ArrayList<PagoMensual>();
       
        //-------------------------------------------------------------------
        //    Se obtiene fecha del primer dia del mes anterior a la fecha hoy
        //-------------------------------------------------------------------
        
        GregorianCalendar fechaHoy = new GregorianCalendar();
        int dia = new Integer(01);
        Integer mes2;
        Integer año2;
        int añoA = fechaHoy.get(Calendar.YEAR);
        int mesA = fechaHoy.get(Calendar.MONTH) - 1;
        if (!(mesA > 0 | mesA == 0)) {
            mes2 = 11;
            año2 = añoA - 1;
        } else {
            mes2 = mesA;
            año2 = añoA;
        }

        GregorianCalendar fechaAnt = new GregorianCalendar(año2, mes2, dia);

        //-------------------------------------------------------------
        
        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {
            PagoMensual pm = (PagoMensual) it.next();
            if (pm.fecMovPagoMensual.after(fechaAnt)) {            
                pagosSeleccionados.add(pm);
            }
        }
        return pagosSeleccionados;
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
        Collections.sort(listaDesOrdenada, new ComparadorPagoMensual());

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
        Suscripcion sus;

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

            sus = pm.getSuscripcion();

            pagoMensualDT.setNumSuscriptor(sus.getNumSuscripcion());
            pagoMensualDT.setNumCedSuscriptor(sus.getNumCedSuscriptor());
            pagoMensualDT.setNombSuscriptor(sus.getNombSuscriptor());
            pagoMensualDT.setApelSuscriptor(sus.getApelSuscriptor());

            listaPagos.add(pagoMensualDT);
        }
        return listaPagos;
    }

    public void editarPagoMensualAction() {

        PagoMensualDT pmSel = getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del pago  seleccionado la cual se buscara como "PagoMensualDT" en el map #{PagoMensualDT} 
        ctx.getExternalContext().getSessionMap().put("PagoMensualDT", pmSel);

        setFecPagoMensual(pmSel.getFecPagoMensual());
        setRefPagoMensual(pmSel.getRefPagoMensual());
        setMtoPagoMensual(pmSel.getMtoPagoMensual());
        setIdBanco("1");
        setIdCuenta("1");
        //   setNumMesesPagoMensual(pmSel.getNumMesesPagoMensual());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.modificar = false;

    }

    public void revertirPagoSuscripcion() throws ValidatorException, ParseException {


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
        //----------------------------------------------------------------------
        // Se actualiza el edo del pago mensual seleccionado 
        //----------------------------------------------------------------------
        pagoMensual.setEdoPagoMensual("PEND");
        String edoAntSus = pagoMensual.getEdoAntSuscripcion();
        pagoMensual.setEdoAntSuscripcion(""); 
        //----------------------------------------------------------------------
        // Se actualiza el pago mensual en la bd enviandola al dao
        //----------------------------------------------------------------------
        DaoQuery<PagoMensual> dao1 = new DaoQuery<PagoMensual>();

        try {
            dao1.almacenarOModificarEntidad(pagoMensual);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        
        suscripcion = pagoMensual.getSuscripcion();
        float saldoSuscripcion = suscripcion.getSaldoSuscripcion() - pagoMensual.getMtoPagoMensual();
        suscripcion.setSaldoSuscripcion(saldoSuscripcion);
        suscripcion.setEdoSuscripcion(edoAntSus);
        
        //----------------------------------------------------------------------
        // Se actualiza la suscripcion en la bd enviandola al dao
        //----------------------------------------------------------------------
        DaoQuery<Suscripcion> dao = new DaoQuery<Suscripcion>();

        try {
            dao.almacenarOModificarEntidad(suscripcion);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.modificar = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El pago mensual de la suscripción número " + " " + suscripcion.getNumSuscripcion() + " ha sido revertido satisfactoriamente!"));
        return;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setIdBanco(String idBanco) {
        this.idBanco = idBanco;
    }

    public String getIdBanco() {
        return idBanco;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta() {
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