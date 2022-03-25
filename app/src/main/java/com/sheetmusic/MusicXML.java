package com.sheetmusic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.talobin.scanner.Scanner;
import com.talobin.scanner.model.Progress;
import com.talobin.scanner.model.ScanOutput;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import uk.co.dolphin_com.rscore.ex.ConvertFailedException;
import uk.co.dolphin_com.rscore.ex.NoNotesException;
import uk.co.dolphin_com.rscore.ex.RScoreException;
import uk.co.dolphin_com.rscore.ex.TooManyStaffsException;

public class MusicXML {
    private static final String TAG = "Log: ";
    private Context context = null;
    private Activity activity = null;
    private static File selected_xml = null;


    public MusicXML(Context sentContext, Activity sentActivity, String caller) throws InterruptedException {
        context = sentContext;
        activity = sentActivity;
        tryForXML(caller);

        //return selected_xml;
    }

    private void tryForXML(String caller) throws InterruptedException {
        //searching for XML related to caller
        File existingXML = searchForXML(caller);

        //If no existingXML, createXML
        if (existingXML == null) {
            createXML(caller);
        }

        else {
            //found existing xml for selection
            selected_xml = existingXML;
            startDisplay(selected_xml);
        }
    }
    private File searchForXML(String caller) {
        //Context context = this.getContext();
        File file = new File(context.getFilesDir(), "sheet_music_xml");

        for (File f : file.listFiles()){
            if (f.getName().contains(caller)){
                return f;
            }
        }
        return null;
    }

    private void storeXML(String caller) {
        //Context context = this.getContext();
        File source = new File(context.getFilesDir(), "currentXml");
        File destination = new File(context.getFilesDir(), "sheet_music_xml/" + caller + ".xml");

        Log.d(TAG, "File Exists: " + source.exists());

        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();

            // write the output file (You have now copied the file)
            outputStream.flush();
            outputStream.close();

            Log.d(TAG,"Copied XML file to sheet_music_xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //created new xml for selection
        selected_xml = destination;
        startDisplay(selected_xml);
    }



    //Gets image from app storage
    private Bitmap getImageBitmap(String caller) {
        //Context context = this.getContext();
        File file;

        //Check if it's a drawable resource since they need to be handled differently
        int id = context.getResources().getIdentifier(caller, "drawable", context.getPackageName());
        //if id is not zero, then we know it's a valid drawable resource.
        if (id != 0){
            Drawable d = context.getDrawable(id);
            return ((BitmapDrawable) d).getBitmap();
        }
        else {
            file = new File(context.getFilesDir(), "sheet_music");

            for (File f : file.listFiles()){
                if (f.getName().contains(caller)){
                    return BitmapFactory.decodeFile(f.getAbsolutePath());
                }
            }
        }
        return null;
    }

    private void createXML(String caller) {

        //Context context = this.getContext();
        Bitmap music = getImageBitmap(caller);

        if (music != null){

            Disposable disposable = Scanner.INSTANCE.scanBitmap(music, context)
                    .subscribeOn(Schedulers.newThread())
                    .doOnError(new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (throwable instanceof ConvertFailedException) {
                                Log.e(TAG, "ConvertFailedException"+throwable);
                            } else if (throwable instanceof TooManyStaffsException) {
                                Log.e(TAG, "TooManyStaffsException"+throwable);
                            } else if (throwable instanceof RScoreException) {
                                Log.e(TAG, "RScoreException"+throwable);
                            } else if (throwable instanceof NoNotesException) {
                                Log.e(TAG, "NoNotesException"+throwable);
                            }
                        }
                    })
                    .subscribe(new Consumer<ScanOutput>() {
                        @Override
                        public void accept(final ScanOutput output) throws Exception {
                            activity.runOnUiThread((Runnable) () -> {
                                if (output instanceof Progress) {
                                    if (((Progress) output).getCompletionPercent() == 100) {
                                        Log.d(TAG, "ScanOutput:" + output);
                                        storeXML(caller);
                                    }
                                    Log.d(TAG, "Progress:" + ((Progress) output).getCompletionPercent());
                                } else {
                                    //Log.d(TAG, "Score:" + theScore);
                                    Log.d(TAG, "Score: something about theScore");
                                }
                            });
                        }
                    });

        }
        else{
            Log.d(TAG, "Music image file not found");
        }

    }


    private void startDisplay(File selected_xml) {

    }
}
