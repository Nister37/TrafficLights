package be.kdg.programming5.business.domain;

import be.kdg.programming5.enums.MaintenanceLogTypes;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_log")
public class MaintenanceLog {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLog.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceLogTypes kind;

    @Column(nullable = false)
    private double cost;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "invoice_number", nullable = false, length = 100)
    private String invoiceNumber;

    /**
     * Association entity relationship - replaces @ManyToMany per course guidelines (Scenario 1).
     */
    @OneToMany(mappedBy = "maintenanceLog", fetch = FetchType.LAZY)
    private List<MaintenanceLogCompany> maintenanceLogCompanies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traffic_light_id")
    private TrafficLight trafficLight;

    protected MaintenanceLog() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new maintenance log with auto-generated ID.
     */
    public MaintenanceLog(LocalDate date, String description, MaintenanceLogTypes kind, double cost, boolean completed, String invoiceNumber) {
        this.date = date;
        this.description = description;
        this.kind = kind;
        this.cost = cost;
        this.completed = completed;
        this.invoiceNumber = invoiceNumber;
        this.maintenanceLogCompanies = new ArrayList<>();
        logger.debug("Created new MaintenanceLog (ID will be auto-generated), kind: {}", kind);
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public MaintenanceLog(int id, LocalDate date, String description, MaintenanceLogTypes kind, double cost, boolean completed, String invoiceNumber) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.kind = kind;
        this.cost = cost;
        this.completed = completed;
        this.invoiceNumber = invoiceNumber;
        this.maintenanceLogCompanies = new ArrayList<>();
        logger.debug("Created new MaintenanceLog with id: {}, kind: {}", id, kind);
    }

    /**
     * Adds a maintenance company to this log through the association entity.
     * Establishes bidirectional relationship using the new MaintenanceLogCompany entity.
     *
     * @param company the maintenance company to add
     */
    public void addMaintenanceCompany(MaintenanceCompany company) {
        logger.debug("Adding maintenance company to log id: {}", this.id);
        // Check if association already exists
        boolean exists = maintenanceLogCompanies.stream()
                .anyMatch(mlc -> mlc.getMaintenanceCompany().equals(company));
        if (!exists) {
            MaintenanceLogCompany association = new MaintenanceLogCompany(this, company);
            maintenanceLogCompanies.add(association);
            company.getMaintenanceLogCompanies().add(association);
            logger.debug("Maintenance company added successfully to log id: {}", this.id);
        } else {
            logger.debug("Maintenance company already exists in log id: {}", this.id);
        }
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        logger.debug("Setting traffic light for maintenance log id: {}", this.id);
        this.trafficLight = trafficLight;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }

    /**
     * Returns the association entities linking this log to maintenance companies.
     */
    public List<MaintenanceLogCompany> getMaintenanceLogCompanies() {
        return maintenanceLogCompanies;
    }

    /**
     * Convenience method to get the maintenance companies associated with this log.
     * Extracts companies from the association entities.
     */
    public List<MaintenanceCompany> getMaintenanceCompanies() {
        return maintenanceLogCompanies.stream()
                .map(MaintenanceLogCompany::getMaintenanceCompany)
                .toList();
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public MaintenanceLogTypes getKind() {
        return kind;
    }

    public double getCost() {
        return cost;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    @Override
    public String toString() {
        return "MaintenanceLog{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", kind=" + kind +
                ", cost=" + cost +
                ", completed=" + completed +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                '}';
    }
}
