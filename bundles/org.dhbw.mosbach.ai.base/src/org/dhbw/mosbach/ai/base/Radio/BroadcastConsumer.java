package org.dhbw.mosbach.ai.base.Radio;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class BroadcastConsumer implements Runnable, IBroadcastConsumer{

    String multiCastAddress;
    int multiCastPort;
    private volatile boolean stop=false;

    volatile String serviceTyp="";
    volatile ArrayList<String> serviceURLS = new ArrayList<>();

    public BroadcastConsumer( String multiCastAddress, int multiCastPort){
        this.multiCastAddress = multiCastAddress;
        this.multiCastPort = multiCastPort;
    }

    @Override
    public ArrayList<String> getServiceURLs() {
        return serviceURLS;
    }

    @Override
    public String getServiceTyp() {
        return serviceTyp;
    }

    public void stop(){
        this.stop=true;
    }

    public void run() {

        try {
            //Address
            final int bufferSize = 64 * 4; //Maximum size of transfer object

            //Create Socket
            System.out.println("Join Multicastsocket on address " + multiCastAddress + " and port " + multiCastPort + ".");
            InetAddress group = InetAddress.getByName(multiCastAddress);
            MulticastSocket s = new MulticastSocket(multiCastPort);
            s.joinGroup(group);



            //Receive data
            while (stop==false) {
                //System.out.println("Wating for datagram to be received...");

                //Create buffer
                byte[] buffer = new byte[bufferSize];
                s.receive(new DatagramPacket(buffer, bufferSize, group, multiCastPort));
                //  System.out.println("Datagram received!");


                try {


                    ServiceInformation serviceInformation = ServiceConverter.getServiceInformation(buffer);
                    if (serviceInformation!=null && serviceInformation.serviceTyp != null && serviceInformation.urls !=null){
                        this.serviceTyp = serviceInformation.serviceTyp;
                        this.serviceURLS = serviceInformation.urls;
                    }
                } catch (Exception e) {
                    System.out.println("No object could be read from the received UDP datagram."+e);
                }

            }

            s.leaveGroup(InetAddress.getByName(multiCastAddress));
            s.close();

        }catch (Exception e){
            System.out.println(e);
        }
    }

}
