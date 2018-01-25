package com.sankalpa.mustream;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class SpeakerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private static final String TAG = "Speaker";
    TextView ipAddress;
    Thread latencyThread;


    private static final int RC_BARCODE_CAPTURE = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        ipAddress = findViewById(R.id.ip_address);

//        ipAddress = findViewById(R.id.ipaddress);
//        textView.setText(message);

//        this.latencyThread = new Thread(new LatencyThread());
//        this.latencyThread.start();

/*
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(SpeakerActivity.this);
        Session session = SessionBuilder.getInstance()
                .setAudioEncoder(SessionBuilder.AUDIO_AMRNB)
                .setContext(getApplicationContext())
                .setVideoEncoder(SessionBuilder.VIDEO_NONE).build();

        client = new RtspClient();
        client.setSession(session);
*/


//        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
//        executor.schedule(new NetworkDiscoveryClient(), 1, TimeUnit.SECONDS );
        //this.thread = new Thread(new NetworkDiscoveryClient());
        //this.thread.start();

    }
    public void play(View view) {
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mp.setDataSource("rtsp://" + Config.getInstance().getIpAddress() + ":" + Config.STREAM_PORT_ADDRESS + "/");
            mp.setOnErrorListener(this);
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Config.getInstance().setIpAddress(contents);
                ipAddress.setText(contents);
                Log.d(TAG, "contents: " + contents);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "RESULT_CANCELED");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void scanQR(View view) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }
}
