package com.sankalpa.mustream;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MediaPlayerActivity extends AppCompatActivity {
    Thread player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        String message=getResources().getString(R.string.media);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        this.player = new Thread(new NetworkDiscoveryServer(this));
        this.player.start();
    }

    public void play(View view) {
    }

    public void generateQRCode(String str){

    }
}
