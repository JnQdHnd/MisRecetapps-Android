package curso.android.misrecetapps.controllers.artefacto;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.R;
import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.databinding.FragmentArtefactoListBinding;
import curso.android.misrecetapps.interfaces.OnArtefactoClickListener;
import curso.android.misrecetapps.model.Artefacto;

public class ArtefactoListFragment extends Fragment implements OnArtefactoClickListener {

    private FragmentArtefactoListBinding binding;
    private RecyclerView mRecyclerView;
    private ArtefactoListAdapter mArtefactoListAdapter;
    private ArtefactoViewModel mViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentArtefactoListBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(ArtefactoViewModel.class);
        configAdapter();
        configAdapterView();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fabNewArtefacto.setOnClickListener(view1 -> {
            mViewModel.clearAll();
            NavHostFragment.findNavController(ArtefactoListFragment.this)
                    .navigate(R.id.action_ArtefactoList_to_ArtefactoForm);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void configAdapter(){
        mArtefactoListAdapter = new ArtefactoListAdapter(new ArrayList<>(), this);
    }

    private void configAdapterView(){
        mRecyclerView = binding.recyclerViewArtefactos;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRecyclerView.setAdapter(mArtefactoListAdapter);
    }

    @Override
    public void onItemClick(Artefacto artefacto) {
        mViewModel.setmArtefacto(artefacto);
        NavHostFragment.findNavController(ArtefactoListFragment.this)
                .navigate(R.id.action_ArtefactoList_to_ArtefactoForm);
    }

    @Override
    public void onLongItemClick(Artefacto artefacto) {
    }

    @Override
    public void onResume() {
        super.onResume();
        ArtefactoDB artefactoDB = new ArtefactoDB();
        mArtefactoListAdapter.setList(artefactoDB.findAll());
        configSerchBox();
    }

    public void configSerchBox(){
        int cantidadDeItems = mArtefactoListAdapter.getItemCount();
        int numMin = 10;
        if(cantidadDeItems <= numMin){
            binding.serchBox.setVisibility(View.GONE);
        }
        else{
            binding.serchBox.setVisibility(View.VISIBLE);
            binding.serchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<Artefacto> listCopy = new ArtefactoDB().findAll();
                    mArtefactoListAdapter.filter(s, listCopy);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }
}