package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.enums.MaintenanceLogTypes;
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
public class MaintenanceLogController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLogController.class);

    private final MaintenanceLogService maintenanceLogService;
    private final TrafficLightService trafficLightService;
    private final MaintenanceCompanyService maintenanceCompanyService;

    public MaintenanceLogController(MaintenanceLogService maintenanceLogService, TrafficLightService trafficLightService, be.kdg.programming5.business.services.MaintenanceCompanyService maintenanceCompanyService) {
        this.maintenanceLogService = maintenanceLogService;
        this.trafficLightService = trafficLightService;
        this.maintenanceCompanyService = maintenanceCompanyService;
        logger.debug("MaintenanceLogController initialized");
    }

    @GetMapping("/maintenanceLogs")
    public String getAllMaintenanceLogs(
            @RequestParam(required = false) Boolean completed,
            Model model) {
        logger.debug("Getting all maintenance logs with filter - completed: {}", completed);

        List<MaintenanceLog> logs = maintenanceLogService.getAllMaintenanceLogs();

        if (completed != null) {
            logger.debug("Filtering by completed: {}", completed);
            logs = logs.stream()
                    .filter(log -> log.isCompleted() == completed)
                    .collect(Collectors.toList());
        }

        model.addAttribute("maintenanceLogs", logs);
        model.addAttribute("selectedCompleted", completed);

        logger.debug("Returning {} maintenance logs", logs.size());
        return "maintenance-logs";
    }

    @PostMapping("/addMaintenanceLog")
    public String addMaintenanceLog(
            @RequestParam String date,
            @RequestParam String description,
            @RequestParam MaintenanceLogTypes kind,
            @RequestParam double cost,
            @RequestParam(required = false, defaultValue = "false") boolean completed,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam int trafficLightId,
            Model model) {
        logger.debug("Adding new maintenance log - date: {}, kind: {}", date, kind);

        // Create maintenance log without ID - it will be auto-generated
        MaintenanceLog log = new MaintenanceLog(
                LocalDate.parse(date),
                description,
                kind,
                cost,
                completed,
                invoiceNumber != null ? invoiceNumber : ""
        );

        try {
            // Fetch traffic light first, then pass to service (proper 3-layer architecture)
            TrafficLight trafficLight = trafficLightService.getTrafficLightById(trafficLightId);
            maintenanceLogService.addMaintenanceLogWithTrafficLight(log, trafficLight);
            logger.debug("Maintenance log created and saved successfully");
            return "redirect:/maintenanceLogs";
        } catch (Exception e) {
            logger.error("Failed to add maintenance log: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("date", date);
            model.addAttribute("description", description);
            model.addAttribute("kind", kind);
            model.addAttribute("cost", cost);
            model.addAttribute("completed", completed);
            model.addAttribute("invoiceNumber", invoiceNumber);
            model.addAttribute("trafficLightId", trafficLightId);
            model.addAttribute("logTypes", MaintenanceLogTypes.values());
            model.addAttribute("trafficLights", trafficLightService.getAllTrafficLights());
            return "add-maintenance-log";
        }
    }

    @GetMapping("/addMaintenanceLog")
    public String addMaintenanceLogForm(Model model) {
        logger.debug("Displaying add maintenance log form");
        model.addAttribute("logTypes", MaintenanceLogTypes.values());
        model.addAttribute("trafficLights", trafficLightService.getAllTrafficLights());
        return "add-maintenance-log";
    }

    @GetMapping("/maintenanceLog/{id}")
    public String getMaintenanceLogDetails(@PathVariable int id, Model model) {
        logger.debug("Getting details for maintenance log id: {}", id);

        // Use JOIN FETCH method to load maintenance log with companies in single query
        var log = maintenanceLogService.getMaintenanceLogByIdWithCompanies(id);

        if (log == null) {
            logger.warn("Maintenance log with id {} not found", id);
            return "redirect:/maintenanceLogs";
        }

        // Companies already loaded via JOIN FETCH - use convenience method
        var maintenanceCompanies = log.getMaintenanceCompanies();

        model.addAttribute("log", log);
        model.addAttribute("maintenanceCompanies", maintenanceCompanies);
        logger.debug("Displaying details for maintenance log: {} with {} companies", log.getId(), maintenanceCompanies.size());
        return "maintenance-log-details";
    }

    @PostMapping("/maintenanceLog/{id}/delete")
    public String deleteMaintenanceLog(@PathVariable int id) {
        logger.debug("Deleting maintenance log with id: {}", id);
        maintenanceLogService.deleteMaintenanceLog(id);
        logger.debug("Maintenance log deleted successfully");
        return "redirect:/maintenanceLogs";
    }
}
