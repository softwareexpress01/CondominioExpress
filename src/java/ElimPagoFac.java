
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.PagoFacCxC;
import org.hibernate.HibernateException;
import util.DaoQuery;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author Jesus
 */
public class ElimPagoFac {

    public void ElimPagoFac() {
    }

    public static void main(String arg[]) {
        
        // Se buscan los pagos de facturas que apuntan a facturas de gastos no comunes (disc= GNC)

        Collection<PagoFacCxC> pagosFac = new ArrayList<PagoFacCxC>();
        PagoFacCxC pagoFac;
        FacGastoNoComun fgnc;

        String nombQuery = "buscarPagosFacDeGNC";

        DaoQuery<PagoFacCxC> dataAcces = new DaoQuery();
        try {
            pagosFac = (Collection<PagoFacCxC>) dataAcces.buscarListaEntidades(nombQuery);
        } catch (HibernateException he) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        }
        
        // Se buscan las facturas de gastos no comunes (disc= GNC) para ver si existen, si no existen se genera msg
       
        for (Iterator it = pagosFac.iterator();
                it.hasNext();) {
            pagoFac = (PagoFacCxC) it.next();

            String nombQuery1 = "buscarFacGastoNoComunPorId";
            fgnc=null; 

            DaoQuery<FacGastoNoComun> dataAcces1 = new DaoQuery<FacGastoNoComun>();
            try {
                fgnc = dataAcces1.buscarEntidadPorId(pagoFac.getIdFacCxC(), nombQuery1);
            } catch (HibernateException he) {
                FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Se ha detectado una falla temporal de comunicaciones...por favor, intente mas tarde"));
        
            }
            if(fgnc==null){
                System.out.println("La factura de  gasto no comun no existe, pagofac=" + ""+ pagoFac.getId() + "" + "id fgnc=" + pagoFac.getIdFacCxC()); 
            }
        }
    }
}
