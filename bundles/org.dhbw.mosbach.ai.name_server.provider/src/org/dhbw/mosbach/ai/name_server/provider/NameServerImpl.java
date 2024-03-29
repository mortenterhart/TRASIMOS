package org.dhbw.mosbach.ai.name_server.provider;

import org.dhbw.mosbach.ai.base.MapChunk;
import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.radio.Configuration;
import org.dhbw.mosbach.ai.name_server.api.INameServer;
import org.dhbw.mosbach.ai.radio.api.RadioSOAP;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

@WebService(endpointInterface = "org.dhbw.mosbach.ai.name_server.api.INameServer")
@Component(name = "name-server", service = INameServer.class, immediate = true)
public class NameServerImpl implements INameServer {

    private Map<String, MapChunk> infoServers = Collections.synchronizedMap(new HashMap<>());
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

        BroadcastConsumer radioListener = new BroadcastConsumer(Configuration.Radio_multiCastAddress, Configuration.Radio_multiCastPort);
        Thread radioListenerThread = new Thread(radioListener);
        radioListenerThread.start();

        while (!radioListener.isServiceFound()) {
        }

        if (radioListener.getServiceURLs().size() > 0) {
            String radioRegiURl = radioListener.getServiceURLs().get(0);
            RadioSOAP radioSOAP = null;
            try {
                radioSOAP = new RadioSOAP(radioRegiURl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            try {
                String localIp = Inet4Address.getLocalHost().getHostAddress();
                //Register nameService
                String nameserviceURL = "http://" + localIp + ":9001/NameServer";

                Objects.requireNonNull(radioSOAP).registerServiceAccess(Configuration.NameService_ContentType, nameserviceURL);
            } catch (Exception e) {
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
    public synchronized String registerInfoServer(String url) {
        String boundaries = null;
        switch (infoServers.size()) {
            case 0:
                boundaries = wholeMap.getTopLeft().toString() + ":" +
                        wholeMap.getTopRight().toString() + ":" +
                        wholeMap.getTopLeft().midPoint(wholeMap.getBottomLeft()) + ":" +
                        wholeMap.getTopRight().midPoint(wholeMap.getBottomRight());
                break;
            default:
                boundaries = wholeMap.getTopLeft().midPoint(wholeMap.getBottomLeft()) + ":" +
                        wholeMap.getTopRight().midPoint(wholeMap.getBottomRight()) + ":" +
                        wholeMap.getBottomLeft() + ":" +
                        wholeMap.getBottomRight();
                break;

        }

        System.out.println("Regisered Info: " + url + " bounds: " + boundaries);
        if (infoServers.size() < 2) {
            infoServers.put(url, new MapChunk(boundaries));
        }

        return boundaries;
    }

    @Override
    @WebMethod
    public String getInfoServer(Position position) {
        String url = "";
        for (Entry<String, MapChunk> singleInfoServer : infoServers.entrySet()) {
            MapChunk mapChunk = singleInfoServer.getValue();
            if (mapChunk.isWithin(position)) {
                url = singleInfoServer.getKey();
                break;
            }
        }

        if (url.isEmpty()) {
            return "OutOfMap";
        }

        return url;
    }
}
