package curso.android.misrecetapps.controllers.receta.details;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;

import curso.android.misrecetapps.R;
import curso.android.misrecetapps.databinding.DetallesInstruccionBinding;
import curso.android.misrecetapps.model.Instruccion;

public class RecetaDetailsInstruccionesAdapter extends RecyclerView.Adapter<RecetaDetailsInstruccionesAdapter.ViewHolder>{
    //ATRIBUTOS
    private List<Instruccion> list;
    private Context context;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaDetailsInstruccionesAdapter(List<Instruccion> list){
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final DetallesInstruccionBinding binding;

        private final RelativeLayout rowTitle;
        private final TextView title;
        private final TextView text;
        private final AppCompatImageView photo;

        ViewHolder(@NonNull DetallesInstruccionBinding b){
            super(b.getRoot());
            binding = b;
            rowTitle = b.detallesInstruccionLlTitulo;
            title = b.detallesInstruccionTitulo;
            text = b.detallesInstruccionTexto;
            photo = b.detallesInstruccionPhoto;
        }

        public DetallesInstruccionBinding getBinding() {return binding;}
        public TextView getTitle() {return title;}
        public TextView getText() {return text;}

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        DetallesInstruccionBinding binding;
        binding = DetallesInstruccionBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Instruccion item = list.get(position);

        holder.title.setId(View.generateViewId());
        holder.text.setId(View.generateViewId());
        holder.photo.setId(View.generateViewId());

        if(item.getOrden() == null || item.getOrden() == 0){
            if(list.size() == 1){
                holder.rowTitle.setVisibility(View.GONE);
                holder.text.setHint(R.string.sin_instrucciones);
            }
        }
        else {
            holder.title.setText(String.format(
                    context.getResources().getString(R.string.title_receta_card_instruccion),
                    item.getOrden()));
            if(item.getInstruccion() != null && !item.getInstruccion().isEmpty()){
                holder.text.setText(item.getInstruccion());
            }
            else{
                holder.text.setVisibility(View.GONE);
            }
            String photoName = item.getFoto();
            if(photoName != null){

                File directorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File photoFile = new File(directorio + "/" + File.separator + photoName);
                Log.i("LOAD-PHOTO", "Cargando foto desde path: " + photoName);
                holder.photo.setVisibility(View.VISIBLE);
                Glide
                        .with(context)
                        .load(photoFile)
                        .override(0, 250)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.photo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(List<Instruccion> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Instruccion> getList() {
        return list;
    }

}
