package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.base.radio.Configuration;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class InformationSOAP implements IInformationSystem {

    private IInformationSystem informationSystem;

    public InformationSOAP(String urlToInformationSystem) throws MalformedURLException {
        //CREATE KLIENTÉL
        System.out.println(urlToInformationSystem);
        URL wsdlUrl = new URL(urlToInformationSystem + "?wsdl");
        QName qname = new QName(Configuration.InfoSystem_NameSpace, Configuration.InfoSystem_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        informationSystem = service.getPort(IInformationSystem.class);
    }


    @Override
    public boolean receivePosition(V2Info v2Info) {
        return informationSystem.receivePosition(v2Info);
    }

    @Override
    public ArrayList<V2Info> getNeighbours(V2Info v2Info) {
        return informationSystem.getNeighbours(v2Info);
    }

    @Override
    public ArrayList<V2Info> getNeighboursRemote(Position position, double radius) {
        return informationSystem.getNeighboursRemote(position, radius);
    }

    @Override
    public void receiveFinished(V2Info v2Info) {
        informationSystem.receiveFinished(v2Info);
    }
}
