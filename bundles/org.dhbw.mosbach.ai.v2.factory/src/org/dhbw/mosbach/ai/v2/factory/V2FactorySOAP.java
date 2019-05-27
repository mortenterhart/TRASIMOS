package org.dhbw.mosbach.ai.v2.factory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class V2FactorySOAP implements IV2Factory {

    IV2Factory factoryService;

    public V2FactorySOAP(String V2FactoryUrl) throws MalformedURLException {

        URL wsdlUrl = new URL(V2FactoryUrl + "?wsdl");
        QName qname = new QName("http://factory.v2.ai.mosbach.dhbw.org/", "FactoryServiceService");
        Service factory = Service.create(wsdlUrl, qname);
        factoryService = factory.getPort(IV2Factory.class);
    }

    @Override
    public void createV2Cars(int count) {
        factoryService.createV2Cars(count);
    }
}
