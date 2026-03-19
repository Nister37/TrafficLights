package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.exception.ForbiddenOperationException;
import be.kdg.programming5.exception.TrafficLightNotFoundException;
import be.kdg.programming5.repository.TrafficLightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing traffic lights.
 * Handles business logic and validation for traffic light operations.
 */
@Service
public class TrafficLightServiceImpl implements TrafficLightService {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightServiceImpl.class);

    private final TrafficLightRepository trafficLightRepository;
    private final IntersectionService intersectionService;
    private final MaintenanceLogService maintenanceLogService;
    private final UserService userService;

    public TrafficLightServiceImpl(TrafficLightRepository trafficLightRepository,
                                   IntersectionService intersectionService,
                                   MaintenanceLogService maintenanceLogService,
                                   UserService userService) {
        this.trafficLightRepository = trafficLightRepository;
        this.intersectionService = intersectionService;
        this.maintenanceLogService = maintenanceLogService;
        this.userService = userService;
    }

    /**
     * Retrieves all traffic lights from the repository.
     *
     * @return list of all traffic lights
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getAllTrafficLights() {
        logger.debug("Fetching all traffic lights");
        return trafficLightRepository.findAll();
    }

    /**
     * Retrieves a traffic light by its ID.
     *
     * @param id the ID of the traffic light
     * @return the traffic light with the specified ID
     * @throws TrafficLightNotFoundException if no traffic light with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public TrafficLight getTrafficLightById(int id) {
        logger.debug("Fetching traffic light by id: {}", id);
        TrafficLight trafficLight = trafficLightRepository.findById(id).orElse(null);
        if (trafficLight == null) {
            throw new TrafficLightNotFoundException(id);
        }
        return trafficLight;
    }

    /**
     * Retrieves all traffic lights associated with a specific intersection.
     *
     * @param intersectionId the ID of the intersection
     * @return list of traffic lights for the specified intersection
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getTrafficLightsByIntersectionId(int intersectionId) {
        logger.debug("Fetching traffic lights for intersection: {}", intersectionId);
        return trafficLightRepository.findByIntersectionId(intersectionId);
    }

    /**
     * Creates and persists a new traffic light.
     *
     * @param trafficLight the traffic light to add
     */
    @Override
    @Transactional
    public void addTrafficLight(TrafficLight trafficLight) {
        logger.debug("Adding traffic light: {}", trafficLight.getId());
        trafficLightRepository.save(trafficLight);
    }

    /**
     * Creates and persists a new traffic light with intersection relationship.
     * This method handles the bidirectional relationship setup within a transaction
     * to avoid LazyInitializationException.
     *
     * @param trafficLight the traffic light to add
     * @param intersectionId the ID of the intersection to associate with
     */
    @Override
    @Transactional
    public void addTrafficLightWithIntersection(TrafficLight trafficLight, int intersectionId) {
        logger.debug("Adding traffic light with intersection: {}", intersectionId);
        Intersection intersection = intersectionService.getIntersectionById(intersectionId);
        if (intersection != null) {
            trafficLight.setIntersection(intersection);
        }

        ApplicationUser owner = userService.getAuthenticatedUser().orElse(null);
        trafficLight.setOwner(owner);

        trafficLightRepository.save(trafficLight);
    }

    /**
     * Creates a new traffic light and returns the saved entity.
     * The service is responsible for creating the domain entity.
     */
    @Override
    @Transactional
    public TrafficLight createTrafficLight(TrafficLightStatus status, LocalDate installationDate,
                                           Direction direction, TrafficLightType type,
                                           boolean rightArrow, int intersectionId) {
        logger.debug("Creating traffic light for intersection: {}", intersectionId);
        Intersection intersection = intersectionService.getIntersectionById(intersectionId);
        TrafficLight trafficLight = new TrafficLight(status, installationDate, direction, type, rightArrow);
        trafficLight.setIntersection(intersection);

        ApplicationUser owner = userService.getAuthenticatedUser().orElse(null);
        trafficLight.setOwner(owner);

        return trafficLightRepository.save(trafficLight);
    }

    /**
     * Partially updates a traffic light (merge patch).
     * Only non-null fields are applied. The service fetches and mutates the entity.
     */
    @Override
    @Transactional
    public TrafficLight updateTrafficLight(int id, TrafficLightStatus status, Direction direction,
                                           TrafficLightType type, Boolean rightArrow) {
        logger.debug("Updating traffic light: {}", id);
        TrafficLight trafficLight = getTrafficLightById(id);

        assertAuthenticatedUserCanModify(trafficLight);

        if (status != null) trafficLight.setStatus(status);
        if (direction != null) trafficLight.setDirection(direction);
        if (type != null) trafficLight.setType(type);
        if (rightArrow != null) trafficLight.setRightArrow(rightArrow);
        return trafficLightRepository.save(trafficLight);
    }

    /**
     * Deletes a traffic light by its ID.
     * First deletes all related maintenance logs via MaintenanceLogService.
     *
     * @param id the ID of the traffic light to delete
     */
    @Override
    @Transactional
    public void deleteTrafficLight(int id) {
        logger.debug("Deleting traffic light: {}", id);

        TrafficLight trafficLight = getTrafficLightById(id);
        assertAuthenticatedUserCanModify(trafficLight);

        maintenanceLogService.deleteByTrafficLightId(id);
        trafficLightRepository.deleteById(id);
    }

    private void assertAuthenticatedUserCanModify(TrafficLight trafficLight) {
        ApplicationUser currentUser = userService.getAuthenticatedUser()
                .orElseThrow(() -> new ForbiddenOperationException("You must be logged in to perform this action."));

        if (currentUser.getRole() == be.kdg.programming5.business.domain.UserRole.ADMIN) {
            return;
        }

        ApplicationUser owner = trafficLight.getOwner();
        if (owner == null || owner.getId() != currentUser.getId()) {
            throw new ForbiddenOperationException("Only the owner or an admin can modify this traffic light.");
        }
    }

    /**
     * Retrieves traffic lights filtered by status.
     * Uses Spring Data JPA method query.
     *
     * @param status the status to filter by
     * @return list of traffic lights with the specified status
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getTrafficLightsByStatus(TrafficLightStatus status) {
        logger.debug("Fetching traffic lights by status: {}", status);
        return trafficLightRepository.findByStatus(status);
    }

    /**
     * Retrieves traffic lights installed after a specific date.
     * Uses Spring Data JPA method query (findBy...After).
     *
     * @param date the date threshold
     * @return list of traffic lights installed after the specified date
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getTrafficLightsInstalledAfter(LocalDate date) {
        logger.debug("Fetching traffic lights installed after: {}", date);
        return trafficLightRepository.findByInstallationDateAfter(date);
    }

    /**
     * Retrieves old traffic lights by status installed before a specific date.
     * Uses custom @Query JPQL query to demonstrate advanced query capabilities.
     *
     * @param status the status to filter by
     * @param beforeDate the date threshold
     * @return list of traffic lights with specified status installed before the date
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrafficLight> getOldTrafficLightsByStatus(TrafficLightStatus status, LocalDate beforeDate) {
        logger.debug("Fetching old traffic lights - status: {}, before: {}", status, beforeDate);
        return trafficLightRepository.findOldTrafficLightsByStatus(status, beforeDate);
    }

    /**
     * Retrieves a traffic light by ID with maintenance logs eagerly loaded using JOIN FETCH.
     *
     * @param id the ID of the traffic light
     * @return the traffic light with maintenance logs loaded
     * @throws TrafficLightNotFoundException if no traffic light with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public TrafficLight getTrafficLightByIdWithMaintenanceLogs(int id) {
        logger.debug("Fetching traffic light by id with maintenance logs: {}", id);
        TrafficLight trafficLight = trafficLightRepository.findByIdWithMaintenanceLogs(id).orElse(null);
        if (trafficLight == null) {
            throw new TrafficLightNotFoundException(id);
        }
        return trafficLight;
    }
}
