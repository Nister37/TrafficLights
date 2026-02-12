package be.kdg.programming5.business.domain;

import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Base entity class for traffic lights.
 * Uses SINGLE_TABLE inheritance strategy - all subclasses stored in one table with DTYPE discriminator.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TrafficLight")
@Table(name = "traffic_light")
public class TrafficLight {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLight.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrafficLightStatus status;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Direction direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrafficLightType type;

    @Column(name = "right_arrow", nullable = false)
    private boolean rightArrow;

    /**
     * Bidirectional relationship with MaintenanceLogs.
     */
    @OneToMany(mappedBy = "trafficLight", fetch = FetchType.LAZY)
    private List<MaintenanceLog> maintenanceLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intersection_id")
    private Intersection intersection;

    protected TrafficLight() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new traffic light with auto-generated ID.
     */
    public TrafficLight(TrafficLightStatus status, LocalDate installationDate, Direction direction, TrafficLightType type, boolean rightArrow) {
        this.status = status;
        this.installationDate = installationDate;
        this.direction = direction;
        this.type = type;
        this.rightArrow = rightArrow;
        this.maintenanceLogs = new ArrayList<>();
        logger.debug("Created new TrafficLight (ID will be auto-generated)");
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public TrafficLight(int id, TrafficLightStatus status, LocalDate installationDate, Direction direction, TrafficLightType type, boolean rightArrow) {
        this.id = id;
        this.status = status;
        this.installationDate = installationDate;
        this.direction = direction;
        this.type = type;
        this.rightArrow = rightArrow;
        this.maintenanceLogs = new ArrayList<>();
        logger.debug("Created new TrafficLight with id: {}", id);
    }

    /**
     * Adds a maintenance log to this traffic light and establishes bidirectional relationship.
     * If the log already exists in the collection, the operation is skipped.
     *
     * @param maintenanceLog the maintenance log to add to this traffic light
     */
    public void addMaintenanceLog(MaintenanceLog maintenanceLog) {
        logger.debug("Adding maintenance log to traffic light id: {}", this.id);
        if (!this.maintenanceLogs.contains(maintenanceLog)) {
            this.maintenanceLogs.add(maintenanceLog);
            maintenanceLog.setTrafficLight(this);
            logger.debug("Maintenance log added successfully to traffic light id: {}", this.id);
        } else {
            logger.debug("Maintenance log already exists for traffic light id: {}", this.id);
        }
    }

    public List<MaintenanceLog> getMaintenanceLogs() {
        return maintenanceLogs;
    }

    public Integer getId() {
        return id;
    }

    public TrafficLightStatus getStatus() {
        return status;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public Direction getDirection() {
        return direction;
    }

    public TrafficLightType getType() {
        return type;
    }

    public boolean isRightArrow() {
        return rightArrow;
    }

    /**
     * Associates this traffic light with an intersection.
     * Used to establish the many-to-one relationship.
     *
     * @param intersection the intersection to associate with this traffic light
     */
    public void setIntersection(Intersection intersection) {
        logger.debug("Setting intersection for traffic light id: {}", this.id);
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    /**
     * Returns the category/type name of this traffic light.
     * Used for display purposes in templates since accessing class.simpleName is restricted.
     * Uses Hibernate.getClass() to handle proxy objects correctly.
     *
     * @return the simple class name (e.g., "TrafficLight", "SmartTrafficLight", "PedestrianTrafficLight")
     */
    public String getCategory() {
        return Hibernate.getClass(this).getSimpleName();
    }

    @Override
    public String toString() {
        return "TrafficLight{" +
                "id=" + id +
                ", status=" + status +
                ", installationDate=" + installationDate +
                ", direction=" + direction +
                ", type=" + type +
                ", rightArrow=" + rightArrow +
                '}';
    }
}
