package com.sankalpa.mustream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class SyncServer implements Runnable {
    Context c;

    public SyncServer(Context c){
        this.c = c;
    }
    @Override
    public void run() {

        final List<WebSocket> conn = new ArrayList<>();
        final AsyncHttpServer server = new AsyncHttpServer();
        server.websocket("/", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                conn.add(webSocket);
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
}
