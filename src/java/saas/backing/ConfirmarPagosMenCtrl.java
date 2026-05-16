/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package saas.backing;

/**
 *
 * @author jesus
 */
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.hibernate.HibernateException;
import saas.modelos.PagoMensual;
import saas.modelos.PagoMensualDT;
import saas.modelos.Plan;
import saas.modelos.Suscripcion;
import util.*;

public class ConfirmarPagosMenCtrl extends DaoCrud<PagoMensual, Suscripcion, Integer> implements Serializable {

    private Collection<PagoMensual> listaPagosMensuales = new ArrayList<PagoMensual>();
    private Collection<PagoMensualDT> listaPagosMensualesDT = new ArrayList<PagoMensualDT>();
    private Collection<PagoMensual> listaPagosConfirmados = new ArrayList<PagoMensual>();
    private Collection<Suscripcion> listaSuscripciones = new ArrayList<Suscripcion>();
    private PagoMensualDT[] pagosSeleccionados;
    private Validator val = (Validator) JSFUtil.getManagedObject("Validator");

    public ConfirmarPagosMenCtrl() {
    }

    public String salir() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.ejecutarQuery = true;
        beanUsuario.ejecutarQueryDT = true;

        return "salir";

    }

    public String revertirPagoMensual() {


        return "revertirPagosMensuales";
    }

    public Collection getPagosMensualesPorSuscripcion() {

        listaPagosMensuales.clear();
        String nombQuery = "buscarPagosMensuales";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            listaPagosMensuales = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        listaPagosMensualesDT.clear();
        listaPagosMensualesDT = inicializarPagosMensualesDT(listaPagosMensuales);
        Collection listaOrd = ordenarLista(listaPagosMensualesDT);

        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecctio a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.

        List<PagoMensualDT> listaDesOrdenada = new ArrayList<PagoMensualDT>();

        for (Iterator<PagoMensualDT> it = lista.iterator();
                it.hasNext();) {
            PagoMensualDT pm = (PagoMensualDT) it.next();
            listaDesOrdenada.add(pm);
        }
        Collections.sort(listaDesOrdenada, new ComparadorPagoMensualFecha());

        Collection<PagoMensualDT> coleccionOrdenada = new ArrayList<PagoMensualDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            PagoMensualDT pmo = listaDesOrdenada.get(i);
            coleccionOrdenada.add(pmo);
        }
        return coleccionOrdenada;
    }

    private Collection<PagoMensualDT> inicializarPagosMensualesDT(Collection listaPagosMensuales) {

        Collection listaPagos = new ArrayList();
        val = (Validator) JSFUtil.getManagedObject("Validator");
        Suscripcion sus;

        for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                it.hasNext();) {

            PagoMensual pm = (PagoMensual) it.next();

            PagoMensualDT pagoMensualDT = new PagoMensualDT();

            pagoMensualDT.setId(pm.getId());
            pagoMensualDT.setBcoPagoMensual(pm.getBcoPagoMensual());
            pagoMensualDT.setCtaPagoMensual(pm.getCtaPagoMensual());

            //----------------------------------------------------------------
            //Se da formato a la fecha del pago para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaPagoMensual = pm.getFecPagoMensual();
            String dia = "";
            String mes = "";
            int diaPM = fechaPagoMensual.get(Calendar.DAY_OF_MONTH);
            int mesPM = fechaPagoMensual.get(Calendar.MONTH) + 1;
            int añoPM = fechaPagoMensual.get(Calendar.YEAR);

            if (diaPM < 10) {
                dia = "0" + diaPM;
            } else {
                dia = String.valueOf(diaPM);
            }

            if (mesPM < 10) {
                mes = "0" + mesPM;
            } else {
                mes = String.valueOf(mesPM);
            }

            String stringFechaDp = dia + "-" + mes + "-" + añoPM;


            pagoMensualDT.setFecPagoMensual(stringFechaDp);
            pagoMensualDT.setRefPagoMensual(pm.getRefPagoMensual());

            //----------------------------------------------------------------
            //Se da formato al monto y al saldo para mostrarlos en el DT
            //----------------------------------------------------------------
            pagoMensualDT.setMtoPagoMensual(val.convertirFloatAString(pm.getMtoPagoMensual()));

            //----------------------------------------------------------------
            //Se da formato a la fecha del mov para mostrarla en el data table
            //----------------------------------------------------------------
            Calendar fechaMov = pm.getFecMovPagoMensual();
            String diaM = "";
            String mesM = "";
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

            pagoMensualDT.setFecMovPagoMensual(stringFechaMov);
            pagoMensualDT.setEdoPagoMensual(pm.getEdoPagoMensual());

            sus = pm.getSuscripcion();

            pagoMensualDT.setNumSuscriptor(sus.getId().toString());
            pagoMensualDT.setNombSuscriptor(sus.getNombSuscriptor());
            pagoMensualDT.setApelSuscriptor(sus.getApelSuscriptor());

            listaPagos.add(pagoMensualDT);
        }
        return listaPagos;
    }

    public Plan buscarPlan(Integer id) {

        Plan plan;
        String nombQuery = "buscarPlanPorIdSuscripcion";

        DaoQuery<Plan> dataAcces = new DaoQuery<Plan>();
        try {
            plan = dataAcces.buscarEntidadPorId(id, nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones... por favor, intente mas tarde"));
            return (null);
        }

        return plan;
    }

    public void confirmarPagosMensuales() throws ValidatorException, MessagingException {


        //--------------------------------------------------------
        // Verifica si selecciono el o los pagos a procesar
        //--------------------------------------------------------

        if (pagosSeleccionados == null | pagosSeleccionados.length == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Por favor, seleccione los pagos a procesar..."));
            return;
        }

        listaSuscripciones.clear();
        listaPagosConfirmados.clear();

        for (int i = 0; i < pagosSeleccionados.length; i++) {
            PagoMensualDT pagoMesSel = (PagoMensualDT) pagosSeleccionados[i];

            String idPagoMesSel = pagoMesSel.getId().toString();
            //buscar objeto a modificar en el arreglo de pagos mensuales, listaPagosMensuales
            PagoMensual pagoMensual = null;
            for (Iterator<PagoMensual> it = listaPagosMensuales.iterator();
                    it.hasNext();) {
                pagoMensual = (PagoMensual) it.next();
                if (pagoMensual.getId().toString().equals(idPagoMesSel)) {
                    break;
                }
            }
            //----------------------------------------------------------------------
            // Se actualiza el estado del pago mensual y el saldo de la suscripcion 
            //----------------------------------------------------------------------
            Suscripcion suscripcion = pagoMensual.getSuscripcion();

            pagoMensual.setEdoPagoMensual("CANC");
            pagoMensual.setEdoAntSuscripcion(suscripcion.getEdoSuscripcion()); //Por si se va a revertir
            listaPagosConfirmados.add(pagoMensual);

            Float saldoSuscripcion = 0.0f;
            Float saldoSusc = 0.0f;

            Suscripcion susc;
            Plan plan = buscarPlan(suscripcion.getId());

            if (!listaSuscripciones.contains(suscripcion)) {
                saldoSuscripcion = suscripcion.getSaldoSuscripcion() + pagoMensual.getMtoPagoMensual();
                suscripcion.setSaldoSuscripcion(saldoSuscripcion);
                if (suscripcion.getEdoSuscripcion().equals("INA") & suscripcion.getSaldoSuscripcion() >= plan.getMtoPlan()) {

                    //Las dos lineas siguientes FR para que reste el monto del plan a la suscripción por el monto pagado
                    saldoSuscripcion = suscripcion.getSaldoSuscripcion() - plan.getMtoPlan();
                    suscripcion.setSaldoSuscripcion(saldoSuscripcion);
                    //MOdificado el 3-11-2015
                    suscripcion.setEdoSuscripcion("ACT");
                }
                listaSuscripciones.add(suscripcion);
            } else {

                //---------------------------------------------------------------------------------
                //En caso de que la suscripcion ya este en la lista, se actuliza su saldo y edo
                //---------------------------------------------------------------------------------
                for (Iterator<Suscripcion> it = listaSuscripciones.iterator();
                        it.hasNext();) {
                    susc = (Suscripcion) it.next();
                    if (suscripcion.getId().toString().equals(susc.getId().toString())) {
                        saldoSusc = susc.getSaldoSuscripcion() + pagoMensual.getMtoPagoMensual();
                        susc.setSaldoSuscripcion(saldoSusc);
                        if (susc.getEdoSuscripcion().equals("INA") & susc.getSaldoSuscripcion() >= plan.getMtoPlan()) {
                            saldoSusc = susc.getSaldoSuscripcion() - plan.getMtoPlan();
                            susc.setSaldoSuscripcion(saldoSusc);
                            susc.setEdoSuscripcion("ACT");
                        }
                        break;
                    }
                }
            }

        }
        actualizarEdoPagosMensuales(listaPagosConfirmados);
        actualizarSaldoSuscripciones(listaSuscripciones);

        enviarEmailConfirmacion(listaPagosConfirmados);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Los pagos seleccionados fueron confirmados satisfactoriamente!..."));
        return;
    }

    public void actualizarEdoPagosMensuales(Collection listaPagosMensuales) throws ValidatorException {
        //----------------------------------------------------------------------
        // Se crean las alicuotas en la bd enviando la lista al dao
        //----------------------------------------------------------------------
        DaoQuery<Collection> dao = new DaoQuery<Collection>();

        try {
            dao.almacenarListaEntidades(listaPagosMensuales);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void actualizarSaldoSuscripciones(Collection listaSuscripciones) throws ValidatorException {
        //----------------------------------------------------------------------
        // Se actualizan las suscripciones en la bd enviando la lista al dao
        //----------------------------------------------------------------------
        DaoQuery<Collection> dao = new DaoQuery<Collection>();

        try {
            dao.almacenarListaEntidades(listaSuscripciones);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public void enviarEmailConfirmacion(Collection<PagoMensual> listaPagosConfirmados) throws MessagingException {


        boolean auth = true;
        boolean debug = true;
        int port = 587;
        String host = "condominioexpressve.com";
        //   Protocol prhost = "condominioexpressve.com"otocol = Protocol.SMTP;
        debug = true;
        final String emailUsuario ="administrador@condominioexpressve.com";
        final String contraseñaUsuario = "tango#16";
        
       /* String host = "us2.smtp.mailhostbox.com";
        final String emailUsuario = "informes@condominioexpressve.com";
        final String contraseñaUsuario = "X^Z%Bmhs7";*/

        String asunto = "Software Express, confirmación de pago mensual";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", true);//Protocol.SMTP

        Authenticator authenticator = null;
        if (auth) {
            props.put("mail.smtp.auth", true);
            authenticator = new Authenticator() {

                private PasswordAuthentication pa = new PasswordAuthentication(emailUsuario, contraseñaUsuario);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }
        Session session = Session.getInstance(props, authenticator);
        session.setDebug(debug);
        // Construir texto del correo  para cada pago confirmado y se envia a cada uno
        PagoMensual pagoMensual;
        Suscripcion sus;
        String para = "";
        //   BodyPart texto = new MimeBodyPart();

        String msj = "";

        for (Iterator<PagoMensual> it = listaPagosConfirmados.iterator();
                it.hasNext();) {
            pagoMensual = (PagoMensual) it.next();
            sus = pagoMensual.getSuscripcion();

            msj = "Sr(a):" + sus.getNombSuscriptor() + "," + "hemos recibido de su parte un pago por un monto de Bs:"
                    + val.convertirFloatAString(pagoMensual.getMtoPagoMensual());

            para = sus.getEmailSuscriptor();

            MimeMessage message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(emailUsuario));
                InternetAddress[] address = {new InternetAddress(para)};
                message.setRecipients(Message.RecipientType.TO, address);
                message.setRecipients(Message.RecipientType.BCC, emailUsuario);
                message.setSubject(asunto);
                message.setSentDate(new Date());
                message.setText(msj);

                Transport.send(message);
            } catch (MessagingException ex) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }

            msj = "";
        }
    }

    public PagoMensualDT[] getPagosSeleccionados() {
        return pagosSeleccionados;
    }

    public void setPagosSeleccionados(PagoMensualDT[] pagosSeleccionados) {
        this.pagosSeleccionados = pagosSeleccionados;
    }
}
