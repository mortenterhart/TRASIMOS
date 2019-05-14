package org.dhbw.mosbach.ai.trasimos.core;

import org.dhbw.mosbach.ai.trasimos.core.model.Position;

import java.util.Vector;

public interface IV2 {

   Position getCurrentPosition();
   Position getDestination();
   Position getOrigin();
   int getId();
}
