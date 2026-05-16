/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
import movimientos.modelos.CuotaEsp;
import movimientos.modelos.CuotaEspDT;
import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import java.util.GregorianCalendar;

import java.util.Date;
import java.text.SimpleDateFormat;


import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import java.util.Calendar;

public class CuotaEspCtrl extends DaoCrud<CuotaEsp, Condominio, Integer> implements Serializable {

    private String fecCuotaEsp;
    private String mtoTotCuotaEsp;
    private String calcCuotaEsp;
    private String idFondoCuotaEsp;
    private String nombFondoCuotaEsp;
    private String idConceptoCuotaEsp;
    private String descConceptoCuotaEsp;
    private String nroMesesCuotaEsp;
    private String fecApartirCuotaEsp;
    private Double saldoCuotaEsp;
    private String estadoCuotaEsp;
    private String obsCuotaEsp;
    private Collection listaOrd = new ArrayList();
    private Collection<CuotaEspDT> listaCuotaEspDT = new ArrayList<CuotaEspDT>();
    private Collection<CuotaEsp> listCuotaEsp = new ArrayList<CuotaEsp>();
    private Collection<CuotaEsp> listCuotaEspSel = new ArrayList<CuotaEsp>();
    private CuotaEspDT filaSeleccionada;

    public CuotaEspCtrl() {
    }

    public void inicializarPanel() {

        this.setMtoTotCuotaEsp(null);
        this.setCalcCuotaEsp(null);
        this.setIdFondoCuotaEsp(null);
        this.setNombFondoCuotaEsp(null);
        this.setIdConceptoCuotaEsp(null);
        this.setDescConceptoCuotaEsp(null);
        this.setNroMesesCuotaEsp(null);
        this.setFecApartirCuotaEsp(null);
        this.setObsCuotaEsp(null);

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

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Integer idConceptoInteger = Integer.parseInt(idConceptoCuotaEsp);

        if (!(idConceptoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idConceptoCuotaEsp)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }

        Integer idFondoInteger = Integer.parseInt(idFondoCuotaEsp);

        if (!(idFondoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Fondo, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(idFondoCuotaEsp)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esMonto(mtoTotCuotaEsp)) {
            mensaje = "El campo Monto debe ser mayor que cero. Use punto decimal si lo requiere, por favor intente de nuevo. ";
            return mensaje;
        }

        if ("Seleccione".equals(calcCuotaEsp)) {
            mensaje = " Aun no ha seleccionado en el campo Calcular, por favor, intente de nuevo";
            return mensaje;
        }

        if (!val.esEntero(nroMesesCuotaEsp)) {
            mensaje = "El campo Número de Meses debe ser numérico mayor que cero, por favor intente de nuevo. ";
            return mensaje;
        }

        Integer nroMesesInteger = Integer.parseInt(nroMesesCuotaEsp);
        if (nroMesesInteger == 0) {
            mensaje = "El campo Número de Meses no puede ser cero, por favor intente de nuevo. ";
            return mensaje;
        }

        if (!val.esFecha(fecApartirCuotaEsp)) {
            mensaje = "La Fecha A partir de, debe ser con el formato dd-mm-aaaa, por favor intente de nuevo. ";
            return mensaje;
        }
        System.out.println("El substring de la fecha es:" + "" + fecApartirCuotaEsp.substring(0, 2));
        if (!fecApartirCuotaEsp.substring(0, 2).equals("01")) {
            mensaje = "El dia de la Fecha A partir de, debe ser 01 (comienzo de período), por favor intente de nuevo. ";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getFondosPorCondominio() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        // Obtiene los Fondos para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Fondo> listFondo = new ArrayList<Fondo>();
        String nombQuery = "buscarFondosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listFondo = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listFondo.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }
        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {
            Fondo fon = (Fondo) it.next();
            listaOrd.add(new SelectItem(fon.getId(), fon.getDescFondo()));
        }

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Fondo> listaDesOrdenada = new ArrayList<Fondo>();

        for (Iterator<Fondo> it = lista.iterator();
                it.hasNext();) {
            Fondo fondo1 = (Fondo) it.next();
            listaDesOrdenada.add(fondo1);
        }
        Collections.sort(listaDesOrdenada, new ComparadorFondo());

        Collection<Fondo> coleccionOrdenada = new ArrayList<Fondo>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Fondo fondo2 = listaDesOrdenada.get(i);
            coleccionOrdenada.add(fondo2);
        }
        return coleccionOrdenada;
    }

    public Collection getConceptosPorCondominio() {

        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));


