package com.example.sheetmusic;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.sheetmusic.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int SELECT_PICTURE = 200;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UiModeManager enableDarkMode = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        enableDarkMode.setNightMode(UiModeManager.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simple permissions check
                if (permissionsCheck()) {
                    addImage();
                } else {
                    permissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE, SELECT_PICTURE);
                }
            }
        });
    }

    private void permissionRequest(String permission, int val){
        requestPermissions(
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                SELECT_PICTURE);
    }

    private void fabToast(int s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private boolean permissionsCheck() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void addImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Deprecated, but I'm keeping it -S, still needs to actually import and do something with the picture however
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                if (null != imageUri) {
                    copyImageToApp(data);
                }
            }
        }
    }

    public void copyImageToApp(Intent data) {
        // Copy image file from external/local storage to app storage
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }



    public void hideFab(){
        binding.fab.hide();
        //can hide individual features in menu, but still need to figure out how to disable 3
        //(binding.toolbar.findViewById(R.id.feature_switch)).setVisibility(View.VISIBLE);

    }
    public void showFab(){
        binding.fab.show();
        //(binding.toolbar.findViewById(R.id.feature_switch)).setVisibility(View.INVISIBLE);
        //(binding.toolbar.findViewById(R.id.)).setVisibility(View.INVISIBLE);
    }
}