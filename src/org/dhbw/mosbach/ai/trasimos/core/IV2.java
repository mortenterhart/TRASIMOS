package org.dhbw.mosbach.ai.trasimos.core;

import org.dhbw.mosbach.ai.trasimos.core.model.Position;

import java.util.Vector;

public interface IV2 {

    void getPosition(Position position, Vector<Long> direction, double speed);
}
