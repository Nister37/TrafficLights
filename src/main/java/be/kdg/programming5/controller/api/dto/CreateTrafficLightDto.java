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
public class CreateTrafficLightDto {

    @NotNull(message = "Status is required")
    private TrafficLightStatus status;

    @NotNull(message = "Installation date is required")
    @PastOrPresent(message = "Installation date cannot be in the future")
    private LocalDate installationDate;

    @NotNull(message = "Direction is required")
    private Direction direction;

    @NotNull(message = "Type is required")
    private TrafficLightType type;

    private boolean rightArrow;

    @NotNull(message = "Intersection ID is required")
    @Positive(message = "Intersection ID must be a positive number")
    private Integer intersectionId;

    public CreateTrafficLightDto() {
    }

    public CreateTrafficLightDto(TrafficLightStatus status, LocalDate installationDate,
                                  Direction direction, TrafficLightType type,
                                  boolean rightArrow, Integer intersectionId) {
        this.status = status;
        this.installationDate = installationDate;
        this.direction = direction;
        this.type = type;
        this.rightArrow = rightArrow;
        this.intersectionId = intersectionId;
    }

    public TrafficLightStatus getStatus() {
        return status;
    }

    public void setStatus(TrafficLightStatus status) {
        this.status = status;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public TrafficLightType getType() {
        return type;
    }

    public void setType(TrafficLightType type) {
        this.type = type;
    }

    public boolean isRightArrow() {
        return rightArrow;
    }

    public void setRightArrow(boolean rightArrow) {
        this.rightArrow = rightArrow;
    }

    public Integer getIntersectionId() {
        return intersectionId;
    }

    public void setIntersectionId(Integer intersectionId) {
        this.intersectionId = intersectionId;
    }
}

