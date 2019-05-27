package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.V2Info;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IInformationSystem {

    @WebMethod
    public boolean receivePosition(V2Info v2Info);

    @WebMethod
    public ArrayList<V2Info> getNeighbours(V2Info v2Info);

    @WebMethod
    public ArrayList<V2Info> getNeighboursRemote(Position position, double radius);

    @WebMethod
    public void receiveFinished(V2Info v2Info);
}
