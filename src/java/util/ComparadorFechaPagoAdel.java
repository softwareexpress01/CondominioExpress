/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import movimientos.modelos.PagoAdelantado;

/**
 *
 * @author softw
 */
public class ComparadorFechaPagoAdel implements Comparator {

    SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
    Date fecha01 = null;
    Date fecha02 = null;

    @Override
    public int compare(Object o1, Object o2) {

        PagoAdelantado c1 = (PagoAdelantado) o1;
        PagoAdelantado c2 = (PagoAdelantado) o2;

        int resultadoIdInm = c1.getInmueble().getId().compareTo(c2.getInmueble().getId());
        if (resultadoIdInm != 0) {
            return resultadoIdInm; // Si son diferentes, ordena por inmueble
        }
        try {
            fecha01 = formato.parse(c1.getFecPagoAdelantado());
            fecha02 = formato.parse(c2.getFecPagoAdelantado());
        } catch (ParseException ex) {
            Logger.getLogger(ComparadorFechaPagoAdel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fecha01.compareTo(fecha02);
    }

    
    @Override
        public boolean equals(Object o) {
        return this == o;
    }

    @Override
        public int hashCode() {
        int hash = 3;
        return hash;
    }
}
