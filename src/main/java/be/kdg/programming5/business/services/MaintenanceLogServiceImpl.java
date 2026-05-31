package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceLog;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.repository.MaintenanceLogCompanyRepository;
import be.kdg.programming5.repository.MaintenanceLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing maintenance logs.
 */
@Service
public class MaintenanceLogServiceImpl implements MaintenanceLogService {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceLogServiceImpl.class);

    private final MaintenanceLogRepository maintenanceLogRepository;
    private final MaintenanceLogCompanyRepository maintenanceLogCompanyRepository;

    public MaintenanceLogServiceImpl(MaintenanceLogRepository maintenanceLogRepository,
                                     MaintenanceLogCompanyRepository maintenanceLogCompanyRepository) {
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.maintenanceLogCompanyRepository = maintenanceLogCompanyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceLog> getAllMaintenanceLogs() {
        logger.debug("Fetching all maintenance logs");
        return maintenanceLogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceLog> getMaintenanceLogsByTrafficLightId(int trafficLightId) {
        logger.debug("Fetching maintenance logs for traffic light: {}", trafficLightId);
        return maintenanceLogRepository.findByTrafficLightId(trafficLightId);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceLog getMaintenanceLogById(int id) {
        logger.debug("Fetching maintenance log by id: {}", id);
        return maintenanceLogRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void addMaintenanceLog(MaintenanceLog maintenanceLog) {
        logger.debug("Adding maintenance log: {}", maintenanceLog.getId());
        maintenanceLogRepository.save(maintenanceLog);
    }

    @Override
    @Transactional
    public void addMaintenanceLogWithTrafficLight(MaintenanceLog maintenanceLog, TrafficLight trafficLight) {
        logger.debug("Adding maintenance log with traffic light: {}", trafficLight.getId());
        maintenanceLog.setTrafficLight(trafficLight);
        maintenanceLogRepository.save(maintenanceLog);
    }

    @Override
    @Transactional
    public void deleteMaintenanceLog(int id) {
        logger.debug("Deleting maintenance log: {}", id);
        maintenanceLogCompanyRepository.deleteByMaintenanceLogId(id);
        maintenanceLogRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByTrafficLightId(int trafficLightId) {
        logger.debug("Deleting all maintenance logs for traffic light: {}", trafficLightId);
        maintenanceLogCompanyRepository.deleteByTrafficLightId(trafficLightId);
        maintenanceLogRepository.deleteByTrafficLightId(trafficLightId);
    }

    /**
     * Retrieves a maintenance log by ID with maintenance companies eagerly loaded using JOIN FETCH.
     *
     * @param id the ID of the maintenance log
     * @return the maintenance log with companies loaded, or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public MaintenanceLog getMaintenanceLogByIdWithCompanies(int id) {
        logger.debug("Fetching maintenance log by id with companies: {}", id);
        return maintenanceLogRepository.findByIdWithCompanies(id).orElse(null);
    }

}
