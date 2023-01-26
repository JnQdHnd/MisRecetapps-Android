package curso.android.misrecetapps;

import android.app.Application;
import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.MisRecetappsDatabase;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.model.Producto;

public class MyApplication extends Application {

    private boolean inicializado = false;

    @Override
    public void onCreate() {
        Log.i("APPLICATION", "Creando aplicacion");
        super.onCreate();
        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(MisRecetappsDatabase.class)
                        .databaseName(MisRecetappsDatabase.NAME)
                        .build())
                .build());

        verificaInicializado();

        if(!inicializado){
            Log.i("APPLICATION", "Creando productos y artefactos DEFAULT");
            ArtefactoDB artefactoDB = new ArtefactoDB();
            ProductoDB productoDB = new ProductoDB();
            ItemsDefault itemsDefault = new ItemsDefault();
            itemsDefault.generaArtefactosDefault();
            itemsDefault.generaProductosDefault();
            artefactoDB.saveList(itemsDefault.getmArtefactosDefault());
            productoDB.saveList(itemsDefault.getmProductosDefault());
        }
    }

    private void verificaInicializado(){
        long productosSize = SQLite.select(Method.count())
                .from(Producto.class)
                .count();
        if(productosSize > 0){
            inicializado = true;
        }
        Log.i("APPLICATION", "Productos SIZE: " + productosSize);
    }
}
