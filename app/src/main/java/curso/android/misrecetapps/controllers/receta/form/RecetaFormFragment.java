package curso.android.misrecetapps.controllers.receta.form;


import android.os.Bundle;
import android.os.Environment;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.Validator;
import curso.android.misrecetapps.activities.PhotoTaker;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.ArtefactoEnUsoDB;
import curso.android.misrecetapps.database.IngredienteDB;
import curso.android.misrecetapps.database.InstruccionDB;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.databinding.FragmentRecipeFormBinding;
import curso.android.misrecetapps.databinding.SelectPhotoSourceBinding;
import curso.android.misrecetapps.model.Artefacto;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Receta;

public class RecetaFormFragment extends Fragment {

    private FragmentRecipeFormBinding binding;
    private SelectPhotoSourceBinding bindingPhotoSource;
    private SpeechRecognizer mSpeechRecognizer;
    private PhotoTaker mPhotoTaker;
    private RecyclerView ingredientesReciclerView;
    private RecyclerView artefactosReciclerView;
    private RecyclerView instruccionesReciclerView;
    private RecetaFormArtefactoAdapter artefactoAdapter;
    private RecetaFormIngredienteAdapter ingredienteAdapter;
    private RecetaFormInstruccionAdapter instruccionAdapter;
    private RecetaViewModel mViewModel;
    private RecetaDB mRecetaDB;
    private File mDirectorio;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        Log.i("FORM", "Create View");

        binding = FragmentRecipeFormBinding.inflate(inflater, container, false);
        bindingPhotoSource = SelectPhotoSourceBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(RecetaViewModel.class);
        mPhotoTaker = new PhotoTaker(requireActivity().getActivityResultRegistry(), getContext(), mViewModel);
        getLifecycle().addObserver(mPhotoTaker);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        mRecetaDB = new RecetaDB();
        mViewModel.setActiveFragment(mViewModel.ACTIVE_FRAGMENT_FORM);
        mDirectorio = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        configBackPressed();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d("FORM", "onViewCreated!");
        super.onViewCreated(view, savedInstanceState);
        if(mViewModel.getReceta() == null) { mViewModel.inicializaReceta(); }

        configReceta();

        configCardInstrucciones();
        configCardArtefactos();
        configCardIngredientes();

        configAddIngredienteButton();
        configAddInstruccionButton();
        configAddArtefactoButton();

