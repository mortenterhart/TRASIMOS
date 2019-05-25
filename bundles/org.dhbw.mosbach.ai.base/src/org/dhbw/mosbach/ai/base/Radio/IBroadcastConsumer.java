package org.dhbw.mosbach.ai.base.Radio;
import java.util.ArrayList;

public interface IBroadcastConsumer {

    ArrayList<String> getServiceURLs();
    String getServiceTyp();
    void stop();

}
