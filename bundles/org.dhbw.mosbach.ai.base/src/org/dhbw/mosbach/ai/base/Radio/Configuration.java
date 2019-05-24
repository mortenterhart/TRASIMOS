package org.dhbw.mosbach.ai.base.Radio;


public class Configuration {
    public static String general_https =  "http://";

    public static String general_Seperation = ";";

    public static String Radio_multiCastAddress = "224.0.0.1";
    public static int Radio_multiCastPort = 50000;
    public static final String Radio_ContentType = "radio";
    public static final int Radio_Delay_Broadcast= 1000;

    public static String Radio_Registration_url = ":20000/Registration";
    public static final String Radio_Registration_IMPL_NameSpace = "http://provider.radio.ai.mosbach.dhbw.org/";
    public static final String Radio_Registration_Local_Part = "RegisterServiceService";


    public static String NameService_multiCastAddress = "224.0.0.2";
    public static int NameService_multiCastPort = 50001;
    public static final String NameService_ContentType = "nameservice";
    public static final int NameService_Delay_Broadcast = 200;

    public static String Webserver_multiCastAddress = "224.0.0.3";
    public static int Webserver_multiCastPort = 50002;
    public static final String Webserver_ContentType = "webserver";
    public static final int Webserver_Delay_Broadcast = 200;

}
