package org.dhbw.mosbach.ai.webserver.provider;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.webserver.api.IWebserver;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Map;

@Component(name = "webserver", service = IWebserver.class, immediate = true)
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
