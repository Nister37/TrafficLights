package be.kdg.programming5.controller.api;

import be.kdg.programming5.TestHelper;
import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.repository.MaintenanceCompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the public maintenance-company creation endpoint used by
 * the standalone Week 10 client. The request traverses the real service and repository.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PublicMaintenanceCompaniesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestHelper testHelper;

    @Autowired
    private MaintenanceCompanyRepository maintenanceCompanyRepository;

    @AfterEach
    void cleanUp() {
        testHelper.cleanUp();
    }

    @Test
    void createMaintenanceCompanyWithoutAuthenticationOrCsrfShouldReturn201() throws Exception {
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
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Signal Support"))
                .andExpect(jsonPath("$.contactEmail").value("contact@signalsupport.example"))
                .andExpect(jsonPath("$.since").value("2024-01-15"));

        List<MaintenanceCompany> companies = maintenanceCompanyRepository.findAll();
        assertEquals(1, companies.size());

        MaintenanceCompany saved = companies.getFirst();
        assertTrue(saved.getId() > 0);
        assertEquals("Signal Support", saved.getName());
        assertEquals("+32 123 45 67", saved.getContactPhone());
        assertEquals("contact@signalsupport.example", saved.getContactEmail());
        assertTrue(saved.isActive());
        assertEquals(LocalDate.of(2024, 1, 15), saved.getSince());
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
        long companyCountBeforeRequest = maintenanceCompanyRepository.count();

        mockMvc.perform(post("/api/public/maintenance-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertEquals(companyCountBeforeRequest, maintenanceCompanyRepository.count());
    }
}
