package org.dhbw.mosbach.ai.name_server.provider;

import org.dhbw.mosbach.ai.base.MapChunk;
import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.Radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.base.Radio.RegisterOnRadio;
import org.dhbw.mosbach.ai.name_server.api.INameServer;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.name_server.api.INameServer")
@Component(name = "name-server", service = INameServer.class)
public class NameServerImpl implements INameServer {
	
	Map<String, MapChunk> infoServers = Collections.synchronizedMap(new HashMap<>());
	private static MapChunk wholeMap;

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Name Server booting ...");
        
        wholeMap = new MapChunk();
        wholeMap.setTopLeft(new Position(49.8000, 9.0000));
        wholeMap.setTopRight(new Position(49.8000, 9.5000));
        wholeMap.setBottomLeft(new Position(49.3000, 9.000));
        wholeMap.setBottomRight(new Position(49.3000, 9.5000));
        
		Object implementor = new NameServerImpl();
		String address = "http://0.0.0.0:9001/NameServer";
		Endpoint.publish(address, implementor);

        BroadcastConsumer radioListener = new BroadcastConsumer(Configuration.Radio_multiCastAddress,Configuration.Radio_multiCastPort);
		Thread radioListenerThread = new Thread(radioListener);
		radioListenerThread.start();
		
        while (radioListener.isServiceFound() == false){}

        if (radioListener.getServiceURLs().size() > 0) {
        	String radioRegiURl = radioListener.getServiceURLs().get(0);
        	RegisterOnRadio registerOnRadio = new RegisterOnRadio(radioRegiURl);

        	//Register nameService
        	String nameserviceURL = "http://0.0.0.0:9001/NameServer";
        	try {
        		registerOnRadio.registrateURLOnRadio(Configuration.NameService_ContentType, nameserviceURL);
        	} catch (MalformedURLException e) {
        		e.printStackTrace();
        	}
        }
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Name server shutting down ...");
    }

	@Override
	@WebMethod
	public String registerInfoServer(String url) {
		String boundaries = null;
		switch(infoServers.size()) {
		case 0: 
			boundaries = wholeMap.getTopLeft().toString() + ":" +
						wholeMap.getTopRight().toString() + ":" + 
						wholeMap.getTopLeft().midPoint(wholeMap.getBottomLeft()) + ":" +
						wholeMap.getTopRight().midPoint(wholeMap.getBottomRight());
			break;
		case 1:
			boundaries = wholeMap.getTopLeft().midPoint(wholeMap.getBottomLeft()) + ":" +
						wholeMap.getTopRight().midPoint(wholeMap.getBottomRight()) + ":" +
						wholeMap.getBottomLeft() + ":" +
						wholeMap.getBottomRight();
			break;
		}
				
		infoServers.put(url, new MapChunk(boundaries));
		
		return boundaries;
	}

	@Override
	@WebMethod
	public String getInfoServer(Position position) {
		
		String url = "";
		
		for(Entry<String, MapChunk> singleInfoServer: infoServers.entrySet()) {
			
			MapChunk mapChunk = singleInfoServer.getValue();
			
			if(mapChunk.isWithin(position)) {
				url = singleInfoServer.getKey();
				break;
			}
		}
		
		return url;
	}
}