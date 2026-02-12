package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceLog;

import java.util.List;

public interface MaintenanceLogService {
    List<MaintenanceLog> getAllMaintenanceLogs();
    List<MaintenanceLog> getMaintenanceLogsByTrafficLightId(int trafficLightId);
    MaintenanceLog getMaintenanceLogById(int id);
    void addMaintenanceLog(MaintenanceLog maintenanceLog);
    void addMaintenanceLogWithTrafficLight(MaintenanceLog maintenanceLog, int trafficLightId);
    void deleteMaintenanceLog(int id);
}
