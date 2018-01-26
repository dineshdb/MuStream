package com.sankalpa.mustream;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.sankalpa.mustream.events.ServerAddressEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by deenesh12 on 1/25/18.
 */

public class SyncClient implements Runnable{

    AsyncHttpClient client;

    @Override
    public void run() {
        EventBus.getDefault().register(this);
        client = AsyncHttpClient.getDefaultInstance();
    }
    @Subscribe
    public void startConnection(ServerAddressEvent e){
        client.websocket(e.getServer(), null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
            }
        });
    }
}