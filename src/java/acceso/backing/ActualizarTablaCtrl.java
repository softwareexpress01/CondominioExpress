/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import acceso.modelos.CodigoAcceso;
import datosbasicos.modelos.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.NotaCredDebCxC;
import org.hibernate.HibernateException;
import util.DaoCrud;
import util.DaoQuery;

/**
 *
 * @author jesus
 */
public class ActualizarTablaCtrl extends DaoCrud<Inmueble, Propiedad, Integer> implements Serializable {

    private Collection<NotaCredDebCxC> listaNCDCxC = new ArrayList<NotaCredDebCxC>();
    private Collection<Inmueble> listaInm = new ArrayList<Inmueble>();
    private Collection<DeudaPendCxC> listaDeudaPend = new ArrayList<DeudaPendCxC>();
    private Collection<Condominio> listaCondominios = new ArrayList();
    private String codInm = null;
    private String nombProp = null;
    private String telProp = null;
    private String emailProp = null;
    private String alic;
    private Integer idCond = 322;
    private String edificio;
    private String numero;
    private String codigo;
    private String blanco = "";
    private String telefono = "";
    private String direccion;
    private String piso;
    private String apto;
    private String area = "90";
    private Propiedad prop;
    private Integer idCon = 20905;
    private Concepto concepto;
    private String saldo;
    private Inmueble inm;
    private String fecha; //fecha de la deuda pendiente
    private Collection idprop = new ArrayList();
    private ArrayList dataInm = new ArrayList();
    private ArrayList deudaInm = new ArrayList();
    private Integer idPropiedad;
    private String destFile;
    private String sector;
    private String data;
    private String lista;
    private String idCondominio;
    private String fecCreacion;
    private String periodoActual;
    private Condominio condominio;
    private int i = 0;

    public ActualizarTablaCtrl() {

        //idprop.add(761);
        //  idprop.add(767);
        // idprop.add(768);
        // idprop.add(769);
        //   idprop.add(770);
        idprop.add(771);

        // dataInm.add("Inmuebles AsoAlto.xls");
        //  dataInm.add("Inmuebles El Cigarral.xls");
        //  dataInm.add("Inmuebles Vista al Sol.xls");
        //  dataInm.add("Inmuebles Las Iguanas.xls");
        //   dataInm.add("Inmuebles Laguna Mar Alfa.xls");
        // dataInm.add("Inmuebles Laguna Mar Delta.xls");
        //   -------------------------------------------
        //  deudaInm.add("Deuda AsoAlto.xls");
        //   deudaInm.add("Deuda El Cigarral.xls");
        //   deudaInm.add("Deuda Vista del Sol.xls");
        //  deudaInm.add("Deuda Las Iguanas.xls");
        //  deudaInm.add("Deuda Laguna Mar Alfa.xls");
        deudaInm.add("Deuda Laguna Mar Delta.xls");
    }

    public void actualizarPeriodoNotaCredDeb() throws ParseException {

        getNotasCredDebCxCPorInmueble();

        inicializarNotaCredDeb();

        DaoQuery<NotaCredDebCxC> dao = new DaoQuery<NotaCredDebCxC>();

        try {
            dao.almacenarListaEntidades(listaNCDCxC);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }

    }

