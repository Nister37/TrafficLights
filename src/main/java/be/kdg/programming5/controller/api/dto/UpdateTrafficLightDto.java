package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

/**
 * DTO for partially updating a TrafficLight via PATCH (merge patch).
 * All fields are optional - only provided (non-null) fields will be updated.
 * No validation annotations here: null means "don't update this field".
 */
public class UpdateTrafficLightDto {

    private TrafficLightStatus status;
    private Direction direction;
    private TrafficLightType type;
    private Boolean rightArrow;

    public UpdateTrafficLightDto() {
    }

    public TrafficLightStatus getStatus() {
        return status;
    }

    public void setStatus(TrafficLightStatus status) {
        this.status = status;
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

    public Boolean getRightArrow() {
        return rightArrow;
    }

    public void setRightArrow(Boolean rightArrow) {
        this.rightArrow = rightArrow;
    }
}

