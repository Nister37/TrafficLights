package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.TrafficLightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TrafficLight entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface TrafficLightRepository extends JpaRepository<TrafficLight, Integer> {

    /**
     * Find all traffic lights belonging to a specific intersection.
     * Fetch the owner for DTO mapping after the service transaction closes.
     */
    @Query("SELECT t FROM TrafficLight t LEFT JOIN FETCH t.owner WHERE t.intersection.id = :intersectionId")
    List<TrafficLight> findByIntersectionId(@Param("intersectionId") int intersectionId);

    /**
     * Query Method 1: Find traffic lights by status.
     * Derived query - Spring Data JPA generates implementation automatically.
     */
    List<TrafficLight> findByStatus(TrafficLightStatus status);

    /**
     * Find traffic lights installed after a specific date.
     * Fetch the owner for Thymeleaf authorization checks after the service transaction closes.
     */
    @Query("SELECT t FROM TrafficLight t LEFT JOIN FETCH t.owner WHERE t.installationDate > :date")
    List<TrafficLight> findByInstallationDateAfter(@Param("date") LocalDate date);

    /**
     * Custom Query: Find old traffic lights by status (installed before a specific date).
     * Uses @Query annotation with JPQL.
     */
    @Query("SELECT tl FROM TrafficLight tl LEFT JOIN FETCH tl.owner WHERE tl.status = :status AND tl.installationDate < :beforeDate ORDER BY tl.installationDate ASC")
    List<TrafficLight> findOldTrafficLightsByStatus(@Param("status") TrafficLightStatus status, @Param("beforeDate") LocalDate beforeDate);

    /**
     * Find traffic light by ID with maintenance logs eagerly fetched.
     */
    @Query("SELECT t FROM TrafficLight t LEFT JOIN FETCH t.maintenanceLogs WHERE t.id = :id")
    Optional<TrafficLight> findByIdWithMaintenanceLogs(@Param("id") int id);

    /**
     * Fetch all traffic lights with their owner eagerly loaded.
     * Needed for rendering views when spring.jpa.open-in-view=false.
     */
    @Query("SELECT t FROM TrafficLight t LEFT JOIN FETCH t.owner")
    List<TrafficLight> findAllWithOwner();

    /**
     * Fetch a traffic light with owner + intersection + maintenance logs.
     * Used for the traffic light details page to avoid lazy loading issues.
     */
    @Query("SELECT t FROM TrafficLight t " +
           "LEFT JOIN FETCH t.owner " +
           "LEFT JOIN FETCH t.intersection " +
           "LEFT JOIN FETCH t.maintenanceLogs " +
           "WHERE t.id = :id")
    Optional<TrafficLight> findByIdWithDetails(@Param("id") int id);
}

