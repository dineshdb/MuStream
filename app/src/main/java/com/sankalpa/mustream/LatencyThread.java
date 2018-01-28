package com.sankalpa.mustream;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by deenesh12 on 1/25/18.
 */

public class LatencyThread implements Runnable {

    private static final String TAG = "LATENCY";

    @Override
    public void run() {
        while(!Thread.interrupted()){
            String host = Config.getInstance().getIpAddress();
            int timeOut = 3000;
            int sum = 0;
            for(int i=0; i < 5; i++){
                long BeforeTime = System.currentTimeMillis();
                try{
                    InetAddress.getByName(host).isReachable(timeOut);
                    long AfterTime = System.currentTimeMillis();
                    sum += AfterTime - BeforeTime;
                } catch (UnknownHostException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Config.getInstance().setLatency(sum / 5);
            //Log.d(TAG, "" + sum / 5);
        }
    }
}
