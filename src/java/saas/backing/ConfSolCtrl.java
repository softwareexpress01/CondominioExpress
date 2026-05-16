package saas.backing;

import datosbasicos.backing.InmuebleCtrl;
import datosbasicos.modelos.Condominio;
import informes.backing.CobranzaCtrl;
import saas.modelos.PagoMensual;
import saas.modelos.Suscripcion;
import saas.modelos.SuscripcionDT;
import saas.modelos.Compañia;
import saas.modelos.Plan;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.hibernate.HibernateException;
import util.*;

public class ConfSolCtrl extends DaoCrud<Suscripcion, PagoMensual, Integer> implements Serializable {

    private SuscripcionDT[] solSeleccionadas;
    private Collection<Suscripcion> listaSusConfirmadas = new ArrayList<Suscripcion>();
    Collection<Suscripcion> lista = new ArrayList<Suscripcion>();
    private Condominio cond;

    public ConfSolCtrl() {
    }

    public String salir() {

        return "datosbasicos";

    }

    public Collection getSolicitudesPorConfirmar() {

        Collection<Suscripcion> listaOrd = new ArrayList<Suscripcion>();

        String nombQuery = "buscarSuscripcionesPend";
        DaoQuery<Collection> dataAcces = new DaoQuery<Collection>();
        try {
            lista = dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            System.out.println("buscando suscripciones pendientes...");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return lista;
        }

        if (lista.isEmpty()) {

            return lista;

        }

        listaOrd = ordenarLista(lista);
        return listaOrd;
    }

    public Collection ordenarLista(Collection lista) {

        // Es necesario copiar la lista tipo Collecction a tipo List para aplicar el sort,
        // despues se vuelve a pasar a una coleccion.
        List<Suscripcion> listaDesOrdenada = new ArrayList<Suscripcion>();

        for (Iterator<Suscripcion> it = lista.iterator();
                it.hasNext();) {
            Suscripcion suscrip = (Suscripcion) it.next();
            listaDesOrdenada.add(suscrip);
        }

        Collections.sort(listaDesOrdenada, new ComparadorSuscripcion());//devuelve la lista ordenada en listaDesOrdenada

        Collection<SuscripcionDT> coleccionOrdenada = new ArrayList<SuscripcionDT>();

        for (int i = 0; i < listaDesOrdenada.size(); i++) {
            Suscripcion fdt = listaDesOrdenada.get(i);

            SuscripcionDT sDT = new SuscripcionDT();
            sDT.setId(fdt.getId());
            sDT.setNombSuscriptor(fdt.getNombSuscriptor());
            sDT.setApelSuscriptor(fdt.getApelSuscriptor());
            sDT.setNumCedSuscriptor(fdt.getNumCedSuscriptor());
            sDT.setEmailSuscriptor(fdt.getEmailSuscriptor());
            sDT.setTelSuscriptor(fdt.getTelSuscriptor());
            sDT.setCelSuscriptor(fdt.getCelSuscriptor());
            sDT.setFechaMovSol(fdt.getFechaMovSol());
            sDT.setFechaMovSolString(calendarAString(fdt.getFechaMovSol()));
            sDT.setEdoSolSuscripcion(fdt.getEdoSolSuscripcion());
            sDT.setBcoSolSuscripcion(fdt.getBcoSolSuscripcion());
            sDT.setNumCtaSolSuscripcion(fdt.getNumCtaSolSuscripcion());
            sDT.setRefSolSuscripcion(fdt.getRefSolSuscripcion());
            sDT.setMtoSolSuscripcion(fdt.getMtoSolSuscripcion());
            sDT.setFecPagoSolSuscripcion(fdt.getFecPagoSolSuscripcion());
            sDT.setPlan(fdt.getPlan());

            coleccionOrdenada.add(sDT);
        }
        return coleccionOrdenada;
    }

