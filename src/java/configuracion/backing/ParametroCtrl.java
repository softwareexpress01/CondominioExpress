/**
 * Backing bean para registro xhtml
 */
package configuracion.backing;

/**
 *
 * @author jesus
 */
import configuracion.modelos.Parametro;
import configuracion.modelos.ParametroActMod;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.hibernate.HibernateException;
import util.*;

public class ParametroCtrl extends DaoCrud<Parametro, Condominio, Integer> implements Serializable {

    private Parametro parametroNuevo = new Parametro();

    public ParametroCtrl() {
    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.mostrar = false;
        beanUsuario.mostrarMonto = false;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.mostrar = false;
        beanUsuario.mostrarMonto = false;

        return "configuracion";

    }

    public void mostrarMontoFijo(ValueChangeEvent vce) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        FacesContext ctx = FacesContext.getCurrentInstance();
        ParametroActMod parametroActMod = (ParametroActMod) ctx.getExternalContext().getSessionMap().get("ParametroActMod");
        String monto = "monto";
        if (parametroActMod.getTipoFacturacion().equals(monto)) {
            String cero = "0";
            parametroActMod.setMtoFijoFacturacion(cero);
            beanUsuario.setMostrarMonto(false);
        } else {
            beanUsuario.setMostrarMonto(true);
            String cero = "0";
            parametroActMod.setMtoFijoFacturacion(cero);
        }
    }

    public String validarPanel() {

        FacesContext ctx = FacesContext.getCurrentInstance();
        ParametroActMod parametroActMod = (ParametroActMod) ctx.getExternalContext().getSessionMap().get("ParametroActMod");

        String enBlanco = new String();
        String mensaje = new String();
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        //-----------------------------------------------------------------------------------
        if (parametroActMod.getProxAvisoCobro().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del próximo aviso de cobro...";
            return mensaje;
        }
        if (!val.esEntero(parametroActMod.getProxAvisoCobro())) {
            mensaje = "El próximo aviso de cobro debe ser un número entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------   
        if (parametroActMod.getProxNotaDebCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de la próxima nota de debito de CxC...";
            return mensaje;
        }
        if (!val.esEntero(parametroActMod.getProxNotaDebCxC())) {
            mensaje = "El número de la próxima nota de debito de CxC debe ser un número entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if (parametroActMod.getProxReciboCobro().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número del próximo recibo de cobro...";
            return mensaje;
        }
        if (!val.esEntero(parametroActMod.getProxReciboCobro())) {
            mensaje = "El próximo recibo de cobro debe ser un número entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }
        //----------------------------------------------------------------------------------- 
        if (parametroActMod.getProxNotaCredCxC().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de la próxima nota de credito de CxC...";
            return mensaje;
        }
        if (!val.esEntero(parametroActMod.getProxNotaCredCxC())) {
            mensaje = "El numero de la próxima nota de credito de CxC debe ser un número entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }

        //-----------------------------------------------------------------------------------

        if (parametroActMod.getProxNotaCredCxP().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el número de la próima nota de credito de CxP...";
            return mensaje;
        }
        if (!val.esEntero(parametroActMod.getProxNotaCredCxP())) {
            mensaje = "El numero de la próxima nota de credito de CxP debe ser un número entero mayor que cero, por favor, intente de nuevo...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if (parametroActMod.getPorcMora().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el porcentaje de mora de CxC...";
            return mensaje;
        }

        if (!val.esMontoCero(parametroActMod.getPorcMora())) {
            mensaje = "El % de mora debe ser entero o decimal, mayor o igual que cero. Use punto decimal si lo requiere.Por favor, intente de nuevo";
            return mensaje;
        }
        if (Double.parseDouble(parametroActMod.getPorcMora()) > 100) {
            mensaje = "El % de mora debe ser menor que 100.Por favor, intente de nuevo";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        if ((Double.parseDouble(parametroActMod.getPorcMora()) > 0)) {
            if (parametroActMod.getNumDiasVenCxC().equals(enBlanco)) {
                mensaje = "Por favor, ingrese el número de días de vencimiento de las facturas de CxC...";
                return mensaje;
            }

            if ((!val.esEntero(parametroActMod.getNumDiasVenCxC()))) {
                mensaje = "El nro. de días de vencimiento de las facturas de cxc, debe ser entero positivo. Por favor, intente de nuevo";
                return mensaje;
            }
        }
        //-----------------------------------------------------------------------------------
        if (parametroActMod.getMtoFijoFacturacion().equals(enBlanco)) {
            mensaje = "Por favor, ingrese el monto fijo de facturacion...";
            return mensaje;
        }
        //-----------------------------------------------------------------------------------
        String monto = "monto";
        if (parametroActMod.getTipoFacturacion().equals(monto)) {
            if (!val.esDecimal(parametroActMod.getMtoFijoFacturacion())) {
                mensaje = "El monto fijo de facturacion debe ser un número entero o decimal mayor que cero, por favor, intente de nuevo...";
                return mensaje;
            }
        }
        //-----------------------------------------------------------------------------------

        return mensaje;
    }

    public void registrarParametro() {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();
        String nombCond = beanUsuario.getCondominioActual();
        Condominio condominio = null;
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        FacesContext ctx = FacesContext.getCurrentInstance();
        ParametroActMod parametroActMod = (ParametroActMod) ctx.getExternalContext().getSessionMap().get("ParametroActMod");
        //-----------------------------------------------------------------------------------
        //Convertir de String a Double o Integer segun sea el caso antes de pasarlo a Parametro
        //-----------------------------------------------------------------------------------
        parametroNuevo.setId(parametroActMod.getId());

        Integer proxAviso = Integer.parseInt(parametroActMod.getProxAvisoCobro());
        parametroNuevo.setProxAvisoCobro(proxAviso);

        Integer proxRecibo = Integer.parseInt(parametroActMod.getProxReciboCobro());
        parametroNuevo.setProxReciboCobro(proxRecibo);

        Integer debCxC = Integer.parseInt(parametroActMod.getProxNotaDebCxC());
        parametroNuevo.setProxNotaDebCxC(debCxC);

        Integer credCxC = Integer.parseInt(parametroActMod.getProxNotaCredCxC());
        parametroNuevo.setProxNotaCredCxC(credCxC);

        Integer credCxP = Integer.parseInt(parametroActMod.getProxNotaCredCxP());
        parametroNuevo.setProxNotaCredCxP(credCxP);

        parametroNuevo.setTipoFacturacion(parametroActMod.getTipoFacturacion());

        Double mtoFijo = Double.valueOf(parametroActMod.getMtoFijoFacturacion());
        parametroNuevo.setMtoFijoFacturacion(mtoFijo);

        Float porcMora = Float.valueOf(parametroActMod.getPorcMora());
        parametroNuevo.setPorcMora(porcMora);


        parametroNuevo.setNumDiasVenCxC(Integer.parseInt(parametroActMod.getNumDiasVenCxC()));

        parametroNuevo.setObsAvisoCobro(parametroActMod.getObsAvisoCobro());
        parametroNuevo.setObsAvisoCobro2(parametroActMod.getObsAvisoCobro2());

        parametroNuevo.setObsReciboPago(parametroActMod.getObsReciboPago());
        //-----------------------------------------------------------------------------------

        parametroNuevo.setCondominio(condominio);
        try {
            almacenarOModificarEntidad(parametroNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Los parametros del condominio " + nombCond + " han sido actualizados satisfactoriamente!"));
        return;
    }
}
