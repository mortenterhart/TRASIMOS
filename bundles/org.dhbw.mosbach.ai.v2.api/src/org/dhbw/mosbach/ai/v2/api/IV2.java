package org.dhbw.mosbach.ai.v2.api;

import org.dhbw.mosbach.ai.base.Position;

public interface IV2 {

   V2Info getV2Information();
   Position getCurrentPosition();
   Position getDestination();
   Position getOrigin();
   long getId();
}
