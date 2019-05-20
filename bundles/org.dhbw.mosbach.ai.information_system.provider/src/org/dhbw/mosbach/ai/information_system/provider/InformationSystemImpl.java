package org.dhbw.mosbach.ai.information_system.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.Math;

import org.dhbw.mosbach.ai.information_system.api.IInformationSystem;
import org.dhbw.mosbach.ai.information_system.api.IPublishPosition;
import org.dhbw.mosbach.ai.information_system.api.model.Position;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.ComponentContext;

@Component(name = "information-system", service = {IInformationSystem.class, IPublishPosition.class})
public class InformationSystemImpl implements IInformationSystem, IPublishPosition {
    private HashMap<Long, Position> vehiclesToObserve = new HashMap<Long, Position>();

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
        // Add/Update vehicle in Map
        // if vehicle id already exists the position of vehicle will be overridden
        vehiclesToObserve.put(v2Id, position);
    }

    /**
     * get neighbours of vehicle by its id and speed.
     * requres that vehicle is observed by this information system
     * @param v2Id id of v2 vehicle
     * @param speed speed of v2 vehicle
     * @return list of positions of neighbours
     */
    @Override
    public List<Position> getNeighbours(long v2Id, double speed) {
        // If vehicle is new, neighbours can not be resolved
        // Vehicle needs to publish its position first
        if (isVehicleKnown(v2Id)) {
            List<Position> positionOfNeighbours = new ArrayList<>();
            // stopping distance = reaction time distance + actual stopping distance
            double stoppingDistance = (speed / 10 * 3) + (speed / 10 * speed / 10);
            Position positionOfVehicle = vehiclesToObserve.get(v2Id);

            for (Position pos : vehiclesToObserve.values()) {
                if (distanceBetweenPositions(positionOfVehicle, pos) <= stoppingDistance) {
                    positionOfNeighbours.add(pos);
                }
            }

            return positionOfNeighbours;
        }
        return null;
    }

    /**
     * get neighbours of position in a specific radius
     * @param position centre position
     * @param radius radius around position
     * @return list of positions of neighbours
     */
    @Override
    public List<Position> getNeighbours(Position position, double radius){
        List<Position> positionOfNeighbours = new ArrayList<>();

        for (Position pos : vehiclesToObserve.values()) {
            if (distanceBetweenPositions(position, pos) <= radius) {
                positionOfNeighbours.add(pos);
            }
        }

        return positionOfNeighbours;
    }

    @Override
    public void overtakeInformationService(Position position00, Position position10, Position position01,
                                           Position position11) {

    }

    /**
     * remove vehicle from observing
     * @param v2Id id of v2 vehicle
     */
    @Override
    public void receiveFinished(long v2Id) {
        vehiclesToObserve.remove(v2Id);
    }

    @Override
    public void publish(Position position) {

    }

    private double distanceBetweenPositions(Position a, Position b) {
        return Math.sqrt(
                (b.getLatitude() - a.getLatitude()) * (b.getLatitude() - a.getLatitude()) +
                        (b.getLongitude() - a.getLongitude()) * (b.getLongitude() - a.getLongitude()));
    }

    private boolean isVehicleKnown(long vehicleId) {
        return vehiclesToObserve.containsKey(vehicleId);
    }
}
