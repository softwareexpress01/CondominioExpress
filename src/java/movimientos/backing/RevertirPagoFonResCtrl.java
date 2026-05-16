package movimientos.backing;

import datosbasicos.modelos.Banco;
import datosbasicos.modelos.Condominio;
import datosbasicos.modelos.Proveedor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import org.hibernate.HibernateException;
import javax.faces.context.FacesContext;
import movimientos.modelos.PagoFonRes;
import movimientos.modelos.RevertirPagoFonResDT;

public class RevertirPagoFonResCtrl extends DaoCrud<PagoFonRes, Proveedor, Integer> implements Serializable {

    public String idProveedor;
    private String desPagoFonRes;
    private String numDocPagoFonRes;
    private String mtoPagoFonRes;
    private String formaPagoFonRes;
    private String numRefPagoFonRes;
    private String fecDocPagoFonRes;
    private String bancoPagoFonRes;
    private Collection<PagoFonRes> listaPagosFonRes = new ArrayList<PagoFonRes>();
    private Collection<RevertirPagoFonResDT> listaPagosFonResDT = new ArrayList<RevertirPagoFonResDT>();
    private RevertirPagoFonResDT filaSeleccionada;

    public RevertirPagoFonResCtrl() {
    }

    public void inicializarPanel() {

        this.setDesPagoFonRes(null);
        this.setNumDocPagoFonRes(null);
        this.setMtoPagoFonRes(null);
        this.setFormaPagoFonRes(null);
        this.setNumRefPagoFonRes(null);
        this.setFecDocPagoFonRes(null);
        this.setBancoPagoFonRes(null);

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;

    }

    public String irASeleccionarCondominio() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.revertir = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {


        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;
        beanUsuario.revertir = true;

        return "informes";

    }

    public Collection getPagosFonRes() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        /*if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
        return listaPagosFonResDT;
        }*/

        listaPagosFonResDT.clear();
        listaPagosFonRes.clear();

