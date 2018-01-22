package com.sankalpa.mustream;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by deenesh12 on 1/21/18.
 */

public class NetworkDiscoveryClient implements Runnable {
    private static final String TAG = "com.sankalpa.mustream";
    @Override
    public void run() {
        DatagramSocket c;
        try{
            c = new DatagramSocket();
            c.setBroadcast(true);
            byte[] sendData = "DISCOVER_MuSTEEAMSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("0.0.0.0"), Constants.PORT_ADDRESS);
            c.send(sendPacket);

            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements()){
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if(networkInterface.isLoopback() || !networkInterface.isUp()){
                    continue;
                }

                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()){
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null){
                        continue;
                    }

                    try {
                        DatagramPacket sPacket = new DatagramPacket(sendData, sendData.length, broadcast, Constants.PORT_ADDRESS);
                        c.send(sPacket);
                    } catch (UnknownHostException e){
                    }
                }

                Log.d(TAG, "Tried searching over all interfaces, waiting for response.");

                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                c.receive(receivePacket);
                Config.getInstance().setIpAddress(receivePacket.getAddress().getHostAddress());
                Log.d(TAG, "Broadcast response from " + receivePacket.getAddress().getHostAddress());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
