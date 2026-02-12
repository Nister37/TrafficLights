package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceCompany;
import be.kdg.programming5.repository.MaintenanceCompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing maintenance companies.
 */
@Service
public class MaintenanceCompanyServiceImpl implements MaintenanceCompanyService {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceCompanyServiceImpl.class);

    private final MaintenanceCompanyRepository maintenanceCompanyRepository;

    public MaintenanceCompanyServiceImpl(MaintenanceCompanyRepository maintenanceCompanyRepository) {
        this.maintenanceCompanyRepository = maintenanceCompanyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCompany> getAllMaintenanceCompanies() {
        logger.debug("Fetching all maintenance companies");
        return maintenanceCompanyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCompany> getFilteredMaintenanceCompanies(String nameFilter, String activeFilter) {
        logger.debug("Fetching filtered companies - name: {}, active: {}", nameFilter, activeFilter);
        return maintenanceCompanyRepository.findAll().stream()
                .filter(c -> nameFilter.isEmpty() || c.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(c -> activeFilter.isEmpty() || c.isActive() == Boolean.parseBoolean(activeFilter))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceCompany getMaintenanceCompanyById(int id) {
        logger.debug("Fetching maintenance company by id: {}", id);
        return maintenanceCompanyRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceCompany getMaintenanceCompanyByIdWithLogs(int id) {
        logger.debug("Fetching maintenance company by id with logs: {}", id);
        return maintenanceCompanyRepository.findByIdWithMaintenanceLogs(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCompany> getCompaniesByMaintenanceLogId(int maintenanceLogId) {
        logger.debug("Fetching companies for maintenance log: {}", maintenanceLogId);
        return maintenanceCompanyRepository.findByMaintenanceLogId(maintenanceLogId);
    }

    @Override
    @Transactional
    public void addMaintenanceCompany(MaintenanceCompany company) {
        logger.debug("Adding maintenance company: {}", company.getId());
        maintenanceCompanyRepository.save(company);
    }

    @Override
    @Transactional
    public void deleteMaintenanceCompany(int id) {
        logger.debug("Deleting maintenance company: {}", id);
        maintenanceCompanyRepository.deleteById(id);
    }
}
