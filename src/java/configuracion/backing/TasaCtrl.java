/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuracion.backing;

import datosbasicos.modelos.Condominio;
import configuracion.modelos.Tasa;
import configuracion.modelos.Tasa;
import configuracion.modelos.TasaDT;
import configuracion.modelos.TasaDT;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;

public class TasaCtrl extends DaoCrud<Tasa, Condominio, Integer> implements Serializable {

    private String periodo;
    private String tasa;

    Collection<TasaDT> listaTasasDT = new ArrayList<TasaDT>();
    Collection<Tasa> listTasas = new ArrayList<Tasa>();

    private TasaDT filaSeleccionada;
    TasaDT tasaSel;

    public TasaCtrl() {
    }

    public void inicializarPanel() {

        this.setPeriodo(null);
        this.setTasa(null);

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

        beanUsuario.ejecutarQueryDT = true;

        return "selcond";
    }

    public String salir() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.ejecutarQueryDT = true;

        return "configuracion";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (periodo.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el período";
            return mensaje;
        }
        if (!val.esFormatoPeriodo(periodo)) {
            mensaje = "El formato del periodo debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        if (tasa.equals(enBlanco)) {
            mensaje = "La tasa  está en blanco. Por favor intente de nuevo.";
            return mensaje;
        }

        if (!val.esMonto(tasa)) {
            mensaje = "La tasa debe ser numérica y mayor que cero";
            return mensaje;
        }
        Float tasaFloat = Float.parseFloat(tasa);

        if (tasaFloat <= 0) {
            mensaje = "El valor de la tasa debe ser numérico mayor que cero";
            return mensaje;
        }
        return mensaje;
    }

    public Collection getTasasPorCondominio() {

        // Obtiene las tasas para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        /*if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaTasasDT;
        }*/

        String nombQuery = "buscarTasasPorIdCondominio";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listTasas = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaOrd = ordenarLista(listTasas);// para ordenar la lista de bancos

        listaTasasDT = inicializarTasasDT(listaOrd);

        beanUsuario.ejecutarQueryDT = false;

        return listaTasasDT;
    }

    private Collection<TasaDT> inicializarTasasDT(Collection listaOrd) {

        listaTasasDT.clear();
        DaoQuery<List> dataAcces = new DaoQuery<>();
        String periodoStr, tasaStr;

        for (Iterator<Tasa> it = listaOrd.iterator();
                it.hasNext();) {

            Tasa tasa = (Tasa) it.next();
            if (tasa == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                TasaDT tasaDT = new TasaDT();
                tasaDT.setId(tasa.getId());

                // convertir periodo actual  de Calendar a String para mostrar en el DT
                Calendar fechaMov = tasa.getPeriodoTasa();

                String mesM = "";
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (mesMov < 10) {
                    mesM = "0" + mesMov;
                } else {
                    mesM = String.valueOf(mesMov);
                }

                String stringFechaMov = mesM + "-" + añoMov;
                //
                tasaDT.setPeriodo(stringFechaMov);
                tasaStr = String.valueOf(tasa.getMtoTasa());
                tasaDT.setTasa(tasaStr);

                listaTasasDT.add(tasaDT);
            }
        }
        return listaTasasDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<Tasa> listaDesOrdenada = new ArrayList<Tasa>();

        for (Iterator<Tasa> it = lista.iterator();
                it.hasNext();) {
            Tasa banco = (Tasa) it.next();
            listaDesOrdenada.add(banco);
        }
        Collections.sort(listaDesOrdenada, new ComparadorTasa());

        Collection<Tasa> coleccionOrdenada = new ArrayList<Tasa>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Tasa banco = listaDesOrdenada.get(i);
            coleccionOrdenada.add(banco);
        }
        return coleccionOrdenada;
    }

    public void editarTasaAction() {

        tasaSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("TasaDT", tasaSel);

        this.setPeriodo(tasaSel.getPeriodo());
        this.setTasa(tasaSel.getTasa());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(tasaSel.getPeriodo());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------
        String idTasa = tasaSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de tasas, listaOrd
        Tasa tasa = null;

        for (Iterator<Tasa> it = listTasas.iterator();
                it.hasNext();) {
            tasa = (Tasa) it.next();
            if (tasa.getId().toString().equals(idTasa)) {
                //Almacena la fila de la tasa seleccionada ,el cual se buscara como "Tasa" en el map #{Tasa} 
                ctx.getExternalContext().getSessionMap().put("Tasa", tasa);
                break;
            }
        }

        //Almacena la fila de la tasa seleccionada ,el cual se buscara como "Tasa" en el map #{Concepto} 
        ctx.getExternalContext().getSessionMap().put("Tasa", tasa);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarTasa() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Tasa tasaNueva = new Tasa();

        //-------------------------------------------------------------------
        // Convertir periodo a Calendar
        //-------------------------------------------------------------------
        String stringPeriodo = "01-" + periodo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        tasaNueva.setPeriodoTasa(cal);
        Float mto = Double.valueOf(tasa).floatValue();
        tasaNueva.setMtoTasa(mto);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        // verficar si ya hay una tasa definida en el periodo ingresado
       

        for (Iterator<Tasa> it = listTasas.iterator();
                it.hasNext();) {
            Tasa tasaBD = (Tasa) it.next();
            if (tasaBD.getPeriodoTasa().equals(cal)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ya hay una tasa definida para ese período"));
                    return;
                }
        }
        /*        condominio.getTasas().add(bancoNuevo);*/
        tasaNueva.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(tasaNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Tasa ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarTasa() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        String stringPeriodo = "01-" + periodo;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Tasa tasaNueva = new Tasa();
        tasaNueva.setPeriodoTasa(cal);
        Float mto = Double.valueOf(tasa).floatValue();

        tasaNueva.setMtoTasa(mto);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        
        tasaNueva.setCondominio(condominio);

        TasaDT tasaCtx = (TasaDT) JSFUtil.getManagedObject("TasaDT");
        /*Buscar el Id del objeto a Modificar para Update */
        Integer idTasa = tasaCtx.getId();
        
        tasaNueva.setId(idTasa);

        for (Iterator<Tasa> it = listTasas.iterator();
                it.hasNext();) {
            Tasa tasaBD = (Tasa) it.next();
            if (tasaBD.getPeriodoTasa().equals(cal)) {
                    FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Ya hay una tasa definida para ese período"));
                    return;
                }
        }
        try {
            almacenarOModificarEntidad(tasaNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La  Tasa ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setTasa(String tasa) {
        this.tasa = tasa;
    }

    public String getTasa() {
        return tasa;
    }

    public void setFilaSeleccionada(TasaDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public TasaDT getFilaSeleccionada() {
        return filaSeleccionada;
    }

}
