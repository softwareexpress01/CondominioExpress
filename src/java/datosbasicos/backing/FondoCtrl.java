/*-
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Fondo;
import datosbasicos.modelos.FondoDT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

public class FondoCtrl extends DaoCrud<Fondo, Condominio, Integer> implements Serializable {

    private String fecFondo;
    private String descFondo;
    private String tipoFondo;
    private String porcentFondo;
    private String idBcoFondo;
    private String idCtaFondo;
    private String mtoIniFondo;
    private String saldoFondo;
    private String obsFondo;
    private Collection listBan = new ArrayList();
    private Collection listFondo = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private Collection<FondoDT> listaFondoDT = new ArrayList<FondoDT>();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    private FondoDT filaSeleccionada;
    private String tipoFondoSeleccionado;

    public FondoCtrl() {
    }

    public void inicializarPanel() {

        this.setFecFondo(null);
        this.setDescFondo(null);
        this.setTipoFondo(null);
        this.setPorcentFondo(null);
        /*this.setIdBcoFondo(null);
        this.setIdCtaFondo(null); No limpiar estos id para que muestre el DT actualizado cuando agrega o modifica objetos*/
        this.setMtoIniFondo(null);
        this.setSaldoFondo(null);
        this.setObsFondo(null);
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
        return "datosbasicos";

    }

    public String validarPanel() {

        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");


        Integer idBcoInteger = Integer.parseInt(idBcoFondo);

        if (!(idBcoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el banco, por favor intente de nuevo";
            return mensaje;
        }

        Integer idCtaInteger = Integer.parseInt(idCtaFondo);

        if (!(idCtaInteger > 0)) {
            mensaje = " Aun no ha seleccionado una Cuenta, por favor intente de nuevo ";
            return mensaje;
        }

        if (!(fecFondo.length() > 0)) {
            mensaje = "El campo Fecha está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esFecha(fecFondo)) {
            mensaje = "La Fecha debe ser con el formato DD-MM-AAAA, por favor intente de nuevo ";
            return mensaje;
        }

        if (!(descFondo.length() > 0)) {
            mensaje = "El campo Descripción está en blanco, por favor intente de nuevo ";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(descFondo)) {
            mensaje = "El campo Descripción no es válido, por favor intente de nuevo ";
            return mensaje;
        }

        if ("Seleccione".equals(tipoFondo)) {
            mensaje = "Debe seleccionar el tipo de Fondo, por favor intente de nuevo  ";
            return mensaje;
        }
        if ("Reserva".equals(tipoFondo)) {
            if (!val.esMonto(porcentFondo)) {
                mensaje = "El Porcentaje debe ser numérico. Use punto decimal si lo requiere. Por favor, intente de nuevo ";
                return mensaje;
            }
        }
        if (!val.esMontoCero(mtoIniFondo)) {
            mensaje = "El Monto inicial debe ser numérico. Use punto decimal si lo requiere. Por favor intente de nuevo";
            return mensaje;
        }

        Float mtoFloat = Float.valueOf(mtoIniFondo);

        if (mtoFloat < 0) {
            mensaje = "El Monto inicial no puede ser negativo. Por favor intente de nuevo";
            return mensaje;
        }



        return mensaje;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBcoFondo == null) {
            return listaOrd;
        }

        Integer idBcoInteger = Integer.parseInt(idBcoFondo);

        if (!(idBcoInteger > 0)) {
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


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (cuentas.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return listaOrd;

        } else {
            if (!beanUsuario.modificar) {
                beanUsuario.agregar = true;
            } else {
                beanUsuario.agregar = false;
            }
        }

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
        }

        return listaOrd;

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
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (bancos.isEmpty()) {
            listBan.clear();
            listBan.add(new SelectItem(new Integer(0), "Sin Configurar"));
            beanUsuario.agregar = true;
            beanUsuario.modificar = true;
            beanUsuario.eliminar = true;
            return (List) listBan;

        } else {
            if (!beanUsuario.modificar) {
                beanUsuario.agregar = true;

            }
        }

        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listBan;
    }

    public Collection getFondosPorCondominio() {


        listFondo.clear();
        listaOrdII.clear();
        listaFondoDT.clear();

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

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
            return (List) listaFondoDT;
        } else {
            listaFondoDT = inicializarFondoDT(listFondo);
            return listaFondoDT;
        }
    }

    private Boolean FondoReservaExiste() {

        Boolean existe = false;

        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {

            Fondo fdo = (Fondo) it.next();
            if (fdo.getTipoFondo().equals("Reserva")) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    private Collection<FondoDT> inicializarFondoDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        for (Iterator<Fondo> it = listaOrd.iterator();
                it.hasNext();) {

            Fondo fdo = (Fondo) it.next();
            if (fdo == null) {// Si no hay fONDOS DEFINIDOS devuelve la lista en blanco
                break;
            } else {
                Integer idBanInteger = fdo.getIdBcoFondo();
                String nombBco = buscarNombBco(idBanInteger);
                Integer idCtaInteger = fdo.getIdCtaFondo();
                String ctaBco = buscarCtaBco(idCtaInteger);

                FondoDT fondoDT = new FondoDT();

                fondoDT.setId(fdo.getId());

                fondoDT.setFecFondo(fdo.getFecFondo());

                fondoDT.setNombBcoFondo(nombBco);
                fondoDT.setCtaBcoFondo(ctaBco);

                fondoDT.setDescFondo(fdo.getDescFondo());
                fondoDT.setTipoFondo(fdo.getTipoFondo());

                fondoDT.setPorcentFondo(fdo.getPorcentFondo());
                fondoDT.setPorcentFondoString(val.convertirDoubleAString(fdo.getPorcentFondo()));
                fondoDT.setIdBcoFondo(fdo.getIdBcoFondo());
                fondoDT.setIdCtaFondo(fdo.getIdCtaFondo());
                fondoDT.setMtoIniFondo(fdo.getMtoIniFondo());
                fondoDT.setMtoIniFondoString(val.convertirDoubleAString(fdo.getMtoIniFondo()));
                fondoDT.setSaldoFondo(fdo.getSaldoFondo());
                fondoDT.setSaldoFondoString(val.convertirDoubleAString(fdo.getSaldoFondo()));
                fondoDT.setObsFondo(fdo.getObsFondo());

                listaFondoDT.add(fondoDT);
            }
        }
        return listaFondoDT;
    }

    public String buscarNombBco(Integer idBco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public String buscarCtaBco(Integer idCta) {

        Cuenta cuenta;
        String nombQuery = "buscarCuentaPorId";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String descCuenta = cuenta.getNumCuenta();
        return descCuenta;
    }

    public void editarFondoAction() {

        FondoDT fondoSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "FondoDT" en el map 
        ctx.getExternalContext().getSessionMap().put("FondoDT", fondoSel);

        this.setFecFondo(fondoSel.getFecFondo());
        this.setDescFondo(fondoSel.getDescFondo());
        this.setTipoFondo(fondoSel.getTipoFondo());

        idBcoFondo = fondoSel.getIdBcoFondo().toString();
        idCtaFondo = fondoSel.getIdCtaFondo().toString();
        tipoFondoSeleccionado = fondoSel.getTipoFondo();
        this.setObsFondo(fondoSel.getObsFondo());

        //-----------------------------------------------------------        
        //                  Convertir de Double a String
        //-----------------------------------------------------------

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        this.setPorcentFondo(val.convertirDoubleAString(fondoSel.getPorcentFondo()));
        this.setMtoIniFondo(val.convertirDoubleAString(fondoSel.getMtoIniFondo()));
        this.setSaldoFondo(val.convertirDoubleAString(fondoSel.getSaldoFondo()));
        //----------------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(fondoSel.getDescFondo());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idFondo = fondoSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Fondos, listaOrd
        Fondo fondo = null;

        for (Iterator<Fondo> it = listFondo.iterator();
                it.hasNext();) {
            fondo = (Fondo) it.next();
            if (fondo.getId().toString().equals(idFondo)) {
                //Almacena la fila del fondo seleccionado ,el cual se buscara como "Fondo" en el map #{Fondo} 
                ctx.getExternalContext().getSessionMap().put("Fondo", fondo);
                break;
            }
        }

        ctx.getExternalContext().getSessionMap().put("Fondo", fondo);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarFondo() throws ValidatorException {

        String vacio = new String();
        saldoFondo = mtoIniFondo;

        if ("Reserva".equals(tipoFondo)) {
            if (FondoReservaExiste()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Ya existe un Fondo de reserva. Por favor, intente de nuevo."));
                return;
            }
        }

        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }



        Fondo fondoNuevo = new Fondo();

        fondoNuevo.setFecFondo(fecFondo);
        fondoNuevo.setDescFondo(descFondo);
        fondoNuevo.setTipoFondo(tipoFondo);
        fondoNuevo.setIdBcoFondo(Integer.parseInt(idBcoFondo));
        fondoNuevo.setIdCtaFondo(Integer.parseInt(idCtaFondo));
        //-----------------------------------------------------------        
        //                  Convertir de String a Double y mover
        //-----------------------------------------------------------


        /*if ((!(porcentFondo.isEmpty())) &  (!(porcentFondo == null))){
        mto = Double.valueOf(porcentFondo).doubleValue();
        fondoNuevo.setPorcentFondo(mto);
        } else {
        fondoNuevo.setPorcentFondo(000.00);
        }*/

        // Si es Fondo de reserva, le asigna el porcentaje tipiado, sino, el porcentaje es cero siempre ya que no se va a usar en los otros tipos de Fondos.
        Double mto = 0.0;
        fondoNuevo.setPorcentFondo(000.00);

        if ("Reserva".equals(tipoFondo)) {
            mto = Double.valueOf(porcentFondo).doubleValue();
            fondoNuevo.setPorcentFondo(mto);

        }

        mto = Double.valueOf(mtoIniFondo).doubleValue();
        fondoNuevo.setMtoIniFondo(mto);
        fondoNuevo.setSaldoFondo(mto);
        //-----------------------------------------------------------
        fondoNuevo.setObsFondo(obsFondo);
        fondoNuevo.setEdoFondo("ACT");

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

        // buscar Cuenta para establecer el vínculo 

        Cuenta cta = null;
        Integer idCta = Integer.parseInt(idCtaFondo);
        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            cta = (Cuenta) it.next();
            if (cta.id == idCta) {
                break;
            }

        }

        /*condominio.getFondos().add(fondoNuevo);*/
        fondoNuevo.setCuenta(cta);
        fondoNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(fondoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones FONDO NUEVO...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Fondo " + fondoNuevo.getDescFondo() + " ha sido registrado satisfactoriamente!"));



    }

    public void modificarFondo() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        // no se permite cambiar un fondo de reserva a otro tipo ya que debe haber al menos uno (de reserva)

        if ((tipoFondoSeleccionado.equals("Reserva")) & (!(tipoFondo.equals("Reserva")))) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Cambio del fondo de reserva a otro tipo no está permitido. Por favor, intente de nuevo."));
            return;
        }

        // si se quiere cambiar a fondo de reserva,se valida que no exista ninguno

        if ((!(tipoFondoSeleccionado.equals("Reserva"))) & (tipoFondo.equals("Reserva"))) {
            if (FondoReservaExiste()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Ya existe un fondo de reserva. Por favor, intente de nuevo."));
                return;
            }
        }


        Fondo fondoSel = (Fondo) JSFUtil.getManagedObject("Fondo");

        fondoSel.setFecFondo(fecFondo);
        fondoSel.setDescFondo(descFondo);
        fondoSel.setTipoFondo(tipoFondo);

        fondoSel.setIdBcoFondo(Integer.parseInt(idBcoFondo));
        fondoSel.setIdCtaFondo(Integer.parseInt(idCtaFondo));
        //-----------------------------------------------------------        
        //                  Convertir de String a Double y mover
        //-----------------------------------------------------------
        Double mto = 0.0;
        fondoSel.setPorcentFondo(000.00);

        if ("Reserva".equals(tipoFondo)) {
            mto = Double.valueOf(porcentFondo).doubleValue();
            fondoSel.setPorcentFondo(mto);

        }

        mto = Double.valueOf(mtoIniFondo).doubleValue();
        fondoSel.setMtoIniFondo(mto);
        mto = Double.valueOf(saldoFondo).doubleValue();
        fondoSel.setSaldoFondo(mto);
        //-----------------------------------------------------------
        fondoSel.setObsFondo(obsFondo);
        fondoSel.setEdoFondo("ACT");

        try {
            almacenarOModificarEntidad(fondoSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Falla al modificar"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Fondo " + fondoSel.getDescFondo() + " ha sido modificado satisfactoriamente!"));

    }

    public void setFecFondo(String fecFondo) {
        this.fecFondo = fecFondo;
    }

    public String getFecFondo() {
        return fecFondo;
    }

    public void setDescFondo(String descFondo) {
        this.descFondo = descFondo;
    }

    public String getDescFondo() {
        return descFondo;
    }

    public void setTipoFondo(String tipoFondo) {
        this.tipoFondo = tipoFondo;
    }

    public String getTipoFondo() {
        return tipoFondo;
    }

    public void setPorcentFondo(String porcentFondo) {
        this.porcentFondo = porcentFondo;
    }

    public String getPorcentFondo() {
        return porcentFondo;
    }

    public void setIdBcoFondo(String idBcoFondo) {
        this.idBcoFondo = idBcoFondo;
    }

    public String getIdBcoFondo() {
        return idBcoFondo;
    }

    public void setIdCtaFondo(String idCtaFondo) {
        this.idCtaFondo = idCtaFondo;
    }

    public String getIdCtaFondo() {
        return idCtaFondo;
    }

    public void setMtoIniFondo(String mtoIniFondo) {
        this.mtoIniFondo = mtoIniFondo;
    }

    public String getMtoIniFondo() {
        return mtoIniFondo;
    }

    public void setSaldoFondo(String saldoFondo) {
        this.saldoFondo = saldoFondo;
    }

    public String getSaldoFondo() {
        return saldoFondo;

    }

    public void setObsFondo(String obsFondo) {
        this.obsFondo = obsFondo;
    }

    public String getObsFondo() {
        return obsFondo;

    }

    public void setFilaSeleccionada(FondoDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public FondoDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
