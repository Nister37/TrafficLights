package be.kdg.programming5.service;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.business.services.UserService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.repository.TrafficLightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
class TrafficLightCacheTest {

    @Autowired
    private TrafficLightService trafficLightService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private TrafficLightRepository trafficLightRepository;

    @MockitoBean
    private IntersectionService intersectionService;

    @MockitoBean
    private MaintenanceLogService maintenanceLogService;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    @AfterEach
    void clearTrafficLightSearchCache() {
        cacheManager.getCache("trafficLightSearch").clear();
    }

    @Test
    void addTrafficLightShouldEvictCachedStatusSearch() {
        given(trafficLightRepository.findByStatus(TrafficLightStatus.ACTIVE)).willReturn(List.of());

        trafficLightService.getTrafficLightsByStatus(TrafficLightStatus.ACTIVE);
        trafficLightService.getTrafficLightsByStatus(TrafficLightStatus.ACTIVE);
        then(trafficLightRepository).should(times(1)).findByStatus(TrafficLightStatus.ACTIVE);

        TrafficLight imported = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2023, 3, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        trafficLightService.addTrafficLight(imported);
        trafficLightService.getTrafficLightsByStatus(TrafficLightStatus.ACTIVE);

        then(trafficLightRepository).should(times(2)).findByStatus(TrafficLightStatus.ACTIVE);
    }
}
