package com.sankalpa.mustream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.rtsp.RtspClient;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpeakerActivity extends AppCompatActivity {

    Thread thread;
    RtspClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        Intent intent = getIntent();
        String message=getResources().getString(R.string.speaker);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(SpeakerActivity.this);
        Session session = SessionBuilder.getInstance()
                .setAudioEncoder(SessionBuilder.AUDIO_AMRNB)
                .setContext(getApplicationContext())
                .setDestination("224.0.0.1")
                .setVideoEncoder(SessionBuilder.VIDEO_NONE).build();

        client = new RtspClient();
        client.setSession(session);


//        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
//        executor.schedule(new NetworkDiscoveryClient(), 1, TimeUnit.SECONDS );
        this.thread = new Thread(new NetworkDiscoveryClient());
        this.thread.start();

        MediaPlayer mp = new MediaPlayer();
    }
}
