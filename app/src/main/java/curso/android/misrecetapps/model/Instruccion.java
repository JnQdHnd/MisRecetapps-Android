package curso.android.misrecetapps.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import curso.android.misrecetapps.database.MisRecetappsDatabase;

/**
 * Clase entity que gestiona las instrucciones en el proyecto.
 * @author Julian Quenard
 * 01-09-2021
 */
@Table(database = MisRecetappsDatabase.class, name = "instrucciones")
public class Instruccion extends BaseModel {


    //ATRIBUTOS ------------------------------------------

    @PrimaryKey(autoincrement = true)
    private long id;
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    private Receta receta;
    @Column
    private String instruccion;
    @Column
    private long orden;
    @Column
    private String foto;

    //COSTRUCTORES ----------------------------------------------

    public Instruccion() {
    }

    public Instruccion(String instruccion, Long orden, String foto) {
        this.instruccion = instruccion;
        this.orden = orden;
        this.foto = foto;
    }

    public Instruccion(Long id, String instruccion, Long orden, String foto) {
        this.id = id;
        this.instruccion = instruccion;
        this.orden = orden;
        this.foto = foto;
    }

    //METODOS ----------------------------------------------

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

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }

    public Long getOrden() {
        return orden;
    }

    public void setOrden(Long orden) {
        this.orden = orden;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }
}
