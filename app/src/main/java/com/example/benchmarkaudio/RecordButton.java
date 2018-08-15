package com.example.benchmarkaudio;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import static com.example.benchmarkaudio.MainActivity.LOG_TAG;
import static com.example.benchmarkaudio.MainActivity.mFileName;

/**
 * Created by Jorge on 07/08/2018.
 */


public class RecordButton extends android.support.v7.widget.AppCompatButton {

    public static  MediaRecorder mRecorder = null;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }


    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public RecordButton(Context context) {
        super(context);
        setText("Start recording");
        setOnClickListener(clicker);
    }
    boolean mStartRecording = true;

    OnClickListener clicker = new OnClickListener() {
        public void onClick(View v) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                setText("Stop recording");
            } else {
                setText("Start recording");
            }
            mStartRecording = !mStartRecording;
        }
    };
}
