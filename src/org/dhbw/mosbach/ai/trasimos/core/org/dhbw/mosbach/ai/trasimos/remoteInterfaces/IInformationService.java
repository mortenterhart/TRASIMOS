package org.dhbw.mosbach.ai.trasimos.core.org.dhbw.mosbach.ai.trasimos.remoteInterfaces;

import org.dhbw.mosbach.ai.trasimos.core.model.Position;

public interface IInformationService {
    public void receivePosition(long ID, Position position);
    public void getNeighbours(long ID, double speed);
    public void overtakeInformationService(Position position00, Position position10, Position position01, Position position11);
    public void receiveFinished(long ID);
}
