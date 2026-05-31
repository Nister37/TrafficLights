package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceCompany;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceCompanyService {
    List<MaintenanceCompany> getAllMaintenanceCompanies();
    List<MaintenanceCompany> getFilteredMaintenanceCompanies(String nameFilter, String activeFilter);
    MaintenanceCompany getMaintenanceCompanyById(int id);
    MaintenanceCompany getMaintenanceCompanyByIdWithLogs(int id);
    List<MaintenanceCompany> getCompaniesByMaintenanceLogId(int maintenanceLogId);
    void addMaintenanceCompany(MaintenanceCompany maintenanceCompany);
    MaintenanceCompany createMaintenanceCompany(String name, String contactPhone, String contactEmail,
                                                boolean active, LocalDate since);
    void deleteMaintenanceCompany(int id);
}
