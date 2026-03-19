package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * DTO for creating a new TrafficLight via POST request.
 * Contains validation constraints to ensure valid input.
 */
public record CreateTrafficLightDto(
        @NotNull(message = "Status is required") TrafficLightStatus status,
        @NotNull(message = "Installation date is required")
        @PastOrPresent(message = "Installation date cannot be in the future")
        LocalDate installationDate,
        @NotNull(message = "Direction is required") Direction direction,
        @NotNull(message = "Type is required") TrafficLightType type,
        boolean rightArrow,
        @NotNull(message = "Intersection ID is required")
        @Positive(message = "Intersection ID must be a positive number")
        Integer intersectionId
) {
}

