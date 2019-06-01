package org.dhbw.mosbach.ai.radio.api;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService()
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IRadio {

    @WebMethod
    public void registerServiceAccess(String serviceTyp, String url);
}
