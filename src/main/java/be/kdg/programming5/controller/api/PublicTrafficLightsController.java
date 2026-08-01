package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public read endpoint accessible without authentication.
 * Used by the standalone W10 client application.
 */
@RestController
@RequestMapping("/api/public/traffic-lights")
public class PublicTrafficLightsController {

    private final TrafficLightService trafficLightService;
    private final TrafficLightMapper trafficLightMapper;

    public PublicTrafficLightsController(TrafficLightService trafficLightService,
                                         TrafficLightMapper trafficLightMapper) {
        this.trafficLightService = trafficLightService;
        this.trafficLightMapper = trafficLightMapper;
    }

    /**
     * Intended for the standalone client SPA.
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

}



