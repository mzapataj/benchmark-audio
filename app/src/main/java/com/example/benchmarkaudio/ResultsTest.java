package com.example.benchmarkaudio;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import static com.example.benchmarkaudio.MainActivity.ring;
import static com.example.benchmarkaudio.MainActivity.mFileName;
import static com.example.benchmarkaudio.MainActivity.stopPlaying;

public class ResultsTest extends AppCompatActivity {
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_test);
        Button btn= findViewById(R.id.button2);
        btn.setText(getString(R.string.button2));
    }

    private void startPlayingRecorded() throws IOException {
        ring = new MediaPlayer();
        final Button btn= findViewById(R.id.button2);
        ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btn.setText("Reproducir");
                try {
                    stopPlaying();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = false;
            }
        });
        ring.setDataSource(mFileName);
        ring.prepare();
        ring.start();
    }

    public void eventPlay(View view) throws IOException {
        Button btn= findViewById(R.id.button2);
        if(!isPlaying){
            btn.setText("Detener");
            startPlayingRecorded();
            isPlaying = true;
        }else{
            stopPlaying();
            btn.setText("Reproducir");
            isPlaying = false;
        }
    }
}
