package com.sankalpa.mustream;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MediaPlayerActivity extends AppCompatActivity {
    Thread player;
    private int QRcodeWidth = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        TextView textView = findViewById(R.id.ip_address);
        ImageView qrCode = findViewById(R.id.qr_code);

        String ipAddress = Network.getWifiIpAddress(this);

        Bitmap img = generateQRCode(ipAddress + ":" + Config.STREAM_PORT_ADDRESS);
        qrCode.setImageBitmap(img);

        textView.setText(ipAddress);

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
    public void play(View view) {
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
    }

    public void playNext(View view) {
    }
}
