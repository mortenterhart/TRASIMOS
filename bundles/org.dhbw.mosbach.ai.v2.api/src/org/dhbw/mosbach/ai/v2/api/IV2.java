package org.dhbw.mosbach.ai.v2.api;

import org.dhbw.mosbach.ai.base.Position;

import java.util.Vector;

public interface IV2 {

   Position getCurrentPosition();
   Position getDestination();
   Position getOrigin();
   int getId();
}
