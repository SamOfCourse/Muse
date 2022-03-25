package com.sheetmusic;

import android.media.SoundPool;

public class MetronomeThread extends Thread{
    SoundPool soundPool;
    Metronome metronome;
    int beatId;

    public MetronomeThread(Metronome met, SoundPool pool, int id){
        metronome = met;
        soundPool = pool;
        beatId = id;
    }

    public void run(){
        try{
            while(true){
                System.out.println(System.currentTimeMillis());
                soundPool.play(beatId, 1, 1, 0, 0, 1);
                Thread.sleep(metronome.getInterval());
            }
        } catch (InterruptedException e){
            System.out.println("MetronomeThread stopped");
            return;
        }
    }
}
