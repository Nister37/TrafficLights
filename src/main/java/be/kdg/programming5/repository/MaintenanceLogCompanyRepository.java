package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.MaintenanceLogCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for MaintenanceLogCompany association entity.
 */
@Repository
public interface MaintenanceLogCompanyRepository extends JpaRepository<MaintenanceLogCompany, Integer> {

    /**
     * Delete all company associations for maintenance logs of a specific traffic light.
     */
    @Modifying
    @Query("DELETE FROM MaintenanceLogCompany mlc WHERE mlc.maintenanceLog.trafficLight.id = :trafficLightId")
    void deleteByTrafficLightId(@Param("trafficLightId") int trafficLightId);

    /**
     * Delete all log associations for a specific maintenance company.
     */
    @Modifying
    @Query("DELETE FROM MaintenanceLogCompany mlc WHERE mlc.maintenanceCompany.id = :maintenanceCompanyId")
    void deleteByMaintenanceCompanyId(@Param("maintenanceCompanyId") int maintenanceCompanyId);

    /**
     * Delete all company associations for a specific maintenance log.
     */
    @Modifying
    @Query("DELETE FROM MaintenanceLogCompany mlc WHERE mlc.maintenanceLog.id = :maintenanceLogId")
    void deleteByMaintenanceLogId(@Param("maintenanceLogId") int maintenanceLogId);
}

