package org.dhbw.mosbach.ai.v2.provider;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.v2.api.IV2;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Map;
import java.util.Vector;

@Component(name = "v2", service = IV2.class)
public class V2Impl implements IV2 {

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("V2 booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("V2 shutting down ...");
    }

    @Override
    public void getPosition(Position position, Vector<Long> direction, double speed) {

    }
}
