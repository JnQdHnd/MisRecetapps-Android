package curso.android.misrecetapps.database;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.interfaces.IProductoService;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Producto_Table;

public class ProductoDB implements IProductoService {
    @Override
    public List<Producto> findAll() {
        return SQLite.select()
                .from(Producto.class)
                .orderBy(Producto_Table.nombre, true)
                .queryList();
    }

    @Override
    public List<String> findAllNames() {
        List<String> list = new ArrayList<>();
        for(Producto producto : SQLite.select()
                .from(Producto.class)
                .orderBy(Producto_Table.nombre, true)
                .queryList()){
            list.add(producto.getNombre());
        }
        return list;
    }

    @Override
    public Producto findById(Long id) {
        return SQLite.select()
                .from(Producto.class)
                .where(Producto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public Producto findByNombre(String nombre) {
        return SQLite.select()
                .from(Producto.class)
                .where(Producto_Table.nombre.is(nombre))
                .querySingle();
    }

    @Override
    public void delete(Long id) {
        SQLite.delete()
                .from(Producto.class)
                .where(Producto_Table.id.eq(id))
                .query();
    }

    @Override
    public void delete(@NonNull Producto item) {
        item.delete();
    }

    @Override
    public void save(Producto item) {
        item.save();
    }

    @Override
    public void saveList(List<Producto> list) {
        for (Producto item : list) {
            save(item);
        }
    }

    @Override
    public boolean existsByNombre(String nombre) {
        boolean exists = false;
        long i = SQLite.select()
                .from(Producto.class)
                .where(Producto_Table.nombre.eq(nombre))
                .count();
        if(i > 0){
            exists = true;
        }
        return exists;
    }
}