        // Obtiene los conceptos para el condominio dado (id), los muestra en el panel 1
        // Obtiene el id del condominio del beanUsuario

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Collection<Concepto> listCon = new ArrayList<Concepto>();
        String nombQuery = "buscarConceptosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();

        try {
            listCon = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        if (listCon.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        }


        for (Iterator<Concepto> it = listCon.iterator();
                it.hasNext();) {
            Concepto con = (Concepto) it.next();
            listaOrd.add(new SelectItem(con.getId(), con.getNombConcepto()));
        }


        return listaOrd;
    }

    public Collection getCuotasEspPorCondominio() {

        listaCuotaEspDT.clear();
        listCuotaEspSel.clear();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombQuery = "buscarTodosCuotasEspPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listCuotaEsp = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listCuotaEsp.isEmpty()) {
            return listaCuotaEspDT;
        }

        /*
         * Collection listaOrd = ordenarLista(listFondo);// para ordenar la
         * lista de fondos
         */

        /**
         * **************** AQUI SE SELECCIONAN SOLO AQUELLOS OBJETOS CON
         * ESTADO DIFERENTE DE "CANC" *************
         */
        for (Iterator<CuotaEsp> it = listCuotaEsp.iterator();
                it.hasNext();) {
            CuotaEsp ce = (CuotaEsp) it.next();
            if (!(ce.estadoCuotaEsp.equals("CANC"))) {
                listCuotaEspSel.add(ce);
            }
        }
        /**
         * *******************************************************************************************************
         */
        listaCuotaEspDT = inicializarFondoDT(listCuotaEspSel);

