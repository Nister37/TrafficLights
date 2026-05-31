package be.kdg.programming5.controller.mvc;

import be.kdg.programming5.business.services.CsvImportService;
import be.kdg.programming5.config.security.AdminOnly;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class AdminController {

    private final CsvImportService csvImportService;

    public AdminController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @GetMapping("/admin")
    @AdminOnly
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/admin/upload-csv")
    @AdminOnly
    public String uploadCsvPage() {
        return "admin-upload-csv";
    }

    /**
     * Accepts the CSV upload, hands the stream to the async service, and returns
     * the upload page immediately with an "in progress" message so the user is
     * not left waiting for the import to complete.
     */
    @PostMapping("/admin/upload-csv")
    @AdminOnly
    public String handleCsvUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a CSV file before uploading.");
            return "admin-upload-csv";
        }

        csvImportService.importTrafficLightsAsync(file.getBytes());

        model.addAttribute("inProgress", true);
        return "admin-upload-csv";
    }
}
