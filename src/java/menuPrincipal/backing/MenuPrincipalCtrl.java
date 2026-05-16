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
public class MenuPrincipalCtrl {

    private BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
    //
    //  ----- Comentar las siguientes instruccion para ambiente de desarrollo ------
    //   private String http = "http://";
    //   private String ip = beanUsuario.getIpHost();
    //   private String puerto = ":8080/Condominio4/faces/";
    //  private String hostCond = http.concat(ip).concat(puerto);
    // ------------------------------------------------------------------------------------
    // Dejar esta que usa el dominio para la ip dinámica, antes se colocaba la ip pública
    //-------------------------------------------------------------------------------------
     private final String hostCond= beanUsuario.getHostCond();

    public MenuPrincipalCtrl() {
    }

    public void inicializarBotones() {

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
        beanUsuario.mostrar2 = false;
        beanUsuario.mostrar1 = true;
    }

    /**
     * Datos Basicos
     */
    public void datosCondominio() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CondominioUI.xhtml"));
        System.out.println("reporte = " + beanUsuario.reporte);
    }

    public void datosPropiedades() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PropiedadUI.xhtml"));
    }

    public void datosCategorias() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CategoriaUI.xhtml"));
    }

    public void datosConceptos() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("ConceptoUI.xhtml"));

    }

    public void datosInmuebles() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("InmuebleUI.xhtml"));

    }

    public void datosProveedores() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("ProveedorUI.xhtml"));
    }

    public void datosBancos() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("BancoUI.xhtml"));
    }

    public void datosCuentas() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CuentaUI.xhtml"));
    }

    public void datosChequeras() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("ChequeraUI.xhtml"));
    }

    public String datosConsultarSuscripcion() {

        inicializarBotones();
        return "datosconsultarsuscripcion";

    }

    public void datosFondos() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("FondoUI.xhtml"));

    }

    public String salir() {

        beanUsuario.setOperaciones(false);
        inicializarBotones();
        return "bienvenida";

    }

    /**
     * Movimientos del mes
     */
    public void gastosComunesOrd() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("GastoComunOrdUI.xhtml"));
    }

    public void gastosComunesExt() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("GastoComunExtUI.xhtml"));
    }

    public void gastosNoComunes() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("GastoNoComunUI.xhtml"));
    }

    public void fondosGenerales() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("TranFondoUI.xhtml"));
    }

    public void cuotasEspeciales() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CuotaEspUI.xhtml"));
    }

    public void movBancarios() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("MovBancarioUI.xhtml"));
    }

    public void ajustesCond() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("AjustesCondUI.xhtml"));
    }

    public void pagosPorInmueble() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PagoCxCUI.xhtml"));
    }

    public void revertirPagosCxC() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("RevertirPagoCxCUI.xhtml"));
    }

    public void notaCredDebCxC() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("NotaCredDebCxCUI.xhtml"));
    }

    public void pagoAdelantado() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PagoAdelantadoUI.xhtml"));
    }

    public void deudaPendienteCxC() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("DeudaPendCxCUI.xhtml"));
    }

    public void deudaPendienteCxP() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("DeudaPendCxPUI.xhtml"));
    }

    public void notaCredDebCxP() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("NotaCredDebCxPUI.xhtml"));
    }

    public void pagosAbonosCxP() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PagoCxPUI.xhtml"));
    }

    public void revertirPagoCxP() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("RevertirPagoCxPUI.xhtml"));
    }

    public void pagosFonRes() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PagoFonResUI.xhtml"));
    }

    public void revertirPagosFonRes() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("RevertirPagoFonResUI.xhtml"));
    }

    /**
     * Informes
     */
    public void movimientosCta() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("MovBanRepUI.xhtml"));
    }

    public String distribucionDelGasto() {

        inicializarBotones();
        //     beanUsuario.setReporte(hostCond.concat("DistGastoUI.xhtml"));
        return "distribucion";
    }

    public void recibosCxC() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("ReciboPagoUI.xhtml"));
    }

    public void recibosPagoAdel() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("ReciboPagoAdelUI.xhtml"));
    }

    public void edoCtaInmueble() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("EdoCuentaInmUI.xhtml"));
    }

    public void generarReportePagosInm() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CobranzaUI.xhtml"));
    }

    public void generarReporteMovDelMes() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("MovDelMesUI.xhtml"));
    }

    public void generarEdoCuentaProveedor() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("EdoCuentaProvUI.xhtml"));
    }

    public void generarReportePagosProveedor() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PagosProvUI.xhtml"));
    }

    public void generarAvisosDeCobro() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("AvisoDeCobroUI.xhtml"));
    }

    public void generarCorrespondencia() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CorrespondenciaUI.xhtml"));
    }

    /**
     * Configuracion
     */
    public void configurarLogo() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("LogoUI.xhtml"));
    }

    public void perfilesUsuario() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("PerfilUI.xhtml"));
    }
     public void registrarTasas() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("TasaUI.xhtml"));
    }

    /**
     * Administracion
     */
    public void cierrePreliminar() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CierrePreliminarUI.xhtml"));
    }

    public void cierreMensual() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("CierreMensualUI.xhtml"));
    }

    public void mostrarBlog() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("InvitacionBlogUI.xhtml"));
    }

    /**
     * Operaciones
     */
    public String pagosSuscripcion() {

        inicializarBotones();
        return "pagosSuscripcion";

    }

    public String pagosMensuales() {

        inicializarBotones();
        return "pagosMensuales";

    }

    public String revertirPagosSuscripciones() {

        inicializarBotones();
        return "revertirPagosSuscripciones";

    }

    public String revertirPagosMensuales() {

        inicializarBotones();
        return "revertirPagosMensuales";

    }

    public String confirmarSolicitudesSus() {

        inicializarBotones();
        return "confirmarSolSus";

    }

    public String revertirConfirmacionesSolSus() {

        inicializarBotones();
        return "revertirConf";

    }

    public String realizarCobros() {

        inicializarBotones();
        return "realizarCobros";

    }

    public String registrarPlanes() {

        inicializarBotones();
        return "registrarPlanes";

    }

    public String consultaExpress() {

        inicializarBotones();
        return "consultaExpress";

    }

    public String buscarSuscripcion() {

        inicializarBotones();
        return "buscarSuscripcion";

    }

    public String terminosYCondiciones() {

        inicializarBotones();
        return "terminosycondiciones";

    }
    public String migrarInmuebles() {

        inicializarBotones();
        return "migrarTodosLosInm";

    }
    public String PagosAutoCxP() {

        inicializarBotones();
        return "pagosAutoDeCxP";

    }

    public void miscelaneos() {

        inicializarBotones();
        beanUsuario.setReporte(hostCond.concat("MiscelaneosUI.xhtml"));

    }

    public String irASeleccionarCondominio() {


        beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.agregar = false;
        beanUsuario.modificar = true;
        beanUsuario.eliminar = true;
        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }
}
