package curso.android.misrecetapps.controllers.receta.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.databinding.CompraIngredienteBinding;
import curso.android.misrecetapps.model.Producto;

public class RecetaDetailsCompraAdapter extends RecyclerView.Adapter<RecetaDetailsCompraAdapter.ViewHolder>{
    //ATRIBUTOS
    private List<Producto> list;
    private Context context;
    private final List<String> mUnidadesDeMedida;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaDetailsCompraAdapter(List<Producto> list){
        this.list = list;
        this.mUnidadesDeMedida = Auxiliar.configListUnidadDeProductoSimbolo();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CompraIngredienteBinding binding;
        private final TextView nombre;
        private final AppCompatEditText precio;
        private final AppCompatEditText cantidad;
        private final TextView unidad;

        ViewHolder(@NonNull CompraIngredienteBinding b){
            super(b.getRoot());
            binding = b;
            nombre = b.compraIngredienteNombre;
            precio = b.compraInputPrice;
            cantidad = b.compraInputCantidad;
            unidad = b.selectUnidadProducto;
        }

        public CompraIngredienteBinding getBinding() { return binding; }
        public TextView getNombre() { return nombre; }
        public AppCompatEditText getPrecio() { return precio; }
        public AppCompatEditText getCantidad() { return cantidad; }
        public TextView getUnidad() { return unidad; }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        CompraIngredienteBinding bindingIngredientes;
        bindingIngredientes = CompraIngredienteBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(bindingIngredientes);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Producto item = list.get(position);
        List<String> unidadesDeMedida = Auxiliar.configListUnidadesSimbolo(item.getUnidadDeMedida().getTipoDeMedida());

        holder.nombre.setId(View.generateViewId());
        holder.precio.setId(View.generateViewId());
        holder.cantidad.setId(View.generateViewId());
        holder.unidad.setId(View.generateViewId());

        holder.nombre.setText(item.getNombre());
        holder.precio.setText(Auxiliar.formateaPrecio(item.getPrecio()));
        holder.cantidad.setText(Auxiliar.formateaCantidad(item.getCantidad()));
        Auxiliar.configSelect(holder.unidad, unidadesDeMedida, context);
        holder.unidad.setText(item.getUnidadDeMedida().getSimbolo());

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(List<Producto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Producto> getList() {
        return list;
    }

    public List<String> getmUnidadesDeMedida() { return mUnidadesDeMedida; }
}
