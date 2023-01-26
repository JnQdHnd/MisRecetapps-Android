package curso.android.misrecetapps.controllers.receta.form;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.controllers.artefacto.ArtefactoFormDialog;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.databinding.RecetaArtefactoCardBinding;
import curso.android.misrecetapps.model.Artefacto;
import curso.android.misrecetapps.model.ArtefactoEnUso;

public class RecetaFormArtefactoAdapter extends RecyclerView.Adapter<RecetaFormArtefactoAdapter.ViewHolder>{

    //ATRIBUTOS
    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView mInstruccionRecyclerView;
    private FragmentActivity mActivity;
    private FragmentManager mFragmentManager;
    private ArtefactoDB mItemsDB;
    private List<String> mUnidadesTemperaturaSimbolos;
    private List<String> mIntensidades;
    private RecetaViewModel mViewModel;
    private FloatingActionButton mFloatingBtn;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaFormArtefactoAdapter(RecetaViewModel viewModel,
                                      FragmentActivity fragmentActivity,
                                      RecyclerView instruccionRecyclerView,
                                      FloatingActionButton floatingActionButton){
        this.mItemsDB = new ArtefactoDB();
        this.mActivity = fragmentActivity;
        this.mFragmentManager = fragmentActivity.getSupportFragmentManager();
        this.mUnidadesTemperaturaSimbolos = Auxiliar.configListUnidadDeTemperatura();
        this.mIntensidades = Arrays.asList("Muy Baja", "Baja", "Media", "Alta", "Muy Alta");
        this.mViewModel = viewModel;
        this.mInstruccionRecyclerView = instruccionRecyclerView;
        this.mFloatingBtn = floatingActionButton;
        if(mViewModel.getReceta().getArtefactosEnUso() == null){
            viewModel.inicializaListArtefactosEnUso();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecetaArtefactoCardBinding binding;
        private FrameLayout card;
        private TextView title;
        private TextView selectArtefacto;
        private AppCompatEditText minutos;
        private TextView selectIntensidad;
        private AppCompatEditText temperatura;
        private TextView unidad;
        private ImageButton deleteButton;
        private FrameLayout containerMinTempInt;
        private LinearLayout llTemperatura;
        private RelativeLayout rlIntensidad;

        ViewHolder(@NonNull RecetaArtefactoCardBinding b){
            super(b.getRoot());
            binding = b;
            card = b.getRoot();
            title = b.recetaTitleCardArtefacto;
            selectArtefacto = b.selectArtefacto;
            minutos = b.inputMinutosArtefacto;
            selectIntensidad = b.selectIntensidad;
            temperatura = b.inputTemperatura;
            unidad = b.selectUnidadTemperatura;
            deleteButton = b.recetaCardArtefactoDelete;
            containerMinTempInt = b.artefactoMinTempIntContainer;
            llTemperatura = b.lyTemperatura;
            rlIntensidad = b.lyIntensidad;
        }

        public TextView getSelectArtefacto() { return selectArtefacto; }
        public AppCompatEditText getMinutos() { return minutos; }
        public TextView getSelectIntensidad() { return selectIntensidad; }
        public AppCompatEditText getTemperatura() { return temperatura; }
        public TextView getUnidad() { return unidad; }
        public TextView getTitle() { return title; }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        RecetaArtefactoCardBinding binding;
        binding = RecetaArtefactoCardBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int numeroDeVista = 0;

        ArtefactoEnUso item = mViewModel.getReceta().getArtefactosEnUso().get(position);

        holder.card.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.title.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.deleteButton.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.selectArtefacto.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.minutos.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.selectIntensidad.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.temperatura.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.unidad.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.llTemperatura.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.rlIntensidad.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.containerMinTempInt.setId(
                Auxiliar.BASE_ARTEFACTO + Auxiliar.BASE_VISTA * numeroDeVista++ + position);

        int num = position + 1;
        String titulo = String.format(context.getResources().getString(R.string.title_receta_card_artefacto), num);

        holder.title.setText(titulo);

        Auxiliar.configSelect(holder.selectArtefacto, context, mFragmentManager, new ArtefactoFormDialog());

        if(item.getArtefactoId() != null && item.getArtefactoId() > 0){
            Artefacto artefacto = mItemsDB.findById(item.getArtefactoId());
            holder.selectArtefacto.setText(artefacto.getNombre());
            holder.containerMinTempInt.setVisibility(View.VISIBLE);
            if(item.getMinutosDeUso() > 0){
                holder.minutos.setText(String.valueOf(item.getMinutosDeUso()));
            }
            boolean esHorno = artefacto.isEsHorno();
            if(esHorno){
                holder.rlIntensidad.setVisibility(View.GONE);
                holder.llTemperatura.setVisibility(View.VISIBLE);
                Auxiliar.configSelect(
                        holder.unidad,
                        mUnidadesTemperaturaSimbolos,
                        context);
                if(item.getTemperatura() > 0){
                    holder.temperatura.setText(Auxiliar.formateaCantidad(item.getTemperatura()));
                }
                if(item.getUnidadDeTemperatura() != null){
                    holder.unidad.setText(item.getUnidadDeTemperatura().getSimbolo());
                }
            }
            else{
                holder.rlIntensidad.setVisibility(View.VISIBLE);
                holder.llTemperatura.setVisibility(View.GONE);
                Auxiliar.configSelect(
                        holder.selectIntensidad,
                        mIntensidades,
                        context);
                if(item.getIntensidadDeUso() != null){
                    holder.selectIntensidad.setText(item.getIntensidadDeUso());
                }
            }
        }
        else{
            holder.selectArtefacto.setText(null);
            holder.containerMinTempInt.setVisibility(View.GONE);
            holder.minutos.setText(null);
            holder.temperatura.setText(null);
            holder.unidad.setText(null);
            holder.selectIntensidad.setText(null);
        }

        holder.selectArtefacto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(holder.selectArtefacto.getText() != null &&
                        !holder.selectArtefacto.getText().toString().trim().isEmpty()){
                    String itemSelected = holder.selectArtefacto.getText().toString();
                    Artefacto item = mItemsDB.findByNombre(itemSelected);
                    boolean esHorno = item.isEsHorno();
                    holder.containerMinTempInt.setVisibility(View.VISIBLE);
                    holder.selectArtefacto.clearFocus();
                    holder.minutos.requestFocus();
                    if (esHorno) {
                        holder.rlIntensidad.setVisibility(View.GONE);
                        holder.llTemperatura.setVisibility(View.VISIBLE);
                        Auxiliar.configSelect(
                                holder.unidad,
                                mUnidadesTemperaturaSimbolos,
                                context);
                        setNextFocus(holder.minutos, holder.temperatura);
                    }
                    else {
                        holder.rlIntensidad.setVisibility(View.VISIBLE);
                        holder.llTemperatura.setVisibility(View.GONE);
                        Auxiliar.configSelect(
                                holder.selectIntensidad,
                                mIntensidades,
                                context);
                        setNextFocus(holder.minutos, holder.selectIntensidad);
                    }
                }
            }
        });

        if (mViewModel.getReceta().getArtefactosEnUso().size() <= 1) {
            holder.deleteButton.setImageResource(R.drawable.ic_broom);
            holder.deleteButton.setOnClickListener(view -> {
                holder.selectArtefacto.setText(null);
                holder.containerMinTempInt.setVisibility(View.GONE);
                holder.minutos.setText(null);
                holder.temperatura.setText(null);
                holder.unidad.setText(null);
                holder.selectIntensidad.setText(null);
            });
        }
        else{
            holder.deleteButton.setImageResource(R.drawable.ic_trash_can);
            holder.deleteButton.setOnClickListener(view -> {
                conservaArtefactos(false);
                mViewModel.getReceta().getArtefactosEnUso().remove(position);
                notifyDataSetChanged();
            });
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        Auxiliar.configFocusSelect(holder.selectArtefacto, mActivity, mViewModel);
        Auxiliar.configFocusInput(holder.minutos, mFloatingBtn, mViewModel);
        Auxiliar.configFocusSelect(holder.selectIntensidad, mActivity, mViewModel);
        Auxiliar.configFocusInputStart(holder.temperatura, scale, mFloatingBtn, mViewModel);
        Auxiliar.configFocusSelectEnd(holder.unidad, scale, mActivity, mViewModel);

        setNextFocus(holder.selectArtefacto, holder.minutos);
        setNextFocus(holder.temperatura, holder.unidad, position);
        setNextFocus(holder.unidad, position);
        setNextFocus(holder.selectIntensidad, position);

        if(mViewModel.getmFieldToFocus() != null){
            if(mViewModel.getmFieldToFocus().getId() == holder.selectArtefacto.getId()){
                holder.selectArtefacto.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.minutos.getId()){
                holder.minutos.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.temperatura.getId()){
                holder.temperatura.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.unidad.getId()){
                holder.unidad.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.selectIntensidad.getId()){
                holder.selectIntensidad.requestFocus();
            }
        }

    }

    @Override
    public int getItemCount() {
        return this.mViewModel.getReceta().getArtefactosEnUso().size();
    }

    public void setList(List<ArtefactoEnUso> list) {
        this.mViewModel.getReceta().setArtefactosEnUso(list);
        notifyDataSetChanged();
    }

    public List<ArtefactoEnUso> getList() {
        return mViewModel.getReceta().getArtefactosEnUso();
    }

    public List<String> getListUnidades() {
        return mUnidadesTemperaturaSimbolos;
    }

    public List<String> getListIntensidades() {
        return mIntensidades;
    }

    public void conservaArtefactos(boolean isSaving){
        List<ArtefactoEnUso> artefactosEnUso = new ArrayList<>();
        for(int i = 0; i < getList().size(); i++){
            ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
            TextView artefacto = holder.getSelectArtefacto();
            AppCompatEditText etMinutos = holder.getMinutos();
            TextView tvIntensidad = holder.getSelectIntensidad();
            AppCompatEditText etTemperatura = holder.getTemperatura();
            TextView tvUnidad = holder.getUnidad();
            String nombre = artefacto.getText().toString().trim();
            String minutos = etMinutos.getText().toString().trim();
            String intensidad = tvIntensidad.getText().toString().trim();
            String temperatura = etTemperatura.getText().toString().trim();
            String unidad = tvUnidad.getText().toString().trim();

            ArtefactoEnUso artefactoEnUso = new ArtefactoEnUso();
            if(!artefactoEnBlanco(artefacto)){
                if(!nombre.isEmpty()){
                    Artefacto a = mItemsDB.findByNombre(nombre);
                    artefactoEnUso.setArtefactoId(a.getId());
                    boolean esHorno = a.isEsHorno();
                    if(!minutos.isEmpty()){
                        artefactoEnUso.setMinutosDeUso(minutos);
                    }
                    if(esHorno){
                        if(!temperatura.isEmpty()) {
                            artefactoEnUso.setTemperatura(Auxiliar.formatNumberToDouble(temperatura));
                        }
                        if(!unidad.isEmpty()) {
                            artefactoEnUso.setUnidadDeTemperaturaString(unidad);
                        }
                    }
                    else{
                        if(!intensidad.isEmpty()) {
                            artefactoEnUso.setIntensidadDeUso(intensidad);
                        }
                    }
                }
            }
            if(isSaving){
                if(!artefactoEnBlanco(artefacto)){
                    artefactosEnUso.add(artefactoEnUso);
                }
            }
            else{
                artefactosEnUso.add(artefactoEnUso);
            }
        }
        mViewModel.getReceta().setArtefactosEnUso(artefactosEnUso);
    }
    public boolean artefactoEnBlanco(@NonNull TextView artefacto) {
        boolean artefactoEnBlanco = false;
        if (artefacto.getText() == null){
            artefactoEnBlanco = true;
        }
        else if (artefacto.getText().toString().trim().isEmpty()) {
            artefactoEnBlanco = true;
        }
        return artefactoEnBlanco;
    }

    public void setNextFocus(@NonNull TextView currentField, TextView nextField){
        currentField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result) {
                case EditorInfo.IME_ACTION_DONE:
                    currentField.clearFocus();
                    nextField.requestFocus();
                    mViewModel.setmFieldToFocus(nextField);
                    break;
            }
            return false;
        });
    }

    public void setNextFocus(@NonNull TextView currentField, TextView nextField, int position){
        currentField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            int nextPosition = position + 1;
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result) {
                case EditorInfo.IME_ACTION_DONE:
                    currentField.clearFocus();
                    if(!nextField.getText().toString().isEmpty()){
                        if(nextPosition < this.getItemCount()){
                            TextView optionalNextField;
                            RecetaFormArtefactoAdapter.ViewHolder nextHolder = (RecetaFormArtefactoAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(nextPosition);
                            optionalNextField = nextHolder.getSelectArtefacto();
                            optionalNextField.requestFocus();
                            mViewModel.setmFieldToFocus(optionalNextField);
                        }
                        else{
                            TextView optionalNextField;
                            RecetaFormInstruccionAdapter.ViewHolder holder =
                                    (RecetaFormInstruccionAdapter.ViewHolder) mInstruccionRecyclerView
                                            .findViewHolderForAdapterPosition(0);
                            optionalNextField = holder.getInstruccion();
                            optionalNextField.requestFocus();
                            mViewModel.setmFieldToFocus(optionalNextField);
                        }
                    }
                    else {
                        nextField.requestFocus();
                        mViewModel.setmFieldToFocus(nextField);
                    }
                    break;
            }
            return false;
        });
    }

    public void setNextFocus(@NonNull TextView currentField, int position){
        int nextPosition = position + 1;
        currentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(currentField.getText() != null){
                    if(!currentField.getText().toString().isEmpty()){
                        if(nextPosition < getItemCount()){
                            TextView optionalNextField;
                            ViewHolder nextHolder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(nextPosition);
                            if(nextHolder != null){
                                optionalNextField = nextHolder.getSelectArtefacto();
                                currentField.clearFocus();
                                optionalNextField.requestFocus();
                                mViewModel.setmFieldToFocus(optionalNextField);
                            }
                        }
                        else{
                            TextView optionalNextField;
                            RecetaFormInstruccionAdapter.ViewHolder nextHolder =
                                    (RecetaFormInstruccionAdapter.ViewHolder) mInstruccionRecyclerView
                                            .findViewHolderForAdapterPosition(0);
                            if(nextHolder != null){
                                optionalNextField = nextHolder.getInstruccion();
                                currentField.clearFocus();
                                optionalNextField.requestFocus();
                                mViewModel.setmFieldToFocus(optionalNextField);
                            }
                        }
                    }
                }
            }
        });
    }
}
