package com.sankalpa.mustream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Speaker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        Intent intent = getIntent();
        String message=getResources().getString(R.string.speaker);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
