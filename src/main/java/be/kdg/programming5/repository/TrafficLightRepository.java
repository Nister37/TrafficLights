package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.TrafficLightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for TrafficLight entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface TrafficLightRepository extends JpaRepository<TrafficLight, Integer> {

    /**
     * Find all traffic lights belonging to a specific intersection.
     * Spring Data JPA will generate the query automatically based on method name.
     */
    List<TrafficLight> findByIntersectionId(int intersectionId);

    /**
     * Query Method 1: Find traffic lights by status.
     * Derived query - Spring Data JPA generates implementation automatically.
     */
    List<TrafficLight> findByStatus(TrafficLightStatus status);

    /**
     * Query Method 2: Find traffic lights installed after a specific date.
     * Derived query - Spring Data JPA generates implementation automatically.
     */
    List<TrafficLight> findByInstallationDateAfter(LocalDate date);

    /**
     * Custom Query: Find old traffic lights by status (installed before a specific date).
     * Uses @Query annotation with JPQL.
     */
    @Query("SELECT tl FROM TrafficLight tl WHERE tl.status = :status AND tl.installationDate < :beforeDate ORDER BY tl.installationDate ASC")
    List<TrafficLight> findOldTrafficLightsByStatus(@Param("status") TrafficLightStatus status, @Param("beforeDate") LocalDate beforeDate);
}

