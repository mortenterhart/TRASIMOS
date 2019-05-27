package org.dhbw.mosbach.ai.name_server.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.Configuration;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class NameServerSOAP implements INameServer{

    INameServer nameServer;

    public NameServerSOAP(String nameserverURL) throws MalformedURLException {

        //CREATE KLIENTÉL
        URL wsdlUrl =  new URL(nameserverURL+"?wsdl");
        QName qname = new QName(Configuration.NameService_IMPL_NameSpace,Configuration.NameService_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        nameServer  = service.getPort(INameServer.class);

    }

    @Override
    public String registerInfoServer(String url) {
        return nameServer.registerInfoServer(url);
    }

    @Override
    public String getInfoServer(Position position) {
        return nameServer.getInfoServer(position);
    }
}
