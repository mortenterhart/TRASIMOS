package org.dhbw.mosbach.ai.v2.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.V2Info;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class V2SOAP implements IV2{

    IV2 iv2;

    public V2SOAP(String v2URL) throws MalformedURLException {
        //CREATE KLIENTÉL
        URL wsdlUrl =  new URL(v2URL);
        QName qname = new QName(Configuration.V2_NameSpace,Configuration.V2_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        iv2  = service.getPort(IV2.class);

    }

    @Override
    public V2Info getV2Information() {
        return iv2.getV2Information();
    }

    @Override
    public Position getCurrentPosition() {
        return iv2.getCurrentPosition();
    }

    @Override
    public Position getDestination() {
        return iv2.getDestination();
    }

    @Override
    public Position getOrigin() {
        return iv2.getOrigin();
    }

    @Override
    public void reduceSpeed(double distance) {
        iv2.reduceSpeed(distance);
    }

    @Override
    public long getId() {
        return iv2.getId();
    }
}
