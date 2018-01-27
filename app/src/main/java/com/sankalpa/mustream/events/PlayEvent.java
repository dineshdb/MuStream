package com.sankalpa.mustream.events;

/**
 * Created by deenesh12 on 1/26/18.
 */

public class PlayEvent {
    public int offset = 0;
    public String music;
    public int track;
    public PlayEvent(int offset){
        this.offset = offset;
    }

}
