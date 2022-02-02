import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import com.example.sheetmusic.R;

import java.util.HashMap;

public class MetronomeActivity extends Activity {
    private static SoundPool soundPool;
    private static HashMap<String, Integer> soundPoolMap;
    static int mod = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<String, Integer>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMetronome();
            }
        }).start();
    }

    public void startMetronome(){
        try{
            setBPM(140);
            while(true){
                if(metronomeWillPlay() == true){
                    playSound("pop.wav");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setBPM(int bpm){
        if(bpm == 0){
            mod = 1000;
        } else{
            mod = 60000 / bpm;
        }
    }

    public boolean metronomeWillPlay(){
        if((System.currentTimeMillis() % mod) == 0){
            return true;
        } else{
            return false;
        }
    }

    public void playSound(String filePath){
        try{
            if(!soundPoolMap.containsKey(filePath)){
                AssetFileDescriptor afd = getAssets().openFd(filePath);
                if(afd == null){
                    System.out.println("Could not find sound" + filePath);
                    return;
                }

                int id = soundPool.load(afd, 1);
                soundPoolMap.put(filePath, id);
            }
            int id = soundPoolMap.get(filePath);
            soundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f);
            System.out.println("Sound Id: " + id);
        } catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