    public void getNotasCredDebCxCPorInmueble() {

        listaNCDCxC.clear();

        String nombQuery = "buscarNotasCredDebCxC";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaNCDCxC = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    private void inicializarNotaCredDeb() throws ParseException {

        for (Iterator<NotaCredDebCxC> it = listaNCDCxC.iterator();
                it.hasNext();) {

            NotaCredDebCxC ncd = (NotaCredDebCxC) it.next();
            String fechaNota = ncd.getFecNotaCredDebCxC();

            String dia = "01";
            String mes = "";
            String año = "";

            Integer primerGuion = fechaNota.indexOf("-");
            Integer segundoGuion = fechaNota.lastIndexOf("-");

            if (primerGuion.equals(2)) {
                if (segundoGuion.equals(5)) {
                    mes = fechaNota.substring(3, 5);
                } else {
                    mes = "0" + fechaNota.charAt(3);
                }
            } else {
                if (segundoGuion.equals(4)) {
                    mes = fechaNota.substring(2, 4);
                } else {
                    mes = "0" + fechaNota.charAt(2);
                }
            }

            if (segundoGuion.equals(5)) {
                año = fechaNota.substring(6);
            } else {
                if (segundoGuion.equals(4)) {
                    año = fechaNota.substring(5);
                } else {
                    if (segundoGuion.equals(3)) {
                        año = fechaNota.substring(4);
                    }
                }
            }

            String fecha = dia + "-" + mes + "-" + año;
            System.out.println("la fecha es ----------------------" + fecha);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(fecha);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.MILLISECOND, 0);
            ncd.setPerNotaCredDebCxC(cal);

        }
    }

    public void cargaDeInmuebles() throws ParseException, IOException, BiffException {

        for (Iterator it = idprop.iterator();
                it.hasNext();) {
            idPropiedad = (Integer) it.next();
            prop = buscarPropiedadPorId(idPropiedad);
            lista = (String) dataInm.get(i);
            destFile = "C:/Users/softw/Downloads/".concat(lista);

            //  try {
            Workbook wb = Workbook.getWorkbook(new File(destFile));
            System.out.println(wb.getNumberOfSheets());

            for (int sheetNo = 0; sheetNo < wb.getNumberOfSheets(); sheetNo++) {
                Sheet sheet = wb.getSheet(sheetNo);
                int columns = sheet.getColumns();
                int rows = sheet.getRows();

                System.out.println("Sheet Name\t" + wb.getSheet(sheetNo).getName());

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        data = sheet.getCell(col, row).getContents().toString();
                        System.out.print(data);
                        if (col == 0) {
                            codInm = data;
                        } else if (col == 1) {
                            nombProp = data;
                        } else if (col == 2) {
                            alic = data;
                        } else if (col == 3) {
                            telProp = data;
                        } else if (col == 4) {
                            emailProp = data;
                        }
                    }
                    System.out.println("\n");
                    System.out.println("voy a crear el inmueble");
                    crearInmueble(codInm, nombProp, alic, telProp, emailProp, area);
                    System.out.println("regresé de crear el inmueble");
                }
                almacenarInmuebles();
            }
            i = i + 1;
        }
        //    } catch (Exception ioe) {
        //         System.out.println("Falla al leer archivo");
        //    }

    }

    private void crearInmueble(String codInm, String nombProp, String alic, String telProp, String emailProp, String area) {

        Inmueble inmueble = new Inmueble();
        //     Double area = new Double(123.0);
        //   inmueble.setCodInmueble(convertirCodInm(codInm));
        inmueble.setCodInmueble(codInm);

        inmueble.setNombPropInmueble(nombProp);

        inmueble.setTelInmueble(convertirTelefono(telProp));
        //inmueble.setTelInmueble(telProp);
        inmueble.setEmailPropInmueble(emailProp);
        inmueble.setAlicInmueble(Double.parseDouble(alic));
        inmueble.setCedRifPropInmueble("V9999999");
        inmueble.setAreaInmueble(Double.parseDouble(area));
        inmueble.setTipoInmueble("Apartamento");
        inmueble.setDirInmueble(armarDireccion(codInm));
        inmueble.setPropiedad(prop);
        // -------------- generar y registrar el codigo de acceso y establecer vinculo con el inmueble

        Integer idcodigoAcceso;
        idcodigoAcceso = asignarCodigoAcceso();
        if (idcodigoAcceso == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(Codigo Acceso)...por favor, intente mas tarde"));
            return;
        }
        CodigoAcceso codAccesoObj;
        codAccesoObj = buscarCodigoAcceso(idcodigoAcceso);
        inmueble.setCodigoAcceso(codAccesoObj);

        listaInm.add(inmueble);

    }

    private String convertirCodInm(String codInm) {

        edificio = codInm.substring(0, 1);
        numero = codInm.substring(2, 5);
        codigo = edificio.concat(numero);
        return codigo;
    }

    private String convertirTelefono(String telProp) {

        if (telProp.equals(blanco)) {
            telefono = "9999-9999999";
            System.out.println(telefono);
            return telefono;
        }

        //      codigo = telProp.substring(0, 4);
        //      telefono = codigo.concat("-").concat(telProp.substring(4));
        return telProp;

    }

    //   private String armarDireccion(String codInm) {
    //       edificio = codInm.substring(0, 1);
    //      numero = codInm.substring(2);
    //       piso = codInm.substring(2, 4);
    //      apto = edificio.concat(numero);
    //       direccion = "Edif." + " " + edificio + " " + "piso" + " " + piso + " " + "apto." + " " + apto;
