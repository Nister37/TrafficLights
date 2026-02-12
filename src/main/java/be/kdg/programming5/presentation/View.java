package be.kdg.programming5.presentation;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class View {
    private final Scanner scanner = new Scanner(System.in);

    public int showMenuAndGetChoice() {
        System.out.println("""
               \s
                What would you like to do?
                ==========================
                0) Quit
                1) Show all Intersections
                2) Show traffic lights which belong to specific intersection id
                3) Show all Maintenance logs
                4) Show Maintenance companies by name and/or if still exists
                Choice (0-4):\s""");
        return scanner.nextInt();
    }

    public int getIntersectionId() {
        System.out.print("Enter intersection ID (mandatory): ");
        scanner.nextLine(); // consume newline
        return Integer.parseInt(scanner.nextLine().trim());
    }

    public String[] getCompanyFilters() {
        scanner.nextLine(); // consume newline

        System.out.print("Enter company name (optional - press Enter to skip): ");
        String nameFilter = scanner.nextLine().trim();

        System.out.print("Enter active status (true/false, optional - press Enter to skip): ");
        String activeFilter = scanner.nextLine().trim();

        return new String[]{nameFilter, activeFilter};
    }

    public void showIntersections(List<Intersection> intersections) {
        System.out.println("\n=== All Intersections ===");
        intersections.forEach(System.out::println);
    }

    public void showTrafficLights(List<TrafficLight> trafficLights, int intersectionId) {
        System.out.println("\n=== Traffic Lights for Intersection ID: " + intersectionId + " ===");
        trafficLights.forEach(System.out::println);
    }

    public void showMaintenanceLogs(List<MaintenanceLog> logs) {
        System.out.println("\n=== All Maintenance Logs ===");
        logs.forEach(System.out::println);
    }

    public void showMaintenanceCompanies(List<MaintenanceCompany> companies) {
        System.out.println("\n=== Filtered Maintenance Companies ===");
        companies.forEach(System.out::println);
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    public Scanner getScanner() {
        return scanner;
    }
}
