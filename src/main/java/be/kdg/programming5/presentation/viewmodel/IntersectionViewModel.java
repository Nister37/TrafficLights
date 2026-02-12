package be.kdg.programming5.presentation.viewmodel;

import be.kdg.programming5.enums.IntersectionTypes;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * ViewModel for Intersection form.
 * Represents the data that comes from the add-intersection form.
 */
public class IntersectionViewModel {
    private Integer id;

    @NotNull(message = "{intersection.latitude.required}")
    @DecimalMin(value = "-90.0", message = "{intersection.latitude.range}")
    @DecimalMax(value = "90.0", message = "{intersection.latitude.range}")
    private Double latitude;

    @NotNull(message = "{intersection.longitude.required}")
    @DecimalMin(value = "-180.0", message = "{intersection.longitude.range}")
    @DecimalMax(value = "180.0", message = "{intersection.longitude.range}")
    private Double longitude;

    @NotNull(message = "{intersection.type.required}")
    private IntersectionTypes type;

    @NotNull(message = "{intersection.roadCount.required}")
    @Min(value = 2, message = "{intersection.roadCount.min}")
    @Max(value = 8, message = "{intersection.roadCount.max}")
    private Integer roadCount;

    private boolean smartEnabled;

    @NotNull(message = "{intersection.openedOn.required}")
    @PastOrPresent(message = "{intersection.openedOn.pastOrPresent}")
    private LocalDate openedOn;

    private boolean hasPedestrianCrossing;
    private String intersectionImage;

    // Default constructor required by Spring
    public IntersectionViewModel() {
    }

    public IntersectionViewModel(Integer id, Double latitude, Double longitude, IntersectionTypes type,
                                  Integer roadCount, boolean smartEnabled, LocalDate openedOn,
                                  boolean hasPedestrianCrossing, String intersectionImage) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.roadCount = roadCount;
        this.smartEnabled = smartEnabled;
        this.openedOn = openedOn;
        this.hasPedestrianCrossing = hasPedestrianCrossing;
        this.intersectionImage = intersectionImage;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public IntersectionTypes getType() {
        return type;
    }

    public void setType(IntersectionTypes type) {
        this.type = type;
    }

    public Integer getRoadCount() {
        return roadCount;
    }

    public void setRoadCount(Integer roadCount) {
        this.roadCount = roadCount;
    }

    public boolean isSmartEnabled() {
        return smartEnabled;
    }

    public void setSmartEnabled(boolean smartEnabled) {
        this.smartEnabled = smartEnabled;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public void setOpenedOn(LocalDate openedOn) {
        this.openedOn = openedOn;
    }

    public boolean isHasPedestrianCrossing() {
        return hasPedestrianCrossing;
    }

    public void setHasPedestrianCrossing(boolean hasPedestrianCrossing) {
        this.hasPedestrianCrossing = hasPedestrianCrossing;
    }

    public String getIntersectionImage() {
        return intersectionImage;
    }

    public void setIntersectionImage(String intersectionImage) {
        this.intersectionImage = intersectionImage;
    }

    @Override
    public String toString() {
        return "IntersectionViewModel{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", type=" + type +
                ", roadCount=" + roadCount +
                ", smartEnabled=" + smartEnabled +
                ", openedOn=" + openedOn +
                ", hasPedestrianCrossing=" + hasPedestrianCrossing +
                ", intersectionImage='" + intersectionImage + '\'' +
                '}';
    }
}