//        return direccion;
    //   }
    private String armarDireccion(String codInm) {

        // direccion = "Asoalto" + ", " + " " + codInm;
        //  direccion = armarDireccionEC(codInm);
        //  direccion = armarDireccionVAS(codInm);
        //  direccion = armarDireccionLI(codInm);
        direccion = armarDireccionLM(codInm);
        //---------------------------------------------------------
        // ***************** AsoAlto  ********************
        //  edificio = codInm.substring(0, 1);
        //   piso = codInm.substring(2, 4);
        //   apto = codInm.substring(4);
        //   direccion = "Edf. Zulia" + ", " + "Torre" + " " + edificio + " " + "piso" + " " + piso + " " + "apto." + " " + apto;
        return direccion;

    }

    private String armarDireccionEC(String codInm) {

        if (codInm.substring(0, 1).equals("L")) {
            sector = "Local";
            apto = codInm.substring(2);
            direccion = "El Cigarral" + ", " + " " + sector + " " + apto;
            return direccion;
        }
        if (codInm.substring(0, 1).equals("S")) {
            sector = "Sotano";
            direccion = "El Cigarral" + ", " + " " + sector;
            return direccion;
        }

        if (codInm.substring(0, 1).equals("P")) {
            sector = "PH";
            direccion = "El Cigarral" + ", " + " " + sector;
            return direccion;
        }
        sector = codInm.substring(0, 1);
        apto = codInm.substring(2);
        direccion = "El Cigarral" + ", " + "piso" + " " + sector + " " + "apto." + " " + apto;
        return direccion;
    }

    private String armarDireccionVAS(String codInm) {

        if (codInm.substring(0, 2).equals("PB")) {
            sector = "PB";
            apto = codInm.substring(3);
            direccion = "El Cigarral" + ", " + " " + sector + "-" + apto;
            return direccion;
        }
        if (codInm.substring(0, 2).equals("PH")) {
            sector = "PH";
            direccion = "El Cigarral" + ", " + " " + sector;
            return direccion;
        }
        if (codInm.substring(0, 2).equals("DP")) {
            sector = "DP";
            apto = codInm.substring(3);
            direccion = "El Cigarral" + ", " + " " + sector + "-" + apto;
            return direccion;
        }

        sector = codInm.substring(0, 1);
        apto = codInm.substring(1);
        direccion = "El Cigarral" + ", " + "piso" + " " + sector + " " + "apto." + " " + apto;
        return direccion;
    }

    private String armarDireccionLI(String codInm) {

        if (codInm.substring(0, 2).equals("PB")) {
            sector = "PB";
            apto = codInm.substring(3);
            direccion = "Las Iguanas" + ", " + " " + sector + "-" + apto;
            return direccion;
        }

        sector = codInm.substring(0, 1);
        apto = codInm.substring(2);
        direccion = "El Cigarral" + ", " + "piso" + " " + sector + " " + "apto." + " " + apto;
        return direccion;
    }

    private String armarDireccionLM(String codInm) {

        edificio = codInm.substring(0, 1);
        piso = codInm.substring(1, 3);
        apto = codInm.substring(3);
        direccion = "Edf. Laguna Mar" + ", " + "Torre" + " " + edificio + " " + "piso" + " " + piso + " " + "apto." + " " + apto;
        return direccion;
    }

    private void almacenarInmuebles() {

        DaoQuery<Inmueble> dao = new DaoQuery<Inmueble>();

        try {
            dao.almacenarListaEntidades(listaInm);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    private Propiedad buscarPropiedadPorId(Integer idProp) {

        String nombQuery = "buscarPropiedadPorId";

        DaoQuery<Propiedad> dataAcces = new DaoQuery<Propiedad>();
        try {
            prop = dataAcces.buscarEntidadPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }
        return prop;
    }

    public Integer asignarCodigoAcceso() {

        Boolean codExiste = true;
        Integer cont;
        Integer idAcceso = null;

        while (codExiste) {

            //Generar Codigo de acceso:
            Random r = new Random();
            Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
            Long seed = fecCal.getTimeInMillis();
            Integer ent = Long.valueOf(seed).intValue();
            if (ent < 0) {
                ent = ent * -1;
            }
            cont = r.nextInt(ent);
            idAcceso = cont;

            // Verificar si id ya existe en la tabla de codigos
            CodigoAcceso codAccesoObj = new CodigoAcceso();
            codAccesoObj = buscarCodigoAcceso(idAcceso);

            if (codAccesoObj == null) {
                // codigo de acceso no existe
                codExiste = false;
            }
        }
        // Almacenar en tabla

        CodigoAcceso codigoAccesoNuevo = new CodigoAcceso();

        codigoAccesoNuevo.setId(idAcceso);

        // Buscar Condominio
        Condominio condominio;
        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidad(idCond, nombQuery);

        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(Condominio)...por favor, intente mas tarde"));

            return null;
        }
        codigoAccesoNuevo.setCondominio(condominio);

        DaoQuery<CodigoAcceso> dataAcces1 = new DaoQuery<CodigoAcceso>();
        Integer idCodigoAccesoNuevo;
        try {
            idCodigoAccesoNuevo = dataAcces1.almacenarEntidad(codigoAccesoNuevo);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones.(Almacenar codAcc)..por favor, intente mas tarde"));

            return null;
        }

        return idCodigoAccesoNuevo;
    }

    public CodigoAcceso buscarCodigoAcceso(Integer idAcceso) {

        String nombQuery = "buscarCodigoAccesoPorId";

        DaoQuery<CodigoAcceso> dataAcces = new DaoQuery<CodigoAcceso>();
        CodigoAcceso codAccesoObj = new CodigoAcceso();

        try {
            codAccesoObj = dataAcces.buscarEntidad(idAcceso, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones(buscar codAcceso)...por favor, intente mas tarde"));

        }
        return codAccesoObj;
    }

    public void cargaDeudadPend() {

        concepto = buscarConceptoPorId(idCon);
        i = 0;
        for (Iterator it = idprop.iterator();
                it.hasNext();) {
            idPropiedad = (Integer) it.next();
            prop = buscarPropiedadPorId(idPropiedad);
            lista = (String) deudaInm.get(i);
            destFile = "C:/Users/softw/Downloads/".concat(lista);

            buscarInmuebles(idPropiedad);

            try {
                Workbook wb = Workbook.getWorkbook(new File(destFile));
                System.out.println(wb.getNumberOfSheets());

                for (int sheetNo = 0; sheetNo < wb.getNumberOfSheets(); sheetNo++) {
                    Sheet sheet = wb.getSheet(sheetNo);
                    int columns = sheet.getColumns();
                    int rows = sheet.getRows();

                    System.out.println("Sheet Name\t" + wb.getSheet(sheetNo).getName());

                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < columns; col++) {
                            data = sheet.getCell(col, row).getContents().toString();
                            System.out.print(data);
                            if (col == 0) {
                                codInm = data;
                            } else if (col == 1) {
                                fecha = data;
                            } else if (col == 2) {
                                saldo = data;
                            }
                        }
                        System.out.println("\n");
                        crearDeudaPend(codInm, fecha, saldo);

                    }
                    almacenarDeudaPend();
                }
            } catch (Exception ioe) {
                System.out.println("Falla al leer archivo");
            }
            i = i + 1;
        }
    }

    public Concepto buscarConceptoPorId(Integer idConc) {

        String nombQuery = "buscarConceptoPorId";
        DaoQuery<Concepto> dataAcces = new DaoQuery<Concepto>();
        try {
            concepto = dataAcces.buscarEntidadPorId(idConc, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return concepto;
    }

    private void crearDeudaPend(String codInm, String fecha, String saldo) throws ParseException {

        DeudaPendCxC dpend = new DeudaPendCxC();

        dpend.setFecDeudaPendCxC(convertirFecha(fecha));
        dpend.setEdoDeudaPendCxC("PEND");
        dpend.setFecMovDP(new GregorianCalendar());
        dpend.setIdConcepto(idCon);
        inm = buscarInmueblePorCodigo(codInm);
        dpend.setInmueble(inm);
        dpend.setMtoDeudaPendCxC(Double.parseDouble(saldo));
        dpend.setSaldoDeudaPendCxC(Double.parseDouble(saldo));
        dpend.setTipoDeudaPendCxC("ORD");

        listaDeudaPend.add(dpend);
    }

    private void buscarInmuebles(Integer idProp) {

        listaInm.clear();

        String nombQuery = "buscarInmueblesPorIdProp";
        DaoQuery<List> dataAcces = new DaoQuery<List>();
        try {
            listaInm = dataAcces.buscarListaEntidadesPorId(idProp, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }

    }

    private Inmueble buscarInmueblePorCodigo(String codInm) {

        Inmueble inmueble = null;

        for (Iterator<Inmueble> it = listaInm.iterator();
                it.hasNext();) {
            inmueble = (Inmueble) it.next();
            if (inmueble.getCodInmueble().equals(codInm)) {
                break;
            }
        }
        return inmueble;
    }

    private void almacenarDeudaPend() {

        DaoQuery<DeudaPendCxC> dao = new DaoQuery<DeudaPendCxC>();

        try {
            dao.almacenarListaEntidades(listaDeudaPend);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public void actualizarFecCreacionYPeriodo() {

        destFile = "D:/".concat("Condominios.xls");

        try {
            Workbook wb = Workbook.getWorkbook(new File(destFile));
            System.out.println(wb.getNumberOfSheets());

            for (int sheetNo = 0; sheetNo < wb.getNumberOfSheets(); sheetNo++) {
                Sheet sheet = wb.getSheet(sheetNo);
                int columns = sheet.getColumns();
                int rows = sheet.getRows();

                System.out.println("Sheet Name\t" + wb.getSheet(sheetNo).getName());

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        data = sheet.getCell(col, row).getContents().toString();
                        System.out.print(data);
                        if (col == 0) {
                            idCondominio = data;
                        } else if (col == 1) {
                            fecCreacion = data;
                        } else if (col == 2) {
                            periodoActual = data;
                        }
                    }
                    System.out.println("\n");
                    actualizarCondominio(idCondominio, periodoActual, fecCreacion);

                }
                almacenarCondominio();
            }
        } catch (Exception ioe) {
            System.out.println("Falla al leer archivo");
        }

    }

    private void actualizarCondominio(String idCondominio, String periodoActual, String fecCreacion) throws ParseException {

        buscarCondominio(Integer.parseInt(idCondominio));

        condominio.setPeriodoActual(convertirFecha(periodoActual));
        condominio.setFecCreacion(convertirFecha(fecCreacion));
        listaCondominios.add(condominio);
    }

    public void buscarCondominio(Integer idCond) {

        String nombQuery = "buscarCondominioPorId";
        DaoQuery<Condominio> dataAcces = new DaoQuery<Condominio>();
        try {
            condominio = dataAcces.buscarEntidadPorId(idCond, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    private void almacenarCondominio() {

        DaoQuery<Condominio> dao = new DaoQuery<Condominio>();

        try {
            dao.almacenarListaEntidades(listaCondominios);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
    }

}
