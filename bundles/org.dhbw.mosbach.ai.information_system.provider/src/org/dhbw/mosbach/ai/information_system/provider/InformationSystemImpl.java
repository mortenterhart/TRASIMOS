package org.dhbw.mosbach.ai.information_system.provider;

import org.dhbw.mosbach.ai.base.Position;
import org.dhbw.mosbach.ai.information_system.api.IInformationSystem;
import org.dhbw.mosbach.ai.information_system.api.IPublishPosition;
import org.osgi.annotation.versioning.Version;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Map;

@Component(name = "information-system", service = { IInformationSystem.class, IPublishPosition.class })
public class InformationSystemImpl implements IInformationSystem, IPublishPosition {

    @Activate
    public void activate(ComponentContext context, BundleContext bundleContext, Map<String, ?> properties) {
        System.out.println("Information system booting ...");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Information system shutting down ...");
    }

    @Override
    public void receivePosition(long v2Id, Position position) {

    }

    @Override
    public void getNeighbours(long v2Id, double speed) {

    }

    @Override
    public void overtakeInformationService(Position position00, Position position10, Position position01,
                                           Position position11) {

    }

    @Override
    public void receiveFinished(long v2Id) {

    }

    @Override
    public void publish(Position position) {

    }
}
