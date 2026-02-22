package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for Intersection resources.
 * Provides endpoints for retrieving intersection-related data.
 */
@RestController
@RequestMapping("/api/intersections")
public class IntersectionsController {
    private static final Logger logger = LoggerFactory.getLogger(IntersectionsController.class);

    private final IntersectionService intersectionService;
    private final TrafficLightMapper trafficLightMapper;

    public IntersectionsController(IntersectionService intersectionService,
                                   TrafficLightMapper trafficLightMapper) {
        this.intersectionService = intersectionService;
        this.trafficLightMapper = trafficLightMapper;
    }

    /**
     * GET /api/intersections/{id}/traffic-lights - Retrieve all traffic lights of an intersection.
     * Returns 200 OK with list, or 204 No Content if empty.
     * Returns 404 Not Found if intersection doesn't exist.
     */
    @GetMapping("/{id}/traffic-lights")
    public ResponseEntity<List<TrafficLightDto>> getTrafficLightsForIntersection(@PathVariable("id") int intersectionId) {
        logger.debug("REST: Getting traffic lights for intersection: {}", intersectionId);

        // Verify intersection exists (throws IntersectionNotFoundException if not found)
        intersectionService.getIntersectionById(intersectionId);

        List<TrafficLight> trafficLights = intersectionService.getTrafficLightsByIntersectionId(intersectionId);

        if (trafficLights.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(trafficLightMapper.toTrafficLightDtoList(trafficLights));
    }
}

