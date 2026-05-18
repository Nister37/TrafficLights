package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.controller.api.dto.TrafficLightDto;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
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

import be.kdg.programming5.exception.TrafficLightNotFoundException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

/**
 * Unit tests for TrafficLightsController — GET /api/traffic-lights.
 *
 * Uses @SpringBootTest so the real controller and security config are loaded.
 * MockMvc is set up manually via webAppContextSetup with the security filter chain applied.
 * TrafficLightService and TrafficLightMapper are replaced with Mockito mocks (@MockitoBean)
 * so only the controller logic is under test.
 */
@SpringBootTest
@ActiveProfiles("test")
class TrafficLightsControllerUnitTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Apply the real security filter chain so unauthenticated requests return 401
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @MockitoBean
    private TrafficLightService trafficLightService;

    @MockitoBean
    private TrafficLightMapper trafficLightMapper;

    // =====================================================================
    // GET /api/traffic-lights
    // =====================================================================

    @Test
    @WithMockUser
    void getAllTrafficLightsWhenLightsExistShouldReturn200WithBody() throws Exception {
        // Arrange
        TrafficLight trafficLight = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        TrafficLightDto dto = new TrafficLightDto(
                1, TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false, 10, "TrafficLight"
        );

        given(trafficLightService.getAllTrafficLights()).willReturn(List.of(trafficLight));
        // anyList() avoids reference comparison — the controller passes its own list instance
        given(trafficLightMapper.toTrafficLightDtoList(anyList())).willReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/traffic-lights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    void getAllTrafficLightsWhenNoLightsExistShouldReturn204() throws Exception {
        // Arrange — empty list triggers the 204 branch in the controller
        given(trafficLightService.getAllTrafficLights()).willReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/traffic-lights"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllTrafficLightsWhenUnauthenticatedShouldReturn401() throws Exception {
        // Act & Assert — security config returns 401 for unauthenticated /api/** requests
        mockMvc.perform(get("/api/traffic-lights"))
                .andExpect(status().isUnauthorized());
    }

    // =====================================================================
    // GET /api/traffic-lights/{id}
    // =====================================================================

    @Test
    @WithMockUser
    void getTrafficLightByIdWhenFoundShouldReturn200WithBody() throws Exception {
        // Arrange
        TrafficLight trafficLight = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        TrafficLightDto dto = new TrafficLightDto(
                1, TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false, 10, "TrafficLight"
        );
        given(trafficLightService.getTrafficLightById(1)).willReturn(trafficLight);
        given(trafficLightMapper.toTrafficLightDto(any(TrafficLight.class))).willReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/traffic-lights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    void getTrafficLightByIdWhenNotFoundShouldReturn404() throws Exception {
        // Arrange — service throws when the ID doesn't exist in the system
        given(trafficLightService.getTrafficLightById(999))
                .willThrow(new TrafficLightNotFoundException(999));

        // Act & Assert — GlobalExceptionHandler maps the exception to 404 for API requests
        mockMvc.perform(get("/api/traffic-lights/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrafficLightByIdWhenUnauthenticatedShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/traffic-lights/1"))
                .andExpect(status().isUnauthorized());
    }

    // =====================================================================
    // POST /api/traffic-lights
    // =====================================================================

    @Test
    @WithMockUser
    void createTrafficLightWithValidBodyShouldReturn201WithCreatedLight() throws Exception {
        // Arrange
        TrafficLight saved = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        TrafficLightDto dto = new TrafficLightDto(
                5, TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false, 1, "TrafficLight"
        );
        given(trafficLightService.createTrafficLight(
                any(), any(), any(), any(), anyBoolean(), anyInt())).willReturn(saved);
        given(trafficLightMapper.toTrafficLightDto(any(TrafficLight.class))).willReturn(dto);

        String body = """
                {
                  "status": "ACTIVE",
                  "installationDate": "2021-06-15",
                  "direction": "N",
                  "type": "COLLISION",
                  "rightArrow": false,
                  "intersectionId": 1
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/traffic-lights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    void createTrafficLightWithMissingRequiredFieldsShouldReturn400() throws Exception {
        // Arrange — status and installationDate are @NotNull in CreateTrafficLightDto
        String invalidBody = """
                {
                  "rightArrow": false,
                  "intersectionId": 1
                }
                """;

        // Act & Assert — @Valid on the controller triggers MethodArgumentNotValidException → 400
        mockMvc.perform(post("/api/traffic-lights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTrafficLightWhenUnauthenticatedShouldReturn401() throws Exception {
        String body = """
                {
                  "status": "ACTIVE",
                  "installationDate": "2021-06-15",
                  "direction": "N",
                  "type": "COLLISION",
                  "rightArrow": false,
                  "intersectionId": 1
                }
                """;

        mockMvc.perform(post("/api/traffic-lights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // =====================================================================
    // PATCH /api/traffic-lights/{id}
    // =====================================================================

    @Test
    @WithMockUser
    void updateTrafficLightWithValidBodyShouldReturn204() throws Exception {
        // Arrange — updateTrafficLight returns the updated entity; controller ignores it and returns 204
        TrafficLight updated = new TrafficLight(
                TrafficLightStatus.MAINTENANCE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        given(trafficLightService.updateTrafficLight(anyInt(), any(), any(), any(), any()))
                .willReturn(updated);

        String body = """
                {"status": "MAINTENANCE"}
                """;

        mockMvc.perform(patch("/api/traffic-lights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateTrafficLightWhenUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(patch("/api/traffic-lights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"MAINTENANCE\"}")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // =====================================================================
    // DELETE /api/traffic-lights/{id}
    // =====================================================================

    @Test
    @WithMockUser
    void deleteTrafficLightWhenFoundShouldReturn204() throws Exception {
        // Arrange — controller calls getById first to verify existence, then deletes
        TrafficLight light = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        given(trafficLightService.getTrafficLightById(1)).willReturn(light);

        mockMvc.perform(delete("/api/traffic-lights/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteTrafficLightWhenNotFoundShouldReturn404() throws Exception {
        // Arrange — getById throws; the delete call is never reached
        given(trafficLightService.getTrafficLightById(404))
                .willThrow(new TrafficLightNotFoundException(404));

        mockMvc.perform(delete("/api/traffic-lights/404").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTrafficLightWhenUnauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/traffic-lights/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}




