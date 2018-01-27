package com.sankalpa.mustream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.sankalpa.mustream.events.PauseEvent;
import com.sankalpa.mustream.events.PlayEvent;
import com.sankalpa.mustream.events.PrepareEvent;
import com.sankalpa.mustream.events.StopEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class SyncServer implements Runnable {
    private static final String TAG = "SyncServer";
    Context c;
    AsyncHttpServer server;
    List<WebSocket> conn;

    public static final String PAUSE = "pause";
    public static final String PLAY = "play ";
    public static final String STOP = "stop";
    public static final String PREPARE = "prepare ";
    public static final String REQUEST_STAT = "request_stat";

    int currentMusic = -1;
    public SyncServer(Context c){
        this.c = c;
        EventBus.getDefault().register(this);
        conn = new ArrayList<>();
        server = new AsyncHttpServer();
    }
    @Override
    public void run() {
        server.websocket("/", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                conn.add(webSocket);
                if(currentMusic != -1){
                    webSocket.send(PREPARE + currentMusic);
                }

                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error " + ex.getMessage());
                        } finally {
                            conn.remove(webSocket);
                        }
                    }
                });
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
//                        Log.d(TAG, "Got message " + s);
                        if(s.startsWith(REQUEST_STAT)){
                            webSocket.send(PLAY + Config.getInstance().mp.getCurrentPosition());
                            if(!Config.getInstance().mp.isPlaying()){
                                webSocket.send(PAUSE);
                            }
                        }
                    }
                });

            }
        });
        server.listen(Config.WEBSOCKET_PORT);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void stopMusic(StopEvent e){
        int size = conn.size();
        for(int i = 0; i < size; i++){
            conn.get(i).send(STOP);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void pauseMusic(PauseEvent e){
        int size = conn.size();
        for(int i = 0; i < size; i++){
            conn.get(i).send(PAUSE);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void playMusic(PlayEvent e){
        final String msg = PLAY + e.offset;
        int size = conn.size();
        for(int i = 0; i < size; i++){
            conn.get(i).send(msg);
        }
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void prepareMusic(PrepareEvent e){
        currentMusic = e.music;
        final String msg = PREPARE + e.music;
        for(WebSocket w: conn){
            w.send(msg);
        }
    }
}
