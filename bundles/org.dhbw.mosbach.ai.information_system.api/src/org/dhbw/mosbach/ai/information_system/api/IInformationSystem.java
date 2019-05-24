package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.base.Position;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IInformationSystem {

    @WebMethod
    public boolean receivePosition(long v2Id, Position position);

    @WebMethod
    public ArrayList<Position> getNeighbours(long v2Id, double speed);

    @WebMethod
    public ArrayList<Position> getNeighbours(Position position, double radius);

    @WebMethod
    public void receiveFinished(long v2Id);
}
