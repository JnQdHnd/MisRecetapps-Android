package curso.android.misrecetapps.controllers.receta.form;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import curso.android.misrecetapps.Auxiliar;
import curso.android.misrecetapps.R;
import curso.android.misrecetapps.activities.PhotoTaker;
import curso.android.misrecetapps.activities.VoiceToTextListener;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.databinding.RecetaInstruccionCardBinding;
import curso.android.misrecetapps.databinding.SelectPhotoSourceBinding;
import curso.android.misrecetapps.model.Instruccion;

public class RecetaFormInstruccionAdapter extends RecyclerView.Adapter<RecetaFormInstruccionAdapter.ViewHolder>{

    //ATRIBUTOS
    static final int REQUEST_RECORD_VOICE = 1;
    private Context context;
    private FragmentActivity activity;
    private RecyclerView mRecyclerView;
    private SpeechRecognizer mSpeechRecognizer;
    private PhotoTaker mPhotoTaker;
    private RecetaViewModel mViewModel;
    private File mDirectorio;
    private FloatingActionButton mFloatingBtn;
    private SelectPhotoSourceBinding bindingPhotoSource;

    //CONSTRUCTOR ----------------------------------------------
    public RecetaFormInstruccionAdapter(RecetaViewModel viewModel, SpeechRecognizer speechRecognizer,
                                        PhotoTaker takePictureObserver,
                                        FragmentActivity activity,
                                        FloatingActionButton floatingActionButton,
                                        SelectPhotoSourceBinding bindingPhotoSource){
        this.mSpeechRecognizer = speechRecognizer;
        this.mPhotoTaker = takePictureObserver;
        this.activity = activity;
        this.mViewModel = viewModel;
        this.mFloatingBtn = floatingActionButton;
        this.bindingPhotoSource = bindingPhotoSource;
        if(mViewModel.getReceta().getInstrucciones() == null){ viewModel.inicializaListInstrucciones(); }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecetaInstruccionCardBinding binding;
        private FrameLayout card;
        private TextView title;
        private ImageButton deleteButton;
        private ImageButton photoButton;
        private ImageButton micButton;
        private AppCompatEditText instruccion;
        private CardView photoCardView;
        private AppCompatImageView photoImageView;
        private AppCompatImageButton instruccionCheck;

        ViewHolder(@NonNull RecetaInstruccionCardBinding b){
            super(b.getRoot());
            binding = b;
            card = b.getRoot();
            title = b.recetaTitleCardInstruccion;
            deleteButton = b.recetaCardInstruccionDelete;
            photoButton = b.recetaCardInstruccionPhoto;
            micButton = b.recetaCardInstruccionMic;
            instruccion = b.inputInstruccion;
            photoCardView = b.cardImageInstruccion;
            photoImageView = b.imageInstruccion;
            instruccionCheck = b.instruccionCheck;
        }

        public TextView getTitle() { return title; }
        public AppCompatEditText getInstruccion() { return instruccion; }
        public AppCompatImageView getPhotoImageView() { return photoImageView; }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        this.mDirectorio = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        RecetaInstruccionCardBinding binding;
        binding = RecetaInstruccionCardBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int numeroDeVista = 0;

        Instruccion item = mViewModel.getReceta().getInstrucciones().get(position);

        holder.card.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.title.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.deleteButton.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.photoButton.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.micButton.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.instruccion.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.photoCardView.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.photoImageView.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);
        holder.instruccionCheck.setId(
                Auxiliar.BASE_INSTRUCCION + Auxiliar.BASE_VISTA * numeroDeVista++ + position);

        int num = position + 1;
        String titulo = String.format(context.getResources().getString(R.string.title_receta_card_instruccion), num);

        holder.title.setText(titulo);

        if (mViewModel.getReceta().getInstrucciones().size() <= 1) {
            holder.deleteButton.setImageResource(R.drawable.ic_broom);
            holder.deleteButton.setOnClickListener(view -> {
                holder.instruccion.setText("");
                holder.instruccion.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                if(item.getFoto() != null){
                    Auxiliar.deletePhoto(context, item.getFoto());
                    mViewModel.getReceta().getInstrucciones().get(position).setFoto(null);
                    holder.photoButton.setImageResource(R.drawable.ic_camera);
                    if(holder.photoCardView.getVisibility() == View.VISIBLE){
                        holder.photoCardView.setVisibility(View.GONE);
                    }
                    configTakePhotoButton(holder.photoButton, position);
                }
            });
        }
        else{
            holder.deleteButton.setImageResource(R.drawable.ic_trash_can);
            holder.deleteButton.setOnClickListener(view -> {
                conservaInstrucciones(false);
                mViewModel.getReceta().getInstrucciones().remove(position);
                notifyDataSetChanged();
            });
        }

