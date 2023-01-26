package curso.android.misrecetapps.interfaces;


import curso.android.misrecetapps.model.Producto;

public interface OnProductoClickListener {
    void onItemClick(Producto producto);
    void onLongItemClick(Producto producto);
}
