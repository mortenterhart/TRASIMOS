package org.dhbw.mosbach.ai.name_server.provider;

import java.util.Map;

import org.dhbw.mosbach.ai.name_server.api.INameServer;
import org.dhbw.mosbach.ai.name_server.api.model.Position;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(name = "name-server", service = INameServer.class)
public class NameServerImpl implements INameServer {

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Name Server booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Name server shutting down ...");
    }
    
    @Override
    public void getNameServer(Position position) {

    }
}
