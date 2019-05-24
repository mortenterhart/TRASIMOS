package org.dhbw.mosbach.ai.v2.factory;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class V2FactoryClient {

    public static void main(String[] args) throws Exception {
        URL wsdlUrl = new URL("http://localhost:25001/v2factory/createV2Cars?wsdl");
        QName qname = new QName("http://factory.v2.ai.mosbach.dhbw.org/", "FactoryServiceService");
        Service factory = Service.create(wsdlUrl, qname);
        IV2Factory factoryService = factory.getPort(IV2Factory.class);
        factoryService.createV2Cars(500);
    }
}
