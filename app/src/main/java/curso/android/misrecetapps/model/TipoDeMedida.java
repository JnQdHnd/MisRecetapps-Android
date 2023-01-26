package curso.android.misrecetapps.model;

/**
 * @author Julián Quenard *
 * 01-09-2021
 */
public enum TipoDeMedida {

    TEMPERATURA("temperatura","CELCIUS"),
    CONSUMO_GAS("consumoGas","METRO3_MINUTO"),
    CONSUMO_ELECTRICO("consumoElectrico","KILOWATT_MINUTO"),
    MASA ("masa","GRAMO"),
    CAPACIDAD("capacidad","MILILITRO"),
    UNIDAD("unidad","UNIDAD");

    private final String nombre;
    private final String unidadDeBase;

    TipoDeMedida (String nombre, String unidadDeBase){
        this.nombre = nombre;
        this.unidadDeBase = unidadDeBase;
    }

    public String getUnidadDeBase() {
        return unidadDeBase;
    }

    public String getNombre() {
        return nombre;
    }

}
