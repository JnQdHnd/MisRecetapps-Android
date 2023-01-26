package curso.android.misrecetapps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import curso.android.misrecetapps.activities.ImportFromJson;
import curso.android.misrecetapps.controllers.receta.RecetaViewModel;
import curso.android.misrecetapps.databinding.ActivityMainBinding;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private ImportFromJson mImportFromJson;
    private RecetaViewModel mViewModel;
    private static final int WRITE_STORAGE_CODE = 101;
    private static final int READ_STORAGE_CODE = 102;
    private static final int RECORD_AUDIO_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MAIN ACTIVITY", "onCreate");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this).get(RecetaViewModel.class);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = binding.toolbar;

        configTitle();

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_CODE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
        checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_CODE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        mViewModel = new ViewModelProvider(this).get(RecetaViewModel.class);
        mImportFromJson = new ImportFromJson(getActivityResultRegistry(), this, mViewModel, navController);
        getLifecycle().addObserver(mImportFromJson);
        configHandleIntentFromImportFile(getIntent());

        binding.importRecetaBtn.setOnClickListener(view -> mImportFromJson.load());
        mViewModel.setmImportButton(binding.importRecetaBtn);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        configHandleIntentFromImportFile(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_exit:
                showAppClosingDialog();
            case R.id.receta_form:
                mViewModel.clearAll();
                navController.navigate(R.id.receta_form);
                return true;
            default:
                return NavigationUI.onNavDestinationSelected(item, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(navController.getCurrentDestination().getId() == R.id.receta_details){
            navController.navigate(R.id.receta_list);
            return true;
        }
        else {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void showAppClosingDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Estás saliendo")
                .setMessage("¿Seguro que quieres salir?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", (dialog, which) ->
                        this.finishAndRemoveTask())
                .show();
    }

    private void configTitle() {
        SpannableString tituloMisRecetapps = new SpannableString("MisRecetapps");
        ForegroundColorSpan color1 = new ForegroundColorSpan(Color.parseColor("#c0aae3"));
        ForegroundColorSpan color2 = new ForegroundColorSpan(Color.parseColor("#a279e3"));
        ForegroundColorSpan color3 = new ForegroundColorSpan(Color.parseColor("#6f50a1"));
        ForegroundColorSpan color4 = new ForegroundColorSpan(Color.parseColor("#a279e3"));
        tituloMisRecetapps.setSpan(color1, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tituloMisRecetapps.setSpan(color2, 3, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tituloMisRecetapps.setSpan(color3, 9, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tituloMisRecetapps.setSpan(color4, 11, 12, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        binding.homeBtn.setText(tituloMisRecetapps);
        binding.homeBtn.setOnClickListener(view -> navController.navigate(R.id.receta_list));
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Log.i("CHECK PERMISSION", "Permission already granted");
        }
    }

    private void configHandleIntentFromImportFile(@NonNull Intent receivedIntent) {
        String receivedAction = receivedIntent.getAction();
        if (receivedAction.equals(Intent.ACTION_VIEW)) {
            Uri uri = receivedIntent.getData();
            if(uri != null){
                String fileName = Auxiliar.queryName(getContentResolver(), uri);
                Log.i("OPENFILE", fileName);
                if(fileName.contains(".misrapps")){
                    mImportFromJson.importFileSucces(uri);
                }
                else {
                    Log.e("OPENFILE", "Nose puede abrir: " + fileName);
                    Toast.makeText(this,"Solo se pueden importar archivos de tipo MISRAPPS...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}