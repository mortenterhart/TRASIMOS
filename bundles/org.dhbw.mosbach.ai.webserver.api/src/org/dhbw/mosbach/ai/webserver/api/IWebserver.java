package org.dhbw.mosbach.ai.webserver.api;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.dhbw.mosbach.ai.base.V2Info;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IWebserver {

    @WebMethod
    public void receivePosition(V2Info v2Info);
}
