
    @Test
    @DisplayName("Should find rating by ride ID")
    void shouldFindRatingByRideId() {
        // Given
        Integer rideId = 100;
package com.cabbooking.ratingservice.repository;
        // When
        Optional<Rating> foundRating = ratingRepository.findByRideId(rideId);

        // Then
        assertTrue(foundRating.isPresent());
        assertEquals(rideId, foundRating.get().getRideId());
        assertEquals(200, foundRating.get().getDriverId());
        assertEquals(5, foundRating.get().getScore());
    }

    @Test
    @DisplayName("Should return empty optional when rating not found by ride ID")
    void shouldReturnEmptyOptionalWhenRatingNotFoundByRideId() {
        // Given
        Integer nonExistentRideId = 999;

        // When
        Optional<Rating> foundRating = ratingRepository.findByRideId(nonExistentRideId);

        // Then
        assertFalse(foundRating.isPresent());
    }

    @Test
    @DisplayName("Should save rating successfully")
    void shouldSaveRatingSuccessfully() {
        // Given
        Rating newRating = new Rating();
        newRating.setRideId(103);
        newRating.setDriverId(202);
        newRating.setUserId(303);
        newRating.setScore(5);
        newRating.setFeedback("Outstanding service!");

        // When
        Rating savedRating = ratingRepository.save(newRating);

        // Then
        assertNotNull(savedRating.getRatingId());
        assertEquals(103, savedRating.getRideId());
        assertEquals(202, savedRating.getDriverId());
        assertEquals(5, savedRating.getScore());
        assertEquals("Outstanding service!", savedRating.getFeedback());
    }

    @Test
    @DisplayName("Should update average rating when new rating is added")
    void shouldUpdateAverageRatingWhenNewRatingIsAdded() {
        // Given
        Integer driverId = 200;
        Double initialAverage = ratingRepository.findAverageRatingByDriverId(driverId);
        assertEquals(4.5, initialAverage, 0.01);

        // Add new rating
        Rating newRating = new Rating();
        newRating.setRideId(104);
        newRating.setDriverId(200);
        newRating.setUserId(304);
        newRating.setScore(3);
        newRating.setFeedback("Okay service");

        // When
        ratingRepository.save(newRating);
        entityManager.flush();

        Double updatedAverage = ratingRepository.findAverageRatingByDriverId(driverId);

        // Then
        assertNotNull(updatedAverage);
        assertEquals(4.0, updatedAverage, 0.01); // Average of 5, 4, 3 = 4.0
    }

    @Test
    @DisplayName("Should handle single rating for driver")
    void shouldHandleSingleRatingForDriver() {
        // Given
        Integer singleDriverId = 203;
        Rating singleRating = new Rating();
        singleRating.setRideId(105);
        singleRating.setDriverId(singleDriverId);
        singleRating.setUserId(305);
        singleRating.setScore(5);
        singleRating.setFeedback("Perfect!");

        // When
        ratingRepository.save(singleRating);
        entityManager.flush();

        Double averageRating = ratingRepository.findAverageRatingByDriverId(singleDriverId);

        // Then
        assertNotNull(averageRating);
        assertEquals(5.0, averageRating, 0.01);
    }
}

import com.cabbooking.ratingservice.entity.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Rating Repository Tests")
class RatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingRepository ratingRepository;

    private Rating rating1;
    private Rating rating2;
    private Rating rating3;

    @BeforeEach
    void setUp() {
        // Create test ratings for driver 200
        rating1 = new Rating();
        rating1.setRideId(100);
        rating1.setDriverId(200);
        rating1.setUserId(300);
        rating1.setScore(5);
        rating1.setFeedback("Excellent!");

        rating2 = new Rating();
        rating2.setRideId(101);
        rating2.setDriverId(200);
        rating2.setUserId(301);
        rating2.setScore(4);
        rating2.setFeedback("Good service");

        rating3 = new Rating();
        rating3.setRideId(102);
        rating3.setDriverId(201);
        rating3.setUserId(302);
        rating3.setScore(3);
        rating3.setFeedback("Average");

        entityManager.persistAndFlush(rating1);
        entityManager.persistAndFlush(rating2);
        entityManager.persistAndFlush(rating3);
    }

    @Test
    @DisplayName("Should find average rating by driver ID")
    void shouldFindAverageRatingByDriverId() {
        // Given
        Integer driverId = 200;

        // When
        Double averageRating = ratingRepository.findAverageRatingByDriverId(driverId);

        // Then
        assertNotNull(averageRating);
        assertEquals(4.5, averageRating, 0.01); // Average of 5 and 4 = 4.5
    }

    @Test
    @DisplayName("Should return null when no ratings exist for driver")
    void shouldReturnNullWhenNoRatingsExistForDriver() {
        // Given
        Integer nonExistentDriverId = 999;

        // When
        Double averageRating = ratingRepository.findAverageRatingByDriverId(nonExistentDriverId);

        // Then
        assertNull(averageRating);
    }

