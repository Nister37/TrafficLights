package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.MaintenanceCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for MaintenanceCompany entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface MaintenanceCompanyRepository extends JpaRepository<MaintenanceCompany, Integer> {

    /**
     * Find a maintenance company by ID with eagerly loaded maintenance logs via association entity.
     * Uses LEFT JOIN FETCH to avoid LazyInitializationException when accessing logs outside transaction.
     */
    @Query("SELECT mc FROM MaintenanceCompany mc " +
           "LEFT JOIN FETCH mc.maintenanceLogCompanies mlc " +
           "LEFT JOIN FETCH mlc.maintenanceLog " +
           "WHERE mc.id = :id")
    Optional<MaintenanceCompany> findByIdWithMaintenanceLogs(@Param("id") int id);

    /**
     * Find maintenance companies associated with a specific maintenance log via association entity.
     */
    @Query("SELECT mc FROM MaintenanceCompany mc " +
           "JOIN mc.maintenanceLogCompanies mlc " +
           "WHERE mlc.maintenanceLog.id = :maintenanceLogId")
    List<MaintenanceCompany> findByMaintenanceLogId(@Param("maintenanceLogId") int maintenanceLogId);
}

