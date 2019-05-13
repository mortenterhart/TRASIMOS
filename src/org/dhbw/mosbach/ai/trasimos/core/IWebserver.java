package org.dhbw.mosbach.ai.trasimos.core;

import org.dhbw.mosbach.ai.trasimos.core.model.Position;

public interface IWebserver {

    public void receivePosition(long ID, Position position);
}
