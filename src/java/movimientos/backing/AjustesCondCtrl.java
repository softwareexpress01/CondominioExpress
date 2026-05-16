package movimientos.backing;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Concepto;
import datosbasicos.modelos.Condominio;
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


import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.AjustesCond;
import movimientos.modelos.AjustesCondDT;
import org.hibernate.HibernateException;
import util.*;

public class AjustesCondCtrl extends DaoCrud<AjustesCond, Condominio, Integer> implements Serializable {

    private String tipoAjustesCond;
    private String idConcepto;
    private String descAjustesCond;
    public String mtoAjustesCond;
    private String perAjustesCond;
    private Collection<AjustesCond> listaAjustesCond = new ArrayList<AjustesCond>();
    private Collection<AjustesCond> listaAjustesCondSel = new ArrayList<AjustesCond>();
    private Collection<AjustesCondDT> listaAjustesCondDT = new ArrayList<AjustesCondDT>();
    private List listaProveedores = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private List listaConceptos = new ArrayList();
    private AjustesCondDT filaSeleccionada;

    public AjustesCondCtrl() {
    }

    public void inicializarPanel() {


        this.setTipoAjustesCond(null);
        this.setIdConcepto(null);
        this.setMtoAjustesCond(null);
        this.setPerAjustesCond(null);
        
        this.setDescAjustesCond("");

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
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0); 

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        String cero = "0";

        if (!(tipoAjustesCond.length() > 0)) {
            mensaje = " Aun no ha seleccionado el tipo de ajuste, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(tipoAjustesCond)) {
            mensaje = " Aun no ha seleccionado el tipo de ajuste, por favor intente de nuevo";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------
        // Verifica si selecciono algun Concepto   (diferente de la opcion Seleccione)
        //--------------------------------------------------------------------------------------
        if (idConcepto.toString().equals(cero)) {//selecciono la que aparece como Seleccione
            mensaje = "Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(idConcepto)) {
            mensaje = " Aun no ha seleccionado el tipo de ajuste, por favor intente de nuevo";
            return mensaje;
        }
        if (descAjustesCond.equals(enBlanco)) {
            mensaje = "Aun no ha ingresado una descripción, por favor intente de nuevo";
            return mensaje;
        }

        //--------------------------------------------------------------------------------------
        if (this.getMtoAjustesCond().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto ...";
            return mensaje;
        }
        if (!val.esMonto(this.getMtoAjustesCond())) {
            mensaje = "El monto debe ser mayor que cero, use punto decimal si lo requiere. Por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que haya ingresado el periodo
        //--------------------------------------------------------------------
        String perGasto = perAjustesCond;

        if (perGasto.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el perido al cual va a asignar el gasto ...";
            return mensaje;
        }
        //-------------------------------------------------------------------
        // Verifica que el formato del periodo sea el adecuado
        //--------------------------------------------------------------------

        if (!val.esFormatoPeriodo(this.perAjustesCond)) {
            mensaje = "El formato del periodo debe ser mm-aaaa, por favor intente de nuevo...";
            return mensaje;
        }

        //-------------------------------------------------------------------
        // Verifica que el formato este dentro del rango adecuado o valido
        //--------------------------------------------------------------------

        if (!val.esRangoValido(this.perAjustesCond)) {
            mensaje = "El periodo debe el periodo actual o el siguiente, por favor intente de nuevo...";
            return mensaje;
        }

        return mensaje;
    }

    public Collection getAjustesCondPorCond() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        /*if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
         return listaAjustesCondDT;
         }*/

        listaAjustesCondDT.clear();
        listaAjustesCond.clear();
        listaAjustesCondSel.clear();

        // Obtiene las notas de credito/debito para el Proveedor dado (id), los muestra en el panel 2

        Integer idCond = Integer.valueOf(beanUsuario.getIdCondActual());
        String nombQuery = "buscarAjustesCondPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaAjustesCond = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listaAjustesCond.isEmpty()) {
            return listaAjustesCondDT;
        }

        /**
         * **************** AQUI SE SELECCIONAN SOLO AQUELLOS OBJETOS CON
         * ESTADO DIFERENTE DE "CANC" *************
         */
        for (Iterator<AjustesCond> it = listaAjustesCond.iterator();
                it.hasNext();) {
            AjustesCond ajustes = (AjustesCond) it.next();
            if (!(ajustes.edoAjustesCond.equals("CANC"))) {
                listaAjustesCondSel.add(ajustes);
            }
        }

        /**
         * *******************************************************************************************************
         */
        Collection listaOrd = ordenarLista(listaAjustesCondSel);
        // se inicializa el objeto  para el DataTable...
        listaAjustesCondDT = inicializarAjustesCondDT(listaOrd);

       
        return listaAjustesCondDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<AjustesCond> listaDesOrdenada = new ArrayList<AjustesCond>();

        for (Iterator<AjustesCond> it = lista.iterator();
                it.hasNext();) {
            AjustesCond ncd = (AjustesCond) it.next();
            listaDesOrdenada.add(ncd);
        }

