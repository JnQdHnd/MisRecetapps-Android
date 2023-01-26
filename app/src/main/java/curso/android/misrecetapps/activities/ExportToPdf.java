package curso.android.misrecetapps.activities;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import curso.android.misrecetapps.controllers.receta.RecetaViewModel;

public class ExportToPdf implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mExportPdfLauncher;
    private final RecetaViewModel mViewModel;
    private final Context mContext;
    private View mView;

    public ExportToPdf(@NonNull ActivityResultRegistry registry, Context context, RecetaViewModel recetaViewModel) {
        this.mRegistry = registry;
        this.mContext = context;
        this.mViewModel = recetaViewModel;
    }

    public void savePdf(View view){
        Log.i("PDF", "SAVING PDF");
        this.mView = view;
        mExportPdfLauncher.launch(mViewModel.getReceta().getNombre());
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
        mExportPdfLauncher = mRegistry.register(
                "documentCreate",
                owner,
                new ActivityResultContracts.CreateDocument("application/pdf"),
                result -> {
                    Log.i("PDF", "OBTENIENDO RESULT");
                    String nombreDeArchivo = getFileNameFromUri(mContext.getContentResolver(), result);
                    generatePdf(nombreDeArchivo);
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

    public void generatePdf(String nombreAsignadoAlArchivo) {

        Log.i("PDF", "GENERANDO PDF");

        PdfDocument pdfDocument = writePDFDocument(mView);

        File file;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nombreAsignadoAlArchivo);
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(mContext, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDF", "NO SE PUDO GENERAR EL PDF! ERROR AL CREAR O ESCRIBIR EL ARCHIVO");
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    public Uri generatePdf(View pdfView) {
        Log.i("PDF", "GENERANDO PDF URI");

        PdfDocument pdfDocument = writePDFDocument(pdfView);
        Uri uri = null;
        File file;
        try {
            File directorio = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            file = new File(directorio
                    + "/" + File.separator
                    + mViewModel.getReceta().getNombre().replaceAll("\\s","")
                    + ".pdf");
            file.createNewFile();
            pdfDocument.writeTo(new FileOutputStream(file));
            uri = FileProvider.getUriForFile(mContext, "curso.android.misrecetapps.fileprovider", file);
            mContext.grantUriPermission("android", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Toast.makeText(mContext, "Enviando receta en PDF...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDF", "NO SE PUDO GENERAR EL PDF! ERROR AL CREAR O ESCRIBIR EL ARCHIVO");
            e.printStackTrace();
        }
        pdfDocument.close();

        return uri;
    }

    @NonNull
    private PdfDocument writePDFDocument(@NonNull View view) {

        Log.i("PDF", "Escribiendo PDF document...");

        PdfDocument pdfDocument = new PdfDocument();
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.
                    Builder(pageWidth, pageHeight,  1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas pageCanvas = page.getCanvas();
        pageCanvas.scale(1f, 1f);
        pageWidth = pageCanvas.getWidth();
        pageHeight = pageCanvas.getHeight();
        int measureWidth = View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY);
        view.measure(measureWidth, measuredHeight);
        view.layout(0, 0, pageWidth, pageHeight);
        view.draw(pageCanvas);
        pdfDocument.finishPage(page);

        Log.i("PDF", "PDF document creado con éxito!");

        return pdfDocument;
    }

    public ActivityResultLauncher<String> getmExportPdfLauncher() {
        return mExportPdfLauncher;
    }
}




