package org.dhbw.mosbach.ai.webserver.api;

import org.dhbw.mosbach.ai.base.V2Info;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class WebserverSOAP implements IWebserver {

    private IWebserver iWebserver;

    public WebserverSOAP(String v2URL) throws MalformedURLException {
        //CREATE KLIENTÉL
        URL wsdlUrl = new URL(v2URL);
        QName qname = new QName("http://provider.webserver.ai.mosbach.dhbw.org/", "WebserverImplService");
        Service service = Service.create(wsdlUrl, qname);
        iWebserver = service.getPort(IWebserver.class);

    }

    @Override
    public void receivePosition(V2Info v2Info) {
        iWebserver.receivePosition(v2Info);
    }
}
