package org.dhbw.mosbach.ai.information_system.api;

import org.dhbw.mosbach.ai.base.Position;

import java.util.List;

public interface IInformationSystem {

    public boolean receivePosition(long v2Id, Position position);

    public List<Position> getNeighbours(long v2Id, double speed);

    public List<Position> getNeighbours(Position position, double radius);

    public void receiveFinished(long v2Id);
}
