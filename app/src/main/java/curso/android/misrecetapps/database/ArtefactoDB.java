package curso.android.misrecetapps.database;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.interfaces.IArtefactoService;
import curso.android.misrecetapps.model.Artefacto;
import curso.android.misrecetapps.model.Artefacto_Table;

public class ArtefactoDB implements IArtefactoService {
    @Override
    public List<Artefacto> findAll() {
        return SQLite.select()
                .from(Artefacto.class)
                .orderBy(Artefacto_Table.nombre, true)
                .queryList();
    }

    @Override
    public List<String> findAllNames() {
        List<String> list = new ArrayList<>();
        for(Artefacto artefacto : SQLite.select()
                .from(Artefacto.class)
                .orderBy(Artefacto_Table.nombre, true)
                .queryList()){
            list.add(artefacto.getNombre());
        }
        return list;
    }

    @Override
    public Artefacto findById(Long id) {
        return SQLite.select()
                .from(Artefacto.class)
                .where(Artefacto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public Artefacto findByNombre(String nombre) {
        return SQLite.select()
                .from(Artefacto.class)
                .where(Artefacto_Table.nombre.is(nombre))
                .querySingle();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(Artefacto.class)
                .where(Artefacto_Table.id.eq(id))
                .query();
    }

    @Override
    public void delete(@NonNull Artefacto item) {
        item.delete();
    }

    @Override
    public void save(Artefacto item) {
        item.save();
    }

    @Override
    public void saveList(List<Artefacto> list) {
        for (Artefacto item : list) {
            save(item);
        }
    }

    @Override
    public boolean existsByNombre(String nombre) {
        boolean exists = false;
        long i = SQLite.select()
                .from(Artefacto.class)
                .where(Artefacto_Table.nombre.eq(nombre))
                .count();
        if(i > 0){
            exists = true;
        }
        return exists;
    }

}
