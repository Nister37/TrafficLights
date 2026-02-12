package be.kdg.programming5.business.services;

import be.kdg.programming5.business.domain.MaintenanceCompany;

import java.util.List;

public interface MaintenanceCompanyService {
    List<MaintenanceCompany> getAllMaintenanceCompanies();
    List<MaintenanceCompany> getFilteredMaintenanceCompanies(String nameFilter, String activeFilter);
    MaintenanceCompany getMaintenanceCompanyById(int id);
    MaintenanceCompany getMaintenanceCompanyByIdWithLogs(int id);
    List<MaintenanceCompany> getCompaniesByMaintenanceLogId(int maintenanceLogId);
    void addMaintenanceCompany(MaintenanceCompany maintenanceCompany);
    void deleteMaintenanceCompany(int id);
}
