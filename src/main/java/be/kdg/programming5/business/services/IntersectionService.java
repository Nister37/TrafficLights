package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;

import java.util.List;

public interface IntersectionService {
    List<Intersection> getAllIntersections();
    List<TrafficLight> getTrafficLightsByIntersectionId(int intersectionId);
    Intersection getIntersectionById(int id);

    /**
     * Retrieves an intersection by ID with traffic lights eagerly loaded.
     */
    Intersection getIntersectionByIdWithTrafficLights(int id);

    void addIntersection(Intersection intersection);
    void deleteIntersection(int id);
}
