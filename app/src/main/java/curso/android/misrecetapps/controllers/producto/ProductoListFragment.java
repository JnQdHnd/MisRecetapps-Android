package curso.android.misrecetapps.controllers.producto;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.R;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.databinding.FragmentProductoListBinding;
import curso.android.misrecetapps.interfaces.OnProductoClickListener;
import curso.android.misrecetapps.model.Producto;


public class ProductoListFragment extends Fragment implements OnProductoClickListener {

    private FragmentProductoListBinding binding;
    private RecyclerView mRecyclerView;
    private ProductoListAdapter mProductoListAdapter;
    private ProductoViewModel mViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentProductoListBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        configAdapter();
        configAdapterView();
        configSerchBox();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabNewProducto.setOnClickListener(view1 -> {
            mViewModel.clearAll();
            NavHostFragment.findNavController(ProductoListFragment.this)
                    .navigate(R.id.action_ProductoList_to_ProductoForm);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void configAdapter(){
        mProductoListAdapter = new ProductoListAdapter(new ArrayList<>(), this);
    }

    private void configAdapterView(){
        mRecyclerView = binding.recyclerViewProductos;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRecyclerView.setAdapter(mProductoListAdapter);
    }

    @Override
    public void onItemClick(Producto producto) {
        mViewModel.setmProducto(producto);
        NavHostFragment.findNavController(ProductoListFragment.this)
                .navigate(R.id.action_ProductoList_to_ProductoForm);
    }

    @Override
    public void onLongItemClick(Producto producto) {
    }

    @Override
    public void onResume() {
        super.onResume();
        ProductoDB productoDB = new ProductoDB();
        mProductoListAdapter.setList(productoDB.findAll());
    }

    public void configSerchBox(){
        binding.serchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Producto> listCopy = new ProductoDB().findAll();
                mProductoListAdapter.filter(s, listCopy);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}