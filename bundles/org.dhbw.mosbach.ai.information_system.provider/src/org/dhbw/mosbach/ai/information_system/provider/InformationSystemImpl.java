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
    public boolean receivePosition(long v2Id, Position position) {
        // Add/Update vehicle in Map
        // if vehicle id already exists the position of vehicle will be overridden
        if(isVehicleInBoundary(position)){
            vehiclesToObserve.put(v2Id, position);
            return true;
        }
        return false;
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
            Position positionOfVehicle = vehiclesToObserve.get(v2Id);

            for (Position pos : vehiclesToObserve.values()) {
                if (distanceBetweenPositions(positionOfVehicle, pos) <= calcStoppingDistance(speed)) {
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

    private boolean isVehicleInBoundary(Position position) {
        if (position.getLongitude() > westBound && position.getLongitude() < eastBound && position.getLatitude() < northBound && position.getLatitude() > southBound){
            return true;
        }
        return false;
    }

    private boolean isVehicleNearBoundary(Position position, double speed){
        double stoppingDistance = calcStoppingDistance(speed);
        if(position.getLatitude() > (westBound + stoppingDistance) && position.getLatitude() < (eastBound - stoppingDistance) &&
                position.getLatitude() < (northbound - stoppingDistance) && position.getLatitude() > (southbound + stoppingDistance)){
            return true;
        }
        return false;
    }

    private double calcStoppingDistance(double speed){
        return (speed / 10 * 3) + (speed / 10 * speed / 10);
    }
}
