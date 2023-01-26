package curso.android.misrecetapps.controllers.receta.list;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import curso.android.misrecetapps.R;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.RecetaDB;
import curso.android.misrecetapps.databinding.ItemRecetaBinding;
import curso.android.misrecetapps.interfaces.OnRecetaClickListener;
import curso.android.misrecetapps.model.Receta;

public class RecetaListAdapter extends RecyclerView.Adapter<RecetaListAdapter.ViewHolder> {

    //ATRIBUTOS

    private final RecetaViewModel mViewModel;
    private Context mContext;
    private final OnRecetaClickListener mListener;
    private final RecetaDB mRecetaDB = new RecetaDB();

    //CONSTRUCTOR ----------------------------------------------
    public RecetaListAdapter(RecetaViewModel viewModel, OnRecetaClickListener listener){
        this.mViewModel = viewModel;
        this.mListener = listener;

    }

    //VIEWHOLDER ----------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemRecetaBinding itemRecetaBinding;
        ViewHolder(@NonNull ItemRecetaBinding irb) {
            super(irb.getRoot());
            itemRecetaBinding = irb;
        }
        void setListener(final Receta receta, final OnRecetaClickListener listener){
            itemRecetaBinding.containerReceta.setOnClickListener(view -> listener.onItemClick(receta));

            itemRecetaBinding.containerReceta.setOnLongClickListener(view -> {
                listener.onLongItemClick(receta, view);
                return true;
            });

        }
    }

    //METODOS
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        ItemRecetaBinding itemRecetaBinding;
        itemRecetaBinding = ItemRecetaBinding.inflate(LayoutInflater.from(mContext));
        return new ViewHolder(itemRecetaBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Receta receta = mViewModel.getmRecetasVisibles().get(position);

        holder.setListener(receta, mListener);

        holder.itemRecetaBinding.tvNombreReceta.setText(receta.getNombre());
        if(receta.isEsFavorita()){
            holder.itemRecetaBinding.ivFavoritoReceta.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_full_36));
        }
        else{
            holder.itemRecetaBinding.ivFavoritoReceta.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_36));
        }

        holder.itemRecetaBinding.ivFavoritoReceta.setOnClickListener(view -> {
            if(receta.isEsFavorita()){
                receta.setEsFavorita(false);
                mRecetaDB.save(receta);
                holder.itemRecetaBinding.ivFavoritoReceta.setImageResource(R.drawable.ic_star_border_36);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::getNombre));
                    mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::isEsFavorita).reversed());
                }
                else{
                    Collections.sort(mViewModel.getmRecetasVisibles(), new Comparator<Receta>() {
                        @Override
                        public int compare(Receta r1, Receta r2) {
                            return r1.getNombre().compareTo(r2.getNombre());
                        }
                    });
                    for(int i = 0; i < mViewModel.getmRecetasVisibles().size(); i++){
                        Receta r = mViewModel.getmRecetasVisibles().get(i);
                        if(r.isEsFavorita()){
                            mViewModel.getmRecetasVisibles().add(0, r);
                            mViewModel.getmRecetasVisibles().remove(i+1);
                        }
                    }
                }
            }
            else {
                receta.setEsFavorita(true);
                mRecetaDB.save(receta);
                holder.itemRecetaBinding.ivFavoritoReceta.setImageResource(R.drawable.ic_star_full_36);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::getNombre));
                    mViewModel.getmRecetasVisibles().sort(Comparator.comparing(Receta::isEsFavorita).reversed());
                }
                else{
                    Collections.sort(mViewModel.getmRecetasVisibles(), new Comparator<Receta>() {
                        @Override
                        public int compare(Receta r1, Receta r2) {
                            return r1.getNombre().compareTo(r2.getNombre());
                        }
                    });
                    for(int i = 0; i < mViewModel.getmRecetasVisibles().size(); i++){
                        Receta r = mViewModel.getmRecetasVisibles().get(i);
                        if(r.isEsFavorita()){
                            mViewModel.getmRecetasVisibles().add(0, r);
                            mViewModel.getmRecetasVisibles().remove(i+1);
                        }
                    }
                }
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if(mViewModel.getmRecetasVisibles() == null){
            mViewModel.setmRecetasVisibles(mRecetaDB.findAllComplete());
        }
        return this.mViewModel.getmRecetasVisibles().size();
    }

    public void add(Receta receta){
        if (!mViewModel.getmRecetasVisibles().contains(receta)) {
            mViewModel.getmRecetasVisibles().add(receta);
            notifyDataSetChanged();
        }
    }

    public void setList(List<Receta> recetas) {
        this.mViewModel.setmRecetasVisibles(recetas);
        notifyDataSetChanged();
    }



}
