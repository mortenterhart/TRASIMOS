package org.dhbw.mosbach.ai.v2.factory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IV2Factory {

    @WebMethod
    public void createV2Cars(int count);
}
