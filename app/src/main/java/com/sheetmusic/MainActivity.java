package com.sheetmusic;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.sheetmusic.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int SELECT_PICTURE = 200;
    private int REQUEST_IMAGE_CAPTURE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create folders to store sheet_music and the sheet_music_xml
        makeFolders();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Declaring an array of permissions
        String[] permissions = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA };

        //On fab click, permissions are requested and checked. If allowed it will call addImage().
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequest(permissions, SELECT_PICTURE);
                List<String> allowedPermissions = permissionsCheck(permissions);

                //If no permissions show an explanation on why the permissions are needed
                if (allowedPermissions.size() == 0) {
                    permissionsDialog();
                } else {
                    addImage(allowedPermissions);
                }
            }
        });
    }

    //User wants to add file but no permissions were found
    private void permissionsDialog() {
        //To be implemented later
    }

    // If the device has a camera, it requests CAMERA and READ_EXTERNAL_STORAGE permissions
    // If the device does not have a camera, only READ_EXTERNAL_STORAGE permission is requested
    private void permissionRequest(String[] permissions, int val){
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){

            //Commented out requesting camera permissions to because it leads to the emulator crashing
            //requestPermissions(permissions, SELECT_PICTURE);
            requestPermissions(
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    SELECT_PICTURE);
        }
        else {
            requestPermissions(
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    SELECT_PICTURE);
        }
    }

    //Checks permissions and returns a list of the allowed ones
    private List<String> permissionsCheck(String[] permissions) {
        List<String> allowedPermissions = new ArrayList<String>();
        for(String permission : permissions){
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                allowedPermissions.add(permission);
            }
        }
        return allowedPermissions;
        //ContextCompat.checkSelfPermission(this, )
    }

    //The allowed permissions are passed in so the appropriate method of adding an image can be offered to the user
    //NOTE: Emulator doesn't have a camera, so while I *think* the other actions work, it will crash the program if you try to take a picture
    public void addImage(List<String> permissions) {
        //Create the intent
        Intent intent = new Intent();
        intent.setType("image/*");


        //If both permissions were accepted, then offer both image selection options
        //--!Crashes program on emulator (no camera)!--
        if (permissions.size() == 2){
            intent.setAction(Intent.ACTION_CHOOSER);
            intent.putExtra(Intent.EXTRA_TITLE, "Select option of adding file");
            intent.putExtra(Intent.EXTRA_INTENT, MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            startActivityForResult(intent, SELECT_PICTURE);
        }
        //If only the camera permission was granted
        //--!Crashes program on emulator (no camera)!--
        else if (permissions.get(0) == Manifest.permission.CAMERA){
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        //If only reading external storage permissions were granted
        //Works
        else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                if (null != imageUri) {
                    Log.d("File Path: ", data.getDataString());

                    //The first fragment houses the actual library, so we send that the data.
                    imageToAppDir(data);
                }
            }
        }
    }

    //Code for this method was pulled (with some tweaks) from user Tomás Rodrigues answer in:
    //https://stackoverflow.com/questions/51589764/copy-image-from-one-folder-to-another-android
    public void imageToAppDir(Intent data){
        Uri imagePath = data.getData();
        String outputPath = getFilesDir().toString() + "/sheet_music";

        String lastSegment = imagePath.getLastPathSegment();
        if (lastSegment.contains("/")){
            String[] pathSegments = lastSegment.split("/");
            lastSegment = pathSegments[pathSegments.length - 1];
        }


        Log.d("APP FILES DIR: ", getFilesDir().listFiles()[0].toString());
        Log.d("APP FILES DIR: ", imagePath.getLastPathSegment());

        InputStream in = null;
        OutputStream out = null;
        try {
            in = getContentResolver().openInputStream(imagePath);
            out = new FileOutputStream(outputPath + "/" + lastSegment);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            Log.d("Copied file to ", outputPath);

        } catch (FileNotFoundException e) {
            Log.e("file not found error in imagetoappdir: ", e.getMessage());
        } catch (Exception e) {
            Log.e("other error in imagetoappdir", e.getMessage());
        }
    }


    //Checks if folders exist in app-specific storage, if they don't it creates them.
    private void makeFolders() {
        File sheet_music = new File(getFilesDir(), "sheet_music");
        File sheet_music_xml = new File(getFilesDir(), "sheet_music_xml");

        if (!sheet_music.exists()) {
            sheet_music.mkdir();
        }
        if (!sheet_music_xml.exists()){
            sheet_music_xml.mkdir();
        }
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    // Hides the fab
    public void hideFab(){
        binding.fab.hide();
    }
    // Shows the fab
    public void showFab(){
        binding.fab.show();
    }
}