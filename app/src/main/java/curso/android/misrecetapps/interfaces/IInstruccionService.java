package curso.android.misrecetapps.interfaces;

import java.util.List;

import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Receta;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IInstruccionService {
	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	Instruccion findById(Long id);

	void delete(Instruccion item);

	void delete(Long id);

	void saveList(List<Instruccion> list);

	void saveList(List<Instruccion> list, Receta receta);

	List<Instruccion> getListByReceta(Receta receta);

}
