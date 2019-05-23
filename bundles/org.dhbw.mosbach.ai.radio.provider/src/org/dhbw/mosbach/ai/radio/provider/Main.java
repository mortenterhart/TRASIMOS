package org.dhbw.mosbach.ai.radio.provider;

//EXAMPLE HOW TO USE THE RADIO
public class Main {
    public static void main(String args[]){


        //RADIO MUST BE STARTED [not by client]
        Radio radio = new Radio();
        Thread radioT = new Thread(radio);
        radioT.start();


        //CLIENT








        //RegisterService.startService(null, Configuration.general_https+"0.0.0.0"+Configuration.Radio_Registration_url);
        //while (true);
    }
}
