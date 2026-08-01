package be.kdg.programming5.controller.mvc;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.domain.UserRole;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.MaintenanceLogTypes;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Integration tests for the MVC TrafficLightController and its Thymeleaf views.
 * The requests use real security, services, repositories, and test-database fixtures.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TrafficLightMvcControllerTest {

    private static final String USERNAME = "mvc-user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestHelper testHelper;

    private ApplicationUser applicationUser;

    @BeforeEach
    void setUp() {
        applicationUser = testHelper.applicationUser(USERNAME, "test-password", UserRole.USER);
    }

    @AfterEach
    void cleanUp() {
        testHelper.cleanUp();
    }

    @Test
    @WithUserDetails(value = USERNAME, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getTrafficLightsByStatusWhenAuthenticatedShouldRenderViewWithFilteredList() throws Exception {
        Intersection intersection = createIntersection();
        testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 1),
                Direction.N, TrafficLightType.COLLISION, false,
                intersection, applicationUser
        );
        testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 3, 10),
                Direction.S, TrafficLightType.NON_COLLISION, true,
                intersection, applicationUser
        );
        testHelper.trafficLight(
                TrafficLightStatus.MAINTENANCE, LocalDate.of(2020, 1, 1),
                Direction.E, TrafficLightType.COLLISION, false,
                intersection, applicationUser
        );

        mockMvc.perform(get("/trafficLights").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-lights"))
                .andExpect(model().attribute("trafficLights", hasSize(2)))
                .andExpect(model().attribute("selectedStatus", TrafficLightStatus.ACTIVE));
    }

    @Test
    void getTrafficLightsByStatusWhenUnauthenticatedShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/trafficLights").param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getTrafficLightDetailWhenFoundShouldRenderDetailView() throws Exception {
        Intersection intersection = createIntersection();
        TrafficLight trafficLight = testHelper.trafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false,
                intersection, applicationUser
        );
        testHelper.maintenanceLog(
                LocalDate.of(2023, 1, 15), "Test LED replacement",
                MaintenanceLogTypes.ELECTRICAL, 150.0, true, "INV-MVC-001",
                trafficLight
        );

        mockMvc.perform(get("/trafficLight/{id}", trafficLight.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-light-details"))
                .andExpect(model().attribute("trafficLight",
                        hasProperty("id", is(trafficLight.getId()))))
                .andExpect(model().attribute("maintenanceLogs", hasSize(1)));
    }

    @Test
    void getTrafficLightDetailWhenNotFoundShouldShowErrorInTrafficLightsView() throws Exception {
        mockMvc.perform(get("/trafficLight/{id}", 999_999))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-lights"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("trafficLights", hasSize(0)));
    }

    private Intersection createIntersection() {
        return testHelper.intersection(
                51.2194, 4.4025, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2020, 1, 1), true, "/images/test.png"
        );
    }
}
