package curso.android.misrecetapps.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import curso.android.misrecetapps.interfaces.IArtefactoEnUsoService;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.ArtefactoEnUso_Table;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.Receta_Table;


public class ArtefactoEnUsoDB implements IArtefactoEnUsoService {
    @Override
    public ArtefactoEnUso findById(Long id) {
        return SQLite.select()
                .from(ArtefactoEnUso.class)
                .where(ArtefactoEnUso_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public void delete(ArtefactoEnUso item) {
        item.delete();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(ArtefactoEnUso.class)
                .where(ArtefactoEnUso_Table.id.eq(id))
                .query();
    }

    @Override
    public void saveList(List<ArtefactoEnUso> list) {
        if (list != null){
            if(list.size() > 0){
                for (ArtefactoEnUso item : list) {
                    item.save();
                }
            }
        }
    }

    @Override
    public void saveList(List<ArtefactoEnUso> list, Receta receta) {
        Receta r = SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.eq(receta.getNombre()))
                .querySingle();
        if (list != null){
            if(list.size()>0){
                for(ArtefactoEnUso item : list){
                    item.setReceta(r);
                    item.save();
                }
            }
        }
    }

    @Override
    public List<ArtefactoEnUso> getListByReceta(Receta receta) {
        return SQLite.select()
                .from(ArtefactoEnUso.class)
                .where(ArtefactoEnUso_Table.receta_id.eq(receta.getId()))
                .queryList();
    }

}
