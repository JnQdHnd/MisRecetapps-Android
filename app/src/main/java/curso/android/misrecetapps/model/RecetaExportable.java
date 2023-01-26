package curso.android.misrecetapps.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import curso.android.misrecetapps.database.ArtefactoDB;
import curso.android.misrecetapps.database.ProductoDB;

public class RecetaExportable {

    private Receta mReceta;
    List <Producto> mProductos;
    List <Artefacto> mArtefactos;
    private final ProductoDB productoDB = new ProductoDB();
    private final ArtefactoDB artefactoDB = new ArtefactoDB();

    public RecetaExportable(Receta receta) {
        Log.i("RecetaExportable", "GENERANDO RecetaExportable");
        this.mReceta = receta;
        this.mProductos = new ArrayList<>();
        this.mArtefactos = new ArrayList<>();
        setIngredientesProductos(receta);
        setArtefactosEnUsoArtefactos(receta);
    }

    private void setIngredientesProductos(@NonNull Receta receta){
        if(receta.getIngredientes() != null){
            if(receta.getIngredientes().size() > 0){
                for (Ingrediente ingrediente : receta.getIngredientes()){
                    if(ingrediente.getId() != null){
                        long idProducto = ingrediente.getProductoId();
                        Producto producto = productoDB.findById(idProducto);
                        mProductos.add(producto);
                    }
                }
            }
        }
    }

    private void setArtefactosEnUsoArtefactos(@NonNull Receta receta){
        if(receta.getArtefactosEnUso() != null){
            if(receta.getArtefactosEnUso().size() > 0){
                for (ArtefactoEnUso artefactoEnUso : receta.getArtefactosEnUso()){
                    if(artefactoEnUso.getArtefactoId() != null){
                        long idArtefacto = artefactoEnUso.getArtefactoId();
                        Artefacto artefacto = artefactoDB.findById(idArtefacto);
                        mArtefactos.add(artefacto);
                    }
                }
            }
        }
    }

    public Receta getmReceta() {
        return mReceta;
    }

    public List<Producto> getmProductos() {
        return mProductos;
    }

    public List<Artefacto> getmArtefactos() {
        return mArtefactos;
    }

}
