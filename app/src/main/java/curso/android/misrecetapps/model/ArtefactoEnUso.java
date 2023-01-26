package curso.android.misrecetapps.model;

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

/**
 * Clase entity que gestiona los Roles de los Usuarios en el proyecto.
 * @author Julian Quenard
 * 01-09-2021
 */
@Table(database = MisRecetappsDatabase.class, name = "artefactos_en_uso")
public class ArtefactoEnUso extends BaseModel {

    //ATRIBUTOS----------------------------------------------------------

    @PrimaryKey(autoincrement = true)
    private long id;
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    private Receta receta;
    @Column
    private boolean esHorno;
    @Column
    private Long artefactoId;
    @Column
    private int minutosDeUso;
    @Column
    private String intensidadDeUso;
    @Column
    private double temperatura;
    @Column
    private UnidadDeMedida unidadDeTemperatura;

    //CONSTRUCTORES

    public ArtefactoEnUso() {
    }

    public ArtefactoEnUso(boolean esHorno, Long artefactoId, int minutosDeUso, String intensidadDeUso) {
        this.esHorno = esHorno;
        this.artefactoId = artefactoId;
        this.minutosDeUso = minutosDeUso;
        this.intensidadDeUso = intensidadDeUso;
    }

    public ArtefactoEnUso(boolean esHorno, Long artefactoId, int minutosDeUso, double temperatura, UnidadDeMedida unidadDeTemperatura) {
        this.esHorno = esHorno;
        this.artefactoId = artefactoId;
        this.minutosDeUso = minutosDeUso;
        this.temperatura = temperatura;
        this.unidadDeTemperatura = unidadDeTemperatura;
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

    public Long getArtefactoId() {
        return artefactoId;
    }
    public void setArtefactoId(Long artefactoId) {
        this.artefactoId = artefactoId;
    }
    public void setArtefactoId(String artefactoId) {
        if(artefactoId != null && !artefactoId.isEmpty() && Long.parseLong(artefactoId)>0) {
            this.artefactoId = Long.parseLong(artefactoId);
        }
    }

    public int getMinutosDeUso() {
        return minutosDeUso;
    }
    public void setMinutosDeUso(int minutosDeUso) {
        this.minutosDeUso = minutosDeUso;
    }
    public void setMinutosDeUso(String minutosDeUso) {
        if(minutosDeUso != null && !minutosDeUso.isEmpty() && Integer.parseInt(minutosDeUso)>0) {
            this.minutosDeUso = Integer.parseInt(minutosDeUso);
        }
        else {
            this.minutosDeUso = 0;
        }
    }

    public String getIntensidadDeUso() {
        return intensidadDeUso;
    }
    public void setIntensidadDeUso(String intensidadDeUso) {
        this.intensidadDeUso = intensidadDeUso;
    }
    public double getTemperatura() {
        return temperatura;
    }
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
    public UnidadDeMedida getUnidadDeTemperatura() {
        return unidadDeTemperatura;
    }

    public void setUnidadDeTemperatura(UnidadDeMedida unidadDeTemperatura) {
        this.unidadDeTemperatura = unidadDeTemperatura;
    }

    public void setUnidadDeTemperaturaString(String unidadDeMedida) {
        if(unidadDeMedida != null && !unidadDeMedida.isEmpty() && !unidadDeMedida.equals("0")) {
            for(UnidadDeMedida um : UnidadDeMedida.values()){
                if(um.getSimbolo().equals(unidadDeMedida)){
                    this.unidadDeTemperatura = um;
                    break;
                }
            }
        }
        else{
            this.unidadDeTemperatura = null;
        }
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

    public String getCantidadFormateada() {
        if(minutosDeUso==0) {
            return "0";
        }
        else {
            DecimalFormat formato = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ITALY));
            return formato.format(minutosDeUso);
        }
    }
    public boolean esHorno() {
        return esHorno;
    }
    public void setEsHorno(boolean esHorno) {
        this.esHorno = esHorno;
    }
    public void setEsHorno(String esHorno) {
        if(esHorno != null && !esHorno.isEmpty()) {
            this.esHorno = Boolean.parseBoolean(esHorno);
        }
        else {
            System.out.println("SET HORNO POR DEFECTO EN FALSE, PERO NO ES EL VALOR RECIBIDO...");
            this.esHorno = false;
        }
    }

    public boolean isEsHorno() {
        return esHorno;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    @NonNull
    @Override
    public String toString() {
        return "ArtefactoEnUso{" +
                "id=" + id +
                ", receta=" + receta +
                ", esHorno=" + esHorno +
                ", artefactoId=" + artefactoId +
                ", minutosDeUso=" + minutosDeUso +
                ", intensidadDeUso='" + intensidadDeUso + '\'' +
                ", temperatura=" + temperatura +
                ", unidadDeTemperatura=" + unidadDeTemperatura +
                '}';
    }
}
