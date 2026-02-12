package be.kdg.programming5.business.domain;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Association entity for the many-to-many relationship between MaintenanceLog and MaintenanceCompany.
 * Implements Scenario 1 from the course guidelines - association class with unique constraint.
 * This approach provides clear ownership and predictable JPA behavior compared to @ManyToMany.
 */
@Entity
@Table(name = "maintenance_log_company",
       uniqueConstraints = @UniqueConstraint(columnNames = {"maintenance_log_id", "maintenance_company_id"}))
public class MaintenanceLogCompany {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLogCompany.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_log_id")
    private MaintenanceLog maintenanceLog;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_company_id")
    private MaintenanceCompany maintenanceCompany;

    /**
     * Optional attribute to store when the company was assigned to the maintenance log.
     * Demonstrates that association classes can hold additional data about the relationship.
     */
    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    protected MaintenanceLogCompany() {
        // Required by JPA
    }

    /**
     * Creates a new association between a maintenance log and a maintenance company.
     *
     * @param maintenanceLog    the maintenance log
     * @param maintenanceCompany the maintenance company
     */
    public MaintenanceLogCompany(MaintenanceLog maintenanceLog, MaintenanceCompany maintenanceCompany) {
        this.maintenanceLog = maintenanceLog;
        this.maintenanceCompany = maintenanceCompany;
        this.assignedDate = LocalDate.now();
        logger.debug("Created MaintenanceLogCompany association: log={}, company={}",
                maintenanceLog.getId(), maintenanceCompany.getId());
    }

    public Integer getId() {
        return id;
    }

    public MaintenanceLog getMaintenanceLog() {
        return maintenanceLog;
    }

    public MaintenanceCompany getMaintenanceCompany() {
        return maintenanceCompany;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    @Override
    public String toString() {
        return "MaintenanceLogCompany{" +
                "id=" + id +
                ", maintenanceLogId=" + (maintenanceLog != null ? maintenanceLog.getId() : null) +
                ", maintenanceCompanyId=" + (maintenanceCompany != null ? maintenanceCompany.getId() : null) +
                ", assignedDate=" + assignedDate +
                '}';
    }
}




