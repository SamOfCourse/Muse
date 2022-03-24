package com.example.sheetmusic;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int SELECT_PICTURE = 200;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Switch sw;
    private int beatSound;
    private SoundPool soundPool;
    private Metronome metronome;
    private TextView bpmViewer;

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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else{
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        beatSound = soundPool.load(this, R.raw.pop,1);
        metronome = new Metronome(soundPool, beatSound);

        bpmViewer = findViewById(R.id.textViewTempo);

        bpmViewer.setText(String.valueOf(metronome.getBpm()));
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

        MenuItem item = menu.findItem(R.id.feature_switch);
        item.setActionView(R.layout.switch_item);

        sw = item.getActionView().findViewById(R.id.switch_id);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    System.out.println("metronome play");
                    metronome.play();
                } else{
                    System.out.println("metronome stop");
                    metronome.stop();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String res = String.valueOf(item.getTitle());
        int bpm;
        try{
            bpm = Integer.parseInt(res);
            metronome.setBpm(bpm);
            bpmViewer.setText(String.valueOf(metronome.getBpm()));
        }catch (NumberFormatException e){
            System.out.println("Menu Item is not bpm value");
        }


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