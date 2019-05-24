package org.dhbw.mosbach.ai.base.Radio;


public class Configuration {
    public static String general_https =  "http://";

    public static String general_Seperation = ";";

    public static double root00_mappoint_lang = 0;
    public static double root00_mappoint_lat = 0;

    public static double root11_mapppoint_lang = 2;
    public static double root11_mapppoint_lat = 2;


    //Radio Multicast Group
    public static String Radio_multiCastAddress = "224.0.0.1";
    public static int Radio_multiCastPort = 50000;
    public static final String Radio_ContentType = "radio";
    public static final int Radio_Delay_Broadcast= 1000;


    //Nameservice Multicast Group
    public static String NameService_multiCastAddress = "224.0.0.2";
    public static int NameService_multiCastPort = 50001;
    public static final String NameService_ContentType = "nameservice";
    public static final int NameService_Delay_Broadcast = 200;
    public static final String NameSrvice_url = ":30000/Nameservice";

    //Webserver Multicast
    public static String Webserver_multiCastAddress = "224.0.0.3";
    public static int Webserver_multiCastPort = 50002;
    public static final String Webserver_ContentType = "webserver";
    public static final int Webserver_Delay_Broadcast = 200;




    //SOAP Access

    //Radio QName
    public static String Radio_Registration_url = ":20000/Registration";
    public static final String Radio_Registration_IMPL_NameSpace = "http://provider.radio.ai.mosbach.dhbw.org/";
    public static final String Radio_Registration_Local_Part = "RegisterServiceService";

    //Nameservice QName
    public static final String NameService_IMPL_NameSpace = "http://provider.name_server.ai.mosbach.dhbw.org/";
    public static final String NameService_Local_Part ="NameServerImplService";

    //V2 QName
    public static final String V2_NameSpace ="http://provider.v2.ai.mosbach.dhbw.org/";
    public static final String V2_Local_Part ="V2ImplService";

    //Info QName
    public static final String InfoSystem_NameSpace="http://provider.information_system.ai.mosbach.dhbw.org/";
    public static final String InfoSystem_Local_Part="InformationSystemImplService";

    //Webserver QName
    //V2 QName
    //URL = Configuration.general_https+ <ID> + port (between V2PortMin and V2PortMax) + Configuration.V2SOAP
    public static String V2SOAP = "/V2InfoService";
    public static int V2PortMin = 30000;
    public static int V2PortMax = 40000;

}
