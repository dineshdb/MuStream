package com.sankalpa.mustream;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnected()){
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        } else {
        }

        //PowerManager powerManager =(PowerManager)this.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ""); // PARTIAL_WAKE_LOCK Only keeps CPU on
        //wakeLock.acquire();
    }

    public void speaker(View view) {
        Config.getInstance().setMode(Mode.SPEAKER);
        Intent intent = new Intent(this, SpeakerActivity.class);
        startActivity(intent);
    }

    public void microphone(View view) {
        Config.getInstance().setMode(Mode.MICROPHONE);
        Intent intent = new Intent(this, MicroPhoneActivity.class);
        startActivity(intent);
    }

    public void mediaPlayer(View view) {
        Config.getInstance().setMode(Mode.MUSIC_PLAYER);
        Intent intent = new Intent(this, MediaPlayerActivity.class);
        startActivity(intent);
    }
}