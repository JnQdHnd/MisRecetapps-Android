package curso.android.misrecetapps;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import curso.android.misrecetapps.controllers.artefacto.ArtefactoFormDialog;
import curso.android.misrecetapps.controllers.producto.ProductoFormDialog;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.TipoDeMedida;
import curso.android.misrecetapps.model.UnidadDeMedida;

public final class Auxiliar {

    public static Locale localeToFormat = Locale.ITALY;
    public static final int BASE_INGREDIENTE = 10000;
    public static final int BASE_ARTEFACTO = 20000;
    public static final int BASE_INSTRUCCION = 30000;
    public static final int BASE_VISTA = 100;

    public static Locale getLocaleToFormat() { return localeToFormat; }

    public static void setLocaleToFormat(Locale localeToFormat) { Auxiliar.localeToFormat = localeToFormat; }

    public static void configSelect(@NonNull TextView textView, List<String> list, Context context){
        textView.setOnClickListener(v -> {
            Dialog dialog;
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(colorDrawable, 30, 30, 30, 30);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.show();
            EditText editText=dialog.findViewById(R.id.edit_text);
            ListView listView=dialog.findViewById(R.id.list_view);
            AppCompatImageButton addButton = dialog.findViewById(R.id.add_item_button);
            TextView subtitle = dialog.findViewById(R.id.subtitulo_search_dialog);

            addButton.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                textView.setText(adapter.getItem(position));
                dialog.dismiss();
            });
        });
    }

    public static void configSelect(@NonNull TextView textView,
                                    Context context,
                                    FragmentManager fragmentManager,
                                    DialogFragment dialogFragment){
        textView.setOnClickListener(v -> {

            Dialog dialog;
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            int w = ViewGroup.LayoutParams.MATCH_PARENT;
            int h = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(w,h);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(colorDrawable, 30, 30, 30, 30);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.show();
            EditText editText=dialog.findViewById(R.id.edit_text);
            ListView listView=dialog.findViewById(R.id.list_view);
            AppCompatImageButton addButton = dialog.findViewById(R.id.add_item_button);

            List<String> list = new ProductoDB().findAllNames();

            if(dialogFragment.getClass() == ProductoFormDialog.class){
                ((ProductoFormDialog) dialogFragment).setmTextView(textView);
                ((ProductoFormDialog) dialogFragment).setmParentDialog(dialog);
                list = new ProductoDB().findAllNames();
            }
            else if(dialogFragment.getClass() == ArtefactoFormDialog.class){
                ((ArtefactoFormDialog) dialogFragment).setmTextView(textView);
                ((ArtefactoFormDialog) dialogFragment).setmParentDialog(dialog);
                list = new ArtefactoDB().findAllNames();
            }

            addButton.setOnClickListener(view -> dialogFragment.show(fragmentManager, "itemDialog"));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                textView.setText(adapter.getItem(position));
                dialog.dismiss();
            });
        });
    }

    public static void configFocusSelect(@NonNull TextView textView,
                                         Activity activity,
                                         RecetaViewModel viewModel){
        textView.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_selected);
                hideKeyboard(activity);
                viewModel.setmFieldToFocus(textView);
                Log.i("HAS FOCUS", "ID TEXT VIEW: " + textView.getId());
            }
            else{
                textView.setBackgroundResource(R.drawable.background_square_white_bordered);
            }
        });
    }

    public static void configFocusInput(@NonNull TextView textView,
                                        FloatingActionButton floatingActionButton,
                                        RecetaViewModel viewModel){
        textView.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_selected);
                floatingActionButton.setVisibility(View.GONE);
                viewModel.setmFieldToFocus(textView);
                Log.i("HAS FOCUS", "Input" );
            }
            else{
                textView.setBackgroundResource(R.drawable.background_square_white_bordered);
                floatingActionButton.setVisibility(View.VISIBLE);
                Log.i("NOT HAS FOCUS", "Input" );
            }
        });
    }

    public static void configFocusSelectEnd(TextView textView,
                                            float scale,
                                            Activity activity,
                                            RecetaViewModel viewModel){
        int padding_10dp = (int) (10 * scale + 0.5f);
        textView.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_selected_end);
                hideKeyboard(activity);
                viewModel.setmFieldToFocus(textView);
            }
            else{
                Log.i("PADDING", String.valueOf(padding_10dp));
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_end);
                textView.setPadding(padding_10dp, 0, padding_10dp, 0);
            }
        });
    }

    public static void configFocusInputStart(@NonNull TextView textView,
                                             float scale,
                                             FloatingActionButton floatingActionButton,
                                             RecetaViewModel viewModel){
        int padding_10dp = (int) (10 * scale + 0.5f);
        textView.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_selected_start);
                floatingActionButton.setVisibility(View.GONE);
                viewModel.setmFieldToFocus(textView);
            }
            else{
                textView.setBackgroundResource(R.drawable.background_square_white_bordered_start);
                textView.setPadding(padding_10dp, 0, padding_10dp, 0);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void configSelectConversor(@NonNull TextView textView, List<String> list, Context context, TextView tvCantidad, Ingrediente item){
        Log.i("INICIANDO", "configurando select unidad");
        textView.setOnClickListener(v -> {
            Dialog dialog;
            dialog=new Dialog(context);
            dialog.setContentView(R.layout.dialog_spinner);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(colorDrawable, 30, 30, 30, 30);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.show();
            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                UnidadDeMedida unidadOriginal = item.getUnidadDeMedida();
                double cantidadOriginal = item.getCantidad();
                textView.setText(adapter.getItem(position));
                textView.setTextColor(ContextCompat.getColor(context, R.color.primary_carbon));
                String newUnidadText = textView.getText().toString().trim();
                double newCantidad = convertirUnidad(cantidadOriginal, unidadOriginal, newUnidadText);
                tvCantidad.setText(Auxiliar.formateaCantidad(newCantidad));
                dialog.dismiss();
            });
        });
    }

    public static void configSelectConversor(@NonNull TextView textView, List<String> list, Context context, TextView tvTemperatura, ArtefactoEnUso item){
        textView.setOnClickListener(v -> {
            Dialog dialog;
            dialog=new Dialog(context);
            dialog.setContentView(R.layout.dialog_spinner);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(colorDrawable, 30, 30, 30, 30);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.show();
            ListView listView=dialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                UnidadDeMedida unidadOriginal = item.getUnidadDeTemperatura();
                double cantidadOriginal = item.getTemperatura();
                textView.setText(adapter.getItem(position));
                textView.setTextColor(ContextCompat.getColor(context, R.color.primary_carbon));
                String newUnidadText = textView.getText().toString().trim();
                double newCantidad = convertirUnidad(cantidadOriginal, unidadOriginal, newUnidadText);
                tvTemperatura.setText(Auxiliar.formateaCantidad(newCantidad));
                dialog.dismiss();
            });
        });
    }

    public static double convertirUnidad(double cantidad, UnidadDeMedida unidadOriginal, String newUnidadText) {

        UnidadDeMedida newUnidad = null;

        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if(u.getSimbolo().equals(newUnidadText)){
                newUnidad = u;
                break;
            }
        }

        if (unidadOriginal != null && newUnidad != null) {
            if(unidadOriginal.getTipoDeMedida().equals(newUnidad.getTipoDeMedida())
                    && !unidadOriginal.getTipoDeMedida().equals("temperatura")) {
                cantidad = cantidad * unidadOriginal.getCantidadDeLaUnidadDeBase();
                cantidad = cantidad / newUnidad.getCantidadDeLaUnidadDeBase();
            }
            else if(unidadOriginal.getNombre().equals("Grado/s Celsius") && newUnidad.getNombre().equals("Grado/s Farenheit")){
                cantidad =  cantidad * 1.8 + 32;
            }
            else if(unidadOriginal.getNombre().equals("Grado/s Farenheit") && newUnidad.getNombre().equals("Grado/s Celsius")){
                cantidad =  (cantidad - 32) / 1.8;
            }
            else {
                Log.e("ERROR",  "Para cambiar la unidad de medida, el tipo de medida debe coincidir....");
            }
            return cantidad;
        }
        else {
            Log.e("ERROR",  "Error en la seleccion o asignacion de unidades...");
        }
        return cantidad;
    }

    @NonNull
    public static File createTempImageFile(@NonNull Context context) throws IOException {
        Log.i("CREATE JPG FILE", "Creando archivo de imagen TEMP");
        String uniqueNameFile = UUID.randomUUID().toString();
        String imageFileName = "TEMP_" + uniqueNameFile + ".jpg";
        File directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(directorio + "/" + File.separator + imageFileName);
        if(image.createNewFile()){
            Log.i("CREATE JPG FILE", "Archivo JPG TEMP creado con éxito!");
        }
        else{
            Log.e("CREATE JPG FILE", "No se pudo crear el archivo JPG TEMP!");
        }
        return image;
    }

    public static void renombrarArchivo(@NonNull File oldfile, File newfile) {
        if (oldfile.renameTo(newfile)) {
            Log.i("RENOMBRANDO PHOTO","archivo renombrado: " + oldfile.getName() + " --> " + newfile.getName());
        } else {
            Log.e("RENOMBRANDO PHOTO","Fallo renombrado de archivo");
        }
    }

    public static String renombrarArchivo(Context context, String oldPhotoName) {
        String newPhotoName = oldPhotoName.replace("TEMP", "MisRecetapps");
        File directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File oldFile = new File(directorio + "/" + File.separator + oldPhotoName);
        File newFile = new File(directorio + "/" + File.separator + newPhotoName);
        Auxiliar.renombrarArchivo(oldFile, newFile);
        return newPhotoName;
    }

    public static void deletePhoto(Context context, String photoFileName){
        File directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File fileToDelete = new File(directorio + "/" + File.separator + photoFileName);
        Log.i("PHOTO DELETE", "BORRANDO FOTO: " + fileToDelete.getPath());
        if(fileToDelete.delete()){
            Log.i("PHOTO DELETE", "Foto eliminada con éxito: " + photoFileName);
        }
        else {
            Log.e("PHOTO DELETE", "Error al eliminar la foto: " + photoFileName);
        }
    }

    public static void deleteTemporaryPhotos(@NonNull Context context){
        File directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = directorio.listFiles();
        if (files != null) {
            for (File file : files) {
                String photoFileName = file.getName().toLowerCase(Locale.ROOT);
                if (photoFileName.contains("temp")) {
                    deletePhoto(context, photoFileName);
                }
            }
        }
    }

    public static void buttonDisabled(@NonNull ImageButton button) {
        button.setClickable(false);
        button.setEnabled(false);
        button.setImageAlpha(75);
    }

    public static void buttonEnabled(@NonNull ImageButton button) {
        button.setClickable(true);
        button.setEnabled(true);
        button.setImageAlpha(225);
    }



    @NonNull
    public static List<String> configListUnidadesByTipo(String tipoDeMedidaNombre) {
        List<String> unidadesTemp = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(tipoDeMedidaNombre)) {
                unidadesTemp.add(u.getNombre());
            }
        }
        if(!tipoDeMedidaNombre.equals(TipoDeMedida.UNIDAD.getNombre())){
            unidadesTemp.add(UnidadDeMedida.UNIDAD.getNombre());
        }
        return unidadesTemp;
    }

    @NonNull
    public static List<String> configListUnidadesArtefactoSimbolos() {
        List<String> unidades = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(TipoDeMedida.CONSUMO_ELECTRICO.getNombre()) ||
                    u.getTipoDeMedida().equals(TipoDeMedida.CONSUMO_GAS.getNombre())) {
                unidades.add(u.getSimbolo());
            }
        }
        return unidades;
    }

    @NonNull
    public static List<String> configListUnidadesSimbolo(String tipoDeMedidaNombre) {
        List<String> unidadesTemp = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(tipoDeMedidaNombre)) {
                unidadesTemp.add(u.getSimbolo());
            }
        }
        return unidadesTemp;
    }

    @NonNull
    public static List<String> configListUnidadDeProducto() {
        List<String> unidadesDeProducto = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(TipoDeMedida.MASA.getNombre()) ||
                    u.getTipoDeMedida().equals(TipoDeMedida.CAPACIDAD.getNombre()) ||
                    u.getTipoDeMedida().equals(TipoDeMedida.UNIDAD.getNombre())) {
                unidadesDeProducto.add(u.getNombre());
            }
        }
        return unidadesDeProducto;
    }

    @NonNull
    public static List<String> configListUnidadDeProductoSimbolo() {
        List<String> unidadesDeProducto = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(TipoDeMedida.MASA.getNombre()) ||
                    u.getTipoDeMedida().equals(TipoDeMedida.CAPACIDAD.getNombre()) ||
                    u.getTipoDeMedida().equals(TipoDeMedida.UNIDAD.getNombre())) {
                unidadesDeProducto.add(u.getSimbolo());
            }
        }
        return unidadesDeProducto;
    }

    @NonNull
    public static List<String> configListUnidadDeTemperatura() {
        List<String> unidadesDeTemperatura = new ArrayList<>();
        for (UnidadDeMedida u : UnidadDeMedida.values()) {
            if (u.getTipoDeMedida().equals(TipoDeMedida.TEMPERATURA.getNombre())) {
                unidadesDeTemperatura.add(u.getSimbolo());
            }
        }
        return unidadesDeTemperatura;
    }

    public static long getProductoIdByNombre(String nombre, @NonNull List<Producto> list) {
        long id = -1;
        for(Producto p : list){
            if(p.getNombre().equals(nombre)){
                id = p.getId();
                break;
            }
        }
        return id;
    }

    public static String getProductoNombreById(long id, @NonNull List<Producto> list) {
        String nombre = "";
        for(Producto p : list){
            if(id == p.getId()){
                nombre = p.getNombre();
                break;
            }
        }
        return nombre;
    }

    public static String formateaPrecio(double precio) {
        if(precio==0) {
            return "0,00";
        }
        else {
            DecimalFormat formato = new DecimalFormat("###,##0.00", new DecimalFormatSymbols(localeToFormat));
            return formato.format(precio);
        }
    }

    public static String formateaCantidad(double cantidad) {
        if(cantidad==0) {
            return "0";
        }
        else {
            DecimalFormat formato = new DecimalFormat("0.##", new DecimalFormatSymbols(localeToFormat));
            return formato.format(cantidad);
        }
    }

    public static Double formatNumberToDouble(String formatNumber) {
        Double d = Double.valueOf(0);
        NumberFormat format = NumberFormat.getInstance(localeToFormat);
        Number number;
        if (formatNumber != null) {
            if(!formatNumber.isEmpty()){
                try {
                    number = format.parse(formatNumber);
                    if (number != null) {
                        d = number.doubleValue();
                    }
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
        return d;
    }

    public static String queryName(@NonNull ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public static long getFileSize(@NonNull ContentResolver resolver, Uri fileUri) {
        Cursor returnCursor = resolver.
                query(fileUri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        long size = returnCursor.getLong(sizeIndex);
        returnCursor.close();

        return size;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
