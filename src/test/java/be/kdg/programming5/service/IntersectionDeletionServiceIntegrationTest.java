package be.kdg.programming5.service;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.MaintenanceLogTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.repository.IntersectionRepository;
import be.kdg.programming5.repository.MaintenanceCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogRepository;
import be.kdg.programming5.repository.TrafficLightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies child-first deletion for an intersection and its traffic-light subtree.
 */
@SpringBootTest
@ActiveProfiles("test")
class IntersectionDeletionServiceIntegrationTest {

    @Autowired
    private IntersectionService intersectionService;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private MaintenanceLogCompanyRepository maintenanceLogCompanyRepository;

    @Autowired
    private MaintenanceCompanyRepository maintenanceCompanyRepository;

    private Intersection seededIntersection;
    private MaintenanceCompany seededCompany;

    @BeforeEach
    void setUp() {
        seededIntersection = testHelper.intersection(
                50.0, 4.0, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );

        TrafficLight trafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false,
                seededIntersection
        );

        MaintenanceLog log = testHelper.maintenanceLog(
                LocalDate.of(2023, 1, 15), "Test LED replacement",
                MaintenanceLogTypes.ELECTRICAL, 150.0, true, "INV-TEST-001",
                trafficLight
        );

        seededCompany = testHelper.maintenanceCompany(
                "Test Maintenance Co.", "+32 123 456", "test@maintenance.be",
                true, LocalDate.of(2015, 6, 1)
        );

        testHelper.maintenanceLogCompany(log, seededCompany);
    }

    @AfterEach
    void tearDown() {
        testHelper.cleanUp();
    }

    @Test
    void deleteIntersectionShouldRemoveTrafficLightSubtreeAndKeepCompany() {
        int intersectionId = seededIntersection.getId();
        int companyId = seededCompany.getId();

        intersectionService.deleteIntersection(intersectionId);

        assertTrue(intersectionRepository.findById(intersectionId).isEmpty());
        assertTrue(trafficLightRepository.findAll().isEmpty());
        assertTrue(maintenanceLogRepository.findAll().isEmpty());
        assertTrue(maintenanceLogCompanyRepository.findAll().isEmpty());
        assertTrue(maintenanceCompanyRepository.findById(companyId).isPresent());
    }
}