        configSaveReceta();

    }

    @Override
    public void onResume() {
        Log.i("ON RESUME", "ON RESUME!!!!!!!!!!");
        super.onResume();

        setNextFocus(binding.recetaFormNombre, binding.recetaFormPorciones);
        setNextFocus(binding.recetaFormPorciones, ingredientesReciclerView);
        Auxiliar.configFocusInput(binding.recetaFormNombre, binding.fabCheck, mViewModel);
        Auxiliar.configFocusInput(binding.recetaFormPorciones, binding.fabCheck, mViewModel);

        if(mViewModel.getmFieldToFocus() != null){
            Log.i("ON RESUME", "fielToFocus not null");
            if(mViewModel.getmFieldToFocus().getId() == binding.recetaFormNombre.getId()){
                binding.recetaFormNombre.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == binding.recetaFormPorciones.getId()){
                binding.recetaFormPorciones.requestFocus();
            }
        }
        else {
            binding.recetaFormNombre.requestFocus();
            binding.recetaFormNombre.clearFocus();
            mViewModel.setmFieldToFocus(null);
            Log.i("ON RESUME", "Clear focus, because fielToFocus NULL");
        }

        binding.recetaFormLayout.setOnClickListener(view -> {
            binding.recetaFormNombre.requestFocus();
            binding.recetaFormNombre.clearFocus();
            Auxiliar.hideKeyboard(getActivity());
            mViewModel.setmFieldToFocus(null);
        });

    }



    @Override
    public void onDestroyView() {
        Log.d("FORM", "onDestroyView: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(mViewModel.getActiveFragment() != mViewModel.ACTIVE_FRAGMENT_FORM){
            Auxiliar.deleteTemporaryPhotos(getContext());
        }
        if(mViewModel.getActiveFragment() == mViewModel.ACTIVE_FRAGMENT_FORM ||
                mViewModel.getActiveFragment() == mViewModel.ACTIVE_FRAGMENT_DETAILS){
            conservaDatos(false);
        }
        else{
            mViewModel.clearAll();
        }
        super.onDestroyView();
        mPhotoTaker.clear();
        binding = null;
    }

    public void configBackPressed(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(mViewModel.getReceta() == null || mViewModel.getReceta().getId() == 0){
                    NavHostFragment
                            .findNavController(RecetaFormFragment.this)
                            .navigate(R.id.action_RecetaForm_to_RecipesList);
                }
                else{
                    NavHostFragment
                            .findNavController(RecetaFormFragment.this)
                            .navigate(R.id.action_RecetaForm_to_RecetaDetails);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void configReceta() {

        if(mViewModel.getReceta().getNombre() != null){
            if(!mViewModel.getReceta().getNombre().isEmpty()){
                binding.recetaFormNombre.setText(mViewModel.getReceta().getNombre());
            }
        }
        else{
            binding.recetaFormNombre.setText(null);
        }

        if(mViewModel.getReceta().getPorciones() > 0){
            binding.recetaFormPorciones.setText(Auxiliar.formateaCantidad(mViewModel.getReceta().getPorciones()));
        }
        else{
            binding.recetaFormPorciones.setText(null);
        }

        if(mViewModel.getReceta().getFoto() != null){
            Log.i("FORM", "Cargando foto: " + mViewModel.getReceta().getFoto());
            loadPhoto();
            configDeletePhotoButton();
        }
        else{
            binding.photoPrincipalButton.setImageResource(R.drawable.ic_camera_20);
            if(binding.photoPrincipalCardview.getVisibility() == View.VISIBLE){
                binding.photoPrincipalCardview.setVisibility(View.GONE);
            }
            binding.photoPrincipalImageview.setImageDrawable(null);
            configTakePhotoButton();
        }

    }

    private void configTakePhotoButton() {

        View popupView = bindingPhotoSource.getRoot();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        binding.photoPrincipalButton.setOnClickListener(view -> {
            mViewModel.setmFragmentTemp(this);
            mViewModel.setmPhotoOriginTemp(PhotoTaker.PHOTO_LOCATED_IN_PRINCIPAL);
            conservaDatos(false);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        });

        bindingPhotoSource.getRoot().setOnClickListener(view -> popupWindow.dismiss());

        bindingPhotoSource.takePicture.setOnClickListener(view -> {
            popupWindow.dismiss();
            mPhotoTaker.takePicture();
        });

        bindingPhotoSource.importPhoto.setOnClickListener(view -> {
            popupWindow.dismiss();
            mPhotoTaker.importPhoto();
        });

    }

    private void configDeletePhotoButton() {
        binding.photoPrincipalButton.setImageResource(R.drawable.ic_image_remove_outline_20);
        binding.photoPrincipalButton.setOnClickListener(view -> {

            String photoFileName = mViewModel.getReceta().getFoto();
            Auxiliar.deletePhoto(getContext(), photoFileName);
            mViewModel.getReceta().setFoto(null);
            binding.photoPrincipalButton.setImageResource(R.drawable.ic_camera_20);
            if(binding.photoPrincipalCardview.getVisibility() == View.VISIBLE){
                binding.photoPrincipalCardview.setVisibility(View.GONE);
            }
            configTakePhotoButton();

        });
    }

    public void loadPhoto() {
        String photoName = mViewModel.getReceta().getFoto();
        File photoFile = new File(mDirectorio + "/" + File.separator + photoName);
        Log.i("FORM", "Cargando foto desde path: " + photoName);
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        Glide.with(getContext())
                .load(photoFile)
                .apply(options)
                .into(binding.photoPrincipalImageview);
        if(binding.photoPrincipalCardview.getVisibility() == View.GONE){
            binding.photoPrincipalCardview.setVisibility(View.VISIBLE);
        }
    }

    private void configCardIngredientes() {
        ingredienteAdapter =
                new RecetaFormIngredienteAdapter(
                        mViewModel,
                        getActivity(),
                        artefactosReciclerView,
                        binding.fabCheck);
        ingredientesReciclerView = binding.recetaIngredientesCardsContainer;
        ingredientesReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        ingredientesReciclerView.setAdapter(ingredienteAdapter);
        ingredientesReciclerView.setNestedScrollingEnabled(false);
    }

    private void configCardArtefactos() {
        artefactoAdapter = new RecetaFormArtefactoAdapter(mViewModel, getActivity(), instruccionesReciclerView, binding.fabCheck);
        artefactosReciclerView = binding.recetaArtefactosCardsContainer;
        artefactosReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        artefactosReciclerView.setAdapter(artefactoAdapter);
        artefactosReciclerView.setNestedScrollingEnabled(false);
    }

    private void configCardInstrucciones() {
        instruccionAdapter = new RecetaFormInstruccionAdapter(mViewModel, mSpeechRecognizer,
                mPhotoTaker, getActivity(), binding.fabCheck, bindingPhotoSource);
        instruccionesReciclerView = binding.recetaInstruccionesCardsContainer;
        instruccionesReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        instruccionesReciclerView.setAdapter(instruccionAdapter);
        instruccionesReciclerView.setNestedScrollingEnabled(false);
    }

    private void configAddIngredienteButton() {
        binding.recetaFormAddIngredienteButton.setOnClickListener(view -> {
            ingredienteAdapter.conservaIngredientes();
            mViewModel.getReceta().getIngredientes().add(new Ingrediente());
            ingredienteAdapter.notifyDataSetChanged();
        });
    }

    private void configAddArtefactoButton() {
        binding.recetaFormAddArtefactoButton.setOnClickListener(view -> {
            artefactoAdapter.conservaArtefactos(false);
            mViewModel.getReceta().getArtefactosEnUso().add(new ArtefactoEnUso());
            artefactoAdapter.notifyDataSetChanged();
        });
    }

    private void configAddInstruccionButton() {
        binding.recetaFormAddInstruccionButton.setOnClickListener(view -> {
            instruccionAdapter.conservaInstrucciones(false);
            mViewModel.getReceta().getInstrucciones().add(new Instruccion());
            instruccionAdapter.notifyDataSetChanged();

        });
    }

    private void configSaveReceta() {
        binding.fabCheck.setOnClickListener(view -> {
            if(validateFields()){

                String oldPhotoName = mViewModel.getReceta().getFoto();
                if(oldPhotoName != null && !oldPhotoName.isEmpty()){
                    String newPhotoName = oldPhotoName.replace("TEMP", "PRINCIPAL");
                    File oldFile = new File(mDirectorio + "/" + File.separator + oldPhotoName);
                    File newFile = new File(mDirectorio + "/" + File.separator + newPhotoName);
                    Auxiliar.renombrarArchivo(oldFile, newFile);
                    mViewModel.getReceta().setFoto(newPhotoName);
                }
                conservaDatos(true);
                saveRecetaEnDB(mViewModel.getReceta());
                String recetaNombre = mViewModel.getReceta().getNombre();
                mViewModel.setReceta(mRecetaDB.findByNombreComplete(recetaNombre));
                NavHostFragment.findNavController(RecetaFormFragment.this)
                        .navigate(R.id.action_RecetaForm_to_RecetaDetails);
            }
        });
    }

    public void saveRecetaEnDB(Receta receta) {

        Log.i("FORM", "Guardando receta...");

        IngredienteDB ingredienteDB = new IngredienteDB();
        ArtefactoEnUsoDB artefactoEnUsoDB = new ArtefactoEnUsoDB();
        InstruccionDB instruccionDB = new InstruccionDB();

        try {
            if(mViewModel.getReceta().getId() > 0){
                Log.e("DELETE RECETA DB", "Eliminando: " + mViewModel.getReceta().getNombre());
                mRecetaDB.delete(mViewModel.getReceta().getId());
            }
            receta.setId(0);
            mRecetaDB.save(receta);
            ingredienteDB.saveList(receta.getIngredientes(), receta);
            artefactoEnUsoDB.saveList(receta.getArtefactosEnUso(), receta);
            instruccionDB.saveList(receta.getInstrucciones(), receta);

            String msg = mViewModel.getReceta().getId() > 0 ? "Receta editada con éxito." : "Receta guardada con éxito.";
            Log.i("SAVE-RECETA", msg);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SAVE-RECETA", "Error al guardar receta en la BASE DE DATOS.");
        }
    }

    public void conservaDatos(boolean isSaving) {

        Log.i("CONSERVA-RECETA", "Conservando datos temporales de Receta...");

        if(mViewModel.getReceta() != null){

            if(binding.recetaFormNombre.getText() != null){
                 mViewModel.getReceta().setNombre(binding.recetaFormNombre.getText().toString().trim());
            }
            else{
                mViewModel.getReceta().setNombre(null);
            }

            if(binding.recetaFormPorciones.getText() != null){
                String porciones = binding.recetaFormPorciones.getText().toString().trim();
                if(Validator.isNumeric(porciones)){
                    mViewModel.getReceta().setPorciones(Double.parseDouble(porciones));
                }
            }
            else{
                mViewModel.getReceta().setPorciones(0);
            }
            instruccionAdapter.conservaInstrucciones(isSaving);
            ingredienteAdapter.conservaIngredientes();
            artefactoAdapter.conservaArtefactos(isSaving);

        }
    }

    public boolean validateFields() {

        Log.i("VALIDANDO", "Validando casilleros...");

        boolean isValid = true;

        for(int i = 0; i < artefactoAdapter.getList().size(); i++){

            RecetaFormArtefactoAdapter.ViewHolder holder =
                    (RecetaFormArtefactoAdapter.ViewHolder) artefactosReciclerView
                            .findViewHolderForAdapterPosition(i);

            TextView artefactoEnUso = null;
            AppCompatEditText minutos = null;
            TextView intensidad = null;
            AppCompatEditText temperatura = null;
            TextView unidad = null;
            
            if(holder != null){
                artefactoEnUso = holder.getSelectArtefacto();
                minutos = holder.getMinutos();
                intensidad = holder.getSelectIntensidad();
                temperatura = holder.getTemperatura();
                unidad = holder.getUnidad();
            }

            Artefacto artefacto;
            
            boolean isValidArtefacto;
            boolean isValidMinutos = false;
            boolean isValidTemperatura = false;
            boolean isValidUnidad = false;
            boolean isValidIntensidad = false;
            boolean esHorno = false;

            if (!artefactoAdapter.artefactoEnBlanco(artefactoEnUso)) {
                isValidArtefacto = Validator.isSelectValid(artefactoEnUso, new ArtefactoDB().findAllNames(),
                        getString(R.string.error_receta_artefacto));

                if (isValidArtefacto) {
                    artefacto = new ArtefactoDB().findByNombre(artefactoEnUso.getText().toString().trim());
                    isValidMinutos = Validator.isNumericValid(minutos, getString(R.string.error_receta_minutos));
                    esHorno = artefacto.isEsHorno();
                    if (esHorno) {
                        isValidTemperatura = Validator.isNumericValid(temperatura, getString(R.string.error_input_temperatura));
                        isValidUnidad = Validator.isSelectValid(unidad, artefactoAdapter.getListUnidades(), getString(R.string.error_receta_seleccionar_opcion));
                    } else {
                        isValidIntensidad = Validator.isSelectValid(intensidad, artefactoAdapter.getListIntensidades(), getString(R.string.error_receta_seleccionar_opcion));
                    }
                }
                if (esHorno) {
                    if (!isValidArtefacto || !isValidMinutos || !isValidTemperatura || !isValidUnidad) {
                        isValid = false;
                    }
                } else {
                    if (!isValidArtefacto || !isValidMinutos || !isValidIntensidad) {
                        isValid = false;
                    }
                }
            }
        }


        for(int i = 0; i < ingredienteAdapter.getList().size(); i++){
            RecetaFormIngredienteAdapter.ViewHolder holder =
                    (RecetaFormIngredienteAdapter.ViewHolder) ingredientesReciclerView
                            .findViewHolderForAdapterPosition(i);
            TextView selectProducto = null;
            AppCompatEditText cantidad = null;
            TextView selectUnidad = null;
            if(holder != null){
                selectProducto = holder.getSelectProducto();
                cantidad = holder.getCantidad();
                selectUnidad = holder.getSelectUnidad();
            }
            boolean isValidProducto;
            boolean isValidCantidad;
            boolean isValidUnidad;
            isValidProducto = Validator.isSelectValid(selectProducto, new ProductoDB().findAllNames(),
                    getString(R.string.error_receta_producto));

            isValidCantidad = Validator.isNumericValid(cantidad, getString(R.string.error_receta_cantidad));

            isValidUnidad = Validator.isSelectValid(selectUnidad, ingredienteAdapter.getListUnidades(),
                    getString(R.string.error_receta_unidad_producto));

            if(!isValidProducto || !isValidCantidad || !isValidUnidad){
                isValid = false;
            }
        }

        TextInputEditText porciones = binding.recetaFormPorciones;
        TextInputEditText nombre = binding.recetaFormNombre;

        boolean isValidPorciones;
        boolean isValidNombre;

        isValidPorciones = Validator.isNumericValid(porciones, getString(R.string.error_receta_porciones));
        isValidNombre = Validator.isNombreValid(nombre, mViewModel, getString(R.string.error_receta_nombre));

        if(!isValidNombre || !isValidPorciones){
            isValid = false;
        }
        return isValid;
    }

    public void setNextFocus(TextView currentField, TextView nextField){
        currentField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            Log.i("setNextFocus", "Automatico...");
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result) {
                case EditorInfo.IME_ACTION_DONE:
                    currentField.clearFocus();
                    nextField.requestFocus();
                    break;
            }
            return false;
        });
    }

    public void setNextFocus(@NonNull TextView currentField, RecyclerView recyclerView){
        currentField.setOnEditorActionListener((textView, actionId, keyEvent) -> {

            RecetaFormIngredienteAdapter.ViewHolder holder =
                    (RecetaFormIngredienteAdapter.ViewHolder) recyclerView
                            .findViewHolderForAdapterPosition(0);

            TextView nextField = holder.getSelectProducto();

            if(holder != null){
                Log.i("HOLDER", "Not null");
            }
            else{
                Log.i("HOLDER", "Null");
            }

            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result) {
                case EditorInfo.IME_ACTION_DONE:
                    currentField.clearFocus();
                    nextField.requestFocus();
                    break;
            }
            return false;
        });
    }
}