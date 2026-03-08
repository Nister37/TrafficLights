package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.TrafficLightService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.exception.TrafficLightNotFoundException;
import be.kdg.programming5.presentation.viewmodel.TrafficLightViewModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling traffic light-related HTTP requests.
 * Manages CRUD operations and various filtering options for traffic lights.
 * Demonstrates Spring Data JPA method queries and custom @Query usage.
 */
@Controller
public class TrafficLightController {
    private static final Logger logger = LoggerFactory.getLogger(TrafficLightController.class);

    private final TrafficLightService trafficLightService;
    private final IntersectionService intersectionService;
    private final MaintenanceLogService maintenanceLogService;

    public TrafficLightController(TrafficLightService trafficLightService, IntersectionService intersectionService, be.kdg.programming5.business.services.MaintenanceLogService maintenanceLogService) {
        this.trafficLightService = trafficLightService;
        this.intersectionService = intersectionService;
        this.maintenanceLogService = maintenanceLogService;
        logger.debug("TrafficLightController initialized");
    }

    /**
     * Displays all traffic lights filtered by status.
     * This is the main listing page with mandatory status filter.
     *
     * @param status the status to filter by (required parameter)
     * @param model Spring MVC model for passing data to view
     * @return view name "traffic-lights" displaying filtered traffic lights
     */
    @GetMapping("/trafficLights")
    public String getAllTrafficLights(
            @RequestParam TrafficLightStatus status,
            Model model) {
        logger.debug("Getting all traffic lights with mandatory filter - status: {}", status);

        List<TrafficLight> trafficLights = trafficLightService.getAllTrafficLights();

        trafficLights = trafficLights.stream()
                .filter(tl -> tl.getStatus() == status)
                .collect(Collectors.toList());

        model.addAttribute("trafficLights", trafficLights);
        model.addAttribute("statuses", TrafficLightStatus.values());
        model.addAttribute("selectedStatus", status);

        logger.debug("Returning {} traffic lights", trafficLights.size());
        return "traffic-lights";
    }

    @PostMapping("/addTrafficLight")
    public String addTrafficLight(@Valid @ModelAttribute("trafficLight") TrafficLightViewModel viewModel,
                                  BindingResult bindingResult,
                                  Model model) {
        logger.debug("Adding new traffic light from ViewModel: {}", viewModel);

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors occurred: {}", bindingResult.getAllErrors());
            // Re-populate the dropdown lists
            model.addAttribute("statuses", TrafficLightStatus.values());
            model.addAttribute("directions", Direction.values());
            model.addAttribute("types", TrafficLightType.values());
            model.addAttribute("intersections", intersectionService.getAllIntersections());
            return "add-traffic-light";
        }

        // Create traffic light without ID - it will be auto-generated
        TrafficLight trafficLight = new TrafficLight(
                viewModel.getStatus(),
                viewModel.getInstallationDate(),
                viewModel.getDirection(),
                viewModel.getType(),
                viewModel.isRightArrow()
        );

        try {
            // Service layer will handle intersection relationship within transaction
            trafficLightService.addTrafficLightWithIntersection(trafficLight, viewModel.getIntersectionId());
            logger.debug("Traffic light created and saved successfully");
            return "redirect:/trafficLights?status=ACTIVE";
        } catch (Exception e) {
            logger.error("Failed to add traffic light: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("trafficLight", viewModel);
            model.addAttribute("statuses", TrafficLightStatus.values());
            model.addAttribute("directions", Direction.values());
            model.addAttribute("types", TrafficLightType.values());
            model.addAttribute("intersections", intersectionService.getAllIntersections());
            return "add-traffic-light";
        }
    }