        configMicButton(holder.micButton, holder.instruccion);

        if(item.getInstruccion() != null){
            if(!item.getInstruccion().isEmpty()){
                holder.instruccion.setText(item.getInstruccion());
            }
            else{
                holder.instruccion.setText("");
            }
        }
        else{
            holder.instruccion.setText("");
        }

        if(item.getFoto() != null){
            loadPhoto(holder.photoCardView, context, item.getFoto(), holder.photoImageView, holder.instruccion);
            configDeletePhotoButton(holder.photoButton, item.getFoto(), position, holder.photoCardView, holder.instruccion);

            Log.i("PHOTO", "Visibility: LANDSCAPE = " + Configuration.ORIENTATION_LANDSCAPE);
            int orientation = activity.getResources().getConfiguration().orientation;
            Log.i("PHOTO", "Visibility: ORIENTATION = " + orientation);
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                Log.i("PHOTO", "Visibility: LANDSCAPE");
                ViewGroup.LayoutParams params = holder.photoImageView.getLayoutParams();
                int height = params.height;
                holder.instruccion.setHeight(height);
            }
            else {
                Log.i("PHOTO", "Visibility: PORTRAIT");
            }
        }
        else{
            holder.photoButton.setImageResource(R.drawable.ic_camera);
            if(holder.photoCardView.getVisibility() == View.VISIBLE){
                holder.photoCardView.setVisibility(View.GONE);
                holder.instruccion.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            holder.photoImageView.setImageDrawable(null);
            configTakePhotoButton(holder.photoButton, position);
        }

        holder.instruccion.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                mFloatingBtn.setVisibility(View.GONE);
                holder.instruccion.setBackgroundResource(R.drawable.background_square_white_bordered_selected);
                holder.instruccionCheck.setVisibility(View.VISIBLE);
                mViewModel.setmFieldToFocus(holder.instruccion);
            }
            else{
                holder.instruccion.setBackgroundResource(R.drawable.background_square_white_bordered);
                holder.instruccionCheck.setVisibility(View.GONE);
                mFloatingBtn.setVisibility(View.VISIBLE);
            }
        });

        int nextPosition = position + 1;
        holder.instruccionCheck.setOnClickListener(view -> {
            holder.instruccion.clearFocus();
            if(nextPosition < getItemCount()){
                ViewHolder nextHolder =
                        (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(nextPosition);
                nextHolder.instruccion.requestFocus();
            }
            else{
                Auxiliar.hideKeyboard(activity);
            }
        });

        if(mViewModel.getmFieldToFocus() != null){
            if(mViewModel.getmFieldToFocus().getId() == holder.instruccion.getId()){
                holder.instruccion.requestFocus();
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.mViewModel.getReceta().getInstrucciones().size();
    }

    public void setList(List<Instruccion> list) {
        this.mViewModel.getReceta().setInstrucciones(list);
        notifyDataSetChanged();
    }

    public List<Instruccion> getList() {
        return mViewModel.getReceta().getInstrucciones();
    }

    private void configTakePhotoButton(@NonNull ImageButton photoButton, int position) {

        View popupView = bindingPhotoSource.getRoot();
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        photoButton.setOnClickListener(view -> {
            mViewModel.setmAdapterTemp(this);
            mViewModel.setmPhotoOriginTemp(PhotoTaker.PHOTO_LOCATED_IN_INSTRUCCION);
            mViewModel.setmPositionTemp(position);
            conservaInstrucciones(false);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        });

        bindingPhotoSource.getRoot().setOnClickListener(view -> popupWindow.dismiss());

        bindingPhotoSource.takePicture.setOnClickListener(view -> {
            popupWindow.dismiss();
            mPhotoTaker.takePicture();
        });

        bindingPhotoSource.importPhoto.setOnClickListener(view -> {
            popupWindow.dismiss();
            mPhotoTaker.importPhoto();
        });

    }

    private void configDeletePhotoButton(@NonNull ImageButton photoButton,
                                         String photoFileName,
                                         int position,
                                         CardView photoCardView,
                                         AppCompatEditText instrucciones) {

        photoButton.setImageResource(R.drawable.ic_image_remove_outline);
        photoButton.setOnClickListener(view -> {
            Auxiliar.deletePhoto(context, photoFileName);
            mViewModel.getReceta().getInstrucciones().get(position).setFoto(null);
            photoButton.setImageResource(R.drawable.ic_camera);
            if(photoCardView.getVisibility() == View.VISIBLE){
                photoCardView.setVisibility(View.GONE);
                instrucciones.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            }
            configTakePhotoButton(photoButton, position);
        });
    }

    private void configMicButton(ImageButton buttonMic, AppCompatEditText editText) {
        Log.i("Config MIC", "Configurando microfono button...");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            buttonMic.setOnClickListener(view -> {
                mSpeechRecognizer.setRecognitionListener(new VoiceToTextListener(editText, buttonMic));
                mSpeechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                activaIconoEscucha(buttonMic);
            });
        } else {
            Log.i("VOICE_TO_TEXT_ANULADO", "Anulando Speech Recognicer" + Build.VERSION.SDK_INT);
            buttonMic.setEnabled(false);
            buttonMic.setVisibility(View.GONE);
        }
    }

    public void activaIconoEscucha(@NonNull ImageButton buttonMic){
        buttonMic.setImageResource(R.drawable.avd_ear_hearing_20);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AnimatedVectorDrawable earIcon = (AnimatedVectorDrawable) buttonMic.getDrawable();
            earIcon.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    earIcon.start();
                }
            });
            earIcon.start();
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_VOICE);
        }
    }

    public void conservaInstrucciones(boolean isSaving) {
        long orden = 1;
        List<Instruccion> instrucciones = new ArrayList<>();
        for(int i = 0; i < getList().size(); i++){
            ViewHolder holder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
            AppCompatEditText etInstruccionTexto = holder.getInstruccion();
            AppCompatImageView ivInstruccionFoto = holder.getPhotoImageView();
            String instruccionText = etInstruccionTexto.getText().toString().trim();
            Instruccion instruccion = new Instruccion();
            if(!instruccionEnBlanco(etInstruccionTexto, ivInstruccionFoto)){
                instruccion.setInstruccion(instruccionText);
                String photoFileName = mViewModel.getReceta().getInstrucciones().get(i).getFoto();
                if (isSaving){
                    if(photoFileName != null && !photoFileName.isEmpty()){
                        instruccion.setFoto(Auxiliar.renombrarArchivo(context, photoFileName));
                    }
                    else{
                        instruccion.setFoto(photoFileName);
                    }
                }
                else{
                    instruccion.setFoto(photoFileName);
                }
            }
            if(isSaving){
                if(!instruccionEnBlanco(etInstruccionTexto, ivInstruccionFoto)){
                    instruccion.setOrden(orden);
                    orden++;
                    instrucciones.add(instruccion);
                }
            }
            else{
                instruccion.setOrden(orden);
                orden++;
                instrucciones.add(instruccion);
            }
        }
        mViewModel.getReceta().setInstrucciones(instrucciones);
    }

    public boolean instruccionEnBlanco(@NonNull AppCompatEditText instruccion, AppCompatImageView image) {
        boolean instruccionEnBlanco = false;
        if (instruccion.getText() == null || instruccion.getText().toString().trim().isEmpty()) {
            if(image.getHeight() <= 0){
                instruccionEnBlanco = true;
            }
        }
        return instruccionEnBlanco;
    }

    public void loadPhoto(@NonNull CardView photoCardView,
                          Context context,
                          String photoName,
                          AppCompatImageView photoImageView,
                          AppCompatEditText instrucciones) {

        File photoFile = new File(mDirectorio + "/" + File.separator + photoName);

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        Glide.with(context)
                .load(photoFile)
                .apply(options)
                .into(photoImageView);
        if(photoCardView.getVisibility() == View.GONE){
            Log.i("PHOTO", "Visibility: GONE");
            photoCardView.setVisibility(View.VISIBLE);
            Log.i("PHOTO", "Visibility: VISIBLE");
//            Log.i("PHOTO", "Visibility: LANDSCAPE = " + Configuration.ORIENTATION_LANDSCAPE);
//            int orientation = activity.getResources().getConfiguration().orientation;
//            Log.i("PHOTO", "Visibility: ORIENTATION = " + orientation);
//            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
//                Log.i("PHOTO", "Visibility: LANDSCAPE");
//                ViewGroup.LayoutParams params = photoImageView.getLayoutParams();
//                int height = params.height;
//                instrucciones.setHeight(height);
//            }
//            else {
//                Log.i("PHOTO", "Visibility: PORTRAIT");
//            }
        }
    }



}
