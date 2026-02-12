package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for MaintenanceLog entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Integer> {

    /**
     * Find all maintenance logs for a specific traffic light.
     * Spring Data JPA will generate the query automatically based on method name.
     */
    List<MaintenanceLog> findByTrafficLightId(int trafficLightId);

    /**
     * Find maintenance log by ID with maintenance companies eagerly fetched.
     */
    @Query("SELECT m FROM MaintenanceLog m " +
           "LEFT JOIN FETCH m.maintenanceLogCompanies mlc " +
           "LEFT JOIN FETCH mlc.maintenanceCompany " +
           "WHERE m.id = :id")
    Optional<MaintenanceLog> findByIdWithCompanies(@Param("id") int id);
}

