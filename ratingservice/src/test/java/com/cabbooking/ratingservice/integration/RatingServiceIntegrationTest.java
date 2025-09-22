package com.cabbooking.ratingservice.integration;

import com.cabbooking.ratingservice.dto.RatingDTO;
import com.cabbooking.ratingservice.entity.Rating;
import com.cabbooking.ratingservice.repository.RatingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Rating Service Integration Tests")
class RatingServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RatingRepository ratingRepository;

    @MockBean
    private com.cabbooking.ratingservice.client.DriverServiceClient driverServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private RatingDTO ratingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // Mock driver service client response
        when(driverServiceClient.updateDriverRating(anyString(), anyDouble()))
                .thenReturn(ResponseEntity.ok().build());

        ratingDTO = new RatingDTO();
        ratingDTO.setRideId(100);
        ratingDTO.setDriverId(200);
        ratingDTO.setUserId(300);
        ratingDTO.setScore(5);
        ratingDTO.setFeedback("Excellent service!");
    }

    @Test
    @DisplayName("Should create rating and calculate average rating - full integration")
    void shouldCreateRatingAndCalculateAverageRatingFullIntegration() throws Exception {
        // Create first rating
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.feedback").value("Excellent service!"));

        // Verify average rating
        mockMvc.perform(get("/api/ratings/driver/200/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.0"));

        // Create second rating for same driver
        RatingDTO secondRating = new RatingDTO();
        secondRating.setRideId(101);
        secondRating.setDriverId(200);
        secondRating.setUserId(301);
        secondRating.setScore(3);
        secondRating.setFeedback("Average service");


        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRating)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(3));

        // Verify updated average rating
        mockMvc.perform(get("/api/ratings/driver/200/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.0")); // Average of 5 and 3
    }

    @Test
    @DisplayName("Should handle complete rating workflow")
    void shouldHandleCompleteRatingWorkflow() throws Exception {
        // Step 1: Create rating
        String response = mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RatingDTO createdRating = objectMapper.readValue(response, RatingDTO.class);

        // Step 2: Retrieve rating by ride ID
        mockMvc.perform(get("/api/ratings/ride/" + createdRating.getRideId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").value(createdRating.getRideId()))
                .andExpect(jsonPath("$.driverId").value(200))
                .andExpect(jsonPath("$.score").value(5));

        // Step 3: Get average rating for driver
        mockMvc.perform(get("/api/ratings/driver/200/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("5.0"));
    }

    @Test
    @DisplayName("Should persist rating data correctly")
    void shouldPersistRatingDataCorrectly() throws Exception {
        // Create rating through API
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDTO)))
                .andExpect(status().isOk());

        // Verify data persisted in database
        Rating savedRating = ratingRepository.findByRideId(100).orElse(null);
        assert savedRating != null;
        assert savedRating.getDriverId().equals(200);
        assert savedRating.getScore().equals(5);
        assert "Excellent service!".equals(savedRating.getFeedback());
    }

    @Test
    @DisplayName("Should handle validation errors in integration")
    void shouldHandleValidationErrorsInIntegration() throws Exception {
        // Create rating with invalid score
        RatingDTO invalidRating = new RatingDTO();
        invalidRating.setRideId(100);
        invalidRating.setDriverId(200);
        invalidRating.setUserId(300);
        invalidRating.setScore(6); // Invalid score
        invalidRating.setFeedback("Test feedback");

        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRating)))
                .andExpect(status().isInternalServerError());

        // Verify no data was persisted
        assert ratingRepository.findByRideId(100).isEmpty();
    }
}
