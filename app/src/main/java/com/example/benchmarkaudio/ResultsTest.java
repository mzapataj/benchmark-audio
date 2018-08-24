package com.example.benchmarkaudio;

import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.benchmarkaudio.MainActivity.ring;
import static com.example.benchmarkaudio.MainActivity.mFileName;
import static com.example.benchmarkaudio.MainActivity.stopPlaying;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ResultsTest extends AppCompatActivity {
    private boolean isPlaying = false;
    private MediaExtractor mex = new MediaExtractor();
    String bitRate;
    int sampleRate;
    private byte[] audioBytes;
    private Complex[] domainTime;
    private Complex[] domainFrequency;
    LineGraphSeries<DataPoint> series;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_test);
        try {
            metaDataRetrieve();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Button btn= findViewById(R.id.button2);
        btn.setText(getString(R.string.button2));
        createChart(domainFrequency);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void metaDataRetrieve() throws IOException, NoSuchFieldException, IllegalAccessException {
        mex.setDataSource(mFileName);
        MediaFormat mef = mex.getTrackFormat(0);
        sampleRate = mef.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        bitRate = MediaFormat.KEY_BIT_RATE;
        audioBytes = convert(mFileName);
        audioBytes = setLength2Power(audioBytes);
        domainTime = Complex.byteToComplexArr(audioBytes);
        domainFrequency = FFT.fft(domainTime);
        inicializarListView2();
    }
    public byte[] setLength2Power(byte[] arr){
        int n = arr.length;
        n = (int) Math.pow(2,(int)(Math.log(n)/Math.log(2)));
        byte[] result = new byte[n];
        System.arraycopy(arr, 0, result, 0, n);
        return result;
    }

    public void inicializarListView2() throws NoSuchFieldException, IllegalAccessException {
        final ListView milista = (ListView)findViewById(R.id.milista2);
        final HashMap<String,String> hash = new HashMap<>();
        //hash.put("Tasa de bits:",bitRate+"bps");

        hash.put("Tasa de muestreo:",sampleRate+"Hz");
        hash.put("Numero de bytes:",audioBytes.length+" bytes");
        hash.put("Frecuencia:",calculateFrequency(16000,audioBytes)+"Hz");

        final List<HashMap<String,String>> listItem = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(this, listItem,R.layout.list_item2, new String[]{"First Line", "Second Line"}, new int[]{R.id.text21,R.id.text22});
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

    public void createChart(Complex[] arr){
        double y,x;
        x = 0;
        double delta = (8000.0)/arr.length;
        GraphView graph = (GraphView) findViewById(R.id.grafica);
        series = new LineGraphSeries<DataPoint>();
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Hz");
        gridLabel.setVerticalAxisTitle("dB");
        series.setThickness(1);

        int n = arr.length;
        for (int i = 0; i<n; i++) {
            y = arr[i].abs();
            series.appendData(new DataPoint(x, y), true, n);
            x = x + delta;
        }
        graph.addSeries(series);
    }

    public void eventPlay(View view) throws IOException {
        Button btn= findViewById(R.id.button2);
        if(!isPlaying){
            btn.setText(getString(R.string.stop));
            startPlayingRecorded();
            isPlaying = true;
        }else{
            stopPlaying();
            btn.setText("Reproducir");
            isPlaying = false;
        }
    }

    public byte[] convert(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    public static int calculateFrequency(int sampleRate, byte [] audioData){

        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0))
            {
                numCrossing++;
            }
        }

        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;

        return (int)frequency;
    }
}
