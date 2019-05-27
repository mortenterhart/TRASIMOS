package org.dhbw.mosbach.ai.webserver.provider;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.base.V2Info;
import org.dhbw.mosbach.ai.base.Radio.BroadcastConsumer;
import org.dhbw.mosbach.ai.base.Radio.Configuration;
import org.dhbw.mosbach.ai.radio.api.RadioSOAP;
import org.dhbw.mosbach.ai.v2.factory.IV2Factory;
import org.dhbw.mosbach.ai.webserver.api.IWebserver;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(name = "webserver", service = IWebserver.class, immediate = true)
@WebService(endpointInterface = "org.dhbw.mosbach.ai.webserver.api.IWebserver")
@Path("/webserver")
public class WebserverImpl implements IWebserver {

    private Map<Long, Position> v2Positions = Collections.synchronizedMap(new HashMap<>());
    private IV2Factory v2Factory;
    private HttpService httpService;
    private String servletPath = "/v2map";

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Webserver booting ...");

        //registerServiceAtRadio();

        startService();
        startServlet();
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Webserver shutting down ...");
        
        httpService.unregister(servletPath);
    }

    @Reference(unbind = "unbindHttpService")
    public void bindHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    public void unbindHttpService(HttpService httpService) {
        this.httpService = null;
    }

    @Override
    @WebMethod
    public void receivePosition(V2Info v2Info) {
        System.out.printf("Received position (%f, %f) from V2 %d%n", v2Info.position.latitude,
                v2Info.position.longitude, v2Info.V2id);
        v2Positions.put(v2Info.V2id, v2Info.position);
    }

    @WebMethod
    public void registerV2Factory(String soapURL) throws MalformedURLException {
        URL wsdlURL = new URL(soapURL + "?wsdl");
        QName qname = new QName("http://provider.webserver.ai.mosbach.dhbw.org/", "WebserverImplService");
        Service reference = Service.create(wsdlURL, qname);
        this.v2Factory = reference.getPort(IV2Factory.class);
    }

    @GET
    @Path("/getV2Positions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Position> getV2Positions() {
        return new ArrayList<>(v2Positions.values());
    }

    @POST
    @Path("/createV2s")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createV2Cars(@FormParam("count") int count) {
        this.v2Factory.createV2Cars(count);
    }

    private void startService() {
        try {
            String webserverAddress = "http://0.0.0.0:9005/webserverService";
            Endpoint.publish(webserverAddress, this);
        } catch (WebServiceException exc) {
            exc.printStackTrace();
        }
    }

    private void startServlet() {
        try {
            httpService.registerServlet(servletPath, new MapServlet(), null, null);
            servletPath = "/map";
            httpService.registerResources(servletPath, "WEB-INF", null);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    public void registerServiceAtRadio() {
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
                // Register webserverService
                String webserviceURL = "http://" + localIp + ":9005/webserverService";

                radioSOAP.registerServiceAccess(Configuration.Webserver_ContentType, webserviceURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
