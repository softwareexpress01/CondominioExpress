/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package menuPrincipal.backing;

import util.BeanUsuario;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ConExpMenuCtrl {

    public ConExpMenuCtrl() {
    }

    public void inicializarBotones() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.deshabilitar = false;
        beanUsuario.si = false;
        beanUsuario.no = false;
        beanUsuario.mostrar = false;
        beanUsuario.mostrarMonto = false;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.revertir = true;
        beanUsuario.deshabRadio = false;
        beanUsuario.deshabilitar = false;
    }

   
    public String salir() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setOperaciones(false);
        inicializarBotones();
        
        return "salir";

    }

   
    /**          Informes    */
   
    public String distribucionDelGasto() {

        inicializarBotones();
        return "distribucion";

    }

    public String recibosCxC() {

        inicializarBotones();
        return "recibos";

    }

    public String recibosPagoAdel() {

        inicializarBotones();
        return "pagosAdel";

    }

    public String edoCtaInmueble() {

        inicializarBotones();
        return "edocuenta";

    }
    public String notificacionPago() {

        inicializarBotones();
        return "notificacionPago";

    }

    public String generarReporteMovDelMes() {

        inicializarBotones();
        return "movimientosDelMes";

    }

   
    public String generarReportePagosProveedor() {

        inicializarBotones();
        return "pagosProv";

    }

    public String generarAvisosDeCobro() {

        inicializarBotones();
        return "avisos";

    }

   
}
