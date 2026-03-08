package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.controller.api.dto.CreateTrafficLightDto;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import be.kdg.programming5.controller.api.dto.UpdateTrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for TrafficLight resources.
 * Provides endpoints for CRUD operations on traffic lights.
 */
@RestController
@RequestMapping("/api/traffic-lights")
public class TrafficLightsController {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightsController.class);

    private final TrafficLightService trafficLightService;
    private final TrafficLightMapper trafficLightMapper;

    public TrafficLightsController(TrafficLightService trafficLightService,
                                   TrafficLightMapper trafficLightMapper) {
        this.trafficLightService = trafficLightService;
        this.trafficLightMapper = trafficLightMapper;
    }

    /**
     * GET /api/traffic-lights - Retrieve all traffic lights.
     * Returns 200 OK with list, or 204 No Content if empty.
     */
    @GetMapping
    public ResponseEntity<List<TrafficLightDto>> getAllTrafficLights() {
        logger.debug("REST: Getting all traffic lights");
        List<TrafficLight> trafficLights = trafficLightService.getAllTrafficLights();

        if (trafficLights.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(trafficLightMapper.toTrafficLightDtoList(trafficLights));
    }

    /**
     * GET /api/traffic-lights/{id} - Retrieve a specific traffic light.
     * Returns 200 OK with the traffic light, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrafficLightDto> getTrafficLight(@PathVariable("id") int id) {
        logger.debug("REST: Getting traffic light with id: {}", id);
        TrafficLight trafficLight = trafficLightService.getTrafficLightById(id);
        return ResponseEntity.ok(trafficLightMapper.toTrafficLightDto(trafficLight));
    }

    /**
     * POST /api/traffic-lights - Create a new traffic light.
     * Returns 201 Created with the created traffic light, or 400 Bad Request if validation fails.
     * Returns 404 Not Found if the intersection doesn't exist.
     */
    @PostMapping
    public ResponseEntity<TrafficLightDto> createTrafficLight(
            @RequestBody @Valid CreateTrafficLightDto createDto) {
        logger.debug("REST: Creating traffic light for intersection: {}", createDto.getIntersectionId());

        TrafficLight savedTrafficLight = trafficLightService.createTrafficLight(
                createDto.getStatus(),
                createDto.getInstallationDate(),
                createDto.getDirection(),
                createDto.getType(),
                createDto.isRightArrow(),
                createDto.getIntersectionId()
        );

        return new ResponseEntity<>(
                trafficLightMapper.toTrafficLightDto(savedTrafficLight),
                HttpStatus.CREATED);
    }

    /**
     * PATCH /api/traffic-lights/{id} - Partially update a traffic light (merge patch).
     * Only provided (non-null) fields are updated.
     * Returns 204 No Content on success, 404 Not Found if not exists.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateTrafficLight(
            @PathVariable("id") int id,
            @RequestBody UpdateTrafficLightDto updateDto) {
        logger.debug("REST: Patching traffic light with id: {}", id);
        trafficLightService.updateTrafficLight(
                id,
                updateDto.getStatus(),
                updateDto.getDirection(),
                updateDto.getType(),
                updateDto.getRightArrow()
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/traffic-lights/{id} - Delete a traffic light.
     * Returns 204 No Content on success, or 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrafficLight(@PathVariable("id") int id) {
        logger.debug("REST: Deleting traffic light with id: {}", id);

        // Verify existence first (throws TrafficLightNotFoundException if not found)
        trafficLightService.getTrafficLightById(id);
        trafficLightService.deleteTrafficLight(id);

        return ResponseEntity.noContent().build();
    }
}


