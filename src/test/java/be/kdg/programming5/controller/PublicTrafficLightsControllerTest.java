package be.kdg.programming5.controller;

import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.controller.api.mapper.TrafficLightMapper;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the remaining public read-only traffic light endpoint.
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
    void getTrafficLightsWithoutAuthenticationWhenNoLightsExistShouldReturn204() throws Exception {
        given(trafficLightService.getAllTrafficLights()).willReturn(List.of());

        mockMvc.perform(get("/api/public/traffic-lights"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createTrafficLightWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/public/traffic-lights")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
