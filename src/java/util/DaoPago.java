/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import configuracion.modelos.Parametro;
import datosbasicos.modelos.Cuenta;
import datosbasicos.modelos.Fondo;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import movimientos.modelos.Alicuota;
import movimientos.modelos.DeudaPendCxC;
import movimientos.modelos.FacCuotaEsp;
import movimientos.modelos.FacGastoNoComun;
import movimientos.modelos.GastoNoComun;
import movimientos.modelos.MovBancario;
import movimientos.modelos.NotaCredDebCxC;
import movimientos.modelos.PagoFacCxC;
import movimientos.modelos.TranFondo;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author softw
 */
public class DaoPago implements GenericDaoPago, Serializable {

    public DaoPago() {

    }

    @Override
    public void almacenarPagoCxC(Collection<PagoFacCxC> transPagoFac, Collection<FacGastoNoComun> transFacGNC, Collection<GastoNoComun> transGNC, Collection<DeudaPendCxC> transDeudaPend, Collection<Alicuota> transAlicuota,
            Collection<FacCuotaEsp> transFacCE, Collection<NotaCredDebCxC> transNotaDeb, Parametro parametro, NotaCredDebCxC notaCreDebCxCNueva, MovBancario MovBancarioNuevo, Cuenta cuenta,
            Collection<TranFondo> transTranFondo, Collection<Fondo> transFondos) throws HibernateException {

        PagoFacCxC pagoFac = null;
        FacGastoNoComun facGNC;
        GastoNoComun gnc;
        DeudaPendCxC dp;
        Alicuota alic;
        FacCuotaEsp fce;
        NotaCredDebCxC nd;
        TranFondo tf;
        Fondo fondo;

        Session sesion = null;
        Transaction tx = null;

        try {
            sesion = HibernateUtil.getSessionFactory().openSession();
            tx = sesion.beginTransaction();

            for (Iterator<PagoFacCxC> it = transPagoFac.iterator();
                    it.hasNext();) {
                pagoFac = (PagoFacCxC) it.next();
                sesion.saveOrUpdate(pagoFac);
            }

            for (Iterator<FacGastoNoComun> it = transFacGNC.iterator();
                    it.hasNext();) {
                facGNC = (FacGastoNoComun) it.next();
                sesion.saveOrUpdate(facGNC);
            }

            for (Iterator<GastoNoComun> it = transGNC.iterator();
                    it.hasNext();) {
                gnc = (GastoNoComun) it.next();
                sesion.saveOrUpdate(gnc);
            }

            for (Iterator<DeudaPendCxC> it = transDeudaPend.iterator();
                    it.hasNext();) {
                dp = (DeudaPendCxC) it.next();
                sesion.saveOrUpdate(dp);
            }

            for (Iterator<Alicuota> it = transAlicuota.iterator();
                    it.hasNext();) {
                alic = (Alicuota) it.next();
                sesion.saveOrUpdate(alic);
            }

            for (Iterator<FacCuotaEsp> it = transFacCE.iterator();
                    it.hasNext();) {
                fce = (FacCuotaEsp) it.next();
                sesion.saveOrUpdate(fce);
            }

            for (Iterator<NotaCredDebCxC> it = transNotaDeb.iterator();
                    it.hasNext();) {
                nd = (NotaCredDebCxC) it.next();
                sesion.saveOrUpdate(nd);
            }
            if (parametro != null) {
                sesion.saveOrUpdate(parametro);
                sesion.saveOrUpdate(notaCreDebCxCNueva);
            }
            sesion.saveOrUpdate(MovBancarioNuevo);
            sesion.saveOrUpdate(cuenta);
            
            for (Iterator<TranFondo> it = transTranFondo.iterator();
                    it.hasNext();) {
                tf = (TranFondo) it.next();
                sesion.saveOrUpdate(tf);
            }

            for (Iterator<Fondo> it = transFondos.iterator();
                    it.hasNext();) {
                fondo = (Fondo) it.next();
                sesion.saveOrUpdate(fondo);
            }

            tx.commit();
        } catch (HibernateException he) {
            System.err.println("Error al procesar la transaccion de pago," + he);
            try {
                tx.rollback();
            } catch (HibernateException e) {
                System.err.println("No se pudo hacer rollback de la transaccion," + he);
                throw e;
            }
            throw he;
        } finally {
            transPagoFac.clear();
            transFacGNC.clear();
            transGNC.clear();
            transDeudaPend.clear();
            transAlicuota.clear();
            transFacCE.clear();
            transNotaDeb.clear();
            transTranFondo.clear();
            transFondos.clear();
            sesion.clear();
            sesion.close();
        }
    }

    @Override
    public void almacenarPagoCxP() {

    }

}
