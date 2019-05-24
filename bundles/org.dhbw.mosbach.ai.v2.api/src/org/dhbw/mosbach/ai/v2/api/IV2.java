package org.dhbw.mosbach.ai.v2.api;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.V2Info;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService()
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IV2 {

   @WebMethod
   V2Info getV2Information();

   @WebMethod
   Position getCurrentPosition();

   @WebMethod
   Position getDestination();

   @WebMethod
   Position getOrigin();

   @WebMethod
   long getId();
}
