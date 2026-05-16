/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datosbasicos.backing;

import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Chequera;
import datosbasicos.modelos.Usuario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import util.BeanUsuario;
import util.DaoCrud;
import util.DaoQuery;
import util.JSFUtil;
import java.util.Iterator;

/**
 *
 * @author jesus
 */
public class ElimBancoCtrl extends DaoCrud<Banco, Condominio, Integer> implements Serializable {

    public ElimBancoCtrl() {
    }

    public String regresar() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.si = false;
        beanUsuario.no = false;
        return "regresar";

    }

    public void eliminarBanco() throws ValidatorException {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Banco bancoSel = (Banco) ctx.getExternalContext().getSessionMap().get("Banco");

        Banco banco = buscarBancoCuentas(bancoSel.getId());
        banco.setEdoBanco("INA");

        for (Iterator<Cuenta> it = banco.getCuentas().iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            cta.setEdoCuenta("INA");
            Cuenta cuentaII = buscarCuentaChequeras(cta.getId());
            if (!(cuentaII == null)) {
                for (Iterator<Chequera> it2 = cuentaII.getChequeras().iterator();
                        it2.hasNext();) {
                    Chequera cheq = (Chequera) it2.next();
                    cheq.setEdoElimChequera("INA");
                }

                DaoQuery<Cuenta> dataAccesI = new DaoQuery<Cuenta>();
                try {
                    dataAccesI.almacenarOModificarEntidad(cuentaII);

                } catch (HibernateException he) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                    return;
                }
            }
        }

        try {
            almacenarOModificarEntidad(banco);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.si = true;
        beanUsuario.no = true;

        ctx.getExternalContext().getSessionMap().remove("Banco");
        ctx.getExternalContext().getSessionMap().remove("BancoDT");
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El Banco " + bancoSel.getNombBanco() + " ha sido eliminado de la Base de datos, recuerde actualizar fondos relacionados "));
        
    }

    public Banco buscarBancoCuentas(Integer idBanco) {

        Banco banco = null;

        String nombQuery = "buscarBancoFetchCuentas";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return banco;
    }

    public Cuenta buscarCuentaChequeras(Integer idCta) {

        Cuenta cuenta = null;

        String nombQuery = "buscarCuentaFetchChequeras";
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();
        try {
            cuenta = dataAcces.buscarEntidadPorId(idCta, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return cuenta;
    }
}
