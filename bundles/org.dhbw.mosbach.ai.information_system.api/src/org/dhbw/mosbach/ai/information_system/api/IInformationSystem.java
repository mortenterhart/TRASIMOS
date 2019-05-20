package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.information_system.api.model.Position;

import java.util.List;

public interface IInformationSystem {

    public void receivePosition(long v2Id, Position position);

    public List<Position> getNeighbours(long v2Id, double speed);

    public List<Position> getNeighbours(Position position, double radius);

    public void overtakeInformationService(Position position00, Position position10, Position position01, Position position11);

    public void receiveFinished(long v2Id);
}
