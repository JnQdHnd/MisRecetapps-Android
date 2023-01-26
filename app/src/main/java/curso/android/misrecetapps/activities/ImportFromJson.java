package curso.android.misrecetapps.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.BufferedReader;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.ArtefactoEnUsoDB;
import curso.android.misrecetapps.database.IngredienteDB;
import curso.android.misrecetapps.database.InstruccionDB;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.model.Artefacto;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.RecetaExportable;

public class ImportFromJson implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String[]> mImportJsonLauncher;
    private final Context mContext;
    private final RecetaViewModel mViewModel;
    private final NavController mNavController;

    public ImportFromJson(@NonNull ActivityResultRegistry registry, Context context, RecetaViewModel viewModel, NavController navController) {
        this.mRegistry = registry;
        this.mContext = context;
        this.mViewModel = viewModel;
        this.mNavController = navController;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mImportJsonLauncher = mRegistry.register(
                "documentImportJson",
                owner,
                new ActivityResultContracts.OpenDocument(),
                result -> {
                    if(result != null){
                        String mimeType = Auxiliar.queryName(mContext.getContentResolver(), result);
                        Log.i("JSON", "RESULT NOT NULL. MIMETYPE: " + mimeType);
                        if(mimeType.contains(".misrapps")){
                            importFileSucces(result);
                        }
                        else {
                            Toast.makeText(
                                    mContext,
                                    "Extensión de archivo erronea. Debe ser 'misrapps'",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    else{
                        Log.e("JSON", "RESULT");
                    }
                }
        );
    }

    public void load(){
        Log.i("JSON", "LOADING JSON");
        Toast.makeText(mContext, "IMPORTANDO RECETA...", Toast.LENGTH_LONG).show();
        String[] mimeTypes = new String[] {"application/json", "application/misrapps", "text/json"};
        mImportJsonLauncher.launch(mimeTypes);
    }

    private RecetaExportable recetaFromJson(@NonNull Uri jsonFile) throws IOException {
        Log.i("JSON", "TRANSFORMANDO JSON FILE EN RECETA");
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        File file = copyFileUsingStream(jsonFile);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        String responce = stringBuilder.toString();
        return gson.fromJson(responce, RecetaExportable.class);
    }

    @NonNull
    private File copyFileUsingStream(@NonNull Uri uri) throws IOException {
        Log.i("JSON", "COPY JSON TO TEMP");
        File directorio = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File dest = new File(directorio
                + "/" + File.separator
                + "recetaImportTemp.misrapps");
        dest.createNewFile();
        InputStream is = mContext.getContentResolver().openInputStream(uri);
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            Log.i("JSON", "JSON COPY WRITE SUCCESS");
        } finally {
            is.close();
            if (os != null) {
                os.close();
            }
        }
        return dest;
    }

    private void showImportRecetaDialog(Receta receta){
        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Importando Receta")
                .setMessage("¿Está seguro de importar " + receta.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) -> {
                    RecetaDB recetaDB = new RecetaDB();
                    receta.setFoto(null);
                    for (Instruccion i:receta.getInstrucciones()) {
                        i.setFoto(null);
                    }
                    saveRecetaEnDB(receta, recetaDB);
                    String recetaNombre = receta.getNombre();
                    mViewModel.setReceta(recetaDB.findByNombreComplete(recetaNombre));
                    mNavController.navigate(R.id.receta_details);
                })
                .show();
    }

    public void saveRecetaEnDB(Receta receta, RecetaDB recetaDB) {

        Log.i("IMPORT", "Guardando receta...");

        for(Ingrediente ingrediente: receta.getIngredientes()){
            ingrediente.setId(0L);
        }
        for(ArtefactoEnUso artefactoEnUso: receta.getArtefactosEnUso()){
            artefactoEnUso.setId(0L);
        }
        for(Instruccion instruccion: receta.getInstrucciones()){
            instruccion.setId(0L);
        }

        IngredienteDB ingredienteDB = new IngredienteDB();
        ArtefactoEnUsoDB artefactoEnUsoDB = new ArtefactoEnUsoDB();
        InstruccionDB instruccionDB = new InstruccionDB();

        try {
            String nombreDeRecetaOriginal = receta.getNombre();
            boolean recetaYaExiste = recetaDB.existsByNombre(nombreDeRecetaOriginal);
            int i = 1;
            while (recetaYaExiste){
                Log.i("IMPORT", "Ya existe receta con nombre: " + receta.getNombre());
                String nuevoNombre = nombreDeRecetaOriginal + " (" + i + ")";
                recetaYaExiste = recetaDB.existsByNombre(nuevoNombre);
                if(!recetaYaExiste){
                    Log.i("IMPORT", "El nuevo nombre no existe: " + nuevoNombre);
                    receta.setNombre(nuevoNombre);
                }
                i++;
            }
            receta.setId(0);
            recetaDB.save(receta);
            Receta r = recetaDB.findByNombre(receta.getNombre());
            for(Ingrediente ingrediente : receta.getIngredientes()){
                ingrediente.setReceta(r);

            }
            ingredienteDB.saveList(receta.getIngredientes(), receta);
            artefactoEnUsoDB.saveList(receta.getArtefactosEnUso(), receta);
            instruccionDB.saveList(receta.getInstrucciones(), receta);
            Toast.makeText(mContext, "Receta importada con éxito.", Toast.LENGTH_LONG).show();
            Log.i("SAVE-RECETA", "Receta importada con éxito.");
        } catch (Exception e) {
            Log.e("SAVE-RECETA", "Error al guardar receta en la BASE DE DATOS.");
            Log.e("SAVE-RECETA", e.toString());
            e.printStackTrace();
        }
    }

    public void importFileSucces(Uri result){
        Log.i("JSON", "GENERANDO RECETA DESDE JSON");
        RecetaExportable recetaExportable;
        try {
            recetaExportable = recetaFromJson(result);
            Receta receta = parseRecetaExportableToReceta(recetaExportable);
            showImportRecetaDialog(receta);
        } catch (IOException e) {
            Log.e("JSON", "JSON WRITE ERROR");
            e.printStackTrace();
        }
    }

    private Receta parseRecetaExportableToReceta(RecetaExportable recetaExportable) {
        Log.i("JSON", "PARSE RECETAEX TO RECETA");
        ProductoDB productoDB = new ProductoDB();
        ArtefactoDB artefactoDB = new ArtefactoDB();
        Receta receta = recetaExportable.getmReceta();

        List<Producto> productos = recetaExportable.getmProductos();
        List<Artefacto> artefactos = recetaExportable.getmArtefactos();

        if(productos.size() > 0){
            for(int i = 0; i < receta.getIngredientes().size(); i++){
                Ingrediente ingrediente = receta.getIngredientes().get(i);
                Producto producto = productos.get(i);
                Log.i("PARSING RECETAEX", "ingrediente id: "
                        + ingrediente.getProductoId()
                        + " - producto: "
                        + producto.getId() + " " + producto.getNombre());
                if (productoDB.existsByNombre(producto.getNombre())) {
                    Log.i("PARSING RECETAEX", "Existe producto con ese nombre. Buscando ID...");
                    Producto productoLocal = productoDB.findByNombre(producto.getNombre());
                    producto.setId(productoLocal.getId());
                }
                else {
                    Log.i("PARSING RECETAEX", "NO existe producto con ese nombre. CREANDO...");
                    producto.setId(0);
                    productoDB.save(producto);
                    producto.setId(productoDB.findByNombre(producto.getNombre()).getId());
                }
                ingrediente.setProductoId(producto.getId());
            }
        }

        if(artefactos.size() > 0){
            for(int i = 0; i < receta.getArtefactosEnUso().size(); i++){
                ArtefactoEnUso artefactoEnUso = receta.getArtefactosEnUso().get(i);
                Artefacto artefacto = artefactos.get(i);
                if (artefactoDB.existsByNombre(artefacto.getNombre())) {
                    Artefacto artefactoLocal = artefactoDB.findByNombre(artefacto.getNombre());
                    artefacto.setId(artefactoLocal.getId());
                }
                else {
                    artefacto.setId(0);
                    artefactoDB.save(artefacto);
                    artefacto.setId(artefactoDB.findByNombre(artefacto.getNombre()).getId());
                }
                artefactoEnUso.setArtefactoId(artefacto.getId());
            }
        }

        return receta;
    }
}




