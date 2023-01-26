package curso.android.misrecetapps.model;

import java.util.concurrent.TimeUnit;

/**
 * @author Julián Quenard *
 * 01-09-2021
 */
public enum UnidadDeMedida {
    GRAMO("Gramo/s","g",TipoDeMedida.MASA.getNombre(),TipoDeMedida.MASA.getUnidadDeBase(),1),
    MILILITRO("Mililitro/s","ml",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),1),
    KILOGRAMO("Kilogramo/s","kg",TipoDeMedida.MASA.getNombre(),TipoDeMedida.MASA.getUnidadDeBase(),1000),
    LITRO("Litro/s","L",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),1000),
    UNIDAD("Unidad/es","un.",TipoDeMedida.UNIDAD.getNombre(),TipoDeMedida.UNIDAD.getUnidadDeBase(),1),
    MILIGRAMO("Miligramo/s","mg",TipoDeMedida.MASA.getNombre(),TipoDeMedida.MASA.getUnidadDeBase(),0.001),
    TAZA("Taza/s","tz",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),250),
    CUCHARADA_SOPERA("Cuacharada/s Sopera/s","cda",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),15),
    CUCHARADA_POSTRE("Cuacharada/s de Postre","cdta",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),5),
    LIBRA("Libra/s","lb",TipoDeMedida.MASA.getNombre(),TipoDeMedida.MASA.getUnidadDeBase(),453.6),
    ONZA("Onza/s","oz",TipoDeMedida.MASA.getNombre(),TipoDeMedida.MASA.getUnidadDeBase(),28.34),
    ONZA_LIQUIDA("Onza/s Líquida/s","fl.oz",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),29.5),
    KILOWATT_HORA("Kilowatt/s por hora","kw/h",TipoDeMedida.CONSUMO_ELECTRICO.getNombre(),TipoDeMedida.CONSUMO_ELECTRICO.getUnidadDeBase(), TimeUnit.MINUTES.convert(1, TimeUnit.HOURS)),
    KILOWATT_MINUTO("Kilowatt/s por minuto","kw/min",TipoDeMedida.CONSUMO_ELECTRICO.getNombre(),TipoDeMedida.CONSUMO_ELECTRICO.getUnidadDeBase(),1),
    WATT_HORA("Watt/s por hora","w/h",TipoDeMedida.CONSUMO_ELECTRICO.getNombre(),TipoDeMedida.CONSUMO_ELECTRICO.getUnidadDeBase(), TimeUnit.MINUTES.convert(1, TimeUnit.HOURS)/1000),
    WATT_MINUTO("Watt/s por minuto","w/min",TipoDeMedida.CONSUMO_ELECTRICO.getNombre(),TipoDeMedida.CONSUMO_ELECTRICO.getUnidadDeBase(),1/1000),
    METRO3_HORA("Metro/s cúbico/s por hora","m3/h",TipoDeMedida.CONSUMO_GAS.getNombre(),TipoDeMedida.CONSUMO_GAS.getUnidadDeBase(),TimeUnit.MINUTES.convert(1, TimeUnit.HOURS)),
    METRO3_MINUTO("Metro/s cúbico/s por minuto","m3/min",TipoDeMedida.CONSUMO_GAS.getNombre(),TipoDeMedida.CONSUMO_GAS.getUnidadDeBase(),1),
    METRO3("Metro/s cúbico/s","m3",TipoDeMedida.CAPACIDAD.getNombre(),TipoDeMedida.CAPACIDAD.getUnidadDeBase(),1000000),
    CELCIUS("Grado/s Celsius","°C",TipoDeMedida.TEMPERATURA.getNombre(),TipoDeMedida.TEMPERATURA.getUnidadDeBase(),1),
    FARENHEIT("Grado/s Farenheit","°F",TipoDeMedida.TEMPERATURA.getNombre(),TipoDeMedida.TEMPERATURA.getUnidadDeBase(),1);

    private String nombre;
    private String simbolo;
    private String tipoDeMedida;
    private String unidadDeBase;
    private double cantidadDeLaUnidadDeBase; // Ej.: Dado que para el kilogramo la unidad de base es el gramo, la cantidad sería 1000.

    private UnidadDeMedida(String nombre, String simbolo, String tipoDeMedida, String unidadDeBase,
                           double cantidadDeLaUnidadDeBase) {
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.tipoDeMedida = tipoDeMedida;
        this.unidadDeBase = unidadDeBase;
        this.cantidadDeLaUnidadDeBase = cantidadDeLaUnidadDeBase;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getSimbolo() {
        return simbolo;
    }
    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
    public String getTipoDeMedida() {
        return tipoDeMedida;
    }
    public void setTipoDeMedida(String tipoDeMedida) {
        this.tipoDeMedida = tipoDeMedida;
    }

    public double getCantidadDeLaUnidadDeBase() {
        return cantidadDeLaUnidadDeBase;
    }

    public double convertir(double cantidad, UnidadDeMedida nuevaUnidadDeMedida) {
        if(this.getTipoDeMedida().equals(nuevaUnidadDeMedida.getTipoDeMedida())
                && !this.getTipoDeMedida().equals("temperatura")) {
            cantidad = cantidad * this.getCantidadDeLaUnidadDeBase();
            cantidad = cantidad / nuevaUnidadDeMedida.getCantidadDeLaUnidadDeBase();
            return cantidad;
        }

        else if(this.nombre.equals("Grado/s Celsius") && nuevaUnidadDeMedida.getNombre().equals("Grado/s Farenheit")){
            cantidad =  cantidad * 1.8 + 32;
            return cantidad;
        }
        else if(this.nombre.equals("Grado/s Farenheit") && nuevaUnidadDeMedida.getNombre().equals("Grado/s Celsius")){
            cantidad =  (cantidad - 32) / 1.8;
            return cantidad;
        }
        else {
            System.out.println("ERROR: Para cambiar la unidad de medida, el tipo de medida debe coincidir.");
            return cantidad;
        }
    }

}
