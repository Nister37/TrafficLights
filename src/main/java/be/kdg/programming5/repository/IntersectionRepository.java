package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Intersection entity.
 * Extends JpaRepository to get basic CRUD operations automatically.
 */
@Repository
public interface IntersectionRepository extends JpaRepository<Intersection, Integer> {
}

