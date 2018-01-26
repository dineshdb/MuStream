package com.sankalpa.mustream;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sankalpa.mustream.events.PauseEvent;
import com.sankalpa.mustream.events.PlayEvent;
import com.sankalpa.mustream.events.StopEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "MediaPlayer";
    private int QRcodeWidth = 500;
    MediaPlayer mp;
    Thread server ;

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
        mp.setOnErrorListener(this);
        mp.setOnPreparedListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wildfire);
            mp.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();
/*        this.player = new Thread(new NetworkDiscoveryServer(this));
        this.player.start();*/

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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playMusic(PlayEvent e){
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pauseMusic(PauseEvent e){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopMusic(StopEvent e){

    }
    public void play(View view){
        if(mp.isPlaying()){
            mp.pause();
        } else{
            mp.start();
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

    public void stop(View view) {
        mp.stop();
    }

    public void playNext(View view) {
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
}
