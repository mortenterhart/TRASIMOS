package org.dhbw.mosbach.ai.radio.provider;

import org.dhbw.mosbach.ai.radio.api.IRadio;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Map;

@Component(name = "radio", service = IRadio.class)
public class RadioImpl implements IRadio {

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Radio booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Radio shutting down ...");
    }

    @Override
    public void registerServiceAccess(String url, int radioPort) {
        // Register the specified service located under the url under the
        // specified port for other network participants to be able to find
        // the service
    }
}
