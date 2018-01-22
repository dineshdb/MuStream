package com.sankalpa.mustream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MicroPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_phone);
        Intent intent = getIntent();
        String message=getResources().getString(R.string.microphone);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
