package curso.android.misrecetapps.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;

public class PhotoTaker implements DefaultLifecycleObserver {

    public static final int PHOTO_LOCATED_IN_PRINCIPAL = 0;
    public static final int PHOTO_LOCATED_IN_INSTRUCCION = 1;
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<Uri> mTakePictureLauncher;
    private ActivityResultLauncher<String> mImportImageLauncher;
    private final Context mContext;
    private Bitmap mPhoto;
    private final RecetaViewModel mViewModel;
    private File mPhotoFileTemp;
    private Uri mPhotoUriTemp;

    public PhotoTaker(@NonNull ActivityResultRegistry registry, Context context, RecetaViewModel viewModel) {
        this.mRegistry = registry;
        this.mContext = context;
        this.mViewModel = viewModel;
        createTempPhotoUri();
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        Log.i("PHOTO TAKER", "onCreate!!!");
        mTakePictureLauncher = mRegistry.register(
                "takePicture",
                owner,
                new ActivityResultContracts.TakePicture(),
                result -> {
                    mPhoto = null;
                    if (result) {
                        processImage(mPhotoUriTemp, mPhotoFileTemp);
                    } else {
                        Log.i("PHOTO TAKER", "No hay resultado!!!");
                    }
                }
        );

        mImportImageLauncher = mRegistry.register(
                "documentImportImage",
                owner,
                new ActivityResultContracts.GetContent(), result -> {
                    if(result != null){
                        File newFile = null;
                        File oldFile = null;
                        try {
                            oldFile = copyFileUsingStream(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            newFile = Auxiliar.createTempImageFile(mContext);
                        } catch (IOException ex) {
                            Log.e("ERROR", ex.toString());
                        }
                        Auxiliar.renombrarArchivo(oldFile, newFile);
                        int photoOrigin = mViewModel.getmPhotoOriginTemp();
                        if (photoOrigin == PHOTO_LOCATED_IN_INSTRUCCION) {
                            int position = mViewModel.getmPositionTemp();
                            Log.i("PHOTO TAKER","Position in photo taker: " + position);
                            mViewModel.getReceta().getInstrucciones().get(position).setFoto(newFile.getName());
                            mViewModel.getmAdapterTemp().notifyDataSetChanged();
                        }
                        if (photoOrigin == PHOTO_LOCATED_IN_PRINCIPAL) {
                            mViewModel.getReceta().setFoto(newFile.getName());
                            mViewModel.getmFragmentTemp().getParentFragmentManager()
                                    .beginTransaction()
                                    .detach(mViewModel.getmFragmentTemp()).commit();
                            mViewModel.getmFragmentTemp().getParentFragmentManager()
                                    .beginTransaction()
                                    .attach(mViewModel.getmFragmentTemp()).commit();
                        }
                    }
                    else{
                        Log.i("PHOTO TAKER", "No hay resultado!!!");
                    }
                }
        );

    }

    public File convertImageUriToFile(Uri imageUri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String uri = cursor.getString(column_index);
        cursor.close();
        Log.i("PHOTO TAKER", uri);
        File file = new File(uri);

        return file;

    }

    public void processImage(Uri photoUri, File originalFile){
        try {
            mPhoto = getBitmapFromUri(mContext, photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean photoNotNull = mPhoto != null;
        if (photoNotNull) {
            File newFile = null;
            try {
                newFile = Auxiliar.createTempImageFile(mContext);
            } catch (IOException ex) {
                Log.e("ERROR", ex.toString());
            }
            if (newFile != null) {
                Auxiliar.renombrarArchivo(originalFile, newFile);
                int photoOrigin = mViewModel.getmPhotoOriginTemp();
                if (photoOrigin == PHOTO_LOCATED_IN_INSTRUCCION) {
                    int position = mViewModel.getmPositionTemp();
                    Log.i("PHOTO TAKER","Position in photo taker: " + position);
                    mViewModel.getReceta().getInstrucciones().get(position).setFoto(newFile.getName());
                    mViewModel.getmAdapterTemp().notifyDataSetChanged();
                }
                if (photoOrigin == PHOTO_LOCATED_IN_PRINCIPAL) {
                    mViewModel.getReceta().setFoto(newFile.getName());
                    mViewModel.getmFragmentTemp().getParentFragmentManager()
                            .beginTransaction()
                            .detach(mViewModel.getmFragmentTemp()).commit();
                    mViewModel.getmFragmentTemp().getParentFragmentManager()
                            .beginTransaction()
                            .attach(mViewModel.getmFragmentTemp()).commit();
                }
            }
            else{
                Log.e("PHOTO TAKER", "No se pudo tomar la foto, error al crear el archivo");
            }
        }
    }

    public void takePicture() {

        if(mPhotoUriTemp != null){
            mTakePictureLauncher.launch(mPhotoUriTemp);
        }
        else{
            Log.e("PHOTO TAKER", "No se pudo tomar la foto. No se creo correctamente el TEMP FILE...");
        }
    }

    public void importPhoto(){
        Log.i("PHOTO", "LOADING PHOTO");
        Toast.makeText(mContext, "IMPORTANDO IMAGEN...", Toast.LENGTH_LONG).show();
        mImportImageLauncher.launch("image/*");
    }

    @NonNull
    private File copyFileUsingStream(@NonNull Uri uri) throws IOException {
        Log.i("LOAD IMAGE", "COPY IMAGE TO TEMP");
        File directorio = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dest = new File(directorio
                + "/" + File.separator
                + "imageImportTemp.jpg");
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
            Log.i("IMAGE", "IMAGE COPY WRITE SUCCESS");
        } finally {
            is.close();
            if (os != null) {
                os.close();
            }
        }
        return dest;
    }

    public void clear(){
        mTakePictureLauncher.unregister();
    }

    private void createTempPhotoUri() {
        File photoFileTemp = null;
        try {
            File directorio = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            photoFileTemp = new File(directorio + "/" + File.separator + "photoTemp.jpg");
            photoFileTemp.createNewFile();
        } catch (IOException ex) {
            Log.e("PHOTO TAKER", ex.toString());
        }
        this.mPhotoFileTemp = photoFileTemp;

        Uri uriTemp = null;
        if (photoFileTemp != null) {
            uriTemp = FileProvider.getUriForFile(
                    mContext,
                    "curso.android.misrecetapps.fileprovider",
                    photoFileTemp);
        }
        else{
            Log.e("PHOTO TAKER", "Error creando TEMP FILE para camara input...");
        }
        this.mPhotoUriTemp = uriTemp;
    }

    public Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        if(uri == null){
            Toast toast = Toast.makeText(context, "Ups! Evite rotar el movil después de sacar la foto...", Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return null;
        }
        else{
            InputStream input = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1)) return null;
            //Image resolution is based on 480x800
            float hh = 800f;//The height is set as 800f here
            float ww = 480f;//Set the width here to 480f
            //Zoom ratio. Because it is a fixed scale, only one data of height or width is used for calculation
            int be = 1;//be=1 means no scaling
            if (originalWidth > originalHeight && originalWidth > ww) {//If the width is large, scale according to the fixed size of the width
                be = (int) (originalWidth / ww);
            } else if (originalWidth < originalHeight && originalHeight > hh) {//If the height is high, scale according to the fixed size of the width
                be = (int) (originalHeight / hh);
            }
            if (be <= 0)
                be = 1;
            //Proportional compression
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;//Set scaling
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();

            return compressImage(bitmap);
        }
    }

    public Bitmap compressImage(Bitmap image) {
        Log.i("PHOTO-TAKER", "compressImage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//Quality compression method, here 100 means no compression, store the compressed data in the BIOS
        int quality = 100;
        Log.i("PHOTO-TAKER", String.valueOf(baos.toByteArray().length / 1024f));
        while (baos.toByteArray().length / 1024f > 100) {  //Cycle to determine if the compressed image is greater than 100kb, greater than continue compression
            baos.reset();//Reset the BIOS to clear it
            //First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
            image.compress(Bitmap.CompressFormat.JPEG, quality, baos);//Here, the compression options are used to store the compressed data in the BIOS
            quality -= 10;//10 less each time
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//Store the compressed data in ByteArrayInputStream
        return BitmapFactory.decodeStream(isBm, null, null);
    }

}
