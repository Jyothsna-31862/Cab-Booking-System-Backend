package com.cabbooking.ratingservice.controller;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@DisplayName("Rating Controller Tests")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        ratingDTO = new RatingDTO();
        ratingDTO.setRatingId(1);
        ratingDTO.setRideId(100);
        ratingDTO.setDriverId(200);
        ratingDTO.setUserId(300);
        ratingDTO.setScore(5);
        ratingDTO.setFeedback("Excellent service!");
    }

    @Test
    @DisplayName("Should create rating successfully")
    void shouldCreateRatingSuccessfully() throws Exception {
        // Given
        when(ratingService.createRating(any(RatingDTO.class))).thenReturn(ratingDTO);

        // When & Then
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ratingId").value(1))
                .andExpect(jsonPath("$.rideId").value(100))
                .andExpect(jsonPath("$.driverId").value(200))
                .andExpect(jsonPath("$.userId").value(300))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.feedback").value("Excellent service!"));

        verify(ratingService).createRating(any(RatingDTO.class));
    }

    @Test
    @DisplayName("Should handle invalid rating creation")
    void shouldHandleInvalidRatingCreation() throws Exception {
        // Given
        RatingDTO invalidRating = new RatingDTO();
        invalidRating.setScore(6); // Invalid score

        when(ratingService.createRating(any(RatingDTO.class)))
                .thenThrow(new RuntimeException("Score must be between 1 and 5"));

        // When & Then
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRating)))
                .andExpect(status().isInternalServerError());

        verify(ratingService).createRating(any(RatingDTO.class));
    }

    @Test
    @DisplayName("Should get average rating for driver successfully")
    void shouldGetAverageRatingForDriverSuccessfully() throws Exception {
        // Given
        Integer driverId = 200;
        Double averageRating = 4.5;
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(averageRating);

        // When & Then
        mockMvc.perform(get("/api/ratings/driver/{driverId}/average", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("4.5"));

        verify(ratingService).getAverageRatingForDriver(driverId);
    }

    @Test
    @DisplayName("Should return zero when driver has no ratings")
    void shouldReturnZeroWhenDriverHasNoRatings() throws Exception {
        // Given
        Integer driverId = 999;
        when(ratingService.getAverageRatingForDriver(driverId)).thenReturn(0.0);

        // When & Then
        mockMvc.perform(get("/api/ratings/driver/{driverId}/average", driverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0.0"));

        verify(ratingService).getAverageRatingForDriver(driverId);
    }

    @Test
    @DisplayName("Should get rating by ride ID successfully")
    void shouldGetRatingByRideIdSuccessfully() throws Exception {
        // Given
        Integer rideId = 100;
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.of(ratingDTO));

        // When & Then
        mockMvc.perform(get("/api/ratings/ride/{rideId}", rideId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ratingId").value(1))
                .andExpect(jsonPath("$.rideId").value(100))
                .andExpect(jsonPath("$.driverId").value(200))
                .andExpect(jsonPath("$.score").value(5));

        verify(ratingService).getRatingById(rideId);
    }

    @Test
    @DisplayName("Should return 404 when rating not found by ride ID")
    void shouldReturn404WhenRatingNotFoundByRideId() throws Exception {
        // Given
        Integer rideId = 999;
        when(ratingService.getRatingById(rideId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/ratings/ride/{rideId}", rideId))
                .andExpect(status().isNotFound());

        verify(ratingService).getRatingById(rideId);
    }

    @Test
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJsonInRequestBody() throws Exception {
        // Given
        String malformedJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(ratingService, never()).createRating(any());
    }

    @Test
    @DisplayName("Should handle missing request body")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(ratingService, never()).createRating(any());
    }

    @Test
    @DisplayName("Should handle service layer exceptions gracefully")
    void shouldHandleServiceLayerExceptionsGracefully() throws Exception {
        // Given
        when(ratingService.getAverageRatingForDriver(anyInt()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(get("/api/ratings/driver/{driverId}/average", 200))
                .andExpect(status().isInternalServerError());

        verify(ratingService).getAverageRatingForDriver(200);
    }

    @Test
    @DisplayName("Should validate path variables")
    void shouldValidatePathVariables() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ratings/driver/abc/average"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/ratings/ride/xyz"))
                .andExpect(status().isBadRequest());

        verify(ratingService, never()).getAverageRatingForDriver(anyInt());
        verify(ratingService, never()).getRatingById(anyInt());
    }

    @Test
    @DisplayName("Should handle empty feedback in rating creation")
    void shouldHandleEmptyFeedbackInRatingCreation() throws Exception {
        // Given
        RatingDTO ratingWithEmptyFeedback = new RatingDTO();
        ratingWithEmptyFeedback.setRatingId(1);
        ratingWithEmptyFeedback.setRideId(100);
        ratingWithEmptyFeedback.setDriverId(200);
        ratingWithEmptyFeedback.setUserId(300);
        ratingWithEmptyFeedback.setScore(4);
        ratingWithEmptyFeedback.setFeedback("");

        when(ratingService.createRating(any(RatingDTO.class))).thenReturn(ratingWithEmptyFeedback);

        // When & Then
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingWithEmptyFeedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").value(""))
                .andExpect(jsonPath("$.score").value(4));

        verify(ratingService).createRating(any(RatingDTO.class));
    }
}
