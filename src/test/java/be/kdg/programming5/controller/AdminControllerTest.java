package be.kdg.programming5.controller;

import be.kdg.programming5.business.services.CsvImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private CsvImportService csvImportService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void uploadCsvPageWhenUserIsNotAdminShouldReturn403() throws Exception {
        mockMvc.perform(get("/admin/upload-csv"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleCsvUploadWhenFileIsEmptyShouldShowError() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        mockMvc.perform(multipart("/admin/upload-csv")
                        .file(emptyFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-upload-csv"))
                .andExpect(model().attribute("error", "Please select a CSV file before uploading."));

        then(csvImportService).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void handleCsvUploadShouldCopyBytesAndStartImport() throws Exception {
        byte[] csvBytes = """
                status,installationDate,direction,type,rightArrow,intersectionId
                ACTIVE,2023-03-15,N,COLLISION,false,1
                """.getBytes(StandardCharsets.UTF_8);
        MockMultipartFile file = new MockMultipartFile("file", "traffic-lights.csv", "text/csv", csvBytes);

        mockMvc.perform(multipart("/admin/upload-csv")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-upload-csv"))
                .andExpect(model().attribute("inProgress", true));

        then(csvImportService).should().importTrafficLightsAsync(aryEq(csvBytes));
    }
}
