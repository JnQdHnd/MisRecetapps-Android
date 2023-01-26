package curso.android.misrecetapps.controllers.receta.list;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.activities.ExportToJson;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.databinding.FragmentRecipeListBinding;
import curso.android.misrecetapps.databinding.SelectLongclickRecetaListadoBinding;
import curso.android.misrecetapps.interfaces.OnRecetaClickListener;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Receta;

public class RecetaListFragment extends Fragment implements OnRecetaClickListener {

    private FragmentRecipeListBinding binding;
    private SelectLongclickRecetaListadoBinding bindingLongClickReceta;
    private RecyclerView mRecyclerViewRecetas;
    private RecetaListAdapter mRecetaListAdapter;
    private RecyclerView mRecyclerViewProductosInFilter;
    private RecetaListProductoFilterAdapter mProductoFilterAdapter;
    private RecetaViewModel mViewModel;
    private ExportToJson mJsonExporter;
    private final ProductoDB mProductoDB = new ProductoDB();
    private final RecetaDB mRecetaDB = new RecetaDB();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i("LIST", "onCreateView!!!");
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        bindingLongClickReceta = SelectLongclickRecetaListadoBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(RecetaViewModel.class);
        mJsonExporter = new ExportToJson(
                requireActivity().getActivityResultRegistry(), getContext(), mViewModel);
        getLifecycle().addObserver(mJsonExporter);
        mViewModel.clearAll();
        configAdapter();
        configAdapterView();
        configFilterByProducto();
        configBackPressed();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.i("LIST", "onViewCreated!!!");
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setActiveFragment(mViewModel.ACTIVE_FRAGMENT_LIST);
        binding.fabNewRecipe.setOnClickListener(view1 -> NavHostFragment.findNavController(RecetaListFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
    }

    @Override
    public void onDestroyView() {
        Log.i("LIST", "onDestroyView");
        super.onDestroyView();
        if(mViewModel.getActiveFragment() != mViewModel.ACTIVE_FRAGMENT_LIST){
            mViewModel.setProductosInFilter(null);
            mViewModel.setmRecetasVisibles(null);
        }
        mViewModel.getmImportButton().setVisibility(View.GONE);
        binding = null;
    }

    private void configAdapter(){
        mRecetaListAdapter = new RecetaListAdapter(mViewModel, this);
        if(mViewModel.getProductosInFilter() == null){
            List<Producto> temp = new ArrayList<>();
            mViewModel.setProductosInFilter(temp);
        }
        mProductoFilterAdapter = new RecetaListProductoFilterAdapter(mViewModel, mRecetaListAdapter, binding.serchBox);
    }

    private void configAdapterView(){
        mRecyclerViewRecetas = binding.recyclerViewRecetas;
        mRecyclerViewRecetas.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), RecyclerView.VERTICAL, false));
        mRecyclerViewRecetas.setAdapter(mRecetaListAdapter);

        mRecyclerViewProductosInFilter = binding.recyclerviewProductoFilter;
        mRecyclerViewProductosInFilter.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), RecyclerView.HORIZONTAL, false));
        mRecyclerViewProductosInFilter.setAdapter(mProductoFilterAdapter);
    }

    @Override
    public void onResume() {
        Log.i("LIST", "ON RESUME !!!");
        super.onResume();
        if(mViewModel.getmRecetasVisibles() == null){
            Log.i("LIST", "getmRecetasVisibles() == null");
            mViewModel.setmRecetasVisibles(mRecetaDB.findAllComplete());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.i("LIST", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
            mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::isEsFavorita).reversed());
        }
        else{
            Log.i("LIST", "Else");
            for(int i = 0; i < mViewModel.getmRecetasVisibles().size(); i++){
                Receta receta = mViewModel.getmRecetasVisibles().get(i);
                if(receta.isEsFavorita()){
                    mViewModel.getmRecetasVisibles().add(0, receta);
                    mViewModel.getmRecetasVisibles().remove(i+1);
                }
            }
        }
        configSerchBox();
        mViewModel.getmImportButton().setVisibility(View.VISIBLE);
    }

    private void configFilterByProducto() {

        if(mViewModel.isFilterBarVisible()){
            binding.filterByProductoContainer.setVisibility(View.VISIBLE);
        }
        else {
            binding.filterByProductoContainer.setVisibility(View.GONE);
        }

        binding.filterByProductoButton.setOnClickListener(view -> {
            if(mViewModel.isFilterBarVisible()){
                binding.filterByProductoContainer.setVisibility(View.GONE);
                mViewModel.setFilterBarVisible(false);
            }
            else {
                binding.filterByProductoContainer.setVisibility(View.VISIBLE);
                mViewModel.setFilterBarVisible(true);
            }
        });

        List<String> productosStrings = mProductoDB.findAllNames();
        Auxiliar.configSelect(binding.selectProductoFilter, productosStrings, getContext());
        binding.buttonAddFilterProducto.setOnClickListener(view -> {
            LinearLayout.LayoutParams paramsOn = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsOn.setMarginEnd(20);
            LinearLayout.LayoutParams paramsOff = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsOff.setMarginEnd(0);
            if(binding.selectProductoFilter.getText() != null){
                String texto = binding.selectProductoFilter.getText().toString().trim();
                if(!texto.isEmpty()){
                    if(!mViewModel.existInProductosInFilter(texto)){
                        Producto producto = mProductoDB.findByNombre(
                                binding.selectProductoFilter.getText().toString().trim());
                        mViewModel.getProductosInFilter().add(producto);
                        mViewModel.filterRecetasVisiblesByProductosInFilter();
                        binding.selectProductoFilter.setText("");
                        binding.selectErrorProductoFilter.setError(null);
                        binding.selectErrorProductoFilter.setLayoutParams(paramsOff);
                        mProductoFilterAdapter.notifyDataSetChanged();
                        mRecetaListAdapter.notifyDataSetChanged();
                    }
                    else {
                        binding.selectErrorProductoFilter.setError("¡El producto ya fue seleccionado!");
                        binding.selectErrorProductoFilter.setLayoutParams(paramsOn);
                    }
                }
                else {
                    binding.selectErrorProductoFilter.setError("¡Debe seleccionar un producto!");
                    binding.selectErrorProductoFilter.setLayoutParams(paramsOn);
                }
            }
            else {
                binding.selectErrorProductoFilter.setError("¡Debe seleccionar un producto!");
                binding.selectErrorProductoFilter.setLayoutParams(paramsOn);
            }
        });
    }

    public void configSerchBox(){
        Log.i("LIST", "configSerchBox");
        int cantidadDeRecetas = mRecetaDB.findAll().size();
        int numMin = 10;
        if(cantidadDeRecetas <= numMin){
            binding.serchBox.setVisibility(View.GONE);
        }
        else{
            binding.serchBox.setVisibility(View.VISIBLE);
            binding.serchBox.setText("");
            binding.serchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<Receta> listCopy = mRecetaDB.findAllComplete();
                    mViewModel.filterByText(s, listCopy);
                    mRecetaListAdapter.notifyDataSetChanged();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    public void configBackPressed(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showAppClosingDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void showAppClosingDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Estás saliendo")
                .setMessage("¿Seguro que quieres salir?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) ->
                        requireActivity().finishAndRemoveTask())
                .show();
    }

    @Override
    public void onItemClick(Receta receta) {
        mViewModel.setReceta(receta);
        NavHostFragment.findNavController(RecetaListFragment.this)
                .navigate(R.id.action_List_to_Details);
    }

    @Override
    public void onLongItemClick(@NonNull Receta receta, View view) {
        View popupView = bindingLongClickReceta.getRoot();
        int width = popupView.getLayoutParams().width;
        int height = popupView.getLayoutParams().height;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        Log.i("LONG CLICK", "Inicio...");
        bindingLongClickReceta.nombreDeReceta.setText(receta.getNombre());
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        bindingLongClickReceta.getRoot().setOnClickListener(view1 -> popupWindow.dismiss());
        bindingLongClickReceta.seeReceta.setOnClickListener(view12 -> {
            popupWindow.dismiss();
            mViewModel.setReceta(receta);
            NavHostFragment.findNavController(RecetaListFragment.this)
                    .navigate(R.id.action_List_to_Details);
        });
        bindingLongClickReceta.deleteReceta.setOnClickListener(view13 -> {
            popupWindow.dismiss();
            showDeleteRecetaDialog(receta);
        });
        bindingLongClickReceta.sendReceta.setOnClickListener(view14 -> {
            popupWindow.dismiss();
            mViewModel.setReceta(receta);
            Uri uri = mJsonExporter.generateJson();
            if(uri != null){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("application/json");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Enviando receta..."));
            }
        });
    }

    private void showDeleteRecetaDialog(@NonNull Receta receta){
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Eliminando Receta")
                .setMessage("¿Está seguro de eliminar " + receta.getNombre() + "?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) -> {
                    mRecetaDB.delete(receta);
                    if(receta.getFoto() != null && !receta.getFoto().isEmpty()){
                        Auxiliar.deletePhoto(getContext(), receta.getFoto());
                    }
                    if(receta.getInstrucciones() != null){
                        if(receta.getInstrucciones().size() > 0){
                            for(Instruccion instruccion : receta.getInstrucciones()){
                                if(instruccion.getFoto() != null && !instruccion.getFoto().isEmpty()){
                                    Auxiliar.deletePhoto(getContext(), instruccion.getFoto());
                                }
                            }
                        }
                    }
                    mViewModel.setmRecetasVisibles(mRecetaDB.findAllComplete());
                    mRecetaListAdapter.notifyDataSetChanged();
                })
                .show();
    }
}