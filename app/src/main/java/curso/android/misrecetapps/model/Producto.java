package curso.android.misrecetapps.model;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import curso.android.misrecetapps.database.MisRecetappsDatabase;

/**
 * Clase entity que gestiona los Productos en el proyecto.
 * @author Julian Quenard
 * 01-09-2021
 */

@Table(database = MisRecetappsDatabase.class, name = "MISRECETAPPS_DB")
public class Producto extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//ATRIBUTOS----------------------------------------------------------
	
	@PrimaryKey(autoincrement = true)
	private long id;
	
	@Column
	private String nombre;

	@Column
	private UnidadDeMedida unidadDeMedida;

	@Column
	private String codigoDeBarra;

	@Column
	private double desperdicio;

	private String descripcion;

	@Column
	private double cantidad;

	@Column
	private double precio;

	@Column
	private double precioSinDesperdicio;

	//CONSTRUCTORES

	public Producto() {
	}

	public Producto(Long id, String nombre, UnidadDeMedida unidadDeMedida,
					double desperdicio, double cantidad, double precio,
					double precioSinDesperdicio) {
		this.id = id;
		this.nombre = nombre;
		this.unidadDeMedida = unidadDeMedida;
		this.desperdicio = desperdicio;
		this.cantidad = cantidad;
		this.precio = precio;
		this.precioSinDesperdicio = precioSinDesperdicio;
	}

	public Producto(String nombre, UnidadDeMedida unidadDeMedida,
					double desperdicio, double cantidad, double precio,
					double precioSinDesperdicio) {
		this.nombre = nombre;
		this.unidadDeMedida = unidadDeMedida;
		this.desperdicio = desperdicio;
		this.cantidad = cantidad;
		this.precio = precio;
		this.precioSinDesperdicio = precioSinDesperdicio;
	}

	public Producto(double cantidad,
					double desperdicio,
					String nombre,
					double precio,
					double precioSinDesperdicio,
					UnidadDeMedida unidadDeMedida) {
		this.nombre = nombre;
		this.unidadDeMedida = unidadDeMedida;
		this.desperdicio = desperdicio;
		this.cantidad = cantidad;
		this.precio = precio;
		this.precioSinDesperdicio = precioSinDesperdicio;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public UnidadDeMedida getUnidadDeMedida() {
		return unidadDeMedida;
	}

	public String getCodigoDeBarra() {
		return codigoDeBarra;
	}

	public void setCodigoDeBarra(String codigoDeBarra) {
		this.codigoDeBarra = codigoDeBarra;
	}

	public void setUnidadDeMedida(UnidadDeMedida unidadDeMedida) {
		this.unidadDeMedida = unidadDeMedida;
	}

	public void setUnidadDeMedidaString(String unidadDeMedida) {
		if(unidadDeMedida != null){
			if(!unidadDeMedida.isEmpty()){
				if(!unidadDeMedida.equals("0")){
					for(UnidadDeMedida um : UnidadDeMedida.values()){
						if(um.getNombre().equals(unidadDeMedida)){
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

	public void setUnidadDeMedidaSimboloString(String unidadDeMedida) {
		if(unidadDeMedida != null){
			if(!unidadDeMedida.isEmpty()){
				if(!unidadDeMedida.equals("0")){
					for(UnidadDeMedida um : UnidadDeMedida.values()){
						if(um.getSimbolo().equals(unidadDeMedida)){
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

	public double getDesperdicio() {
		return desperdicio;
	}

	public void setDesperdicio(double desperdicio) {
		this.desperdicio = desperdicio;
	}

	public double getPrecioSinDesperdicio() {
		return precioSinDesperdicio;
	}

	public void setPrecioSinDesperdicio(double precioSinDesperdicio) {
		this.precioSinDesperdicio = precioSinDesperdicio;
	}
	
	public void setPrecioSinDesperdicioDesdeCantidad() {
		double cantidadSinDesperdicio = this.cantidad - (this.desperdicio * this.cantidad / 100);
		this.precioSinDesperdicio = this.cantidad * this.precio / cantidadSinDesperdicio;
	}

}
