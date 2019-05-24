package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.Configuration;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class InformationSOAP implements IInformationSystem {

    IInformationSystem informationSystem;

    public InformationSOAP(String urlToInformationSystem) throws MalformedURLException {

        //CREATE KLIENTÉL
        URL wsdlUrl =  new URL(urlToInformationSystem);
        QName qname = new QName(Configuration.InfoSystem_NameSpace,Configuration.InfoSystem_Local_Part);
        Service service = Service.create(wsdlUrl, qname);
        informationSystem  = service.getPort(IInformationSystem.class);
    }

    @Override
    public boolean receivePosition(long v2Id, Position position) {
        return informationSystem.receivePosition(v2Id,position);
    }

    @Override
    public ArrayList<Position> getNeighbours(long v2Id, double speed) {
        return informationSystem.getNeighbours(v2Id,speed);
    }

    @Override
    public ArrayList<Position> getNeighbours(Position position, double radius) {
        return informationSystem.getNeighbours(position,radius);
    }

    @Override
    public void receiveFinished(long v2Id) {
        informationSystem.receiveFinished(v2Id);
    }
}
