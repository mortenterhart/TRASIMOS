package org.dhbw.mosbach.ai.radio.provider;

import org.dhbw.mosbach.ai.base.Radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.radio.api.RadioSOAP;

import java.net.MalformedURLException;
import java.util.ArrayList;

//EXAMPLE HOW TO USE THE RADIO
public class Main {
    public static void main(String args[]){



        //RADIO MUST BE STARTED [not by client]
        Radio radio = new Radio();
        Thread radioT = new Thread(radio);
        radioT.start();


        //CLIENT

        //1. Listen to Radio
        BroadcastConsumer radioListener = new BroadcastConsumer(Configuration.Radio_multiCastAddress,Configuration.Radio_multiCastPort);
        Thread radioListenerThread = new Thread(radioListener);
        radioListenerThread.start();


        //2. Listen to Nameserver
        BroadcastConsumer  nameListener = new BroadcastConsumer(Configuration.NameService_multiCastAddress,Configuration.NameService_multiCastPort);
        Thread nameListenerThread = new Thread(nameListener);
        nameListenerThread.start();

        //3. Listen to Webserver
        BroadcastConsumer  webListener = new BroadcastConsumer(Configuration.Webserver_multiCastAddress,Configuration.Webserver_multiCastPort);
        Thread webListenerThread = new Thread(webListener);
        webListenerThread.start();


        //--------------------------------Meanwhile Nameserver and Webserver register ---------------------------//
        //1. Get Radio register URL
        while (radioListener.isServiceFound()==false){
            //radio didnt post its own service yet :(
        }

        //radio URL
        if (radioListener.getServiceURLs().size()>0) {
            String radioRegiURl = radioListener.getServiceURLs().get(0);


            RadioSOAP radioSOAP = null;
            try {
                radioSOAP = new RadioSOAP(radioRegiURl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            //Register nameService
            String nameserviceURL = "http://nameserviceUrl/NameService";
            radioSOAP.registerServiceAccess(Configuration.NameService_ContentType,nameserviceURL);


            //Register webservice
            String webServiceURL = "http://nameserviceUrl/NameService";
            radioSOAP.registerServiceAccess(Configuration.Webserver_ContentType,webServiceURL);


        }

        //--------------------------------Meanwhile Nameserver and Webserver register ---------------------------//


        //Now Check if Nameservice url && webservice url is posted

        while (true){

            if (nameListener.isServiceFound()){

                ArrayList<String> urlsOfNameService = nameListener.getServiceURLs();

                for (String s:urlsOfNameService) {
                    System.out.println("Nameservice on URL : "+s);
                }

            }


            if (webListener.isServiceFound()){

                ArrayList<String> urlsOfNameService = webListener.getServiceURLs();

                for (String s:urlsOfNameService) {
                    System.out.println("Webserver on URL : "+s);
                }

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //RegisterService.startService(null, Configuration.general_https+"0.0.0.0"+Configuration.Radio_Registration_url);
        //while (true);
    }
}
