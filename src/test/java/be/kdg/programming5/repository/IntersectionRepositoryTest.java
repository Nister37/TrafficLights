package be.kdg.programming5.repository;

import be.kdg.programming5.business.domain.Intersection;
import be.kdg.programming5.business.domain.TrafficLight;
import be.kdg.programming5.enums.Direction;
import be.kdg.programming5.enums.IntersectionTypes;
import be.kdg.programming5.enums.TrafficLightStatus;
import be.kdg.programming5.enums.TrafficLightType;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IntersectionRepositoryTest {

    @Autowired
    private IntersectionRepository intersectionRepository;

    @Autowired
    private TrafficLightRepository trafficLightRepository;

    private Intersection seededIntersection;
    private TrafficLight seededTrafficLight;

    @BeforeEach
    void setUp() {
        seededIntersection = intersectionRepository.save(new Intersection(
                51.2, 4.4, IntersectionTypes.CROSSROADS, 4,
                true, LocalDate.of(2019, 3, 1), true, "/images/test-intersection.png"
        ));

        seededTrafficLight = new TrafficLight(
                TrafficLightStatus.ACTIVE, LocalDate.of(2022, 5, 20),
                Direction.N, TrafficLightType.COLLISION, false
        );
        seededTrafficLight.setIntersection(seededIntersection);
        seededTrafficLight = trafficLightRepository.save(seededTrafficLight);
    }

    @AfterEach
    void tearDown() {
        // Remove child before parent to respect FK constraints
        trafficLightRepository.deleteAllInBatch();
        intersectionRepository.deleteAllInBatch();
    }

    // ==============================
    // Lazy / Eager loading tests
    // ==============================

    @Test
    void findByIdShouldNotEagerlyLoadTrafficLights() {
        // Act
        Intersection fetched = intersectionRepository
                .findById(seededIntersection.getId()).orElseThrow();

        // Assert — @OneToMany(fetch = FetchType.LAZY) must NOT be initialized after a plain findById
        assertFalse(Hibernate.isInitialized(fetched.getTrafficLights()),
                "trafficLights should NOT be initialized after a plain findById (lazy loading)");
    }

    @Test
    void findByIdWithTrafficLightsShouldEagerlyLoadTrafficLights() {
        // Act
        Intersection fetched = intersectionRepository
                .findByIdWithTrafficLights(seededIntersection.getId()).orElseThrow();

        // Assert — JOIN FETCH query must initialize the collection
        assertTrue(Hibernate.isInitialized(fetched.getTrafficLights()),
                "trafficLights SHOULD be initialized when fetched via findByIdWithTrafficLights (JOIN FETCH)");
        assertEquals(1, fetched.getTrafficLights().size(),
                "Should have exactly 1 traffic light from seed data");
    }

    // ==============================
    // Constraint tests
    // ==============================

    @Test
    void savingIntersectionWithNullTypeShouldFailWithConstraintViolation() {
        // Arrange — type is @Column(nullable = false, @Enumerated)
        Intersection invalidIntersection = new Intersection(
                50.0, 3.7, null, 3,
                false, LocalDate.of(2020, 1, 1), false, null
        );

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class,
                () -> intersectionRepository.save(invalidIntersection),
                "Persisting an intersection with null type should violate the NOT NULL constraint");
    }

    @Test
    void deletingIntersectionWithTrafficLightsShouldFailDueToForeignKey() {
        // Arrange — seeded intersection still has a traffic light referencing it

        // Act & Assert — the FK on traffic_light.intersection_id prevents deletion
        assertThrows(DataIntegrityViolationException.class,
                () -> intersectionRepository.deleteById(seededIntersection.getId()),
                "Deleting an intersection should fail when traffic lights still reference it");
    }
}

