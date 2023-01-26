package curso.android.misrecetapps.controllers.producto;

import androidx.lifecycle.ViewModel;
import curso.android.misrecetapps.model.Producto;

public class ProductoViewModel extends ViewModel {

    private Producto mProducto;

    public Producto getmProducto() {
        return mProducto;
    }

    public void setmProducto(Producto mProducto) {
        this.mProducto = mProducto;
    }

    public void clearAll() {
        this.mProducto = null;
    }
}
