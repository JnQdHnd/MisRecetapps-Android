package curso.android.misrecetapps.database;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import curso.android.misrecetapps.interfaces.IIngredienteService;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Ingrediente_Table;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.Receta_Table;

public class IngredienteDB implements IIngredienteService {

    @Override
    public void saveList(List<Ingrediente> list){
        if (list != null){
            if(list.size()>0){
                for(Ingrediente item : list) {
                    item.save();
                }
            }
        }
    }

    @Override
    public void saveList(List<Ingrediente> list, Receta receta){
        Receta r = SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.eq(receta.getNombre()))
                .querySingle();
        if (list != null && r != null){
            if(list.size() > 0){
                for(Ingrediente item : list){
                    item.setReceta(r);
                    item.save();
                }
            }
        }
    }

    @Override
    public List<Ingrediente> getListByReceta(@NonNull Receta receta) {
        return SQLite.select()
                .from(Ingrediente.class)
                .where(Ingrediente_Table.receta_id.eq(receta.getId()))
                .queryList();
    }

    @Override
    public void delete(@NonNull Ingrediente item) {
        item.delete();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(Ingrediente.class)
                .where(Ingrediente_Table.id.eq(id))
                .query();
    }

    @Override
    public void save(@NonNull Ingrediente item) {
        item.save();
    }

    @Override
    public Ingrediente findById(Long id) {
        return SQLite.select()
                .from(Ingrediente.class)
                .where(Ingrediente_Table.id.eq(id))
                .querySingle();
    }
}
