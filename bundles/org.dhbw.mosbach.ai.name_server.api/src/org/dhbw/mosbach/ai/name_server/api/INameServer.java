package org.dhbw.mosbach.ai.name_server.api;

import org.dhbw.mosbach.ai.base.Position;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Provides a way of global state to see which information servers hold which chunk of the entire map
 * and also serves as a look up utility for V2 cars
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface INameServer {

    /**
     * Registers an information server with the Name server to provide a
     * map chunk of the information server to handle
     *
     * @param url Given information server URL
     * @return A string representation of the boundaries for the map chunk
     */
    @WebMethod
    public String registerInfoServer(String url);

    /**
     * Returns the information server that contains the map chunk, where the given position lies within
     *
     * @param position Given position
     * @return A URL to an information server
     */
    @WebMethod
    public String getInfoServer(Position position);
}
