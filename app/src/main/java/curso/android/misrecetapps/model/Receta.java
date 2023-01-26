package curso.android.misrecetapps.model;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import curso.android.misrecetapps.database.MisRecetappsDatabase;
import curso.android.misrecetapps.database.ProductoDB;

@Table(database = MisRecetappsDatabase.class, name = "recetas")
public class Receta extends BaseModel {

    public static final String ID = "id";

    //ATRIBUTOS -------------------------------------------------------------------
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String nombre;

    private List<Ingrediente> ingredientes;

    private List<ArtefactoEnUso> artefactosEnUso;

    private List<Instruccion> instrucciones;

    private List<Preparacion> preparaciones;

    @Column
    private double porciones;

    @Column
    private double costo;

    @Column
    private boolean esFavorita;

    @Column
    private String foto;

    //CONSTRUCTORES ----------------------------------------------------
    public Receta() {}

    public Receta(String nombre, List<Ingrediente> ingredientes,
                  List<ArtefactoEnUso> artefactosEnUso, List<Instruccion> instrucciones,
                  double porciones, double costo) {
        this.nombre = nombre;
        this.ingredientes = ingredientes;
        this.artefactosEnUso = artefactosEnUso;
        this.instrucciones = instrucciones;
        this.porciones = porciones;
        this.costo = costo;
        this.esFavorita = false;
    }

    public Receta(String nombre, List<Preparacion> preparaciones, double porciones, double costo) {
        this.nombre = nombre;
        this.preparaciones = preparaciones;
        this.porciones = porciones;
        this.costo = costo;
        this.esFavorita = false;
    }

    //GETTERS Y SETTERS

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<ArtefactoEnUso> getArtefactosEnUso() {
        return artefactosEnUso;
    }

    public void setArtefactosEnUso(List<ArtefactoEnUso> artefactosEnUso) {
        this.artefactosEnUso = artefactosEnUso;
    }

    public List<Instruccion> getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(List<Instruccion> instrucciones) {
        this.instrucciones = instrucciones;
    }

    public List<Preparacion> getPreparaciones() {
        return preparaciones;
    }

    public void setPreparaciones(List<Preparacion> preparaciones) {
        this.preparaciones = preparaciones;
    }

    public double getPorciones() {
        return porciones;
    }

    public void setPorciones(double porciones) {
        this.porciones = porciones;
    }

    public double getCosto() {
        return costo;
    }

    public String getCostoString() {
        return String.valueOf(costo);
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public boolean isEsFavorita() {
        return esFavorita;
    }

    public void setEsFavorita(boolean esFavorita) {
        this.esFavorita = esFavorita;
    }

    public String getFoto() {return foto;}

    public void setFoto(String foto) {this.foto = foto;}

    public double calculaCostoTotal(ProductoDB productoDB) {
        double costoIngredientes = 0;
        double costoArtefactos = 0;
        if(ingredientes != null && ingredientes.size() > 0){
            for(Ingrediente i : ingredientes) {
                Producto p = productoDB.findById(i.getProductoId());
                double precioProducto = p.getPrecioSinDesperdicio();
                double cantidadProducto = p.getCantidad();
                UnidadDeMedida unidadProducto = p.getUnidadDeMedida();

                double cantidadIngrediente = i.getCantidad();
                UnidadDeMedida unidadIngrediente = i.getUnidadDeMedida();
                if(unidadProducto != unidadIngrediente) {
                    cantidadIngrediente = unidadIngrediente.convertir(cantidadIngrediente, unidadProducto);
                }
                double precioIngrediente = cantidadIngrediente * precioProducto / cantidadProducto;
                costoIngredientes = costoIngredientes + precioIngrediente;
            }
        }
        return costoIngredientes + costoArtefactos;
    }

    public double calculaCostoTotal(List<Producto> productosEnReceta) {
        double costoIngredientes = 0;
        double costoArtefactos = 0;
        if(ingredientes != null && ingredientes.size() > 0){
            for(Ingrediente i : ingredientes) {
                if(productosEnReceta != null && productosEnReceta.size() > 0) {
                    for (Producto p : productosEnReceta) {
                        if (i.getProductoId().equals(p.getId())) {
                            double precioProducto = p.getPrecioSinDesperdicio();
                            double cantidadProducto = p.getCantidad();
                            UnidadDeMedida unidadProducto = p.getUnidadDeMedida();

                            double cantidadIngrediente = i.getCantidad();
                            UnidadDeMedida unidadIngrediente = i.getUnidadDeMedida();
                            if (unidadProducto != unidadIngrediente) {
                                cantidadIngrediente = unidadIngrediente.convertir(cantidadIngrediente, unidadProducto);
                            }
                            double precioIngrediente = cantidadIngrediente * precioProducto / cantidadProducto;
                            costoIngredientes = costoIngredientes + precioIngrediente;
                            break;
                        }
                    }
                }
                else{
                    Log.e("CALCULA-COSTOS", "Lista de productos null o empty");
                    break;
                }
            }
        }
        else{
            Log.e("CALCULA-COSTOS", "Lista de ingredientes null o empty");
        }
        return costoIngredientes + costoArtefactos;
    }
}
