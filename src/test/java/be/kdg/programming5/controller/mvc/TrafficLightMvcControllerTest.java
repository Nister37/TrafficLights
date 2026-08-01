package be.kdg.programming5.controller.mvc;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.exception.TrafficLightNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the MVC TrafficLightController — Thymeleaf view rendering.
 *
 * Uses @SpringBootTest so the real MVC controller and security config are loaded.
 * MockMvc is set up via webAppContextSetup with the security filter chain applied.
 * Service dependencies are replaced with Mockito mocks (@MockitoBean) so only
 * the controller and view-rendering logic are under test.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TrafficLightMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrafficLightService trafficLightService;

    @MockitoBean
    private IntersectionService intersectionService;

    @MockitoBean
    private MaintenanceLogService maintenanceLogService;

    // =====================================================================
    // GET /trafficLights?status=... (requires authentication)
    // =====================================================================

    @Test
    @WithMockUser
    void getTrafficLightsByStatusWhenAuthenticatedShouldRenderViewWithFilteredList() throws Exception {
        // Arrange — three lights returned, only two match the ACTIVE filter applied in the controller
        TrafficLight active1 = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 1),
                Direction.N, TrafficLightType.COLLISION, false
        );
        TrafficLight active2 = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 3, 10),
                Direction.S, TrafficLightType.NON_COLLISION, true
        );
        TrafficLight maintenance = new TrafficLight(
                TrafficLightStatus.MAINTENANCE, LocalDate.of(2020, 1, 1),
                Direction.E, TrafficLightType.COLLISION, false
        );
        given(trafficLightService.getAllTrafficLights()).willReturn(List.of(active1, active2, maintenance));

        // Act & Assert
        mockMvc.perform(get("/trafficLights").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-lights"))
                .andExpect(model().attributeExists("trafficLights"))
                .andExpect(model().attribute("trafficLights", hasSize(2)))
                .andExpect(model().attribute("selectedStatus", TrafficLightStatus.ACTIVE));
    }

    @Test
    void getTrafficLightsByStatusWhenUnauthenticatedShouldRedirectToLogin() throws Exception {
        // Act & Assert — security config redirects to /login for unauthenticated MVC requests
        mockMvc.perform(get("/trafficLights").param("status", "ACTIVE"))
                .andExpect(status().is3xxRedirection());
    }

    // =====================================================================
    // GET /trafficLight/{id} (public — permitAll in SecurityConfig)
    // =====================================================================

    @Test
    void getTrafficLightDetailWhenFoundShouldRenderDetailView() throws Exception {
        // Arrange — public endpoint, no authentication required
        TrafficLight trafficLight = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        given(trafficLightService.getTrafficLightByIdWithMaintenanceLogs(1)).willReturn(trafficLight);

        // Act & Assert
        mockMvc.perform(get("/trafficLight/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-light-details"))
                .andExpect(model().attributeExists("trafficLight"))
                .andExpect(model().attributeExists("maintenanceLogs"));
    }

    @Test
    void getTrafficLightDetailWhenNotFoundShouldShowErrorInTrafficLightsView() throws Exception {
        // Arrange — service throws when ID doesn't exist
        given(trafficLightService.getTrafficLightByIdWithMaintenanceLogs(999))
                .willThrow(new TrafficLightNotFoundException(999));

        // Act & Assert — local @ExceptionHandler in TrafficLightController handles this:
        // returns "traffic-lights" view with an "error" model attribute
        mockMvc.perform(get("/trafficLight/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("traffic-lights"))
                .andExpect(model().attributeExists("error"));
    }
}

