package com.cabbooking.ratingservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rating Entity Tests")
class RatingTest {

    private Rating rating;

    @BeforeEach
    void setUp() {
        rating = new Rating();
    }

    @Test
    @DisplayName("Should create rating with valid data")
    void shouldCreateRatingWithValidData() {
        // Given
        Integer ratingId = 1;
        Integer rideId = 100;
        Integer driverId = 200;
        Integer userId = 300;
        Integer score = 5;
        String feedback = "Excellent service!";

        // When
        rating.setRatingId(ratingId);
        rating.setRideId(rideId);
        rating.setDriverId(driverId);
        rating.setUserId(userId);
        rating.setScore(score);
        rating.setFeedback(feedback);

        // Then
        assertEquals(ratingId, rating.getRatingId());
        assertEquals(rideId, rating.getRideId());
        assertEquals(driverId, rating.getDriverId());
        assertEquals(userId, rating.getUserId());
        assertEquals(score, rating.getScore());
        assertEquals(feedback, rating.getFeedback());
    }

    @Test
    @DisplayName("Should create rating with minimum required fields")
    void shouldCreateRatingWithMinimumRequiredFields() {
        // Given
        Integer rideId = 100;
        Integer driverId = 200;
        Integer userId = 300;
        Integer score = 4;

        // When
        rating.setRideId(rideId);
        rating.setDriverId(driverId);
        rating.setUserId(userId);
        rating.setScore(score);

        // Then
        assertEquals(rideId, rating.getRideId());
        assertEquals(driverId, rating.getDriverId());
        assertEquals(userId, rating.getUserId());
        assertEquals(score, rating.getScore());
        assertNull(rating.getFeedback());
    }

    @Test
    @DisplayName("Should handle null feedback")
    void shouldHandleNullFeedback() {
        // Given
        rating.setFeedback(null);

        // Then
        assertNull(rating.getFeedback());
    }

    @Test
    @DisplayName("Should handle empty feedback")
    void shouldHandleEmptyFeedback() {
        // Given
        String emptyFeedback = "";
        rating.setFeedback(emptyFeedback);

        // Then
        assertEquals(emptyFeedback, rating.getFeedback());
    }
}
