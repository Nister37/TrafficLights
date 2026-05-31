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
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the unauthenticated creation endpoint used by the standalone W10 client.
 */
@SpringBootTest
@ActiveProfiles("test")
class PublicTrafficLightsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private TrafficLightService trafficLightService;

    @MockitoBean
    private TrafficLightMapper trafficLightMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void createTrafficLightWithoutAuthenticationOrCsrfShouldReturn201() throws Exception {
        TrafficLight saved = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false
        );
        TrafficLightDto dto = new TrafficLightDto(
                5, TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false, 1, "TrafficLight"
        );
        given(trafficLightService.createPublicTrafficLight(
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

        mockMvc.perform(post("/api/public/traffic-lights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        then(trafficLightService).should().createPublicTrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2021, 6, 15),
                Direction.N, TrafficLightType.COLLISION, false, 1);
    }

    @Test
    void createTrafficLightWithMissingRequiredFieldsShouldReturn400() throws Exception {
        String invalidBody = """
                {
                  "rightArrow": false,
                  "intersectionId": 1
                }
                """;

        mockMvc.perform(post("/api/public/traffic-lights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}
