package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

import java.time.LocalDate;

/**
 * Traffic light response for the intersection details AJAX view.
 * Includes the owner username so the browser can hide unauthorized actions.
 */
public record IntersectionTrafficLightDto(
        Integer id,
        TrafficLightStatus status,
        LocalDate installationDate,
        Direction direction,
        TrafficLightType type,
        boolean rightArrow,
        Integer intersectionId,
        String category,
        String ownerUsername
) {
}
