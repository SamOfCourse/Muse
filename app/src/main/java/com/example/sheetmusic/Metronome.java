package com.example.sheetmusic;

import android.media.SoundPool;

public class Metronome {
    private SoundPool soundPool;
    private int beatSound;
    private int bpm = 60;
    private int interval = 1000;
    private boolean playing = false;
    private MetronomeThread thread;

    public Metronome(SoundPool pool, int id){
        soundPool = pool;
        beatSound = id;
        thread = new MetronomeThread(this, soundPool, beatSound);
    }

    public void setBpm(int b){
        if(b >= 30 && b <= 300){
            bpm = b;
            interval = (int)(1000*(60.0/bpm));
            System.out.println("New interval: " + interval);
        }
        else{
            System.out.println("Invalid Bpm: " + b);
        }
    }

    public int getBpm(){
        return bpm;
    }

    public int getInterval(){
        return interval;
    }

    public void setPlaying(boolean playing){
        this.playing = playing;
    }

    public void play(){
        System.out.println("Interval: " + this.getInterval());
        if(!thread.isAlive()){
            thread = new MetronomeThread(this, soundPool, beatSound);
            thread.start();
        }

    }

    public void stop(){
        thread.interrupt();
    }

}
