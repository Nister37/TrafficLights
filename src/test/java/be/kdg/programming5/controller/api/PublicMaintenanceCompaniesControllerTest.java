package be.kdg.programming5.controller.api;

import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.services.MaintenanceCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the public maintenance company creation endpoint used by the standalone W10 client.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PublicMaintenanceCompaniesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaintenanceCompanyService maintenanceCompanyService;

    @Test
    void createMaintenanceCompanyWithoutAuthenticationOrCsrfShouldReturn201() throws Exception {
        MaintenanceCompany saved = new MaintenanceCompany(
                5, "Signal Support", "+32 123 45 67",
                "contact@signalsupport.example", true, LocalDate.of(2024, 1, 15)
        );
        given(maintenanceCompanyService.createMaintenanceCompany(
                anyString(), anyString(), anyString(), anyBoolean(), any(LocalDate.class)))
                .willReturn(saved);

        String body = """
                {
                  "name": "Signal Support",
                  "contactPhone": "+32 123 45 67",
                  "contactEmail": "contact@signalsupport.example",
                  "active": true,
                  "since": "2024-01-15"
                }
                """;

        mockMvc.perform(post("/api/public/maintenance-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Signal Support"))
                .andExpect(jsonPath("$.contactEmail").value("contact@signalsupport.example"));

        then(maintenanceCompanyService).should().createMaintenanceCompany(
                "Signal Support", "+32 123 45 67",
                "contact@signalsupport.example", true, LocalDate.of(2024, 1, 15));
    }

    @Test
    void createMaintenanceCompanyWithMissingNameShouldReturn400() throws Exception {
        assertInvalidRequest("""
                {
                  "contactPhone": "+32 123 45 67",
                  "contactEmail": "contact@signalsupport.example",
                  "active": true,
                  "since": "2024-01-15"
                }
                """);
    }

    @Test
    void createMaintenanceCompanyWithInvalidEmailShouldReturn400() throws Exception {
        assertInvalidRequest("""
                {
                  "name": "Signal Support",
                  "contactPhone": "+32 123 45 67",
                  "contactEmail": "not-an-email",
                  "active": true,
                  "since": "2024-01-15"
                }
                """);
    }

    @Test
    void createMaintenanceCompanyWithFutureSinceDateShouldReturn400() throws Exception {
        assertInvalidRequest("""
                {
                  "name": "Signal Support",
                  "contactPhone": "+32 123 45 67",
                  "contactEmail": "contact@signalsupport.example",
                  "active": true,
                  "since": "2099-01-15"
                }
                """);
    }

    private void assertInvalidRequest(String body) throws Exception {
        mockMvc.perform(post("/api/public/maintenance-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        then(maintenanceCompanyService).shouldHaveNoInteractions();
    }
}
