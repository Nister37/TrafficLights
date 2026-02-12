package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.TrafficLightStatus;

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
