package org.dhbw.mosbach.ai.webserver.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.V2Info;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class WebserverSOAP implements IWebserver{


    IWebserver iWebserver;

    public WebserverSOAP(String v2URL) throws MalformedURLException {
        //CREATE KLIENTÉL
        URL wsdlUrl =  new URL(v2URL);
        QName qname = new QName(Configuration.V2_NameSpace,Configuration.V2_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        iWebserver  = service.getPort(IWebserver.class);

    }

    @Override
    public void receivePosition(V2Info v2Info) {
        iWebserver.receivePosition(v2Info);
    }
}
