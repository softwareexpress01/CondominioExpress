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
import javax.faces.model.SelectItem;
import movimientos.modelos.PagoCxP;
import movimientos.modelos.PagoCxPDT;

public class RevertirPagoCxPCtrl extends DaoCrud<PagoCxP, Proveedor, Integer> implements Serializable {

    public String idProveedor;
    private String desPagoCxP;
    private String numDocPagoCxP;
    private String mtoPagoCxP;
    private String formaPagoCxP;
    private String numRefPagoCxP;
    private String fecDocPagoCxP;
    private String bancoPagoCxP;
    private Collection<PagoCxP> listaPagosCxP = new ArrayList<PagoCxP>();
    private Collection<PagoCxPDT> listaPagosCxPDT = new ArrayList<PagoCxPDT>();
    private List listaProveedores = new ArrayList();
    private PagoCxPDT filaSeleccionada;

    public RevertirPagoCxPCtrl() {
    }

    public void inicializarPanel() {

        this.setDesPagoCxP(null);
        this.setNumDocPagoCxP(null);
        this.setMtoPagoCxP(null);
        this.setFormaPagoCxP(null);
        this.setNumRefPagoCxP(null);
        this.setFecDocPagoCxP(null);
        this.setBancoPagoCxP(null);

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

        return "movimientos";

    }

