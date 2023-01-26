package curso.android.misrecetapps.controllers.receta.details;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.databinding.DetallesArtefactoBinding;
import curso.android.misrecetapps.model.Artefacto;
import curso.android.misrecetapps.model.ArtefactoEnUso;

public class RecetaDetailsArtefactosAdapter extends RecyclerView.Adapter<RecetaDetailsArtefactosAdapter.ViewHolder>{

    //ATRIBUTOS
    private List<ArtefactoEnUso> list;
    private Context context;
    private final ArtefactoDB artefactoDB;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaDetailsArtefactosAdapter(List<ArtefactoEnUso> list){
        this.artefactoDB = new ArtefactoDB();
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final DetallesArtefactoBinding binding;
        private final TextView temperatura;
        private final TextView unidad;
        private Artefacto artefacto;

        ViewHolder(@NonNull DetallesArtefactoBinding b){
            super(b.getRoot());
            binding = b;
            temperatura = b.detallesArtefactosTemperatura;
            unidad = b.detallesArtefactosSelectUnidad;
        }

        public TextView getTemperatura() {return temperatura;}
        public TextView getUnidad() {return unidad;}
        public Artefacto getArtefacto() {return artefacto;}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        DetallesArtefactoBinding binding;
        binding = DetallesArtefactoBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ArtefactoEnUso item = list.get(position);
        if(item.getArtefactoId() != null){
            holder.artefacto = artefactoDB.findById(item.getArtefactoId());
            holder.binding.detallesArtefactosTexto.setText(holder.artefacto.getNombre());
            Log.i("VER-ARTEFACTO", "Artefacto: " + item);
            holder.binding.detallesArtefactosMinutos.setText(String.valueOf(item.getMinutosDeUso()));
            if(holder.artefacto.isEsHorno()){
                List<String> unidadesDeMedida = Auxiliar.configListUnidadesSimbolo(item.getUnidadDeTemperatura().getTipoDeMedida());
                holder.binding.detallesArtefactosLlIntensidad.setVisibility(View.GONE);
                holder.binding.detallesArtefactosLlTemperatura.setVisibility(View.VISIBLE);
                holder.binding.detallesArtefactosTemperatura.setText(Auxiliar.formateaCantidad(item.getTemperatura()));

                Auxiliar.configSelectConversor(
                        holder.binding.detallesArtefactosSelectUnidad,
                        unidadesDeMedida,
                        context,
                        holder.binding.detallesArtefactosTemperatura,
                        item);

                holder.binding.detallesArtefactosSelectUnidad.setText(item.getUnidadDeTemperatura().getSimbolo());
            }
            else{
                holder.binding.detallesArtefactosLlIntensidad.setVisibility(View.VISIBLE);
                holder.binding.detallesArtefactosLlTemperatura.setVisibility(View.GONE);
                holder.binding.detallesArtefactosIntensidad.setText(item.getIntensidadDeUso());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setList(List<ArtefactoEnUso> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<ArtefactoEnUso> getList() {
        return list;
    }

}
