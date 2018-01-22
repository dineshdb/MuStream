package com.sankalpa.mustream;

/**
 * Created by deenesh12 on 1/21/18.
 */

public class Config {
    public static final int DISCOVERY_PORT_ADDRESS = 9863;
    private String ipAddress;
    private static Config instance = null;

    private Mode mode;

    private Config(){
    }

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }

    public String getIpAddress(){
        return ipAddress;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
