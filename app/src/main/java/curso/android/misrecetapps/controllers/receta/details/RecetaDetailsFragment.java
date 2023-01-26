package curso.android.misrecetapps.controllers.receta.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.Validator;
import curso.android.misrecetapps.activities.ExportToJson;
import curso.android.misrecetapps.activities.ExportToPdf;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.databinding.CargaCompraBinding;
import curso.android.misrecetapps.databinding.FragmentRecipeDetailsBinding;
import curso.android.misrecetapps.databinding.InfoLecturaDeRecetaBinding;
import curso.android.misrecetapps.databinding.SelectSendFormatBinding;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Receta;

public class RecetaDetailsFragment extends Fragment {

    private FragmentRecipeDetailsBinding binding;
    private CargaCompraBinding bindingCompra;
    private SelectSendFormatBinding bindingSendFile;
    private InfoLecturaDeRecetaBinding bindingInfoLectura;
    private SpeechRecognizer mSpeechRecognizer;
    private TextToSpeech mTextToSpeech;
    private RecyclerView ingredientesReciclerView;
    private RecyclerView artefactosReciclerView;
    private RecyclerView instruccionesReciclerView;
    private RecyclerView compraRecyclerView;
    private RecetaDetailsIngredientesAdapter ingredientesAdapter;
    private RecetaDetailsArtefactosAdapter artefactosAdapter;
    private RecetaDetailsInstruccionesAdapter instruccionesAdapter;
    private RecetaDetailsCompraAdapter compraAdapter;
    private RecetaDB mRecetaDB;
    private ProductoDB mProductoDB;
    private RecetaViewModel mViewModel;
    private Receta mReceta;
    private List<Producto> mProductosEnReceta;
    private boolean mOptionsBarVisible = false;
    private AudioManager amanager;
    private ExportToPdf mPdfExporter;
    private ExportToJson mJsonExporter;
    private int indexSpeech = -100;
    private String tituloSpeech;
    private String textoSpeech;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false);
        bindingCompra = CargaCompraBinding.inflate(inflater, container,false);
        bindingInfoLectura = InfoLecturaDeRecetaBinding.inflate(inflater, container,false);
        bindingSendFile = SelectSendFormatBinding.inflate(inflater, container,false);
        mViewModel = new ViewModelProvider(requireActivity()).get(RecetaViewModel.class);
        mRecetaDB = new RecetaDB();
        mProductoDB = new ProductoDB();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        amanager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if(mViewModel.getProductos() == null){mViewModel.setProductos(new ArrayList<>());}
        mProductosEnReceta = mViewModel.getProductos();
        mViewModel.setReceta(mRecetaDB.findByNombreComplete(mViewModel.getReceta().getNombre()));
        mReceta = mViewModel.getReceta();
        mPdfExporter = new ExportToPdf(
                requireActivity().getActivityResultRegistry(), getContext(), mViewModel);
        getLifecycle().addObserver(mPdfExporter);
        mJsonExporter = new ExportToJson(
                requireActivity().getActivityResultRegistry(), getContext(), mViewModel);
        getLifecycle().addObserver(mJsonExporter);
        mViewModel.setActiveFragment(mViewModel.ACTIVE_FRAGMENT_DETAILS);
        mViewModel.setSpeekerButton(binding.detallesInstruccionesSpeeker);
        configReceta();
        configOptionsButton();
        configOptionsBarButtons();
        configActualizaCompra();
        configPorcionesChange();
        configTextToSpeech();
        configBackPressed();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        conservarDatos();
        if(mViewModel.getActiveFragment() != mViewModel.ACTIVE_FRAGMENT_DETAILS){
            mPdfExporter.getmExportPdfLauncher().unregister();
            if(mViewModel.isListening()){
                finalizaEscucha();
            }
        }
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mViewModel.isPdfDownloaded()){
            binding.detallesBarraOpciones.setVisibility(View.VISIBLE);
            binding.detallesOpcionesButton.setVisibility(View.VISIBLE);
            mViewModel.setPdfDownloaded(false);
        }

        Log.i("RECETA DETAILS", "ON RESUME: " + mReceta.getId());

        List<Ingrediente> ingredientes = mReceta.getIngredientes();
        ingredientesAdapter.setList(ingredientes);

        List<ArtefactoEnUso> artefactosEnUso = mReceta.getArtefactosEnUso();
        if(artefactosEnUso == null || artefactosEnUso.size() <= 0){
            binding.detallesArtefactosLlPrincipal.setVisibility(View.GONE);
            mViewModel.inicializaListArtefactosEnUso();
            artefactosEnUso = mReceta.getArtefactosEnUso();
        }
        else if(artefactosEnUso.size() == 1){
            if(artefactosEnUso.get(0).getArtefactoId() == null ||
                    artefactosEnUso.get(0).getArtefactoId() == 0){
                binding.detallesArtefactosLlPrincipal.setVisibility(View.GONE);
            }
        }
        artefactosAdapter.setList(artefactosEnUso);

        List<Instruccion> instrucciones = mReceta.getInstrucciones();
        if(instrucciones == null || instrucciones.size() <= 0){
            mViewModel.inicializaListInstrucciones();
        }
        instruccionesAdapter.setList(instrucciones);
    }



    public void configBackPressed(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment
                        .findNavController(RecetaDetailsFragment.this)
                        .navigate(R.id.action_Details_to_List);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void configOptionsBarButtons() {
        configEdit();
        configDelete();
        configExportToPdf();
        configSendReceta();
        configFavourite();
    }

    private void configEdit() {
        binding.detallesEdit.setOnClickListener(view -> NavHostFragment
                .findNavController(RecetaDetailsFragment.this)
                .navigate(R.id.action_Details_to_FormEdit));
    }

    private void configDelete() {
        binding.detallesDelete.setOnClickListener(view -> showDeleteRecetaDialog());
    }

    private void showDeleteRecetaDialog(){
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Eliminando Receta")
                .setMessage("¿Está seguro de eliminar " + mReceta.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) -> {
                    mRecetaDB.delete(mReceta);
                    if(mReceta.getFoto() != null && !mReceta.getFoto().isEmpty()){
                        Auxiliar.deletePhoto(getContext(), mReceta.getFoto());
                    }
                    if(mReceta.getInstrucciones() != null){
                        if(mReceta.getInstrucciones().size() > 0){
                            for(Instruccion instruccion : mReceta.getInstrucciones()){
                                if(instruccion.getFoto() != null && !instruccion.getFoto().isEmpty()){
                                    Auxiliar.deletePhoto(getContext(), instruccion.getFoto());
                                }
                            }
                        }
                    }
                    NavHostFragment.findNavController(RecetaDetailsFragment.this)
                            .navigate(R.id.action_Details_to_List);
                })
                .show();
    }

    private void configExportToPdf() {
        binding.detallesPdf.setOnClickListener(view -> {
            binding.detallesBarraOpciones.setVisibility(View.GONE);
            binding.detallesOpcionesButton.setVisibility(View.GONE);
            mViewModel.setPdfDownloaded(true);
            mPdfExporter.savePdf(binding.containerGeneral);
        });
    }

    private void configSendReceta() {

        View popupView = bindingSendFile.getRoot();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        binding.detallesCompartir.setOnClickListener(view ->
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0));

        bindingSendFile.getRoot().setOnClickListener(view -> popupWindow.dismiss());

        bindingSendFile.sendReceta.setOnClickListener(view -> {
            popupWindow.dismiss();
            Uri uri = mJsonExporter.generateJson();
            if(uri != null){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("application/json");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Enviando receta..."));
            }
        });

        bindingSendFile.sendPdf.setOnClickListener(view -> {
            popupWindow.dismiss();

            binding.detallesBarraOpciones.setVisibility(View.GONE);
            binding.detallesOpcionesButton.setVisibility(View.GONE);
            Uri uri = mPdfExporter.generatePdf(binding.containerGeneral);
            binding.detallesBarraOpciones.setVisibility(View.VISIBLE);
            binding.detallesOpcionesButton.setVisibility(View.VISIBLE);
            if(uri != null){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Enviando PDF..."));
            }
        });

    }

    private void configFavourite() {
        if(mReceta.isEsFavorita()){
            binding.detallesFavorita.setImageResource(R.drawable.ic_star_full_24);
        }
        else {
            binding.detallesFavorita.setImageResource(R.drawable.ic_star_border_24);
        }
        binding.detallesFavorita.setOnClickListener(view -> {
            if(mReceta.isEsFavorita()){
                mReceta.setEsFavorita(false);
                mRecetaDB.save(mReceta);
                binding.detallesFavorita.setImageResource(R.drawable.ic_star_border_24);
            }
            else {
                mReceta.setEsFavorita(true);
                mRecetaDB.save(mReceta);
                binding.detallesFavorita.setImageResource(R.drawable.ic_star_full_24);
            }
        });
    }

    private void configOptionsButton() {
        binding.detallesOpcionesButton.setOnClickListener(view -> {
            if(mOptionsBarVisible){
                binding.detallesBarraOpciones.setVisibility(View.GONE);
                mOptionsBarVisible = false;
            }
            else {
                binding.detallesBarraOpciones.setVisibility(View.VISIBLE);
                mOptionsBarVisible = true;
            }
        });
    }

    private void configReceta() {
        Log.i("VER - RECETA", "configReceta: Titulo y porciones");
        binding.detallesTitulo.setText(mReceta.getNombre());
        binding.detallesPorciones.setText(Auxiliar.formateaCantidad(mReceta.getPorciones()));
        String photoName = mReceta.getFoto();
        if(photoName != null){
            File directorio = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = new File(directorio + "/" + File.separator + photoName);
            Log.i("LOAD-PHOTO", "Cargando foto PRINCIPAL desde path: " + photoFile.getPath());
            binding.photoLlayout.setVisibility(View.VISIBLE);
            int w = binding.photoPrincipalImageview.getWidth();
            int h = binding.photoPrincipalImageview.getHeight();
            Log.i("LOAD-PHOTO", "W: " + w + " - H: " + h);
            Glide.with(getContext())
                    .load(photoFile)
                    .override(0, 250)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.photoPrincipalImageview);
        }
        configIngredientesRecyclerView();
        configArtefactosRecyclerView();
        configInstruccionesRecyclerView();
    }

    private void configIngredientesRecyclerView() {
        ingredientesAdapter = new RecetaDetailsIngredientesAdapter(new ArrayList<>());
        ingredientesReciclerView = binding.detallesIngredientesContainer;
        ingredientesReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        ingredientesReciclerView.setAdapter(ingredientesAdapter);
        ingredientesReciclerView.setNestedScrollingEnabled(false);
    }

    private void configArtefactosRecyclerView() {
        artefactosAdapter = new RecetaDetailsArtefactosAdapter(new ArrayList<>());
        artefactosReciclerView = binding.detallesArtefactosContainer;
        artefactosReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        artefactosReciclerView.setAdapter(artefactosAdapter);
        artefactosReciclerView.setNestedScrollingEnabled(false);
    }

    private void configInstruccionesRecyclerView() {
        instruccionesAdapter = new RecetaDetailsInstruccionesAdapter(new ArrayList<>());
        instruccionesReciclerView = binding.detallesInstruccionesContainer;
        instruccionesReciclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        instruccionesReciclerView.setAdapter(instruccionesAdapter);
        instruccionesReciclerView.setNestedScrollingEnabled(false);
    }

    private void configPorcionesChange() {
        binding.detallesPorciones.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != null && charSequence.length() > 0){
                    double porcionesOld = mReceta.getPorciones();
                    double porcionesNew = Double.parseDouble(charSequence.toString());
                    mReceta.setPorciones(porcionesNew);
                    for(Ingrediente ingrediente : mReceta.getIngredientes()){
                        ingrediente.setCantidad(ingrediente.getCantidad() / porcionesOld * porcionesNew);
                    }
                    ingredientesAdapter.setList(mReceta.getIngredientes());
                    mReceta.setCosto(mReceta.calculaCostoTotal(mProductosEnReceta));
                    binding.detallesCostoxporcion.setText(Auxiliar.formateaPrecio(mReceta.getCosto() / mReceta.getPorciones()));
                    binding.detallesCostototal.setText(Auxiliar.formateaPrecio(mReceta.getCosto()));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void configActualizaCompra() {
        if(mProductosEnReceta.isEmpty()){
            for(Ingrediente ingrediente : mViewModel.getReceta().getIngredientes()){
                Producto producto = mProductoDB.findById(ingrediente.getProductoId());
                mProductosEnReceta.add(producto);
            }
        }
        mReceta.setCosto(mReceta.calculaCostoTotal(mProductosEnReceta));
        binding.detallesCostoxporcion.setText(Auxiliar.formateaPrecio(mReceta.getCosto() / mReceta.getPorciones()));
        binding.detallesCostototal.setText(Auxiliar.formateaPrecio(mReceta.getCosto()));
        binding.detallesActualizaPrecio.setOnClickListener(view -> {
            compraRecyclerView = bindingCompra.compraIngredienteContainer;
            compraAdapter = new RecetaDetailsCompraAdapter(mProductosEnReceta);
            compraRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            compraRecyclerView.setAdapter(compraAdapter);
            Log.i("ACTUALIZA COMPRA", "Click en actualiza compra");
            showPopupActualizaCompra(view);
        });
    }

    public void showPopupActualizaCompra(View view) {

        View popupView = bindingCompra.getRoot();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        bindingCompra.compraCancelButton.setOnClickListener(v -> popupWindow.dismiss());

        bindingCompra.compraInfoButton.setOnClickListener(v -> {
            bindingCompra.compraInfoButton.setVisibility(View.GONE);
            bindingCompra.compraInfoBackButton.setVisibility(View.VISIBLE);
            bindingCompra.compraInfoContainer.setVisibility(View.VISIBLE);
            bindingCompra.compraMainContainer.setVisibility(View.GONE);
        });

        bindingCompra.compraInfoContainer.setOnClickListener(v -> {
            bindingCompra.compraInfoButton.setVisibility(View.VISIBLE);
            bindingCompra.compraInfoBackButton.setVisibility(View.GONE);
            bindingCompra.compraInfoContainer.setVisibility(View.GONE);
            bindingCompra.compraMainContainer.setVisibility(View.VISIBLE);
        });

        bindingCompra.compraInfoBackButton.setOnClickListener(v -> {
            bindingCompra.compraInfoButton.setVisibility(View.VISIBLE);
            bindingCompra.compraInfoBackButton.setVisibility(View.GONE);
            bindingCompra.compraInfoContainer.setVisibility(View.GONE);
            bindingCompra.compraMainContainer.setVisibility(View.VISIBLE);
        });

        bindingCompra.compraActualizaButton.setOnClickListener(v -> {
            boolean isValid = true;
            for(int i = 0; i<compraAdapter.getItemCount(); i++){
                RecetaDetailsCompraAdapter.ViewHolder holder =
                        (RecetaDetailsCompraAdapter.ViewHolder) compraRecyclerView
                                .findViewHolderForAdapterPosition(i);
                if(holder != null){
                    AppCompatEditText precio = holder.getPrecio();
                    AppCompatEditText cantidad = holder.getCantidad();
                    TextView unidad = holder.getUnidad();
                    boolean isPrecioValid =
                            Validator.isNumericValid(precio, "Debe utilizar números y ',' como separador decimal...");
                    boolean isCantidadValid =
                            Validator.isNumericValid(cantidad, "Debe utilizar números y '.' como separador decimal...");
                    boolean isUnidadValid =
                            Validator.isSelectValid(unidad, compraAdapter.getmUnidadesDeMedida(), "Debe seleccionar una opción del listado");
                    if(isPrecioValid && isCantidadValid && isUnidadValid){
                        double doublePrecio = Auxiliar.formatNumberToDouble(precio.getText().toString().trim());
                        double doubleCantidad = Auxiliar.formatNumberToDouble(cantidad.getText().toString().trim());
                        Log.i("CANTIDAD", "Detalles: " + doubleCantidad);
                        Log.i("PRECIO", "Detalles: " + doublePrecio);
                        mProductosEnReceta.get(i).setPrecio(doublePrecio);
                        mProductosEnReceta.get(i).setCantidad(doubleCantidad);
                        Log.i("CANTIDAD", "Detalles: " + mProductosEnReceta.get(i).getCantidad());
                        Log.i("CANTIDAD", "Detalles: " + mProductosEnReceta.get(i).getPrecio());
                        mProductosEnReceta.get(i).setUnidadDeMedidaSimboloString(unidad.getText().toString().trim());
                        mProductosEnReceta.get(i).setPrecioSinDesperdicioDesdeCantidad();
                    }
                    else{
                        isValid = false;
                    }
                }
                else{
                    isValid = false;
                }
            }
            mViewModel.setProductos(mProductosEnReceta);
            if(isValid){
                mReceta.setCosto(mReceta.calculaCostoTotal(mProductosEnReceta));
                binding.detallesCostoxporcion.setText(Auxiliar.formateaPrecio(mReceta.getCosto() / mReceta.getPorciones()));
                binding.detallesCostototal.setText(Auxiliar.formateaPrecio(mReceta.getCosto()));
                popupWindow.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void configTextToSpeech() {

        configInfoLectura();

        List<Instruccion> instrucciones = mViewModel.getReceta().getInstrucciones();
        Log.i("TEXT TO SPEACH", "Cantidad de instrucciones: " + instrucciones.size());
        Intent actionRecognizeSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mTextToSpeech =  new TextToSpeech(getContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                mTextToSpeech.setLanguage(new Locale("ES"));
                mTextToSpeech.setSpeechRate(0.85F);
            }
        });

        configSpeechRecognizer(instrucciones, actionRecognizeSpeech);

        binding.detallesInstruccionesSpeeker.setOnClickListener(view -> {
            if(!mViewModel.isListening()){
                activaIconoEscucha();
                String infoSpeech = "Comenzando con la lectura de la receta. Recuerde utilizar las palabras claves.";
                mTextToSpeech.speak(infoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                mSpeechRecognizer.startListening(actionRecognizeSpeech);
                mViewModel.setIsListening(true);
            }
            else{
                finalizaEscucha();
                binding.detallesInstruccionesSpeeker.setImageResource(R.drawable.ic_bullhorn_outline);
            }
        });

        if(mViewModel.isListening()){
            activaIconoEscucha();
        }
        else{
            binding.detallesInstruccionesSpeeker.setImageResource(R.drawable.ic_bullhorn_outline);
        }

    }

    public void activaIconoEscucha(){
        binding.detallesInstruccionesSpeeker.setImageResource(R.drawable.avd_ear_hearing);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AnimatedVectorDrawable earIcon = (AnimatedVectorDrawable) binding.detallesInstruccionesSpeeker.getDrawable();
            earIcon.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    earIcon.start();
                }
            });
            earIcon.start();
        }
    }

    public void finalizaEscucha(){
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        mSpeechRecognizer.cancel();
        Log.i("DETENER ESCUCHA", "Escucha terminada ...");
        mViewModel.setIsListening(false);
    }

    public void configSpeechRecognizer(List<Instruccion> instrucciones, Intent actionRecognizeSpeech){
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            }
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float v) {}
            @Override
            public void onBufferReceived(byte[] bytes) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int i) {
                if(mViewModel.isListening()){
                    mSpeechRecognizer.startListening(actionRecognizeSpeech);
                }
            }
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String comandoDeVoz = text.get(0);
                comandoDeVoz = comandoDeVoz.toLowerCase().trim();

                Log.i("COMANDO DE VOZ", comandoDeVoz);

                switch (comandoDeVoz){
                    case "reproducir":
                    case "reproducir paso":
                    case "reproducir pasos":
                    case "comenzar":
                    case "iniciar":
                        if(indexSpeech != -100){
                            indexSpeech = 0;
                        }
                        reproducePaso();
                        mSpeechRecognizer.startListening(actionRecognizeSpeech);
                        break;
                    case "siguiente":
                    case "next":
                        if(indexSpeech > -100) {
                            indexSpeech++;
                            reproducePaso();
                        }
                        mSpeechRecognizer.startListening(actionRecognizeSpeech);
                        break;
                    case "previo":
                    case "previa":
                    case "anterior":
                        if(indexSpeech > -100){
                            indexSpeech--;
                            reproducePaso();
                        }
                        else if(indexSpeech == -500){
                            indexSpeech = instrucciones.size() - 1;
                            reproducePaso();
                        }
                        mSpeechRecognizer.startListening(actionRecognizeSpeech);
                        break;
                    case "reproducir todo":
                    case "reproducir todos":
                    case "reproducir toda":
                    case "reproducir todas":
                    case "todo":
                    case "todos":
                    case "todas":
                    case "toda":
                        reproduceTodo();
                        mSpeechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                        break;
                    case "repetir":
                        if(indexSpeech == -500){
                            reproduceTodo();
                        }
                        else{
                            reproducePaso();
                        }
                        mSpeechRecognizer.startListening(actionRecognizeSpeech);
                        break;
                    case "detener":
                    case "stop":
                    case "salir":
                        finalizaEscucha();
                        mViewModel.getSpeekerButton().setImageResource(R.drawable.ic_bullhorn_outline);
                        break;
                    default:
                        mSpeechRecognizer.startListening(actionRecognizeSpeech);
                        break;
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {}
            @Override
            public void onEvent(int i, Bundle bundle) {}

            private void reproducePaso(){
                Log.i("REPRODUCE PASO", "Reproduciendo paso ...");
                if(instrucciones == null || instrucciones.size() == 0){
                    textoSpeech = "No hay instrucciones para esta receta.";
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                }
                else if(indexSpeech == -100){

                    indexSpeech = 0;
                    tituloSpeech = "Paso " + (indexSpeech + 1);
                    textoSpeech = instrucciones.get(indexSpeech).getInstruccion();
                    if(textoSpeech == null || textoSpeech.isEmpty()){
                        textoSpeech = "Solo hay una imagen en este paso.";
                    }
                    mTextToSpeech.speak(tituloSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);

                }
                else if(indexSpeech > -100 && indexSpeech < 0){
                    String infoSpeech = "Ya se encuentra en el primer paso. No puede retroceder más.";
                    indexSpeech = 0;
                    tituloSpeech = "Paso " + (indexSpeech + 1);
                    textoSpeech = instrucciones.get(indexSpeech).getInstruccion();
                    mTextToSpeech.speak(infoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(tituloSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);

                }
                else if(indexSpeech < instrucciones.size()){
                    tituloSpeech = "Paso " + (indexSpeech + 1);
                    textoSpeech = instrucciones.get(indexSpeech).getInstruccion();
                    mTextToSpeech.speak(tituloSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                }
                else if(indexSpeech >= instrucciones.size()){
                    String infoSpeech = "Ha llegado al último paso. No puede avanzar más.";
                    indexSpeech = instrucciones.size() - 1;
                    tituloSpeech = "Paso " + (indexSpeech + 1);
                    textoSpeech = instrucciones.get(indexSpeech).getInstruccion();
                    mTextToSpeech.speak(infoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(tituloSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                }
            }

            private void reproduceTodo(){
                Log.i("REPRODUCE TODO", "Reproduciendo todo ...");
                indexSpeech = -500;
                for(Instruccion instruccion : instrucciones){
                    tituloSpeech = "Paso " + instruccion.getOrden();
                    Log.i("TEXT TO SPEACH", "Leyendo : " + tituloSpeech);
                    textoSpeech = instruccion.getInstruccion();
                    mTextToSpeech.speak(tituloSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                    mTextToSpeech.speak(textoSpeech, TextToSpeech.QUEUE_ADD, new Bundle(), null);
                }
            }
        });
    }

    public void configInfoLectura(){
        binding.detallesInstruccionesInfo.setOnClickListener(view -> {
            View popupView = bindingInfoLectura.getRoot();
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true;
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            bindingInfoLectura.infoLecturaCancelButton.setOnClickListener(v -> popupWindow.dismiss());
        });
    }

    public void conservarDatos(){
        String porciones = binding.detallesPorciones.getText().toString().trim();
        mReceta.setPorciones(Double.parseDouble(porciones));
        String costo = binding.detallesCostototal.getText().toString().trim();
        mReceta.setCosto(Auxiliar.formatNumberToDouble(costo));
        for(int i = 0; i < ingredientesAdapter.getList().size();i++){
            RecetaDetailsIngredientesAdapter.ViewHolder holder =
                    (RecetaDetailsIngredientesAdapter.ViewHolder) ingredientesReciclerView
                            .findViewHolderForAdapterPosition(i);
            String cantidad = holder.getCantidad().getText().toString().trim();
            mReceta.getIngredientes().get(i).setCantidad(Auxiliar.formatNumberToDouble(cantidad));
            String unidad = holder.getUnidad().getText().toString().trim();
            mReceta.getIngredientes().get(i).setUnidadDeMedidaString(unidad);
        }
        for(int i = 0; i < artefactosAdapter.getList().size();i++){
            RecetaDetailsArtefactosAdapter.ViewHolder holder =
                    (RecetaDetailsArtefactosAdapter.ViewHolder) artefactosReciclerView
                            .findViewHolderForAdapterPosition(i);
            if(holder != null){
                if(holder.getArtefacto().isEsHorno()){
                    if(holder.getTemperatura() != null){
                        if(holder.getTemperatura().getText() != null){
                            if(!holder.getTemperatura().getText().toString().trim().isEmpty()){
                                String temperatura = holder.getTemperatura().getText().toString().trim();
                                mReceta.getArtefactosEnUso().get(i).setTemperatura(Auxiliar.formatNumberToDouble(temperatura));
                            }
                        }
                    }
                    if(holder.getUnidad() != null){
                        if(holder.getUnidad().getText() != null) {
                            if (!holder.getUnidad().getText().toString().trim().isEmpty()) {
                                String unidad = holder.getUnidad().getText().toString().trim();
                                mReceta.getArtefactosEnUso().get(i).setUnidadDeTemperaturaString(unidad);
                            }
                        }
                    }
                }
            }
        }
    }
}