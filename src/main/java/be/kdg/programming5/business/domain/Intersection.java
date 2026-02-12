package be.kdg.programming5.business.domain;

import be.kdg.programming5.enums.IntersectionTypes;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intersection")
public class Intersection {
    private static final Logger logger = LoggerFactory.getLogger(Intersection.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IntersectionTypes type;

    @Column(name = "road_count", nullable = false)
    private int roadCount;

    @Column(name = "is_smart_enabled", nullable = false)
    private boolean isSmartEnabled;

    @Column(name = "opened_on", nullable = false)
    private LocalDate openedOn;

    @Column(name = "has_pedestrian_crossing", nullable = false)
    private boolean hasPedestrianCrossing;

    @Column(name = "intersection_image")
    private String intersectionImage;

    @OneToMany(mappedBy = "intersection", fetch = FetchType.LAZY)
    private List<TrafficLight> trafficLights;

    protected Intersection() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new intersection with auto-generated ID.
     */
    public Intersection(double latitude, double longitude, IntersectionTypes type, int roadCount, boolean isSmartEnabled, LocalDate openedOn, boolean hasPedestrianCrossing, String intersectionImage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.roadCount = roadCount;
        this.isSmartEnabled = isSmartEnabled;
        this.openedOn = openedOn;
        this.hasPedestrianCrossing = hasPedestrianCrossing;
        this.intersectionImage = intersectionImage;
        this.trafficLights = new ArrayList<>();
        logger.debug("Created new Intersection (ID will be auto-generated), type: {}", type);
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public Intersection(int id, double latitude, double longitude, IntersectionTypes type, int roadCount, boolean isSmartEnabled, LocalDate openedOn, boolean hasPedestrianCrossing, String intersectionImage) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.roadCount = roadCount;
        this.isSmartEnabled = isSmartEnabled;
        this.openedOn = openedOn;
        this.hasPedestrianCrossing = hasPedestrianCrossing;
        this.intersectionImage = intersectionImage;
        this.trafficLights = new ArrayList<>();
        logger.debug("Created new Intersection with id: {}, type: {}", id, type);
    }

    public void addTrafficLight(TrafficLight trafficLight) {
        logger.debug("Adding traffic light to intersection id: {}", this.id);
        if (!this.trafficLights.contains(trafficLight)) {
            this.trafficLights.add(trafficLight);
            trafficLight.setIntersection(this);
            logger.debug("Traffic light added successfully to intersection id: {}", this.id);
        } else {
            logger.debug("Traffic light already exists in intersection id: {}", this.id);
        }
    }

    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }

    public Integer getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public IntersectionTypes getType() {
        return type;
    }

    public int getRoadCount() {
        return roadCount;
    }

    public boolean isSmartEnabled() {
        return isSmartEnabled;
    }

    public LocalDate getOpenedOn() {
        return openedOn;
    }

    public boolean isHasPedestrianCrossing() {
        return hasPedestrianCrossing;
    }

    public String getIntersectionImage() {
        return intersectionImage;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", type=" + type +
                ", roadCount=" + roadCount +
                ", isSmartEnabled=" + isSmartEnabled +
                ", openedOn=" + openedOn +
                ", hasPedestrianCrossing=" + hasPedestrianCrossing +
                ", intersectionImage='" + intersectionImage + '\'' +
                '}';
    }
}
