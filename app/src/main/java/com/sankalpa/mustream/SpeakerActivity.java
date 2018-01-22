package com.sankalpa.mustream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpeakerActivity extends AppCompatActivity {

    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        Intent intent = getIntent();
        String message=getResources().getString(R.string.speaker);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

//        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
//        executor.schedule(new NetworkDiscoveryClient(), 1, TimeUnit.SECONDS );
        this.thread = new Thread(new NetworkDiscoveryClient());
        this.thread.start();
    }
}
