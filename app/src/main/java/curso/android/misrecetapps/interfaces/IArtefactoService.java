package curso.android.misrecetapps.interfaces;

import java.util.List;

import curso.android.misrecetapps.model.Artefacto;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IArtefactoService {
	/**
	 * Método que trae todos los elementos de la BD.
	 * @return List<Artefacto>
	 */
	List<Artefacto> findAll();

	/**
	 * Método que trae el item que corresponda al Id de la BD.
	 * @return Artefacto
	 */
	Artefacto findById(Long id);
	
	/**
	 * Método que trae el elemento de la BD cuyo nombre coincida con el parámetro "term".
	 * @return Artefacto
	 */
	Artefacto findByNombre(String nombre);
	
	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	void delete(Long id);

	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	void delete(Artefacto item);
	
	/**
	 * Método que guarda en la DB el objeto pasado como parámetro.
	 */
	void save(Artefacto item);

	/**
	 * Método que guarda en la DB los objetos que se encuentran en la lista.
	 */
	void saveList(List<Artefacto> list);

	/**
	 * Método que verifica la existencia de un elemento en la BD que tenga el nombre indicado.
	 * @return boolean
	 */
	boolean existsByNombre(String nombre);

    List<String> findAllNames();
}

