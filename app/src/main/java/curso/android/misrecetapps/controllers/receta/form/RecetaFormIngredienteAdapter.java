package curso.android.misrecetapps.controllers.receta.form;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.controllers.producto.ProductoFormDialog;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.database.ProductoDB;
import curso.android.misrecetapps.databinding.RecetaIngredienteCardBinding;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.UnidadDeMedida;

public class RecetaFormIngredienteAdapter extends RecyclerView.Adapter<RecetaFormIngredienteAdapter.ViewHolder>{

    //ATRIBUTOS
    private Context context;
    private FragmentActivity mActivity;
    private FragmentManager mFragmentManager;
    private RecyclerView mRecyclerView;
    private ProductoDB mItemsDB;
    private List<String> mUnidadesDeMedida;
    private RecetaViewModel mViewModel;
    private RecyclerView mArtefactoRecyclerView;
    private FloatingActionButton mFloatingBtn;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaFormIngredienteAdapter(RecetaViewModel viewModel,
                                        @NonNull FragmentActivity activity,
                                        RecyclerView artefactoRecyclerView,
                                        FloatingActionButton floatingActionButton){
        this.mItemsDB = new ProductoDB();
        this.mActivity = activity;
        this.mFragmentManager = activity.getSupportFragmentManager();
        this.mViewModel = viewModel;
        this.mUnidadesDeMedida = Auxiliar.configListUnidadDeProducto();
        this.mArtefactoRecyclerView = artefactoRecyclerView;
        this.mFloatingBtn = floatingActionButton;
        if(mViewModel.getReceta().getIngredientes() == null){ viewModel.inicializaListIngredientes(); }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecetaIngredienteCardBinding binding;
        private FrameLayout card;
        private TextView title;
        private TextView selectProducto;
        private AppCompatEditText cantidad;
        private TextView selectUnidad;
        private ImageButton deleteButton;

        ViewHolder(@NonNull RecetaIngredienteCardBinding b){
            super(b.getRoot());
            binding = b;
            card = b.getRoot();
            title = b.recetaTitleCardIngrediente;
            selectProducto = b.selectProducto;
            cantidad = b.inputCantidad;
            selectUnidad = b.selectUnidadProducto;
            deleteButton = b.recetaCardIngredienteDelete;
        }

        public TextView getTitle() {return title;}
        public TextView getSelectProducto() {return selectProducto;}
        public AppCompatEditText getCantidad() {return cantidad;}
        public TextView getSelectUnidad() {return selectUnidad;}

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        RecetaIngredienteCardBinding binding;
        binding = RecetaIngredienteCardBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int numeroDeVista = 0;

        Ingrediente item = mViewModel.getReceta().getIngredientes().get(position);

        holder.card.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.title.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.deleteButton.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.selectProducto.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.cantidad.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.selectUnidad.setId(
                Auxiliar.BASE_INGREDIENTE + Auxiliar.BASE_VISTA * numeroDeVista++ + position);

        Log.i("ID INGREDIENTE", "Id" + position + ": " + holder.selectProducto.getId());
        Log.i("ID INGREDIENTE", "Id" + position + ": " + holder.cantidad.getId());

        int num = position + 1;
        String titulo = String.format(context.getResources().getString(R.string.title_receta_card_ingrediente), num);

        holder.title.setText(titulo);

        Auxiliar.configSelect(holder.selectProducto, context, mFragmentManager, new ProductoFormDialog());
        Auxiliar.configSelect(holder.selectUnidad, mUnidadesDeMedida, context);

        final float scale = context.getResources().getDisplayMetrics().density;
        Auxiliar.configFocusSelect(holder.selectProducto, mActivity, mViewModel);
        Auxiliar.configFocusInputStart(holder.cantidad, scale, mFloatingBtn, mViewModel);
        Auxiliar.configFocusSelectEnd(holder.selectUnidad, scale, mActivity, mViewModel);

        if(item.getProductoId() != null && item.getProductoId() > 0){
            holder.selectProducto.setText(Auxiliar.getProductoNombreById(item.getProductoId(), mItemsDB.findAll()));
        }
        else{
            holder.selectProducto.setText(null);
        }
        if(item.getCantidad() > 0){
            holder.cantidad.setText(Auxiliar.formateaCantidad(item.getCantidad()));
        }
        else{
            holder.cantidad.setText(null);
        }
        if(item.getUnidadDeMedida() != null){
            holder.selectUnidad.setText(item.getUnidadDeMedida().getNombre());
        }
        else{
            holder.selectUnidad.setText(null);
        }

        holder.selectProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String itemSelected = holder.selectProducto.getText().toString();
                for (Producto p : mItemsDB.findAll()) {
                    if (itemSelected.equals(p.getNombre())) {
                        UnidadDeMedida unidadDeMedida = p.getUnidadDeMedida();
                        Auxiliar.configSelect(holder.selectUnidad, Auxiliar.configListUnidadesByTipo(unidadDeMedida.getTipoDeMedida()), context);
                        holder.selectUnidad.setTextColor(ContextCompat.getColor(context, R.color.primary_carbon));
                        holder.selectUnidad.setText(unidadDeMedida.getNombre());
                        holder.selectProducto.clearFocus();
                        holder.cantidad.requestFocus();
                        break;
                    }
                }
            }
        });

        if (mViewModel.getReceta().getIngredientes().size() <= 1) {
            holder.deleteButton.setImageResource(R.drawable.ic_broom);
            holder.deleteButton.setOnClickListener(view -> {
                holder.selectProducto.setText(null);
                holder.cantidad.setText(null);
                holder.selectUnidad.setText(null);
            });
        }
        else{
            holder.deleteButton.setImageResource(R.drawable.ic_trash_can);
            holder.deleteButton.setOnClickListener(view -> {
                Log.i("DELETE INGREDIENTE", "position: " + position);
                conservaIngredientes();
                mViewModel.getReceta().getIngredientes().remove(position);
                notifyDataSetChanged();
            });
        }
        setNextFocus(holder.cantidad, holder.selectUnidad, position);

        if(mViewModel.getmFieldToFocus() != null){
            if(mViewModel.getmFieldToFocus().getId() == holder.selectProducto.getId()){
                holder.selectProducto.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.cantidad.getId()){
                holder.cantidad.requestFocus();
            }
            else if(mViewModel.getmFieldToFocus().getId() == holder.selectUnidad.getId()){
                holder.selectUnidad.requestFocus();
            }
        }

    }

    @Override
    public int getItemCount() {
        return this.mViewModel.getReceta().getIngredientes().size();
    }

    public void setList(List<Ingrediente> list) {
        this.mViewModel.getReceta().setIngredientes(list);
        notifyDataSetChanged();
    }

    public List<Ingrediente> getList() {
        return mViewModel.getReceta().getIngredientes();
    }

    public List<String> getListUnidades() {
        return mUnidadesDeMedida;
    }

    public void conservaIngredientes() {
        List<Ingrediente> ingredientes = new ArrayList<>();

        for(int i = 0; i < getList().size(); i++){
            ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
            TextView tvProducto = holder.getSelectProducto();
            AppCompatEditText etCantidad = holder.getCantidad();
            TextView tvUnidad = holder.getSelectUnidad();
            String producto = tvProducto.getText().toString().trim();
            String cantidad = etCantidad.getText().toString().trim();
            Log.i("CANTIDAD", "conservaIngredientes: " + cantidad);
            double doubleCantidad = Auxiliar.formatNumberToDouble(etCantidad.getText().toString().trim());
            Log.i("CANTIDAD", "conservaIngredientes: " + doubleCantidad);
            String unidadDeMedida = tvUnidad.getText().toString().trim();
            long idProducto = Auxiliar.getProductoIdByNombre(producto, mItemsDB.findAll());
            Ingrediente ingrediente = new Ingrediente();
            if(!ingredienteEnBlanco(tvProducto, etCantidad, tvUnidad)){
                if(!producto.isEmpty()){
                    ingrediente.setProductoId(idProducto);
                }
                if(doubleCantidad > 0){
                    ingrediente.setCantidad(doubleCantidad);
                }
                if(!unidadDeMedida.isEmpty()){
                    ingrediente.setUnidadDeMedidaString(unidadDeMedida);
                }
            }
            ingredientes.add(ingrediente);
        }
        mViewModel.getReceta().setIngredientes(ingredientes);
    }

    public boolean ingredienteEnBlanco(@NonNull TextView ingrediente, AppCompatEditText cantidad, TextView unidad) {
        boolean ingredienteEnBlanco = false;
        if (ingrediente.getText() == null || ingrediente.getText().toString().trim().isEmpty()){
            if(cantidad.getText() == null  || cantidad.getText().toString().trim().isEmpty()){
                if(unidad.getText() == null || unidad.getText().toString().trim().isEmpty()){
                    ingredienteEnBlanco = true;
                }
            }
        }
        return ingredienteEnBlanco;
    }


    public void setNextFocus(@NonNull TextView currentField, TextView nextField, int position){
        currentField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            TextView optionalNextField;
            int nextPosition = position + 1;
            int result = actionId & EditorInfo.IME_MASK_ACTION;
            switch(result) {
                case EditorInfo.IME_ACTION_DONE:
                    currentField.clearFocus();
                    if(!nextField.getText().toString().isEmpty()){
                        if(nextPosition < this.getItemCount()){
                            ViewHolder nextHolder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(nextPosition);
                            optionalNextField = nextHolder.getSelectProducto();
                            optionalNextField.requestFocus();
                        }
                        else{

                            RecetaFormArtefactoAdapter.ViewHolder holder =
                                    (RecetaFormArtefactoAdapter.ViewHolder) mArtefactoRecyclerView
                                            .findViewHolderForAdapterPosition(0);
                            optionalNextField = holder.getSelectArtefacto();
                            optionalNextField.requestFocus();
                        }
                    }
                    else {
                        nextField.requestFocus();
                    }
                    mViewModel.setmFieldToFocus(nextField);
                    break;
            }
            return false;
        });
    }
}
