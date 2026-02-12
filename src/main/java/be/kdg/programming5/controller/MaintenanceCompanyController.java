package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.services.MaintenanceCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MaintenanceCompanyController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceCompanyController.class);

    public final MaintenanceCompanyService maintenanceCompanyService;

    public MaintenanceCompanyController(MaintenanceCompanyService maintenanceCompanyService) {
        this.maintenanceCompanyService = maintenanceCompanyService;
        logger.debug("MaintenanceCompanyController initialized");
    }

    @GetMapping("/maintenanceCompanies")
    public String getAllMaintenanceCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            Model model) {
        logger.debug("Getting all maintenance companies with filters - name: {}, active: {}", name, active);

        List<MaintenanceCompany> companies = maintenanceCompanyService.getAllMaintenanceCompanies();

        if (name != null && !name.trim().isEmpty()) {
            logger.debug("Filtering by name containing: {}", name);
            companies = companies.stream()
                    .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
            model.addAttribute("selectedName", name);
        }

        if (active != null) {
            logger.debug("Filtering by active: {}", active);
            companies = companies.stream()
                    .filter(c -> c.isActive() == active)
                    .collect(Collectors.toList());
            model.addAttribute("selectedActive", active);
        }

        model.addAttribute("companies", companies);

        logger.debug("Returning {} maintenance companies", companies.size());
        return "maintenance-companies";
    }

    @PostMapping("/addMaintenanceCompany")
    public String addMaintenanceCompany(
            @RequestParam String name,
            @RequestParam String contactPhone,
            @RequestParam String contactEmail,
            @RequestParam(required = false, defaultValue = "false") boolean active,
            @RequestParam String since) {
        logger.debug("Adding new maintenance company - name: {}, active: {}", name, active);

        MaintenanceCompany company = new MaintenanceCompany(
                name,
                contactPhone,
                contactEmail,
                active,
                LocalDate.parse(since)
        );

        maintenanceCompanyService.addMaintenanceCompany(company);

        logger.debug("Maintenance company created and saved successfully");
        return "redirect:/maintenanceCompanies";
    }

    @GetMapping("/maintenanceCompany/{id}")
    public String getMaintenanceCompanyDetails(@PathVariable int id, Model model) {
        logger.debug("Getting details for maintenance company id: {}", id);

        // Use the method that eagerly loads maintenance logs to avoid LazyInitializationException
        MaintenanceCompany company = maintenanceCompanyService.getMaintenanceCompanyByIdWithLogs(id);

        if (company == null) {
            logger.warn("Maintenance company with id {} not found", id);
            return "redirect:/maintenanceCompanies";
        }

        model.addAttribute("company", company);
        logger.debug("Displaying details for maintenance company: {}", company);
        return "maintenance-company-details";
    }

    @GetMapping("/addMaintenanceCompany")
    public String addMaintenanceCompanyForm() {
        logger.debug("Displaying add maintenance company form");
        return "add-maintenance-company";
    }

    @PostMapping("/maintenanceCompany/{id}/delete")
    public String deleteMaintenanceCompany(@PathVariable int id) {
        logger.debug("Deleting maintenance company with id: {}", id);
        maintenanceCompanyService.deleteMaintenanceCompany(id);
        logger.debug("Maintenance company deleted successfully");
        return "redirect:/maintenanceCompanies";
    }
}
