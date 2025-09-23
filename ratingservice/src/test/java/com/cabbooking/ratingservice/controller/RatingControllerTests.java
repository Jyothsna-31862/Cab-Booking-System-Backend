package com.cabbooking.ratingservice.controller;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class RatingControllerTests {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private RatingDTO testRatingDTO;

    @BeforeEach
    void logStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @AfterEach
    void logEnd(TestInfo testInfo) {
        log.info("Finished test: {}", testInfo.getDisplayName());
    }

    @BeforeEach
    void setUp() {
        testRatingDTO = new RatingDTO();
        testRatingDTO.setRatingId(1);
        testRatingDTO.setRideId(100);
        testRatingDTO.setDriverId(200);
        testRatingDTO.setUserId(300);
        testRatingDTO.setScore((byte) 5);
        testRatingDTO.setComments("Excellent service!");
    }

    @Test
    @DisplayName("createRating() should return CREATED and RatingDTO")
    void createRating_returnsCreatedAndRatingDto() {
        when(ratingService.createRating(testRatingDTO)).thenReturn(testRatingDTO);

        ResponseEntity<RatingDTO> response = ratingController.createRating(testRatingDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testRatingDTO, response.getBody());
    }

    @Test
    @DisplayName("getRatingByRideId() should return OK and RatingDTO when rating exists")
    void getRatingByRideId_returnsOkAndRatingDto_whenRatingExists() {
        Integer rideId = 100;
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.of(testRatingDTO));

        ResponseEntity<RatingDTO> response = ratingController.getRatingByRideId(rideId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testRatingDTO, response.getBody());
    }

    @Test
    @DisplayName("getRatingByRideId() should return NOT_FOUND when rating does not exist")
    void getRatingByRideId_returnsNotFound_whenRatingDoesNotExist() {
        Integer rideId = 999;
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.empty());

        ResponseEntity<RatingDTO> response = ratingController.getRatingByRideId(rideId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("getAverageRatingForDriver() should return OK and average rating")
    void getAverageRatingForDriver_returnsOkAndAverageRating() {
        Integer driverId = 200;
        Double expectedAverage = 4.5;
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(expectedAverage);

        ResponseEntity<Double> response = ratingController.getAverageRatingForDriver(driverId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAverage, response.getBody());
    }

    @Test
    @DisplayName("getAverageRatingForDriver() should return OK and 0.0 when no ratings exist")
    void getAverageRatingForDriver_returnsOkAndZero_whenNoRatingsExist() {
        Integer driverId = 300;
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(0.0);

        ResponseEntity<Double> response = ratingController.getAverageRatingForDriver(driverId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody());
    }
}
