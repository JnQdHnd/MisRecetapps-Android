package curso.android.misrecetapps.controllers.producto;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.databinding.FragmentProductoFormBinding;
import curso.android.misrecetapps.model.Producto;

public class ProductoFormDialog extends DialogFragment {

    private ProductoViewModel mViewModel;
    private ProductoDB mProductoDB;
    private FragmentProductoFormBinding binding;
    private List<String> mUnidades;
    private TextView mTextView;
    private Dialog mParentDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        mProductoDB = new ProductoDB();
        mUnidades = Auxiliar.configListUnidadDeProductoSimbolo();
        if(mViewModel.getmProducto() == null) { mViewModel.setmProducto(new Producto()); }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_producto_form);
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
        binding = FragmentProductoFormBinding.inflate(inflater, container, false);
        binding.scrollView.setBackgroundResource(R.drawable.background_square_white_bordered);
        Auxiliar.configSelect(binding.productoUnidad, Auxiliar.configListUnidadDeProductoSimbolo(), getContext());
        configSave();
        return binding.getRoot();
    }

    private void configSave() {
        binding.fabCheck.setOnClickListener(view -> {
            if(validateFields()){
                conservaDatos();
                saveEnDB(mViewModel.getmProducto());
                mTextView.setText(mViewModel.getmProducto().getNombre());
                mViewModel.clearAll();
                mParentDialog.dismiss();
                dismiss();
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

    public void setmTextView(TextView mTextView) {
        this.mTextView = mTextView;
    }

    public void setmParentDialog(Dialog mParentDialog) {
        this.mParentDialog = mParentDialog;
    }
}
