package org.dhbw.mosbach.ai.webserver.api;

import org.dhbw.mosbach.ai.webserver.api.model.Position;

public interface IWebserver {

    public void receivePosition(long ID, Position position);
}