        return listaCuotaEspDT;
    }

    private Collection<CuotaEspDT> inicializarFondoDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<CuotaEsp> it = listaOrd.iterator();
                it.hasNext();) {
            CuotaEsp cuotaEsp = (CuotaEsp) it.next();
            if (cuotaEsp == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                Integer idFondoInteger = cuotaEsp.getIdFondoCuotaEsp();
                String nombFondo = buscarFondo(idFondoInteger);

                Integer idConcInteger = cuotaEsp.getIdConceptoCuotaEsp();
                String nombConc = buscarConcepto(idConcInteger);

                CuotaEspDT cuotaEspDT = new CuotaEspDT();

                cuotaEspDT.setId(cuotaEsp.getId());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = cuotaEsp.getFecCuotaEsp();
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

                cuotaEspDT.setFecCuotaEsp(stringFechaMov);
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                String mtoCuotaEsp = val.convertirDoubleAString(cuotaEsp.getMtoTotCuotaEsp());    /*
                 * De Doble a String
                 */
                cuotaEspDT.setMtoTotCuotaEsp(mtoCuotaEsp);

                cuotaEspDT.setCalcCuotaEsp(cuotaEsp.getCalcCuotaEsp());
                cuotaEspDT.setIdFondoCuotaEsp(cuotaEsp.getIdFondoCuotaEsp());
                cuotaEspDT.setNombFondoCuotaEsp(nombFondo);
                cuotaEspDT.setIdConceptoCuotaEsp(cuotaEsp.getIdConceptoCuotaEsp());
                cuotaEspDT.setDescConceptoCuotaEsp(nombConc);
                cuotaEspDT.setNroMesesCuotaEsp(cuotaEsp.getNroMesesCuotaEsp());

                String stringFec = calendarAString(cuotaEsp.getFecApartirCuotaEsp());

                cuotaEspDT.setFecApartirCuotaEsp(stringFec);
                cuotaEspDT.setSaldoCuotaEsp(val.convertirDoubleAString(cuotaEsp.getSaldoCuotaEsp()));
                cuotaEspDT.setEstadoCuotaEsp(cuotaEsp.getEstadoCuotaEsp());
                cuotaEspDT.setObsCuotaEsp(cuotaEsp.getObsCuotaEsp());

                listaCuotaEspDT.add(cuotaEspDT);
            }
        }
        return listaCuotaEspDT;
    }

    public String calendarAString(Calendar fecha) {

        Calendar StringFec = fecha;
        String perDia = "";
        String perMes = "";
        int dMov = fecha.get(Calendar.DAY_OF_MONTH);
        int mMov = fecha.get(Calendar.MONTH) + 1;
        int aMov = fecha.get(Calendar.YEAR);

        if (dMov < 10) {
            perDia = "0" + dMov;
        } else {
            perDia = String.valueOf(dMov);
        }

        if (mMov < 10) {
            perMes = "0" + mMov;
        } else {
            perMes = String.valueOf(mMov);
        }

        String stringFec = perDia + "-" + perMes + "-" + aMov;

        return stringFec;

    }

    public String buscarFondo(Integer idFondo) {

        Fondo fondo;
        String nombQuery = "buscarFondoPorId";
        DaoQuery<Fondo> dataAcces = new DaoQuery<Fondo>();
        try {
            fondo = dataAcces.buscarEntidadPorId(idFondo, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        String nombreFdo = fondo.getDescFondo();
        return nombreFdo;
    }

    public String buscarConcepto(Integer idConc) {

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
        String nombConc = concepto.getNombConcepto();
        return nombConc;
    }

    public void editarCuotaEspAction() {

        CuotaEspDT cuotaEspSel = this.getFilaSeleccionada();

        if ("EPRO".equals(cuotaEspSel.getEstadoCuotaEsp())) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("La Cuota especial seleccionada esta en Progreso, no se puede modificar ni eliminar."));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila de la Cuota Especial  seleccionada la cual se buscara como "CuotaEsp" en el map #{CuotaEsp} 
        ctx.getExternalContext().getSessionMap().put("CuotaEspDT", cuotaEspSel);

        this.setCalcCuotaEsp(cuotaEspSel.getCalcCuotaEsp());
        this.setIdFondoCuotaEsp(cuotaEspSel.getIdFondoCuotaEsp().toString());
        this.setIdConceptoCuotaEsp(cuotaEspSel.getIdConceptoCuotaEsp().toString());
        this.setNroMesesCuotaEsp(cuotaEspSel.getNroMesesCuotaEsp().toString());
        this.setFecApartirCuotaEsp(cuotaEspSel.getFecApartirCuotaEsp());

        this.setObsCuotaEsp(cuotaEspSel.getObsCuotaEsp());

        this.setMtoTotCuotaEsp(cuotaEspSel.getMtoTotCuotaEsp());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(cuotaEspSel.getDescConceptoCuotaEsp());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idCuotaEsp = cuotaEspSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Cuotas especiales, listaOrd
        CuotaEsp cuotaEsp = null;

        for (Iterator<CuotaEsp> it = listCuotaEsp.iterator();
                it.hasNext();) {
            cuotaEsp = (CuotaEsp) it.next();
            if (cuotaEsp.getId().toString().equals(idCuotaEsp)) {
                //Almacena la fila del fondo seleccionado ,el cual se buscara como "CuotEsp" en el map #{cuotaEsp} 
                ctx.getExternalContext().getSessionMap().put("CuotaEsp", cuotaEsp);
                break;
            }
        }

//Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
        ctx.getExternalContext().getSessionMap().put("CuotaEsp", cuotaEsp);


        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarCuotaEsp() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();


        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        CuotaEsp cuotaEspSel = new CuotaEsp();

        Calendar fecha = new GregorianCalendar();
        cuotaEspSel.setFecCuotaEsp(fecha);

        cuotaEspSel.setCalcCuotaEsp(calcCuotaEsp);
        cuotaEspSel.setIdFondoCuotaEsp(Integer.parseInt(idFondoCuotaEsp));
        cuotaEspSel.setIdConceptoCuotaEsp(Integer.parseInt(idConceptoCuotaEsp));
        cuotaEspSel.setNroMesesCuotaEsp(Integer.parseInt(nroMesesCuotaEsp));

        //-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecApartirCuotaEsp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//----------------------------------------------------------

        cuotaEspSel.setFecApartirCuotaEsp(cal);
        cuotaEspSel.setEstadoCuotaEsp("PEND");
        cuotaEspSel.setObsCuotaEsp(obsCuotaEsp);

        Double mto = Double.valueOf(mtoTotCuotaEsp).doubleValue();
        cuotaEspSel.setMtoTotCuotaEsp(mto);
        cuotaEspSel.setSaldoCuotaEsp(mto);

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

        cuotaEspSel.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(cuotaEspSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Cuota Especial ha sido registrada satisfactoriamente!"));


    }

    public void modificarCuotaEsp() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        CuotaEsp cuotaEspSel = new CuotaEsp();

        CuotaEspDT cuotaEspDT = (CuotaEspDT) JSFUtil.getManagedObject("CuotaEspDT");

        cuotaEspSel.setEstadoCuotaEsp(cuotaEspDT.getEstadoCuotaEsp());

        cuotaEspSel.setCalcCuotaEsp(calcCuotaEsp);
        cuotaEspSel.setIdFondoCuotaEsp(Integer.parseInt(idFondoCuotaEsp));
        cuotaEspSel.setIdConceptoCuotaEsp(Integer.parseInt(idConceptoCuotaEsp));
        cuotaEspSel.setNroMesesCuotaEsp(Integer.parseInt(nroMesesCuotaEsp));

        //-----------------------------------------------------------
        //  Convertir el string de fecha a Calendar 
        //-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecApartirCuotaEsp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        //----------------------------------------------------------

        cuotaEspSel.setFecApartirCuotaEsp(cal);

        cuotaEspSel.setFecApartirCuotaEsp(cal);
        cuotaEspSel.setObsCuotaEsp(obsCuotaEsp);

        Calendar fecha = new GregorianCalendar();
        cuotaEspSel.setFecCuotaEsp(fecha);

        Double mto = Double.valueOf(mtoTotCuotaEsp).doubleValue();
        cuotaEspSel.setMtoTotCuotaEsp(mto);
        cuotaEspSel.setSaldoCuotaEsp(mto);

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


        cuotaEspSel.setCondominio(condominio);

        CuotaEsp beanCuotaEsp = (CuotaEsp) JSFUtil.getManagedObject("CuotaEsp");
        cuotaEspSel.id = beanCuotaEsp.id;

        try {
            almacenarOModificarEntidad(cuotaEspSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }


        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La Cuota Especial ha sido modificada satisfactoriamente"));

    }

    public void setFecCuotaEsp(String fecCuotaEsp) {
        this.fecCuotaEsp = fecCuotaEsp;
    }

    public String getFecCuotaEsp() {
        return fecCuotaEsp;
    }

    public void setMtoTotCuotaEsp(String mtoTotCuotaEsp) {
        this.mtoTotCuotaEsp = mtoTotCuotaEsp;
    }

    public String getMtoTotCuotaEsp() {
        return mtoTotCuotaEsp;
    }

    public void setCalcCuotaEsp(String calcCuotaEsp) {
        this.calcCuotaEsp = calcCuotaEsp;
    }

    public String getCalcCuotaEsp() {
        return calcCuotaEsp;
    }

    public void setIdFondoCuotaEsp(String idFondoCuotaEsp) {
        this.idFondoCuotaEsp = idFondoCuotaEsp;
    }

    public String getIdFondoCuotaEsp() {
        return idFondoCuotaEsp;
    }

    public void setNombFondoCuotaEsp(String nombFondoCuotaEsp) {
        this.nombFondoCuotaEsp = nombFondoCuotaEsp;
    }

    public String getNombFondoCuotaEsp() {
        return nombFondoCuotaEsp;
    }

    public void setIdConceptoCuotaEsp(String idConceptoCuotaEsp) {
        this.idConceptoCuotaEsp = idConceptoCuotaEsp;
    }

    public String getIdConceptoCuotaEsp() {
        return idConceptoCuotaEsp;
    }

    public void setDescConceptoCuotaEsp(String descConceptoCuotaEsp) {
        this.descConceptoCuotaEsp = descConceptoCuotaEsp;
    }

    public String getDesConceptoCuotaEsp() {
        return descConceptoCuotaEsp;
    }

    public void setNroMesesCuotaEsp(String nroMesesCuotaEsp) {
        this.nroMesesCuotaEsp = nroMesesCuotaEsp;
    }

    public String getNroMesesCuotaEsp() {
        return nroMesesCuotaEsp;
    }

    public void setFecApartirCuotaEsp(String fecApartirCuotaEsp) {
        this.fecApartirCuotaEsp = fecApartirCuotaEsp;
    }

    public String getFecApartirCuotaEsp() {
        return fecApartirCuotaEsp;
    }

    public void setSaldoCuotaEsp(Double saldoCuotaEsp) {
        this.saldoCuotaEsp = saldoCuotaEsp;
    }

    public Double getSaldoCuotaEsp() {
        return saldoCuotaEsp;

    }

    public void setEstadoCuotaEsp(String estadoCuotaEsp) {
        this.estadoCuotaEsp = estadoCuotaEsp;
    }

    public String getEstadoCuotaEsp() {
        return estadoCuotaEsp;
    }

    public void setObsCuotaEsp(String obsCuotaEsp) {
        this.obsCuotaEsp = obsCuotaEsp;
    }

    public String getObsCuotaEsp() {
        return obsCuotaEsp;

    }

    public void setFilaSeleccionada(CuotaEspDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public CuotaEspDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
