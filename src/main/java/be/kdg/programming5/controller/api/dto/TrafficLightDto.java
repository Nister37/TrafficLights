package be.kdg.programming5.controller.api.dto;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;

import java.time.LocalDate;

/**
 * DTO for TrafficLight entity - used for API responses.
 */
public class TrafficLightDto {
    private Integer id;
    private TrafficLightStatus status;
    private LocalDate installationDate;
    private Direction direction;
    private TrafficLightType type;
    private boolean rightArrow;
    private Integer intersectionId;
    private String category;

    public TrafficLightDto() {
    }

    public TrafficLightDto(Integer id, TrafficLightStatus status, LocalDate installationDate,
                           Direction direction, TrafficLightType type, boolean rightArrow,
                           Integer intersectionId, String category) {
        this.id = id;
        this.status = status;
        this.installationDate = installationDate;
        this.direction = direction;
        this.type = type;
        this.rightArrow = rightArrow;
        this.intersectionId = intersectionId;
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

