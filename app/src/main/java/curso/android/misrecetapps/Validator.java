package curso.android.misrecetapps;

import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

import curso.android.misrecetapps.controllers.artefacto.ArtefactoViewModel;
import curso.android.misrecetapps.controllers.producto.ProductoViewModel;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.database.RecetaDB;

public final class Validator {

    public static boolean isNumericValid(@NonNull AppCompatEditText input, String errorMessage){
        boolean isValid = false;
        if(input.getText() != null){
            if(!input.getText().toString().trim().isEmpty()){
                Log.i("VALIDANDO NUMERIC", input.getText().toString().trim());
                if(isNumeric(input.getText().toString().trim())){
                    input.setError(null);
                    Log.i("VALIDANDO NUMERIC", "¡¡Valido!!");
                    isValid = true;
                }
                else{
                    Log.e("VALIDANDO NUMERIC", "Error: No es numero");
                    input.setError(errorMessage);
                    //input.requestFocus();
                }
            }
            else{
                Log.e("VALIDANDO NUMERIC", "Error: Esta vacio");
                input.setError(errorMessage);
                //input.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO NUMERIC", "Error: Es nulo");
            input.setError(errorMessage);
            //input.requestFocus();
        }
        return isValid;
    }

    public static boolean isNumericValid(@NonNull TextInputEditText input, String errorMessage){
        boolean isValid = false;
        if(input.getText() != null){
            if(!input.getText().toString().trim().isEmpty()){
                if(isNumeric(input.getText().toString().trim())){
                    input.setError(null);
                    Log.i("VALIDANDO NUMERIC", "¡¡Valido!!");
                    isValid = true;
                }
                else{
                    Log.e("VALIDANDO NUMERIC", "Error: No es numero");
                    input.setError(errorMessage);
                    //input.requestFocus();
                }
            }
            else{
                Log.e("VALIDANDO NUMERIC", "Error: Esta vacio");
                input.setError(errorMessage);
                //input.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO NUMERIC", "Error: Es nulo");
            input.setError(errorMessage);
            //input.requestFocus();
        }
        return isValid;
    }

    public static boolean isSelectValid(@NonNull TextView select, List<String> list,  String errorMessage){
        boolean isValid = false;
        if(select.getText() != null){
            if(!select.getText().toString().trim().isEmpty()){
                if(list.contains(select.getText().toString().trim())){
                    select.setError(null);
                    Log.i("VALIDANDO SELECT", "¡¡Valido!!");
                    isValid = true;
                }
                else{
                    Log.e("VALIDANDO SELECT", "Error: No contenido en listado de opciones");
                    select.setError(errorMessage);
                    select.requestFocus();
                }
            }
            else{
                Log.e("VALIDANDO SELECT", "Error: Está vacío");
                select.setError(errorMessage);
                select.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO SELECT", "Es nulo");
            select.setError(errorMessage);
            select.requestFocus();
        }
        return isValid;
    }

    public static boolean isNumeric(@NonNull String str) {
        Double number = Auxiliar.formatNumberToDouble(str);
        return number != null;
    }

    public static boolean isNombreValid(@NonNull TextInputEditText input, RecetaViewModel mViewModel, String errorMessage){
        boolean isValid = false;
        if(input.getText() != null){
            String text = input.getText().toString().trim();
            if(!text.isEmpty()){
                if(mViewModel.getReceta().getId() > 0){
                    Log.i("VALIDANDO NOMBRE", "¡¡Valido!! Edicion de receta...");
                    input.setError(null);
                    isValid = true;
                }
                else{
                    RecetaDB recetaDB = new RecetaDB();
                    if(!recetaDB.existsByNombre(text)){
                        Log.i("VALIDANDO NOMBRE", "¡¡Valido!!");
                        input.setError(null);
                        isValid = true;
                    }
                    else{
                        Log.e("VALIDANDO NOMBRE", "Error: Ya existe una receta con ese nombre");
                        input.setError(errorMessage);
                        input.requestFocus();
                    }
                }
            }
            else{
                Log.e("VALIDANDO NOMBRE", "Error: Esta vacio");
                input.setError(errorMessage);
                input.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO NOMBRE", "Error: Es nulo");
            input.setError(errorMessage);
            input.requestFocus();
        }
        return isValid;
    }


    public static boolean isNombreValid(@NonNull TextInputEditText input, ProductoViewModel mViewModel, String errorMessage){
        boolean isValid = false;
        if(input.getText() != null){
            String text = input.getText().toString().trim();
            if(!text.isEmpty()){
                if(mViewModel.getmProducto().getId() > 0){
                    Log.i("VALIDANDO NOMBRE", "¡¡Valido!! Edicion de receta...");
                    input.setError(null);
                    isValid = true;
                }
                else{
                    ProductoDB productoDB = new ProductoDB();
                    if(!productoDB.existsByNombre(text)){
                        Log.i("VALIDANDO NOMBRE", "¡¡Valido!!");
                        input.setError(null);
                        isValid = true;
                    }
                    else{
                        Log.e("VALIDANDO NOMBRE", "Error: Ya existe una receta con ese nombre");
                        input.setError(errorMessage);
                        input.requestFocus();
                    }
                }
            }
            else{
                Log.e("VALIDANDO NOMBRE", "Error: Esta vacio");
                input.setError(errorMessage);
                input.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO NOMBRE", "Error: Es nulo");
            input.setError(errorMessage);
            input.requestFocus();
        }
        return isValid;
    }

    public static boolean isNombreValid(@NonNull TextInputEditText input, ArtefactoViewModel mViewModel, String errorMessage){
        boolean isValid = false;
        if(input.getText() != null){
            String text = input.getText().toString().trim();
            if(!text.isEmpty()){
                if(mViewModel.getmArtefacto().getId() > 0){
                    Log.i("VALIDANDO NOMBRE", "¡¡Valido!! Edicion de receta...");
                    input.setError(null);
                    isValid = true;
                }
                else{
                    ArtefactoDB artefactoDB = new ArtefactoDB();
                    if(!artefactoDB.existsByNombre(text)){
                        Log.i("VALIDANDO NOMBRE", "¡¡Valido!!");
                        input.setError(null);
                        isValid = true;
                    }
                    else{
                        Log.e("VALIDANDO NOMBRE", "Error: Ya existe una receta con ese nombre");
                        input.setError(errorMessage);
                        input.requestFocus();
                    }
                }
            }
            else{
                Log.e("VALIDANDO NOMBRE", "Error: Esta vacio");
                input.setError(errorMessage);
                input.requestFocus();
            }
        }
        else{
            Log.e("VALIDANDO NOMBRE", "Error: Es nulo");
            input.setError(errorMessage);
            input.requestFocus();
        }
        return isValid;
    }

    public static boolean radioButtonChecked(RadioButton intensidad,
                                             RadioButton temperatura,
                                             String errorMessage) {
        boolean isValid = false;
        if(intensidad.isChecked() || temperatura.isChecked()){
            temperatura.setError(null);
            isValid = true;
        }
        else{
            temperatura.setError(errorMessage);
            temperatura.requestFocus();
        }
        return isValid;

    }

}