    public void registrarConfirmacion() throws ValidatorException, ParseException, MessagingException {

        //--------------------------------------------------------
        // Verifica si seleccionó todas o alguna de las solicitudes
        //--------------------------------------------------------
        if (solSeleccionadas == null | solSeleccionadas.length == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("No ha seleccionado ninguna Solicitud, por favor, intente de nuevo"));
            return;
        }
        listaSusConfirmadas.clear();
        Float saldoSus;
        Float totalOper;
        for (int i = 0; i < solSeleccionadas.length; i++) {

            SuscripcionDT suscripcionSel = (SuscripcionDT) solSeleccionadas[i];
            Suscripcion suscripcionConf = new Suscripcion();

            suscripcionConf.setEdoSolSuscripcion("CONF");
            suscripcionConf.setNumSuscripcion(suscripcionSel.getId().toString());
            suscripcionConf.setEdoSuscripcion("ACT");

            Calendar fecCal = Calendar.getInstance(TimeZone.getDefault());
            suscripcionConf.setFecSuscripcion(fecCal);

            //Generar y asignar Contraseña:
            Random r = new Random();
            Long seed = fecCal.getTimeInMillis();
            Integer ent = Long.valueOf(seed).intValue();
            if (ent < 0) {
                ent = ent * -1;
            }
            Integer cont = r.nextInt(ent);
            suscripcionConf.setContSuscripcion(cont.toString());

            //Asignar Dia de corte:
            Integer diaCorte = fecCal.get(Calendar.DAY_OF_MONTH);
            if (diaCorte > 29) {
                diaCorte = 28;
            }
            suscripcionConf.setDiaCorteSuscripcion(diaCorte.toString());

            // Hay que verificar si el monto del depósito es suficiente para cubrir la suscripción mas el mes adelantado
            // 7 de marzo 2016 - F. Rojas.
            Plan planSel = buscarPlan(suscripcionSel.getId());
            totalOper = planSel.getMtoPlan() + planSel.getMtoSusPlan();

            if (suscripcionSel.getMtoSolSuscripcion() < totalOper) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Confirmación interrumpida. Monto insuficiente de alguna(s) de las solicitudes."));
                return;
            }
            saldoSus = suscripcionSel.getMtoSolSuscripcion() - totalOper;
            suscripcionConf.setSaldoSuscripcion(saldoSus);

            // Resto de los atributos de la clase:
            suscripcionConf.setNombSuscriptor(suscripcionSel.getNombSuscriptor());
            suscripcionConf.setApelSuscriptor(suscripcionSel.getApelSuscriptor());
            suscripcionConf.setNumCedSuscriptor(suscripcionSel.getNumCedSuscriptor());
            suscripcionConf.setEmailSuscriptor(suscripcionSel.getEmailSuscriptor());
            suscripcionConf.setTelSuscriptor(suscripcionSel.getTelSuscriptor());
            suscripcionConf.setCelSuscriptor(suscripcionSel.getCelSuscriptor());
            suscripcionConf.setFechaMovSol(suscripcionSel.getFechaMovSol());
            suscripcionConf.setBcoSolSuscripcion(suscripcionSel.getBcoSolSuscripcion());
            suscripcionConf.setNumCtaSolSuscripcion(suscripcionSel.getNumCtaSolSuscripcion());
            suscripcionConf.setRefSolSuscripcion(suscripcionSel.getRefSolSuscripcion());
            suscripcionConf.setMtoSolSuscripcion(suscripcionSel.getMtoSolSuscripcion());
            suscripcionConf.setFecPagoSolSuscripcion(suscripcionSel.getFecPagoSolSuscripcion());
            suscripcionConf.setPlan(suscripcionSel.getPlan());
            suscripcionConf.setId(suscripcionSel.getId());
            Compañia com = buscarCompañia();
            suscripcionConf.setCompañia(com);
            //DaoQuery<Suscripcion> dataAcces = new DaoQuery<Suscripcion>();

