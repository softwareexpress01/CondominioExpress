/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import datosbasicos.modelos.ProveedorDT;
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
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;

public class ProveedorCtrl extends DaoCrud<Proveedor, Condominio, Integer> implements Serializable {

    private String nombProveedor;
    private String cedRifProveedor;
    private String dirProveedor;
    private String emailProveedor;
    private String contactoProveedor;
    private String codTel1Proveedor;
    private String tel1Proveedor;
    private String codTel2Proveedor;
    private String tel2Proveedor;
    private String codOperCelProveedor;
    private String telCelProveedor;
    Collection<ProveedorDT> listaProvDT = new ArrayList<ProveedorDT>();
    Collection<Proveedor> listProv = new ArrayList<Proveedor>();
    private ProveedorDT filaSeleccionada;

    public ProveedorCtrl() {
    }

    public void inicializarPanel() {

        this.setDirProveedor(null);
        this.setNombProveedor(null);
        this.setEmailProveedor(null);
        this.setCedRifProveedor(null);
        this.setContactoProveedor(null);
        this.setCodTel1Proveedor(null);
        this.setTel1Proveedor(null);
        this.setCodTel2Proveedor(null);
        this.setTel2Proveedor(null);
        this.setCodOperCelProveedor(null);
        this.setTelCelProveedor(null);

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

        if (enBlanco.equals(nombProveedor)) {
            mensaje = "El nombre del Proveedor está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(nombProveedor)) {
            mensaje = "El nombre del Proveedor debe ser alfanumérico. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esCedRif(cedRifProveedor)) {
            mensaje = "Formato Ced/Rif debe ser V9999999 o J301211150. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esAlfaNumPlus(dirProveedor)) {
            mensaje = "La Dirección del Proveedor debe ser alfanumérica. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esEmail(emailProveedor)) {
            mensaje = "Formato del eMail es Incorrecto. Por favor intente de nuevo";
            return mensaje;
        }
        if (!enBlanco.equals(contactoProveedor)) {
            if (!val.esAlfaNumPlus(contactoProveedor)) {
                mensaje = "Nombre del Contacto debe ser alfabético. Por favor intente de nuevo";
                return mensaje;
            }
        }
        if (enBlanco.equals(codTel1Proveedor)) {
            mensaje = "El código del teléfono 1 no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esEntero(codTel1Proveedor)) {
            mensaje = "Código del teléfono 1 debe ser un valor numérico mayor que cero (Ejem: 0212). Por favor intente de nuevo";
            return mensaje;
        }

        Integer codTel1 = Integer.parseInt(codTel1Proveedor);
        if (codTel1 == 0) {
            mensaje = "Código del teléfono 1 no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(codTel1Proveedor.length() == 4)) {
            mensaje = "Código del teléfono 1 debe ser de 4 dígitos. Por favor intente de nuevo";
            return mensaje;
        }

        if (enBlanco.equals(tel1Proveedor)) {
            mensaje = "El número del teléfono 1 no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esEntero(tel1Proveedor)) {
            mensaje = "Número del teléfono 1 debe ser numérico mayor que cero. Por favor intente de nuevo";
            return mensaje;
        }
        Integer tel1 = Integer.parseInt(tel1Proveedor);

        if (tel1 == 0) {
            mensaje = "El número del teléfono 1 no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(tel1Proveedor.length() <= 8)) {
            mensaje = "Número de teléfono 1 debe ser máximo de 8 dígitos. Por favor intente de nuevo";
            return mensaje;
        }
        if (enBlanco.equals(codTel2Proveedor)) {
            mensaje = "El código del teléfono 2 no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esEntero(codTel2Proveedor)) {
            mensaje = "Código del teléfono 2 debe ser un valor numérico mayor que cero(Ejem: 0212). Por favor intente de nuevo";
            return mensaje;
        }

        Integer codTel2 = Integer.parseInt(codTel2Proveedor);

        if (codTel2 == 0) {
            mensaje = "Código del teléfono 2 no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(codTel2Proveedor.length() == 4)) {
            mensaje = "Código del teléfono 2 debe ser de 4 dígitos. Por favor intente de nuevo";
            return mensaje;
        }

        if (enBlanco.equals(tel2Proveedor)) {
            mensaje = "El número del teléfono 2 no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }
         if (!val.esEntero(tel2Proveedor)) {
            mensaje = "Número del teléfono 2 debe ser numérico mayor que cero. Por favor intente de nuevo";
            return mensaje;
        }
        Integer tel2 = Integer.parseInt(tel2Proveedor);

        if (tel2 == 0) {
            mensaje = "El número del teléfono 2 no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(tel2Proveedor.length() <= 8)) {
            mensaje = "Número de teléfono 2 debe ser máximo de 8 dígito. Por favor intente de nuevos";
            return mensaje;
        }

        if (enBlanco.equals(codOperCelProveedor)) {
            mensaje = "Código del teléfono Celular no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esEntero(codOperCelProveedor)) {
            mensaje = "Código del teléfono Celular debe ser un valor numérico mayor que cero (Ejem: 0416). Por favor intente de nuevo";
            return mensaje;
        }
        Integer codTelCel = Integer.parseInt(codOperCelProveedor);

        if (enBlanco.equals(codOperCelProveedor)) {
            mensaje = "El códi está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (codTelCel == 0) {
            mensaje = "Código del teléfono Celular no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        
        if (!(codOperCelProveedor.length() == 4)) {
            mensaje = "Código del teléfono celular debe ser de 4 dígitos. Por favor intente de nuevo";
            return mensaje;
        }

        if (enBlanco.equals(telCelProveedor)) {
            mensaje = "El número del teléfono Celular no puede estar en blanco. Por favor intente de nuevo";
            return mensaje;
        }
        if (!val.esEntero(telCelProveedor)) {
            mensaje = "Teléfono celular debe ser numérico mayor que cero. Por favor intente de nuevo";
            return mensaje;
        }
        Integer telCel = Integer.parseInt(telCelProveedor);

        if (telCel == 0) {
            mensaje = "El número del teléfono celular no puede ser ceros. Por favor intente de nuevo";
            return mensaje;
        }

        if (!(telCelProveedor.length() <= 8)) {
            mensaje = "Número de teléfono celular  debe ser máximo de 8 dígitos. Por favor intente de nuevo";
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

    public Collection getProveedoresPorCondominio() {

        // Obtiene las proveedores para el condominio dado (id)
        // Obtiene el id del condominio del beanUsuario
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaProvDT;
        }

        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listProv = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        if (listProv.isEmpty()) {
            return listaProvDT;
        }

        Collection listaOrd = ordenarLista(listProv);// para ordenar la lista de proveedores

        listaProvDT.clear();
        listaProvDT = inicializarProveedorDT(listaOrd);

        beanUsuario.ejecutarQueryDT = false;

        return listaProvDT;
    }

    private Collection<ProveedorDT> inicializarProveedorDT(Collection listaOrd) {

        for (Iterator<Proveedor> it = listaOrd.iterator();
                it.hasNext();) {

            Proveedor prov = (Proveedor) it.next();
            if (prov == null) {// Si no hay gastos comunes devuelve la lista en blanco
                break;
            } else {
                ProveedorDT provDT = new ProveedorDT();
                provDT.setId(prov.getId());
                provDT.setNombProveedor(prov.getNombProveedor());
                provDT.setCedRifProveedor(prov.getCedRifProveedor());
                provDT.setDirProveedor(prov.getDirProveedor());
                provDT.setEmailProveedor(prov.getEmailProveedor());
                provDT.setContactoProveedor(prov.getContactoProveedor());

                provDT.setTel1ConcatProveedor(prov.getTel1Proveedor());
                provDT.setTel2ConcatProveedor(prov.getTel2Proveedor());
                provDT.setTelCelConcatProveedor(prov.getTelCelProveedor());


                listaProvDT.add(provDT);
            }
        }
        return listaProvDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<Proveedor> listaDesOrdenada = new ArrayList<Proveedor>();

        for (Iterator<Proveedor> it = lista.iterator();
                it.hasNext();) {
            Proveedor prov = (Proveedor) it.next();
            listaDesOrdenada.add(prov);
        }
        Collections.sort(listaDesOrdenada, new ComparadorProv());

        Collection<Proveedor> coleccionOrdenada = new ArrayList<Proveedor>();


        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Proveedor prov = listaDesOrdenada.get(i);

            coleccionOrdenada.add(prov);
        }
        return coleccionOrdenada;
    }

    public void editarProveedorAction() {

        ProveedorDT provSel = this.getFilaSeleccionada();

        FacesContext ctx = FacesContext.getCurrentInstance();

        ctx.getExternalContext().getSessionMap().put("ProveedorDT", provSel);

        this.setNombProveedor(provSel.getNombProveedor());
        this.setCedRifProveedor(provSel.getCedRifProveedor());
        this.setDirProveedor(provSel.getDirProveedor());
        this.setEmailProveedor(provSel.getEmailProveedor());
        this.setContactoProveedor(provSel.getContactoProveedor());

        String telefono;         /* lo siguiente es para separar el telefono en cod + numero */

        telefono = provSel.getTel1ConcatProveedor();
        this.setCodTel1Proveedor(telefono.substring(0, 4));
        this.setTel1Proveedor(telefono.substring(5));

        telefono = provSel.getTel2ConcatProveedor();
        this.setCodTel2Proveedor(telefono.substring(0, 4));
        this.setTel2Proveedor(telefono.substring(5));

        telefono = provSel.getTelCelConcatProveedor();
        this.setCodOperCelProveedor(telefono.substring(0, 4));
        this.setTelCelProveedor(telefono.substring(5));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(provSel.getNombProveedor());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idProv = provSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de Fondos, listaOrd
        Proveedor prov = null;

        for (Iterator<Proveedor> it = listProv.iterator();
                it.hasNext();) {
            prov = (Proveedor) it.next();
            if (prov.getId().toString().equals(idProv)) {
                //Almacena la fila del fondo seleccionado ,el cual se buscara como "Fondo" en el map #{Fondo} 
                ctx.getExternalContext().getSessionMap().put("Proveedor", prov);
                break;
            }
        }

//Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 
        ctx.getExternalContext().getSessionMap().put("Proveedor", prov);


        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

    }

    public void registrarProveedor() throws ValidatorException {


        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        Proveedor proveedorNuevo = new Proveedor();
        proveedorNuevo.setNombProveedor(nombProveedor);
        proveedorNuevo.setCedRifProveedor(cedRifProveedor);
        proveedorNuevo.setDirProveedor(dirProveedor);
        proveedorNuevo.setEmailProveedor(emailProveedor);
        proveedorNuevo.setContactoProveedor(contactoProveedor);
        proveedorNuevo.setEdoProveedor("ACT");

        String guion = "-";
        proveedorNuevo.setTel1Proveedor(codTel1Proveedor.concat(guion.concat(tel1Proveedor)));
        proveedorNuevo.setTel2Proveedor(codTel2Proveedor.concat(guion.concat(tel2Proveedor)));
        proveedorNuevo.setTelCelProveedor(codOperCelProveedor.concat(guion.concat(telCelProveedor)));

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

        proveedorNuevo.setCondominio(condominio);

        try {
            almacenarOModificarEntidad(proveedorNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Proveedor " + proveedorNuevo.getNombProveedor() + " ha sido registrado satisfactoriamente!"));
        return;
    }

    public void modificarProveedor() throws ValidatorException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        Proveedor provSel = new Proveedor();

        provSel.setNombProveedor(nombProveedor);
        provSel.setCedRifProveedor(cedRifProveedor);
        provSel.setDirProveedor(dirProveedor);
        provSel.setEmailProveedor(emailProveedor);
        provSel.setContactoProveedor(contactoProveedor);
        provSel.setEdoProveedor("ACT");
        String guion = "-";
        provSel.setTel1Proveedor(codTel1Proveedor.concat(guion.concat(tel1Proveedor)));
        provSel.setTel2Proveedor(codTel2Proveedor.concat(guion.concat(tel2Proveedor)));
        provSel.setTelCelProveedor(codOperCelProveedor.concat(guion.concat(telCelProveedor)));

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
        provSel.setCondominio(condominio);

        Proveedor provCtx = (Proveedor) JSFUtil.getManagedObject("Proveedor"); /*Buscar el Id del objeto a Modificar para Update */
        Integer idProv = provCtx.getId();
        provSel.setId(idProv);

        try {
            almacenarOModificarEntidad(provSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();
        beanUsuario.ejecutarQueryDT = true;

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Proveedor " + provSel.getNombProveedor() + " ha sido modificada satisfactoriamente!"));
        return;
    }

    public void setNombProveedor(String nombProveedor) {
        this.nombProveedor = nombProveedor;
    }

    public String getNombProveedor() {
        return nombProveedor;
    }

    public void setCedRifProveedor(String cedRifProveedor) {
        this.cedRifProveedor = cedRifProveedor;
    }

    public String getCedRifProveedor() {
        return cedRifProveedor;
    }

    public void setDirProveedor(String dirProveedor) {
        this.dirProveedor = dirProveedor;
    }

    public String getDirProveedor() {
        return dirProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setContactoProveedor(String contactoProveedor) {
        this.contactoProveedor = contactoProveedor;
    }

    public String getContactoProveedor() {
        return contactoProveedor;
    }

    public void setCodTel1Proveedor(String codTel1Proveedor) {
        this.codTel1Proveedor = codTel1Proveedor;
    }

    public String getCodTel1Proveedor() {
        return codTel1Proveedor;
    }

    public void setTel1Proveedor(String tel1Proveedor) {
        this.tel1Proveedor = tel1Proveedor;
    }

    public String getTel1Proveedor() {
        return tel1Proveedor;
    }

    public void setCodTel2Proveedor(String codTel2Proveedor) {
        this.codTel2Proveedor = codTel2Proveedor;
    }

    public String getCodTel2Proveedor() {
        return codTel2Proveedor;

    }

    public void setTel2Proveedor(String tel2Proveedor) {
        this.tel2Proveedor = tel2Proveedor;
    }

    public String getTel2Proveedor() {
        return tel2Proveedor;

    }

    public void setCodOperCelProveedor(String codOperCelProveedor) {
        this.codOperCelProveedor = codOperCelProveedor;
    }

    public String getCodOperCelProveedor() {
        return codOperCelProveedor;

    }

    public void setTelCelProveedor(String telCelProveedor) {
        this.telCelProveedor = telCelProveedor;
    }

    public String getTelCelProveedor() {
        return telCelProveedor;

    }

    public void setFilaSeleccionada(ProveedorDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public ProveedorDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
