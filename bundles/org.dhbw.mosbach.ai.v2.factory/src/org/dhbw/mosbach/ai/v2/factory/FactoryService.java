package org.dhbw.mosbach.ai.v2.factory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.v2.factory.FactoryService")
public class FactoryService implements IV2Factory {

    @WebMethod
    public void createV2Cars(int count) {
        V2Factory factory = new V2Factory();
        factory.createV2Cars(count);
    }

    public static void startService() {
        Object service = new FactoryService();
        String factoryAddress = "http://localhost:25001/v2factory/createV2Cars";
        Endpoint.publish(factoryAddress, service);
    }

    public static void main(String[] args) {
        startService();

        while(true);
    }
}
