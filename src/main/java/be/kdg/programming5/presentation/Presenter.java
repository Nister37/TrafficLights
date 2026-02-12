package be.kdg.programming5.presentation;

import be.kdg.programming5.business.services.IntersectionService;
import be.kdg.programming5.business.services.MaintenanceCompanyService;
import be.kdg.programming5.business.services.MaintenanceLogService;
import be.kdg.programming5.business.services.TrafficLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Presenter {
    private final View view;
    private final IntersectionService intersectionService;
    private final MaintenanceLogService maintenanceLogService;
    private final MaintenanceCompanyService maintenanceCompanyService;
    private final TrafficLightService trafficLightService;

    @Autowired
    public Presenter(View view,
                    IntersectionService intersectionService,
                    MaintenanceLogService maintenanceLogService,
                    MaintenanceCompanyService maintenanceCompanyService,
                    TrafficLightService trafficLightService) {
        this.view = view;
        this.intersectionService = intersectionService;
        this.maintenanceLogService = maintenanceLogService;
        this.maintenanceCompanyService = maintenanceCompanyService;
        this.trafficLightService = trafficLightService;
    }

    public void start() {
        System.out.println("=== Traffic Lights Management System ===\n");
        do {
            try {
                int choice = view.showMenuAndGetChoice();
                handleMenuChoice(choice);
            } catch (Exception e) {
                view.showError("Invalid input. Please enter a number between 0 and 4.");
            }
        } while (true);
    }

    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 0 -> quit();
            case 1 -> showAllIntersections();
            case 2 -> showTrafficLightsByIntersectionId();
            case 3 -> showAllMaintenanceLogs();
            case 4 -> showMaintenanceCompaniesByFilters();
            default -> view.showError("Invalid choice. Please select 0-4.");
        }
    }

    private void showAllIntersections() {
        try {
            var intersections = intersectionService.getAllIntersections();
            view.showIntersections(intersections);
        } catch (Exception e) {
            view.showError("Failed to load intersections: " + e.getMessage());
        }
    }

    private void showTrafficLightsByIntersectionId() {
        try {
            int intersectionId = view.getIntersectionId();
            var trafficLights = intersectionService.getTrafficLightsByIntersectionId(intersectionId);
            view.showTrafficLights(trafficLights, intersectionId);
        } catch (Exception e) {
            view.showError("Failed to load traffic lights: " + e.getMessage());
        }
    }

    private void showAllMaintenanceLogs() {
        try {
            var logs = maintenanceLogService.getAllMaintenanceLogs();
            view.showMaintenanceLogs(logs);
        } catch (Exception e) {
            view.showError("Failed to load maintenance logs: " + e.getMessage());
        }
    }

    private void showMaintenanceCompaniesByFilters() {
        try {
            String[] filters = view.getCompanyFilters();
            var companies = maintenanceCompanyService.getFilteredMaintenanceCompanies(filters[0], filters[1]);
            view.showMaintenanceCompanies(companies);
        } catch (Exception e) {
            view.showError("Failed to load maintenance companies: " + e.getMessage());
        }
    }

    private void quit() {
        view.getScanner().close();
        System.exit(0);
    }
}