    @GetMapping("/addTrafficLight")
    public String addTrafficLightForm(Model model) {
        logger.debug("Displaying add traffic light form");

        // Add empty ViewModel to model for form binding
        model.addAttribute("trafficLight", new TrafficLightViewModel());
        model.addAttribute("statuses", TrafficLightStatus.values());
        model.addAttribute("directions", Direction.values());
        model.addAttribute("types", TrafficLightType.values());
        model.addAttribute("intersections", intersectionService.getAllIntersections());

        return "add-traffic-light";
    }

    @GetMapping("/trafficLight/{id}")
    public String getTrafficLightDetails(@PathVariable int id, Model model) {
        logger.debug("Getting details for traffic light id: {}", id);

        // Use JOIN FETCH method to load traffic light with maintenance logs in single query
        TrafficLight trafficLight = trafficLightService.getTrafficLightByIdWithMaintenanceLogs(id);

        if (trafficLight == null) {
            logger.warn("Traffic light with id {} not found", id);
            return "redirect:/trafficLights?status=ACTIVE";
        }

        // Maintenance logs already loaded via JOIN FETCH
        List<MaintenanceLog> maintenanceLogs = trafficLight.getMaintenanceLogs();

        model.addAttribute("trafficLight", trafficLight);
        model.addAttribute("maintenanceLogs", maintenanceLogs);
        logger.debug("Displaying details for traffic light: {} with {} maintenance logs", trafficLight, maintenanceLogs.size());
        return "traffic-light-details";
    }

    @PostMapping("/trafficLight/{id}/delete")
    public String deleteTrafficLight(@PathVariable int id) {
        logger.debug("Deleting traffic light with id: {}", id);
        trafficLightService.deleteTrafficLight(id);
        logger.debug("Traffic light deleted successfully");
        return "redirect:/trafficLights?status=ACTIVE";
    }

    /**
     * Search traffic lights installed after a specific date using derived query method.
     * Uses: TrafficLightRepository.findByInstallationDateAfter(LocalDate date)
     */
    @GetMapping("/trafficLights/byDate")
    public String searchByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate afterDate, Model model) {
        logger.debug("Searching traffic lights installed after: {}", afterDate);
        List<TrafficLight> trafficLights = trafficLightService.getTrafficLightsInstalledAfter(afterDate);
        model.addAttribute("trafficLights", trafficLights);
        model.addAttribute("statuses", TrafficLightStatus.values());
        model.addAttribute("filterInfo", "Installed after " + afterDate);
        logger.debug("Found {} traffic lights installed after {}", trafficLights.size(), afterDate);
        return "traffic-lights";
    }

    /**
     * Search old traffic lights by status using custom @Query annotation.
     * Uses: TrafficLightRepository.findOldTrafficLightsByStatus(status, beforeDate)
     */
    @GetMapping("/trafficLights/oldByStatus")
    public String searchOldByStatus(
            @RequestParam TrafficLightStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate,
            Model model) {
        logger.debug("Searching old traffic lights with status: {} before: {}", status, beforeDate);
        List<TrafficLight> trafficLights = trafficLightService.getOldTrafficLightsByStatus(status, beforeDate);
        model.addAttribute("trafficLights", trafficLights);
        model.addAttribute("statuses", TrafficLightStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("filterInfo", "Status: " + status + " installed before " + beforeDate);
        logger.debug("Found {} old traffic lights with status {} before {}", trafficLights.size(), status, beforeDate);
        return "traffic-lights";
    }

    /**
     * Handles TrafficLightNotFoundException locally in this controller.
     * Logs the error and returns to the traffic lights list page with an error message.
     *
     * @param ex the exception that was thrown
     * @param model the model to add error attributes to
     * @return view name for the traffic lights list page
     */
    @ExceptionHandler(TrafficLightNotFoundException.class)
    public String handleTrafficLightNotFound(TrafficLightNotFoundException ex, Model model) {
        logger.error("Traffic light not found: {}", ex.getMessage());

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("statuses", TrafficLightStatus.values());
        model.addAttribute("trafficLights", Collections.emptyList());

        return "traffic-lights";
    }
}
