package org.dhbw.mosbach.ai.information_system.provider;


import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.information_system.api.IInformationSystem;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.ArrayList;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.information_system.api.IInformationSystem")
public class InformationSystemSOAP implements IInformationSystem {

    public InformationSystemSOAP(){

    }

    public static void main(String[] args){
        IInformationSystem impl = new InformationSystemSOAP();
        Object implementor = impl;
        String address = "http://localhost:9001/extremeCoolSoapApi";
        Endpoint.publish(address,implementor);
    }


    @Override
    public boolean receivePosition(long v2Id, Position position) {
        return false;
    }


    @Override
    public ArrayList<Position> getNeighbours(long v2Id, double speed) {
        return null;
    }

    @Override
    public ArrayList<Position> getNeighbours(Position position, double radius) {
        return null;
    }

    @Override
    public void receiveFinished(long v2Id) {

    }
}
