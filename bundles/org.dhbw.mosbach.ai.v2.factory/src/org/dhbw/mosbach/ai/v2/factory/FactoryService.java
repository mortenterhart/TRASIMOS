package org.dhbw.mosbach.ai.v2.factory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.v2.factory.IV2Factory")
public class FactoryService implements IV2Factory {

    private V2Factory factory = new V2Factory();

    @WebMethod
    public void createV2Cars(int count) {
        System.out.println("V2Factory: Creating " + count + " cars");
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
