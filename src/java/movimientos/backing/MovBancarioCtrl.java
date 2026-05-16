/*-
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movimientos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Cuenta;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import movimientos.modelos.MovBancario;
import movimientos.modelos.MovBancarioDT;
import org.hibernate.HibernateException;
import util.*;

public class MovBancarioCtrl extends DaoCrud<MovBancario, Cuenta, Integer> implements Serializable {

    private String idBcoMovBancario;
    private String idCtaMovBancario;
    public String saldoMovBancario;
    private String tipoOperMovBancario;
    private String fecMovBancario;
    private String refMovBancario;
    private String mtoMovBancario;
    private String descMovBancario;
    private Collection listBan = new ArrayList();
    private Collection listMovBancario = new ArrayList();
    private Collection listaOrd = new ArrayList();
    private Collection listaOrdII = new ArrayList();
    private Collection<MovBancarioDT> listaMovBancarioDT = new ArrayList<MovBancarioDT>();
    private Collection<Cuenta> cuentas = new ArrayList<Cuenta>();
    public String saldoCta;
    public Double mtoPrev;
    public String tipoOper;
    Integer idMovBanInt;
    private MovBancarioDT filaSeleccionada;

    public MovBancarioCtrl() {
    }

    public void inicializarPanel() {

        this.setSaldoMovBancario(null);
        this.setTipoOperMovBancario(null);
        this.setFecMovBancario(null);
        this.setRefMovBancario(null);
        this.setMtoMovBancario(null);
        this.setDescMovBancario(null);

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

        beanUsuario.deshabilitar = false;

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

        beanUsuario.deshabilitar = false;

        return "movimientos";

    }

    public String validarPanel() throws ParseException {

        String enBlanco = new String();
        String mensaje = new String();

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Integer idBancoInteger = Integer.parseInt(idBcoMovBancario);

        if (!(idBancoInteger > 0)) {
            mensaje = " Aun no ha seleccionado el Banco, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idBcoMovBancario)) {
            mensaje = " Aun no ha seleccionado el Concepto, por favor intente de nuevo";
            return mensaje;
        }

        Integer idCuentaInteger = Integer.parseInt(idCtaMovBancario);

        if (!(idCuentaInteger > 0)) {
            mensaje = " Aun no ha seleccionado la Cuenta, por favor intente de nuevo";
            return mensaje;
        }
        if ("Seleccione".equals(idCtaMovBancario)) {
            mensaje = " Aun no ha seleccionado la Cuenta, por favor intente de nuevo";
            return mensaje;
        }

        if ("Seleccione".equals(tipoOperMovBancario)) {
            mensaje = " Aun no ha seleccionado el Tipo de Operación, por favor intente de nuevo";
            return mensaje;
        }

        if (fecMovBancario.equals(enBlanco)) {
            mensaje = "La Fecha del Movimiento está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        if (!val.esFecha(fecMovBancario)) {
            mensaje = "La Fecha debe ser con el formato DD-MM-AAAA ";
            return mensaje;
        }

//  Convertir el string de fecha a Calendar para verificar que la fecha del Mov sea del periodo actual o del siguiente
//--------------------------------------------------------------------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecMovBancario);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int mesMov = cal.get(Calendar.MONTH);
        int añoMov = cal.get(Calendar.YEAR);
        Integer mes = mesMov;
        Integer año = añoMov;
        Integer mesMas = mes + 1;

        String periodo = mesMas.toString() + "-" + año.toString();
        if (!val.esRangoValido(periodo)) {
            mensaje = "La Fecha del Movimiento debe ser del periodo actual o del próximo. Por favor intente de nuevo ";
            return mensaje;
        }

//---------------------------------------------------------------------------------------------------------------------



        if (!val.esEntero(refMovBancario)) {
            mensaje = "El Número de Referencia no es numérico entero";
            return mensaje;
        }

        mensaje = verificarRefMovBancario(refMovBancario); /* Se verifica que la referncia no exista en el mov.*/
        if (!mensaje.equals(enBlanco)) {
            return mensaje;
        }

        if (!val.esMonto(mtoMovBancario)) {
            mensaje = "El Monto debe se numérico sin signo. Use punto decimal si lo requiere. Por Favor intente de nuevo ";
            return mensaje;
        }

        if (descMovBancario.equals(enBlanco)) {
            mensaje = "La Descripción del Movimiento está en blanco. Por favor intente de nuevo";
            return mensaje;
        }

        return mensaje;
    }

    public String verificarRefMovBancario(String ref) {

        String mensaje = new String();

        if (listMovBancario.isEmpty()) {
            return mensaje;
        }
        if (idMovBanInt == 0) {

            for (Iterator<MovBancarioDT> it = listaMovBancarioDT.iterator();
                    it.hasNext();) {
                MovBancarioDT movBan = (MovBancarioDT) it.next();
                if (ref.equals(movBan.getRefMovBancario())) {
                    mensaje = "El número de referencia ya existe, por favor intente de nuevo";
                    break;
                }
            }
        }
        if (idMovBanInt != 0) {
            for (Iterator<MovBancarioDT> it = listaMovBancarioDT.iterator();
                    it.hasNext();) {
                MovBancarioDT movBan = (MovBancarioDT) it.next();

                if (ref.equals(movBan.getRefMovBancario())) {

                    if (!((movBan.getId().equals(idMovBanInt)))) {

                        mensaje = "El número de referencia ya existe, por favor intente de nuevo";
                        break;
                    }
                }
            }


        }
        return mensaje;
    }

    public Collection getCuentasPorBanco() {

        cuentas.clear();
        listaOrd.clear();
        listaOrd.add(new SelectItem(new Integer(0), "Seleccione"));

        if (idBcoMovBancario == null) {
            return listaOrd;
        }

        String nombQuery = "buscarCuentasPorIdBanco";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        Integer idBcoInteger = Integer.parseInt(idBcoMovBancario);
        try {
            cuentas = dataAcces.buscarListaEntidadesPorId(idBcoInteger, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return listaOrd;
        }


        if (cuentas.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;
            //beanUsuario.modificar = true;
            //beanUsuario.eliminar = true;
            return listaOrd;

        } else {
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            if (!beanUsuario.modificar) {
                beanUsuario.agregar = true;

            }
        }

        for (Iterator<Cuenta> it = cuentas.iterator();
                it.hasNext();) {
            Cuenta cta = (Cuenta) it.next();
            if (!(cta.getEdoCuenta().equals("INA"))) {
                listaOrd.add(new SelectItem(cta.getId(), cta.getNumCuenta()));
            }
        }
        return listaOrd;

    }

    public String getSaldoCta() {


        if (idCtaMovBancario == null) {
            return null;
        }
        if (idCtaMovBancario.equals("0")) {
            return null;
        }

        Integer idCtaSel = Integer.parseInt(idCtaMovBancario);
        Cuenta cuenta = null;
        try {
            cuenta = (Cuenta) encontrarEntidadPorId(idCtaSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return null;
        }

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        saldoCta = val.convertirDoubleAString(cuenta.getSaldoActCuenta());

        return saldoCta;
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
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        /*--------------------- VERFICACIÓN DE SI HAY BANCOS DEFINIDOS --------------------------------- */

        if (bancos.isEmpty()) {

            listaOrd.clear();
            listaOrd.add(new SelectItem(new Integer(0), "Sin Configurar"));
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = true;
            // beanUsuario.modificar = true;
            //beanUsuario.eliminar = true;
            return (List) listBan;

        } else {
            BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
            beanUsuario.agregar = false;
        }



        /*---------------------------------------------------------------------------------*/

        for (int i = 0; i < bancos.size(); i++) {
            Banco cat = (Banco) bancos.get(i);
            listBan.add(new SelectItem(cat.getId(), cat.getNombBanco()));
        }

        return (List) listBan;
    }

    public Collection getMovBancariosPorCuenta() {


        listMovBancario.clear();
        listaOrdII.clear();
        listaMovBancarioDT.clear();
        Collection listaMovBancarioSel = new ArrayList();
        if (idBcoMovBancario == null) {
            return listaMovBancarioDT;
        }
        if (idCtaMovBancario == null) {
            return listaOrd;
        }

        Integer idCtaInt = Integer.parseInt(idCtaMovBancario);

        String nombQuery = "buscarMovBancariosPorCuenta";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listMovBancario = dataAcces.buscarListaEntidadesPorId(idCtaInt, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde "));
            return (null);
        }

        if (listMovBancario.isEmpty()) {

            return (List) listaMovBancarioDT;
        }

        // Seleccionar los movimientos con idPago != null

        for (Iterator<MovBancario> it = listMovBancario.iterator();
                it.hasNext();) {
            MovBancario MovBancario = (MovBancario) it.next();

            if (!(MovBancario.getIdPago() == null)) {

                if (MovBancario.getIdPago() == 0) {

                    listaMovBancarioSel.add(MovBancario);
                }
            }
        }
        Collection listaOrdII = ordenarLista(listaMovBancarioSel);// para ordenar la lista de MovBancarios*/
        listaMovBancarioDT = inicializarMovBancarioDT(listaOrdII);
        return listaMovBancarioDT;

    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<MovBancario> listaDesOrdenada = new ArrayList<MovBancario>();

        for (Iterator<MovBancario> it = lista.iterator();
                it.hasNext();) {
            MovBancario MovBancario = (MovBancario) it.next();
            listaDesOrdenada.add(MovBancario);
        }
        Collections.sort(listaDesOrdenada, new ComparadorMovBancario());

        Collection<MovBancario> coleccionOrdenada = new ArrayList<MovBancario>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            MovBancario MovBancario = listaDesOrdenada.get(i);
            coleccionOrdenada.add(MovBancario);
        }
        return coleccionOrdenada;
    }

    private Collection<MovBancarioDT> inicializarMovBancarioDT(Collection listaOrd) {

        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        for (Iterator<MovBancario> it = listaOrd.iterator();
                it.hasNext();) {

            MovBancario fdo = (MovBancario) it.next();
            if (fdo == null) {// Si no hay MovBancarioS DEFINIDOS devuelve la lista en blanco
                break;
            } else {
                Integer idBanInteger = fdo.getIdBcoMovBancario();
                String nombBco = buscarNombBco(idBanInteger);
                Integer idCtaInteger = fdo.getIdCtaMovBancario();
                String ctaBco = buscarCtaBco(idCtaInteger);

                MovBancarioDT MovBancarioDT = new MovBancarioDT();

                MovBancarioDT.setId(fdo.getId());
                MovBancarioDT.setIdBcoMovBancario(fdo.getIdBcoMovBancario());
                MovBancarioDT.setIdCtaMovBancario(fdo.getIdCtaMovBancario());

                MovBancarioDT.setNombBcoMovBancario(nombBco);
                MovBancarioDT.setDescCtaMovBancario(ctaBco);

                MovBancarioDT.setTipoOperMovBancario(fdo.getTipoOperMovBancario());

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------

                Calendar fechaMov = fdo.getFecMovBancario();
                String diaM;
                String mesM;
                int diaMov = fechaMov.get(Calendar.DAY_OF_MONTH);
                int mesMov = fechaMov.get(Calendar.MONTH) + 1;
                int añoMov = fechaMov.get(Calendar.YEAR);

                if (diaMov < 10) {
                    diaM = "0" + diaMov;
                } else {
                    diaM = String.valueOf(diaMov);
                }

                if (mesMov < 10) {
                    mesM = "0" + mesMov;
                } else {
                    mesM = String.valueOf(mesMov);
                }

                String stringFechaMov = diaM + "-" + mesM + "-" + añoMov;

                //-----------------------------------------------------------------------

                MovBancarioDT.setFecMovBancario(stringFechaMov);
                MovBancarioDT.setRefMovBancario(fdo.getRefMovBancario());
                MovBancarioDT.setMtoMovBancario(fdo.getMtoMovBancario());
                MovBancarioDT.setDescMovBancario(fdo.getDescMovBancario());
                //----------------------------------------------------------------
                //Se da formato al monto y al saldo para mostrarlos en el DT
                //----------------------------------------------------------------
                MovBancarioDT.setMtoMovBancarioString(val.convertirDoubleAString(fdo.getMtoMovBancario()));
                listaMovBancarioDT.add(MovBancarioDT);
            }
        }
        return listaMovBancarioDT;
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

    public void editarMovBancarioAction() {

        MovBancarioDT MovBancarioSel = this.getFilaSeleccionada();
        idMovBanInt = MovBancarioSel.getId();

        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del condominio seleccionado la cual se buscara como "Propiedad" en el map #{Propiedad} 
        ctx.getExternalContext().getSessionMap().put("MovBancarioDT", MovBancarioSel);

        this.setIdBcoMovBancario(MovBancarioSel.getIdBcoMovBancario().toString());
        this.setIdCtaMovBancario(MovBancarioSel.getIdCtaMovBancario().toString());
        this.setFecMovBancario(MovBancarioSel.getFecMovBancario().toString());
        this.setRefMovBancario(MovBancarioSel.getRefMovBancario());

        // Si el mov viene con signo negativo - según el tipo de operación que lo originó - se cambia a positivo para el panel 1 para evitar 
        // problemas con las validaciones

        Double mtoMov = 0.0;
        if (MovBancarioSel.getMtoMovBancario() < 0) {
            mtoMov = MovBancarioSel.getMtoMovBancario() * -1;
        } else {
            mtoMov = MovBancarioSel.getMtoMovBancario();
        }
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        this.setMtoMovBancario(val.convertirDoubleAString(mtoMov));
        tipoOper = MovBancarioSel.getTipoOperMovBancario();
        mtoPrev = MovBancarioSel.getMtoMovBancario() * -1;

        this.setDescMovBancario(MovBancarioSel.getDescMovBancario());
        this.setTipoOperMovBancario(MovBancarioSel.getTipoOperMovBancario());

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setNombEntidadElim(MovBancarioSel.getRefMovBancario().toString());

        //-----------------------------------------------------------------
        //                     por si se va a eliminar
        //-----------------------------------------------------------------


        String idMovBancario = MovBancarioSel.getId().toString();
        //buscar objeto con posibilidad a eliminarse, en el arreglo de MovBancarios, listaOrd
        MovBancario MovBancario = null;

        for (Iterator<MovBancario> it = listMovBancario.iterator();
                it.hasNext();) {
            MovBancario = (MovBancario) it.next();
            if (MovBancario.getId().toString().equals(idMovBancario)) {
                //Almacena la fila del MovBancario seleccionado ,el cual se buscara como "MovBancario" en el map #{MovBancario} 
                ctx.getExternalContext().getSessionMap().put("MovBancario", MovBancario);
                break;
            }
        }

//Almacena la fila del concepto seleccionado ,el cual se buscara como "Concepto" en el map #{Concepto} 

        ctx.getExternalContext().getSessionMap().put("MovBancario", MovBancario);

        beanUsuario.agregar = true;
        beanUsuario.modificar = false;
        beanUsuario.eliminar = false;

        beanUsuario.setDeshabilitar(true);
    }

    public void registrarMovBancario() throws ValidatorException, ParseException {

        Integer idBcoInt = Integer.parseInt(idBcoMovBancario);
        idMovBanInt = 0;

        String vacio = new String();
        String mensaje = validarPanel();

        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }
        MovBancario MovBancarioNuevo = new MovBancario();

        MovBancarioNuevo.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCtaMovBancario);
        MovBancarioNuevo.setIdCtaMovBancario(idCtaInt);
        MovBancarioNuevo.setTipoOperMovBancario(tipoOperMovBancario);

        //-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------


        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecMovBancario);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//----------------------------------------------------------

        MovBancarioNuevo.setFecMovBancario(cal);
        MovBancarioNuevo.setRefMovBancario(refMovBancario);
        Double mto = Double.valueOf(mtoMovBancario).doubleValue();
        if ("Pago de Cheque".equals(tipoOperMovBancario) | "Ret/Transferencia".equals(tipoOperMovBancario) | "Nota de Débito".equals(tipoOperMovBancario)) {
            MovBancarioNuevo.setMtoMovBancario(mto * -1);
        } else {
            MovBancarioNuevo.setMtoMovBancario(mto);
        }

        MovBancarioNuevo.setDescMovBancario(descMovBancario);

        MovBancarioNuevo.setIdPago(0);

        Cuenta cuenta;


        try {
            cuenta = (Cuenta) encontrarEntidadPorId(idCtaInt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        /* actualización del Saldo de la Cuenta. Esta operación disminuye el monto ya que es un pago*/
        /*_______________________________________________________________________________________*/
        String tipoOper1 = "Pago de Cheque";
        String tipoOper2 = "Dep/Transferencia";
        String tipoOper3 = "Nota de Débito";
        String tipoOper4 = "Ret/Transferencia";
        String tipoOper5 = "Nota de Crédito";

        if (tipoOper1.equals(tipoOperMovBancario)) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        if (tipoOper2.equals(tipoOperMovBancario)) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() + mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        if (tipoOper3.equals(tipoOperMovBancario)) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        if (tipoOper4.equals(tipoOperMovBancario)) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        if (tipoOper5.equals(tipoOperMovBancario)) {

            Double nuevoSaldo = cuenta.getSaldoActCuenta() + mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        MovBancarioNuevo.setCuenta(cuenta);


        try {
            almacenarOModificarEntidad(MovBancarioNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
        DaoQuery<Cuenta> dataAcces = new DaoQuery<Cuenta>();


        try {

            dataAcces.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El MovBancario " + MovBancarioNuevo.getDescMovBancario() + " ha sido registrado satisfactoriamente!"));


    }

    public void modificarMovBancario() throws ValidatorException, ParseException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        MovBancario MovBancarioSel = new MovBancario();

        Integer idBcoInt = Integer.parseInt(idBcoMovBancario);
        MovBancarioSel.setIdBcoMovBancario(idBcoInt);
        Integer idCtaInt = Integer.parseInt(idCtaMovBancario);
        MovBancarioSel.setIdCtaMovBancario(idCtaInt);
        MovBancarioSel.setTipoOperMovBancario(tipoOperMovBancario);

        //-----------------------------------------------------------
//  Convertir el string de fecha a Calendar 
//-----------------------------------------------------------

        DateFormat formatter;
        Date date;
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        date = (Date) formatter.parse(fecMovBancario);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

//----------------------------------------------------------

        MovBancarioSel.setFecMovBancario(cal);
        MovBancarioSel.setRefMovBancario(refMovBancario);

        Double mto = Double.valueOf(mtoMovBancario).doubleValue();
        if ("Pago de Cheque".equals(tipoOperMovBancario) | "Ret/Transferencia".equals(tipoOperMovBancario) | "Nota de Débito".equals(tipoOperMovBancario)) {
            MovBancarioSel.setMtoMovBancario(mto * -1);
        } else {
            MovBancarioSel.setMtoMovBancario(mto);
        }

        MovBancarioSel.setDescMovBancario(descMovBancario);
        MovBancarioSel.setIdPago(0);

        Cuenta cuenta = null;
        try {
            cuenta = (Cuenta) encontrarEntidadPorId(idCtaInt);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }


        MovBancarioSel.setCuenta(cuenta);

        MovBancarioDT beanMovBancario = (MovBancarioDT) JSFUtil.getManagedObject("MovBancarioDT");
        MovBancarioSel.id = beanMovBancario.id;

        MovBancarioSel.setCuenta(cuenta);

        try {
            almacenarOModificarEntidad(MovBancarioSel);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Falla al modificar"));
            return;
        }

        /*inicializarPanel();*/

        /* Restablece el Saldo de la Cuenta. Aplica la modificación indicada al mismo /
        /*_______________________________________________________________________________________*/


        String tipoOper1 = "Pago de Cheque";
        String tipoOper2 = "Dep/Transferencia";
        String tipoOper3 = "Nota de Débito";
        String tipoOper4 = "Ret/Transferencia";
        String tipoOper5 = "Nota de Crédito";



        if (tipoOper1.equals(tipoOperMovBancario)) {

            Double saldoAct = cuenta.getSaldoActCuenta();
            saldoAct = saldoAct + mtoPrev;
            Double nuevoSaldo = saldoAct - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper2.equals(tipoOperMovBancario)) {

            Double saldoAct = cuenta.getSaldoActCuenta();
            saldoAct = saldoAct + mtoPrev;
            Double nuevoSaldo = saldoAct + mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper3.equals(tipoOperMovBancario)) {

            Double saldoAct = cuenta.getSaldoActCuenta();
            saldoAct = saldoAct + mtoPrev;
            Double nuevoSaldo = saldoAct - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper4.equals(tipoOperMovBancario)) {

            Double saldoAct = cuenta.getSaldoActCuenta();
            saldoAct = saldoAct + mtoPrev;
            Double nuevoSaldo = saldoAct - mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }
        if (tipoOper5.equals(tipoOperMovBancario)) {

            Double saldoAct = cuenta.getSaldoActCuenta();
            saldoAct = saldoAct + mtoPrev;
            Double nuevoSaldo = saldoAct + mto;
            cuenta.setSaldoActCuenta(nuevoSaldo);
        }

        DaoCrudAux<Cuenta> daoCrud = new DaoCrudAux<Cuenta>();
        try {

            daoCrud.almacenarOModificarEntidad(cuenta);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }

        inicializarPanel();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El MovBancario " + MovBancarioSel.getDescMovBancario() + " ha sido modificado satisfactoriamente!"));

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        beanUsuario.setDeshabilitar(false);


    }

    public void setIdBcoMovBancario(String idBcoMovBancario) {
        this.idBcoMovBancario = idBcoMovBancario;
    }

    public String getIdBcoMovBancario() {
        return idBcoMovBancario;
    }

    public void setIdCtaMovBancario(String idCtaMovBancario) {
        this.idCtaMovBancario = idCtaMovBancario;
    }

    public String getIdCtaMovBancario() {
        return idCtaMovBancario;
    }

    public void setTipoOperMovBancario(String tipoOperMovBancario) {
        this.tipoOperMovBancario = tipoOperMovBancario;
    }

    public String getTipoOperMovBancario() {
        return tipoOperMovBancario;
    }

    public void setFecMovBancario(String fecMovBancario) {
        this.fecMovBancario = fecMovBancario;
    }

    public String getFecMovBancario() {
        return fecMovBancario;
    }

    public void setRefMovBancario(String refMovBancario) {
        this.refMovBancario = refMovBancario;
    }

    public String getRefMovBancario() {
        return refMovBancario;
    }

    public void setMtoMovBancario(String mtoMovBancario) {
        this.mtoMovBancario = mtoMovBancario;
    }

    public String getMtoMovBancario() {
        return mtoMovBancario;
    }

    public void setDescMovBancario(String descMovBancario) {
        this.descMovBancario = descMovBancario;
    }

    public String getDescMovBancario() {
        return descMovBancario;
    }

    public void setSaldoMovBancario(String saldoMovBancario) {
        this.saldoMovBancario = saldoMovBancario;
    }

    public String getSaldoMovBancario() {
        return saldoMovBancario;
    }

    public void setFilaSeleccionada(MovBancarioDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public MovBancarioDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
