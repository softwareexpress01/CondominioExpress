/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.BancoDT;
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

public class BancoCtrl extends DaoCrud<Banco, Condominio, Integer> implements Serializable {

    private String nombBanco;
    private String nombOficBanco;
    private String codTelBanco;
    private String telBanco;
    Collection<BancoDT> listaBancosDT = new ArrayList<BancoDT>();
    Collection<Banco> listBancos = new ArrayList<Banco>();

    private BancoDT filaSeleccionada;
    
    public BancoCtrl() {
    }

    public void inicializarPanel() {

        this.setNombBanco(null);
        this.setNombOficBanco(null);
        this.setCodTelBanco(null);
        this.setTelBanco(null);


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

        return "datosbasicos";

    }

    public String validarPanel() {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        if (nombBanco.equals(enBlanco)) {
            mensaje = "Por favor, ingrese el Nombre del Banco";
            return mensaje;
        }
        
        if (!val.esNombApell(nombBanco)) {
            mensaje = "El nombre del Banco debe ser alfabético";
            return mensaje;
        }

        if (!val.esNombApell(nombOficBanco)) {
            mensaje = "El Nombre de la Oficina debe ser alfabético";
            return mensaje;
        }

        if (codTelBanco.equals(enBlanco)) {
            mensaje = "Código del teléfono está en blanco. Por favor intente de nuevo.";
            return mensaje;
        }
        
        
     
        if (!val.esEntero(codTelBanco)) {
            mensaje = "Código del teléfono debe ser numérico mayor que cero";
            return mensaje;
        }
        Integer codTelInt = Integer.parseInt(codTelBanco);
        if (codTelInt <= 0) {
            mensaje = "Código del teléfono debe ser numérico mayor que cero";
            return mensaje;
        }
        if (!(codTelBanco.length() == 4)) {
            mensaje = "Código del teléfono  debe ser de 4 dígitos";
            return mensaje;
        }
        
        
        if (telBanco.equals(enBlanco)) {
            mensaje = "El número del teléfono está en blanco. Por favor intente de nuevo.";
            return mensaje;
        }
        
        
        
        if (!val.esEntero(telBanco)) {
            mensaje = "Número del teléfono debe ser numérico";
            return mensaje;
        }
        Integer telInt = Integer.parseInt(telBanco);
        
        if (telInt <= 0) {
            mensaje = "Número del teléfono debe ser numérico mayor que cero";
            return mensaje;
        }
        if (!(telBanco.length() <= 8)) {
            mensaje = "Número de teléfono debe ser máximo de 8 dígitos";
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
    
    public Collection getBancosPorCondominio() {

        // Obtiene los bancos para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaBancosDT;
        }

        String nombQuery = "buscarBancosPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listBancos = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        Collection listaOrd = ordenarLista(listBancos);// para ordenar la lista de bancos

        listaBancosDT = inicializarBancosDT(listaOrd);

        beanUsuario.ejecutarQueryDT = false;

        return listaBancosDT;
    }

    private Collection<BancoDT> inicializarBancosDT(Collection listaOrd) {

        listaBancosDT.clear();

        for (Iterator<Banco> it = listaOrd.iterator();
                it.hasNext();) {

            Banco banco = (Banco) it.next();
            if (banco == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                BancoDT bancoDT = new BancoDT();
                bancoDT.setId(banco.getId());
                bancoDT.setNombBanco(banco.getNombBanco());
                bancoDT.setNombOficBanco(banco.getNombOficBanco());
                bancoDT.setTelConcatBanco(banco.getTelBanco());

                listaBancosDT.add(bancoDT);
            }
        }
        return listaBancosDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Banco> listaDesOrdenada = new ArrayList<Banco>();

        for (Iterator<Banco> it = lista.iterator();
                it.hasNext();) {
            Banco banco = (Banco) it.next();
            listaDesOrdenada.add(banco);
        }
        Collections.sort(listaDesOrdenada, new ComparadorBanco());

        Collection<Banco> coleccionOrdenada = new ArrayList<Banco>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Banco banco = listaDesOrdenada.get(i);
            coleccionOrdenada.add(banco);
        }
        return coleccionOrdenada;
    }

