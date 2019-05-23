package org.dhbw.mosbach.ai.base.Radio;

import org.dhbw.mosbach.ai.radio.api.IRadio;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterOnRadio {


    String url;


    public RegisterOnRadio(String RadioUrl) throws MalformedURLException {
        this.url=RadioUrl;
    }

    public void registrateURLOnRadio(String contentTypeOfService,String urlOfService) throws MalformedURLException {
        URL wsdlUrl = new URL(url+"?wsdl");
        System.out.println("Try To register nameService on: "+wsdlUrl.toString());

        QName qname = new QName(Configuration.Radio_Registration_IMPL_NameSpace,Configuration.Radio_Registration_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        IRadio registerService = service.getPort(IRadio.class);
        registerService.registerServiceAccess(contentTypeOfService,urlOfService);
    }
}
