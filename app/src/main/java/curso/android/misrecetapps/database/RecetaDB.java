package curso.android.misrecetapps.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import curso.android.misrecetapps.interfaces.IRecetaService;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.Receta_Table;

public class RecetaDB implements IRecetaService {
    @Override
    public List<Receta> findAll() {
        return SQLite.select()
                .from(Receta.class)
                .orderBy(Receta_Table.nombre, true)
                .queryList();
    }

    @Override
    public List<Receta> findAllComplete() {
        List<Receta> recetas = SQLite.select()
                .from(Receta.class)
                .orderBy(Receta_Table.nombre, true)
                .queryList();
        IngredienteDB ingredienteDB = new IngredienteDB();
        InstruccionDB instruccionDB = new InstruccionDB();
        ArtefactoEnUsoDB artefactoEnUsoDB =  new ArtefactoEnUsoDB();
        for(Receta receta : recetas){
            receta.setIngredientes(ingredienteDB.getListByReceta(receta));
            receta.setArtefactosEnUso(artefactoEnUsoDB.getListByReceta(receta));
            receta.setInstrucciones(instruccionDB.getListByReceta(receta));
        }
        return recetas;
    }

    @Override
    public Receta findById(Long id) {
        return SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public Receta findByNombreComplete(String nombre) {
        Receta receta = SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.is(nombre))
                .querySingle();

        if (receta != null) {
            Log.i("RECETA DB ID", String.valueOf(receta.getId()));
        }

        IngredienteDB ingredienteDB = new IngredienteDB();
        InstruccionDB instruccionDB = new InstruccionDB();
        ArtefactoEnUsoDB artefactoEnUsoDB =  new ArtefactoEnUsoDB();
        if (receta != null) {
            receta.setIngredientes(ingredienteDB.getListByReceta(receta));
            receta.setArtefactosEnUso(artefactoEnUsoDB.getListByReceta(receta));
            receta.setInstrucciones(instruccionDB.getListByReceta(receta));
        }
        return receta;
    }

    @Override
    public Receta findByNombre(String nombre) {
        return SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.is(nombre))
                .querySingle();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(Receta.class)
                .where(Receta_Table.id.eq(id))
                .query();
    }

    @Override
    public void delete(@NonNull Receta item) {
        item.delete();
    }

    @Override
    public void save(Receta item) {
        try {
            item.save();
            Log.i("DBFLOW", "Receta guardada " + item.getNombre() + " en BD");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("DBFLOW", "Error al guardar receta en BD");
        }
    }

    @Override
    public boolean existsByNombre(String nombre) {
        boolean exists = false;
        long i = SQLite.select()
                .from(Receta.class)
                .where(Receta_Table.nombre.eq(nombre))
                .count();
        if(i > 0){
            exists = true;
        }
        return exists;
    }
}