            try {
                almacenarOModificarEntidad(suscripcionConf);
            } catch (HibernateException he) {
                System.out.println("almacenando o modificando la suscripcion...");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones (SUSCRIPCION).por favor, intente mas tarde"));
                return;
            }
            listaSusConfirmadas.add(suscripcionConf);
            registrarPagoSuscripcion(suscripcionConf);
        }
        CobranzaCtrl cobranza = new CobranzaCtrl();
        cond = cobranza.buscarCondominio();
        enviarEmailConfirmacion(listaSusConfirmadas);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("La(s) Suscripcion(es) seleccionada(s) ha(n) sido Confirmadas(s)"));

    }

    public Plan buscarPlan(Integer id) {

        Plan plan;
        String nombQuery = "buscarPlanPorIdSuscripcion";

        DaoQuery<Plan> dataAcces = new DaoQuery<Plan>();
        try {
            plan = dataAcces.buscarEntidadPorId(id, nombQuery);
        } catch (HibernateException he) {
            System.out.println("buscando el plan...");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones... por favor, intente mas tarde"));
            return (null);
        }

        return plan;
    }

    public String calendarAString(Calendar fecha) {

        Calendar StringFec = fecha;
        String perDia = "";
        String perMes = "";
        int dMov = fecha.get(Calendar.DAY_OF_MONTH);
        int mMov = fecha.get(Calendar.MONTH) + 1;
        int aMov = fecha.get(Calendar.YEAR);

        if (dMov < 10) {
            perDia = "0" + dMov;
        } else {
            perDia = String.valueOf(dMov);
        }

        if (mMov < 10) {
            perMes = "0" + mMov;
        } else {
            perMes = String.valueOf(mMov);
        }

        String stringFec = perDia + "-" + perMes + "-" + aMov;

        return stringFec;

    }

    public Compañia buscarCompañia() {

        Compañia compañia;
        String nombQuery = "buscarCompañia";
        DaoQuery<Compañia> dataAcces = new DaoQuery<Compañia>();
        try {
            compañia = dataAcces.buscarEntidad(nombQuery);
        } catch (HibernateException he) {
            System.out.println("buscando compañia...");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return (null);
        }

        return compañia;
    }

    public void enviarEmailConfirmacion(Collection<Suscripcion> listaSusConfirmadas) throws MessagingException {

        boolean auth = true;
        boolean debug = true;
        int port = 587;
        String host = "condominioexpressve.com";
        Protocol protocol = Protocol.SMTP;
        debug = true;
        final String emailUsuario = "administrador@condominioexpressve.com";
        final String contraseñaUsuario = "tango#16";
        final String emailSoftwareExp = "softwareexpress01@gmail.com";
        
       /*String host = "us2.smtp.mailhostbox.com";
        final String emailUsuario = "informes@condominioexpressve.com";
        final String contraseñaUsuario = "X^Z%Bmhs7";*/

        String asunto = "Software Express, confirmación de pago de Suscripción";

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
        Suscripcion sus;
        String para = "";
        //   BodyPart texto = new MimeBodyPart();

        String msj = "";
        
        for (Iterator<Suscripcion> it = listaSusConfirmadas.iterator();
                it.hasNext();) {
            sus = (Suscripcion) it.next();
            msj = "Sr(a):" + sus.getNombSuscriptor() + "," + "hemos confirmado su pago por concepto de suscripción a nuestros Servicios de software para la gestión de Condominios. "
                    + "Su número de Suscripción es: " + sus.getNumSuscripcion().toString() + " " + "y su contraseña es: " + sus.getContSuscripcion().toString()
                    + " . Felicitaciones ! le damos la mas cordial bienvenida. "
                    + "Ya está inscrita su Suscripción. Requerimos que se registre ahora como usuario administrador del sistema."
                    + "Para ello deberá ir a la opción de <entrar> al sistema, a través de nuestra página web, y luego usar el link de <Registrarse> en el cual podrá crear usted mismo, el código de usuario y contraseña de su preferencia. "
                    + "Esto le permitirá hacer Login y obtener los beneficios y ventajas en el  uso de Condominio Express, el sistema mas novedoso y completo del mercado nacional.";

            //      texto.setText(msj);
            para = sus.getEmailSuscriptor();

            MimeMessage message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(emailUsuario));
                InternetAddress[] address = {new InternetAddress(para)};
                message.setRecipients(Message.RecipientType.TO, address);
                message.setRecipients(Message.RecipientType.BCC, emailSoftwareExp);
                message.setSubject(asunto);
                message.setSentDate(new Date());
                message.setText(msj);

                Transport.send(message);
            } catch (MessagingException ex) {
                System.out.println("enviando correo...");
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
                return;
            }

            msj = "";
        }
    }

    public void registrarPagoSuscripcion(Suscripcion sus) throws ParseException {

        PagoMensual pagoMensualNuevo = new PagoMensual();

        pagoMensualNuevo.setBcoPagoMensual(sus.getBcoSolSuscripcion());
        pagoMensualNuevo.setCtaPagoMensual(sus.getNumCtaSolSuscripcion());

        pagoMensualNuevo.setFecPagoMensual(sus.getFecPagoSolSuscripcion());
        pagoMensualNuevo.setRefPagoMensual(sus.getRefSolSuscripcion());

        Float mto = Float.valueOf(sus.getMtoSolSuscripcion());
        pagoMensualNuevo.setMtoPagoMensual(sus.getMtoSolSuscripcion());
        //-----------------------------------------------------------
        //                  Toma la fecha del servidor 
        //-----------------------------------------------------------
        Calendar fecha = new GregorianCalendar();
        pagoMensualNuevo.setFecMovPagoMensual(fecha);
        pagoMensualNuevo.setEdoPagoMensual("CANC");

        pagoMensualNuevo.setSuscripcion(sus);

        DaoQuery<PagoMensual> dataAcces2 = new DaoQuery<PagoMensual>();

        try {
            dataAcces2.almacenarEntidad(pagoMensualNuevo);
        } catch (HibernateException he) {
            System.out.println("almacenando pago mensual nuevo...");
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones.por favor, intente mas tarde"));
        }

    }

    public SuscripcionDT[] getSolSeleccionadas() {
        return solSeleccionadas;
    }

    public void setSolSeleccionadas(SuscripcionDT[] solSelecciondas) {
        this.solSeleccionadas = solSelecciondas;
    }
}
