package curso.android.misrecetapps.controllers.receta;

import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.controllers.receta.form.RecetaFormFragment;
import curso.android.misrecetapps.controllers.receta.form.RecetaFormInstruccionAdapter;
import curso.android.misrecetapps.model.ArtefactoEnUso;
import curso.android.misrecetapps.model.Ingrediente;
import curso.android.misrecetapps.model.Instruccion;
import curso.android.misrecetapps.model.Producto;
import curso.android.misrecetapps.model.Receta;

public class RecetaViewModel extends ViewModel {

    //ATRIBUTOS
    public final int ACTIVE_FRAGMENT_LIST = 0;
    public final int ACTIVE_FRAGMENT_FORM = 1;
    public final int ACTIVE_FRAGMENT_DETAILS = 2;
    private int mActiveFragment;
    private Receta mReceta;
    private List<Producto> mProductos;
    private List<Producto> mProductosInFilter;
    private List<Receta> mRecetasVisibles;
    private Integer mPhotoOriginTemp;
    private RecetaFormInstruccionAdapter mAdapterTemp;
    private RecetaFormFragment mFragmentTemp;
    private Integer mPositionTemp;
    private boolean mFilterBarVisible = false;
    private boolean mPdfDownloaded = false;
    private boolean mIsListening = false;
    private AppCompatImageButton mImportButton;
    private AppCompatImageButton mSpeekerButton;
    private View mFieldToFocus;

    //METODOS
    public List<Producto> getProductos() {
        return mProductos;
    }

    public void setProductos(List<Producto> mProductos) {
        this.mProductos = mProductos;
    }

    public Receta getReceta() {
        return mReceta;
    }

    public void setReceta(Receta receta) {
        this.mReceta = receta;
    }

    public Integer getmPhotoOriginTemp() {
        return mPhotoOriginTemp;
    }

    public void setmPhotoOriginTemp(Integer mPhotoOriginTemp) {
        this.mPhotoOriginTemp = mPhotoOriginTemp;
    }

    public Integer getmPositionTemp() {
        return mPositionTemp;
    }

    public void setmPositionTemp(Integer mPositionTemp) {
        this.mPositionTemp = mPositionTemp;
    }

    public RecetaFormInstruccionAdapter getmAdapterTemp() {
        return mAdapterTemp;
    }

    public void setmAdapterTemp(RecetaFormInstruccionAdapter mAdapterTemp) {
        this.mAdapterTemp = mAdapterTemp;
    }

    public RecetaFormFragment getmFragmentTemp() {
        return mFragmentTemp;
    }

    public void setmFragmentTemp(RecetaFormFragment mFragmentTemp) {
        this.mFragmentTemp = mFragmentTemp;
    }

    public void inicializaReceta(){
        mReceta = new Receta();
    }

    public int getActiveFragment() {
        return mActiveFragment;
    }

    public void setActiveFragment(int activeFragment) {
        this.mActiveFragment = activeFragment;
    }

    public List<Producto> getProductosInFilter() {
        return mProductosInFilter;
    }

    public void setProductosInFilter(List<Producto> mProductosInFilter) {
        this.mProductosInFilter = mProductosInFilter;
    }

    public boolean isFilterBarVisible() {
        return mFilterBarVisible;
    }

    public void setFilterBarVisible(boolean mFilterBarVisible) {
        this.mFilterBarVisible = mFilterBarVisible;
    }

    public List<Receta> getmRecetasVisibles() {
        return mRecetasVisibles;
    }

    public void setmRecetasVisibles(List<Receta> mRecetasVisibles) {
        this.mRecetasVisibles = mRecetasVisibles;
    }

    public void inicializaListIngredientes(){
        Ingrediente ingrediente = new Ingrediente();
        mReceta.setIngredientes(new ArrayList<>());
        mReceta.getIngredientes().add(ingrediente);
    }

    public void inicializaListArtefactosEnUso(){
        ArtefactoEnUso artefactoEnUso = new ArtefactoEnUso();
        mReceta.setArtefactosEnUso(new ArrayList<>());
        mReceta.getArtefactosEnUso().add(artefactoEnUso);
    }

    public void inicializaListInstrucciones(){
        Instruccion instruccion = new Instruccion();
        mReceta.setInstrucciones(new ArrayList<>());
        mReceta.getInstrucciones().add(instruccion);
    }

    public void clearAll() {
        Log.i("RECETA VIEWMODEL", "ClearAll()");
        this.mReceta = null;
        this.mProductos = null;
        this.mPhotoOriginTemp = null;
        this.mAdapterTemp = null;
        this.mFragmentTemp = null;
        this.mPositionTemp = null;
    }

    public boolean existInProductosInFilter(String nombreDeProducto){
        for (Producto p : mProductosInFilter) {
            if(p.getNombre().equalsIgnoreCase(nombreDeProducto)){
                return true;
            }
        }
        return false;
    }

    public void filterRecetasVisiblesByProductosInFilter() {
        List<Receta> listTemp = new ArrayList<>();
        for(Receta receta: mRecetasVisibles){
            boolean recetaEnListado = false;
            for(Ingrediente ingrediente : receta.getIngredientes()){
                if(!recetaEnListado){
                    for (Producto producto: mProductosInFilter) {
                        if(ingrediente.getProductoId() == producto.getId()){
                            listTemp.add(receta);
                            recetaEnListado = true;
                            break;
                        }
                    }
                }
                else{
                    break;
                }
            }
        }
        setmRecetasVisibles(listTemp);
    }

    public void filterByText(CharSequence textInBox, List<Receta> listCopy) {
        mRecetasVisibles.clear();
        String text = String.valueOf(textInBox);
        if(text.isEmpty()){
            setmRecetasVisibles(listCopy);
        } else{
            text = text.toLowerCase();
            for(Receta item: listCopy){
                if(item.getNombre().toLowerCase().contains(text)){
                    mRecetasVisibles.add(item);
                }
            }
        }
        if(mProductosInFilter != null){
            if(mProductosInFilter.size() > 0){
                filterRecetasVisiblesByProductosInFilter();
            }
        }
    }

    public boolean isPdfDownloaded() {
        return mPdfDownloaded;
    }

    public void setPdfDownloaded(boolean mPdfDownloaded) {
        this.mPdfDownloaded = mPdfDownloaded;
    }

    public AppCompatImageButton getmImportButton() {
        return mImportButton;
    }

    public void setmImportButton(AppCompatImageButton mImportButton) {
        this.mImportButton = mImportButton;
    }

    public boolean isListening() {
        return mIsListening;
    }

    public void setIsListening(boolean mIsListening) {
        this.mIsListening = mIsListening;
    }

    public AppCompatImageButton getSpeekerButton() {
        return mSpeekerButton;
    }

    public void setSpeekerButton(AppCompatImageButton mSpeekerButton) {
        this.mSpeekerButton = mSpeekerButton;
    }

    public View getmFieldToFocus() {
        return mFieldToFocus;
    }

    public void setmFieldToFocus(View mFieldToFocus) {
        this.mFieldToFocus = mFieldToFocus;
    }
}
