package org.dhbw.mosbach.ai.webserver.provider;

import java.util.Map;

import org.dhbw.mosbach.ai.webserver.api.IWebserver;
import org.dhbw.mosbach.ai.webserver.api.model.Position;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(name = "webserver", service = IWebserver.class)
public class WebserverImpl implements IWebserver {

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Webserver booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Webserver shutting down ...");
    }

    @Override
    public void receivePosition(long ID, Position position) {

    }
}
