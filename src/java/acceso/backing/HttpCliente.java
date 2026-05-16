/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package acceso.backing;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author jesus
 */
public class HttpCliente {

    public void HttpCliente() {
    }

    public static void main(String arg[]) throws IOException {

        conexion();
    }

    public static void conexion() throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://condominioexpress.wordpress.com/2018/08/28/consulta-express/");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    System.out.println(EntityUtils.toString(entity));
                } else {

                    HeaderIterator it = response.headerIterator();
                    while (it.hasNext()) {
                        System.out.println(it.next());
                    }
                    System.out.println(EntityUtils.toString(entity));
                }
            }
        } finally {
            response.close();
        }
    }
}
