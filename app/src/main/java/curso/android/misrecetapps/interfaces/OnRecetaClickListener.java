package curso.android.misrecetapps.interfaces;

import android.view.View;

import curso.android.misrecetapps.model.Receta;

public interface OnRecetaClickListener {
    void onItemClick(Receta receta);
    void onLongItemClick(Receta receta, View view);
}
