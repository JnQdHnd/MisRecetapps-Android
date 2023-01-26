package curso.android.misrecetapps.controllers.artefacto;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.Validator;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.databinding.FragmentArtefactoFormBinding;
import curso.android.misrecetapps.model.Artefacto;

public class ArtefactoFormDialog extends DialogFragment {

    private ArtefactoViewModel mViewModel;
    private ArtefactoDB mArtefactoDB;
    private FragmentArtefactoFormBinding binding;
    private List<String> mUnidades;
    private TextView mTextView;
    private Dialog mParentDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(ArtefactoViewModel.class);
        mArtefactoDB = new ArtefactoDB();
        mUnidades = Auxiliar.configListUnidadesArtefactoSimbolos();
        if(mViewModel.getmArtefacto() == null) { mViewModel.setmArtefacto(new Artefacto()); }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_artefacto_form);
        int w = ViewGroup.LayoutParams.MATCH_PARENT;
        int h = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(w,h);
        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(colorDrawable, 30, 30, 30, 30);
        dialog.getWindow().setBackgroundDrawable(inset);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentArtefactoFormBinding.inflate(inflater, container, false);
        binding.scrollView.setBackgroundResource(R.drawable.background_square_white_bordered);
        Auxiliar.configSelect(binding.artefactoUnidad, mUnidades, getContext());
        configSave();
        return binding.getRoot();
    }

    private void configSave() {
        binding.fabCheck.setOnClickListener(view -> {
            if(validateFields()){
                conservaDatos();
                saveEnDB(mViewModel.getmArtefacto());
                mTextView.setText(mViewModel.getmArtefacto().getNombre());
                mViewModel.clearAll();
                mParentDialog.dismiss();
                dismiss();
            }
        });
    }

    private void conservaDatos() {

        if(mViewModel.getmArtefacto() == null){
            mViewModel.setmArtefacto(new Artefacto());
        }

        if(binding.artefactoFormNombre.getText() != null){
            String nombre = binding.artefactoFormNombre.getText().toString().trim();
            mViewModel.getmArtefacto().setNombre(nombre);
        }
        if(binding.artefactoInputCantidad.getText() != null){
            String cantidad = binding.artefactoInputCantidad.getText().toString().trim();
            if(Validator.isNumeric(cantidad)){
                mViewModel.getmArtefacto().setConsumoEnergetico(Auxiliar.formatNumberToDouble(cantidad));
            }
        }
        if(binding.artefactoUnidad.getText() != null){
            String unidad = binding.artefactoUnidad.getText().toString().trim();
            mViewModel.getmArtefacto().setUnidadDeConsumoString(unidad);
        }
        if(binding.medidaIntensidad.isChecked()){
            mViewModel.getmArtefacto().setEsHorno(false);
        }
        else if(binding.medidaTemperatura.isChecked()){
            mViewModel.getmArtefacto().setEsHorno(true);
        }
    }

    public boolean validateFields() {

        Log.i("VALIDANDO ARTEFACTO", "Validando casilleros...");

        boolean isValid = true;

        boolean isValidNombre;
        boolean isValidCantidad;
        boolean isValidUnidad;
        boolean isValidRadioButton;

        TextInputEditText nombre = binding.artefactoFormNombre;
        AppCompatEditText cantidad = binding.artefactoInputCantidad;
        TextView unidad = binding.artefactoUnidad;
        RadioButton intensidad = binding.medidaIntensidad;
        RadioButton temperatura = binding.medidaTemperatura;

        isValidNombre = Validator.isNombreValid(nombre, mViewModel, getString(R.string.error_nombre_existe));
        isValidCantidad = Validator.isNumericValid(cantidad, getString(R.string.error_artefacto_cantidad));
        isValidUnidad = Validator.isSelectValid(unidad, mUnidades, getString(R.string.error_unidad));
        isValidRadioButton = Validator.radioButtonChecked(intensidad, temperatura, getString(R.string.error_radio_button));

        if(!isValidNombre || !isValidCantidad || !isValidUnidad || !isValidRadioButton){
            isValid = false;
        }

        return isValid;

    }

    public void saveEnDB(Artefacto artefacto){

        Log.i("SAVING-RECETA", "Guardando artefacto...");

        try {
            if(mViewModel.getmArtefacto().getId() > 0){
                Log.e("DELETE ARTEFACTO DB", "Eliminando: " + mViewModel.getmArtefacto().getNombre());
                mArtefactoDB.delete(mViewModel.getmArtefacto().getId());
            }
            artefacto.setId(0);
            mArtefactoDB.save(artefacto);

            String msg = mViewModel.getmArtefacto().getId() > 0 ? "Receta editada con éxito." : "Receta guardada con éxito.";
            Log.i("SAVE ARTEFACTO", msg);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SAVE ARTEFACTO", "Error al guardar ARTEFACTO en la BASE DE DATOS.");
        }
    }


    public void setmTextView(TextView mTextView) {
        this.mTextView = mTextView;
    }

    public void setmParentDialog(Dialog mParentDialog) {
        this.mParentDialog = mParentDialog;
    }
}
