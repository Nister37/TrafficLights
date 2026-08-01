package be.kdg.programming5.controller.api;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.domain.UserRole;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for GET /api/intersections/{id}/traffic-lights.
 * The requests use the real controller, security configuration, service, mapper,
 * repositories, and test database.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class IntersectionsControllerTest {

    private static final String USERNAME = "intersection-owner";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestHelper testHelper;

    private ApplicationUser owner;

    @BeforeEach
    void setUp() {
        owner = testHelper.applicationUser(USERNAME, "test-password", UserRole.USER);
    }

    @AfterEach
    void cleanUp() {
        testHelper.cleanUp();
    }

    @Test
    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getTrafficLightsByIntersectionWhenLightsExistShouldReturn200WithBody() throws Exception {
        Intersection intersection = createIntersection(51.2194, 4.4025);
        TrafficLight trafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 4, 10),
                Direction.E, TrafficLightType.COLLISION, false,
                intersection, owner
        );

        mockMvc.perform(get("/api/intersections/{id}/traffic-lights", intersection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(trafficLight.getId()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].intersectionId").value(intersection.getId()))
                .andExpect(jsonPath("$[0].ownerUsername").value(USERNAME));
    }

    @Test
    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getTrafficLightsByIntersectionWhenNoLightsExistShouldReturn204() throws Exception {
        Intersection intersection = createIntersection(50.8503, 4.3517);

        mockMvc.perform(get("/api/intersections/{id}/traffic-lights", intersection.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getTrafficLightsByIntersectionWhenIntersectionNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/intersections/{id}/traffic-lights", 999_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrafficLightsByIntersectionWhenUnauthenticatedShouldReturn200WithBody() throws Exception {
        Intersection intersection = createIntersection(50.8798, 4.7005);
        TrafficLight trafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 4, 10),
                Direction.E, TrafficLightType.COLLISION, false,
                intersection, owner
        );

        mockMvc.perform(get("/api/intersections/{id}/traffic-lights", intersection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(trafficLight.getId()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].ownerUsername").value(USERNAME));
    }

    private Intersection createIntersection(double latitude, double longitude) {
        return testHelper.intersection(
                latitude, longitude, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );
    }
}
