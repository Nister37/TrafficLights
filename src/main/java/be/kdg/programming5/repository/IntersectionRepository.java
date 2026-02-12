package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Intersection entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface IntersectionRepository extends JpaRepository<Intersection, Integer> {

    /**
     * Find intersection by ID with traffic lights eagerly fetched.
     */
    @Query("SELECT i FROM Intersection i LEFT JOIN FETCH i.trafficLights WHERE i.id = :id")
    Optional<Intersection> findByIdWithTrafficLights(@Param("id") int id);
}