        Collections.sort(listaDesOrdenada, new ComparadorAjustesCond());//devuelve la lista ordenada en listaDesOrdenada

        Collection<AjustesCond> coleccionOrdenada = new ArrayList<AjustesCond>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            AjustesCond nota = listaDesOrdenada.get(i);
            coleccionOrdenada.add(nota);
        }
        return coleccionOrdenada;
    }

    private Collection<AjustesCondDT> inicializarAjustesCondDT(Collection listaAjustesCond) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Collection listaNotas = new ArrayList();
        for (Iterator<AjustesCond> it = listaAjustesCond.iterator();
                it.hasNext();) {

            AjustesCond ajustes = (AjustesCond) it.next();
            if (ajustes == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                Integer idConc = ajustes.getIdConcepto();
                String nombConc = buscarNombConcepto(idConc);

                AjustesCondDT AjustesCondDT = new AjustesCondDT();

                AjustesCondDT.setId(ajustes.getId());
                AjustesCondDT.setIdConcepto(idConc);
                AjustesCondDT.setDescAjustesCond(ajustes.getDescAjustesCond());
                AjustesCondDT.setNombConcepto(nombConc);
                AjustesCondDT.setMtoAjustesCond(ajustes.getMtoAjustesCond());
                //----------------------------------------------------------------
                //Se da formato al monto para mostrarlo en el DT
                //----------------------------------------------------------------
                AjustesCondDT.setMtoAjustesCondString(val.convertirDoubleAString(ajustes.getMtoAjustesCond()));

                AjustesCondDT.setEdoAjustesCond(ajustes.getEdoAjustesCond());
                AjustesCondDT.setTipoAjustesCond(ajustes.getTipoAjustesCond());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = ajustes.getFecMovAjustesCond();
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

                AjustesCondDT.setFecMovAjustesCond(stringFechaMov);

                Calendar periodo = ajustes.getPerAjustesCond();
                String perDia = "";
                String perMes = "";
                int dMov = periodo.get(Calendar.DAY_OF_MONTH);
                int mMov = periodo.get(Calendar.MONTH) + 1;
                int aMov = periodo.get(Calendar.YEAR);

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

                String stringPer = perMes + "-" + aMov;
                AjustesCondDT.setPerAjustesCond(stringPer);


                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos con estado diferente de Cancelado
                //----------------------------------------------------------------------------------
                String cancelado = "CANC";
                if (!ajustes.getEdoAjustesCond().equals(cancelado)) {
                    listaNotas.add(AjustesCondDT);
                }

            }
        }
        return listaNotas;
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
        String descConcepto = concepto.getNombConcepto();
        return descConcepto;
    }

    public List getConceptosPorCondominio() {

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        /*if (!usuarioActual.getEjecutarQuery()) {//determina si ya se ejecuto el metodo, solo lo ejecuta una vez
         return ((List) listaConceptos);
         }*/
        listaConceptos.clear();
        listaConceptos.add(new SelectItem(new Integer(0), "Seleccione"));

        //-------------------------------------------------------------------------------------
        // Genera la lista de Conceptos para el condominio dado, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();
        List<Concepto> conceptos = new ArrayList<Concepto>();
        String nombQuery = "buscarConceptosPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            conceptos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (conceptos.isEmpty()) {// Si no hay conceptos devuelve la lista en blanco
            beanUsuario.agregar = true;
            beanUsuario.mostrar = false;
            beanUsuario.deshabRadio = true;
            listaConceptos.clear();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun concepto para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }
        for (int i = 0; i < conceptos.size(); i++) {
            Concepto conc = (Concepto) conceptos.get(i);
            listaConceptos.add(new SelectItem(conc.getId(), conc.getNombConcepto()));
        }
        return (List) listaConceptos;
    }

    public void editarAjustesCondAction() {

        AjustesCondDT ajustesCondSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado  
        ctx.getExternalContext().getSessionMap().put("AjustesCondDT", ajustesCondSel);

        this.setIdConcepto(ajustesCondSel.getIdConcepto().toString());
        this.setDescAjustesCond(ajustesCondSel.getDescAjustesCond());
        this.setTipoAjustesCond(ajustesCondSel.getTipoAjustesCond());
        this.setPerAjustesCond(ajustesCondSel.getPerAjustesCond());

        this.setMtoAjustesCond(ajustesCondSel.getMtoAjustesCondString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------

        Integer idConc = ajustesCondSel.getIdConcepto();
        String nombConc = buscarNombConcepto(idConc);

        beanUsuario.setNombEntidadElim(nombConc);

        String idNCDCxC = ajustesCondSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de gastos comunes ordinarios, listaGCOrd
        AjustesCond ajustesCond = null;
        for (Iterator<AjustesCond> it = listaAjustesCond.iterator();
                it.hasNext();) {
            ajustesCond = (AjustesCond) it.next();
            if (ajustesCond.getId().toString().equals(idNCDCxC)) {
                //Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
                ctx.getExternalContext().getSessionMap().put("AjustesCond", ajustesCond);
                break;
            }
        }

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarAjustesCond() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        AjustesCond ajustesCondNueva = new AjustesCond();

        ajustesCondNueva.setEdoAjustesCond("PEND");
        Integer idcon = Integer.valueOf(idConcepto).intValue();
        ajustesCondNueva.setIdConcepto(idcon);
        ajustesCondNueva.setTipoAjustesCond(tipoAjustesCond);
        ajustesCondNueva.setDescAjustesCond(descAjustesCond);

        // Integer cero = 0;
        //ajustesCondNueva.setIdPago(cero);// a este atributo se le asignará el id del pago de la notaCredDeb cuando ocurra el mismo

        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        ajustesCondNueva.setFecMovAjustesCond(fecha);

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        Double mto = Double.valueOf(mtoAjustesCond).doubleValue();
        //-----------------------------------------------------------
        /*if ("Credito".equals(tipoAjustesCond)) {
            ajustesCondNueva.setMtoAjustesCond(mto);
        } else {
            ajustesCondNueva.setMtoAjustesCond(mto * -1);
        }*/

        ajustesCondNueva.setMtoAjustesCond(mto);
        
        //-------------------------------------------------------------------
        // Convertir periodo a Calendar
        //-------------------------------------------------------------------

        String stringPeriodo = "01-" + perAjustesCond;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        ajustesCondNueva.setPerAjustesCond(cal);

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

        ajustesCondNueva.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(ajustesCondNueva);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        String nombConc = buscarNombConcepto(idcon);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Ajuste correspondiente a " + nombConc + " ha sido registrado satisfactoriamente!"));

    }

    public void actualizarParametro(Parametro parametro) throws ValidatorException {

        DaoCrudAux<Parametro> dataAcces = new DaoCrudAux<Parametro>();
        try {
            dataAcces.almacenarOModificarEntidad(parametro);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));

        }
    }

    public void modificarAjustesCond() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        AjustesCondDT ajustesCondSel = (AjustesCondDT) ctx.getExternalContext().getSessionMap().get("AjustesCondDT");
        String idAjuste = ajustesCondSel.getId().toString();
        //buscar objeto a modificar en el arreglo de gastos comunes ordinarios, listaGCOrd
        AjustesCond ajuste = null;
        for (Iterator<AjustesCond> it = listaAjustesCond.iterator();
                it.hasNext();) {
            ajuste = (AjustesCond) it.next();
            if (ajuste.getId().toString().equals(idAjuste)) {
                break;
            }
        }

        Integer idcon = Integer.valueOf(idConcepto).intValue();
        ajuste.setIdConcepto(idcon);
        ajuste.setTipoAjustesCond(tipoAjustesCond);
        ajuste.setDescAjustesCond(descAjustesCond);

        //Integer cero = 0;
        //ncd.setIdPago(cero); // a este atributo se le asignará el id del pago de la notaCredDeb cuando ocurra el mismo

        //-----------------------------------------------------------
        //                  Convertir de String a Double
        //-----------------------------------------------------------
        
        Double mto = Double.valueOf(mtoAjustesCond).doubleValue();
        /*if ("Credito".equals(tipoAjustesCond)) {
            ajuste.setMtoAjustesCond(mto);
        } else {
            ajuste.setMtoAjustesCond(mto * -1);
        }*/
        
        ajuste.setMtoAjustesCond(mto);

        //-----------------------------------------------------------

        //-------------------------------------------------------------------
        // Convertir periodo a Calendar
        //-------------------------------------------------------------------

        String stringPeriodo = "01-" + perAjustesCond;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(stringPeriodo);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //-----------------------------------------------------------
        ajuste.setPerAjustesCond(cal);

        try {
            almacenarOModificarEntidad(ajuste);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.ejecutarQueryDT = true;//Controla  la ejecucion repetida de los metodos get del data table

        Integer idConc = ajustesCondSel.getIdConcepto();
        String nombConc = buscarNombConcepto(idConc);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El ajuste correspondiente a " + nombConc + " ha sido modificado satisfactoriamente!"));

    }

    public void setTipoAjustesCond(String tipoAjustesCond) {
        this.tipoAjustesCond = tipoAjustesCond;
    }

    public String getTipoAjustesCond() {
        return tipoAjustesCond;
    }

    public void setMtoAjustesCond(String mtoAjustesCond) {
        this.mtoAjustesCond = mtoAjustesCond;
    }

    public String getMtoAjustesCond() {
        return mtoAjustesCond;
    }

    public void setIdConcepto(String idConcepto) {
        this.idConcepto = idConcepto;
    }

    public String getIdConcepto() {
        return idConcepto;
    }
    
    public void setDescAjustesCond(String descAjustesCond) {
        this.descAjustesCond = descAjustesCond;
    }

    public String getDescAjustesCond() {
        return descAjustesCond;
    }

    public void setPerAjustesCond(String perAjustesCond) {
        this.perAjustesCond = perAjustesCond;
    }

    public String getPerAjustesCond() {
        return perAjustesCond;
    }

    public void setFilaSeleccionada(AjustesCondDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public AjustesCondDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