    public void editarBancoAction() {

        BancoDT bancoSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("BancoDT", bancoSel);

        this.setNombBanco(bancoSel.getNombBanco());
        this.setNombOficBanco(bancoSel.getNombOficBanco());
        this.setCodTelBanco(bancoSel.getCodTelBanco());
        this.setTelBanco(bancoSel.getTelBanco());

        String telefono;         /* lo siguiente es para separar el telefono en cod + numero */

        telefono = bancoSel.getTelConcatBanco();
        this.setCodTelBanco(telefono.substring(0, 4));
        this.setTelBanco(telefono.substring(5));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(bancoSel.getNombBanco());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idProv = bancoSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Fondos, listaOrd
        Banco banco = null;

        for (Iterator<Banco> it = listBancos.iterator();
                it.hasNext();) {
            banco = (Banco) it.next();
            if (banco.getId().toString().equals(idProv)) {
                //Almacena la fila del bancoi seleccionado ,el cual se buscara como "Banco" en el map #{Fondo} 
                ctx.getExternalContext().getSessionMap().put("Banco", banco);
                break;
            }
        }

        //Almacena la fila del banco seleccionado ,el cual se buscara como "Banco" en el map #{Concepto} 

        ctx.getExternalContext().getSessionMap().put("Banco", banco);


        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarBanco() throws ValidatorException {


        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Banco bancoNuevo = new Banco();
        bancoNuevo.setNombBanco(nombBanco);
        bancoNuevo.setNombOficBanco(nombOficBanco);
        bancoNuevo.setEdoBanco("ACT");

        String guion = "-";
        bancoNuevo.setTelBanco(codTelBanco.concat(guion.concat(telBanco)));

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

        /*        condominio.getBancos().add(bancoNuevo);*/
        bancoNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(bancoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Banco " + bancoNuevo.getNombBanco() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarBanco() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Banco bancoSel = new Banco();

        bancoSel.setNombBanco(nombBanco);
        bancoSel.setNombOficBanco(nombOficBanco);
        bancoSel.setEdoBanco("ACT");
        String guion = "-";
        bancoSel.setTelBanco(codTelBanco.concat(guion.concat(telBanco)));

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
        bancoSel.setCondominio(condominio);

        Banco bancoCtx = (Banco) JSFUtil.getManagedObject("Banco"); /*Buscar el Id del objeto a Modificar para Update */
        Integer idBanco = bancoCtx.getId();
        bancoSel.setId(idBanco);

        try {
            almacenarOModificarEntidad(bancoSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;


        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Banco " + bancoSel.getNombBanco() + " ha sido modificado satisfactoriamente!"));
        return;
    }

    public void setNombBanco(String nombBanco) {
        this.nombBanco = nombBanco;
    }

    public String getNombBanco() {
        return nombBanco;
    }

    public void setNombOficBanco(String nombOficBanco) {
        this.nombOficBanco = nombOficBanco;
    }

    public String getNombOficBanco() {
        return nombOficBanco;
    }

    public void setCodTelBanco(String codTelBanco) {
        this.codTelBanco = codTelBanco;
    }

    public String getCodTelBanco() {
        return codTelBanco;
    }

    public void setTelBanco(String telBanco) {
        this.telBanco = telBanco;
    }

    public String getTelBanco() {
        return telBanco;
    }
    
     public void setFilaSeleccionada(BancoDT filaSeleccionada) {
        	this.filaSeleccionada = filaSeleccionada;
    		}

    	   public BancoDT getFilaSeleccionada() {
        	return filaSeleccionada;
    		}

    
}
