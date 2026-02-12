package be.kdg.programming5.controller;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.exception.IntersectionNotFoundException;
import be.kdg.programming5.presentation.viewmodel.IntersectionViewModel;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IntersectionController {
    private static final Logger logger = LoggerFactory.getLogger(IntersectionController.class);

    public final IntersectionService intersectionService;

    public IntersectionController(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
        logger.debug("IntersectionController initialized");
    }

    @GetMapping("/intersections")
    public String getAllIntersections(
            @RequestParam(required = false) IntersectionTypes type,
            @RequestParam(required = false) Boolean isSmartEnabled,
            Model model) {
        logger.debug("Getting all intersections with filters - type: {}, isSmartEnabled: {}", type, isSmartEnabled);

        List<Intersection> intersections = intersectionService.getAllIntersections();

        if (type != null) {
            logger.debug("Filtering by type: {}", type);
            intersections = intersections.stream()
                    .filter(i -> i.getType() == type)
                    .collect(Collectors.toList());
        }

        if (isSmartEnabled != null) {
            logger.debug("Filtering by isSmartEnabled: {}", isSmartEnabled);
            intersections = intersections.stream()
                    .filter(i -> i.isSmartEnabled() == isSmartEnabled)
                    .collect(Collectors.toList());
        }

        model.addAttribute("intersections", intersections);
        model.addAttribute("intersectionTypes", IntersectionTypes.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedSmartEnabled", isSmartEnabled);

        logger.debug("Returning {} intersections", intersections.size());
        return "intersections";
    }

    @PostMapping("/addIntersection")
    public String addIntersection(@Valid @ModelAttribute("intersection") IntersectionViewModel viewModel,
                                  BindingResult bindingResult,
                                  Model model) {
        logger.debug("Adding new intersection from ViewModel: {}", viewModel);

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors occurred: {}", bindingResult.getAllErrors());
            // Re-populate the dropdown lists
            model.addAttribute("intersectionTypes", IntersectionTypes.values());
            return "add-intersection";
        }

        Intersection intersection = new Intersection(
                viewModel.getLatitude(),
                viewModel.getLongitude(),
                viewModel.getType(),
                viewModel.getRoadCount(),
                viewModel.isSmartEnabled(),
                viewModel.getOpenedOn(),
                viewModel.isHasPedestrianCrossing(),
                viewModel.getIntersectionImage() != null ? viewModel.getIntersectionImage() : "/images/intersections/default.png"
        );

        try {
            intersectionService.addIntersection(intersection);
            logger.debug("Intersection created and saved successfully");
            return "redirect:/intersections";
        } catch (IllegalArgumentException e) {
            logger.error("Failed to add intersection: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("intersectionTypes", IntersectionTypes.values());
            return "add-intersection";
        }
    }

    @GetMapping("/addIntersection")
    public String addIntersectionForm(Model model) {
        logger.debug("Displaying add intersection form");

        // Add empty ViewModel to model for form binding
        model.addAttribute("intersection", new IntersectionViewModel());
        model.addAttribute("intersectionTypes", IntersectionTypes.values());

        return "add-intersection";
    }

    @GetMapping("/intersection/{id}")
    public String getIntersectionDetails(@PathVariable int id, Model model) {
        logger.debug("Getting details for intersection id: {}", id);
        // Use JOIN FETCH method to avoid N+1 queries - loads traffic lights in single query
        var intersection = intersectionService.getIntersectionByIdWithTrafficLights(id);
        if (intersection == null) {
            logger.warn("Intersection with id {} not found", id);
            return "redirect:/intersections";
        }
        // Traffic lights are already loaded via JOIN FETCH
        var trafficLights = intersection.getTrafficLights();
        model.addAttribute("intersection", intersection);
        model.addAttribute("trafficLights", trafficLights);
        logger.debug("Displaying details for intersection: {} with {} traffic lights", intersection.getId(), trafficLights.size());
        return "intersection-details";
    }

    @PostMapping("/intersection/{id}/delete")
    public String deleteIntersection(@PathVariable int id) {
        logger.debug("Deleting intersection with id: {}", id);
        intersectionService.deleteIntersection(id);
        logger.debug("Intersection deleted successfully");
        return "redirect:/intersections";
    }

    /**
     * Handles IntersectionNotFoundException locally in this controller.
     * Logs the error and returns to the intersections list page with an error message.
     *
     * @param ex the exception that was thrown
     * @param model the model to add error attributes to
     * @return view name for the intersections list page
     */
    @ExceptionHandler(IntersectionNotFoundException.class)
    public String handleIntersectionNotFound(IntersectionNotFoundException ex, Model model) {
        logger.error("Intersection not found: {}", ex.getMessage());

        model.addAttribute("error", ex.getMessage());
        model.addAttribute("intersectionTypes", IntersectionTypes.values());
        model.addAttribute("intersections", Collections.emptyList());

        return "intersections";
    }
}
