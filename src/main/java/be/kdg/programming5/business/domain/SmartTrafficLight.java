package be.kdg.programming5.business.domain;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Smart traffic light with sensors and connectivity features.
 * Extends TrafficLight using SINGLE_TABLE inheritance (DTYPE = SmartTrafficLight).
 */
@Entity
@DiscriminatorValue("SmartTrafficLight")
public class SmartTrafficLight extends TrafficLight {
    private static final Logger logger = LoggerFactory.getLogger(SmartTrafficLight.class);

    @Column(name = "sensor_type")
    private String sensorType;

    @Column(name = "has_connectivity")
    private boolean hasConnectivity;

    protected SmartTrafficLight() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new smart traffic light with auto-generated ID.
     */
    public SmartTrafficLight(TrafficLightStatus status, LocalDate installationDate,
                             Direction direction, TrafficLightType type, boolean rightArrow,
                             String sensorType, boolean hasConnectivity) {
        super(status, installationDate, direction, type, rightArrow);
        this.sensorType = sensorType;
        this.hasConnectivity = hasConnectivity;
        logger.debug("Created new SmartTrafficLight (ID auto-generated), sensorType: {}, connectivity: {}",
                sensorType, hasConnectivity);
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public SmartTrafficLight(int id, TrafficLightStatus status, LocalDate installationDate,
                             Direction direction, TrafficLightType type, boolean rightArrow,
                             String sensorType, boolean hasConnectivity) {
        super(id, status, installationDate, direction, type, rightArrow);
        this.sensorType = sensorType;
        this.hasConnectivity = hasConnectivity;
        logger.debug("Created new SmartTrafficLight with id: {}, sensorType: {}, connectivity: {}",
                id, sensorType, hasConnectivity);
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public boolean isHasConnectivity() {
        return hasConnectivity;
    }

    public void setHasConnectivity(boolean hasConnectivity) {
        this.hasConnectivity = hasConnectivity;
    }

    @Override
    public String toString() {
        return "SmartTrafficLight{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", installationDate=" + getInstallationDate() +
                ", direction=" + getDirection() +
                ", type=" + getType() +
                ", rightArrow=" + isRightArrow() +
                ", sensorType='" + sensorType + '\'' +
                ", hasConnectivity=" + hasConnectivity +
                '}';
    }
}
