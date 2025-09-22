package com.cabbooking.ratingservice.serviceimpl;

import com.cabbooking.ratingservice.client.DriverServiceClient;
import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.exception.InvalidRatingException;
import com.cabbooking.ratingservice.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Rating Service Implementation Tests")
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DriverServiceClient driverServiceClient;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private RatingDTO ratingDTO;
    private Rating rating;

    @BeforeEach
    void setUp() {
        ratingDTO = new RatingDTO();
        ratingDTO.setRatingId(1);
        ratingDTO.setRideId(100);
        ratingDTO.setDriverId(200);
        ratingDTO.setUserId(300);
        ratingDTO.setScore(5);
        ratingDTO.setFeedback("Excellent service!");

        rating = new Rating();
        rating.setRatingId(1);
        rating.setRideId(100);
        rating.setDriverId(200);
        rating.setUserId(300);
        rating.setScore(5);
        rating.setFeedback("Excellent service!");
    }

    @Test
    @DisplayName("Should create rating successfully")
    void shouldCreateRatingSuccessfully() {
        // Given
        when(modelMapper.map(ratingDTO, Rating.class)).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(modelMapper.map(rating, RatingDTO.class)).thenReturn(ratingDTO);
        when(ratingRepository.findAverageRatingByDriverId(anyInt())).thenReturn(4.5);
        when(driverServiceClient.updateDriverRating(anyString(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());

        // When
        RatingDTO result = ratingService.createRating(ratingDTO);

        // Then
        assertNotNull(result);
        assertEquals(ratingDTO.getRatingId(), result.getRatingId());
        assertEquals(ratingDTO.getScore(), result.getScore());
        verify(ratingRepository).save(any(Rating.class));
        verify(driverServiceClient).updateDriverRating(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Should throw exception for invalid score - below minimum")
    void shouldThrowExceptionForInvalidScoreBelowMinimum() {
        // Given
        ratingDTO.setScore(0);

        // When & Then
        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(ratingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid score - above maximum")
    void shouldThrowExceptionForInvalidScoreAboveMaximum() {
        // Given
        ratingDTO.setScore(6);

        // When & Then
        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(ratingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should throw exception for null score")
    void shouldThrowExceptionForNullScore() {
        // Given
        ratingDTO.setScore(null);

        // When & Then
        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> ratingService.createRating(ratingDTO)
        );

        assertEquals("Score must be between 1 and 5.", exception.getMessage());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should get average rating for driver")
    void shouldGetAverageRatingForDriver() {
        // Given
        Integer driverId = 200;
        Double expectedAverage = 4.5;
        when(ratingRepository.findAverageRatingByDriverId(driverId)).thenReturn(expectedAverage);

        // When
        Double result = ratingService.getAverageRatingForDriver(driverId);

        // Then
        assertEquals(expectedAverage, result);
        verify(ratingRepository).findAverageRatingByDriverId(driverId);
    }

    @Test
    @DisplayName("Should return 0.0 when no ratings exist for driver")
    void shouldReturnZeroWhenNoRatingsExistForDriver() {
        // Given
        Integer driverId = 200;
        when(ratingRepository.findAverageRatingByDriverId(driverId)).thenReturn(null);

        // When
        Double result = ratingService.getAverageRatingForDriver(driverId);

        // Then
        assertEquals(0.0, result);
        verify(ratingRepository).findAverageRatingByDriverId(driverId);
    }

    @Test
    @DisplayName("Should get rating by ride ID successfully")
    void shouldGetRatingByRideIdSuccessfully() {
        // Given
        Integer rideId = 100;
        when(ratingRepository.findByRideId(rideId)).thenReturn(Optional.of(rating));
        when(modelMapper.map(rating, RatingDTO.class)).thenReturn(ratingDTO);

        // When
        Optional<RatingDTO> result = ratingService.getRatingById(rideId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(ratingDTO.getRatingId(), result.get().getRatingId());
        assertEquals(ratingDTO.getScore(), result.get().getScore());
        verify(ratingRepository).findByRideId(rideId);
    }

    @Test
    @DisplayName("Should return empty optional when rating not found")
    void shouldReturnEmptyOptionalWhenRatingNotFound() {
        // Given
        Integer rideId = 999;
        when(ratingRepository.findByRideId(rideId)).thenReturn(Optional.empty());

        // When
        Optional<RatingDTO> result = ratingService.getRatingById(rideId);

        // Then
        assertFalse(result.isPresent());
        verify(ratingRepository).findByRideId(rideId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should handle driver service failure gracefully")
    void shouldHandleDriverServiceFailureGracefully() {
        // Given
        when(modelMapper.map(ratingDTO, Rating.class)).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(modelMapper.map(rating, RatingDTO.class)).thenReturn(ratingDTO);
        when(ratingRepository.findAverageRatingByDriverId(anyInt())).thenReturn(4.5);
        when(driverServiceClient.updateDriverRating(anyString(), anyDouble()))
                .thenThrow(new RuntimeException("Driver service unavailable"));

        // When
        RatingDTO result = ratingService.createRating(ratingDTO);

        // Then
        assertNotNull(result);
        assertEquals(ratingDTO.getRatingId(), result.getRatingId());
        verify(ratingRepository).save(any(Rating.class));
        verify(driverServiceClient).updateDriverRating(anyString(), anyDouble());
    }

    @Test
    @DisplayName("Should handle repository exception during save")
    void shouldHandleRepositoryExceptionDuringSave() {
        // Given
        when(modelMapper.map(ratingDTO, Rating.class)).thenReturn(rating);
        when(ratingRepository.save(any(Rating.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ratingService.createRating(ratingDTO)
        );

        assertEquals("An error occurred while saving the rating.", exception.getMessage());
        verify(ratingRepository).save(any(Rating.class));
        verify(driverServiceClient, never()).updateDriverRating(anyString(), anyDouble());
    }
}
