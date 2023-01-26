package curso.android.misrecetapps.controllers.artefacto;

import androidx.lifecycle.ViewModel;

import curso.android.misrecetapps.model.Artefacto;

public class ArtefactoViewModel extends ViewModel {

    private Artefacto mArtefacto;

    public Artefacto getmArtefacto() {
        return mArtefacto;
    }

    public void setmArtefacto(Artefacto mArtefacto) {
        this.mArtefacto = mArtefacto;
    }

    public void clearAll() {
        this.mArtefacto = null;
    }
}
