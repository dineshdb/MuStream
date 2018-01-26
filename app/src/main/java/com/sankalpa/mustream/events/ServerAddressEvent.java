package com.sankalpa.mustream.events;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class ServerAddressEvent {
    String server;
    public ServerAddressEvent(String s){
        server = s;
    }

    public String getServer(){
        return server;
    }
}
