package curso.android.misrecetapps.controllers.artefacto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import curso.android.misrecetapps.databinding.ItemArtefactoBinding;
import curso.android.misrecetapps.interfaces.OnArtefactoClickListener;
import curso.android.misrecetapps.model.Artefacto;

public class ArtefactoListAdapter extends RecyclerView.Adapter<ArtefactoListAdapter.ViewHolder> {

    //ATRIBUTOS
    private List<Artefacto> mArtefactos;
    private Context context;
    private final OnArtefactoClickListener listener;

    //CONSTRUCTOR ----------------------------------------------
    public ArtefactoListAdapter(List<Artefacto> artefactos, OnArtefactoClickListener listener){
        this.mArtefactos = artefactos;
        this.listener = listener;
    }

    //VIEWHOLDER ----------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemArtefactoBinding itemBinding;
        ViewHolder(@NonNull ItemArtefactoBinding irb) {
            super(irb.getRoot());
            itemBinding = irb;
        }
        void setListener(final Artefacto artefacto, final OnArtefactoClickListener listener){
            itemBinding.containerArtefacto.setOnClickListener(view -> listener.onItemClick(artefacto));
            itemBinding.containerArtefacto.setOnLongClickListener(view -> {
                listener.onLongItemClick(artefacto);
                return true;
            });
        }
    }

    //METODOS
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ItemArtefactoBinding itemBinding;
        itemBinding = ItemArtefactoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Artefacto artefacto = mArtefactos.get(position);

        holder.setListener(artefacto, listener);
        holder.itemBinding.tvNombreArtefacto.setText(artefacto.getNombre());

    }

    @Override
    public int getItemCount() {
        return this.mArtefactos.size();
    }

    public void add(Artefacto artefacto){
        if (!mArtefactos.contains(artefacto)) {
            mArtefactos.add(artefacto);
            notifyDataSetChanged();
        }
    }

    public void setList(List<Artefacto> artefactos) {
        this.mArtefactos = artefactos;
        notifyDataSetChanged();
    }

    public void filter(CharSequence textInBox, List<Artefacto> listCopy) {
        mArtefactos.clear();
        String text = String.valueOf(textInBox);
        if(text.isEmpty()){
            listCopy.get(0).getNombre();
            mArtefactos = listCopy;
        } else{
            text = text.toLowerCase();
            for(Artefacto item: listCopy){
                if(item.getNombre().toLowerCase().contains(text)){
                    mArtefactos.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
