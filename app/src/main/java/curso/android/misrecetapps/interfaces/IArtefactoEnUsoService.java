package curso.android.misrecetapps.interfaces;

import java.util.List;

import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Receta;

/**
 * @author Julián Quenard *
 * 15 jul. 2022
 */
public interface IArtefactoEnUsoService {
	/**
	 * Método que elimina el item que corresponda al id de la BD.
	 */
	ArtefactoEnUso findById(Long id);

	void delete(ArtefactoEnUso item);

	void delete(Long id);

	void saveList(List<ArtefactoEnUso> list);

	void saveList(List<ArtefactoEnUso> list, Receta receta);

	List<ArtefactoEnUso> getListByReceta(Receta receta);

}
