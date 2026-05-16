/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import java.util.Collection;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.MovBancario;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoFacCxC;
import movimientos.modelos.TranFondo;

/**
 *
 * @author softw
 */
public interface GenericDaoPago {
    
    public void almacenarPagoCxC (Collection<PagoFacCxC> transPagoFac, Collection<FacGastoNoComun> transFacGNC, Collection<GastoNoComun> transGNC, Collection<DeudaPendCxC> transDeudaPend, Collection<Alicuota> transAlicuota,
                    Collection<FacCuotaEsp> transFacCE, Collection<NotaCredDebCxC> transNotaDeb,Parametro parametro, NotaCredDebCxC notaCreDebCxCNueva, MovBancario MovBancarioNuevo, Cuenta cuenta,
                    Collection<TranFondo> transTranFondo, Collection<Fondo> transFondos) ;
    
    public void almacenarPagoCxP ();
   
}
