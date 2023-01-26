package curso.android.misrecetapps.controllers.producto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import curso.android.misrecetapps.databinding.ItemProductoBinding;
import curso.android.misrecetapps.interfaces.OnProductoClickListener;
import curso.android.misrecetapps.model.Producto;

public class ProductoListAdapter extends RecyclerView.Adapter<ProductoListAdapter.ViewHolder> {

    //ATRIBUTOS
    private List<Producto> mProductos;
    private Context context;
    private final OnProductoClickListener listener;

    //CONSTRUCTOR ----------------------------------------------
    public ProductoListAdapter(List<Producto> productos, OnProductoClickListener listener){
        this.mProductos = productos;
        this.listener = listener;
    }

    //VIEWHOLDER ----------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemProductoBinding itemBinding;
        ViewHolder(@NonNull ItemProductoBinding irb) {
            super(irb.getRoot());
            itemBinding = irb;
        }
        void setListener(final Producto producto, final OnProductoClickListener listener){
            itemBinding.containerProducto.setOnClickListener(view -> listener.onItemClick(producto));
            itemBinding.containerProducto.setOnLongClickListener(view -> {
                listener.onLongItemClick(producto);
                return true;
            });
        }
    }

    //METODOS
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ItemProductoBinding itemBinding;
        itemBinding = ItemProductoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Producto producto = mProductos.get(position);

        holder.setListener(producto, listener);
        holder.itemBinding.tvNombreProducto.setText(producto.getNombre());

    }

    @Override
    public int getItemCount() {
        return this.mProductos.size();
    }

    public void add(Producto producto){
        if (!mProductos.contains(producto)) {
            mProductos.add(producto);
            notifyDataSetChanged();
        }
    }

    public void setList(List<Producto> productos) {
        this.mProductos = productos;
        notifyDataSetChanged();
    }

    public List<Producto> getList() {
        return mProductos;
    }

    public void filter(CharSequence textInBox, List<Producto> listCopy) {
        mProductos.clear();
        String text = String.valueOf(textInBox);
        if(text.isEmpty()){
            listCopy.get(0).getNombre();
            mProductos = listCopy;
        } else{
            text = text.toLowerCase();
            for(Producto item: listCopy){
                if(item.getNombre().toLowerCase().contains(text)){
                    mProductos.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
