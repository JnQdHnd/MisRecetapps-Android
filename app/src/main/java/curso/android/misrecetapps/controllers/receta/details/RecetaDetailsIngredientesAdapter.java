package curso.android.misrecetapps.controllers.receta.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.databinding.DetallesIngredienteBinding;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Producto;

public class RecetaDetailsIngredientesAdapter extends RecyclerView.Adapter<RecetaDetailsIngredientesAdapter.ViewHolder>{
    //ATRIBUTOS
    private List<Ingrediente> list;
    private Context context;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaDetailsIngredientesAdapter(List<Ingrediente> list){
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final DetallesIngredienteBinding detallesIngredienteBinding;
        private final TextView cantidad;
        private final TextView unidad;

        ViewHolder(@NonNull DetallesIngredienteBinding b){
            super(b.getRoot());
            detallesIngredienteBinding = b;
            cantidad = b.detallesIngredientesCantidad;
            unidad = b.detallesIngredientesUnidadSelect;
        }

        public TextView getCantidad() {return cantidad;}
        public TextView getUnidad() {return unidad;}

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        DetallesIngredienteBinding bindingIngredientes;
        bindingIngredientes = DetallesIngredienteBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(bindingIngredientes);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ingrediente item = list.get(position);
        ProductoDB productoDB = new ProductoDB();
        Producto producto = productoDB.findById(item.getProductoId());
        List<String> unidadesDeMedida = Auxiliar.configListUnidadesSimbolo(item.getUnidadDeMedida().getTipoDeMedida());
        holder.detallesIngredienteBinding.detallesIngredientesTexto.setText(producto.getNombre());
        holder.detallesIngredienteBinding.detallesIngredientesCantidad.setText(Auxiliar.formateaCantidad(item.getCantidad()));

        Auxiliar.configSelectConversor(
                holder.detallesIngredienteBinding.detallesIngredientesUnidadSelect,
                unidadesDeMedida,
                context,
                holder.detallesIngredienteBinding.detallesIngredientesCantidad,
                item);

        holder.detallesIngredienteBinding.detallesIngredientesUnidadSelect.setText(item.getUnidadDeMedida().getSimbolo());
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(List<Ingrediente> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Ingrediente> getList() {
        return list;
    }

}
