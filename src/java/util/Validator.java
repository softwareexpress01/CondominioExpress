/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Autor: Frank Rojas H. */
package util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator implements Serializable {

    public void validator() {
    }

    public boolean esAlfa(String campo) {

        String alfa = "ABCDEFGHIJKLMNOPQRSTUVWYZ abcdefghijklmnopqrstuvwyz,";
        int start = 0;
        Boolean carValido = true;
        char car;
        int longCad = campo.length() - 1;

        do {

            car = campo.charAt(start);
            int i = alfa.indexOf(car);
            if (i < 0) {
                carValido = false;
            }

        } while ((start++ < longCad) && (carValido));

        if (carValido) {
            return true;
        } else {
            return false;
        }
    }

    public boolean esCedRif(String cedRif) {

        boolean siEs;

        siEs = esAlfaNum(cedRif);
        if (!siEs) {
            return false;
        }
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("([JjVvEeGg]{1}[0-9]{6,8})");
        mat = pat.matcher(cedRif);

        if (mat.find()) {

            return true;
        } else {
            return false;
        }
    }

    public boolean esAlfaNum(String campo) {

        String alfa = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz1234567890-";
        int start = 0;
        Boolean carValido = true;
        char car;
        int longCad = campo.length() - 1;

        if (longCad <= 0) {
            return true;
        }
        do {

            car = campo.charAt(start);
            int i = alfa.indexOf(car);
            if (i < 0) {
                carValido = false;
            }

        } while ((start++ < longCad) && (carValido));

        if (carValido) {
            return true;
        } else {
            return false;
        }
    }

    public boolean esAlfaNumPlus(String campo) {

        String caracteres = "°|!$%=?¡´¨*}[]<>";

        for (Integer i = 0; i < campo.length(); i++) {
            if (caracteres.indexOf(campo.charAt(i), 0) != -1) {
                return false;
            }
        }
        return true;
    }

//    public boolean esEmail(String correo) {
    //       Pattern pat = null;
    //      Matcher mat = null;
    //      pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([0-9a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
    //      mat = pat.matcher(correo);
    //      if (mat.find()) {
    //           return true;
    //       } else {
    //          return false;
    //      }
    //   }
    public boolean esEmail(String correo) {

        String email = correo;

        if (email.indexOf("@") < 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean esEntero(String numIn) {

        try {
            Integer entero = Integer.parseInt(numIn.trim());
            if (entero <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean esFecha(String fechax) {

        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            formatoFecha.setLenient(false);
            formatoFecha.parse(fechax);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Valida si el periodo tiene formato valido con el formato MM-aaaa.
     *
     * @return true si cumple el formato, false en caso contrario.
     *
     */
    public boolean esFormatoPeriodo(String periodo) {

        String fechax = "01-" + periodo;
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            formatoFecha.setLenient(false);
            formatoFecha.parse(fechax);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public Calendar convertirFecha(String fecha) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date = sdf.parse(fecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Valida si el periodo esta dentro del rango permitido. Debe ser igual al
     * periodo actual o igual al periodo siguiente
     *
     * @return true si cumple , false en caso contrario.
     *
     */
    public boolean esRangoValido(String periodo) throws ParseException {

        String stringPeriodo = "01-" + periodo;
        Calendar perValidar;
        String stringPerSiguiente;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(stringPeriodo);
            perValidar = Calendar.getInstance();
            perValidar.setTime(date);
        } catch (ParseException e) {
            return false;
        }
        BeanUsuario beanUsuario = (BeanUsuario) JSFUtil.getManagedObject("BeanUsuario");
        Calendar perActual = beanUsuario.getPeriodoActual();

        int mesPerActual = perActual.get(Calendar.MONTH) + 1;
        int añoPerActual = perActual.get(Calendar.YEAR);

        int añoPerSiguiente = añoPerActual;
        int mesPerSiguiente = mesPerActual + 1;

        if (mesPerSiguiente > 12) {
            mesPerSiguiente = 1;
            añoPerSiguiente = añoPerSiguiente + 1;
        }
        if (mesPerSiguiente <= 9) {
            stringPerSiguiente = "01-" + "0" + mesPerSiguiente + "-" + añoPerSiguiente;
        } else {
            stringPerSiguiente = "01-" + mesPerSiguiente + "-" + añoPerSiguiente;
        }

        Calendar perSiguiente = convertirFecha(stringPerSiguiente);


        if (perValidar.equals(perActual)
                | (perValidar.equals(perSiguiente))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean esMinSeisCar(String numIn) {
        if (numIn.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

    public boolean esMinCuatroCar(String numIn) {

        if (numIn.length() < 4) {
            return false;
        } else {
            return true;
        }
    }

    public boolean esMonto(String numIn) {
        Float monto;

        try {
            monto = Float.valueOf(numIn);
            if (monto > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean esMontoCero(String numIn) {
        Float monto;

        try {
            monto = Float.valueOf(numIn);
            if (monto >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean esNombApell(String nombApell) {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([a-zA-Z]+\\s*)");
        mat = pat.matcher(nombApell);
        if (mat.find()) {

            return true;
        } else {
            return false;
        }
    }

    public boolean esNumCta(String numCta) {
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^([0-9]{4}-[0-9]{4}-[0-9]{2}-[0-9]{10})");
        mat = pat.matcher(numCta);
        if (mat.find()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean sonDatosEnBlanco(String datos) {
        String datosEntrada;
        datosEntrada = datos;
        boolean siSon = false;
        int vacio = datosEntrada.length();
        if (vacio == 0) {
            siSon = true;
        }
        if (siSon) {

            return true;
        } else {
            return false;
        }
    }

    public boolean esDecimal(String numDecimal) {

        try {
            Double decimal = Double.valueOf(numDecimal);
            if (decimal > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean esLong(String numIn) {
        try {
            Long.parseLong(numIn);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public String convertirDoubleAString(Double cantidad) {

        Formatter fmt = new Formatter();
        String cantString = fmt.format(Locale.US, "%.2f", cantidad).toString();
        fmt.close();

        return cantString;
    }

    public String convertirFloatAString(Float cantidad) {

        Formatter fmt = new Formatter();
        String cantString = fmt.format(Locale.US, "%.2f", cantidad).toString();
        fmt.close();

        return cantString;
    }
}
