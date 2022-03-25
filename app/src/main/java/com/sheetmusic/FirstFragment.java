package com.sheetmusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.sheetmusic.databinding.FragmentFirstBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {


        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Preloaded sheet music is hardcoded into the app
        ImageButton button = getView().findViewById(R.id.imageButton);
        ImageButton button2 = getView().findViewById(R.id.imageButton2);
        ImageButton button3 = getView().findViewById(R.id.imageButton3);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        loadFromStorage();

    }

    //Create ImageButtons for each file in storage, and add those buttons to the library.
    public void loadFromStorage() {
        Context context = this.getContext();
        View layoutView = (View) getView().findViewById(R.id.linear_library);

        File file = new File(context.getFilesDir(), "sheet_music");

        for (File f : file.listFiles()){

            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

            ImageButton imageButton = new ImageButton(layoutView.getContext());
            imageButton.setImageBitmap(bitmap);

            //Tag is set to the filename, this will allow us to find the associated XML in the second fragment.
            imageButton.setTag(removeFileExtension(f.getName()));

            imageButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            imageButton.setAdjustViewBounds(true);
            imageButton.setOnClickListener(this);

            ((ViewGroup)layoutView).addView(imageButton);
//            Log.d("File added to view: ", f.getName());
        }

//        ViewGroup viewGroup = (ViewGroup)layoutView;
//        for (int i = 0; i < viewGroup.getChildCount(); i++){
//            Log.d("View List: ", viewGroup.getChildAt(i).toString());
//        }

    }

    //Removes the file's extension before it is used as the tag.
    private Object removeFileExtension(String name) {
        String[] strings = name.split("\\.");
        return strings[0];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {

        //When an ImageButton is clicked, create a bundle with the ImageButton's Tag
        //and pass it as an argument to the second fragment.
        Bundle bundle = new Bundle();
        bundle.putString("file", (String)v.getTag());
        NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);

    }
}