package com.sankalpa.mustream.events;

/**
 * Created by deenesh12 on 1/27/18.
 */

public class PrepareEvent {
    public int music;
    public int offset = 0;

    public PrepareEvent(int m){
        music = m;
    }
    public PrepareEvent(int m, int o){
        music = m;
        offset = o;
    }
}
