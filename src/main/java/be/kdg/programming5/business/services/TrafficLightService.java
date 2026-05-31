package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

import java.time.LocalDate;
import java.util.List;

public interface TrafficLightService {
    List<TrafficLight> getAllTrafficLights();
    TrafficLight getTrafficLightById(int id);
    List<TrafficLight> getTrafficLightsByIntersectionId(int intersectionId);
    void addTrafficLight(TrafficLight trafficLight);
    void addTrafficLightWithIntersection(TrafficLight trafficLight, int intersectionId);
    void deleteTrafficLight(int id);

    /**
     * Creates a new traffic light and returns the saved entity.
     * The service is responsible for creating the domain entity.
     */
    TrafficLight createTrafficLight(TrafficLightStatus status, LocalDate installationDate,
                                    Direction direction, TrafficLightType type,
                                    boolean rightArrow, int intersectionId);

    /**
     * Creates a traffic light without assigning an owner.
     * Used only by the public endpoint for the standalone client repo application.
     */
    TrafficLight createPublicTrafficLight(TrafficLightStatus status, LocalDate installationDate,
                                          Direction direction, TrafficLightType type,
                                          boolean rightArrow, int intersectionId);

    /**
     * Partially updates a traffic light (merge patch).
     * Only non-null fields are applied.
     */
    TrafficLight updateTrafficLight(int id, TrafficLightStatus status, Direction direction,
                                    TrafficLightType type, Boolean rightArrow);

    /**
     * Find traffic lights by status.
     */
    List<TrafficLight> getTrafficLightsByStatus(TrafficLightStatus status);

    /**
     * Find traffic lights installed after a specific date.
     */
    List<TrafficLight> getTrafficLightsInstalledAfter(LocalDate date);

    /**
     * Find old traffic lights by status (installed before a specific date).
     */
    List<TrafficLight> getOldTrafficLightsByStatus(TrafficLightStatus status, LocalDate beforeDate);

    /**
     * Retrieves a traffic light by ID with maintenance logs eagerly loaded.
     */
    TrafficLight getTrafficLightByIdWithMaintenanceLogs(int id);
}
