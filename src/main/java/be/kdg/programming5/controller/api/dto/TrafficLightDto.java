package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

import java.time.LocalDate;

/**
 * DTO for TrafficLight entity - used for API responses.
 */
public record TrafficLightDto(
        Integer id,
        TrafficLightStatus status,
        LocalDate installationDate,
        Direction direction,
        TrafficLightType type,
        boolean rightArrow,
        Integer intersectionId,
        String category
) {
}

