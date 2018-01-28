package com.sankalpa.mustream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.rtsp.RtspServer;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MicroPhoneActivity extends AppCompatActivity implements Session.Callback {

    private static final String TAG = "MicrophoneActivity";
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_phone);
        Intent intent = getIntent();
        String message=getResources().getString(R.string.microphone);
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(RtspServer.KEY_PORT, String.valueOf(Config.STREAM_PORT_ADDRESS));
        editor.commit();

        // Configures the SessionBuilder See this https://stackoverflow.com/questions/26405539/send-a-multicast-audio-in-rtsp-using-libstreaming-for-upstreaming-from-an-androi
        session = SessionBuilder.getInstance()
                .setCallback(this)
                .setAudioEncoder(SessionBuilder.AUDIO_AMRNB)
                .setContext(getApplicationContext())
//                .setAudioQuality(AudioQuality.parseQuality("80kbps"))
//                .setDestination("224.0.0.1")
                .setVideoEncoder(SessionBuilder.VIDEO_NONE).build();

        // Starts the RTSP server
        this.startService(new Intent(this, RtspServer.class));
        //  startServer();
    }

    public void startServer(){
        try{
            AudioManager audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audio.setMode(AudioManager.MODE_IN_COMMUNICATION);
            AudioGroup audioGroup = new AudioGroup();
            audioGroup.setMode(AudioGroup.MODE_NORMAL);
            AudioStream audioStream = new AudioStream(InetAddress.getLocalHost());
            audioStream.setCodec(AudioCodec.PCMU);
            audioStream.setMode(RtpStream.MODE_NORMAL);
            //set receiver(vlc player) machine ip address(please update with your machine ip)
            audioStream.associate(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, (byte)1, (byte)19 }), 22222);
            audioStream.join(audioGroup);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.release();
    }
    @Override
    public void onBitrateUpdate(long l) {
        Log.d(TAG, "Bitrate : " + l);
    }

    @Override
    public void onSessionError(int i, int i1, Exception e) {
        Log.e(TAG, "Session error");
    }

    @Override
    public void onPreviewStarted() {

    }

    @Override
    public void onSessionConfigured() {
        Log.d(TAG, "Session configured");
        session.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d(TAG, "Session started");
    }

    @Override
    public void onSessionStopped() {

    }
}
