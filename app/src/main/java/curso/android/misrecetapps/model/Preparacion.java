package curso.android.misrecetapps.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

public class Preparacion {
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private long recetaId;
    @Column
    private String nombreDePreparacion;

    public Preparacion() {
    }

    public Preparacion(Long id, Long recetaId, String nombreDePreparacion) {
        this.id = id;
        this.recetaId = recetaId;
        this.nombreDePreparacion = nombreDePreparacion;
    }

    public Preparacion(Long recetaId, String nombreDePreparacion) {
        this.recetaId = recetaId;
        this.nombreDePreparacion = nombreDePreparacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(Long recetaId) {
        this.recetaId = recetaId;
    }

    public String getNombreDePreparacion() {
        return nombreDePreparacion;
    }

    public void setNombreDePreparacion(String nombreDePreparacion) {
        this.nombreDePreparacion = nombreDePreparacion;
    }
}
