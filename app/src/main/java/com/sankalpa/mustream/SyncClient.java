package com.sankalpa.mustream;

import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.sankalpa.mustream.events.PauseEvent;
import com.sankalpa.mustream.events.PlayEvent;
import com.sankalpa.mustream.events.PrepareEvent;
import com.sankalpa.mustream.events.RequestCurrentPosition;
import com.sankalpa.mustream.events.ServerAddressEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by deenesh12 on 1/25/18.
 */

public class SyncClient implements Runnable{

    private static final String TAG = "SyncClient";
    AsyncHttpClient client;
    WebSocket w;

    @Override
    public void run() {
        EventBus.getDefault().register(this);
        client = AsyncHttpClient.getDefaultInstance();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void currentPositionRequested(RequestCurrentPosition e){
        w.send(SyncServer.REQUEST_STAT);
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startConnection(ServerAddressEvent e){
        Log.d(TAG, "Starting websocket" + e.getServer());

        client.websocket("http://" + e.getServer() + ":" + Config.WEBSOCKET_PORT + "/", null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if(ex != null){
                    Log.d(TAG, "Exception while connecting " + ex.getMessage());
                    return;
                }
                Log.d(TAG, "Connected " );
                w = webSocket;

                w.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
//                        Log.d(TAG, "Got message: " + s);

                        if(s.startsWith(SyncServer.PREPARE)){
                            int id = Integer.parseInt(s.split(" ")[1]);
                            EventBus.getDefault().post(new PrepareEvent(id));
                        } else if(s.startsWith(SyncServer.PLAY)){
                            final int offset = Integer.parseInt(s.split(" ")[1]);
                            EventBus.getDefault().post(new PlayEvent(offset));
                        } else if(s.startsWith(SyncServer.PAUSE)){
                            EventBus.getDefault().post(new PauseEvent());
                        }
                    }
                });
              /*  webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });*/
            }
        });
    }
}