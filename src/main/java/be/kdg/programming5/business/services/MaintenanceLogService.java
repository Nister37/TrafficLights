package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;

import java.util.List;

public interface MaintenanceLogService {
    List<MaintenanceLog> getAllMaintenanceLogs();
    List<MaintenanceLog> getMaintenanceLogsByTrafficLightId(int trafficLightId);
    MaintenanceLog getMaintenanceLogById(int id);
    void addMaintenanceLog(MaintenanceLog maintenanceLog);
    void addMaintenanceLogWithTrafficLight(MaintenanceLog maintenanceLog, TrafficLight trafficLight);
    void deleteMaintenanceLog(int id);

    /**
     * Deletes all maintenance logs for a specific traffic light.
     */
    void deleteByTrafficLightId(int trafficLightId);

    /**
     * Retrieves a maintenance log by ID with maintenance companies eagerly loaded.
     */
    MaintenanceLog getMaintenanceLogByIdWithCompanies(int id);
}
