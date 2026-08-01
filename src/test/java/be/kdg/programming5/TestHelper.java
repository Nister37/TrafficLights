package be.kdg.programming5;

import be.kdg.programming5.business.domain.ApplicationUser;
import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.MaintenanceLogCompany;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.business.domain.UserRole;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.MaintenanceLogTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import be.kdg.programming5.repository.IntersectionRepository;
import be.kdg.programming5.repository.MaintenanceCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogRepository;
import be.kdg.programming5.repository.TrafficLightRepository;
import be.kdg.programming5.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TestHelper {
    private final UserRepository userRepository;
    private final IntersectionRepository intersectionRepository;
    private final TrafficLightRepository trafficLightRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final MaintenanceCompanyRepository maintenanceCompanyRepository;
    private final MaintenanceLogCompanyRepository maintenanceLogCompanyRepository;

    public TestHelper(UserRepository userRepository,
                      IntersectionRepository intersectionRepository,
                      TrafficLightRepository trafficLightRepository,
                      MaintenanceLogRepository maintenanceLogRepository,
                      MaintenanceCompanyRepository maintenanceCompanyRepository,
                      MaintenanceLogCompanyRepository maintenanceLogCompanyRepository) {
        this.userRepository = userRepository;
        this.intersectionRepository = intersectionRepository;
        this.trafficLightRepository = trafficLightRepository;
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.maintenanceCompanyRepository = maintenanceCompanyRepository;
        this.maintenanceLogCompanyRepository = maintenanceLogCompanyRepository;
    }

    public ApplicationUser applicationUser(String username, String passwordHash, UserRole role) {
        return userRepository.save(new ApplicationUser(username, passwordHash, role));
    }

    public Intersection intersection(double latitude, double longitude, IntersectionTypes type,
                                     int roadCount, boolean smartEnabled, LocalDate openedOn,
                                     boolean pedestrianCrossing, String image) {
        var intersection = new Intersection(
                latitude,
                longitude,
                type,
                roadCount,
                smartEnabled,
                openedOn,
                pedestrianCrossing,
                image
        );
        return intersectionRepository.save(intersection);
    }

    public TrafficLight trafficLight(TrafficLightStatus status, LocalDate installationDate,
                                     Direction direction, TrafficLightType type, boolean rightArrow,
                                     Intersection intersection) {
        return trafficLight(status, installationDate, direction, type, rightArrow, intersection, null);
    }

    public TrafficLight trafficLight(TrafficLightStatus status, LocalDate installationDate,
                                     Direction direction, TrafficLightType type, boolean rightArrow,
                                     Intersection intersection, ApplicationUser owner) {
        var trafficLight = new TrafficLight(status, installationDate, direction, type, rightArrow);
        trafficLight.setIntersection(intersection);
        if (owner != null) {
            trafficLight.setOwner(owner);
        }
        return trafficLightRepository.save(trafficLight);
    }

    public MaintenanceLog maintenanceLog(LocalDate date, String description, MaintenanceLogTypes kind,
                                         double cost, boolean completed, String invoiceNumber,
                                         TrafficLight trafficLight) {
        var maintenanceLog = new MaintenanceLog(date, description, kind, cost, completed, invoiceNumber);
        maintenanceLog.setTrafficLight(trafficLight);
        return maintenanceLogRepository.save(maintenanceLog);
    }

    public MaintenanceCompany maintenanceCompany(String name, String contactPhone, String contactEmail,
                                                 boolean active, LocalDate since) {
        var company = new MaintenanceCompany(name, contactPhone, contactEmail, active, since);
        return maintenanceCompanyRepository.save(company);
    }

    public MaintenanceLogCompany maintenanceLogCompany(MaintenanceLog maintenanceLog,
                                                       MaintenanceCompany maintenanceCompany) {
        var association = new MaintenanceLogCompany(maintenanceLog, maintenanceCompany);
        return maintenanceLogCompanyRepository.save(association);
    }

    public void cleanUp() {
        maintenanceLogCompanyRepository.deleteAllInBatch();
        maintenanceLogRepository.deleteAllInBatch();
        trafficLightRepository.deleteAllInBatch();
        maintenanceCompanyRepository.deleteAllInBatch();
        intersectionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
}
