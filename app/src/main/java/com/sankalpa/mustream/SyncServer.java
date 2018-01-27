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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class SyncServer implements Runnable {
    Context c;
    AsyncHttpServer server;
    List<WebSocket> conn;

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
                    webSocket.send("prepare:" + currentMusic);
                }

                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            conn.remove(webSocket);
                        }
                    }
                });
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("SERVERTAG",s);
                        Toast.makeText(c,s,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        server.listen(Config.WEBSOCKET_PORT);
    }

    @Subscribe
    public void stopMusic(StopEvent e){
        for(WebSocket w:conn){
            w.send("stop");
        }
    }

    @Subscribe
    public void pauseMusic(PauseEvent e){
        for(WebSocket w:conn){
            w.send("pause");
        }
    }

    @Subscribe
    public void playMusic(PlayEvent e){
        for(WebSocket w:conn){
            w.send("play");
        }
    }
    @Subscribe
    public void prepareMusic(PrepareEvent e){
        currentMusic = e.music;
        for(WebSocket w: conn){
            w.send("prepare " + currentMusic );
        }
    }
}
