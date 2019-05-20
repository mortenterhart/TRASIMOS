package org.dhbw.mosbach.ai.name_server.api;

import org.dhbw.mosbach.ai.base.Position;

public interface INameServer {

	public String registerInfoServer(String url);
    public String getInfoServer(Position position);
}
