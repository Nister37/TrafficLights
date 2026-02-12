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
 * Pedestrian traffic light with audio signal and button request features.
 * Extends TrafficLight using SINGLE_TABLE inheritance (DTYPE = PedestrianTrafficLight).
 */
@Entity
@DiscriminatorValue("PedestrianTrafficLight")
public class PedestrianTrafficLight extends TrafficLight {
    private static final Logger logger = LoggerFactory.getLogger(PedestrianTrafficLight.class);

    @Column(name = "has_audio_signal")
    private boolean hasAudioSignal;

    @Column(name = "has_button_request")
    private boolean hasButtonRequest;

    protected PedestrianTrafficLight() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new pedestrian traffic light with auto-generated ID.
     */
    public PedestrianTrafficLight(TrafficLightStatus status, LocalDate installationDate,
                                  Direction direction, TrafficLightType type, boolean rightArrow,
                                  boolean hasAudioSignal, boolean hasButtonRequest) {
        super(status, installationDate, direction, type, rightArrow);
        this.hasAudioSignal = hasAudioSignal;
        this.hasButtonRequest = hasButtonRequest;
        logger.debug("Created new PedestrianTrafficLight (ID auto-generated), audioSignal: {}, buttonRequest: {}",
                hasAudioSignal, hasButtonRequest);
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public PedestrianTrafficLight(int id, TrafficLightStatus status, LocalDate installationDate,
                                  Direction direction, TrafficLightType type, boolean rightArrow,
                                  boolean hasAudioSignal, boolean hasButtonRequest) {
        super(id, status, installationDate, direction, type, rightArrow);
        this.hasAudioSignal = hasAudioSignal;
        this.hasButtonRequest = hasButtonRequest;
        logger.debug("Created new PedestrianTrafficLight with id: {}, audioSignal: {}, buttonRequest: {}",
                id, hasAudioSignal, hasButtonRequest);
    }

    public boolean isHasAudioSignal() {
        return hasAudioSignal;
    }

    public void setHasAudioSignal(boolean hasAudioSignal) {
        this.hasAudioSignal = hasAudioSignal;
    }

    public boolean isHasButtonRequest() {
        return hasButtonRequest;
    }

    public void setHasButtonRequest(boolean hasButtonRequest) {
        this.hasButtonRequest = hasButtonRequest;
    }

    @Override
    public String toString() {
        return "PedestrianTrafficLight{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", installationDate=" + getInstallationDate() +
                ", direction=" + getDirection() +
                ", type=" + getType() +
                ", rightArrow=" + isRightArrow() +
                ", hasAudioSignal=" + hasAudioSignal +
                ", hasButtonRequest=" + hasButtonRequest +
                '}';
    }
}

