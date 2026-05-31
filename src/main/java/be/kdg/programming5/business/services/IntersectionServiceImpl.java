package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.exception.IntersectionNotFoundException;
import be.kdg.programming5.repository.IntersectionRepository;
import be.kdg.programming5.repository.TrafficLightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing intersections.
 */
@Service
public class IntersectionServiceImpl implements IntersectionService {
    private static final Logger logger = LoggerFactory.getLogger(IntersectionServiceImpl.class);

    private final IntersectionRepository intersectionRepository;
    private final TrafficLightRepository trafficLightRepository;
    private final MaintenanceLogService maintenanceLogService;

    public IntersectionServiceImpl(IntersectionRepository intersectionRepository,
                                   TrafficLightRepository trafficLightRepository,
                                   MaintenanceLogService maintenanceLogService) {
        this.intersectionRepository = intersectionRepository;
        this.trafficLightRepository = trafficLightRepository;
        this.maintenanceLogService = maintenanceLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Intersection> getAllIntersections() {
        logger.debug("Fetching all intersections");
        return intersectionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getTrafficLightsByIntersectionId(int intersectionId) {
        logger.debug("Fetching traffic lights for intersection: {}", intersectionId);
        return trafficLightRepository.findByIntersectionId(intersectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Intersection getIntersectionById(int id) {
        logger.debug("Fetching intersection by id: {}", id);
        Intersection intersection = intersectionRepository.findById(id).orElse(null);
        if (intersection == null) {
            throw new IntersectionNotFoundException(id);
        }
        return intersection;
    }

    /**
     * Retrieves an intersection by ID with traffic lights eagerly loaded using JOIN FETCH.
     *
     * @param id the ID of the intersection
     * @return the intersection with traffic lights loaded
     * @throws IntersectionNotFoundException if no intersection with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public Intersection getIntersectionByIdWithTrafficLights(int id) {
        logger.debug("Fetching intersection by id with traffic lights: {}", id);
        Intersection intersection = intersectionRepository.findByIdWithTrafficLights(id).orElse(null);
        if (intersection == null) {
            throw new IntersectionNotFoundException(id);
        }
        return intersection;
    }

    @Override
    @Transactional
    public void addIntersection(Intersection intersection) {
        logger.debug("Adding intersection: {}", intersection.getId());
        intersectionRepository.save(intersection);
    }

    @Override
    @Transactional
    public void deleteIntersection(int id) {
        logger.debug("Deleting intersection: {}", id);
        List<TrafficLight> trafficLights = trafficLightRepository.findByIntersectionId(id);
        for (TrafficLight trafficLight : trafficLights) {
            maintenanceLogService.deleteByTrafficLightId(trafficLight.getId());
            trafficLightRepository.deleteById(trafficLight.getId());
        }
        intersectionRepository.deleteById(id);
    }
}