        Integer idCond = beanUsuario.getIdCondActual();
        String nombQuery = "buscarPagosFonResPorIdCond";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosFonRes = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listaPagosFonRes);// para ordenar la lista 

        // se inicializa el objeto  para el DataTable...
        listaPagosFonResDT = inicializarPagosFonResDT(listaOrd);

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaPagosFonResDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoFonRes> listaDesOrdenada = new ArrayList<PagoFonRes>();

        for (Iterator<PagoFonRes> it = lista.iterator();
                it.hasNext();) {
            PagoFonRes pFonRes = (PagoFonRes) it.next();
            listaDesOrdenada.add(pFonRes);
        }

        Collections.sort(listaDesOrdenada, new ComparadorRevertirPagoFonRes());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoFonRes> coleccionOrdenada = new ArrayList<PagoFonRes>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoFonRes pago = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pago);
        }
        return coleccionOrdenada;
    }

    public Condominio buscarCondominio() throws ValidatorException {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Integer idCond = beanUsuario.getIdCondActual();

        Condominio condominio = null;//Para buscar el objeto condominio usa el DaoQuery
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return condominio;
    }

    private Collection<RevertirPagoFonResDT> inicializarPagosFonResDT(Collection lista) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");

        Calendar periodo = beanUsuario.getPeriodoActual();
        Calendar perPago;

        for (Iterator<PagoFonRes> it = lista.iterator();
                it.hasNext();) {

            PagoFonRes pagoFonRes = (PagoFonRes) it.next();
            if (pagoFonRes == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                RevertirPagoFonResDT pagoFonResDT = new RevertirPagoFonResDT();

                pagoFonResDT.setId(pagoFonRes.getId());
                pagoFonResDT.setDesPagoFonRes(pagoFonRes.getDesPagoFonRes());
                pagoFonResDT.setNumDocPagoFonRes(pagoFonRes.getNumDocPagoFonRes());
                
                pagoFonResDT.setMtoPagoFonRes(pagoFonRes.getMtoPagoFonRes());
                //----------------------------------------------------------------
                //Se da formato al monto para mostrarlo en el DT
                //----------------------------------------------------------------
                pagoFonResDT.setMtoPagoFonResString(val.convertirDoubleAString(pagoFonRes.getMtoPagoFonRes()));
                
                pagoFonResDT.setFormaPagoFonRes(pagoFonRes.getFormaPagoFonRes());
                pagoFonResDT.setFecDocPagoFonRes(pagoFonRes.getFecDocPagoFonRes());
                Integer idBco = pagoFonRes.getBancoPagoFonRes();
                String nombBanco = buscarNombreBanco(idBco);
                pagoFonResDT.setNombBancoPagoFonRes(nombBanco);

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = pagoFonRes.getFecMovPagoFonRes();
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

                pagoFonResDT.setFecMovPagoFonRes(stringFechaMov);

                Calendar fecMov = pagoFonRes.getFecMovPagoFonRes();

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos cargados despues del ultimo periodo cerrado
                //----------------------------------------------------------------------------------

                perPago = pagoFonRes.getPeriodoActual();
                if (perPago.equals(periodo) | perPago.after(periodo)) {
                    listaPagosFonResDT.add(pagoFonResDT);
                }


            }
        }
        return listaPagosFonResDT;
    }

    public String buscarNombreBanco(Integer idBanco) {

        Banco banco;
        String nombQuery = "buscarBancoPorId";
        DaoQuery<Banco> dataAcces = new DaoQuery<Banco>();
        try {
            banco = dataAcces.buscarEntidadPorId(idBanco, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        String nombreBco = banco.getNombBanco();
        return nombreBco;
    }

    public void editarPagoFonResAction() {

        RevertirPagoFonResDT pagoFonResSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("RevertirPagoFonResDT", pagoFonResSel);

        this.setDesPagoFonRes(pagoFonResSel.getDesPagoFonRes());
        this.setNumDocPagoFonRes(pagoFonResSel.getNumDocPagoFonRes());
        this.setMtoPagoFonRes(pagoFonResSel.getMtoPagoFonResString());
        this.setFormaPagoFonRes(pagoFonResSel.getFormaPagoFonRes());
        this.setNumRefPagoFonRes(pagoFonResSel.getNumDocPagoFonRes());
        this.setFecDocPagoFonRes(pagoFonResSel.getFecDocPagoFonRes());
        this.setBancoPagoFonRes(pagoFonResSel.getNombBancoPagoFonRes());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //             Guarda el nro del documento a revertir
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(pagoFonResSel.getNumDocPagoFonRes());
        beanUsuario.revertir = false;

    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setDesPagoFonRes(String desPagoFonRes) {
        this.desPagoFonRes = desPagoFonRes;
    }

    public String getDesPagoFonRes() {
        return desPagoFonRes;
    }

    public void setNumDocPagoFonRes(String numDocPagoFonRes) {
        this.numDocPagoFonRes = numDocPagoFonRes;
    }

    public String getNumDocPagoFonRes() {
        return numDocPagoFonRes;
    }

    public String getMtoPagoFonRes() {
        return mtoPagoFonRes;
    }

    public void setMtoPagoFonRes(String mtoPagoFonRes) {
        this.mtoPagoFonRes = mtoPagoFonRes;
    }

    public String getFormaPagoFonRes() {
        return formaPagoFonRes;
    }

    public void setFormaPagoFonRes(String formaPagoFonRes) {
        this.formaPagoFonRes = formaPagoFonRes;
    }

    public String getNumRefPagoFonRes() {
        return numRefPagoFonRes;
    }

    public void setNumRefPagoFonRes(String numRefPagoFonRes) {
        this.numRefPagoFonRes = numRefPagoFonRes;
    }

    public String getFecDocPagoFonRes() {
        return fecDocPagoFonRes;
    }

    public void setFecDocPagoFonRes(String fecDocPagoFonRes) {
        this.fecDocPagoFonRes = fecDocPagoFonRes;
    }

    public void setBancoPagoFonRes(String bancoPagoFonRes) {
        this.bancoPagoFonRes = bancoPagoFonRes;
    }

    public String getBancoPagoFonRes() {
        return bancoPagoFonRes;
    }

    // .....
    public void setFilaSeleccionada(RevertirPagoFonResDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public RevertirPagoFonResDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
