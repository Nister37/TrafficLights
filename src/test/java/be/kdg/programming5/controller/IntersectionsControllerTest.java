package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.controller.api.dto.IntersectionTrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.exception.IntersectionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for IntersectionsController — GET /api/intersections/{id}/traffic-lights.
 *
 * Uses @SpringBootTest so the real controller and security config are loaded.
 * IntersectionService and TrafficLightMapper are replaced with Mockito mocks so only
 * the controller routing and security behaviour are under test.
 */
@SpringBootTest
@ActiveProfiles("test")
class IntersectionsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private IntersectionService intersectionService;

    @MockitoBean
    private TrafficLightMapper trafficLightMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    // =====================================================================
    // GET /api/intersections/{id}/traffic-lights
    // =====================================================================

    @Test
    @WithMockUser
    void getTrafficLightsByIntersectionWhenLightsExistShouldReturn200WithBody() throws Exception {
        // Arrange — intersection exists and has one traffic light
        TrafficLight light = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 4, 10),
                Direction.E, TrafficLightType.COLLISION, false
        );
        IntersectionTrafficLightDto dto = new IntersectionTrafficLightDto(
                3, TrafficLightStatus.ACTIVE, LocalDate.of(2022, 4, 10),
                Direction.E, TrafficLightType.COLLISION, false, 1, "TrafficLight", "user1"
        );
        // getIntersectionById is called first to verify existence — null return is fine here
        given(intersectionService.getIntersectionById(1)).willReturn(null);
        given(intersectionService.getTrafficLightsByIntersectionId(1)).willReturn(List.of(light));
        given(trafficLightMapper.toIntersectionTrafficLightDtoList(anyList())).willReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/intersections/1/traffic-lights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].ownerUsername").value("user1"));
    }

    @Test
    @WithMockUser
    void getTrafficLightsByIntersectionWhenNoLightsExistShouldReturn204() throws Exception {
        // Arrange — intersection exists but has no traffic lights
        given(intersectionService.getIntersectionById(2)).willReturn(null);
        given(intersectionService.getTrafficLightsByIntersectionId(2)).willReturn(List.of());

        // Act & Assert — empty list triggers the 204 branch in the controller
        mockMvc.perform(get("/api/intersections/2/traffic-lights"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getTrafficLightsByIntersectionWhenIntersectionNotFoundShouldReturn404() throws Exception {
        // Arrange — first existence check throws; traffic lights are never queried
        given(intersectionService.getIntersectionById(999))
                .willThrow(new IntersectionNotFoundException(999));

        // Act & Assert — GlobalExceptionHandler maps the exception to 404 for API requests
        mockMvc.perform(get("/api/intersections/999/traffic-lights"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrafficLightsByIntersectionWhenUnauthenticatedShouldReturn401() throws Exception {
        // Act & Assert — security config requires authentication for all /api/** endpoints
        mockMvc.perform(get("/api/intersections/1/traffic-lights"))
                .andExpect(status().isUnauthorized());
    }
}

