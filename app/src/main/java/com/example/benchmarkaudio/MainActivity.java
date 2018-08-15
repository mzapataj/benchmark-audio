package com.example.benchmarkaudio;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.benchmarkaudio.PlayButton.mPlayer;
//import static com.example.benchmarkaudio.RecordButton.mRecorder;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AudioRecordTest";

    public static String mFileName = null;

    public  static MediaPlayer  mPlayer = null;

    //Recording
    boolean mStartRecording = true;
    private RecordButton mRecordButton = null;
    public static  MediaRecorder mRecorder = null;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private MediaPlayer ring;
    private MediaPlayer lastRing=null;
    boolean mStartPlaying = true;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setContentView(R.layout.activity_main);
        inicializarListView();
        setClickEventSoundButton();
        createTabs();

    }



    public void setClickEventSoundButton(){
        Button btns[] = new Button[1];
        int i = 1;

        for (Button btn :
                btns) {
            try {
                btn = findViewById(R.id.class.getField("btn" + i).getInt(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            final int id = getResources().getIdentifier("sound" + i, "raw", getPackageName());
            /*ring = MediaPlayer.create(MainActivity.this, id);
            final MediaPlayer finalRing = ring;
            */
            final Button finalBtn = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onRecord(mStartRecording);
                        onPlaying(mStartPlaying,id);
                        if (mStartRecording) {
                            finalBtn.setText("Grabando...");
                        } else {
                            finalBtn.setText("Comenzar prueba");
                        }
                        mStartRecording = !mStartRecording;
                        mStartPlaying = !mStartPlaying;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/sound"+"1"+".mp3");
                    /*finalRing.start();
                    lastRing = finalRing;*/
                }
            });
            i++;
        }
    }
    private void onPlaying(boolean start,int id) throws IOException {
        if (start) {
            startPlaying(id);
        } else {
            stopPlaying();
        }

    }
    private void startPlaying(int id){
        ring = MediaPlayer.create(MainActivity.this, id);
        ring.start();
    }
    public void stopPlaying() throws IOException {
        if (ring != null) {
            ring.stop();
            try {
                ring.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void createTabs(){
        TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setup();
        TabHost.TabSpec  ts = tabhost.newTabSpec("tag1");
        ts.setContent(R.id.tab1);
        ts.setIndicator("Prueba");
        tabhost.addTab(ts);

        ts = tabhost.newTabSpec("tag2");
        ts.setContent(R.id.tab2);
        ts.setIndicator("Historial");
        tabhost.addTab(ts);

       /* ts= tabhost.newTabSpec("tag3");
        ts.setContent(R.id.tab3);
        ts.setIndicator("Third Tab");
        tabhost.addTab(ts);
        */
    }

    public void inicializarListView(){
        final ListView milista = (ListView)findViewById(R.id.milista);
        HashMap<String,String> hash = new HashMap<>();
        hash.put("Sonido","Bark Sound");
        List<HashMap<String,String>> listItem = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItem,R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1,R.id.text2});

        Iterator it= hash.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String,String> resultmap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultmap.put("First Line", pair.getKey().toString());
            resultmap.put("Second Line", pair.getValue().toString());
            listItem.add(resultmap);
        }
        milista.setAdapter(adapter);
    }
    @Override
    public void onStop() {
        super.onStop();

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void onRecord(boolean start) throws IOException {
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



        /*
        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/sound"+"1"+".mp3");

                    String NameFile = "sound1";
                    MediaPlayer ring= null;
                    try {
                        ring = MediaPlayer.create(MainActivity.this,R.raw.class.getField(NameFile).getInt(null));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    ring.start();
                }
            });

    }
    /*
    /*public void playSound(Uri pathSound){
        MediaPlayer ring= MediaPlayer.create(MainActivity.this,pathSound);
        ring.start();
    }
    */
}
