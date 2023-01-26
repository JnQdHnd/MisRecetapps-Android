package curso.android.misrecetapps.controllers.receta.list;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;

import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.databinding.ProductoFilterBinding;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Receta;

public class RecetaListProductoFilterAdapter extends RecyclerView.Adapter<RecetaListProductoFilterAdapter.ViewHolder>{

    //ATRIBUTOS
    private Context mContext;
    private final RecetaViewModel mViewModel;
    private final RecetaListAdapter mListAdapter;
    private final AppCompatEditText mSerchBox;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaListProductoFilterAdapter(RecetaViewModel viewModel,
                                           RecetaListAdapter listAdapter,
                                           AppCompatEditText serchBox){
        this.mViewModel = viewModel;
        this.mListAdapter = listAdapter;
        this.mSerchBox = serchBox;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ProductoFilterBinding binding;
        private final TextView productoNombre;
        private final ImageButton removeButton;
        private Producto producto;

        ViewHolder(@NonNull ProductoFilterBinding b){
            super(b.getRoot());
            binding = b;
            productoNombre = b.productoFilterNombre;
            removeButton = b.removeProductoFilter;
        }

        public Producto getProducto() {
            return producto;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        ProductoFilterBinding binding;
        binding = ProductoFilterBinding.inflate(LayoutInflater.from(mContext));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Producto item = mViewModel.getProductosInFilter().get(position);
        holder.producto = item;
        holder.productoNombre.setText(item.getNombre());
        holder.removeButton.setOnClickListener(view -> {
            mViewModel.getProductosInFilter().remove(position);
            CharSequence searchText = mSerchBox.getText().toString().trim();
            mViewModel.filterByText(searchText, new RecetaDB().findAllComplete());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::isEsFavorita).reversed());
            }
            else{
                for(int i = 0; i < mViewModel.getmRecetasVisibles().size(); i++){
                    Receta r = mViewModel.getmRecetasVisibles().get(i);
                    if(r.isEsFavorita()){
                        mViewModel.getmRecetasVisibles().add(0, r);
                        mViewModel.getmRecetasVisibles().remove(i+1);
                    }
                }
            }
            notifyDataSetChanged();
            mListAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mViewModel.getProductosInFilter().size();
    }

}
