package curso.android.misrecetapps.controllers.artefacto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.Validator;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.databinding.FragmentArtefactoFormBinding;
import curso.android.misrecetapps.model.Artefacto;

public class ArtefactoFormFragment extends Fragment {

    //ATRIBUTOS
    private ArtefactoViewModel mViewModel;
    private ArtefactoDB mArtefactoDB;
    private FragmentArtefactoFormBinding binding;
    private List<String> mUnidades;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentArtefactoFormBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ArtefactoViewModel.class);
        mArtefactoDB = new ArtefactoDB();
        mUnidades = Auxiliar.configListUnidadesArtefactoSimbolos();
        if(mViewModel.getmArtefacto() == null) { mViewModel.setmArtefacto(new Artefacto()); }
        configSave();

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.d("DESTROYVIEW", "onDestroyView: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        conservaDatos();
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Artefacto artefacto = mViewModel.getmArtefacto();
        if(artefacto.getNombre() != null){
            binding.artefactoFormNombre.setText(artefacto.getNombre());
        }
        if(artefacto.getId() > 0 && artefacto.getConsumoEnergetico() >= 0){
            binding.artefactoInputCantidad.setText(Auxiliar.formateaCantidad(artefacto.getConsumoEnergetico()));
        }
        Auxiliar.configSelect(binding.artefactoUnidad, mUnidades, getContext());
        if(artefacto.getUnidadDeConsumo() != null){
            binding.artefactoUnidad.setText(artefacto.getUnidadDeConsumo().getSimbolo());
        }
        if(artefacto.getId() > 0){
            if(artefacto.isEsHorno()){
                binding.medidaTemperatura.setChecked(true);
            }
            else{
                binding.medidaIntensidad.setChecked(true);
            }
        }
    }

    private void configSave() {
        binding.fabCheck.setOnClickListener(view -> {
            if(validateFields()){
                conservaDatos();
                saveEnDB(mViewModel.getmArtefacto());
                String artefactoNombre = mViewModel.getmArtefacto().getNombre();
                mViewModel.setmArtefacto(mArtefactoDB.findByNombre(artefactoNombre));
                NavHostFragment.findNavController(ArtefactoFormFragment.this)
                        .navigate(R.id.action_ArtefactoForm_to_ArtefactoList);
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
}