package com.example.benchmarkaudio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import static com.example.benchmarkaudio.MainActivity.LOG_TAG;
import static com.example.benchmarkaudio.MainActivity.mFileName;

/**
 * Created by Jorge on 07/08/2018.
 */

public class PlayButton extends android.support.v7.widget.AppCompatButton {
    public  static MediaPlayer  mPlayer = null;
    boolean mStartPlaying = true;


    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                setText("Stop playing");
            } else {
                setText("Start playing");
            }
            mStartPlaying = !mStartPlaying;
        }
    };
    public PlayButton(Context context) {
        super(context);setText("Start playing");
        setOnClickListener(clicker);

    }
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
}
