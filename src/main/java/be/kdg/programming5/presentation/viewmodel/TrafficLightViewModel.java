package be.kdg.programming5.presentation.viewmodel;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * ViewModel for TrafficLight form.
 * Represents the data that comes from the add-traffic-light form.
 */
public class TrafficLightViewModel {
    private Integer id;

    @NotNull(message = "{trafficLight.status.required}")
    private TrafficLightStatus status;

    @NotNull(message = "{trafficLight.installationDate.required}")
    @PastOrPresent(message = "{trafficLight.installationDate.pastOrPresent}")
    private LocalDate installationDate;

    @NotNull(message = "{trafficLight.direction.required}")
    private Direction direction;

    @NotNull(message = "{trafficLight.type.required}")
    private TrafficLightType type;

    private boolean rightArrow;

    @NotNull(message = "{trafficLight.intersectionId.required}")
    private Integer intersectionId;

    // Default constructor required by Spring
    public TrafficLightViewModel() {
    }

    public TrafficLightViewModel(int id, TrafficLightStatus status, LocalDate installationDate,
                                  Direction direction, TrafficLightType type, boolean rightArrow,
                                  Integer intersectionId) {
        this.id = id;
        this.status = status;
        this.installationDate = installationDate;
        this.direction = direction;
        this.type = type;
        this.rightArrow = rightArrow;
        this.intersectionId = intersectionId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public String toString() {
        return "TrafficLightViewModel{" +
                "id=" + id +
                ", status=" + status +
                ", installationDate=" + installationDate +
                ", direction=" + direction +
                ", type=" + type +
                ", rightArrow=" + rightArrow +
                ", intersectionId=" + intersectionId +
                '}';
    }
}

