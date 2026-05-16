package configuracion.modelos;


import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
    

/**
 * Logo.java serves as the POJO for storing information about logos
 * 
 */
public class Logo implements Serializable {

    private Integer id;
    private Blob logo;
    private InputStream logoStream;

    public Logo() throws SQLException {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

   
    public Blob getLogo()  {
        return this.logo;
    }

  
    public void setLogo(Blob logo) {
        this.logo = logo;
    }

    public InputStream getLogoStream() throws SQLException {
        return this.logoStream;
    }

    public void setLogoStream(InputStream logoStream) throws SQLException {
        this.logoStream = logoStream;
    }
}
