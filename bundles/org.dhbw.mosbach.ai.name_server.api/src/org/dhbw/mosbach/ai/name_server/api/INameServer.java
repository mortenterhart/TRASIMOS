package org.dhbw.mosbach.ai.name_server.api;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.dhbw.mosbach.ai.base.Position;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface INameServer {

	@WebMethod
	public String registerInfoServer(String url);
	@WebMethod
    public String getInfoServer(Position position);
}
