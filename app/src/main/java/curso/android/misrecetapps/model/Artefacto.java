package curso.android.misrecetapps.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
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

@Table(database = MisRecetappsDatabase.class, name = "artefactos")
public class Artefacto extends BaseModel {

	public static final String ID = "id";
	
	//ATRIBUTOS----------------------------------------------------------
	
	@PrimaryKey(autoincrement = true)
	private long id;
	
	@Column
	private String nombre;
	
	private String descripcion;

	@Column
	private double consumoEnergetico; // Se deberá indicar la cantidad de consumo establecida por el fabricante. Ej: 0.85Kw/h, 2.56m3/h, etc.

	@Column
	private UnidadDeMedida unidadDeConsumo; // Se deberá indicar la unidad de consumo establecida por el fabricante. Ej: Kw/h, m3/h, etc.

	@Column
	private boolean esHorno;

	//CONSTRUCTORES-----------------------------------------------------

	public Artefacto() {
	}
	public Artefacto(Long id, String nombre, UnidadDeMedida unidadDeConsumo, double consumoEnergetico, boolean esHorno) {
		this.id = id;
		this.nombre = nombre;
		this.unidadDeConsumo = unidadDeConsumo;
		this.consumoEnergetico = consumoEnergetico;
		this.esHorno = esHorno;
	}
	public Artefacto(double consumoEnergetico, boolean esHorno, String nombre, UnidadDeMedida unidadDeConsumo) {
		this.nombre = nombre;
		this.unidadDeConsumo = unidadDeConsumo;
		this.consumoEnergetico = consumoEnergetico;
		this.esHorno = esHorno;
	}

	//METODOS------------------------------------------------------------
		
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
	
	
	@NonNull
	@Override
	public String toString() {
		return "Artefacto [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", unidadDeConsumo="
				+ unidadDeConsumo + ", consumoEnergetico=" + consumoEnergetico
				+ ", esHorno=" + esHorno + "]";
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public UnidadDeMedida getUnidadDeConsumo() {
		return unidadDeConsumo;
	}
	public void setUnidadDeConsumo(UnidadDeMedida unidadDeConsumo) {
		this.unidadDeConsumo = unidadDeConsumo;
	}
	public void setUnidadDeConsumoString(String unidadDeMedida) {
		if(unidadDeMedida != null){
			if(!unidadDeMedida.isEmpty()){
				if(!unidadDeMedida.equals("0")){
					for(UnidadDeMedida um : UnidadDeMedida.values()){
						if(um.getNombre().equals(unidadDeMedida) || um.getSimbolo().equals(unidadDeMedida)){
							this.unidadDeConsumo = um;
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

	public double getConsumoEnergetico() {
		return consumoEnergetico;
	}
	public void setConsumoEnergetico(double consumoEnergetico) {
		this.consumoEnergetico = consumoEnergetico;
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
	public boolean isEsHorno() {
		return esHorno;
	}
	public void setEsHorno(boolean esHorno) {
		this.esHorno = esHorno;
	}
	public void setEsHorno(String esHornoText) {
		boolean esHorno = false;
		if(esHornoText.equalsIgnoreCase("true")) {
			esHorno = true;
		}
		else if(esHornoText.equalsIgnoreCase("false")) {
			esHorno = false;
		}
		this.esHorno = esHorno;
	}

}