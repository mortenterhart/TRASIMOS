package org.dhbw.mosbach.ai.radio.api;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class RadioSOAP implements IRadio {

    IRadio radio;

    public RadioSOAP(String radioURL) throws MalformedURLException {

        URL wsdlUrl = new URL(radioURL+"?wsdl");
        System.out.println("Try To register nameService on: "+wsdlUrl.toString());
        QName qname = new QName("http://provider.radio.ai.mosbach.dhbw.org/","RegisterServiceService");
        Service service = Service.create(wsdlUrl, qname);
        radio = service.getPort(IRadio.class);
    }



    @Override
    public void registerServiceAccess(String url, String serviceTyp) {
        radio.registerServiceAccess(serviceTyp,url);
    }
}
