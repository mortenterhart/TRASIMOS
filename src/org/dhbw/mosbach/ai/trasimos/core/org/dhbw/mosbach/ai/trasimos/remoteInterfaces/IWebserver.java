package org.dhbw.mosbach.ai.trasimos.core.org.dhbw.mosbach.ai.trasimos.remoteInterfaces;

import org.dhbw.mosbach.ai.trasimos.core.model.Position;

public interface IWebserver {
    public void receivePosition(long ID, Position position);
}
