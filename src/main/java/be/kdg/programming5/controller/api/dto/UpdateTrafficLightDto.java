package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

/**
 * DTO for partially updating a TrafficLight via PATCH (merge patch).
 * All fields are optional - only provided (non-null) fields will be updated.
 * No validation annotations here: null means "don't update this field".
 */
public record UpdateTrafficLightDto(
        TrafficLightStatus status,
        Direction direction,
        TrafficLightType type,
        Boolean rightArrow
) {
}

