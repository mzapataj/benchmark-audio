package com.example.benchmarkaudio;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SelectAudio extends AppCompatActivity {
    public static final String SELECTED_SOUND = "com.example.benchmark.MESSAGE";
    public static RadioButton radio_toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_audio);
        setToggleRadio();
    }

    public void setToggleRadio (){
        if (radio_toggle == null){
            radio_toggle = findViewById(R.id.radio1);
            radio_toggle.toggle();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onRadioButtonClicked(View view){

        Intent intent = new Intent(this, MainActivity.class);

        boolean checked = ((RadioButton) view).isChecked();
        byte idSound = 1;

        switch (view.getId()){
            case R.id.radio1:
                if (checked){
                    radio_toggle = findViewById(R.id.radio1);
                    idSound = 1;
                }
                break;
            case R.id.radio2:
                if (checked){
                    radio_toggle = findViewById(R.id.radio2);
                    idSound = 2;
                }
                break;
            case R.id.radio3:
                if (checked){
                    idSound = 3;
                    radio_toggle = findViewById(R.id.radio3);
                }
                break;
        }

        radio_toggle.setChecked(true);

        intent.putExtra(SELECTED_SOUND, idSound);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
