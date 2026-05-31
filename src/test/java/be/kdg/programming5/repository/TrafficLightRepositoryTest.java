package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.*;
import be.kdg.programming5.enums.*;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository layer tests for TrafficLight and related entities.
 * Uses @SpringBootTest with the isolated PostgreSQL database via the test profile.
 * Data is seeded in @BeforeEach and cleaned up in @AfterEach.
 */
@SpringBootTest
@ActiveProfiles("test")
class TrafficLightRepositoryTest {

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private MaintenanceCompanyRepository maintenanceCompanyRepository;

    @Autowired
    private MaintenanceLogCompanyRepository maintenanceLogCompanyRepository;

    private Intersection seededIntersection;
    private TrafficLight seededTrafficLight;
    private MaintenanceLog seededMaintenanceLog;
    private MaintenanceCompany seededMaintenanceCompany;
    private MaintenanceLogCompany seededMaintenanceLogCompany;

    @BeforeEach
    void setUp() {
        // Seed a complete entity graph:
        // Intersection -> TrafficLight -> MaintenanceLog -> MaintenanceLogCompany <- MaintenanceCompany
        seededIntersection = intersectionRepository.save(new Intersection(
                50.0, 4.0, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        ));

        seededTrafficLight = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        seededTrafficLight.setIntersection(seededIntersection);
        seededTrafficLight = trafficLightRepository.save(seededTrafficLight);

        seededMaintenanceLog = new MaintenanceLog(
                LocalDate.of(2023, 1, 15), "Test LED replacement",
                MaintenanceLogTypes.ELECTRICAL, 150.0, true, "INV-TEST-001"
        );
        seededMaintenanceLog.setTrafficLight(seededTrafficLight);
        seededMaintenanceLog = maintenanceLogRepository.save(seededMaintenanceLog);

        seededMaintenanceCompany = maintenanceCompanyRepository.save(new MaintenanceCompany(
                "Test Maintenance Co.", "+32 123 456", "test@maintenance.be",
                true, LocalDate.of(2015, 6, 1)
        ));

        seededMaintenanceLogCompany = maintenanceLogCompanyRepository.save(
                new MaintenanceLogCompany(seededMaintenanceLog, seededMaintenanceCompany)
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up in correct FK order (children first)
        maintenanceLogCompanyRepository.deleteAllInBatch();
        maintenanceLogRepository.deleteAllInBatch();
        trafficLightRepository.deleteAllInBatch();
        intersectionRepository.deleteAllInBatch();
        maintenanceCompanyRepository.deleteAllInBatch();
    }

    // ==============================
    // Delete cascade behavior tests
    // ==============================

    @Test
    void deletingTrafficLightWithMaintenanceLogsShouldFailDueToForeignKey() {
        // Arrange — seeded TrafficLight has a MaintenanceLog referencing it

        // Act & Assert — FK constraint prevents deletion without removing children first
        assertThrows(DataIntegrityViolationException.class, () ->
                trafficLightRepository.deleteById(seededTrafficLight.getId()),
                "Deleting a traffic light should fail when maintenance logs still reference it");
    }

    @Test
    void deletingTrafficLightAfterRemovingAssociatedRecordsShouldSucceed() {
        // Arrange
        int trafficLightId = seededTrafficLight.getId();

        // Act — remove children in correct order (same strategy as the service layer)
        maintenanceLogCompanyRepository.deleteById(seededMaintenanceLogCompany.getId());
        maintenanceLogRepository.deleteById(seededMaintenanceLog.getId());
        trafficLightRepository.deleteById(trafficLightId);

        // Assert
        assertTrue(trafficLightRepository.findById(trafficLightId).isEmpty(),
                "Traffic light should be deleted after all associated records are removed first");
    }

    // =================================================
    // Hibernate mapping — nullability constraint test
    // =================================================

    @Test
    void savingTrafficLightWithNullStatusShouldFailWithConstraintViolation() {
        // Arrange — status is @Column(nullable = false)
        TrafficLight invalidTrafficLight = new TrafficLight(
                null, LocalDate.of(2021, 1, 1),
                Direction.N, TrafficLightType.COLLISION, false
        );

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () ->
                trafficLightRepository.save(invalidTrafficLight),
                "Persisting a traffic light with null status should violate the NOT NULL constraint");
    }

    // ===========================
    // Eager / Lazy loading tests
    // ===========================

    @Test
    void findByIdShouldNotEagerlyLoadMaintenanceLogs() {
        // Act
        TrafficLight fetched = trafficLightRepository
                .findById(seededTrafficLight.getId()).orElseThrow();

        // Assert — @OneToMany(fetch = FetchType.LAZY) should NOT be initialized
        assertFalse(Hibernate.isInitialized(fetched.getMaintenanceLogs()),
                "maintenanceLogs should NOT be initialized when fetched via findById (lazy loading)");
    }

    @Test
    void findByIdWithMaintenanceLogsShouldEagerlyLoadMaintenanceLogs() {
        // Act
        TrafficLight fetched = trafficLightRepository
                .findByIdWithMaintenanceLogs(seededTrafficLight.getId()).orElseThrow();

        // Assert — JOIN FETCH should initialize the collection
        assertTrue(Hibernate.isInitialized(fetched.getMaintenanceLogs()),
                "maintenanceLogs SHOULD be initialized when fetched via findByIdWithMaintenanceLogs (JOIN FETCH)");
        assertEquals(1, fetched.getMaintenanceLogs().size(),
                "Should have exactly 1 maintenance log from seed data");
    }
}
