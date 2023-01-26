package curso.android.misrecetapps.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import curso.android.misrecetapps.database.MisRecetappsDatabase;

@Table(database = MisRecetappsDatabase.class, name = "ingredientes")
public class Ingrediente extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private long id;
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    private Receta receta;
    @Column
    private Long productoId;
    @Column
    private double cantidad;
    @Column
    private UnidadDeMedida unidadDeMedida;

    //CONSTRUCTORES----------------------------------------------------
    public Ingrediente() {
    }

    public Ingrediente(Long productoId, double cantidad, UnidadDeMedida unidadDeMedida) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.unidadDeMedida = unidadDeMedida;
    }

    //METODOS------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setId(String id) {
        if(id != null && !id.isEmpty() && Long.parseLong(id)>0) {
            this.id = Long.parseLong(id);
        }
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
    public void setProductoId(String productoId) {
        if(productoId != null && !productoId.isEmpty() && Long.parseLong(productoId)>0) {
            this.productoId = Long.parseLong(productoId);
        }
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public UnidadDeMedida getUnidadDeMedida() {
        return unidadDeMedida;
    }

    public void setUnidadDeMedida(UnidadDeMedida unidadDeMedida) {
        this.unidadDeMedida = unidadDeMedida;
    }

    public void setUnidadDeMedidaString(String unidadDeMedida) {
        if(unidadDeMedida != null){
            if(!unidadDeMedida.isEmpty()){
                if(!unidadDeMedida.equals("0")){
                    for(UnidadDeMedida um : UnidadDeMedida.values()){
                        if(um.getNombre().equals(unidadDeMedida) || um.getSimbolo().equals(unidadDeMedida)){
                            this.unidadDeMedida = um;
                            break;
                        }
                    }
                }
            }
        }
        else{
            Log.e("GUARDANDO-UNIDAD_ING", "No se pudo guardar!!");
        }
    }

    public String getCantidadFormateado() {
        if(cantidad==0) {
            return "0";
        }
        else {
            DecimalFormat formato = new DecimalFormat("#####0.##", new DecimalFormatSymbols(Locale.ITALY));
            return formato.format(cantidad);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Ingrediente [productoId=" + productoId + ", cantidad=" + cantidad + ", unidadDeMedida=" + unidadDeMedida
                + "]";
    }

    public String getCantidadFormateada(double cantidad) {
        if(cantidad==0) {
            return "0";
        }
        else {
            DecimalFormat formato = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ITALY));
            return formato.format(cantidad);
        }
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }
}
