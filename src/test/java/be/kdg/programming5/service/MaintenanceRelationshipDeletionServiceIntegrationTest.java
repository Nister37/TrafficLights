package be.kdg.programming5.service;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.MaintenanceCompanyService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.MaintenanceLogTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.repository.MaintenanceCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies child-first deletion for the maintenance log/company association entity.
 */
@SpringBootTest
@ActiveProfiles("test")
class MaintenanceRelationshipDeletionServiceIntegrationTest {

    @Autowired
    private MaintenanceCompanyService maintenanceCompanyService;

    @Autowired
    private MaintenanceLogService maintenanceLogService;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private MaintenanceLogCompanyRepository maintenanceLogCompanyRepository;

    @Autowired
    private MaintenanceCompanyRepository maintenanceCompanyRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    private MaintenanceCompany seededCompany;
    private MaintenanceLog seededLog;

    @BeforeEach
    void setUp() {
        Intersection intersection = testHelper.intersection(
                50.0, 4.0, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );

        TrafficLight trafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false,
                intersection
        );

        seededLog = testHelper.maintenanceLog(
                LocalDate.of(2023, 1, 15), "Test LED replacement",
                MaintenanceLogTypes.ELECTRICAL, 150.0, true, "INV-TEST-001",
                trafficLight
        );

        seededCompany = testHelper.maintenanceCompany(
                "Test Maintenance Co.", "+32 123 456", "test@maintenance.be",
                true, LocalDate.of(2015, 6, 1)
        );

        testHelper.maintenanceLogCompany(seededLog, seededCompany);
    }

    @AfterEach
    void tearDown() {
        testHelper.cleanUp();
    }

    @Test
    void deleteMaintenanceCompanyShouldRemoveAssociationsAndKeepLog() {
        int companyId = seededCompany.getId();
        int logId = seededLog.getId();

        maintenanceCompanyService.deleteMaintenanceCompany(companyId);

        assertTrue(maintenanceCompanyRepository.findById(companyId).isEmpty());
        assertTrue(maintenanceLogCompanyRepository.findAll().isEmpty());
        assertTrue(maintenanceLogRepository.findById(logId).isPresent());
    }

    @Test
    void deleteMaintenanceLogShouldRemoveAssociationsAndKeepCompany() {
        int companyId = seededCompany.getId();
        int logId = seededLog.getId();

        maintenanceLogService.deleteMaintenanceLog(logId);

        assertTrue(maintenanceLogRepository.findById(logId).isEmpty());
        assertTrue(maintenanceLogCompanyRepository.findAll().isEmpty());
        assertTrue(maintenanceCompanyRepository.findById(companyId).isPresent());
    }
}
