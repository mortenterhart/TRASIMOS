package org.dhbw.mosbach.ai.radio.provider;


import org.dhbw.mosbach.ai.base.radio.ServiceConverter;
import org.dhbw.mosbach.ai.base.radio.ServiceInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BroadcastPublisher implements Runnable {

    private String ContentType;
    private String multiCastAddress;
    private int multiCastPort;
    private volatile ServiceInformation serviceInformation;
    private InetAddress group;
    private MulticastSocket s;
    private int delayInMs;

    private volatile boolean stop = false;

    public void stop() {
        this.stop = true;
    }

    public BroadcastPublisher(String multiCastAddress, int multiCastPort, ServiceInformation serviceInformation, String ContentType, int DelayInMs) throws IOException {
        this.multiCastAddress = multiCastAddress;
        this.multiCastPort = multiCastPort;
        this.serviceInformation = serviceInformation;
        this.ContentType = ContentType;
        this.delayInMs = DelayInMs;

        //Create Socket
        System.out.println("Create socket on address " + multiCastAddress + " and port " + multiCastPort + ".");
        group = InetAddress.getByName(multiCastAddress);
        s = new MulticastSocket(multiCastPort);
        s.joinGroup(group);
    }

    @Override
    public void run() {

        while (!stop) {
            try {
                Thread.sleep(delayInMs);
                Thread.sleep(5000);
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() throws IOException {
        //Address
        //String multiCastAddress = "224.0.0.1";
        //final int multiCastPort = 52684;
        //Prepare Data
        byte[] data = ServiceConverter.convertServiceInformationToByte(serviceInformation);

        if (serviceInformation.urls.size() > 0)
            System.out.println(serviceInformation.serviceTyp + " : " + serviceInformation.urls.get(0));
        //Send data
        s.send(new DatagramPacket(data, data.length, group, multiCastPort));
    }

    public void setMessage(ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
    }

}