    public Collection getPagosCxPPorProveedor() {

        //--------------------------------------------------------------------------------------------
        // si no ha seleccionado o es la primera vez que muestra la pagina, retorna coleccion en blanco
        //--------------------------------------------------------------------------------------------

        if (idProveedor == null) {
            idProveedor = "0";
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        if (!beanUsuario.getEjecutarQueryDT()) {//controla la ejecucion del metodo, si va o no a la bd
            return listaPagosCxPDT;
        }

        listaPagosCxPDT.clear();
        listaPagosCxP.clear();
        // Obtiene las notas de credito/debito para el Proveedor dado (id), los muestra en el panel 2
        Integer idInm = Integer.valueOf(idProveedor).intValue();
        String nombQuery = "buscarPagosCxPPorIdProveedor";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosCxP = dataAcces.buscarListaEntidadesPorId(idInm, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        Collection listaOrd = ordenarLista(listaPagosCxP);// para ordenar la lista 

        // se inicializa el objeto  para el DataTable...
        listaPagosCxPDT = inicializarPagosCxPDT(listaOrd);

        beanUsuario.ejecutarQuery = false;//Impide la ejecucion repetida de los metodos get de datos basicos
        beanUsuario.ejecutarQueryDT = false;//Impide la ejecucion repetida de los metodos get del data table

        return listaPagosCxPDT;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoCxP> listaDesOrdenada = new ArrayList<PagoCxP>();

        for (Iterator<PagoCxP> it = lista.iterator();
                it.hasNext();) {
            PagoCxP pCxP = (PagoCxP) it.next();
            listaDesOrdenada.add(pCxP);
        }

        Collections.sort(listaDesOrdenada, new ComparadorRevertirPagoCxP());//devuelve la lista ordenada en listaDesOrdenada

        Collection<PagoCxP> coleccionOrdenada = new ArrayList<PagoCxP>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoCxP pago = listaDesOrdenada.get(i);
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

    private Collection<PagoCxPDT> inicializarPagosCxPDT(Collection lista) {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Validator val = (Validator) JSFUtil.getManagedObject("Validator");
        Calendar periodo = beanUsuario.getPeriodoActual();
        Calendar perPago;

        for (Iterator<PagoCxP> it = lista.iterator();
                it.hasNext();) {

            PagoCxP pagoCxP = (PagoCxP) it.next();
            if (pagoCxP == null) {// Si no hay notas devuelve la lista en blanco
                break;
            } else {
                PagoCxPDT pagoCxPDT = new PagoCxPDT();

                pagoCxPDT.setId(pagoCxP.getId());
                pagoCxPDT.setDesPagoCxP(pagoCxP.getDesPagoCxP());
                pagoCxPDT.setNumDocPagoCxP(pagoCxP.getNumDocPagoCxP());
                pagoCxPDT.setMtoPagoCxP(val.convertirDoubleAString(pagoCxP.getMtoPagoCxP()));
                pagoCxPDT.setFormaPagoCxP(pagoCxP.getFormaPagoCxP());
                pagoCxPDT.setFecDocPagoCxP(pagoCxP.getFecDocPagoCxP());
                Integer idBco = pagoCxP.getBancoPagoCxP();
                String nombBanco = buscarNombreBanco(idBco);
                pagoCxPDT.setBancoPagoCxP(nombBanco);

                //----------------------------------------------------------------
                //Se da formato a la fecha del mov para mostrarla en el data table
                //----------------------------------------------------------------
                Calendar fechaMov = pagoCxP.getFecMovPagoCxP();
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

                pagoCxPDT.setFecMovPagoCxP(stringFechaMov);

                //----------------------------------------------------------------------------------
                // Pasa a la lista solo los movimientos cargados despues del ultimo periodo cerrado
                //----------------------------------------------------------------------------------

                perPago = pagoCxP.getPeriodoActual();
                if (perPago.equals(periodo) | perPago.after(periodo)) {
                    listaPagosCxPDT.add(pagoCxPDT);
                }


            }
        }
        return listaPagosCxPDT;
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

    public List getProveedoresPorCondominio() {

        listaProveedores.clear();
        listaProveedores.add(new SelectItem(new Integer(0), "Seleccione"));

        BeanUsuario usuarioActual = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        //-------------------------------------------------------------------------------------
        // Genera la lista de Proveedors para la propiedad dada, la muestra en el selecOneMenu
        //-------------------------------------------------------------------------------------

        Integer idCond = usuarioActual.getIdCondActual();
        List<Proveedor> proveedores = new ArrayList<Proveedor>();
        String nombQuery = "buscarProveedoresPorIdCond";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            proveedores = dataAcces.buscarListaEntidadesPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        if (proveedores.isEmpty()) {// Si no hay Proveedors devuelve la lista en blanco
            listaProveedores.clear();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Aun no se ha sido registrado ningun Proveedor para el condominio actual!.Por favor, contacte con su administrador..."));
            return (null);
        }

        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor prov = (Proveedor) proveedores.get(i);
            listaProveedores.add(new SelectItem(prov.getId(), prov.getNombProveedor()));
        }

        beanUsuario.ejecutarQueryDT = true;

        return (List) listaProveedores;
    }

    public void editarPagoCxPAction() {

        PagoCxPDT pagoCxPSel = this.getFilaSeleccionada();
        FacesContext ctx = FacesContext.getCurrentInstance();
        //Almacena la fila del gasto seleccionado ,el cual se buscara como "GastoComunOrdDT" en el map #{GastoComunOrdDT} 
        ctx.getExternalContext().getSessionMap().put("PagoCxPDT", pagoCxPSel);

        this.setDesPagoCxP(pagoCxPSel.getDesPagoCxP());
        this.setNumDocPagoCxP(pagoCxPSel.getNumDocPagoCxP());
        this.setMtoPagoCxP(pagoCxPSel.getMtoPagoCxP());
        this.setFormaPagoCxP(pagoCxPSel.getFormaPagoCxP());
        this.setNumRefPagoCxP(pagoCxPSel.getNumRefPagoCxP());
        this.setFecDocPagoCxP(pagoCxPSel.getFecDocPagoCxP());
        this.setBancoPagoCxP(pagoCxPSel.getBancoPagoCxP().toString());
        //-----------------------------------------------------------
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        //-----------------------------------------------------------------
        //             Guarda el nro del documento a revertir
        //-----------------------------------------------------------------
        beanUsuario.setNombEntidadElim(pagoCxPSel.getNumDocPagoCxP());
        beanUsuario.revertir = false;

    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setDesPagoCxP(String desPagoCxP) {
        this.desPagoCxP = desPagoCxP;
    }

    public String getDesPagoCxP() {
        return desPagoCxP;
    }

    public void setNumDocPagoCxP(String numDocPagoCxP) {
        this.numDocPagoCxP = numDocPagoCxP;
    }

    public String getNumDocPagoCxP() {
        return numDocPagoCxP;
    }

    public String getMtoPagoCxP() {
        return mtoPagoCxP;
    }

    public void setMtoPagoCxP(String mtoPagoCxP) {
        this.mtoPagoCxP = mtoPagoCxP;
    }

    public String getFormaPagoCxP() {
        return formaPagoCxP;
    }

    public void setFormaPagoCxP(String formaPagoCxP) {
        this.formaPagoCxP = formaPagoCxP;
    }

    public String getNumRefPagoCxP() {
        return numRefPagoCxP;
    }

    public void setNumRefPagoCxP(String numRefPagoCxP) {
        this.numRefPagoCxP = numRefPagoCxP;
    }

    public String getFecDocPagoCxP() {
        return fecDocPagoCxP;
    }

    public void setFecDocPagoCxP(String fecDocPagoCxP) {
        this.fecDocPagoCxP = fecDocPagoCxP;
    }

    public void setBancoPagoCxP(String bancoPagoCxP) {
        this.bancoPagoCxP = bancoPagoCxP;
    }

    public String getBancoPagoCxP() {
        return bancoPagoCxP;
    }

    // .....
    public void setFilaSeleccionada(PagoCxPDT filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    public PagoCxPDT getFilaSeleccionada() {
        return filaSeleccionada;
    }
}
