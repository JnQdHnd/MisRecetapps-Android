package curso.android.misrecetapps.controllers.producto;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.Validator;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.databinding.FragmentProductoFormBinding;
import curso.android.misrecetapps.model.Producto;

public class ProductoFormFragment extends Fragment {

    //ATRIBUTOS
    private ProductoViewModel mViewModel;
    private ProductoDB mProductoDB;
    private FragmentProductoFormBinding binding;
    private List<String> mUnidades;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProductoFormBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        mProductoDB = new ProductoDB();
        mUnidades = Auxiliar.configListUnidadDeProductoSimbolo();
        if(mViewModel.getmProducto() == null) { mViewModel.setmProducto(new Producto()); }
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
        Producto producto = mViewModel.getmProducto();
        if(producto.getNombre() != null){
            binding.productoFormNombre.setText(producto.getNombre());
        }
        if(producto.getId() > 0 && producto.getPrecio() >= 0){
            binding.productoInputPrice.setText(Auxiliar.formateaPrecio(producto.getPrecio()));
        }
        if(producto.getCantidad() > 0){
            binding.productoInputCantidad.setText(Auxiliar.formateaCantidad(producto.getCantidad()));
        }
        Auxiliar.configSelect(binding.productoUnidad, Auxiliar.configListUnidadDeProductoSimbolo(), getContext());
        if(producto.getUnidadDeMedida() != null){
            binding.productoUnidad.setText(producto.getUnidadDeMedida().getSimbolo());
        }
        if(producto.getId() > 0 && producto.getDesperdicio() >= 0){
            binding.productoFormDesperdicio.setText(Auxiliar.formateaCantidad(producto.getDesperdicio()));
        }
    }

    private void configSave() {
        binding.fabCheck.setOnClickListener(view -> {
            if(validateFields()){
                conservaDatos();
                saveEnDB(mViewModel.getmProducto());
                String productoNombre = mViewModel.getmProducto().getNombre();
                mViewModel.setmProducto(mProductoDB.findByNombre(productoNombre));
                NavHostFragment.findNavController(ProductoFormFragment.this)
                        .navigate(R.id.action_ProductoForm_to_ProductoList);
            }
        });
    }

    private void conservaDatos() {

        if(mViewModel.getmProducto() == null){
            mViewModel.setmProducto(new Producto());
        }

        if(binding.productoFormNombre.getText() != null){
            String nombre = binding.productoFormNombre.getText().toString().trim();
            mViewModel.getmProducto().setNombre(nombre);
        }
        if(binding.productoInputPrice.getText() != null){
            String precio = binding.productoInputPrice.getText().toString().trim();
            if(Validator.isNumeric(precio)){
                mViewModel.getmProducto().setPrecio(Auxiliar.formatNumberToDouble(precio));
            }
        }
        if(binding.productoInputCantidad.getText() != null){
            String cantidad = binding.productoInputCantidad.getText().toString().trim();
            if(Validator.isNumeric(cantidad)){
                mViewModel.getmProducto().setCantidad(Auxiliar.formatNumberToDouble(cantidad));
            }
        }
        if(binding.productoUnidad.getText() != null){
            String unidad = binding.productoUnidad.getText().toString().trim();
            mViewModel.getmProducto().setUnidadDeMedidaSimboloString(unidad);
        }
        if(binding.productoFormDesperdicio.getText() != null){
            String desperdicio = binding.productoFormDesperdicio.getText().toString().trim();
            if(Validator.isNumeric(desperdicio)){
                mViewModel.getmProducto().setDesperdicio(Auxiliar.formatNumberToDouble(desperdicio));
            }
        }
    }

    public boolean validateFields() {

        Log.i("VALIDANDO PRODUCTO", "Validando casilleros...");

        boolean isValid = true;

        boolean isValidNombre;
        boolean isValidPrecio;
        boolean isValidCantidad;
        boolean isValidUnidad;
        boolean isValidDesperdicio;

        TextInputEditText nombre = binding.productoFormNombre;
        AppCompatEditText precio = binding.productoInputPrice;
        AppCompatEditText cantidad = binding.productoInputCantidad;
        TextView unidad = binding.productoUnidad;
        AppCompatEditText desperdicio = binding.productoFormDesperdicio;

        isValidNombre = Validator.isNombreValid(nombre, mViewModel, getString(R.string.error_nombre_existe));
        isValidPrecio = Validator.isNumericValid(precio, getString(R.string.error_precio));
        isValidCantidad = Validator.isNumericValid(cantidad, getString(R.string.error_producto_cantidad));
        isValidUnidad = Validator.isSelectValid(unidad, mUnidades, getString(R.string.error_receta_unidad_producto));
        isValidDesperdicio = Validator.isNumericValid(desperdicio, getString(R.string.error_desperdicio));

        if(!isValidNombre || !isValidPrecio || !isValidCantidad || !isValidUnidad || !isValidDesperdicio){
            isValid = false;
        }

        return isValid;

    }

    public void saveEnDB(Producto producto){

        Log.i("SAVING-RECETA", "Guardando producto...");

        try {
            if(mViewModel.getmProducto().getId() > 0){
                Log.e("DELETE PRODUCTO DB", "Eliminando: " + mViewModel.getmProducto().getNombre());
                mProductoDB.delete(mViewModel.getmProducto().getId());
            }
            producto.setId(0);
            producto.setPrecioSinDesperdicioDesdeCantidad();
            mProductoDB.save(producto);
            String msg = mViewModel.getmProducto().getId() > 0 ? "Receta editada con éxito." : "Receta guardada con éxito.";
            Log.i("SAVE PRODUCTO", msg);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SAVE PRODUCTO", "Error al guardar PRODUCTO en la BASE DE DATOS.");
        }

    }
}