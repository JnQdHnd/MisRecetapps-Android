package curso.android.misrecetapps.interfaces;

import java.util.List;

import curso.android.misrecetapps.model.Preparacion;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IPreparacionService {
	/**
	 * Método que trae todos los elementos de la BD.
	 * @return List<Preparacion>
	 */
	public List<Preparacion> findAll();
	
	/**
	 * Método que trae todos los elementos de la BD y los ordena según el criterio indicado "sort".
	 * @return List<Preparacion>
	 */
	public List<Preparacion> findAllSorted();
	
	/**
	 * Método que trae el item que corresponda al Id de la BD.
	 * @param id
	 * @return Preparacion
	 */
	public Preparacion findOne(Long id);
	
	/**
	 * Método que trae el item que corresponda al Id de la BD.
	 * @param recetaId
	 * @return Preparacion
	 */
	public Preparacion findByRecetaId(Long recetaId);
	
	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 * @param id
	 */
	public void delete(Long id);
	
	/**
	 * Método que guarda en la DB el objeto pasado como parámetro.
	 * @param preparacion
	 */
	public void save(Preparacion preparacion);

}