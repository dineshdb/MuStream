package com.sankalpa.mustream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void speaker(View view) {
        Intent intent = new Intent(this, Speaker.class);
        startActivity(intent);
    }

    public void microphone(View view) {
        Intent intent = new Intent(this, MicroPhone.class);
        startActivity(intent);
    }

    public void mediaPlayer(View view) {
        Intent intent = new Intent(this, MediaPlayer.class);
        startActivity(intent);
    }
}
