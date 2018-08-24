package com.example.benchmarkaudio;

import android.Manifest;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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
    Intent intent;
    public static String mFileName = null;

    public int soundPlaying = -1;
    public  static MediaPlayer  mPlayer = null;
    Toast toast;
    //Recording
    boolean mStartRecording = true;
    private RecordButton mRecordButton = null;
    public static  MediaRecorder mRecorder = null;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    public static MediaPlayer ring;
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
        mFileName = getDir("res",MODE_PRIVATE).getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_main);
        try {
            inicializarListView();
            setClickEventSoundButton();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        createTabs();
    }



    public void setClickEventSoundButton() throws NoSuchFieldException, IllegalAccessException {

        Button btn = findViewById(R.id.class.getField("btn1").getInt(null));
            final int id = getResources().getIdentifier("sound" + messageIntentReceived(), "raw", getPackageName());
            final Button finalBtn = btn;

            final Context contextMain = this;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onRecord(mStartRecording);
                        onPlaying(mStartPlaying,id);
                        if (mStartRecording) {
                            finalBtn.setText(getString(R.string.stop));
                            soundPlaying = id;
                            findViewById(R.id.milista).setVisibility(View.INVISIBLE);
                        } else {
                            soundPlaying = -1;
                            intent = new Intent(contextMain,ResultsTest.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finalBtn.setText(getString(R.string.button1));
                            findViewById(R.id.milista).setVisibility(View.VISIBLE);
                        }
                        mStartRecording = !mStartRecording;
                        mStartPlaying = !mStartPlaying;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void onPlaying(boolean start,int id) throws IOException {
        if (start) {
            startPlaying(id);
        } else {
            stopPlaying();
        }

    }
    private void startPlaying(final int id){
        ring = MediaPlayer.create(MainActivity.this, id);
        final Button finalBtn = findViewById(R.id.btn1);
        ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                intent = new Intent(getBaseContext(),ResultsTest.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                try {
                    onRecord(mStartRecording);
                    onPlaying(mStartPlaying,id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mStartRecording = !mStartRecording;
                mStartPlaying = !mStartPlaying;
                startActivity(intent);
                finalBtn.setText(getString(R.string.button1));
            }
        });
        ring.start();
    }


    protected static void stopPlaying() throws IOException {
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
        TabHost tabhost = findViewById(R.id.tabhost);
        tabhost.setup();
        TabHost.TabSpec  ts = tabhost.newTabSpec("tag1");
        ts.setContent(R.id.tab1);
        ts.setIndicator("Prueba");
        tabhost.addTab(ts);

        ts = tabhost.newTabSpec("tag2");
        ts.setContent(R.id.tab2);
        ts.setIndicator("Historial");
        tabhost.addTab(ts);
    }

    public void inicializarListView() throws NoSuchFieldException, IllegalAccessException {
        final ListView milista = (ListView)findViewById(R.id.milista);
        final HashMap<String,String> hash = new HashMap<>();
        hash.put(getString(R.string.sound),getString(R.string.class.getField("radio"+messageIntentReceived()).getInt(null)));
        final List<HashMap<String,String>> listItem = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(this, listItem,R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1,R.id.text2});
        Iterator it= hash.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String,String> resultmap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultmap.put("First Line", pair.getKey().toString());
            resultmap.put("Second Line", pair.getValue().toString());
            listItem.add(resultmap);
        }
        milista.setAdapter(adapter);
        milista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                intent = new Intent(getBaseContext(),SelectAudio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                listItem.clear();
                hash.clear();
                startActivity(intent);
                finish();

                //Reset activity
                /*finish();
                overridePendingTransition( 0, 0);
                startActivity(new Intent(getBaseContext(),MainActivity.class));
                overridePendingTransition( 0, 0);*/
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(soundPlaying!=-1) {
            stopRecording();
            try {
                stopPlaying();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
            //startActivity(new Intent(getBaseContext(),MainActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Toast toast = Toast.makeText(getBaseContext(), "Comenzando...", Toast.LENGTH_SHORT);
        toast.show();
        */
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

    public Byte messageIntentReceived() {
        intent = getIntent();
        return intent.getByteExtra(SelectAudio.SELECTED_SOUND, (byte) 1);
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
