/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuracion.backing;

import configuracion.modelos.Parametro;
import configuracion.modelos.ParametroActMod;
import datosbasicos.modelos.Condominio;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.JSFUtil;

/**
 *
 * @author jesus
 */
public class ConfiguracionCtrl extends DaoCrud<Parametro, Condominio, Integer> implements Serializable {

    private BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
    //  ----- Comentar las siguientes instruccion para ambiente de desarrollo ------
    //  private String http = "http://";
    //  private String ip = beanUsuario.getIpHost();
    //  private String puerto = ":8080/CondominioExpress/faces/";
    //   private String hostCond = http.concat(ip).concat(puerto);
    // ---------------------------------------------------------------------------
    private final String hostCond = beanUsuario.getHostCond();

    public ConfiguracionCtrl() {
    }

    public void buscarParametro() {

        Integer idCond = beanUsuario.getIdCondActual();
        Condominio condominio;
        try {
            condominio = (Condominio) encontrarEntidadPorId(idCond);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        ParametroActMod parametroActMod = new ParametroActMod();
        Parametro par = condominio.getParametro();

        if (par == null) {
            //Inicializa el radio a gasto...

            String tipofac = "gastos";
            parametroActMod.setTipoFacturacion(tipofac);
            String cero = "0";
            parametroActMod.setMtoFijoFacturacion(cero);
            beanUsuario.mostrarMonto = false;
            FacesContext ctx = FacesContext.getCurrentInstance();
            //Almacena un objeto Parametro en el sessionmap,el cual se buscara como "ParametroActMod" en el map #{ParametroActMod} 
            ctx.getExternalContext().getSessionMap().put("ParametroActMod", parametroActMod);
        } else {
            parametroActMod.setId(par.getId());
            parametroActMod.setProxAvisoCobro(par.getProxAvisoCobro().toString());
            parametroActMod.setProxReciboCobro(par.getProxReciboCobro().toString());
            parametroActMod.setProxNotaDebCxC(par.getProxNotaDebCxC().toString());
            parametroActMod.setProxNotaCredCxC(par.getProxNotaCredCxC().toString());

            parametroActMod.setPorcMora(par.getPorcMora().toString());
            parametroActMod.setNumDiasVenCxC(par.getNumDiasVenCxC().toString());
            parametroActMod.setProxNotaCredCxP(par.getProxNotaCredCxP().toString());
            parametroActMod.setObsAvisoCobro(par.getObsAvisoCobro());
            parametroActMod.setObsAvisoCobro2(par.getObsAvisoCobro2());
            parametroActMod.setObsReciboPago(par.getObsReciboPago());
            //---------------------------------------------
            // para asignar el valor al htmlSelectOneRadio.
            //---------------------------------------------
            String tipofac = par.getTipoFacturacion();
            parametroActMod.setTipoFacturacion(tipofac);
            if (tipofac.equals("monto")) {
                beanUsuario.mostrarMonto = true;
            } else {
                String cero = "0";
                parametroActMod.setMtoFijoFacturacion(cero);
                beanUsuario.mostrarMonto = false;
            }
            //---------------------------------------------
            parametroActMod.setMtoFijoFacturacion(par.getMtoFijoFacturacion().toString());

            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getExternalContext().getSessionMap().put("ParametroActMod", parametroActMod);
        }
        beanUsuario.mostrar2 = false;
        beanUsuario.mostrar1 = true;
        beanUsuario.setReporte(hostCond.concat("ParametroUI.xhtml"));
    }
}
