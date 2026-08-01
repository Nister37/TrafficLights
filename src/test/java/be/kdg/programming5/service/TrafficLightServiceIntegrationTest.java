package be.kdg.programming5.service;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.*;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.config.security.CustomUserDetails;
import be.kdg.programming5.enums.*;
import be.kdg.programming5.exception.ForbiddenOperationException;
import be.kdg.programming5.exception.TrafficLightNotFoundException;
import be.kdg.programming5.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TrafficLightService.
 * Uses @SpringBootTest with the test profile (isolated PostgreSQL database).
 * Tests service methods with real repositories — no mocking.
 * SecurityContext is set up manually to simulate authenticated users.
 */
@SpringBootTest
@ActiveProfiles("test")
class TrafficLightServiceIntegrationTest {

    @Autowired
    private TrafficLightService trafficLightService;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    private ApplicationUser ownerUser;
    private ApplicationUser adminUser;
    private ApplicationUser otherUser;
    private TrafficLight seededTrafficLight;

    @BeforeEach
    void setUp() {
        // Seed users with different roles
        ownerUser = testHelper.applicationUser("testowner", "hashedpw", UserRole.USER);
        adminUser = testHelper.applicationUser("testadmin", "hashedpw", UserRole.ADMIN);
        otherUser = testHelper.applicationUser("testother", "hashedpw", UserRole.USER);

        // Seed intersection
        Intersection seededIntersection = testHelper.intersection(
                50.0, 4.0, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );

        // Seed traffic light owned by ownerUser
        seededTrafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false,
                seededIntersection, ownerUser
        );

        // Seed maintenance log chain (needed for delete cascade testing)
        MaintenanceLog log = testHelper.maintenanceLog(
                LocalDate.of(2023, 1, 15), "Test LED replacement",
                MaintenanceLogTypes.ELECTRICAL, 150.0, true, "INV-TEST-001",
                seededTrafficLight
        );

        MaintenanceCompany company = testHelper.maintenanceCompany(
                "Test Maintenance Co.", "+32 123 456", "test@maintenance.be",
                true, LocalDate.of(2015, 6, 1)
        );

        testHelper.maintenanceLogCompany(log, company);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        testHelper.cleanUp();
    }

    // ===============================
    // deleteTrafficLight tests
    // ===============================

    @Test
    void deleteTrafficLightAsOwnerShouldSucceed() {
        // Arrange
        authenticateAs(ownerUser);
        int id = seededTrafficLight.getId();

        // Act
        trafficLightService.deleteTrafficLight(id);

        // Assert — traffic light and its maintenance logs should be removed
        assertTrue(trafficLightRepository.findById(id).isEmpty(),
                "Traffic light should be deleted when owner requests deletion");
        assertTrue(maintenanceLogRepository.findAll().isEmpty(),
                "Maintenance logs should be cascade-deleted by the service");
    }

    @Test
    void deleteTrafficLightAsAdminShouldSucceed() {
        // Arrange
        authenticateAs(adminUser);
        int id = seededTrafficLight.getId();

        // Act
        trafficLightService.deleteTrafficLight(id);

        // Assert
        assertTrue(trafficLightRepository.findById(id).isEmpty(),
                "Traffic light should be deleted when admin requests deletion");
    }

    @Test
    void deleteTrafficLightAsNonOwnerShouldThrowForbiddenOperationException() {
        // Arrange
        authenticateAs(otherUser);

        // Act & Assert
        assertThrows(ForbiddenOperationException.class,
                () -> trafficLightService.deleteTrafficLight(seededTrafficLight.getId()),
                "Non-owner user should not be allowed to delete another user's traffic light");
    }

    @Test
    void deleteTrafficLightWithNonExistentIdShouldThrowTrafficLightNotFoundException() {
        // Arrange
        authenticateAs(ownerUser);

        // Act & Assert
        assertThrows(TrafficLightNotFoundException.class,
                () -> trafficLightService.deleteTrafficLight(999_999),
                "Deleting a non-existent traffic light should throw TrafficLightNotFoundException");
    }

    // ===============================
    // updateTrafficLight tests
    // ===============================

    @Test
    void updateTrafficLightAsOwnerShouldApplyPartialChanges() {
        // Arrange
        authenticateAs(ownerUser);

        // Act — only update status, leave other fields null (partial/merge patch)
        TrafficLight updated = trafficLightService.updateTrafficLight(
                seededTrafficLight.getId(), TrafficLightStatus.MAINTENANCE, null, null, null
        );

        // Assert — updated field changed, others unchanged
        assertEquals(TrafficLightStatus.MAINTENANCE, updated.getStatus(),
                "Status should be updated to MAINTENANCE");
        assertEquals(Direction.N, updated.getDirection(),
                "Direction should remain unchanged when null was passed");
        assertEquals(TrafficLightType.COLLISION, updated.getType(),
                "Type should remain unchanged when null was passed");
        assertFalse(updated.isRightArrow(),
                "Right arrow should remain unchanged when null was passed");
    }

    @Test
    void updateTrafficLightAsAdminShouldSucceed() {
        // Arrange
        authenticateAs(adminUser);

        // Act — admin updates multiple fields on a traffic light they don't own
        TrafficLight updated = trafficLightService.updateTrafficLight(
                seededTrafficLight.getId(), null, Direction.S, TrafficLightType.NON_COLLISION, true
        );

        // Assert
        assertEquals(Direction.S, updated.getDirection(),
                "Direction should be updated to S");
        assertEquals(TrafficLightType.NON_COLLISION, updated.getType(),
                "Type should be updated to NON_COLLISION");
        assertTrue(updated.isRightArrow(),
                "Right arrow should be updated to true");
        assertEquals(TrafficLightStatus.ACTIVE, updated.getStatus(),
                "Status should remain unchanged when null was passed");
    }

    @Test
    void updateTrafficLightAsNonOwnerShouldThrowForbiddenOperationException() {
        // Arrange
        authenticateAs(otherUser);

        // Act & Assert
        assertThrows(ForbiddenOperationException.class,
                () -> trafficLightService.updateTrafficLight(
                        seededTrafficLight.getId(), TrafficLightStatus.MAINTENANCE, null, null, null
                ),
                "Non-owner user should not be allowed to update another user's traffic light");
    }

    @Test
    void updateTrafficLightWithNonExistentIdShouldThrowTrafficLightNotFoundException() {
        // Arrange
        authenticateAs(ownerUser);

        // Act & Assert
        assertThrows(TrafficLightNotFoundException.class,
                () -> trafficLightService.updateTrafficLight(
                        999_999, TrafficLightStatus.MAINTENANCE, null, null, null
                ),
                "Updating a non-existent traffic light should throw TrafficLightNotFoundException");
    }

    /**
     * Sets up the SecurityContext with the given ApplicationUser,
     * matching how UserServiceImpl.getAuthenticatedUser() resolves the user.
     */
    private void authenticateAs(ApplicationUser user) {
        CustomUserDetails userDetails = new CustomUserDetails(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                user.getId()
        );
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}







