package curso.android.misrecetapps.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import curso.android.misrecetapps.interfaces.IInstruccionService;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Instruccion_Table;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.Receta_Table;

public class InstruccionDB implements IInstruccionService {
    @Override
    public Instruccion findById(Long id) {
        return SQLite.select()
                .from(Instruccion.class)
                .where(Instruccion_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public void delete(Instruccion item) {
        item.delete();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(Instruccion.class)
                .where(Instruccion_Table.id.eq(id))
                .query();
    }

    @Override
    public void saveList(List<Instruccion> list) {
        if (list != null && list.size()>0){
            for(Instruccion item : list){
                item.save();
            }
        }
    }

    @Override
    public void saveList(List<Instruccion> list, Receta receta) {
        Receta r = SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.eq(receta.getNombre()))
                .querySingle();
        if (list != null && list.size()>0){
            for(Instruccion item : list){
                item.setReceta(r);
                item.save();
            }
        }
    }

    @Override
    public List<Instruccion> getListByReceta(Receta receta) {
        return SQLite.select()
                .from(Instruccion.class)
                .where(Instruccion_Table.receta_id.eq(receta.getId()))
                .queryList();
    }

}
