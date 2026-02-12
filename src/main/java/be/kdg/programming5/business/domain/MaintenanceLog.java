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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "maintenance_log_company",
            joinColumns = @JoinColumn(name = "maintenance_log_id"),
            inverseJoinColumns = @JoinColumn(name = "maintenance_company_id")
    )
    private List<MaintenanceCompany> maintenanceCompanies;

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
        this.maintenanceCompanies = new ArrayList<>();
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
        this.maintenanceCompanies = new ArrayList<>();
        logger.debug("Created new MaintenanceLog with id: {}, kind: {}", id, kind);
    }

    public void addMaintenanceCompany(MaintenanceCompany company) {
        logger.debug("Adding maintenance company to log id: {}", this.id);
        if (!maintenanceCompanies.contains(company)) {
            maintenanceCompanies.add(company);
            company.addMaintenanceLog(this);
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

    public List<MaintenanceCompany> getMaintenanceCompanies() {
        return maintenanceCompanies;
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
