package configuracion.backing;

import datosbasicos.modelos.Condominio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import util.*;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.primefaces.model.UploadedFile;

public class LogoCtrl extends DaoCrud<Condominio, Condominio, Integer> implements Serializable {

    private UploadedFile file;
    private boolean valido;

    // Extensiones permitidas
    private static final Set<String> EXT_PERMITIDAS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif"));

    // Tipos MIME permitidos
    private static final Set<String> MIME_PERMITIDOS = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif"
    ));

    public LogoCtrl() {
    }

    public String irASeleccionarCondominio() {

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");

        beanUsuario.setCondominioActual(null);
        beanUsuario.setIdCondActual(0);

        return "selcond";
    }

    public String salir() {

        return "configuracion";

    }

    public String validarPanel() {

        String mensaje = new String();

        if (file == null) {
            mensaje = "Por favor, seleccione el logo a registrar...";
            return mensaje;
        }

        esImagenValida(file);

        if (!valido) {
            mensaje = "Extensión de archivo no permitida (Solo JPG,JPEG, PNG o GIF) o " + ""
                    + "imagen invalida. Por favor, intente de nuevo";
            return mensaje;
        }

        if (file.getSize() > 1000000) {
            mensaje = "El tamaño del logo esta limitado a 1M bytes, por favor, intente de nuevo";
            return mensaje;
        }

        String nombre = file.getFileName();

        if (nombre.length() > 20) {
            mensaje = "El nombre del logo no puede contener mas de 20 caracteres, por favor, intente de nuevo";
            return mensaje;
        }
        return mensaje;
    }

    public boolean esImagenValida(UploadedFile file) {
        valido = true;
// Validar tipo MIME
        String mimeType = file.getContentType();
        if (!MIME_PERMITIDOS.contains(mimeType.toLowerCase())) {
            return valido = false;
        }

        // Validar extensión
        String nombre = file.getFileName();
        String extension = obtenerExtension(nombre);
        if (extension == null || !EXT_PERMITIDAS.contains(extension.toLowerCase())) {
            return valido = false;
        }
        return valido;
    }

    private String obtenerExtension(String nombre) {
        if (nombre != null && nombre.contains(".")) {
            return nombre.substring(nombre.lastIndexOf('.') + 1);
        }
        return null;
    }

    public void upLoad() throws SQLException, IOException {

        String vacio = new String();
        String mensaje = validarPanel();
        if (!mensaje.equals(vacio)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(mensaje));
            return;
        }

        //-----------------------------------------------------------
        // Del archivo a subir se obtiene un inputStream, del cual
        // se generara un arreglo de bytes.
        //-----------------------------------------------------------
        byte[] bFile = new byte[(int) file.getSize()];

        InputStream in;
        try {
            in = file.getInputstream();
            //convert file into array of bytes
            in.read(bFile);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones, por favor, intente mas tarde"));
            return;
        }

        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        String destino = beanUsuario.getRuta();  // Esta ruta se asigna al beanUsuario en SelCondCtrl
        Integer idCond = beanUsuario.getIdCondActual();

        //--------------------------------------------------------------------------------
        // En caso de que el condominio ya tenga un logo, se elimina para crear el nuevo
        //--------------------------------------------------------------------------------
        Condominio condominio = encontrarEntidadPorId(idCond);

        String archivo = condominio.getNombLogo();

        if (archivo != null) {
            if (!archivo.equals("logoTemp.jpg")) {
                System.out.println("dentro del if.....borrando");
                File f = new File(destino + archivo);
                f.delete();
            }
        }
        OutputStream out;

        String nombre = idCond + file.getFileName();

        try {
            out = new FileOutputStream(new File(destino + nombre));
        } catch (Exception eo) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones,por favor, intente mas tarde"));
            return;
        }
        out.write(bFile);

        in.close();
        out.flush();
        out.close();

        registrarLogo(nombre, condominio);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("El logo del condominio ha sido registrado satisfactoriamente!"));
        return;

    }

    public void registrarLogo(String nombLogo, Condominio condominio) {

        condominio.setNombLogo(nombLogo);

        try {
            almacenarOModificarEntidad(condominio);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
            return;
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
