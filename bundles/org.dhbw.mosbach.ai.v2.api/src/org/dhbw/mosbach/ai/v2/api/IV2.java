package org.dhbw.mosbach.ai.v2.api;

import org.dhbw.mosbach.ai.v2.api.model.Position;

import java.util.Vector;

public interface IV2 {

    void getPosition(Position position, Vector<Long> direction, double speed);
}
