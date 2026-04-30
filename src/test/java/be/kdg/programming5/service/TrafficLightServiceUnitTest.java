package be.kdg.programming5.service;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.business.services.UserService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.exception.TrafficLightNotFoundException;
import be.kdg.programming5.repository.TrafficLightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

/**
 * Unit tests for TrafficLightService — repository dependencies are replaced with Mockito mocks
 * so only the service orchestration logic is under test.
 *
 * @MockitoBean replaces beans in the Spring context; the real service implementation
 * (TrafficLightServiceImpl) is autowired and exercised directly.
 */
@SpringBootTest
@ActiveProfiles("test")
class TrafficLightServiceUnitTest {

    @Autowired
    private TrafficLightService trafficLightService;

    @MockitoBean
    private TrafficLightRepository trafficLightRepository;

    // Wired by the service but not exercised by the methods under test — mocked to
    // satisfy Spring's dependency injection without hitting the real implementation
    @MockitoBean
    private IntersectionService intersectionService;

    @MockitoBean
    private MaintenanceLogService maintenanceLogService;

    @MockitoBean
    private UserService userService;

    // =====================================================================
    // getAllTrafficLights
    // =====================================================================

    @Test
    void getAllTrafficLightsWhenLightsExistShouldReturnAllLights() {
        // Arrange
        TrafficLight light = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        given(trafficLightRepository.findAllWithOwner()).willReturn(List.of(light));

        // Act
        List<TrafficLight> result = trafficLightService.getAllTrafficLights();

        // Assert
        assertEquals(1, result.size());
        assertEquals(TrafficLightStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void getAllTrafficLightsWhenNoLightsExistShouldReturnEmptyList() {
        // Arrange
        given(trafficLightRepository.findAllWithOwner()).willReturn(List.of());

        // Act
        List<TrafficLight> result = trafficLightService.getAllTrafficLights();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTrafficLightsShouldDelegateToRepositoryFindAllWithOwner() {
        // Arrange
        given(trafficLightRepository.findAllWithOwner()).willReturn(List.of());

        // Act
        trafficLightService.getAllTrafficLights();

        // Assert — verify the correct query method was called, not a plain findAll
        then(trafficLightRepository).should().findAllWithOwner();
    }

    // =====================================================================
    // getTrafficLightById
    // =====================================================================

    @Test
    void getTrafficLightByIdWhenFoundShouldReturnCorrectLight() {
        // Arrange
        TrafficLight light = new TrafficLight(
                1, TrafficLightStatus.MAINTENANCE, LocalDate.of(2020, 3, 1),
                Direction.S, TrafficLightType.NON_COLLISION, true
        );
        given(trafficLightRepository.findById(1)).willReturn(Optional.of(light));

        // Act
        TrafficLight result = trafficLightService.getTrafficLightById(1);

        // Assert
        assertEquals(1, result.getId());
        assertEquals(TrafficLightStatus.MAINTENANCE, result.getStatus());
        assertEquals(Direction.S, result.getDirection());
    }

    @Test
    void getTrafficLightByIdWhenNotFoundShouldThrowTrafficLightNotFoundException() {
        // Arrange — empty Optional simulates a missing record
        given(trafficLightRepository.findById(999)).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(TrafficLightNotFoundException.class,
                () -> trafficLightService.getTrafficLightById(999),
                "Should throw TrafficLightNotFoundException when no light exists for the given ID");
    }

    @Test
    void getTrafficLightByIdShouldQueryRepositoryWithTheExactId() {
        // Arrange
        TrafficLight light = new TrafficLight(
                42, TrafficLightStatus.ACTIVE, LocalDate.of(2022, 1, 1),
                Direction.E, TrafficLightType.COLLISION, false
        );
        given(trafficLightRepository.findById(42)).willReturn(Optional.of(light));

        // Act
        trafficLightService.getTrafficLightById(42);

        // Assert — verify that findById was called with exactly the ID that was passed in
        then(trafficLightRepository).should().findById(42);
    }
}

