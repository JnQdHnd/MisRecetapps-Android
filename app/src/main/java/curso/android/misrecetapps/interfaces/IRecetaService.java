package curso.android.misrecetapps.interfaces;

import java.util.List;

import curso.android.misrecetapps.model.Receta;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IRecetaService {
	/**
	 * Método que trae todos los elementos de la BD.
	 * @return List<Receta>
	 */
	List<Receta> findAll();

    List<Receta> findAllComplete();

	/**
	 * Método que trae el item que corresponda al Id de la BD.
	 * @return Receta
	 */
	Receta findById(Long id);

	Receta findByNombreComplete(String nombre);

	/**
	 * Método que trae el elemento de la BD cuyo nombre coincida con el parámetro "term".
	 * @return List<Receta>
	 */
	Receta findByNombre(String nombre);
	
	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	void delete(Long id);

	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	void delete(Receta item);
	
	/**
	 * Método que guarda en la DB el objeto pasado como parámetro.
	 */
	void save(Receta receta);

	/**
	 * Método que verifica la existencia de un elemento por nombre.
	 */
	boolean existsByNombre(String nombre);

}
