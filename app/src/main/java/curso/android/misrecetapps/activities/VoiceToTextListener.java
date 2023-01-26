package curso.android.misrecetapps.activities;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;

import curso.android.misrecetapps.R;

public class VoiceToTextListener implements RecognitionListener {

    private final AppCompatEditText mEditText;
    private final ImageButton mButtonMic;

    public VoiceToTextListener(AppCompatEditText editText, ImageButton buttonMic) {
        this.mEditText = editText;
        this.mButtonMic = buttonMic;
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) { mEditText.setHint("Escuchando..."); }
    @Override
    public void onBeginningOfSpeech() {
        mEditText.setHint("Escuchando...");
    }
    @Override
    public void onRmsChanged(float v) {}
    @Override
    public void onBufferReceived(byte[] bytes) {}
    @Override
    public void onEndOfSpeech() {}
    @Override
    public void onError(int i) {
        Log.e("VOICE_TO_TEXT", "onError:" + i);
        mButtonMic.setImageResource(R.drawable.ic_mic);
    }

    @Override
    public void onResults(@NonNull Bundle bundle) {
        Log.i("VOICE_TO_TEXT", "Pasando voz a texto onResult...");
        ArrayList<String> text = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(mEditText.getText() != null){
            if(mEditText.getText().toString().isEmpty()){
                Log.i("MIC", "Escribiendo mensaje nuevo...");
                if(text != null){
                    String texto = text.get(0);
                    texto = texto.concat(". ");
                    texto = texto.substring(0, 1).toUpperCase() + texto.substring(1);
                    mEditText.setText(texto);
                }
            }
            else {
                Log.i("MIC", "Escribiendo mensaje sobre anterior...");
                String previousText = mEditText.getText().toString();
                if (text != null) {
                    String texto = text.get(0);
                    texto = texto.concat(". ");
                    texto = texto.substring(0, 1).toUpperCase() + texto.substring(1);
                    mEditText.setText(previousText.concat(texto));
                }
            }
        }
        mButtonMic.setImageResource(R.drawable.ic_mic);
    }

    @Override
    public void onPartialResults(Bundle bundle) {}
    @Override
    public void onEvent(int i, Bundle bundle) {}
}
