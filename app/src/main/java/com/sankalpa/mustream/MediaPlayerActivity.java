package com.sankalpa.mustream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sankalpa.mustream.events.PauseEvent;
import com.sankalpa.mustream.events.PlayEvent;
import com.sankalpa.mustream.events.PlayNextEvent;
import com.sankalpa.mustream.events.PrepareEvent;
import com.sankalpa.mustream.events.StopEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "MediaPlayer";
    private int QRcodeWidth = 500;
    MediaPlayer mp;
    Thread server ;
    Intent intent;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        TextView textView = findViewById(R.id.ip_address);
        ImageView qrCode = findViewById(R.id.qr_code);

        String ipAddress = Network.getWifiIpAddress(this);

        Bitmap img = generateQRCode(ipAddress );
        qrCode.setImageBitmap(img);

        textView.setText(ipAddress);

        server = new Thread(new SyncServer(this));
        server.start();

        mp = new MediaPlayer();
        powerManager =(PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ""); // PARTIAL_WAKE_LOCK Only keeps CPU on
        wakeLock.acquire();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }
    public void play(View view){
        if(mp.isPlaying()){
            mp.pause();
            EventBus.getDefault().post(new PauseEvent());
            ((Button) findViewById(R.id.play_pause)).setText("Play");
        } else{
            mp.start();
            EventBus.getDefault().post(new PlayEvent(mp.getCurrentPosition()));
            ((Button) findViewById(R.id.play_pause)).setText("Pause");
        }
    }
    Bitmap generateQRCode(String v){
                BitMatrix bitMatrix;
                try {
                    bitMatrix = new MultiFormatWriter().encode(v, BarcodeFormat.DATA_MATRIX.QR_CODE,
                            QRcodeWidth, QRcodeWidth, null
                    );
                } catch (IllegalArgumentException Illegalargumentexception) {
                    return null;
                } catch (WriterException e1) {
                    return null;
                }
                int bitMatrixWidth = bitMatrix.getWidth();
                int bitMatrixHeight = bitMatrix.getHeight();

                int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

                for (int y = 0; y < bitMatrixHeight; y++) {
                    int offset = y * bitMatrixWidth;
                    for (int x = 0; x < bitMatrixWidth; x++) {
                        pixels[offset + x] = bitMatrix.get(x, y) ?
                                getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

                bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void select(View view) {
        intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            try {
                if(mp.isPlaying()) {
                    mp.stop();
                }
                mp.setOnErrorListener(this);
                mp.setOnPreparedListener(this);
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.pause();
                Uri datum = data.getData();
                mp.setDataSource(this, datum);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.prepareAsync();
        }
    }

    public void stop(View view) {
        mp.stop();
        EventBus.getDefault().post(new StopEvent());
    }

    public void playNext(View view) {
        EventBus.getDefault().post(new PlayNextEvent());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "Error playing file");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        EventBus.getDefault().post(new PlayEvent(0));
        Log.d(TAG, "Replaying");
    }
}
