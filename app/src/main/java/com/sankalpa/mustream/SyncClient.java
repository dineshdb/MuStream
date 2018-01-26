package com.sankalpa.mustream;

/**
 * Created by deenesh12 on 1/25/18.
 */

import com.sankalpa.mustream.events.PlayEvent;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import org.java_websocket.drafts.Draft;

public class SyncClient extends WebSocketClient {

    public SyncClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public SyncClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("Hello, it is me. Mario :)");
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        EventBus.getDefault().post(new PlayEvent());
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}