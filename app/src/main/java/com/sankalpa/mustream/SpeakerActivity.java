package com.sankalpa.mustream;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.sankalpa.mustream.events.PauseEvent;
import com.sankalpa.mustream.events.PlayEvent;
import com.sankalpa.mustream.events.PrepareEvent;
import com.sankalpa.mustream.events.RequestCurrentPosition;
import com.sankalpa.mustream.events.ServerAddressEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class SpeakerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    private static final String TAG = "Speaker";
    TextView ipAddress;
    Thread latencyThread;
    Thread websocketClient;
    MediaPlayer mp;
    PowerManager.WakeLock wakeLock;
    Button play_pause;
    Switch mode;


    private static final int RC_BARCODE_CAPTURE = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);
        ipAddress = findViewById(R.id.ip_address);
        play_pause = findViewById(R.id.play_pause);
        mode = findViewById(R.id.microphone_switch);
        websocketClient = new Thread(new SyncClient());
        websocketClient.start();

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);

        PowerManager powerManager =(PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ""); // PARTIAL_WAKE_LOCK Only keeps CPU on

        wakeLock.acquire();

        this.latencyThread = new Thread(new LatencyThread());
        this.latencyThread.start();


//        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
//        executor.schedule(new NetworkDiscoveryClient(), 1, TimeUnit.SECONDS );
        //this.thread = new Thread(new NetworkDiscoveryClient());
        //this.thread.start();
    }

    public void play(View view) {
        if(mp.isPlaying()){
            mp.pause();
            play_pause.setText("Play");
        } else {
            onPrepared(mp);
            play_pause.setText("Pause");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void pauseMusic(PauseEvent e){
        mp.pause();
        wakeLock.release();
    }
    @Subscribe
    public void playMusic(PlayEvent e){
        mp.seekTo(e.offset + Config.getInstance().latency / 2);
        mp.start();
//           Log.d(TAG, " " + e.offset);

       wakeLock.acquire();
    }
    @Subscribe
    public void prepareMusic(PrepareEvent e){
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + e.music);
            mp.setDataSource(this, uri);
            mp.prepareAsync();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Config.getInstance().setIpAddress(contents);
                if(mode.isChecked()){
                    try {
                        mp.setDataSource("rtsp://" +  contents + ":" + Config.STREAM_PORT_ADDRESS + "/" );
                        mp.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().post(new ServerAddressEvent(contents));
                }
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
        if(mode.isChecked()){
            Log.d(TAG, "Onprepared cll");
            mp.start();
        } else {
            EventBus.getDefault().post(new RequestCurrentPosition());
        }
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
