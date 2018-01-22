package com.sankalpa.mustream;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by deenesh12 on 1/21/18.
 */

public class NetworkDiscoveryServer implements Runnable {
    Context c;
    private static final String TAG = "com.sankalpa.mustream";
    DatagramSocket socket;

    public NetworkDiscoveryServer(Context c){
        this.c = c;
    }

    @Override
    public void run() {
        WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifi.createMulticastLock("dk.aboaya.pingpong");
        lock.acquire();

        try {
            socket = new DatagramSocket(Constants.PORT_ADDRESS, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                Log.d(TAG, "Ready to accept packets");

                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                String message = new String(packet.getData()).trim();

                if (message.equals("DISCOVER_MuSTEEAMSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_MuSTEEAMSERVER_RESPONSE".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());

                    socket.send(sendPacket);
                    Log.d(TAG, "Sent packets to " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
