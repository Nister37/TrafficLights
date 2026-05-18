package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.controller.api.dto.CreateTrafficLightDto;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public REST endpoints accessible without authentication.
 * Used by the standalone W10 client application.
 * CSRF protection is disabled for this path in SecurityConfig.
 */
@RestController
@RequestMapping("/api/public/traffic-lights")
public class PublicTrafficLightsController {

    private static final Logger logger = LoggerFactory.getLogger(PublicTrafficLightsController.class);

    private final TrafficLightService trafficLightService;
    private final TrafficLightMapper trafficLightMapper;

    public PublicTrafficLightsController(TrafficLightService trafficLightService,
                                         TrafficLightMapper trafficLightMapper) {
        this.trafficLightService = trafficLightService;
        this.trafficLightMapper = trafficLightMapper;
    }

    /**
     * GET /api/public/traffic-lights - List all traffic lights without authentication.
     * Intended for the standalone client SPA (W10).
     */
    @GetMapping
    public ResponseEntity<List<TrafficLightDto>> getAllTrafficLights() {
        List<TrafficLightDto> dtos = trafficLightService.getAllTrafficLights().stream()
                .map(trafficLightMapper::toTrafficLightDto)
                .toList();
        return dtos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(dtos);
    }

    /**
     * POST /api/public/traffic-lights - Create a new traffic light without authentication.
     * Intended for the standalone client SPA (W10).
     * Returns 201 Created with the created resource, or 400 Bad Request on validation failure.
     */
    @PostMapping
    public ResponseEntity<TrafficLightDto> createTrafficLight(
            @RequestBody @Valid CreateTrafficLightDto createDto) {
        logger.debug("Public REST: Creating traffic light for intersection: {}", createDto.intersectionId());

        TrafficLight saved = trafficLightService.createTrafficLight(
                createDto.status(),
                createDto.installationDate(),
                createDto.direction(),
                createDto.type(),
                createDto.rightArrow(),
                createDto.intersectionId()
        );

        return new ResponseEntity<>(trafficLightMapper.toTrafficLightDto(saved), HttpStatus.CREATED);
    }
}



