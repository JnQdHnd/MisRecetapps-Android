package curso.android.misrecetapps.interfaces;


import java.util.List;

import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Receta;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IIngredienteService {

	/**
	 * Método que guarda en la DB el objeto pasado como parámetro.
	 */
	void save(Ingrediente item);

	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */

	Ingrediente findById(Long id);

	void delete(Ingrediente item);

	void delete(Long id);

	void saveList(List<Ingrediente> list);

	void saveList(List<Ingrediente> list, Receta receta);

	List<Ingrediente> getListByReceta(Receta receta);

}
