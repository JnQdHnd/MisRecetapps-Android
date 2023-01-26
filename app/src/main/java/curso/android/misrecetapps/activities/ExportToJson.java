package curso.android.misrecetapps.activities;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.model.Receta;
import curso.android.misrecetapps.model.RecetaExportable;

public class ExportToJson implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mExportJsonLauncher;
    private final RecetaViewModel mViewModel;
    private final Context mContext;

    public ExportToJson(@NonNull ActivityResultRegistry registry, Context context, RecetaViewModel recetaViewModel) {
        this.mRegistry = registry;
        this.mContext = context;
        this.mViewModel = recetaViewModel;
    }

    public void save(){
        Log.i("JSON", "SAVING JSON");
        mExportJsonLauncher.launch(mViewModel.getReceta().getNombre());
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
        mExportJsonLauncher = mRegistry.register(
                "documentCreateJson",
                owner,
                new ActivityResultContracts.CreateDocument("application/json"),
                result -> {
                    Log.i("JSON", "OBTENIENDO RESULT");
                    String path = ExportToJson.this.getFileNameFromUri(mContext.getContentResolver(), result);
                    ExportToJson.this.generateJson(path);
                    Log.i("PATH", path);
                }
        );
    }

    private String getFileNameFromUri(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public void generateJson(String nombreAsignadoAlArchivo) {

        Log.i("JSON", "GENERANDO JSON");

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        Receta receta = mViewModel.getReceta();
        RecetaExportable recetaExportable = new RecetaExportable(receta);
        File file;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), nombreAsignadoAlArchivo);
            String json = gson.toJson(recetaExportable);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Toast.makeText(mContext, "JSON file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("EXPORT TO JSON", e.getMessage());
        }
    }

    public Uri generateJson() {

        Log.i("JSON", "GENERANDO JSON");

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        Receta receta = mViewModel.getReceta();
        RecetaExportable recetaExportable = new RecetaExportable(receta);
        File file;
        Uri uri = null;
        try {
            File directorio = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            file = new File(directorio
                    + "/" + File.separator
                    + receta.getNombre().replaceAll("\\s","")
                    + ".misrapps");
            file.createNewFile();
            String json = gson.toJson(recetaExportable);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            uri = FileProvider.getUriForFile(mContext, "curso.android.misrecetapps.fileprovider", file);
            mContext.grantUriPermission("android", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Toast.makeText(mContext, "Enviando archivo de receta...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("EXPORT TO JSON", e.getMessage());
        }

        return uri;

    }

}




