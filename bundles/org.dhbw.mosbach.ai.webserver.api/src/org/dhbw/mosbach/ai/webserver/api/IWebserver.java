package org.dhbw.mosbach.ai.webserver.api;

import org.dhbw.mosbach.ai.base.Position;

public interface IWebserver {

    public void receivePosition(long ID, Position position);
}
