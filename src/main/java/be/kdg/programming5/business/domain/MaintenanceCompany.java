package be.kdg.programming5.business.domain;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_company")
public class MaintenanceCompany {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceCompany.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDate since;

    @ManyToMany(mappedBy = "maintenanceCompanies", fetch = FetchType.LAZY)
    private List<MaintenanceLog> maintenanceLogs;

    protected MaintenanceCompany() {
        // Required by JPA
    }

    /**
     * Constructor for creating a new maintenance company with auto-generated ID.
     */
    public MaintenanceCompany(String name, String contactPhone, String contactEmail, boolean active, LocalDate since) {
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.active = active;
        this.since = since;
        this.maintenanceLogs = new ArrayList<>();
        logger.debug("Created new MaintenanceCompany (ID will be auto-generated), name: {}", name);
    }

    /**
     * Constructor with explicit ID - used for testing and data initialization.
     * @deprecated Use constructor without ID for new entities; ID is auto-generated.
     */
    public MaintenanceCompany(int id, String name, String contactPhone, String contactEmail, boolean active, LocalDate since) {
        this.id = id;
        this.name = name;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.active = active;
        this.since = since;
        this.maintenanceLogs = new ArrayList<>();
        logger.debug("Created new MaintenanceCompany with id: {}, name: {}", id, name);
    }

    public void addMaintenanceLog(MaintenanceLog log) {
        logger.debug("Adding maintenance log to company id: {}", this.id);
        if (!maintenanceLogs.contains(log)) {
            maintenanceLogs.add(log);
            log.addMaintenanceCompany(this);
            logger.debug("Maintenance log added successfully to company id: {}", this.id);
        } else {
            logger.debug("Maintenance log already exists in company id: {}", this.id);
        }
    }

    public List<MaintenanceLog> getMaintenanceLogs() {
        return maintenanceLogs;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDate getSince() {
        return since;
    }

    @Override
    public String toString() {
        return "MaintenanceCompany{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", active=" + active +
                ", since=" + since +
                '}';
    }
}
